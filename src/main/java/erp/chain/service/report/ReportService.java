package erp.chain.service.report;

import erp.chain.domain.Branch;
import erp.chain.domain.Employee;
import erp.chain.domain.Goods;
import erp.chain.mapper.*;
import erp.chain.mapper.o2o.VipMapper;
import erp.chain.mapper.report.ReportMapper;
import erp.chain.model.Pager;
import erp.chain.model.report.GrossProfitPagerModel;
import erp.chain.model.report.SaleEmpWorkPagerModel;
import erp.chain.service.setting.OrganizationService;
import erp.chain.utils.CommonUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.Validate;
import org.apache.commons.lang.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import saas.api.common.ApiRest;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 报表
 * Created with IntelliJ IDEA.
 * User: AnCong
 * Date: 2016/11/21
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class ReportService{
    @Autowired
    private ReportMapper reportMapper;
    @Autowired
    private EmployeeMapper employeeMapper;
    @Autowired
    private AreaMapper areaMapper;
    @Autowired
    private VipMapper vipMapper;
    @Autowired
    private GoodsMapper goodsMapper;
    @Autowired
    private PaymentMapper paymentMapper;
    @Autowired
    private OrganizationService organizationService;

    /**
     * 交易流水报表
     *
     * @param params
     * @return
     */
    public ApiRest saleFlowReport(Map params){
        ApiRest apiRest = new ApiRest();
        BigInteger tenantId = BigInteger.valueOf(Long.parseLong((String)params.get("tenantId")));
        BigInteger branchId = BigInteger.valueOf(Long.parseLong((String)params.get("branchId")));
        Map map = new HashMap();
        Integer rows = Integer.parseInt(params.get("rows").toString());
        Integer offset = (Integer.parseInt(params.get("page").toString()) - 1) * rows;
        params.put("rows", rows);
        params.put("offset", offset);
        params.put("tenantId", tenantId);
        params.put("branchId", branchId);
        if(params.get("startDate") != null && !params.get("startDate").equals("")){
            String startDate = "";
            if(params.get("startDate").toString().length() == 10){
                startDate = params.get("startDate").toString() + " 00:00:00";
            }
            else if(params.get("startDate").toString().length() == 16){
                startDate = params.get("startDate").toString() + ":00";
            }
            params.put("startDate", startDate);
        }
        if(params.get("endDate") != null && !params.get("endDate").equals("")){
            String endDate = "";
            if(params.get("endDate").toString().length() == 10){
                endDate = params.get("endDate").toString() + " 23:59:59";
            }
            else if(params.get("endDate").toString().length() == 16){
                endDate = params.get("endDate").toString() + ":59";
            }
            params.put("endDate", endDate);
        }
        if(params.get("field") != null && !params.get("field").equals("") && params.get("order") != null && !params.get("order").equals("")){
            params.put("field", CommonUtils.formatOrderField(params.get("field").toString()));
            params.put("order", params.get("order").toString());
        }
        if(params.get("parentId") != null){
            params.remove("branchId");
        }
        List<Map> list = reportMapper.saleFlowReport(params);
        List<Map> footer = reportMapper.saleFlowReportSum(params);
        map.put("total", footer.get(0).get("total"));
        map.put("rows", list);
        map.put("footer", footer);
        apiRest.setIsSuccess(true);
        apiRest.setData(map);
        apiRest.setMessage("查询交易流水报表成功！");
        return apiRest;
    }

    /**
     * 明细流水报表
     *
     * @param params
     * @return
     */
    public ApiRest saleGoodsReport(Map params){
        ApiRest apiRest = new ApiRest();
        BigInteger tenantId = BigInteger.valueOf(Long.parseLong((String)params.get("tenantId")));
        BigInteger branchId = BigInteger.valueOf(Long.parseLong((String)params.get("branchId")));
        Map map = new HashMap();
        Integer rows = Integer.parseInt(params.get("rows").toString());
        Integer offset = (Integer.parseInt(params.get("page").toString()) - 1) * rows;
        params.put("rows", rows);
        params.put("offset", offset);
        params.put("tenantId", tenantId);
        params.put("branchId", branchId);
        if(params.get("startDate") != null && !params.get("startDate").equals("")){
            String startDate = "";
            if(params.get("startDate").toString().length() == 10){
                startDate = params.get("startDate").toString() + " 00:00:00";
            }
            else if(params.get("startDate").toString().length() == 16){
                startDate = params.get("startDate").toString() + ":00";
            }
            params.put("startDate", startDate);
        }
        if(params.get("endDate") != null && !params.get("endDate").equals("")){
            String endDate = "";
            if(params.get("endDate").toString().length() == 10){
                endDate = params.get("endDate").toString() + " 23:59:59";
            }
            else if(params.get("endDate").toString().length() == 16){
                endDate = params.get("endDate").toString() + ":59";
            }
            params.put("endDate", endDate);
        }
        if(params.get("field") != null && !params.get("field").equals("") && params.get("order") != null && !params.get("order").equals("")){
            params.put("field", CommonUtils.formatOrderField(params.get("field").toString()));
            params.put("order", params.get("order").toString());
        }
        if(params.get("parentId") != null){
            params.remove("branchId");
        }
        List<Map> list = reportMapper.saleGoodsReport(params);
        List<Map> footer = reportMapper.saleGoodsReportSum(params);
        map.put("total", footer.get(0).get("total"));
        map.put("rows", list);
        map.put("footer", footer);
        apiRest.setIsSuccess(true);
        apiRest.setData(map);
        apiRest.setMessage("查询明细流水报表成功！");
        return apiRest;
    }

    /**
     * 支付流水报表
     *
     * @param params
     * @return
     */
    public ApiRest salePaymentReport(Map params){
        ApiRest apiRest = new ApiRest();
        BigInteger tenantId = BigInteger.valueOf(Long.parseLong((String)params.get("tenantId")));
        BigInteger branchId = BigInteger.valueOf(Long.parseLong((String)params.get("branchId")));
        Map map = new HashMap();
        Integer rows = Integer.parseInt(params.get("rows").toString());
        Integer offset = (Integer.parseInt(params.get("page").toString()) - 1) * rows;
        params.put("rows", rows);
        params.put("offset", offset);
        params.put("tenantId", tenantId);
        params.put("branchId", branchId);
        if(params.get("startDate") != null && !params.get("startDate").equals("")){
            String startDate = "";
            if(params.get("startDate").toString().length() == 10){
                startDate = params.get("startDate").toString() + " 00:00:00";
            }
            else if(params.get("startDate").toString().length() == 16){
                startDate = params.get("startDate").toString() + ":00";
            }
            params.put("startDate", startDate);
        }
        if(params.get("endDate") != null && !params.get("endDate").equals("")){
            String endDate = "";
            if(params.get("endDate").toString().length() == 10){
                endDate = params.get("endDate").toString() + " 23:59:59";
            }
            else if(params.get("endDate").toString().length() == 16){
                endDate = params.get("endDate").toString() + ":59";
            }
            params.put("endDate", endDate);
        }
        if(params.get("field") != null && !params.get("field").equals("") && params.get("order") != null && !params.get("order").equals("")){
            params.put("field", CommonUtils.formatOrderField(params.get("field").toString()));
            params.put("order", params.get("order").toString());
        }
        if(params.get("parentId") != null){
            params.remove("branchId");
        }
        List<Map> list = reportMapper.salePaymentReport(params);
        List<Map> footer = reportMapper.salePaymentReportSum(params);
        map.put("total", footer.get(0).get("total"));
        map.put("rows", list);
        map.put("footer", footer);
        apiRest.setIsSuccess(true);
        apiRest.setData(map);
        apiRest.setMessage("查询支付流水报表成功！");
        return apiRest;
    }

    /**
     * 单品汇总报表
     *
     * @param params
     * @return
     */
    public ApiRest singleSaleReport(Map params){
        ApiRest apiRest = new ApiRest();
        BigInteger tenantId = BigInteger.valueOf(Long.parseLong((String)params.get("tenantId")));
        String branchId = (String)params.get("branchId");
        Map map = new HashMap();
        Integer rows = Integer.parseInt(params.get("rows").toString());
        Integer offset = (Integer.parseInt(params.get("page").toString()) - 1) * rows;
        params.put("rows", rows);
        params.put("offset", offset);
        params.put("tenantId", tenantId);
        params.put("branchId", branchId);
        if(params.get("startDate") != null && !params.get("startDate").equals("")){
            String startDate = "";
            if(params.get("startDate").toString().length() == 10){
                startDate = params.get("startDate").toString() + " 00:00:00";
            }
            else if(params.get("startDate").toString().length() == 16){
                startDate = params.get("startDate").toString() + ":00";
            }
            params.put("startDate", startDate);
        }
        if(params.get("endDate") != null && !params.get("endDate").equals("")){
            String endDate = "";
            if(params.get("endDate").toString().length() == 10){
                endDate = params.get("endDate").toString() + " 23:59:59";
            }
            else if(params.get("endDate").toString().length() == 16){
                endDate = params.get("endDate").toString() + ":59";
            }
            params.put("endDate", endDate);
        }
        if("-1".equals(params.get("categoryId"))){
            params.put("categoryId", null);
        }
        int isShowPackage = 1;
        if(params.get("isShowPackage") != null && !"".equals(params.get("isShowPackage"))){
            isShowPackage = Integer.parseInt(params.get("isShowPackage").toString());
        }
        params.put("isShowPackage", isShowPackage);
        int onlySelf = 0;
        if(params.get("onlySelf") != null && !"".equals(params.get("onlySelf"))){
            onlySelf = Integer.parseInt(params.get("onlySelf").toString());
        }
        params.put("onlySelf", onlySelf);
        if(params.get("field") != null && !params.get("field").equals("") && params.get("order") != null && !params.get("order").equals("")){
            params.put("field", CommonUtils.formatOrderField(params.get("field").toString()));
            params.put("order", params.get("order").toString());
        }
        if(params.get("parentId") != null){
            params.remove("branchId");
        }
        List<Map> list = reportMapper.singleSaleReport(params);
        List<Map> footer = reportMapper.singleSaleReportSum(params);
        BigDecimal pageSum = new BigDecimal(0);
        NumberFormat nf = new DecimalFormat("##0.00");
        NumberFormat mf = new DecimalFormat("##0.000");
        BigDecimal sumAmount = BigDecimal.valueOf(Double.valueOf(footer.get(0).get("realAmount").toString()));
        String pagePercentage;
        for(Map i : list){
            BigDecimal totalAmount = BigDecimal.valueOf(Double.valueOf(i.get("realAmount").toString()));
            pageSum = pageSum.add(totalAmount);
            if(!Objects.equals(sumAmount, BigDecimal.valueOf(0.0)) && !Objects.equals(totalAmount, BigDecimal.valueOf(0.0))){
                i.put("percentage", nf.format(totalAmount.divide(sumAmount, 10, BigDecimal.ROUND_UP).multiply(BigDecimal.valueOf(100))) + "%");
            }
            else{
                i.put("percentage", "0.00%");
            }
            i.put("profitRate", mf.format(BigDecimal.valueOf(Double.valueOf(i.get("profitRate").toString())).multiply(BigDecimal.valueOf(100))) + "%");
        }
        if(!Objects.equals(sumAmount, BigDecimal.valueOf(0.0)) && !Objects.equals(pageSum, BigDecimal.valueOf(0.0))){
            pagePercentage = nf.format(pageSum.divide(sumAmount, 10, BigDecimal.ROUND_UP).multiply(BigDecimal.valueOf(100))) + "%";
        }
        else{
            pagePercentage = "0.00%";
        }
        map.put("total", footer.get(0).get("total"));
        map.put("rows", list);
        map.put("footer", footer);
        map.put("sumAmount", sumAmount);
        map.put("pageSum", pageSum);
        map.put("pagePercentage", pagePercentage);
        apiRest.setIsSuccess(true);
        apiRest.setData(map);
        apiRest.setMessage("查询单品汇总报表成功！");
        return apiRest;
    }

    /**
     * 分类汇总报表
     *
     * @param params
     * @return
     */
    public ApiRest cateSaleReport(Map params){
        ApiRest apiRest = new ApiRest();
        BigInteger tenantId = BigInteger.valueOf(Long.parseLong((String)params.get("tenantId")));
        String branchId = ((String)params.get("branchId"));
        Map map = new HashMap();
        Integer rows = Integer.parseInt(params.get("rows").toString());
        Integer offset = (Integer.parseInt(params.get("page").toString()) - 1) * rows;
        params.put("rows", rows);
        params.put("offset", offset);
        params.put("tenantId", tenantId);
        params.put("branchId", branchId);
        if(params.get("startDate") != null && !params.get("startDate").equals("")){
            String startDate = "";
            if(params.get("startDate").toString().length() == 10){
                startDate = params.get("startDate").toString() + " 00:00:00";
            }
            else if(params.get("startDate").toString().length() == 16){
                startDate = params.get("startDate").toString() + ":00";
            }
            params.put("startDate", startDate);
        }
        if(params.get("endDate") != null && !params.get("endDate").equals("")){
            String endDate = "";
            if(params.get("endDate").toString().length() == 10){
                endDate = params.get("endDate").toString() + " 23:59:59";
            }
            else if(params.get("endDate").toString().length() == 16){
                endDate = params.get("endDate").toString() + ":59";
            }
            params.put("endDate", endDate);
        }
        if("-1".equals(params.get("categoryId"))){
            params.put("categoryId", null);
        }
        int isShowPackage = 1;
        if(params.get("isShowPackage") != null && !"".equals(params.get("isShowPackage"))){
            isShowPackage = Integer.parseInt(params.get("isShowPackage").toString());
        }
        params.put("isShowPackage", isShowPackage);
        if(params.get("field") != null && !params.get("field").equals("") && params.get("order") != null && !params.get("order").equals("")){
            params.put("field", CommonUtils.formatOrderField(params.get("field").toString()));
            params.put("order", params.get("order").toString());
        }
        if(params.get("parentId") != null){
            params.remove("branchId");
        }
        List<Map> list = reportMapper.cateSaleReport(params);
        List<Map> footer = reportMapper.cateSaleReportSum(params);
        BigDecimal pageSum = new BigDecimal(0);
        NumberFormat nf = new DecimalFormat("##0.00");
        BigDecimal sumAmount = BigDecimal.valueOf(Double.valueOf(footer.get(0).get("realAmount").toString()));
        String pagePercentage;
        for(Map i : list){
            BigDecimal totalAmount = BigDecimal.valueOf(Double.valueOf(i.get("realAmount").toString()));
            pageSum = pageSum.add(totalAmount);
            if(!Objects.equals(sumAmount, BigDecimal.valueOf(0.0)) && !Objects.equals(totalAmount, BigDecimal.valueOf(0.0))){
                i.put("percentage", nf.format(totalAmount.divide(sumAmount, 10, BigDecimal.ROUND_UP).multiply(BigDecimal.valueOf(100))) + "%");
            }
            else{
                i.put("percentage", "0.00%");
            }
        }
        if(!Objects.equals(sumAmount, BigDecimal.valueOf(0.0)) && !Objects.equals(pageSum, BigDecimal.valueOf(0.0))){
            pagePercentage = nf.format(pageSum.divide(sumAmount, 10, BigDecimal.ROUND_UP).multiply(BigDecimal.valueOf(100))) + "%";
        }
        else{
            pagePercentage = "0.00%";
        }
        map.put("total", footer.get(0).get("total"));
        map.put("rows", list);
        map.put("footer", footer);
        map.put("sumAmount", sumAmount);
        map.put("pageSum", pageSum);
        map.put("pagePercentage", pagePercentage);
        apiRest.setIsSuccess(true);
        apiRest.setData(map);
        apiRest.setMessage("查询分类汇总报表成功！");
        return apiRest;
    }

    /**
     * 销售趋势报表
     *
     * @param params
     * @return
     */
    public ApiRest saleTrendReport(Map params){
        ApiRest apiRest = new ApiRest();
        BigInteger tenantId = BigInteger.valueOf(Long.parseLong((String)params.get("tenantId")));
        String branchId = (String)params.get("branchId");
        Map map = new HashMap();
        Integer rows = Integer.parseInt(params.get("rows").toString());
        Integer offset = (Integer.parseInt(params.get("page").toString()) - 1) * rows;
        params.put("rows", rows);
        params.put("offset", offset);
        params.put("tenantId", tenantId);
        params.put("branchId", branchId);
        if(params.get("startDate") != null && !params.get("startDate").equals("")){
            String startDate = "";
            if(params.get("startDate").toString().length() == 10){
                startDate = params.get("startDate").toString() + " 00:00:00";
            }
            else if(params.get("startDate").toString().length() == 16){
                startDate = params.get("startDate").toString() + ":00";
            }
            params.put("startDate", startDate);
        }
        if(params.get("endDate") != null && !params.get("endDate").equals("")){
            String endDate = "";
            if(params.get("endDate").toString().length() == 10){
                endDate = params.get("endDate").toString() + " 23:59:59";
            }
            else if(params.get("endDate").toString().length() == 16){
                endDate = params.get("endDate").toString() + ":59";
            }
            params.put("endDate", endDate);
        }
        if(params.get("field") != null && !params.get("field").equals("") && params.get("order") != null && !params.get("order").equals("")){
            params.put("field", CommonUtils.formatOrderField(params.get("field").toString()));
            params.put("order", params.get("order").toString());
        }
        if(params.get("parentId") != null){
            params.remove("branchId");
        }
        List<Map> list = reportMapper.saleTrendReport(params);
        List<Map> footer = reportMapper.saleTrendReportSum(params);
        BigDecimal pageSum = new BigDecimal(0);
        NumberFormat nf = new DecimalFormat("##0.00");
        BigDecimal sumAmount = BigDecimal.valueOf(Double.valueOf(footer.get(0).get("realAmount").toString()));
        String pagePercentage;
        for(Map i : list){
            if(params.get("trendType").equals("hour")){
                i.put("checkoutAt", i.get("checkoutAt") + ":00~" + i.get("dateInterval") + ":00");
                i.remove("dateInterval");
            }
            BigDecimal totalAmount = BigDecimal.valueOf(Double.valueOf(i.get("realAmount").toString()));
            pageSum = pageSum.add(totalAmount);
            if(!Objects.equals(sumAmount, BigDecimal.valueOf(0)) && !Objects.equals(totalAmount, BigDecimal.valueOf(0))){
                i.put("percentage", nf.format(totalAmount.divide(sumAmount, 10, BigDecimal.ROUND_UP).multiply(BigDecimal.valueOf(100))) + "%");
            }
            else{
                i.put("percentage", "0.00%");
            }
        }
        if(!Objects.equals(sumAmount, BigDecimal.valueOf(0)) && !Objects.equals(pageSum, BigDecimal.valueOf(0))){
            pagePercentage = nf.format(pageSum.divide(sumAmount, 10, BigDecimal.ROUND_UP).multiply(BigDecimal.valueOf(100))) + "%";
        }
        else{
            pagePercentage = "0.00%";
        }
        map.put("total", footer.get(0).get("total"));
        map.put("rows", list);
        map.put("footer", footer);
        map.put("sumAmount", sumAmount);
        map.put("pageSum", pageSum);
        map.put("pagePercentage", pagePercentage);
        apiRest.setIsSuccess(true);
        apiRest.setData(map);
        apiRest.setMessage("查询销售趋势报表成功！");
        return apiRest;
    }

    /**
     * 销售趋势折线图
     */
    public ApiRest lineForSaleTrend(Map params){
        ApiRest apiRest = new ApiRest();
        BigInteger tenantId = BigInteger.valueOf(Long.parseLong((String)params.get("tenantId")));
        String branchId = (String)params.get("branchId");
        Map map = new HashMap();
        Integer rows = Integer.parseInt(params.get("rows").toString());
        Integer offset = (Integer.parseInt(params.get("page").toString()) - 1) * rows;
        params.put("rows", rows);
        params.put("offset", offset);
        params.put("tenantId", tenantId);
        params.put("branchId", branchId);
        if(params.get("startDate") != null && !params.get("startDate").equals("")){
            String startDate = "";
            if(params.get("startDate").toString().length() == 10){
                startDate = params.get("startDate").toString() + " 00:00:00";
            }
            else if(params.get("startDate").toString().length() == 16){
                startDate = params.get("startDate").toString() + ":00";
            }
            params.put("startDate", startDate);
        }
        if(params.get("endDate") != null && !params.get("endDate").equals("")){
            String endDate = "";
            if(params.get("endDate").toString().length() == 10){
                endDate = params.get("endDate").toString() + " 23:59:59";
            }
            else if(params.get("endDate").toString().length() == 16){
                endDate = params.get("endDate").toString() + ":59";
            }
            params.put("endDate", endDate);
        }
        List<Map> list = reportMapper.saleTrendReport(params);
        List dateList = new ArrayList();
        List realAmount = new ArrayList();
        List saleNum = new ArrayList();
        List saleUnitPrice = new ArrayList();
        if(list != null && list.size() > 0){
            for(int i = 0; i < list.size(); i++){
                Map resultMap = list.get(i);
                if("hour".equals(params.get("trendType"))){
                    dateList.add(resultMap.get("checkoutAt")+":00～"+resultMap.get("dateInterval")+":00");
                }else{
                    dateList.add(resultMap.get("checkoutAt"));
                }
                realAmount.add(resultMap.get("realAmount"));
                saleNum.add(resultMap.get("saleNum"));
                saleUnitPrice.add(resultMap.get("saleUnitPrice"));
            }
        }
        map.put("dateList", dateList);
        map.put("realAmount", realAmount);
        map.put("saleNum", saleNum);
        map.put("saleUnitPrice", saleUnitPrice);
        apiRest.setIsSuccess(true);
        apiRest.setData(map);
        apiRest.setMessage("折线图数据查询成功！");
        return apiRest;
    }

    /**
     * 营业概况报表
     *
     * @param params
     * @return
     */
    public ApiRest businessReport(Map params){
        ApiRest apiRest = new ApiRest();
        List<Map> list = reportMapper.businessReport(params);
        apiRest.setIsSuccess(true);
        apiRest.setData(list.get(0));
        apiRest.setMessage("查询营业概况报表成功！");
        return apiRest;
    }

    /**
     * 营业概况报表
     *
     * @param params
     * @return
     */
    public ApiRest businessSumReport(Map params){
        ApiRest apiRest = new ApiRest();
        Map result = new HashMap();
        String today = params.get("today").toString();
        params.put("startDate", today);
        params.put("endDate", today);
        List<Map> todayList = reportMapper.businessReport(params);
        result.put("today", todayList.get(0));

        String yesterday = reportMapper.getDate(today, -1);
        params.put("startDate", yesterday);
        params.put("endDate", yesterday);
        List<Map> yesterdayList = reportMapper.businessReport(params);
        result.put("yesterday", yesterdayList.get(0));

        String sevenDaysAgo = reportMapper.getDate(today, -7);
        params.put("startDate", sevenDaysAgo);
        params.put("endDate", yesterday);
        List<Map> sevenDaysAgoList = reportMapper.businessReport(params);
        result.put("sevenDaysAgo", sevenDaysAgoList.get(0));

        String thirtyDaysAgo = reportMapper.getDate(today, -30);
        params.put("startDate", thirtyDaysAgo);
        params.put("endDate", yesterday);
        List<Map> thirtyDaysAgoList = reportMapper.businessReport(params);
        result.put("thirtyDaysAgo", thirtyDaysAgoList.get(0));

        apiRest.setIsSuccess(true);
        apiRest.setData(result);
        apiRest.setMessage("查询首页营业概况报表成功！");
        return apiRest;
    }

    /**
     * 机构报表(旧接口)
     *
     * @param params
     * @return
     */
    /*public ApiRest branchReportOld(Map params){
        ApiRest apiRest = new ApiRest();
        BigInteger tenantId = BigInteger.valueOf(Long.parseLong((String)params.get("tenantId")));
        //BigInteger branchId = BigInteger.valueOf(Long.parseLong((String) params.get("branchId")));
        Map map = new HashMap();
        Integer rows = Integer.parseInt(params.get("rows").toString());
        Integer offset = (Integer.parseInt(params.get("page").toString()) - 1) * rows;
        params.put("rows", rows);
        params.put("offset", offset);
        params.put("tenantId", tenantId);
        String branchSql = "SELECT id FROM branch WHERE tenant_id=" + tenantId + " AND is_deleted=0";
        String unionSql = " UNION SELECT id FROM branch WHERE tenant_id=" + tenantId + " AND is_deleted=0";
        if(params.get("nameOrCode") != null && !params.get("nameOrCode").equals("")){
            branchSql += " AND (code LIKE \"%" + params.get("nameOrCode") + "%\" OR name LIKE \"%" + params.get("nameOrCode") + "%\")";
            unionSql += " AND (code LIKE \"%" + params.get("nameOrCode") + "%\" OR name LIKE \"%" + params.get("nameOrCode") + "%\")";
        }
        //区域权限控制
        //拥有本店权限
        BigInteger userId = BigInteger.valueOf(Long.parseLong(params.get("userId").toString()));
        Employee e = employeeMapper.getEmployee(userId, tenantId);
        List<Map> areaList;
        String areaId = "";
        String qyeryId = "";
        if(params.get("areaId") != null && !params.get("areaId").equals("")){
            areaId = params.get("areaId").toString();
            if(Integer.parseInt(areaId) == -1){
                //区域权限控制
                if(params.get("isUserAreas") != null && "true".equals(params.get("isUserAreas"))){
                    if(!"0000".equals(e.getCode())){
                        qyeryId = e.getUserAreas() == null ? "" : e.getUserAreas();
                        unionSql += " AND id = " + e.getBranchId();
                    } else{
                        areaList = reportMapper.firstAreaList("-1", tenantId);
                        for(int x = 0; x < areaList.size(); x++){
                            Map map1 = areaList.get(x);
                            areaId = map1.get("id").toString();
                            List<Map> aList = reportMapper.areaList(areaId);
                            qyeryId += aList.get(0).get("ids").toString().substring(2) + ",";
                        }
                        qyeryId = qyeryId.substring(0, qyeryId.lastIndexOf(","));
                    }
                } else{
                    areaList = reportMapper.firstAreaList("-1", tenantId);
                    for(int x = 0; x < areaList.size(); x++){
                        Map map1 = areaList.get(x);
                        areaId = map1.get("id").toString();
                        List<Map> aList = reportMapper.areaList(areaId);
                        qyeryId += aList.get(0).get("ids").toString().substring(2) + ",";
                    }
                    qyeryId = qyeryId.substring(0, qyeryId.lastIndexOf(","));
                }
            } else{
                areaList = reportMapper.areaList(areaId);
                qyeryId = areaList.get(0).get("ids").toString().substring(2);
                if(params.get("isUserAreas") != null && "true".equals(params.get("isUserAreas"))){
                    String qId[] = qyeryId.split(",");
                    String ids = "";
                    if(!"0000".equals(e.getCode())){
                        for(int i = 0; i < qId.length; i++){
                            if(e.getUserAreas().indexOf(qId[i]) != -1){
                                ids += qId[i] + ",";
                            }
                        }
                        qyeryId = ids.substring(0, ids.lastIndexOf(","));
                    }
                }
            }
            if(!qyeryId.equals("")){
                branchSql += " AND area_id in (" + qyeryId + ")";
                if(params.get("branchId") != null && !params.get("branchId").equals("")){
                    BigInteger branchId = BigInteger.valueOf(Long.parseLong((String)params.get("branchId")));
                    params.put("branchId", branchId);
                    //branchSql += " AND id=" + branchId;
                }
            } else{
                branchSql += " AND area_id=" + areaId;
                if(params.get("branchId") != null && !params.get("branchId").equals("")){
                    BigInteger branchId = BigInteger.valueOf(Long.parseLong((String)params.get("branchId")));
                    params.put("branchId", branchId);
                    branchSql += " AND id=" + branchId;
                }
            }
        } else{
            if("0000".equals(e.getCode())){
                areaList = reportMapper.firstAreaList("-1", tenantId);
                for(int x = 0; x < areaList.size(); x++){
                    Map map1 = areaList.get(x);
                    areaId = map1.get("id").toString();
                    List<Map> aList = reportMapper.areaList(areaId);
                    qyeryId += aList.get(0).get("ids").toString().substring(2) + ",";
                }
                qyeryId = qyeryId.substring(0, qyeryId.lastIndexOf(","));
            } else{
                if(e.getUserAreas() != null || !"".equals(e.getUserAreas())){
                    branchSql += " AND area_id in (" + e.getUserAreas() + ")";
                    if(params.get("branchId") != null && !params.get("branchId").equals("")){
                        BigInteger branchId = BigInteger.valueOf(Long.parseLong((String)params.get("branchId")));
                        params.put("branchId", branchId);
                        //branchSql += " AND id=" + branchId;
                    }
                    unionSql += " AND id = " + e.getBranchId();
                    branchSql += (unionSql + " order by id");
                } else{
                    branchSql += " AND id = " + e.getBranchId();
                    params.put("branchSql", branchSql);
                    params.put("startDate", params.get("startDate"));
                    params.put("endDate", params.get("endDate"));
                }
            }
        }
            *//*if(params.get("branchId")!=null&&!params.get("branchId").equals("")){
                BigInteger branchId = BigInteger.valueOf(Long.parseLong((String) params.get("branchId")));
                params.put("branchId", branchId);
                branchSql += " AND id=" + branchId;
            }*//*
        if(params.get("areaId") != null && !params.get("areaId").equals("") && Integer.parseInt(params.get("areaId").toString()) == -1
                && params.get("isUserAreas") != null && "true".equals(params.get("isUserAreas"))){
            branchSql += (unionSql + " order by id");
        }
        params.put("branchSql", branchSql);
        if(params.get("startDate") != null && !params.get("startDate").equals("")){
            String startDate = "";
            if(params.get("startDate").toString().length() == 10){
                startDate = params.get("startDate").toString() + " 00:00:00";
            } else if(params.get("startDate").toString().length() == 16){
                startDate = params.get("startDate").toString() + ":00";
            }
            params.put("startDate", startDate);
        }
        if(params.get("endDate") != null && !params.get("endDate").equals("")){
            String endDate = "";
            if(params.get("endDate").toString().length() == 10){
                endDate = params.get("endDate").toString() + " 23:59:59";
            } else if(params.get("endDate").toString().length() == 16){
                endDate = params.get("endDate").toString() + ":59";
            }
            params.put("endDate", endDate);
        }
        List<List> list = reportMapper.branchReport(params);

        *//*线上订单*//*
        params.put("allAreaId", qyeryId);
        List<Map> dietOrderList = reportMapper.dietOrderReport(params);
        if("1".equals(params.get("saleType"))){
            List reportList = list.get(0);
            int length = reportList.size();
            if((rows + offset) < length){
                length = rows + offset;
            }
            for(int i = offset; i < length; i++){
                Map map1 = (Map)reportList.get(i);
                String branchId = map1.get("id").toString();
                if(dietOrderList.size() > 0){
                    for(int j = 0; j < dietOrderList.size(); j++){
                        Map dMap = dietOrderList.get(j);
                        if(branchId.equals(dMap.get("id").toString())){
                            map1.put("totalAmount", dMap.get("totalAmount"));
                            map1.put("totalCount", dMap.get("totalCount"));
                            if(BigDecimal.valueOf(Long.parseLong(dMap.get("totalCount").toString())) == BigDecimal.ZERO){
                                map1.put("average", 0);
                            } else{
                                map1.put("average", BigDecimal.valueOf(Float.parseFloat(dMap.get("totalAmount").toString())).divide(BigDecimal.valueOf(Float.parseFloat(dMap.get("totalCount").toString())), 3, RoundingMode.HALF_UP));
                            }
                            break;
                        } else{
                            map1.put("totalAmount", 0);
                            map1.put("totalCount", 0);
                            map1.put("average", 0);
                        }
                    }
                } else{
                    map1.put("totalAmount", 0);
                    map1.put("totalCount", 0);
                    map1.put("average", 0);
                }
            }
            List<Map> fList = reportMapper.dietOrderSumReport(params);
            Map fMap = fList.get(0);
            List footList = list.get(1);
            Map map2 = (Map)footList.get(0);
            map2.put("totalAmount", fMap.get("totalAmount"));
            map2.put("totalCount", fMap.get("totalCount"));
            if(BigDecimal.valueOf(Long.parseLong(fMap.get("totalCount").toString())) == BigDecimal.ZERO){
                map2.put("average", 0);
            } else{
                map2.put("average", BigDecimal.valueOf(Float.parseFloat(fMap.get("totalAmount").toString())).divide(BigDecimal.valueOf(Float.parseFloat(fMap.get("totalCount").toString())), 3, RoundingMode.HALF_UP));
            }
            String total;
            if(((Map)list.get(1).get(0)).get("total") != null && !"".equals(((Map)list.get(1).get(0)).get("total"))){
                total = ((Map)list.get(1).get(0)).get("total").toString();
            } else{
                total = "0";
            }
            map.put("rows", list.get(0));
            map.put("footer", footList);
            map.put("total", total);
        } else if("0".equals(params.get("saleType"))){
            if(list.get(0).size() == 0){
                List fList = new ArrayList<>();
                Map fMap = new HashMap();
                fMap.put("average", 0);
                fMap.put("code", "合计");
                fMap.put("total", 0);
                fMap.put("totalAmount", 0);
                fMap.put("totalCount", 0);
                fMap.put("vipStoreAmount", 0);
                fMap.put("vipTradeAmount", 0);
                fList.add(fMap);
                map.put("rows", new ArrayList<>());
                map.put("footer", fList);
                map.put("total", 0);
            } else{
                String total = ((Map)list.get(1).get(0)).get("total").toString();
                map.put("rows", list.get(0));
                map.put("footer", list.get(1));
                map.put("total", total);
            }
        }else{
            if(list.get(0).size() == 0){
                List fList = new ArrayList<>();
                Map fMap = new HashMap();
                fMap.put("average", 0);
                fMap.put("code", "合计");
                fMap.put("total", 0);
                fMap.put("totalAmount", 0);
                fMap.put("totalCount", 0);
                fMap.put("vipStoreAmount", 0);
                fMap.put("vipTradeAmount", 0);
                fList.add(fMap);
                map.put("rows", new ArrayList<>());
                map.put("footer", fList);
                map.put("total", 0);
            } else{
                String total = ((Map)list.get(1).get(0)).get("total").toString();
                List reportList = list.get(0);
                int length = reportList.size();
                if((rows + offset) < length){
                    length = rows + offset;
                }
                for(int i = offset; i < length; i++){
                    Map map1 = (Map)reportList.get(i);
                    String branchId = map1.get("id").toString();
                    if(dietOrderList.size() > 0){
                        for(int j = 0; j < dietOrderList.size(); j++){
                            Map dMap = dietOrderList.get(j);
                            if(branchId.equals(dMap.get("id").toString())){
                                map1.put("totalAmount", new BigDecimal(dMap.get("totalAmount").toString()).add(new BigDecimal(map1.get("totalAmount").toString())));
                                map1.put("totalCount", new BigDecimal(dMap.get("totalCount").toString()).add(new BigDecimal(map1.get("totalCount").toString())));
                                if(BigDecimal.valueOf(Long.parseLong(dMap.get("totalCount").toString())) == BigDecimal.ZERO){

                                } else{
                                    map1.put("average", BigDecimal.valueOf(Float.parseFloat(map1.get("totalAmount").toString())).divide(BigDecimal.valueOf(Float.parseFloat(map1.get("totalCount").toString())), 3, RoundingMode.HALF_UP));
                                }
                                break;
                            }
                        }
                    }
                }
                List<Map> fList = reportMapper.dietOrderSumReport(params);
                Map fMap = fList.get(0);
                List footList = list.get(1);
                Map map2 = (Map)footList.get(0);
                map2.put("totalAmount", new BigDecimal(fMap.get("totalAmount").toString()).add(new BigDecimal(map2.get("totalAmount").toString())));
                map2.put("totalCount", new BigDecimal(fMap.get("totalCount").toString()).add(new BigDecimal(map2.get("totalCount").toString())));
                if(BigDecimal.valueOf(Long.parseLong(fMap.get("totalCount").toString())) == BigDecimal.ZERO){

                } else{
                    map2.put("average", BigDecimal.valueOf(Float.parseFloat(map2.get("totalAmount").toString())).divide(BigDecimal.valueOf(Float.parseFloat(map2.get("totalCount").toString())), 3, RoundingMode.HALF_UP));
                }
                map.put("rows", reportList);
                map.put("footer", footList);
                map.put("total", total);
            }
        }
        apiRest.setIsSuccess(true);
        apiRest.setData(map);
        apiRest.setMessage("查询机构报表成功！");
        return apiRest;
    }*/

    /**
     * 机构报表
     *
     * @param params
     * @return
     */
    public ApiRest branchReport(Map params){
        ApiRest apiRest = new ApiRest();
        BigInteger tenantId = BigInteger.valueOf(Long.parseLong((String)params.get("tenantId")));
        Map map = new HashMap();
        Integer rows = Integer.parseInt(params.get("rows").toString());
        Integer offset = (Integer.parseInt(params.get("page").toString()) - 1) * rows;
        params.put("rows", rows);
        params.put("offset", offset);
        //区域权限控制
        //拥有本店权限
        BigInteger userId = BigInteger.valueOf(Long.parseLong(params.get("userId").toString()));
        Employee e = employeeMapper.getEmployee(userId, tenantId);
        List<Map> areaList;
        String areaId;
        String queryId = "";
        if(params.get("areaId") != null && !params.get("areaId").equals("")){
            areaId = params.get("areaId").toString();
            if(Integer.parseInt(areaId) == -1){
                //区域权限控制
                if(params.get("isUserAreas") != null && "true".equals(params.get("isUserAreas"))){
                    if(!"0000".equals(e.getCode())){
                        queryId = e.getUserAreas() == null ? "" : e.getUserAreas();
                    } else{
                        areaList = reportMapper.firstAreaList("-1", tenantId);
                        for(int x = 0; x < areaList.size(); x++){
                            Map map1 = areaList.get(x);
                            areaId = map1.get("id").toString();
                            List<Map> aList = reportMapper.areaList(areaId);
                            queryId += aList.get(0).get("ids").toString().substring(2) + ",";
                        }
                        queryId = queryId.substring(0, queryId.lastIndexOf(","));
                    }
                } else{
                    areaList = reportMapper.firstAreaList("-1", tenantId);
                    for(int x = 0; x < areaList.size(); x++){
                        Map map1 = areaList.get(x);
                        areaId = map1.get("id").toString();
                        List<Map> aList = reportMapper.areaList(areaId);
                        queryId += aList.get(0).get("ids").toString().substring(2) + ",";
                    }
                    queryId = queryId.substring(0, queryId.lastIndexOf(","));
                }
            } else{
                areaList = reportMapper.areaList(areaId);
                queryId = areaList.get(0).get("ids").toString().substring(2);
                if(params.get("isUserAreas") != null && "true".equals(params.get("isUserAreas"))){
                    String qId[] = queryId.split(",");
                    String ids = "";
                    if(!"0000".equals(e.getCode())){
                        for(int i = 0; i < qId.length; i++){
                            if(e.getUserAreas().indexOf(qId[i]) != -1){
                                ids += qId[i] + ",";
                            }
                        }
                        queryId = ids.substring(0, ids.lastIndexOf(","));
                    }
                }
            }
            if(!queryId.equals("")){
                if(Integer.parseInt(areaId) != -1){
                    params.put("branchId", null);
                }
            }
        } else{
            if("0000".equals(e.getCode())){
                areaList = reportMapper.firstAreaList("-1", tenantId);
                for(int x = 0; x < areaList.size(); x++){
                    Map map1 = areaList.get(x);
                    areaId = map1.get("id").toString();
                    List<Map> aList = reportMapper.areaList(areaId);
                    queryId += aList.get(0).get("ids").toString().substring(2) + ",";
                }
                queryId = queryId.substring(0, queryId.lastIndexOf(","));
            } else{
                if(e.getUserAreas() != null && !"".equals(e.getUserAreas())){
                    queryId = e.getUserAreas();
                }else{
                    BigInteger branchId = e.getBranchId();
                    params.put("branchId", branchId);
                }
            }
        }
        params.put("areaId", queryId);
        if(params.get("startDate") != null && !params.get("startDate").equals("")){
            String startDate = "";
            if(params.get("startDate").toString().length() == 10){
                startDate = params.get("startDate").toString() + " 00:00:00";
            }
            else if(params.get("startDate").toString().length() == 16){
                startDate = params.get("startDate").toString() + ":00";
            }
            params.put("startDate", startDate);
        }
        if(params.get("endDate") != null && !params.get("endDate").equals("")){
            String endDate = "";
            if(params.get("endDate").toString().length() == 10){
                endDate = params.get("endDate").toString() + " 23:59:59";
            }
            else if(params.get("endDate").toString().length() == 16){
                endDate = params.get("endDate").toString() + ":59";
            }
            params.put("endDate", endDate);
        }
        List<Map> list = reportMapper.branchReport(params);
        List<Map> footer = reportMapper.branchReportSum(params);
        map.put("rows", list);
        map.put("footer", footer);
        map.put("total", footer.get(0).get("total"));
        apiRest.setData(map);
        apiRest.setIsSuccess(true);
        apiRest.setData(map);
        apiRest.setMessage("查询机构报表成功！");
        return apiRest;
    }

    /**
     * 获取所有门店支付方式
     * @param params
     * @return
     */
    public ApiRest getPayMethodWays(Map params){
        ApiRest apiRest = new ApiRest();
        BigInteger tenantId = BigInteger.valueOf(Long.parseLong((String)params.get("tenantId")));
       /* BigInteger branchId = BigInteger.valueOf(Long.parseLong((String)params.get("branchId")));*/
        params.put("tenantId", tenantId);
      /*  params.put("branchId", branchId);*/
        if(params.get("startDate") != null && !params.get("startDate").equals("")){
            String startDate = "";
            if(params.get("startDate").toString().length() == 10){
                startDate = params.get("startDate").toString() + " 00:00:00";
            }
            else if(params.get("startDate").toString().length() == 16){
                startDate = params.get("startDate").toString() + ":00";
            }
            params.put("startDate", startDate);
        }
        if(params.get("endDate") != null && !params.get("endDate").equals("")){
            String endDate = "";
            if(params.get("endDate").toString().length() == 10){
                endDate = params.get("endDate").toString() + " 23:59:59";
            }
            else if(params.get("endDate").toString().length() == 16){
                endDate = params.get("endDate").toString() + ":59";
            }
            params.put("endDate", endDate);
        }
        List<Map> payWays = reportMapper.paymentMethodWays(params);
        apiRest.setIsSuccess(true);
        apiRest.setData(payWays);
        apiRest.setMessage("查询支付方式成功！");
        return apiRest;
    }

    /**
     * 获取全部支付方式（储值+收款）
     * */
    public ApiRest getAllPayWays(Map params){
        ApiRest apiRest = new ApiRest();
        if(params.get("startDate") != null && !params.get("startDate").equals("")){
            String startDate = "";
            if(params.get("startDate").toString().length() == 10){
                startDate = params.get("startDate").toString() + " 00:00:00";
            }
            else if(params.get("startDate").toString().length() == 16){
                startDate = params.get("startDate").toString() + ":00";
            }
            params.put("startDate", startDate);
        }
        if(params.get("endDate") != null && !params.get("endDate").equals("")){
            String endDate = "";
            if(params.get("endDate").toString().length() == 10){
                endDate = params.get("endDate").toString() + " 23:59:59";
            }
            else if(params.get("endDate").toString().length() == 16){
                endDate = params.get("endDate").toString() + ":59";
            }
            params.put("endDate", endDate);
        }
        List<Map> list = reportMapper.getAllPayWays(params);
        apiRest.setData(list);
        apiRest.setIsSuccess(true);
        apiRest.setMessage("查询支付方式成功");
        return apiRest;
    }

    /**
     * 门店支付方式汇总报表
     */
    public ApiRest branchPaymentMethodReport(Map params) {
        ApiRest apiRest = new ApiRest();
        BigInteger tenantId = BigInteger.valueOf(Long.parseLong((String)params.get("tenantId")));
        /*BigInteger branchId = BigInteger.valueOf(Long.parseLong((String)params.get("branchId")));*/
        Integer rows=0;
        Integer offset=0;
        Map map = new HashMap();
        if(params.get("rows")!=null){
            rows = Integer.parseInt(params.get("rows").toString());
        }else{
            rows = 0;
        }
        if(params.get("page")!=null){
            offset = (Integer.parseInt(params.get("page").toString()) - 1) * rows;
        }else{
            offset=10;
        }
        //区域权限控制
        //拥有本店权限
        BigInteger userId = BigInteger.valueOf(Long.parseLong(params.get("userId").toString()));
        Employee e = employeeMapper.getEmployee(userId, tenantId);
        List<Map> areaList;
        String areaId;
        String queryId = "";
        if(params.get("areaId") != null && !params.get("areaId").equals("")){
            areaId = params.get("areaId").toString();
            if(Integer.parseInt(areaId) == -1){
                //区域权限控制
                if(params.get("isUserAreas") != null && "true".equals(params.get("isUserAreas"))){
                    if(!"0000".equals(e.getCode())){
                        queryId = e.getUserAreas() == null ? "" : e.getUserAreas();
                    } else{
                        areaList = reportMapper.firstAreaList("-1", tenantId);
                        for(int x = 0; x < areaList.size(); x++){
                            Map map1 = areaList.get(x);
                            areaId = map1.get("id").toString();
                            List<Map> aList = reportMapper.areaList(areaId);
                            queryId += aList.get(0).get("ids").toString().substring(2) + ",";
                        }
                        queryId = queryId.substring(0, queryId.lastIndexOf(","));
                    }
                } else{
                    areaList = reportMapper.firstAreaList("-1", tenantId);
                    for(int x = 0; x < areaList.size(); x++){
                        Map map1 = areaList.get(x);
                        areaId = map1.get("id").toString();
                        List<Map> aList = reportMapper.areaList(areaId);
                        queryId += aList.get(0).get("ids").toString().substring(2) + ",";
                    }
                    queryId = queryId.substring(0, queryId.lastIndexOf(","));
                }
            } else{
                areaList = reportMapper.areaList(areaId);
                queryId = areaList.get(0).get("ids").toString().substring(2);
                if(params.get("isUserAreas") != null && "true".equals(params.get("isUserAreas"))){
                    String qId[] = queryId.split(",");
                    String ids = "";
                    if(!"0000".equals(e.getCode())){
                        for(int i = 0; i < qId.length; i++){
                            if(e.getUserAreas().indexOf(qId[i]) != -1){
                                ids += qId[i] + ",";
                            }
                        }
                        queryId = ids.substring(0, ids.lastIndexOf(","));
                    }
                }
            }
            if(!queryId.equals("")){
                if(Integer.parseInt(areaId) != -1){
                    params.put("branchId", null);
                }
            }
        } else{
            if("0000".equals(e.getCode())){
                areaList = reportMapper.firstAreaList("-1", tenantId);
                for(int x = 0; x < areaList.size(); x++){
                    Map map1 = areaList.get(x);
                    areaId = map1.get("id").toString();
                    List<Map> aList = reportMapper.areaList(areaId);
                    queryId += aList.get(0).get("ids").toString().substring(2) + ",";
                }
                queryId = queryId.substring(0, queryId.lastIndexOf(","));
            } else{
                if(e.getUserAreas() != null && !"".equals(e.getUserAreas())){
                    queryId = e.getUserAreas();
                }else{
                    BigInteger branchId = e.getBranchId();
                    params.put("branchId", branchId);
                }
            }
        }
        params.put("areaId", queryId);
        params.put("rows", rows);
        params.put("offset", offset);
        params.put("tenantId", tenantId);
       /* params.put("branchId", branchId);*/
        if(params.get("startDate") != null && !params.get("startDate").equals("")){
            String startDate = "";
            if(params.get("startDate").toString().length() == 10){
                startDate = params.get("startDate").toString() + " 00:00:00";
            }
            else if(params.get("startDate").toString().length() == 16){
                startDate = params.get("startDate").toString() + ":00";
            }
            params.put("startDate", startDate);
        }
        if(params.get("endDate") != null && !params.get("endDate").equals("")){
            String endDate = "";
            if(params.get("endDate").toString().length() == 10){
                endDate = params.get("endDate").toString() + " 23:59:59";
            }
            else if(params.get("endDate").toString().length() == 16){
                endDate = params.get("endDate").toString() + ":59";
            }
            params.put("endDate", endDate);
        }
        List<Map> resultList = new ArrayList<>();
        List<Map> resultFooterList = new ArrayList<>();
        List<Map> payWays = reportMapper.paymentMethodWays(params);
        if((payWays == null || payWays.size() == 0) && "1".equals(params.get("isApp"))){
            map.put("rows", new ArrayList<Map>());
            Map map1 = new HashMap();
            map1.put("totalAmount", 0);
            map.put("footer",resultFooterList );
            map.put("total", 0);
        }else{
            params.put("payWays", payWays);
            List<Map> list = reportMapper.paymentMethodReport(params);
            List<Map> footer = reportMapper.paymentMethodReportSum(params);
            for(Map i : list){
                Map result = new HashMap();
                for(Object key : i.keySet()){
                    if(key.toString().contains("ZDY")){
                        result.put("ZDY_" + key.toString().substring(3, key.toString().length()), i.get(key));
                    }
                    else{
                        result.put(key, i.get(key));
                    }
                }
                resultList.add(result);
            }
            for(Map i : footer){
                Map result = new HashMap();
                for(Object key : i.keySet()){
                    if(key.toString().contains("ZDY")){
                        result.put("ZDY_" + key.toString().substring(3, key.toString().length()), i.get(key));
                    }
                    else{
                        result.put(key, i.get(key));
                    }
                }
                resultFooterList.add(result);
            }
            map.put("rows", resultList);
            map.put("footer", resultFooterList);
            map.put("total", footer.get(0).get("total"));
        }
        apiRest.setIsSuccess(true);
        apiRest.setData(map);
        apiRest.setMessage("查询收款对账报表成功！");
        return apiRest;
    }

    /**
     *  机构汇总
     * */
    public ApiRest gatheringOfBranchReport(Map params){
        ApiRest apiRest = new ApiRest();
        BigInteger tenantId = BigInteger.valueOf(Long.parseLong((String)params.get("tenantId")));
        Map map = new HashMap();
        Integer rows = Integer.parseInt(params.get("rows").toString());
        Integer offset = (Integer.parseInt(params.get("page").toString()) - 1) * rows;
        params.put("rows", rows);
        params.put("offset", offset);
        //区域权限控制
        //拥有本店权限
        BigInteger userId = BigInteger.valueOf(Long.parseLong(params.get("userId").toString()));
        Employee e = employeeMapper.getEmployee(userId, tenantId);
        List<Map> areaList;
        String areaId;
        String queryId = "";
        if(params.get("areaId") != null && !params.get("areaId").equals("")){
            areaId = params.get("areaId").toString();
            if(Integer.parseInt(areaId) == -1){
                //区域权限控制
                if(params.get("isUserAreas") != null && "true".equals(params.get("isUserAreas"))){
                    if(!"0000".equals(e.getCode())){
                        queryId = e.getUserAreas() == null ? "" : e.getUserAreas();
                    } else{
                        areaList = reportMapper.firstAreaList("-1", tenantId);
                        for(int x = 0; x < areaList.size(); x++){
                            Map map1 = areaList.get(x);
                            areaId = map1.get("id").toString();
                            List<Map> aList = reportMapper.areaList(areaId);
                            queryId += aList.get(0).get("ids").toString().substring(2) + ",";
                        }
                        queryId = queryId.substring(0, queryId.lastIndexOf(","));
                    }
                } else{
                    areaList = reportMapper.firstAreaList("-1", tenantId);
                    for(int x = 0; x < areaList.size(); x++){
                        Map map1 = areaList.get(x);
                        areaId = map1.get("id").toString();
                        List<Map> aList = reportMapper.areaList(areaId);
                        queryId += aList.get(0).get("ids").toString().substring(2) + ",";
                    }
                    queryId = queryId.substring(0, queryId.lastIndexOf(","));
                }
            } else{
                areaList = reportMapper.areaList(areaId);
                queryId = areaList.get(0).get("ids").toString().substring(2);
                if(params.get("isUserAreas") != null && "true".equals(params.get("isUserAreas"))){
                    String qId[] = queryId.split(",");
                    String ids = "";
                    if(!"0000".equals(e.getCode())){
                        for(int i = 0; i < qId.length; i++){
                            if(e.getUserAreas().indexOf(qId[i]) != -1){
                                ids += qId[i] + ",";
                            }
                        }
                        queryId = ids.substring(0, ids.lastIndexOf(","));
                    }
                }
            }
            if(!queryId.equals("")){
                if(Integer.parseInt(areaId) != -1){
                    params.put("branchId", null);
                }
            }
        } else{
            if("0000".equals(e.getCode())){
                areaList = reportMapper.firstAreaList("-1", tenantId);
                for(int x = 0; x < areaList.size(); x++){
                    Map map1 = areaList.get(x);
                    areaId = map1.get("id").toString();
                    List<Map> aList = reportMapper.areaList(areaId);
                    queryId += aList.get(0).get("ids").toString().substring(2) + ",";
                }
                queryId = queryId.substring(0, queryId.lastIndexOf(","));
            } else{
                if(e.getUserAreas() != null && !"".equals(e.getUserAreas())){
                    queryId = e.getUserAreas();
                }else{
                    BigInteger branchId = e.getBranchId();
                    params.put("branchId", branchId);
                }
            }
        }
        params.put("areaId", queryId);
        if(params.get("startDate") != null && !params.get("startDate").equals("")){
            String startDate = "";
            if(params.get("startDate").toString().length() == 10){
                startDate = params.get("startDate").toString() + " 00:00:00";
            }
            else if(params.get("startDate").toString().length() == 16){
                startDate = params.get("startDate").toString() + ":00";
            }
            params.put("startDate", startDate);
        }
        if(params.get("endDate") != null && !params.get("endDate").equals("")){
            String endDate = "";
            if(params.get("endDate").toString().length() == 10){
                endDate = params.get("endDate").toString() + " 23:59:59";
            }
            else if(params.get("endDate").toString().length() == 16){
                endDate = params.get("endDate").toString() + ":59";
            }
            params.put("endDate", endDate);
        }
        //查询该段时间内使用的支付方式
        List<Map> storePayWays = reportMapper.storePayWays(params);
        List<Map> payWays = reportMapper.paymentMethodWays(params);
        params.put("storePayWays", storePayWays);
        params.put("payWays", payWays);
        List<Map> list = reportMapper.gatheringOfBranchReport(params);
        map.put("rows", list);
        map.put("total", list.size());
        apiRest.setIsSuccess(true);
        apiRest.setData(map);
        apiRest.setMessage("查询机构收款汇总成功");
        return apiRest;
    }


    /**
     * 收款对账报表
     *
     * @param params
     * @return
     */
    public ApiRest payReport(Map params){
        ApiRest apiRest = new ApiRest();
        BigInteger tenantId = BigInteger.valueOf(Long.parseLong((String)params.get("tenantId")));
        BigInteger branchId = BigInteger.valueOf(Long.parseLong((String)params.get("branchId")));
        Map map = new HashMap();
        Integer rows = Integer.parseInt(params.get("rows").toString());
        Integer offset = (Integer.parseInt(params.get("page").toString()) - 1) * rows;
        params.put("rows", rows);
        params.put("offset", offset);
        params.put("tenantId", tenantId);
        params.put("branchId", branchId);
        if(params.get("startDate") != null && !params.get("startDate").equals("")){
            String startDate = "";
            if(params.get("startDate").toString().length() == 10){
                startDate = params.get("startDate").toString() + " 00:00:00";
            }
            else if(params.get("startDate").toString().length() == 16){
                startDate = params.get("startDate").toString() + ":00";
            }
            params.put("startDate", startDate);
        }
        if(params.get("endDate") != null && !params.get("endDate").equals("")){
            String endDate = "";
            if(params.get("endDate").toString().length() == 10){
                endDate = params.get("endDate").toString() + " 23:59:59";
            }
            else if(params.get("endDate").toString().length() == 16){
                endDate = params.get("endDate").toString() + ":59";
            }
            params.put("endDate", endDate);
        }
        List<Map> payWays = reportMapper.payWays(params);
        params.put("payWays", payWays);
        if(params.get("field") != null && !params.get("field").equals("") && params.get("order") != null && !params.get("order").equals("")){
            params.put("field", CommonUtils.formatOrderField(params.get("field").toString()));
            params.put("order", params.get("order").toString());
        }
        if(params.get("parentId") != null){
            params.remove("branchId");
        }
        List<Map> list = reportMapper.payReport(params);
        List<Map> footer = reportMapper.payReportSum(params);
        List<Map> resultList = new ArrayList<>();
        List<Map> resultFooterList = new ArrayList<>();
        for(Map i : list){
            Map result = new HashMap();
            for(Object key : i.keySet()){
                if(key.toString().contains("ZDY")){
                    result.put("ZDY_" + key.toString().substring(3, key.toString().length()), i.get(key));
                }
                else{
                    result.put(key, i.get(key));
                }
            }
            resultList.add(result);
        }
        for(Map i : footer){
            Map result = new HashMap();
            for(Object key : i.keySet()){
                if(key.toString().contains("ZDY")){
                    result.put("ZDY_" + key.toString().substring(3, key.toString().length()), i.get(key));
                }
                else{
                    result.put(key, i.get(key));
                }
            }
            resultFooterList.add(result);
        }
        map.put("rows", resultList);
        map.put("footer", resultFooterList);
        map.put("total", footer.get(0).get("total"));
        apiRest.setIsSuccess(true);
        apiRest.setData(map);
        apiRest.setMessage("查询收款对账报表成功！");
        return apiRest;
    }

    /**
     * 储值对账
     */
    public ApiRest storedValueReport(Map params){
        ApiRest apiRest = new ApiRest();
        Integer offset = 0;
        Map map = new HashMap();
        BigInteger tenantId = BigInteger.valueOf(Long.parseLong(params.get("tenantId").toString()));
        BigInteger branchId = BigInteger.valueOf(Long.parseLong(params.get("branchId").toString()));
        Integer rows = Integer.parseInt(params.get("rows").toString());
        if(Integer.parseInt(params.get("page").toString()) != 0){
            offset = (Integer.parseInt(params.get("page").toString()) - 1) * rows;
        }
        if(null != params.get("storeFrom")){
            if(params.get("storeFrom").equals("3")){
                params.remove("storeFrom");
            }
            else{
                params.put("storeFrom", Integer.parseInt(params.get("storeFrom").toString()));
            }
        }
        params.put("rows", rows);
        params.put("offset", offset);
        params.put("tenantId", tenantId);
        params.put("branchId", branchId);
        params.put("storeBranchId", branchId);
        if(params.get("field") != null && !params.get("field").equals("") && params.get("order") != null && !params.get("order").equals("")){
            params.put("field", CommonUtils.formatOrderField(params.get("field").toString()));
            params.put("order", params.get("order").toString());
        }
        if(params.get("parentId") != null){
            params.remove("storeBranchId");
        }
        List<Map> list = reportMapper.storedValueReport(params);//相同条件下分页查询数据
        List<Map> totalsum = reportMapper.storedValueReportTotalSum(params);//总条数 和合计值 sums
        map.put("rows", list);
        map.put("footer", totalsum);
        map.put("total", totalsum.get(0).get("total"));
        apiRest.setIsSuccess(true);
        apiRest.setData(map);
        apiRest.setMessage("查询储值对账报表成功！");
        return apiRest;
    }

    /**
     * 收款日对账
     */
    public ApiRest dayPayReport(Map params){
        ApiRest apiRest = new ApiRest();
        Integer offset = 0;
        BigInteger tenantId = BigInteger.valueOf(Long.parseLong(params.get("tenantId").toString()));
        BigInteger branchId = BigInteger.valueOf(Long.parseLong(params.get("branchId").toString()));
        Map map = new HashMap();
        Integer rows = Integer.parseInt(params.get("rows").toString());
        if(Integer.parseInt(params.get("page").toString()) != 0){
            offset = (Integer.parseInt(params.get("page").toString()) - 1) * rows;
        }

        params.put("rows", rows);
        params.put("offset", offset);
        params.put("tenantId", tenantId);
        params.put("branchId", branchId);
        List<Map> payWays = reportMapper.payWays(params);
        params.put("payWays", payWays);
        List<Map> list = reportMapper.dayPayReport(params);
        List<Map> footer = reportMapper.dayPayReportSum(params);
        List<Map> resultList = new ArrayList<>();
        List<Map> resultFooterList = new ArrayList<>();
        for(Map i : list){
            Map result = new HashMap();
            for(Object key : i.keySet()){
                if(key.toString().contains("ZDY")){
                    result.put("ZDY_" + key.toString().substring(3, key.toString().length()), i.get(key));
                }
                else{
                    result.put(key, i.get(key));
                }
            }
            resultList.add(result);
        }
        for(Map i : footer){
            Map result = new HashMap();
            for(Object key : i.keySet()){
                if(key.toString().contains("ZDY")){
                    result.put("ZDY_" + key.toString().substring(3, key.toString().length()), i.get(key));
                }
                else{
                    result.put(key, i.get(key));
                }
            }
            resultFooterList.add(result);
        }
        map.put("rows", resultList);
        map.put("footer", resultFooterList);
        map.put("total", footer.get(0).get("total"));
        apiRest.setIsSuccess(true);
        apiRest.setData(map);
        apiRest.setMessage("查询收款日对账报表成功！");
        return apiRest;
    }

    /**
     * 支付汇总（微信）报表
     *
     * @param params
     * @return
     */
    public ApiRest paymentReport(Map params){
        ApiRest apiRest = new ApiRest();
        BigInteger tenantId = BigInteger.valueOf(Long.parseLong((String)params.get("tenantId")));
        BigInteger branchId = BigInteger.valueOf(Long.parseLong((String)params.get("branchId")));
        Map map = new HashMap();
        Integer rows = Integer.parseInt(params.get("rows") == null ? String.valueOf(20) : params.get("rows").toString());
        Integer offset = (Integer.parseInt(params.get("page") == null ? String.valueOf(1) : params.get("page").toString()) - 1) * rows;
        params.put("rows", rows);
        params.put("offset", offset);
        params.put("tenantId", tenantId);
        params.put("branchId", branchId);
        if(params.get("startDate") != null && !params.get("startDate").equals("")){
            String startDate = "";
            if(params.get("startDate").toString().length() == 10){
                startDate = params.get("startDate").toString() + " 00:00:00";
            }
            else if(params.get("startDate").toString().length() == 16){
                startDate = params.get("startDate").toString() + ":00";
            }
            params.put("startDate", startDate);
        }
        if(params.get("endDate") != null && !params.get("endDate").equals("")){
            String endDate = "";
            if(params.get("endDate").toString().length() == 10){
                endDate = params.get("endDate").toString() + " 23:59:59";
            }
            else if(params.get("endDate").toString().length() == 16){
                endDate = params.get("endDate").toString() + ":59";
            }
            params.put("endDate", endDate);
        }
        List<Map> payWays = reportMapper.payWays(params);
        params.put("payWays", payWays);
        List<Map> list = reportMapper.paymentReport(params);
        map.put("rows", list);
        map.put("total", payWays.size());
        apiRest.setIsSuccess(true);
        apiRest.setData(map);
        apiRest.setMessage("查询支付汇总报表成功！");
        return apiRest;
    }

    /**
     * 获取收款对账的支付方式
     *
     * @param params
     * @return
     */
    public ApiRest payWays(Map params){
        ApiRest apiRest = new ApiRest();
        BigInteger tenantId = BigInteger.valueOf(Long.parseLong((String)params.get("tenantId")));
        BigInteger branchId = BigInteger.valueOf(Long.parseLong((String)params.get("branchId")));
        params.put("tenantId", tenantId);
        params.put("branchId", branchId);
        if(params.get("startDate") != null && !params.get("startDate").equals("")){
            String startDate = "";
            if(params.get("startDate").toString().length() == 10){
                startDate = params.get("startDate").toString() + " 00:00:00";
            }
            else if(params.get("startDate").toString().length() == 16){
                startDate = params.get("startDate").toString() + ":00";
            }
            params.put("startDate", startDate);
        }
        if(params.get("endDate") != null && !params.get("endDate").equals("")){
            String endDate = "";
            if(params.get("endDate").toString().length() == 10){
                endDate = params.get("endDate").toString() + " 23:59:59";
            }
            else if(params.get("endDate").toString().length() == 16){
                endDate = params.get("endDate").toString() + ":59";
            }
            params.put("endDate", endDate);
        }
        List<Map> payWays = reportMapper.payWays(params);
        apiRest.setIsSuccess(true);
        apiRest.setData(payWays);
        apiRest.setMessage("查询支付方式成功！");
        return apiRest;
    }

    /**
     * 分页查询员工收银情况报表
     * 注：只统计当前条件下使用过的支付方式
     *
     * @author hxh
     */
    public Pager querySaleEmpWorkPager(SaleEmpWorkPagerModel model){
        //当前条件下使用过的支付方式
        List<Map<String, Object>> payInfo = reportMapper.usedPaymentInfo(model);
        List<String> payCodes = new ArrayList<>();
        for(Map<String, Object> info : payInfo){
            payCodes.add((String)info.get("paymentCode"));
        }

        Map<String, Object> count = reportMapper.querySaleEmpWorkCount(model, payCodes);
        long num = (long)count.get("num");
        count.remove("num");
        if(num > 0){
            model.getPager().setRows(reportMapper.querySaleEmpWorkPager(model, payCodes));
        }
        model.getPager().setTotal(num);
        model.getPager().setFooter(count);
        return model.getPager();
    }

    /**
     * 毛利分析报表
     *
     * @author hxh
     */
    public Pager queryGrossProfitPager(GrossProfitPagerModel model){
        Map<String, Object> count = reportMapper.queryGrossProfitCount(model);
        long num = (long)count.get("num");
        count.remove("num");
        if(num > 0){
            model.getPager().setRows(reportMapper.queryGrossProfitPager(model));
        }
        model.getPager().setTotal(num);
        model.getPager().setFooter(count);
        return model.getPager();
    }

    /**
     * 销售趋势报表
     *
     * @param params
     * @return
     */
    public ApiRest saleTrend30DaysReport(Map params) throws ParseException{
        ApiRest apiRest = new ApiRest();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String end = sdf.format(new Date());
        String start = reportMapper.getDate(end, -29);
        List dateList = new ArrayList();
        Map dataMap = new HashMap();
        Calendar startC = Calendar.getInstance();
        Date startD = sdf.parse(start);
        startC.setTime(startD);
        Calendar endC = Calendar.getInstance();
        Date endD = sdf.parse(end);
        endC.setTime(endD);
        dateList.add(sdf.format(startC.getTime()));
        while(startC.before(endC)){
            startC.add(Calendar.DATE, 1);
            dateList.add(sdf.format(startC.getTime()));
        }
        BigInteger tenantId = BigInteger.valueOf(Long.valueOf(params.get("tenantId").toString()));
        if(params.get("parentId")!=null && !"".equals(params.get("parentId").toString())){
            params.remove("branchId");
            BigInteger parentId = BigInteger.valueOf(Long.valueOf(params.get("parentId").toString()));
            List<BigInteger> resultList = new ArrayList<>();
            resultList.add(parentId);
            resultList = organizationService.getChildBranchIds(tenantId,parentId,resultList);
            String branchIdStr = "";
            for(BigInteger id:resultList){
                branchIdStr = (branchIdStr+id+",");
            }
            if(branchIdStr.length()>0){
                branchIdStr = branchIdStr.substring(0,branchIdStr.length()-1);
            }
            params.put("branchIds",branchIdStr);
        }
        String startDate = start + " 00:00:00";
        String endDate = end + " 23:59:59";
        params.put("startDate", startDate);
        params.put("endDate", endDate);
        List<Map> list = reportMapper.saleTrend30DaysReport(params);
        List<Map> footer = reportMapper.saleTrend30DaysReportSum(params);
        BigDecimal pageSum = new BigDecimal(0);
        NumberFormat nf = new DecimalFormat("##0.00");
        BigDecimal sumAmount = BigDecimal.valueOf(Double.valueOf(footer.get(0).get("realAmount").toString()));
        for(Map i : list){
            BigDecimal totalAmount = BigDecimal.valueOf(Double.valueOf(i.get("realAmount").toString()));
            pageSum = pageSum.add(totalAmount);
            if(!Objects.equals(sumAmount, BigDecimal.valueOf(0)) && !Objects.equals(totalAmount, BigDecimal.valueOf(0))){
                i.put("percentage", nf.format(totalAmount.divide(sumAmount, 10, BigDecimal.ROUND_UP).multiply(BigDecimal.valueOf(100))) + "%");
            }
            else{
                i.put("percentage", "0.00%");
            }
        }
        for(Map i : list){
            Map data = new HashMap();
            data.put("percentage", i.get("percentage"));
            data.put("realAmount", i.get("realAmount"));
            data.put("returnNum", i.get("returnNum"));
            data.put("saleNum", i.get("saleNum"));
            data.put("saleUnitPrice", i.get("saleUnitPrice"));
            dataMap.put(i.get("checkoutAt"), data);
        }
        Map result = new HashMap();
        result.put("dateList", dateList);
        result.put("rows", dataMap);
        apiRest.setIsSuccess(true);
        apiRest.setData(result);
        apiRest.setMessage("查询30天销售趋势成功！");
        return apiRest;
    }

    /**
     * 支付方式汇总
     *
     * @return
     */
    public ApiRest paymentTypeReport(Map params){
        ApiRest apiRest = new ApiRest();
        String tenantId = (String)params.get("tenantId");
        String branchId = (String)params.get("branchId");
        Map map = new HashMap();
        Integer rows = Integer.parseInt(params.get("rows").toString());
        Integer page = Integer.parseInt(params.get("page").toString());
        Integer offset = (page - 1) * rows;
        params.put("rows", rows);
        params.put("offset", offset);
        params.put("branchId", branchId);
        params.put("tenantId", tenantId);
        if(params.get("startDate") != null && !params.get("startDate").equals("")){
            String startDate = "";
            if(params.get("startDate").toString().length() == 10){
                startDate = params.get("startDate").toString() + " 00:00:00";
            }
            else if(params.get("startDate").toString().length() == 16){
                startDate = params.get("startDate").toString() + ":00";
            }
            params.put("startDate", startDate);
        }
        if(params.get("endDate") != null && !params.get("endDate").equals("")){
            String endDate = "";
            if(params.get("endDate").toString().length() == 10){
                endDate = params.get("endDate").toString() + " 23:59:59";
            }
            else if(params.get("endDate").toString().length() == 16){
                endDate = params.get("endDate").toString() + ":59";
            }
            params.put("endDate", endDate);
        }
        /*
         * 查询商户下所有支付方式
         * */
        List<Map> payWays = reportMapper.allPayment(params);
        StringBuffer allPayments = new StringBuffer();
        for(int i = 0; i < payWays.size(); i++){
            Map payWaysMap = payWays.get(i);
            allPayments.append("'" + (payWaysMap.get("paymentCode").toString()) + "'").append(",");
        }
        if(allPayments.indexOf(",") != -1){
            params.put("allPayments", allPayments.substring(0, allPayments.lastIndexOf(",")).toString());
        }
        else{
            params.put("allPayments", allPayments.toString());
        }
        List<Map> list = reportMapper.paymentTypeReport(params);
        List<Map> footer = reportMapper.paymentTypeSum(params);
        map.put("rows", list);
        map.put("footer", footer);
        map.put("total", footer.get(0).get("total"));
        apiRest.setIsSuccess(true);
        apiRest.setData(map);
        apiRest.setMessage("查询支付方式汇总表成功！");
        return apiRest;
    }

    /**
     * 区域销售汇总表
     *
     * @param params
     * @return
     */
    public ApiRest areaReport(Map params){
        ApiRest apiRest = new ApiRest();
        String areaId = "-1";
        BigInteger tenantId = BigInteger.valueOf(Long.parseLong((String)params.get("tenantId")));
        if(params.get("areaId") != null && !"".equals(params.get("areaId"))){
            areaId = (String)params.get("areaId");
        }
        /*if (params.get("areaId") == null){
            areaId = reportMapper.findAreaId(tenantId).toString();
        }else{
            areaId = (String) params.get("areaId");
        }*/
        Integer rows = Integer.parseInt((String)params.get("rows"));
        Integer page = Integer.parseInt((String)params.get("page"));
        Integer offset = (page - 1) * rows;
        params.put("rows", rows);
        params.put("offset", offset);
        params.put("tenantId", tenantId);
        if(params.get("startDate") != null && !params.get("startDate").equals("")){
            String startDate = "";
            if(params.get("startDate").toString().length() == 10){
                startDate = params.get("startDate").toString() + " 00:00:00";
            }
            else if(params.get("startDate").toString().length() == 16){
                startDate = params.get("startDate").toString() + ":00";
            }
            params.put("startDate", startDate);
        }
        if(params.get("endDate") != null && !params.get("endDate").equals("")){
            String endDate = "";
            if(params.get("endDate").toString().length() == 10){
                endDate = params.get("endDate").toString() + " 23:59:59";
            }
            else if(params.get("endDate").toString().length() == 16){
                endDate = params.get("endDate").toString() + ":59";
            }
            params.put("endDate", endDate);
        }
        //选最高级区域
        List<Map> firstAreaList;
        BigInteger userId = BigInteger.valueOf(Long.parseLong(params.get("userId").toString()));
        Employee e = employeeMapper.getEmployee(userId, tenantId);
        String userAreas = e.getUserAreas();
        if(!"0000".equals(e.getCode()) && (userAreas == null || "".equals(userAreas))){
            userAreas = "null";
            Map nMap = new HashMap();
            List footer = new ArrayList<Map>();
            Map fMap = new HashMap();
            fMap.put("totalAmount", 0);
            fMap.put("tc", 0);
            fMap.put("ac", 0);
            fMap.put("tradeAmount", 0);
            fMap.put("storeAmount", 0);
            footer.add(fMap);
            nMap.put("rows", new ArrayList<Map>());
            nMap.put("footer", footer);
            nMap.put("total", 0);
            apiRest.setIsSuccess(true);
            apiRest.setData(nMap);
            apiRest.setMessage("查询区域销售汇总表成功！");
            return apiRest;
        }
        int authorityType = 0;
        if("0000".equals(e.getCode())){
            authorityType = 1;
        }
        if(Integer.parseInt(areaId) == -1){
            //查询所有一级区域
            firstAreaList = reportMapper.findFirstArea(tenantId, userAreas, authorityType);
            if(firstAreaList.size() == 0){
                Map nMap = new HashMap();
                List footer = new ArrayList<Map>();
                Map fMap = new HashMap();
                fMap.put("totalAmount", 0);
                fMap.put("tc", 0);
                fMap.put("ac", 0);
                fMap.put("tradeAmount", 0);
                fMap.put("storeAmount", 0);
                footer.add(fMap);
                nMap.put("rows", new ArrayList<Map>());
                nMap.put("footer", footer);
                nMap.put("total", 0);
                apiRest.setIsSuccess(true);
                apiRest.setData(nMap);
                apiRest.setMessage("查询区域销售汇总表成功！");
                return apiRest;
            }
            if(!"0000".equals(e.getCode())){
                String firstAreaId = firstAreaList.get(0).get("id").toString();
                if(userAreas == null || "".equals(userAreas) || userAreas.indexOf(firstAreaId) == -1){
                    Map nMap = new HashMap();
                    nMap.put("rows", new ArrayList<Map>());
                    nMap.put("footer", new ArrayList<Map>());
                    nMap.put("total", 0);
                    apiRest.setIsSuccess(true);
                    apiRest.setData(nMap);
                    apiRest.setMessage("查询区域销售汇总表成功！");
                    return apiRest;
                }
            }
            List<Map> arealist = new ArrayList();
            Map map = new HashMap();
            for(int j = 0; j < firstAreaList.size(); j++){
                List<Map> list;
                Map firstAreaMap = firstAreaList.get(j);
                BigInteger aId = BigInteger.valueOf(Long.parseLong(firstAreaMap.get("id").toString()));
                params.put("parentId", -1);
                params.put("areaId", aId);
                /*
                 * 统计子区域
                 * 1.查询是否有子区域
                 * 2.添加子区域id
                 * */
                Boolean flag = false;
                String allAreaId = "";
                String pId = aId.toString();
                while(!flag){
                    String tmpId = "";
                    //查询是否有子区域
                    Integer count = reportMapper.isSubregion(pId.toString(), tenantId);
                    if(count > 0){
                        List<Map> subregionList = reportMapper.findSubregions(pId.toString(), tenantId);
                        for(int i = 0; i < subregionList.size(); i++){
                            //拼接子区域ID
                            allAreaId += subregionList.get(i).get("id") + ",";
                            tmpId += subregionList.get(i).get("id") + ",";
                        }
                        pId = tmpId.substring(0, tmpId.lastIndexOf(","));
                    }
                    else{
                        flag = true;
                    }
                }
                allAreaId = aId + "," + allAreaId;
                if(params.get("isUserAreas") != null && "true".equals(params.get("isUserAreas")) && !"0000".equals(e.getCode())){
                    params.put("allAreaId", userAreas);
                }
                else{
                    params.put("allAreaId", allAreaId.substring(0, allAreaId.lastIndexOf(",")));
                }
                params.put("startDate", params.get("startDate"));
                params.put("endDate", params.get("endDate"));
                list = reportMapper.areaReport(params);
                arealist.add(list.get(0));
            }

            //判断是否含有子区域
            for(int x = 0; x < arealist.size(); x++){
                Map map1 = arealist.get(x);
                String id = String.valueOf(map1.get("id"));
                Integer count = reportMapper.isSubregion(id, tenantId);
                if(count > 0){
                    map1.put("isSubregion", true);
                }
                else{
                    map1.put("isSubregion", false);
                }
            }

            //foot
            List<Map> footer = new ArrayList();
            double total_amount = 0.00; //总营业额
            Integer tc = 0;             //总客单
            double ac = 0.00;           //平均客单额
            double trade_amount = 0.00; //会员消费总额
            double store_amount = 0.00; //会员储值总额
            for(int m = 0; m < arealist.size(); m++){
                Map map2 = arealist.get(m);
                total_amount += Double.parseDouble(map2.get("totalAmount").toString());
                tc += Integer.parseInt(map2.get("tc").toString());
                trade_amount += Double.parseDouble(map2.get("tradeAmount").toString());
                store_amount += Double.parseDouble(map2.get("storeAmount").toString());
            }
            if(tc == 0){
                ac = 0;
            }
            else{
                ac = total_amount / tc;
            }
            Map fMap = new HashMap();
            fMap.put("totalAmount", total_amount);
            fMap.put("tc", tc);
            fMap.put("ac", ac);
            fMap.put("tradeAmount", trade_amount);
            fMap.put("storeAmount", store_amount);
            footer.add(fMap);

            List nList = new ArrayList();
            Integer count;
            if((rows + offset) < arealist.size()){
                count = rows;
            }
            else{
                count = arealist.size();
            }
            for(int j = offset; j < count; j++){
                Map nMap = arealist.get(j);
                nList.add(nMap);
            }
            map.put("rows", nList);
            map.put("footer", footer);
            map.put("total", firstAreaList.size());
            apiRest.setIsSuccess(true);
            apiRest.setData(map);
            apiRest.setMessage("查询子区域销售汇总表成功！");
        }
        else{
            Map map = new HashMap();
            params.put("parentId", areaId);
            params.put("areaId", areaId);
            /*
             * 统计子区域
             * 1.查询是否有子区域
             * 2.添加子区域id
             * */
            Boolean flag = false;
            String allAreaId = "";
            String pId = areaId;
            while(!flag){
                String tmpId = "";
                //查询是否有子区域
                Integer count = reportMapper.isSubregion(pId, tenantId);
                if(count > 0){
                    List<Map> subregionList = reportMapper.findSubregions(pId, tenantId);
                    for(int i = 0; i < subregionList.size(); i++){
                        //拼接子区域ID
                        allAreaId += subregionList.get(i).get("id") + ",";
                        tmpId += subregionList.get(i).get("id") + ",";
                    }
                    pId = tmpId.substring(0, tmpId.lastIndexOf(","));
                }
                else{
                    flag = true;
                }
            }
            allAreaId = areaId + "," + allAreaId;
            if(params.get("isUserAreas") != null && "true".equals(params.get("isUserAreas")) && !"0000".equals(e.getCode())){
                String allAreaIds = allAreaId.substring(0, allAreaId.lastIndexOf(","));
                String aId[] = allAreaIds.split(",");
                String aIds = "";
                for(int i = 0; i < aId.length; i++){
                    String id = aId[i];
                    if(userAreas.indexOf(id) != -1){
                        aIds += aId[i] + ",";
                    }
                }
                params.put("allAreaId", aIds.substring(0, aIds.lastIndexOf(",")));
            }
            else{
                params.put("allAreaId", allAreaId.substring(0, allAreaId.lastIndexOf(",")));
            }
            params.put("startDate", params.get("startDate"));
            params.put("endDate", params.get("endDate"));
            List<Map> list = reportMapper.areaReport(params);
            List<Map> footer = reportMapper.areaSum(params);
            for(int i = 0; i < list.size(); i++){
                Map map1 = list.get(i);
                String id = String.valueOf(map1.get("id"));
                Integer count = reportMapper.isSubregion(id, tenantId);
                if(count > 0){
                    map1.put("isSubregion", true);
                }
                else{
                    map1.put("isSubregion", false);
                }
            }
            map.put("rows", list);
            map.put("footer", footer);
            map.put("total", footer.get(0).get("total"));
            apiRest.setIsSuccess(true);
            apiRest.setData(map);
            apiRest.setMessage("查询区域销售汇总表成功！");
        }
        return apiRest;
    }

    /**
     * 子区域销售汇总
     *
     * @param params
     * @return
     */
    public ApiRest subregionReport(Map params){
        ApiRest apiRest = new ApiRest();
        BigInteger parentId = BigInteger.valueOf(Long.parseLong(params.get("parentId").toString()));
        BigInteger tenantId = BigInteger.valueOf(Long.parseLong(params.get("tenantId").toString()));
        Map map = new HashMap();
        Integer rows = Integer.parseInt(params.get("rows").toString());
        Integer page = Integer.parseInt(params.get("page").toString());
        Integer offset = (page - 1) * rows;
        params.put("rows", rows);
        params.put("offset", offset);
        params.put("parentId", parentId);
        if(params.get("startDate") != null && !params.get("startDate").equals("")){
            String startDate = "";
            if(params.get("startDate").toString().length() == 10){
                startDate = params.get("startDate").toString() + " 00:00:00";
            }
            else if(params.get("startDate").toString().length() == 16){
                startDate = params.get("startDate").toString() + ":00";
            }
            params.put("startDate", startDate);
        }
        if(params.get("endDate") != null && !params.get("endDate").equals("")){
            String endDate = "";
            if(params.get("endDate").toString().length() == 10){
                endDate = params.get("endDate").toString() + " 23:59:59";
            }
            else if(params.get("endDate").toString().length() == 16){
                endDate = params.get("endDate").toString() + ":59";
            }
            params.put("endDate", endDate);
        }
        params.put("tenantId", tenantId);
        BigInteger userId = BigInteger.valueOf(Long.parseLong(params.get("userId").toString()));
        Employee e = employeeMapper.getEmployee(userId, tenantId);
        String userAreas = e.getUserAreas();
        //查询子区域数量
        List<Map> subregionsCount = new ArrayList<Map>();
        //查询子区域
        List<Map> subregionList = new ArrayList<Map>();
        if("0000".equals(e.getCode())){
            params.put("userAreas", null);
            subregionsCount = reportMapper.subregionsSum(params);
            subregionList = reportMapper.subregions(params);
        }
        else{
            if(userAreas != null || !"".equals(userAreas)){
                params.put("userAreas", userAreas);
                subregionsCount = reportMapper.subregionsSum(params);
                subregionList = reportMapper.subregions(params);
            }
        }
        List<Map> list = new ArrayList();
        if(subregionList.size() > 0){
            for(int j = 0; j < subregionList.size(); j++){
                Map idMap = subregionList.get(j);
                String areaId = idMap.get("id").toString();
                /*
                 * 统计下级子区域
                 * 1.判断是否有子区域
                 * 2.添加子区域id
                 * */
                Boolean flag = false;
                String allAreaId = "";
                String pId = areaId;
                while(!flag){
                    String tmpId = "";
                    //查询是否有子区域
                    Integer count = reportMapper.isSubregion(pId, tenantId);
                    if(count > 0){
                        List<Map> nSubregionList = reportMapper.findSubregions(pId, tenantId);
                        for(int i = 0; i < nSubregionList.size(); i++){
                            //拼接子区域ID
                            allAreaId += nSubregionList.get(i).get("id") + ",";
                            tmpId += nSubregionList.get(i).get("id") + ",";
                        }
                        pId = tmpId.substring(0, tmpId.lastIndexOf(","));
                    }
                    else{
                        flag = true;
                    }
                }
                allAreaId = areaId + "," + allAreaId;
                if(params.get("isUserAreas") != null && "true".equals(params.get("isUserAreas")) && !"0000".equals(e.getCode())){
                    String areaIds[] = allAreaId.substring(0, allAreaId.lastIndexOf(",")).split(",");
                    String ids = "";
                    for(int i = 0; i < areaIds.length; i++){
                        if(userAreas.indexOf(areaIds[i]) != -1){
                            ids += areaIds[i] + ",";
                        }
                    }
                    params.put("allAreaId", ids.substring(0, ids.lastIndexOf(",")));
                }
                else{
                    params.put("allAreaId", allAreaId.substring(0, allAreaId.lastIndexOf(",")));
                }

                //查询子区域报表
                params.put("areaId", areaId);
                params.put("offset", 0);
                List<Map> sList = reportMapper.areaReport(params);
                list.add(sList.get(0));
            }
            for(int i = 0; i < list.size(); i++){
                Map map1 = list.get(i);
                String id = String.valueOf(map1.get("id"));
                Integer count = reportMapper.isSubregion(id, tenantId);
                if(count > 0){
                    map1.put("isSubregion", true);
                }
                else{
                    map1.put("isSubregion", false);
                }
            }

            List<Map> footer = new ArrayList();
            double total_amount = 0.00; //总营业额
            Integer tc = 0;             //总客单
            double ac = 0.00;           //平均客单额
            double trade_amount = 0.00; //会员消费总额
            double store_amount = 0.00; //会员储值总额
            for(int m = 0; m < list.size(); m++){
                Map map2 = list.get(m);
                total_amount += Double.parseDouble(map2.get("totalAmount").toString());
                tc += Integer.parseInt(map2.get("tc").toString());
                trade_amount += Double.parseDouble(map2.get("tradeAmount").toString());
                store_amount += Double.parseDouble(map2.get("storeAmount").toString());
            }
            if(tc == 0){
                ac = 0;
            }
            else{
                ac = total_amount / tc;
            }
            Map fMap = new HashMap();
            fMap.put("totalAmount", total_amount);
            fMap.put("tc", tc);
            fMap.put("ac", ac);
            fMap.put("tradeAmount", trade_amount);
            fMap.put("storeAmount", store_amount);
            footer.add(fMap);
            List nList = new ArrayList();
            Integer count = 0;
            if((rows + offset) < list.size()){
                count = rows;
            }
            else{
                count = list.size();
            }
            for(int j = offset; j < count; j++){
                Map nMap = list.get(j);
                nList.add(nMap);
            }
            map.put("rows", nList);
            map.put("footer", footer);
            map.put("total", subregionsCount.get(0).get("total"));
            apiRest.setIsSuccess(true);
            apiRest.setData(map);
            apiRest.setMessage("查询子区域销售汇总表成功！");
        }
        else{
            Map nMap = new HashMap();
            nMap.put("rows", new ArrayList<Map>());
            nMap.put("footer", new ArrayList<Map>());
            nMap.put("total", 0);
            apiRest.setIsSuccess(true);
            apiRest.setData(nMap);
            apiRest.setMessage("查询区域销售汇总表成功！");
            return apiRest;
        }
        return apiRest;
    }

    /**
     * 营业概况报表
     *
     * @param params
     * @return
     */
    public ApiRest businessOverviewReport(Map params){
        ApiRest apiRest = new ApiRest();
        Map map = new HashMap();
        String branchId = params.get("branchId").toString();
        Integer saleType = Integer.parseInt(params.get("saleType").toString());
        params.put("branchId", branchId);
        params.put("saleType", saleType);
        params.put("startDate", params.get("startDate"));
        params.put("endDate", params.get("endDate"));
        List<Map> list = reportMapper.businessOverviewReport(params);
        map.put("rows", list);
        apiRest.setIsSuccess(true);
        apiRest.setData(map);
        apiRest.setMessage("查询营业概况成功");
        return apiRest;
    }

    /**
     * 赠退明细报表
     *
     * @param params
     * @return
     */
    public ApiRest retirementReport(Map params){
        ApiRest apiRest = new ApiRest();
        Map map = new HashMap();
        if(params.get("page") != null && params.get("rows") != null){
            params.put("offset", ((Integer.parseInt(params.get("page").toString()) - 1) * Integer.parseInt(params.get("rows").toString())));
        }
        if(params.get("startDate") != null && !params.get("startDate").equals("")){
            String startDate = "";
            if(params.get("startDate").toString().length() == 10){
                startDate = params.get("startDate").toString() + " 00:00:00";
            }
            else if(params.get("startDate").toString().length() == 16){
                startDate = params.get("startDate").toString() + ":00";
            }
            params.put("startDate", startDate);
        }
        if(params.get("endDate") != null && !params.get("endDate").equals("")){
            String endDate = "";
            if(params.get("endDate").toString().length() == 10){
                endDate = params.get("endDate").toString() + " 23:59:59";
            }
            else if(params.get("endDate").toString().length() == 16){
                endDate = params.get("endDate").toString() + ":59";
            }
            params.put("endDate", endDate);
        }
        if("-1".equals(params.get("categoryId"))){
            params.put("categoryId", null);
        }
        if(params.get("field") != null && !params.get("field").equals("") && params.get("order") != null && !params.get("order").equals("")){
            params.put("field", CommonUtils.formatOrderField(params.get("field").toString()));
            params.put("order", params.get("order").toString());
        }
        if(params.get("parentId") != null){
            params.remove("branchId");
        }
        List<Map> list = reportMapper.retirementReport(params);
        List<Map> footer = reportMapper.retirementReportSum(params);
        map.put("rows", list);
        map.put("total", footer.get(0).get("total"));
        map.put("footer", footer);
        apiRest.setIsSuccess(true);
        apiRest.setData(map);
        apiRest.setMessage("查询赠退明细报表成功");
        return apiRest;
    }

    /**
     * 库存预警报表
     *
     * @param params
     * @return
     */
    public ApiRest storeWarningReport(Map params){
        ApiRest apiRest = new ApiRest();
        Map map = new HashMap();
        if(params.get("page") != null && params.get("rows") != null){
            params.put("offset", ((Integer.parseInt(params.get("page").toString()) - 1) * Integer.parseInt(params.get("rows").toString())));
        }
        if(params.get("isShowNoChange") == null || "".equals(params.get("isShowNoChange"))){
            params.put("isShowNoChange", 0);
        }
        List<Map> list = reportMapper.storeWarningReport(params);
        Long count = reportMapper.storeWarningReportSum(params);
        map.put("rows", list);
        map.put("total", count);
        apiRest.setData(map);
        apiRest.setIsSuccess(true);
        apiRest.setMessage("查询库存预警报表成功！");
        return apiRest;
    }

    public List<Map> pushData(){
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String today = sdf.format(date);
        String yesterday = reportMapper.getDate(today, -1);
        Map params = new HashMap();
        params.put("startDate", yesterday);
        params.put("endDate", yesterday);
        List<Map> yesterdayList = reportMapper.pushData(params);
        return yesterdayList;
    }

    @Autowired
    private BranchMapper branchMapper;

    /**
     * 经营情况通知接口 saas-wx 调用(2016年8月27日11:30:50 刘艳东)
     *
     * @param beginDate
     * @param endDate
     * @return
     */
    public ApiRest operatingSituationStatistics(Date beginDate, Date endDate, BigInteger userId, String partitionCode){
        ApiRest apiRest = new ApiRest();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Employee employee = employeeMapper.findEmployeeByUser(userId);
        Validate.notNull(employee, "员工不存在！");
        Branch branch = branchMapper.findBranchByTenantIdAndBranchId(employee.getTenantId(), employee.getBranchId());
        Validate.notNull(branch, "机构不存在！");
        BigInteger tenantId = branch.getTenantId();
        BigInteger branchId = branch.getId();
        String branchName = branch.getName();
        List<Map> thisData = reportMapper.wxPushBusiness(tenantId, branchId, simpleDateFormat.format(beginDate), simpleDateFormat.format(endDate));
        if(thisData.size() == 0){
            apiRest.setIsSuccess(false);
            apiRest.setError("未查询到本周营业数据");
            return apiRest;
        }
        Map thisWeekData = thisData.get(0);
        BigDecimal thisWeekRealAmount = thisWeekData.get("realAmount") != null ? BigDecimal.valueOf(Double.valueOf(thisWeekData.get("realAmount").toString())) : BigDecimal.ZERO;
        BigDecimal thisWeekSaleNum = thisWeekData.get("saleNum") != null ? BigDecimal.valueOf(Double.valueOf(thisWeekData.get("saleNum").toString())) : BigDecimal.ZERO;
        BigDecimal thisWeekAveragePrice = thisWeekData.get("averagePrice") != null ? BigDecimal.valueOf(Double.valueOf(thisWeekData.get("averagePrice").toString())) : BigDecimal.ZERO;
        List<Map> lastData = reportMapper.wxPushBusiness(tenantId, branchId, simpleDateFormat.format(DateUtils.addWeeks(beginDate, -1)), simpleDateFormat.format(DateUtils.addWeeks(endDate, -1)));
        if(thisData.size() == 0){
            apiRest.setIsSuccess(false);
            apiRest.setError("为查询到本周营业数据");
            return apiRest;
        }
        Map lastWeekData = lastData.get(0);
        BigDecimal lastWeekRealAmount = lastWeekData.get("realAmount") != null ? BigDecimal.valueOf(Double.valueOf(lastWeekData.get("realAmount").toString())) : BigDecimal.ZERO;
        BigDecimal lastWeekSaleNum = lastWeekData.get("saleNum") != null ? BigDecimal.valueOf(Double.valueOf(lastWeekData.get("saleNum").toString())) : BigDecimal.ZERO;
        BigDecimal lastWeekAveragePrice = lastWeekData.get("averagePrice") != null ? BigDecimal.valueOf(Double.valueOf(lastWeekData.get("averagePrice").toString())) : BigDecimal.ZERO;
        Long newVipCount = reportMapper.countVipIncrease(tenantId, branchId, simpleDateFormat.format(beginDate), simpleDateFormat.format(endDate));
        // 消费总金额
        BigDecimal totalConsumptionAmount = thisWeekRealAmount;
        // 消费笔数
        BigDecimal consumptionCount = thisWeekSaleNum;
        // 营业额同比上周增长
        BigDecimal yearOnYearGrowth = thisWeekRealAmount.subtract(lastWeekRealAmount);
        // 客流量同比上周增长数量
        BigDecimal passengerFlowGrowth = thisWeekSaleNum.subtract(lastWeekSaleNum);
        // 客单价同比上周增长数量
        BigDecimal averagePriceGrowth = thisWeekAveragePrice.subtract(lastWeekAveragePrice);
        Map<String, String> data = new HashMap<String, String>();
        data.put("tenantId", tenantId.toString());
        data.put("branchId", branchId.toString());
        data.put("branchName", branchName);
        data.put("newVipCount", newVipCount.toString());
        data.put("totalConsumptionAmount", totalConsumptionAmount.toString());
        data.put("consumptionCount", consumptionCount.toString());
        data.put("yearOnYearGrowth", yearOnYearGrowth.toString());
        data.put("passengerFlowGrowth", passengerFlowGrowth.toString());
        data.put("averagePriceGrowth", averagePriceGrowth.toString());
        apiRest.setIsSuccess(true);
        apiRest.setMessage("查询成功！");
        apiRest.setData(data);
        return apiRest;
    }

    public ApiRest businessOverview(Map params){
        ApiRest apiRest = new ApiRest();
        /*boolean branchFlag = false;
        ApiRest restT = SaaSApi.findTenantById(new BigInteger(params.get("tenantId").toString()));
        if(Constants.REST_RESULT_SUCCESS.equals(restT.getResult())){
            if(restT.getData() != null){
                Tenant tenant = ApiBaseServiceUtils.MapToObject((Map)((Map)restT.getData()).get("tenant"), Tenant.class);
                branchFlag = tenant.isBranchManagementVip();
            }
        }
        else{
            return restT;
        }
        if(branchFlag){
            params.put("branchFlag", "1");
        }*/
        Map resultMap = new HashMap();
        if(params.get("flag").equals("1")){//营业概况
            if(params.get("startDate") == null || params.get("startDate").equals("")){
                apiRest.setIsSuccess(false);
                apiRest.setError("参数startDate不能为空！");
                return apiRest;
            }
            if(params.get("endDate") == null || params.get("endDate").equals("")){
                apiRest.setIsSuccess(false);
                apiRest.setError("参数endDate不能为空！");
                return apiRest;
            }
            if(params.get("startDate") != null && !params.get("startDate").equals("")){
                String startDate = "";
                if(params.get("startDate").toString().length() == 10){
                    startDate = params.get("startDate").toString() + " 00:00:00";
                }
                else if(params.get("startDate").toString().length() == 16){
                    startDate = params.get("startDate").toString() + ":00";
                }
                params.put("startDate", startDate);
            }
            if(params.get("endDate") != null && !params.get("endDate").equals("")){
                String endDate = "";
                if(params.get("endDate").toString().length() == 10){
                    endDate = params.get("endDate").toString() + " 23:59:59";
                }
                else if(params.get("endDate").toString().length() == 16){
                    endDate = params.get("endDate").toString() + ":59";
                }
                params.put("endDate", endDate);
            }
            //查询会员分组
            String preBranchIds = params.get("branchIds").toString();
            if(params.get("branchIds") != null && params.get("tenantId") != null){
                Map<String, Object> param = new HashMap<>();
                param.put("id", params.get("branchIds"));
                param.put("tenantId", params.get("tenantId"));
                Branch branch = branchMapper.find(param);
                if(branch != null && branch.getGroupCode() != null && !"".equals(branch.getGroupCode())){
                    param.remove("id");
                    param.put("groupCode", branch.getGroupCode());
                    List<Branch> branches = branchMapper.findBranchByTenantId(param);
                    if(branches != null && branches.size() > 0){
                        String bIds = "";
                        for(Branch b : branches){
                            bIds += b.getId() + ",";
                        }
                        bIds = bIds.substring(0, bIds.length() - 1);
                        //params.remove("branchId");
                        params.put("branchIds", bIds);
                    }
                }
            }
            Map vipMap = reportMapper.businessVip(params);  //会员数据->会员总数、储值余额
            params.put("branchIds", preBranchIds);
            Map posMap = reportMapper.businessPos(params);    //店铺销售->客单价、客流人数、实收金额 ,店铺优惠->销售金额、优惠金额、赠送金额 , 异常数据->优惠单数
            Map orderMap = reportMapper.businessOrder(params);//线上订单、总计、微商城订单,线上订单->美团订单、美团订单->订单原价、商家实收 , 线上订单->饿了么、饿了么->订单原价、商家实收
            Map payMap = reportMapper.businessPayment(params); //店铺收款->总计、现金支付、移动支付、其他支付
            Map vstoreMap = reportMapper.businessVipStore(params); //会员数据->充值实收
            Map vtradeMap = reportMapper.businessVipTrade(params); //会员数据->储值消费
            Map storeSaleMap = reportMapper.businessStoreSale(params); // 库存数据->实收金额
            Map storeOccurMap = reportMapper.businessStoreOccur(params);//库存数据->成本金额
            Map storeMap = reportMapper.businessStore(params); //  库存数据->库存金额 ，异常数据->库存过剩、库存不足
            Map retireMap = reportMapper.businessRetire(params);//异常数据->退货明细、赠送明细
            getMapValue(resultMap, vipMap);
            getMapValue(resultMap, posMap);
            getMapValue(resultMap, orderMap);
            getMapValue(resultMap, payMap);
            getMapValue(resultMap, vstoreMap);
            getMapValue(resultMap, vtradeMap);
            getMapValue(resultMap, storeSaleMap);
            getMapValue(resultMap, storeOccurMap);
            getMapValue(resultMap, storeMap);
            getMapValue(resultMap, retireMap);
        }
        else if(params.get("flag").equals("2")){//首页
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            String today = sdf.format(new Date());
            params.put("startDate", today);
            params.put("endDate", today);
            if(params.get("startDate") != null && !params.get("startDate").equals("")){
                String startDate = "";
                if(params.get("startDate").toString().length() == 10){
                    startDate = params.get("startDate").toString() + " 00:00:00";
                }
                else if(params.get("startDate").toString().length() == 16){
                    startDate = params.get("startDate").toString() + ":00";
                }
                params.put("startDate", startDate);
            }
            if(params.get("endDate") != null && !params.get("endDate").equals("")){
                String endDate = "";
                if(params.get("endDate").toString().length() == 10){
                    endDate = params.get("endDate").toString() + " 23:59:59";
                }
                else if(params.get("endDate").toString().length() == 16){
                    endDate = params.get("endDate").toString() + ":59";
                }
                params.put("endDate", endDate);
            }
            Map toMap = new HashMap();
            Map vipMap = reportMapper.businessVip(params);
            Map posMap = reportMapper.businessPos(params);
            Map orderMap = reportMapper.businessOrder(params);
            Map payMap = reportMapper.businessPayment(params);
            Map vstoreMap = reportMapper.businessVipStore(params);
            Map vtradeMap = reportMapper.businessVipTrade(params);
            Map storeSaleMap = reportMapper.businessStoreSale(params);
            Map storeOccurMap = reportMapper.businessStoreOccur(params);
            Map storeMap = reportMapper.businessStore(params);
            Map retireMap = reportMapper.businessRetire(params);
            getMapValue(toMap, vipMap);
            getMapValue(toMap, posMap);
            getMapValue(toMap, orderMap);
            getMapValue(toMap, payMap);
            getMapValue(toMap, vstoreMap);
            getMapValue(toMap, vtradeMap);
            getMapValue(toMap, storeSaleMap);
            getMapValue(toMap, storeOccurMap);
            getMapValue(toMap, storeMap);
            getMapValue(toMap, retireMap);
            resultMap.put("today", toMap);
            String yesterday = reportMapper.getDate(today, -1);
            params.put("startDate", yesterday);
            params.put("endDate", yesterday);
            if(params.get("startDate") != null && !params.get("startDate").equals("")){
                String startDate = "";
                if(params.get("startDate").toString().length() == 10){
                    startDate = params.get("startDate").toString() + " 00:00:00";
                }
                else if(params.get("startDate").toString().length() == 16){
                    startDate = params.get("startDate").toString() + ":00";
                }
                params.put("startDate", startDate);
            }
            if(params.get("endDate") != null && !params.get("endDate").equals("")){
                String endDate = "";
                if(params.get("endDate").toString().length() == 10){
                    endDate = params.get("endDate").toString() + " 23:59:59";
                }
                else if(params.get("endDate").toString().length() == 16){
                    endDate = params.get("endDate").toString() + ":59";
                }
                params.put("endDate", endDate);
            }
            Map yesMap = new HashMap();
            Map vipMap1 = reportMapper.businessVip(params);
            Map posMap1 = reportMapper.businessPos(params);
            Map orderMap1 = reportMapper.businessOrder(params);
            Map payMap1 = reportMapper.businessPayment(params);
            Map vstoreMap1 = reportMapper.businessVipStore(params);
            Map vtradeMap1 = reportMapper.businessVipTrade(params);
            Map storeSaleMap1 = reportMapper.businessStoreSale(params);
            Map storeOccurMap1 = reportMapper.businessStoreOccur(params);
            Map storeMap1 = reportMapper.businessStore(params);
            Map retireMap1 = reportMapper.businessRetire(params);
            getMapValue(yesMap, vipMap1);
            getMapValue(yesMap, posMap1);
            getMapValue(yesMap, orderMap1);
            getMapValue(yesMap, payMap1);
            getMapValue(yesMap, vstoreMap1);
            getMapValue(yesMap, vtradeMap1);
            getMapValue(yesMap, storeSaleMap1);
            getMapValue(yesMap, storeOccurMap1);
            getMapValue(yesMap, storeMap1);
            getMapValue(yesMap, retireMap1);
            resultMap.put("yesterday", yesMap);
        }
        apiRest.setIsSuccess(true);
        apiRest.setData(resultMap);
        apiRest.setMessage("查询营业状况成功！");
        return apiRest;
    }

    public void getMapValue(Map resultMap, Map sorMap){
        for(Object entry : sorMap.entrySet()){
            if(!(((Map.Entry)entry).getValue() instanceof Date)){
                resultMap.put(((Map.Entry)entry).getKey(), ((Map.Entry)entry).getValue().toString());
            }
        }
    }

    /**
     * 套餐销售汇总
     */
    public ApiRest packageReport(Map params){
        ApiRest apiRest = new ApiRest();
        BigInteger tenantId = BigInteger.valueOf(Long.parseLong(params.get("tenantId").toString()));
        BigInteger branchId = BigInteger.valueOf(Long.parseLong(params.get("branchId").toString()));
        Integer rows = Integer.parseInt(params.get("rows").toString());
        Integer offset = (Integer.parseInt(params.get("page").toString()) - 1) * rows;
        params.put("tenantId", tenantId);
        params.put("branchId", branchId);
        params.put("rows", rows);
        params.put("offset", offset);
        if(params.get("startDate") != null && !params.get("startDate").equals("")){
            String startDate = "";
            if(params.get("startDate").toString().length() == 10){
                startDate = params.get("startDate").toString() + " 00:00:00";
            }
            else if(params.get("startDate").toString().length() == 16){
                startDate = params.get("startDate").toString() + ":00";
            }
            params.put("startDate", startDate);
        }
        if(params.get("endDate") != null && !params.get("endDate").equals("")){
            String endDate = "";
            if(params.get("endDate").toString().length() == 10){
                endDate = params.get("endDate").toString() + " 23:59:59";
            }
            else if(params.get("endDate").toString().length() == 16){
                endDate = params.get("endDate").toString() + ":59";
            }
            params.put("endDate", endDate);
        }
        if(params.get("field") != null && !params.get("field").equals("") && params.get("order") != null && !params.get("order").equals("")){
            params.put("field", CommonUtils.formatOrderField(params.get("field").toString()));
            params.put("order", params.get("order").toString());
        }
        if(params.get("parentId") != null){
            params.remove("branchId");
        }
        List<Map> list = reportMapper.packageReport(params);
        List<Map> footer = reportMapper.packageReportSum(params);
        Map map = new HashMap();
        map.put("rows", list);
        map.put("total", footer.get(0).get("total"));
        map.put("footer", footer);
        apiRest.setIsSuccess(true);
        apiRest.setData(map);
        apiRest.setMessage("查询套餐销售报表成功");
        return apiRest;
    }

    /**
     * 测试自主打印
     * */
    public List testAutoPrint(Map params){
        List list = new ArrayList();
        Map map = new HashMap();
        List<Map> list1 = reportMapper.packageReport(params);
        Map map1 = new HashMap();
        map1.put("goodsCode","套餐编码");
        map1.put("goodsName", "套餐名称");
        map1.put("quantity", "销售数量");
        map1.put("totalAmount","实收金额");
        Map map2 = new HashMap();
        map2.put("套餐名称", "");
        map2.put("日期", "2018-07-01 00:00:00 至 2018-07-31 23:59:59");
        map2.put("机构", "惠管家微餐厅测试门店");
        map2.put("订单来源", "线下");
        map.put("rows", list1);
        map.put("columns", map1);
        map.put("condition", map2);
        list.add(map);
        return list;
    }

    /**
     * 会员台账-积分纪录
     */
    public ApiRest vipScoreRecord(Map params){
        ApiRest apiRest = new ApiRest();
        Map map = new HashMap();
        BigInteger tenantId = BigInteger.valueOf(Long.parseLong(params.get("tenantId").toString()));
        String atBranchId = null;
        if(params.get("atBranchId") != null && !"".equals(params.get("atBranchId"))){
            atBranchId = params.get("atBranchId").toString();
        }
        params.put("tenantId", tenantId);
        //查询会员分组
        if(params.get("branchId") != null && params.get("tenantId") != null){
            Map<String, Object> param = new HashMap<>();
            param.put("id", params.get("branchId"));
            param.put("tenantId", params.get("tenantId"));
            Branch branch = branchMapper.find(param);
            if(branch != null && branch.getGroupCode() != null && !"".equals(branch.getGroupCode())){
                param.remove("id");
                param.put("groupCode", branch.getGroupCode());
                List<Branch> branches = branchMapper.findBranchByTenantId(param);
                if(branches != null && branches.size() > 0){
                    String bIds = "";
                    for(Branch b : branches){
                        bIds += b.getId() + ",";
                    }
                    bIds = bIds.substring(0, bIds.length() - 1);
                    params.remove("branchId");
                    params.put("branchIds", bIds);
                }
            }
        }
        params.put("isCoverDeleted", true);
            /*List<VipVo> vipList = vipMapper.findVoByCondition(params);
            String vipIds = "";
            for(int i=0; i<vipList.size(); i++){
                VipVo vipVo = vipList.get(i);
                vipIds += vipVo.getId() + ",";
            }
            params.put("vipList", vipIds.substring(0, vipIds.lastIndexOf(",")));*/
        if(params.get("page") != null && params.get("rows") != null){
            params.put("offset", ((Integer.parseInt(params.get("page").toString()) - 1) * Integer.parseInt(params.get("rows").toString())));
        }
        params.put("branchId", atBranchId);
        if(params.get("field") != null && !params.get("field").equals("") && params.get("order") != null && !params.get("order").equals("")){
            params.put("field", CommonUtils.formatOrderField(params.get("field").toString()));
            params.put("order", params.get("order").toString());
        }
        if(params.get("parentId") != null){
            params.remove("branchId");
            params.remove("branchIds");
        }
        List<Map> list = reportMapper.vipScoreReport(params);
        Long count = reportMapper.vipScoreReportSum(params);
        List<Map> footer = reportMapper.vipScoreReportFoot(params);
        map.put("rows", list);
        map.put("total", count);
        map.put("footer", footer);
        apiRest.setIsSuccess(true);
        apiRest.setMessage("查询积分记录报表成功");
        apiRest.setData(map);
        return apiRest;
    }

    /**
     * 会员台帐-储值记录
     */
    public ApiRest vipStoreRecord(Map params){
        ApiRest apiRest = new ApiRest();
        Map map = new HashMap();
        BigInteger tenantId = BigInteger.valueOf(Long.parseLong(params.get("tenantId").toString()));
        String atBranchId = null;
        if(params.get("atBranchId") != null && !"".equals(params.get("atBranchId"))){
            atBranchId = params.get("atBranchId").toString();
        }
        params.put("tenantId", tenantId);
        //查询会员分组
        if(params.get("branchId") != null && params.get("tenantId") != null){
            Map<String, Object> param = new HashMap<>();
            param.put("id", params.get("branchId"));
            param.put("tenantId", params.get("tenantId"));
            Branch branch = branchMapper.find(param);
            if(branch != null && branch.getGroupCode() != null && !"".equals(branch.getGroupCode())){
                param.remove("id");
                param.put("groupCode", branch.getGroupCode());
                List<Branch> branches = branchMapper.findBranchByTenantId(param);
                if(branches != null && branches.size() > 0){
                    String bIds = "";
                    for(Branch b : branches){
                        bIds += b.getId() + ",";
                    }
                    bIds = bIds.substring(0, bIds.length() - 1);
                    params.remove("branchId");
                    params.put("branchIds", bIds);
                }
            }
        }
        params.put("isCoverDeleted", true);
            /*List<VipVo> vipList = vipMapper.findVoByCondition(params);
            if(vipList != null && vipList.size()>0){
                String vipIds = "";
                for(int i=0; i<vipList.size(); i++){
                    VipVo vipVo = vipList.get(i);
                    vipIds += vipVo.getId() + ",";
                }
                params.put("vipList", vipIds.substring(0, vipIds.lastIndexOf(",")));
            }*/

        if(params.get("page") != null && params.get("rows") != null){
            params.put("offset", ((Integer.parseInt(params.get("page").toString()) - 1) * Integer.parseInt(params.get("rows").toString())));
        }
        params.put("branchId", atBranchId);
        params.put("paymentId",params.get("payType"));//使用payType接收传递的paymentId,页面中有paymentId，id重复所以没做改变。
        params.remove("payType");
        if(null!=params.get("paymentId")) {
            Map paymentResult = paymentMapper.getPaymentByIdAndBranchId(new BigInteger(params.get("branchId").toString()), new BigInteger(params.get("paymentId").toString()), new BigInteger(params.get("tenantId").toString()));
            if (null != paymentResult) {
                String paymentCode = paymentResult.get("paymentCode").toString();
                switch (paymentCode) {
                    case "CSH":
                        params.put("payType", 1);
                        break;
                    case "ZFB":
                        params.put("payType", 3);
                        break;
                    case "WX":
                        params.put("payType", 4);
                        break;
                    case "HZF":
                        params.put("payType", 5);
                        break;
                    case "HYQB":
                        params.put("payType", 7);
                        break;
                    default:
                        break;
                }
            }
        }
        if(params.get("field") != null && !params.get("field").equals("") && params.get("order") != null && !params.get("order").equals("")){
            params.put("field", CommonUtils.formatOrderField(params.get("field").toString()));
            params.put("order", params.get("order").toString());
        }
        if(params.get("parentId") != null){
            params.remove("branchId");
        }
        List<Map> list = reportMapper.vipStoreReport(params);
        Long count = reportMapper.vipStoreReportSum(params);
        List<Map> footer = reportMapper.vipStoreReportFoot(params);
        map.put("rows", list);
        map.put("total", count);
        map.put("footer", footer);
        apiRest.setIsSuccess(true);
        apiRest.setMessage("查询储值记录报表成功");
        apiRest.setData(map);
        return apiRest;
    }

    /**
     * 会员台账-消费记录
     */
    public ApiRest vipTradeRecord(Map params){
        ApiRest apiRest = new ApiRest();
        BigInteger tenantId = BigInteger.valueOf(Long.parseLong(params.get("tenantId").toString()));
        String atBranchId = null;
        if(params.get("atBranchId") != null && !"".equals(params.get("atBranchId"))){
            atBranchId = params.get("atBranchId").toString();
        }
        //查询会员分组
        if(params.get("branchId") != null && params.get("tenantId") != null){
            Map<String, Object> param = new HashMap<>();
            param.put("id", params.get("branchId"));
            param.put("tenantId", params.get("tenantId"));
            Branch branch = branchMapper.find(param);
            if(branch != null && branch.getGroupCode() != null && !"".equals(branch.getGroupCode())){
                param.remove("id");
                param.put("groupCode", branch.getGroupCode());
                List<Branch> branches = branchMapper.findBranchByTenantId(param);
                if(branches != null && branches.size() > 0){
                    String bIds = "";
                    for(Branch b : branches){
                        bIds += b.getId() + ",";
                    }
                    bIds = bIds.substring(0, bIds.length() - 1);
                    params.remove("branchId");
                    params.put("branchIds", bIds);
                }
            }
        }
            /*List<VipVo> vipList = vipMapper.findVoByCondition(params);
            String vipIds = "";
            for(int i=0; i<vipList.size(); i++){
                VipVo vipVo = vipList.get(i);
                vipIds += vipVo.getId() + ",";
            }
            params.put("vipList", vipIds.substring(0, vipIds.lastIndexOf(",")));*/
        if(params.get("rows") != null && params.get("page") != null){
            params.put("offset", ((Integer.parseInt(params.get("page").toString()) - 1) * Integer.parseInt(params.get("rows").toString())));
        }
        params.put("tenantId", tenantId);
        params.put("branchId", atBranchId);
        if(params.get("field") != null && !params.get("field").equals("") && params.get("order") != null && !params.get("order").equals("")){
            params.put("field", CommonUtils.formatOrderField(params.get("field").toString()));
            params.put("order", params.get("order").toString());
        }
        if(params.get("parentId") != null){
            params.remove("atBranchId");
        }
        List list = reportMapper.vipTradeReport(params);
        Long count = reportMapper.vipTradeReportSum(params);
        List<Map> footer = reportMapper.vipTradeReportFoot(params);
        Map map = new HashMap();
        map.put("rows", list);
        map.put("total", count);
        map.put("footer", footer);
        apiRest.setData(map);
        apiRest.setMessage("查询会员消费台账成功");
        apiRest.setIsSuccess(true);
        return apiRest;
    }

    /**
     * 单据详细信息
     */
    public ApiRest orderDetailInfo(Map params){
        ApiRest apiRest = new ApiRest();
        Map info = new HashMap();
        BigInteger tenantId = BigInteger.valueOf(Long.parseLong(params.get("tenantId").toString()));
        BigInteger branchId = BigInteger.valueOf(Long.parseLong(params.get("branchId").toString()));
        params.put("tenantId", tenantId);
        params.put("branchId", branchId);
        List<Map> tradeInfo = reportMapper.infoInTradeHistory(params);
        if(tradeInfo == null || tradeInfo.size() <= 0){
            apiRest.setIsSuccess(false);
            apiRest.setError("未查询到单据信息");
            return apiRest;
        }
        Map tradeMap = tradeInfo.get(0);
        info.put("tradeNo", tradeMap.get("tradeNo"));
        info.put("tradeDate", tradeMap.get("tradeDate"));
        info.put("vipName", tradeMap.get("vipName"));
        info.put("phone", tradeMap.get("phone"));
        List<Map> saleInfo = reportMapper.infoInSale(params);
        if(saleInfo == null || saleInfo.size() <= 0){
            apiRest.setIsSuccess(false);
            apiRest.setError("未查询到流水信息");
            return apiRest;
        }
        Map saleMap = saleInfo.get(0);
        info.put("totalAmount", saleMap.get("totalAmount"));
        info.put("discountAmount", saleMap.get("discountAmount"));
        info.put("giveAmount", saleMap.get("giveAmount"));
        info.put("receivedAmount", saleMap.get("receivedAmount"));
        info.put("isRefund", saleMap.get("isRefund"));
        List<Map> paymentInfo = reportMapper.infoInPayment(params);
        if(paymentInfo == null || paymentInfo.size() <= 0){
            apiRest.setIsSuccess(false);
            apiRest.setError("未查询到支付信息");
            return apiRest;
        }
            /*Map paymentMap = paymentInfo.get(0);
            info.put("goodsName", paymentMap.get("goodsName"));
            info.put("quantity", paymentMap.get("quantity"));
            info.put("goodsAmount", paymentMap.get("goodsAmount"));*/
        info.put("paymentInfo", paymentInfo);
        List<Map> goodsInfo = reportMapper.infoInSaleDetail(params);
        info.put("details", goodsInfo);
        apiRest.setIsSuccess(true);
        apiRest.setData(info);
        apiRest.setMessage("查询单据详细信息成功");
        return apiRest;
    }

    /**
     * 规格汇总报表
     */
    public ApiRest standardReport(Map params){
        ApiRest apiRest = new ApiRest();
        Map map = new HashMap();
        BigInteger tenantId = BigInteger.valueOf(Long.parseLong(params.get("tenantId").toString()));
        BigInteger branchId = BigInteger.valueOf(Long.parseLong(params.get("branchId").toString()));
        Integer rows = Integer.parseInt(params.get("rows").toString());
        Integer offset = (Integer.parseInt(params.get("page").toString()) - 1) * rows;
        params.put("tenantId", tenantId);
        params.put("branchId", branchId);
        params.put("rows", rows);
        params.put("offset", offset);
        List<Map> list = new ArrayList<>();
        Long count = 0L;
        List<Map> footer = new ArrayList<>();
        //查询条件区域内销售商品ID
        if(params.get("parentId") != null){
            params.remove("branchId");
        }
        List<Map> ids = reportMapper.standardSaleGoodsIds(params);
        if(ids != null && ids.size() > 0){
            if(params.get("field") != null && !params.get("field").equals("") && params.get("order") != null && !params.get("order").equals("")){
                params.put("field", CommonUtils.formatOrderField(params.get("field").toString()));
                params.put("order", params.get("order").toString());
            }
            String goodsIds = "";
            for(int i=0; i<ids.size(); i++){
                goodsIds += ids.get(i).get("goodsId").toString() + ",";
            }
            params.put("goodsIds", "".equals(goodsIds) == true ? "" : goodsIds.substring(0, goodsIds.lastIndexOf(",")));
            List<Map> sIds = reportMapper.standardIds(params);
            String standardIds = "";
            for(int j=0; j<sIds.size(); j++){
                standardIds += sIds.get(j).get("id").toString() + ",";
            }
            if(StringUtils.isNotEmpty(standardIds)){
                params.put("goodsIds", "".equals(standardIds) == true ? "" : standardIds.substring(0, standardIds.lastIndexOf(",")));
                list = reportMapper.standardReport(params);
                //count = reportMapper.standardReportSum(params);
                footer = reportMapper.standardReportFoot(params);
            }else{
                Map map1 = new HashMap();
                map1.put("grossProfit", null);
                map1.put("purchasingPrice", null);
                map1.put("quantity", null);
                map1.put("totalAmount", null);
                footer.add(map1);
            }
        }else{
            Map map1 = new HashMap();
            map1.put("grossProfit", null);
            map1.put("purchasingPrice", null);
            map1.put("quantity", null);
            map1.put("totalAmount", null);
            footer.add(map1);
        }
        map.put("rows", list);
        map.put("total", footer.get(0).get("total") == null ? 0 : footer.get(0).get("total"));
        map.put("footer", footer);
        apiRest.setData(map);
        apiRest.setIsSuccess(true);
        apiRest.setMessage("查询规格汇总报表成功");
        return apiRest;
    }

    /**
     * 查询进销存报表
     */
    public ApiRest goodsInvoicingReport(Map params){
        ApiRest apiRest = new ApiRest();
        Map map = new HashMap();
        BigInteger tenantId = BigInteger.valueOf(Long.parseLong(params.get("tenantId").toString()));
        String branchId = params.get("branchId").toString();
        Integer rows = Integer.parseInt(params.get("rows").toString());
        Integer offset = (Integer.parseInt(params.get("page").toString()) - 1) * rows;
        params.put("tenantId", tenantId);
        params.put("branchId", branchId);
        params.put("rows", rows);
        params.put("offset", offset);
        int onlySelf = 0;
        if(params.get("onlySelf") != null && !"".equals(params.get("onlySelf"))){
            onlySelf = Integer.parseInt(params.get("onlySelf").toString());
        }
        List list = reportMapper.goodsInvoicingReport(params);
        Long count = reportMapper.goodsInvoicingReportSum(params);
        List beginList;
        List endList;
        Map goodMap;
        List<Map> footer = reportMapper.goodsInvoicingReportFoot(params);
        Map footerMap = footer.get(0);
        BigDecimal beginQuantity = BigDecimal.ZERO;
        BigDecimal beginAmount = BigDecimal.ZERO;
        BigDecimal endQuantity = BigDecimal.ZERO;
        BigDecimal endAmount = BigDecimal.ZERO;
        if(list != null && list.size() > 0){
            if(params.get("goodsId") == null || "".equals(params.get("goodsId"))){
                for(int i = 0; i < list.size(); i++){
                    goodMap = (Map)list.get(i);
                    BigInteger goodsId = BigInteger.valueOf(Long.parseLong(goodMap.get("goodsId").toString()));
                    params.put("goodsId", goodsId);
                    beginList = reportMapper.beginAmount(params);
                    endList = reportMapper.endAmount(params);
                    Map beginMap = (Map)beginList.get(0);
                    Map endMap = (Map)endList.get(0);
                    goodMap.put("beginQuantity", beginMap.get("beginQuantity"));
                    goodMap.put("beginAmount", beginMap.get("beginAmount"));
                    goodMap.put("endQuantity", endMap.get("endQuantity"));
                    goodMap.put("endAmount", endMap.get("endAmount"));
                }
                params.put("offset", null);
                params.put("rows", null);
                params.put("goodsId", null);
                List list1 = reportMapper.goodsInvoicingReport(params);
                for(int i = 0; i < list1.size(); i++){
                    goodMap = (Map)list1.get(i);
                    BigInteger goodsId = BigInteger.valueOf(Long.parseLong(goodMap.get("goodsId").toString()));
                    params.put("goodsId", goodsId);
                    beginList = reportMapper.beginAmount(params);
                    endList = reportMapper.endAmount(params);
                    Map beginMap = (Map)beginList.get(0);
                    Map endMap = (Map)endList.get(0);
                    beginQuantity = beginQuantity.add(BigDecimal.valueOf(Float.parseFloat(beginMap.get("beginQuantity").toString())));
                    beginAmount = beginAmount.add(BigDecimal.valueOf(Float.parseFloat(beginMap.get("beginAmount").toString())));
                    endQuantity = endQuantity.add(BigDecimal.valueOf(Float.parseFloat(endMap.get("endQuantity").toString())));
                    endAmount = endAmount.add(BigDecimal.valueOf(Float.parseFloat(endMap.get("endAmount").toString())));
                }
                footerMap.put("beginQuantity", beginQuantity);
                footerMap.put("beginAmount", beginAmount);
                footerMap.put("endQuantity", endQuantity);
                footerMap.put("endAmount", endAmount);
            }
            else{
                beginList = reportMapper.beginAmount(params);
                endList = reportMapper.endAmount(params);
                goodMap = (Map)list.get(0);
                Map beginMap = (Map)beginList.get(0);
                Map endMap = (Map)endList.get(0);
                goodMap.put("beginQuantity", beginMap.get("beginQuantity"));
                goodMap.put("beginAmount", beginMap.get("beginAmount"));
                goodMap.put("endQuantity", endMap.get("endQuantity"));
                goodMap.put("endAmount", endMap.get("endAmount"));
                footerMap.put("beginQuantity", beginMap.get("beginQuantity"));
                footerMap.put("beginAmount", beginMap.get("beginAmount"));
                footerMap.put("endQuantity", endMap.get("endQuantity"));
                footerMap.put("endAmount", endMap.get("endAmount"));
            }
        }
        else{
            footerMap.put("beginQuantity", 0);
            footerMap.put("beginAmount", 0);
            footerMap.put("endQuantity", 0);
            footerMap.put("endAmount", 0);
        }
        map.put("rows", list);
        map.put("total", count);
        map.put("footer", footer);
        apiRest.setData(map);
        apiRest.setIsSuccess(true);
        apiRest.setMessage("查询进销存报表成功");
        return apiRest;
    }

    /**
     * 单品销售趋势分析
     */
    public ApiRest saleTrendAnalysis(Map params){
        ApiRest apiRest = new ApiRest();
        List dateList = new ArrayList();
        List realQuantity = new ArrayList();
        List realAmount = new ArrayList();
        List grossProfit = new ArrayList();
        List<Map> list = reportMapper.saleTrendAnalysis(params);
        params.put("id", params.get("goodsId"));
        Goods goods = goodsMapper.findGoodsByIdAndTenantId(params);
        String goodsInfo = goods.getGoodsCode() + "-" + goods.getGoodsName();
        if(list != null && list.size() > 0){
            for(int i = 0; i < list.size(); i++){
                Map map = list.get(i);
                dateList.add(map.get("createAt"));
                realQuantity.add(map.get("realQuantity"));
                realAmount.add(map.get("realAmount"));
                grossProfit.add(map.get("grossProfit"));
            }
        }
        Map resultMap = new HashMap();
        resultMap.put("goodsInfo", goodsInfo);
        resultMap.put("dateList", dateList);
        resultMap.put("realQuantity", realQuantity);
        resultMap.put("realAmount", realAmount);
        resultMap.put("grossProfit", grossProfit);
        apiRest.setIsSuccess(true);
        apiRest.setData(resultMap);
        apiRest.setMessage("查询单品销售趋势成功");
        return apiRest;
    }

    /**
     * 单品销售趋势报表
     */
    public ApiRest saleAnalysisReport(Map params){
        ApiRest apiRest = new ApiRest();
        List<Map> list = reportMapper.saleTrendAnalysis(params);
        params.put("id", params.get("goodsId"));
        Map resultMap = new HashMap();
        resultMap.put("rows", list);
        resultMap.put("total", list.size());
        apiRest.setIsSuccess(true);
        apiRest.setData(resultMap);
        return apiRest;
    }

    /**
     * 会员对账报表
     * */
    public ApiRest vipStatementReport(Map params){
        ApiRest apiRest = new ApiRest();
        Map map = new HashMap();
        Integer rows = Integer.parseInt(params.get("rows").toString());
        Integer offset = (Integer.parseInt(params.get("page").toString()) - 1) * rows;
        params.put("rows", rows);
        params.put("offset", offset);
        if(params.get("startDate") != null && !params.get("startDate").equals("")){
            String startDate = "";
            if(params.get("startDate").toString().length() == 10){
                startDate = params.get("startDate").toString() + " 00:00:00";
            }
            else if(params.get("startDate").toString().length() == 16){
                startDate = params.get("startDate").toString() + ":00";
            }
            params.put("startDate", startDate);
        }
        if(params.get("endDate") != null && !params.get("endDate").equals("")){
            String endDate = "";
            if(params.get("endDate").toString().length() == 10){
                endDate = params.get("endDate").toString() + " 23:59:59";
            }
            else if(params.get("endDate").toString().length() == 16){
                endDate = params.get("endDate").toString() + ":59";
            }
            params.put("endDate", endDate);
        }
        List<Map> list = reportMapper.vipStatementReport(params);
        List<Map> footer = reportMapper.vipStatementReportSum(params);
        Map footerMap = footer.get(0);
        List beginList;
        List endList;
        Map vipMap;
        BigDecimal beginAmount = BigDecimal.ZERO;
        BigDecimal endAmount = BigDecimal.ZERO;
        if(list != null && list.size() > 0){
            if(params.get("vipId") == null || "".equals(params.get("vipId"))){
                for(int i = 0; i < list.size(); i++){
                    vipMap = list.get(i);
                    BigInteger vipId = BigInteger.valueOf(Long.parseLong(vipMap.get("vipId").toString()));
                    params.put("vipId", vipId);
                    beginList = reportMapper.vipBeginAmount(params);
                    endList = reportMapper.vipEndAmount(params);
                    Map beginMap = (Map)beginList.get(0);
                    Map endMap = (Map)endList.get(0);
                    vipMap.put("beginAmount", beginMap.get("beginAmount"));
                    vipMap.put("endAmount", endMap.get("endAmount"));
                }
                params.put("offset", null);
                params.put("rows", null);
                params.put("vipId", null);
                List<Map> list1 = reportMapper.vipStatementReport(params);
                for(int i = 0; i < list1.size(); i++){
                    vipMap = list1.get(i);
                    BigInteger vipId = BigInteger.valueOf(Long.parseLong(vipMap.get("vipId").toString()));
                    params.put("vipId", vipId);
                    beginList = reportMapper.vipBeginAmount(params);
                    endList = reportMapper.vipEndAmount(params);
                    Map beginMap = (Map)beginList.get(0);
                    Map endMap = (Map)endList.get(0);
                    beginAmount = beginAmount.add(BigDecimal.valueOf(Float.parseFloat(beginMap.get("beginAmount").toString())));
                    endAmount = endAmount.add(BigDecimal.valueOf(Float.parseFloat(endMap.get("endAmount").toString())));
                }
                footerMap.put("beginAmount", beginAmount);
                footerMap.put("endAmount", endAmount);
            }
            else{
                beginList = reportMapper.vipBeginAmount(params);
                endList = reportMapper.vipEndAmount(params);
                vipMap = list.get(0);
                Map beginMap = (Map)beginList.get(0);
                Map endMap = (Map)endList.get(0);
                vipMap.put("beginAmount", beginMap.get("beginAmount"));
                vipMap.put("endAmount", endMap.get("endAmount"));
                footerMap.put("beginAmount", beginMap.get("beginAmount"));
                footerMap.put("endAmount", endMap.get("endAmount"));
            }
        } else {
            footerMap.put("beginAmount", 0);
            footerMap.put("endAmount", 0);
        }
        map.put("rows", list);
        map.put("footer", footer);
        map.put("total", footer.get(0).get("total"));
        apiRest.setIsSuccess(true);
        apiRest.setData(map);
        apiRest.setMessage("查询会员对账报表成功");
        return apiRest;
    }

    public ApiRest branchSaleDaily(Map params){
        ApiRest apiRest = new ApiRest();
        if(params.get("page") != null && params.get("rows") != null){
            params.put("offset", ((Integer.parseInt(params.get("page").toString()) - 1) * Integer.parseInt(params.get("rows").toString())));
        }
        String startDate = "";
        String endDate = "";
        if(params.get("startDate").toString().length() == 10){
            startDate = params.get("startDate").toString() + " 00:00:00";
        }
        else if(params.get("startDate").toString().length() == 16){
            startDate = params.get("startDate").toString() + ":00";
        }
        if(params.get("endDate").toString().length() == 10){
            endDate = params.get("endDate").toString() + " 23:59:59";
        }
        else if(params.get("endDate").toString().length() == 16){
            endDate = params.get("endDate").toString() + ":59";
        }
        params.put("startDate", startDate);
        params.put("endDate", endDate);
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String lastYearDayStartDate;
        String lastYearDayEndDate;
        String yesterdayStartDate;
        String yesterdayEndDate;
        String monthStartDate = startDate.substring(0, 8) + "01 00:00:00";
        String monthEndDate = startDate.substring(0, 8) + "31 23:59:59";
        params.put("monthStartDate", monthStartDate);
        params.put("monthEndDate", monthEndDate);
        String lastYearMonthStartDate;
        String lastYearMonthEndDate;
        String lastMonthStartDate;
        String lastMonthEndDate;
        String yearStartDate = startDate.substring(0, 5) + "01-01 00:00:00";
        String yearEndDate = startDate.substring(0, 5) + "12-31 23:59:59";
        params.put("yearStartDate", yearStartDate);
        params.put("yearEndDate", yearEndDate);
        String lastYearStartDate;
        String lastYearEndDate;
        try{
            //去年今日
            Calendar c = Calendar.getInstance();
            c.setTime(format.parse(startDate));
            c.add(Calendar.YEAR, -1);
            lastYearDayStartDate = format.format(c.getTime());
            lastYearDayEndDate = lastYearDayStartDate.substring(0, 10) + " 23:59:59";
            params.put("lastYearDayStartDate", lastYearDayStartDate);
            params.put("lastYearDayEndDate", lastYearDayEndDate);
            //昨日
            c.setTime(format.parse(startDate));
            c.add(Calendar.DATE, -1);
            yesterdayStartDate = format.format(c.getTime());
            yesterdayEndDate = yesterdayStartDate.substring(0, 10) + " 23:59:59";
            params.put("yesterdayStartDate", yesterdayStartDate);
            params.put("yesterdayEndDate", yesterdayEndDate);
            //去年本月
            c.setTime(format.parse(monthStartDate));
            c.add(Calendar.YEAR, -1);
            lastYearMonthStartDate = format.format(c.getTime());
            lastYearMonthEndDate = lastYearMonthStartDate.substring(0, 8) + "31 23:59:59";
            params.put("lastYearMonthStartDate", lastYearMonthStartDate);
            params.put("lastYearMonthEndDate", lastYearMonthEndDate);
            //上月
            c.setTime(format.parse(monthStartDate));
            c.add(Calendar.MONTH, -1);
            lastMonthStartDate = format.format(c.getTime());
            lastMonthEndDate = lastMonthStartDate.substring(0, 10) + "23:59:59";
            params.put("lastMonthStartDate", lastMonthStartDate);
            params.put("lastMonthEndDate", lastMonthEndDate);
            //去年
            lastYearStartDate = lastYearDayStartDate.substring(0, 5) + "01-01 00:00:00";
            lastYearEndDate = lastYearDayStartDate.substring(0, 5) + "12-31 23:59:59";
            params.put("lastYearStartDate", lastYearStartDate);
            params.put("lastYearEndDate", lastYearEndDate);
            Map map = new HashMap();
            List<Map> list = reportMapper.branchSaleDaily(params);
            List<Map> footer = reportMapper.branchSaleDailySum(params);
            map.put("rows", list);
            map.put("footer", footer);
            map.put("total", footer.get(0).get("total"));
            apiRest.setIsSuccess(true);
            apiRest.setData(map);
            apiRest.setMessage("查询机构销售日报成功");
            return apiRest;
        }catch (ParseException e){
            apiRest.setIsSuccess(false);
            apiRest.setError(e.getMessage());
        }
        return apiRest;
    }

    public ApiRest branchIntervalDaily(Map params){
        ApiRest apiRest = new ApiRest();
        if(params.get("page") != null && params.get("rows") != null){
            params.put("offset", ((Integer.parseInt(params.get("page").toString()) - 1) * Integer.parseInt(params.get("rows").toString())));
        }
        String startDate = "";
        String endDate = "";
        if(params.get("startDate").toString().length() == 10){
            startDate = params.get("startDate").toString() + " 00:00:00";
        }
        else if(params.get("startDate").toString().length() == 16){
            startDate = params.get("startDate").toString() + ":00";
        }
        if(params.get("endDate").toString().length() == 10){
            endDate = params.get("endDate").toString() + " 23:59:59";
        }
        else if(params.get("endDate").toString().length() == 16){
            endDate = params.get("endDate").toString() + ":59";
        }
        params.put("startDate", startDate);
        params.put("endDate", endDate);
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String lastStartDate;
        String lastEndDate;
        String yearStartDate = startDate.substring(0, 5) + "01-01 00:00:00";
        String yearEndDate = startDate.substring(0, 5) + "12-31 23:59:59";
        params.put("yearStartDate", yearStartDate);
        params.put("yearEndDate", yearEndDate);
        String lastYearStartDate;
        String lastYearEndDate;
        try{
            //去年同期
            Calendar c = Calendar.getInstance();
            c.setTime(format.parse(startDate));
            c.add(Calendar.YEAR, -1);
            lastStartDate = format.format(c.getTime());
            lastEndDate = lastStartDate.substring(0, 10) + " 23:59:59";
            params.put("lastStartDate", lastStartDate);
            params.put("lastEndDate", lastEndDate);
            //去年
            c.setTime(format.parse(yearStartDate));
            c.add(Calendar.YEAR, -1);
            lastYearStartDate = lastStartDate.substring(0, 5) + "01-01 00:00:00";
            lastYearEndDate = lastStartDate.substring(0, 5) + "12-31 23:59:59";
            params.put("lastYearStartDate", lastYearStartDate);
            params.put("lastYearEndDate", lastYearEndDate);
            List<Map> list = reportMapper.branchIntervalDaily(params);
            List<Map> footer = reportMapper.branchIntervalDailySum(params);
            Map map = new HashMap();
            map.put("rows", list);
            map.put("footer", footer);
            map.put("total", footer.get(0).get("total"));
            apiRest.setData(map);
            apiRest.setIsSuccess(true);
            apiRest.setMessage("查询机构销售区间日报成功");
        }catch (ParseException e) {
            apiRest.setIsSuccess(false);
            apiRest.setError(e.getMessage());
        }
        return apiRest;
    }

    /**
     * 获取流水信息，三条一起返回
     * @param params
     * @return
     */
    public ApiRest getSaleInfo(Map<String, String> params){
        ApiRest apiRest = new ApiRest();
        Map sale = reportMapper.getSaleInfo(params);
        List<Map> saleDetailList = reportMapper.getSaleDetailInfo(params);
        List<Map> salePaymentList = reportMapper.getSalePaymentInfo(params);
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("sale", sale);
        resultMap.put("saleDetail", saleDetailList);
        resultMap.put("salePayment", salePaymentList);
        apiRest.setIsSuccess(true);
        apiRest.setData(resultMap);
        return apiRest;
    }
}
