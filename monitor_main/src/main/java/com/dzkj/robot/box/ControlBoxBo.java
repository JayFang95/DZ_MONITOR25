package com.dzkj.robot.box;

import com.dzkj.bean.RtCode;
import com.dzkj.common.CommonUtil;
import com.dzkj.enums.DeviceType;
import com.dzkj.robot.socket.common.ChannelHandlerUtil;
import lombok.Data;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * Copyright(c),2018-2023,合肥市鼎足空间技术有限公司
 *
 * @author jing.fang
 * @date 2023/2/16
 * @description 控制器业务类
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
@Data
@Slf4j
@ToString
public class ControlBoxBo {

    /**
     * 命令处理状态码
     * 0：空闲；1：处理中；2：超时；3：取消；4:控制器掉线;10：正常处理结束
     */
    private volatile int processCommandStatus = 0;

    /**
     * 接收到的消息字符串
     */
    private String receivedMsg = "";

    /**
     * 命令处理是否应用超时
     */
    private boolean canProcessCommandTimeout = false;

    /**
     * 超时任务周期: 秒
     */
    private Long timerPeriod;

    /**
     * 关联仪器类型
     */
    private DeviceType deviceType;

    /**
     * 关联仪器信息(仪器型号、序列号、电源电量等)，如果连接仪器失败，显示：连接仪器失败!
     */
    private String deviceInfo;

    /**
     * 仪器序列号
     */
    private String serialNo;

    /**
     * 连接服务器时，显示的ID号：IP+端口号
     */
    private String clientId;

    /**
     * 是否在线
     */
    private boolean onLine;

    /**
     * 上线时间
     */
    private Date onLineTime;

    /**
     * 保活计数器
     */
    private int keepAliveCounter;

    /**
     * 命令名
     */
    private String commandName;

    /**
     * 计数器
     **/
    private CountDownLatch countDownLatch;

    public ControlBoxBo() {
        this.deviceType = DeviceType.NULL;
        this.timerPeriod = 10L;
    }

    /**
     * 接收到信息，激发接收事件
     **/
    public void received(String msg) {
        // 复制接收信息
        this.receivedMsg = msg;
        //结束测量等待
        if (countDownLatch != null && countDownLatch.getCount() == 1) {
            if (this.processCommandStatus == 1){
                //正常接收
                this.processCommandStatus = 10;
            }
            countDownLatch.countDown();
        }else {
            log.warn("操作指令已经释放 - {}", msg);
        }
    }

    /**
     * 取消命令
     *
     * @description: 取消命令
     * @author: jing.fang
     * @Date: 2023/2/16 15:44
     **/
    public void cancelCommand()
    {
        //还在执行中
        this.processCommandStatus = 3;
        //结束测量等待
        if (countDownLatch != null && countDownLatch.getCount() == 1) {
            countDownLatch.countDown();
        }
        log.info("操作取消");
    }

    /**
     * 控制器掉线
     *
     * @description: 控制器掉线
     * @author: jing.fang
     * @Date: 2023/2/16 15:45
     **/
    public void offLineOperate() {
        //结束测量等待
        if (countDownLatch != null && countDownLatch.getCount() == 1) {
            if (this.processCommandStatus == 1){
                //还在执行中
                this.processCommandStatus = 4;
            }
            countDownLatch.countDown();
        }
        log.info("控制器掉线");
    }

    /**
     * 设置命令超时时间
     *
     * @description: 设置命令超时时间
     * @author: jing.fang
     * @Date: 2023/2/16 15:45
     * @param canTimeout 可否启用超时
     * @param seconds  默认超时时间
     **/
    public void setProcessCommandTimeout(boolean canTimeout, Long seconds) {
        if (seconds == null){
            seconds = 10L;
        }
        this.timerPeriod = seconds;
        this.canProcessCommandTimeout = canTimeout;
    }

    /**
     * 处理命令代码串
     *
     * @description: 处理命令代码串
     * @author: jing.fang
     * @Date: 2023/2/16 15:47
     * @param commandCode 命令代码串
     * @param results 有效信息列表
     * @return com.dzkj.bean.RtCode
     **/
    public RtCode processCommand(String commandCode, List<String> results) {
        //处于非空闲状态，直接返回
        if (processCommandStatus != 0){
            log.info("执行{}时控制器状态非空闲",commandName);
            return null;
        }
        // 发送指令，发生失败直接返回
        boolean result = ChannelHandlerUtil.sendCommand(clientId, serialNo, commandCode);
        if (!result){
            return CommonUtil.SYS_GRC_OFFLINE;
        }
        // 设为“处理中”状态
        processCommandStatus = 1;
        countDownLatch = new CountDownLatch(1);
        // 2023/6/15  启用超时器(修改停止使用线程循环等待超时，直接使用countDownLatch的超时机制)
        try {
            boolean cdlRes ;
            long timeout = canProcessCommandTimeout ? timerPeriod : 10;
            // 等待测量返回结果
            cdlRes = countDownLatch.await(timeout, TimeUnit.SECONDS);
            //超时但还在执行中
            if (!cdlRes && processCommandStatus == 1){
                processCommandStatus = 2;
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        //返回处理结果
        RtCode rc= processReceivedMessage(receivedMsg, results);
        //恢复为“空闲”状态
        processCommandStatus = 0;
        return rc;
    }

    /**
     * 处理接收到的信息字符串
     *
     * @description: 处理接收到的信息字符串
     * @author: jing.fang
     * @Date: 2023/2/16 15:53
     * @param receivedMessage 接收到的信息字符串
     * @param results  有效信息列表
     * @return com.dzkj.bean.RtCode
     **/
    private RtCode processReceivedMessage(String receivedMessage, List<String> results) {
        //超时处理
        if (processCommandStatus == 2) {
            return CommonUtil.SYS_GRC_TIMEOUT;
        }

        //取消处理
        if (processCommandStatus == 3) {
            return CommonUtil.SYS_GRC_CANCEL;
        }

        //掉线处理
        if (processCommandStatus == 4) {
            return CommonUtil.SYS_GRC_OFFLINE;
        }

        //正常处理
        switch (this.deviceType) {
            case TRIMBLE:
                return processReceivedMessageTrimble(receivedMessage, results);
            case SOKKA:
                return processReceivedMessageSokka(receivedMessage, results);
            case LEI_CA:
            default:
                return processReceivedMessageLeica(receivedMessage, results);
        }
    }

    private RtCode processReceivedMessageLeica(String receivedMessage, List<String> results) {
        //发送"c+休止符"指令去停止耗时指令时，返回信息为"?"-暂停处理GeoCom
        if (Objects.equals(receivedMessage, "?")) {
            return CommonUtil.SYS_GRC_OK;
        }
        /*
         * 有效返回数据信息格式类似于：%R1P,0,0:RC,reSwitch
         * 正常返回都没问题。230527测试，对一些老仪器，开机指令可能出现乱码错误
         */
        try {
            //获取返回消息“：”号之后的有效信息字符段
            String msg = receivedMessage.split(":")[1];
            //去除回车换行符
            msg = msg.replace("\r\n", "");
            String[] infos = msg.split(",");
            //需要返回参数时，才赋值参数
            if (results != null) {
                //清空成果列表
                results.clear();
                //获取有效消息文本值列表
                results.addAll(Arrays.asList(infos).subList(1, infos.length));
            }
            //获取返回码
            int codeValue = Integer.parseInt(infos[0]);
            RtCode rc = CommonUtil.getRtCodeByCodeValue(this.deviceType, codeValue);
            if (Objects.equals(rc.getName(), "GRC_OK")) {
                rc = CommonUtil.SYS_GRC_OK;
            }
            return rc;
        } catch (Exception e) {
            e.printStackTrace();
            log.info("{} 接收到待处理消息格式错误: {}", receivedMessage, e.getMessage());
            RtCode grcErr = CommonUtil.SYS_GRC_ERR;
            grcErr.setNote("执行异常:接收到待处理消息格式错误_" + receivedMessage);
            return grcErr;
        }
    }

    private RtCode processReceivedMessageTrimble(String receivedMessage, List<String> results) {
        return CommonUtil.SYS_GRC_OK;
    }

    private RtCode processReceivedMessageSokka(String receivedMessage, List<String> results) {
        return CommonUtil.SYS_GRC_OK;
    }

}
