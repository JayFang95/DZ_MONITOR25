package com.dzkj.robot.socket.common;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

/**
 * Copyright(c),2018-2024,合肥市鼎足空间技术有限公司
 *
 * @author jing.fang
 * @date 2024/3/25 9:01
 * @description netty接收对象转16进制
 * history
 * <author>    <time>    <version>    <desc>
 * 作者姓名     修改时间     版本号        描述
 */
public class HexDecoder extends ByteToMessageDecoder {

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) throws Exception {
        String HEXES = "0123456789ABCDEF";
        byte[] req = new byte[byteBuf.readableBytes()];
        byteBuf.readBytes(req);
        final StringBuilder hex = new StringBuilder(2 * req.length);

        for (byte b : req) {
            hex.append(HEXES.charAt((b & 0xF0) >> 4))
                    .append(HEXES.charAt((b & 0x0F)));
        }
        list.add(hex.toString());
    }

    public String bytesToHexString(byte[] bArray) {
        StringBuilder sb = new StringBuilder(bArray.length);
        String sTemp;
        for (byte b : bArray) {
            sTemp = Integer.toHexString(0xFF & b);
            if (sTemp.length() < 2)
                sb.append(0);
            sb.append(sTemp.toUpperCase());
        }
        return sb.toString();
    }

    public static String toHexString1(byte[] b) {
        StringBuilder buffer = new StringBuilder();
        for (byte value : b) {
            buffer.append(toHexString1(value));
        }
        return buffer.toString();
    }

    public static String toHexString1(byte b) {
        String s = Integer.toHexString(b & 0xFF);
        if (s.length() == 1) {
            return "0" + s;
        } else {
            return s;
        }
    }

}
