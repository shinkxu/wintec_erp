package erp.chain.service.o2o;

import com.saas.common.ResultJSON;
import com.saas.common.exception.ServiceException;
import com.saas.common.util.LogUtil;
import erp.chain.domain.Card;
import erp.chain.domain.CardAccount;
import erp.chain.domain.CardBook;
import erp.chain.domain.CardType;
import erp.chain.domain.o2o.*;
import erp.chain.domain.o2o.vo.CardTypeVo;
import erp.chain.mapper.CardMapper;
import erp.chain.mapper.o2o.VipBookMapper;
import erp.chain.mapper.o2o.VipMapper;
import erp.chain.mapper.o2o.VipTypeMapper;
import erp.chain.mapper.report.ReportMapper;
import erp.chain.service.system.SmsService;
import erp.chain.utils.CommonUtils;
import erp.chain.utils.GsonUntil;
import erp.chain.utils.WeiXinUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import saas.api.ProxyApi;
import saas.api.common.ApiRest;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 卡业务类
 * Created by wangms on 2017/9/26.
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class CardService{
    @Autowired
    private CardMapper cardMapper;
    @Autowired
    private VipTypeMapper vipTypeMapper;
    @Autowired
    private VipMapper vipMapper;
    @Autowired
    private VipBookMapper vipBookMapper;
    @Autowired
    private DietPromotionService dietPromotionService;
    @Autowired
    private SmsService smsService;
    @Autowired
    private CardCouponsService cardCouponsService;
    @Autowired
    private ReportMapper reportMapper;

    /**
     * 新增或修改卡方案
     *
     * @return
     * @throws ServiceException
     */
    public ApiRest addOrUpdateCardType(Map params) throws ServiceException, ParseException{
        ApiRest rest = new ApiRest();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        CardType cardType = new CardType();
        //id不为空为修改流程
        if(params.get("id") != null && !"".equals(params.get("id"))){
            Map<String, Object> param1 = new HashMap<>();
            param1.put("tenantId", params.get("tenantId"));
            param1.put("id", params.get("id"));
            cardType = cardMapper.findCardTypeById(param1);
            if(cardType == null){
                rest.setIsSuccess(false);
                String str = "";
                if("1".equals(params.get("cardKind")) || "2".equals(params.get("cardKind"))){
                    str += "预付卡方案";
                }
                else if("3".equals(params.get("cardKind"))){
                    str += "次卡方案";
                }
                str += "不存在或已删除!";
                rest.setMessage(str);
                return rest;
            }
        }
        else{
            //新增流程
            cardType.setTenantId(new BigInteger(params.get("tenantId").toString()));
            cardType.setBranchId(new BigInteger(params.get("branchId").toString()));
            Map<String, Object> param = new HashMap<>();
            param.put("tenantId", params.get("tenantId"));
            param.put("branchId", params.get("branchId"));
            List<CardType> list = cardMapper.listCardTypes(param);
            int typeCodeInt = 0;
            if(null != list && list.size() > 0){
                for(CardType ct : list){
                    if(Integer.parseInt(ct.getTypeCode()) > typeCodeInt){
                        typeCodeInt = Integer.parseInt(ct.getTypeCode());
                    }
                }
            }
            cardType.setTypeCode(String.format("%04d", typeCodeInt + 1));
            cardType.setCreateAt(new Date());
            if(params.get("createBy") != null){
                cardType.setCreateBy(params.get("createBy").toString());
            }
        }

        if(params.get("typeName") != null){
            cardType.setTypeName(params.get("typeName").toString());
        }
        if(params.get("cardKind") != null){
            cardType.setCardKind(Integer.valueOf(params.get("cardKind").toString()));
        }
        if(params.get("deposit") != null){
            cardType.setDeposit(new BigDecimal(params.get("deposit").toString()));
        }
        if(params.get("giftLimit") != null){
            cardType.setGiftLimit(new BigDecimal(params.get("giftLimit").toString()));
        }
        if(params.get("giftAmount") != null){
            cardType.setGiftAmount(new BigDecimal(params.get("giftAmount").toString()));
        }
        if(params.get("giftTimes") != null){
            cardType.setGiftTimes(new BigInteger(params.get("giftTimes").toString()));
        }
        //次卡
        if(params.get("tmGoodsId") != null){
            cardType.setTmGoodsId(new BigInteger(params.get("tmGoodsId").toString()));
        }
        if(params.get("tmGoodsName") != null){
            cardType.setTmGoodsName(params.get("tmGoodsName").toString());
        }
        if(params.get("tmTotalTimes") != null){
            cardType.setTmTotalTimes(new BigInteger(params.get("tmTotalTimes").toString()));
        }
        if(params.get("tmPrice") != null){
            cardType.setTmPrice(new BigDecimal(params.get("tmPrice").toString()));
        }
        if(params.get("tmIntervalType") != null){
            cardType.setTmIntervalType(new Integer(params.get("tmIntervalType").toString()));
        }
        if(cardType.getTmIntervalType() != null && cardType.getTmIntervalType() != 3){
            cardType.setStartTime(null);
            cardType.setEndTime(null);
        }
        cardType.setTmInterval(params.get("tmInterval") == null ? null : new BigInteger(params.get("tmInterval").toString()));

        if("1".equals(params.get("allowRefund"))){
            cardType.setAllowRefund(true);
        }
        else{
            cardType.setAllowRefund(false);
        }
        if(params.get("startTime") != null){
            cardType.setStartTime(format.parse(params.get("startTime").toString() + " 00:00:00"));
        }
        if(params.get("endTime") != null){
            cardType.setEndTime(format.parse(params.get("endTime").toString() + " 23:59:59"));
        }
        cardType.setLastUpdateAt(new Date());
        cardType.setUseRuleDay(params.get("useRuleDay") == null ? null : Integer.parseInt(params.get("useRuleDay").toString()));
        cardType.setUseRuleTime(params.get("useRuleDay") == null ? null : Integer.parseInt(params.get("useRuleTime").toString()));
        if("1".equals(params.get("isDeleted"))){
            Map map = new HashMap();
            map.put("tenantId", cardType.getTenantId());
            map.put("id", cardType.getId());
            map.put("cardKind", cardType.getCardKind());
            int count = cardMapper.cardIsUsed(map);
            if(count > 0){
                rest.setIsSuccess(false);
                rest.setMessage(cardType.getCardKind() == 3 ? "次卡方案正在使用中，无法删除" : "面值卡方案正在使用中，无法删除");
                return rest;
            }
            cardType.setIsDeleted(true);
        }
        if(cardType.getTmIntervalType() != null && (cardType.getTmIntervalType() == 1 || cardType.getTmIntervalType() == 2)){
            cardType.setStartTime(null);
            cardType.setEndTime(null);
        }

        //面值卡
        if(params.get("denomination") != null){
            cardType.setDenomination(new BigDecimal(params.get("denomination").toString()));
        }

        if(params.get("id") != null && !"".equals(params.get("id"))){
            int flag = cardMapper.updateCardType(cardType);
            if(flag == 1){
                rest.setIsSuccess(true);
                if("1".equals(params.get("isDeleted"))){
                    rest.setMessage(getMessage(params.get("cardKind").toString(), 3, true));
                }
                else{
                    rest.setMessage(getMessage(params.get("cardKind").toString(), 2, true));
                }
                rest.setData(cardType);
            }
            else{
                if("1".equals(params.get("isDeleted"))){
                    rest.setMessage(getMessage(params.get("cardKind").toString(), 3, false));
                }
                else{
                    rest.setMessage(getMessage(params.get("cardKind").toString(), 2, false));
                }
                rest.setIsSuccess(true);
            }
        }
        else{
            int flag = cardMapper.insertCardType(cardType);
            if(flag == 1){
                rest.setIsSuccess(true);
                rest.setMessage(getMessage(params.get("cardKind").toString(), 1, true));
                rest.setData(cardType);
            }
            else{
                rest.setIsSuccess(true);
                rest.setMessage(getMessage(params.get("cardKind").toString(), 1, false));
            }

        }
        return rest;
    }

    private String getMessage(String cardKind, int type, boolean success){
        String str = "";
        if(type == 1){
            str += "新增";
        }
        else if(type == 2){
            str += "修改";
        }
        else if(type == 3){
            str += "删除";
        }
        if("1".equals(cardKind)){
            str += "预付卡方案";
        }
        else if("3".equals(cardKind)){
            str += "次卡方案";
        }
        if(success){
            str += "成功！";
        }
        else{
            str += "失败!";
        }

        return str;
    }


    /**
     * 查询卡方案列表
     *
     * @return
     * @throws ServiceException
     */
    public ResultJSON listCardTypes(Map params) throws ServiceException{
        ResultJSON rest = new ResultJSON();
        List<CardType> countlist = cardMapper.listCardTypes(params);
        if(params.get("page") != null && params.get("rows") != null){
            params.put("offset", (Integer.parseInt(params.get("page").toString()) - 1) * Integer.parseInt(params.get("rows").toString()));
        }
        List<CardType> list = cardMapper.listCardTypes(params);
        Map<String, Object> map = new HashMap<>();
        map.put("total", countlist.size());
        map.put("rows", list);
        rest.setIsSuccess(true);
        rest.setMsg("查询卡方案成功!");
        rest.setJsonMap(map);
        return rest;
    }

    /**
     * 预付卡开卡、制卡
     *
     * @return
     * @throws ServiceException
     */
    public ApiRest createCardAccount(Map params) throws ServiceException{
        ApiRest rest = new ApiRest();
        Map<String, Object> param = new HashMap<>();
        param.put("tenantId", params.get("tenantId"));
        param.put("cardCode", params.get("cardCode"));
        Card card = cardMapper.findByCodeAndTenantId(param);

        if(card != null){
            CardAccount account = new CardAccount();
            account.setTenantId(new BigInteger(params.get("tenantId").toString()));
            account.setBranchId(new BigInteger(params.get("branchId").toString()));
            account.setCardId(card.getId());
            account.setTypeId(new BigInteger(params.get("cardTypeId").toString()));
            account.setCardCode(card.getCode());
            if(params.get("vipStore") != null){
                account.setVipStore(new BigDecimal(params.get("vipStore").toString()));
                account.setVipStoreTotal(new BigDecimal(params.get("vipStore").toString()));
            }
            else{
                account.setVipStore(BigDecimal.ZERO);
                account.setVipStoreTotal(BigDecimal.ZERO);
            }
            if(params.get("vipGiftTotal") != null){
                account.setVipGiftTotal(new BigDecimal(params.get("vipGiftTotal").toString()));
                account.setVipStore(account.getVipStore().add(new BigDecimal(params.get("vipGiftTotal").toString())));
            }
            else{
                account.setVipGiftTotal(BigDecimal.ZERO);
            }
            if(params.get("phone") != null){
                account.setPhone(params.get("phone").toString());
            }
            if(params.get("memo") != null){
                account.setMemo(params.get("memo").toString());
            }
            account.setStatus(0);
            if(params.get("createBy") != null){
                account.setCreateBy(params.get("createBy").toString());
                account.setLastUpdateBy(params.get("createBy").toString());
            }
            account.setCreateAt(new Date());
            account.setLastUpdateAt(new Date());
            if(params.get("cardKind") != null){
                account.setCardKind(new Integer(params.get("cardKind").toString()));
            }
            int flag = cardMapper.insertCardAccount(account);
            if(flag == 1){
                card.setHolderId(account.getId());
                card.setLastUpdateAt(new Date());
                cardMapper.updateCard(card);

                rest.setIsSuccess(true);
                rest.setMessage("创建卡账户成功!");
                rest.setData(account);
            }
            else{
                rest.setIsSuccess(true);
                rest.setMessage("创建卡账户失败!");
            }
        }

        return rest;
    }


    /**
     * 查询卡账户
     *
     * @return
     * @throws ServiceException
     */
    public ApiRest listCardAccounts(Map params) throws ServiceException{
        ApiRest rest = new ApiRest();
        List<CardAccount> countlist = cardMapper.listCardAccounts(params);
        if(params.get("page") != null && params.get("rows") != null){
            params.put("offset", (Integer.parseInt(params.get("page").toString()) - 1) * Integer.parseInt(params.get("rows").toString()));
        }
        List<CardAccount> list = cardMapper.listCardAccounts(params);
        Map<String, Object> map = new HashMap<>();
        map.put("total", countlist.size());
        map.put("rows", list);
        rest.setIsSuccess(true);
        rest.setMessage("查询卡账户列表成功!");
        rest.setData(map);
        return rest;
    }


    /**
     * 查询卡账户
     *
     * @return
     * @throws ServiceException
     */
    public ResultJSON listCardAccounts1(Map params) throws ServiceException{
        ResultJSON rest = new ResultJSON();
        List<CardAccount> countlist = cardMapper.listCardAccounts(params);
        if(params.get("page") != null && params.get("rows") != null){
            params.put("offset", (Integer.parseInt(params.get("page").toString()) - 1) * Integer.parseInt(params.get("rows").toString()));
        }
        List<CardAccount> list = cardMapper.listCardAccounts(params);
        Map<String, Object> map = new HashMap<>();
        map.put("total", countlist.size());
        map.put("rows", list);
        rest.setIsSuccess(true);
        rest.setMsg("查询卡账户成功!");
        rest.setJsonMap(map);
        return rest;
    }

    /**
     * 查询卡方案列表
     *
     * @return
     * @throws ServiceException
     */
    public ApiRest listCardBook(Map params) throws ServiceException{
        ApiRest rest = new ApiRest();
        List<Map> countlist = cardMapper.listCardBookVo(params);
        if(params.get("page") != null && params.get("rows") != null){
            params.put("offset", (Integer.parseInt(params.get("page").toString()) - 1) * Integer.parseInt(params.get("rows").toString()));
        }
        if(params.get("field") != null && !params.get("field").equals("") && params.get("order") != null && !params.get("order").equals("")){
            params.put("field", CommonUtils.formatOrderField(params.get("field").toString()));
            params.put("order", params.get("order").toString());
        }
        if(params.get("parentId") != null){
            params.remove("branchId");
        }
        List<Map> list = cardMapper.listCardBookVo(params);
        List<Map> footer = cardMapper.listCardBookVoSum(params);
        Map<String, Object> map = new HashMap<>();
        map.put("total", countlist.size());
        map.put("rows", list);
        map.put("footer", footer);
        rest.setIsSuccess(true);
        rest.setMessage("查询卡方案成功!");
        rest.setData(map);
        return rest;
    }

    /**
     * 卡充值
     *
     * @return
     * @throws ServiceException
     */
    public ApiRest storeForCard(Map params) throws ServiceException{
        ApiRest rest = new ApiRest();
        Map<String, Object> param = new HashMap<>();
        param.put("tenantId", params.get("tenantId"));
        param.put("cardCode", params.get("cardCode"));
        List<CardAccount> accounts = cardMapper.listCardAccounts(param);
        if(accounts != null && accounts.size() > 0){
            CardAccount account = accounts.get(0);
            if(params.get("amount") != null && !"".equals(params.get("amount"))){
                param.put("id", account.getTypeId());
                CardType cardType = cardMapper.findCardTypeById(param);
                CardBook cardBook = new CardBook();
                cardBook.setVipStore(account.getVipStore());
                if(cardType != null){
                    //查询是否满足优惠条件
                    if(cardType.getGiftAmount() != null && cardType.getGiftLimit() != null
                            && new BigDecimal(params.get("amount").toString()).doubleValue() >= cardType.getGiftLimit().doubleValue()){

                        //如果是仅首次优惠
                        if(cardType.getGiftTimes().intValue() == 1 && account.getVipStoreTotal().doubleValue() >= 0){
                            account.setVipStore(account.getVipStore().add(new BigDecimal(params.get("amount").toString())));
                            account.setVipStoreTotal(account.getVipStoreTotal().add(new BigDecimal(params.get("amount").toString())));
                        }
                        else{
                            account.setVipStore(account.getVipStore().add(new BigDecimal(params.get("amount").toString())).add(cardType.getGiftAmount()));
                            account.setVipStoreTotal(account.getVipStoreTotal().add(new BigDecimal(params.get("amount").toString())).add(cardType.getGiftAmount()));
                            account.setVipGiftTotal(account.getVipGiftTotal().add(cardType.getGiftAmount()));
                            cardBook.setGiftAmount(cardType.getGiftAmount());
                        }
                    }
                    else{
                        account.setVipStore(account.getVipStore().add(new BigDecimal(params.get("amount").toString())));
                        account.setVipStoreTotal(account.getVipStoreTotal().add(new BigDecimal(params.get("amount").toString())));
                    }

                    account.setLastUpdateAt(new Date());

                    cardBook.setTenantId(new BigInteger(params.get("tenantId").toString()));
                    if(params.get("branchId") != null){
                        cardBook.setBranchId(new BigInteger(params.get("branchId").toString()));
                    }
                    else{
                        cardBook.setBranchId(account.getBranchId());
                    }
                    if(params.get("cardType") != null){
                        cardBook.setCardType(params.get("cardType").toString());
                    }
                    //操作类型：1制卡2充值3退款4退款扣除5回收5消费6消费退款
                    cardBook.setOperateType("2");
                    cardBook.setPayAmount(new BigDecimal(params.get("amount").toString()));
                    cardBook.setVipStoreAfter(account.getVipStore());
                    if(params.get("remark") != null){
                        cardBook.setRemark(params.get("remark").toString());
                    }
                    if(params.get("createby") != null){
                        cardBook.setCreateBy(params.get("createby").toString());
                        cardBook.setLastUpdateBy(params.get("createby").toString());
                    }
                    cardBook.setCreateAt(new Date());
                    cardBook.setLastUpdateAt(new Date());
                    cardBook.setHolderId(account.getVipId());
                    cardMapper.updateCardAccount(account);
                    cardMapper.insertCardBook(cardBook);

                    rest.setIsSuccess(true);
                    rest.setMessage("充值成功!");
                    rest.setData(account);
                }
            }
            else{
                rest.setIsSuccess(false);
                rest.setMessage("充值金额不能为空!");
            }
        }
        else{
            rest.setIsSuccess(false);
            rest.setMessage("卡号不存在或已停用!");
        }
        return rest;
    }


    /**
     * 预付卡卡退卡
     *
     * @return
     * @throws ServiceException
     */
    public ApiRest refundOtherCard(Map params) throws ServiceException{
        ApiRest rest = new ApiRest();
        if(params.get("cardCode") != null && params.get("tenantId") != null){
            Map<String, Object> map = new HashMap<>();
            map.put("tenantId", params.get("tenantId"));
            map.put("code", params.get("cardCode"));
            Card card = cardMapper.findByCodeAndTenantId(map);
            if(card != null && card.getHolderId() != null && card.getType() == 4 && card.getState() == 1){
                CardBook cardBook = new CardBook();
                Map<String, Object> map1 = new HashMap<>();
                map1.put("tenantId", params.get("tenantId"));
                map1.put("id", card.getHolderId());
                CardAccount account = cardMapper.findCardAccountById(map1);
                if(account != null){
                    Map<String, Object> map2 = new HashMap<>();
                    map2.put("tenantId", params.get("tenantId"));
                    map2.put("id", account.getTypeId());
                    CardType cardType = cardMapper.findCardTypeById(map2);
                    if(cardType != null && cardType.isAllowRefund()){
                        account.setLastUpdateAt(new Date());
                        if(params.get("createBy") != null){
                            account.setLastUpdateBy(params.get("createBy").toString());
                            card.setLastUpdateBy(params.get("createBy").toString());
                            cardBook.setCreateBy(params.get("createBy").toString());
                            cardBook.setLastUpdateBy(params.get("createBy").toString());
                        }

                        cardMapper.updateCardAccount(account);

                        card.setLastUpdateAt(new Date());
                        card.setState(0);
                        card.setDeleted(true);
                        cardMapper.updateCard(card);
                        cardBook.setTenantId(new BigInteger(params.get("tenantId").toString()));
                        if(params.get("branchId") != null){
                            cardBook.setBranchId(new BigInteger(params.get("branchId").toString()));
                        }
                        else{
                            cardBook.setBranchId(account.getBranchId());
                        }
                        cardBook.setCardType(card.getType().toString());
                        //操作类型：1制卡2充值3退款4退款扣除5回收5消费6消费退款
                        cardBook.setOperateType("3");
                        cardBook.setVipStore(account.getVipStore());
                        cardBook.setVipStoreAfter(BigDecimal.ZERO);
                        if(params.get("remark") != null){
                            cardBook.setRemark(params.get("remark").toString());
                        }
                        cardBook.setLastUpdateAt(new Date());
                        cardBook.setCreateAt(new Date());
                        cardBook.setHolderId(account.getVipId());
                        cardBook.setIsDeleted(false);
                        cardBook.setTypeId(cardType.getId());
                        if(params.get("inputAmount") != null){
                            cardBook.setInputAmount(new BigDecimal(params.get("inputAmount").toString()));
                        }

                        cardMapper.insertCardBook(cardBook);
                        rest.setIsSuccess(true);
                        rest.setMessage("会员卡退卡成功!");
                        rest.setData(account);
                    }
                    else{
                        rest.setIsSuccess(false);
                        rest.setMessage("该会员卡不允许退卡!");
                    }
                }
            }
            else{
                rest.setIsSuccess(false);
                rest.setMessage("会员卡不存在或已停用!");
            }
        }
        else{
            rest.setIsSuccess(false);
            rest.setMessage("参数不能为null!");
        }
        return rest;
    }


    /**
     * 次卡查询
     *
     * @return
     * @throws ServiceException
     */
    public ApiRest findTimesCardByVipId(Map params) throws ServiceException{
        ApiRest rest = new ApiRest();
        List<CardAccount> countlist = cardMapper.listCardAccounts(params);
        if(params.get("page") != null && params.get("rows") != null){
            params.put("offset", (Integer.parseInt(params.get("page").toString()) - 1) * Integer.parseInt(params.get("rows").toString()));
        }
        List<CardAccount> list = cardMapper.listCardAccounts(params);
        Map<String, Object> map = new HashMap<>();
        map.put("total", countlist.size());
        map.put("rows", list);
        rest.setIsSuccess(true);
        rest.setMessage("查询卡方案成功!");
        rest.setData(map);
        return rest;
    }


    /**
     * 次卡统计数据查询
     *
     * @return
     * @throws ServiceException
     */
    public ResultJSON qryTimesCardData(Map params) throws ServiceException{
        ResultJSON rest = new ResultJSON();
        params.put("cardKind", 3);
        List<CardTypeVo> countlist = cardMapper.listCardTypesVo(params);
        if(params.get("page") != null && params.get("rows") != null){
            params.put("offset", (Integer.parseInt(params.get("page").toString()) - 1) * Integer.parseInt(params.get("rows").toString()));
        }
        List<CardTypeVo> list = cardMapper.listCardTypesVo(params);
        if(list != null && list.size() > 0){
            for(CardTypeVo cardTypeVo : list){
                //统计余额
                Map<String, Object> map = new HashMap<>();
                map.put("tenantId", params.get("tenantId"));
                map.put("typeId", cardTypeVo.getId());
                map.put("cardKind", 3);
                List<CardAccount> accounts = cardMapper.listCardAccounts(map);
                int timesCount = 0;
                int cardCount = 0;
                double storeTotal = 0;
                for(CardAccount account : accounts){
                    if(account.getTmRemainTimes() != null && account.getTmRemainTimes().intValue() > 0 && account.getTmPrice() != null){
                        cardCount++;
                        timesCount += account.getTmRemainTimes().intValue();
                        storeTotal += account.getTmPrice().doubleValue() * account.getTmRemainTimes().intValue() / account.getTmTotalTimes().intValue();
                    }
                }
                cardTypeVo.setRemainCardsCount(new BigInteger(String.valueOf(cardCount)));
                cardTypeVo.setRemainTimesCount(new BigInteger(String.valueOf(timesCount)));
                cardTypeVo.setRemainStoreTotal(new BigDecimal(String.valueOf(storeTotal)));

                //统计充值、退卡、消费
                int buyTimesCount = 0;
                int buyCardCount = 0;
                double buyStoreTotal = 0;
                int useTimesCount = 0;
                int useCardCount = 0;
                double useStoreTotal = 0;
                int backTimesCount = 0;
                int backCardCount = 0;
                double backStoreTotal = 0;
                map.put("cardType", 7);
                map.put("startTime", params.get("startTime"));
                map.put("endTime", params.get("endTime"));
                List<CardBook> books = cardMapper.listCardBook(map);
                for(CardBook book : books){
                    //充值
                    if("2".equals(book.getOperateType()) && book.getTypeId().equals(cardTypeVo.getId())){
                        buyCardCount++;
                        buyTimesCount += book.getTmRemainTimes().intValue();
                        buyStoreTotal += cardTypeVo.getTmPrice().doubleValue() * book.getTmRemainTimes().intValue() / cardTypeVo.getTmTotalTimes().intValue();
                    }
                    //消费
                    if("5".equals(book.getOperateType()) && book.getTypeId().equals(cardTypeVo.getId())){
                        useCardCount++;
                        useTimesCount += book.getChangeTimes().intValue();
                        useStoreTotal += cardTypeVo.getTmPrice().doubleValue() * book.getChangeTimes().intValue() / cardTypeVo.getTmTotalTimes().intValue();
                    }

                    //退卡
                    if("3".equals(book.getOperateType()) && book.getTypeId().equals(cardTypeVo.getId())){
                        backCardCount++;
                        backTimesCount += book.getChangeTimes().intValue();
                        backStoreTotal += cardTypeVo.getTmPrice().doubleValue() * book.getChangeTimes().intValue() / cardTypeVo.getTmTotalTimes().intValue();
                    }
                }
                cardTypeVo.setBuyCardsCount(new BigInteger(String.valueOf(buyCardCount)));
                cardTypeVo.setBuyTimesCount(new BigInteger(String.valueOf(buyTimesCount)));
                cardTypeVo.setBuyStoreTotal(new BigDecimal(String.valueOf(buyStoreTotal)));

                cardTypeVo.setUseCardsCount(new BigInteger(String.valueOf(useCardCount)));
                cardTypeVo.setUseTimesCount(new BigInteger(String.valueOf(useTimesCount)));
                cardTypeVo.setUseStoreTotal(new BigDecimal(String.valueOf(useStoreTotal)));

                cardTypeVo.setBackCardsCount(new BigInteger(String.valueOf(backCardCount)));
                cardTypeVo.setBackTimesCount(new BigInteger(String.valueOf(backTimesCount)));
                cardTypeVo.setBackStoreTotal(new BigDecimal(String.valueOf(backStoreTotal)));

            }
        }
        Map<String, Object> map = new HashMap<>();
        map.put("total", countlist.size());
        map.put("rows", list);
        rest.setIsSuccess(true);
        rest.setMsg("查询卡方案成功!");
        rest.setJsonMap(map);
        return rest;
    }


    /**
     * 次卡充值（购买次卡）
     *
     * @return
     * @throws ServiceException
     */
    public ApiRest createTimesCard(Map params) throws ServiceException{
        ApiRest rest = new ApiRest();
        Card card = null;
        Map<String, Object> param = new HashMap<>();
        param.put("tenantId", params.get("tenantId"));
        if(params.get("cardCode") != null && !"".equals(params.get("cardCode"))){
            param.put("cardCode", params.get("cardCode"));
            card = cardMapper.findByCodeAndTenantId(param);
        }
        param.put("id", params.get("typeId"));
        CardType cardType = cardMapper.findCardTypeById(param);
        if(cardType != null){
            CardAccount account = new CardAccount();
            if(card != null){
                account.setCardId(card.getId());
                account.setCardCode(card.getCode());
            }
            else{
                account.setCardId(new BigInteger("-1"));
            }
            account.setTenantId(new BigInteger(params.get("tenantId").toString()));
            account.setBranchId(new BigInteger(params.get("branchId").toString()));
            account.setTypeId(cardType.getId());
            account.setVipId(new BigInteger(params.get("vipId").toString()));
            account.setTypeName(cardType.getTypeName());
            account.setTmTotalTimes(cardType.getTmTotalTimes());
            if(params.get("vipStore") != null){
                account.setVipStore(new BigDecimal(params.get("vipStore").toString()));
                account.setVipStoreTotal(new BigDecimal(params.get("vipStore").toString()));
            }
            else{
                account.setVipStore(BigDecimal.ZERO);
                account.setVipStoreTotal(BigDecimal.ZERO);
            }
            if(params.get("vipGiftTotal") != null){
                account.setVipGiftTotal(new BigDecimal(params.get("vipGiftTotal").toString()));
                account.setVipStore(account.getVipStore().add(new BigDecimal(params.get("vipGiftTotal").toString())));
            }
            else{
                account.setVipGiftTotal(BigDecimal.ZERO);
            }
            if(params.get("phone") != null){
                account.setPhone(params.get("phone").toString());
            }
            if(params.get("memo") != null){
                account.setMemo(params.get("memo").toString());
            }
            account.setStatus(0);
            if(params.get("createBy") != null){
                account.setCreateBy(params.get("createBy").toString());
                account.setLastUpdateBy(params.get("createBy").toString());
            }
            account.setCreateAt(new Date());
            account.setLastUpdateAt(new Date());
            if(params.get("cardKind") != null){
                account.setCardKind(new Integer(params.get("cardKind").toString()));
            }

            //次卡相关
            account.setTmGoodsId(cardType.getTmGoodsId());
            account.setTmGoodsName(cardType.getTmGoodsName());
            if(cardType.getTmInterval() != null){
                account.setTmInterval(cardType.getTmInterval());
            }
            account.setTmIntervalType(cardType.getTmIntervalType());
            account.setTmPrice(cardType.getTmPrice());
            account.setTmRemainTimes(cardType.getTmTotalTimes());
            if(cardType.getStartTime() != null){
                account.setStartTime(cardType.getStartTime());
            }
            if(cardType.getEndTime() != null){
                account.setEndTime(cardType.getEndTime());
            }
            int flag = cardMapper.insertCardAccount(account);

            //记录
            CardBook cardBook = new CardBook();
            cardBook.setOperateType("2");
            cardBook.setTmRemainTimes(account.getTmRemainTimes());
            cardBook.setChangeTimes(BigInteger.ZERO);
            if(params.get("createBy") != null){
                cardBook.setCreateBy(params.get("createBy").toString());
                cardBook.setLastUpdateBy(params.get("createBy").toString());
            }
            cardBook.setTenantId(new BigInteger(params.get("tenantId").toString()));
            if(params.get("branchId") != null){
                cardBook.setBranchId(new BigInteger(params.get("branchId").toString()));
            }
            else{
                cardBook.setBranchId(account.getBranchId());
            }
            // cardType 7次卡
            cardBook.setCardType("7");
            if(params.get("remark") != null){
                cardBook.setRemark(params.get("remark").toString());
            }
            cardBook.setLastUpdateAt(new Date());
            cardBook.setCreateAt(new Date());
            cardBook.setHolderId(account.getVipId());
            cardBook.setIsDeleted(false);
            cardBook.setTypeId(cardType.getId());
            cardMapper.insertCardBook(cardBook);

            if(flag == 1){
                if(card != null){
                    card.setHolderId(account.getId());
                    card.setLastUpdateAt(new Date());
                    cardMapper.updateCard(card);
                }
                rest.setIsSuccess(true);
                rest.setMessage("购买次卡成功!");
                rest.setData(account);
            }
            else{
                rest.setIsSuccess(true);
                rest.setMessage("购买次卡失败!");
            }
        }
        else{
            rest.setIsSuccess(false);
            rest.setMessage("次卡方案不存在！");
        }

        return rest;
    }

    /**
     * 修改卡账户（次卡使用，次卡退卡等）
     *
     * @return
     * @throws ServiceException
     */
    @Transactional(rollbackFor = Exception.class)
    public ApiRest updateCardAccount(Map params) throws ServiceException, ParseException{
        ApiRest rest = new ApiRest();
        List<Map> list = GsonUntil.jsonAsList(params.get("data").toString(), Map.class);
        if(list != null && list.size() > 0){
            for(Map m : list){
                Map<String, Object> param = new HashMap<>();
                param.put("tenantId", params.get("tenantId"));
                param.put("id", m.get("accountId"));
                CardAccount account = cardMapper.findCardAccountById(param);
                CardBook cardBook = new CardBook();
                if(account != null && account.getTypeId() != null && account.getCardKind() == 3){
                    param.put("id", account.getTypeId());
                    CardType cardType = cardMapper.findCardTypeById(param);
                    if(cardType != null){
                        //次卡退卡
                        if("1".equals(params.get("business"))){
                            account.setStatus(1);
                            //折算应退金额
                            double refundValue = account.getTmPrice().doubleValue() * (account.getTmRemainTimes().intValue() / cardType.getTmTotalTimes().intValue());
                            //操作类型：1制卡2充值3退款4退款扣除5回收5消费6消费退款
                            cardBook.setOperateType("3");
                            cardBook.setTmRemainTimes(account.getTmRemainTimes());
                            cardBook.setChangeTimes(account.getTmRemainTimes());
                            rest.setIsSuccess(true);
                            rest.setMessage("次卡退卡成功!");
                            rest.setData(refundValue);
                        }
                        else if("2".equals(params.get("business"))){ //次卡使用
                            if(m.get("amount") != null && !"".equals(m.get("amount"))){
                                /*if(cardType.getTmIntervalType()!=1){
                                    Date startDate=null;
                                    Date endDate=null;
                                    Date nowDate=new Date();
                                    SimpleDateFormat sd=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                    if(cardType.getTmIntervalType()==2){
                                        startDate=account.getCreateAt();
                                        String endDateStr = reportMapper.getDate(sd.format(startDate), cardType.getTmInterval().intValue());
                                        endDate=sd.parse(endDateStr);
                                    }else if(cardType.getTmIntervalType()==3){
                                        startDate=cardType.getStartTime();
                                        endDate=cardType.getEndTime();
                                    }
                                    if(startDate!=null&&endDate!=null){
                                        if(!(nowDate.before(endDate)&&nowDate.after(startDate))){
                                            rest.setIsSuccess(false);
                                            rest.setError("核销失败，您的卡未在有效用卡时间内");
                                            rest.setMessage("核销失败，您的卡未在有效用卡时间内");
                                            return rest;
                                        }
                                    }
                                }*/
                                int amount = Integer.parseInt(m.get("amount").toString());
                                if(cardType.getUseRuleDay() != null && cardType.getUseRuleTime() != null){
                                    Map map = new HashMap();
                                    map.put("tenantId", cardType.getTenantId());
                                    map.put("branchId", cardType.getBranchId());
                                    map.put("typeId", cardType.getId());
                                    map.put("holderId", account.getVipId());
                                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                                    Date nowDate = new Date();
                                    Calendar calendar = Calendar.getInstance();
                                    calendar.setTime(nowDate);
                                    //往前推cardType.getUseRuleDay()天
                                    calendar.set(Calendar.DATE, calendar.get(Calendar.DATE) - cardType.getUseRuleDay() + 1);
                                    Date updateDate = calendar.getTime();
                                    map.put("startDate", sdf.format(updateDate) + " 00:00:00");
                                    int usedTimes = cardMapper.queryUsedTimes(map);
                                    if((usedTimes+amount) > cardType.getUseRuleTime()){
                                        rest.setIsSuccess(false);
                                        rest.setError("核销失败，您的卡" + cardType.getUseRuleDay() + "天内最多消费" + cardType.getUseRuleTime() + "次");
                                        rest.setMessage("核销失败，您的卡" + cardType.getUseRuleDay() + "天内最多消费" + cardType.getUseRuleTime() + "次");
                                        return rest;
                                    }
                                }
                                if(account.getTmRemainTimes().intValue() >= amount){
                                    account.setTmRemainTimes(new BigInteger(String.valueOf(account.getTmRemainTimes().intValue() - amount)));
                                    cardBook.setOperateType("5");
                                    cardBook.setTmRemainTimes(account.getTmRemainTimes());
                                    cardBook.setChangeTimes(new BigInteger(String.valueOf(amount)));
                                }
                                else{
                                    //失败则抛异常回滚
                                    throw new ServiceException("次卡剩余次数不足!");
                                }
                            }
                        }else if("3".equals(params.get("business"))){ //次卡退款
                            int amount = Integer.parseInt(m.get("amount").toString());
                            account.setTmRemainTimes(new BigInteger(String.valueOf(account.getTmRemainTimes().intValue() + amount)));
                            cardBook.setOperateType("6");
                            cardBook.setTmRemainTimes(account.getTmRemainTimes());
                            cardBook.setChangeTimes(new BigInteger(String.valueOf(-amount)));
                        }
                    }
                    if(params.get("createBy") != null){
                        account.setLastUpdateBy(params.get("createBy").toString());
                        cardBook.setCreateBy(params.get("createBy").toString());
                        cardBook.setLastUpdateBy(params.get("createBy").toString());
                    }
                    cardMapper.updateCardAccount(account);
                    cardBook.setTenantId(new BigInteger(params.get("tenantId").toString()));
                    if(params.get("branchId") != null){
                        cardBook.setBranchId(new BigInteger(params.get("branchId").toString()));
                    }
                    else{
                        cardBook.setBranchId(account.getBranchId());
                    }
                    // cardType 7次卡
                    cardBook.setCardType("7");
                    cardBook.setVipStore(account.getVipStore());
                    cardBook.setVipStoreAfter(BigDecimal.ZERO);
                    if(params.get("remark") != null){
                        cardBook.setRemark(params.get("remark").toString());
                    }
                    cardBook.setLastUpdateAt(new Date());
                    cardBook.setCreateAt(new Date());
                    cardBook.setHolderId(account.getVipId());
                    cardBook.setIsDeleted(false);
                    cardBook.setTypeId(cardType.getId());
                    if(params.get("inputAmount") != null){
                        cardBook.setInputAmount(new BigDecimal(params.get("inputAmount").toString()));
                    }
                    cardMapper.insertCardBook(cardBook);
                }

                rest.setIsSuccess(true);
                rest.setMessage("处理成功!");
            }
        }
        return rest;
    }


    /**
     * 创建卡
     *
     * @return
     * @throws ServiceException
     */
    public ApiRest createCard(Map params) throws ServiceException{
        ApiRest rest = new ApiRest();
        rest.setIsSuccess(false);
        rest.setMessage("创建卡失败");
        Card card = new Card();
        card.setHolderId(new BigInteger(params.get("vipId").toString()));
        card.setDeleted(false);
        card.setBranchCode(params.get("branchCode").toString());
        card.setCode(params.get("code").toString());
        card.setState(1);
        card.setTenantCode(params.get("tenantCode").toString());
        card.setTenantId(new BigInteger(params.get("tenantId").toString()));
        card.setType(4);
        card.setCreateAt(new Date());
        card.setLastUpdateAt(new Date());
        if(params.get("createBy") != null){
            card.setCreateBy(params.get("createBy").toString());
        }
        int flag = cardMapper.insertCard(card);
        if(flag == 1){
            rest.setIsSuccess(true);
            rest.setMessage("创建卡成功");
        }
        return rest;
    }

    /**
     * 会员生日（定时任务）
     *
     * @return
     * @throws ServiceException
     */
    public ApiRest vipBirthdayThread(Map params) throws ServiceException, ParseException{
        ApiRest rest = new ApiRest();
        rest.setIsSuccess(false);
        Map<String, Object> map1 = new HashMap<>();
        map1.put("promotionType", 2);
        List<VipSpecialPromotion> specialPromotions = vipTypeMapper.findSpecialPromotions(map1);
        SimpleDateFormat format = new SimpleDateFormat("MM-dd");
        if(specialPromotions != null){
            for(VipSpecialPromotion promotion : specialPromotions){
                Map<String, Object> map2 = new HashMap<>();
                map2.put("tenantId", promotion.getTenantId());
                map2.put("status", 0);
                map2.put("branchId", promotion.getBranchId());
                map2.put("todayIsBirthday",format.format(new Date()));
                List<Vip> vips = vipMapper.select(map2);
                for(Vip vip : vips){
                    Map<String, Object> map5 = new HashMap<>();
                    map5.put("id", vip.getTypeId());
                    map5.put("tenantId", vip.getTenantId());
                    VipType vipType = vipTypeMapper.findByCondition(map5);

                    if(vipType != null && BigInteger.ONE.equals(vipType.getToSavePoints())){
                        //赠送积分
                        if(promotion.getAddScore() != null && promotion.getAddScore().compareTo(BigDecimal.ZERO) != 0){
                            dealScore(vip, promotion.getAddScore());
                            if(vip.getRemainingScore() != null){
                                vip.setRemainingScore(vip.getRemainingScore().add(promotion.getAddScore()));
                                vip.setSumScore(vip.getSumScore().add(promotion.getAddScore()));
                            }
                            else{
                                vip.setRemainingScore(promotion.getAddScore());
                                vip.setSumScore(promotion.getAddScore());
                            }
                            if(vip.getLargessscore() != null){
                                vip.setLargessscore(vip.getLargessscore().add(promotion.getAddScore()));
                            }
                            else{
                                vip.setLargessscore(promotion.getAddScore());
                            }
                            vipMapper.update(vip);
                            if(vip.getPhone() != null && !vip.getPhone().equals("")){
                                DecimalFormat sd = new DecimalFormat("#");
                                Map smsMap = new HashMap();
                                BigInteger branchId = params.get("branchId") == null ? vip.getBranchId() : new BigInteger(params.get("branchId").toString());
                                smsMap.put("type", 3);
                                smsMap.put("tenantId", vip.getTenantId().toString());
                                smsMap.put("number", vip.getPhone());
                                smsMap.put("branchId", branchId.toString());
                                smsMap.put("content", ("生日活动赠送###" + sd.format(promotion.getAddScore()) + "###" + sd.format(vip.getRemainingScore())));
                                ApiRest ssr = smsService.sendSMS(smsMap);
                                if(!ssr.getIsSuccess()){
                                    LogUtil.logInfo("发送消费短信失败！vipId=" + vip.getId());
                                }
                            }
                            WeiXinUtils.updateMemberBonusSafe(vip, "会员生日赠送积分！");
                            WeiXinUtils.sendPointChangeTemplateSafe(vip.getId(), promotion.getAddScore().intValue(), 1);
                        }
                        //赠送优惠券
                        if(StringUtils.isNotEmpty(promotion.getPrizeDietPromotionIds())){
                            Map<String, String> map3 = new HashMap<>();
                            map3.put("tenantId", vip.getTenantId().toString());
                            map3.put("cardId", promotion.getPrizeDietPromotionIds());
                            map3.put("vipId", vip.getId().toString());
                            map3.put("channel", "birthday");
                            map3.put("branchId", vip.getBranchId().toString());
                            cardCouponsService.sendCardToVip(map3);
                        }
                    }
                }
            }
        }
        rest.setIsSuccess(true);
        rest.setMessage("会员生日（定时任务）执行完毕。");
        return rest;
    }

    /**
     * 清空积分（定时任务）
     *
     * @return
     * @throws ServiceException
     */
    public ApiRest clearScoreThread(Map params) throws ServiceException{
        ApiRest rest = new ApiRest();
        rest.setIsSuccess(false);
        Map<String, String> map1 = new HashMap<>();
        map1.put("isClearScore", "1");
        Calendar calendar = Calendar.getInstance();
        if(calendar.get(Calendar.MONTH) == 0 && calendar.get(Calendar.DAY_OF_MONTH) == 1){
            ApiRest rest1 = ProxyApi.proxyPost("bs", "tenant", "findTenantList", map1);
            if(rest1.getIsSuccess()){
                List<Map> tenants = (List<Map>)((Map)rest1.getData()).get("tenantList");
                if(tenants != null && tenants.size() > 0){
                    for(Map tenant : tenants){
                        if(Boolean.valueOf(tenant.get("isClearScore").toString())){
                            Map<String, Object> map2 = new HashMap<>();
                            map2.put("tenantId", tenant.get("id"));
                            map2.put("status", 0);
                            List<Vip> vips = vipMapper.select(map2);
                            if(vips != null && vips.size() > 0){
                                for(Vip v : vips){
                                    if(v.getRemainingScore() != null && v.getRemainingScore().doubleValue() != 0){
                                        BigDecimal remainingScore = v.getRemainingScore();

                                        VipBook vipBook = new VipBook();
                                        //记录积分台帐
                                        vipBook.setVipId(v.getId());
                                        vipBook.setTenantId(v.getTenantId());
                                        vipBook.setBranchId(v.getBranchId());
                                        vipBook.setBookType(4);
                                        vipBook.setOperateBy("1月1日清空积分！");
                                        vipBook.setOperateAt(new Date());
                                        vipBook.setVipCode(v.getVipCode());
                                        vipBook.setTotalScore(v.getRemainingScore());
                                        vipBook.setVipScore(new BigDecimal("-1").multiply(v.getRemainingScore()));
                                        vipBook.setCreateAt(new Date());
                                        vipBook.setLastUpdateAt(new Date());
                                        vipBook.setStoreFrom("4");
                                        vipBook.setMemo("定时任务清空积分！");
                                        vipBook.setPaymentCode("QKJF");
                                        vipBookMapper.insert(vipBook);
                                        v.setRemainingScore(BigDecimal.ZERO);
                                        vipMapper.update(v);

                                        WeiXinUtils.updateMemberBonusSafe(v, WeiXinUtils.obtainRecordBonus(4));
                                        WeiXinUtils.sendPointChangeTemplateSafe(v.getId(), remainingScore.intValue() * -1, 2);
                                    }
                                }
                            }
                        }
                    }
                }
            }

        }
        rest.setIsSuccess(true);
        rest.setMessage("清空积分（定时任务）执行完毕。");
        return rest;
    }

    private void dealScore(Vip vip, BigDecimal addScore){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        VipBook vipBook = new VipBook();

        //记录积分台帐
        vipBook.setVipId(vip.getId());
        vipBook.setTenantId(vip.getTenantId());
        vipBook.setBranchId(vip.getBranchId());
        vipBook.setBookType(3);
        vipBook.setOperateBy("会员生日赠送");
        vipBook.setOperateAt(new Date());
        vipBook.setVipCode(vip.getVipCode());
        vipBook.setTotalScore(vip.getRemainingScore());
        vipBook.setVipScore(addScore);
        vipBook.setCreateAt(new Date());
        vipBook.setLastUpdateAt(new Date());
        vipBook.setStoreFrom("2");
        vipBook.setPaymentCode("HYSR"+sdf.format(new Date()));
        vipBookMapper.insert(vipBook);
    }

    public ApiRest addWxCard(Map params){
        ApiRest rest = new ApiRest();
        rest.setIsSuccess(false);
        WxCard wxCard = new WxCard();
        wxCard.setTenantId(new BigInteger(params.get("tenantId").toString()));
        wxCard.setBranchId(new BigInteger(params.get("branchId").toString()));
        wxCard.setCardId(params.get("cardId").toString());
        if(params.get("checkStatus") != null){
            wxCard.setCheckStatus(Integer.parseInt(params.get("checkStatus").toString()));
        }
        wxCard.setCreateAt(new Date());
        wxCard.setLastUpdateAt(new Date());
        cardMapper.insertWxCard(wxCard);

        return rest;
    }

    public ApiRest updateWxCard(Map params){
        ApiRest rest = new ApiRest();
        rest.setIsSuccess(false);
        if(params.get("cardId") != null && params.get("tenantId") != null){
            Map<String, Object> map = new HashMap<>();
            map.put("cardId", params.get("cardId"));
            map.put("tenantId", params.get("tenantId"));
            WxCard wxCard = cardMapper.findWxCard(map);
            if(wxCard != null){
                if("1".equals(params.get("payGiftCard"))){
                    wxCard.setPayGiftCard(true);
                }
                else if("1".equals(params.get("payGiftCard"))){
                    wxCard.setPayGiftCard(false);
                }
                if(params.get("qrCode") != null){
                    wxCard.setQrCode(params.get("qrCode").toString());
                }
                if(params.get("ruleId") != null){
                    wxCard.setRuleId(params.get("ruleId").toString());
                }
                if(params.get("url") != null){
                    wxCard.setBackgroundPicUrl(params.get("url").toString());
                }
                wxCard.setLastUpdateAt(new Date());
                cardMapper.updateWxCard(wxCard);
            }
        }
        return rest;
    }

    public ApiRest getWxCard(Map params){
        ApiRest rest = new ApiRest();
        rest.setIsSuccess(false);
        WxCard wxCard = cardMapper.findWxCard(params);
        rest.setIsSuccess(true);
        rest.setMessage("查询微信会员卡成功！");
        if(wxCard != null){
            rest.setData(wxCard);
        }
        else{
            rest.setData(new WxCard());
        }
        return rest;
    }

}
