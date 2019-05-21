package erp.chain.utils;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.Validate;

import java.math.BigInteger;
import java.text.SimpleDateFormat;

/**
 * hxh
 * 2016/3/1.
 */

public class Args extends Validate {
    /**
     * 是否数字，
     */
    public static void isNumber(String val) {
        if (StringUtils.trimToNull(val) == null) {

        }
    }

    /**
     * 是否整数，
     * val = "  " -> false
     * val = null -> false
     * val = "" -> false
     * val = "e44" -> false
     * val = "23" -> true
     */
    public static void isInteger(String val, String mes) {
        try {
            new BigInteger(val);
        } catch (Exception e) {
            throw new IllegalArgumentException(mes);
        }
    }

    /**
     * 是否日期
     * val = "  " -> false
     * val = null -> false
     * val = "" -> false
     * val = "e44" -> false
     * val = "1970-10-12 13:14:00" -> true
     *
     * @param mes
     */
    public static void isDate(String date, String mes) {

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            format.parse(date);
        } catch (Exception e) {
            throw new IllegalArgumentException(mes);
        }
    }

    /**
     * 判断validStr是否在in里边
     * @param validStr == null 等价于 validStr == ""
     * @param in == null 则false
     * @param mes
     */
    public static void isIn(String validStr, String[] in, String mes) {
        validStr = StringUtils.trimToEmpty(validStr);

        if (in == null){
            throw new IllegalArgumentException(mes);
        }
        for (String i: in){
            if (validStr.equals(i)){
                return;
            }
        }
        throw new IllegalArgumentException(mes);
    }
}
