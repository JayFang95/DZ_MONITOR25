package com.dzkj.biz.wx;

import com.dzkj.common.util.WeiXinUtil;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

/**
 * Copyright(c),2018-2021,合肥市鼎足空间技术有限公司
 *
 * @author jing.fang
 * @date 2021/5/26
 * @description 微信业务接口实现
 * history
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
@Component
public class WeiXinBizImpl implements IWeiXinBiz {

    @Autowired
    WeiXinUtil weiXinUtil;

    @Override
    public boolean check(String signature, String timestamp, String nonce, String echostr) {
        String token = weiXinUtil.getToken();
        //1）将token、timestamp、nonce三个参数进行字典序排序
        String[] strs = new String[]{token, timestamp, nonce};
        Arrays.sort(strs);
        String str = strs[0] + strs[1] + strs[2];
        // 2）将三个参数字符串拼接成一个字符串进行sha1加密
        String mySignature = DigestUtils.sha1Hex(str);
        // 3）开发者获得加密后的字符串可与signature对比，标识该请求来源于微信
        return mySignature.equalsIgnoreCase(signature);
    }

    /**
     * 进行sha1加密
     */
    private String sha1(String src) {
        //调用算法 org.apache.commons.codec.digest.DigestUtils
        String hex = DigestUtils.shaHex(src.getBytes());
        try {
            //获取加密对象
            MessageDigest md = MessageDigest.getInstance("sha1");
            //加密
            byte[] digest = md.digest(src.getBytes());
            //处理加密结果
            char[] chars = {'0','1','2','3','4','5','6','7','8','9','a','b','c','d','e','f'};
            StringBuilder sb = new StringBuilder();
            for (byte b : digest) {
                sb.append(chars[(b>>4)&15]);
                sb.append(chars[b&15]);
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }
}
