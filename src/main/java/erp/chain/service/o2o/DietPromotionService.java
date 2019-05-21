package erp.chain.service.o2o;

import com.saas.common.ResultJSON;
import com.saas.common.exception.ServiceException;
import com.saas.common.util.Common;
import erp.chain.domain.*;
import erp.chain.domain.o2o.*;
import erp.chain.domain.o2o.vo.DietGameScratchCardVO;
import erp.chain.domain.o2o.vo.PromotionPaybackVo;
import erp.chain.domain.system.SysConfig;
import erp.chain.mapper.*;
import erp.chain.mapper.o2o.CardCouponsMapper;
import erp.chain.mapper.o2o.CardToVipMapper;
import erp.chain.mapper.o2o.VipMapper;
import erp.chain.service.system.TenantConfigService;
import erp.chain.utils.DateWeekContains;
import erp.chain.utils.GsonUntil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import saas.api.common.ApiRest;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by songzhiqiang on 2017/1/18.
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class DietPromotionService{
    @Autowired
    private DietPromotionMapper dietPromotionMapper;
    @Autowired
    private DietPromotionBranchRMapper dietPromotionBranchRMapper;
    @Autowired
    private DietPromotionGoodsRMapper dietPromotionGoodsRMapper;
    @Autowired
    private DietPromotionBuyGiveMapper dietPromotionBuyGiveMapper;
    @Autowired
    private DietPromotionSatisfyReduceMapper dietPromotionSatisfyReduceMapper;
    @Autowired
    private DietPromotionDiscountMapper dietPromotionDiscountMapper;
    @Autowired
    private DietPromotionTotalReduceMapper dietPromotionTotalReduceMapper;
    @Autowired
    private DietPromotionSpecialMapper dietPromotionSpecialMapper;
    @Autowired
    private DietPromotionAmountMapper dietPromotionAmountMapper;
    @Autowired
    private BranchMapper branchMapper;
    @Autowired
    private DietPromotionFestivalMapper dietPromotionFestivalMapper;
    @Autowired
    private DietPromotionPaybackMapper dietPromotionPaybackMapper;
    @Autowired
    private DietGameScratchCardMapper dietGameScratchCardMapper;
    @Autowired
    private DietGamePrizeItemMapper dietGamePrizeItemMapper;
    @Autowired
    private CardCouponsMapper cardCouponsMapper;
    @Autowired
    private GoodsMapper goodsMapper;
    @Autowired
    private DietOrderPromotionRMapper dietOrderPromotionRMapper;
    @Autowired
    private VipService vipService;
    @Autowired
    private VipMapper vipMapper;
    @Autowired
    private DietGameScratchVipMapper dietGameScratchVipMapper;
    @Autowired
    private DietGameScratchCardHistoryMapper dietGameScratchCardHistoryMapper;
    @Autowired
    private CardToVipMapper cardToVipMapper;
    @Autowired
    private TenantConfigService tenantConfigService;
    @Autowired
    private CardCouponsService cardCouponsService;

    /**
     * 查询促销活动列表
     */
    public ResultJSON queryDietpromotionList(Map params) throws ServiceException, ParseException{
        ResultJSON resultJSON = new ResultJSON();
        Map<String, Object> map = new HashMap<>();
        int startNum = 0;
        int endNum = 9999;
        if(params.get("page") != null && params.get("rows") != null && !"".equals(params.get("rows"))){
            startNum = (Integer.parseInt(params.get("page").toString()) - 1) * Integer.parseInt(params.get("rows").toString()) + 1;
            endNum = Integer.parseInt(params.get("rows").toString()) + startNum;

        }
        List<DietPromotion> list = dietPromotionMapper.dietPromotionList(params);
        List<Activity> activities = new ArrayList<>();
        SimpleDateFormat formatA = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat formatB = new SimpleDateFormat("HH:mm:ss");
        SimpleDateFormat formatC = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        int count = 0;
        for(int i = 0; i < list.size(); i++){
            DietPromotion dietPromotion = list.get(i);
            if(params.get("branchId") != null){
                if(!dietPromotion.getCreateBranchId().equals(new BigInteger(params.get("branchId").toString()))){
                    Map<String, Object> param1 = new HashMap<>();
                    param1.put("dietPromotionId", list.get(i).getId());
                    param1.put("branchId", params.get("branchId"));
                    List<DietPromotionBranchR> rList = dietPromotionBranchRMapper.dietPromotionBranchRList(param1);
                    if(!(rList != null && rList.size() > 0)){
                        continue;
                    }
                }
            }
            Activity activity = new Activity();
            activity.setEffectiveInterval(dietPromotion.getEffectiveInterval());
            activity.setId(dietPromotion.getId());
            activity.setTenantId(dietPromotion.getTenantId());
            activity.setPromotionCode(dietPromotion.getPromotionCode());
            activity.setPromotionName(dietPromotion.getPromotionName());
            activity.setPromotionType(dietPromotion.getPromotionType());
            activity.setStartDate(dietPromotion.getStartDate());
            activity.setEndDate(dietPromotion.getEndDate());
            activity.setStartTime(dietPromotion.getStartTime());
            activity.setEndTime(dietPromotion.getEndTime());
            activity.setApplyToMon(dietPromotion.getApplyToMon());
            activity.setApplyToTue(dietPromotion.getApplyToTue());
            activity.setApplyToWed(dietPromotion.getApplyToWed());
            activity.setApplyToThu(dietPromotion.getApplyToThu());
            activity.setApplyToFri(dietPromotion.getApplyToFri());
            activity.setApplyToSat(dietPromotion.getApplyToSat());
            activity.setApplyToSun(dietPromotion.getApplyToSun());
            activity.setScope(dietPromotion.getScope());
            activity.setForCustomerType(dietPromotion.getForCustomerType());
            activity.setMemGradeId(dietPromotion.getMemGradeId());
            activity.setMemo(dietPromotion.getMemo());
            activity.setPromotionStatus(dietPromotion.getPromotionStatus());
            activity.setUse(dietPromotion.getIsUse());
            activity.setSuperposition(dietPromotion.getIsSuperposition());
            activity.setCreateAt(dietPromotion.getCreateAt());
            activity.setCreateBy(dietPromotion.getCreateBy());
            activity.setAuditeAt(dietPromotion.getAuditeAt());
            activity.setAuditeBy(dietPromotion.getAuditeBy());
            activity.setLastUpdateBy(dietPromotion.getLastUpdateBy());
            activity.setLastUpdateAt(dietPromotion.getLastUpdateAt());
            activity.setDeleted(dietPromotion.isDeleted());
            //String activityTime = formatA.format(dietPromotion.getStartDate()) + " " + formatB.format(dietPromotion.getStartTime()) + "至" + formatA.format(dietPromotion.getEndDate()) + " " + formatB.format(dietPromotion.getEndTime());
            String activityTime = formatA.format(dietPromotion.getStartDate()) + " 至 " + formatA.format(dietPromotion.getEndDate());
            activity.setActivityTime(activityTime);
            String startStr = formatA.format(dietPromotion.getStartDate()) + " " + formatB.format(dietPromotion.getStartTime());
            String endStr = formatA.format(dietPromotion.getEndDate()) + " " + formatB.format(dietPromotion.getEndTime());
            Date startDate = formatC.parse(startStr);
            Date endDate = formatC.parse(endStr);
            Date nowDate = new Date();
            if(startDate.before(nowDate) && endDate.after(nowDate) && dietPromotion.getIsUse()){
                activity.setStatus(new BigInteger("1")); //执行中
                if("5".equals(params.get("marketingStatus")) || ("1".equals(params.get("marketingStatus")) || params.get("marketingStatus") == null)){
                    if(params.get("startDate") == null && params.get("endDate") == null){
                        count++;
                        if(count >= startNum && count < endNum){
                            activities.add(activity);
                        }
                    }
                    else{
                        Date startDateQuery = formatC.parse(params.get("startDate") + " 00:00:00");
                        Date endDateQuery = formatC.parse(params.get("endDate") + " 23:59:59");
                        if((startDateQuery.compareTo(startDate) <= 0 && endDateQuery.compareTo(startDate) >= 0) || (startDateQuery.compareTo(endDate) <= 0 && endDateQuery.compareTo(endDate) >= 0)){
                            count++;
                            if(count >= startNum && count < endNum){
                                activities.add(activity);
                            }
                        }
                    }
                }
            }
            else if(startDate.after(nowDate) && dietPromotion.getIsUse()){
                activity.setStatus(new BigInteger("2")); //待执行
                if("5".equals(params.get("marketingStatus")) || ("2".equals(params.get("marketingStatus")) || params.get("marketingStatus") == null)){
                    if(params.get("startDate") == null && params.get("endDate") == null){
                        count++;
                        if(count >= startNum && count < endNum){
                            activities.add(activity);
                        }
                    }
                    else{
                        Date startDateQuery = formatC.parse(params.get("startDate") + " 00:00:00");
                        Date endDateQuery = formatC.parse(params.get("endDate") + " 23:59:59");
                        if((startDateQuery.compareTo(startDate) <= 0 && endDateQuery.compareTo(startDate) >= 0) || (startDateQuery.compareTo(endDate) <= 0 && endDateQuery.compareTo(endDate) >= 0)){
                            count++;
                            if(count >= startNum && count < endNum){
                                activities.add(activity);
                            }
                        }
                    }
                }
            }
            else{
                activity.setStatus(new BigInteger("3")); ////已终止
                if("3".equals(params.get("marketingStatus")) || params.get("marketingStatus") == null){
                    if(params.get("startDate") == null && params.get("endDate") == null){
                        count++;
                        if(count >= startNum && count < endNum){
                            activities.add(activity);
                        }
                    }
                    else{
                        Date startDateQuery = formatC.parse(params.get("startDate") + " 00:00:00");
                        Date endDateQuery = formatC.parse(params.get("endDate") + " 23:59:59");
                        if((startDateQuery.compareTo(startDate) <= 0 && endDateQuery.compareTo(startDate) >= 0) || (startDateQuery.compareTo(endDate) <= 0 && endDateQuery.compareTo(endDate) >= 0)){
                            count++;
                            if(count >= startNum && count < endNum){
                                activities.add(activity);
                            }
                        }
                    }
                }
            }
        }
        map.put("total", count > 0 ? count : 0);
        map.put("rows", activities);
        resultJSON.setJsonMap(map);
        return resultJSON;
    }

    /**
     * 查询卡券促销活动和卡券游戏促销活动列表
     */
    public ResultJSON queryCardCoupnseAndGame(Map params) throws ServiceException, ParseException{
        ResultJSON resultJSON = new ResultJSON();
        Map<String, Object> map = new HashMap<>();
        params.put("queryCardCoupnseAndGame", "1");
        int startNum = 0;
        int endNum = 9999;
        if(params.get("page") != null && params.get("rows") != null && !"".equals(params.get("rows"))){
            startNum = (Integer.parseInt(params.get("page").toString()) - 1) * Integer.parseInt(params.get("rows").toString()) + 1;
            endNum = Integer.parseInt(params.get("rows").toString()) + startNum;

        }
        List<DietPromotion> list = dietPromotionMapper.dietPromotionList(params);
        List<Activity> activities = new ArrayList<>();
        SimpleDateFormat formatA = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat formatB = new SimpleDateFormat("HH:mm:ss");
        SimpleDateFormat formatC = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        int count = 0;
        for(int i = 0; i < list.size(); i++){
            Activity activity = new Activity();
            DietPromotion dietPromotion = list.get(i);
            activity.setId(dietPromotion.getId());
            activity.setTenantId(dietPromotion.getTenantId());
            activity.setPromotionCode(dietPromotion.getPromotionCode());
            activity.setPromotionName(dietPromotion.getPromotionName());
            activity.setPromotionType(dietPromotion.getPromotionType());
            activity.setStartDate(dietPromotion.getStartDate());
            activity.setEndDate(dietPromotion.getEndDate());
            activity.setStartTime(dietPromotion.getStartTime());
            activity.setEndTime(dietPromotion.getEndTime());
            activity.setApplyToMon(dietPromotion.getApplyToMon());
            activity.setApplyToTue(dietPromotion.getApplyToTue());
            activity.setApplyToWed(dietPromotion.getApplyToWed());
            activity.setApplyToThu(dietPromotion.getApplyToThu());
            activity.setApplyToFri(dietPromotion.getApplyToFri());
            activity.setApplyToSat(dietPromotion.getApplyToSat());
            activity.setApplyToSun(dietPromotion.getApplyToSun());
            activity.setScope(dietPromotion.getScope());
            activity.setForCustomerType(dietPromotion.getForCustomerType());
            activity.setMemGradeId(dietPromotion.getMemGradeId());
            activity.setMemo(dietPromotion.getMemo());
            activity.setPromotionStatus(dietPromotion.getPromotionStatus());
            activity.setUse(dietPromotion.getIsUse());
            activity.setSuperposition(dietPromotion.getIsSuperposition());
            activity.setCreateAt(dietPromotion.getCreateAt());
            activity.setCreateBy(dietPromotion.getCreateBy());
            activity.setAuditeAt(dietPromotion.getAuditeAt());
            activity.setAuditeBy(dietPromotion.getAuditeBy());
            activity.setLastUpdateBy(dietPromotion.getLastUpdateBy());
            activity.setLastUpdateAt(dietPromotion.getLastUpdateAt());
            activity.setDeleted(dietPromotion.isDeleted());
            String activityTime = formatA.format(dietPromotion.getStartDate()) + " " + formatB.format(dietPromotion.getStartTime()) + "至" + formatA.format(dietPromotion.getEndDate()) + " " + formatB.format(dietPromotion.getEndTime());
            activity.setActivityTime(activityTime);
            String startStr = formatA.format(dietPromotion.getStartDate()) + " " + formatB.format(dietPromotion.getStartTime());
            String endStr = formatA.format(dietPromotion.getEndDate()) + " " + formatB.format(dietPromotion.getEndTime());
            Date startDate = formatC.parse(startStr);
            Date endDate = formatC.parse(endStr);
            Date nowDate = new Date();
            Date startDateQuery = formatC.parse(params.get("startDate") + " 00:00:00");
            Date endDateQuery = formatC.parse(params.get("endDate") + " 23:59:59");
            if(startDate.before(nowDate) && endDate.after(nowDate) && dietPromotion.getIsUse()){
                activity.setStatus(new BigInteger("1")); //执行中
                if("1".equals(params.get("marketingStatus")) || params.get("marketingStatus") == null){
                    if(params.get("startDate") == null && params.get("endDate") == null){
                        count++;
                        if(count >= startNum && count < endNum){
                            activities.add(activity);
                        }
                    }
                    else{
                        if((startDateQuery.compareTo(startDate) <= 0 && endDateQuery.compareTo(startDate) >= 0) || (startDateQuery.compareTo(endDate) <= 0 && endDateQuery.compareTo(endDate) >= 0)||(startDateQuery.compareTo(startDate) >= 0 && endDateQuery.compareTo(endDate) <= 0)){
                            count++;
                            if(count >= startNum && count < endNum){
                                activities.add(activity);
                            }
                        }
                    }
                }
            }
            else if(startDate.after(nowDate) && dietPromotion.getIsUse()){
                activity.setStatus(new BigInteger("2")); //待执行
                if("2".equals(params.get("marketingStatus")) || params.get("marketingStatus") == null){
                    if(params.get("startDate") == null && params.get("endDate") == null){
                        count++;
                        if(count >= startNum && count < endNum){
                            activities.add(activity);
                        }
                    }
                    else{
                        if((startDateQuery.compareTo(startDate) <= 0 && endDateQuery.compareTo(startDate) >= 0) || (startDateQuery.compareTo(endDate) <= 0 && endDateQuery.compareTo(endDate) >= 0)||(startDateQuery.compareTo(startDate) >= 0 && endDateQuery.compareTo(endDate) <= 0)){
                            count++;
                            if(count >= startNum && count < endNum){
                                activities.add(activity);
                            }
                        }
                    }
                }
            }
            else{
                activity.setStatus(new BigInteger("3")); ////已终止
                if("3".equals(params.get("marketingStatus")) || params.get("marketingStatus") == null){
                    if(params.get("startDate") == null && params.get("endDate") == null){
                        count++;
                        if(count >= startNum && count < endNum){
                            activities.add(activity);
                        }
                    }
                    else{
                        if((startDateQuery.compareTo(startDate) <= 0 && endDateQuery.compareTo(startDate) >= 0) || (startDateQuery.compareTo(endDate) <= 0 && endDateQuery.compareTo(endDate) >= 0)||(startDateQuery.compareTo(startDate) >= 0 && endDateQuery.compareTo(endDate) <= 0)){
                            count++;
                            if(count >= startNum && count < endNum){
                                activities.add(activity);
                            }
                        }
                    }
                }
            }
        }
        map.put("total", count > 0 ? count : 0);
        map.put("rows", activities);
        resultJSON.setJsonMap(map);
        return resultJSON;
    }

    /**
     * 删除促销活动
     */
    public ResultJSON delDietPromotion(Map params) throws ServiceException, ParseException{
        ResultJSON resultJSON = new ResultJSON();
        DietPromotion dietPromotion = dietPromotionMapper.findDietPromotionById(params);
        if(dietPromotion != null && params.get("branchId") != null && dietPromotion.getCreateBranchId().compareTo(new BigInteger(params.get("branchId").toString())) != 0){
            resultJSON.setSuccess("1");
            resultJSON.setMsg("不能删除非本店创建的活动！");
            return resultJSON;
        }
        //删除卡券需要判定是否有卡券游戏占用
        if(dietPromotion != null && dietPromotion.getPromotionType() == 8){
            Map<String, Object> map = new HashMap<>();
            map.put("tenantId", dietPromotion.getTenantId());
            map.put("dietPromotionId", dietPromotion.getId());
            DietPromotionFestival festival = dietPromotionFestivalMapper.findByPromotionId(map);
            if(festival != null){
                map.put("prizeDietPromotionId", festival.getId());
                map.remove("dietPromotionId");
                List<DietGamePrizeItem> items = dietGamePrizeItemMapper.dietGamePrizeItemList(map);
                if(items != null && items.size() > 0){
                    for(DietGamePrizeItem item : items){
                        Map<String, Object> map1 = new HashMap<>();
                        map1.put("tenantId", dietPromotion.getTenantId());
                        map1.put("id", item.getDietPromotionId());
                        DietPromotion dp = dietPromotionMapper.findDietPromotionById(map1);
                        SimpleDateFormat formatA = new SimpleDateFormat("yyyy-MM-dd");
                        SimpleDateFormat formatB = new SimpleDateFormat("HH:mm:ss");
                        SimpleDateFormat formatC = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        if(dp != null && dp.getStartDate() != null && dp.getStartTime() != null && dp.getEndDate() != null && dp.getEndTime() != null){
                            String startStr = formatA.format(dp.getStartDate()) + " " + formatB.format(dp.getStartTime());
                            String endStr = formatA.format(dp.getEndDate()) + " " + formatB.format(dp.getEndTime());
                            Date nowDate = new Date();
                            Date startDate = formatC.parse(startStr);
                            Date endDate = formatC.parse(endStr);
                            if(startDate.before(nowDate) && endDate.after(nowDate) && dp.getPromotionStatus() == 0){
                                resultJSON.setSuccess("1");
                                resultJSON.setMsg("当前活动已添加到游戏活动中，无法删除！");
                                return resultJSON;
                            }
                            else if(startDate.after(nowDate) && dp.getPromotionStatus() == 0){
                                resultJSON.setSuccess("1");
                                resultJSON.setMsg("当前活动已添加到游戏活动中，无法删除！");
                                return resultJSON;
                            }
                        }
                    }
                }
            }
        }
        Map<String, Object> mappp = new HashMap<>();
        mappp.put("dietPromotionId", params.get("id"));
        mappp.put("lastUpdateAt", new Date());
        mappp.put("lastUpdateBy", "administrator");
        params.put("lastUpdateAt", new Date());
        params.put("lastUpdateBy", "administrator");
        params.put("isDeleted", "1");
        int flagDietPromotion = dietPromotionMapper.update(params);
        int flagDietPromotionBranchR = 0;
        int flagDietPromotionGoodsR = 0;
        int flagDietPromotionSpecific = 0;
        if(flagDietPromotion == 1){
            params.put("dietPromotionId", params.get("id"));
            List<DietPromotionBranchR> dietPromotionBranchRs = dietPromotionBranchRMapper.dietPromotionBranchRList(params);
            Map<String, Object> map = new HashMap<>();
            map.put("isDeleted", "1");
            map.put("lastUpdateAt", new Date());
            map.put("lastUpdateBy", "administrator");
            for(int i = 0; i < dietPromotionBranchRs.size(); i++){
                map.put("id", dietPromotionBranchRs.get(i).getId());
                flagDietPromotionBranchR = dietPromotionBranchRMapper.update(map);
            }
            if((dietPromotionBranchRs.size() > 0 && flagDietPromotionBranchR == 1) || (dietPromotionBranchRs.size() == 0 && flagDietPromotionBranchR == 0)){
                Map<String, Object> param = new HashMap<>();
                param.put("dietPromotionId", params.get("id"));
                List<DietPromotionGoodsR> dietPromotionGoodsRs = dietPromotionGoodsRMapper.dietPromotionGoodsRList(param);
                Map<String, Object> mapp = new HashMap<>();
                mapp.put("isDeleted", "1");
                mapp.put("lastUpdateAt", new Date());
                mapp.put("lastUpdateBy", "administrator");
                for(int j = 0; j < dietPromotionGoodsRs.size(); j++){
                    mapp.put("id", dietPromotionGoodsRs.get(j).getId());
                    flagDietPromotionGoodsR = dietPromotionGoodsRMapper.update(mapp);
                }
                if((dietPromotionGoodsRs.size() > 0 && flagDietPromotionGoodsR == 1 && dietPromotion != null) || (dietPromotionGoodsRs.size() == 0 && flagDietPromotionGoodsR == 0 && dietPromotion != null)){
                    Map<String, Object> mapppp = new HashMap<>();
                    mapppp.put("isDeleted", "1");
                    mapppp.put("lastUpdateAt", new Date());
                    mapppp.put("lastUpdateBy", "administrator");
                    if(dietPromotion.getPromotionType() == 4){
                        List<DietPromotionBuyGive> dietPromotionBuyGives = dietPromotionBuyGiveMapper.dietPromotionBuyGiveList(mappp);
                        for(int k = 0; k < dietPromotionBuyGives.size(); k++){
                            mapppp.put("id", dietPromotionBuyGives.get(k).getId());
                            flagDietPromotionSpecific = dietPromotionBuyGiveMapper.update(mapppp);
                        }
                    }
                    else if(dietPromotion.getPromotionType() == 6){
                        List<DietPromotionDiscount> dietPromotionDiscounts = dietPromotionDiscountMapper.dietPromotionDiscountList(mappp);
                        for(int k = 0; k < dietPromotionDiscounts.size(); k++){
                            mapppp.put("id", dietPromotionDiscounts.get(k).getId());
                            flagDietPromotionSpecific = dietPromotionDiscountMapper.update(mapppp);
                        }
                    }
                    else if(dietPromotion.getPromotionType() == 7){
                        List<DietPromotionSatisfyReduce> dietPromotionSatisfyReduces = dietPromotionSatisfyReduceMapper.dietPromotionSatisfyReduceList(mappp);
                        for(int k = 0; k < dietPromotionSatisfyReduces.size(); k++){
                            mapppp.put("id", dietPromotionSatisfyReduces.get(k).getId());
                            flagDietPromotionSpecific = dietPromotionSatisfyReduceMapper.update(mapppp);
                        }
                    }
                    else if(dietPromotion.getPromotionType() == 9){
                        List<DietPromotionTotalReduce> dietPromotionTotalReduces = dietPromotionTotalReduceMapper.dietPromotionTotalReduceList(mappp);
                        for(int k = 0; k < dietPromotionTotalReduces.size(); k++){
                            mapppp.put("id", dietPromotionTotalReduces.get(k).getId());
                            flagDietPromotionSpecific = dietPromotionTotalReduceMapper.update(mapppp);
                        }
                    }
                    else if(dietPromotion.getPromotionType() == 11){
                        List<DietPromotionSpecial> dietPromotionSpecials = dietPromotionSpecialMapper.dietPromotionSpecialList(mappp);
                        for(int k = 0; k < dietPromotionSpecials.size(); k++){
                            mapppp.put("id", dietPromotionSpecials.get(k).getId());
                            flagDietPromotionSpecific = dietPromotionSpecialMapper.update(mapppp);
                        }
                    }
                    else if(dietPromotion.getPromotionType() == 8){
                        List<DietPromotionFestival> dietPromotionFestivals = dietPromotionFestivalMapper.dietPromotionFestivalList(mappp);
                        for(int k = 0; k < dietPromotionFestivals.size(); k++){
                            mapppp.put("id", dietPromotionFestivals.get(k).getId());
                            flagDietPromotionSpecific = dietPromotionFestivalMapper.update(mapppp);
                        }
                    }
                    else if(dietPromotion.getPromotionType() == 12){
                        List<DietGameScratchCard> dietGameScratchCards = dietGameScratchCardMapper.dietGameScratchCardList(mappp);
                        for(int k = 0; k < dietGameScratchCards.size(); k++){
                            mapppp.put("id", dietGameScratchCards.get(k).getId());
                            flagDietPromotionSpecific = dietGameScratchCardMapper.update(mapppp);
                        }
                    }
                    else if(dietPromotion.getPromotionType() == 13){
                        List<DietPromotionPayback> dietPromotionPaybacks = dietPromotionPaybackMapper.dietPromotionPaybackList(mappp);
                        for(int k = 0; k < dietPromotionPaybacks.size(); k++){
                            mapppp.put("id", dietPromotionPaybacks.get(k).getId());
                            flagDietPromotionSpecific = dietPromotionPaybackMapper.update(mapppp);
                        }
                    }
                    else if(dietPromotion.getPromotionType() == 14){
                        List<DietPromotionAmount> dietPromotionAmounts = dietPromotionAmountMapper.dietPromotionAmountList(mappp);
                        for(int k = 0; k < dietPromotionAmounts.size(); k++){
                            mapppp.put("id", dietPromotionAmounts.get(k).getId());
                            flagDietPromotionSpecific = dietPromotionAmountMapper.update(mapppp);
                        }
                    }
                    if(flagDietPromotionSpecific == 1){
                        resultJSON.setSuccess("0");
                        resultJSON.setMsg("促销活动删除成功");
                        return resultJSON;
                    }
                }
            }
        }
        resultJSON.setSuccess("1");
        resultJSON.setMsg("促销活动删除失败");
        return resultJSON;
    }

    /**
     * 检查活动是否终止
     */
    public ResultJSON checkClickStop(Map params) throws ServiceException{
        ResultJSON resultJSON = new ResultJSON();
        List<DietPromotion> dietPromotions = dietPromotionMapper.dietPromotionList(params);
        if(dietPromotions.size() > 0){
            if(dietPromotions.get(0).getPromotionStatus() == 0){
                //启用
                resultJSON.setSuccess("0");
                resultJSON.setMsg("促销活动状态启用");
            }
            else{
                //禁用
                resultJSON.setSuccess("1");
                resultJSON.setMsg("促销活动状态禁用");
            }
        }
        return resultJSON;
    }

    /**
     * 终止促销活动
     */
    public ResultJSON clickStop(Map params) throws ParseException{
        ResultJSON resultJSON = new ResultJSON();
        DietPromotion dietPromotions = dietPromotionMapper.findDietPromotionById(params);
        if(dietPromotions != null && params.get("branchId") != null && dietPromotions.getCreateBranchId() != null && dietPromotions.getCreateBranchId().compareTo(new BigInteger(params.get("branchId").toString())) != 0){
            resultJSON.setSuccess("1");
            resultJSON.setMsg("不能终止非本店创建的活动！");
            return resultJSON;
        }
        //终止卡券需要判定是否有卡券游戏占用
        if(dietPromotions != null && dietPromotions.getPromotionType() == 8){
            Map<String, Object> map = new HashMap<>();
            map.put("tenantId", dietPromotions.getTenantId());
            map.put("dietPromotionId", dietPromotions.getId());
            DietPromotionFestival festival = dietPromotionFestivalMapper.findByPromotionId(map);
            if(festival != null){
                map.put("prizeDietPromotionId", festival.getId());
                map.remove("dietPromotionId");
                List<DietGamePrizeItem> items = dietGamePrizeItemMapper.dietGamePrizeItemList(map);
                if(items != null && items.size() > 0){
                    for(DietGamePrizeItem item : items){
                        Map<String, Object> map1 = new HashMap<>();
                        map1.put("tenantId", dietPromotions.getTenantId());
                        map1.put("id", item.getDietPromotionId());
                        DietPromotion dp = dietPromotionMapper.findDietPromotionById(map1);
                        SimpleDateFormat formatA = new SimpleDateFormat("yyyy-MM-dd");
                        SimpleDateFormat formatB = new SimpleDateFormat("HH:mm:ss");
                        SimpleDateFormat formatC = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        if(dp != null && dp.getStartDate() != null && dp.getStartTime() != null && dp.getEndDate() != null && dp.getEndTime() != null){
                            String startStr = formatA.format(dp.getStartDate()) + " " + formatB.format(dp.getStartTime());
                            String endStr = formatA.format(dp.getEndDate()) + " " + formatB.format(dp.getEndTime());
                            Date nowDate = new Date();
                            Date startDate = formatC.parse(startStr);
                            Date endDate = formatC.parse(endStr);
                            if(startDate.before(nowDate) && endDate.after(nowDate) && dp.getPromotionStatus() == 0){
                                resultJSON.setSuccess("1");
                                resultJSON.setMsg("当前活动已添加到游戏活动中，无法终止！");
                                return resultJSON;
                            }
                            else if(startDate.after(nowDate) && dp.getPromotionStatus() == 0){
                                resultJSON.setSuccess("1");
                                resultJSON.setMsg("当前活动已添加到游戏活动中，无法终止！");
                                return resultJSON;
                            }
                        }
                    }
                }
            }
        }

        if(dietPromotions != null){
            if(dietPromotions.getPromotionStatus() == 1){
                resultJSON.setSuccess("0");
                resultJSON.setMsg("该活动已终止！");
                return resultJSON;
            }
            Map<String, Object> map = new HashMap<>();
            map.put("promotionStatus", 1);
            map.put("isUse", 0);
            map.put("id", params.get("id"));
            map.put("lastUpdateAt", new Date());
            map.put("lastUpdateBy", "administrator");
            int flag = dietPromotionMapper.update(map);
            int flagDietPromotionBranchR = 0;
            if(flag == 1){
                Map<String, Object> mapp = new HashMap<>();
                mapp.put("dietPromotionId", dietPromotions.getId());
                mapp.put("lastUpdateAt", new Date());
                mapp.put("lastUpdateBy", "administrator");
                List<DietPromotionBranchR> dietPromotionBranchRs = dietPromotionBranchRMapper.dietPromotionBranchRList(mapp);
                Map<String, Object> mappp = new HashMap<>();
                mappp.put("isDeleted", "1");
                for(int i = 0; i < dietPromotionBranchRs.size(); i++){
                    mappp.put("id", dietPromotionBranchRs.get(i).getId());
                    mappp.put("lastUpdateAt", new Date());
                    mappp.put("lastUpdateBy", "administrator");
                    flagDietPromotionBranchR = dietPromotionBranchRMapper.update(mappp);
                    if(flagDietPromotionBranchR == 0){
                        resultJSON.setSuccess("1");
                        resultJSON.setMsg("终止促销活动失败");
                        return resultJSON;
                    }
                }
                resultJSON.setSuccess("0");
                resultJSON.setMsg("终止促销活动成功");
            }
            else{
                resultJSON.setSuccess("1");
                resultJSON.setMsg("终止促销活动失败");
            }
        }
        return resultJSON;
    }

    /**
     * 新增促销活动
     */
    public ResultJSON addPromotion(Map params, DietPromotion dietPromotion, List list) throws ServiceException, ParseException{
        ResultJSON resultJSON = new ResultJSON();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat simpleDateFormat1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat simpleDateFormat2 = new SimpleDateFormat("HH:mm:ss");
        Date newStartDate = simpleDateFormat.parse((String)params.get("start"));
        Date newEndDate = simpleDateFormat.parse(params.get("end").toString());
        String[] branchIds = ((String)params.get("branchIds")).split(",");
        for(int k = 0; k < branchIds.length; k++){
            if(list != null){
                for(int i = 0; i < list.size(); i++){
                    Map<String, Object> map = new HashMap<>();
                    map.put("goodsId", ((Map)list.get(i)).get("buyGoodsId"));
                    map.put("endLine", params.get("start").toString().substring(0, 10));
                    List<DietPromotionGoodsR> dietPromotionGoodsRs = dietPromotionGoodsRMapper.dietPromotionGoodsRList(map);
                    if(dietPromotionGoodsRs != null && dietPromotionGoodsRs.size() > 0){
                        for(int j = 0; j < dietPromotionGoodsRs.size(); j++){
                            String oldStart = simpleDateFormat.format(dietPromotionGoodsRs.get(j).getStartDate()) + " " + simpleDateFormat2.format(dietPromotionGoodsRs.get(j).getStartTime());
                            Date oldStartDate = simpleDateFormat.parse(oldStart);
                            String oldEnd = simpleDateFormat.format(dietPromotionGoodsRs.get(j).getEndDate()) + " " + simpleDateFormat2.format(dietPromotionGoodsRs.get(j).getEndTime());
                            Date oldEndDate = simpleDateFormat.parse(oldEnd);
                            Map<String, Object> map1 = new HashMap<>();
                            map1.put("dietPromotionId", dietPromotionGoodsRs.get(j).getDietPromotionId());
                            List<DietPromotionBranchR> dietPromotionBranchRs = dietPromotionBranchRMapper.dietPromotionBranchRList(map1);
                            for(int m = 0; m < dietPromotionBranchRs.size(); m++){
                                if(newEndDate.before(oldStartDate) || newStartDate.after(oldEndDate) || !dietPromotionBranchRs.get(m).getBranchId().equals(new BigInteger(branchIds[k]))){
                                }
                                else{
                                    //判断周
                                    String eqWeeks = DateWeekContains.getEqWeeks(params.get("weekInsert").toString(), dietPromotionGoodsRs.get(j).getWeek());
                                    if(eqWeeks.length() > 0){
                                        Map<String, Object> map3 = new HashMap<>();
                                        map3.put("id", dietPromotionBranchRs.get(m).getDietPromotionId());
                                        map3.put("tenantId", params.get("tenantId"));
                                        List<DietPromotion> dietPromotions = dietPromotionMapper.dietPromotionList(map3);
                                        if(dietPromotions != null && dietPromotions.size() > 0){
                                            DietPromotion dietPromotion1 = dietPromotions.get(0);
                                            if(dietPromotion1.getScope() == 0){
                                                if(dietPromotion1.getForCustomerType() == 0){
                                                    resultJSON.setSuccess("1");
                                                    resultJSON.setMsg("选择的商品在已有的时段已经参与促销，请重新选择");
                                                    return resultJSON;
                                                }
                                                else{
                                                    if(Integer.parseInt(params.get("forCustomerType").toString()) == 0 || Integer.parseInt(params.get("forCustomerType").toString()) == dietPromotion1.getForCustomerType()){
                                                        resultJSON.setSuccess("1");
                                                        resultJSON.setMsg("选择的商品在已有的时段已经参与促销，请重新选择");
                                                        return resultJSON;
                                                    }
                                                }
                                            }
                                            else{
                                                if(Integer.parseInt(params.get("scope").toString()) == 0 || Integer.parseInt(params.get("scope").toString()) == dietPromotion1.getScope()){
                                                    if(dietPromotion1.getForCustomerType() == 0){
                                                        resultJSON.setSuccess("1");
                                                        resultJSON.setMsg("选择的商品在已有的时段已经参与促销，请重新选择");
                                                        return resultJSON;
                                                    }
                                                    else{
                                                        if(Integer.parseInt(params.get("forCustomerType").toString()) == 0 || Integer.parseInt(params.get("forCustomerType").toString()) == dietPromotion1.getForCustomerType()){
                                                            resultJSON.setSuccess("1");
                                                            resultJSON.setMsg("选择的商品在已有的时段已经参与促销，请重新选择");
                                                            return resultJSON;
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        Date startDate = simpleDateFormat.parse(params.get("start").toString().substring(0, 10));
        Date endDate = simpleDateFormat.parse(params.get("end").toString().substring(0, 10));
        Date startTime = simpleDateFormat1.parse(params.get("start") + " 00:00:00");
        Date endTime = simpleDateFormat1.parse(params.get("end") + " 23:59:59");
        dietPromotion.setStartDate(new java.sql.Date(startDate.getTime()));
        dietPromotion.setStartTime(new java.sql.Time(startTime.getTime()));
        dietPromotion.setEndDate(new java.sql.Date(endDate.getTime()));
        dietPromotion.setEndTime(new java.sql.Time(endTime.getTime()));

        dietPromotion.setTenantId(new BigInteger((String)params.get("tenantId")));
        dietPromotion.setCreateBranchId(new BigInteger((String)params.get("myBranchId")));
        String index = vipService.getNextSelValue(SysConfig.SYS_DIETPROMOTION_NUM, 4, new BigInteger(params.get("tenantId").toString()));
        String y_M_d = Common.getNowCorrect2Millisecond().substring(2, 8);//六位年月日
        dietPromotion.setPromotionCode("CX" + y_M_d + index);

        String[] weeks = params.get("weekInsert").toString().split(",");
        if(weeks == null){
            resultJSON.setMsg("未选择星期");
            resultJSON.setSuccess("1");
            return resultJSON;
        }
        else{
            for(int i = 0; i < weeks.length; i++){
                if(i + 1 == 1){
                    if(Integer.valueOf(weeks[i]) == 1){
                        dietPromotion.setApplyToMon(1);
                    }
                    else{
                        dietPromotion.setApplyToMon(0);
                    }
                }
                if(i + 1 == 2){
                    if(Integer.valueOf(weeks[i]) == 1){
                        dietPromotion.setApplyToTue(1);
                    }
                    else{
                        dietPromotion.setApplyToTue(0);
                    }
                }
                if(i + 1 == 3){
                    if(Integer.valueOf(weeks[i]) == 1){
                        dietPromotion.setApplyToWed(1);
                    }
                    else{
                        dietPromotion.setApplyToWed(0);
                    }
                }
                if(i + 1 == 4){
                    if(Integer.valueOf(weeks[i]) == 1){
                        dietPromotion.setApplyToThu(1);
                    }
                    else{
                        dietPromotion.setApplyToThu(0);
                    }
                }
                if(i + 1 == 5){
                    if(Integer.valueOf(weeks[i]) == 1){
                        dietPromotion.setApplyToFri(1);
                    }
                    else{
                        dietPromotion.setApplyToFri(0);
                    }
                }
                if(i + 1 == 6){
                    if(Integer.valueOf(weeks[i]) == 1){
                        dietPromotion.setApplyToSat(1);
                    }
                    else{
                        dietPromotion.setApplyToSat(0);
                    }
                }
                if(i + 1 == 7){
                    if(Integer.valueOf(weeks[i]) == 1){
                        dietPromotion.setApplyToSun(1);
                    }
                    else{
                        dietPromotion.setApplyToSun(0);
                    }
                }
            }
        }
        if(params.get("effectiveInterval") != null){
            dietPromotion.setEffectiveInterval(params.get("effectiveInterval").toString());
        }
        dietPromotion.setSuperposition(false);
        dietPromotion.setDeleted(false);
        dietPromotion.setPromotionName(params.get("promotionName").toString());
        dietPromotion.setUse(true);
        dietPromotion.setCreateBy("administrator");
        dietPromotion.setLastUpdateBy("administrator");
        dietPromotion.setCreateAt(new Date());
        dietPromotion.setLastUpdateAt(new Date());
        int flag = dietPromotionMapper.insert(dietPromotion);
        if(flag == 1){
            ApiRest ass = tenantConfigService.addTenantConfigOne(BigInteger.valueOf(Long.valueOf(((String)params.get("tenantId")).toString())), SysConfig.SYS_DIETPROMOTION_NUM);
            if(!ass.getIsSuccess()){
                resultJSON.setSuccess("1");
                resultJSON.setMsg(ass.getError());
                return resultJSON;
            }
                /*resultJSON.setSuccess("0");
                resultJSON.setMsg("卡券保存成功");*/
            if(list != null){
                //买赠促销
                if(dietPromotion.getPromotionType() == 4){
                    for(int i = 0; i < list.size(); i++){
                        Map<String, Object> mapDietPromotionBuyGive = new HashMap<>();
                        mapDietPromotionBuyGive.put("goodsId", ((Map)(list.get(i))).get("buyGoodsId"));
                        mapDietPromotionBuyGive.put("giveGoodsId", ((Map)(list.get(i))).get("giveGoodsId"));
                        mapDietPromotionBuyGive.put("buyNum", ((Map)(list.get(i))).get("buyNum"));
                        mapDietPromotionBuyGive.put("giveNum", ((Map)(list.get(i))).get("giveNum"));
                        mapDietPromotionBuyGive.put("tenantId", new BigInteger(params.get("tenantId").toString()));
                        mapDietPromotionBuyGive.put("branchId", new BigInteger(params.get("myBranchId").toString()));
                        mapDietPromotionBuyGive.put("dietPromotionId", dietPromotion.getId());
                        mapDietPromotionBuyGive.put("isDeleted", 0);
                        mapDietPromotionBuyGive.put("createBy", "administrator");
                        mapDietPromotionBuyGive.put("lastUpdateBy", "administrator");
                        mapDietPromotionBuyGive.put("createAt", new Date());
                        mapDietPromotionBuyGive.put("lastUpdateAt", new Date());
                        int flagDietPromotionBuyGive = dietPromotionBuyGiveMapper.insert(mapDietPromotionBuyGive);
                        if(flagDietPromotionBuyGive == 1){
                            Map<String, Object> mapDietPromotionGoodsR = new HashMap<>();
                            mapDietPromotionGoodsR.put("goodsId", ((Map)(list.get(i))).get("buyGoodsId"));
                            mapDietPromotionGoodsR.put("dietPromotionId", dietPromotion.getId());
                            mapDietPromotionGoodsR.put("isUse", 1);
                            mapDietPromotionGoodsR.put("startDate", dietPromotion.getStartDate());
                            mapDietPromotionGoodsR.put("startTime", dietPromotion.getStartTime());
                            mapDietPromotionGoodsR.put("endDate", dietPromotion.getEndDate());
                            mapDietPromotionGoodsR.put("endTime", dietPromotion.getEndTime());
                            StringBuffer weekTemp = new StringBuffer();
                            if(params.get("weekInsert").toString().contains(",")){
                                for(int j = 0; j < weeks.length; j++){
                                    if(j < weeks.length - 1){
                                        weekTemp.append(weeks[j] + ",");
                                    }
                                    else{
                                        weekTemp.append(weeks[j]);
                                    }
                                }
                                mapDietPromotionGoodsR.put("week", weekTemp.toString());
                            }
                            else{
                                mapDietPromotionGoodsR.put("week", params.get("weekInsert"));
                            }
                            mapDietPromotionGoodsR.put("isDeleted", 0);
                            mapDietPromotionGoodsR.put("createBy", "administrator");
                            mapDietPromotionGoodsR.put("lastUpdateBy", "administrator");
                            mapDietPromotionGoodsR.put("createAt", new Date());
                            mapDietPromotionGoodsR.put("lastUpdateAt", new Date());
                            mapDietPromotionGoodsR.put("tenantId", dietPromotion.getTenantId());
                            int flagDietPromotionGoodsR = dietPromotionGoodsRMapper.insert(mapDietPromotionGoodsR);
                            if(flagDietPromotionGoodsR != 1){
                                resultJSON.setSuccess("1");
                                resultJSON.setMsg("促销活动保存失败");
                                return resultJSON;
                            }
                        }
                        else{
                            resultJSON.setSuccess("1");
                            resultJSON.setMsg("促销活动保存失败");
                            return resultJSON;
                        }
                    }
                }
                //再买优惠
                if(dietPromotion.getPromotionType() == 6){
                    for(int i = 0; i < list.size(); i++){
                        Map<String, Object> mapDietPromotionDiscount = new HashMap<>();
                        mapDietPromotionDiscount.put("goodsId", ((Map)list.get(i)).get("buyGoodsId"));
                        mapDietPromotionDiscount.put("quantity", ((Map)list.get(i)).get("quantity"));
                        mapDietPromotionDiscount.put("discount", ((Map)list.get(i)).get("discount"));
                        mapDietPromotionDiscount.put("dietPromotionId", dietPromotion.getId());
                        mapDietPromotionDiscount.put("isDeleted", 0);
                        mapDietPromotionDiscount.put("tenantId", new BigInteger((String)params.get("tenantId")));
                        mapDietPromotionDiscount.put("branchId", new BigInteger((String)params.get("myBranchId")));
                        mapDietPromotionDiscount.put("createBy", "administrator");
                        mapDietPromotionDiscount.put("lastUpdateBy", "administrator");
                        mapDietPromotionDiscount.put("quantity", 1);
                        mapDietPromotionDiscount.put("createAt", new Date());
                        mapDietPromotionDiscount.put("lastUpdateAt", new Date());
                        int flagDietPromotionDiscount = dietPromotionDiscountMapper.insert(mapDietPromotionDiscount);
                        if(flagDietPromotionDiscount == 1){
                            Map<String, Object> mapDietPromotionGoodsR = new HashMap<>();
                            mapDietPromotionGoodsR.put("goodsId", ((Map)(list.get(i))).get("buyGoodsId"));
                            mapDietPromotionGoodsR.put("dietPromotionId", dietPromotion.getId());
                            mapDietPromotionGoodsR.put("isUse", 1);
                            mapDietPromotionGoodsR.put("startDate", dietPromotion.getStartDate());
                            mapDietPromotionGoodsR.put("startTime", dietPromotion.getStartTime());
                            mapDietPromotionGoodsR.put("endDate", dietPromotion.getEndDate());
                            mapDietPromotionGoodsR.put("endTime", dietPromotion.getEndTime());
                            StringBuffer weekTemp = new StringBuffer();
                            if(params.get("weekInsert").toString().contains(",")){
                                for(int j = 0; j < weeks.length; j++){
                                    if(j < weeks.length - 1){
                                        weekTemp.append(weeks[j] + ",");
                                    }
                                    else{
                                        weekTemp.append(weeks[j]);
                                    }
                                }
                                mapDietPromotionGoodsR.put("week", weekTemp.toString());
                            }
                            else{
                                mapDietPromotionGoodsR.put("week", params.get("weekInsert"));
                            }
                            mapDietPromotionGoodsR.put("isDeleted", 0);
                            mapDietPromotionGoodsR.put("createBy", "administrator");
                            mapDietPromotionGoodsR.put("lastUpdateBy", "administrator");
                            mapDietPromotionGoodsR.put("createAt", new Date());
                            mapDietPromotionGoodsR.put("lastUpdateAt", new Date());
                            mapDietPromotionGoodsR.put("tenantId", dietPromotion.getTenantId());
                            int flagDietPromotionGoodsR = dietPromotionGoodsRMapper.insert(mapDietPromotionGoodsR);
                            if(flagDietPromotionGoodsR != 1){
                                resultJSON.setSuccess("1");
                                resultJSON.setMsg("促销活动保存失败");
                                return resultJSON;
                            }
                        }
                        else{
                            resultJSON.setSuccess("1");
                            resultJSON.setMsg("促销活动保存失败");
                            return resultJSON;
                        }
                    }
                }
                //满减
                if(dietPromotion.getPromotionType() == 7){
                    for(int i = 0; i < list.size(); i++){
                        Map<String, Object> mapDietPromotionSatisfyReduce = new HashMap<>();
                        mapDietPromotionSatisfyReduce.put("goodsId", ((Map)list.get(i)).get("buyGoodsId"));
                        mapDietPromotionSatisfyReduce.put("satisfy", ((Map)list.get(i)).get("satisfy"));
                        mapDietPromotionSatisfyReduce.put("reduction", ((Map)list.get(i)).get("reduction"));
                        mapDietPromotionSatisfyReduce.put("dietPromotionId", dietPromotion.getId());
                        mapDietPromotionSatisfyReduce.put("isDeleted", 0);
                        mapDietPromotionSatisfyReduce.put("tenantId", new BigInteger((String)params.get("tenantId")));
                        mapDietPromotionSatisfyReduce.put("branchId", new BigInteger((String)params.get("myBranchId")));
                        mapDietPromotionSatisfyReduce.put("createBy", "administrator");
                        mapDietPromotionSatisfyReduce.put("lastUpdateBy", "administrator");
                        mapDietPromotionSatisfyReduce.put("createAt", new Date());
                        mapDietPromotionSatisfyReduce.put("lastUpdateAt", new Date());
                        int flagDietPromotionSatisfyReduce = dietPromotionSatisfyReduceMapper.insert(mapDietPromotionSatisfyReduce);
                        if(flagDietPromotionSatisfyReduce == 1){
                            Map<String, Object> mapDietPromotionGoodsR = new HashMap<>();
                            mapDietPromotionGoodsR.put("goodsId", ((Map)(list.get(i))).get("buyGoodsId"));
                            mapDietPromotionGoodsR.put("dietPromotionId", dietPromotion.getId());
                            mapDietPromotionGoodsR.put("isUse", 1);
                            mapDietPromotionGoodsR.put("startDate", dietPromotion.getStartDate());
                            mapDietPromotionGoodsR.put("startTime", dietPromotion.getStartTime());
                            mapDietPromotionGoodsR.put("endDate", dietPromotion.getEndDate());
                            mapDietPromotionGoodsR.put("endTime", dietPromotion.getEndTime());
                            StringBuffer weekTemp = new StringBuffer();
                            if(params.get("weekInsert").toString().contains(",")){
                                for(int j = 0; j < weeks.length; j++){
                                    if(j < weeks.length - 1){
                                        weekTemp.append(weeks[j] + ",");
                                    }
                                    else{
                                        weekTemp.append(weeks[j]);
                                    }
                                }
                                mapDietPromotionGoodsR.put("week", weekTemp.toString());
                            }
                            else{
                                mapDietPromotionGoodsR.put("week", params.get("weekInsert"));
                            }
                            mapDietPromotionGoodsR.put("isDeleted", 0);
                            mapDietPromotionGoodsR.put("createBy", "administrator");
                            mapDietPromotionGoodsR.put("lastUpdateBy", "administrator");
                            mapDietPromotionGoodsR.put("createAt", new Date());
                            mapDietPromotionGoodsR.put("lastUpdateAt", new Date());
                            mapDietPromotionGoodsR.put("tenantId", dietPromotion.getTenantId());
                            int flagDietPromotionGoodsR = dietPromotionGoodsRMapper.insert(mapDietPromotionGoodsR);
                            if(flagDietPromotionGoodsR != 1){
                                resultJSON.setSuccess("1");
                                resultJSON.setMsg("促销活动保存失败");
                                return resultJSON;
                            }
                        }
                        else{
                            resultJSON.setSuccess("1");
                            resultJSON.setMsg("促销活动保存失败");
                            return resultJSON;
                        }
                    }
                }
                //单品促销
                if(dietPromotion.getPromotionType() == 11){
                    for(int i = 0; i < list.size(); i++){
                        Map<String, Object> mapDietPromotionSpecial = new HashMap<>();
                        mapDietPromotionSpecial.put("goodsId", ((Map)list.get(i)).get("buyGoodsId"));
                        if(((Map)list.get(i)).get("promotionPercent") != null && !"".equals(((Map)list.get(i)).get("promotionPercent"))
                                && ((Map)list.get(i)).get("promotionPrice") != null && !"".equals(((Map)list.get(i)).get("promotionPrice"))){
                            mapDietPromotionSpecial.put("promotionPrice", ((Map)list.get(i)).get("promotionPrice"));
                            mapDietPromotionSpecial.put("promotionPercent", ((Map)list.get(i)).get("promotionPercent"));
                        }
                        else{
                            resultJSON.setSuccess("1");
                            resultJSON.setMsg("促销折扣或促销价格不能为空！");
                            return resultJSON;
                        }
                        mapDietPromotionSpecial.put("dietPromotionId", dietPromotion.getId());
                        mapDietPromotionSpecial.put("isDeleted", 0);
                        mapDietPromotionSpecial.put("tenantId", new BigInteger((String)params.get("tenantId")));
                        mapDietPromotionSpecial.put("branchId", new BigInteger((String)params.get("myBranchId")));
                        mapDietPromotionSpecial.put("createBy", "administrator");
                        mapDietPromotionSpecial.put("lastUpdateBy", "administrator");
                        mapDietPromotionSpecial.put("createAt", new Date());
                        mapDietPromotionSpecial.put("lastUpdateAt", new Date());
                        int flagDietPromotionSpecial = dietPromotionSpecialMapper.insert(mapDietPromotionSpecial);
                        if(flagDietPromotionSpecial == 1){
                            Map<String, Object> mapDietPromotionGoodsR = new HashMap<>();
                            mapDietPromotionGoodsR.put("goodsId", ((Map)(list.get(i))).get("buyGoodsId"));
                            mapDietPromotionGoodsR.put("dietPromotionId", dietPromotion.getId());
                            mapDietPromotionGoodsR.put("isUse", 1);
                            mapDietPromotionGoodsR.put("startDate", dietPromotion.getStartDate());
                            mapDietPromotionGoodsR.put("startTime", dietPromotion.getStartTime());
                            mapDietPromotionGoodsR.put("endDate", dietPromotion.getEndDate());
                            mapDietPromotionGoodsR.put("endTime", dietPromotion.getEndTime());
                            StringBuffer weekTemp = new StringBuffer();
                            if(params.get("weekInsert").toString().contains(",")){
                                for(int j = 0; j < weeks.length; j++){
                                    if(j < weeks.length - 1){
                                        weekTemp.append(weeks[j] + ",");
                                    }
                                    else{
                                        weekTemp.append(weeks[j]);
                                    }
                                }
                                mapDietPromotionGoodsR.put("week", weekTemp.toString());
                            }
                            else{
                                mapDietPromotionGoodsR.put("week", params.get("weekInsert"));
                            }
                            mapDietPromotionGoodsR.put("isDeleted", 0);
                            mapDietPromotionGoodsR.put("createBy", "administrator");
                            mapDietPromotionGoodsR.put("lastUpdateBy", "administrator");
                            mapDietPromotionGoodsR.put("createAt", new Date());
                            mapDietPromotionGoodsR.put("lastUpdateAt", new Date());
                            mapDietPromotionGoodsR.put("tenantId", dietPromotion.getTenantId());
                            int flagDietPromotionGoodsR = dietPromotionGoodsRMapper.insert(mapDietPromotionGoodsR);
                            if(flagDietPromotionGoodsR != 1){
                                resultJSON.setSuccess("1");
                                resultJSON.setMsg("促销活动保存失败");
                                return resultJSON;
                            }
                        }
                        else{
                            resultJSON.setSuccess("1");
                            resultJSON.setMsg("促销活动保存失败");
                            return resultJSON;
                        }
                    }
                }
                //数量促销
                if(dietPromotion.getPromotionType() == 14){
                    for(int i = 0; i < list.size(); i++){
                        Map<String, Object> mapDietPromotionl = new HashMap<>();
                        mapDietPromotionl.put("goodsId", ((Map)list.get(i)).get("buyGoodsId"));
                        mapDietPromotionl.put("promotionPrice", ((Map)list.get(i)).get("promotionPrice"));
                        mapDietPromotionl.put("amount", ((Map)list.get(i)).get("amount"));
                        mapDietPromotionl.put("conditionFor", ((Map)list.get(i)).get("conditionFor"));
                        mapDietPromotionl.put("dietPromotionId", dietPromotion.getId());
                        mapDietPromotionl.put("isDeleted", 0);
//                            mapDietPromotionl.put("tenantId",new BigInteger((String)params.get("tenantId")));
//                            mapDietPromotionl.put("branchId",new BigInteger((String)params.get("myBranchId")));
                        mapDietPromotionl.put("createBy", "administrator");
                        mapDietPromotionl.put("lastUpdateBy", "administrator");
                        mapDietPromotionl.put("createAt", new Date());
                        mapDietPromotionl.put("lastUpdateAt", new Date());
                        mapDietPromotionl.put("tenantId", dietPromotion.getTenantId());
                        int flagDietPromotion = dietPromotionAmountMapper.insert(mapDietPromotionl);
                        if(flagDietPromotion == 1){
                            Map<String, Object> mapDietPromotionGoodsR = new HashMap<>();
                            mapDietPromotionGoodsR.put("goodsId", ((Map)(list.get(i))).get("buyGoodsId"));
                            mapDietPromotionGoodsR.put("dietPromotionId", dietPromotion.getId());
                            mapDietPromotionGoodsR.put("isUse", 1);
                            mapDietPromotionGoodsR.put("startDate", dietPromotion.getStartDate());
                            mapDietPromotionGoodsR.put("startTime", dietPromotion.getStartTime());
                            mapDietPromotionGoodsR.put("endDate", dietPromotion.getEndDate());
                            mapDietPromotionGoodsR.put("endTime", dietPromotion.getEndTime());
                            StringBuffer weekTemp = new StringBuffer();
                            if(params.get("weekInsert").toString().contains(",")){
                                for(int j = 0; j < weeks.length; j++){
                                    if(j < weeks.length - 1){
                                        weekTemp.append(weeks[j] + ",");
                                    }
                                    else{
                                        weekTemp.append(weeks[j]);
                                    }
                                }
                                mapDietPromotionGoodsR.put("week", weekTemp.toString());
                            }
                            else{
                                mapDietPromotionGoodsR.put("week", params.get("weekInsert"));
                            }
                            mapDietPromotionGoodsR.put("isDeleted", 0);
                            mapDietPromotionGoodsR.put("createBy", "administrator");
                            mapDietPromotionGoodsR.put("lastUpdateBy", "administrator");
                            mapDietPromotionGoodsR.put("createAt", new Date());
                            mapDietPromotionGoodsR.put("lastUpdateAt", new Date());
                            mapDietPromotionGoodsR.put("tenantId", dietPromotion.getTenantId());
                            int flagDietPromotionGoodsR = dietPromotionGoodsRMapper.insert(mapDietPromotionGoodsR);
                            if(flagDietPromotionGoodsR != 1){
                                resultJSON.setSuccess("1");
                                resultJSON.setMsg("促销活动保存失败");
                                return resultJSON;
                            }
                        }
                        else{
                            resultJSON.setSuccess("1");
                            resultJSON.setMsg("促销活动保存失败");
                            return resultJSON;
                        }
                    }
                }
                for(int k = 0; k < branchIds.length; k++){
                    Map<String, Object> mapDietPromotionBranchR = new HashMap<>();
                    mapDietPromotionBranchR.put("dietPromotionId", dietPromotion.getId());
                    mapDietPromotionBranchR.put("branchId", BigInteger.valueOf(Long.valueOf(branchIds[k])));
                    mapDietPromotionBranchR.put("isDeleted", 0);
                    mapDietPromotionBranchR.put("createBy", "administrator");
                    mapDietPromotionBranchR.put("lastUpdateBy", "administrator");
                    mapDietPromotionBranchR.put("createAt", new Date());
                    mapDietPromotionBranchR.put("lastUpdateAt", new Date());
                    mapDietPromotionBranchR.put("tenantId", dietPromotion.getTenantId());
                    int flagDietPromotionBranchR = dietPromotionBranchRMapper.insert(mapDietPromotionBranchR);
                    if(flagDietPromotionBranchR == 1){
                        resultJSON.setSuccess("0");
                        resultJSON.setMsg("促销活动保存成功");
                    }
                    else{
                        resultJSON.setSuccess("1");
                        resultJSON.setMsg("促销活动保存失败");
                        return resultJSON;
                    }
                }
            }
        }
        else{
            resultJSON.setSuccess("1");
            resultJSON.setMsg("促销活动保存失败");
        }
        return resultJSON;
    }

    /**
     * 新增卡券活动
     */
    public ResultJSON addCardCouponsActivity(Map params) throws ServiceException, ParseException{
        ResultJSON resultJSON = new ResultJSON();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat simpleDateFormat1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String[] branchIds = ((String)params.get("branchIds")).split(",");
        BigInteger tenantId = new BigInteger((String)params.get("tenantId"));
        BigInteger cardId = new BigInteger((String)params.get("cardId"));
        for(int k = 0; k < branchIds.length; k++){
            BigInteger branchId = new BigInteger(branchIds[k]);
            Map<String, Object> map = new HashMap<>();
            map.put("tenantId", tenantId);
            map.put("cardId", cardId);
            if("1".equals(params.get("precisionUse"))){
                map.put("precisionUse", 1);
            }
            else{
                map.put("precisionUse", 0);
            }
            List<DietPromotionFestival> dietPromotionFestivals = dietPromotionFestivalMapper.dietPromotionFestivalList(map);
            for(int n = 0; n < dietPromotionFestivals.size(); n++){
                Map<String, Object> map1 = new HashMap<>();
                map1.put("id", dietPromotionFestivals.get(n).getDietPromotionId());
                map1.put("queryCardCoupnseAndGame", 1);
                List<DietPromotion> dietPromotions = dietPromotionMapper.dietPromotionList(map1);
                if(dietPromotions != null){
                    for(int i = 0; i < dietPromotions.size(); i++){
                        Map<String, Object> map2 = new HashMap<>();
                        map2.put("dietPromotionId", dietPromotions.get(i).getId());
                        List<DietPromotionBranchR> dietPromotionBranchRs = dietPromotionBranchRMapper.dietPromotionBranchRList(map2);
                        if(dietPromotionBranchRs != null){
                            for(int j = 0; j < dietPromotionBranchRs.size(); j++){
                                if((dietPromotionBranchRs.get(j).getBranchId()).compareTo(branchId) == 0){
                                    Date oldSDT = simpleDateFormat1.parse(dietPromotions.get(i).getStartDate().toString().substring(0, 10) + " " + dietPromotions.get(i).getStartTime().toString());
                                    Date oldEDT = simpleDateFormat1.parse(dietPromotions.get(i).getEndDate().toString().substring(0, 10) + " " + dietPromotions.get(i).getEndTime().toString());
                                    Date newSDT = simpleDateFormat1.parse(params.get("startDate") + " 00:00:00");
                                    Date newEDT = simpleDateFormat1.parse(params.get("endDate") + " 23:59:59");
                                    if(newSDT.after(oldEDT) || newEDT.before(oldSDT)){
                                    }
                                    else{
                                        resultJSON.setSuccess("1");
                                        resultJSON.setMsg("同时间段内已有该活动或代金券，请重新设置活动时间段！");
                                        return resultJSON;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        Map festivalMap=new HashMap();
        festivalMap.put("tenantId", tenantId);
        festivalMap.put("cardId", cardId);
        festivalMap.put("precisionUse", 1);
        List<DietPromotionFestival> festivalList = dietPromotionFestivalMapper.dietPromotionFestivalList(festivalMap);
        if(festivalList.size()>0){
            resultJSON.setSuccess("0");
            resultJSON.setMsg("已有活动，无需再次创建");
            resultJSON.setObject(festivalList.get(0));
            return resultJSON;
        }

        Map<String, Object> mapDietPromotion = new HashMap<>();
        mapDietPromotion.put("tenantId", params.get("tenantId"));
        mapDietPromotion.put("promotionType", 8);
        if(params.get("scope") != null){
            mapDietPromotion.put("scope", params.get("scope"));
        }
        else{
            mapDietPromotion.put("scope", 0);
        }
        if(params.get("forCustomerType") != null){
            mapDietPromotion.put("forCustomerType", params.get("forCustomerType"));
        }
        else{
            mapDietPromotion.put("forCustomerType", 0);
        }
        if(params.get("promotionStatus") != null){
            mapDietPromotion.put("promotionStatus", params.get("promotionStatus"));
        }
        else{
            mapDietPromotion.put("promotionStatus", "0");
        }
        if(params.get("myBranchId")!=null&&!params.get("myBranchId").toString().equals("")){
            mapDietPromotion.put("createBranchId", params.get("myBranchId"));
        }else{
            mapDietPromotion.put("createBranchId", params.get("createBranchId"));
        }
        mapDietPromotion.put("startDate", simpleDateFormat.parse((String)params.get("startDate")));
        mapDietPromotion.put("startTime", simpleDateFormat1.parse(params.get("startDate") + " 00:00:00"));
        mapDietPromotion.put("endDate", simpleDateFormat.parse((String)params.get("endDate")));
        mapDietPromotion.put("endTime", simpleDateFormat1.parse(params.get("endDate") + " 23:59:59"));
        mapDietPromotion.put("promotionName", params.get("activityName"));
        String[] weeks = ((String)params.get("weekInsert")).split(",");
        for(int i = 0; i < weeks.length; i++){
            if(i + 1 == 1){
                if(Integer.valueOf(weeks[i]) == 1){
                    mapDietPromotion.put("applyToMon", true);
                }
                else{
                    mapDietPromotion.put("applyToMon", false);
                }
            }
            if(i + 1 == 2){
                if(Integer.valueOf(weeks[i]) == 1){
                    mapDietPromotion.put("applyToTue", true);
                }
                else{
                    mapDietPromotion.put("applyToTue", false);
                }
            }
            if(i + 1 == 3){
                if(Integer.valueOf(weeks[i]) == 1){
                    mapDietPromotion.put("applyToWed", true);
                }
                else{
                    mapDietPromotion.put("applyToWed", false);
                }
            }
            if(i + 1 == 4){
                if(Integer.valueOf(weeks[i]) == 1){
                    mapDietPromotion.put("applyToThu", true);
                }
                else{
                    mapDietPromotion.put("applyToThu", false);
                }
            }
            if(i + 1 == 5){
                if(Integer.valueOf(weeks[i]) == 1){
                    mapDietPromotion.put("applyToFri", true);
                }
                else{
                    mapDietPromotion.put("applyToFri", false);
                }
            }
            if(i + 1 == 6){
                if(Integer.valueOf(weeks[i]) == 1){
                    mapDietPromotion.put("applyToSat", true);
                }
                else{
                    mapDietPromotion.put("applyToSat", false);
                }
            }
            if(i + 1 == 7){
                if(Integer.valueOf(weeks[i]) == 1){
                    mapDietPromotion.put("applyToSun", true);
                }
                else{
                    mapDietPromotion.put("applyToSun", false);
                }
            }
        }
        String md_code = vipService.getNextSelValue(SysConfig.SYS_DIETPROMOTION_NUM, 4, new BigInteger(params.get("tenantId").toString()));
        String y_M_d = Common.getNowCorrect2Millisecond().substring(2, 8);//六位年月日
        mapDietPromotion.put("promotionCode", "CX" + y_M_d + md_code);
        mapDietPromotion.put("isSuperposition", false);
        mapDietPromotion.put("memo", params.get("memo"));
        mapDietPromotion.put("isDeleted", 0);
        mapDietPromotion.put("promotionMame", params.get("activityName"));
        mapDietPromotion.put("isUse", 1);
        mapDietPromotion.put("createBy", "administrator");
        mapDietPromotion.put("lastUpdateBy", "administrator");
        mapDietPromotion.put("createAt", new Date());
        mapDietPromotion.put("lastUpdateAt", new Date());
        mapDietPromotion.put("effectiveInterval", "00:00-23:59");
        int flagDietPromotion = dietPromotionMapper.insert(mapDietPromotion);
        if(flagDietPromotion == 1){
            ApiRest ass = tenantConfigService.addTenantConfigOne(BigInteger.valueOf(Long.valueOf((params.get("tenantId")).toString())), SysConfig.SYS_DIETPROMOTION_NUM);
            if(!ass.getIsSuccess()){
                resultJSON.setSuccess("1");
                resultJSON.setMsg(ass.getError());
                return resultJSON;
            }
            Map<String, Object> mapFestival = new HashMap<>();
            mapFestival.put("dietPromotionId", mapDietPromotion.get("id"));
            mapFestival.put("tenantId", params.get("tenantId"));
            mapFestival.put("activityName", params.get("activityName"));
            mapFestival.put("cardId", params.get("cardId"));
            mapFestival.put("limitPerOne", -1);
            mapFestival.put("sendLimitValue", params.get("forCustomerCondition"));
            mapFestival.put("totalInventory", Integer.parseInt((String)params.get("amount")));
            mapFestival.put("remainInventory", Integer.parseInt((String)params.get("amount")));
            mapFestival.put("isDeleted", 0);
            mapFestival.put("createBy", "administrator");
            mapFestival.put("lastUpdateBy", "administrator");
            mapFestival.put("createAt", new Date());
            mapFestival.put("lastUpdateAt", new Date());
            if("1".equals(params.get("precisionUse"))){
                mapFestival.put("precisionUse", 1);
            }
            else{
                mapFestival.put("precisionUse", 0);
            }
            int flagFestival = dietPromotionFestivalMapper.insert(mapFestival);
            if(flagFestival == 1){
                if(branchIds != null){
                    for(int i = 0; i < branchIds.length; i++){
                        Map<String, Object> mapDietPromotionBranchR = new HashMap<>();
                        mapDietPromotionBranchR.put("dietPromotionId", mapDietPromotion.get("id"));
                        mapDietPromotionBranchR.put("branchId", BigInteger.valueOf(Long.valueOf(branchIds[i])));
                        mapDietPromotionBranchR.put("isDeleted", 0);
                        mapDietPromotionBranchR.put("createBy", "administrator");
                        mapDietPromotionBranchR.put("lastUpdateBy", "administrator");
                        mapDietPromotionBranchR.put("createAt", new Date());
                        mapDietPromotionBranchR.put("lastUpdateAt", new Date());
                        mapDietPromotionBranchR.put("tenantId", params.get("tenantId"));
                        int flagDietPromotionBranchR = dietPromotionBranchRMapper.insert(mapDietPromotionBranchR);
                        if(flagDietPromotionBranchR == 1){
                            resultJSON.setSuccess("0");
                            resultJSON.setMsg("促销活动保存成功");
                            resultJSON.setObject(mapFestival);
                        }
                        else{
                            resultJSON.setSuccess("1");
                            resultJSON.setMsg("促销活动保存失败");
                            return resultJSON;
                        }
                    }
                }
            }
            else{
                resultJSON.setSuccess("1");
                resultJSON.setMsg("促销活动保存失败");
            }
        }
        else{
            resultJSON.setSuccess("1");
            resultJSON.setMsg("促销活动保存失败");
        }
        return resultJSON;
    }

    /**
     * 新增支付促销活动
     */
    public ResultJSON addPayPreferential(Map params, DietPromotion dietPromotion) throws ServiceException, ParseException{
        ResultJSON resultJSON = new ResultJSON();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat simpleDateFormat1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String[] branchIds = ((String)params.get("branchIds")).split(",");
        for(int k = 0; k < branchIds.length; k++){
            BigInteger branchId = new BigInteger(branchIds[k]);
            BigInteger tenantId = new BigInteger((String)params.get("tenantId"));
            Map<String, Object> map = new HashMap<>();
            map.put("tenantId", tenantId);
            map.put("promotionType", 13);
            List<DietPromotion> dietPromotions = dietPromotionMapper.dietPromotionList(map);
            if(dietPromotions != null){
                for(int i = 0; i < dietPromotions.size(); i++){
                    Map<String, Object> mapDietPromotionBranchR = new HashMap<>();
                    mapDietPromotionBranchR.put("dietPromotionId", dietPromotions.get(i).getId());
                    List<DietPromotionBranchR> dietPromotionBranchRs = dietPromotionBranchRMapper.dietPromotionBranchRList(mapDietPromotionBranchR);
                    if(dietPromotionBranchRs != null){
                        for(int j = 0; j < dietPromotionBranchRs.size(); j++){
                            if((dietPromotionBranchRs.get(j).getBranchId()).compareTo(branchId) == 0){
                                Date oldSDT = simpleDateFormat1.parse(dietPromotions.get(i).getStartDate().toString().substring(0, 10) + " " + dietPromotions.get(i).getStartTime().toString());
                                Date oldEDT = simpleDateFormat1.parse(dietPromotions.get(i).getEndDate().toString().substring(0, 10) + " " + dietPromotions.get(i).getEndTime().toString());
                                Date newSDT = simpleDateFormat1.parse(params.get("start") + " 00:00:00");
                                Date newEDT = simpleDateFormat1.parse(params.get("end") + " 23:59:59");
                                if(newSDT.after(oldEDT) || newEDT.before(oldSDT)){
                                }
                                else{
                                    Map<String, Object> mapDietPromotion = new HashMap<>();
                                    mapDietPromotion.put("id", dietPromotionBranchRs.get(j).getDietPromotionId());
                                    List<DietPromotion> dietPromotionList = dietPromotionMapper.dietPromotionList(mapDietPromotion);
                                    if(dietPromotionList != null){
                                        DietPromotion dietPromotion1 = dietPromotionList.get(0);
                                        if(dietPromotion1.getScope() == 0){
                                            if(dietPromotion1.getForCustomerType() == 0){
                                                resultJSON.setSuccess("1");
                                                resultJSON.setMsg("同时间段内已有活动，请重新设置活动时间段！");
                                                return resultJSON;
                                            }
                                            else if(dietPromotion1.getForCustomerType() == 3){
                                                if("3".equals(params.get("forCustomerType"))){
                                                    if(BigInteger.valueOf(Long.valueOf((String)params.get("vipType"))) == dietPromotion1.getForVipType()){
                                                        resultJSON.setSuccess("1");
                                                        resultJSON.setMsg("同时间段内已有活动，请重新设置活动时间段！");
                                                        return resultJSON;
                                                    }
                                                }
                                                else{
                                                    if(Integer.valueOf((String)params.get("forCustomerType")) == 0 || Integer.valueOf((String)params.get("forCustomerType")) == 1 || Integer.valueOf((String)params.get("forCustomerType")) == dietPromotion.getForCustomerType()){
                                                        resultJSON.setSuccess("1");
                                                        resultJSON.setMsg("同时间段内已有活动，请重新设置活动时间段！");
                                                        return resultJSON;
                                                    }
                                                }
                                            }
                                            else{
                                                if(Integer.valueOf((String)params.get("forCustomerType")) == 0 || Integer.valueOf((String)params.get("forCustomerType")) == dietPromotion1.getForCustomerType()){
                                                    resultJSON.setSuccess("1");
                                                    resultJSON.setMsg("同时间段内已有活动，请重新设置活动时间段！");
                                                    return resultJSON;
                                                }
                                            }
                                        }
                                        else{
                                            if(Integer.valueOf((String)params.get("scope")) == 0 || Integer.valueOf((String)params.get("scope")) == dietPromotion1.getScope()){
                                                if(dietPromotion1.getForCustomerType() == 0){
                                                    resultJSON.setSuccess("1");
                                                    resultJSON.setMsg("同时间段内已有活动，请重新设置活动时间段！");
                                                    return resultJSON;
                                                }
                                                else if(dietPromotion1.getForCustomerType() == 3){
                                                    if("3".equals(params.get("forCustomerType"))){
                                                        if(BigInteger.valueOf(Long.valueOf((String)params.get("vipType"))) == dietPromotion1.getForVipType()){
                                                            resultJSON.setSuccess("1");
                                                            resultJSON.setMsg("同时间段内已有活动，请重新设置活动时间段！");
                                                            return resultJSON;
                                                        }
                                                    }
                                                    else{
                                                        if(Integer.valueOf((String)params.get("forCustomerType")) == 0 || Integer.valueOf((String)params.get("forCustomerType")) == 1 || Integer.valueOf((String)params.get("forCustomerType")) == dietPromotion.getForCustomerType()){
                                                            resultJSON.setSuccess("1");
                                                            resultJSON.setMsg("同时间段内已有活动，请重新设置活动时间段！");
                                                            return resultJSON;
                                                        }
                                                    }
                                                }
                                                else{
                                                    if(Integer.valueOf((String)params.get("forCustomerType")) == 0 || Integer.valueOf((String)params.get("forCustomerType")) == dietPromotion1.getForCustomerType()){
                                                        resultJSON.setSuccess("1");
                                                        resultJSON.setMsg("同时间段内已有活动，请重新设置活动时间段！");
                                                        return resultJSON;
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        Date startDate = simpleDateFormat.parse((String)params.get("start"));
        Date endDate = simpleDateFormat.parse((String)params.get("end"));
        Date startTime = simpleDateFormat1.parse(params.get("start") + " 00:00:00");
        Date endTime = simpleDateFormat1.parse(params.get("end") + " 23:59:59");
        dietPromotion.setStartDate(new java.sql.Date(startDate.getTime()));
        dietPromotion.setStartTime(new java.sql.Time(startTime.getTime()));
        dietPromotion.setEndDate(new java.sql.Date(endDate.getTime()));
        dietPromotion.setEndTime(new java.sql.Time(endTime.getTime()));

        dietPromotion.setTenantId(new BigInteger((String)params.get("tenantId")));
        dietPromotion.setCreateBranchId(new BigInteger((String)params.get("myBranchId")));
        if("3".equals(params.get("forCustomerType"))){
            dietPromotion.setForVipType(new BigInteger((String)params.get("vipType")));
        }
        else{
            dietPromotion.setForVipType(new BigInteger("0"));
        }
        String index = vipService.getNextSelValue(SysConfig.SYS_DIETPROMOTION_NUM, 4, new BigInteger(params.get("tenantId").toString()));
        String y_M_d = Common.getNowCorrect2Millisecond().substring(2, 8);//六位年月日
        dietPromotion.setPromotionCode("CX" + y_M_d + index);

        String[] weeks = ((String)params.get("weekInsert")).split(",");
        if(weeks == null){
            resultJSON.setMsg("未选择星期");
            resultJSON.setSuccess("1");
            return resultJSON;
        }
        else{
            for(int i = 0; i < weeks.length; i++){
                if(i + 1 == 1){
                    if(Integer.valueOf(weeks[i]) == 1){
                        dietPromotion.setApplyToMon(1);
                    }
                    else{
                        dietPromotion.setApplyToMon(0);
                    }
                }
                if(i + 1 == 2){
                    if(Integer.valueOf(weeks[i]) == 1){
                        dietPromotion.setApplyToTue(1);
                    }
                    else{
                        dietPromotion.setApplyToTue(0);
                    }
                }
                if(i + 1 == 3){
                    if(Integer.valueOf(weeks[i]) == 1){
                        dietPromotion.setApplyToWed(1);
                    }
                    else{
                        dietPromotion.setApplyToWed(0);
                    }
                }
                if(i + 1 == 4){
                    if(Integer.valueOf(weeks[i]) == 1){
                        dietPromotion.setApplyToThu(1);
                    }
                    else{
                        dietPromotion.setApplyToThu(0);
                    }
                }
                if(i + 1 == 5){
                    if(Integer.valueOf(weeks[i]) == 1){
                        dietPromotion.setApplyToFri(1);
                    }
                    else{
                        dietPromotion.setApplyToFri(0);
                    }
                }
                if(i + 1 == 6){
                    if(Integer.valueOf(weeks[i]) == 1){
                        dietPromotion.setApplyToSat(1);
                    }
                    else{
                        dietPromotion.setApplyToSat(0);
                    }
                }
                if(i + 1 == 7){
                    if(Integer.valueOf(weeks[i]) == 1){
                        dietPromotion.setApplyToSun(1);
                    }
                    else{
                        dietPromotion.setApplyToSun(0);
                    }
                }
            }
        }
        dietPromotion.setSuperposition(false);
        dietPromotion.setDeleted(false);
        dietPromotion.setPromotionName((String)params.get("promotionName"));
        dietPromotion.setUse(true);
        dietPromotion.setCreateBy("administrator");
        dietPromotion.setLastUpdateBy("administrator");
        dietPromotion.setCreateAt(new Date());
        dietPromotion.setLastUpdateAt(new Date());
        int flag = dietPromotionMapper.insert(dietPromotion);
        if(flag == 1){
            ApiRest ass = tenantConfigService.addTenantConfigOne(BigInteger.valueOf(Long.valueOf(((String)params.get("tenantId")))), SysConfig.SYS_DIETPROMOTION_NUM);
            if(!ass.getIsSuccess()){
                resultJSON.setSuccess("1");
                resultJSON.setMsg(ass.getError());
                return resultJSON;
            }
            if(dietPromotion.getPromotionType() == 13){
                Map<String, Object> mapDietPromotionPayback = new HashMap<>();
                mapDietPromotionPayback.put("dietPromotionId", dietPromotion.getId());
                mapDietPromotionPayback.put("satisfy", new BigDecimal((String)params.get("forCustomerCondition")));
                mapDietPromotionPayback.put("tenantId", dietPromotion.getTenantId());
                mapDietPromotionPayback.put("discount", new BigDecimal((String)params.get("discount")));
                mapDietPromotionPayback.put("payType", new BigInteger((String)params.get("payType")));
                mapDietPromotionPayback.put("remark", params.get("memo"));
                mapDietPromotionPayback.put("isDeleted", 0);
                mapDietPromotionPayback.put("createBy", "administrator");
                mapDietPromotionPayback.put("lastUpdateBy", "administrator");
                mapDietPromotionPayback.put("createAt", new Date());
                mapDietPromotionPayback.put("lastUpdateAt", new Date());
                if(params.get("goodsId") != null){
                    mapDietPromotionPayback.put("goodsId", params.get("goodsId"));
                }
                if(params.get("goodsName") != null){
                    mapDietPromotionPayback.put("goodsName", params.get("goodsName"));
                }
                int flagDietPromotionPayback = dietPromotionPaybackMapper.insert(mapDietPromotionPayback);
                if(flagDietPromotionPayback == 1){
                    if(branchIds != null){
                        for(int i = 0; i < branchIds.length; i++){
                            Map<String, Object> mapDietPromotionBranchR = new HashMap<>();
                            mapDietPromotionBranchR.put("dietPromotionId", dietPromotion.getId());
                            mapDietPromotionBranchR.put("branchId", BigInteger.valueOf(Long.valueOf(branchIds[i])));
                            mapDietPromotionBranchR.put("isDeleted", 0);
                            mapDietPromotionBranchR.put("createBy", "administrator");
                            mapDietPromotionBranchR.put("lastUpdateBy", "administrator");
                            mapDietPromotionBranchR.put("createAt", new Date());
                            mapDietPromotionBranchR.put("lastUpdateAt", new Date());
                            mapDietPromotionBranchR.put("tenantId", dietPromotion.getTenantId());
                            int flagDietPromotionBranchR = dietPromotionBranchRMapper.insert(mapDietPromotionBranchR);
                            if(flagDietPromotionBranchR == 1){
                                resultJSON.setSuccess("0");
                                resultJSON.setMsg("促销活动保存成功");
                            }
                            else{
                                resultJSON.setSuccess("1");
                                resultJSON.setMsg("促销活动保存失败");
                                return resultJSON;
                            }
                        }
                    }
                }
                else{
                    resultJSON.setSuccess("1");
                    resultJSON.setMsg("促销活动保存失败");
                }
            }
        }
        else{
            resultJSON.setSuccess("1");
            resultJSON.setMsg("促销活动保存失败");
        }
        return resultJSON;
    }

    /**
     * 新增卡券游戏活动
     */
    public ResultJSON addGame(Map params, DietPromotion dietPromotion) throws ServiceException, ParseException{
        ResultJSON resultJSON = new ResultJSON();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat simpleDateFormat1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String[] branchIds = ((String)params.get("branchIds")).split(",");
        String createBranchId = (String)params.get("myBranchId");
        BigInteger tenantId = new BigInteger((String)params.get("tenantId"));
        Map<String, Object> map0 = new HashMap<>();
        map0.put("tenantId", tenantId);
        map0.put("promotionType", 12);
        List<DietPromotion> dietPromotions1 = dietPromotionMapper.getDietPromotionList(map0);
        if(dietPromotions1 != null){
            for(int i = 0; i < dietPromotions1.size(); i++){
                Map<String, Object> mapDietPromotionBranchR = new HashMap<>();
                mapDietPromotionBranchR.put("dietPromotionId", dietPromotions1.get(i).getId());
                List<DietPromotionBranchR> dietPromotionBranchRs = dietPromotionBranchRMapper.dietPromotionBranchRList(mapDietPromotionBranchR);
                if(dietPromotionBranchRs != null){
                    for(int j = 0; j < dietPromotionBranchRs.size(); j++){
                        if(params.get("branchIds").toString().contains(dietPromotionBranchRs.get(j).getBranchId().toString())){
                            Date oldSDT = simpleDateFormat1.parse(dietPromotions1.get(i).getStartDate().toString().substring(0, 10) + " " + dietPromotions1.get(i).getStartTime().toString());
                            Date oldEDT = simpleDateFormat1.parse(dietPromotions1.get(i).getEndDate().toString().substring(0, 10) + " " + dietPromotions1.get(i).getEndTime().toString());
                            Date newSDT = simpleDateFormat1.parse(params.get("start") + " 00:00:00");
                            Date newEDT = simpleDateFormat1.parse(params.get("end") + " 23:59:59");
                            if(newSDT.after(oldEDT) || newEDT.before(oldSDT)){
                            }
                            else{
                                resultJSON.setSuccess("1");
                                resultJSON.setMsg("同时间段内已有活动，请重新设置活动时间段！");
                                return resultJSON;
                            }
                        }
                    }
                }
            }
        }
        Date startDate = simpleDateFormat.parse((String)params.get("start"));
        Date endDate = simpleDateFormat.parse((String)params.get("end"));
        Date startTime = simpleDateFormat1.parse(params.get("start") + " 00:00:00");
        Date endTime = simpleDateFormat1.parse(params.get("end") + " 23:59:59");
        if(params.get("giftGoodsOne") != null){
            if(Integer.parseInt((String)params.get("giftGoodsOne")) != 0){
                Map<String, Object> mapDietPromotionFestival = new HashMap<>();
                mapDietPromotionFestival.put("id", new BigInteger((String)params.get("giftGoodsOne")));
                List<DietPromotionFestival> dietPromotionFestivals = dietPromotionFestivalMapper.dietPromotionFestivalList(mapDietPromotionFestival);
                if(dietPromotionFestivals != null){
                    DietPromotionFestival dietPromotionFestival = dietPromotionFestivals.get(0);
                    Map<String, Object> mapDietPromotion = new HashMap<>();
                    mapDietPromotion.put("id", dietPromotionFestival.getDietPromotionId());
                    List<DietPromotion> dietPromotions = dietPromotionMapper.getDietPromotionList(mapDietPromotion);
                    if(dietPromotions != null){
                        if(dietPromotions.size() > 0){
                            DietPromotion dietPromotion1 = dietPromotions.get(0);
                            Date feativalSDT = simpleDateFormat1.parse(dietPromotion1.getStartDate().toString().substring(0, 10) + " " + dietPromotion1.getStartTime().toString());
                            Date festivalEDT = simpleDateFormat1.parse(dietPromotion1.getEndDate().toString().substring(0, 10) + " " + dietPromotion1.getEndTime().toString());
                            if(startTime.before(feativalSDT) || endTime.after(festivalEDT)){
                                resultJSON.setSuccess("1");
                                resultJSON.setMsg("游戏活动时间范围不能大于选择的卡券活动范围！");
                                return resultJSON;
                            }
                        }
                    }
                }
            }
        }
        if(params.get("giftGoodsTwo") != null){
            if(Integer.parseInt((String)params.get("giftGoodsTwo")) != 0){
                Map<String, Object> mapDietPromotionFestival = new HashMap<>();
                mapDietPromotionFestival.put("id", new BigInteger((String)params.get("giftGoodsTwo")));
                List<DietPromotionFestival> dietPromotionFestivals = dietPromotionFestivalMapper.dietPromotionFestivalList(mapDietPromotionFestival);
                if(dietPromotionFestivals != null){
                    DietPromotionFestival dietPromotionFestival = dietPromotionFestivals.get(0);
                    Map<String, Object> mapDietPromotion = new HashMap<>();
                    mapDietPromotion.put("id", dietPromotionFestival.getDietPromotionId());
                    List<DietPromotion> dietPromotions = dietPromotionMapper.dietPromotionList(mapDietPromotion);
                    if(dietPromotions != null){
                        if(dietPromotions.size() > 0){
                            DietPromotion dietPromotion1 = dietPromotions.get(0);
                            Date feativalSDT = simpleDateFormat1.parse(dietPromotion1.getStartDate().toString().substring(0, 10) + " " + dietPromotion1.getStartTime().toString());
                            Date festivalEDT = simpleDateFormat1.parse(dietPromotion1.getEndDate().toString().substring(0, 10) + " " + dietPromotion1.getEndTime().toString());
                            if(!(startTime.after(feativalSDT) && endTime.before(festivalEDT))){
                                resultJSON.setSuccess("1");
                                resultJSON.setMsg("游戏活动时间范围不能大于选择的卡券活动范围！");
                                return resultJSON;
                            }
                        }
                    }
                }
            }
        }
        if(params.get("giftGoodsThree") != null){
            if(Integer.parseInt((String)params.get("giftGoodsThree")) != 0){
                Map<String, Object> mapDietPromotionFestival = new HashMap<>();
                mapDietPromotionFestival.put("id", new BigInteger((String)params.get("giftGoodsThree")));
                List<DietPromotionFestival> dietPromotionFestivals = dietPromotionFestivalMapper.dietPromotionFestivalList(mapDietPromotionFestival);
                if(dietPromotionFestivals != null){
                    DietPromotionFestival dietPromotionFestival = dietPromotionFestivals.get(0);
                    Map<String, Object> mapDietPromotion = new HashMap<>();
                    mapDietPromotion.put("id", dietPromotionFestival.getDietPromotionId());
                    List<DietPromotion> dietPromotions = dietPromotionMapper.dietPromotionList(mapDietPromotion);
                    if(dietPromotions != null){
                        if(dietPromotions.size() > 0){
                            DietPromotion dietPromotion1 = dietPromotions.get(0);
                            Date feativalSDT = simpleDateFormat1.parse(dietPromotion1.getStartDate().toString().substring(0, 10) + " " + dietPromotion1.getStartTime().toString());
                            Date festivalEDT = simpleDateFormat1.parse(dietPromotion1.getEndDate().toString().substring(0, 10) + " " + dietPromotion1.getEndTime().toString());
                            if(!(startTime.after(feativalSDT) && endTime.before(festivalEDT))){
                                resultJSON.setSuccess("1");
                                resultJSON.setMsg("游戏活动时间范围不能大于选择的卡券活动范围！");
                                return resultJSON;
                            }
                        }
                    }
                }
            }
        }
        BigDecimal winRate = new BigDecimal("0.00");
        if(params.get("winRateOne") != null && params.get("winRateTwo") == null && params.get("winRateThree") == null){
            winRate = winRate.add(new BigDecimal((String)params.get("winRateOne")));
        }
        if(params.get("winRateOne") == null && params.get("winRateTwo") != null && params.get("winRateThree") == null){
            winRate = winRate.add(new BigDecimal((String)params.get("winRateTwo")));
        }
        if(params.get("winRateOne") == null && params.get("winRateTwo") == null && params.get("winRateThree") != null){
            winRate = winRate.add(new BigDecimal((String)params.get("winRateThree")));
        }
        if(params.get("winRateOne") != null && params.get("winRateTwo") != null && params.get("winRateThree") == null){
            winRate = winRate.add(new BigDecimal((String)params.get("winRateOne"))).add(new BigDecimal((String)params.get("winRateTwo")));
        }
        if(params.get("winRateOne") != null && params.get("winRateTwo") == null && params.get("winRateThree") != null){
            winRate = winRate.add(new BigDecimal((String)params.get("winRateOne"))).add(new BigDecimal((String)params.get("winRateThree")));
        }
        if(params.get("winRateOne") == null && params.get("winRateTwo") != null && params.get("winRateThree") != null){
            winRate = winRate.add(new BigDecimal((String)params.get("winRateTwo"))).add(new BigDecimal((String)params.get("winRateThree")));
        }
        if(params.get("winRateOne") != null && params.get("winRateTwo") != null && params.get("winRateThree") != null){
            winRate = winRate.add(new BigDecimal((String)params.get("winRateOne"))).add(new BigDecimal((String)params.get("winRateTwo"))).add(new BigDecimal((String)params.get("winRateThree")));
        }
        if((new BigDecimal("100")).compareTo(winRate) < 0){
            resultJSON.setSuccess("1");
            resultJSON.setMsg("中奖率之和不能大于100%");
            return resultJSON;
        }
        dietPromotion.setStartDate(new java.sql.Date(startDate.getTime()));
        dietPromotion.setStartTime(new java.sql.Time(startTime.getTime()));
        dietPromotion.setEndDate(new java.sql.Date(endDate.getTime()));
        dietPromotion.setEndTime(new java.sql.Time(endTime.getTime()));

        dietPromotion.setTenantId(new BigInteger((String)params.get("tenantId")));
        dietPromotion.setCreateBranchId(new BigInteger((String)params.get("myBranchId")));

        String md_id = createBranchId;
        Map map = new HashMap<>();
        map.put("branchId", md_id);
        map.put("tenantId", params.get("tenantId"));
        String index = vipService.getNextSelValue(SysConfig.SYS_DIETPROMOTION_NUM, 4, new BigInteger(params.get("tenantId").toString()));
        String y_M_d = Common.getNowCorrect2Millisecond().substring(2, 8);//六位年月日
        dietPromotion.setPromotionCode("CX" + y_M_d + index);

        String[] weeks = ((String)params.get("weekInsert")).split(",");
        if(weeks == null){
            resultJSON.setMsg("未选择星期");
            resultJSON.setSuccess("1");
            return resultJSON;
        }
        else{
            for(int i = 0; i < weeks.length; i++){
                if(i + 1 == 1){
                    if(Integer.valueOf(weeks[i]) == 1){
                        dietPromotion.setApplyToMon(1);
                    }
                    else{
                        dietPromotion.setApplyToMon(0);
                    }
                }
                if(i + 1 == 2){
                    if(Integer.valueOf(weeks[i]) == 1){
                        dietPromotion.setApplyToTue(1);
                    }
                    else{
                        dietPromotion.setApplyToTue(0);
                    }
                }
                if(i + 1 == 3){
                    if(Integer.valueOf(weeks[i]) == 1){
                        dietPromotion.setApplyToWed(1);
                    }
                    else{
                        dietPromotion.setApplyToWed(0);
                    }
                }
                if(i + 1 == 4){
                    if(Integer.valueOf(weeks[i]) == 1){
                        dietPromotion.setApplyToThu(1);
                    }
                    else{
                        dietPromotion.setApplyToThu(0);
                    }
                }
                if(i + 1 == 5){
                    if(Integer.valueOf(weeks[i]) == 1){
                        dietPromotion.setApplyToFri(1);
                    }
                    else{
                        dietPromotion.setApplyToFri(0);
                    }
                }
                if(i + 1 == 6){
                    if(Integer.valueOf(weeks[i]) == 1){
                        dietPromotion.setApplyToSat(1);
                    }
                    else{
                        dietPromotion.setApplyToSat(0);
                    }
                }
                if(i + 1 == 7){
                    if(Integer.valueOf(weeks[i]) == 1){
                        dietPromotion.setApplyToSun(1);
                    }
                    else{
                        dietPromotion.setApplyToSun(0);
                    }
                }
            }
        }
        dietPromotion.setSuperposition(false);
        dietPromotion.setDeleted(false);
        dietPromotion.setPromotionName((String)params.get("activityName"));
        dietPromotion.setUse(true);
        dietPromotion.setCreateBy("administrator");
        dietPromotion.setLastUpdateBy("administrator");
        dietPromotion.setCreateAt(new Date());
        dietPromotion.setScope(0);
        dietPromotion.setForCustomerType(0);
        dietPromotion.setLastUpdateAt(new Date());
        dietPromotion.setEffectiveInterval("00:00-23:59");
        int flag = dietPromotionMapper.insert(dietPromotion);
        if(flag == 1){
            ApiRest ass = tenantConfigService.addTenantConfigOne(tenantId, SysConfig.SYS_DIETPROMOTION_NUM);
            if(!ass.getIsSuccess()){
                resultJSON.setSuccess("1");
                resultJSON.setMsg(ass.getError());
                return resultJSON;
            }
            if(dietPromotion.getPromotionType() == 12){
                Map<String, Object> mapDietGameScratchCard = new HashMap<>();
                mapDietGameScratchCard.put("dietPromotionId", dietPromotion.getId());
                mapDietGameScratchCard.put("tenantId", dietPromotion.getTenantId());
                mapDietGameScratchCard.put("gameName", params.get("activityName"));
                mapDietGameScratchCard.put("attendTimes", Integer.parseInt((String)params.get("amount")));
                mapDietGameScratchCard.put("remark", params.get("remark"));
                mapDietGameScratchCard.put("isDeleted", 0);
                mapDietGameScratchCard.put("createBy", "administrator");
                mapDietGameScratchCard.put("lastUpdateBy", "administrator");
                mapDietGameScratchCard.put("createAt", new Date());
                mapDietGameScratchCard.put("lastUpdateAt", new Date());
                int flagDietGameScratchCard = dietGameScratchCardMapper.insert(mapDietGameScratchCard);
                if(flagDietGameScratchCard == 1){
                    String giveName = null;
                    String winRates = null;
                    String number = null;
                    String giftGoods = null;
                    Map<String, Object> mapDietGamePrizeItem = new HashMap<>();
                    mapDietGamePrizeItem.put("dietPromotionId", dietPromotion.getId());
                    mapDietGamePrizeItem.put("tenantId", dietPromotion.getTenantId());
                    mapDietGamePrizeItem.put("isDeleted", 0);
                    mapDietGamePrizeItem.put("createBy", "administrator");
                    mapDietGamePrizeItem.put("lastUpdateBy", "administrator");
                    mapDietGamePrizeItem.put("createAt", new Date());
                    mapDietGamePrizeItem.put("lastUpdateAt", new Date());
                    if(params.get("giveNameOne") != null){
                        giveName = (String)params.get("giveNameOne");
                        winRates = (String)params.get("winRateOne");
                        number = (String)params.get("numberOne");
                        mapDietGamePrizeItem.put("itemName", giveName);
                        mapDietGamePrizeItem.put("winningProbability", new BigDecimal(winRates));
                        mapDietGamePrizeItem.put("amount", Integer.parseInt(number));
                        if(params.get("giftGoodsOne") != null && Integer.parseInt(params.get("giftGoodsOne").toString()) != 0){
                            giftGoods = (String)params.get("giftGoodsOne");
                            mapDietGamePrizeItem.put("prizeDietPromotionId", Integer.parseInt(giftGoods));
                            mapDietGamePrizeItem.put("prizeDietPromotionType", Integer.parseInt("8"));
                            Map<String, Object> map1 = new HashMap<>();
                            map1.put("id", giftGoods);
                            DietPromotionFestival dietPromotionFestival = dietPromotionFestivalMapper.findByPromotionId(map1);
                            if(dietPromotionFestival != null){
                                mapDietGamePrizeItem.put("prizeDietPromotionName", dietPromotionFestival.getActivityName());
                            }
                        }
                        else{
                            mapDietGamePrizeItem.put("prizeDietPromotionId", 0);
                            mapDietGamePrizeItem.put("prizeDietPromotionType", 0);
                            mapDietGamePrizeItem.put("prizeDietPromotionName", params.get("giveNameOne"));
                        }
                        dietGamePrizeItemMapper.insert(mapDietGamePrizeItem);
                    }
                    if(params.get("giveNameTwo") != null){
                        giveName = (String)params.get("giveNameTwo");
                        winRates = (String)params.get("winRateTwo");
                        number = (String)params.get("numberTwo");
                        mapDietGamePrizeItem.put("itemName", giveName);
                        mapDietGamePrizeItem.put("winningProbability", new BigDecimal(winRates));
                        mapDietGamePrizeItem.put("amount", Integer.parseInt(number));
                        if(params.get("giftGoodsTwo") != null && Integer.parseInt(params.get("giftGoodsTwo").toString()) != 0){
                            giftGoods = (String)params.get("giftGoodsTwo");
                            mapDietGamePrizeItem.put("prizeDietPromotionId", Integer.parseInt(giftGoods));
                            mapDietGamePrizeItem.put("prizeDietPromotionType", Integer.parseInt("8"));
                            Map<String, Object> map1 = new HashMap<>();
                            map1.put("id", giftGoods);
                            DietPromotionFestival dietPromotionFestival = dietPromotionFestivalMapper.findByPromotionId(map1);
                            if(dietPromotionFestival != null){
                                mapDietGamePrizeItem.put("prizeDietPromotionName", dietPromotionFestival.getActivityName());
                            }
                        }
                        else{
                            mapDietGamePrizeItem.put("prizeDietPromotionId", 0);
                            mapDietGamePrizeItem.put("prizeDietPromotionType", 0);
                            mapDietGamePrizeItem.put("prizeDietPromotionName", params.get("giveNameTwo"));
                        }
                        dietGamePrizeItemMapper.insert(mapDietGamePrizeItem);
                    }
                    if(params.get("giveNameThree") != null){
                        giveName = (String)params.get("giveNameThree");
                        winRates = (String)params.get("winRateThree");
                        number = (String)params.get("numberThree");
                        mapDietGamePrizeItem.put("itemName", giveName);
                        mapDietGamePrizeItem.put("winningProbability", new BigDecimal(winRates));
                        mapDietGamePrizeItem.put("amount", Integer.parseInt(number));
                        if(params.get("giftGoodsThree") != null && Integer.parseInt(params.get("giftGoodsThree").toString()) != 0){
                            giftGoods = (String)params.get("giftGoodsThree");
                            mapDietGamePrizeItem.put("prizeDietPromotionId", Integer.parseInt(giftGoods));
                            mapDietGamePrizeItem.put("prizeDietPromotionType", Integer.parseInt("8"));
                            Map<String, Object> map1 = new HashMap<>();
                            map1.put("id", giftGoods);
                            DietPromotionFestival dietPromotionFestival = dietPromotionFestivalMapper.findByPromotionId(map1);
                            if(dietPromotionFestival != null){
                                mapDietGamePrizeItem.put("prizeDietPromotionName", dietPromotionFestival.getActivityName());
                            }
                        }
                        else{
                            mapDietGamePrizeItem.put("prizeDietPromotionId", 0);
                            mapDietGamePrizeItem.put("prizeDietPromotionType", 0);
                            mapDietGamePrizeItem.put("prizeDietPromotionName", params.get("giveNameThree"));
                        }
                        dietGamePrizeItemMapper.insert(mapDietGamePrizeItem);
                    }
                    if(branchIds != null){
                        for(int i = 0; i < branchIds.length; i++){
                            Map<String, Object> mapDietPromotionBranchR = new HashMap<>();
                            mapDietPromotionBranchR.put("dietPromotionId", dietPromotion.getId());
                            mapDietPromotionBranchR.put("branchId", BigInteger.valueOf(Long.valueOf(branchIds[i])));
                            mapDietPromotionBranchR.put("isDeleted", 0);
                            mapDietPromotionBranchR.put("createBy", "administrator");
                            mapDietPromotionBranchR.put("lastUpdateBy", "administrator");
                            mapDietPromotionBranchR.put("createAt", new Date());
                            mapDietPromotionBranchR.put("lastUpdateAt", new Date());
                            mapDietPromotionBranchR.put("tenantId", dietPromotion.getTenantId());
                            int flagDietPromotionBranchR = dietPromotionBranchRMapper.insert(mapDietPromotionBranchR);
                            if(flagDietPromotionBranchR == 1){
                                resultJSON.setSuccess("0");
                                resultJSON.setMsg("促销活动保存成功");
                                return resultJSON;
                            }
                            else{
                                resultJSON.setSuccess("1");
                                resultJSON.setMsg("促销活动保存失败");
                                return resultJSON;
                            }
                        }
                    }
                }
                else{
                    resultJSON.setSuccess("1");
                    resultJSON.setMsg("促销活动保存失败");
                }
            }
        }
        else{
            resultJSON.setSuccess("1");
            resultJSON.setMsg("促销活动保存失败");
        }
        return resultJSON;
    }

    /**
     * 新增整单优惠促销活动
     */
    public ResultJSON addCardVipActivityAllReduce(Map params, DietPromotion dietPromotion) throws ServiceException, ParseException{
        ResultJSON resultJSON = new ResultJSON();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat simpleDateFormat1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String[] branchIds = ((String)params.get("branchIds")).split(",");
        for(int k = 0; k < branchIds.length; k++){
            BigInteger branchId = new BigInteger(branchIds[k]);
            BigInteger tenantId = new BigInteger((String)params.get("tenantId"));
            Map<String, Object> map = new HashMap<>();
            map.put("tenantId", tenantId);
            map.put("promotionType", 9);
            List<DietPromotion> dietPromotions = dietPromotionMapper.dietPromotionList(map);
            if(dietPromotions != null){
                for(int i = 0; i < dietPromotions.size(); i++){
                    Map<String, Object> mapDietPromotionBranchR = new HashMap<>();
                    mapDietPromotionBranchR.put("dietPromotionId", dietPromotions.get(i).getId());
                    List<DietPromotionBranchR> dietPromotionBranchRs = dietPromotionBranchRMapper.dietPromotionBranchRList(mapDietPromotionBranchR);
                    if(dietPromotionBranchRs != null){
                        for(int j = 0; j < dietPromotionBranchRs.size(); j++){
                            if((dietPromotionBranchRs.get(j).getBranchId()).compareTo(branchId) == 0){
                                Date oldSDT = simpleDateFormat1.parse(dietPromotions.get(i).getStartDate().toString().substring(0, 10) + " " + dietPromotions.get(i).getStartTime().toString());
                                Date oldEDT = simpleDateFormat1.parse(dietPromotions.get(i).getEndDate().toString().substring(0, 10) + " " + dietPromotions.get(i).getEndTime().toString());
                                Date newSDT = simpleDateFormat1.parse(params.get("start") + " 00:00:00");
                                Date newEDT = simpleDateFormat1.parse(params.get("end") + " 23:59:59");
                                if(newSDT.after(oldEDT) || newEDT.before(oldSDT)){
                                }
                                else{
                                    Map<String, Object> mapDietPromotion = new HashMap<>();
                                    mapDietPromotion.put("id", dietPromotionBranchRs.get(j).getDietPromotionId());
                                    List<DietPromotion> dietPromotionList = dietPromotionMapper.dietPromotionList(mapDietPromotion);
                                    if(dietPromotionList != null){
                                        DietPromotion dietPromotion1 = dietPromotionList.get(0);
                                        if(dietPromotion1.getScope() == 0){
                                            if(dietPromotion1.getForCustomerType() == 0){
                                                resultJSON.setSuccess("1");
                                                resultJSON.setMsg("同时间段内已有活动，请重新设置活动时间段！");
                                                return resultJSON;
                                            }
                                            else if(dietPromotion1.getForCustomerType() == 3){
                                                if("3".equals(params.get("forCustomerType"))){
                                                    if(BigInteger.valueOf(Long.valueOf((String)params.get("vipType"))) == dietPromotion1.getForVipType()){
                                                        resultJSON.setSuccess("1");
                                                        resultJSON.setMsg("同时间段内已有活动，请重新设置活动时间段！");
                                                        return resultJSON;
                                                    }
                                                }
                                                else{
                                                    if(Integer.valueOf((String)params.get("forCustomerType")) == 0 || Integer.valueOf((String)params.get("forCustomerType")) == 1 || Integer.valueOf((String)params.get("forCustomerType")) == dietPromotion.getForCustomerType()){
                                                        resultJSON.setSuccess("1");
                                                        resultJSON.setMsg("同时间段内已有活动，请重新设置活动时间段！");
                                                        return resultJSON;
                                                    }
                                                }
                                            }
                                            else{
                                                if(Integer.valueOf((String)params.get("forCustomerType")) == 0 || Integer.valueOf((String)params.get("forCustomerType")) == dietPromotion1.getForCustomerType()){
                                                    resultJSON.setSuccess("1");
                                                    resultJSON.setMsg("同时间段内已有活动，请重新设置活动时间段！");
                                                    return resultJSON;
                                                }
                                            }
                                        }
                                        else{
                                            if(Integer.valueOf((String)params.get("scope")) == 0 || Integer.valueOf((String)params.get("scope")) == dietPromotion1.getScope()){
                                                if(dietPromotion1.getForCustomerType() == 0){
                                                    resultJSON.setSuccess("1");
                                                    resultJSON.setMsg("同时间段内已有活动，请重新设置活动时间段！");
                                                    return resultJSON;
                                                }
                                                else if(dietPromotion1.getForCustomerType() == 3){
                                                    if("3".equals(params.get("forCustomerType"))){
                                                        if(BigInteger.valueOf(Long.valueOf((String)params.get("vipType"))) == dietPromotion1.getForVipType()){
                                                            resultJSON.setSuccess("1");
                                                            resultJSON.setMsg("同时间段内已有活动，请重新设置活动时间段！");
                                                            return resultJSON;
                                                        }
                                                    }
                                                    else{
                                                        if(Integer.valueOf((String)params.get("forCustomerType")) == 0 || Integer.valueOf((String)params.get("forCustomerType")) == 1 || Integer.valueOf((String)params.get("forCustomerType")) == dietPromotion.getForCustomerType()){
                                                            resultJSON.setSuccess("1");
                                                            resultJSON.setMsg("同时间段内已有活动，请重新设置活动时间段！");
                                                            return resultJSON;
                                                        }
                                                    }
                                                }
                                                else{
                                                    if(Integer.valueOf((String)params.get("forCustomerType")) == 0 || Integer.valueOf((String)params.get("forCustomerType")) == dietPromotion1.getForCustomerType()){
                                                        resultJSON.setSuccess("1");
                                                        resultJSON.setMsg("同时间段内已有活动，请重新设置活动时间段！");
                                                        return resultJSON;
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        Date startDate = simpleDateFormat.parse((String)params.get("start"));
        Date endDate = simpleDateFormat.parse((String)params.get("end"));
        Date startTime = simpleDateFormat1.parse(params.get("start") + " 00:00:00");
        Date endTime = simpleDateFormat1.parse(params.get("end") + " 23:59:59");
        dietPromotion.setStartDate(new java.sql.Date(startDate.getTime()));
        dietPromotion.setStartTime(new java.sql.Time(startTime.getTime()));
        dietPromotion.setEndDate(new java.sql.Date(endDate.getTime()));
        dietPromotion.setEndTime(new java.sql.Time(endTime.getTime()));
        dietPromotion.setTenantId(new BigInteger((String)params.get("tenantId")));
        dietPromotion.setCreateBranchId(new BigInteger((String)params.get("myBranchId")));
        if("3".equals(params.get("forCustomerType"))){
            dietPromotion.setForVipType(new BigInteger((String)params.get("vipType")));
        }
        else{
            dietPromotion.setForVipType(new BigInteger("0"));
        }
        String index = vipService.getNextSelValue(SysConfig.SYS_DIETPROMOTION_NUM, 4, new BigInteger(params.get("tenantId").toString()));
        String y_M_d = Common.getNowCorrect2Millisecond().substring(2, 8);//六位年月日
        dietPromotion.setPromotionCode("CX" + y_M_d + index);

        String[] weeks = ((String)params.get("weekInsert")).split(",");
        if(weeks == null){
            resultJSON.setMsg("未选择星期");
            resultJSON.setSuccess("1");
            return resultJSON;
        }
        else{
            for(int i = 0; i < weeks.length; i++){
                if(i + 1 == 1){
                    if(Integer.valueOf(weeks[i]) == 1){
                        dietPromotion.setApplyToMon(1);
                    }
                    else{
                        dietPromotion.setApplyToMon(0);
                    }
                }
                if(i + 1 == 2){
                    if(Integer.valueOf(weeks[i]) == 1){
                        dietPromotion.setApplyToTue(1);
                    }
                    else{
                        dietPromotion.setApplyToTue(0);
                    }
                }
                if(i + 1 == 3){
                    if(Integer.valueOf(weeks[i]) == 1){
                        dietPromotion.setApplyToWed(1);
                    }
                    else{
                        dietPromotion.setApplyToWed(0);
                    }
                }
                if(i + 1 == 4){
                    if(Integer.valueOf(weeks[i]) == 1){
                        dietPromotion.setApplyToThu(1);
                    }
                    else{
                        dietPromotion.setApplyToThu(0);
                    }
                }
                if(i + 1 == 5){
                    if(Integer.valueOf(weeks[i]) == 1){
                        dietPromotion.setApplyToFri(1);
                    }
                    else{
                        dietPromotion.setApplyToFri(0);
                    }
                }
                if(i + 1 == 6){
                    if(Integer.valueOf(weeks[i]) == 1){
                        dietPromotion.setApplyToSat(1);
                    }
                    else{
                        dietPromotion.setApplyToSat(0);
                    }
                }
                if(i + 1 == 7){
                    if(Integer.valueOf(weeks[i]) == 1){
                        dietPromotion.setApplyToSun(1);
                    }
                    else{
                        dietPromotion.setApplyToSun(0);
                    }
                }
            }
        }
        dietPromotion.setSuperposition(false);
        dietPromotion.setDeleted(false);
        dietPromotion.setPromotionName((String)params.get("promotionName"));
        dietPromotion.setUse(true);
        dietPromotion.setCreateBy("administrator");
        dietPromotion.setLastUpdateBy("administrator");
        dietPromotion.setCreateAt(new Date());
        dietPromotion.setLastUpdateAt(new Date());
        int flag = dietPromotionMapper.insert(dietPromotion);
        if(flag == 1){
            ApiRest ass = tenantConfigService.addTenantConfigOne(BigInteger.valueOf(Long.valueOf(((String)params.get("tenantId")))), SysConfig.SYS_DIETPROMOTION_NUM);
            if(!ass.getIsSuccess()){
                resultJSON.setSuccess("1");
                resultJSON.setMsg(ass.getError());
                return resultJSON;
            }
            if(dietPromotion.getPromotionType() == 9){
                Map<String, Object> mapDietPromotionTotalReduce = new HashMap<>();
                mapDietPromotionTotalReduce.put("dietPromotionId", dietPromotion.getId());
                mapDietPromotionTotalReduce.put("satisfy", new BigDecimal((String)params.get("satisfy")));
                mapDietPromotionTotalReduce.put("reduceType", new BigInteger((String)params.get("reduceType")));
                mapDietPromotionTotalReduce.put("isDeleted", 0);
                mapDietPromotionTotalReduce.put("createBy", "administrator");
                mapDietPromotionTotalReduce.put("lastUpdateBy", "administrator");
                mapDietPromotionTotalReduce.put("createAt", new Date());
                mapDietPromotionTotalReduce.put("lastUpdateAt", new Date());
                int flagDietPromotionTotalReduce = 0;
                if("1".equals(params.get("reduceType"))){
                    mapDietPromotionTotalReduce.put("reduction", new BigDecimal((String)params.get("reduceValue")));
                    flagDietPromotionTotalReduce = dietPromotionTotalReduceMapper.insert(mapDietPromotionTotalReduce);
                }
                else if("2".equals(params.get("reduceType"))){
                    mapDietPromotionTotalReduce.put("discount", new BigDecimal((String)params.get("reduceValue")));
                    flagDietPromotionTotalReduce = dietPromotionTotalReduceMapper.insert(mapDietPromotionTotalReduce);
                }
                else if("3".equals(params.get("reduceType"))){
                    if(params.get("dietPromotionJson") != null){
                        List<Map> totals = GsonUntil.jsonAsList(params.get("dietPromotionJson").toString(), Map.class);
                        if(totals != null){
                            for(Map m : totals){
                                mapDietPromotionTotalReduce.put("discount", new BigDecimal(m.get("discount").toString()));
                                mapDietPromotionTotalReduce.put("categoryId", m.get("catId"));
                                flagDietPromotionTotalReduce = dietPromotionTotalReduceMapper.insert(mapDietPromotionTotalReduce);
                            }
                        }
                    }
                }
                if(flagDietPromotionTotalReduce == 1){
                    if(branchIds != null){
                        for(int i = 0; i < branchIds.length; i++){
                            Map<String, Object> mapDietPromotionBranchR = new HashMap<>();
                            mapDietPromotionBranchR.put("dietPromotionId", dietPromotion.getId());
                            mapDietPromotionBranchR.put("branchId", BigInteger.valueOf(Long.valueOf(branchIds[i])));
                            mapDietPromotionBranchR.put("isDeleted", 0);
                            mapDietPromotionBranchR.put("createBy", "administrator");
                            mapDietPromotionBranchR.put("lastUpdateBy", "administrator");
                            mapDietPromotionBranchR.put("createAt", new Date());
                            mapDietPromotionBranchR.put("lastUpdateAt", new Date());
                            mapDietPromotionBranchR.put("tenantId", dietPromotion.getTenantId());
                            int flagDietPromotionBranchR = dietPromotionBranchRMapper.insert(mapDietPromotionBranchR);
                            if(flagDietPromotionBranchR == 1){
                                resultJSON.setSuccess("0");
                                resultJSON.setMsg("促销活动保存成功");
                            }
                            else{
                                resultJSON.setSuccess("1");
                                resultJSON.setMsg("促销活动保存失败");
                                return resultJSON;
                            }
                        }
                    }
                }
                else{
                    resultJSON.setSuccess("1");
                    resultJSON.setMsg("促销活动保存失败");
                }
            }
        }
        else{
            resultJSON.setSuccess("1");
            resultJSON.setMsg("促销活动保存失败");
        }
        return resultJSON;
    }

    /**
     * 查询卡券促销活动列表
     */
    public ResultJSON listFestival(Map params) throws ServiceException, ParseException{
        ResultJSON resultJSON = new ResultJSON();
        Map<String, Object> mapDietPromotion = new HashMap<>();
        Map<String, Object> map = new HashMap<>();
        mapDietPromotion.put("tenantId", new BigInteger((String)params.get("tenantId")));
        mapDietPromotion.put("promotionType", new BigInteger("8"));
        mapDietPromotion.put("isUse", 0);
        mapDietPromotion.put("promotionName", params.get("promotionName"));
        List<DietPromotion> dietPromotions = dietPromotionMapper.dietPromotionFestivalList(mapDietPromotion);
        List<DietPromotion> dietPromotionList = new ArrayList<>();
        List<DietPromotionFestival> dietPromotionFestivals = new ArrayList<>();
        for(DietPromotion dp : dietPromotions){
            SimpleDateFormat formatA = new SimpleDateFormat("yyyy-MM-dd");
            SimpleDateFormat formatB = new SimpleDateFormat("HH:mm:ss");
            SimpleDateFormat formatC = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            if(dp.getStartDate() != null && dp.getStartTime() != null && dp.getEndDate() != null && dp.getEndTime() != null){
                String startStr = formatA.format(dp.getStartDate()) + " " + formatB.format(dp.getStartTime());
                String endStr = formatA.format(dp.getEndDate()) + " " + formatB.format(dp.getEndTime());
                Date nowDate = new Date();
                Date startDate = formatC.parse(startStr);
                Date endDate = formatC.parse(endStr);
                if(startDate.before(nowDate) && endDate.after(nowDate) && dp.getPromotionStatus() == 0){
                    dietPromotionList.add(dp);
                }
                else if(startDate.after(nowDate) && dp.getPromotionStatus() == 0){
                    dietPromotionList.add(dp);
                }
            }
        }
        for(DietPromotion dpn : dietPromotionList){
            Map<String, Object> mapDietPromotionFestival = new HashMap<>();
            mapDietPromotionFestival.put("dietPromotionId", dpn.getId());
            DietPromotionFestival dietPromotionFestival = dietPromotionFestivalMapper.findByPromotionId(mapDietPromotionFestival);
            if(dietPromotionFestival!=null){
                dietPromotionFestivals.add(dietPromotionFestival);
            }
        }
        int count = dietPromotionFestivals.size();
        map.put("total", count > 0 ? count : 0);
        map.put("rows", dietPromotionFestivals);
        resultJSON.setJsonMap(map);
        return resultJSON;
    }

    /**
     * 根据促销活动id查询促销活动
     */
    public ResultJSON findActivityByPromotionId(Map params) throws ServiceException{
        ResultJSON resultJSON = new ResultJSON();
        ActivityVo activityVo = new ActivityVo();
        DietPromotionPayback dietPromotionPayback = new DietPromotionPayback();
        DietPromotionFestival dietPromotionFestival = new DietPromotionFestival();
        Map<String, Object> map = new HashMap<>();
        Map<String, Object> mapDietPromotion = new HashMap<>();
        mapDietPromotion.put("id", params.get("id"));
        DietPromotion promotion = dietPromotionMapper.findDietPromotionById(mapDietPromotion);
        Map<String, Object> mapDietPromotionKinds = new HashMap<>();
        mapDietPromotionKinds.put("dietPromotionId", promotion.getId());
        if(promotion.getPromotionType() == 13){
            dietPromotionPayback = dietPromotionPaybackMapper.findDietPromotionPaybackByDietPromotionId(mapDietPromotionKinds);
        }
        if(promotion.getPromotionType() == 8){
            dietPromotionFestival = dietPromotionFestivalMapper.findByPromotionId(mapDietPromotionKinds);
        }
        Map<String, Object> mapDietPromotionBranchR = new HashMap<>();
        mapDietPromotionBranchR.put("dietPromotionId", promotion.getId());
        List<DietPromotionBranchR> dietPromotionBranchRs = dietPromotionBranchRMapper.dietPromotionBranchRList(mapDietPromotionBranchR);
        activityVo.setPromotionName(promotion.getPromotionName());
        String branchIds = "";
        String branchName = "";
        for(int i = 0; i < dietPromotionBranchRs.size(); i++){
            branchIds += dietPromotionBranchRs.get(i).getBranchId() + ",";
            Map<String, Object> mapBranch = new HashMap<>();
            mapBranch.put("branchId", dietPromotionBranchRs.get(i).getBranchId());
            mapBranch.put("tenantId", params.get("tenantId"));
            Branch branch = branchMapper.findByTenantIdAndBranchId(mapBranch);
            branchName += branch.getName() + ",";
        }
        if(branchIds.length() > 2){
            branchIds.substring(0, branchIds.length() - 2);
        }
        if(branchName.length() > 2){
            branchName.substring(0, branchName.length() - 2);
        }
        activityVo.setPromotionId(new BigInteger((String)params.get("id")));
        activityVo.setBranchIds(branchIds);
        activityVo.setBranchNames(branchName);
        activityVo.setPromotionType(promotion.getPromotionType());
        activityVo.setForCustomerType(promotion.getForCustomerType());
        activityVo.setScope(promotion.getScope());
        activityVo.setStartDate(promotion.getStartDate());
        activityVo.setEndDate(promotion.getEndDate());
        activityVo.setStartAndEndDate(promotion.getStartDate() + "至" + promotion.getEndDate());
        activityVo.setApplyToMon(promotion.getApplyToMon());
        activityVo.setApplyToTue(promotion.getApplyToTue());
        activityVo.setApplyToWed(promotion.getApplyToWed());
        activityVo.setApplyToThu(promotion.getApplyToThu());
        activityVo.setApplyToFri(promotion.getApplyToFri());
        activityVo.setApplyToSat(promotion.getApplyToSat());
        activityVo.setApplyToSun(promotion.getApplyToSun());
        activityVo.setEffectiveInterval(promotion.getEffectiveInterval());
        if(promotion.getPromotionType() == 4){
            List<HashMap<String, Object>> details = dietPromotionBuyGiveMapper.queryBuyGiveDetailList(new BigInteger((String)params.get("id")));
            activityVo.setDetails(details);
        }
        if(promotion.getPromotionType() == 6){
            List<HashMap<String, Object>> details = dietPromotionDiscountMapper.queryDiscountDetailList(new BigInteger((String)params.get("id")));
            activityVo.setDetails(details);
        }
        if(promotion.getPromotionType() == 7){
            List<HashMap<String, Object>> details = dietPromotionSatisfyReduceMapper.querySatisfyReduceDetailList(new BigInteger((String)params.get("id")));
            activityVo.setDetails(details);
        }
        if(promotion.getPromotionType() == 11){
            List<HashMap<String, Object>> details = dietPromotionSpecialMapper.querySpecialDetailList(new BigInteger((String)params.get("id")));
            activityVo.setDetails(details);
        }
        if(promotion.getPromotionType() == 14){
            List<HashMap<String, Object>> details = dietPromotionAmountMapper.queryAmountDetailList(new BigInteger((String)params.get("id")));
            activityVo.setDetails(details);
        }
        if(promotion.getPromotionType() == 9){
            Map<String, Object> map1 = new HashMap<>();
            map1.put("dietPromotionId", params.get("id"));
            List<HashMap<String, Object>> details = dietPromotionTotalReduceMapper.dietPromotionTotalReduceList1(map1);
            if(details != null && details.size() > 0){
                if(details.get(0).get("satisfy") != null){
                    activityVo.setSatisfy(new BigDecimal(details.get(0).get("satisfy").toString()));
                }
                if(details.get(0).get("reduceType") != null){
                    activityVo.setReduceType(new Integer(details.get(0).get("reduceType").toString()));
                }
                if(Integer.valueOf(details.get(0).get("reduceType").toString()) == 3){
                    activityVo.setDetails(details);
                }
                else{
                    if(details.get(0).get("reduction") != null){
                        activityVo.setReduction(new BigDecimal(details.get(0).get("reduction").toString()));
                    }
                    if(details.get(0).get("discount") != null){
                        activityVo.setDiscount(new BigDecimal(details.get(0).get("discount").toString()));
                    }
                }
            }
        }
        if(promotion.getPromotionType() == 13){
            activityVo.setSatisfy(dietPromotionPayback.getSatisfy());
            activityVo.setPayType(dietPromotionPayback.getPayType());
            activityVo.setDiscount(dietPromotionPayback.getDiscount());
        }
        if(promotion.getPromotionType() == 8){
            activityVo.setCardId(dietPromotionFestival.getCardId());
            Map<String, Object> mapDietPromotionFestival = new HashMap<>();
            mapDietPromotionFestival.put("id", dietPromotionFestival.getCardId());
            CardCoupons cardCoupons = cardCouponsMapper.findCardCouponsById(mapDietPromotionFestival);
            if(cardCoupons != null){
                activityVo.setCardName(cardCoupons.getCardName());
            }
            activityVo.setSendLimitValue(dietPromotionFestival.getSendLimitValue());
            activityVo.setTotalInventory(dietPromotionFestival.getTotalInventory());
        }
        //卡券活动
        if(promotion.getPromotionType() == 12){
            DietGameScratchCard dietGameScratchCard = dietGameScratchCardMapper.findByDietPromotionId(mapDietPromotionKinds);
            if(dietGameScratchCard != null){
                activityVo.setGameName(dietGameScratchCard.getGameName());
                activityVo.setAttendTimes(dietGameScratchCard.getAttendTimes());
                activityVo.setRemark(dietGameScratchCard.getRemark());
                List<DietGamePrizeItem> items = dietGamePrizeItemMapper.dietGamePrizeItemList(mapDietPromotionKinds);
                if(items != null && items.size() > 0){
                    for(DietGamePrizeItem item : items){
                        Map<String, Object> param2 = new HashMap<>();
                        param2.put("id", item.getDietPromotionId());
                        DietPromotion dietPromotion = dietPromotionMapper.findDietPromotionById(param2);
                        if(dietPromotion != null){
                            item.setPromotionName(dietPromotion.getPromotionName());
                        }
                    }
                    activityVo.setItems(items);
                }
            }
        }
        map.put("activityVo", activityVo);
        resultJSON.setJsonMap(map);
        return resultJSON;
    }

    /**
     * 更新整单优惠促销活动
     */
    public ResultJSON updateTotalReduce(Map params, DietPromotion dietPromotion) throws ServiceException, ParseException{
        ResultJSON resultJSON = new ResultJSON();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat simpleDateFormat1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String[] branchIds = ((String)params.get("branchIds")).split(",");
        String createBranchId = (String)params.get("myBranchId");
        for(int k = 0; k < branchIds.length; k++){
            BigInteger branchId = new BigInteger(branchIds[k]);
            BigInteger tenantId = new BigInteger((String)params.get("tenantId"));
            Map<String, Object> map = new HashMap<>();
            map.put("tenantId", tenantId);
            map.put("promotionId", new BigInteger((String)params.get("id")));
            List<DietPromotion> dietPromotions = dietPromotionMapper.dietPromotionList(map);
            if(dietPromotions != null){
                for(int i = 0; i < dietPromotions.size(); i++){
                    Map<String, Object> mapDietPromotionBranchR = new HashMap<>();
                    mapDietPromotionBranchR.put("dietPromotionId", dietPromotions.get(i).getId());
                    List<DietPromotionBranchR> dietPromotionBranchRs = dietPromotionBranchRMapper.dietPromotionBranchRList(mapDietPromotionBranchR);
                    if(dietPromotionBranchRs != null){
                        for(int j = 0; j < dietPromotionBranchRs.size(); j++){
                            if((dietPromotionBranchRs.get(j).getBranchId()).compareTo(branchId) == 0){
                                Date oldSDT = simpleDateFormat1.parse(dietPromotions.get(i).getStartDate().toString().substring(0, 10) + " " + dietPromotions.get(i).getStartTime().toString());
                                Date oldEDT = simpleDateFormat1.parse(dietPromotions.get(i).getEndDate().toString().substring(0, 10) + " " + dietPromotions.get(i).getEndTime().toString());
                                Date newSDT = simpleDateFormat1.parse(params.get("start") + " 00:00:00");
                                Date newEDT = simpleDateFormat1.parse(params.get("end") + " 23:59:59");
                                if(newSDT.after(oldEDT) || newEDT.before(oldSDT)){
                                }
                                else{
                                    Map<String, Object> mapDietPromotion = new HashMap<>();
                                    mapDietPromotion.put("id", dietPromotionBranchRs.get(j).getDietPromotionId());
                                    List<DietPromotion> dietPromotionList = dietPromotionMapper.dietPromotionList(mapDietPromotion);
                                    if(dietPromotionList != null && dietPromotionList.size() > 0){
                                        DietPromotion dietPromotion1 = dietPromotionList.get(0);
                                        if(dietPromotion1.getScope() == 0){
                                            if(dietPromotion1.getForCustomerType() == 0){
                                                resultJSON.setSuccess("1");
                                                resultJSON.setMsg("同时间段内已有活动，请重新设置活动时间段！");
                                                return resultJSON;
                                            }
                                            else if(dietPromotion1.getForCustomerType() == 3){
                                                if("3".equals(params.get("forCustomerType"))){
                                                    if(BigInteger.valueOf(Long.valueOf((String)params.get("vipType"))) == dietPromotion1.getForVipType()){
                                                        resultJSON.setSuccess("1");
                                                        resultJSON.setMsg("同时间段内已有活动，请重新设置活动时间段！");
                                                        return resultJSON;
                                                    }
                                                }
                                                else{
                                                    if(Integer.valueOf((String)params.get("forCustomerType")) == 0 || Integer.valueOf((String)params.get("forCustomerType")) == 1 || Integer.valueOf((String)params.get("forCustomerType")) == dietPromotion.getForCustomerType()){
                                                        resultJSON.setSuccess("1");
                                                        resultJSON.setMsg("同时间段内已有活动，请重新设置活动时间段！");
                                                        return resultJSON;
                                                    }
                                                }
                                            }
                                            else{
                                                if(Integer.valueOf((String)params.get("forCustomerType")) == 0 || Integer.valueOf((String)params.get("forCustomerType")) == dietPromotion1.getForCustomerType()){
                                                    resultJSON.setSuccess("1");
                                                    resultJSON.setMsg("同时间段内已有活动，请重新设置活动时间段！");
                                                    return resultJSON;
                                                }
                                            }
                                        }
                                        else{
                                            if(Integer.valueOf((String)params.get("scope")) == 0 || Integer.valueOf((String)params.get("scope")) == dietPromotion1.getScope()){
                                                if(dietPromotion1.getForCustomerType() == 0){
                                                    resultJSON.setSuccess("1");
                                                    resultJSON.setMsg("同时间段内已有活动，请重新设置活动时间段！");
                                                    return resultJSON;
                                                }
                                                else if(dietPromotion1.getForCustomerType() == 3){
                                                    if("3".equals(params.get("forCustomerType"))){
                                                        if(BigInteger.valueOf(Long.valueOf((String)params.get("vipType"))) == dietPromotion1.getForVipType()){
                                                            resultJSON.setSuccess("1");
                                                            resultJSON.setMsg("同时间段内已有活动，请重新设置活动时间段！");
                                                            return resultJSON;
                                                        }
                                                    }
                                                    else{
                                                        if(Integer.valueOf((String)params.get("forCustomerType")) == 0 || Integer.valueOf((String)params.get("forCustomerType")) == 1 || Integer.valueOf((String)params.get("forCustomerType")) == dietPromotion.getForCustomerType()){
                                                            resultJSON.setSuccess("1");
                                                            resultJSON.setMsg("同时间段内已有活动，请重新设置活动时间段！");
                                                            return resultJSON;
                                                        }
                                                    }
                                                }
                                                else{
                                                    if(Integer.valueOf((String)params.get("forCustomerType")) == 0 || Integer.valueOf((String)params.get("forCustomerType")) == dietPromotion1.getForCustomerType()){
                                                        resultJSON.setSuccess("1");
                                                        resultJSON.setMsg("同时间段内已有活动，请重新设置活动时间段！");
                                                        return resultJSON;
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        Date startDate = simpleDateFormat.parse((String)params.get("start"));
        Date endDate = simpleDateFormat.parse((String)params.get("end"));
        Date startTime = simpleDateFormat1.parse(params.get("start") + " 00:00:00");
        Date endTime = simpleDateFormat1.parse(params.get("end") + " 23:59:59");
        dietPromotion.setStartDate(new java.sql.Date(startDate.getTime()));
        dietPromotion.setStartTime(new java.sql.Time(startTime.getTime()));
        dietPromotion.setEndDate(new java.sql.Date(endDate.getTime()));
        dietPromotion.setEndTime(new java.sql.Time(endTime.getTime()));
        dietPromotion.setTenantId(new BigInteger((String)params.get("tenantId")));
        dietPromotion.setCreateBranchId(new BigInteger((String)params.get("myBranchId")));
        if("3".equals(params.get("forCustomerType"))){
            dietPromotion.setForVipType(new BigInteger((String)params.get("vipType")));
        }
        else{
            dietPromotion.setForVipType(new BigInteger("0"));
        }

        String[] weeks = ((String)params.get("weekInsert")).split(",");
        if(weeks == null){
            resultJSON.setMsg("未选择星期");
            resultJSON.setSuccess("1");
            return resultJSON;
        }
        else{
            for(int i = 0; i < weeks.length; i++){
                if(i + 1 == 1){
                    if(Integer.valueOf(weeks[i]) == 1){
                        dietPromotion.setApplyToMon(1);
                    }
                    else{
                        dietPromotion.setApplyToMon(0);
                    }
                }
                if(i + 1 == 2){
                    if(Integer.valueOf(weeks[i]) == 1){
                        dietPromotion.setApplyToTue(1);
                    }
                    else{
                        dietPromotion.setApplyToTue(0);
                    }
                }
                if(i + 1 == 3){
                    if(Integer.valueOf(weeks[i]) == 1){
                        dietPromotion.setApplyToWed(1);
                    }
                    else{
                        dietPromotion.setApplyToWed(0);
                    }
                }
                if(i + 1 == 4){
                    if(Integer.valueOf(weeks[i]) == 1){
                        dietPromotion.setApplyToThu(1);
                    }
                    else{
                        dietPromotion.setApplyToThu(0);
                    }
                }
                if(i + 1 == 5){
                    if(Integer.valueOf(weeks[i]) == 1){
                        dietPromotion.setApplyToFri(1);
                    }
                    else{
                        dietPromotion.setApplyToFri(0);
                    }
                }
                if(i + 1 == 6){
                    if(Integer.valueOf(weeks[i]) == 1){
                        dietPromotion.setApplyToSat(1);
                    }
                    else{
                        dietPromotion.setApplyToSat(0);
                    }
                }
                if(i + 1 == 7){
                    if(Integer.valueOf(weeks[i]) == 1){
                        dietPromotion.setApplyToSun(1);
                    }
                    else{
                        dietPromotion.setApplyToSun(0);
                    }
                }
            }
        }
        dietPromotion.setSuperposition(false);
        dietPromotion.setDeleted(false);
        dietPromotion.setPromotionName((String)params.get("promotionName"));
        dietPromotion.setUse(true);
        dietPromotion.setLastUpdateBy("administrator");
        dietPromotion.setLastUpdateAt(new Date());
        int flag = dietPromotionMapper.update(dietPromotion);
        if(flag == 1){
            if(dietPromotion.getPromotionType() == 9){
                Map<String, Object> mapDietPromotionTotal = new HashMap<>();
                mapDietPromotionTotal.put("dietPromotionId", new BigInteger((String)params.get("id")));
                DietPromotionTotalReduce dietPromotionTotalReduce = dietPromotionTotalReduceMapper.findDietPromotionTotalReduceByDietPromotionId(mapDietPromotionTotal);
                Map<String, Object> mapDietPromotionTotalReduce = new HashMap<>();
                mapDietPromotionTotalReduce.put("id", dietPromotionTotalReduce.getId());
                mapDietPromotionTotalReduce.put("dietPromotionId", dietPromotion.getId());
                mapDietPromotionTotalReduce.put("satisfy", new BigDecimal((String)params.get("satisfy")));
                mapDietPromotionTotalReduce.put("reduceType", new BigInteger((String)params.get("reduceType")));
                if("1".equals(params.get("reduceType"))){
                    mapDietPromotionTotalReduce.put("reduction", new BigDecimal((String)params.get("reduceValue")));
                }
                if("2".equals(params.get("reduceType"))){
                    mapDietPromotionTotalReduce.put("discount", new BigDecimal((String)params.get("reduceValue")));
                }
                mapDietPromotionTotalReduce.put("isDeleted", 0);
                mapDietPromotionTotalReduce.put("lastUpdateBy", "administrator");
                mapDietPromotionTotalReduce.put("lastUpdateAt", new Date());
                int flagDietPromotionTotalReduce = dietPromotionTotalReduceMapper.update(mapDietPromotionTotalReduce);
                if(flagDietPromotionTotalReduce == 1){
                    Map<String, Object> dietPromotionBranchRMap = new HashMap<>();
                    dietPromotionBranchRMap.put("dietPromotionId", new BigInteger((String)params.get("id")));
                    List<DietPromotionBranchR> dietPromotionBranchRs = dietPromotionBranchRMapper.dietPromotionBranchRList(dietPromotionBranchRMap);
                    Map<String, Object> updateDietPromotionBranchRMap = new HashMap<>();
                    updateDietPromotionBranchRMap.put("isDeleted", 1);
                    for(int j = 0; j < dietPromotionBranchRs.size(); j++){
                        updateDietPromotionBranchRMap.put("id", dietPromotionBranchRs.get(j).getId());
                        updateDietPromotionBranchRMap.put("lastUpdateBy", "administrator");
                        updateDietPromotionBranchRMap.put("lastUpdateAt", new Date());
                        int flagUpdateDietPromotionBranchR = dietPromotionBranchRMapper.update(updateDietPromotionBranchRMap);
                        if(flagUpdateDietPromotionBranchR == 0){
                            resultJSON.setSuccess("1");
                            resultJSON.setMsg("促销活动保存失败");
                            return resultJSON;
                        }
                    }
                    if(branchIds != null){
                        for(int i = 0; i < branchIds.length; i++){
                            Map<String, Object> mapDietPromotionBranchR = new HashMap<>();
                            mapDietPromotionBranchR.put("dietPromotionId", dietPromotion.getId());
                            mapDietPromotionBranchR.put("branchId", BigInteger.valueOf(Long.valueOf(branchIds[i])));
                            mapDietPromotionBranchR.put("isDeleted", 0);
                            mapDietPromotionBranchR.put("createBy", "administrator");
                            mapDietPromotionBranchR.put("lastUpdateBy", "administrator");
                            mapDietPromotionBranchR.put("createAt", new Date());
                            mapDietPromotionBranchR.put("lastUpdateAt", new Date());
                            mapDietPromotionBranchR.put("tenantId", dietPromotion.getTenantId());
                            int flagDietPromotionBranchR = dietPromotionBranchRMapper.insert(mapDietPromotionBranchR);
                            if(flagDietPromotionBranchR == 1){
                                resultJSON.setSuccess("0");
                                resultJSON.setMsg("促销活动保存成功");
                            }
                            else{
                                resultJSON.setSuccess("1");
                                resultJSON.setMsg("促销活动保存失败");
                                return resultJSON;
                            }
                        }
                    }
                }
                else{
                    resultJSON.setSuccess("1");
                    resultJSON.setMsg("促销活动保存失败");
                }
            }
        }
        else{
            resultJSON.setSuccess("1");
            resultJSON.setMsg("促销活动保存失败");
        }
        return resultJSON;
    }

    /**
     * 更新支付促销活动
     */
    public ResultJSON updatePayPreferential(Map params, DietPromotion dietPromotion) throws ServiceException, ParseException{
        ResultJSON resultJSON = new ResultJSON();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat simpleDateFormat1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String[] branchIds = ((String)params.get("branchIds")).split(",");
        String createBranchId = (String)params.get("myBranchId");
        for(int k = 0; k < branchIds.length; k++){
            BigInteger branchId = new BigInteger(branchIds[k]);
            BigInteger tenantId = new BigInteger((String)params.get("tenantId"));
            Map<String, Object> map = new HashMap<>();
            map.put("tenantId", tenantId);
            map.put("promotionId", new BigInteger((String)params.get("id")));
            List<DietPromotion> dietPromotions = dietPromotionMapper.dietPromotionList(map);
            if(dietPromotions != null){
                for(int i = 0; i < dietPromotions.size(); i++){
                    Map<String, Object> mapDietPromotionBranchR = new HashMap<>();
                    mapDietPromotionBranchR.put("dietPromotionId", dietPromotions.get(i).getId());
                    List<DietPromotionBranchR> dietPromotionBranchRs = dietPromotionBranchRMapper.dietPromotionBranchRList(mapDietPromotionBranchR);
                    if(dietPromotionBranchRs != null){
                        for(int j = 0; j < dietPromotionBranchRs.size(); j++){
                            if((dietPromotionBranchRs.get(j).getBranchId()).compareTo(branchId) == 0){
                                Date oldSDT = simpleDateFormat1.parse(dietPromotions.get(i).getStartDate().toString().substring(0, 10) + " " + dietPromotions.get(i).getStartTime().toString());
                                Date oldEDT = simpleDateFormat1.parse(dietPromotions.get(i).getEndDate().toString().substring(0, 10) + " " + dietPromotions.get(i).getEndTime().toString());
                                Date newSDT = simpleDateFormat1.parse((String)params.get("start") + " 00:00:00");
                                Date newEDT = simpleDateFormat1.parse((String)params.get("end") + " 23:59:59");
                                if(newSDT.after(oldEDT) || newEDT.before(oldSDT)){
                                }
                                else{
                                    Map<String, Object> mapDietPromotion = new HashMap<>();
                                    mapDietPromotion.put("id", dietPromotionBranchRs.get(j).getDietPromotionId());
                                    List<DietPromotion> dietPromotionList = dietPromotionMapper.dietPromotionList(mapDietPromotion);
                                    if(dietPromotionList != null){
                                        DietPromotion dietPromotion1 = dietPromotionList.get(0);
                                        if(dietPromotion1.getScope() == 0){
                                            if(dietPromotion1.getForCustomerType() == 0){
                                                resultJSON.setSuccess("1");
                                                resultJSON.setMsg("同时间段内已有活动，请重新设置活动时间段！");
                                                return resultJSON;
                                            }
                                            else if(dietPromotion1.getForCustomerType() == 3){
                                                if("3".equals(params.get("forCustomerType"))){
                                                    if(BigInteger.valueOf(Long.valueOf((String)params.get("vipType"))) == dietPromotion1.getForVipType()){
                                                        resultJSON.setSuccess("1");
                                                        resultJSON.setMsg("同时间段内已有活动，请重新设置活动时间段！");
                                                        return resultJSON;
                                                    }
                                                }
                                                else{
                                                    if(Integer.valueOf((String)params.get("forCustomerType")) == 0 || Integer.valueOf((String)params.get("forCustomerType")) == 1 || Integer.valueOf((String)params.get("forCustomerType")) == dietPromotion.getForCustomerType()){
                                                        resultJSON.setSuccess("1");
                                                        resultJSON.setMsg("同时间段内已有活动，请重新设置活动时间段！");
                                                        return resultJSON;
                                                    }
                                                }
                                            }
                                            else{
                                                if(Integer.valueOf((String)params.get("forCustomerType")) == 0 || Integer.valueOf((String)params.get("forCustomerType")) == dietPromotion1.getForCustomerType()){
                                                    resultJSON.setSuccess("1");
                                                    resultJSON.setMsg("同时间段内已有活动，请重新设置活动时间段！");
                                                    return resultJSON;
                                                }
                                            }
                                        }
                                        else{
                                            if(Integer.valueOf((String)params.get("scope")) == 0 || Integer.valueOf((String)params.get("scope")) == dietPromotion1.getScope()){
                                                if(dietPromotion1.getForCustomerType() == 0){
                                                    resultJSON.setSuccess("1");
                                                    resultJSON.setMsg("同时间段内已有活动，请重新设置活动时间段！");
                                                    return resultJSON;
                                                }
                                                else if(dietPromotion1.getForCustomerType() == 3){
                                                    if("3".equals(params.get("forCustomerType"))){
                                                        if(BigInteger.valueOf(Long.valueOf((String)params.get("vipType"))) == dietPromotion1.getForVipType()){
                                                            resultJSON.setSuccess("1");
                                                            resultJSON.setMsg("同时间段内已有活动，请重新设置活动时间段！");
                                                            return resultJSON;
                                                        }
                                                    }
                                                    else{
                                                        if(Integer.valueOf((String)params.get("forCustomerType")) == 0 || Integer.valueOf((String)params.get("forCustomerType")) == 1 || Integer.valueOf((String)params.get("forCustomerType")) == dietPromotion.getForCustomerType()){
                                                            resultJSON.setSuccess("1");
                                                            resultJSON.setMsg("同时间段内已有活动，请重新设置活动时间段！");
                                                            return resultJSON;
                                                        }
                                                    }
                                                }
                                                else{
                                                    if(Integer.valueOf((String)params.get("forCustomerType")) == 0 || Integer.valueOf((String)params.get("forCustomerType")) == dietPromotion1.getForCustomerType()){
                                                        resultJSON.setSuccess("1");
                                                        resultJSON.setMsg("同时间段内已有活动，请重新设置活动时间段！");
                                                        return resultJSON;
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        Date startDate = simpleDateFormat.parse((String)params.get("start"));
        Date endDate = simpleDateFormat.parse((String)params.get("end"));
        Date startTime = simpleDateFormat1.parse(params.get("start") + " 00:00:00");
        Date endTime = simpleDateFormat1.parse(params.get("end") + " 23:59:59");
        dietPromotion.setStartDate(new java.sql.Date(startDate.getTime()));
        dietPromotion.setStartTime(new java.sql.Time(startTime.getTime()));
        dietPromotion.setEndDate(new java.sql.Date(endDate.getTime()));
        dietPromotion.setEndTime(new java.sql.Time(endTime.getTime()));

        dietPromotion.setTenantId(new BigInteger((String)params.get("tenantId")));
        dietPromotion.setCreateBranchId(new BigInteger((String)params.get("myBranchId")));
        if("3".equals(params.get("forCustomerType"))){
            dietPromotion.setForVipType(new BigInteger((String)params.get("vipType")));
        }
        else{
            dietPromotion.setForVipType(new BigInteger("0"));
        }

        String[] weeks = ((String)params.get("weekInsert")).split(",");
        if(weeks == null){
            resultJSON.setMsg("未选择星期");
            resultJSON.setSuccess("1");
            return resultJSON;
        }
        else{
            for(int i = 0; i < weeks.length; i++){
                if(i + 1 == 1){
                    if(Integer.valueOf(weeks[i]) == 1){
                        dietPromotion.setApplyToMon(1);
                    }
                    else{
                        dietPromotion.setApplyToMon(0);
                    }
                }
                if(i + 1 == 2){
                    if(Integer.valueOf(weeks[i]) == 1){
                        dietPromotion.setApplyToTue(1);
                    }
                    else{
                        dietPromotion.setApplyToTue(0);
                    }
                }
                if(i + 1 == 3){
                    if(Integer.valueOf(weeks[i]) == 1){
                        dietPromotion.setApplyToWed(1);
                    }
                    else{
                        dietPromotion.setApplyToWed(0);
                    }
                }
                if(i + 1 == 4){
                    if(Integer.valueOf(weeks[i]) == 1){
                        dietPromotion.setApplyToThu(1);
                    }
                    else{
                        dietPromotion.setApplyToThu(0);
                    }
                }
                if(i + 1 == 5){
                    if(Integer.valueOf(weeks[i]) == 1){
                        dietPromotion.setApplyToFri(1);
                    }
                    else{
                        dietPromotion.setApplyToFri(0);
                    }
                }
                if(i + 1 == 6){
                    if(Integer.valueOf(weeks[i]) == 1){
                        dietPromotion.setApplyToSat(1);
                    }
                    else{
                        dietPromotion.setApplyToSat(0);
                    }
                }
                if(i + 1 == 7){
                    if(Integer.valueOf(weeks[i]) == 1){
                        dietPromotion.setApplyToSun(1);
                    }
                    else{
                        dietPromotion.setApplyToSun(0);
                    }
                }
            }
        }
        dietPromotion.setSuperposition(false);
        dietPromotion.setDeleted(false);
        dietPromotion.setPromotionName((String)params.get("promotionName"));
        dietPromotion.setUse(true);
        dietPromotion.setLastUpdateBy("administrator");
        dietPromotion.setLastUpdateAt(new Date());
        int flag = dietPromotionMapper.update(dietPromotion);
        if(flag == 1){
            ApiRest ass = tenantConfigService.addTenantConfigOne(BigInteger.valueOf(Long.valueOf(((String)params.get("tenantId")))), SysConfig.SYS_DIETPROMOTION_NUM);
            if(!ass.getIsSuccess()){
                resultJSON.setSuccess("1");
                resultJSON.setMsg(ass.getError());
                return resultJSON;
            }
            if(dietPromotion.getPromotionType() == 13){
                Map<String, Object> mapDietPromotionPaybackk = new HashMap<>();
                mapDietPromotionPaybackk.put("dietPromotionId", new BigInteger((String)params.get("id")));
                DietPromotionPayback dietPromotionPayback = dietPromotionPaybackMapper.findDietPromotionPaybackByDietPromotionId(mapDietPromotionPaybackk);
                Map<String, Object> mapDietPromotionPayback = new HashMap<>();
                mapDietPromotionPayback.put("id", dietPromotionPayback.getId());
                mapDietPromotionPayback.put("dietPromotionId", dietPromotion.getId());
                mapDietPromotionPayback.put("satisfy", new BigDecimal((String)params.get("forCustomerCondition")));
                mapDietPromotionPayback.put("tenantId", dietPromotion.getTenantId());
                mapDietPromotionPayback.put("discount", new BigDecimal((String)params.get("discount")));
                mapDietPromotionPayback.put("payType", new BigInteger((String)params.get("payType")));
                mapDietPromotionPayback.put("remark", params.get("memo"));
                mapDietPromotionPayback.put("isDeleted", 0);
                mapDietPromotionPayback.put("createBy", "administrator");
                mapDietPromotionPayback.put("lastUpdateBy", "administrator");
                mapDietPromotionPayback.put("createAt", new Date());
                mapDietPromotionPayback.put("lastUpdateAt", new Date());
                int flagDietPromotionPayback = dietPromotionPaybackMapper.update(mapDietPromotionPayback);
                if(flagDietPromotionPayback == 1){
                    Map<String, Object> dietPromotionBranchRMap = new HashMap<>();
                    dietPromotionBranchRMap.put("dietPromotionId", new BigInteger((String)params.get("id")));
                    List<DietPromotionBranchR> dietPromotionBranchRs = dietPromotionBranchRMapper.dietPromotionBranchRList(dietPromotionBranchRMap);
                    Map<String, Object> updateDietPromotionBranchRMap = new HashMap<>();
                    updateDietPromotionBranchRMap.put("isDeleted", 1);
                    for(int j = 0; j < dietPromotionBranchRs.size(); j++){
                        updateDietPromotionBranchRMap.put("id", dietPromotionBranchRs.get(j).getId());
                        updateDietPromotionBranchRMap.put("lastUpdateBy", "administrator");
                        updateDietPromotionBranchRMap.put("lastUpdateAt", new Date());
                        int flagUpdateDietPromotionBranchR = dietPromotionBranchRMapper.update(updateDietPromotionBranchRMap);
                        if(flagUpdateDietPromotionBranchR == 0){
                            resultJSON.setSuccess("1");
                            resultJSON.setMsg("促销活动保存失败");
                            return resultJSON;
                        }
                    }
                    if(branchIds != null){
                        for(int i = 0; i < branchIds.length; i++){
                            Map<String, Object> mapDietPromotionBranchR = new HashMap<>();
                            mapDietPromotionBranchR.put("dietPromotionId", dietPromotion.getId());
                            mapDietPromotionBranchR.put("branchId", BigInteger.valueOf(Long.valueOf(branchIds[i])));
                            mapDietPromotionBranchR.put("isDeleted", 0);
                            mapDietPromotionBranchR.put("createBy", "administrator");
                            mapDietPromotionBranchR.put("lastUpdateBy", "administrator");
                            mapDietPromotionBranchR.put("createAt", new Date());
                            mapDietPromotionBranchR.put("lastUpdateAt", new Date());
                            mapDietPromotionBranchR.put("tenantId", dietPromotion.getTenantId());
                            int flagDietPromotionBranchR = dietPromotionBranchRMapper.insert(mapDietPromotionBranchR);
                            if(flagDietPromotionBranchR == 1){
                                resultJSON.setSuccess("0");
                                resultJSON.setMsg("促销活动保存成功");
                            }
                            else{
                                resultJSON.setSuccess("1");
                                resultJSON.setMsg("促销活动保存失败");
                                return resultJSON;
                            }
                        }
                    }
                }
                else{
                    resultJSON.setSuccess("1");
                    resultJSON.setMsg("促销活动保存失败");
                }
            }
        }
        else{
            resultJSON.setSuccess("1");
            resultJSON.setMsg("促销活动保存失败");
        }
        return resultJSON;
    }

    /**
     * 修改促销活动
     */
    public ResultJSON updatePromotion(Map params, DietPromotion dietPromotion, List list) throws ServiceException, ParseException{
        ResultJSON resultJSON = new ResultJSON();
        String createBranchId = (String)params.get("myBranchId");
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat simpleDateFormat1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date newStartDate = simpleDateFormat.parse((String)params.get("start"));
        Date newEndDate = simpleDateFormat.parse((String)params.get("end"));
        String[] branchIds = ((String)params.get("branchIds")).split(",");
        for(int k = 0; k < branchIds.length; k++){
            BigInteger branchId = new BigInteger(branchIds[k]);
            if(list != null){
                for(int i = 0; i < list.size(); i++){
                    String goodsIdStr = String.valueOf((((Map)list.get(i)).get("buyGoodsId")));
                    goodsIdStr = goodsIdStr.substring(0, goodsIdStr.length() - 2);
                    BigInteger goodsId = new BigInteger(goodsIdStr);
                    Map<String, Object> map = new HashMap<>();
                    map.put("goodsId", goodsId);
                    List<DietPromotionGoodsR> dietPromotionGoodsRs = dietPromotionGoodsRMapper.dietPromotionGoodsRList(map);
                    if(dietPromotionGoodsRs != null && dietPromotionGoodsRs.size() > 0){
                        for(int j = 0; j < dietPromotionGoodsRs.size(); j++){
                            Date oldStartDate = dietPromotionGoodsRs.get(j).getStartDate();
                            Date oldEndDate = dietPromotionGoodsRs.get(j).getEndDate();
                            Map<String, Object> map1 = new HashMap<>();
                            map1.put("dietPromotionId", dietPromotionGoodsRs.get(j).getDietPromotionId());
                            List<DietPromotionBranchR> dietPromotionBranchRs = dietPromotionBranchRMapper.dietPromotionBranchRList(map1);
                            for(int m = 0; m < dietPromotionBranchRs.size(); m++){
                                if(newEndDate.before(oldStartDate) || newStartDate.after(oldEndDate)){

                                }
                                else{
                                    //判断周
                                    HashMap<String, Object> map2 = DateWeekContains.dateContains(oldStartDate, oldEndDate, newStartDate, newEndDate);
                                    String oldWeek = dietPromotionGoodsRs.get(j).getWeek();
                                    if(DateWeekContains.weekContainsCardActivity(map2, oldWeek, (String)params.get("weekInsert"))){
                                        Map<String, Object> map3 = new HashMap<>();
                                        map3.put("id", dietPromotionBranchRs.get(m).getDietPromotionId());
                                        map3.put("tenantId", params.get("tenantId"));
                                        List<DietPromotion> dietPromotions = dietPromotionMapper.dietPromotionList(map3);
                                        if(dietPromotions != null){
                                            DietPromotion dietPromotion1 = dietPromotions.get(0);
                                            if(dietPromotion1.getScope() == 0){
                                                if(dietPromotion1.getForCustomerType() == 0){
                                                    resultJSON.setSuccess("1");
                                                    resultJSON.setMsg("选择的商品在已有的时段已经参与促销，请重新选择");
                                                    return resultJSON;
                                                }
                                                else{
                                                    if(Integer.valueOf(params.get("forCustomerType").toString()) == 0 || params.get("forCustomerType") == dietPromotion1.getForCustomerType()){
                                                        resultJSON.setSuccess("1");
                                                        resultJSON.setMsg("选择的商品在已有的时段已经参与促销，请重新选择");
                                                        return resultJSON;
                                                    }
                                                }
                                            }
                                            else{
                                                if(Integer.valueOf(params.get("scope").toString()) == 0 || params.get("scope") == dietPromotion1.getScope()){
                                                    if(dietPromotion1.getForCustomerType() == 0){
                                                        resultJSON.setSuccess("1");
                                                        resultJSON.setMsg("选择的商品在已有的时段已经参与促销，请重新选择");
                                                        return resultJSON;
                                                    }
                                                    else{
                                                        if(Integer.valueOf(params.get("forCustomerType").toString()) == 0 || params.get("forCustomerType") == dietPromotion1.getForCustomerType()){
                                                            resultJSON.setSuccess("1");
                                                            resultJSON.setMsg("选择的商品在已有的时段已经参与促销，请重新选择");
                                                            return resultJSON;
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                    ;
                                }
                            }
                        }
                    }
                }
            }
        }
        Date startDate = simpleDateFormat.parse((String)params.get("start"));
        Date endDate = simpleDateFormat.parse((String)params.get("end"));
        Date startTime = simpleDateFormat1.parse(params.get("start") + " 00:00:00");
        Date endTime = simpleDateFormat1.parse(params.get("end") + " 23:59:59");
        dietPromotion.setStartDate(new java.sql.Date(startDate.getTime()));
        dietPromotion.setStartTime(new java.sql.Time(startTime.getTime()));
        dietPromotion.setEndDate(new java.sql.Date(endDate.getTime()));
        dietPromotion.setEndTime(new java.sql.Time(endTime.getTime()));

        dietPromotion.setTenantId(new BigInteger((String)params.get("tenantId")));
        dietPromotion.setCreateBranchId(new BigInteger((String)params.get("myBranchId")));
        String[] weeks = ((String)params.get("weekInsert")).split(",");
        if(weeks == null){
            resultJSON.setMsg("未选择星期");
            resultJSON.setSuccess("1");
            return resultJSON;
        }
        else{
            for(int i = 0; i < weeks.length; i++){
                if(i + 1 == 1){
                    if(Integer.valueOf(weeks[i]) == 1){
                        dietPromotion.setApplyToMon(1);
                    }
                    else{
                        dietPromotion.setApplyToMon(0);
                    }
                }
                if(i + 1 == 2){
                    if(Integer.valueOf(weeks[i]) == 1){
                        dietPromotion.setApplyToTue(1);
                    }
                    else{
                        dietPromotion.setApplyToTue(0);
                    }
                }
                if(i + 1 == 3){
                    if(Integer.valueOf(weeks[i]) == 1){
                        dietPromotion.setApplyToWed(1);
                    }
                    else{
                        dietPromotion.setApplyToWed(0);
                    }
                }
                if(i + 1 == 4){
                    if(Integer.valueOf(weeks[i]) == 1){
                        dietPromotion.setApplyToThu(1);
                    }
                    else{
                        dietPromotion.setApplyToThu(0);
                    }
                }
                if(i + 1 == 5){
                    if(Integer.valueOf(weeks[i]) == 1){
                        dietPromotion.setApplyToFri(1);
                    }
                    else{
                        dietPromotion.setApplyToFri(0);
                    }
                }
                if(i + 1 == 6){
                    if(Integer.valueOf(weeks[i]) == 1){
                        dietPromotion.setApplyToSat(1);
                    }
                    else{
                        dietPromotion.setApplyToSat(0);
                    }
                }
                if(i + 1 == 7){
                    if(Integer.valueOf(weeks[i]) == 1){
                        dietPromotion.setApplyToSun(1);
                    }
                    else{
                        dietPromotion.setApplyToSun(0);
                    }
                }
            }
        }
        dietPromotion.setSuperposition(false);
        dietPromotion.setDeleted(false);
        dietPromotion.setPromotionName((String)params.get("promotionName"));
        dietPromotion.setUse(true);
        dietPromotion.setLastUpdateBy("administrator");
        dietPromotion.setLastUpdateAt(new Date());
        int flag = dietPromotionMapper.update(dietPromotion);
        if(flag == 1){
            if(list != null){
                //买赠促销
                if(dietPromotion.getPromotionType() == 4){
                    Map<String, Object> dietPromotionBuyGiveMap = new HashMap<>();
                    dietPromotionBuyGiveMap.put("dietPromotionId", new BigInteger((String)params.get("id")));
                    List<DietPromotionBuyGive> dietPromotionBuyGives = dietPromotionBuyGiveMapper.dietPromotionBuyGiveList(dietPromotionBuyGiveMap);
                    Map<String, Object> deleteDietPromotionBuyGiveMap = new HashMap<>();
                    deleteDietPromotionBuyGiveMap.put("isDeleted", 1);
                    for(int j = 0; j < dietPromotionBuyGives.size(); j++){
                        deleteDietPromotionBuyGiveMap.put("id", dietPromotionBuyGives.get(j).getId());
                        deleteDietPromotionBuyGiveMap.put("lastUpdateBy", "administrator");
                        deleteDietPromotionBuyGiveMap.put("lastUpdateAt", new Date());
                        int falgDeleteDietPromotionBuyGive = dietPromotionBuyGiveMapper.update(deleteDietPromotionBuyGiveMap);
                        if(falgDeleteDietPromotionBuyGive == 0){
                            resultJSON.setSuccess("1");
                            resultJSON.setMsg("促销活动保存失败");
                            return resultJSON;
                        }
                    }
                    for(int i = 0; i < list.size(); i++){
                        Map<String, Object> mapDietPromotionBuyGive = new HashMap<>();
                        mapDietPromotionBuyGive.put("goodsId", ((Map)(list.get(i))).get("buyGoodsId"));
                        mapDietPromotionBuyGive.put("giveGoodsId", ((Map)(list.get(i))).get("giveGoodsId"));
                        mapDietPromotionBuyGive.put("buyNum", ((Map)(list.get(i))).get("buyNum"));
                        mapDietPromotionBuyGive.put("giveNum", ((Map)(list.get(i))).get("giveNum"));
                        mapDietPromotionBuyGive.put("dietPromotionId", dietPromotion.getId());
                        mapDietPromotionBuyGive.put("isDeleted", 0);
                        mapDietPromotionBuyGive.put("createBy", "administrator");
                        mapDietPromotionBuyGive.put("lastUpdateBy", "administrator");
                        mapDietPromotionBuyGive.put("createAt", new Date());
                        mapDietPromotionBuyGive.put("lastUpdateAt", new Date());
                        mapDietPromotionBuyGive.put("tenantId", new BigInteger((String)params.get("tenantId")));
                        mapDietPromotionBuyGive.put("branchId", new BigInteger((String)params.get("myBranchId")));
                        int flagDietPromotionBuyGive = dietPromotionBuyGiveMapper.insert(mapDietPromotionBuyGive);
                        if(flagDietPromotionBuyGive == 1){
                            Map<String, Object> dietPromotionGoodsRMap = new HashMap<>();
                            dietPromotionGoodsRMap.put("dietPromotionId", new BigInteger((String)params.get("id")));
                            List<DietPromotionGoodsR> dietPromotionGoodsRs = dietPromotionGoodsRMapper.dietPromotionGoodsRList(dietPromotionGoodsRMap);
                            Map<String, Object> deleteDietPromotionGoodsRMap = new HashMap<>();
                            deleteDietPromotionGoodsRMap.put("isDeleted", 1);
                            for(int j = 0; j < dietPromotionGoodsRs.size(); j++){
                                deleteDietPromotionGoodsRMap.put("id", dietPromotionGoodsRs.get(j).getId());
                                deleteDietPromotionGoodsRMap.put("lastUpdateBy", "administrator");
                                deleteDietPromotionGoodsRMap.put("lastUpdateAt", new Date());
                                int falgDeleteDietPromotionGoodsR = dietPromotionGoodsRMapper.update(deleteDietPromotionGoodsRMap);
                                if(falgDeleteDietPromotionGoodsR == 0){
                                    resultJSON.setSuccess("1");
                                    resultJSON.setMsg("促销活动保存失败");
                                    return resultJSON;
                                }
                            }
                            Map<String, Object> mapDietPromotionGoodsR = new HashMap<>();
                            mapDietPromotionGoodsR.put("goodsId", ((Map)(list.get(i))).get("buyGoodsId"));
                            mapDietPromotionGoodsR.put("dietPromotionId", dietPromotion.getId());
                            mapDietPromotionGoodsR.put("isUse", 1);
                            mapDietPromotionGoodsR.put("startDate", dietPromotion.getStartDate());
                            mapDietPromotionGoodsR.put("startTime", dietPromotion.getStartTime());
                            mapDietPromotionGoodsR.put("endDate", dietPromotion.getEndDate());
                            mapDietPromotionGoodsR.put("endTime", dietPromotion.getEndTime());
                            StringBuffer weekTemp = new StringBuffer();
                            if(params.get("weekInsert").toString().contains(",")){
                                for(int j = 0; j < weeks.length; j++){
                                    if(j < weeks.length - 1){
                                        weekTemp.append(weeks[j] + ",");
                                    }
                                    else{
                                        weekTemp.append(weeks[j]);
                                    }
                                }
                                mapDietPromotionGoodsR.put("week", weekTemp.toString());
                            }
                            else{
                                mapDietPromotionGoodsR.put("week", params.get("weekInsert"));
                            }
                            mapDietPromotionGoodsR.put("isDeleted", 0);
                            mapDietPromotionGoodsR.put("createBy", "administrator");
                            mapDietPromotionGoodsR.put("lastUpdateBy", "administrator");
                            mapDietPromotionGoodsR.put("createAt", new Date());
                            mapDietPromotionGoodsR.put("lastUpdateAt", new Date());
                            mapDietPromotionGoodsR.put("tenantId", dietPromotion.getTenantId());
                            int flagDietPromotionGoodsR = dietPromotionGoodsRMapper.insert(mapDietPromotionGoodsR);
                            if(flagDietPromotionGoodsR == 1){
                                Map<String, Object> dietPromotionBranchRMap = new HashMap<>();
                                dietPromotionBranchRMap.put("dietPromotionId", new BigInteger((String)params.get("id")));
                                List<DietPromotionBranchR> dietPromotionBranchRs = dietPromotionBranchRMapper.dietPromotionBranchRList(dietPromotionBranchRMap);
                                Map<String, Object> updateDietPromotionBranchRMap = new HashMap<>();
                                updateDietPromotionBranchRMap.put("isDeleted", 1);
                                for(int j = 0; j < dietPromotionBranchRs.size(); j++){
                                    updateDietPromotionBranchRMap.put("id", dietPromotionBranchRs.get(j).getId());
                                    updateDietPromotionBranchRMap.put("lastUpdateBy", "administrator");
                                    updateDietPromotionBranchRMap.put("lastUpdateAt", new Date());
                                    int flagUpdateDietPromotionBranchR = dietPromotionBranchRMapper.update(updateDietPromotionBranchRMap);
                                    if(flagUpdateDietPromotionBranchR == 0){
                                        resultJSON.setSuccess("1");
                                        resultJSON.setMsg("促销活动保存失败");
                                        return resultJSON;
                                    }
                                }
                            }
                            else{
                                resultJSON.setSuccess("1");
                                resultJSON.setMsg("促销活动保存失败");
                                return resultJSON;
                            }
                        }
                        else{
                            resultJSON.setSuccess("1");
                            resultJSON.setMsg("促销活动保存失败");
                            return resultJSON;
                        }
                    }
                }
                //再买优惠
                if(dietPromotion.getPromotionType() == 6){
                    Map<String, Object> dietPromotionDiscountMap = new HashMap<>();
                    dietPromotionDiscountMap.put("dietPromotionId", new BigInteger((String)params.get("id")));
                    List<DietPromotionDiscount> dietPromotionDiscounts = dietPromotionDiscountMapper.dietPromotionDiscountList(dietPromotionDiscountMap);
                    Map<String, Object> deleteDietPromotionDiscountsMap = new HashMap<>();
                    deleteDietPromotionDiscountsMap.put("isDeleted", 1);
                    for(int j = 0; j < dietPromotionDiscounts.size(); j++){
                        deleteDietPromotionDiscountsMap.put("id", dietPromotionDiscounts.get(j).getId());
                        deleteDietPromotionDiscountsMap.put("lastUpdateBy", "administrator");
                        deleteDietPromotionDiscountsMap.put("lastUpdateAt", new Date());
                        int falgDeleteDietPromotionDiscount = dietPromotionDiscountMapper.update(deleteDietPromotionDiscountsMap);
                        if(falgDeleteDietPromotionDiscount == 0){
                            resultJSON.setSuccess("1");
                            resultJSON.setMsg("促销活动保存失败");
                            return resultJSON;
                        }
                    }
                    for(int i = 0; i < list.size(); i++){
                        Map<String, Object> mapDietPromotionDiscount = new HashMap<>();
                        mapDietPromotionDiscount.put("goodsId", ((Map)list.get(i)).get("buyGoodsId"));
                        mapDietPromotionDiscount.put("quantity", ((Map)list.get(i)).get("quantity"));
                        mapDietPromotionDiscount.put("discount", ((Map)list.get(i)).get("discount"));
                        mapDietPromotionDiscount.put("dietPromotionId", dietPromotion.getId());
                        mapDietPromotionDiscount.put("isDeleted", 0);
                        mapDietPromotionDiscount.put("createBy", "administrator");
                        mapDietPromotionDiscount.put("lastUpdateBy", "administrator");
                        mapDietPromotionDiscount.put("createAt", new Date());
                        mapDietPromotionDiscount.put("lastUpdateAt", new Date());
                        mapDietPromotionDiscount.put("tenantId", new BigInteger((String)params.get("tenantId")));
                        mapDietPromotionDiscount.put("branchId", new BigInteger((String)params.get("myBranchId")));
                        int flagDietPromotionDiscount = dietPromotionDiscountMapper.insert(mapDietPromotionDiscount);
                        if(flagDietPromotionDiscount == 1){
                            Map<String, Object> dietPromotionGoodsRMap = new HashMap<>();
                            dietPromotionGoodsRMap.put("dietPromotionId", new BigInteger((String)params.get("id")));
                            List<DietPromotionGoodsR> dietPromotionGoodsRs = dietPromotionGoodsRMapper.dietPromotionGoodsRList(dietPromotionGoodsRMap);
                            Map<String, Object> deleteDietPromotionGoodsRMap = new HashMap<>();
                            deleteDietPromotionGoodsRMap.put("isDeleted", 1);
                            for(int j = 0; j < dietPromotionGoodsRs.size(); j++){
                                deleteDietPromotionGoodsRMap.put("id", dietPromotionGoodsRs.get(j).getId());
                                deleteDietPromotionGoodsRMap.put("lastUpdateBy", "administrator");
                                deleteDietPromotionGoodsRMap.put("lastUpdateAt", new Date());
                                int falgDeleteDietPromotionGoodsR = dietPromotionGoodsRMapper.update(deleteDietPromotionGoodsRMap);
                                if(falgDeleteDietPromotionGoodsR == 0){
                                    resultJSON.setSuccess("1");
                                    resultJSON.setMsg("促销活动保存失败");
                                    return resultJSON;
                                }
                            }
                            Map<String, Object> mapDietPromotionGoodsR = new HashMap<>();
                            mapDietPromotionGoodsR.put("goodsId", ((Map)(list.get(i))).get("buyGoodsId"));
                            mapDietPromotionGoodsR.put("dietPromotionId", dietPromotion.getId());
                            mapDietPromotionGoodsR.put("isUse", 1);
                            mapDietPromotionGoodsR.put("startDate", dietPromotion.getStartDate());
                            mapDietPromotionGoodsR.put("startTime", dietPromotion.getStartTime());
                            mapDietPromotionGoodsR.put("endDate", dietPromotion.getEndDate());
                            mapDietPromotionGoodsR.put("endTime", dietPromotion.getEndTime());
                            StringBuffer weekTemp = new StringBuffer();
                            if(params.get("weekInsert").toString().contains(",")){
                                for(int j = 0; j < weeks.length; j++){
                                    if(j < weeks.length - 1){
                                        weekTemp.append(weeks[j] + ",");
                                    }
                                    else{
                                        weekTemp.append(weeks[j]);
                                    }
                                }
                                mapDietPromotionGoodsR.put("week", weekTemp.toString());
                            }
                            else{
                                mapDietPromotionGoodsR.put("week", params.get("weekInsert"));
                            }
                            mapDietPromotionGoodsR.put("isDeleted", 0);
                            mapDietPromotionGoodsR.put("createBy", "administrator");
                            mapDietPromotionGoodsR.put("lastUpdateBy", "administrator");
                            mapDietPromotionGoodsR.put("createAt", new Date());
                            mapDietPromotionGoodsR.put("lastUpdateAt", new Date());
                            mapDietPromotionGoodsR.put("tenantId", dietPromotion.getTenantId());
                            int flagDietPromotionGoodsR = dietPromotionGoodsRMapper.insert(mapDietPromotionGoodsR);
                            if(flagDietPromotionGoodsR == 1){
                                Map<String, Object> dietPromotionBranchRMap = new HashMap<>();
                                dietPromotionBranchRMap.put("dietPromotionId", new BigInteger((String)params.get("id")));
                                List<DietPromotionBranchR> dietPromotionBranchRs = dietPromotionBranchRMapper.dietPromotionBranchRList(dietPromotionBranchRMap);
                                Map<String, Object> updateDietPromotionBranchRMap = new HashMap<>();
                                updateDietPromotionBranchRMap.put("isDeleted", 1);
                                for(int j = 0; j < dietPromotionBranchRs.size(); j++){
                                    updateDietPromotionBranchRMap.put("id", dietPromotionBranchRs.get(j).getId());
                                    updateDietPromotionBranchRMap.put("lastUpdateBy", "administrator");
                                    updateDietPromotionBranchRMap.put("lastUpdateAt", new Date());
                                    int flagUpdateDietPromotionBranchR = dietPromotionBranchRMapper.update(updateDietPromotionBranchRMap);
                                    if(flagUpdateDietPromotionBranchR == 0){
                                        resultJSON.setSuccess("1");
                                        resultJSON.setMsg("促销活动保存失败");
                                        return resultJSON;
                                    }
                                }

                            }
                            else{
                                resultJSON.setSuccess("1");
                                resultJSON.setMsg("促销活动保存失败");
                                return resultJSON;
                            }
                        }
                        else{
                            resultJSON.setSuccess("1");
                            resultJSON.setMsg("促销活动保存失败");
                            return resultJSON;
                        }
                    }
                }
                //满减
                if(dietPromotion.getPromotionType() == 7){
                    Map<String, Object> dietPromotionSatisfyMap = new HashMap<>();
                    dietPromotionSatisfyMap.put("dietPromotionId", new BigInteger((String)params.get("id")));
                    List<DietPromotionSatisfyReduce> dietPromotionSatisfyReduces = dietPromotionSatisfyReduceMapper.dietPromotionSatisfyReduceList(dietPromotionSatisfyMap);
                    Map<String, Object> deleteDietPromotionSatisfyReduceMap = new HashMap<>();
                    deleteDietPromotionSatisfyReduceMap.put("isDeleted", 1);
                    for(int j = 0; j < dietPromotionSatisfyReduces.size(); j++){
                        deleteDietPromotionSatisfyReduceMap.put("id", dietPromotionSatisfyReduces.get(j).getId());
                        deleteDietPromotionSatisfyReduceMap.put("lastUpdateBy", "administrator");
                        deleteDietPromotionSatisfyReduceMap.put("lastUpdateAt", new Date());
                        int falgDeleteDietPromotionSatisfyReduce = dietPromotionSatisfyReduceMapper.update(deleteDietPromotionSatisfyReduceMap);
                        if(falgDeleteDietPromotionSatisfyReduce == 0){
                            resultJSON.setSuccess("1");
                            resultJSON.setMsg("促销活动保存失败");
                            return resultJSON;
                        }
                    }
                    for(int i = 0; i < list.size(); i++){
                        Map<String, Object> mapDietPromotionSatisfyReduce = new HashMap<>();
                        mapDietPromotionSatisfyReduce.put("goodsId", ((Map)list.get(i)).get("buyGoodsId"));
                        mapDietPromotionSatisfyReduce.put("satisfy", ((Map)list.get(i)).get("satisfy"));
                        mapDietPromotionSatisfyReduce.put("reduction", ((Map)list.get(i)).get("reduction"));
                        mapDietPromotionSatisfyReduce.put("dietPromotionId", dietPromotion.getId());
                        mapDietPromotionSatisfyReduce.put("isDeleted", 0);
                        mapDietPromotionSatisfyReduce.put("createBy", "administrator");
                        mapDietPromotionSatisfyReduce.put("lastUpdateBy", "administrator");
                        mapDietPromotionSatisfyReduce.put("createAt", new Date());
                        mapDietPromotionSatisfyReduce.put("lastUpdateAt", new Date());
                        mapDietPromotionSatisfyReduce.put("tenantId", new BigInteger((String)params.get("tenantId")));
                        mapDietPromotionSatisfyReduce.put("branchId", new BigInteger((String)params.get("myBranchId")));
                        int flagDietPromotionSatisfyReduce = dietPromotionSatisfyReduceMapper.insert(mapDietPromotionSatisfyReduce);
                        if(flagDietPromotionSatisfyReduce == 1){
                            Map<String, Object> dietPromotionGoodsRMap = new HashMap<>();
                            dietPromotionGoodsRMap.put("dietPromotionId", new BigInteger((String)params.get("id")));
                            List<DietPromotionGoodsR> dietPromotionGoodsRs = dietPromotionGoodsRMapper.dietPromotionGoodsRList(dietPromotionGoodsRMap);
                            Map<String, Object> deleteDietPromotionGoodsRMap = new HashMap<>();
                            deleteDietPromotionGoodsRMap.put("isDeleted", 1);
                            for(int j = 0; j < dietPromotionGoodsRs.size(); j++){
                                deleteDietPromotionGoodsRMap.put("id", dietPromotionGoodsRs.get(j).getId());
                                deleteDietPromotionGoodsRMap.put("lastUpdateBy", "administrator");
                                deleteDietPromotionGoodsRMap.put("lastUpdateAt", new Date());
                                int falgDeleteDietPromotionGoodsR = dietPromotionGoodsRMapper.update(deleteDietPromotionGoodsRMap);
                                if(falgDeleteDietPromotionGoodsR == 0){
                                    resultJSON.setSuccess("1");
                                    resultJSON.setMsg("促销活动保存失败");
                                    return resultJSON;
                                }
                            }
                            Map<String, Object> mapDietPromotionGoodsR = new HashMap<>();
                            mapDietPromotionGoodsR.put("goodsId", ((Map)(list.get(i))).get("buyGoodsId"));
                            mapDietPromotionGoodsR.put("dietPromotionId", dietPromotion.getId());
                            mapDietPromotionGoodsR.put("isUse", 1);
                            mapDietPromotionGoodsR.put("startDate", dietPromotion.getStartDate());
                            mapDietPromotionGoodsR.put("startTime", dietPromotion.getStartTime());
                            mapDietPromotionGoodsR.put("endDate", dietPromotion.getEndDate());
                            mapDietPromotionGoodsR.put("endTime", dietPromotion.getEndTime());
                            StringBuffer weekTemp = new StringBuffer();
                            if(params.get("weekInsert").toString().contains(",")){
                                for(int j = 0; j < weeks.length; j++){
                                    if(j < weeks.length - 1){
                                        weekTemp.append(weeks[j] + ",");
                                    }
                                    else{
                                        weekTemp.append(weeks[j]);
                                    }
                                }
                                mapDietPromotionGoodsR.put("week", weekTemp.toString());
                            }
                            else{
                                mapDietPromotionGoodsR.put("week", params.get("weekInsert"));
                            }
                            mapDietPromotionGoodsR.put("isDeleted", 0);
                            mapDietPromotionGoodsR.put("createBy", "administrator");
                            mapDietPromotionGoodsR.put("lastUpdateBy", "administrator");
                            mapDietPromotionGoodsR.put("createAt", new Date());
                            mapDietPromotionGoodsR.put("lastUpdateAt", new Date());
                            mapDietPromotionGoodsR.put("tenantId", dietPromotion.getTenantId());
                            int flagDietPromotionGoodsR = dietPromotionGoodsRMapper.insert(mapDietPromotionGoodsR);
                            if(flagDietPromotionGoodsR == 1){
                                Map<String, Object> dietPromotionBranchRMap = new HashMap<>();
                                dietPromotionBranchRMap.put("dietPromotionId", new BigInteger((String)params.get("id")));
                                List<DietPromotionBranchR> dietPromotionBranchRs = dietPromotionBranchRMapper.dietPromotionBranchRList(dietPromotionBranchRMap);
                                Map<String, Object> updateDietPromotionBranchRMap = new HashMap<>();
                                updateDietPromotionBranchRMap.put("isDeleted", 1);
                                for(int j = 0; j < dietPromotionBranchRs.size(); j++){
                                    updateDietPromotionBranchRMap.put("id", dietPromotionBranchRs.get(j).getId());
                                    updateDietPromotionBranchRMap.put("lastUpdateBy", "administrator");
                                    updateDietPromotionBranchRMap.put("lastUpdateAt", new Date());
                                    int flagUpdateDietPromotionBranchR = dietPromotionBranchRMapper.update(updateDietPromotionBranchRMap);
                                    if(flagUpdateDietPromotionBranchR == 0){
                                        resultJSON.setSuccess("1");
                                        resultJSON.setMsg("促销活动保存失败");
                                        return resultJSON;
                                    }
                                }

                            }
                            else{
                                resultJSON.setSuccess("1");
                                resultJSON.setMsg("促销活动保存失败");
                                return resultJSON;
                            }
                        }
                        else{
                            resultJSON.setSuccess("1");
                            resultJSON.setMsg("促销活动保存失败");
                            return resultJSON;
                        }
                    }
                }
                //单品促销
                if(dietPromotion.getPromotionType() == 11){
                    Map<String, Object> dietPromotionSpecialMap = new HashMap<>();
                    dietPromotionSpecialMap.put("dietPromotionId", new BigInteger((String)params.get("id")));
                    List<DietPromotionSpecial> dietPromotionSpecials = dietPromotionSpecialMapper.dietPromotionSpecialList(dietPromotionSpecialMap);
                    Map<String, Object> deleteDietPromotionSpecialMap = new HashMap<>();
                    deleteDietPromotionSpecialMap.put("isDeleted", 1);
                    for(int j = 0; j < dietPromotionSpecials.size(); j++){
                        deleteDietPromotionSpecialMap.put("id", dietPromotionSpecials.get(j).getId());
                        deleteDietPromotionSpecialMap.put("lastUpdateBy", "administrator");
                        deleteDietPromotionSpecialMap.put("lastUpdateAt", new Date());
                        int falgDeleteDietPromotionSpecial = dietPromotionSpecialMapper.update(deleteDietPromotionSpecialMap);
                        if(falgDeleteDietPromotionSpecial == 0){
                            resultJSON.setSuccess("1");
                            resultJSON.setMsg("促销活动保存失败");
                            return resultJSON;
                        }
                    }
                    for(int i = 0; i < list.size(); i++){
                        Map<String, Object> mapDietPromotionSpecial = new HashMap<>();
                        mapDietPromotionSpecial.put("goodsId", ((Map)list.get(i)).get("buyGoodsId"));
                        mapDietPromotionSpecial.put("promotionPrice", ((Map)list.get(i)).get("promotionPrice"));
                        mapDietPromotionSpecial.put("promotionPercent", ((Map)list.get(i)).get("promotionPercent"));
                        mapDietPromotionSpecial.put("dietPromotionId", dietPromotion.getId());
                        mapDietPromotionSpecial.put("isDeleted", 0);
                        mapDietPromotionSpecial.put("createBy", "administrator");
                        mapDietPromotionSpecial.put("lastUpdateBy", "administrator");
                        mapDietPromotionSpecial.put("createAt", new Date());
                        mapDietPromotionSpecial.put("lastUpdateAt", new Date());
                        mapDietPromotionSpecial.put("tenantId", new BigInteger((String)params.get("tenantId")));
                        mapDietPromotionSpecial.put("branchId", new BigInteger((String)params.get("myBranchId")));
                        int flagDietPromotionSpecial = dietPromotionSpecialMapper.insert(mapDietPromotionSpecial);
                        if(flagDietPromotionSpecial == 1){
                            Map<String, Object> dietPromotionGoodsRMap = new HashMap<>();
                            dietPromotionGoodsRMap.put("dietPromotionId", new BigInteger((String)params.get("id")));
                            List<DietPromotionGoodsR> dietPromotionGoodsRs = dietPromotionGoodsRMapper.dietPromotionGoodsRList(dietPromotionGoodsRMap);
                            Map<String, Object> deleteDietPromotionGoodsRMap = new HashMap<>();
                            deleteDietPromotionGoodsRMap.put("isDeleted", 1);
                            for(int j = 0; j < dietPromotionGoodsRs.size(); j++){
                                deleteDietPromotionGoodsRMap.put("id", dietPromotionGoodsRs.get(j).getId());
                                deleteDietPromotionGoodsRMap.put("lastUpdateBy", "administrator");
                                deleteDietPromotionGoodsRMap.put("lastUpdateAt", new Date());
                                int falgDeleteDietPromotionGoodsR = dietPromotionGoodsRMapper.update(deleteDietPromotionGoodsRMap);
                                if(falgDeleteDietPromotionGoodsR == 0){
                                    resultJSON.setSuccess("1");
                                    resultJSON.setMsg("促销活动保存失败");
                                    return resultJSON;
                                }
                            }
                            Map<String, Object> mapDietPromotionGoodsR = new HashMap<>();
                            mapDietPromotionGoodsR.put("goodsId", ((Map)(list.get(i))).get("buyGoodsId"));
                            mapDietPromotionGoodsR.put("dietPromotionId", dietPromotion.getId());
                            mapDietPromotionGoodsR.put("isUse", 1);
                            mapDietPromotionGoodsR.put("startDate", dietPromotion.getStartDate());
                            mapDietPromotionGoodsR.put("startTime", dietPromotion.getStartTime());
                            mapDietPromotionGoodsR.put("endDate", dietPromotion.getEndDate());
                            mapDietPromotionGoodsR.put("endTime", dietPromotion.getEndTime());
                            StringBuffer weekTemp = new StringBuffer();
                            if(params.get("weekInsert").toString().contains(",")){
                                for(int j = 0; j < weeks.length; j++){
                                    if(j < weeks.length - 1){
                                        weekTemp.append(weeks[j] + ",");
                                    }
                                    else{
                                        weekTemp.append(weeks[j]);
                                    }
                                }
                                mapDietPromotionGoodsR.put("week", weekTemp.toString());
                            }
                            else{
                                mapDietPromotionGoodsR.put("week", params.get("weekInsert"));
                            }
                            mapDietPromotionGoodsR.put("isDeleted", 0);
                            mapDietPromotionGoodsR.put("createBy", "administrator");
                            mapDietPromotionGoodsR.put("lastUpdateBy", "administrator");
                            mapDietPromotionGoodsR.put("createAt", new Date());
                            mapDietPromotionGoodsR.put("lastUpdateAt", new Date());
                            mapDietPromotionGoodsR.put("tenantId", dietPromotion.getTenantId());
                            int flagDietPromotionGoodsR = dietPromotionGoodsRMapper.insert(mapDietPromotionGoodsR);
                            if(flagDietPromotionGoodsR == 1){
                                Map<String, Object> dietPromotionBranchRMap = new HashMap<>();
                                dietPromotionBranchRMap.put("dietPromotionId", new BigInteger((String)params.get("id")));
                                List<DietPromotionBranchR> dietPromotionBranchRs = dietPromotionBranchRMapper.dietPromotionBranchRList(dietPromotionBranchRMap);
                                Map<String, Object> updateDietPromotionBranchRMap = new HashMap<>();
                                updateDietPromotionBranchRMap.put("isDeleted", 1);
                                for(int j = 0; j < dietPromotionBranchRs.size(); j++){
                                    updateDietPromotionBranchRMap.put("id", dietPromotionBranchRs.get(j).getId());
                                    updateDietPromotionBranchRMap.put("lastUpdateBy", "administrator");
                                    updateDietPromotionBranchRMap.put("lastUpdateAt", new Date());
                                    int flagUpdateDietPromotionBranchR = dietPromotionBranchRMapper.update(updateDietPromotionBranchRMap);
                                    if(flagUpdateDietPromotionBranchR == 0){
                                        resultJSON.setSuccess("1");
                                        resultJSON.setMsg("促销活动保存失败");
                                        return resultJSON;
                                    }
                                }
                            }
                            else{
                                resultJSON.setSuccess("1");
                                resultJSON.setMsg("促销活动保存失败");
                                return resultJSON;
                            }
                        }
                        else{
                            resultJSON.setSuccess("1");
                            resultJSON.setMsg("促销活动保存失败");
                            return resultJSON;
                        }
                    }
                }
                Map<String, Object> param = new HashMap<>();
                param.put("dietPromotionId", dietPromotion.getId());
                //查询已有机构关联
                List<DietPromotionBranchR> branchRs = dietPromotionBranchRMapper.dietPromotionBranchRList(param);
                //遍历
                for(int k = 0; k < branchIds.length; k++){
                    boolean has = false;
                    if(branchRs != null){
                        for(DietPromotionBranchR branchR : branchRs){
                            //判断是否存在
                            if(branchR.getBranchId().equals(new BigInteger(branchIds[k]))){
                                branchRs.remove(branchR);
                                has = true;
                            }
                        }
                        //不存在则添加新关系
                        if(!has){
                            Map<String, Object> mapDietPromotionBranchR = new HashMap<>();
                            mapDietPromotionBranchR.put("dietPromotionId", dietPromotion.getId());
                            mapDietPromotionBranchR.put("branchId", BigInteger.valueOf(Long.valueOf(branchIds[k])));
                            mapDietPromotionBranchR.put("isDeleted", 0);
                            mapDietPromotionBranchR.put("createBy", "administrator");
                            mapDietPromotionBranchR.put("lastUpdateBy", "administrator");
                            mapDietPromotionBranchR.put("createAt", new Date());
                            mapDietPromotionBranchR.put("lastUpdateAt", new Date());
                            mapDietPromotionBranchR.put("tenantId", dietPromotion.getTenantId());
                            int flagDietPromotionBranchR = dietPromotionBranchRMapper.insert(mapDietPromotionBranchR);
                            if(flagDietPromotionBranchR == 1){
                                resultJSON.setSuccess("0");
                                resultJSON.setMsg("促销活动保存成功");
                            }
                            else{
                                resultJSON.setSuccess("1");
                                resultJSON.setMsg("促销活动保存失败");
                                return resultJSON;
                            }
                        }
                    }
                }
                //删除未关联的机构
                if(branchRs != null && branchRs.size() > 0){
                    for(DietPromotionBranchR branchR : branchRs){
                        Map<String, Object> param1 = new HashMap<>();
                        param1.put("id", branchR.getId());
                        param1.put("isDeleted", 1);
                        dietPromotionBranchRMapper.update(param1);
                    }
                }
            }
        }
        else{
            resultJSON.setSuccess("1");
            resultJSON.setMsg("促销活动保存失败");
        }
        return resultJSON;
    }

    /**
     * 修改卡券活动
     */
    public ResultJSON updateCardCouponsActivity(Map params) throws ServiceException, ParseException{
        ResultJSON resultJSON = new ResultJSON();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat simpleDateFormat1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String createBranchId = (String)params.get("myBranchId");
        String[] branchIds = ((String)params.get("branchIds")).split(",");
        for(int k = 0; k < branchIds.length; k++){
            BigInteger branchId = new BigInteger(branchIds[k]);
            BigInteger tenantId = new BigInteger((String)params.get("tenantId"));
            String activityName = (String)params.get("activityName");
            BigInteger cardId = new BigInteger((String)params.get("cardId"));
            Map<String, Object> map = new HashMap<>();
            map.put("tenantId", tenantId);
            map.put("cardId", cardId);
            List<DietPromotionFestival> dietPromotionFestivals = dietPromotionFestivalMapper.dietPromotionFestivalList(map);
            for(int n = 0; n < dietPromotionFestivals.size(); n++){
                Map<String, Object> map1 = new HashMap<>();
                map1.put("id", dietPromotionFestivals.get(n).getDietPromotionId());
                DietPromotion dietPromotion = dietPromotionMapper.findDietPromotionById(map1);
                if(dietPromotion != null){
                    Map<String, Object> map2 = new HashMap<>();
                    map2.put("dietPromotionId", dietPromotion.getId());
                    List<DietPromotionBranchR> dietPromotionBranchRs = dietPromotionBranchRMapper.dietPromotionBranchRList(map2);
                    if(dietPromotionBranchRs != null){
                        for(int j = 0; j < dietPromotionBranchRs.size(); j++){
                            if((dietPromotionBranchRs.get(j).getBranchId()).compareTo(branchId) == 0){
                                Date oldSDT = simpleDateFormat1.parse(dietPromotion.getStartDate().toString().substring(0, 10) + " " + dietPromotion.getStartTime().toString());
                                Date oldEDT = simpleDateFormat1.parse(dietPromotion.getEndDate().toString().substring(0, 10) + " " + dietPromotion.getEndTime().toString());
                                Date newSDT = simpleDateFormat1.parse(params.get("startDate") + " 00:00:00");
                                Date newEDT = simpleDateFormat1.parse(params.get("endDate") + " 23:59:59");
                                if(newSDT.after(oldEDT) || newEDT.before(oldSDT)){
                                }
                                else{
                                    if(dietPromotionBranchRs.get(j).getDietPromotionId().doubleValue() != Integer.parseInt(params.get("id").toString())){
                                        resultJSON.setSuccess("1");
                                        resultJSON.setMsg("同时间段内已有该活动或代金券，请重新设置活动时间段！");
                                        return resultJSON;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        Map<String, Object> mapDietPromotion = new HashMap<>();
        mapDietPromotion.put("tenantId", params.get("tenantId"));
        mapDietPromotion.put("createBranchId", params.get("createBranchId"));
        mapDietPromotion.put("promotionType", 8);
        mapDietPromotion.put("scope", params.get("scope"));
        mapDietPromotion.put("forCustomerType", params.get("forCustomerType"));
        mapDietPromotion.put("promotionStatus", params.get("promotionStatus"));
        mapDietPromotion.put("createBranchId", params.get("myBranchId"));
        mapDietPromotion.put("startDate", simpleDateFormat.parse((String)params.get("startDate")));
        mapDietPromotion.put("startTime", simpleDateFormat1.parse((String)params.get("startDate") + " 00:00:00"));
        mapDietPromotion.put("endDate", simpleDateFormat.parse((String)params.get("endDate")));
        mapDietPromotion.put("endTime", simpleDateFormat1.parse((String)params.get("endDate") + " 00:00:00"));
        mapDietPromotion.put("promotionName", params.get("activityName"));
        String[] weeks = ((String)params.get("weekInsert")).split(",");
        for(int i = 0; i < weeks.length; i++){
            if(i + 1 == 1){
                if(Integer.valueOf(weeks[i]) == 1){
                    mapDietPromotion.put("applyToMon", true);
                }
                else{
                    mapDietPromotion.put("applyToMon", false);
                }
            }
            if(i + 1 == 2){
                if(Integer.valueOf(weeks[i]) == 1){
                    mapDietPromotion.put("applyToTue", true);
                }
                else{
                    mapDietPromotion.put("applyToTue", false);
                }
            }
            if(i + 1 == 3){
                if(Integer.valueOf(weeks[i]) == 1){
                    mapDietPromotion.put("applyToWed", true);
                }
                else{
                    mapDietPromotion.put("applyToWed", false);
                }
            }
            if(i + 1 == 4){
                if(Integer.valueOf(weeks[i]) == 1){
                    mapDietPromotion.put("applyToThu", true);
                }
                else{
                    mapDietPromotion.put("applyToThu", false);
                }
            }
            if(i + 1 == 5){
                if(Integer.valueOf(weeks[i]) == 1){
                    mapDietPromotion.put("applyToFri", true);
                }
                else{
                    mapDietPromotion.put("applyToFri", false);
                }
            }
            if(i + 1 == 6){
                if(Integer.valueOf(weeks[i]) == 1){
                    mapDietPromotion.put("applyToSat", true);
                }
                else{
                    mapDietPromotion.put("applyToSat", false);
                }
            }
            if(i + 1 == 7){
                if(Integer.valueOf(weeks[i]) == 1){
                    mapDietPromotion.put("applyToSun", true);
                }
                else{
                    mapDietPromotion.put("applyToSun", false);
                }
            }
        }
        String md_code = vipService.getNextSelValue(SysConfig.SYS_DIETPROMOTION_NUM, 4, new BigInteger(params.get("tenantId").toString()));
        String y_M_d = Common.getNowCorrect2Millisecond().substring(2, 8);//六位年月日
        mapDietPromotion.put("promotionCode", "CX" + y_M_d + md_code);
        mapDietPromotion.put("isSuperposition", false);
        mapDietPromotion.put("memo", params.get("memo"));
        mapDietPromotion.put("isDeleted", 0);
        mapDietPromotion.put("promotionMame", params.get("activityName"));
        mapDietPromotion.put("isUse", 1);
        mapDietPromotion.put("id", new BigInteger((String)params.get("id")));
        mapDietPromotion.put("lastUpdateBy", "administrator");
        mapDietPromotion.put("lastUpdateAt", new Date());
        int flagDietPromotion = dietPromotionMapper.update(mapDietPromotion);
        if(flagDietPromotion == 1){
            Map<String, Object> mapFindFetival = new HashMap<>();
            mapFindFetival.put("dietPromotionId", mapDietPromotion.get("id"));
            DietPromotionFestival dietPromotionFestival = dietPromotionFestivalMapper.findByPromotionId(mapFindFetival);
            Map<String, Object> mapFestival = new HashMap<>();
            if(dietPromotionFestival != null){
                mapFestival.put("id", dietPromotionFestival.getId());
            }
            mapFestival.put("dietPromotionId", mapDietPromotion.get("id"));
            mapFestival.put("tenantId", params.get("tenantId"));
            mapFestival.put("activityName", params.get("activityName"));
            mapFestival.put("cardId", params.get("cardId"));
            mapFestival.put("limitPerOne", -1);
            mapFestival.put("sendLimitValue", params.get("forCustomerCondition"));
            mapFestival.put("totalInventory", Integer.parseInt((String)params.get("amount")));
            mapFestival.put("remainInventory", Integer.parseInt((String)params.get("amount")));
            mapFestival.put("isDeleted", 0);
            int flagFestival = dietPromotionFestivalMapper.update(mapFestival);
            if(flagFestival == 1){
                Map<String, Object> dietPromotionBranchRMap = new HashMap<>();
                dietPromotionBranchRMap.put("dietPromotionId", new BigInteger((String)params.get("id")));
                List<DietPromotionBranchR> dietPromotionBranchRs = dietPromotionBranchRMapper.dietPromotionBranchRList(dietPromotionBranchRMap);
                Map<String, Object> updateDietPromotionBranchRMap = new HashMap<>();
                updateDietPromotionBranchRMap.put("isDeleted", 1);
                for(int j = 0; j < dietPromotionBranchRs.size(); j++){
                    updateDietPromotionBranchRMap.put("id", dietPromotionBranchRs.get(j).getId());
                    updateDietPromotionBranchRMap.put("lastUpdateBy", "administrator");
                    updateDietPromotionBranchRMap.put("lastUpdateAt", new Date());
                    int flagUpdateDietPromotionBranchR = dietPromotionBranchRMapper.update(updateDietPromotionBranchRMap);
                    if(flagUpdateDietPromotionBranchR == 0){
                        resultJSON.setSuccess("1");
                        resultJSON.setMsg("促销活动保存失败");
                        return resultJSON;
                    }
                }
                if(branchIds != null){
                    for(int i = 0; i < branchIds.length; i++){
                        Map<String, Object> mapDietPromotionBranchR = new HashMap<>();
                        mapDietPromotionBranchR.put("dietPromotionId", mapDietPromotion.get("id"));
                        mapDietPromotionBranchR.put("branchId", BigInteger.valueOf(Long.valueOf(branchIds[i])));
                        mapDietPromotionBranchR.put("isDeleted", 0);
                        mapDietPromotionBranchR.put("createBy", "administrator");
                        mapDietPromotionBranchR.put("lastUpdateBy", "administrator");
                        mapDietPromotionBranchR.put("createAt", new Date());
                        mapDietPromotionBranchR.put("lastUpdateAt", new Date());
                        mapDietPromotionBranchR.put("tenantId", params.get("tenantId"));
                        int flagDietPromotionBranchR = dietPromotionBranchRMapper.insert(mapDietPromotionBranchR);
                        if(flagDietPromotionBranchR == 1){
                            resultJSON.setSuccess("0");
                            resultJSON.setMsg("促销活动保存成功");
                        }
                        else{
                            resultJSON.setSuccess("1");
                            resultJSON.setMsg("促销活动保存失败");
                            return resultJSON;
                        }
                    }
                }
            }
            else{
                resultJSON.setSuccess("1");
                resultJSON.setMsg("促销活动保存失败");
            }
        }
        else{
            resultJSON.setSuccess("1");
            resultJSON.setMsg("促销活动保存失败");
        }
        return resultJSON;
    }

    /**
     * 更新卡券游戏活动
     */
    public ResultJSON updateGame(Map params, DietPromotion dietPromotion) throws ServiceException, ParseException{
        ResultJSON resultJSON = new ResultJSON();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat simpleDateFormat1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String[] branchIds = ((String)params.get("branchIds")).split(",");
        String createBranchId = (String)params.get("myBranchId");
        for(int k = 0; k < branchIds.length; k++){
            BigInteger branchId = new BigInteger(branchIds[k]);
            BigInteger tenantId = new BigInteger((String)params.get("tenantId"));
            Map<String, Object> map = new HashMap<>();
            map.put("tenantId", tenantId);
            map.put("promotionType", 12);
            List<DietPromotion> dietPromotions = dietPromotionMapper.dietPromotionList(map);
            if(dietPromotions != null){
                for(int i = 0; i < dietPromotions.size(); i++){
                    Map<String, Object> mapDietPromotionBranchR = new HashMap<>();
                    mapDietPromotionBranchR.put("dietPromotionId", dietPromotions.get(i).getId());
                    List<DietPromotionBranchR> dietPromotionBranchRs = dietPromotionBranchRMapper.dietPromotionBranchRList(mapDietPromotionBranchR);
                    if(dietPromotionBranchRs != null){
                        for(int j = 0; j < dietPromotionBranchRs.size(); j++){
                            if((dietPromotionBranchRs.get(j).getBranchId()).compareTo(branchId) == 0){
                                Date oldSDT = simpleDateFormat1.parse(dietPromotions.get(i).getStartDate().toString().substring(0, 10) + " " + dietPromotions.get(i).getStartTime().toString());
                                Date oldEDT = simpleDateFormat1.parse(dietPromotions.get(i).getEndDate().toString().substring(0, 10) + " " + dietPromotions.get(i).getEndTime().toString());
                                Date newSDT = simpleDateFormat1.parse(params.get("start") + " 00:00:00");
                                Date newEDT = simpleDateFormat1.parse(params.get("end") + " 23:59:59");
                                if(newSDT.after(oldEDT) || newEDT.before(oldSDT)){
                                }
                                else{
                                    resultJSON.setSuccess("1");
                                    resultJSON.setMsg("同时间段内已有活动，请重新设置活动时间段！");
                                    return resultJSON;
                                }
                            }
                        }
                    }
                }
            }
        }
        Date startDate = simpleDateFormat.parse((String)params.get("start"));
        Date endDate = simpleDateFormat.parse((String)params.get("end"));
        Date startTime = simpleDateFormat1.parse(params.get("start") + " 00:00:00");
        Date endTime = simpleDateFormat1.parse(params.get("end") + " 23:59:59");
        if(params.get("giftGoodsOne") != null){
            if(Integer.parseInt((String)params.get("giftGoodsOne")) != 0){
                Map<String, Object> mapDietPromotionFestival = new HashMap<>();
                mapDietPromotionFestival.put("id", new BigInteger((String)params.get("giftGoodsOne")));
                List<DietPromotionFestival> dietPromotionFestivals = dietPromotionFestivalMapper.dietPromotionFestivalList(mapDietPromotionFestival);
                if(dietPromotionFestivals != null){
                    DietPromotionFestival dietPromotionFestival = dietPromotionFestivals.get(0);
                    Map<String, Object> mapDietPromotion = new HashMap<>();
                    mapDietPromotion.put("id", dietPromotionFestival.getDietPromotionId());
                    List<DietPromotion> dietPromotions = dietPromotionMapper.dietPromotionList(mapDietPromotion);
                    if(dietPromotions != null){
                        if(dietPromotions.size() > 0){
                            DietPromotion dietPromotion1 = dietPromotions.get(0);
                            Date feativalSDT = simpleDateFormat1.parse(dietPromotion1.getStartDate().toString().substring(0, 10) + " " + dietPromotion1.getStartTime().toString());
                            Date festivalEDT = simpleDateFormat1.parse(dietPromotion1.getEndDate().toString().substring(0, 10) + " " + dietPromotion1.getEndTime().toString());
                            if(!(startTime.after(feativalSDT) && endTime.before(festivalEDT))){
                                resultJSON.setSuccess("1");
                                resultJSON.setMsg("游戏活动时间范围不能大于选择的卡券活动范围！");
                                return resultJSON;
                            }
                        }
                    }
                }
            }
        }
        if(params.get("giftGoodsTwo") != null){
            if(Integer.parseInt((String)params.get("giftGoodsTwo")) != 0){
                Map<String, Object> mapDietPromotionFestival = new HashMap<>();
                mapDietPromotionFestival.put("id", new BigInteger((String)params.get("giftGoodsTwo")));
                List<DietPromotionFestival> dietPromotionFestivals = dietPromotionFestivalMapper.dietPromotionFestivalList(mapDietPromotionFestival);
                if(dietPromotionFestivals != null){
                    DietPromotionFestival dietPromotionFestival = dietPromotionFestivals.get(0);
                    Map<String, Object> mapDietPromotion = new HashMap<>();
                    mapDietPromotion.put("id", dietPromotionFestival.getDietPromotionId());
                    List<DietPromotion> dietPromotions = dietPromotionMapper.dietPromotionList(mapDietPromotion);
                    if(dietPromotions != null){
                        DietPromotion dietPromotion1 = dietPromotions.get(0);
                        Date feativalSDT = simpleDateFormat1.parse(dietPromotion1.getStartDate().toString().substring(0, 10) + " " + dietPromotion1.getStartTime().toString());
                        Date festivalEDT = simpleDateFormat1.parse(dietPromotion1.getEndDate().toString().substring(0, 10) + " " + dietPromotion1.getEndTime().toString());
                        if(!(startTime.after(feativalSDT) && endTime.before(festivalEDT))){
                            resultJSON.setSuccess("1");
                            resultJSON.setMsg("游戏活动时间范围不能大于选择的卡券活动范围！");
                            return resultJSON;
                        }
                    }
                }
            }
        }
        if(params.get("giftGoodsThree") != null){
            if(Integer.parseInt((String)params.get("giftGoodsThree")) != 0){
                Map<String, Object> mapDietPromotionFestival = new HashMap<>();
                mapDietPromotionFestival.put("id", new BigInteger((String)params.get("giftGoodsThree")));
                List<DietPromotionFestival> dietPromotionFestivals = dietPromotionFestivalMapper.dietPromotionFestivalList(mapDietPromotionFestival);
                if(dietPromotionFestivals != null){
                    DietPromotionFestival dietPromotionFestival = dietPromotionFestivals.get(0);
                    Map<String, Object> mapDietPromotion = new HashMap<>();
                    mapDietPromotion.put("id", dietPromotionFestival.getDietPromotionId());
                    List<DietPromotion> dietPromotions = dietPromotionMapper.dietPromotionList(mapDietPromotion);
                    if(dietPromotions != null){
                        DietPromotion dietPromotion1 = dietPromotions.get(0);
                        Date feativalSDT = simpleDateFormat1.parse(dietPromotion1.getStartDate().toString().substring(0, 10) + " " + dietPromotion1.getStartTime().toString());
                        Date festivalEDT = simpleDateFormat1.parse(dietPromotion1.getEndDate().toString().substring(0, 10) + " " + dietPromotion1.getEndTime().toString());
                        if(!(startTime.after(feativalSDT) && endTime.before(festivalEDT))){
                            resultJSON.setSuccess("1");
                            resultJSON.setMsg("游戏活动时间范围不能大于选择的卡券活动范围！");
                            return resultJSON;
                        }
                    }
                }
            }
        }
        BigDecimal winRate = new BigDecimal("0.00");
        if(params.get("winRateOne") != null && params.get("winRateTwo") == null && params.get("winRateThree") == null){
            winRate = winRate.add(new BigDecimal((String)params.get("winRateOne")));
        }
        if(params.get("winRateOne") == null && params.get("winRateTwo") != null && params.get("winRateThree") == null){
            winRate = winRate.add(new BigDecimal((String)params.get("winRateTwo")));
        }
        if(params.get("winRateOne") == null && params.get("winRateTwo") == null && params.get("winRateThree") != null){
            winRate = winRate.add(new BigDecimal((String)params.get("winRateThree")));
        }
        if(params.get("winRateOne") != null && params.get("winRateTwo") != null && params.get("winRateThree") == null){
            winRate = winRate.add(new BigDecimal((String)params.get("winRateOne"))).add(new BigDecimal((String)params.get("winRateTwo")));
        }
        if(params.get("winRateOne") != null && params.get("winRateTwo") == null && params.get("winRateThree") != null){
            winRate = winRate.add(new BigDecimal((String)params.get("winRateOne"))).add(new BigDecimal((String)params.get("winRateThree")));
        }
        if(params.get("winRateOne") == null && params.get("winRateTwo") != null && params.get("winRateThree") != null){
            winRate = winRate.add(new BigDecimal((String)params.get("winRateTwo"))).add(new BigDecimal((String)params.get("winRateThree")));
        }
        if(params.get("winRateOne") != null && params.get("winRateTwo") != null && params.get("winRateThree") != null){
            winRate = winRate.add(new BigDecimal((String)params.get("winRateOne"))).add(new BigDecimal((String)params.get("winRateTwo"))).add(new BigDecimal((String)params.get("winRateThree")));
        }
        if((new BigDecimal("100")).compareTo(winRate) < 0){
            resultJSON.setSuccess("1");
            resultJSON.setMsg("中奖率之和不能大于100%");
            return resultJSON;
        }
        dietPromotion.setStartDate(new java.sql.Date(startDate.getTime()));
        dietPromotion.setStartTime(new java.sql.Time(startTime.getTime()));
        dietPromotion.setEndDate(new java.sql.Date(endDate.getTime()));
        dietPromotion.setEndTime(new java.sql.Time(endTime.getTime()));

        dietPromotion.setTenantId(new BigInteger((String)params.get("tenantId")));
        dietPromotion.setCreateBranchId(new BigInteger((String)params.get("myBranchId")));

        String[] weeks = ((String)params.get("weekInsert")).split(",");
        if(weeks == null){
            resultJSON.setMsg("未选择星期");
            resultJSON.setSuccess("1");
            return resultJSON;
        }
        else{
            for(int i = 0; i < weeks.length; i++){
                if(i + 1 == 1){
                    if(Integer.valueOf(weeks[i]) == 1){
                        dietPromotion.setApplyToMon(1);
                    }
                    else{
                        dietPromotion.setApplyToMon(0);
                    }
                }
                if(i + 1 == 2){
                    if(Integer.valueOf(weeks[i]) == 1){
                        dietPromotion.setApplyToTue(1);
                    }
                    else{
                        dietPromotion.setApplyToTue(0);
                    }
                }
                if(i + 1 == 3){
                    if(Integer.valueOf(weeks[i]) == 1){
                        dietPromotion.setApplyToWed(1);
                    }
                    else{
                        dietPromotion.setApplyToWed(0);
                    }
                }
                if(i + 1 == 4){
                    if(Integer.valueOf(weeks[i]) == 1){
                        dietPromotion.setApplyToThu(1);
                    }
                    else{
                        dietPromotion.setApplyToThu(0);
                    }
                }
                if(i + 1 == 5){
                    if(Integer.valueOf(weeks[i]) == 1){
                        dietPromotion.setApplyToFri(1);
                    }
                    else{
                        dietPromotion.setApplyToFri(0);
                    }
                }
                if(i + 1 == 6){
                    if(Integer.valueOf(weeks[i]) == 1){
                        dietPromotion.setApplyToSat(1);
                    }
                    else{
                        dietPromotion.setApplyToSat(0);
                    }
                }
                if(i + 1 == 7){
                    if(Integer.valueOf(weeks[i]) == 1){
                        dietPromotion.setApplyToSun(1);
                    }
                    else{
                        dietPromotion.setApplyToSun(0);
                    }
                }
            }
        }
        dietPromotion.setSuperposition(false);
        dietPromotion.setDeleted(false);
        dietPromotion.setPromotionName((String)params.get("activityName"));
        dietPromotion.setUse(true);
        dietPromotion.setId(new BigInteger((String)params.get("id")));
        dietPromotion.setLastUpdateBy("administrator");
        dietPromotion.setLastUpdateAt(new Date());
        int flag = dietPromotionMapper.update(dietPromotion);
        if(flag == 1){
            if(dietPromotion.getPromotionType() == 12){
                Map<String, Object> mapFindDietGameScratchCard = new HashMap<>();
                mapFindDietGameScratchCard.put("dietPromotionId", new BigInteger((String)params.get("id")));
                DietGameScratchCard dietGameScratchCard = dietGameScratchCardMapper.findByDietPromotionId(mapFindDietGameScratchCard);
                Map<String, Object> mapDietGameScratchCard = new HashMap<>();
                if(dietGameScratchCard != null){
                    mapDietGameScratchCard.put("id", dietGameScratchCard.getId());
                }
                mapDietGameScratchCard.put("dietPromotionId", dietPromotion.getId());
                mapDietGameScratchCard.put("tenantId", dietPromotion.getTenantId());
                mapDietGameScratchCard.put("gameName", params.get("activityName"));
                mapDietGameScratchCard.put("attendTimes", Integer.parseInt((String)params.get("amount")));
                mapDietGameScratchCard.put("remark", params.get("remark"));
                mapDietGameScratchCard.put("isDeleted", 0);
                mapDietGameScratchCard.put("lastUpdateBy", "administrator");
                mapDietGameScratchCard.put("lastUpdateAt", new Date());
                int flagDietGameScratchCard = dietGameScratchCardMapper.update(mapDietGameScratchCard);
                if(flagDietGameScratchCard == 1){
                    Map<String, Object> mapFindDietGamePrizeItem = new HashMap<>();
                    mapFindDietGamePrizeItem.put("dietPromotionId", new BigInteger((String)params.get("id")));
                    List<DietGamePrizeItem> dietGamePrizeItems = dietGamePrizeItemMapper.dietGamePrizeItemList(mapFindDietGamePrizeItem);
                    Map<String, Object> mapDeleteDietGamePrizeItem = new HashMap<>();
                    mapDeleteDietGamePrizeItem.put("isDeleted", 1);
                    for(int k = 0; k < dietGamePrizeItems.size(); k++){
                        mapDeleteDietGamePrizeItem.put("id", dietGamePrizeItems.get(k).getId());
                        mapDeleteDietGamePrizeItem.put("lastUpdateBy", "administrator");
                        mapDeleteDietGamePrizeItem.put("lastUpdateAt", new Date());
                        int falgDeleteDietGamePrizeItem = dietGamePrizeItemMapper.update(mapDeleteDietGamePrizeItem);
                        if(falgDeleteDietGamePrizeItem == 0){
                            resultJSON.setSuccess("1");
                            resultJSON.setMsg("促销活动保存失败");
                            return resultJSON;
                        }
                    }
                    String giveName = null;
                    String winRates = null;
                    String number = null;
                    String giftGoods = null;
                    if(params.get("giftGoodsOne") != null){
                        giveName = (String)params.get("giveNameOne");
                        winRates = (String)params.get("winRateOne");
                        number = (String)params.get("numberOne");
                        giftGoods = (String)params.get("giftGoodsOne");
                    }
                    if(params.get("giftGoodsTwo") != null){
                        giveName = (String)params.get("giveNameTwo");
                        winRates = (String)params.get("winRateTwo");
                        number = (String)params.get("numberTwo");
                        giftGoods = (String)params.get("giftGoodsTwo");
                    }
                    if(params.get("giftGoodsThree") != null){
                        giveName = (String)params.get("giveNameThree");
                        winRates = (String)params.get("winRateThree");
                        number = (String)params.get("numberThree");
                        giftGoods = (String)params.get("giftGoodsThree");
                    }
                    Map<String, Object> mapDietGamePrizeItem = new HashMap<>();
                    mapDietGamePrizeItem.put("dietPromotionId", dietPromotion.getId());
                    mapDietGamePrizeItem.put("tenantId", dietPromotion.getTenantId());
                    mapDietGamePrizeItem.put("itemName", giveName);
                    mapDietGamePrizeItem.put("winningProbability", new BigDecimal(winRates));
                    mapDietGamePrizeItem.put("amount", Integer.parseInt(number));
                    if(Integer.parseInt(giftGoods) != 0){
                        mapDietGamePrizeItem.put("prizeDietPromotionId", Integer.parseInt(giftGoods));
                        mapDietGamePrizeItem.put("prizeDietPromotionType", Integer.parseInt("8"));
                    }
                    mapDietGamePrizeItem.put("isDeleted", 0);
                    int flagDietGamePrizeItem = dietGamePrizeItemMapper.insert(mapDietGamePrizeItem);
                    if(flagDietGamePrizeItem == 1){
                        Map<String, Object> dietPromotionBranchRMap = new HashMap<>();
                        dietPromotionBranchRMap.put("dietPromotionId", new BigInteger((String)params.get("id")));
                        List<DietPromotionBranchR> dietPromotionBranchRs = dietPromotionBranchRMapper.dietPromotionBranchRList(dietPromotionBranchRMap);
                        Map<String, Object> updateDietPromotionBranchRMap = new HashMap<>();
                        updateDietPromotionBranchRMap.put("isDeleted", 1);
                        for(int j = 0; j < dietPromotionBranchRs.size(); j++){
                            updateDietPromotionBranchRMap.put("id", dietPromotionBranchRs.get(j).getId());
                            updateDietPromotionBranchRMap.put("lastUpdateBy", "administrator");
                            updateDietPromotionBranchRMap.put("lastUpdateAt", new Date());
                            int flagUpdateDietPromotionBranchR = dietPromotionBranchRMapper.update(updateDietPromotionBranchRMap);
                            if(flagUpdateDietPromotionBranchR == 0){
                                resultJSON.setSuccess("1");
                                resultJSON.setMsg("促销活动保存失败");
                                return resultJSON;
                            }
                        }
                        if(branchIds != null){
                            for(int i = 0; i < branchIds.length; i++){
                                Map<String, Object> mapDietPromotionBranchR = new HashMap<>();
                                mapDietPromotionBranchR.put("dietPromotionId", dietPromotion.getId());
                                mapDietPromotionBranchR.put("branchId", BigInteger.valueOf(Long.valueOf(branchIds[i])));
                                mapDietPromotionBranchR.put("isDeleted", 0);
                                mapDietPromotionBranchR.put("createBy", "administrator");
                                mapDietPromotionBranchR.put("lastUpdateBy", "administrator");
                                mapDietPromotionBranchR.put("createAt", new Date());
                                mapDietPromotionBranchR.put("lastUpdateAt", new Date());
                                mapDietPromotionBranchR.put("tenantId", dietPromotion.getTenantId());
                                int flagDietPromotionBranchR = dietPromotionBranchRMapper.insert(mapDietPromotionBranchR);
                                if(flagDietPromotionBranchR == 1){
                                    resultJSON.setSuccess("0");
                                    resultJSON.setMsg("促销活动保存成功");
                                    return resultJSON;
                                }
                                else{
                                    resultJSON.setSuccess("1");
                                    resultJSON.setMsg("促销活动保存失败");
                                    return resultJSON;
                                }
                            }
                        }
                    }
                    else{
                        resultJSON.setSuccess("1");
                        resultJSON.setMsg("促销活动保存失败");
                    }
                }
                else{
                    resultJSON.setSuccess("1");
                    resultJSON.setMsg("促销活动保存失败");
                }
            }
        }
        else{
            resultJSON.setSuccess("1");
            resultJSON.setMsg("促销活动保存失败");
        }
        return resultJSON;
    }

    /**
     * 获取机构促销活动_满减 摘要
     *
     * @param params
     * @return
     */
    public Map<String, String> qryDietPromotionAbstract(Map params) throws ParseException{
        Map<String, String> map = new HashMap<>();
        BigInteger tenantId = new BigInteger(params.get("tenantId").toString());
        DecimalFormat d1 = new DecimalFormat("0.0");
        DecimalFormat d2 = new DecimalFormat("0.00");
        SimpleDateFormat date = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat time = new SimpleDateFormat("HH:mm:ss");
        SimpleDateFormat dateTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        int a = 1;
        int b = 1;
        int c = 1;
        int f = 1;
        int e = 1;
        int h = 1;
        Map<String, Object> param = new HashMap<>();
        param.put("branchId", params.get("branchId"));
        List<DietPromotionBranchR> dietPromotionBranchRs = dietPromotionBranchRMapper.dietPromotionBranchRList(param);
        for(int p = 0; p < dietPromotionBranchRs.size(); p++){
            Map<String, Object> param1 = new HashMap<>();
            param1.put("tenantId", params.get("tenantId"));
            param1.put("id", dietPromotionBranchRs.get(p).getDietPromotionId());
            param1.put("scope", 1);
            param1.put("promotionStatus", 0);
            List<DietPromotion> list = dietPromotionMapper.dietPromotionList(param1);
            for(int i = 0; i < list.size(); i++){
                DietPromotion d = list.get(i);
                Date startDT = dateTime.parse(date.format(d.getStartDate()) + " " + time.format(d.getStartTime()));
                Date endDT = dateTime.parse(date.format(d.getEndDate()) + " " + time.format(d.getEndTime()));
                Date nowDate = new Date();
                if((startDT.before(nowDate) && endDT.after(nowDate)) || startDT.after(nowDate)){
                    //获取星期
                    int toWeek = Calendar.getInstance().get(Calendar.DAY_OF_WEEK);
                    int isWeekSatisfy = 0;
                    switch(toWeek){
                        case 1:
                            isWeekSatisfy = d.getApplyToSun();
                            break;
                        case 2:
                            isWeekSatisfy = d.getApplyToMon();
                            break;
                        case 3:
                            isWeekSatisfy = d.getApplyToTue();
                            break;
                        case 4:
                            isWeekSatisfy = d.getApplyToWed();
                            break;
                        case 5:
                            isWeekSatisfy = d.getApplyToThu();
                            break;
                        case 6:
                            isWeekSatisfy = d.getApplyToFri();
                            break;
                        case 7:
                            isWeekSatisfy = d.getApplyToSat();
                            break;
                    }
                    if(isWeekSatisfy != 0){
                        if(d.getForCustomerType() != 2){
                            if(d.getPromotionType() == 7 && a == 1){
                                Map<String, Object> param2 = new HashMap<>();
                                param2.put("dietPromotionId", d.getId());
                                DietPromotionSatisfyReduce dsr = dietPromotionSatisfyReduceMapper.findByCondition(param2);
                                if(dsr != null){
                                    Map<String, Object> param3 = new HashMap<>();
                                    param3.put("id", dsr.getGoodsId());
                                    List<Goods> mg = goodsMapper.findListByCondition(param3);
                                    if(mg.size() > 0){
                                        if(mg.get(0) != null){
                                            String man = "0";
                                            String jian = "0";
                                            String m = d2.format(dsr.getSatisfy());
                                            String j = d2.format(dsr.getReduction());
                                            int intM = Integer.parseInt(m.substring(m.indexOf(".") + 1, m.length()));
                                            int intJ = Integer.parseInt(j.substring(j.indexOf(".") + 1, j.length()));
                                            if(intM > 0){
                                                if(Integer.parseInt(m.substring(m.length() - 1, m.length())) > 0){
                                                    man = d2.format(dsr.getSatisfy());
                                                }
                                                else{
                                                    man = d1.format(dsr.getSatisfy());
                                                }
                                            }
                                            else{
                                                man = m.substring(0, m.indexOf("."));
                                            }
                                            if(intJ > 0){
                                                if(Integer.parseInt(j.substring(j.length() - 1, j.length())) > 0){
                                                    jian = d2.format(dsr.getReduction());
                                                }
                                                else{
                                                    jian = d1.format(dsr.getReduction());
                                                }
                                            }
                                            else{
                                                jian = j.substring(0, j.indexOf("."));
                                            }
                                            a = a + 1;
                                            if(mg.get(0).getIsTakeout()){
                                                map.put("7", "(外)" + mg.get(0).getGoodsName() + ":满" + man + "元减" + jian + "元; ");
                                            }
                                            else{
                                                map.put("7", mg.get(0).getGoodsName() + ":满" + man + "元减" + jian + "元; ");
                                            }
                                        }
                                    }
                                }
                            }
                            if(d.getPromotionType() == 11 && b == 1){
                                Map<String, Object> param2 = new HashMap<>();
                                param2.put("dietPromotionId", d.getId());
                                DietPromotionSpecial dsr = dietPromotionSpecialMapper.findByCondition(param2);
                                if(dsr != null){
                                    Map<String, Object> param3 = new HashMap<>();
                                    param3.put("id", dsr.getGoodsId());
                                    List<Goods> mg = goodsMapper.findListByCondition(param3);
                                    if(mg.size() > 0){
                                        if(mg.get(0) != null){
                                            String special = "0";
                                            String m = d2.format(dsr.getPromotionPrice());
                                            int intM = Integer.parseInt(m.substring(m.indexOf(".") + 1, m.length()));
                                            if(intM > 0){
                                                if(Integer.parseInt(m.substring(m.length() - 1, m.length())) > 0){
                                                    special = d2.format(dsr.getPromotionPrice());
                                                }
                                                else{
                                                    special = d1.format(dsr.getPromotionPrice());
                                                }
                                            }
                                            else{
                                                special = m.substring(0, m.indexOf("."));
                                            }
                                            b = b + 1;
                                            //dSatisfyReduceDate += g.goodsName+":促销价为"+special+"元;"
                                            if(mg.get(0).getIsTakeout()){
                                                map.put("11", "(外)" + mg.get(0).getGoodsName() + ":促销价为" + special + "元;");
                                            }
                                            else{
                                                map.put("11", mg.get(0).getGoodsName() + ":促销价为" + special + "元;");
                                            }
                                        }
                                    }
                                }
                            }
                            if(d.getPromotionType() == 4 && c == 1){
                                Map<String, Object> param2 = new HashMap<>();
                                param2.put("dietPromotionId", d.getId());
                                DietPromotionBuyGive dsr = dietPromotionBuyGiveMapper.findByCondition(param2);
                                if(dsr != null){
                                    Map<String, Object> param3 = new HashMap<>();
                                    param3.put("id", dsr.getGoodsId());
                                    List<Goods> mg = goodsMapper.findListByCondition(param3);
                                    param3.put("id", dsr.getGiveGoodsId());
                                    List<Goods> mgTwo = goodsMapper.findListByCondition(param3);
                                    if(mg.size() > 0 && mgTwo.size() > 0){
                                        if(mg.get(0) != null && mgTwo.get(0) != null){
                                            String buyAmount = "0";
                                            String zengAmount = "0";
                                            String m = d2.format(dsr.getBuyNum());
                                            int intM = Integer.parseInt(m.substring(m.indexOf(".") + 1, m.length()));
                                            String j = d2.format(dsr.getGiveNum());
                                            int intJ = Integer.parseInt(j.substring(j.indexOf(".") + 1, j.length()));
                                            if(intM > 0){
                                                if(Integer.parseInt(m.substring(m.length() - 1, m.length())) > 0){
                                                    buyAmount = d2.format(dsr.getBuyNum());
                                                }
                                                else{
                                                    buyAmount = d1.format(dsr.getBuyNum());
                                                }
                                            }
                                            else{
                                                buyAmount = m.substring(0, m.indexOf("."));
                                            }
                                            if(intJ > 0){
                                                if(Integer.parseInt(j.substring(j.length() - 1, j.length())) > 0){
                                                    zengAmount = d2.format(dsr.getGiveNum());
                                                }
                                                else{
                                                    zengAmount = d1.format(dsr.getGiveNum());
                                                }
                                            }
                                            else{
                                                zengAmount = j.substring(0, j.indexOf("."));
                                            }
                                            c = c + 1;
                                            if(mg.get(0).getIsTakeout()){
                                                map.put("4", "(外)" + mg.get(0).getGoodsName() + ":买" + buyAmount + "份" + mg.get(0).getGoodsName() + "赠送" + zengAmount + "份" + mgTwo.get(0).getGoodsName() + ";");
                                            }
                                            else{
                                                map.put("4", mg.get(0).getGoodsName() + ":买" + buyAmount + "份" + mg.get(0).getGoodsName() + "赠送" + zengAmount + "份" + mgTwo.get(0).getGoodsName() + ";");
                                            }
                                        }
                                    }
                                }
                            }
                            if(d.getPromotionType() == 6 && f == 1){
                                Map<String, Object> param2 = new HashMap<>();
                                param2.put("dietPromotionId", d.getId());
                                DietPromotionDiscount dsr = dietPromotionDiscountMapper.findByCondition(param2);
                                if(dsr != null){
                                    Map<String, Object> param3 = new HashMap<>();
                                    param3.put("id", dsr.getGoodsId());
                                    List<Goods> mg = goodsMapper.findListByCondition(param3);
                                    if(mg.size() > 0){
                                        if(mg.get(0) != null){
                                            String quantity = "0";
                                            String discount = "0";
                                            int quantityy = 0;

                                            String m = d2.format(dsr.getQuantity());
                                            int intM = Integer.parseInt(m.substring(m.indexOf(".") + 1, m.length()));
                                            String j = d2.format(dsr.getDiscount());
                                            int intJ = Integer.parseInt(j.substring(j.indexOf(".") + 1, j.length()));
                                            if(intM > 0){
                                                if(Integer.parseInt(m.substring(m.length() - 1, m.length())) > 0){
                                                    quantity = d2.format(dsr.getQuantity());
                                                }
                                                else{
                                                    quantity = d1.format(dsr.getQuantity());
                                                }
                                            }
                                            else{
                                                quantity = m.substring(0, m.indexOf("."));
                                            }
                                            if(intJ > 0){
                                                if(Integer.parseInt(j.substring(j.length() - 1, j.length())) > 0){
                                                    discount = d2.format(dsr.getDiscount());
                                                }
                                                else{
                                                    discount = d1.format(dsr.getDiscount());
                                                }
                                            }
                                            else{
                                                discount = j.substring(0, j.indexOf("."));
                                            }
                                            f = f + 1;
                                            quantityy = Integer.parseInt(quantity) + 1;
                                            if(mg.get(0).getIsTakeout()){
                                                map.put("6", "(外)" + mg.get(0).getGoodsName() + ":第" + quantityy + "份" + mg.get(0).getGoodsName() + "打" + Double.parseDouble(discount) / 10 + "折;");
                                            }
                                            else{
                                                map.put("6", mg.get(0).getGoodsName() + ":第" + quantityy + "份" + mg.get(0).getGoodsName() + "打" + Double.parseDouble(discount) / 10 + "折;");
                                            }
                                        }
                                    }
                                }
                            }
                            if(d.getPromotionType() == 9 && e == 1){
                                Map<String, Object> param2 = new HashMap<>();
                                param2.put("dietPromotionId", d.getId());
                                DietPromotionTotalReduce dsr = dietPromotionTotalReduceMapper.findDietPromotionTotalReduceByDietPromotionId(param2);
                                if(dsr != null){
                                    String reduction = "0";
                                    String discount = "0";
                                    String m = d2.format(dsr.getSatisfy());
                                    if(dsr.getReduceType() == 1){
                                        String k = d2.format(dsr.getReduction());
                                        int intK = Integer.parseInt(k.substring(k.indexOf(".") + 1, k.length()));
                                        if(intK > 0){
                                            if(Integer.parseInt(k.substring(k.length() - 1, k.length())) > 0){
                                                reduction = d2.format(dsr.getReduction());
                                            }
                                            else{
                                                reduction = d1.format(dsr.getReduction());
                                            }
                                        }
                                        else{
                                            reduction = k.substring(0, k.indexOf("."));
                                        }
                                    }
                                    else{
                                        String j = d2.format(dsr.getDiscount());
                                        int intJ = Integer.parseInt(j.substring(j.indexOf(".") + 1, j.length()));
                                        if(intJ > 0){
                                            if(Integer.parseInt(j.substring(j.length() - 1, j.length())) > 0){
                                                discount = d2.format(dsr.getDiscount());
                                            }
                                            else{
                                                discount = d1.format(dsr.getDiscount());
                                            }
                                        }
                                        else{
                                            discount = j.substring(0, j.indexOf("."));
                                        }
                                    }
                                    e = e + 1;
                                    if(dsr.getReduceType() == 1){
                                        map.put("9", "整单优惠:满" + d2.format(dsr.getSatisfy()) + "优惠" + reduction + "元;");
                                    }
                                    else{
                                        map.put("9", "整单优惠:满" + d2.format(dsr.getSatisfy()) + "打" + Double.parseDouble(discount) / 10 + "折;");
                                    }
                                }
                            }
                            if(d.getPromotionType() == 13 && h == 1){
                                Map<String, Object> param2 = new HashMap<>();
                                param2.put("dietPromotionId", d.getId());
                                DietPromotionPayback dietPromotionPayback = dietPromotionPaybackMapper.findDietPromotionPaybackByDietPromotionId(param2);
                                if(dietPromotionPayback != null){
                                    String discount = "0";
                                    String m = d2.format(dietPromotionPayback.getSatisfy());
                                    String j = d2.format(dietPromotionPayback.getDiscount());
                                    int intJ = Integer.parseInt(j.substring(j.indexOf(".") + 1, j.length()));
                                    if(intJ > 0){
                                        if(Integer.parseInt(j.substring(j.length() - 1, j.length())) > 0){
                                            discount = d2.format(dietPromotionPayback.getDiscount());
                                        }
                                        else{
                                            discount = d1.format(dietPromotionPayback.getDiscount());
                                        }
                                    }
                                    else{
                                        discount = j.substring(0, j.indexOf("."));
                                    }
                                    h = h + 1;
                                    String payTypes = "";
                                    if(dietPromotionPayback.getPayType().intValue() == 1){
                                        payTypes = "储值卡支付,";
                                    }
                                    else if(dietPromotionPayback.getPayType().intValue() == 2){
                                        payTypes = "支付宝支付,";
                                    }
                                    else if(dietPromotionPayback.getPayType().intValue() == 3){
                                        payTypes = "微信支付,";
                                    }
                                    else if(dietPromotionPayback.getPayType().intValue() == 4){
                                        payTypes = "现金支付,";
                                    }
                                    map.put("13", "支付促销:" + payTypes + "满" + d2.format(dietPromotionPayback.getSatisfy()) + "打" + Double.parseDouble(discount) / 10 + "折;");
                                }
                            }
                        }
                    }
                }
            }
        }
        return map;
    }

    /**
     * 查询订单的促销明细
     *
     * @param params
     */
    public ApiRest qryOrderPromotion(Map params){
        ApiRest rest = new ApiRest();
        if(params.get("orderId") != null){
            Map<String, Object> param2 = new HashMap<>();
            param2.put("orderId", params.get("orderId"));
            List<DietOrderPromotionR> list = dietOrderPromotionRMapper.select(param2);
            rest.setIsSuccess(true);
            rest.setMessage("查询成功!");
            rest.setData(list);
        }
        else{
            rest.setIsSuccess(false);
            rest.setMessage("orderId不存在!");
        }
        return rest;
    }

    /**
     * pos用验证接口，扫码与存储信息一致(有效期60s)
     *
     * @return
     */
    public ApiRest checkCodeWithPos(Map params){
        ApiRest apiRest = new ApiRest();
        Long nowMills = System.currentTimeMillis();
        if(params.get("vipCode") != null && !params.get("vipCode").equals("")
                && params.get("code") != null && !params.get("code").equals("")
                && params.get("tenantId") != null && !params.get("tenantId").equals("")){
            apiRest.setIsSuccess(false);
            apiRest.setMessage("验证码不存在或已过期!");
            apiRest.setError("验证码不存在或已过期!");
            Map<String, Object> map = new HashMap<>();
            map.put("vipCode", params.get("vipCode"));
            map.put("tenantId", params.get("tenantId"));
            Vip vip = vipMapper.findByCondition(map);
            if(vip != null && Objects.equals(vip.getTenantId(), BigInteger.valueOf(Long.valueOf(params.get("tenantId").toString())))){
                List<WxPosCode> wxPosCodes = vipMapper.findAllByVipIdAndCode(vip.getId(), params.get("code").toString());
                for(WxPosCode wxPosCode : wxPosCodes){
                    //60s内有效
                    Long createAt = wxPosCode.getCreateAt().getTime();
                    if(nowMills - createAt <= 60 * 1000){
                        apiRest.setIsSuccess(true);
                        apiRest.setMessage("验证成功!");
                    }
                }
            }
            else{
                apiRest.setIsSuccess(false);
                apiRest.setMessage("会员与商户不匹配!");
                apiRest.setError("会员与商户不匹配!");
            }

        }
        else{
            apiRest.setIsSuccess(false);
            apiRest.setMessage("tenantId、vipCode或code不存在!!");
            apiRest.setError("tenantId、vipCode或code不存在!!");
        }
        return apiRest;

    }

    /**
     * 获取整单满减活动列表
     *
     * @author szq
     */
    public List<DietPromotionTotalReduce> listTotalReduceList(Map params) throws ParseException{
        List<DietPromotionTotalReduce> totalList = new ArrayList<>();
        Map<String, Object> param = new HashMap<>();
        //查询所有整单活动
        param.put("tenantId", params.get("tenantId"));
        param.put("promotionType", 9);
        List<DietPromotion> dietProList = dietPromotionMapper.dietPromotionList(param);

        for(DietPromotion dp : dietProList){
            //店铺筛选
            Map<String, Object> param1 = new HashMap<>();
            param1.put("dietPromotionId", dp.getId());
            param1.put("branchId", params.get("branchId"));
            List<DietPromotionBranchR> branchR = dietPromotionBranchRMapper.dietPromotionBranchRList(param1);
            //日期筛选
            SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd ");
            SimpleDateFormat format2 = new SimpleDateFormat("HH:mm:ss");
            SimpleDateFormat format3 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String startStr = format1.format(dp.getStartDate()) + format2.format(dp.getStartTime());
            String endStr = format1.format(dp.getEndDate()) + format2.format(dp.getEndTime());
            Date nowDate = new Date();
            //获取星期
            String weeks = "";
            String[] weekss;
            if(dp.getApplyToMon() != null){
                weeks += "1,";
            }
            else{
                weeks += "0,";
            }
            if(dp.getApplyToTue() != null){
                weeks += "1,";
            }
            else{
                weeks += "0,";
            }
            if(dp.getApplyToWed() != null){
                weeks += "1,";
            }
            else{
                weeks += "0,";
            }
            if(dp.getApplyToThu() != null){
                weeks += "1,";
            }
            else{
                weeks += "0,";
            }
            if(dp.getApplyToFri() != null){
                weeks += "1,";
            }
            else{
                weeks += "0,";
            }
            if(dp.getApplyToSat() != null){
                weeks += "1,";
            }
            else{
                weeks += "0,";
            }
            if(dp.getApplyToSun() != null){
                weeks += "1,";
            }
            else{
                weeks += "0,";
            }
            weekss = weeks.substring(0, weeks.length() - 1).split(",");
            Calendar calendar = Calendar.getInstance();
            int weekNum = calendar.get(Calendar.DAY_OF_WEEK) - 1;
            if(calendar.get(Calendar.DAY_OF_WEEK) == 1){
                weekNum = 7;
            }
            //条件判断
            if("1".equals(weekss[weekNum - 1]) && nowDate.after(format3.parse(startStr)) && nowDate.before(format3.parse(endStr)) && (dp.getScope() == 0 || dp.getScope() == 2) &&
                    dp.getPromotionStatus() == 0 && null != branchR && branchR.size() > 0 && (dp.getForCustomerType() == 0 || dp.getForCustomerType() == 1)){
                //满足条件则添加到整单活动列表
                Map<String, Object> param2 = new HashMap<>();
                param2.put("dietPromotionId", dp.getId());
                DietPromotionTotalReduce totalReduce = dietPromotionTotalReduceMapper.findDietPromotionTotalReduceByDietPromotionId(param2);
                if(totalReduce != null){
                    totalReduce.setForCustomerType(dp.getForCustomerType());
                    totalList.add(totalReduce);
                }
            }
        }
        return totalList;
    }

    /**
     * 查询支付促销活动
     *
     * @author szq
     */
    public ApiRest listPayBackTwo(Map params) throws ParseException{
        ApiRest rest = new ApiRest();
        List<PromotionPaybackVo> promotionPaybackVos = new ArrayList<>();
        Map<String, Object> param = new HashMap<>();
        param.put("tenantId", params.get("tenantId"));
        param.put("promotionType", 13);
        List<DietPromotion> dietPromotions = dietPromotionMapper.dietPromotionList(param);
        SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd ");
        SimpleDateFormat format2 = new SimpleDateFormat("HH:mm:ss");
        SimpleDateFormat format3 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        for(DietPromotion dp : dietPromotions){
            Map<String, Object> param1 = new HashMap<>();
            param1.put("dietPromotionId", dp.getId());
            param1.put("branchId", params.get("branchId"));
            List<DietPromotionBranchR> branchR = dietPromotionBranchRMapper.dietPromotionBranchRList(param1);
            //日期筛选
            String startStr = format1.format(dp.getStartDate()) + format2.format(dp.getStartTime());
            String endStr = format1.format(dp.getEndDate()) + format2.format(dp.getEndTime());
            Date nowDate = new Date();
            //获取星期
            String weeks = "";
            String[] weekss;
            if(dp.getApplyToMon() != null){
                weeks += "1,";
            }
            else{
                weeks += "0,";
            }
            if(dp.getApplyToTue() != null){
                weeks += "1,";
            }
            else{
                weeks += "0,";
            }
            if(dp.getApplyToWed() != null){
                weeks += "1,";
            }
            else{
                weeks += "0,";
            }
            if(dp.getApplyToThu() != null){
                weeks += "1,";
            }
            else{
                weeks += "0,";
            }
            if(dp.getApplyToFri() != null){
                weeks += "1,";
            }
            else{
                weeks += "0,";
            }
            if(dp.getApplyToSat() != null){
                weeks += "1,";
            }
            else{
                weeks += "0,";
            }
            if(dp.getApplyToSun() != null){
                weeks += "1,";
            }
            else{
                weeks += "0,";
            }
            weekss = weeks.substring(0, weeks.length() - 1).split(",");
            Calendar calendar = Calendar.getInstance();
            int weekNum = calendar.get(Calendar.DAY_OF_WEEK) - 1;
            if(calendar.get(Calendar.DAY_OF_WEEK) == 1){
                weekNum = 7;
            }
            //条件判断
            if("1".equals(weekss[weekNum - 1]) && nowDate.after(format3.parse(startStr)) && nowDate.before(format3.parse(endStr)) &&
                    (dp.getScope() == 0 || dp.getScope() == 2) && dp.getPromotionStatus() == 0 && null != branchR && branchR.size() > 0){
                //满足条件则添加到整单活动列表
                Map<String, Object> param2 = new HashMap<>();
                param2.put("dietPromotionId", dp.getId());
                DietPromotionPayback dietPromotionPayBack = dietPromotionPaybackMapper.findDietPromotionPaybackByDietPromotionId(param2);
                if(dietPromotionPayBack != null){
                    PromotionPaybackVo promotionPaybackVo = new PromotionPaybackVo();
                    promotionPaybackVo.setId(dp.getId());
                    promotionPaybackVo.setCreateAt(dp.getCreateAt());
                    promotionPaybackVo.setPromotionType(dp.getPromotionType());
                    promotionPaybackVo.setForCustomerType(dp.getForCustomerType());
                    promotionPaybackVo.setScope(dp.getScope());
                    promotionPaybackVo.setForVipType(dp.getForVipType());
                    promotionPaybackVo.setWeek(weeks);
                    promotionPaybackVo.setStartDate(format1.format(dp.getStartDate()));
                    promotionPaybackVo.setEndDate(format1.format(dp.getEndDate()));
                    promotionPaybackVo.setTenantId(dp.getTenantId());
                    promotionPaybackVo.setCreateBy(dp.getCreateBy());
                    promotionPaybackVo.setRemark(dp.getMemo());
                    promotionPaybackVo.setSatisfy(dietPromotionPayBack.getSatisfy());
                    promotionPaybackVo.setDiscount(dietPromotionPayBack.getDiscount());
                    promotionPaybackVo.setPayType(dietPromotionPayBack.getPayType());
                    promotionPaybackVos.add(promotionPaybackVo);
                }
            }
        }
        rest.setData(promotionPaybackVos);
        rest.setIsSuccess(true);
        rest.setMessage("支付促销活动列表查询成功");
        return rest;
    }

    /**
     * 查询商户刮刮乐活动
     *
     * @param params
     */
    public ApiRest qryScratchCard(Map params) throws ParseException{
        ApiRest rest = new ApiRest();
        if(params.get("tenantId") != null && params.get("vipId") != null){
            Map<String, Object> map1 = new HashMap<>();
            map1.put("tenantId", params.get("tenantId"));
            List<DietGameScratchCard> scratchCards = dietGameScratchCardMapper.dietGameScratchCardList(map1);
            DietGameScratchCardVO dietGameScratchCardVO = new DietGameScratchCardVO();

            rest.setIsSuccess(false);
            rest.setMessage("该商户下没有生效的刮刮乐活动!");

            for(DietGameScratchCard dsc : scratchCards){
                Map<String, Object> map2 = new HashMap<>();
                map2.put("id", dsc.getDietPromotionId());
                DietPromotion dietPromotion = dietPromotionMapper.findDietPromotionById(map2);
                SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd ");
                SimpleDateFormat format2 = new SimpleDateFormat("HH:mm:ss");
                SimpleDateFormat format3 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                if(dietPromotion != null){
                    //日期筛选
                    String startStr = format1.format(dietPromotion.getStartDate()) + format2.format(dietPromotion.getStartTime());
                    String endStr = format1.format(dietPromotion.getEndDate()) + format2.format(dietPromotion.getEndTime());
                    Date nowDate = new Date();
                    //获取星期
                    int applyWeek = 0;
                    Calendar calendar = Calendar.getInstance();
                    int weekNum = calendar.get(Calendar.DAY_OF_WEEK) - 1;
                    if(calendar.get(Calendar.DAY_OF_WEEK) == 1){
                        weekNum = 7;
                    }
                    switch(weekNum){
                        case 1:
                            applyWeek = dietPromotion.getApplyToMon();
                            break;
                        case 2:
                            applyWeek = dietPromotion.getApplyToTue();
                            break;
                        case 3:
                            applyWeek = dietPromotion.getApplyToWed();
                            break;
                        case 4:
                            applyWeek = dietPromotion.getApplyToThu();
                            break;
                        case 5:
                            applyWeek = dietPromotion.getApplyToFri();
                            break;
                        case 6:
                            applyWeek = dietPromotion.getApplyToSat();
                            break;
                        case 7:
                            applyWeek = dietPromotion.getApplyToSun();
                            break;
                    }
                    //是否有效
                    if(nowDate.after(format3.parse(startStr)) && nowDate.before(format3.parse(endStr)) && dietPromotion.getPromotionStatus() == 0 && applyWeek == 1){
                        Map<String, Object> map3 = new HashMap<>();
                        map3.put("vipId", params.get("vipId"));
                        map3.put("scratchCardId", dsc.getId());
                        DietGameScratchVip scratchVip = dietGameScratchVipMapper.findByCondition(map3);
                        if(scratchVip == null){
                            scratchVip = new DietGameScratchVip();
                            scratchVip.setTenantId(dsc.getTenantId());
                            scratchVip.setRemainTimes(dsc.getAttendTimes());
                            scratchVip.setVipId(new BigInteger(params.get("vipId").toString()));
                            scratchVip.setScratchCardId(dsc.getId());
                            scratchVip.setCreateAt(new Date());
                            scratchVip.setLastUpdateAt(new Date());
                            scratchVip.setIsDeleted(false);
                            dietGameScratchVipMapper.insert(scratchVip);
                        }
                        else{
                            Calendar today = Calendar.getInstance();
                            today.set(Calendar.HOUR_OF_DAY, 0);
                            today.set(Calendar.MINUTE, 0);
                            today.set(Calendar.SECOND, 0);
                            //如果用户上次参加活动时间是今天以前，则重置次数

                            if(today.getTime().compareTo(scratchVip.getLastUpdateAt()) > 0){
                                scratchVip.setRemainTimes(dsc.getAttendTimes());
                                scratchVip.setLastUpdateAt(new Date());
                                dietGameScratchVipMapper.update(scratchVip);
                            }
                        }
                        dietGameScratchCardVO.setDietPromotionId(dietPromotion.getId());
                        dietGameScratchCardVO.setAttendTimes(scratchVip.getRemainTimes());
                        dietGameScratchCardVO.setCreateAt(dsc.getCreateAt());
                        dietGameScratchCardVO.setGameName(dsc.getGameName());
                        dietGameScratchCardVO.setId(dsc.getId());
                        dietGameScratchCardVO.setRemark(dsc.getRemark());
                        dietGameScratchCardVO.setTenantId(dsc.getTenantId());

                        DietGamePrizeItem defaultItem = new DietGamePrizeItem();
                        //获取所中奖项item
                        Map<String, Object> map4 = new HashMap<>();
                        map4.put("dietPromotionId", dietPromotion.getId());
                        List<DietGamePrizeItem> items = dietGamePrizeItemMapper.dietGamePrizeItemList(map4);
                        defaultItem.setItemName("谢谢参与!");
                        defaultItem.setDietPromotionId(dietPromotion.getId());
                        defaultItem.setTenantId(dietPromotion.getTenantId());
                        defaultItem.setAmount(new BigInteger("1"));
                        //默认奖项
                        double random = Math.random(); // 0~1的随机数
                        if(items.size() == 1){  //有一种奖品
                            if(random < items.get(0).getWinningProbability().doubleValue() / 100){
                                //中奖
                                defaultItem = items.get(0);
                            }
                        }
                        else if(items.size() == 2){ //有两种奖品
                            if(random <= items.get(0).getWinningProbability().doubleValue() / 100){
                                //中奖item0
                                defaultItem = items.get(0);
                            }
                            else if(random <= items.get(0).getWinningProbability().doubleValue() / 100 + items.get(1).getWinningProbability().doubleValue() / 100){
                                //中奖item1
                                defaultItem = items.get(1);
                            }
                        }
                        else if(items.size() == 3){ //有三种奖品
                            if(random <= items.get(0).getWinningProbability().doubleValue() / 100){
                                //中奖item0
                                defaultItem = items.get(0);
                            }
                            else if(random <= items.get(0).getWinningProbability().doubleValue() / 100 + items.get(1).getWinningProbability().doubleValue() / 100){
                                //中奖item1
                                defaultItem = items.get(1);
                            }
                            else if(random <= items.get(0).getWinningProbability().doubleValue() / 100 + items.get(1).getWinningProbability().doubleValue() / 100 + items.get(2).getWinningProbability().doubleValue() / 100){
                                //中奖item2
                                defaultItem = items.get(2);
                            }
                        }
                        dietGameScratchCardVO.setItemName(defaultItem.getItemName());
                        //检测奖品库存
                        if(defaultItem.getAmount().intValue() > 0){
                            dietGameScratchCardVO.setItemId(defaultItem.getId());
                            //prizeDietPromotionType = 8 表示奖品为卡券 （常量）
                            if(defaultItem.getPrizeDietPromotionType() != null && defaultItem.getPrizeDietPromotionType().intValue() == 8){
                                Map<String, Object> map5 = new HashMap<>();
                                map5.put("id", defaultItem.getDietPromotionId());
                                DietPromotionFestival festival = dietPromotionFestivalMapper.findByPromotionId(map5);
                                //判断卡券库存
                                if(festival != null && festival.getRemainInventory().intValue() > 0){
                                    dietGameScratchCardVO.setPrizePromotionType(defaultItem.getPrizeDietPromotionType());
                                    dietGameScratchCardVO.setPrizePromotionId(defaultItem.getPrizeDietPromotionId());
                                }
                            }
                        }
                        else{
                            dietGameScratchCardVO.setItemName("谢谢参与!");
                        }

                        rest.setData(dietGameScratchCardVO);
                        rest.setIsSuccess(true);
                        rest.setMessage("商户刮刮乐活动查询成功!");
                        if(scratchVip.getRemainTimes().intValue() <= 0){
                            rest.setIsSuccess(false);
                            rest.setMessage("剩余抽奖次数为0!");
                        }
                        break;
                    }
                }
            }
        }
        else{
            rest.setIsSuccess(false);
            rest.setMessage("商户id或会员id不存在!");
        }
        return rest;
    }

    /**
     * 保存刮刮乐抽奖结果
     *
     * @param params
     * @return
     */
    public ApiRest saveScratchCardResult(Map params) throws ParseException{
        ApiRest rest = new ApiRest();
        if(params.get("tenantId") != null && params.get("itemName") != null && params.get("vipId") != null && params.get("scratchCardId") != null){
            Map<String, Object> map1 = new HashMap<>();
            map1.put("vipId", params.get("vipId"));
            map1.put("scratchCardId", params.get("scratchCardId"));
            DietGameScratchVip scratchVip = dietGameScratchVipMapper.findByCondition(map1);
            if(scratchVip != null){
                //参与次数
                if(scratchVip.getRemainTimes().intValue() > 0){
                    scratchVip.setRemainTimes(scratchVip.getRemainTimes().add(new BigInteger("-1")));
                    scratchVip.setLastUpdateAt(new Date());
                    dietGameScratchVipMapper.update(scratchVip);
                }
                //记录游戏历史
                DietGameScratchCardHistory history = new DietGameScratchCardHistory();
                history.setTenantId(scratchVip.getTenantId());
                history.setVipId(scratchVip.getVipId());
                history.setScratchCardId(scratchVip.getScratchCardId());
                history.setResultName(params.get("itemName").toString());
                if(params.get("itemId") != null){
                    Map<String, Object> map2 = new HashMap<>();
                    map2.put("id", params.get("itemId"));
                    DietGamePrizeItem item = dietGamePrizeItemMapper.findById(map2);
                    if(item != null){
                        //奖品库存
                        item.setAmount(item.getAmount().add(new BigInteger("-1")));
                        history.setResultItemId(item.getId());
                        //处理卡券库存
                        if(item.getPrizeDietPromotionType().intValue() == 8){
                            map2.put("id", item.getPrizeDietPromotionId());
                            DietPromotionFestival festival = dietPromotionFestivalMapper.findByPromotionId(map2);
                            if(festival != null){
                                //发送卡券
                                sendCard(scratchVip.getVipId(), festival, new BigInteger(params.get("tenantId").toString()));
                            }
                        }
                        item.setLastUpdateAt(new Date());
                        dietGamePrizeItemMapper.update2(item);
                    }
                }
                history.setCreateAt(new Date());
                history.setLastUpdateAt(new Date());
                dietGameScratchCardHistoryMapper.insert(history);
            }
            rest.setIsSuccess(true);
            rest.setMessage("抽奖结果处理成功!");
        }
        else{
            rest.setIsSuccess(false);
            rest.setMessage("tenantId、vipId、或itemName为空!");
        }
        return rest;
    }

    /**
     * 发送卡券
     *
     * @param vipId
     * @param festival
     * @param
     * @return
     */
    public void sendCard(BigInteger vipId, DietPromotionFestival festival, BigInteger tenantId) throws ParseException{
        Map<String, Object> map2 = new HashMap<>();
        map2.put("id", festival.getCardId());
        CardCoupons card = cardCouponsMapper.findCardCouponsById(map2);
        map2.put("id", festival.getDietPromotionId());
        DietPromotion dietPromotion = dietPromotionMapper.findDietPromotionById(map2);
        if(card != null && dietPromotion != null){
            String week = "";
            if(dietPromotion.getApplyToMon() != null){
                week += "1,";
            }
            else{
                week += "0,";
            }
            if(dietPromotion.getApplyToTue() != null){
                week += "1,";
            }
            else{
                week += "0,";
            }
            if(dietPromotion.getApplyToWed() != null){
                week += "1,";
            }
            else{
                week += "0,";
            }
            if(dietPromotion.getApplyToThu() != null){
                week += "1,";
            }
            else{
                week += "0,";
            }
            if(dietPromotion.getApplyToFri() != null){
                week += "1,";
            }
            else{
                week += "0,";
            }
            if(dietPromotion.getApplyToSat() != null){
                week += "1,";
            }
            else{
                week += "0,";
            }
            if(dietPromotion.getApplyToSun() != null){
                week += "1,";
            }
            else{
                week += "0,";
            }
            CardToVip cardToVip = new CardToVip();
            cardToVip.setTenantId(tenantId);
            cardToVip.setFestivalId(festival.getId());
            cardToVip.setVipId(vipId);
            cardToVip.setCardCouponsId(festival.getCardId());
            cardToVip.setPeriodOfValidity(new BigInteger("0"));
            cardToVip.setCardName(card.getCardName());
            cardToVip.setCardType(card.getCardType());
            cardToVip.setUseStatus("0");
            cardToVip.setLimitValue(card.getLimitValue());
            cardToVip.setFaceValue(card.getFaceValue());
            cardToVip.setWeek(week);
            //换算时间
            SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd ");
            SimpleDateFormat format2 = new SimpleDateFormat("HH:mm:ss");
            SimpleDateFormat format3 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String startStr = format1.format(dietPromotion.getStartDate()) + format2.format(dietPromotion.getStartTime());
            String endStr = format1.format(dietPromotion.getEndDate()) + format2.format(dietPromotion.getEndTime());

            cardToVip.setStartTime(format3.parse(startStr));
            cardToVip.setEndTime(format3.parse(endStr));
            cardToVip.setColorValue(card.getColorValue());
            //生成卡券编码
            long mills = System.currentTimeMillis();
            String code = "" + tenantId + festival.getId() + mills;
            cardToVip.setCardCode(code);

            cardToVipMapper.insert(cardToVip);
            //修改库存
            festival.setRemainInventory(festival.getRemainInventory().add(new BigInteger("-1")));
            dietPromotionFestivalMapper.update2(festival);
        }
    }

    /**
     * 会员领取卡券(微餐厅主动领券)
     *
     * @param params
     */
    public ApiRest sendCardToVipForCt(Map params) throws ParseException{
        ApiRest apiRest=new ApiRest();
        if(params.get("tenantId") != null && params.get("cardId") != null && params.get("vipId") != null){
            Map par=new HashMap();
            par.put("id",params.get("cardId"));
            par.put("tenantId",params.get("tenantId"));
            CardCoupons cardCoupons=cardCouponsMapper.findCardCouponsById(par);
            if(null != cardCoupons){
                Branch branch = branchMapper.getMainBranch(params.get("tenantId").toString());
                Map<String, String> map3 = new HashMap<>();
                map3.put("tenantId", params.get("tenantId").toString());
                map3.put("cardId", params.get("cardId").toString());
                map3.put("vipId", params.get("vipId").toString());
                map3.put("channel", "wechat");
                if(branch!=null){
                    map3.put("branchId", branch.getId().toString());
                }
                cardCouponsService.sendCardToVip(map3);
                apiRest.setIsSuccess(true);
                apiRest.setMessage("领取优惠券成功！");
                //apiRest=cardCouponsService.sendVipCardForScoreMall(map);
            }else{
                apiRest.setIsSuccess(false);
                apiRest.setError("未查询到优惠券信息");
            }
        }else{
            apiRest.setIsSuccess(false);
            apiRest.setError("参数错误！");
        }
        return apiRest;
    }

    /**
     * 会员领取卡券
     *
     * @param params
     */
    @Transactional(rollbackFor = Exception.class)
    public ApiRest sendCardToVip(Map params) throws ParseException{
        ApiRest rest = new ApiRest();
        if(params.get("tenantId") != null && params.get("festivalId") != null && params.get("vipId") != null){
            Map<String, Object> map2 = new HashMap<>();
            map2.put("id", params.get("festivalId"));
            DietPromotionFestival festival = dietPromotionFestivalMapper.findByPromotionId(map2);
            map2.put("id", params.get("vipId"));
            Vip vip = vipMapper.findById(map2);
            if(festival != null && vip != null){
                Map<String, Object> map = new HashMap<>();
                map.put("festivalId", festival.getId());
                map.put("vipId", vip.getId());
                //map.put("useStatus", 0);
                //判断是否已领取
                List<CardToVip> check = cardToVipMapper.listCardToVip(map);
                if(check != null && check.size() > 0){
                    rest.setIsSuccess(false);
                    rest.setMessage("已参加过该活动!");
                    rest.setError("已参加过该活动!");
                    return rest;
                }
                //判断库存
                if(festival.getRemainInventory().intValue() <= 0){
                    rest.setIsSuccess(false);
                    rest.setMessage("该卡券已被抢光!");
                    rest.setError("该卡券已被抢光!");
                    return rest;
                }
                map2.put("id", festival.getCardId());
                CardCoupons card = cardCouponsMapper.findCardCouponsById(map2);
                map2.put("id", festival.getDietPromotionId());
                DietPromotion dietPromotion = dietPromotionMapper.findDietPromotionById(map2);
                if(card != null && dietPromotion != null){
                    String week = "";
                    if(dietPromotion.getApplyToMon() != null){
                        week += "1,";
                    }
                    else{
                        week += "0,";
                    }
                    if(dietPromotion.getApplyToTue() != null){
                        week += "1,";
                    }
                    else{
                        week += "0,";
                    }
                    if(dietPromotion.getApplyToWed() != null){
                        week += "1,";
                    }
                    else{
                        week += "0,";
                    }
                    if(dietPromotion.getApplyToThu() != null){
                        week += "1,";
                    }
                    else{
                        week += "0,";
                    }
                    if(dietPromotion.getApplyToFri() != null){
                        week += "1,";
                    }
                    else{
                        week += "0,";
                    }
                    if(dietPromotion.getApplyToSat() != null){
                        week += "1,";
                    }
                    else{
                        week += "0,";
                    }
                    if(dietPromotion.getApplyToSun() != null){
                        week += "1,";
                    }
                    else{
                        week += "0,";
                    }
                    CardToVip cardToVip = new CardToVip();
                    cardToVip.setTenantId(new BigInteger(params.get("tenantId").toString()));
                    cardToVip.setFestivalId(festival.getId());
                    cardToVip.setVipId(vip.getId());
                    cardToVip.setCardCouponsId(festival.getCardId());
                    if(card.getPeriodOfValidity() != null){
                        cardToVip.setPeriodOfValidity(card.getPeriodOfValidity());
                    }
                    else{
                        cardToVip.setPeriodOfValidity(new BigInteger("0"));
                    }
                    cardToVip.setCardName(card.getCardName());
                    cardToVip.setCardType(card.getCardType());
                    cardToVip.setUseStatus("0");
                    if(card.getLimitValue() != null){
                        cardToVip.setLimitValue(card.getLimitValue());
                    }
                    else{
                        cardToVip.setLimitValue(BigDecimal.ZERO);
                    }
                    if(card.getDiscount() != null){
                        cardToVip.setDiscount(card.getDiscount());
                    }
                    cardToVip.setFaceValue(card.getFaceValue());
                    cardToVip.setWeek(week);
                    //换算时间
                    SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd ");
                    SimpleDateFormat format2 = new SimpleDateFormat("HH:mm:ss");
                    SimpleDateFormat format3 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

                    String startStr = format1.format(dietPromotion.getStartDate()) + format2.format(dietPromotion.getStartTime());
                    String endStr = format1.format(dietPromotion.getEndDate()) + format2.format(dietPromotion.getEndTime());
                    if(card.getPeriodOfValidity()!=null&&
                            !card.getPeriodOfValidity().equals(BigInteger.valueOf(-1))&&
                            !card.getPeriodOfValidity().equals(BigInteger.ZERO)){
                        Date today = new Date();
                        Calendar c = Calendar.getInstance();
                        c.setTime(today);
                        c.add(Calendar.DAY_OF_MONTH, card.getPeriodOfValidity().intValue()>1?(card.getPeriodOfValidity().intValue()-1):0);// 今天+45天
                        Date endDate = c.getTime();
                        startStr=format1.format(today)+" 00:00:00";
                        endStr=format1.format(endDate)+" 23:59:59";
                    }
                    cardToVip.setStartTime(format3.parse(startStr));
                    cardToVip.setEndTime(format3.parse(endStr));
                    if(card.getColorValue() != null){
                        cardToVip.setColorValue(card.getColorValue());
                    }
                    else{
                        cardToVip.setColorValue("0");
                    }

                    //生成卡券编码
                    long mills = System.currentTimeMillis();
                    String code = params.get("tenantId").toString() + params.get("festivalId") + mills;
                    cardToVip.setCardCode(code);
                    cardToVip.setCreateAt(new Date());
                    cardToVip.setLastUpdateAt(new Date());
                    cardToVipMapper.insert(cardToVip);
                    //修改库存
                    festival.setRemainInventory(festival.getRemainInventory().add(new BigInteger("-1")));
                    dietPromotionFestivalMapper.update2(festival);
                    rest.setIsSuccess(true);
                    rest.setData(cardToVip);
                    rest.setMessage("卡券领取成功!");
                }else{
                    rest.setIsSuccess(false);
                    rest.setMessage("活动或卡券不存在!");
                }
            }
            else{
                rest.setIsSuccess(false);
                rest.setMessage("卡券活动或会员不存在!");
            }
        }
        else{
            rest.setIsSuccess(false);
            rest.setMessage("商户id、卡券活动id或会员id不存在!");
        }
        return rest;
    }

    public List<DietPromotionTotalReduce> listDietPromotionTotalReduce(BigInteger tenantId, BigInteger branchId, List<Integer> scopes, List<Integer> forCustomerTypes) throws ParseException{
        List<DietPromotionTotalReduce> dietPromotionTotalReduces = new ArrayList<DietPromotionTotalReduce>();
        List<DietPromotion> dietPromotions = dietPromotionMapper.findAllByTenantIdAndPromotionType(tenantId, 9);
        Calendar calendar = Calendar.getInstance();
        int weekNum = calendar.get(Calendar.DAY_OF_WEEK) - 1;
        if(weekNum == 0){
            weekNum = 7;
        }
        for(DietPromotion dietPromotion : dietPromotions){
            DietPromotionBranchR dietPromotionBranchR = dietPromotionBranchRMapper.findByBranchIdAndDietPromotionId(branchId, dietPromotion.getId());
            if(dietPromotionBranchR == null || scopes.indexOf(dietPromotion.getScope()) < 0 || forCustomerTypes.indexOf(dietPromotion.getForCustomerType()) < 0 || dietPromotion.getPromotionStatus() == 1){
                continue;
            }
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date startDate = simpleDateFormat.parse(dateFormat.format(dietPromotion.getStartDate()) + " " + timeFormat.format(dietPromotion.getStartTime()));
            Date endDate = simpleDateFormat.parse(dateFormat.format(dietPromotion.getEndDate()) + " " + timeFormat.format(dietPromotion.getEndTime()));

            boolean[] weeks = new boolean[7];
            if(dietPromotion.getApplyToMon() == 1){
                weeks[0] = true;
            }

            if(dietPromotion.getApplyToTue() == 1){
                weeks[1] = true;
            }

            if(dietPromotion.getApplyToWed() == 1){
                weeks[2] = true;
            }

            if(dietPromotion.getApplyToThu() == 1){
                weeks[3] = true;
            }

            if(dietPromotion.getApplyToFri() == 1){
                weeks[4] = true;
            }

            if(dietPromotion.getApplyToSat() == 1){
                weeks[5] = true;
            }

            if(dietPromotion.getApplyToSun() == 1){
                weeks[6] = true;
            }
            Date date = new Date();
            if(weeks[weekNum - 1] && date.after(startDate) && date.before(endDate)){
                DietPromotionTotalReduce dietPromotionTotalReduce = dietPromotionTotalReduceMapper.findByDietPromotionId(dietPromotion.getId());
                if(dietPromotionTotalReduce != null){
                    dietPromotionTotalReduce.setForCustomerType(dietPromotion.getForCustomerType());
                    dietPromotionTotalReduces.add(dietPromotionTotalReduce);
                }
            }
        }
        return dietPromotionTotalReduces;
    }
}
