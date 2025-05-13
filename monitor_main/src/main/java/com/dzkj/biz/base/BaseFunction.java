package com.dzkj.biz.base;

import com.dzkj.biz.survey.vo.OnlineCfgResultVo;

import java.util.List;

/**
 * Copyright(c),2018-2020,合肥市鼎足空间技术有限公司
 *
 * @author liao
 * @date 2020-08-15 17:39
 * @description 基础计算函数
 * history
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
public class BaseFunction {
    //两点相同默认坐标允许误差(1mm)
    private static final double ee = 0.001;
    //region 公共方法

    /**
     * @param data 字符串数据
     * @return boolean
     * @throws
     * @description 功能描述:判断数据是否是数值(正负值,含小数)
     * @author
     * @date 2020-08-23 22:10
     * @see
     **/
    public static boolean isNumeric(String data) {
        String pattern = "^[+-]?(\\d*)(\\.\\d*)?$";
        return data.matches(pattern);
    }

    /**
     * 功能说明:计算两点距离
     *
     * @param x1 起点X
     * @param y1 起点Y
     * @param x2 终点X
     * @param y2 终点Y
     * @return 距离dist
     * @author chance
     * @description 计算两点距离
     * @date 2019-12-03 15:59
     **/
    public static double distAB(double x1, double y1, double x2, double y2) {
        double dX = x2 - x1;
        double dY = y2 - y1;
        return Math.sqrt(dX * dX + dY * dY);
    }

    /**
     * @param x1 起点X
     * @param y1 起点Y
     * @param x2 终点X
     * @param y2 终点Y
     * @return boolean
     * @throws
     * @description 功能描述:指定两点是否重合(允许坐标误差1mm)
     * @author liao
     * @date 2020-08-22 08:56
     * @see
     **/
    public static boolean samePoint(double x1, double y1, double x2, double y2) {
        return (samePoint(x1, y1, x2, y2, ee));
    }

    /**
     * @param x1 起点X
     * @param y1 起点Y
     * @param x2 终点X
     * @param y2 终点Y
     * @param ee 允许误差
     * @return boolean
     * @throws
     * @description 功能描述:指定两点是否重合
     * @author liao
     * @date 2020-08-22 08:56
     * @see
     **/
    public static boolean samePoint(double x1, double y1, double x2, double y2, double ee) {
        double dX = x2 - x1;
        double dY = y2 - y1;
        return ((Math.abs(dX) < ee && Math.abs(dY) < ee));
    }

    /**
     * 功能说明: 计算两点方位角[0,2Pi),两点重合时，方位角为0.0
     *
     * @param x1 起点X
     * @param y1 起点Y
     * @param x2 终点X
     * @param y2 终点Y
     * @return 方位角Az
     * @author chance
     * @description 计算两点方位角[0, 2Pi),两点重合时，方位角为0.0
     * @date 2019-12-03 16:00
     **/
    public static double alphaAB(double x1, double y1, double x2, double y2) {
        double dX = x2 - x1;
        double dY = y2 - y1;
        //两点重合
        if (samePoint(x1, y1, x2, y2)) {
            return 0.0;
        } //endif
        //不重合
        double directVal = Math.atan2(dY, dX);
        //归整到[0,2Pi)
        directVal = Angle.getDefaultRange(directVal);
        return directVal;
    }

    /**
     * 功能说明:导线方位角计算[0,2Pi)
     *
     * @param lastLineDirection 上一导线方位角
     * @param beta              导线夹角
     * @param angleType         夹角类别：0左角；1右角
     * @return 待计算方位角Az
     * @author chance
     * @description 导线方位角计算[0, 2Pi)
     * @date 2019-12-03 16:01
     **/
    public static double alphaAB(double lastLineDirection, double beta, int angleType) {
        double directVal = 0.0;
        if ((angleType == 1)) {
            directVal = lastLineDirection + Math.PI + beta;
        } else {
            directVal = lastLineDirection - Math.PI - beta;
        }//endif
        //归整到[0,2Pi)
        directVal = Angle.getDefaultRange(directVal);
        return directVal;
    }

    /**
     * 功能说明:坐标旋转变换（局部工程坐标到国家坐标)
     *
     * @param X0    工程坐标原点X(在国家坐标系中)
     * @param Y0    工程坐标原点Y(在国家坐标系中)
     * @param alpha 工程坐标系在国家坐标系中的旋转方位角
     * @param pointXY 待转换点XY值列表，执行后返回转换后的结果
     * @author chance
     * @description 坐标旋转变换（局部工程坐标到国家坐标)
     * @date 2019-12-03 16:11
     **/
    public static void coordTransfer(double X0, double Y0, double alpha, List<Double> pointXY) {
        double x=pointXY.get(0);
        double y=pointXY.get(1);
        double destX = X0 + x * Math.cos(alpha) + y * Math.sin(alpha);
        double destY = Y0 + y * Math.cos(alpha) - x * Math.sin(alpha);

        pointXY.set(0,destX);
        pointXY.set(1,destY);
    }

    /**
     * 功能说明:坐标旋转变换（局部工程坐标到国家坐标，坐标原点不变)
     *
     * @param alpha 工程坐标系在国家坐标系中的旋转方位角
     * @param pointXY 待转换点XY值列表，执行后返回转换后的结果
     * @author chance
     * @description 坐标旋转变换（局部工程坐标到国家坐标，坐标原点不变)
     * @date 2019-12-03 16:11
     **/
    public static void coordTransfer(double alpha, List<Double>pointXY) {
        coordTransfer(0.0,0.0,alpha,pointXY);
    }

    /** 
     * 双精度数值转换为文本
     * 
     * @description 
     * @exception 
     * @see 
     * @author liao
     * @date 2020-09-24 16:38
     * @param val 双精度数值
     * @param digitalNum 小数位数
     * @return java.lang.String 转换结果
    **/
    public static String double2String(double val,int digitalNum){
       return String.format("%." + digitalNum + "f", val);
    }

    /**
     * 验证dms数值合法性(度范围:(-360,360),分、秒范围:[0,60))
     *
     * @description 验证dms数值合法性(度范围:(-360,360),分、秒范围:[0,60))
     * @exception
     * @see
     * @author liao
     * @date 2020-09-24 16:38
     * @param dms d.mmss
     * @return java.lang.String 验证结果：空字符""表示正常
     **/
    public static String validateDMS(String dms){
        String result="";

        //数值验证
        if(!isNumeric(dms)){
            result="角度值含有非法字符";
            return result;
        }//endif

        //范围验证
        //去除负号
        String dms1="" + Math.abs(Double.parseDouble(dms));
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

        String dStr=dms1.split("\\.")[0];
        String mStr=dms1.split("\\.")[1].substring(0,2);
        String sStr=dms1.split("\\.")[1].substring(2);
        sStr=sStr.substring(0,2) + "." + sStr.substring(2);

        int d=Integer.parseInt(dStr);
        double dmsVal= Math.abs(Double.parseDouble(dms1));
        if(d>=360){
            result="角度值超出范围(-360,360)";
            return result;
        }//endif
        //分值、秒值范围验证
        int m=Integer.parseInt(mStr);
        if(m>=60){
            result="分值超出范围[0,60)";
            return result;
        }//endif
        double s=Double.parseDouble(sStr);
        if(s>=60){
            result="秒值超出范围[0,60)";
            return result;
        }//endif

        return result;
    }

    /**
     * 计算测站点、测点间的方位角、竖直角;列表(Ha,Va)
     * @param result result 测量结果对象
     * @param results results
     */
    public static void calcHaVa(OnlineCfgResultVo result, List<Double> results)
    {
        double dx = result.getPtX() - result.getStX();
        double dy = result.getPtY() - result.getStY();
        double dz = result.getPtZ() - result.getStZ() + result.getHt() - result.getHi();

        double hd = Math.sqrt(dx * dx + dy * dy);
        double ha = Math.atan2(dy, dx);
        double va = Math.PI * 0.5 - Math.atan2(dz, hd);
        //是倒镜
        if (!result.isFace1())
        {
            ha = ha - Math.PI;
            va = Math.PI * 2 - va;
        }

        ha = new com.dzkj.common.Angle(ha, 1, false).getRad();
        va = new com.dzkj.common.Angle(va, 1, false).getRad();

        results.clear();
        results.add(ha);
        results.add(va);
    }
    //endregion

    //region 私有方法
    //endregion 私有方法

}//end class
