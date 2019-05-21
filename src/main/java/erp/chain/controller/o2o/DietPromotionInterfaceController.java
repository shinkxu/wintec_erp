package erp.chain.controller.o2o;

import com.saas.common.ResultJSON;
import com.saas.common.util.LogUtil;
import erp.chain.domain.Branch;
import erp.chain.domain.DietPromotion;
import erp.chain.domain.o2o.vo.*;
import erp.chain.service.BranchService;
import erp.chain.service.o2o.CardCouponsService;
import erp.chain.service.o2o.DietPromotionService;
import erp.chain.utils.AppHandler;
import erp.chain.utils.Args;
import erp.chain.utils.BeanUtils;
import erp.chain.utils.GsonUntil;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import saas.api.common.ApiRest;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by songzhiqiang on 2017/1/21.
 */
@Controller
@ResponseBody
@RequestMapping(value = {"/dietPromotionInterface","/frontTenantEatOrderTakeout"})
public class DietPromotionInterfaceController {
    @Autowired
    DietPromotionService dietPromotionService;
    @Autowired
    BranchService branchService;
    @Autowired
    CardCouponsService cardCouponsService;
    /**
     * 新增买赠促销接口
     */
    @RequestMapping("/addBuyGive")
    public ResultJSON addBuyGive(){
        ResultJSON resultJSON = new ResultJSON();
        Map<String,String> params = AppHandler.params();
        try {
            String[] forCustomerType = new String[3];
            forCustomerType[0] = "0";
            forCustomerType[1] = "1";
            forCustomerType[2] = "2";
            String branchIds = StringUtils.trimToNull(params.get("branchIds"));
            Args.isInteger(params.get("tenantId"),"商户id必须为整数");
            Args.isInteger(params.get("myBranchId"), "机构id必须为整数");
            Args.notNull(branchIds, "branchIds不能为空");
            params.put("promotionType", "4");
            Args.isIn(params.get("forCustomerType"), forCustomerType, "请选择适用对象");
            Args.isTrue("0".equals(params.get("promotionStatus")),"promotionStatus必须为0");
            Args.isIn(params.get("scope"), forCustomerType, "请选择活动范围");
            Args.notNull(params.get("startDate"),"开始日期不能为空");
            Args.notNull(params.get("endDate"),"结束日期不能为空");
            Args.notNull(params.get("weekInsert"),"weekInsert不能为空");
            Args.notNull(params.get("dietPromotionJson"),"dietPromotionJson不能为空");
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            if (params.get("startDate") != null && params.get("endDate") != null) {
//                Date startTime = simpleDateFormat.parse(params.get("startDate") + ":00");
//                Date endTime = simpleDateFormat.parse(params.get("endDate") + ":00");
                Date startTime = simpleDateFormat.parse(params.get("startDate") + " 00:00:00");
                Date endTime = simpleDateFormat.parse(params.get("endDate") + " 23:59:59");
                if (startTime.after(endTime) && !(startTime.equals(endTime))) {
                    resultJSON.setSuccess("1");
                    resultJSON.setMsg("起始日期大于结束日期，请重新输入");
                } else if (endTime.before(simpleDateFormat.parse(simpleDateFormat.format(new Date())))) {
                    resultJSON.setSuccess("1");
                    resultJSON.setMsg("结束日期小于当前日期，请重新输入");
                } else {
//                    params.put("start",params.get("startDate") + ":00");
//                    params.put("end",params.get("endDate") + ":00");
                    params.put("start",params.get("startDate"));
                    params.put("end",params.get("endDate"));
                    params.remove("startDate");
                    params.remove("endDate");
                    DietPromotion dietPromotion = new DietPromotion(params);
                    List<DietPromotionBuyGiveVo> dietPromotionBuyGiveVos = GsonUntil.jsonAsList(params.get("dietPromotionJson"),DietPromotionBuyGiveVo.class);
                    resultJSON = dietPromotionService.addPromotion(params, dietPromotion, dietPromotionBuyGiveVos);
                }
            } else {
                resultJSON.setSuccess("1");
                resultJSON.setMsg("请输入开始时间和结束时间");
                return resultJSON;
            }
        } catch(Exception e) {
            LogUtil.logError(e, params);
            resultJSON.setSuccess("1");
            resultJSON.setMsg(e.getMessage());
        }
        return resultJSON;
    }
    /**
     * 新增单品促销接口
     */
    @RequestMapping("/addSpecial")
    public ResultJSON addSpecial(){
        ResultJSON resultJSON = new ResultJSON();
        Map<String,String> params = AppHandler.params();
        try {
            String[] forCustomerType = new String[3];
            forCustomerType[0] = "0";
            forCustomerType[1] = "1";
            forCustomerType[2] = "2";
            String branchIds = StringUtils.trimToNull(params.get("branchIds"));
            Args.isInteger(params.get("tenantId"),"商户id必须为整数");
            Args.isInteger(params.get("myBranchId"), "机构id必须为整数");
            Args.notNull(branchIds, "branchIds不能为空");
            params.put("promotionType","11");
            Args.isIn(params.get("forCustomerType"),forCustomerType,"请选择适用对象");
            Args.isTrue("0".equals(params.get("promotionStatus")),"promotionStatus必须为0");
            Args.isIn(params.get("scope"),forCustomerType,"请选择活动范围");
            Args.notNull(params.get("startDate"),"开始日期不能为空");
            Args.notNull(params.get("endDate"),"结束日期不能为空");
            Args.notNull(params.get("weekInsert"),"weekInsert不能为空");
            Args.notNull(params.get("dietPromotionJson"),"dietPromotionJson不能为空");
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            if (params.get("startDate") != null && params.get("endDate") != null) {
                Date startTime = simpleDateFormat.parse(params.get("startDate") + " 00:00:00");
                Date endTime = simpleDateFormat.parse(params.get("endDate") + " 23:59:59");
                if (startTime.after(endTime) && !(startTime.equals(endTime))) {
                    resultJSON.setSuccess("1");
                    resultJSON.setMsg("起始日期大于结束日期，请重新输入");
                } else if (endTime.before(simpleDateFormat.parse(simpleDateFormat.format(new Date())))) {
                    resultJSON.setSuccess("1");
                    resultJSON.setMsg("结束日期小于当前日期，请重新输入");
                } else {
                    params.put("start",params.get("startDate"));
                    params.put("end",params.get("endDate"));
                    params.remove("startDate");
                    params.remove("endDate");
                    DietPromotion dietPromotion = new DietPromotion(params);
                    List<DietPromotionSpecialVo> dietPromotionSpecialVos = GsonUntil.jsonAsList(params.get("dietPromotionJson"),DietPromotionSpecialVo.class);
                    resultJSON = dietPromotionService.addPromotion(params, dietPromotion, dietPromotionSpecialVos);
                }
            } else {
                resultJSON.setSuccess("1");
                resultJSON.setMsg("请输入开始时间和结束时间");
                return resultJSON;
            }
        } catch(Exception e) {
            LogUtil.logError(e, params);
            resultJSON.setSuccess("1");
            resultJSON.setMsg(e.getMessage());
        }
        return resultJSON;
    }

    /**
     * 再买优惠接口
     */
    @RequestMapping("/addDisCount")
    public ResultJSON addDisCount(){
        ResultJSON resultJSON = new ResultJSON();
        Map<String,String> params = AppHandler.params();
        try {
            String[] forCustomerType = new String[3];
            forCustomerType[0] = "0";
            forCustomerType[1] = "1";
            forCustomerType[2] = "2";
            String branchIds = StringUtils.trimToNull(params.get("branchIds"));
            Args.isInteger(params.get("tenantId"),"商户id必须为整数");
            Args.isInteger(params.get("myBranchId"), "机构id必须为整数");
            Args.notNull(branchIds, "branchIds不能为空");
            params.put("promotionType", "6");
            Args.isIn(params.get("forCustomerType"),forCustomerType,"请选择适用对象");
            Args.isTrue("0".equals(params.get("promotionStatus")),"promotionStatus必须为0");
            Args.isIn(params.get("scope"),forCustomerType,"请选择活动范围");
            Args.notNull(params.get("startDate"),"开始日期不能为空");
            Args.notNull(params.get("endDate"),"结束日期不能为空");
            Args.notNull(params.get("weekInsert"),"weekInsert不能为空");
            Args.notNull(params.get("dietPromotionJson"),"dietPromotionJson不能为空");
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            if (params.get("startDate") != null && params.get("endDate") != null) {
                Date startTime = simpleDateFormat.parse(params.get("startDate") + " 00:00:00");
                Date endTime = simpleDateFormat.parse(params.get("endDate") + " 23:59:59");
                if (startTime.after(endTime) && !(startTime.equals(endTime))) {
                    resultJSON.setSuccess("1");
                    resultJSON.setMsg("起始日期大于结束日期，请重新输入");
                } else if (endTime.before(simpleDateFormat.parse(simpleDateFormat.format(new Date())))) {
                    resultJSON.setSuccess("1");
                    resultJSON.setMsg("结束日期小于当前日期，请重新输入");
                } else {
                    params.put("start",params.get("startDate"));
                    params.put("end",params.get("endDate"));
                    params.remove("startDate");
                    params.remove("endDate");
                    DietPromotion dietPromotion = new DietPromotion(params);
                    List<DietPromotionDiscountVo> dietPromotionDiscountVos = GsonUntil.jsonAsList(params.get("dietPromotionJson"),DietPromotionDiscountVo.class);
                    resultJSON = dietPromotionService.addPromotion(params, dietPromotion, dietPromotionDiscountVos);
                }
            } else {
                resultJSON.setSuccess("1");
                resultJSON.setMsg("请输入开始时间和结束时间");
                return resultJSON;
            }
        } catch(Exception e) {
            LogUtil.logError(e, params);
            resultJSON.setSuccess("1");
            resultJSON.setMsg(e.getMessage());
        }
        return resultJSON;
    }
    /**
     * 新增满减促销活动
     */
    @RequestMapping("/addSatisfyReduce")
    public ResultJSON addSatisfyReduce(){
        ResultJSON resultJSON = new ResultJSON();
        Map<String,String> params = AppHandler.params();
        try {
            String[] forCustomerType = new String[3];
            forCustomerType[0] = "0";
            forCustomerType[1] = "1";
            forCustomerType[2] = "2";
            String branchIds = StringUtils.trimToNull(params.get("branchIds"));
            Args.isInteger(params.get("tenantId"),"商户id必须为整数");
            Args.isInteger(params.get("myBranchId"), "机构id必须为整数");
            Args.notNull(branchIds, "branchIds不能为空");
            params.put("promotionType", "7");
            Args.isIn(params.get("forCustomerType"),forCustomerType,"请选择适用对象");
            Args.isTrue("0".equals(params.get("promotionStatus")),"promotionStatus必须为0");
            Args.isIn(params.get("scope"),forCustomerType,"请选择活动范围");
            Args.notNull(params.get("startDate"),"开始日期不能为空");
            Args.notNull(params.get("endDate"),"结束日期不能为空");
            Args.notNull(params.get("weekInsert"),"weekInsert不能为空");
            Args.notNull(params.get("dietPromotionJson"),"dietPromotionJson不能为空");
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            if (params.get("startDate") != null && params.get("endDate") != null) {
                Date startTime = simpleDateFormat.parse(params.get("startDate") + " 00:00:00");
                Date endTime = simpleDateFormat.parse(params.get("endDate") + " 23:59:59");
                if (startTime.after(endTime) && !(startTime.equals(endTime))) {
                    resultJSON.setSuccess("1");
                    resultJSON.setMsg("起始日期大于结束日期，请重新输入");
                } else if (endTime.before(simpleDateFormat.parse(simpleDateFormat.format(new Date())))) {
                    resultJSON.setSuccess("1");
                    resultJSON.setMsg("结束日期小于当前日期，请重新输入");
                } else {
                    params.put("start",params.get("startDate"));
                    params.put("end",params.get("endDate"));
                    params.remove("startDate");
                    params.remove("endDate");
                    DietPromotion dietPromotion = new DietPromotion(params);
                    List<DietPromotionSatisfyReduceVo> dietPromotionSatisfyReduceVos = GsonUntil.jsonAsList(params.get("dietPromotionJson"),DietPromotionSatisfyReduceVo.class);
                    resultJSON = dietPromotionService.addPromotion(params, dietPromotion, dietPromotionSatisfyReduceVos);
                }
            } else {
                resultJSON.setSuccess("1");
                resultJSON.setMsg("请输入开始时间和结束时间");
                return resultJSON;
            }
        } catch(Exception e) {
            LogUtil.logError(e, params);
            resultJSON.setSuccess("1");
            resultJSON.setMsg(e.getMessage());
        }
        return resultJSON;
    }
    /**
     * 新增卡券活动
     */
    @RequestMapping("/addCardCouponsActivity")
    public ResultJSON addCardCouponsActivity(){
        ResultJSON resultJSON = new ResultJSON();
        Map<String,String> params = AppHandler.params();
        try {
            String[] forCustomerType = new String[3];
            forCustomerType[0] = "0";
            forCustomerType[1] = "1";
            forCustomerType[2] = "2";
            String branchIds = StringUtils.trimToNull(params.get("branchIds"));
            Args.isInteger(params.get("tenantId"), "商户id必须为整数");
            Args.isInteger(params.get("myBranchId"), "机构id必须为整数");
            Args.notNull(branchIds, "branchIds不能为空");
            params.put("promotionType", "8");
            Args.isTrue("8".equals(params.get("promotionType")), "promotionType必须为8");
            Args.isTrue("0".equals(params.get("promotionStatus")), "promotionStatus必须为0");
            Args.notNull(params.get("startDate"), "开始日期不能为空");
            Args.notNull(params.get("endDate"), "结束日期不能为空");
            Args.notNull(params.get("activityName"),"activityName不能为空");
            Args.notNull(params.get("weekInsert"),"weekInsert不能为空");
            Args.notNull(params.get("cardId"),"cardId不能为空");
            Args.notNull(params.get("amount"),"amount不能为空");
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            if (params.get("startDate") != null && params.get("endDate") != null) {
                Date startTime = simpleDateFormat.parse(params.get("startDate")+" 00:00:00");
                Date endTime = simpleDateFormat.parse(params.get("endDate")+" 23:59:59");
                if (startTime.after(endTime) && !(startTime.equals(endTime))) {
                    resultJSON.setSuccess("1");
                    resultJSON.setMsg("起始日期大于结束日期，请重新输入");
                } else if (endTime.before(simpleDateFormat.parse(simpleDateFormat.format(new Date())))) {
                    resultJSON.setSuccess("1");
                    resultJSON.setMsg("结束日期小于当前日期，请重新输入");
                } else {
                    resultJSON = dietPromotionService.addCardCouponsActivity(params);
                }
            } else {
                resultJSON.setSuccess("1");
                resultJSON.setMsg("请输入开始时间和结束时间");
                return resultJSON;
            }
        } catch(Exception e) {
            LogUtil.logError(e, params);
            resultJSON.setSuccess("1");
            resultJSON.setMsg(e.getMessage());
        }
        return resultJSON;
    }

    /**
     * 新增支付促销活动
     */
    @RequestMapping("/addPayPreferential")
    public ResultJSON addPayPreferential(){
        ResultJSON resultJSON = new ResultJSON();
        Map<String,String> params = AppHandler.params();
        try {
            String[] forCustomerType = new String[4];
            forCustomerType[0] = "0";
            forCustomerType[1] = "1";
            forCustomerType[2] = "2";
            forCustomerType[3] = "3";
            String[] scopes = new String[3];
            scopes[0] = "0";
            scopes[1] = "1";
            scopes[2] = "2";
            String branchIds = StringUtils.trimToNull(params.get("branchIds"));
            Args.isInteger(params.get("tenantId"), "商户id必须为整数");
            Args.notNull(branchIds, "branchIds不能为空");
            params.put("promotionType", "13");
            Args.isIn(params.get("forCustomerType"), forCustomerType, "请选择适用对象");
            if ("3".equals(params.get("forCustomerType"))){
                Args.isInteger(params.get("vipType"),"会员类型id必须为整数");
            }

            Args.isTrue("0".equals(params.get("promotionStatus")), "promotionStatus必须为0");
            Args.isIn(params.get("scope"), scopes, "请选择适应范围");
            Args.notNull(params.get("startDate"),"开始日期不能为空");
            Args.notNull(params.get("endDate"),"结束日期不能为空");

            Args.notNull(params.get("weekInsert"),"weekInsert不能为空");
            Args.isInteger(params.get("myBranchId"), "机构id必须为整数");
            Args.notNull(params.get("forCustomerCondition"),"forCustomerCondition不能为空");
            Args.notNull(params.get("payType"),"payType不能为空");
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            if (params.get("startDate") != null && params.get("endDate") != null) {
                Date startTime = simpleDateFormat.parse(params.get("startDate") + " 00:00:00");
                Date endTime = simpleDateFormat.parse(params.get("endDate") + " 23:59:59");
                if (startTime.after(endTime) && !(startTime.equals(endTime))) {
                    resultJSON.setSuccess("1");
                    resultJSON.setMsg("起始日期大于结束日期，请重新输入");
                } else if (endTime.before(simpleDateFormat.parse(simpleDateFormat.format(new Date())))) {
                    resultJSON.setSuccess("1");
                    resultJSON.setMsg("结束日期小于当前日期，请重新输入");
                } else {
                    //resultJSON = dietPromotionService.addCardCouponsActivity(params);
                    params.put("start",params.get("startDate"));
                    params.put("end",params.get("endDate"));
                    params.remove("startDate");
                    params.remove("endDate");
                    DietPromotion dietPromotion = new DietPromotion(params);
                    resultJSON = dietPromotionService.addPayPreferential(params, dietPromotion);
                }
            } else {
                resultJSON.setSuccess("1");
                resultJSON.setMsg("请输入开始时间和结束时间");
                return resultJSON;
            }
        } catch(Exception e) {
            LogUtil.logError(e, params);
            resultJSON.setSuccess("1");
            resultJSON.setMsg(e.getMessage());
        }
        return resultJSON;
    }

    /**
     * 新增卡券游戏促销活动
     */
    @RequestMapping("/addGame")
    public ResultJSON addGame(){
        ResultJSON resultJSON = new ResultJSON();
        Map<String,String> params = AppHandler.params();
        try {
            Args.isInteger(params.get("tenantId"),"商户id必须为整数");
            List<Branch> branches = branchService.findBranchByTenantId(params);
            String branchIds = "";
            for (int i = 0;i<branches.size();i++){
                branchIds += branches.get(i).getId() + ",";
            }
            branchIds = branchIds.substring(0,branchIds.length());
            params.put("branchIds", branchIds);
            Args.notNull(branchIds, "BranchIds不能为空");
            Args.isInteger(params.get("myBranchId"), "机构id必须为整数");
            Args.notNull(params.get("activityName"), "活动名称不能为空");
            params.put("promotionType", "12");
            Args.isInteger(params.get("amount"), "参与频率必须为整数");
            Args.notNull(params.get("startDate"),"开始日期不能为空");
            Args.notNull(params.get("endDate"),"结束日期不能为空");
            Args.notNull(params.get("weekInsert"),"weekInsert不能为空");
            //Args.isTrue(!("-- 无 --".equals(params.get("giftGoodsOne"))&&"-- 无 --".equals(params.get("giftGoodsTwo"))&&"-- 无 --".equals(params.get("giftGoodsThree"))),"至少填一种奖项");
            Args.isTrue(!(params.get("giftGoodsOne") == null && params.get("giftGoodsTwo") == null && params.get("giftGoodsThree") == null), "至少填一种奖项");
            if (params.get("giftGoodsOne") != null) {
                Args.isInteger(params.get("giftGoodsOne"),"giftGoodsOne必须为整数");
                Args.notNull(params.get("giveNameOne"),"奖项名称不能为空");
                Args.notNull(params.get("winRateOne"),"中奖率不能为空");
                Args.isInteger(params.get("numberOne"),"奖品数量不能为空");
            }
            if (params.get("giftGoodsTwo") != null) {
                Args.isInteger(params.get("giftGoodsTwo"),"giftGoodsTwo必须为整数");
                Args.notNull(params.get("giveNameTwo"),"奖项名称不能为空");
                Args.notNull(params.get("winRateTwo"),"中奖率不能为空");
                Args.isInteger(params.get("numberTwo"),"奖品数量不能为空");
            }
            if (params.get("giftGoodsThree") != null) {
                Args.isInteger(params.get("giftGoodsThree"),"giftGoodsThree必须为整数");
                Args.notNull(params.get("giveNameThree"),"奖项名称不能为空");
                Args.notNull(params.get("winRateThree"),"中奖率不能为空");
                Args.isInteger(params.get("numberThree"),"奖品数量不能为空");
            }
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            if (params.get("startDate") != null && params.get("endDate") != null) {
                Date startTime = simpleDateFormat.parse(params.get("startDate") + " 00:00:00");
                Date endTime = simpleDateFormat.parse(params.get("endDate") + " 23:59:59");
                if (startTime.after(endTime) && !(startTime.equals(endTime))) {
                    resultJSON.setSuccess("1");
                    resultJSON.setMsg("起始日期大于结束日期，请重新输入");
                } else if (endTime.before(simpleDateFormat.parse(simpleDateFormat.format(new Date())))) {
                    resultJSON.setSuccess("1");
                    resultJSON.setMsg("结束日期小于当前日期，请重新输入");
                } else {
                    params.put("start",params.get("startDate"));
                    params.put("end",params.get("endDate"));
                    params.remove("startDate");
                    params.remove("endDate");
                    DietPromotion dietPromotion = new DietPromotion(params);
                    resultJSON = dietPromotionService.addGame(params, dietPromotion);
                }
            } else {
                resultJSON.setSuccess("1");
                resultJSON.setMsg("请输入开始时间和结束时间");
                return resultJSON;
            }
        } catch(Exception e) {
            LogUtil.logError(e, params);
            resultJSON.setSuccess("1");
            resultJSON.setMsg(e.getMessage());
        }
        return resultJSON;
    }

    /**
     * 新增整单满减促销活动
     */
    @RequestMapping("/addTotalReduce")
    public ResultJSON addTotalReduce(){
        ResultJSON resultJSON = new ResultJSON();
        Map<String,String> params = AppHandler.params();
        try {
            String[] customerType = new String[3];
            customerType[0] = "0";
            customerType[1] = "1";
            customerType[2] = "2";
            String[] reduceType = new String[3];
            reduceType[0]="1";
            reduceType[1]="2";
            reduceType[2]="3";
            Args.isInteger(params.get("tenantId"), "商户id必须为整数");
            Args.notNull(params.get("branchIds"), "BranchIds不能为空");
            params.put("promotionType", "9");
            Args.isIn(params.get("forCustomerType"), customerType, "请选择适用对象");
            Args.isTrue("0".equals(params.get("promotionStatus")), "promotionStatus必须为0");
            Args.isIn(params.get("scope"), customerType, "请选择活动范围");
            Args.notNull(params.get("satisfy"), "买够金额不能为空");
            Args.isIn(params.get("reduceType"), reduceType, "reduceType只能是1、2、3");
            Args.notNull(params.get("startDate"),"开始日期不能为空");
            Args.notNull(params.get("endDate"), "结束日期不能为空");
            Args.notNull(params.get("weekInsert"),"weekInsert不能为空");
            Args.isInteger(params.get("myBranchId"),"机构id必须为整数");
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            if (params.get("startDate") != null && params.get("endDate") != null) {
                Date startTime = simpleDateFormat.parse(params.get("startDate") + " 00:00:00");
                Date endTime = simpleDateFormat.parse(params.get("endDate") + " 23:59:59");
                if (startTime.after(endTime) && !(startTime.equals(endTime))) {
                    resultJSON.setSuccess("1");
                    resultJSON.setMsg("起始日期大于结束日期，请重新输入");
                } else if (endTime.before(simpleDateFormat.parse(simpleDateFormat.format(new Date())))) {
                    resultJSON.setSuccess("1");
                    resultJSON.setMsg("结束日期小于当前日期，请重新输入");
                } else {
                    params.put("start",params.get("startDate"));
                    params.put("end",params.get("endDate"));
                    params.remove("startDate");
                    params.remove("endDate");
                    DietPromotion dietPromotion = new DietPromotion(params);
                    resultJSON = dietPromotionService.addCardVipActivityAllReduce(params, dietPromotion);
                }
            } else {
                resultJSON.setSuccess("1");
                resultJSON.setMsg("请输入开始时间和结束时间");
                return resultJSON;
            }
        } catch(Exception e) {
            LogUtil.logError(e, params);
            resultJSON.setSuccess("1");
            resultJSON.setMsg(e.getMessage());
        }
        return resultJSON;
    }

    /**
     * 根据tenantId查询卡券活动
     */
    @RequestMapping("/listFestival")
    public ResultJSON listFestival(){
        ResultJSON resultJSON = new ResultJSON();
        Map<String,String> params = AppHandler.params();
        try {
            Args.isInteger(params.get("tenantId"),"商户id必须为整数");
            resultJSON = dietPromotionService.listFestival(params);
        } catch(Exception e) {
            LogUtil.logError(e, params);
            resultJSON.setSuccess("1");
            resultJSON.setMsg(e.getMessage());
        }
        return resultJSON;
    }

    /**
     * 根据促销活动id查询促销活动
     */
    @RequestMapping("/findActivityByPromotionId")
    public ResultJSON findActivityByPromotionId(){
        ResultJSON resultJSON = new ResultJSON();
        Map<String,String> params = AppHandler.params();
        try {
            Args.isInteger(params.get("id"),"id必须为整数");
            resultJSON = dietPromotionService.findActivityByPromotionId(params);
        } catch(Exception e) {
            LogUtil.logError(e, params);
            resultJSON.setSuccess("1");
            resultJSON.setMsg(e.getMessage());
        }
        return resultJSON;
    }

    /**
     * 更新整单满减促销活动
     */
    @RequestMapping("/updateTotalReduce")
    public ResultJSON updateTotalReduce(){
        ResultJSON resultJSON = new ResultJSON();
        Map<String,String> params = AppHandler.params();
        try {
            String[] customerType = new String[3];
            customerType[0] = "0";
            customerType[1] = "1";
            customerType[2] = "2";
            String[] reduceType = new String[2];
            reduceType[0]="1";
            reduceType[1]="2";
            Args.isInteger(params.get("id"), "id必须为整数");
            Args.isInteger(params.get("tenantId"), "商户id必须为整数");
            Args.notNull(params.get("branchIds"), "BranchIds不能为空");
            params.put("promotionType", "9");
            Args.isIn(params.get("forCustomerType"),customerType,"请选择适用对象");
            Args.isTrue("0".equals(params.get("promotionStatus")),"promotionStatus必须为0");
            Args.isIn(params.get("scope"),customerType,"请选择活动范围");
            Args.notNull(params.get("satisfy"),"买够金额不能为空");
            Args.isIn(params.get("reduceType"),reduceType,"reduceType只能是1、2");
            Args.notNull(params.get("reduceValue"),"reduceValue不能为空");
            Args.notNull(params.get("startDate"),"开始日期不能为空");
            Args.notNull(params.get("endDate"),"结束日期不能为空");
            Args.notNull(params.get("weekInsert"),"weekInsert不能为空");
            Args.isInteger(params.get("myBranchId"),"机构id必须为整数");
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            if (params.get("startDate") != null && params.get("endDate") != null) {
                Date startTime = simpleDateFormat.parse(params.get("startDate") + " 00:00:00");
                Date endTime = simpleDateFormat.parse(params.get("endDate") + " 23:59:59");
                if (startTime.after(endTime) && !(startTime.equals(endTime))) {
                    resultJSON.setSuccess("1");
                    resultJSON.setMsg("起始日期大于结束日期，请重新输入");
                } else if (endTime.before(simpleDateFormat.parse(simpleDateFormat.format(new Date())))) {
                    resultJSON.setSuccess("1");
                    resultJSON.setMsg("结束日期小于当前日期，请重新输入");
                } else {
                    params.put("start",params.get("startDate"));
                    params.put("end",params.get("endDate"));
                    params.remove("startDate");
                    params.remove("endDate");
                    DietPromotion dietPromotion = new DietPromotion(params);
                    resultJSON = dietPromotionService.updateTotalReduce(params, dietPromotion);
                }
            } else {
                resultJSON.setSuccess("1");
                resultJSON.setMsg("请输入开始时间和结束时间");
                return resultJSON;
            }
        } catch(Exception e) {
            LogUtil.logError(e, params);
            resultJSON.setSuccess("1");
            resultJSON.setMsg(e.getMessage());
        }
        return resultJSON;
    }

    /**
     * 更新支付促销活动
     */
    @RequestMapping("/updatePayPreferential")
    public ResultJSON updatePayPreferential(){
        ResultJSON resultJSON = new ResultJSON();
        Map<String,String> params = AppHandler.params();
        try {
            String[] forCustomerType = new String[4];
            forCustomerType[0] = "0";
            forCustomerType[1] = "1";
            forCustomerType[2] = "2";
            forCustomerType[3] = "3";
            String[] scopes = new String[3];
            scopes[0] = "0";
            scopes[1] = "1";
            scopes[2] = "2";
            Args.isInteger(params.get("id"), "id必须为整数");
            String branchIds = StringUtils.trimToNull(params.get("branchIds"));
            Args.isInteger(params.get("tenantId"), "商户id必须为整数");
            Args.notNull(branchIds, "branchIds不能为空");
            params.put("promotionType", "13");
            Args.isIn(params.get("forCustomerType"), forCustomerType, "请选择适用对象");
            if ("3".equals(params.get("forCustomerType"))){
                Args.isInteger(params.get("vipType"),"会员类型id必须为整数");
            }

            Args.isTrue("0".equals(params.get("promotionStatus")), "promotionStatus必须为0");
            Args.isIn(params.get("scope"), scopes, "请选择适应范围");
            Args.notNull(params.get("startDate"), "开始日期不能为空");
            Args.notNull(params.get("endDate"),"结束日期不能为空");

            Args.notNull(params.get("weekInsert"),"weekInsert不能为空");
            Args.isInteger(params.get("myBranchId"), "机构id必须为整数");
            Args.notNull(params.get("forCustomerCondition"),"forCustomerCondition不能为空");
            Args.notNull(params.get("payType"), "payType不能为空");
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            if (params.get("startDate") != null && params.get("endDate") != null) {
                Date startTime = simpleDateFormat.parse(params.get("startDate") + " 00:00:00");
                Date endTime = simpleDateFormat.parse(params.get("endDate") + " 23:59:59");
                if (startTime.after(endTime) && !(startTime.equals(endTime))) {
                    resultJSON.setSuccess("1");
                    resultJSON.setMsg("起始日期大于结束日期，请重新输入");
                } else if (endTime.before(simpleDateFormat.parse(simpleDateFormat.format(new Date())))) {
                    resultJSON.setSuccess("1");
                    resultJSON.setMsg("结束日期小于当前日期，请重新输入");
                } else {
                    //resultJSON = dietPromotionService.addCardCouponsActivity(params);
                    params.put("start",params.get("startDate"));
                    params.put("end",params.get("endDate"));
                    params.remove("startDate");
                    params.remove("endDate");
                    DietPromotion dietPromotion = new DietPromotion(params);
                    resultJSON = dietPromotionService.updatePayPreferential(params, dietPromotion);
                }
            } else {
                resultJSON.setSuccess("1");
                resultJSON.setMsg("请输入开始时间和结束时间");
                return resultJSON;
            }
        } catch(Exception e) {
            LogUtil.logError(e, params);
            resultJSON.setSuccess("1");
            resultJSON.setMsg(e.getMessage());
        }
        return resultJSON;
    }

    /**
     * 修改单品促销接口
     */
    @RequestMapping("/updateSpecial")
    public ResultJSON updateSpecial(){
        ResultJSON resultJSON = new ResultJSON();
        Map<String,String> params = AppHandler.params();
        try {
            String[] forCustomerType = new String[3];
            forCustomerType[0] = "0";
            forCustomerType[1] = "1";
            forCustomerType[2] = "2";
            Args.isInteger(params.get("id"), "id必须为整数");
            String branchIds = StringUtils.trimToNull(params.get("branchIds"));
            Args.isInteger(params.get("tenantId"), "商户id必须为整数");
            Args.isInteger(params.get("myBranchId"), "机构id必须为整数");
            Args.notNull(branchIds, "branchIds不能为空");
            params.put("promotionType", "11");
            Args.isIn(params.get("forCustomerType"),forCustomerType,"请选择适用对象");
            Args.isTrue("0".equals(params.get("promotionStatus")), "promotionStatus必须为0");
            Args.isIn(params.get("scope"), forCustomerType, "请选择活动范围");
            Args.notNull(params.get("startDate"),"开始日期不能为空");
            Args.notNull(params.get("endDate"), "结束日期不能为空");
            Args.notNull(params.get("weekInsert"),"weekInsert不能为空");
            Args.notNull(params.get("dietPromotionJson"),"dietPromotionJson不能为空");
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            if (params.get("startDate") != null && params.get("endDate") != null) {
                Date startTime = simpleDateFormat.parse(params.get("startDate") + " 00:00:00");
                Date endTime = simpleDateFormat.parse(params.get("endDate") + " 23:59:59");
                if (startTime.after(endTime) && !(startTime.equals(endTime))) {
                    resultJSON.setSuccess("1");
                    resultJSON.setMsg("起始日期大于结束日期，请重新输入");
                } else if (endTime.before(simpleDateFormat.parse(simpleDateFormat.format(new Date())))) {
                    resultJSON.setSuccess("1");
                    resultJSON.setMsg("结束日期小于当前日期，请重新输入");
                } else {
                    params.put("start",params.get("startDate"));
                    params.put("end",params.get("endDate"));
                    params.remove("startDate");
                    params.remove("endDate");
                    DietPromotion dietPromotion = new DietPromotion(params);
                    List<DietPromotionSpecialVo> dietPromotionSpecialVos = GsonUntil.jsonAsList(params.get("dietPromotionJson"),DietPromotionSpecialVo.class);
                    resultJSON = dietPromotionService.updatePromotion(params, dietPromotion, dietPromotionSpecialVos);
                }
            } else {
                resultJSON.setSuccess("1");
                resultJSON.setMsg("请输入开始时间和结束时间");
                return resultJSON;
            }
        } catch(Exception e) {
            LogUtil.logError(e, params);
            resultJSON.setSuccess("1");
            resultJSON.setMsg(e.getMessage());
        }
        return resultJSON;
    }

    /**
     * 新增满减促销活动
     */
    @RequestMapping("/updateSatisfyReduce")
    public ResultJSON updateSatisfyReduce(){
        ResultJSON resultJSON = new ResultJSON();
        Map<String,String> params = AppHandler.params();
        try {
            String[] forCustomerType = new String[3];
            forCustomerType[0] = "0";
            forCustomerType[1] = "1";
            forCustomerType[2] = "2";
            Args.isInteger(params.get("id"), "id必须为整数");
            String branchIds = StringUtils.trimToNull(params.get("branchIds"));
            Args.isInteger(params.get("tenantId"),"商户id必须为整数");
            Args.isInteger(params.get("myBranchId"), "机构id必须为整数");
            Args.notNull(branchIds, "branchIds不能为空");
            params.put("promotionType", "7");
            Args.isIn(params.get("forCustomerType"),forCustomerType,"请选择适用对象");
            Args.isTrue("0".equals(params.get("promotionStatus")),"promotionStatus必须为0");
            Args.isIn(params.get("scope"),forCustomerType,"请选择活动范围");
            Args.notNull(params.get("startDate"),"startDate不能为空");
            Args.notNull(params.get("endDate"),"endDate不能为空");
            Args.notNull(params.get("weekInsert"),"weekInsert不能为空");
            Args.notNull(params.get("dietPromotionJson"),"dietPromotionJson不能为空");
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            if (params.get("startDate") != null && params.get("endDate") != null) {
                Date startTime = simpleDateFormat.parse(params.get("startDate") + " 00:00:00");
                Date endTime = simpleDateFormat.parse(params.get("endDate") + " 23:59:59");
                if (startTime.after(endTime) && !(startTime.equals(endTime))) {
                    resultJSON.setSuccess("1");
                    resultJSON.setMsg("起始日期大于结束日期，请重新输入");
                } else if (endTime.before(simpleDateFormat.parse(simpleDateFormat.format(new Date())))) {
                    resultJSON.setSuccess("1");
                    resultJSON.setMsg("结束日期小于当前日期，请重新输入");
                } else {
                    params.put("start",params.get("startDate"));
                    params.put("end",params.get("endDate"));
                    params.remove("startDate");
                    params.remove("endDate");
                    DietPromotion dietPromotion = new DietPromotion(params);
                    List<DietPromotionSatisfyReduceVo> dietPromotionSatisfyReduceVos = GsonUntil.jsonAsList(params.get("dietPromotionJson"),DietPromotionSatisfyReduceVo.class);
                    resultJSON = dietPromotionService.updatePromotion(params, dietPromotion, dietPromotionSatisfyReduceVos);
                }
            } else {
                resultJSON.setSuccess("1");
                resultJSON.setMsg("请输入开始时间和结束时间");
                return resultJSON;
            }
        } catch(Exception e) {
            LogUtil.logError(e, params);
            resultJSON.setSuccess("1");
            resultJSON.setMsg(e.getMessage());
        }
        return resultJSON;
    }

    /**
     * 修改卡券活动
     */
    @RequestMapping("/updateCardCouponsActivity")
    public ResultJSON updateCardCouponsActivity(){
        ResultJSON resultJSON = new ResultJSON();
        Map<String,String> params = AppHandler.params();
        try {
            /*String tenantId = params.get("tenantId");
            if(StringUtils.isBlank(tenantId)){
                resultJSON.setSuccess("1");
                resultJSON.setMsg("参数tenantId不能为空！");
                return resultJSON;
            }
            resultJSON = cardCouponsService.qryCardList(params);*/
            String[] forCustomerType = new String[3];
            forCustomerType[0] = "0";
            forCustomerType[1] = "1";
            forCustomerType[2] = "2";
            String branchIds = StringUtils.trimToNull(params.get("branchIds"));
            Args.isInteger(params.get("id"),"id必须为整数");
            Args.isInteger(params.get("tenantId"),"商户id必须为整数");
            Args.isInteger(params.get("myBranchId"), "机构id必须为整数");
            Args.notNull(branchIds, "branchIds不能为空");
            params.put("promotionType", "8");
            Args.isTrue("8".equals(params.get("promotionType")), "promotionType必须为8");
            Args.isTrue("0".equals(params.get("promotionStatus")), "promotionStatus必须为0");
            Args.notNull(params.get("startDate"), "startDate不能为空");
            Args.notNull(params.get("endDate"), "endDate不能为空");
            Args.notNull(params.get("activityName"),"activityName不能为空");
            Args.notNull(params.get("weekInsert"),"weekInsert不能为空");
            Args.notNull(params.get("cardId"),"cardId不能为空");
            Args.notNull(params.get("amount"),"amount不能为空");
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            if (params.get("startDate") != null && params.get("endDate") != null) {
                Date startTime = simpleDateFormat.parse(params.get("startDate")+" 00:00:00");
                Date endTime = simpleDateFormat.parse(params.get("endDate")+" 23:59:59");
                if (startTime.after(endTime) && !(startTime.equals(endTime))) {
                    resultJSON.setSuccess("1");
                    resultJSON.setMsg("起始日期大于结束日期，请重新输入");
                } else if (endTime.before(simpleDateFormat.parse(simpleDateFormat.format(new Date())))) {
                    resultJSON.setSuccess("1");
                    resultJSON.setMsg("结束日期小于当前日期，请重新输入");
                } else {
                    resultJSON = dietPromotionService.updateCardCouponsActivity(params);
                }
            } else {
                resultJSON.setSuccess("1");
                resultJSON.setMsg("请输入开始时间和结束时间");
                return resultJSON;
            }
        } catch(Exception e) {
            LogUtil.logError(e, params);
            resultJSON.setSuccess("1");
            resultJSON.setMsg(e.getMessage());
        }
        return resultJSON;
    }

    /**
     * 更新卡券游戏促销活动
     */
    @RequestMapping("/updateGame")
    public ResultJSON updateGame(){
        ResultJSON resultJSON = new ResultJSON();
        Map<String,String> params = AppHandler.params();
        try {
            Args.isInteger(params.get("tenantId"),"商户id必须为整数");
            List<Branch> branches = branchService.findBranchByTenantId(params);
            String branchIds = "";
            for (int i = 0;i<branches.size();i++){
                branchIds += branches.get(i).getId() + ",";
            }
            branchIds = branchIds.substring(0,branchIds.length());
            params.put("branchIds", branchIds);
            Args.notNull(branchIds, "BranchIds不能为空");
            Args.isInteger(params.get("myBranchId"), "机构id必须为整数");
            Args.notNull(params.get("activityName"), "活动名称不能为空");
            params.put("promotionType", "12");
            Args.isInteger(params.get("amount"), "参与频率必须为整数");
            Args.notNull(params.get("startDate"),"startDate不能为空");
            Args.notNull(params.get("endDate"),"endDate不能为空");
            Args.notNull(params.get("weekInsert"),"weekInsert不能为空");
            //Args.isTrue(!("-- 无 --".equals(params.get("giftGoodsOne"))&&"-- 无 --".equals(params.get("giftGoodsTwo"))&&"-- 无 --".equals(params.get("giftGoodsThree"))),"至少填一种奖项");
            Args.isTrue(!(params.get("giftGoodsOne") == null && params.get("giftGoodsTwo") == null && params.get("giftGoodsThree") == null), "至少填一种奖项");
            if (params.get("giftGoodsOne") != null) {
                Args.isInteger(params.get("giftGoodsOne"),"giftGoodsOne必须为整数");
                Args.notNull(params.get("giveNameOne"),"奖项名称不能为空");
                Args.notNull(params.get("winRateOne"),"中奖率不能为空");
                Args.isInteger(params.get("numberOne"),"奖品数量不能为空");
            }
            if (params.get("giftGoodsTwo") != null) {
                Args.isInteger(params.get("giftGoodsTwo"),"giftGoodsTwo必须为整数");
                Args.notNull(params.get("giveNameTwo"),"奖项名称不能为空");
                Args.notNull(params.get("winRateTwo"),"中奖率不能为空");
                Args.isInteger(params.get("numberTwo"),"奖品数量不能为空");
            }
            if (params.get("giftGoodsThree") != null) {
                Args.isInteger(params.get("giftGoodsThree"),"giftGoodsThree必须为整数");
                Args.notNull(params.get("giveNameThree"),"奖项名称不能为空");
                Args.notNull(params.get("winRateThree"),"中奖率不能为空");
                Args.isInteger(params.get("numberThree"),"奖品数量不能为空");
            }
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            if (params.get("startDate") != null && params.get("endDate") != null) {
                Date startTime = simpleDateFormat.parse(params.get("startDate") + " 00:00:00");
                Date endTime = simpleDateFormat.parse(params.get("endDate") + " 23:59:59");
                if (startTime.after(endTime) && !(startTime.equals(endTime))) {
                    resultJSON.setSuccess("1");
                    resultJSON.setMsg("起始日期大于结束日期，请重新输入");
                } else if (endTime.before(simpleDateFormat.parse(simpleDateFormat.format(new Date())))) {
                    resultJSON.setSuccess("1");
                    resultJSON.setMsg("结束日期小于当前日期，请重新输入");
                } else {
                    params.put("start",params.get("startDate"));
                    params.put("end",params.get("endDate"));
                    params.remove("startDate");
                    params.remove("endDate");
                    DietPromotion dietPromotion = new DietPromotion(params);
                    resultJSON = dietPromotionService.updateGame(params, dietPromotion);
                }
            } else {
                resultJSON.setSuccess("1");
                resultJSON.setMsg("请输入开始时间和结束时间");
                return resultJSON;
            }
        } catch(Exception e) {
            LogUtil.logError(e, params);
            resultJSON.setSuccess("1");
            resultJSON.setMsg(e.getMessage());
        }
        return resultJSON;
    }

    /**
     * 更新再买优惠接口
     */
    @RequestMapping("/updateDisCount")
    public ResultJSON updateDisCount(){
        ResultJSON resultJSON = new ResultJSON();
        Map<String,String> params = AppHandler.params();
        try {
            String[] forCustomerType = new String[3];
            forCustomerType[0] = "0";
            forCustomerType[1] = "1";
            forCustomerType[2] = "2";
            String branchIds = StringUtils.trimToNull(params.get("branchIds"));
            Args.isInteger(params.get("tenantId"), "商户id必须为整数");
            Args.isInteger(params.get("myBranchId"), "机构id必须为整数");
            Args.notNull(branchIds, "branchIds不能为空");
            params.put("promotionType", "6");
            Args.isIn(params.get("forCustomerType"), forCustomerType, "请选择适用对象");
            Args.isTrue("0".equals(params.get("promotionStatus")), "promotionStatus必须为0");
            Args.isIn(params.get("scope"), forCustomerType, "请选择活动范围");
            Args.notNull(params.get("startDate"),"startDate不能为空");
            Args.notNull(params.get("endDate"), "endDate不能为空");
            Args.notNull(params.get("weekInsert"),"weekInsert不能为空");
            Args.notNull(params.get("dietPromotionJson"),"dietPromotionJson不能为空");
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            if (params.get("startDate") != null && params.get("endDate") != null) {
                Date startTime = simpleDateFormat.parse(params.get("startDate") + " 00:00:00");
                Date endTime = simpleDateFormat.parse(params.get("endDate") + " 23:59:59");
                if (startTime.after(endTime) && !(startTime.equals(endTime))) {
                    resultJSON.setSuccess("1");
                    resultJSON.setMsg("起始日期大于结束日期，请重新输入");
                } else if (endTime.before(simpleDateFormat.parse(simpleDateFormat.format(new Date())))) {
                    resultJSON.setSuccess("1");
                    resultJSON.setMsg("结束日期小于当前日期，请重新输入");
                } else {
                    params.put("start",params.get("startDate"));
                    params.put("end",params.get("endDate"));
                    params.remove("startDate");
                    params.remove("endDate");
                    DietPromotion dietPromotion = new DietPromotion(params);
                    List<DietPromotionDiscountVo> dietPromotionDiscountVos = GsonUntil.jsonAsList(params.get("dietPromotionJson"),DietPromotionDiscountVo.class);
                    resultJSON = dietPromotionService.updatePromotion(params, dietPromotion, dietPromotionDiscountVos);
                }
            } else {
                resultJSON.setSuccess("1");
                resultJSON.setMsg("请输入开始时间和结束时间");
                return resultJSON;
            }
        } catch(Exception e) {
            LogUtil.logError(e, params);
            resultJSON.setSuccess("1");
            resultJSON.setMsg(e.getMessage());
        }
        return resultJSON;
    }

    /**
     * 更新买赠促销接口
     */
    @RequestMapping("/updateBuyGive")
    public ResultJSON updateBuyGive(){
        ResultJSON resultJSON = new ResultJSON();
        Map<String,String> params = AppHandler.params();
        try {
            /*String tenantId = params.get("tenantId");
            if(StringUtils.isBlank(tenantId)){
                resultJSON.setSuccess("1");
                resultJSON.setMsg("参数tenantId不能为空！");
                return resultJSON;
            }
            resultJSON = cardCouponsService.qryCardList(params);*/
            String[] forCustomerType = new String[3];
            forCustomerType[0] = "0";
            forCustomerType[1] = "1";
            forCustomerType[2] = "2";
            String branchIds = StringUtils.trimToNull(params.get("branchIds"));
            Args.isInteger(params.get("tenantId"),"商户id必须为整数");
            Args.isInteger(params.get("myBranchId"), "机构id必须为整数");
            Args.notNull(branchIds, "branchIds不能为空");
            params.put("promotionType", "4");
            Args.isIn(params.get("forCustomerType"), forCustomerType, "请选择适用对象");
            Args.isTrue("0".equals(params.get("promotionStatus")),"promotionStatus必须为0");
            Args.isIn(params.get("scope"), forCustomerType, "请选择活动范围");
            Args.notNull(params.get("startDate"),"startDate不能为空");
            Args.notNull(params.get("endDate"),"endDate不能为空");
            Args.notNull(params.get("weekInsert"),"weekInsert不能为空");
            Args.notNull(params.get("dietPromotionJson"),"dietPromotionJson不能为空");
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            if (params.get("startDate") != null && params.get("endDate") != null) {
                Date startTime = simpleDateFormat.parse(params.get("startDate") + " 00:00:00");
                Date endTime = simpleDateFormat.parse(params.get("endDate") + " 23:59:59");
                if (startTime.after(endTime) && !(startTime.equals(endTime))) {
                    resultJSON.setSuccess("1");
                    resultJSON.setMsg("起始日期大于结束日期，请重新输入");
                } else if (endTime.before(simpleDateFormat.parse(simpleDateFormat.format(new Date())))) {
                    resultJSON.setSuccess("1");
                    resultJSON.setMsg("结束日期小于当前日期，请重新输入");
                } else {
                    params.put("start",params.get("startDate"));
                    params.put("end",params.get("endDate"));
                    params.remove("startDate");
                    params.remove("endDate");
                    DietPromotion dietPromotion = new DietPromotion(params);
                    List<DietPromotionBuyGiveVo> dietPromotionBuyGiveVos = GsonUntil.jsonAsList(params.get("dietPromotionJson"),DietPromotionBuyGiveVo.class);
                    resultJSON = dietPromotionService.updatePromotion(params, dietPromotion, dietPromotionBuyGiveVos);
                }
            } else {
                resultJSON.setSuccess("1");
                resultJSON.setMsg("请输入开始时间和结束时间");
                return resultJSON;
            }
        } catch(Exception e) {
            LogUtil.logError(e, params);
            resultJSON.setSuccess("1");
            resultJSON.setMsg(e.getMessage());
        }
        return resultJSON;
    }
    @RequestMapping("/checkVipCodeForPos")
    public String checkVipCodeForPos(){
        ApiRest rest = new ApiRest();
        Map<String,String> params = AppHandler.params();
        try {
            LogUtil.logInfo("******pos验证条码开始********");
            rest = dietPromotionService.checkCodeWithPos(params);
            LogUtil.logInfo("******pos验证条码结果：" + rest.getMessage());
        } catch (Exception e){
            LogUtil.logError("******pos验证条码异常********");
            LogUtil.logError(e, params);
            rest.setIsSuccess(false);
            rest.setMessage("系统异常,验证失败");
            rest.setError("系统异常,验证失败");
        }

        return BeanUtils.toJsonStr(rest);
    }

    /**
     * 查询用户可用优惠券
     */
    @RequestMapping("/listCardsOfVip")
    public String listCardsOfVip(){
        ApiRest rest = new ApiRest();
        Map<String,String> params = AppHandler.params();
        try {
            rest = cardCouponsService.listCardsOfVip(params);
        }catch (Exception e) {
            LogUtil.logError(e, params);
            rest.setIsSuccess(false);
            rest.setMessage("卡券活动查询失败！");
        }
        return BeanUtils.toJsonStr(rest);
    }

    /**
     * 查询支付促销活动
     * @author szq
     */
    @RequestMapping("/listPayback")
    public String listPayback(){
        ApiRest rest = new ApiRest();
        Map<String,String> params = AppHandler.params();
        try {
            Args.isInteger(params.get("branchId"), "机构id应该为整数");
            rest = dietPromotionService.listPayBackTwo(params);
        } catch (Exception e){
            LogUtil.logError(e, params);
            rest.setIsSuccess(false);
        }
        return BeanUtils.toJsonStr(rest);
    }

    /**
     * 查询商户刮刮乐活动
     */
    @RequestMapping("/qryScratchCard")
    public String qryScratchCard(){
        ApiRest rest = new ApiRest();
        Map<String,String> params = AppHandler.params();
        try {
            rest = dietPromotionService.qryScratchCard(params);
        }catch (Exception e) {
            LogUtil.logError(e,params);
            rest.setIsSuccess(false);
            rest.setMessage("商户刮刮乐活动查询失败！");
        }
        return BeanUtils.toJsonStr(rest);
    }

    /**
     * 查询所有(节日)卡券活动列表
     */
    @RequestMapping("/listFestivals")
    public String listFestivals(){
        ApiRest rest = new ApiRest();
        Map<String,String> params = AppHandler.params();
        try {
            rest = cardCouponsService.listFestivals(params);
        }catch (Exception e) {
            LogUtil.logError(e,params);
            rest.setIsSuccess(false);
            rest.setMessage("卡券活动查询失败！");
        }
        return BeanUtils.toJsonStr(rest);
    }

    @RequestMapping("/listBranchsByFestivalId")
    public String listBranchsByFestivalId(){
        ApiRest rest = new ApiRest();
        Map<String,String> params = AppHandler.params();
        try {
            rest = cardCouponsService.listBranchsByFestivalId(params);
        }catch (Exception e) {
            LogUtil.logError(e,params);
            rest.setIsSuccess(false);
            rest.setMessage("卡券机构查询失败！");
        }
        return BeanUtils.toJsonStr(rest);
    }

    /**
     * 保存刮刮乐抽奖结果
     * @return
     */
    @RequestMapping("/saveScratchCardResult")
    public String saveScratchCardResult(){
        ApiRest rest = new ApiRest();
        Map<String,String> params = AppHandler.params();
        try {
            rest = dietPromotionService.saveScratchCardResult(params);
        }catch (Exception e) {
            LogUtil.logError(e,params);
            rest.setIsSuccess(false);
            rest.setMessage("商户刮刮乐活动查询失败！");
        }
        return BeanUtils.toJsonStr(rest);
    }

    /**
     *会员领取卡券接口
     */
    @RequestMapping("/sendCardToVip")
    public String sendCardToVip(){
        ApiRest rest = new ApiRest();
        Map<String,String> params = AppHandler.params();
        try {
            rest = dietPromotionService.sendCardToVip(params);
        }catch (Exception e) {
            LogUtil.logError(e,params);
            rest.setIsSuccess(false);
            rest.setMessage("卡券活动查询失败！");
        }
        return BeanUtils.toJsonStr(rest);
    }

    /**
     *会员领取卡券接口
     */
    @RequestMapping("/sendCardToVipForCt")
    public String sendCardToVipForCt(){
        ApiRest rest = new ApiRest();
        Map<String,String> params = AppHandler.params();
        try {
            rest = dietPromotionService.sendCardToVipForCt(params);
        }catch (Exception e) {
            LogUtil.logError(e,params);
            rest.setIsSuccess(false);
            rest.setMessage("卡券活动查询失败！");
        }
        return BeanUtils.toJsonStr(rest);
    }

    /**
     * 微餐厅卡券核销接口
     */
    @RequestMapping("/useCardForCt")
    public String useCardForCt(){
        ApiRest rest = new ApiRest();
        Map<String,String> params = AppHandler.params();
        try {
            rest = cardCouponsService.useCardForCt(params);
        }catch (Exception e) {
            LogUtil.logError(e,params);
            rest.setIsSuccess(false);
            rest.setMessage("卡券核销失败！");
        }
        return BeanUtils.toJsonStr(rest);
    }


    /**
     * 卡券回退接口
     */
    @RequestMapping("/backupCard")
    public String backupCard(){
        ApiRest rest = new ApiRest();
        Map<String,String> params = AppHandler.params();
        try {
            rest = cardCouponsService.backupCard(params);
        }catch (Exception e) {
            LogUtil.logError(e,params);
            rest.setIsSuccess(false);
            rest.setMessage("卡券回退失败！");
        }
        return BeanUtils.toJsonStr(rest);
    }

    /**
     * 精准营销(发送优惠券)
     */
    @RequestMapping("/sendVipCard")
    public String sendVipCard(){
        ApiRest rest = new ApiRest();
        Map<String,String> params = AppHandler.params();
        try {
            rest = cardCouponsService.sendVipCard(params);
        }catch (Exception e) {
            LogUtil.logError(e,params);
            rest.setIsSuccess(false);
            rest.setMessage("发送优惠券失败！");
        }
        return BeanUtils.toJsonStr(rest);
    }

    /**
     * 新增数量促销
     */
    @RequestMapping("/addAmount")
    public ResultJSON addAmount(){
        ResultJSON resultJSON = new ResultJSON();
        Map<String,String> params = AppHandler.params();
        try {
            String[] forCustomerType = new String[3];
            forCustomerType[0] = "0";
            forCustomerType[1] = "1";
            forCustomerType[2] = "2";
            String branchIds = StringUtils.trimToNull(params.get("branchIds"));
            Args.isInteger(params.get("tenantId"),"商户id必须为整数");
            Args.isInteger(params.get("myBranchId"), "机构id必须为整数");
            Args.notNull(branchIds, "branchIds不能为空");
            params.put("promotionType","14");
            Args.isIn(params.get("forCustomerType"),forCustomerType,"请选择适用对象");
            Args.isTrue("0".equals(params.get("promotionStatus")),"promotionStatus必须为0");
            Args.isIn(params.get("scope"),forCustomerType,"请选择活动范围");
            Args.notNull(params.get("startDate"),"开始日期不能为空");
            Args.notNull(params.get("endDate"),"结束日期不能为空");
            Args.notNull(params.get("weekInsert"),"weekInsert不能为空");
            Args.notNull(params.get("dietPromotionJson"),"dietPromotionJson不能为空");
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            if (params.get("startDate") != null && params.get("endDate") != null) {
                Date startTime = simpleDateFormat.parse(params.get("startDate") + " 00:00:00");
                Date endTime = simpleDateFormat.parse(params.get("endDate") + " 23:59:59");
                if (startTime.after(endTime) && !(startTime.equals(endTime))) {
                    resultJSON.setSuccess("1");
                    resultJSON.setMsg("起始日期大于结束日期，请重新输入");
                } else if (endTime.before(simpleDateFormat.parse(simpleDateFormat.format(new Date())))) {
                    resultJSON.setSuccess("1");
                    resultJSON.setMsg("结束日期小于当前日期，请重新输入");
                } else {
                    params.put("start",params.get("startDate"));
                    params.put("end",params.get("endDate"));
                    params.remove("startDate");
                    params.remove("endDate");
                    DietPromotion dietPromotion = new DietPromotion(params);
                    List<DietPromotionAmountVo> dietPromotionAmountVos = GsonUntil.jsonAsList(params.get("dietPromotionJson"),DietPromotionAmountVo.class);
                    resultJSON = dietPromotionService.addPromotion(params, dietPromotion, dietPromotionAmountVos);
                }
            } else {
                resultJSON.setSuccess("1");
                resultJSON.setMsg("请输入开始时间和结束时间");
                return resultJSON;
            }
        } catch(Exception e) {
            LogUtil.logError(e, params);
            resultJSON.setSuccess("1");
            resultJSON.setMsg(e.getMessage());
        }
        return resultJSON;
    }
}
