package com.dzkj.config;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * Copyright(c),2018-2021,合肥市鼎足空间技术有限公司
 *
 * @author jing.fang
 * @date 2021/3/17
 * @description websocket信息对象
 * history
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class MessageVO implements Serializable {

    /**
     * 0-多地上下线; 5-公司人员上下线;
     */
    private Integer code;

    private String msg;

    private Object data;

    public MessageVO(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }
}
