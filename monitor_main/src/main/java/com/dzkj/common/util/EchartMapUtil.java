package com.dzkj.common.util;

import com.alibaba.fastjson.JSON;

import java.net.URL;
import java.nio.charset.StandardCharsets;

/**
 * Copyright(c),2018-2021,合肥市鼎足空间技术有限公司
 *
 * @author jing.fang
 * @date 2022/3/31
 * @description note
 * history
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
public class EchartMapUtil {

    private static final String AK = "Be8LaBeM8XK8dhC2617v1EWmgQAqEyiG";

    /**
     * 获取经纬度所在省份
     *
     * @description 获取经纬度所在省份
     * @author jing.fang
     * @date 2022/3/31 13:48
     * @param lng lng
     * @param lat lat
     * @return java.lang.String
    **/
    public static String getProvince(String lat, String lng ){
        String location=lng+","+lat;
        String urlString = "http://api.map.baidu.com/reverse_geocoding/v3/?ak="+ AK +"&output=json&coordtype=bd09ll&location="+location;
        String province = "";
        StringBuilder res = new StringBuilder();
        try {
            URL url = new URL(urlString);
            java.net.HttpURLConnection conn = (java.net.HttpURLConnection)url.openConnection();
            conn.setDoOutput(true);
            conn.setRequestMethod("POST");
            java.io.BufferedReader in = new java.io.BufferedReader(new java.io.InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8));
            String line;
            while ((line = in.readLine()) != null) {
                res.append(line).append("\n");
            }
            String addressComponent= JSON.parseObject(res.toString()).getJSONObject("result").getString("addressComponent");
            province = JSON.parseObject(addressComponent).getString("province");
            in.close();
        } catch (Exception e) {
            return "";
        }
        return province == null ? "未知省份" : province;
    }

}
