package com.dzkj.biz.vo;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * Copyright(c),2018-2021,合肥市鼎足空间技术有限公司
 *
 * @author jing.fang
 * @date 2022/9/29 14:58
 * @description 应用消息对象
 * history
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
@Data
@Accessors(chain = true)
public class AppMsgVO {
    /**
     * 人员id:"UserID1|UserID2|UserID3"
     */
    private String touser;
    /**
     * 部门id："PartyID1|PartyID2"
     */
    private String toparty;
    /**
     * 标签id：TagID1 | TagID2
     */
    private String totag;
    /**
     * 消息类型：text, textcard
     */
    private String msgtype;
    /**
     * 应用代理id
     */
    private Long agentid;
    /**
     * 文本消息体：text
     */
    private TextVO text;
    /**
     * 文本卡片消息体：textcard
    **/
    private TextCardVO textcard;
    /**
     * 谁否是保密消息
     */
    private Integer safe;
    /**
     * 是否开启id转译
     */
    private Integer enable_id_trans;
    /**
     * 是否开启重复消息检查
     */
    private Integer enable_duplicate_check;
    /**
     * 是否重复消息检查的时间间隔
     */
    private Integer duplicate_check_interval;

    public AppMsgVO(){
        this.safe = 0;
        this.enable_id_trans = 0;
        this.enable_duplicate_check = 0;
        this.duplicate_check_interval = 1800;
    }

}
