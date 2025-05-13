package com.dzkj.config.websocket;


import com.alibaba.fastjson.JSON;

import javax.websocket.EncodeException;
import javax.websocket.Encoder;
import javax.websocket.EndpointConfig;

/**
 * Copyright(c),2018-2020,合肥市鼎足空间技术有限公司
 *
 * @author jing.fang
 * @date 2020/8/14
 * @description websocket消息
 * history
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
public class ServerEncoder implements Encoder.Text<Object> {

    @Override
    public void destroy() {

    }

    @Override
    public void init(EndpointConfig arg0) {

    }

    @Override
    public String encode(Object object) throws EncodeException {
        try {
            return JSON.toJSONString(object, false);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}

