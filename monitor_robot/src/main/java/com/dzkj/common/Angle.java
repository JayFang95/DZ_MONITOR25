package com.dzkj.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * Copyright(c),2018-2021,合肥市鼎足空间技术有限公司
 *
 * @author jing.fang
 * @date 2023/2/8
 * @description 角度转换操作类
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class Angle {

    /**
     * 弧度值[0，2Pi)
     **/
    private double rad;
    /**
     * 十进制度[0，360)
     **/
    private double deg;
    /**
     * d.mmss[0，360)
     **/
    private double dms;

    /**
     * 度[0，360)
     **/
    private int d;
    /**
     * 分[0，60)
     **/
    private int m;
    /**
     * 秒[0，60)
     **/
    private double s;

    //region 公共方法
    /**
     * 设置弧度值在[0，2pi)之间,根据需要保留负号
     * @param rad 弧度
     * @param keepMinus 是否保留负号
     */
    public static double getDefaultRange(double rad, boolean keepMinus)
    {
        boolean isMinus = rad < 0;
        double theRad = Math.abs(rad);
        //把弧度值转到[0，2pi)之间
        while (theRad >= 2 * Math.PI)
        {
            theRad -= 2 * Math.PI;
        }
        //是负值
        if (isMinus)
        {
            theRad = keepMinus ? -theRad : -theRad + 2 * Math.PI;
        }//end if
        return theRad;
    }

    /**
     * 弧度转十进制度[0，360.0),弧度可为负值
     * @param rad 弧度
     * @param keepMinus 是否保留负号
     * @return 十进制度[0，360.0)
     */
    public static double rad2Deg(double rad, boolean keepMinus)
    {
        double tempRad = getDefaultRange(rad, keepMinus);
        return tempRad * 180.0 / Math.PI;
    }

    /**
     * 弧度转dms[0，360.0),弧度可为负值
     * @param rad 弧度
     * @param keepMinus 是否保留负号: false
     * @return dms[0，360.0)
     */
    public static double rad2Dms(double rad, boolean keepMinus)
    {
        double deg = rad2Deg(rad, keepMinus);
        int d = (int)(Math.floor(deg));
        int m = (int)(Math.floor((deg - d) * 60));
        double s = ((deg - d) * 60 - m) * 60;
        return d + m / 100.0 + s / 10000.0;
    }

    /**
     * 进制度转弧度[0，2Pi),十进制度可为负值
     * @param deg 十进制度
     * @param keepMinus 是否保留负号
     * @return 弧度[0，2Pi)
     */
    public static double deg2Rad(double deg, boolean keepMinus)
    {
        double rad = deg * Math.PI / 180.0;
        return getDefaultRange(rad, keepMinus);
    }

    /**
     * 十进制度转dms[0，360.0),十进制度可为负值
     * @param deg 十进制度
     * @param keepMinus 是否保留负号
     * @return dms[0，360.0)
     */
    public static double deg2Dms(double deg, boolean keepMinus)
    {
        double rad = deg * Math.PI / 180.0;
        return rad2Dms(rad, keepMinus);
    }

    /**
     * dms转十进制度[0，360.0),dms可为负值
     * @param dms 度分秒
     * @param keepMinus 是否保留负号
     * @return deg[0，360.0)
     */
    public static double dms2Deg(double dms, boolean keepMinus)
    {
        boolean isMinus = dms < 0;
        double dms1 = Math.abs(dms);
        int d = (int)(Math.floor(dms1));
        int m = (int)(Math.floor((dms1 - d) * 100));
        double s = ((dms1 - d) * 100 - m) * 100;
        double deg = d + m / 60.0 + s / 3600.0;
        //是负值(最后取)
        if (isMinus)
        {
            deg = -deg;
        }//end if
        //先转为弧度，限制角度范围
        double rad = deg2Rad(deg, keepMinus);
        return rad2Deg(rad, keepMinus);
    }

    /**
     * dms转弧度[0，2Pi),dms可为负值
     * @param dms 度分秒
     * @param keepMinus 是否保留负号
     * @return rad[0，2Pi)
     */
    public static double dms2Rad(double dms, boolean keepMinus)
    {
        double deg = dms2Deg(dms, keepMinus);
        return deg2Rad(deg, keepMinus);
    }

    /**
     * 处理角度差值计算中的0与359差值问题
     * @param deltVal deltVal
     * @return double
     */
    public static double correct0_359(double deltVal)
    {
        if (Math.abs(deltVal) > Math.PI)
        {
            deltVal = deltVal > 0 ? deltVal - Math.PI * 2 : Math.PI * 2 + deltVal;
        }

        return deltVal;
    }

    /**
     * 构造函数,根据角度类型以及所给角度值(可为负值),初始化角度
     * @param angle 角度值
     * @param type 角度值类型： 1:弧度;2:D.MMSS;3:十进制度
     * @param keepMinus 是否保留负号: 默认false
     */
    public Angle(double angle, int type, boolean keepMinus)
    {
        switch (type)
        {
            //弧度
            case 1:
                initByRad(angle,keepMinus);
                break;
            //D.MMSS
            case 2:
                initByDMS(angle, keepMinus);
                break;
            //十进制度
            case 3:
                initByDeg(angle, keepMinus);
                break;
            default:
                break;
        }
    }

    /**
     * 构造函数,根据D MM  SS(D可为负值,MM SS 恒为正),初始化角度
     * @param d 度 可正负
     * @param m 分 正值
     * @param s 秒 正值
     * @param keepMinus 是否保留负号
     */
    public Angle(int d, int m, double s, boolean keepMinus)
    {
        boolean isNegative = (d < 0);
        double d1 = Math.abs(d);
        double deg = d1 + m / 60.0 + s / 3600.0;
        //是负值
        if (isNegative)
        {
            deg = -deg;
        }
        initByDeg(deg, keepMinus);
    }

    /**
     * 显示角度值：d°mm′ss.s″的样式
     * @param digitNum 秒值显示小数位
     * @return d°mm′ss.s″的文本值
     */
    public String showDmmss(int digitNum)
    {
        String dmsStr = showDms(digitNum+4);
        String[] dmsStrs = dmsStr.split("\\.");
        String d = dmsStrs[0];
        String m = dmsStrs[1].substring(0, 2);
        String s = dmsStrs[1].substring(2,digitNum+4);
        //秒有小数点
        if (digitNum > 0)
        {
            s = s.substring(0, 2) + "." + s.substring(2);
        }
        return d + "°" + m + "′" + s + "″";
    }

    /**
     * 显示角度值：d.mmss的样式
     * @param digitNum 结果显示小数位
     * @return d.mmss文本值
     */
    public String showDms(int digitNum)
    {
        double theDeg = Math.abs(this.deg);
        int d = (int)(Math.floor(theDeg));
        double tempVal = (theDeg - d) * 60;
        int m = (int)(Math.floor(tempVal));
        tempVal = (tempVal - m) * 60;
        double s = Math.round(tempVal * Math.pow(10, digitNum - 4)) / Math.pow(10, digitNum - 4);
        //防止1.125999成为1.12600的情况出现
        //s值超60，m进1
        if (s >= 60)
        {
            s -= 60.0;
            m += 1;
            //m值超60，d进1
            if (m >= 60)
            {
                m -= 60;
                d += 1;
            }
            //d值超360，-360
            if (d >= 360)
            {
                d -= 360;
            }
        }

        tempVal = d + m / 100.0 + s / 10000.0;
        tempVal = this.deg < 0 ? -tempVal : tempVal;

        String result = String.format("%." + digitNum + "f", tempVal);
        return result;
    }
    //endregion

    //region 私有方法
    /**
     * 通过弧度初始化
     * @param rad 弧度值
     * @param keepMinus 是否保留负号，默认false
     */
    private void initByRad(double rad, boolean keepMinus)
    {
        //取角度默认范围
        this.rad = Angle.getDefaultRange(rad, keepMinus);
        // 十进制度
        this.deg = Angle.rad2Deg(this.rad, keepMinus);
        //d.mmss
        this.dms = Angle.rad2Dms(this.rad, keepMinus);
        //求dms
        double theDeg = Math.abs(this.deg);
        this.d = (int)(Math.floor(theDeg));
        this.m = (int)(Math.floor((theDeg - this.d) * 60));
        this.s = ((theDeg - this.d) * 60 - this.m) * 60;
        this.d = this.deg<0 ? -this.d : this.d;
    }

    /**
     * 通过deg(十进制度)初始化
     * @param deg 十进制度
     * @param keepMinus 是否保留负号,默认false
     */
    private void initByDeg(double deg, boolean keepMinus)
    {
        this.rad = deg * Math.PI / 180;
        this.initByRad(this.rad, keepMinus);
    }

    /**
     * 通过dms(d.mmss)初始化
     * @param dms dms
     * @param keepMinus 是否保留负号,默认false
     */
    private void initByDMS(double dms, boolean keepMinus)
    {
        boolean isNegative = (dms < 0);
        //取绝对值
        String dms1 = "" + Math.abs(dms);
        String[] tempS = dms1.split("\\.");
        //补足d.mmss
        if (tempS.length == 1)
        {
            dms1 = dms1 + ".00000";
        }
        else
        {
            switch (tempS[1].length())
            {
                case 1:
                    dms1 = dms1 + "0000";
                    break;
                case 2:
                    dms1 = dms1 + "000";
                    break;
                case 3:
                    dms1 = dms1 + "00";
                    break;
                default:
            }//end switch
        }//endif
        tempS = dms1.split("\\.");
        String dStr = tempS[0];
        String mStr = tempS[1].substring(0, 2);
        String sStr = tempS[1].substring(2);
        sStr = sStr.substring(0, 2) + "." + sStr.substring(2);

        this.d = Integer.parseInt(dStr);
        this.m = Integer.parseInt(mStr);
        this.s = Integer.parseInt(sStr);

        this.deg = this.d + this.m / 60.0 + this.s / 3600.0;
        //是负值(最后取)
        if (isNegative)
        {
            this.deg = -this.deg;
        }
        this.initByDeg(this.deg, keepMinus);
    }
    //endregion

}
