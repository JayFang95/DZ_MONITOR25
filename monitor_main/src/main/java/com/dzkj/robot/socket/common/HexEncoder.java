package com.dzkj.robot.socket.common;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import org.apache.commons.lang3.StringUtils;

/**
 * Copyright(c),2018-2024,合肥市鼎足空间技术有限公司
 *
 * @author jing.fang
 * @date 2024/3/25 9:01
 * @description 自定义发送消息格式  发送16进制
 * history
 * <author>    <time>    <version>    <desc>
 * 作者姓名     修改时间     版本号        描述
 */
public class HexEncoder extends MessageToByteEncoder<String> {

    @Override
    protected void encode(ChannelHandlerContext ctx, String msg, ByteBuf byteBuf) throws Exception {
        //将16进制字符串转为数组
        byteBuf.writeBytes(hexString2Bytes(msg));
    }

    /**
     * @description 16进制字符串转字节数组
     * @param hexString 16进制字符串
     * @return 字节数组
     */

    public static byte[] hexString2Bytes(String hexString) {
        if (StringUtils.isEmpty(hexString)) {
            return null;
        }
        hexString = hexString.toUpperCase();
        int length = hexString.length();
        byte[] bytes = new byte[length / 2];
        for (int i = 0; i < length; i += 2) {
            bytes[i / 2] = (byte) ((Character.digit(hexString.charAt(i), 16) << 4)
                    + Character.digit(hexString.charAt(i+1), 16));
        }
        return bytes;

    }

}
