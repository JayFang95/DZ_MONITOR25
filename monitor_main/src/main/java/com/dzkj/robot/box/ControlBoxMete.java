package com.dzkj.robot.box;

import com.dzkj.bean.RtCode;
import com.dzkj.common.CommonUtil;
import com.dzkj.robot.socket.common.ChannelHandlerUtil;
import lombok.Data;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import java.util.Date;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * Copyright(c),2018-2024,合肥市鼎足空间技术有限公司
 *
 * @author jing.fang
 * @date 2024/2/26 16:53
 * @description 温度气压控制器
 * history
 * <author>    <time>    <version>    <desc>
 * 作者姓名     修改时间     版本号        描述
 */
@Data
@Slf4j
@ToString
public class ControlBoxMete {

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
     * 超时任务周期: 秒
     */
    private Long timerPeriod;

    /**
     * 命令处理状态码
     * 0：空闲；1：处理中；2：超时；10：正常处理结束
     */
    private volatile int processCommandStatus = 0;

    /**
     * 接收到的消息字符串
     */
    private String receivedMsg = "";

    /**
     * 计数器
     **/
    private CountDownLatch countDownLatch;

    public ControlBoxMete(){
        this.timerPeriod = 10L;
    }

    //region 公共方法
    /**
     * 接收到信息，激发接收事件
     **/
    public void received(String msg) {
        // 复制接收信息
        this.receivedMsg = msg;
        //正常接收(仪器断电手工重启时，仪器可能回传乱码信息，如果不加判定把状态置为10，后续指令无法执行--230720)
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
            // 等待测量返回结果
            cdlRes = countDownLatch.await(timerPeriod, TimeUnit.SECONDS);
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
    //endregion

    //region 私有方法
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

        //获取温度、湿度
        if (receivedMessage.length() == 18) {
            String wHex = receivedMessage.substring(6, 10);
            String tHex=receivedMessage.substring(10, 14);
            double w = Integer.parseInt(wHex,16)/10.0;
            double t= hex4Str2Int(tHex) / 10.0;
            results.add(String.format("%.1f", t));
            results.add(String.format("%.1f", w));
        }
        //获取气压
        else if (receivedMessage.length() == 14) {
            String pHex = receivedMessage.substring(6, 10);
            double p = Integer.parseInt(pHex, 16) / 100.0;
            results.add(String.format("%.1f", p));
        }

        return CommonUtil.SYS_GRC_OK;
    }

    /**
     * 4字节16进制字符串转10进制整数
     * @param hexStr4 4字节16进制字符串
     */
    private int hex4Str2Int(String hexStr4) {
        //把16进制转为10进制再转为二进制字符串
        int num = Integer.parseInt(hexStr4, 16);
        String byteStr = Integer.toBinaryString(num);
        //通过最高位是否为1判断是否是负数
        if (byteStr.length() == 16 && "1".equals(byteStr.substring(0, 1))) {
            hexStr4 = "FFFF" + hexStr4;
            num = Integer.parseInt(hexStr4, 16);
        }
        return num;
    }
    //endregion
}
