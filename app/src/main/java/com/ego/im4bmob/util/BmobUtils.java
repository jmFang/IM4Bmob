package com.ego.im4bmob.util;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created on 17/7/4 18:13
 */

public class BmobUtils {
    public static final String SMS_TEMPLATE = "smscode";
    /**
     * 正则表达式：验证手机号
     */
    public static final String REGEX_MOBILE = "^((17[0-9])(14[0-9])|(13[0-9])|(15[^4,\\D])|(18[0,5-9]))\\d{8}$";

    public static void toast(Context context, String msg) {
        Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
    }


    public static void log(String log) {
        Log.i("BmobLogin", log);
    }

    public static void wtf(Throwable throwable) {
        Log.wtf("BmobLogin", throwable);
    }

    /**
     * 校验手机号
     *
     * @param mobile
     * @return 校验通过返回true，否则返回false
     */
    public static boolean isMobile(String mobile) {
        return Pattern.matches(REGEX_MOBILE, mobile);
    }


    /**
     * 判断是否含有特殊字符
     *
     * @param str
     * @return true为包含，false为不包含
     */
    public static boolean hasSpecialChar(String str) {
        String regEx = "[ _`~!@#$%^&*()+=|{}':;',\\[\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]|\n|\r|\t";
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(str);
        return m.find();
    }


    /**
     * 密码是否符合要求
     *
     * @param password
     * @return
     */
    public static boolean isConformable(String password) {
        if (TextUtils.isEmpty(password) || password.length() < 6 || password.length() > 20 || hasSpecialChar(password))
            return false;
        return true;
    }


    /**
     * 产生n位随机数
     *
     * @param n
     * @return
     */
    public static long generateRandomNumber(int n) {
        if (n < 1) {
            throw new IllegalArgumentException("随机数位数必须大于0");
        }
        return (long) (Math.random() * 9 * Math.pow(10, n - 1)) + (long) Math.pow(10, n - 1);
    }

    /**
     * 随机账号
     *
     * @return
     */
    public static String getRandomAccount() {
        return "U" + generateRandomNumber(10);
    }
}
