package com.iqianbang.fanpai.utils;

import java.math.BigDecimal;
import java.text.DecimalFormat;

/**
 * 作者：lxn on 2015/12/16 14:25
 * 描述：Utilone ————数字金额运算的工具类
 */
public class MathUtil {
    //默认获取的小数点的后2位
    private static final int FORMAT = 2;

    /**
     * BigDecimal类型的数字和int 类型相乘之后的截取小数点后2位的结果
     * @param price  BigDecimal类型 eg:商品单价
     * @param count  int类型  eg:商品数量
     * @return
     */
    public static BigDecimal getMultiply(BigDecimal price, int count) {
        BigDecimal allprice = price.multiply(new BigDecimal(count));
        allprice= subDigit(allprice,FORMAT);
        return allprice;
    }
    /**
     * BigDecimal类型的数字和int 类型相乘之后以四舍五入的方式取整
     * @param price  BigDecimal类型 eg:商品单价
     * @param count  int类型  eg:商品数量
     * @return  整数结果BigDecimal类型
     */
    public static BigDecimal getMultiplyToInt(BigDecimal price, int count) {
        BigDecimal allprice = price.multiply(new BigDecimal(count));
        allprice= subDigit(allprice,0);
        return allprice;
    }

    /**
     * BigDecimal类型的数字和int 类型相乘之后
     * @param price BigDecimal类型 eg:商品单价
     * @param count int类型  eg:商品数量
     * @param digit int类型  获取小数点后几位
     * @return
     */
   public static BigDecimal getMultiply(BigDecimal price, int count, int digit) {
        BigDecimal allprice = price.multiply(new BigDecimal(count));
        allprice= subDigit(allprice,digit);
        return allprice;
    }

    /**
     * 截取到小数点后几位
     * @param price  BigDecimal类型的数字
     * @param digit  要获取的小数点后几位
     * @return
     */
    public static BigDecimal subDigit(BigDecimal price, int digit){
        BigDecimal returnBigdecimal=price.setScale(digit, BigDecimal.ROUND_HALF_UP);
        return returnBigdecimal;
    }
    
    
    public static BigDecimal subDouble(double price, int digit){
    	BigDecimal b = new BigDecimal(price);
    	BigDecimal returnBigdecimal=b.setScale(digit, BigDecimal.ROUND_HALF_UP);
    	return returnBigdecimal;
    }

    public static BigDecimal subString(String price, int digit){
    	BigDecimal b = new BigDecimal(price);
    	BigDecimal returnBigdecimal=b.setScale(digit, BigDecimal.ROUND_HALF_UP);
    	return returnBigdecimal;
    }
    /**
     * 把一个double 数字转换成 小数点后2位显示的
     * @param math
     * @return
     */
    public static String mathTodecimaltwo(double math){
        DecimalFormat df   = new DecimalFormat("######0.00");
        return df.format(math);
    }

}
