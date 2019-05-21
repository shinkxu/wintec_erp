package erp.chain.controller.report;

import com.saas.common.exception.ServiceException;
import com.saas.common.util.LogUtil;
import erp.chain.model.report.GrossProfitPagerModel;
import erp.chain.model.report.SaleEmpWorkPagerModel;
import erp.chain.service.report.ReportService;
import erp.chain.utils.AppHandler;
import erp.chain.utils.BeanUtils;
import org.apache.commons.lang.Validate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import saas.api.common.ApiRest;

import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 报表
 * operatingSituationStatistics接口未加入
 * Created with IntelliJ IDEA.
 * User: AnCong
 * Date: 2016/11/21
 */
@Controller
@ResponseBody
@RequestMapping(value = {"/report","/reportWebService"})
public class ReportController {
    @Autowired
    private ReportService reportService;

    /*************************************** 微信 **********************************************************/
    /**
     * 营业日报（wx）接口
     *
     * @return
     */
    @RequestMapping("/businessDaily")
    public String businessDaily() {
        ApiRest apiRest = new ApiRest();
        Map<String, String> params = AppHandler.params();
        try {
            apiRest = reportService.businessReport(params);
        } catch (Exception e) {
            apiRest.setIsSuccess(false);
            apiRest.setError(e.getMessage());
            LogUtil.logError(e, params);
        }
        return BeanUtils.toJsonStr(apiRest);
    }

    /**
     * 单品销售汇总（wx）接口
     *
     * @return
     */
    @RequestMapping(value = {"/singleGoodsReport"})
    public String singleGoodsReport() {
        ApiRest apiRest = new ApiRest();
        Map<String, String> params = AppHandler.params();

        try {
            apiRest = reportService.singleSaleReport(params);
        } catch (Exception e) {
            apiRest.setIsSuccess(false);
            apiRest.setError(e.getMessage());
            LogUtil.logError(e, params);
        }
        return BeanUtils.toJsonStr(apiRest);
    }

    /**
     * 收银情况汇总（wx）接口
     *
     * @return
     */
    @RequestMapping("/paymentReport")
    public String paymentReport() {
        ApiRest apiRest = new ApiRest();
        Map<String, String> params = AppHandler.params();
        try {
            apiRest = reportService.paymentReport(params);
        } catch (Exception e) {
            apiRest.setIsSuccess(false);
            apiRest.setError(e.getMessage());
            LogUtil.logError(e, params);
        }
        return BeanUtils.toJsonStr(apiRest);
    }

    /**
     * 时段销售趋势（wx）接口
     *
     * @return
     */
    @RequestMapping("/saleTrendData")
    public String saleTrendData() {
        ApiRest apiRest = new ApiRest();
        Map<String, String> params = AppHandler.params();
        try {
            params.put("trendType", "hour");
            params.put("flag", "wx");
            apiRest = reportService.saleTrendReport(params);
        } catch (Exception e) {
            apiRest.setIsSuccess(false);
            apiRest.setError(e.getMessage());
            LogUtil.logError(e, params);
        }
        return BeanUtils.toJsonStr(apiRest);
    }

    /**
     * 分类汇总（wx）接口
     *
     * @return
     */
    @RequestMapping(value = {"/categorySaleReport"})
    public String categorySaleReport() {
        ApiRest apiRest = new ApiRest();
        Map<String, String> params = AppHandler.params();
        try {
            params.put("flag", "first");
            apiRest = reportService.cateSaleReport(params);
        } catch (Exception e) {
            apiRest.setIsSuccess(false);
            apiRest.setError(e.getMessage());
            LogUtil.logError(e, params);
        }
        return BeanUtils.toJsonStr(apiRest);
    }
    /************************************ 后台或app *********************************************************/
    /**
     * 交易流水报表
     *
     * @return
     */
    @RequestMapping("/saleFlowReport")
    public String saleFlowReport() {
        ApiRest apiRest = new ApiRest();
        Map<String, String> params = AppHandler.params();
        try {
            if (params.get("page") == null || params.get("page").equals("")) {
                apiRest.setIsSuccess(false);
                apiRest.setError("参数page不能为空！");
                return BeanUtils.toJsonStr(apiRest);
            }
            if (params.get("rows") == null || params.get("rows").equals("")) {
                apiRest.setIsSuccess(false);
                apiRest.setError("参数rows不能为空！");
                return BeanUtils.toJsonStr(apiRest);
            }
            if (params.get("tenantId") == null || params.get("tenantId").equals("")) {
                apiRest.setIsSuccess(false);
                apiRest.setError("参数tenantId不能为空！");
                return BeanUtils.toJsonStr(apiRest);
            }
            if (params.get("branchId") == null || params.get("branchId").equals("")) {
                apiRest.setIsSuccess(false);
                apiRest.setError("参数branchId不能为空！");
                return BeanUtils.toJsonStr(apiRest);
            }
            apiRest = reportService.saleFlowReport(params);
        } catch (Exception e) {
            apiRest.setIsSuccess(false);
            apiRest.setError(e.getMessage());
            LogUtil.logError(e, params);
        }
        return BeanUtils.toJsonStr(apiRest);
    }

    /**
     * 明细流水报表
     *
     * @return
     */
    @RequestMapping("/saleGoodsReport")
    public String saleGoodsReport() {
        ApiRest apiRest = new ApiRest();
        Map<String, String> params = AppHandler.params();
        try {
            if (params.get("page") == null || params.get("page").equals("")) {
                apiRest.setIsSuccess(false);
                apiRest.setError("参数page不能为空！");
                return BeanUtils.toJsonStr(apiRest);
            }
            if (params.get("rows") == null || params.get("rows").equals("")) {
                apiRest.setIsSuccess(false);
                apiRest.setError("参数rows不能为空！");
                return BeanUtils.toJsonStr(apiRest);
            }
            if (params.get("tenantId") == null || params.get("tenantId").equals("")) {
                apiRest.setIsSuccess(false);
                apiRest.setError("参数tenantId不能为空！");
                return BeanUtils.toJsonStr(apiRest);
            }
            if (params.get("branchId") == null || params.get("branchId").equals("")) {
                apiRest.setIsSuccess(false);
                apiRest.setError("参数branchId不能为空！");
                return BeanUtils.toJsonStr(apiRest);
            }
            apiRest = reportService.saleGoodsReport(params);
        } catch (Exception e) {
            apiRest.setIsSuccess(false);
            apiRest.setError(e.getMessage());
            LogUtil.logError(e, params);
        }
        return BeanUtils.toJsonStr(apiRest);
    }

    /**
     * 支付流水报表
     *
     * @return
     */
    @RequestMapping("/salePaymentReport")
    public String salePaymentReport() {
        ApiRest apiRest = new ApiRest();
        Map<String, String> params = AppHandler.params();
        try {
            if (params.get("page") == null || params.get("page").equals("")) {
                apiRest.setIsSuccess(false);
                apiRest.setError("参数page不能为空！");
                return BeanUtils.toJsonStr(apiRest);
            }
            if (params.get("rows") == null || params.get("rows").equals("")) {
                apiRest.setIsSuccess(false);
                apiRest.setError("参数rows不能为空！");
                return BeanUtils.toJsonStr(apiRest);
            }
            if (params.get("tenantId") == null || params.get("tenantId").equals("")) {
                apiRest.setIsSuccess(false);
                apiRest.setError("参数tenantId不能为空！");
                return BeanUtils.toJsonStr(apiRest);
            }
            if (params.get("branchId") == null || params.get("branchId").equals("")) {
                apiRest.setIsSuccess(false);
                apiRest.setError("参数branchId不能为空！");
                return BeanUtils.toJsonStr(apiRest);
            }
            apiRest = reportService.salePaymentReport(params);
        } catch (Exception e) {
            apiRest.setIsSuccess(false);
            apiRest.setError(e.getMessage());
            LogUtil.logError(e, params);
        }
        return BeanUtils.toJsonStr(apiRest);
    }

    /**
     * 收款对账
     *
     * @return
     */
    @RequestMapping("/payReport")
    public String payReport() {
        ApiRest apiRest = new ApiRest();
        Map<String, String> params = AppHandler.params();
        try {
            if (params.get("page") == null || params.get("page").equals("")) {
                apiRest.setIsSuccess(false);
                apiRest.setError("参数page不能为空！");
                return BeanUtils.toJsonStr(apiRest);
            }
            if (params.get("rows") == null || params.get("rows").equals("")) {
                apiRest.setIsSuccess(false);
                apiRest.setError("参数rows不能为空！");
                return BeanUtils.toJsonStr(apiRest);
            }
            if (params.get("tenantId") == null || params.get("tenantId").equals("")) {
                apiRest.setIsSuccess(false);
                apiRest.setError("参数tenantId不能为空！");
                return BeanUtils.toJsonStr(apiRest);
            }
            if (params.get("branchId") == null || params.get("branchId").equals("")) {
                apiRest.setIsSuccess(false);
                apiRest.setError("参数branchId不能为空！");
                return BeanUtils.toJsonStr(apiRest);
            }
            apiRest = reportService.payReport(params);
        } catch (Exception e) {
            apiRest.setIsSuccess(false);
            apiRest.setError(e.getMessage());
            LogUtil.logError(e, params);
        }
        return BeanUtils.toJsonStr(apiRest);
    }
    /**
     * 储值对账
     */
    @RequestMapping("/storedValueReport")
    public String storedValueReport(){
        ApiRest apiRest = new ApiRest();
        Map<String, String> params = AppHandler.params();
        try {
            if (params.get("page") == null || params.get("page").equals("")) {
                apiRest.setIsSuccess(false);
                apiRest.setError("参数page不能为空！");
                return BeanUtils.toJsonStr(apiRest);
            }
            if (params.get("rows") == null || params.get("rows").equals("")) {
                apiRest.setIsSuccess(false);
                apiRest.setError("参数rows不能为空！");
                return BeanUtils.toJsonStr(apiRest);
            }
            if (params.get("branchId") == null || params.get("branchId").equals("")) {
                apiRest.setIsSuccess(false);
                apiRest.setError("参数branchId不能为空！");
                return BeanUtils.toJsonStr(apiRest);
            }
            if (params.get("tenantId") == null || params.get("tenantId").equals("")) {
                apiRest.setIsSuccess(false);
                apiRest.setError("参数tenantId不能为空！");
                return BeanUtils.toJsonStr(apiRest);
            }
            apiRest=reportService.storedValueReport(params);
        } catch (Exception e) {
            apiRest.setIsSuccess(false);
            apiRest.setError(e.getMessage());
            LogUtil.logError(e, params);
        }
        return  BeanUtils.toJsonStr(apiRest);
    }


    /**
     * 收款日对账
     * */
    @RequestMapping("/dayPayReport")
    public String dayPayReport(){
        ApiRest apiRest = new ApiRest();
        Map<String, String> params = AppHandler.params();
        if(params.get("tenantId") == null || "".equals(params.get("tenantId"))){
            apiRest.setIsSuccess(false);
            apiRest.setError("参数tenantId不能为空");
            return BeanUtils.toJsonStr(apiRest);
        }
        if(params.get("branchId") == null || "".equals(params.get("branchId"))){
            apiRest.setIsSuccess(false);
            apiRest.setError("参数branchId不能为空");
            return BeanUtils.toJsonStr(apiRest);
        }
        apiRest = reportService.dayPayReport(params);
        return BeanUtils.toJsonStr(apiRest);
    }

    /**
     * 分页查询员工收银情况报表,
     * 注：只统计当前条件下使用过的支付方式
     *
     * @author hxh
     */
    @RequestMapping("/querySaleEmpWorkPager")
    public String querySaleEmpWorkPager() {
        ApiRest rest = new ApiRest();
        SaleEmpWorkPagerModel model = AppHandler.bindParam(SaleEmpWorkPagerModel.class);
        if (!model.validate()) {
            rest.setData(model.getErrors());
            return BeanUtils.toJsonStr(rest);
        }
        rest.setData(reportService.querySaleEmpWorkPager(model).toMap());
        return BeanUtils.toJsonStr(rest);
    }

    /**
     * 单品汇总报表
     *
     * @return
     */
    @RequestMapping("/singleSaleReport")
    public String singleSaleReport() {
        ApiRest apiRest = new ApiRest();
        Map<String, String> params = AppHandler.params();
        try {
            if (params.get("page") == null || params.get("page").equals("")) {
                apiRest.setIsSuccess(false);
                apiRest.setError("参数page不能为空！");
                return BeanUtils.toJsonStr(apiRest);
            }
            if (params.get("rows") == null || params.get("rows").equals("")) {
                apiRest.setIsSuccess(false);
                apiRest.setError("参数rows不能为空！");
                return BeanUtils.toJsonStr(apiRest);
            }
            if (params.get("tenantId") == null || params.get("tenantId").equals("")) {
                apiRest.setIsSuccess(false);
                apiRest.setError("参数tenantId不能为空！");
                return BeanUtils.toJsonStr(apiRest);
            }
            if (params.get("branchId") == null || params.get("branchId").equals("")) {
                apiRest.setIsSuccess(false);
                apiRest.setError("参数branchId不能为空！");
                return BeanUtils.toJsonStr(apiRest);
            }
            apiRest = reportService.singleSaleReport(params);
        } catch (Exception e) {
            apiRest.setIsSuccess(false);
            apiRest.setError(e.getMessage());
            LogUtil.logError(e, params);
        }
        return BeanUtils.toJsonStr(apiRest);
    }

    /**
     * 分类汇总报表
     *
     * @return
     */
    @RequestMapping("/cateSaleReport")
    public String cateSaleReport() {
        ApiRest apiRest = new ApiRest();
        Map<String, String> params = AppHandler.params();
        try {
            if (params.get("page") == null || params.get("page").equals("")) {
                apiRest.setIsSuccess(false);
                apiRest.setError("参数page不能为空！");
                return BeanUtils.toJsonStr(apiRest);
            }
            if (params.get("rows") == null || params.get("rows").equals("")) {
                apiRest.setIsSuccess(false);
                apiRest.setError("参数rows不能为空！");
                return BeanUtils.toJsonStr(apiRest);
            }
            if (params.get("tenantId") == null || params.get("tenantId").equals("")) {
                apiRest.setIsSuccess(false);
                apiRest.setError("参数tenantId不能为空！");
                return BeanUtils.toJsonStr(apiRest);
            }
            if (params.get("branchId") == null || params.get("branchId").equals("")) {
                apiRest.setIsSuccess(false);
                apiRest.setError("参数branchId不能为空！");
                return BeanUtils.toJsonStr(apiRest);
            }
            if (params.get("flag") == null || params.get("flag").equals("")) {
                apiRest.setIsSuccess(false);
                apiRest.setError("参数flag不能为空！");
                return BeanUtils.toJsonStr(apiRest);
            }
            if (params.get("flag").equals("second")) {
                if (params.get("categoryId") != null && params.get("categoryId").equals("-9900")) {
                    apiRest.setIsSuccess(false);
                    apiRest.setError("二级分类无法查询套餐数据！");
                    return BeanUtils.toJsonStr(apiRest);
                }
            }
            apiRest = reportService.cateSaleReport(params);
        } catch (Exception e) {
            apiRest.setIsSuccess(false);
            apiRest.setError(e.getMessage());
            LogUtil.logError(e, params);
        }
        return BeanUtils.toJsonStr(apiRest);
    }

    /**
     * 销售趋势报表
     *
     * @return
     */
    @RequestMapping("/saleTrendReport")
    public String saleTrendReport() {
        ApiRest apiRest = new ApiRest();
        Map<String, String> params = AppHandler.params();
        try {
            if (params.get("page") == null || params.get("page").equals("")) {
                apiRest.setIsSuccess(false);
                apiRest.setError("参数page不能为空！");
                return BeanUtils.toJsonStr(apiRest);
            }
            if (params.get("rows") == null || params.get("rows").equals("")) {
                apiRest.setIsSuccess(false);
                apiRest.setError("参数rows不能为空！");
                return BeanUtils.toJsonStr(apiRest);
            }
            if (params.get("tenantId") == null || params.get("tenantId").equals("")) {
                apiRest.setIsSuccess(false);
                apiRest.setError("参数tenantId不能为空！");
                return BeanUtils.toJsonStr(apiRest);
            }
            if (params.get("branchId") == null || params.get("branchId").equals("")) {
                apiRest.setIsSuccess(false);
                apiRest.setError("参数branchId不能为空！");
                return BeanUtils.toJsonStr(apiRest);
            }
            if (params.get("trendType") == null || params.get("trendType").equals("")) {
                apiRest.setIsSuccess(false);
                apiRest.setError("参数trendType不能为空！");
                return BeanUtils.toJsonStr(apiRest);
            }
            params.put("flag", "pc");
            apiRest = reportService.saleTrendReport(params);
        } catch (Exception e) {
            apiRest.setIsSuccess(false);
            apiRest.setError(e.getMessage());
            LogUtil.logError(e, params);
        }
        return BeanUtils.toJsonStr(apiRest);
    }

    /**
     * 销售趋势折线图
     * */
    @RequestMapping("/lineForSaleTrend")
    public String lineForSaleTrend(){
        ApiRest apiRest = new ApiRest();
        Map params = AppHandler.params();
        try{
            if (params.get("page") == null || params.get("page").equals("")) {
                apiRest.setIsSuccess(false);
                apiRest.setError("参数page不能为空！");
                return BeanUtils.toJsonStr(apiRest);
            }
            if (params.get("rows") == null || params.get("rows").equals("")) {
                apiRest.setIsSuccess(false);
                apiRest.setError("参数rows不能为空！");
                return BeanUtils.toJsonStr(apiRest);
            }
            if (params.get("tenantId") == null || params.get("tenantId").equals("")) {
                apiRest.setIsSuccess(false);
                apiRest.setError("参数tenantId不能为空！");
                return BeanUtils.toJsonStr(apiRest);
            }
            if (params.get("branchId") == null || params.get("branchId").equals("")) {
                apiRest.setIsSuccess(false);
                apiRest.setError("参数branchId不能为空！");
                return BeanUtils.toJsonStr(apiRest);
            }
            if (params.get("trendType") == null || params.get("trendType").equals("")) {
                apiRest.setIsSuccess(false);
                apiRest.setError("参数trendType不能为空！");
                return BeanUtils.toJsonStr(apiRest);
            }
            params.put("flag", "pc");
            apiRest = reportService.lineForSaleTrend(params);
        }catch(Exception e){
            apiRest.setIsSuccess(false);
            apiRest.setError(e.getMessage());
            LogUtil.logError(e, params);
        }
        return BeanUtils.toJsonStr(apiRest);
    }

    /**
     * 营业概况报表
     *
     * @return
     */
    @RequestMapping("/businessReport")
    public String businessReport() {
        ApiRest apiRest = new ApiRest();
        Map<String, String> params = AppHandler.params();
        try {
            if (params.get("tenantId") == null || params.get("tenantId").equals("")) {
                apiRest.setIsSuccess(false);
                apiRest.setError("参数tenantId不能为空！");
                return BeanUtils.toJsonStr(apiRest);
            }
            if (params.get("branchId") == null || params.get("branchId").equals("")) {
                apiRest.setIsSuccess(false);
                apiRest.setError("参数branchId不能为空！");
                return BeanUtils.toJsonStr(apiRest);
            }
            apiRest = reportService.businessReport(params);
        } catch (Exception e) {
            apiRest.setIsSuccess(false);
            apiRest.setError(e.getMessage());
            LogUtil.logError(e, params);
        }
        return BeanUtils.toJsonStr(apiRest);
    }

    /**
     * 机构报表
     *
     * @return
     */
    @RequestMapping("/branchReport")
    public String branchReport() {
        ApiRest apiRest = new ApiRest();
        Map<String, String> params = AppHandler.params();
        try {
            if (params.get("page") == null || params.get("page").equals("")) {
                apiRest.setIsSuccess(false);
                apiRest.setError("参数page不能为空！");
                return BeanUtils.toJsonStr(apiRest);
            }
            if (params.get("rows") == null || params.get("rows").equals("")) {
                apiRest.setIsSuccess(false);
                apiRest.setError("参数rows不能为空！");
                return BeanUtils.toJsonStr(apiRest);
            }
            if (params.get("startDate") == null || params.get("startDate").equals("")) {
                apiRest.setIsSuccess(false);
                apiRest.setError("参数startDate不能为空！");
                return BeanUtils.toJsonStr(apiRest);
            }
            if (params.get("endDate") == null || params.get("endDate").equals("")) {
                apiRest.setIsSuccess(false);
                apiRest.setError("参数rows不能为空！");
                return BeanUtils.toJsonStr(apiRest);
            }
            if (params.get("tenantId") == null || params.get("tenantId").equals("")) {
                apiRest.setIsSuccess(false);
                apiRest.setError("参数tenantId不能为空！");
                return BeanUtils.toJsonStr(apiRest);
            }
//            if (params.get("branchId") == null || params.get("branchId").equals("")) {
//                apiRest.setIsSuccess(false);
//                apiRest.setError("参数branchId不能为空！");
//                return BeanUtils.toJsonStr(apiRest);
//            }
            apiRest = reportService.branchReport(params);
        } catch (Exception e) {
            apiRest.setIsSuccess(false);
            apiRest.setError(e.getMessage());
            LogUtil.logError(e, params);
        }
        return BeanUtils.toJsonStr(apiRest);
    }
    /**
     * 门店支付方式汇总报表
     */
    @RequestMapping("/branchPaymentMethod")
    public String branchPaymentMethod() {
        ApiRest apiRest = new ApiRest();
        Map<String, String> params = AppHandler.params();
        if (params.get("tenantId") == null || params.get("tenantId").equals("")) {
            apiRest.setIsSuccess(false);
            apiRest.setError("参数tenantId不能为空！");
            return BeanUtils.toJsonStr(apiRest);
        }
        try {
            apiRest = reportService.branchPaymentMethodReport(params);
        } catch (Exception e) {
            apiRest.setIsSuccess(false);
            apiRest.setError(e.getMessage());
            LogUtil.logError(e, params);
        }
        return BeanUtils.toJsonStr(apiRest);
    }

    /**
     * 获取所有支付方式（储值+收款）
     * */
    @RequestMapping("/getAllPayWays")
    public String getAllPayWays(){
        ApiRest apiRest = new ApiRest();
        Map<String, String> params = AppHandler.params();
        if(params.get("tenantId") == null || "".equals(params.get("tenantId"))){
            apiRest.setIsSuccess(false);
            apiRest.setError("参数tenantId不能为空");
            return BeanUtils.toJsonStr(apiRest);
        }
        apiRest = reportService.getAllPayWays(params);
        return BeanUtils.toJsonStr(apiRest);
    }

    /**
     * 机构收款汇总表
     * */
    @RequestMapping("/gatheringOfBranchReport")
    public String gatheringOfBranchReport(){
        ApiRest apiRest = new ApiRest();
        Map<String, String> params = AppHandler.params();
        try{
            if(params.get("tenantId") == null || "".equals(params.get("tenantId"))){
                apiRest.setIsSuccess(false);
                apiRest.setError("参数tenantId不能为空");
                return BeanUtils.toJsonStr(apiRest);
            }
            apiRest = reportService.gatheringOfBranchReport(params);
        }catch (Exception e){
            apiRest.setIsSuccess(false);
            apiRest.setError(e.getMessage());
            LogUtil.logError(e, params);
        }
        return BeanUtils.toJsonStr(apiRest);
    }

    /**
     * 获取所有门店使用的支付方式
     */
    @RequestMapping("/getPayMethodWays")
    public String getPayMethodWays() {
        ApiRest apiRest = new ApiRest();
        Map<String, String> params = AppHandler.params();
        try {
            if (params.get("tenantId") == null || params.get("tenantId").equals("")) {
                apiRest.setIsSuccess(false);
                apiRest.setError("参数tenantId不能为空！");
                return BeanUtils.toJsonStr(apiRest);
            }
/*            if (params.get("branchId") == null || params.get("branchId").equals("")) {
                apiRest.setIsSuccess(false);
                apiRest.setError("参数branchId不能为空！");
                return BeanUtils.toJsonStr(apiRest);
            }*/
            apiRest = reportService.getPayMethodWays(params);
        } catch (Exception e) {
            apiRest.setIsSuccess(false);
            apiRest.setError(e.getMessage());
            LogUtil.logError(e, params);
        }
        return BeanUtils.toJsonStr(apiRest);
    }

    /**
     * 毛利分析报表
     */
    @RequestMapping("/queryGrossProfitPager")
    public String queryGrossProfitPager() {
        ApiRest apiRest = new ApiRest();
        GrossProfitPagerModel model = AppHandler.bindParam(GrossProfitPagerModel.class);
        try {
            apiRest.setData(reportService.queryGrossProfitPager(model).toMap());
            apiRest.setIsSuccess(true);
        } catch (Exception e) {
            apiRest.setIsSuccess(false);
            apiRest.setError(e.getMessage());
            LogUtil.logError(e.getMessage());
        }
        return BeanUtils.toJsonStr(apiRest);
    }

    /**
     * 获取收款对账的支付方式
     *
     * @return
     */
    @RequestMapping("/getPayWays")
    public String getPayWays() {
        ApiRest apiRest = new ApiRest();
        Map<String, String> params = AppHandler.params();
        try {
            if (params.get("tenantId") == null || params.get("tenantId").equals("")) {
                apiRest.setIsSuccess(false);
                apiRest.setError("参数tenantId不能为空！");
                return BeanUtils.toJsonStr(apiRest);
            }
            if (params.get("branchId") == null || params.get("branchId").equals("")) {
                apiRest.setIsSuccess(false);
                apiRest.setError("参数branchId不能为空！");
                return BeanUtils.toJsonStr(apiRest);
            }
            apiRest = reportService.payWays(params);
        } catch (Exception e) {
            apiRest.setIsSuccess(false);
            apiRest.setError(e.getMessage());
            LogUtil.logError(e, params);
        }
        return BeanUtils.toJsonStr(apiRest);
    }

    /**
     * 营业概况报表
     *
     * @return
     */
    @RequestMapping("/businessSumReport")
    public String businessSumReport() {
        ApiRest apiRest = new ApiRest();
        Map<String, String> params = AppHandler.params();
        try {
            if (params.get("tenantId") == null || params.get("tenantId").equals("")) {
                apiRest.setIsSuccess(false);
                apiRest.setError("参数tenantId不能为空！");
                return BeanUtils.toJsonStr(apiRest);
            }
            if (params.get("branchId") == null || params.get("branchId").equals("")) {
                apiRest.setIsSuccess(false);
                apiRest.setError("参数branchId不能为空！");
                return BeanUtils.toJsonStr(apiRest);
            }
            if (params.get("today") == null || params.get("today").equals("")) {
                apiRest.setIsSuccess(false);
                apiRest.setError("参数today不能为空！");
                return BeanUtils.toJsonStr(apiRest);
            }
            apiRest = reportService.businessSumReport(params);
        } catch (Exception e) {
            apiRest.setIsSuccess(false);
            apiRest.setError(e.getMessage());
            LogUtil.logError(e, params);
        }
        return BeanUtils.toJsonStr(apiRest);
    }

    /**
     * 销售趋势报表
     *
     * @return
     */
    @RequestMapping("/saleTrend30DaysReport")
    public String saleTrend30DaysReport() {
        ApiRest apiRest = new ApiRest();
        Map<String, String> params = AppHandler.params();
        try {
            if (params.get("tenantId") == null || params.get("tenantId").equals("")) {
                apiRest.setIsSuccess(false);
                apiRest.setError("参数tenantId不能为空！");
                return BeanUtils.toJsonStr(apiRest);
            }
            apiRest = reportService.saleTrend30DaysReport(params);
        } catch (Exception e) {
            apiRest.setIsSuccess(false);
            apiRest.setError(e.getMessage());
            LogUtil.logError(e, params);
        }
        return BeanUtils.toJsonStr(apiRest);
    }

    /**
     * 支付方式汇总表
     *
     * @return
     * */
    @RequestMapping("/paymentTypeReport")
    public String paymentTypeReport(){
        ApiRest apiRest = new ApiRest();
        Map<String, String> params = AppHandler.params();
        try {
            if (params.get("page") == null || params.get("page").equals("")) {
                apiRest.setIsSuccess(false);
                apiRest.setError("参数page不能为空！");
                return BeanUtils.toJsonStr(apiRest);
            }
            if (params.get("rows") == null || params.get("rows").equals("")) {
                apiRest.setIsSuccess(false);
                apiRest.setError("参数rows不能为空！");
                return BeanUtils.toJsonStr(apiRest);
            }
            if (params.get("tenantId") == null || params.get("tenantId").equals("")) {
                apiRest.setIsSuccess(false);
                apiRest.setError("参数tenantId不能为空！");
                return BeanUtils.toJsonStr(apiRest);
            }
            if (params.get("branchId") == null || params.get("branchId").equals("")) {
                apiRest.setIsSuccess(false);
                apiRest.setError("参数branchId不能为空！");
                return BeanUtils.toJsonStr(apiRest);
            }
            apiRest = reportService.paymentTypeReport(params);
        } catch (Exception e) {
            apiRest.setIsSuccess(false);
            apiRest.setError(e.getMessage());
            LogUtil.logError(e, params);
        }
        return BeanUtils.toJsonStr(apiRest);
    }

    /**
     * 区域销售汇总表
     *
     * @return
     * */
    @RequestMapping("/areaReport")
    public String areaReport(){
        ApiRest apiRest = new ApiRest();
        Map<String, String> params = AppHandler.params();
        try{
            if (params.get("page") == null || params.get("page").equals("")) {
                apiRest.setIsSuccess(false);
                apiRest.setError("参数page不能为空！");
                return BeanUtils.toJsonStr(apiRest);
            }
            if (params.get("rows") == null || params.get("rows").equals("")) {
                apiRest.setIsSuccess(false);
                apiRest.setError("参数rows不能为空！");
                return BeanUtils.toJsonStr(apiRest);
            }
            apiRest = reportService.areaReport(params);
        }catch (Exception e){
            apiRest.setIsSuccess(false);
            apiRest.setError(e.getMessage());
            LogUtil.logError(e, params);
        }
        return BeanUtils.toJsonStr(apiRest);
    }

    /**
     * 子区域销售表
     *
     * @return
     * */
    @RequestMapping("/subregionReport")
    public String subregionReport(){
        ApiRest apiRest = new ApiRest();
        Map<String, String> params = AppHandler.params();
        try{
            if (params.get("page") == null || params.get("page").equals("")) {
                apiRest.setIsSuccess(false);
                apiRest.setError("参数page不能为空！");
                return BeanUtils.toJsonStr(apiRest);
            }
            if (params.get("rows") == null || params.get("rows").equals("")) {
                apiRest.setIsSuccess(false);
                apiRest.setError("参数rows不能为空！");
                return BeanUtils.toJsonStr(apiRest);
            }
            if (params.get("parentId") == null || params.get("parentId").equals("")) {
                apiRest.setIsSuccess(false);
                apiRest.setError("参数parentId不能为空！");
                return BeanUtils.toJsonStr(apiRest);
            }
            apiRest = reportService.subregionReport(params);
        }catch (Exception e){
            apiRest.setIsSuccess(false);
            apiRest.setError(e.getMessage());
            LogUtil.logError(e, params);
        }
        return BeanUtils.toJsonStr(apiRest);
    }

    /**
     * 营业概况报表
     *
     * @return
     * */
    @RequestMapping("/businessOverviewReport")
    public String businessOverviewReport(){
        ApiRest apiRest = new ApiRest();
        Map<String, String> params = AppHandler.params();
        try {
            if (params.get("page") == null || params.get("page").equals("")) {
                apiRest.setIsSuccess(false);
                apiRest.setError("参数page不能为空！");
                return BeanUtils.toJsonStr(apiRest);
            }
            if (params.get("rows") == null || params.get("rows").equals("")) {
                apiRest.setIsSuccess(false);
                apiRest.setError("参数rows不能为空！");
                return BeanUtils.toJsonStr(apiRest);
            }
            apiRest = reportService.businessOverviewReport(params);
        }catch (Exception e){
            apiRest.setIsSuccess(false);
            apiRest.setError(e.getMessage());
            return BeanUtils.toJsonStr(apiRest);
        }
        return BeanUtils.toJsonStr(apiRest);
    }

    /**
     * 赠退明细查询报表
     *
     * @return
     * */
    @RequestMapping("/retirementReport")
    public String retirementReport(){
        ApiRest apiRest = new ApiRest();
        Map<String, String> params = AppHandler.params();
        try{
            if(params.get("page") == null || params.get("page").equals("")){
                apiRest.setIsSuccess(false);
                apiRest.setError("参数page不能为空！");
                return BeanUtils.toJsonStr(apiRest);
            }
            if(params.get("rows") == null || params.get("rows").equals("")){
                apiRest.setIsSuccess(false);
                apiRest.setError("参数rows不能为空！");
                return BeanUtils.toJsonStr(apiRest);
            }
            apiRest = reportService.retirementReport(params);
        }catch(Exception e){
            apiRest.setIsSuccess(false);
            apiRest.setError(e.getMessage());
            return BeanUtils.toJsonStr(apiRest);
        }
        return BeanUtils.toJsonStr(apiRest);
    }

    /**
     * 库存预警报表
     * */
    @RequestMapping("/storeWarningReport")
    public String storeWarningReport(){
        ApiRest apiRest = new ApiRest();
        Map<String, String> params = AppHandler.params();
        try{
            if(params.get("tenantId") == null || "".equals(params.get("tenantId"))){
                apiRest.setIsSuccess(false);
                apiRest.setMessage("参数tenantId不能为空！");
                return BeanUtils.toJsonStr(apiRest);
            }
            if(params.get("branchId") == null || "".equals(params.get("branchId"))){
                apiRest.setIsSuccess(false);
                apiRest.setMessage("参数branchId不能为空！");
            }
            if(params.get("storeStatus") == null || "".equals(params.get("storeStatus"))){
                apiRest.setIsSuccess(false);
                apiRest.setMessage("参数storeStatus不能为空！");
            }
            apiRest = reportService.storeWarningReport(params);
        }catch (Exception e){
            apiRest.setIsSuccess(false);
            apiRest.setMessage(e.getMessage());
            return BeanUtils.toJsonStr(apiRest);
        }
        return BeanUtils.toJsonStr(apiRest);
    }


    /**
     * 准备经营状况通知数据(2016年8月27日11:30:04 刘艳东)
     * @return
     */
    @RequestMapping("/operatingSituationStatistics")
    public String operatingSituationStatistics() {
        ApiRest apiRest = new ApiRest();
        Map<String, String> params = AppHandler.params();
        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String beginDateString = params.get("beginDate");
            Validate.notNull(beginDateString, "开始时间不能为空！");
            String endDateString = params.get("endDate");
            Validate.notNull(endDateString, "结束时间不能为空！");
            Date beginDate = simpleDateFormat.parse(beginDateString);
            Date endDate = simpleDateFormat.parse(endDateString);
            String userIdString = params.get("userId");
            Validate.notNull(userIdString, "用户ID 不能为空！");
            BigInteger userId = BigInteger.valueOf(Long.valueOf(userIdString));
            String partitionCode = params.get("partitionCode");
            Validate.notNull(partitionCode, "商户分区码不能为空！");
            apiRest = reportService.operatingSituationStatistics(beginDate, endDate, userId, partitionCode);
        } catch (ServiceException se) {
            LogUtil.logError(se, params);
            apiRest.setIsSuccess(false);
            apiRest.setError(se.getMessage());
        } catch (Exception e) {
            LogUtil.logError(e, params);
            apiRest.setIsSuccess(false);
            apiRest.setError(e.getMessage());
        }
        return BeanUtils.toJsonStr(apiRest);
    }

    @RequestMapping("/businessOverview")
    public String businessOverview(){
        ApiRest apiRest = new ApiRest();
        Map<String, String> params = AppHandler.params();
        try {
            if(params.get("flag") == null || params.get("flag").equals("")){
                apiRest.setIsSuccess(false);
                apiRest.setError("参数flag不能为空！");
                return BeanUtils.toJsonStr(apiRest);
            }
            if(!params.get("flag").equals("1")&&!params.get("flag").equals("2")){
                apiRest.setIsSuccess(false);
                apiRest.setError("参数flag只能为1或2！");
                return BeanUtils.toJsonStr(apiRest);
            }
            /**机构list，中间用","分隔*/
            if(params.get("branchIds") == null || params.get("branchIds").equals("")){
                apiRest.setIsSuccess(false);
                apiRest.setError("参数branchIds不能为空！");
                return BeanUtils.toJsonStr(apiRest);
            }
            if(params.get("tenantId") == null || params.get("tenantId").equals("")){
                apiRest.setIsSuccess(false);
                apiRest.setError("参数tenantId不能为空！");
                return BeanUtils.toJsonStr(apiRest);
            }
            apiRest = reportService.businessOverview(params);
        } catch (ServiceException se) {
            LogUtil.logError(se, params);
            apiRest.setIsSuccess(false);
            apiRest.setError(se.getMessage());
        } catch (Exception e) {
            LogUtil.logError(e, params);
            apiRest.setIsSuccess(false);
            apiRest.setError(e.getMessage());
        }
        return BeanUtils.toJsonStr(apiRest);
    }

    /**
     * 套餐销售汇总
     * */
    @RequestMapping("/packageReport")
    public String packageReport(){
        ApiRest apiRest = new ApiRest();
        Map<String, String> params = AppHandler.params();
        try{
            if(params.get("tenantId") == null || "".equals(params.get("tenantId"))){
                apiRest.setIsSuccess(false);
                apiRest.setError("参数tenantId不能为空");
                return BeanUtils.toJsonStr(apiRest);
            }
            if(params.get("branchId") == null || "".equals(params.get("branchId"))){
                apiRest.setIsSuccess(false);
                apiRest.setError("参数branchId不能为空");
                return BeanUtils.toJsonStr(apiRest);
            }
            if(params.get("rows") == null || "".equals(params.get("rows"))){
                apiRest.setIsSuccess(false);
                apiRest.setError("参数rows不能为空");
                return BeanUtils.toJsonStr(apiRest);
            }
            if(params.get("page") == null || "".equals(params.get("page"))){
                apiRest.setIsSuccess(false);
                apiRest.setError("参数page不能为空");
                return BeanUtils.toJsonStr(apiRest);
            }
            apiRest = reportService.packageReport(params);
        }catch(Exception e){
            apiRest.setIsSuccess(false);
            apiRest.setError("查询套餐销售汇总失败");
            LogUtil.logError(e.getMessage());
        }
        return BeanUtils.toJsonStr(apiRest);
    }

    /**
     * 会员台帐-积分纪录
     * */
    @RequestMapping("/vipScoreRecord")
    public String vipScoreRecord(){
        ApiRest apiRest = new ApiRest();
        Map<String, String> params = AppHandler.params();
        try{
            if(params.get("tenantId") == null || "".equals(params.get("tenantId"))){
                apiRest.setIsSuccess(false);
                apiRest.setError("参数tenantId不能为空");
                return BeanUtils.toJsonStr(apiRest);
            }
            if(params.get("branchId") == null || "".equals(params.get("branchId"))){
                apiRest.setIsSuccess(false);
                apiRest.setError("参数branchId不能为空");
                return BeanUtils.toJsonStr(apiRest);
            }
            apiRest = reportService.vipScoreRecord(params);
        }catch(Exception e){
            apiRest.setIsSuccess(false);
            apiRest.setError("查询会员积分记录报表失败");
            LogUtil.logError(e, params);
        }
        return BeanUtils.toJsonStr(apiRest);
    }


    /**
     * 会员台账-储值记录
     * */
    @RequestMapping("/vipStoreRecord")
    public String vipStoreRecord(){
        ApiRest apiRest = new ApiRest();
        Map<String, String> params = AppHandler.params();
        try{
            if(params.get("tenantId") == null || "".equals(params.get("tenantId"))){
                apiRest.setIsSuccess(false);
                apiRest.setError("参数tenantId不能为空");
                return BeanUtils.toJsonStr(apiRest);
            }
            if(params.get("branchId") == null || "".equals(params.get("branchId"))){
                apiRest.setIsSuccess(false);
                apiRest.setError("参数branchId不能为空");
                return BeanUtils.toJsonStr(apiRest);
            }
            apiRest = reportService.vipStoreRecord(params);
        }catch(Exception e){
            apiRest.setIsSuccess(false);
            apiRest.setError("查询会员储值记录报表失败");
            LogUtil.logError(e, params);
        }
        return BeanUtils.toJsonStr(apiRest);
    }

    /**
     * 会员台账-消费台账
     * */
    @RequestMapping("/vipTradeRecord")
    public String vipTradeRecord(){
        ApiRest apiRest = new ApiRest();
        Map params = AppHandler.params();
        try{
            if(params.get("tenantId") == null || "".equals(params.get("tenantId"))){
                apiRest.setIsSuccess(false);
                apiRest.setError("参数tenantId不能为空");
                return BeanUtils.toJsonStr(apiRest);
            }
            if(params.get("branchId") == null || "".equals(params.get("branchId"))){
                apiRest.setIsSuccess(false);
                apiRest.setError("参数branchId不能为空");
                return BeanUtils.toJsonStr(apiRest);
            }
            apiRest = reportService.vipTradeRecord(params);
        }catch(Exception e){
            apiRest.setIsSuccess(false);
            apiRest.setError("查询会员消费台账失败");
            LogUtil.logError(e, params);
        }
        return BeanUtils.toJsonStr(apiRest);
    }

    /**
     * 查询详细单据信息
     * */
    @RequestMapping("/orderDetailInfo")
    public String orderDetailInfo(){
        ApiRest apiRest = new ApiRest();
        Map params = AppHandler.params();
        try{
            if(params.get("tradeNo") == null || "".equals(params.get("tradeNo"))){
                apiRest.setIsSuccess(false);
                apiRest.setError("参数tradeNo不能为空");
                return BeanUtils.toJsonStr(apiRest);
            }
            if(params.get("tenantId") == null || "".equals(params.get("tenantId"))){
                apiRest.setIsSuccess(false);
                apiRest.setError("参数tenantId不能为空");
                return BeanUtils.toJsonStr(apiRest);
            }
            if(params.get("branchId") == null || "".equals(params.get("branchId"))){
                apiRest.setIsSuccess(false);
                apiRest.setError("参数branchId不能为空");
                return BeanUtils.toJsonStr(apiRest);
            }
            apiRest = reportService.orderDetailInfo(params);
        }catch (Exception e){
            apiRest.setIsSuccess(false);
            apiRest.setError("查询单据详细信息失败");
            LogUtil.logError(e, params);
        }
        return BeanUtils.toJsonStr(apiRest);
    }

    /**
     * 规格汇总报表
     * */
    @RequestMapping("/standardReport")
    public String standardReport(){
        ApiRest apiRest = new ApiRest();
        Map<String, String> params = AppHandler.params();
        try{
            if(params.get("tenantId") == null || "".equals(params.get("tenantId"))){
                apiRest.setIsSuccess(false);
                apiRest.setError("参数tenantId不能为空");
                return BeanUtils.toJsonStr(apiRest);
            }
            if(params.get("branchId") == null || "".equals(params.get("branchId"))){
                apiRest.setIsSuccess(false);
                apiRest.setError("参数branchId不能为空");
                return BeanUtils.toJsonStr(apiRest);
            }
            apiRest = reportService.standardReport(params);
        }catch (Exception e){
            apiRest.setIsSuccess(false);
            apiRest.setError("查询规格汇总报表失败");
            LogUtil.logError(e, params);
        }
        return BeanUtils.toJsonStr(apiRest);
    }

    /**
     * 进销存报表
     * */
    @RequestMapping("/goodsInvoicingReport")
    public String goodsInvoicingReport(){
        ApiRest apiRest = new ApiRest();
        Map params = AppHandler.params();
        try{
            if(params.get("tenantId") == null || "".equals(params.get("tenantId"))){
                apiRest.setIsSuccess(false);
                apiRest.setError("参数tenantId不能为空");
                return BeanUtils.toJsonStr(apiRest);
            }
            if(params.get("branchId") == null || "".equals(params.get("branchId"))){
                apiRest.setIsSuccess(false);
                apiRest.setError("参数branchId不能为空");
                return BeanUtils.toJsonStr(apiRest);
            }
            apiRest = reportService.goodsInvoicingReport(params);
        }catch(Exception e){
            apiRest.setIsSuccess(false);
            apiRest.setError("查询进销存报表失败");
            LogUtil.logError(e, params);
        }
        return BeanUtils.toJsonStr(apiRest);
    }

    /**
     * 单品销售汇总-销售趋势分析
     * */
    @RequestMapping("/saleTrendAnalysis")
    public String saleTrendAnalysis(){
        ApiRest apiRest = new ApiRest();
        Map params = AppHandler.params();
        try{
            if(params.get("tenantId") == null || "".equals(params.get("tenantId"))){
                apiRest.setIsSuccess(false);
                apiRest.setError("参数tenantId不能为空");
                return BeanUtils.toJsonStr(apiRest);
            }
            if(params.get("branchId") == null || "".equals(params.get("branchId"))){
                apiRest.setIsSuccess(false);
                apiRest.setError("参数branchId不能为空");
                return BeanUtils.toJsonStr(apiRest);
            }
            if(params.get("goodsId") == null || "".equals(params.get("goodsId"))){
                apiRest.setIsSuccess(false);
                apiRest.setError("参数goodsId不能为空");
                return BeanUtils.toJsonStr(apiRest);
            }
            if(params.get("startDate") == null || "".equals(params.get("startDate"))){
                apiRest.setIsSuccess(false);
                apiRest.setError("参数startDate不能为空");
                return BeanUtils.toJsonStr(apiRest);
            }
            if(params.get("endDate") == null || "".equals(params.get("endDate"))){
                apiRest.setIsSuccess(false);
                apiRest.setError("参数endDate不能为空");
                return BeanUtils.toJsonStr(apiRest);
            }
            apiRest = reportService.saleTrendAnalysis(params);
        }catch (Exception e){
            apiRest.setIsSuccess(false);
            apiRest.setError(e.getMessage());
            LogUtil.logError(e, params);
        }
        return BeanUtils.toJsonStr(apiRest);
    }

    /**
     * 单品销售汇总-销售趋势报表
     * */
    @RequestMapping("/saleAnalysisReport")
    public String saleAnalysisReport(){
        ApiRest apiRest = new ApiRest();
        Map params = AppHandler.params();
        try{
            if(params.get("tenantId") == null || "".equals(params.get("tenantId"))){
                apiRest.setIsSuccess(false);
                apiRest.setError("参数tenantId不能为空");
                return BeanUtils.toJsonStr(apiRest);
            }
            if(params.get("branchId") == null || "".equals(params.get("branchId"))){
                apiRest.setIsSuccess(false);
                apiRest.setError("参数branchId不能为空");
                return BeanUtils.toJsonStr(apiRest);
            }
            if(params.get("goodsId") == null || "".equals(params.get("goodsId"))){
                apiRest.setIsSuccess(false);
                apiRest.setError("参数goodsId不能为空");
                return BeanUtils.toJsonStr(apiRest);
            }
            if(params.get("startDate") == null || "".equals(params.get("startDate"))){
                apiRest.setIsSuccess(false);
                apiRest.setError("参数startDate不能为空");
                return BeanUtils.toJsonStr(apiRest);
            }
            if(params.get("endDate") == null || "".equals(params.get("endDate"))){
                apiRest.setIsSuccess(false);
                apiRest.setError("参数endDate不能为空");
                return BeanUtils.toJsonStr(apiRest);
            }
            apiRest = reportService.saleAnalysisReport(params);
        }catch(Exception e){
            apiRest.setIsSuccess(false);
            apiRest.setError(e.getMessage());
            LogUtil.logError(e, params);
        }
        return BeanUtils.toJsonStr(apiRest);
    }

    /**
     * 会员对账报表
     * */
    @RequestMapping("vipStatementReport")
    public String vipStatementReport(){
        ApiRest apiRest = new ApiRest();
        Map<String, String> params = AppHandler.params();
        try{
            if(params.get("tenantId") == null || "".equals(params.get("tenantId"))){
                apiRest.setIsSuccess(false);
                apiRest.setError("参数tenantId不能为空");
                return BeanUtils.toJsonStr(apiRest);
            }
            if(params.get("branchId") == null || "".equals(params.get("branchId"))){
                apiRest.setIsSuccess(false);
                apiRest.setError("参数branchId不能为空");
                return BeanUtils.toJsonStr(apiRest);
            }
            apiRest = reportService.vipStatementReport(params);
        }catch (Exception e){
            apiRest.setIsSuccess(false);
            apiRest.setError(e.getMessage());
            LogUtil.logError(e,params);
        }
        return BeanUtils.toJsonStr(apiRest);
    }

    /**
     *  机构销售日报
     * */
    @RequestMapping("/branchSaleDaily")
    public String branchSaleDaily(){
        ApiRest apiRest = new ApiRest();
        Map<String, String> params = AppHandler.params();
        try{
            if(params.get("tenantId") == null || "".equals(params.get("tenantId"))){
                apiRest.setIsSuccess(false);
                apiRest.setError("参数tenantId不能为空");
                return BeanUtils.toJsonStr(apiRest);
            }
            if(params.get("startDate") == null || "".equals(params.get("startDate"))){
                apiRest.setIsSuccess(false);
                apiRest.setError("参数startDate不能为空");
                return BeanUtils.toJsonStr(apiRest);
            }
            if(params.get("endDate") == null || "".equals(params.get("endDate"))){
                apiRest.setIsSuccess(false);
                apiRest.setError("参数endDate不能为空");
                return BeanUtils.toJsonStr(apiRest);
            }
            apiRest = reportService.branchSaleDaily(params);
        }catch(Exception e){
            apiRest.setIsSuccess(false);
            apiRest.setError(e.getMessage());
            LogUtil.logError(e, params);
        }
        return BeanUtils.toJsonStr(apiRest);
    }

    /**
     * 机构区间销售日报
     * */
    @RequestMapping("branchIntervalDaily")
    public String branchIntervalDaily(){
        ApiRest apiRest = new ApiRest();
        Map<String, String> params = AppHandler.params();
        try{
            if(params.get("tenantId") == null || "".equals(params.get("tenantId"))){
                apiRest.setIsSuccess(false);
                apiRest.setError("参数tenantId不能为空");
                return BeanUtils.toJsonStr(apiRest);
            }
            if(params.get("startDate") == null || "".equals(params.get("startDate"))){
                apiRest.setIsSuccess(false);
                apiRest.setError("参数startDate不能为空");
                return BeanUtils.toJsonStr(apiRest);
            }
            if(params.get("endDate") == null || "".equals(params.get("endDate"))){
                apiRest.setIsSuccess(false);
                apiRest.setError("参数endDate不能为空");
                return BeanUtils.toJsonStr(apiRest);
            }
            apiRest = reportService.branchIntervalDaily(params);
        }catch (Exception e){
            apiRest.setIsSuccess(false);
            apiRest.setError(e.getMessage());
            LogUtil.logError(e, params);
        }
        return BeanUtils.toJsonStr(apiRest);
    }

    @RequestMapping("/testAutoPrint")
    public List testAutoPrint(){
        List list;
        Map params = new HashMap();
        params.put("startDate", "2018-07-01 00:00:00");
        params.put("endDate", "2018-07-31 23:59:59");
        params.put("tenantId", "201005");
        params.put("branchId", "1121");
        params.put("offset", "0");
        params.put("rows", "10");
        params.put("saleType", "0");
        list = reportService.testAutoPrint(params);
        return list;
    }

    /**
     * 获取流水，三表一起返回
     * @return
     */
    @RequestMapping("/getSaleInfo")
    public String getSaleInfo(){
        ApiRest apiRest = new ApiRest();
        Map<String, String> params = AppHandler.params();
        try{
            if(params.get("tenantId") == null || "".equals(params.get("tenantId"))){
                apiRest.setIsSuccess(false);
                apiRest.setError("参数tenantId不能为空");
                return BeanUtils.toJsonStr(apiRest);
            }
            if(params.get("branchId") == null || "".equals(params.get("branchId"))){
                apiRest.setIsSuccess(false);
                apiRest.setError("参数branchId不能为空");
                return BeanUtils.toJsonStr(apiRest);
            }
            if(params.get("saleCode") == null || "".equals(params.get("saleCode"))){
                apiRest.setIsSuccess(false);
                apiRest.setError("参数saleCode不能为空");
                return BeanUtils.toJsonStr(apiRest);
            }
            apiRest = reportService.getSaleInfo(params);
        }
        catch(Exception e){
            apiRest.setIsSuccess(false);
            apiRest.setError(e.getMessage());
            LogUtil.logError(e, params);
        }
        return BeanUtils.toJsonStr(apiRest);
    }
}
