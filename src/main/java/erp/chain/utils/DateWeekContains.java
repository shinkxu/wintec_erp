package erp.chain.utils;

import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;

/**
 * Created by songzhiqiang on 2017/1/24.
 */
public class DateWeekContains {
    public static HashMap<String,Object> dateContains(Date oldStartDate,Date oldEndDate,Date newStartDate,Date newEndDate){
        HashMap<String,Object> map = new HashMap<String, Object>();
        //1.现结束时间等于原起始时间
        if(newEndDate.equals(oldStartDate)){
            map.put("includeStartDate",newEndDate);
            map.put("includeEndDate",newEndDate);
        }
        //2.现起始时间小于原起始时间，现结束时间大于原起始时间，小于原结束时间
        else if(newEndDate.after(oldStartDate) && newEndDate.before(oldEndDate) &&newStartDate.before(oldStartDate)){
            map.put("includeStartDate",oldStartDate);
            map.put("includeEndDate",newEndDate);
        }
        //3.现起始时间小于原起始时间，现结束时间等于原结束时间
        else if(newEndDate.equals(oldEndDate) && newStartDate.before(oldStartDate)){
            map.put("includeStartDate",oldStartDate);
            map.put("includeEndDate",oldEndDate);
        }
        //4.现起始时间小于原起始时间，现结束时间大于原结束时间
        else if(newEndDate.after(oldEndDate) && newStartDate.before(oldStartDate)){
            map.put("includeStartDate",oldStartDate);
            map.put("includeEndDate",oldEndDate);
        }
        //5.现起始时间等于原起始时间，现结束时间小于原结束时间
        else if(newStartDate.equals(oldStartDate) && newEndDate.before(oldEndDate)){
            map.put("includeStartDate",oldStartDate);
            map.put("includeEndDate",newEndDate);
        }
        //6.现起始时间等于原起始时间，现结束时间等于原结束时间
        else if(newStartDate.equals(oldStartDate) && newEndDate.equals(oldEndDate)){
            map.put("includeStartDate",oldStartDate);
            map.put("includeEndDate",oldEndDate);
        }
        //7.现起始时间等于原起始时间，现结束时间大于原结束时间
        else if(newStartDate.equals(oldStartDate) && newEndDate.after(oldEndDate)){
            map.put("includeStartDate",oldStartDate);
            map.put("includeEndDate",oldEndDate);
        }
        //8.现起始时间大于原起始时间，现结束时间小于原结束时间
        else if(newStartDate.after(oldStartDate) && newEndDate.before(oldEndDate)){
            map.put("includeStartDate",newStartDate);
            map.put("includeEndDate",newEndDate);
        }
        //9.现起始时间大于原起始时间，小于原结束时间，现结束时间等于原结束时间
        else if(newStartDate.after(oldStartDate) && newStartDate.before(oldEndDate) && newEndDate.equals(oldEndDate)){
            map.put("includeStartDate",newStartDate);
            map.put("includeEndDate",newEndDate);
        }
        //10.现起始时间大于原起始时间，小于原结束时间，现结束时间大于原结束时间
        else if(newStartDate.after(oldStartDate) && newStartDate.before(oldEndDate) && newEndDate.after(oldEndDate)){
            map.put("includeStartDate",newStartDate);
            map.put("includeEndDate",oldEndDate);
        }
        //11.现起始时间等于原结束时间
        else if(newStartDate.equals(oldEndDate)){
            map.put("includeStartDate",newStartDate);
            map.put("includeEndDate",newStartDate);
        }
        //12.现结束时间等于现起始时间，并且在原时间段范围内，包括两边缘时间
        else {
            map.put("includeStartDate",newStartDate);
            map.put("includeEndDate",newEndDate);
        }
        return map;
    }
    /**
     * 促销 判断日期 星期
     * @param map
     * @param oldWeeks
     * @param newWeeks
     * @return
     */
    public static boolean weekContains(HashMap<String,Object> map , String oldWeeks , String[] newWeeks){
        boolean flag = false;
        Date includeStartDate = (Date)map.get("includeStartDate");
        Date includeEndDate = (Date) map.get("includeEndDate");
        Calendar dayc1 = Calendar.getInstance();
        Calendar dayc2 = Calendar.getInstance();
        dayc1.setTime(includeStartDate); //设置calendar的日期
        dayc2.setTime(includeEndDate);
        String[] weeks1 = oldWeeks.split(",");
        StringBuffer weeks = new StringBuffer();
        for (int i = 0; i < weeks1.length; i++) {
            for (int j = 0; j < newWeeks.length; j++) {
                if (weeks1[i].equals(newWeeks[j]))
                    weeks.append(weeks1[i]);
            }
        }
        for (; dayc1.compareTo(dayc2) <= 0;){
            String week = "";
            int weekDay = dayc1.get(Calendar.DAY_OF_WEEK);
            if(Calendar.MONDAY == weekDay) week = "1";
            if(Calendar.TUESDAY == weekDay) week = "2";
            if(Calendar.WEDNESDAY == weekDay) week = "3";
            if(Calendar.THURSDAY == weekDay) week = "4";
            if(Calendar.FRIDAY == weekDay) week = "5";
            if(Calendar.SATURDAY == weekDay) week = "6";
            if(Calendar.SUNDAY == weekDay) week = "7";
            if(weeks.toString().contains(week)){
                flag = true;
            }
            dayc1.add(Calendar.DAY_OF_MONTH, 1);  //加1天
        }
        return flag;
    }

    /**
     * 卡券活动 判断日期 星期
     * @param map
     * @param oldWeeks
     * @param newWeeks
     * @return
     */
    public static boolean weekContainsCardActivity(HashMap<String,Object> map , String oldWeeks , String newWeeks){
        boolean flag = false;
        Date includeStartDate = (Date)map.get("includeStartDate");
        Date includeEndDate = (Date) map.get("includeEndDate");
        Calendar dayc1 = Calendar.getInstance();
        Calendar dayc2 = Calendar.getInstance();
        dayc1.setTime(includeStartDate); //设置calendar的日期
        dayc2.setTime(includeEndDate);
        String[] weeks1 = oldWeeks.split(",");
        StringBuffer weeks = new StringBuffer();
        for (int i = 0; i < weeks1.length; i++) {
            for (int j = 0; j < newWeeks.split(",").length; j++) {
                if (weeks1[i].equals(newWeeks.split(",")[j]))
                    weeks.append(weeks1[i]);
            }
        }
        for (; dayc1.compareTo(dayc2) <= 0;){
            String week = "";
            int weekDay = dayc1.get(Calendar.DAY_OF_WEEK);
            if(Calendar.MONDAY == weekDay) week = "1";
            if(Calendar.TUESDAY == weekDay) week = "2";
            if(Calendar.WEDNESDAY == weekDay) week = "3";
            if(Calendar.THURSDAY == weekDay) week = "4";
            if(Calendar.FRIDAY == weekDay) week = "5";
            if(Calendar.SATURDAY == weekDay) week = "6";
            if(Calendar.SUNDAY == weekDay) week = "7";
            if(weeks.toString().contains(week)){
                flag = true;
            }
            dayc1.add(Calendar.DAY_OF_MONTH, 1);  //加1天
        }
        return flag;
    }

    /**
     * 判断该日期的星期是否在星期数组里面，在返回 false，不在返回true
     * @param date
     * @param weeks
     * @return
     */
    public static boolean dateWeekCompare(Date date , String[] weeks){
        Calendar day = Calendar.getInstance();
        day.setTime(date);
        String week = "";
        int weekDay = day.get(Calendar.DAY_OF_WEEK);
        if(Calendar.MONDAY == weekDay) week = "1";
        if(Calendar.TUESDAY == weekDay) week = "2";
        if(Calendar.WEDNESDAY == weekDay) week = "3";
        if(Calendar.THURSDAY == weekDay) week = "4";
        if(Calendar.FRIDAY == weekDay) week = "5";
        if(Calendar.SATURDAY == weekDay) week = "6";
        if(Calendar.SUNDAY == weekDay) week = "7";

        for (int i = 0; i < weeks.length; i++) {
            if(weeks[i].equals(week)){
                return false;
            }
        }
        return true;
    }

    /**
     *添加会员活动 买A赠B  第二件打折   单品优惠 , 判断开始到结束时间段内包含的星期 是否包含选择的星期。
     * @return
     */
    public static boolean dateWeekssIncludeWeekss(Date newStartDate,Date newEndDate,String weekss){

        SimpleDateFormat sim = new SimpleDateFormat("yyyy-MM-dd");

        Calendar c_begin = new GregorianCalendar();
        Calendar c_end = new GregorianCalendar();
        DateFormatSymbols dfs = new DateFormatSymbols();
        String[] weeks1 = dfs.getWeekdays();

        String newStart = sim.format(newStartDate).toString();
        String newEnd = sim.format(newEndDate).toString();
        c_begin.set(Integer.parseInt(newStart.substring(0,4)), Integer.parseInt(newStart.substring(5,7))-1, Integer.parseInt(newStart.substring(8,10))); //c_begin.set(2016,10,12)  Calendar的月从0-11，所以4月是3.
        c_end.set(Integer.parseInt(newEnd.substring(0,4)), Integer.parseInt(newEnd.substring(5,7))-1, Integer.parseInt(newEnd.substring(8,10))); //c_end.set(2016,12,10)  Calendar的月从0-11，所以5月是4.

        c_end.add(Calendar.DAY_OF_YEAR, 1); // 结束日期下滚一天是为了包含最后一天
        String dateIncludeWeeks = "";
        while (c_begin.before(c_end)) {
            if("星期一".equals(weeks1[c_begin.get(Calendar.DAY_OF_WEEK)])){dateIncludeWeeks+="1,";}
            if("星期二".equals(weeks1[c_begin.get(Calendar.DAY_OF_WEEK)])){dateIncludeWeeks+="2,";}
            if("星期三".equals(weeks1[c_begin.get(Calendar.DAY_OF_WEEK)])){dateIncludeWeeks+="3,";}
            if("星期四".equals(weeks1[c_begin.get(Calendar.DAY_OF_WEEK)])){dateIncludeWeeks+="4,";}
            if("星期五".equals(weeks1[c_begin.get(Calendar.DAY_OF_WEEK)])){dateIncludeWeeks+="5,";}
            if("星期六".equals(weeks1[c_begin.get(Calendar.DAY_OF_WEEK)])){dateIncludeWeeks+="6,";}
            if("星期日".equals(weeks1[c_begin.get(Calendar.DAY_OF_WEEK)])){dateIncludeWeeks+="7,";}
            if(dateIncludeWeeks.length()>=14){break;}
            c_begin.add(Calendar.DAY_OF_YEAR, 1);
        }

        String[] weeks = weekss.split(",");//选择的星期
        String[] dateWeekss = dateIncludeWeeks.split(",");//活动日期对应的星期

        for(int i = 0 ; i < weeks.length ; i++){
            boolean flag = true;

            for(int j = 0 ; j < dateWeekss.length ; j++){
                if(weeks[i] == dateWeekss[j]){//
                    flag = false;
                }
            }

            if(flag){
                return true;
            }

        }
        return false;
    }

    /**
     * 获取重叠的星期
     * @return
     */
    public static String getEqWeeks(String newWeek,String oldWeek){
        String eqWeek = "";
        if(newWeek != null && oldWeek != null){
            String[] newWeeks = newWeek.split(",");
            String[] oldWeeks = oldWeek.split(",");
            for(int i=0;i<newWeeks.length;i++){
                if(newWeeks[i].equals(oldWeeks[i]) && "1".equals(newWeeks[i])){
                    eqWeek += i + ",";
                }
            }
        }
        return eqWeek;
    }
}
