package erp.chain.service.o2o;

import com.saas.common.ResultJSON;
import com.saas.common.exception.ServiceException;
import com.saas.common.util.LogUtil;
import erp.chain.domain.*;
import erp.chain.domain.o2o.*;
import erp.chain.domain.o2o.vo.DietPromotionFestivalVo;
import erp.chain.mapper.*;
import erp.chain.mapper.o2o.CardCouponsMapper;
import erp.chain.mapper.o2o.CardToVipMapper;
import erp.chain.mapper.o2o.DietOrderInfoMapper;
import erp.chain.mapper.o2o.VipMapper;
import erp.chain.service.BranchService;
import erp.chain.utils.CodeUtiles;
import erp.chain.utils.GsonUtils;
import erp.chain.utils.SerialNumberGenerate;
import net.sf.json.JSONArray;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import saas.api.common.ApiRest;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by Administrator on 2017/1/17.
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class CardCouponsService{
    @Autowired
    private CardCouponsMapper cardCouponsMapper;
    @Autowired
    private DietPromotionBranchRMapper dietPromotionBranchRMapper;
    @Autowired
    private DietPromotionFestivalMapper dietPromotionFestivalMapper;
    @Autowired
    private DietPromotionMapper dietPromotionMapper;
    @Autowired
    private CardToVipMapper cardToVipMapper;
    @Autowired
    private DietOrderInfoMapper dietOrderInfoMapper;
    @Autowired
    private BranchMapper branchMapper;
    @Autowired
    private VipMapper vipMapper;
    @Autowired
    private DietPromotionService dietPromotionService;
    @Autowired
    private BranchService branchService;
    @Autowired
    private SerialNumberCreatorMapper serialNumberCreatorMapper;

    /**
     * 查询卡券列表
     */
    public ResultJSON qryCardList(Map params) throws ServiceException{
        ResultJSON result = new ResultJSON();
        Map<String, Object> map = new HashMap<>();
        List<Map> totalList = cardCouponsMapper.getCardCouponsList(params);
        if(params.get("page") != null && params.get("rows") != null){
            params.put("offset", ((Integer.parseInt(params.get("page").toString()) - 1) * Integer.parseInt(params.get("rows").toString())));
        }
        if(params.get("isCheckUseDate")!=null){
            SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            params.put("useDate",sdf.format(new Date()));
        }
        List<Map> list = cardCouponsMapper.getCardCouponsList(params);
        /*for(CardCoupons cardCoupons : list){
            if(cardCoupons.getFestivalId() != null){
                Map<String, Object> map1 = new HashMap<>();
                map1.put("id", cardCoupons.getFestivalId().toString());
                DietPromotionFestival festival = dietPromotionFestivalMapper.findByPromotionId(map1);
                if(festival != null){
                    map1.put("id", festival.getDietPromotionId().toString());
                    map1.put("tenantId", festival.getTenantId().toString());
                    ResultJSON resultJSON = dietPromotionService.findActivityByPromotionId(map1);
                    if(resultJSON.getJsonMap() != null){
                        cardCoupons.setActivityVo((ActivityVo)resultJSON.getJsonMap().get("activityVo"));
                    }
                }
            }
        }*/
        int count = totalList.size();
        map.put("total", count > 0 ? count : 0);
        map.put("rows", list);
        result.setJsonMap(map);
        return result;
    }

    /**
     * 查询卡券发放记录列表
     */
    public ResultJSON qryCardReport(Map params) throws ServiceException{
        ResultJSON result = new ResultJSON();
        Map<String, Object> map = new HashMap<>();
        List<CardCouponsReport> totalList = cardCouponsMapper.cardCouponsReportList(params);
        if(params.get("page") != null && params.get("rows") != null){
            params.put("offset", ((Integer.parseInt(params.get("page").toString()) - 1) * Integer.parseInt(params.get("rows").toString())));
        }
        List<CardCouponsReport> list = cardCouponsMapper.cardCouponsReportList(params);
        int count = totalList.size();
        map.put("total", count > 0 ? count : 0);
        map.put("rows", list);
        result.setJsonMap(map);
        return result;
    }


    /**
     * 查询会员卡券列表
     */
    public ResultJSON vipCardList(Map params) throws ServiceException{
        ResultJSON result = new ResultJSON();
        Map<String, Object> map = new HashMap<>();
        List<CardToVip> totalList = cardToVipMapper.listCardToVip(params);
        if(params.get("page") != null && params.get("rows") != null){
            params.put("offset", ((Integer.parseInt(params.get("page").toString()) - 1) * Integer.parseInt(params.get("rows").toString())));
        }
        List<CardToVip> list = cardToVipMapper.listCardToVip(params);
        int count = totalList.size();
        map.put("total", count > 0 ? count : 0);
        map.put("rows", list);
        result.setJsonMap(map);
        return result;
    }

    /**
     * 根据卡券id查询会员卡券
     */
    public ApiRest getVipCardByCode(Map params) throws ServiceException{
        ApiRest apiRest = new ApiRest();
        CardToVip cardToVip = cardToVipMapper.getVipCardByCode(params);
        if(cardToVip != null){
            apiRest.setIsSuccess(true);
            apiRest.setMessage("查询成功！");
            apiRest.setData(cardToVip);
        }
        else{
            apiRest.setIsSuccess(false);
            apiRest.setError("查询会员卡券失败！");
            apiRest.setMessage("查询会员卡券失败！");
        }
        return apiRest;
    }


    /**
     * 保存卡券
     */
    public ResultJSON saveCardCoupons(Map params) throws ServiceException, ParseException{
        ResultJSON resultJSON = new ResultJSON();
        if(params.get("startTime") != null){
            params.put("startTime", params.get("startTime").toString().length()==19?params.get("startTime"):(params.get("startTime")+":00"));
        }
        if(params.get("endTime") != null){
            params.put("endTime", params.get("endTime").toString().length()==19?params.get("endTime"):(params.get("endTime")+":59"));
        }
        if(params.get("id") != null && !"".equals(params.get("id"))){
            Map<String, Object> map = new HashMap<>();
            map.put("cardId", params.get("id"));
            List<DietPromotionFestival> festivals = dietPromotionFestivalMapper.dietPromotionFestivalList(map);
            for(DietPromotionFestival f : festivals){
                Map<String, Object> map1 = new HashMap<>();
                map1.put("id", f.getDietPromotionId());
                map1.put("tenantId", f.getTenantId());
                DietPromotion dp = dietPromotionMapper.findDietPromotionById(map1);
                SimpleDateFormat formatA = new SimpleDateFormat("yyyy-MM-dd");
                SimpleDateFormat formatB = new SimpleDateFormat("HH:mm:ss");
                SimpleDateFormat formatC = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                if(dp.getStartDate() != null && dp.getStartTime() != null && dp.getEndDate() != null && dp.getEndTime() != null){
                    String startStr = formatA.format(dp.getStartDate()) + " " + formatB.format(dp.getStartTime());
                    String endStr = formatA.format(dp.getEndDate()) + " " + formatB.format(dp.getEndTime());
                    Date nowDate = new Date();
                    Date startDate = formatC.parse(startStr);
                    Date endDate = formatC.parse(endStr);
                    BigInteger cardId=f.getCardId();
                    long count=cardCouponsMapper.countCouponUsed(cardId,f.getTenantId());
                    if(count>0){
                        resultJSON.setSuccess("1");
                        resultJSON.setMsg("优惠券已被领取，仍有用户未使用，修改失败！");
                        return resultJSON;
                    }
                    /*if(startDate.before(nowDate) && endDate.after(nowDate) && dp.getPromotionStatus() == 0){
                        resultJSON.setSuccess("1");
                        resultJSON.setMsg("使用中的优惠券不允许更改");
                        return resultJSON;
                    }
                    else if(startDate.after(nowDate) && dp.getPromotionStatus() == 0){
                        resultJSON.setSuccess("1");
                        resultJSON.setMsg("使用中的优惠券不允许更改");
                        return resultJSON;
                    }*/
                }
            }
            int flag = cardCouponsMapper.updateCardCoupons(params);
            if(flag == 1){
                resultJSON.setSuccess("0");
                resultJSON.setMsg("优惠券更新成功");
                Map<String, Object> map1 = new HashMap<>();
                map1.put("id", params.get("id"));
                CardCoupons cardCoupons = cardCouponsMapper.findCardCouponsById(map1);
                if("1".equals(params.get("openUseRule"))){
                    if(params.get("limitGoodsIds")!=null&&!"".equals(params.get("limitGoodsIds"))){
                        List<String> goodsIds=Arrays.asList(params.get("limitGoodsIds").toString().split(","));
                        //删除原有关联
                        int i=cardCouponsMapper.deleteCouponsGoods(cardCoupons.getTenantId(),cardCoupons.getId());
                        //插入新关系
                        int j=cardCouponsMapper.insertCouponsGoods(cardCoupons.getId(),cardCoupons.getTenantId(),null,goodsIds);
                    }
                    if(params.get("limitBranchIds")!=null&&!"".equals(params.get("limitBranchIds"))){
                        List<String> branchIds=Arrays.asList(params.get("limitBranchIds").toString().split(","));
                        //删除原有关联
                        int i=cardCouponsMapper.deleteCouponsBranch(cardCoupons.getTenantId(),cardCoupons.getId());
                        //插入新关系
                        int j=cardCouponsMapper.insertCouponsBranch(cardCoupons.getId(),cardCoupons.getTenantId(),branchIds);
                    }
                }
                if("1".equals(params.get("openSendRule"))){
                    if(cardCoupons != null && cardCoupons.getFestivalId() == null){
                        //如果是新开启发券规则，则创建
                        params.put("activityName", cardCoupons.getCardName());
                        params.put("cardId", params.get("id").toString());
                        params.put("amount", "9999");
                        if(params.get("sendLimitValue") != null){
                            params.put("forCustomerCondition", params.get("sendLimitValue"));
                        }
                        ResultJSON resultJSON1 = dietPromotionService.addCardCouponsActivity(params);
                        if("0".equals(resultJSON1.getSuccess())){
                            if(resultJSON1.getObject() != null){
                                Map map2 = (Map)resultJSON1.getObject();
                                Map<String, Object> param1 = new HashMap<>();
                                param1.put("id", params.get("id"));
                                param1.put("festivalId", map2.get("id"));
                                cardCouponsMapper.updateCardCoupons(param1);
                            }
                        }
                    }
                }
            }
            else{
                resultJSON.setSuccess("1");
                resultJSON.setMsg("优惠券更新失败");
            }
        }
        else{
            params.put("colorValue", 0);
            params.put("createAt", new Date());
            params.put("lastUpdateAt", new Date());
            CardCoupons cardCoupons=GsonUtils.fromJson(GsonUtils.toJson(params), CardCoupons.class);
            int flag = cardCouponsMapper.addCardCoupons(cardCoupons);
            if(flag == 1){
                if("1".equals(params.get("openUseRule"))){
                    if(params.get("limitGoodsIds")!=null&&!"".equals(params.get("limitGoodsIds"))){
                        List<String> goodsIds=Arrays.asList(params.get("limitGoodsIds").toString().split(","));
                        //删除原有关联
                        int i=cardCouponsMapper.deleteCouponsGoods(cardCoupons.getTenantId(),cardCoupons.getId());
                        //插入新关系
                        int j=cardCouponsMapper.insertCouponsGoods(cardCoupons.getId(),cardCoupons.getTenantId(),null,goodsIds);
                    }
                    if(params.get("limitBranchIds")!=null&&!"".equals(params.get("limitBranchIds"))){
                        List<String> branchIds=Arrays.asList(params.get("limitBranchIds").toString().split(","));
                        //删除原有关联
                        int i=cardCouponsMapper.deleteCouponsBranch(cardCoupons.getTenantId(),cardCoupons.getId());
                        //插入新关系
                        int j=cardCouponsMapper.insertCouponsBranch(cardCoupons.getId(),cardCoupons.getTenantId(),branchIds);
                    }
                }
                //创建发券活动
                if("1".equals(params.get("openSendRule"))){
                    params.put("activityName", params.get("cardName"));
                    params.put("cardId", cardCoupons.getId().toString());
                    params.put("amount", "9999");
                    if(params.get("sendLimitValue") != null){
                        params.put("forCustomerCondition", params.get("sendLimitValue"));
                    }
                    ResultJSON resultJSON1 = dietPromotionService.addCardCouponsActivity(params);
                    if("0".equals(resultJSON1.getSuccess())){
                        if(resultJSON1.getObject() != null){
                            Map map = (Map)resultJSON1.getObject();
                            Map<String, Object> param1 = new HashMap<>();
                            param1.put("id", cardCoupons.getId());
                            param1.put("festivalId", map.get("id"));
                            cardCouponsMapper.updateCardCoupons(param1);
                        }
                    }
                }
                resultJSON.setSuccess("0");
                resultJSON.setMsg("优惠券保存成功");
            }
            else{
                resultJSON.setSuccess("1");
                resultJSON.setMsg("优惠券保存失败");
            }
        }
        return resultJSON;
    }

    /**
     * 删除卡券
     */
    public ResultJSON delCardCoupons(Map params) throws ServiceException, ParseException{
        ResultJSON resultJSON = new ResultJSON();
        CardCoupons cardCoupons = cardCouponsMapper.findCardCouponsById(params);
        if(cardCoupons==null){
            resultJSON.setSuccess("1");
            resultJSON.setMsg("查询优惠券失败！");
            return resultJSON;
        }
       /* Map<String, Object> map = new HashMap<>();
        map.put("cardId", params.get("id"));
        List<DietPromotionFestival> festivals = dietPromotionFestivalMapper.dietPromotionFestivalList(map);
        for(DietPromotionFestival f : festivals){
            Map<String, Object> map1 = new HashMap<>();
            map1.put("id", f.getDietPromotionId());
            map1.put("tenantId", f.getTenantId());
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
                BigInteger cardId=f.getCardId();
                long count=cardCouponsMapper.countCouponUsed(cardId,f.getTenantId());
                if(count>0){
                    resultJSON.setSuccess("1");
                    resultJSON.setMsg("优惠券已被领取，仍有用户未使用，删除失败！");
                    return resultJSON;
                }
                *//*if(startDate.before(nowDate) && endDate.after(nowDate) && dp.getPromotionStatus() == 0){
                    resultJSON.setSuccess("1");
                    resultJSON.setMsg("使用中的优惠券不允许删除");
                    return resultJSON;
                }
                else if(startDate.after(nowDate) && dp.getPromotionStatus() == 0){
                    resultJSON.setSuccess("1");
                    resultJSON.setMsg("使用中的优惠券不允许删除");
                    return resultJSON;
                }*//*
            }
        }*/
        cardCoupons.setIsDeleted(true);
        cardCoupons.setLastUpdateAt(new Date());
        int flag = cardCouponsMapper.updateCardCoupons(cardCoupons);
        if(flag == 1){
            int i=cardCouponsMapper.deleteCouponsGoods(cardCoupons.getTenantId(),cardCoupons.getId());
            int j=cardCouponsMapper.deleteCouponsBranch(cardCoupons.getTenantId(),cardCoupons.getId());
            resultJSON.setSuccess("0");
            resultJSON.setMsg("优惠券删除成功");
        }
        else{
            resultJSON.setSuccess("1");
            resultJSON.setMsg("优惠券删除失败");
        }
        return resultJSON;
    }

    /**
     * 查询会员可用卡券列表
     *
     * @param params
     * @return
     */
    public ApiRest listCardsOfVip(Map params) throws ParseException{
        ApiRest rest = new ApiRest();
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat sdf2=new SimpleDateFormat("yyyy-MM-dd 00:00:00");
        if(params.get("vipId") != null && params.get("cardType") != null){
            List<CardToVip> returnCards = new ArrayList<>();
            Map<String, Object> param = new HashMap<>();
            String queryType="list";
            if(params.get("queryType")!=null&&!params.get("queryType").toString().equals("")){
                queryType=params.get("queryType").toString();
            }
            param.put("vipId", params.get("vipId"));
            if(!"0".equals(params.get("cardType"))){
                param.put("cardType", params.get("cardType"));
            }
            if(params.get("cardId") != null){
                param.put("cardId", params.get("cardId"));
            }
            Date useDate=new Date();
            //param.put("useStatus", "0");
            if(queryType.equals("pay")){
                param.put("useStatus", "0");
                param.put("useDate",sdf.format(useDate));
            }
            List<CardToVip> cards = cardToVipMapper.listCardToVip(param);
            Calendar c = Calendar.getInstance();
            int weekNum = c.get(Calendar.DAY_OF_WEEK) - 1;
            if(c.get(Calendar.DAY_OF_WEEK) == 1){
                weekNum = 7;
            }
            for(CardToVip card : cards){
                Map par=new HashMap();
                par.put("id",card.getCardCouponsId());
                par.put("tenantId",card.getTenantId());
                CardCoupons cardCoupons = cardCouponsMapper.findCardCouponsByIdIncludeDelete(par);
                if(cardCoupons == null){
                    continue;
                }
                if(params.get("limitValue") != null){
                    BigDecimal orderAmount=BigDecimal.valueOf(Double.valueOf(params.get("limitValue").toString()));
                    if(cardCoupons.getLimitValue()!=null&&cardCoupons.getLimitValue().compareTo(orderAmount)>0){
                        continue;
                    }
                }

                card.setIsRelease(1);
                if(card.getReleaseDate()!=null&&card.getReleaseDate().after(sdf2.parse(sdf2.format(new Date())))){
                    card.setIsRelease(0);
                }
                String week=(cardCoupons.getWeekday1()+","+cardCoupons.getWeekday2()+","+cardCoupons.getWeekday3()+","+cardCoupons.getWeekday4()+","+cardCoupons.getWeekday5()+","+cardCoupons.getWeekday6()+","+cardCoupons.getWeekday7()+",");
                card.setWeek(week);
                String[] weeks = card.getWeek().split(",");
                if(card.getStartTime()==null&&card.getEndTime()==null){
                    card.setIsOverLimit(0);
                }else if(card.getStartTime().before(new Date())&&card.getEndTime().after(new Date())){
                    card.setIsOverLimit(0);
                }else{
                    card.setIsOverLimit(1);
                }
                String branchNames = cardCoupons.getLimitBranchNames();
                card.setGiftGoodsName(branchNames);
                card.setLimitBranchNames(branchNames);
                card.setUseEndTime(cardCoupons.getUseEndTime());
                card.setUseStartTime(cardCoupons.getUseStartTime());
                card.setRemark(cardCoupons.getRemark());
                card.setLimitValue(cardCoupons.getLimitValue());

                if(queryType.equals("pay")){
                    if(card.getIsRelease()==0){
                        continue;//未生效的不可下单使用
                    }
                    //判断适用门店
                    String bId = null;
                    if(params.get("branchId") != null){
                        bId = params.get("branchId").toString();
                    }
                    if(bId != null && bId.length() != 0){
                        boolean isUseBranch = false;
                        List<String> branchIds = cardCouponsMapper.getCouponsBranch(cardCoupons.getId(), cardCoupons.getTenantId());
                        if(branchIds.size() > 0){
                            for(String b : branchIds){
                                if(b.equals(bId)){
                                    isUseBranch = true;
                                }
                            }
                            if(!isUseBranch){
                                LogUtil.logInfo("优惠券无法在本机构核销！");
                                continue;
                            }
                        }
                    }
                    else{
                        LogUtil.logInfo("核销门店未知！");
                        continue;
                    }
                    //比较日期和星期
                    if("1".equals(weeks[weekNum - 1])){
                        //card.setOpenSendRule(cardCoupons.isOpenSendRule());
                        //card.setOpenUseRule(cardCoupons.isOpenUseRule());
                        SimpleDateFormat sdft=new SimpleDateFormat("HH:mm:ss");
                        Date useTime=sdft.parse(sdft.format(new Date()));
                        if((cardCoupons.getUseStartTime()==null&&cardCoupons.getUseEndTime()==null)||
                                (useTime.after(cardCoupons.getUseStartTime())
                                &&useTime.before(cardCoupons.getUseEndTime()))){
                            //筛选机构
                            returnCards.add(card);
                        }else{
                            LogUtil.logInfo("查询会员可用券，未在使用范围内（时段）");
                        }
                    }else{
                        LogUtil.logInfo("查询会员可用券，未在使用范围内（周）");
                    }
                }else{
                    returnCards.add(card);
                }

            }
            //Collections.sort(returnCards);
            rest.setIsSuccess(true);
            rest.setMessage("查询成功!");
            rest.setData(returnCards);
        }
        else{
            rest.setIsSuccess(false);
            rest.setMessage("会员id不存在或缺少卡券类型!");
        }
        return rest;
    }


    /**
     * 查询卡券活动列表（微餐厅用）
     *
     * @param params
     */
    public ApiRest listFestivals(Map params) throws ParseException{
        ApiRest rest = new ApiRest();
        SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat format2 = new SimpleDateFormat("HH:mm:ss");
        SimpleDateFormat format3 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        List<DietPromotionFestivalVo> fitOnes = new ArrayList<>();
        if(params.get("tenantId") != null && params.get("vipId") != null){
            String tenantId=params.get("tenantId").toString();
            String vipId=params.get("vipId").toString();
            Date nowDate = new Date();
            BigDecimal receivedAmount=BigDecimal.ZERO;
            if(params.get("payAt") != null && !params.get("payAt").toString().equals("")){
                nowDate=format3.parse(params.get("payAt").toString());
            }
            if(params.get("receivedAmount") != null && !params.get("receivedAmount").toString().equals("")){
                receivedAmount=BigDecimal.valueOf(Double.valueOf(params.get("receivedAmount").toString()));
            }
            Map<String, Object> map = new HashMap<>();
            map.put("tenantId", tenantId);
            map.put("status","0");
            map.put("manualGet","1");
            //map.put("openSendRule","0");
            map.put("getDate",format3.format(nowDate));
            if(receivedAmount!=BigDecimal.ZERO){
                map.put("receivedAmount",receivedAmount);
            }
            List<CardCoupons> cardCouponsList=cardCouponsMapper.cardCouponsList(map);
            for(CardCoupons cardCoupons:cardCouponsList){
                DietPromotionFestivalVo dietPromotionFestivalVo = new DietPromotionFestivalVo();
                Map<String, Object> map3 = new HashMap<>();
                map3.put("tenantId", tenantId);
                map3.put("vipId", vipId);
                map3.put("cardId", cardCoupons.getId());
                map3.put("useStatus", 0);
                List gotCards = cardToVipMapper.listCardToVip(map3);
                if(gotCards!=null&&gotCards.size()>0){//已领取未使用，领用页面显示已领用
                    dietPromotionFestivalVo.setRemainCardCount(gotCards.size());
                }else{//没有未用的，需要判断是否达到领取上限，达到了，页面不显示，未达到显示
                    Map<String, Object> map1 = new HashMap<>();
                    map1.put("cardId", cardCoupons.getId());
                    map1.put("vipId", vipId);
                    //单日领取限制
                    if(cardCoupons.getDayGetLimit()!=null){
                        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
                        map1.put("sendDate",sdf.format(new Date()));
                        //单日领取数
                        List<CardToVip> dayCheck = cardToVipMapper.listCardToVip(map1);
                        if(dayCheck!=null&&BigDecimal.valueOf(Double.valueOf(dayCheck.size())).compareTo(cardCoupons.getDayGetLimit())>=0){
                            LogUtil.logInfo("listFestivals--发券时，会员已有券数量超过此券的单日领取数，vipId="+vipId+"，cardId="+cardCoupons.getId());
                            continue;
                        }
                    }
                    //累计领取数
                    if(cardCoupons.getTotalGetLimit()!=null){
                        map1.remove("sendDate");
                        List<CardToVip> totalCheck = cardToVipMapper.listCardToVip(map1);
                        if(totalCheck!=null&&BigDecimal.valueOf(Double.valueOf(totalCheck.size())).compareTo(cardCoupons.getTotalGetLimit())>=0){
                            LogUtil.logInfo("listFestivals--发券时，会员已有券数量超过此券的累计领取数，vipId="+vipId+"，cardId="+cardCoupons.getId());
                            continue;
                        }
                    }
                }
                dietPromotionFestivalVo.setBranchNames(cardCoupons.getLimitBranchNames());
                //dietPromotionFestivalVo.setDietPromotionFestivalId(festival.getId());
                //dietPromotionFestivalVo.setDietPromotionId(festival.getDietPromotionId());
                dietPromotionFestivalVo.setTenanatId(cardCoupons.getTenantId());
                dietPromotionFestivalVo.setActivityName(cardCoupons.getCardName());
                dietPromotionFestivalVo.setCardId(cardCoupons.getId());
                //dietPromotionFestivalVo.setLimitPerOne(festival.getLimitPerOne());
                //dietPromotionFestivalVo.setSendLimitValue(festival.getSendLimitValue());
                if(cardCoupons.getSendAllAmount()==null){
                    dietPromotionFestivalVo.setTotalInventory(BigInteger.valueOf(-1));
                    dietPromotionFestivalVo.setRemainInventory(BigInteger.valueOf(-1));
                }else{
                    dietPromotionFestivalVo.setTotalInventory(cardCoupons.getSendAllAmount().toBigInteger());
                    BigDecimal remain=cardCoupons.getSendAllAmount().subtract(cardCoupons.getSendedAmount()==null?BigDecimal.ZERO:cardCoupons.getSendedAmount());
                    dietPromotionFestivalVo.setRemainInventory(remain.toBigInteger());
                }

                dietPromotionFestivalVo.setCreateBranchId(cardCoupons.getBranchId());
                //dietPromotionFestivalVo.setPromotionCode(dietPromotion.getPromotionCode());
                //dietPromotionFestivalVo.setPromotionName(dietPromotion.getPromotionName());
                //dietPromotionFestivalVo.setPromotionType(dietPromotion.getPromotionType());
                if(cardCoupons.getStartTime() != null && cardCoupons.getEndTime() != null){
                    Date startDate = format1.parse(format1.format(cardCoupons.getStartTime()));
                    Date endDate = format1.parse(format1.format(cardCoupons.getEndTime()));
                    Date startTime = format2.parse(format2.format(cardCoupons.getStartTime()));
                    Date endTime = format2.parse(format2.format(cardCoupons.getEndTime()));
                    dietPromotionFestivalVo.setStartDate(startDate);
                    dietPromotionFestivalVo.setEndDate(endDate);
                    dietPromotionFestivalVo.setStartTime(startTime);
                    dietPromotionFestivalVo.setEndTime(endTime);
                }
                else{
                    dietPromotionFestivalVo.setStartDate(null);
                    dietPromotionFestivalVo.setEndDate(null);
                    dietPromotionFestivalVo.setStartTime(null);
                    dietPromotionFestivalVo.setEndTime(null);
                }
                dietPromotionFestivalVo.setApplyToMon(cardCoupons.getWeekday1());
                dietPromotionFestivalVo.setApplyToTue(cardCoupons.getWeekday2());
                dietPromotionFestivalVo.setApplyToWed(cardCoupons.getWeekday3());
                dietPromotionFestivalVo.setApplyToThu(cardCoupons.getWeekday4());
                dietPromotionFestivalVo.setApplyToFri(cardCoupons.getWeekday5());
                dietPromotionFestivalVo.setApplyToSat(cardCoupons.getWeekday6());
                dietPromotionFestivalVo.setApplyToSun(cardCoupons.getWeekday7());
                dietPromotionFestivalVo.setScope(1);
                dietPromotionFestivalVo.setForCustomerType(1);
                //dietPromotionFestivalVo.setMemGradeId(dietPromotion.getMemGradeId());
                //dietPromotionFestivalVo.setMemo(dietPromotion.getMemo());
                //dietPromotionFestivalVo.setPromotionStatus(dietPromotion.getPromotionStatus());
                //dietPromotionFestivalVo.setIsUse(dietPromotion.getIsUse());
                //dietPromotionFestivalVo.setIsSuperposition(dietPromotion.getIsSuperposition());

                dietPromotionFestivalVo.setCardName(cardCoupons.getCardName());
                dietPromotionFestivalVo.setCardType(cardCoupons.getCardType());
                dietPromotionFestivalVo.setLimitValue(cardCoupons.getLimitValue());
                dietPromotionFestivalVo.setFaceValue(cardCoupons.getFaceValue());
                dietPromotionFestivalVo.setPeriodOfValidity(cardCoupons.getPeriodOfValidity());
                dietPromotionFestivalVo.setColorValue(cardCoupons.getColorValue());
                dietPromotionFestivalVo.setRemark(cardCoupons.getRemark());
                dietPromotionFestivalVo.setOpenSendRule(cardCoupons.isOpenSendRule());
                dietPromotionFestivalVo.setOpenUseRule(cardCoupons.isOpenUseRule());
                dietPromotionFestivalVo.setDiscount(cardCoupons.getDiscount());
                fitOnes.add(dietPromotionFestivalVo);
            }
            LogUtil.logInfo("查询卡券fitOnes: " + fitOnes);
            rest.setIsSuccess(true);
            rest.setMessage("查询成功！");
            rest.setData(fitOnes);
        }
        else{
            rest.setIsSuccess(false);
            rest.setMessage("商户id、或会员id不存在！");
        }
        return rest;
    }

    public ApiRest listBranchsByFestivalId(Map params){
        ApiRest rest = new ApiRest();
        if(params.get("festivalId") != null){
            Map<String, Object> param1 = new HashMap<>();
            param1.put("id", params.get("festivalId"));
            DietPromotionFestival festival = dietPromotionFestivalMapper.findByPromotionId(param1);
            Map<String, Object> param2 = new HashMap<>();
            param2.put("dietPromotionId", festival.getDietPromotionId());
            param2.put("branchId", params.get("branchId"));
            List<DietPromotionBranchR> branchR = dietPromotionBranchRMapper.dietPromotionBranchRList(param2);
            String branchNames = "";
            for(DietPromotionBranchR b : branchR){
                Map<String, Object> map6 = new HashMap<>();
                map6.put("branchId", b.getBranchId());
                map6.put("tenantId", festival.getTenantId());
                Branch branch = branchMapper.findByTenantIdAndBranchId(map6);
                if(branch != null){
                    branchNames += branch.getName() + ",";
                }
            }
            if(branchNames.length() > 0){
                branchNames = branchNames.substring(0, branchNames.length() - 1);
            }
            else{
                branchNames = "总部";
            }
            rest.setIsSuccess(true);
            rest.setMessage("查询成功！");
            rest.setData(branchNames);
        }
        else{
            rest.setIsSuccess(false);
            rest.setMessage("festivalId不存在！");
        }
        return rest;
    }

    /**
     * 微餐厅卡券核销接口
     *
     * @param params
     * @return
     */
    public ApiRest useCardForCt(Map params) throws ParseException{
        ApiRest rest = new ApiRest();
        List<CardToVip> cardToVips = null;
        Map<String, Object> map = new HashMap<>();
        map.put("tenantId", params.get("tenantId"));
        if(params.get("cardToVipId") != null && !params.get("cardToVipId").toString().equals("")){
            map.put("id", params.get("cardToVipId"));
            cardToVips = cardToVipMapper.listCardToVip(map);
        }
        else if(params.get("cardCode") != null && !params.get("cardCode").toString().equals("")){
            map.put("cardCode", params.get("cardCode"));
            cardToVips = cardToVipMapper.listCardToVip(map);
        }
        else{
            rest.setIsSuccess(false);
            rest.setMessage("未得到有效券码或账户!");
            return rest;
        }
        if(cardToVips != null && cardToVips.size() > 0){
            CardToVip cardToVip = cardToVips.get(0);
            CardCouponsBook cardCouponsBook=new CardCouponsBook();
            List<CardCouponsBook> cardCouponsBooks=new ArrayList<>();
            if("1".equals(cardToVip.getUseStatus()) || cardToVip.isDeleted()){
                rest.setIsSuccess(false);
                rest.setMessage("该优惠券已使用或已过期!");
                return rest;
            }
            //判断是否生效
            if(cardToVip.getReleaseDate()!=null && cardToVip.getReleaseDate().after(new Date())){
                rest.setIsSuccess(false);
                rest.setMessage("优惠券未在规定使用时间内!");
                return rest;
            }
            BigInteger cardCouponId = cardToVip.getCardCouponsId();
            Map p = new HashMap();
            p.put("tenantId", cardToVip.getTenantId());
            p.put("id", cardCouponId);
            CardCoupons cardCoupons = cardCouponsMapper.findCardCouponsByIdIncludeDelete(p);
            //判断使用时间
            if(cardToVip.getStartTime()!=null && cardToVip.getEndTime()!=null && !(cardToVip.getStartTime().before(new Date()) && cardToVip.getEndTime().after(new Date()))){
                rest.setIsSuccess(false);
                rest.setMessage("优惠券未在规定使用时间内!");
                return rest;
            }
            //判断使用星期、时段
            Calendar c = Calendar.getInstance();
            int weekNum = c.get(Calendar.DAY_OF_WEEK) - 1;
            if(c.get(Calendar.DAY_OF_WEEK) == 1){
                weekNum = 7;
            }
            String week=(cardCoupons.getWeekday1()+","+cardCoupons.getWeekday2()+","+cardCoupons.getWeekday3()+","+cardCoupons.getWeekday4()+","+cardCoupons.getWeekday5()+","+cardCoupons.getWeekday6()+","+cardCoupons.getWeekday7()+",");
            String[] weeks = week.split(",");
            if("1".equals(weeks[weekNum - 1])){
                SimpleDateFormat sdf=new SimpleDateFormat("HH:mm:ss");
                Date useTime=sdf.parse(sdf.format(new Date()));
                if(cardCoupons.getUseStartTime()!=null && cardCoupons.getUseEndTime()!=null && !(cardCoupons.getUseStartTime().before(useTime) && cardCoupons.getUseEndTime().after(useTime))){
                    LogUtil.logInfo("查询会员可用券，未在使用范围内（时段）");
                    rest.setIsSuccess(false);
                    rest.setMessage("优惠券未在使用时段内!");
                    return rest;
                }
            }else{
                LogUtil.logInfo("查询会员可用券，未在使用范围内（周）");
                rest.setIsSuccess(false);
                rest.setMessage("优惠券未在使用时段内(周次)!");
                return rest;
            }
            //判断消费金额
            BigDecimal amount = BigDecimal.ZERO;
            DietOrderInfo orderInfo = null;
            String bId;
            if(params.get("orderInfoId") != null){
                Map<String, String> orderMap = new HashMap<>();
                orderMap.put("id", params.get("orderInfoId").toString());
                orderMap.put("tenantId", params.get("tenantId").toString());
                orderInfo = dietOrderInfoMapper.findByCondition(orderMap);
                if(orderInfo==null){
                    rest.setIsSuccess(false);
                    rest.setError("订单不存在！");
                    return rest;
                }
                amount=orderInfo.getTotalAmount()==null?BigDecimal.ZERO:orderInfo.getTotalAmount();
                bId = orderInfo.getBranchId()==null?null:orderInfo.getBranchId().toString();
            }else{
                if(params.get("amount") != null && !"".equals(params.get("amount").toString())){
                    amount = BigDecimal.valueOf(Double.valueOf(params.get("amount").toString()));
                }
                bId = params.get("branchId") == null ? null : params.get("branchId").toString();
            }
            if(cardCoupons.getLimitValue()!=null && amount.compareTo(BigDecimal.ZERO) != 0 && amount.compareTo(cardCoupons.getLimitValue()) < 0){
                rest.setIsSuccess(false);
                rest.setError("此券最低消费金额为" + cardCoupons.getLimitValue() + "，核销失败！");
                return rest;
            }
            //判断适用门店
            if(bId != null && bId.length() != 0){
                boolean isUseBranch = false;
                List<String> branchIds = cardCouponsMapper.getCouponsBranch(cardCoupons.getId(), cardCoupons.getTenantId());
                if(branchIds.size() > 0){
                    for(String b : branchIds){
                        if(b.equals(bId)){
                            isUseBranch = true;
                        }
                    }
                    if(!isUseBranch){
                        rest.setIsSuccess(false);
                        rest.setError("优惠券无法在本机构核销！");
                        return rest;
                    }
                }
            }
            else{
                rest.setIsSuccess(false);
                rest.setError("核销门店未知！");
                return rest;
            }
            String channel=params.get("channel")==null?"":params.get("channel").toString();
            if(orderInfo != null){
                orderInfo.setUsedCardId(cardToVip.getId());
                orderInfo.setLastUpdateAt(new Date());
                dietOrderInfoMapper.update(orderInfo);
                cardCouponsBook.setOrderId(orderInfo.getId());
                cardCouponsBook.setOrderCode(orderInfo.getOrderCode());
            }
            else{
                channel = "pos";
                if(params.get("cardType") != null && !params.get("cardType").toString().equals("")){
                    BigInteger cardType = BigInteger.valueOf(Long.valueOf(params.get("cardType").toString()));
                    if(!cardCoupons.getCardType().equals(cardType)){
                        rest.setIsSuccess(false);
                        rest.setMessage("抱歉，暂不支持此种优惠券的核销!");
                        return rest;
                    }
                }
            }

            //写核销记录
            cardCouponsBook.setBranchId(BigInteger.valueOf(Long.valueOf(bId)));
            cardCouponsBook.setTenantId(cardToVip.getTenantId());
            cardCouponsBook.setBusiness("coupons_pay");
            cardCouponsBook.setCreatedBy(BigInteger.ZERO);
            cardCouponsBook.setUpdatedBy(BigInteger.ZERO);
            cardCouponsBook.setCreatedAt(new Date());
            cardCouponsBook.setUpdatedAt(new Date());
            cardCouponsBook.setCardCouponsId(cardCoupons.getId());
            cardCouponsBook.setVipId(cardToVip.getVipId());
            cardCouponsBook.setChannel(channel);
            cardCouponsBook.setState(true);
            cardCouponsBook.setMemo("核销成功！");
            cardCouponsBook.setCardCode(cardToVip.getCardCode());
            cardToVip.setUseStatus("1");
            cardToVip.setLastUpdateAt(new Date());
            cardToVipMapper.update(cardToVip);
            cardCouponsBooks.add(cardCouponsBook);
            cardCouponsMapper.insertBookList(cardCouponsBooks);
            rest.setIsSuccess(true);
            rest.setData(cardToVip);
            rest.setMessage("优惠券核销成功!");
        }
        else{
            rest.setIsSuccess(false);
            rest.setMessage("优惠券不存在!");
        }

        return rest;
    }

    /**
     * 卡券回退接口
     *
     * @param params
     * @return
     */
    public ApiRest backupCard(Map params){
        ApiRest rest = new ApiRest();
        List<CardToVip> cardToVips = null;
        Map<String, Object> map = new HashMap<>();
        map.put("tenantId", params.get("tenantId"));
        if(params.get("cardToVipId") != null){
            map.put("id", params.get("cardToVipId"));
            cardToVips = cardToVipMapper.listCardToVip(map);
        }
        else if(params.get("cardCode") != null){
            map.put("cardCode", params.get("cardCode"));
            cardToVips = cardToVipMapper.listCardToVip(map);
        }
        else{
            rest.setIsSuccess(false);
            rest.setMessage("会员卡券id或code不存在!");
            return rest;
        }
        String channel="pos";
        if(params.get("channel")!=null){
            channel=params.get("channel").toString();
        }
        if(cardToVips != null && cardToVips.size() > 0){
            CardCouponsBook cardCouponsBook=new CardCouponsBook();
            List<CardCouponsBook> cardCouponsBooks=new ArrayList<>();
            CardToVip cardToVip = cardToVips.get(0);
            if("0".equals(cardToVip.getUseStatus())){
                rest.setIsSuccess(false);
                rest.setMessage("该卡券尚未使用!");
            }
            else{
                String bId = params.get("branchId") == null ? null : params.get("branchId").toString();
                if(params.get("orderInfoId") != null){
                    map.put("id", params.get("orderInfoId"));
                    DietOrderInfo orderInfo = dietOrderInfoMapper.findByCondition(map);
                    if(orderInfo != null && orderInfo.getUsedCardId() != null){
                        bId = orderInfo.getBranchId()==null?null:orderInfo.getBranchId().toString();
                        if(bId == null || bId.length() == 0){
                            rest.setIsSuccess(false);
                            rest.setError("核销门店未知！");
                            return rest;
                        }
                        cardCouponsBook.setOrderId(orderInfo.getId());
                        cardCouponsBook.setOrderCode(orderInfo.getOrderCode());
                        orderInfo.setUsedCardId(null);
                        orderInfo.setLastUpdateAt(new Date());
                        dietOrderInfoMapper.update(orderInfo);
                    }
                    else{
                        rest.setIsSuccess(false);
                        rest.setMessage("该订单不存在或者已经使用过卡券!");
                    }
                }
                if(bId == null || bId.length() == 0){
                    rest.setIsSuccess(false);
                    rest.setError("核销门店未知！");
                    return rest;
                }
                cardToVip.setUseStatus("0");
                cardToVip.setLastUpdateAt(new Date());
                cardToVipMapper.update(cardToVip);
                //写核销记录
                cardCouponsBook.setBranchId(BigInteger.valueOf(Long.valueOf(bId)));
                cardCouponsBook.setTenantId(cardToVip.getTenantId());
                cardCouponsBook.setBusiness("coupons_reback");
                cardCouponsBook.setCreatedBy(BigInteger.ZERO);
                cardCouponsBook.setUpdatedBy(BigInteger.ZERO);
                cardCouponsBook.setCreatedAt(new Date());
                cardCouponsBook.setUpdatedAt(new Date());
                cardCouponsBook.setCardCouponsId(cardToVip.getCardCouponsId());
                cardCouponsBook.setVipId(cardToVip.getVipId());
                cardCouponsBook.setChannel(channel);
                cardCouponsBook.setState(true);
                cardCouponsBook.setMemo("回退成功！");
                cardCouponsBook.setCardCode(cardToVip.getCardCode());
                rest.setIsSuccess(true);
                rest.setMessage("卡券回退成功!");
                rest.setData(cardToVip);
            }
        }
        else{
            rest.setIsSuccess(false);
            rest.setMessage("卡券不存在!");
        }

        return rest;
    }

    /**
     * 精准营销(发送优惠券)
     *
     * @param params
     * @return
     */
    @Deprecated
    public ApiRest sendVipCard(Map params) throws ParseException{
        ApiRest rest = new ApiRest();
        rest.setIsSuccess(false);
        rest.setMessage("发送优惠券失败！");
        if(params.get("cardId") != null && params.get("vipId") != null){
            //新增精准营销活动(已存在不会重复新增)
            params.put("activityName", params.get("cardName"));
            params.put("amount", "99999");
            params.put("precisionUse", 1);
            params.put("startDate", "2017-12-01");
            params.put("endDate", "2099-12-01");
            params.put("weekInsert", "1,1,1,1,1,1,1");
            params.put("scope", 0);
            params.put("forCustomerType", 0);
            params.put("promotionStatus", 0);
            params.put("forCustomerCondition", 0);
            List<Branch> branches = branchService.findBranchByTenantId(params);
            String branchIds = "";
            for(int i = 0; i < branches.size(); i++){
                branchIds += branches.get(i).getId() + ",";
            }
            branchIds = branchIds.substring(0, branchIds.length());
            params.put("branchIds", branchIds);
            params.put("precisionUse", "1");
            dietPromotionService.addCardCouponsActivity(params);
            //获取对应活动
            Map<String, Object> map = new HashMap<>();
            map.put("tenantId", params.get("tenantId"));
            map.put("cardId", params.get("cardId"));
            map.put("precisionUse", 1);
            List<DietPromotionFestival> dietPromotionFestivals = dietPromotionFestivalMapper.dietPromotionFestivalList(map);
            if(dietPromotionFestivals != null && dietPromotionFestivals.size() > 0){
                //发送卡券
                String ids = params.get("vipId").toString();
                String[] idArr = ids.split(",");
                for(int i = 0; i < idArr.length; i++){
                    if(idArr[i] != null && !"".equals(idArr[i])){
                        Map<String, Object> map3 = new HashMap<>();
                        map3.put("tenantId", params.get("tenantId"));
                        map3.put("festivalId", dietPromotionFestivals.get(0).getId());
                        map3.put("vipId", idArr[i]);
                        rest=dietPromotionService.sendCardToVip(map3);
                        if(!rest.getIsSuccess()){
                            return rest;
                        }
                        //发放记录
                        map3.put("cardCouponsId", dietPromotionFestivals.get(0).getCardId());
                        addOrUpdateSendReport(map3);
                    }
                }
                rest.setIsSuccess(true);
                rest.setMessage("发送优惠券成功！");
            }
        }
        return rest;
    }

    /**
     * 精准营销(发送优惠券)
     *
     * @param params
     * @return
     */
    @Deprecated
    public ApiRest sendVipCardForScoreMall(Map params) throws ParseException{
        ApiRest rest = new ApiRest();
        rest.setIsSuccess(false);
        rest.setMessage("发送优惠券失败！");
        if(params.get("cardId") != null && params.get("vipId") != null){
            //新增精准营销活动(已存在不会重复新增)
            params.put("activityName", params.get("cardName"));
            params.put("amount", "99999");
            params.put("precisionUse", 1);
            params.put("startDate", "2017-12-01");
            params.put("endDate", "2099-12-01");
            params.put("weekInsert", "1,1,1,1,1,1,1");
            params.put("scope", 0);
            params.put("forCustomerType", 0);
            params.put("promotionStatus", 0);
            params.put("forCustomerCondition", 0);
            List<Branch> branches = branchService.findBranchByTenantId(params);
            String branchIds = "";
            for(int i = 0; i < branches.size(); i++){
                branchIds += branches.get(i).getId() + ",";
            }
            branchIds = branchIds.substring(0, branchIds.length());
            params.put("branchIds", branchIds);
            params.put("precisionUse", "1");
            dietPromotionService.addCardCouponsActivity(params);
            //获取对应活动
            Map<String, Object> map = new HashMap<>();
            map.put("tenantId", params.get("tenantId"));
            map.put("cardId", params.get("cardId"));
            map.put("precisionUse", 1);
            List<DietPromotionFestival> dietPromotionFestivals = dietPromotionFestivalMapper.dietPromotionFestivalList(map);
            if(dietPromotionFestivals != null && dietPromotionFestivals.size() > 0){
                //发送卡券
                String vipId = params.get("vipId").toString();
                Map<String, Object> map3 = new HashMap<>();
                map3.put("tenantId", params.get("tenantId"));
                map3.put("festivalId", dietPromotionFestivals.get(0).getId());
                map3.put("vipId", vipId);
                rest = dietPromotionService.sendCardToVip(map3);
                if(!rest.getIsSuccess()){
                    return rest;
                }
                //发放记录
                map3.put("cardCouponsId", dietPromotionFestivals.get(0).getCardId());
                addOrUpdateSendReport(map3);
                rest.setIsSuccess(true);
                rest.setMessage("发送优惠券成功！");
            }
        }
        return rest;
    }


    private int addOrUpdateSendReport(Map map){
        List<CardCouponsReport> list = cardCouponsMapper.cardCouponsReportList(map);
        if(list != null && list.size() > 0){
            CardCouponsReport cardCouponsReport = list.get(0);
            if(cardCouponsReport.getSendNumber() != null){
                cardCouponsReport.setSendNumber(cardCouponsReport.getSendNumber().add(BigInteger.ONE));
            }
            else{
                cardCouponsReport.setSendNumber(BigInteger.ONE);
            }
            cardCouponsReport.setLastUpdateAt(new Date());
            cardCouponsMapper.updateCardCouponsReport(cardCouponsReport);

        }
        else{
            Map<String, Object> param = new HashMap<>();
            param.put("tenantId", map.get("tenantId"));
            param.put("id", map.get("cardCouponsId"));
            CardCoupons cardCoupons = cardCouponsMapper.findCardCouponsById(param);

            if(cardCoupons != null){
                CardCouponsReport couponsReport = new CardCouponsReport();
                couponsReport.setSendNumber(BigInteger.ONE);
                couponsReport.setTenantId(new BigInteger(map.get("tenantId").toString()));
                couponsReport.setCardCouponsId(new BigInteger(map.get("cardCouponsId").toString()));
                couponsReport.setCardName(cardCoupons.getCardName());
                couponsReport.setCardType(cardCoupons.getCardType());
                couponsReport.setDiscount(cardCoupons.getDiscount());
                couponsReport.setFaceValue(cardCoupons.getFaceValue());
                couponsReport.setLimitValue(cardCoupons.getLimitValue());
                couponsReport.setCreateAt(new Date());
                couponsReport.setLastUpdateAt(new Date());
                cardCouponsMapper.addCardCouponsReport(couponsReport);
            }
        }


        return 0;
    }

    public ApiRest resetCouponsData(Map<String,String> params){
        ApiRest apiRest=new ApiRest();
        List<CardCoupons> cardCouponsList=cardCouponsMapper.cardCouponsListWithGoodsOrBranch(params);
        for(CardCoupons cardCoupons:cardCouponsList){
            if(StringUtils.isNotEmpty(cardCoupons.getLimitGoodsIds())){
                long goodsCount=cardCouponsMapper.countCouponsGoods(cardCoupons.getId(),cardCoupons.getTenantId());
                if(goodsCount<=0){
                    List<String> goodsIds=Arrays.asList(cardCoupons.getLimitGoodsIds().split(","));
                    cardCouponsMapper.insertCouponsGoods(cardCoupons.getId(),cardCoupons.getTenantId(),null,goodsIds);
                }
            }
            if(StringUtils.isNotEmpty(cardCoupons.getLimitBranchIds())){
                long branchCount=cardCouponsMapper.countCouponsBranch(cardCoupons.getId(),cardCoupons.getTenantId());
                if(branchCount<=0){
                    List<String> branchIds=Arrays.asList(cardCoupons.getLimitBranchIds().split(","));
                    cardCouponsMapper.insertCouponsBranch(cardCoupons.getId(),cardCoupons.getTenantId(),branchIds);
                }
            }
        }
        apiRest.setIsSuccess(true);
        apiRest.setMessage("处理优惠券数据成功！");
        return apiRest;
    }

    /**卡券调整后方法**/
    public ApiRest addOrUpdateCardCoupons(Map<String,String> params) throws ParseException{
        ApiRest apiRest=new ApiRest();
        BigInteger tenantId=BigInteger.valueOf(Long.valueOf(params.get("tenantId")));
        BigInteger branchId=BigInteger.valueOf(Long.valueOf(params.get("branchId")));
        String cardName=params.get("cardName");
        BigInteger cardType=BigInteger.valueOf(Long.valueOf(params.get("cardType")));
        BigDecimal faceValue=null;
        BigDecimal disCount=null;
        if(cardType.equals(BigInteger.valueOf(1))){
            faceValue=BigDecimal.valueOf(Double.valueOf(params.get("faceValue")));
        }else if(cardType.equals(BigInteger.valueOf(3))){
            disCount=BigDecimal.valueOf(Double.valueOf(params.get("discount")));
        }
        String colorValue=params.get("colorValue");
        Integer status=0;
        if("1".equals(params.get("status"))||"true".equals(params.get("status"))){
            status=1;
        }
        Integer manualGet=Integer.valueOf(params.get("manualGet"));
        String sendAllCountType=params.get("sendAllCountType");
        BigDecimal sendAllAmount=null;
        if("1".equals(sendAllCountType)){
            sendAllAmount=BigDecimal.valueOf(Double.valueOf(params.get("sendAllCount")));
        }
        String minAmountType=params.get("minAmountType");
        BigDecimal minAmount=null;
        if("1".equals(minAmountType)){
            minAmount=BigDecimal.valueOf(Double.valueOf(params.get("minAmount")));
        }
        Integer timeType=Integer.valueOf(params.get("timeType"));
        BigInteger periodOfValidity=BigInteger.valueOf(-1);
        String startTimeStr=null;
        String endTimeStr=null;
        if(timeType==2){
            periodOfValidity=BigInteger.valueOf(Long.valueOf(params.get("periodOfValidity")));
        }else if(timeType==3){
            startTimeStr=params.get("startTime").length()==19?params.get("startTime"):(params.get("startTime")+":00");
            endTimeStr=params.get("endTime").length()==19?params.get("endTime"):(params.get("endTime")+":59");
        }
        String useLimitType=params.get("useLimitType");
        Integer useLimit=0;
        if("1".equals(useLimitType)){
            useLimit=Integer.valueOf(params.get("useLimit"));
        }
        String dayMaxGetType=params.get("dayMaxGetType");
        BigDecimal dayMaxGet=null;
        if("1".equals(dayMaxGetType)){
            dayMaxGet=BigDecimal.valueOf(Double.valueOf(params.get("dayMaxGet")));
        }
        String totalMaxGetType=params.get("totalMaxGetType");
        BigDecimal totalMaxGet=null;
        if("1".equals(totalMaxGetType)){
            totalMaxGet=BigDecimal.valueOf(Double.valueOf(params.get("totalMaxGet")));
        }
        String couponUseDateRangeType=params.get("couponUseDateRangeType");
        Integer weekday1=0;
        Integer weekday2=0;
        Integer weekday3=0;
        Integer weekday4=0;
        Integer weekday5=0;
        Integer weekday6=0;
        Integer weekday7=0;
        String useStartTimeStr=null;
        String useEndTimeStr=null;
        if("1".equals(couponUseDateRangeType)){
            weekday1=Integer.valueOf(params.get("weekday1"));
            weekday2=Integer.valueOf(params.get("weekday2"));
            weekday3=Integer.valueOf(params.get("weekday3"));
            weekday4=Integer.valueOf(params.get("weekday4"));
            weekday5=Integer.valueOf(params.get("weekday5"));
            weekday6=Integer.valueOf(params.get("weekday6"));
            weekday7=Integer.valueOf(params.get("weekday7"));
            useStartTimeStr=params.get("useStartTime")+":00";
            useEndTimeStr=params.get("useEndTime")+":59";
        }else{
            weekday1=1;
            weekday2=1;
            weekday3=1;
            weekday4=1;
            weekday5=1;
            weekday6=1;
            weekday7=1;
            useStartTimeStr=null;
            useEndTimeStr=null;
        }
        String sendDateRangeType=params.get("sendDateRangeType");
        String sendStartTimeStr=null;
        String sendEndTimeStr=null;
        if("1".equals(sendDateRangeType)){
            sendStartTimeStr=params.get("sendStartTime")+":00";
            sendEndTimeStr=params.get("sendEndTime")+":59";
        }
        String sendMinAmountType=params.get("sendMinAmountType");
        BigDecimal sendMinAmount=null;
        if("1".equals(sendMinAmountType)){
            sendMinAmount=BigDecimal.valueOf(Double.valueOf(params.get("sendMinAmount")));
        }
        String remark=params.get("remark");
        String limitBranchIds=params.get("branchIds");
        String limitBranchNames=params.get("branchName");

        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat sdf2=new SimpleDateFormat("HH:mm:ss");
        CardCoupons cardCoupons=new CardCoupons();

        if(!periodOfValidity.equals(BigInteger.valueOf(-1)) && useLimit!=0 && periodOfValidity.compareTo(BigInteger.valueOf(useLimit))<0){
            apiRest.setIsSuccess(false);
            apiRest.setError("延迟生效天数不能大于券有效天数");
            return apiRest;
        }
        if(sendAllAmount!=null){
            if(dayMaxGet!=null&&dayMaxGet.compareTo(sendAllAmount)>0){
                apiRest.setIsSuccess(false);
                apiRest.setError("单日领取上限不能大于发放数量");
                return apiRest;
            }
            if(totalMaxGet!=null&&totalMaxGet.compareTo(sendAllAmount)>0){
                apiRest.setIsSuccess(false);
                apiRest.setError("累计领取上限不能大于发放数量");
                return apiRest;
            }
        }
        if(totalMaxGet!=null&&dayMaxGet!=null&&dayMaxGet.compareTo(totalMaxGet)>0){
            apiRest.setIsSuccess(false);
            apiRest.setError("单日领取上限不能大于累计领取上限");
            return apiRest;
        }

        if(StringUtils.isEmpty(params.get("id"))){//新增
            cardCoupons.setTenantId(tenantId);
            cardCoupons.setBranchId(branchId);
            cardCoupons.setSendedAmount(BigDecimal.ZERO);//新增默认为0
            cardCoupons.setCreateAt(new Date());
        }else{//修改
            BigInteger id=BigInteger.valueOf(Long.valueOf(params.get("id")));
            cardCoupons=cardCouponsMapper.getById(id,tenantId);
            if(cardCoupons==null){
                apiRest.setIsSuccess(false);
                apiRest.setError("查询优惠券失败！");
                return apiRest;
            }
            /*long count=cardCouponsMapper.countCouponUsed(cardCoupons.getId(),cardCoupons.getTenantId());
            if(count>0){
                apiRest.setIsSuccess(false);
                apiRest.setError("优惠券已被领取，仍有用户未使用，修改失败！");
                return apiRest;
            }*/
            if(cardCoupons.getSendedAmount()==null){
                cardCoupons.setSendedAmount(BigDecimal.ZERO);
            }
        }
        cardCoupons.setCardName(cardName);
        cardCoupons.setCardType(cardType);
        cardCoupons.setFaceValue(faceValue);
        if(disCount!=null){
            cardCoupons.setDiscount(disCount.multiply(BigDecimal.valueOf(10)));
        }
        cardCoupons.setColorValue(colorValue);
        cardCoupons.setStatus(status==1);
        cardCoupons.setManualGet(manualGet);
        cardCoupons.setSendAllAmount(sendAllAmount);
        cardCoupons.setLimitValue(minAmount);
        cardCoupons.setPeriodOfValidity(periodOfValidity);
        if(startTimeStr!=null&&endTimeStr!=null){
            cardCoupons.setStartTime(sdf.parse(startTimeStr));
            cardCoupons.setEndTime(sdf.parse(endTimeStr));
        }else{
            cardCoupons.setStartTime(null);
            cardCoupons.setEndTime(null);
        }
        cardCoupons.setLimitBranchIds(limitBranchIds);
        cardCoupons.setLimitBranchNames(limitBranchNames);
        cardCoupons.setUseLimit(useLimit);
        cardCoupons.setDayGetLimit(dayMaxGet);
        cardCoupons.setTotalGetLimit(totalMaxGet);
        cardCoupons.setWeekday1(weekday1);
        cardCoupons.setWeekday2(weekday2);
        cardCoupons.setWeekday3(weekday3);
        cardCoupons.setWeekday4(weekday4);
        cardCoupons.setWeekday5(weekday5);
        cardCoupons.setWeekday6(weekday6);
        cardCoupons.setWeekday7(weekday7);
        if(useStartTimeStr!=null&&useEndTimeStr!=null){
            cardCoupons.setUseStartTime(sdf2.parse(useStartTimeStr));
            cardCoupons.setUseEndTime(sdf2.parse(useEndTimeStr));
        }else{
            cardCoupons.setUseStartTime(null);
            cardCoupons.setUseEndTime(null);
        }
        if(sendStartTimeStr!=null&&sendEndTimeStr!=null){
            cardCoupons.setSendStartTime(sdf.parse(sendStartTimeStr));
            cardCoupons.setSendEndTime(sdf.parse(sendEndTimeStr));
        }else{
            cardCoupons.setSendStartTime(null);
            cardCoupons.setSendEndTime(null);
        }
        cardCoupons.setSendLimitValue(sendMinAmount);
        cardCoupons.setRemark(remark);
        cardCoupons.setLastUpdateAt(new Date());
        int flag=0;
        if(cardCoupons.getId()!=null){//更新
            flag=cardCouponsMapper.updateCardCoupons(cardCoupons);
        }else{//新增
            flag=cardCouponsMapper.addCardCoupons(cardCoupons);
        }
        if(flag!=0){
            if(StringUtils.isNotEmpty(limitBranchIds)){
                List<String> branchIds=Arrays.asList(limitBranchIds.split(","));
                //删除原有关联
                int i=cardCouponsMapper.deleteCouponsBranch(cardCoupons.getTenantId(),cardCoupons.getId());
                //插入新关系
                int j=cardCouponsMapper.insertCouponsBranch(cardCoupons.getId(),cardCoupons.getTenantId(),branchIds);
            }else{
                //删除原有关联
                int i=cardCouponsMapper.deleteCouponsBranch(cardCoupons.getTenantId(),cardCoupons.getId());
            }
        }
        apiRest.setIsSuccess(true);
        apiRest.setMessage("保存优惠券成功！");
        return apiRest;
    }
    public ApiRest getCardCouponsById(Map<String,String> params){
        ApiRest apiRest=new ApiRest();
        BigInteger tenantId=BigInteger.valueOf(Long.valueOf(params.get("tenantId")));
        BigInteger couponsId=BigInteger.valueOf(Long.valueOf(params.get("couponsId")));
        CardCoupons cardCoupons=cardCouponsMapper.getById(couponsId,tenantId);
        if(cardCoupons==null){
            apiRest.setIsSuccess(false);
            apiRest.setError("查询优惠券失败！");
            return apiRest;
        }
        apiRest.setIsSuccess(true);
        apiRest.setData(cardCoupons);
        apiRest.setMessage("查询优惠券成功！");
        return apiRest;
    }
    public ApiRest saveSendCouponsActivity(Map params){
        ApiRest apiRest=new ApiRest();
        BigInteger tenantId=BigInteger.valueOf(Long.valueOf(params.get("tenantId").toString()));
        BigInteger branchId=BigInteger.valueOf(Long.valueOf(params.get("branchId").toString()));
        BigInteger empId=BigInteger.valueOf(Long.valueOf(params.get("empId").toString()));
        String name=params.get("name").toString();
        String content=params.get("content").toString();
        BigDecimal score=BigDecimal.ZERO;
        if(params.get("score")!=null){
            score=BigDecimal.valueOf(Double.valueOf(params.get("score").toString()));
        }
        BigDecimal exp=BigDecimal.ZERO;
        if(params.get("exp")!=null){
            exp=BigDecimal.valueOf(Double.valueOf(params.get("exp").toString()));
        }
        if(params.get("cardJson")!=null&&!params.get("cardJson").toString().equals("")
                &&params.get("vipId")!=null&&!params.get("vipId").toString().equals("")){
            String code = "";
            String prefix = "JZYX_" + tenantId;
            code = serialNumberCreatorMapper.getToday(prefix, 5);
            SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
            String dateMark = format.format(new Date());
            code = "JZYX"+dateMark.substring(2, dateMark.length()) + code;

            PrecisionMarketingOrder precisionMarketingOrder=new PrecisionMarketingOrder();
            precisionMarketingOrder.setBranchId(branchId);
            precisionMarketingOrder.setTenantId(tenantId);
            precisionMarketingOrder.setCode(code);
            precisionMarketingOrder.setName(name);
            precisionMarketingOrder.setContent(content);
            precisionMarketingOrder.setCreatedAt(new Date());
            precisionMarketingOrder.setUpdatedAt(new Date());
            precisionMarketingOrder.setCreatedBy(empId);
            precisionMarketingOrder.setUpdatedBy(empId);
            precisionMarketingOrder.setExp(exp);
            precisionMarketingOrder.setScore(score);
            final List<String> vipIds=Arrays.asList(params.get("vipId").toString().split(","));
            precisionMarketingOrder.setVipQuantity(vipIds.size());
            precisionMarketingOrder.setSendGroupId(null);
            cardCouponsMapper.insertMarketingOrder(precisionMarketingOrder);
            String cardJson=params.get("cardJson").toString();
            JSONArray jsonArray=JSONArray.fromObject(cardJson);
            for(Object o:jsonArray){
                Map cardMap=(Map)o;
                BigInteger cardId=BigInteger.valueOf(Long.valueOf(cardMap.get("cardId").toString()));
                Integer quantity=Integer.valueOf(cardMap.get("quantity").toString());
                PrecisionMarketingOrderCouponsDetail precisionMarketingOrderCouponsDetail=new PrecisionMarketingOrderCouponsDetail();
                precisionMarketingOrderCouponsDetail.setBranchId(branchId);
                precisionMarketingOrderCouponsDetail.setTenantId(tenantId);
                precisionMarketingOrderCouponsDetail.setCreatedAt(new Date());
                precisionMarketingOrderCouponsDetail.setUpdatedAt(new Date());
                precisionMarketingOrderCouponsDetail.setCreatedBy(empId);
                precisionMarketingOrderCouponsDetail.setUpdatedBy(empId);
                precisionMarketingOrderCouponsDetail.setCouponsId(cardId);
                precisionMarketingOrderCouponsDetail.setCouponsQuantity(quantity);
                precisionMarketingOrderCouponsDetail.setOrderId(precisionMarketingOrder.getId());
                cardCouponsMapper.insertMarketingOrderCouponsDetail(precisionMarketingOrderCouponsDetail);
            }
            for(String vipIdStr:vipIds){
                BigInteger vipId=BigInteger.valueOf(Long.valueOf(vipIdStr));
                PrecisionMarketingOrderVipDetail precisionMarketingOrderVipDetail=new PrecisionMarketingOrderVipDetail();
                precisionMarketingOrderVipDetail.setBranchId(branchId);
                precisionMarketingOrderVipDetail.setTenantId(tenantId);
                precisionMarketingOrderVipDetail.setCreatedAt(new Date());
                precisionMarketingOrderVipDetail.setUpdatedAt(new Date());
                precisionMarketingOrderVipDetail.setCreatedBy(empId);
                precisionMarketingOrderVipDetail.setUpdatedBy(empId);
                precisionMarketingOrderVipDetail.setOrderId(precisionMarketingOrder.getId());
                precisionMarketingOrderVipDetail.setVipId(vipId);
                cardCouponsMapper.insertMarketingOrderVipDetail(precisionMarketingOrderVipDetail);

            }
            final BigInteger orderId=precisionMarketingOrder.getId();
            final String orderCode=precisionMarketingOrder.getCode();
            final JSONArray cardJsonArr=jsonArray;
            final List<String> vipIdList=vipIds;
            final BigInteger tenantIdP=tenantId;
            final BigInteger branchIdP=branchId;
            final BigInteger empIdP=empId;
            new Thread(){
                public void run(){
                    try{
                        sleep(3000);
                        sendCardCouponsToVip(empIdP,branchIdP,tenantIdP,vipIdList,cardJsonArr,orderId,orderCode);
                    }
                    catch(InterruptedException e){
                        e.printStackTrace();
                    }
                    catch(ParseException e){
                        e.printStackTrace();
                    }
                }
            }.start();
            apiRest.setIsSuccess(true);
            apiRest.setMessage("发送成功！会员或礼品较多时会有延迟，详情请查看发送明细");
        }else{
            apiRest.setIsSuccess(false);
            apiRest.setError("参数错误！");
        }
        return apiRest;
    }
    /**
     * 新精准营销(发送优惠券)
     * 支持多券多会员发送
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public void sendCardCouponsToVip(BigInteger empId,BigInteger branchId,BigInteger tenantId,List<String> vipIds, JSONArray cardJsonArr, BigInteger orderId,String orderCode) throws ParseException{
        if(cardJsonArr!=null&&cardJsonArr.size()>0
                &&vipIds!=null&&vipIds.size()>0){
            List<CardToVip> cardToVips=new ArrayList<>();
            List<CardCouponsBook> cardCouponsBooks=new ArrayList<>();
            Map cardMap=new HashMap();
            Map vipCardMap=new HashMap();
            Map cardCountMap=new HashMap();
            for(String vipIdStr:vipIds){
                BigInteger vipId=BigInteger.valueOf(Long.valueOf(vipIdStr));
                Map vipMap=new HashMap();
                vipMap.put("tenantId",tenantId);
                vipMap.put("id",vipId);
                Vip vip=vipMapper.findById(vipMap);
                if(vip!=null){
                    for(Object cardJson:cardJsonArr){
                        Map cardJsonMap=(Map)cardJson;
                        BigInteger cardId=BigInteger.valueOf(Long.valueOf(cardJsonMap.get("cardId").toString()));
                        Integer quantity=Integer.valueOf(cardJsonMap.get("quantity").toString());
                        CardCoupons cardCoupons=cardCouponsMapper.getById(cardId,tenantId);
                        if(cardCoupons!=null){
                            List<String> cardCodeList=cardCodeArray("COUPONS_" + tenantId.toString(),quantity,10);
                            if(cardCodeList.size()==0){
                                LogUtil.logInfo("券码生成失败！vipId="+vipIdStr+",cardId="+cardId+",prefix=COUPONS_" + tenantId.toString());
                            }
                            for(int i=0;i<quantity;i++){
                                CardCouponsBook cardCouponsBook=new CardCouponsBook();
                                cardCouponsBook.setBranchId(branchId);
                                cardCouponsBook.setTenantId(tenantId);
                                cardCouponsBook.setBusiness("coupons_get");
                                cardCouponsBook.setCreatedBy(empId);
                                cardCouponsBook.setUpdatedBy(empId);
                                cardCouponsBook.setCreatedAt(new Date());
                                cardCouponsBook.setUpdatedAt(new Date());
                                cardCouponsBook.setCardCouponsId(cardCoupons.getId());
                                cardCouponsBook.setVipId(vip.getId());
                                cardCouponsBook.setChannel("direct");
                                cardCouponsBook.setOrderId(orderId);
                                cardCouponsBook.setOrderCode(orderCode);
                                Map<String, Object> map = new HashMap<>();
                                map.put("cardId", cardCoupons.getId());
                                map.put("vipId", vip.getId());

                                Integer totalAlreadyGet=0;
                                if(cardCountMap.get(cardId.toString())!=null){
                                    totalAlreadyGet=Integer.valueOf(cardCountMap.get(cardId.toString()).toString());
                                }
                                //map.put("useStatus", 0);
                                //总发放数量，校验剩余数量
                                if(cardCoupons.getSendAllAmount()!=null){
                                    if((cardCoupons.getSendedAmount()==null?BigDecimal.ZERO:cardCoupons.getSendedAmount()).add(BigDecimal.valueOf(totalAlreadyGet)).compareTo(cardCoupons.getSendAllAmount())>=0){
                                        cardCouponsBook.setCardCode(null);
                                        cardCouponsBook.setState(false);
                                        cardCouponsBook.setMemo("券库存不足");
                                        cardCouponsBooks.add(cardCouponsBook);
                                        LogUtil.logInfo("券库存不足，vipId="+vipId+"，cardId="+cardCoupons.getId());
                                        continue;
                                    }
                                }
                                Integer alreadayGet=0;
                                if(vipCardMap.get(vipIdStr+"_"+cardId.toString())!=null){
                                    alreadayGet=Integer.valueOf(vipCardMap.get(vipIdStr+"_"+cardId.toString()).toString());
                                }
                                //单日领取限制
                                if(cardCoupons.getDayGetLimit()!=null){
                                    SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
                                    map.put("sendDate",sdf.format(new Date()));
                                    //单日领取数
                                    List<CardToVip> dayCheck = cardToVipMapper.listCardToVip(map);
                                    if(dayCheck!=null&&BigDecimal.valueOf(Double.valueOf(dayCheck.size()+alreadayGet)).compareTo(cardCoupons.getDayGetLimit())>=0){
                                        cardCouponsBook.setCardCode(null);
                                        cardCouponsBook.setState(false);
                                        cardCouponsBook.setMemo("超出单日领取数");
                                        cardCouponsBooks.add(cardCouponsBook);
                                        LogUtil.logInfo("发券时，会员已有券数量超过此券的单日领取数，vipId="+vipId+"，cardId="+cardCoupons.getId());
                                        continue;
                                    }
                                }
                                //累计领取数
                                if(cardCoupons.getTotalGetLimit()!=null){
                                    map.remove("sendDate");
                                    List<CardToVip> totalCheck = cardToVipMapper.listCardToVip(map);
                                    if(totalCheck!=null&&BigDecimal.valueOf(Double.valueOf(totalCheck.size()+alreadayGet)).compareTo(cardCoupons.getTotalGetLimit())>=0){
                                        cardCouponsBook.setCardCode(null);
                                        cardCouponsBook.setState(false);
                                        cardCouponsBook.setMemo("超出累计领取数");
                                        cardCouponsBooks.add(cardCouponsBook);
                                        LogUtil.logInfo("发券时，会员已有券数量超过此券的累计领取数，vipId="+vipId+"，cardId="+cardCoupons.getId());
                                        continue;
                                    }
                                }

                                CardToVip cardToVip=new CardToVip();
                                cardToVip.setTenantId(tenantId);
                                cardToVip.setVipId(vipId);
                                cardToVip.setCardCouponsId(cardCoupons.getId());
                                cardToVip.setCardName(cardCoupons.getCardName());
                                cardToVip.setFestivalId(null);
                                cardToVip.setCardType(cardCoupons.getCardType());
                                cardToVip.setUseStatus("0");
                                String week = "";
                                if(cardCoupons.getWeekday1()==1){
                                    week += "1,";
                                }else{
                                    week += "0,";
                                }
                                if(cardCoupons.getWeekday2()==1){
                                    week += "1,";
                                }else{
                                    week += "0,";
                                }
                                if(cardCoupons.getWeekday3()==1){
                                    week += "1,";
                                }else{
                                    week += "0,";
                                }
                                if(cardCoupons.getWeekday4()==1){
                                    week += "1,";
                                }else{
                                    week += "0,";
                                }
                                if(cardCoupons.getWeekday5()==1){
                                    week += "1,";
                                }else{
                                    week += "0,";
                                }
                                if(cardCoupons.getWeekday6()==1){
                                    week += "1,";
                                }else{
                                    week += "0,";
                                }
                                if(cardCoupons.getWeekday7()==1){
                                    week += "1,";
                                }else{
                                    week += "0,";
                                }
                                SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd ");
                                SimpleDateFormat format3 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                Integer useLimit=cardCoupons.getUseLimit()==null?0:cardCoupons.getUseLimit();
                                Date today = new Date();
                                if(cardCoupons.getPeriodOfValidity() != null && !cardCoupons.getPeriodOfValidity().equals(BigInteger.valueOf(-1))){
                                    cardToVip.setPeriodOfValidity(cardCoupons.getPeriodOfValidity());
                                    Calendar c = Calendar.getInstance();
                                    c.setTime(today);
                                    c.add(Calendar.DAY_OF_MONTH, cardCoupons.getPeriodOfValidity().intValue()-1);// 今天+n天
                                    Date endDate = c.getTime();
                                    cardToVip.setStartTime(format3.parse(format1.format(today)+" 00:00:00"));
                                    cardToVip.setEndTime(format3.parse(format1.format(endDate)+" 23:59:59"));
                                }
                                else{
                                    cardToVip.setStartTime(cardCoupons.getStartTime());
                                    cardToVip.setEndTime(cardCoupons.getEndTime());
                                    cardToVip.setPeriodOfValidity(BigInteger.valueOf(-1));
                                }
                                if(cardCoupons.getLimitValue() != null){
                                    cardToVip.setLimitValue(cardCoupons.getLimitValue());
                                }
                                if(cardCoupons.getDiscount() != null){
                                    cardToVip.setDiscount(cardCoupons.getDiscount());
                                }else{
                                    cardToVip.setDiscount(BigDecimal.ZERO);
                                }
                                if(cardCoupons.getFaceValue() != null){
                                    cardToVip.setFaceValue(cardCoupons.getFaceValue());
                                }else{
                                    cardToVip.setFaceValue(BigDecimal.ZERO);
                                }
                                cardToVip.setWeek(week);
                                cardToVip.setColorValue(cardCoupons.getColorValue());

                                if(useLimit == 0){//领券即可用
                                    cardToVip.setReleaseDate(null);
                                }else{//起始时间做处理
                                    Calendar c = Calendar.getInstance();
                                    c.setTime(today);
                                    c.add(Calendar.DAY_OF_MONTH, useLimit);// 今天+n天
                                    Date useDate = c.getTime();
                                    cardToVip.setReleaseDate(format3.parse(format1.format(useDate)+" 00:00:00"));
                                }
                                //生成卡券编码
                                //long mills = System.currentTimeMillis();
                                //SimpleDateFormat sdf=new SimpleDateFormat("yyMMdd");
                                //String prefix=serialNumberCreatorMapper.getToday("COUPONS_" + tenantId.toString(), 8);
                                String code =cardCodeList.size()==0?"":(CodeUtiles.getRandomString(6)+cardCodeList.get(i));
                                cardToVip.setCardCode(code);
                                cardToVip.setCreateAt(new Date());
                                cardToVip.setLastUpdateAt(new Date());
                                cardToVips.add(cardToVip);
                                if(vipCardMap.get(vipIdStr+"_"+cardId.toString())==null){
                                    vipCardMap.put(vipIdStr+"_"+cardId.toString(),1);
                                }else{
                                    Integer indexCount=Integer.valueOf(vipCardMap.get(vipIdStr+"_"+cardId.toString()).toString());
                                    vipCardMap.put(vipIdStr+"_"+cardId.toString(),indexCount+1);
                                }
                                if(cardCountMap.get(cardId.toString())==null){
                                    cardCountMap.put(cardId.toString(),1);
                                }else{
                                    Integer indexTotalCount=Integer.valueOf(cardCountMap.get(cardId.toString()).toString());
                                    cardCountMap.put(cardId.toString(),indexTotalCount+1);
                                }
                                cardCouponsBook.setState(true);
                                cardCouponsBook.setMemo("发送成功！");
                                cardCouponsBook.setCardCode(cardToVip.getCardCode());
                                cardCouponsBooks.add(cardCouponsBook);
                                if(cardMap.get(cardCoupons.getId())!=null){
                                    BigDecimal nowValue=BigDecimal.valueOf(Double.valueOf(cardMap.get(cardCoupons.getId()).toString()));
                                    cardMap.put(cardCoupons.getId(),nowValue.add(BigDecimal.ONE));
                                }else{
                                    cardMap.put(cardCoupons.getId(),cardCoupons.getSendedAmount().add(BigDecimal.ONE));
                                }
                            }
                        }
                    }
                }
            }
            if(cardToVips.size()>0){
                cardToVipMapper.insertList(cardToVips);
                for(Object i:cardMap.keySet()){
                    BigInteger cardId=BigInteger.valueOf(Long.valueOf(i.toString()));
                    BigDecimal sendedAmount=BigDecimal.valueOf(Double.valueOf(cardMap.get(i).toString()));
                    cardCouponsMapper.updateSendedAmount(tenantId,cardId,sendedAmount);
                }
            }
            if(cardCouponsBooks.size()>0){
                cardCouponsMapper.insertBookList(cardCouponsBooks);
            }
        }
    }
    @Transactional(rollbackFor = Exception.class)
    public void sendCardToVip(Map<String,String> params) throws ParseException{
        String cardListStr=params.get("cardId");
        String vipListStr=params.get("vipId");
        String channel=params.get("channel");
        BigInteger tenantId=BigInteger.valueOf(Long.valueOf(params.get("tenantId")));
        BigInteger branchId=BigInteger.valueOf(Long.valueOf(params.get("branchId")));
        BigInteger orderId=null;
        if(StringUtils.isNotEmpty(params.get("orderId"))){
            orderId=BigInteger.valueOf(Long.valueOf(params.get("orderId")));
        }
        BigInteger empId=BigInteger.ZERO;
        if(StringUtils.isNotEmpty(params.get("empId"))){
            empId=BigInteger.valueOf(Long.valueOf(params.get("empId")));
        }
        String orderCode=null;
        if(StringUtils.isNotEmpty(params.get("orderCode"))){
            orderCode=params.get("orderCode");
        }
        if(StringUtils.isNotEmpty(cardListStr)&&StringUtils.isNotEmpty(vipListStr)&&StringUtils.isNotEmpty(channel)){
            List<CardToVip> cardToVips=new ArrayList<>();
            List<CardCouponsBook> cardCouponsBooks=new ArrayList<>();
            Map cardMap=new HashMap();
            Map vipCardMap=new HashMap();
            Map cardCountMap=new HashMap();
            List<String> vipIds=Arrays.asList(vipListStr.split(","));
            for(String vipIdStr:vipIds){
                BigInteger vipId=BigInteger.valueOf(Long.valueOf(vipIdStr));
                Map vipMap=new HashMap();
                vipMap.put("tenantId",tenantId);
                vipMap.put("id",vipId);
                Vip vip=vipMapper.findById(vipMap);
                if(vip!=null){
                    List<String> cardIdList=Arrays.asList(cardListStr.split(","));
                    for(String cardIdStr:cardIdList){
                        String[] cardIds=cardIdStr.split("#");
                        BigInteger cardId=BigInteger.valueOf(Long.valueOf(cardIds[0]));
                        Integer quantity=1;
                        if(cardIds.length>1&&cardIds[1]!=null){
                            quantity=Integer.valueOf(cardIds[1]);
                        }
                        CardCoupons cardCoupons=cardCouponsMapper.getById(cardId,tenantId);
                        if(cardCoupons!=null){
                            List<String> cardCodeList=cardCodeArray("COUPONS_" + tenantId.toString(),quantity,10);
                            if(cardCodeList.size()==0){
                                LogUtil.logInfo("券码生成失败！vipId="+vipIdStr+",cardId="+cardIdStr+",prefix=COUPONS_" + tenantId.toString());
                            }
                            for(int i=0;i<quantity;i++){
                                CardCouponsBook cardCouponsBook=new CardCouponsBook();
                                cardCouponsBook.setBranchId(branchId);
                                cardCouponsBook.setTenantId(tenantId);
                                cardCouponsBook.setBusiness("coupons_get");
                                cardCouponsBook.setCreatedBy(empId);
                                cardCouponsBook.setUpdatedBy(empId);
                                cardCouponsBook.setCreatedAt(new Date());
                                cardCouponsBook.setUpdatedAt(new Date());
                                cardCouponsBook.setCardCouponsId(cardCoupons.getId());
                                cardCouponsBook.setVipId(vip.getId());
                                cardCouponsBook.setChannel(channel);
                                cardCouponsBook.setOrderId(orderId);
                                cardCouponsBook.setOrderCode(orderCode);
                                Map<String, Object> map = new HashMap<>();
                                map.put("cardId", cardCoupons.getId());
                                map.put("vipId", vip.getId());
                                Integer totalAlreadyGet=0;
                                if(cardCountMap.get(cardId.toString())!=null){
                                    totalAlreadyGet=Integer.valueOf(cardCountMap.get(cardId.toString()).toString());
                                }
                                //map.put("useStatus", 0);
                                //总发放数量，校验剩余数量
                                if(cardCoupons.getSendAllAmount()!=null){
                                    if((cardCoupons.getSendedAmount()==null?BigDecimal.ZERO:cardCoupons.getSendedAmount()).add(BigDecimal.valueOf(totalAlreadyGet)).compareTo(cardCoupons.getSendAllAmount())>=0){
                                        LogUtil.logInfo("sendCardToVip--券库存不足，vipId="+vipId+"，cardId="+cardCoupons.getId());
                                        continue;
                                    }
                                }
                                Integer alreadayGet=0;
                                if(vipCardMap.get(vipIdStr+"_"+cardId.toString())!=null){
                                    alreadayGet=Integer.valueOf(vipCardMap.get(vipIdStr+"_"+cardId.toString()).toString());
                                }
                                //单日领取限制
                                if(cardCoupons.getDayGetLimit()!=null){
                                    SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
                                    map.put("sendDate",sdf.format(new Date()));
                                    //单日领取数
                                    List<CardToVip> dayCheck = cardToVipMapper.listCardToVip(map);
                                    if(dayCheck!=null&&BigDecimal.valueOf(Double.valueOf(dayCheck.size()+alreadayGet)).compareTo(cardCoupons.getDayGetLimit())>=0){
                                        LogUtil.logInfo("sendCardToVip--发券时，会员已有券数量超过此券的单日领取数，vipId="+vipId+"，cardId="+cardCoupons.getId());
                                        continue;
                                    }
                                }
                                //累计领取数
                                if(cardCoupons.getTotalGetLimit()!=null){
                                    map.remove("sendDate");
                                    List<CardToVip> totalCheck = cardToVipMapper.listCardToVip(map);
                                    if(totalCheck!=null&&BigDecimal.valueOf(Double.valueOf(totalCheck.size()+alreadayGet)).compareTo(cardCoupons.getTotalGetLimit())>=0){
                                        LogUtil.logInfo("sendCardToVip--发券时，会员已有券数量超过此券的累计领取数，vipId="+vipId+"，cardId="+cardCoupons.getId());
                                        continue;
                                    }
                                }

                                CardToVip cardToVip=new CardToVip();
                                cardToVip.setTenantId(tenantId);
                                cardToVip.setVipId(vipId);
                                cardToVip.setCardCouponsId(cardCoupons.getId());
                                cardToVip.setCardName(cardCoupons.getCardName());
                                cardToVip.setFestivalId(null);
                                cardToVip.setCardType(cardCoupons.getCardType());
                                cardToVip.setUseStatus("0");
                                String week = "";
                                if(cardCoupons.getWeekday1()==1){
                                    week += "1,";
                                }else{
                                    week += "0,";
                                }
                                if(cardCoupons.getWeekday2()==1){
                                    week += "1,";
                                }else{
                                    week += "0,";
                                }
                                if(cardCoupons.getWeekday3()==1){
                                    week += "1,";
                                }else{
                                    week += "0,";
                                }
                                if(cardCoupons.getWeekday4()==1){
                                    week += "1,";
                                }else{
                                    week += "0,";
                                }
                                if(cardCoupons.getWeekday5()==1){
                                    week += "1,";
                                }else{
                                    week += "0,";
                                }
                                if(cardCoupons.getWeekday6()==1){
                                    week += "1,";
                                }else{
                                    week += "0,";
                                }
                                if(cardCoupons.getWeekday7()==1){
                                    week += "1,";
                                }else{
                                    week += "0,";
                                }
                                SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd ");
                                SimpleDateFormat format3 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                Integer useLimit=cardCoupons.getUseLimit()==null?0:cardCoupons.getUseLimit();
                                Date today = new Date();
                                if(cardCoupons.getPeriodOfValidity() != null && !cardCoupons.getPeriodOfValidity().equals(BigInteger.valueOf(-1))){
                                    cardToVip.setPeriodOfValidity(cardCoupons.getPeriodOfValidity());
                                    Calendar c = Calendar.getInstance();
                                    c.setTime(today);
                                    c.add(Calendar.DAY_OF_MONTH, cardCoupons.getPeriodOfValidity().intValue()-1);// 今天+n天
                                    Date endDate = c.getTime();
                                    cardToVip.setStartTime(format3.parse(format1.format(today)+" 00:00:00"));
                                    cardToVip.setEndTime(format3.parse(format1.format(endDate)+" 23:59:59"));
                                }
                                else{
                                    cardToVip.setStartTime(cardCoupons.getStartTime());
                                    cardToVip.setEndTime(cardCoupons.getEndTime());
                                    cardToVip.setPeriodOfValidity(BigInteger.valueOf(-1));
                                }
                                if(cardCoupons.getLimitValue() != null){
                                    cardToVip.setLimitValue(cardCoupons.getLimitValue());
                                }
                                if(cardCoupons.getDiscount() != null){
                                    cardToVip.setDiscount(cardCoupons.getDiscount());
                                }else{
                                    cardToVip.setDiscount(BigDecimal.ZERO);
                                }
                                if(cardCoupons.getFaceValue() != null){
                                    cardToVip.setFaceValue(cardCoupons.getFaceValue());
                                }else{
                                    cardToVip.setFaceValue(BigDecimal.ZERO);
                                }
                                cardToVip.setWeek(week);
                                cardToVip.setColorValue(cardCoupons.getColorValue());

                                if(useLimit == 0){//领券即可用
                                    cardToVip.setReleaseDate(null);
                                }else{//起始时间做处理
                                    Calendar c = Calendar.getInstance();
                                    c.setTime(today);
                                    c.add(Calendar.DAY_OF_MONTH, useLimit);// 今天+n天
                                    Date useDate = c.getTime();
                                    cardToVip.setReleaseDate(format3.parse(format1.format(useDate)+" 00:00:00"));
                                }
                                //生成卡券编码
                                //long mills = System.currentTimeMillis();
                                //SimpleDateFormat sdf=new SimpleDateFormat("yyMMdd");
                                //String prefix=serialNumberCreatorMapper.getToday("COUPONS_" + tenantId.toString(), 8);
                                String code =cardCodeList.size()==0?"":(CodeUtiles.getRandomString(6)+cardCodeList.get(i));
                                cardToVip.setCardCode(code);
                                cardToVip.setCreateAt(new Date());
                                cardToVip.setLastUpdateAt(new Date());
                                cardToVips.add(cardToVip);
                                if(vipCardMap.get(vipIdStr+"_"+cardId.toString())==null){
                                    vipCardMap.put(vipIdStr+"_"+cardId.toString(),1);
                                }else{
                                    Integer indexCount=Integer.valueOf(vipCardMap.get(vipIdStr+"_"+cardId.toString()).toString());
                                    vipCardMap.put(vipIdStr+"_"+cardId.toString(),indexCount+1);
                                }
                                if(cardCountMap.get(cardId.toString())==null){
                                    cardCountMap.put(cardId.toString(),1);
                                }else{
                                    Integer indexTotalCount=Integer.valueOf(cardCountMap.get(cardId.toString()).toString());
                                    cardCountMap.put(cardId.toString(),indexTotalCount+1);
                                }
                                cardCouponsBook.setState(true);
                                cardCouponsBook.setMemo("发送成功！");
                                cardCouponsBook.setCardCode(cardToVip.getCardCode());
                                cardCouponsBooks.add(cardCouponsBook);
                                if(cardMap.get(cardCoupons.getId())!=null){
                                    BigDecimal nowValue=BigDecimal.valueOf(Double.valueOf(cardMap.get(cardCoupons.getId()).toString()));
                                    cardMap.put(cardCoupons.getId(),nowValue.add(BigDecimal.ONE));
                                }else{
                                    cardMap.put(cardCoupons.getId(),cardCoupons.getSendedAmount()==null?BigDecimal.ZERO:cardCoupons.getSendedAmount().add(BigDecimal.ONE));
                                }
                            }
                        }
                    }
                }
            }
            if(cardToVips.size()>0){
                cardToVipMapper.insertList(cardToVips);
                for(Object i:cardMap.keySet()){
                    BigInteger cardId=BigInteger.valueOf(Long.valueOf(i.toString()));
                    BigDecimal sendedAmount=BigDecimal.valueOf(Double.valueOf(cardMap.get(i).toString()));
                    cardCouponsMapper.updateSendedAmount(tenantId,cardId,sendedAmount);
                }
            }
            if(cardCouponsBooks.size()>0){
                cardCouponsMapper.insertBookList(cardCouponsBooks);
            }
        }
    }

    public ApiRest listMarketingOrder(Map<String,String> params){
        ApiRest apiRest = new ApiRest();
        Integer page = 1;
        Integer rows = 20;
        if(StringUtils.isNotEmpty(params.get("page"))){
            page = Integer.valueOf(params.get("page"));
        }
        if(StringUtils.isNotEmpty(params.get("rows"))){
            rows = Integer.valueOf(params.get("rows"));
        }
        Integer offset = (page - 1) * rows;
        params.put("offset", offset.toString());
        if(params.get("noPage") != null && params.get("noPage").equals("1")){
            params.remove("page");
            params.remove("rows");
            params.remove("offset");
        }
        List<Map> marketingOrderList = cardCouponsMapper.listMarketingOrder(params);
        long count = cardCouponsMapper.countMarketingOrder(params);
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("rows", marketingOrderList);
        resultMap.put("total", count);
        apiRest.setIsSuccess(true);
        apiRest.setMessage("查询礼品活动成功！");
        apiRest.setData(resultMap);
        return apiRest;
    }

    public ApiRest orderDetail(Map<String, String> params){
        ApiRest apiRest = new ApiRest();
        Integer page = 1;
        Integer rows = 20;
        if(StringUtils.isNotEmpty(params.get("page"))){
            page = Integer.valueOf(params.get("page"));
        }
        if(StringUtils.isNotEmpty(params.get("rows"))){
            rows = Integer.valueOf(params.get("rows"));
        }
        Integer offset = (page - 1) * rows;
        params.put("separator","<br>");
        params.put("offset", offset.toString());
        if(params.get("noPage") != null && params.get("noPage").equals("1")){
            params.remove("page");
            params.remove("rows");
            params.remove("offset");
        }
        Map<String, Object> resultMap = new HashMap<>();
        List<Map> list = cardCouponsMapper.orderDetail(params);
        List<Map> footer = cardCouponsMapper.countOrderDetail(params);
        Map foot = footer.get(0);
        resultMap.put("rows", list);
        resultMap.put("total", foot.get("total"));
        resultMap.put("footer", footer);
        apiRest.setIsSuccess(true);
        apiRest.setData(resultMap);
        apiRest.setMessage("查询群发明细成功！");
        return apiRest;
    }

    public ApiRest cardCouponsAccount(Map<String,String> params){
        ApiRest apiRest=new ApiRest();
        String acn=params.get("acn");
        Integer page = 1;
        Integer rows = 20;
        if(StringUtils.isNotEmpty(params.get("page"))){
            page = Integer.valueOf(params.get("page"));
        }
        if(StringUtils.isNotEmpty(params.get("rows"))){
            rows = Integer.valueOf(params.get("rows"));
        }
        Integer offset = (page - 1) * rows;
        params.put("offset", offset.toString());
        Map<String, Object> resultMap = new HashMap<>();
        List<Map> list = new ArrayList<>();
        long total=0;
        if(params.get("noPage") != null && params.get("noPage").equals("1")){
            params.remove("page");
            params.remove("rows");
            params.remove("offset");
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

        if(acn.equals("couponsGet")){
            params.put("type","1");
            list=cardCouponsMapper.cardCouponsBook(params);
            total=cardCouponsMapper.countCardCouponsBook(params);
        }else if(acn.equals("couponsPay")){
            params.put("type","2");
            list=cardCouponsMapper.cardCouponsBook(params);
            total=cardCouponsMapper.countCardCouponsBook(params);
        }else if(acn.equals("dayDetail")){
            params.put("detail","1");
            list=cardCouponsMapper.couponsAccount(params);
            total=cardCouponsMapper.countCouponsAccount(params);
        }else if(acn.equals("daySum")){
            params.put("detail","2");
            list=cardCouponsMapper.couponsAccount(params);
            total=cardCouponsMapper.countCouponsAccount(params);
        }else if(acn.equals("monthDetail")){
            params.put("detail","3");
            list=cardCouponsMapper.couponsAccount(params);
            total=cardCouponsMapper.countCouponsAccount(params);
        }else if(acn.equals("monthSum")){
            params.put("detail","4");
            list=cardCouponsMapper.couponsAccount(params);
            total=cardCouponsMapper.countCouponsAccount(params);
        }else if(acn.equals("branchDetail")){
            params.put("detail","5");
            list=cardCouponsMapper.couponsAccount(params);
            total=cardCouponsMapper.countCouponsAccount(params);
        }else if(acn.equals("branchSum")){
            params.put("detail","6");
            list=cardCouponsMapper.couponsAccount(params);
            total=cardCouponsMapper.countCouponsAccount(params);
        }
        resultMap.put("rows", list);
        resultMap.put("total", total);
        apiRest.setIsSuccess(true);
        apiRest.setData(resultMap);
        apiRest.setMessage("查询报表成功！");
        return apiRest;
    }

    public List<String> cardCodeArray(String prefix,Integer size,Integer length){
        List<String> resultList=new ArrayList<>();
        String result=serialNumberCreatorMapper.getNextVal(prefix,size);
        if(StringUtils.isNotEmpty(result)){
            Integer begin=Integer.valueOf(result.split(",")[0]);
            Integer end=Integer.valueOf(result.split(",")[1]);
            if(begin<=end){
                for(int i=begin;i<=end;i++){
                    String code=SerialNumberGenerate.generateCode(length,i);
                    resultList.add(code);
                }
            }
        }
        return resultList;
    }
}
