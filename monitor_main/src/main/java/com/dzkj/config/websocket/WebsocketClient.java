package com.dzkj.config.websocket;

import lombok.Data;

import javax.websocket.Session;

/**
 * Copyright(c),2018-2020,合肥市鼎足空间技术有限公司
 *
 * @author jing.fang
 * @date 2020/10/29
 * @description websocketClient信息
 * history
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
@Data
public class WebsocketClient {

    private Long userId;
    private String createTime;
    private Session session ;

    public WebsocketClient() {
    }
    public WebsocketClient(Long userId, String createTime, Session session){
        this.userId = userId;
        this.createTime = createTime;
        this.session = session;
    }

}
