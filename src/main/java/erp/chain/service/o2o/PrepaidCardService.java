package erp.chain.service.o2o;

import erp.chain.common.Constants;
import erp.chain.domain.Branch;
import erp.chain.domain.CardType;
import erp.chain.domain.o2o.PrepaidCardAccount;
import erp.chain.domain.o2o.PrepaidCardInfo;
import erp.chain.mapper.BranchMapper;
import erp.chain.mapper.CardMapper;
import erp.chain.mapper.SerialNumberCreatorMapper;
import erp.chain.mapper.o2o.PrepaidCardMapper;
import erp.chain.model.o2o.prepaidCard.ListPrepaidCardModel;
import erp.chain.utils.DatabaseHelper;
import erp.chain.utils.PagedSearchModel;
import erp.chain.utils.SearchCondition;
import erp.chain.utils.SearchModel;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import saas.api.common.ApiRest;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 面值卡信息
 * Created with IntelliJ IDEA.
 * User: AnCong
 * Date: 2018/6/5
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class PrepaidCardService{
    @Autowired
    private PrepaidCardMapper prepaidCardMapper;
    @Autowired
    private CardMapper cardMapper;
    @Autowired
    private SerialNumberCreatorMapper serialNumberCreatorMapper;
    @Autowired
    private BranchMapper branchMapper;

    /**
     * 查询面值卡信息列表
     *
     * @param listPrepaidCardModel
     * @return
     */
    public ApiRest prepaidCardList(ListPrepaidCardModel listPrepaidCardModel){
        ApiRest apiRest = new ApiRest();
        SearchModel searchModel = new SearchModel();
        List<SearchCondition> searchConditions = new ArrayList<>();
        searchConditions.add(new SearchCondition("pci.tenant_id", Constants.SQL_OPERATION_SYMBOL_EQUALS, listPrepaidCardModel.getTenantId()));
        searchConditions.add(new SearchCondition("pci.branch_id", Constants.SQL_OPERATION_SYMBOL_EQUALS, listPrepaidCardModel.getBranchId()));
        searchConditions.add(new SearchCondition("pci.is_deleted", Constants.SQL_OPERATION_SYMBOL_EQUALS, 0));
        if(StringUtils.isNotEmpty(listPrepaidCardModel.getCardCode())){
            searchConditions.add(new SearchCondition("pci.card_code", Constants.SQL_OPERATION_SYMBOL_LIKE, "%" + listPrepaidCardModel.getCardCode() + "%"));
        }
        if(listPrepaidCardModel.getTypeId() != null){
            searchConditions.add(new SearchCondition("pci.pre_card_id", Constants.SQL_OPERATION_SYMBOL_EQUALS, listPrepaidCardModel.getTypeId()));
        }
        if(StringUtils.isNotEmpty(listPrepaidCardModel.getStartTime())){
            searchConditions.add(new SearchCondition("pci.create_at", Constants.SQL_OPERATION_SYMBOL_GREATER_THAN_EQUALS, "" + listPrepaidCardModel.getStartTime() + " 00:00:00"));
        }
        if(StringUtils.isNotEmpty(listPrepaidCardModel.getEndTime())){
            searchConditions.add(new SearchCondition("pci.create_at", Constants.SQL_OPERATION_SYMBOL_LESS_THAN_EQUALS, "" + listPrepaidCardModel.getEndTime() + " 23:59:59"));
        }
        searchModel.setSearchConditions(searchConditions);
        long count = prepaidCardMapper.countPreCard(searchModel);
        Map resultMap = new HashMap();
        if(count > 0){
            PagedSearchModel pagedSearchModel = new PagedSearchModel();
            pagedSearchModel.setPage(listPrepaidCardModel.getPage());
            pagedSearchModel.setRows(listPrepaidCardModel.getRows());
            pagedSearchModel.setSearchConditions(searchConditions);
            pagedSearchModel.setOrderBy("create_at DESC");
            List<PrepaidCardInfo> prepaidCardInfos = prepaidCardMapper.listPreCardInfo(pagedSearchModel);
            resultMap.put("rows", prepaidCardInfos);
        }
        else{
            resultMap.put("rows", new ArrayList<>());
        }
        resultMap.put("total", count);
        apiRest.setIsSuccess(true);
        apiRest.setData(resultMap);
        apiRest.setMessage("查询面值卡信息成功！");
        return apiRest;
    }

    public ApiRest addPrepaidCard(PrepaidCardInfo prepaidCardInfo){
        ApiRest apiRest = new ApiRest();
        CardType cardType = cardMapper.findCardTypeById(prepaidCardInfo.getPreCardId(), prepaidCardInfo.getTenantId());
        if(cardType == null){
            apiRest.setIsSuccess(false);
            apiRest.setError("卡方案信息不存在");
            return apiRest;
        }
        if(cardType.getCardKind() == 2){//如果是面值卡，开卡的时候，写入对应的面值信息
            prepaidCardInfo.setStoreAmount(cardType.getDenomination());
        }
        List<String> codesList = prepaidCardMapper.getUsedCode(prepaidCardInfo.getTenantId(), prepaidCardInfo.getBranchId(), prepaidCardInfo.getCardCode());
        if(codesList.size() > 0){
            apiRest.setIsSuccess(false);
            apiRest.setError("卡号已存在，请重新输入！");
            return apiRest;
        }
        DatabaseHelper.insert(prepaidCardInfo);
        saveAccount(1,prepaidCardInfo.getBranchId(),prepaidCardInfo,cardType,BigDecimal.ZERO,BigInteger.ZERO,"",prepaidCardInfo.getOpId(),new Date(),prepaidCardInfo.getCreateBy());
        apiRest.setIsSuccess(true);
        apiRest.setData(prepaidCardInfo);
        apiRest.setMessage("保存面值卡信息成功！");
        return apiRest;
    }

    public ApiRest batchCreateCard(Map<String, String> params){
        ApiRest apiRest = new ApiRest();
        String cards = params.get("cards");
        BigInteger typeId = BigInteger.valueOf(Long.valueOf(params.get("typeId")));
        BigInteger tenantId = BigInteger.valueOf(Long.valueOf(params.get("tenantId")));
        BigInteger branchId = BigInteger.valueOf(Long.valueOf(params.get("branchId")));
        BigInteger opId = BigInteger.valueOf(Long.valueOf(params.get("opId")));
        String createBy = params.get("createBy");
        List<String> cardCodes = Arrays.asList(cards.split(","));
        CardType cardType = cardMapper.findCardTypeById(typeId, tenantId);
        if(cardType == null){
            apiRest.setIsSuccess(false);
            apiRest.setError("未找到卡方案信息！");
            return apiRest;
        }
        BigDecimal storeAmount = BigDecimal.ZERO;
        if(cardType.getCardKind() == 2){//如果是面值卡，开卡的时候，写入对应的面值信息
            storeAmount = cardType.getDenomination();
        }
        if(cardCodes.size() > 0){
            List<String> list = prepaidCardMapper.getUsedCode(tenantId, branchId, null);
            Map<String, String> cMap = new HashMap<>();
            for(String code : list){
                cMap.put(code, code);
            }
            for(String cardCode : cardCodes){
                if(cMap.get(cardCode) != null && cMap.get(cardCode).equals(cardCode)){
                    apiRest.setIsSuccess(false);
                    apiRest.setError("卡号【" + cardCode + "】已经存在，请修改后重新操作！");
                    return apiRest;
                }
            }
            int i = prepaidCardMapper.batchCreateCard(tenantId, branchId, typeId, createBy, cardCodes, storeAmount);
        }
        List<PrepaidCardInfo> prepaidCardInfos=prepaidCardMapper.listCardInfoByCodeList(cardCodes,tenantId);
        for(PrepaidCardInfo prepaidCardInfo:prepaidCardInfos){
            saveAccount(1,prepaidCardInfo.getBranchId(),prepaidCardInfo,cardType,BigDecimal.ZERO,BigInteger.ZERO,"",opId,new Date(),createBy);
        }
        apiRest.setIsSuccess(true);
        apiRest.setMessage("批量制卡成功！");
        return apiRest;
    }

    public ApiRest getUsedCode(Map<String, String> params){
        ApiRest apiRest = new ApiRest();
        BigInteger tenantId = BigInteger.valueOf(Long.valueOf(params.get("tenantId")));
        BigInteger branchId = BigInteger.valueOf(Long.valueOf(params.get("branchId")));
        List<String> usedCodes = prepaidCardMapper.getUsedCode(tenantId, branchId, null);
        apiRest.setIsSuccess(true);
        apiRest.setData(usedCodes);
        apiRest.setMessage("查询已用卡号成功！");
        return apiRest;
    }

    public ApiRest restoreCard(Map<String, String> params){
        ApiRest apiRest = new ApiRest();
        BigInteger tenantId = BigInteger.valueOf(Long.valueOf(params.get("tenantId")));
        BigInteger branchId = BigInteger.valueOf(Long.valueOf(params.get("branchId")));
        BigInteger cardId = BigInteger.valueOf(Long.valueOf(params.get("cardId")));
        BigInteger typeId = BigInteger.valueOf(Long.valueOf(params.get("typeId")));
        BigInteger opId = BigInteger.valueOf(Long.valueOf(params.get("opId")));
        String createBy = params.get("createBy");
        PrepaidCardInfo prepaidCardInfo=prepaidCardMapper.getInfoById(tenantId,cardId);
        if(prepaidCardInfo==null){
            apiRest.setIsSuccess(false);
            apiRest.setError("未查到面值卡信息！");
            return apiRest;
        }
        CardType cardType=cardMapper.findCardTypeById(typeId,tenantId);
        if(cardType==null){
            apiRest.setIsSuccess(false);
            apiRest.setError("未查到面值卡方案！");
            return apiRest;
        }
        saveAccount(6,branchId,prepaidCardInfo,cardType,BigDecimal.ZERO,BigInteger.ZERO,"",opId,new Date(),createBy);
        apiRest.setIsSuccess(true);
        apiRest.setMessage("回收重制卡成功！");
        return apiRest;
    }
    public ApiRest saleOrRefund(Map<String, String> params){
        ApiRest apiRest=new ApiRest();
        Integer isRefund=Integer.valueOf(params.get("isRefund"));
        String cardCode=params.get("cardCode");
        BigInteger tenantId = BigInteger.valueOf(Long.valueOf(params.get("tenantId")));
        BigInteger branchId = BigInteger.valueOf(Long.valueOf(params.get("branchId")));
        BigInteger opId = BigInteger.valueOf(Long.valueOf(params.get("opId")));
        BigInteger paymentId = BigInteger.valueOf(Long.valueOf(params.get("paymentId")));
        String createBy = params.get("createBy");
        String saleCode=params.get("saleCode");
        BigDecimal amount=BigDecimal.valueOf(Double.valueOf(params.get("amount")));
        PrepaidCardInfo prepaidCardInfo=prepaidCardMapper.getInfoByCode(tenantId,cardCode);
        if(prepaidCardInfo==null){
            apiRest.setIsSuccess(false);
            apiRest.setError("未查到卡信息！");
            return apiRest;
        }
        CardType cardType=cardMapper.findCardTypeById(prepaidCardInfo.getPreCardId(),tenantId);
        if(cardType==null){
            apiRest.setIsSuccess(false);
            apiRest.setError("未查到卡方案！");
            return apiRest;
        }
        Branch branch = branchMapper.getMainBranch(tenantId.toString());
        if(branch==null){
            apiRest.setIsSuccess(false);
            apiRest.setError("总部门店不存在！");
            return apiRest;
        }
        if(prepaidCardInfo.getBranchId().compareTo(branchId)!=0&&prepaidCardInfo.getBranchId().compareTo(branch.getId())!=0){
            apiRest.setIsSuccess(false);
            apiRest.setError("您的卡无法在此门店消费！");
            return apiRest;
        }
        if(isRefund==1){//消费退款
            saveAccount(5,branchId,prepaidCardInfo,cardType,amount,paymentId,saleCode,opId,new Date(),createBy);
        }else if (isRefund==0){//消费
            if(cardType.getStartTime()!=null&&cardType.getEndTime()!=null){//限制时间
                if(!(cardType.getStartTime().before(new Date())&&cardType.getEndTime().after(new Date()))){
                    apiRest.setIsSuccess(false);
                    apiRest.setError("您的卡不在活动日期内！");
                    return apiRest;
                }
            }
            if(prepaidCardInfo.getStoreAmount().compareTo(amount)>=0){
                saveAccount(4,branchId,prepaidCardInfo,cardType,amount,paymentId,saleCode,opId,new Date(),createBy);
            }else{
                apiRest.setIsSuccess(false);
                apiRest.setError("卡片余额不足，可用余额￥"+prepaidCardInfo.getStoreAmount());
                return apiRest;
            }
        }
        apiRest.setIsSuccess(true);
        if(isRefund==1){
            apiRest.setMessage("面值卡退款成功！");
        }else if(isRefund==0){
            apiRest.setMessage("面值卡消费成功！");
        }
        return apiRest;
    }

    public ApiRest getPreCardInfoByCode(Map<String, String> params){
        ApiRest apiRest=new ApiRest();
        String cardCode=params.get("cardCode");
        BigInteger tenantId = BigInteger.valueOf(Long.valueOf(params.get("tenantId")));
        PrepaidCardInfo prepaidCardInfo=prepaidCardMapper.getInfoByCode(tenantId,cardCode);
        if(prepaidCardInfo==null){
            apiRest.setIsSuccess(false);
            apiRest.setError("未查到卡信息！");
            return apiRest;
        }
        apiRest.setIsSuccess(true);
        apiRest.setMessage("查询卡信息成功！");
        apiRest.setData(prepaidCardInfo);
        return apiRest;
    }

    private void saveAccount(Integer type, BigInteger branchId, PrepaidCardInfo prepaidCardInfo, CardType cardType, BigDecimal amount, BigInteger paymentId, String orderCode, BigInteger opId, Date opAt, String userName){
        PrepaidCardAccount prepaidCardAccount = new PrepaidCardAccount();
        SimpleDateFormat sdf=new SimpleDateFormat("yyyyMMdd");
        prepaidCardAccount.setOpType(type);
        prepaidCardAccount.setCardId(prepaidCardInfo.getId());
        prepaidCardAccount.setTenantId(prepaidCardInfo.getTenantId());
        prepaidCardAccount.setBranchId(branchId);
        prepaidCardAccount.setCreateAt(opAt);
        prepaidCardAccount.setCreateBy(userName);
        prepaidCardAccount.setLastUpdateAt(opAt);
        prepaidCardAccount.setLastUpdateBy(userName);
        prepaidCardAccount.setIsDeleted(false);
        prepaidCardAccount.setOpId(opId);
        prepaidCardAccount.setPaymentId(paymentId);
        switch(type){
            case 1://制卡
                prepaidCardAccount.setAmount(cardType.getDenomination());
                prepaidCardAccount.setGiveAmount(BigDecimal.ZERO);
                prepaidCardAccount.setTotalAmount(cardType.getDenomination());
                prepaidCardAccount.setOrderCode("MZZK"+sdf.format(new Date())+serialNumberCreatorMapper.getToday("MZZK" + prepaidCardInfo.getTenantId().toString(), 4));
                prepaidCardInfo.setStoreAmount(cardType.getDenomination());
                prepaidCardInfo.setLastUpdateAt(opAt);
                prepaidCardInfo.setLastUpdateBy(userName);
                break;
            case 2://充值
                break;
            case 3://退款
                break;
            case 4://消费
                prepaidCardAccount.setAmount(amount);
                prepaidCardAccount.setGiveAmount(BigDecimal.ZERO);
                prepaidCardAccount.setTotalAmount(prepaidCardInfo.getStoreAmount().subtract(amount));
                prepaidCardAccount.setOrderCode(orderCode);
                prepaidCardInfo.setStoreAmount(prepaidCardInfo.getStoreAmount().subtract(amount));
                prepaidCardInfo.setLastUpdateAt(opAt);
                prepaidCardInfo.setLastUpdateBy(userName);
                break;
            case 5://消费退款
                prepaidCardAccount.setAmount(amount);
                prepaidCardAccount.setGiveAmount(BigDecimal.ZERO);
                prepaidCardAccount.setTotalAmount(prepaidCardInfo.getStoreAmount().add(amount));
                prepaidCardAccount.setOrderCode(orderCode);
                prepaidCardInfo.setStoreAmount(prepaidCardInfo.getStoreAmount().add(amount));
                prepaidCardInfo.setLastUpdateAt(opAt);
                prepaidCardInfo.setLastUpdateBy(userName);
                break;
            case 6://回收
                prepaidCardAccount.setAmount(cardType.getDenomination());
                prepaidCardAccount.setGiveAmount(BigDecimal.ZERO);
                prepaidCardAccount.setTotalAmount(cardType.getDenomination());
                prepaidCardAccount.setOrderCode("MZHS"+sdf.format(new Date())+serialNumberCreatorMapper.getToday("MZHS" + prepaidCardInfo.getTenantId().toString(), 4));
                prepaidCardInfo.setStoreAmount(cardType.getDenomination());
                prepaidCardInfo.setLastUpdateAt(opAt);
                prepaidCardInfo.setLastUpdateBy(userName);
                prepaidCardInfo.setPreCardId(cardType.getId());
                prepaidCardInfo.setCreateAt(opAt);
                break;
            default:
                break;
        }
        int i=prepaidCardMapper.saveAccount(prepaidCardAccount);
        int j=prepaidCardMapper.updateCardAmount(prepaidCardInfo);
    }

    public ApiRest prepaidCardAccountList(Map<String,String> params){
        ApiRest apiRest=new ApiRest();
        Integer page=1;
        Integer rows=20;
        Map<String,Object> par=new HashMap<>();
        if(StringUtils.isNotEmpty(params.get("page"))){
            page=Integer.valueOf(params.get("page"));
        }
        if(StringUtils.isNotEmpty(params.get("rows"))){
            rows=Integer.valueOf(params.get("rows"));
        }
        Integer offset=(page - 1) * rows;
        params.put("offset",offset.toString());

        long count = prepaidCardMapper.countAccount(params);
        Map resultMap = new HashMap();
        if(count > 0){
            List<Map> prepaidCardInfos = prepaidCardMapper.prepaidCardAccountList(params);
            resultMap.put("rows", prepaidCardInfos);
        }
        else{
            resultMap.put("rows", new ArrayList<>());
        }
        resultMap.put("total", count);
        apiRest.setIsSuccess(true);
        apiRest.setData(resultMap);
        apiRest.setMessage("查询面值卡台帐成功！");
        return apiRest;
    }
}
