package erp.chain.service.system;

import com.saas.common.exception.ServiceException;
import erp.chain.mapper.system.WebServiceTenantMonitorMapper;
import org.apache.commons.lang.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import saas.api.common.ApiRest;

import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 统计商户数
 * Created with IntelliJ IDEA.
 * User: AnCong
 * Date: 2017/3/13
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class WebServiceTenantMonitorService{
    @Autowired
    private WebServiceTenantMonitorMapper webServiceTenantMonitorMapper;

    /**
     * 统计机构数量
     *
     * @param tenantType：商户类型(0-全部，1-正式商户，2-测试商户)
     * @return
     */
    @Transactional(readOnly = true)
    public ApiRest statisticsBranchCount(String tenantType){
        ApiRest apiRest = new ApiRest();
        Integer branchCount = webServiceTenantMonitorMapper.branchCount(tenantType);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        String startDate;
        String endDate;
        String yesterdayBegin = simpleDateFormat.format(new Date(new Date().getTime() - 24 * 60 * 60 * 1000)).substring(0, 10) + " 00:00:00";
        String yesterdayEnd = simpleDateFormat.format(new Date(new Date().getTime() - 24 * 60 * 60 * 1000)).substring(0, 10) + " 23:59:59";
        Integer yesterdayBranchCount;
        yesterdayBranchCount = webServiceTenantMonitorMapper.branchCountCondition(yesterdayBegin, yesterdayEnd, tenantType);

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(DateUtils.addMonths(new Date(), -1));
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        startDate = simpleDateFormat.format(calendar.getTime()).substring(0, 10) + " 00:00:00";
        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
        endDate = simpleDateFormat.format(calendar.getTime()).substring(0, 10) + " 23:59:59";
        Integer branchMonth;
        branchMonth = webServiceTenantMonitorMapper.branchCountCondition(startDate, endDate, tenantType);
        Map<String, String> map = new HashMap<String, String>();
        map.put("branchCount", branchCount.toString());
        map.put("yesterdayBranchCount", yesterdayBranchCount.toString());
        map.put("branchMonth", branchMonth.toString());
        apiRest.setData(map);
        apiRest.setIsSuccess(true);
        apiRest.setMessage("查询成功！");
        return apiRest;
    }

    /**
     * 统计机构数量
     *
     * @param beginDateString：开始时间
     * @param endDateString：结束时间
     * @param flag：标志位(daySum-日累计，dayIncrease-日增长，monthIncrease-月增长)
     * @param branchType：机构类型(0-全部，1-正式商户的机构，2-测试商户的机构)
     * @return
     */
    @Transactional(readOnly = true)
    public ApiRest doBranchChartSearch(String beginDateString, String endDateString, String flag, String branchType){
        ApiRest apiRest = new ApiRest();
        try{
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date beginDate = simpleDateFormat.parse(beginDateString + " 00:00:00");
            Date endDate = simpleDateFormat.parse(endDateString + " 23:59:59");
            List<Map<String, String>> data = new ArrayList<Map<String, String>>();
            if("daySum".equals(flag) || "dayIncrease".equals(flag)){
                int days = (int)((endDate.getTime() - beginDate.getTime()) / (1000 * 3600 * 24));
                String begin = simpleDateFormat.format(beginDate);
                for(int i = 0; i <= days; i++){
                    if("dayIncrease".equals(flag)){
                        begin = simpleDateFormat.format(beginDate);
                    }
                    String end = simpleDateFormat.format(beginDate).substring(0, 10) + " 23:59:59";
                    Map<String, String> map = new HashMap<String, String>();
                    map.put("chartDate", simpleDateFormat.format(beginDate).substring(0, 10));
                    Integer branchCount = webServiceTenantMonitorMapper.branchCountCondition(begin, end, branchType);
                    map.put("branchCount", branchCount.toString());
                    data.add(map);
                    beginDate = DateUtils.addDays(beginDate, 1);
                }
            }
            if("monthIncrease".equals(flag)){
                Calendar beginCalendar = Calendar.getInstance();
                beginCalendar.setTime(beginDate);
                Calendar endCalendar = Calendar.getInstance();
                endCalendar.setTime(endDate);
                int monthNumber = (endCalendar.get(Calendar.YEAR) - beginCalendar.get(Calendar.YEAR)) * 12 + endCalendar.get(Calendar.MONTH) - beginCalendar.get(Calendar.MONTH);
                for(int i = 0; i <= monthNumber; i++){
                    Date begin = null;
                    Date end = null;
                    if(i == 0){
                        begin = beginCalendar.getTime();
                        beginCalendar.set(Calendar.DAY_OF_MONTH, beginCalendar.getActualMaximum(Calendar.DAY_OF_MONTH));
                        beginCalendar.set(Calendar.HOUR, 23);
                        beginCalendar.set(Calendar.HOUR_OF_DAY, 23);
                        beginCalendar.set(Calendar.MINUTE, 59);
                        beginCalendar.set(Calendar.SECOND, 59);
                        end = beginCalendar.getTime();
                    }
                    else if(i != monthNumber){
                        beginCalendar.set(Calendar.DAY_OF_MONTH, 1);
                        beginCalendar.set(Calendar.HOUR_OF_DAY, 0);
                        beginCalendar.set(Calendar.MINUTE, 0);
                        beginCalendar.set(Calendar.SECOND, 0);
                        begin = beginCalendar.getTime();
                        beginCalendar.set(Calendar.DAY_OF_MONTH, beginCalendar.getActualMaximum(Calendar.DAY_OF_MONTH));
                        beginCalendar.set(Calendar.HOUR_OF_DAY, 23);
                        beginCalendar.set(Calendar.MINUTE, 59);
                        beginCalendar.set(Calendar.SECOND, 59);
                        end = beginCalendar.getTime();
                    }
                    else{
                        beginCalendar.set(Calendar.DAY_OF_MONTH, 1);
                        beginCalendar.set(Calendar.HOUR_OF_DAY, 0);
                        beginCalendar.set(Calendar.MINUTE, 0);
                        beginCalendar.set(Calendar.SECOND, 0);
                        begin = beginCalendar.getTime();
                        end = endDate;
                    }
                    Map<String, String> map = new HashMap<String, String>();
                    map.put("chartDate", simpleDateFormat.format(begin).substring(0, 10));
                    Integer branchCount = webServiceTenantMonitorMapper.branchCountCondition(simpleDateFormat.format(begin), simpleDateFormat.format(end), branchType);
                    map.put("branchCount", branchCount.toString());
                    data.add(map);
                    beginCalendar.add(Calendar.MONTH, 1);
                }
            }
            apiRest.setData(data);
            apiRest.setIsSuccess(true);
            apiRest.setMessage("查询成功！");
        }
        catch(Exception e){
            ServiceException se = new ServiceException("1001", "查询失败", e.getMessage());
            throw se;
        }
        return apiRest;
    }

    /**
     * 标记测试商户
     *
     * @param tenantId
     * @return
     */
    @Transactional
    public ApiRest markTestTenant(BigInteger tenantId){
        ApiRest apiRest = new ApiRest();
        int i = webServiceTenantMonitorMapper.markTestTenant(tenantId);
        if(i <= 0){
            apiRest.setIsSuccess(false);
            apiRest.setError("标记测试商户失败！");
        }
        else{
            apiRest.setIsSuccess(true);
            apiRest.setMessage("标记测试商户成功！");
        }
        return apiRest;
    }

    /**
     * 根据商户ID, 统计商户的机构是数量
     *
     * @param tenantIds
     * @return
     */
    @Transactional(readOnly = true)
    public ApiRest queryBranchCount(String tenantIds){
        ApiRest apiRest = new ApiRest();
        List<Map> data = webServiceTenantMonitorMapper.queryBranchCount(tenantIds);
        apiRest.setData(data);
        apiRest.setIsSuccess(true);
        apiRest.setMessage("查询成功！");
        return apiRest;
    }
}
