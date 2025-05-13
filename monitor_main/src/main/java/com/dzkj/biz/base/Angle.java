package com.dzkj.biz.base;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * Copyright(c),2018-2020,合肥市鼎足空间技术有限公司
 *
 * @author liao
 * @date 2020-08-15 17:54
 * @description 角度值
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
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

    /**
     * 构造函数,根据角度类型以及所给角度值(可为负值),初始化角度
     *
     * @param angle 角度值
     * @param type  角度值类型： 1:弧度;2:D.MMSS;3:十进制度
     * @description 构造函数, 根据角度类型以及所给角度值(可为负值), 初始化角度
     * @author liao
     * @date 2020-08-18 11:12
     **/
    public Angle(double angle, int type) {
        switch (type) {
            //弧度
            case 1:
                initlByRad(angle);
                break;
            //D.MMSS
            case 2:
                initlByDMS(angle);
                break;
            //十进制度
            case 3:
                initlByDeg(angle);
                break;
            default:
                break;
        }//end switch

    }

    /**
     * 构造函数,根据D MM  SS(D可为负值,MM SS 恒为正),初始化角度
     *
     * @param d 度 可正负
     * @param m 分 正值
     * @param s 秒 正值
     * @description 构造函数, 根据D MM  SS(D可为负值,MM SS 恒为正),初始化角度
     * @author liao
     * @date 2020-08-18 11:49
     **/
    public Angle(int d, int m, double s) {
        boolean isNegative = (d < 0);
        int d1 = Math.abs(d);
        double deg = d1 + m / 60.0 + s / 3600.0;
        //是负值
        if (isNegative) {
            deg = -deg;
        }//endif
        initlByDeg(deg);
    }

    /**
     * 设置弧度值在[0，2pi)之间
     *
     * @param rad 弧度
     * @return [0，2pi)之间的弧度值
     * @description 设置弧度值在[0，2pi)之间
     * @author liao
     * @date 2020-08-20 15:36
     **/
    public static double getDefaultRange(double rad) {
        double theRad = rad;
        if (theRad > 0) {
            //角度为正值
            while (theRad >= 2 * Math.PI) {
                theRad -= 2 * Math.PI;
            }//end while
        } else {
            //角度为负值
            while (theRad < 0) {
                theRad += 2 * Math.PI;
            }//end while
        }//endif
        return theRad;
    }

    /**
     * 弧度转十进制度[0，360.0),弧度可为负值
     *
     * @param rad 弧度
     * @return 十进制度[0，360.0)
     * @description 弧度转十进制度[0，360.0),弧度可为负值
     * @author liao
     * @date 2020-08-20 15:36
     **/
    public static double rad2deg(double rad){
        double tempRad=Angle.getDefaultRange(rad);
        return tempRad*180.0/ Math.PI;
    }

    /**
     * 弧度转dms[0，360.0),弧度可为负值
     *
     * @param rad 弧度
     * @return dms[0，360.0)
     * @description 弧度转dms[0，360.0),弧度可为负值
     * @author liao
     * @date 2020-08-20 15:36
     **/
    public static double rad2dms(double rad){
        double deg=rad2deg(rad);
        int d = (int) (Math.floor(deg));
        int m = (int) (Math.floor((deg - d) * 60));
        double s = ((deg - d) * 60 - m) * 60;
        return d + m / 100.0 + s / 10000.0;
    }

    /**
     * 十进制度转弧度[0，2Pi),十进制度可为负值
     *
     * @param deg 十进制度
     * @return 弧度[0，2Pi)
     * @description 十进制度转弧度[0，2Pi),十进制度可为负值
     * @author liao
     * @date 2020-08-20 15:36
     **/
    public static double deg2rad(double deg){
        double rad=deg*Math.PI/180.0;
        return Angle.getDefaultRange(rad);
    }

    /**
     * 十进制度转dms[0，360.0),十进制度可为负值
     *
     * @param deg 十进制度
     * @return dms[0，360.0)
     * @description 十进制度转dms[0，360.0),十进制度可为负值
     * @author liao
     * @date 2020-08-20 15:36
     **/
    public static double deg2dms(double deg){
        double rad=deg*Math.PI/180.0;
        return rad2dms(rad);
    }

    /**
     * dms转十进制度[0，360.0),dms可为负值
     *
     * @param dms 度分秒
     * @return deg[0，360.0)
     * @description dms转十进制度[0，360.0),dms可为负值
     * @author liao
     * @date 2020-08-20 15:36
     **/
    public static double dms2deg(double dms){
        boolean isNegative = (dms < 0);
        double dms1 = Math.abs(dms);
        int d = (int) (Math.floor(dms1));
        int m = (int) (Math.floor((dms1 - d) * 100));
        double s =((dms1 - d) * 100 - m) * 100;
        double deg=  d + m / 60.0 + s / 3600.0;
        //是负值(最后取)
        if (isNegative) {
            deg = -deg;
        }//end if
        //先转为弧度，限制角度范围
        double rad=deg2rad(deg);
        return rad2deg(rad);
    }

    /**
     * dms转弧度[0，2Pi),dms可为负值
     *
     * @param dms 度分秒
     * @return rad[0，2Pi)
     * @description dms转弧度[0，2Pi),dms可为负值
     * @author liao
     * @date 2020-08-20 15:36
     **/
    public static double dms2rad(double dms){
        double deg= dms2deg(dms);
        return deg2rad(deg);
    }

    /**
     * 通过弧度初始化
     *
     * @param rad 弧度值
     * @description 通过弧度初始化
     * @author liao
     * @date 2020-08-18 11:29
     **/
    private void initlByRad(double rad) {
        //取角度默认范围
        this.rad =Angle.getDefaultRange(rad) ;
        // 十进制度
        this.deg = Angle.rad2deg(this.rad);
        //d.mmss
        this.dms=Angle.rad2dms(this.rad);
        //求dms
        this.d = (int) (Math.floor(deg));
        this.m = (int) (Math.floor((deg - d) * 60));
        this.s = ((deg - d) * 60 - m) * 60;
    }

    /**
     * 通过D.MMSS初始化
     *
     * @param dms D.MMSS
     * @description 通过D.MMSS初始化
     * @author liao
     * @date 2020-08-18 11:29
     **/
    private void initlByDMS(double dms) {
        boolean isNegative = (dms < 0);
        //取绝对值
        String dms1=""+Math.abs(dms);
        String[] tempS=dms1.split("\\.");
        //补足d.mmss
        if(tempS.length==1){
            dms1=dms1 +".00000";
        }else{
            switch (tempS[1].length()){
                case 1:
                    dms1=dms1 +"0000";
                    break;
                case 2:
                    dms1=dms1 +"000";
                    break;
                case 3:
                    dms1=dms1 +"00";
                    break;
            }//end switch
        }//endif
        tempS=dms1.split("\\.");
        String dStr=tempS[0];
        String mStr=tempS[1].substring(0,2);
        String sStr=tempS[1].substring(2);
        sStr=sStr.substring(0,2) + "." + sStr.substring(2);

        this.d = Integer.parseInt(dStr);
        this.m = Integer.parseInt(mStr);
        this.s=Double.parseDouble(sStr);

        this.deg = this.d + this.m / 60.0 + this.s / 3600.0;
        //是负值(最后取)
        if (isNegative) {
            this.deg = -this.deg;
        }//end if
        initlByDeg(this.deg);
    }

    /**
     * 通过deg(十进制度)初始化
     *
     * @param deg deg
     * @description 通过deg(十进制度)初始化
     * @author liao
     * @date 2020-08-18 11:29
     **/
    private void initlByDeg(double deg) {
        this.rad = deg * Math.PI / 180;
        initlByRad(this.rad);
    }

    /**
     * 显示角度值：d°mm′ss.s″的样式
     *
     * @param digitNum 结果显示小数位
     * @description 显示角度值：d°mm′ss.s″的样式
     * @author liao
     * @date 2020-09-24 15:32
     **/
    public String showDMMSS(int digitNum) {
        String dmsStr=showDMS(digitNum);
        String d=dmsStr.split("\\.")[0];
        String m=dmsStr.split("\\.")[1].substring(0,2);
        String s=dmsStr.split("\\.")[1].substring(2);
        //秒有小数点
        if(digitNum>4) {
            s=s.substring(0,2) +"." + s.substring(2);
        }//endif
        return  d + "°" + m + "′" + s + "″";
    }

    /**
     * 显示角度值：d.mmss的样式
     *
     * @param digitNum 结果显示小数位
     * @description 显示角度值：d.mmss的样式
     * @author liao
     * @date 2020-09-24 15:32
     **/
    public String showDMS(int digitNum) {
        int d = (int) (Math.floor(deg));
        double tempVal=(deg-d)*60;
        int m = (int) (Math.floor(tempVal));
        tempVal=(tempVal-m)*60;
        double s = Math.round(tempVal*Math.pow(10,digitNum-4))/Math.pow(10,digitNum-4);
        //防止1.125999成为1.12600的情况出现
        //s值超60，m进1
        if (s >= 60) {
            s-=60.0;
            m+=1;
            //m值超60，d进1
            if(m>=60){
                m-=60;
                d+=1;
            }//endif
            //d值超360，-360
            if(d>=360){
                d-=360;
            }//endif
        }//endif

        tempVal=d+m/100.0+s/10000.0;
        return BaseFunction.double2String(tempVal,digitNum);
    }
}//end class