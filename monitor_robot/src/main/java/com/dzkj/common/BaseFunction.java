package com.dzkj.common;

/**
 * Copyright(c),2018-2024,合肥市鼎足空间技术有限公司
 *
 * @author jing.fang
 * @date 2024/2/27 11:29
 * @description 基础函数
 * history
 * <author>    <time>    <version>    <desc>
 * 作者姓名     修改时间     版本号        描述
 */
public class BaseFunction {

    /**
     * 两点相同默认坐标允许误差(1mm)
     */
    private static final double ee = 0.001;

    /**
     * 计算两点方位角[0,2Pi),两点重合时，方位角为0.0
     * @param x1 起点X
     * @param y1 起点Y
     * @param x2 终点X
     * @param y2 终点Y
     * @return 方位角Az(弧度)
     */
    public static double alphaAb(double x1, double y1, double x2, double y2)
    {
        double dX = x2 - x1;
        double dY = y2 - y1;
        //两点重合
        if (samePoint(x1, y1, x2, y2)) {
            return 0.0;
        }
        //不重合
        double directVal = Math.atan2(dY, dX);
        //归整到[0,2Pi)
        directVal = Angle.getDefaultRange(directVal, false);
        return directVal;
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
    public static double distAb(double x1, double y1, double x2, double y2) {
        double dX = x2 - x1;
        double dY = y2 - y1;
        return Math.sqrt(dX * dX + dY * dY);
    }

    public static boolean samePoint(double x1, double y1, double x2, double y2)
    {
        return (samePoint(x1, y1, x2, y2, ee));
    }

    public static boolean samePoint(double x1, double y1, double x2, double y2, double ee)
    {
        double dX = x2 - x1;
        double dY = y2 - y1;
        return ((Math.abs(dX) < ee && Math.abs(dY) < ee));
    }

    /**
     * 通过指定重复次数的字符生成字符串
     * @param repeatChar 需要重复的字符
     * @param repeatNum 重复次数
     */
    public static String getStringByRepeatChar(String repeatChar,int repeatNum)
    {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < repeatNum; i++)
        {
            result.append(repeatChar);
        }
        return result.toString();
    }

    /**
     * 获取指定长度的主字符串
     * 1.主字符串长度小于指定长度strLen(负值按0处理)，用指定的填充字符fillChar填充，
     * 可指定左填数leftFillLen(负值按0处理),左填数+主要字符串长度小于strLen,剩下的右填充,
     * 左填数+主要字符串长度大于strLen,右截取strLen长度字符
     * 2.主字符串长度大于等于指定长度strLen,左截取strLen长度字符
     * @param mainString 主字符串
     * @param strLen 返回字符串总长
     * @param fillChar 要填充的字符
     * @param leftFillLen 填充长度
     */
    public static String getStringByLen(String mainString,int strLen, String fillChar, int leftFillLen)
    {
        String result = "";
        //指定长度为负时，当作0长度处理
        strLen = Math.max(strLen, 0);
        leftFillLen = Math.max(leftFillLen, 0);

        //主字符串长度大于等于指定长度strLen,左截取strLen长度字符
        if (mainString.length() >= strLen) {
            result=mainString.substring(0,strLen);
        } else {//主字符串长度小于指定长度
            //获取指定长度的左填充字符串
            StringBuilder leftFillString= new StringBuilder();
            while(leftFillString.length()< leftFillLen) {
                leftFillString.append(fillChar);
            }
            leftFillString = new StringBuilder(leftFillString.substring(0, leftFillLen));
            result = leftFillString + mainString;
            //左填数+主要字符串长度小于strLen,剩下的右填充
            if (result.length()< strLen) {
                StringBuilder rightFillString = new StringBuilder();
                int rightFillLen = strLen - result.length();
                while (rightFillString.length()< rightFillLen) {
                    rightFillString.append(fillChar);
                }
                rightFillString = new StringBuilder(rightFillString.substring(0, rightFillLen));
                result += rightFillString;
            } else {//左填数+主要字符串长度大于strLen,右截取strLen长度字符
                result = result.substring(result.length() - strLen);
            }
        }
        return result;
    }

    /**
     * 复制一维数组数值
     * @param array 待填充数组
     * @param columnCopy 一维数组对象
     */
    public static void copyArray1(double[] array, double[] columnCopy){
        System.arraycopy(columnCopy, 0, array, 0, columnCopy.length);
    }
    /**
     * 复制二维数组数值
     * @param array 待填充数组
     * @param columnCopy 二维数组对象
     */
    public static void copyArray2(double[][] array, double[][] columnCopy){
        for (int i = 0; i < columnCopy.length; i++) {
            System.arraycopy(columnCopy[i], 0, array[i], 0, columnCopy[0].length);
        }
    }

}
