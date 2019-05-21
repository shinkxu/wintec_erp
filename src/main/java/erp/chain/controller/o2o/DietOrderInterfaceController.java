package erp.chain.controller.o2o;

import com.saas.common.ResultJSON;
import com.saas.common.exception.ServiceException;
import com.saas.common.util.LogUtil;
import erp.chain.common.Constants;
import erp.chain.domain.Goods;
import erp.chain.domain.o2o.DietOrderInfo;
import erp.chain.service.base.CategoryService;
import erp.chain.service.base.GoodsService;
import erp.chain.service.o2o.DietOrderInfoService;
import erp.chain.service.o2o.DietPromotionService;
import erp.chain.utils.*;
import org.apache.commons.lang.Validate;
import org.apache.commons.lang.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import saas.api.common.ApiRest;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 *
 * Created by wangms on 2017/1/21.
 */
@Controller
@ResponseBody
@RequestMapping(value = {"/dietOrderInterface"})
public class DietOrderInterfaceController {
    private static final String DIET_ORDER_INTERFACE_CONTROLLER_SIMPLE_NAME = "DietOrderInterfaceController";
    @Autowired
    private DietOrderInfoService dietOrderInfoService;
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private GoodsService goodsService;
    @Autowired
    private DietPromotionService dietPromotionService;
    /**
     * 根据tenantId分页查询订单
     * @params
     * 1.tenantId 商户id not null
     * 2.page 页码 not null
     * 3.rows 每页显示的信息数 not null
     * 4.branchType 机构类型 判断机构类型，分店时branchId不能为空
     * 5.branchId 当前机构id（分店时branchId不能为空）
     * 6.sign 是否有高级搜索 可以为空
     * 7.branch 机构（页面选择的机构，例如 ：全部）
     * 8.orderMode 订单类型 可以为空
     * 9.orderStatus 订单状态 可以为空
     * 10.payStatus 支付状态 可以为空
     * 11.orderResource 订单来源 可以为空
     * 12.orderCode 订单号 可以为空
     * 13.orderPerson 订餐人 可以为空
     * 14.orderPhone 订单电话 可以为空
     * 15.payWay 支付方式 可以为空
     * 16.startDate 开始时间 可以为空
     * 17.endDate 结束时间 可以为空
     * @author szq
     * @return data:map()
     */
    @RequestMapping("/listDietOrderInfo")
    public String listDietOrderInfo() {
        ApiRest rest = new ApiRest();
        Map<String,String> params= AppHandler.params();
        try {
            if (params.get("startDate") != null && !"".equals(params.get("startDate"))) {
                params.put("startDate",params.get("startDate")+" 00:00:00");
            }
            if (params.get("endDate") != null && !"".equals(params.get("endDate"))) {
                params.put("endDate",params.get("endDate")+" 23:59:59");
            }
            String isHead = "";
            if(params.get("tenantId") != null && params.get("page") != null && params.get("branchType") != null && params.get("rows") != null){
                if (params.get("branchType").equals("0")) {
                    isHead = "";
                } else {
                    if(params.get("branchId") != null){
                        isHead = params.get("branchId");
                    } else {
                        rest.setIsSuccess(false);
                        rest.setMessage("机构id不能为空。");
                        return BeanUtils.toJsonStr(rest);
                    }
                }
                rest.setData(dietOrderInfoService.listDietOrderInfo(params));
                rest.setMessage("订单查询成功");
                rest.setIsSuccess(true);
            } else {
                rest.setIsSuccess(false);
                rest.setMessage("tenantId、page、branchType、rows不能为空。");
            }
        } catch (Exception e) {
            LogUtil.logError(e, params);
            rest.setMessage(e.getMessage());
            rest.setIsSuccess(false);
        }
        return BeanUtils.toJsonStr(rest);
    }

    /**
     * 根据订单id验证要删除的订单是否是本机构的订单
     * @params
     * 1.id:要删除的促销活动订单id
     * 2.branchId：本机构id
     * @author szq
     */
    @RequestMapping("/verifyDietOrderInfo")
    public String verifyDietOrderInfo() {
        ApiRest rest = new ApiRest();
        Map<String,String> params= AppHandler.params();
        try {
            if(params.get("branchId") != null && params.get("id") != null){
                DietOrderInfo dietOrderInfo = dietOrderInfoService.findDietOrderInfoById(new BigInteger(params.get("id")));
                if (!new BigInteger(params.get("branchId")).equals(dietOrderInfo.getBranchId())) {
                    rest.setMessage("该促销活动订单不是本机构的促销活动订单");
                    rest.setIsSuccess(false);
                } else {
                    rest.setMessage("该促销活动订单是本机构的促销活动订单");
                    rest.setIsSuccess(true);
                }
            } else {
                rest.setMessage("branchId、id不能为空");
                rest.setIsSuccess(false);
            }

        } catch (Exception e) {
            LogUtil.logError(e, params);
            rest.setMessage(e.getMessage());
            rest.setIsSuccess(false);
        }
        return BeanUtils.toJsonStr(rest);
    }

    /**
     * 删除促销活动  不支持批量删除
     * @params
     * 1.id 促销活动id
     * @author szq
     */
    @RequestMapping("/deleteDietOrderInfo")
    public String deleteDietOrderInfo() {
        ApiRest rest = new ApiRest();
        Map<String,String> params= AppHandler.params();
        try {
            if(params.get("id") != null){
                rest = dietOrderInfoService.deleteDietOrderInfo(new BigInteger(params.get("id")));
            } else {
                rest.setMessage("id不能为空");
                rest.setIsSuccess(false);
            }
        } catch (Exception e) {
            LogUtil.logError(e, params);
            rest.setMessage(e.getMessage());
            rest.setIsSuccess(false);
        }
        return BeanUtils.toJsonStr(rest);
    }

    /**
     * 查询一级菜品种类
     * @params
     * 1.tenantId:商户id
     * 2.parentId;上一级id
     * @author szq
     * @return data:Category
     */
    @RequestMapping("/listCategoryOne")
    public String listCategoryOne() {
        ApiRest rest = new ApiRest();
        Map<String,String> params= AppHandler.params();
        try {
            if(params.get("tenantId") != null && params.get("parentId") != null){
                params.put("categoryType","0");
                rest.setData(categoryService.queryCatList(params));
                rest.setMessage("一级分类查询成功");
                rest.setIsSuccess(true);
            } else {
                rest.setMessage("tenantId、parentId不能为空");
                rest.setIsSuccess(false);
            }
        } catch (Exception e) {
            LogUtil.logError(e, params);
            rest.setMessage(e.getMessage());
            rest.setIsSuccess(false);
        }
        return BeanUtils.toJsonStr(rest);
    }

    /**
     * 查询二级菜品种类
     * @params
     * 1.tenantId:商户id
     * @author szq
     * @return data:list
     */
    @RequestMapping("/listCategoryTwo")
    public String listCategoryTwo() {
        ApiRest rest = new ApiRest();
        Map<String,String> params= AppHandler.params();
        try {
            if(params.get("tenantId") != null){
                params.put("categoryType", "0");

                rest.setData(categoryService.queryCatList(params));
                rest.setMessage("二级分类查询成功");
                rest.setIsSuccess(true);
            } else {
                rest.setMessage("tenantId不能为空");
                rest.setIsSuccess(false);
            }
        } catch (Exception e) {
            LogUtil.logError(e, params);
            rest.setMessage(e.getMessage());
            rest.setIsSuccess(false);
        }
        return BeanUtils.toJsonStr(rest);
    }

    /**
     * 查询菜品
     * @params
     * 1.tenantId
     * 2.branchId
     * @author szq
     * @return data:list<Goods>
     */
    @RequestMapping("/listGoods")
    public String listGoods() {
        ApiRest rest = new ApiRest();
        Map<String,String> params= AppHandler.params();
        try {
            if(params.get("tenantId") != null && params.get("branchId") != null){
                rest.setData(goodsService.queryGoodsList(params));
                rest.setMessage("查询成功");
                rest.setIsSuccess(true);
            } else {
                rest.setMessage("tenantId、branchId不能为空");
                rest.setIsSuccess(false);
            }
        } catch (Exception e) {
            LogUtil.logError(e, params);
            rest.setMessage(e.getMessage());
            rest.setIsSuccess(false);
        }
        return BeanUtils.toJsonStr(rest);
    }

    /**
     * 获取整单满减活动列表
     * @params
     * 1.tenantId
     * 2.branchId
     * @author szq
     * @return data:list<DietPromotionTotalReduce>
     */
    @RequestMapping("/listTotalReduceList")
    public String listTotalReduceList() {
        ApiRest rest = new ApiRest();
        Map<String,String> params= AppHandler.params();
        try {
            Args.isInteger(params.get("tenantId"), "商户id应该为整数");
            Args.isInteger(params.get("branchId"), "机构id应该为整数");
            rest.setData(dietPromotionService.listTotalReduceList(params));
            rest.setMessage("整单满减活动查询成功");
            rest.setIsSuccess(true);
        } catch (Exception e) {
            LogUtil.logError(e, params);
            rest.setIsSuccess(false);
        }
        return BeanUtils.toJsonStr(rest);
    }

    /**
     * 新增订单
     * @params
     * 1.tenantId:商户id
     * 2.branchId:开户机构id
     * 3.orderMode:外卖类型（2：外卖自取，4：外卖配送）
     * 4.mobilePhone：移动电话
     * 5.consignee：订餐人
     * 6.address：收货地址（orderMode为2的时候，address可以为空）
     * 7.remark：备注，可以为空
     * 8.deliveryTime:配送时间（orderMode为4的时候，deliveryTime不能为空）
     * 9.appointmentDate：到店自提的时间（orderMode为2的时候，appointmentDate不能为空）
     * 10.discountValue：打折
     * 11.goodsJson：选择的菜品集合
     * @author szq
     */
    @RequestMapping("/createOrder")
    public String createOrder() {
        ApiRest rest = new ApiRest();
        Map<String,String> params= AppHandler.params();
        try {
            if(params.get("tenantId") != null && params.get("branchId") != null){
                String goodsJson = params.get("goodsJson");
                List<Goods> goodsList = GsonUntil.jsonAsList(goodsJson, Goods.class);
                rest = dietOrderInfoService.createOrder(params, goodsList);
            } else {
                rest.setMessage("tenantId、branchId不能为空");
                rest.setIsSuccess(false);
            }
        } catch (Exception e) {
            LogUtil.logError(e, params);
            rest.setMessage(e.getMessage());
            rest.setIsSuccess(false);
        }
        return BeanUtils.toJsonStr(rest);
    }

    /**
     * 根据订单id查询订单详情
     * @params
     * 1.id 订单id
     * @author szq
     * @return data:DietOrderInfoShowVo
     */
    @RequestMapping("/showOrderDetail")
    public String showOrderDetail() {
        ApiRest rest = new ApiRest();
        Map<String,String> params= AppHandler.params();
        try {
            if(params.get("id") != null){
                rest.setData(dietOrderInfoService.showOrderDetail(new BigInteger(params.get("id"))));
                rest.setIsSuccess(true);
                rest.setMessage("订单详情查询成功");
            } else {
                rest.setMessage("tenantId、branchId不能为空");
                rest.setIsSuccess(false);
            }
        } catch (Exception e) {
            LogUtil.logError(e, params);
            rest.setMessage(e.getMessage());
            rest.setIsSuccess(false);
        }
        return BeanUtils.toJsonStr(rest);
    }

    /**
     * 修改订单
     * @params
     * 1.id：订单id
     * 2.name:订餐人名称
     * 3.address 地址
     * 4.phone 电话
     * 5.appointmentDate 预约时间
     * 6.allocationDate 配送时间
     * 7.remark 可以为空
     * @author szq
     */
    @RequestMapping("/editOrder")
    public String editOrder() {
        ApiRest rest = new ApiRest();
        Map<String,String> params= AppHandler.params();
        try {
            rest = dietOrderInfoService.editOrder(params);
        } catch (Exception e) {
            LogUtil.logError(e, params);
            rest.setMessage(e.getMessage());
            rest.setIsSuccess(false);
        }
        return BeanUtils.toJsonStr(rest);
    }

    /**
     * POS下单 POS的接口方法
     * @return
     */
    @RequestMapping("/saveTakeOutOrderByPosTel")
    public String saveTakeOutOrderByPosTel(){
        ResultJSON result = new ResultJSON();
        Map<String,String> params= AppHandler.params();
        try {
            ApiRest rest = dietOrderInfoService.saveTakeoutOrderByTelPOS(params);
            result.setIsSuccess(rest.getIsSuccess());
            result.setMsg(rest.getMessage());
        } catch (ServiceException se) {
            LogUtil.logError(se, params);
            result.setIsSuccess(false);
            result.setMsg("系统异常,保存失败.ERROR:"+se.getMessage());
        } catch (Exception e){
            LogUtil.logError(e, params);
            result.setIsSuccess(false);
            result.setMsg("系统异常,保存失败.ERROR:" + e.getMessage());
        }
        return BeanUtils.toJsonStr(result);
    }
    /**
     * POS的接口方法
     * POS 订单状态 修改 接口方法
     * 修改状态1：（下单 —> 已接单，提交厨房 ）
     * 修改状态2：（下单 —> 已拒绝）
     * 修改状态3：（已接单，提交厨房 —> 正在派送）
     * 修改状态4：（正在派送 —> 已派送）
     * 修改状态5：（已派送 —> 已完成（结束））
     */
    @RequestMapping("/changeOrderStatusByTelPOS")
    public String changeOrderStatusByTelPOS(){
        ResultJSON result = null;
        Map<String,String> params= AppHandler.params();
        try {
            result = dietOrderInfoService.changeOrderStatusByTelPOS(params);
        } catch (Exception e){
            LogUtil.logError(e, params);
            result = new ResultJSON();
            result.setSuccess("false");
            result.setMsg("系统异常,订单状态变更失败");
        }
        return BeanUtils.toJsonStr(result);
    }
    /**
     * POS的接口方法
     * POS 支付状态修改 接口方法
     * 修改状态1：（未支付 —> 已支付 ）
     */
    @RequestMapping("/changePayStatusByTelPOS")
    public String changePayStatusByTelPOS(){
        ResultJSON result = new ResultJSON();
        Map<String,String> params= AppHandler.params();
        try {
            result = dietOrderInfoService.changePayStatusByTelPOS(params);
        } catch (Exception e){
            LogUtil.logError(e, params);
            result.setSuccess("false");
            result.setMsg("系统异常,支付状态变更失败");
        }
        return BeanUtils.toJsonStr(result);
    }

    /**
     * 根据时间范围选择所有订单 POS的接口方法
     */
    @RequestMapping("/listOrderInfoByTelPOS")
    public String listOrderInfoByTelPOS(){
        ResultJSON result = new ResultJSON();
        Map<String,String> params= AppHandler.params();
        try {
            result = dietOrderInfoService.listOrderInfoByTelPOS(params);
        } catch (Exception e){
            LogUtil.logError(e,params);
            result.setSuccess("false");
            result.setMsg("系统异常,查询订单失败");
        }
        return BeanUtils.toJsonStr(result);
    }
    /**
     * 显示订单详情 POS的接口方法
     * @return
     */
    @RequestMapping("/showOrderDetailByTelPOS")
    public String showOrderDetailByTelPOS(){
        ResultJSON result = new ResultJSON();
        Map<String,String> params= AppHandler.params();
        try {
            String id = params.get("dietOrderInfoId");
            result.setObject(dietOrderInfoService.showOrderDetail(new BigInteger(id)));
        } catch (Exception e){
            result.setSuccess("false");
            result.setMsg("显示订单详情失败！");
            LogUtil.logError("显示订单详情:" + e.getMessage());
        }
        return BeanUtils.toJsonStr(result);
    }
    /**
     * pos订单加菜接口
     */
    @RequestMapping("/addOrderDetailForPos")
    public String addOrderDetailForPos(){
        ApiRest rest = new ApiRest();
        Map<String,String> params= AppHandler.params();
        try {
            rest = dietOrderInfoService.addOrderDetail(params);
        } catch (Exception e){
            LogUtil.logError("******pos订单加菜出现异常********");
            LogUtil.logError(e, params);
            rest.setIsSuccess(false);
            rest.setMessage("系统异常,添加失败");
        }
        return BeanUtils.toJsonStr(rest);
    }

    /**
     * pos用验证会员身份接口
     * @return
     */
    @RequestMapping("/checkVipCodeForPos")
    public String checkVipCodeForPos(){
        Map<String,String> params= AppHandler.params();
        ApiRest rest = new ApiRest();
        try {
            LogUtil.logInfo("******pos验证条码开始********");
            ResultJSON result = dietOrderInfoService.checkCodeWithPos(params);
            LogUtil.logInfo("******pos验证条码结果：" + result.getMsg());
            rest.setIsSuccess(result.getIsSuccess());
            rest.setMessage(result.getMsg());
        } catch (Exception e){
            LogUtil.logError("******pos验证条码异常********");
            LogUtil.logError(e, params);
            rest.setIsSuccess(false);
            rest.setMessage("系统异常,验证失败");
        }
        return BeanUtils.toJsonStr(rest);
    }

    @RequestMapping(value = "/listDietPromotionTotalReduce")
    public String listDietPromotionTotalReduce() {
        ApiRest apiRest = null;
        Map<String, String> requestParameters = ApplicationHandler.getRequestParameters();
        try {
            String tenantId = requestParameters.get("tenantId");
            Validate.notNull(tenantId, "参数(tenantId)不能为空！");

            String branchId = requestParameters.get("branchId");
            Validate.notNull(branchId, "参数(branchId)不能为空！");

            String scopes = requestParameters.get("scopes");
            Validate.notNull(scopes, "参数(scopes)不能为空！");

            String forCustomerTypes = requestParameters.get("forCustomerTypes");
            Validate.notNull(forCustomerTypes, "参数(forCustomerTypes)不能为空！");

            String[] scopeArray = scopes.split(",");
            List<Integer> scopeList = new ArrayList<Integer>();
            for (String scope : scopeArray) {
                scopeList.add(Integer.valueOf(scope));
            }

            String[] forCustomerTypeArray = forCustomerTypes.split(",");
            List<Integer> forCustomerTypeList = new ArrayList<Integer>();
            for (String forCustomerType : forCustomerTypeArray) {
                forCustomerTypeList.add(Integer.valueOf(forCustomerType));
            }
            apiRest = new ApiRest();
            apiRest.setData(dietPromotionService.listDietPromotionTotalReduce(BigInteger.valueOf(Long.valueOf(tenantId)), BigInteger.valueOf(Long.valueOf(branchId)), scopeList, forCustomerTypeList));
            apiRest.setMessage("整单满减活动查询成功！");
            apiRest.setIsSuccess(true);
        } catch (Exception e) {
            LogUtil.logError(e, requestParameters);
            apiRest = new ApiRest();
            apiRest.setError(e.getMessage());
            apiRest.setIsSuccess(false);
        }
        return BeanUtils.toJsonStr(apiRest);
    }

    @RequestMapping(value = "/obtainDietOrderInfos")
    @ResponseBody
    public String obtainDietOrderInfos() {
        ApiRest apiRest = null;
        Map<String, String> requestParameters = ApplicationHandler.getRequestParameters();
        try {
            String tenantId = requestParameters.get("tenantId");
            Validate.notNull(tenantId, "参数(tenantId)不能为空！");

            String branchId = requestParameters.get("branchId");
            Validate.notNull(branchId, "参数(branchId)不能为空！");

            String dietOrderInfoIds = requestParameters.get("dietOrderInfoIds");
            Validate.notNull(dietOrderInfoIds, "参数(dietOrderInfoIds)不能为空！");

            String[] array = dietOrderInfoIds.split(",");
            List<BigInteger> bigIntegerDietOrderInfoIds = new ArrayList<BigInteger>();
            for (String dietOrderInfoId : array) {
                bigIntegerDietOrderInfoIds.add(NumberUtils.createBigInteger(dietOrderInfoId));
            }

            apiRest = dietOrderInfoService.obtainDietOrderInfos(NumberUtils.createBigInteger(tenantId), NumberUtils.createBigInteger(branchId), bigIntegerDietOrderInfoIds);
        } catch (Exception e) {
            LogUtil.logError(String.format(Constants.LOGGER_ERROR_FORMAT, "查询店铺信息失败", DIET_ORDER_INTERFACE_CONTROLLER_SIMPLE_NAME, "obtainDietOrderInfos", e.getClass().getSimpleName(), e.getMessage(), requestParameters.toString()));
            apiRest = new ApiRest();
            apiRest.setIsSuccess(false);
            apiRest.setError(e.getMessage());
        }
        return BeanUtils.toJsonStr(apiRest);
    }
}
