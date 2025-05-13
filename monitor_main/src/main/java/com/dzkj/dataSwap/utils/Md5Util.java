package com.dzkj.dataSwap.utils;

import lombok.extern.slf4j.Slf4j;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Copyright(c),2018-2025,合肥市鼎足空间技术有限公司
 *
 * @author jing.fang
 * @date 2025/3/11 上午9:37
 * @description md5加密工具
 * history
 * <author>    <time>    <version>    <desc>
 * 作者姓名     修改时间     版本号        描述
 */
@Slf4j
public class Md5Util {

    /**
     * 字符串加密获取16进制字符串大写
     * @param src 待加密字符串
     * @return 16进制字符串大写
     */
    public static String encode(String src) {
        // 获取MD5加密算法的MessageDigest实例
        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("MD5");
            // 计算输入字符串的MD5散列值
            byte[] hash = md.digest(src.getBytes());

            // 将散列值转换为十六进制字符串
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b).toUpperCase();
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            log.info("MD5加密失败: {}", e.getMessage());
        }
        return null;

    }
}
