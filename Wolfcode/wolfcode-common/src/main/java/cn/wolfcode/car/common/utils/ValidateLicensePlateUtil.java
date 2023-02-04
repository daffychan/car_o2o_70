package cn.wolfcode.car.common.utils;

import org.apache.commons.lang3.StringUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ValidateLicensePlateUtil {

    /**
     * 验证车牌号是否合法
     *
     * @return
     */
    public static boolean isValidateLicensePlate(String LicensePlate) {
        if (StringUtils.isEmpty(LicensePlate)) {
            return false;
        }
        /**
         * 车牌号正则表达式
         */
        String pat = "^[京津沪渝冀豫云辽黑湘皖鲁新苏浙赣鄂桂甘晋蒙陕吉闽贵粤青藏川宁琼使领A-Z]{1}[A-Z]{1}[A-Z0-9]{4}[A-Z0-9挂学警港澳]{1}$";

        Pattern pattern = Pattern.compile(pat);
        Matcher match = pattern.matcher(LicensePlate);
        boolean isMatch = match.matches();
        return isMatch;
    }
}
