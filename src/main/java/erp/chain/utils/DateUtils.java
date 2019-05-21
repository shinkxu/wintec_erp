package erp.chain.utils;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * 时间工具类
 */
public class DateUtils {
    private final static Log logger = LogFactory.getLog(erp.chain.utils.DateUtils.class);

    /**
     * 字符串转换成日期
     */
    public static Date strToDate(String str) {

        String parsePattern = "yyyy-MM-dd HH:mm:ss";
        try {
            return org.apache.commons.lang3.time.DateUtils.parseDate(str,parsePattern);
        } catch (ParseException e) {
            logger.error("时间转换错误：" + e.getMessage(),e);
        }
        return null;
    }
    /**
     * 格式化时间
     */
    public static String formatData(String format , Date date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(format);
        return dateFormat.format(date);
    }
    /**
     * 获取当前天间隔n天日期
     * @return
     */
    public static String getSpaceDate(String format) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(format);
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_MONTH, calendar.get(Calendar.DAY_OF_MONTH) + 0);
        return dateFormat.format(calendar.getTime());
    }
}
