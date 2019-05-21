package erp.chain.service.o2o;

import com.saas.common.ResultJSON;
import com.saas.common.exception.ServiceException;
import com.saas.common.util.LogUtil;
import com.saas.common.util.PartitionCacheUtils;
import erp.chain.domain.Branch;
import erp.chain.domain.Employee;
import erp.chain.domain.Payment;
import erp.chain.domain.o2o.*;
import erp.chain.domain.o2o.vo.VipGlide;
import erp.chain.mapper.BranchMapper;
import erp.chain.mapper.EmployeeMapper;
import erp.chain.mapper.PaymentMapper;
import erp.chain.mapper.o2o.*;
import erp.chain.service.system.SmsService;
import erp.chain.utils.BeanUtils;
import erp.chain.utils.GsonUntil;
import erp.chain.utils.WeiXinUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import saas.api.common.ApiRest;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 会员积分历史
 * Created by wangms on 2017/1/10.
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class VipBookService {
    @Autowired
    private VipBookMapper vipBookMapper;
    @Autowired
    private VipMapper vipMapper;
    @Autowired
    private VipTypeMapper vipTypeMapper;
    @Autowired
    private VipStoreHistoryMapper vipStoreHistoryMapper;
    @Autowired
    private VipTradeHistoryMapper vipTradeHistoryMapper;
    @Autowired
    private BranchMapper branchMapper;
    @Autowired
    private PaymentMapper paymentMapper;
    @Autowired
    private EmployeeMapper employeeMapper;
    @Autowired
    private SmsService smsService;
    @Autowired
    private VipService vipService;

    /**
     * 查询会员台帐列表
     */
    public ResultJSON vipBookList(Map params) {
        ResultJSON result = new ResultJSON();
        //查询会员分组
        if (params.get("branchId") != null && params.get("tenantId") != null) {
            Map<String, Object> paramZ = new HashMap<>();
            paramZ.put("id", params.get("branchId"));
            paramZ.put("tenantId", params.get("tenantId"));
            Branch branch = branchMapper.find(paramZ);
            if (branch != null && branch.getGroupCode() != null && !"".equals(branch.getGroupCode())) {
                paramZ.remove("id");
                paramZ.put("groupCode", branch.getGroupCode());
                List<Branch> branches = branchMapper.findBranchByTenantId(paramZ);
                if (branches != null && branches.size() > 0) {
                    String bIds = "";
                    for (Branch b : branches) {
                        bIds += b.getId() + ",";
                    }
                    bIds = bIds.substring(0, bIds.length() - 1);
                    params.remove("branchId");
                    params.put("groups", bIds);
                }
            }
        }
        Map<String, Object> map = new HashMap<>();
        if (params.get("page") != null && params.get("rows") != null) {
            params.put("offset", (Integer.parseInt(params.get("page").toString()) - 1) * Integer.parseInt(params.get("rows").toString()));
        }
        List<VipBook> list = vipBookMapper.vipBookList(params);
        int count = list.size();
        map.put("total", count > 0 ? count : 0);
        map.put("rows", list);
        result.setJsonMap(map);
        return result;
    }

    /**
     * 新增会员台帐
     */
    public ResultJSON addVipBook(Map params) throws ServiceException {
        ResultJSON result = new ResultJSON();
        if (params.get("id") != null && !"".equals(params.get("id"))) {
            int flag = vipBookMapper.updateVipBook(params);
            if (flag == 1) {
                result.setSuccess("0");
                result.setMsg("会员台帐更新成功");
            } else {
                result.setSuccess("1");
                result.setMsg("会员台帐更新失败");
            }
        } else {
            int flag = vipBookMapper.addVipBook(params);
            if (flag == 1) {
                result.setSuccess("0");
                result.setMsg("会员台帐保存成功");
            } else {
                result.setSuccess("1");
                result.setMsg("会员台帐保存失败");
            }
        }
        return result;
    }

    /**
     * 修改会员台帐(id查询)
     */
    public ResultJSON editVipBookById(BigInteger id) throws ServiceException {
        ResultJSON result = new ResultJSON();
        VipBook vipBook = vipBookMapper.findVipBookById(id);
        result.setSuccess("0");
        result.setObject(vipBook);
        return result;
    }

    /**
     * 删除会员台帐
     */
    public ResultJSON deleteVipBook(String ids) throws ServiceException {
        ResultJSON result = new ResultJSON();
        if (ids != null && !"".equals(ids)) {
            for (String id : ids.split(",")) {
                Map<String, Object> map = new HashMap<>();
                map.put("id", Integer.parseInt(id));
                map.put("isDeleted", 1);
                vipBookMapper.updateVipBook(map);
            }
            result.setSuccess("0");
            result.setMsg("会员台帐删除成功");
        } else {
            result.setSuccess("1");
            result.setMsg("无效的数据。");
        }
        return result;
    }

    /**
     * 查找所有vipCode
     */
    public ResultJSON findAllVipCode() throws ServiceException {
        ResultJSON result = new ResultJSON();
        Map<String, Object> map = new HashMap<>();
        List<Vip> list = vipMapper.select(map);
        map.put("rows", list);
        result.setJsonMap(map);
        return result;
    }

    /**
     * pos上传会员流水
     *
     * @param params
     * @throws ServiceException
     */
    @Transactional(rollbackFor = Exception.class)
    public ResultJSON uploadVipGlide(Map params) throws ServiceException {
        ResultJSON result = new ResultJSON();
        BigInteger tenantId = BigInteger.valueOf(Long.valueOf(params.get("tenantId").toString()));
        if (params.get("jsonData") != null && !"".equals(params.get("jsonData"))) {
            long startTime = System.currentTimeMillis();
            LogUtil.logInfo("pos上传流水uploadVipGlide，开始时间:" + startTime);
            List<VipGlide> glideList = GsonUntil.jsonAsList(String.valueOf(params.get("jsonData")), VipGlide.class);
            List<BigInteger> idList = new ArrayList<>();
            for (int i = 0; i < glideList.size(); i++) {
                String cashier = ((Map) glideList.get(i)).get("cashier").toString();
                Employee employee = employeeMapper.getEmployeeById(BigInteger.valueOf(Long.valueOf(cashier)), tenantId);
                if (employee == null) {
                    result.setSuccess("1");
                    result.setMsg("员工查询失败!");
                    return result;
                }
                String createBy = employee.getName();
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                BigInteger vipId = null;
                if (((Map) glideList.get(i)).get("vipId") != null && !"".equals(((Map) glideList.get(i)).get("vipId"))) {
                    vipId = BigInteger.valueOf(Long.valueOf(((Map) glideList.get(i)).get("vipId").toString()));
                } else {
                    result.setSuccess("1");
                    result.setMsg("数据异常：vipId为空！");
                    return result;
                }
                if (!idList.contains(vipId)) {
                    if (vipId.doubleValue() != -1) {
                        idList.add(vipId);
                    }
                }
                Map<String, Object> map = new HashMap<>();
                map.put("id", vipId);
                Vip vip = vipMapper.findByCondition(map);
                //积分
                if ("JF".equals(((Map) glideList.get(i)).get("paymentCode").toString())) {
                    if (null != vip) {
                        VipBook vipBook = new VipBook();
                        vipBook.setTotalScore(vip.getRemainingScore());
                        double amount = Double.valueOf(((Map) glideList.get(i)).get("amount").toString());
                        if (vip.getRemainingScore() != null) {
                            if (amount <= vip.getRemainingScore().doubleValue()) {
                                map.put("remainingScore", vip.getRemainingScore().doubleValue() - amount);
                                if (null != vip.getOverscore()) {
                                    map.put("overscore", vip.getOverscore().doubleValue() + amount);
                                } else {
                                    map.put("overscore", amount);
                                }
                            } else {
                                LogUtil.logError("pos流水上传异常:" + vip.getId() + "扣除积分大于积分余额。");
                                map.put("remainingScore", 0);
                            }
                        }
                        map.put("lastUpdateAt", new Date());
                        map.put("lastUpdateBy", createBy);
                        //修改累计消费次数及金额
                        if (vip.getBuyTimes() != null) {
                            map.put("buyTimes", vip.getBuyTimes() + 1);
                        } else {
                            map.put("buyTimes", 1);
                        }
                        if (vip.getSumConsume() != null) {
                            map.put("sumConsume", vip.getSumConsume().doubleValue() + amount);
                        } else {
                            map.put("sumConsume", amount);
                        }
                        vipMapper.update(map);

                        //记录积分台帐

                        vipBook.setVipId(vip.getId());
                        vipBook.setTenantId(vip.getTenantId());
                        if (params.get("branchId") != null) {
                            vipBook.setBranchId(new BigInteger(params.get("branchId").toString()));
                        } else {
                            vipBook.setBranchId(vip.getBranchId());
                        }
                        vipBook.setBookType(3);
                        vipBook.setOperateUserId(BigInteger.valueOf(Long.valueOf(((Map) glideList.get(i)).get("cashier").toString())));
                        vipBook.setEmpId(BigInteger.valueOf(Long.valueOf(((Map) glideList.get(i)).get("cashier").toString())));
                        if (((Map) glideList.get(i)).get("operateBy") != null) {
                            vipBook.setOperateBy(((Map) glideList.get(i)).get("operateBy").toString());
                        } else if (((Map) glideList.get(i)).get("createBy") != null) {
                            vipBook.setOperateBy(((Map) glideList.get(i)).get("createBy").toString());
                        } else {
                            vipBook.setOperateBy("收银员");
                        }

                        vipBook.setOperateAt(new Date());
                        vipBook.setVipCode(vip.getVipCode());
                        vipBook.setPaymentCode(((Map) glideList.get(i)).get("paymentCode").toString());
                        vipBook.setVipScore(new BigDecimal(amount));
                        vipBook.setIsDeleted(false);
                        vipBook.setCreateAt(new Date());
                        vipBook.setLastUpdateAt(new Date());
                        vipBook.setCreateBy(createBy);
                        vipBook.setLastUpdateBy(createBy);
                        vipBook.setStoreFrom("0");
                        vipBookMapper.insert(vipBook);
                    }
                }
                //储值消费
                if ("HYQB".equals(((Map) glideList.get(i)).get("paymentCode").toString())) {
                    if (null != vip) {
                        LogUtil.logInfo("pos上传流水,tenantId:" + vip.getTenantId() + ",vipId:" + vip.getId());
                        VipBook vipBook = new VipBook();
                        vipBook.setTotalScore(vip.getRemainingScore());
                        //修改账户信息，并且根据会员等级积分规则积分
                        params.put("tenantId", vip.getTenantId());
                        Map<String, Object> param = new HashMap<>();
                        param.put("id", vip.getTypeId());
                        VipType vipType = vipTypeMapper.findByCondition(param);
                        double amount = 0.0;
                        if (null != ((Map) glideList.get(i)).get("amount")) {
                            amount = Double.valueOf(((Map) glideList.get(i)).get("amount").toString());
                        }
                        if (null != vip.getVipStore()) {
                            if (vip.getVipStore().doubleValue() >= amount) {
                                map.put("vipStore", vip.getVipStore().doubleValue() - amount);

                                //修改累计消费次数及金额
                                if (vip.getBuyTimes() != null) {
                                    map.put("buyTimes", vip.getBuyTimes() + 1);
                                } else {
                                    map.put("buyTimes", 1);
                                }
                                if (vip.getSumConsume() != null) {
                                    map.put("sumConsume", vip.getSumConsume().doubleValue() + amount);
                                } else {
                                    map.put("sumConsume", amount);
                                }

                                //储值扣减 记录储值消费历史
                                VipStoreHistory storeHistory = new VipStoreHistory();

                                storeHistory.setVipId(vip.getId());
                                if (((Map) glideList.get(i)).get("saleCode") != null) {
                                    storeHistory.setStoreCode(((Map) glideList.get(i)).get("saleCode").toString());
                                    LogUtil.logInfo("pos上传流水,saleCode:" + ((Map) glideList.get(i)).get("saleCode").toString());
                                }
                                storeHistory.setStoreType("3");
                                storeHistory.setPayAmount(new BigDecimal(amount));
                                storeHistory.setGiftAmount(new BigDecimal(0));
                                storeHistory.setPayType("7");
                                if (params.get("branchId") != null) {
                                    storeHistory.setStoreBranchId(new BigInteger(params.get("branchId").toString()));
                                } else {
                                    storeHistory.setStoreBranchId(vip.getBranchId());
                                }
                                storeHistory.setIsDeleted(false);
                                storeHistory.setCreateAt(new Date());
                                storeHistory.setLastUpdateAt(new Date());
                                storeHistory.setStoreFrom("0");
                                if (params.get("branchName") != null) {
                                    storeHistory.setStoreBranchName(params.get("branchName").toString());
                                }
                                storeHistory.setCreateBy(createBy);
                                storeHistory.setTenantId(tenantId);
                                vipStoreHistoryMapper.insert(storeHistory);

                                //记录消费历史
                                VipTradeHistory vipTradeHistory = new VipTradeHistory();
                                vipTradeHistory.setVipId(vip.getId());
                                vipTradeHistory.setTradeNo(((Map) glideList.get(i)).get("saleCode").toString());
                                vipTradeHistory.setOrderCode(((Map) glideList.get(i)).get("saleCode").toString());
                                vipTradeHistory.setTradeType("1");
                                vipTradeHistory.setTradeAmount(new BigDecimal(amount));
                                vipTradeHistory.setPayAmount(new BigDecimal(amount));
                                if (params.get("branchId") != null) {
                                    vipTradeHistory.setTradeBranchId(new BigInteger(params.get("branchId").toString()));
                                } else {
                                    vipTradeHistory.setTradeBranchId(vip.getBranchId());
                                }
                                vipTradeHistory.setTradeDate(new Date());
                                if (params.get("branchName") != null) {
                                    vipTradeHistory.setTradeBranchName(params.get("branchName").toString());
                                }
                                //记录应积积分
                                if (((Map) glideList.get(i)).get("posPaymentId").toString() != null) {
                                    BigInteger payId = BigInteger.valueOf(Long.valueOf(((Map) glideList.get(i)).get("posPaymentId").toString()));
                                    vipTradeHistory.setPaymentId(payId);
                                    Map payment = paymentMapper.getPaymentByIdAndBranchId(new BigInteger(params.get("branchId").toString()), payId, vip.getTenantId());
                                    if (payment.get("isScore").toString().equals("true") || payment.get("isScore").toString().equals("1")) {
                                        if (vipType != null && vipType.getToSavePoints().doubleValue() == 1 && vipType.getPointsFactor() != null && vipType.getPointsFactor().doubleValue() != 0) {
                                            vipTradeHistory.setAddScore(new BigDecimal((int) amount / (int) vipType.getPointsFactor().doubleValue()));
                                        }
                                    } else {
                                        vipTradeHistory.setAddScore(BigDecimal.ZERO);
                                    }
                                }
                                vipTradeHistory.setCreateBy(createBy);
                                vipTradeHistory.setLastUpdateBy(createBy);
                                vipTradeHistory.setCreateAt(new Date());
                                vipTradeHistory.setLastUpdateAt(new Date());
                                vipTradeHistory.setTenantId(tenantId);
                                vipTradeHistoryMapper.insert(vipTradeHistory);
                            } else {
                                LogUtil.logError("pos流水上传异常:" + vip.getId() + "储值余额不足。");
                                result.setSuccess("1");
                                result.setMsg("储值余额不足!");
                                return result;
                            }
                        } else {
                            LogUtil.logError("pos流水上传异常:" + vip.getId() + "储值余额不足。");
                            result.setSuccess("1");
                            result.setMsg("储值余额不足!");
                            return result;
                        }
                        map.put("lastUpdateAt", new Date());
                        map.put("lastUpdateBy", createBy);
                        vipMapper.update(map);
                    }

                }
            }
            List<Vip> vipList = new ArrayList<>();
            for (BigInteger id : idList) {
                Map<String, Object> param1 = new HashMap<>();
                param1.put("id", id);
                Vip vip = vipMapper.findByCondition(param1);
                if (vip != null) {
                    vipList.add(vip);
                }
            }
            result.setSuccess("0");
            result.setMsg("上传流水完成!");
            result.setObject(vipList);

            long endTime = System.currentTimeMillis();
            LogUtil.logInfo("pos上传流水uploadVipGlide，结束时间:" + endTime + ",用时:" + (endTime - startTime) + "ms");
        } else {
            result.setSuccess("1");
            result.setMsg("无效的流水数据!");
        }
        return result;
    }

    /**
     * pos上传会员流水（加入防重复提交验证）
     *
     * @param params
     * @throws ServiceException
     */
    @Transactional(rollbackFor = Exception.class)
    public ResultJSON uploadVipGlideV2(Map params) throws ServiceException, IOException{
        ResultJSON result = new ResultJSON();
        //验证token防止重复提交
        if(params.get("token") == null){
            result.setSuccess("1");
            result.setMsg("token不存在!");
            return result;
        }
        String redisToken = "TRADE_"+params.get("tenantId")+"_"+params.get("branchId")+"_"+"_"+params.get("token");
        if(PartitionCacheUtils.get(redisToken) != null){
            result.setSuccess("1");
            result.setMsg("正在处理，请稍候!");
            return result;
        }
        Map<String, Object> checkParam = new HashMap<>();
        checkParam.put("token", params.get("token"));
        checkParam.put("branchId", params.get("branchId"));
        if(params.get("tenantId") != null && !"".equals(params.get("tenantId"))){
            checkParam.put("tenantId", params.get("tenantId"));
        }
        //消费记录
        VipTradeHistory checkTradeHistory = vipTradeHistoryMapper.findByCondition(checkParam);
        //积分记录
        VipBook checkVipBook = vipBookMapper.findByCondition(checkParam);
        if(checkTradeHistory != null || checkVipBook != null){
            PartitionCacheUtils.del(redisToken);
            LogUtil.logInfo("pos上传流水uploadVipGlide，验证token已存在，token:" + params.get("token"));
            result.setSuccess("0");
            result.setMsg("token已存在，数据已处理!");
            return result;
        }
        PartitionCacheUtils.set(redisToken, params.get("token").toString());

        boolean flag = false;
        if (params.get("jsonData") != null && !"".equals(params.get("jsonData"))) {
            long startTime = System.currentTimeMillis();
            LogUtil.logInfo("pos上传流水uploadVipGlide，开始时间:" + startTime);
            LogUtil.logInfo("上传流水json:" + String.valueOf(params.get("jsonData")));
            List<VipGlide> glideList = GsonUntil.jsonAsList(String.valueOf(params.get("jsonData")), VipGlide.class);
            List<BigInteger> idList = new ArrayList<>();
            for (int i = 0; i < glideList.size(); i++) {
                BigInteger vipId = null;
                if (((Map) glideList.get(i)).get("vipId") != null && !"".equals(((Map) glideList.get(i)).get("vipId"))) {
                    vipId = BigInteger.valueOf(Long.valueOf(((Map) glideList.get(i)).get("vipId").toString()));
                } else {
                    result.setSuccess("1");
                    result.setMsg("数据异常：vipId为空！");
                    return result;
                }
                if (!idList.contains(vipId)) {
                    if (vipId.doubleValue() != -1) {
                        idList.add(vipId);
                    }
                }
                if (vipId == null) {
                    result.setSuccess("1");
                    result.setMsg("数据异常：vipId为空！");
                    return result;
                }
                if (vipId.compareTo(BigInteger.ZERO) == 0) {
                    result.setSuccess("1");
                    result.setMsg("数据异常：vipId为0！");
                    return result;
                }
                Map<String, Object> map = new HashMap<>();
                map.put("id", vipId);
                LogUtil.logInfo("查询会员数据：" + BeanUtils.toJsonStr(map));
                Vip vip = vipMapper.findByCondition(map);
                if (vip == null) {
                    result.setSuccess("1");
                    result.setMsg("未查询到会员信息！");
                    return result;
                }
                BigInteger payId = BigInteger.valueOf(Long.valueOf(((Map) glideList.get(i)).get("posPaymentId").toString()));
                //积分
                if ("JF".equals(((Map) glideList.get(i)).get("paymentCode").toString())) {
                    VipBook vipBook = new VipBook();
                    vipBook.setTotalScore(vip.getRemainingScore());
                    Double amount = Double.valueOf(((Map) glideList.get(i)).get("amount").toString());
                    if (vip.getRemainingScore() != null) {
                        if (amount <= vip.getRemainingScore().doubleValue()) {
                            map.put("remainingScore", vip.getRemainingScore().doubleValue() - amount);
                            if (null != vip.getOverscore()) {
                                map.put("overscore", vip.getOverscore().doubleValue() + amount);
                            } else {
                                map.put("overscore", amount);
                            }
                        } else {
                            LogUtil.logError("pos流水上传异常:" + vip.getId() + "扣除积分大于积分余额。");
                            map.put("remainingScore", 0);
                        }
                    }
                    map.put("lastUpdateAt", new Date());
                    if (((Map) glideList.get(i)).get("createBy") != null) {
                        map.put("lastUpdateBy", ((Map) glideList.get(i)).get("createBy").toString());
                    }
                    vipMapper.update(map);

                    //记录积分台帐
                    vipBook.setVipId(vip.getId());
                    vipBook.setTenantId(vip.getTenantId());
                    if (params.get("branchId") != null) {
                        vipBook.setBranchId(new BigInteger(params.get("branchId").toString()));
                    } else {
                        vipBook.setBranchId(vip.getBranchId());
                    }
                    vipBook.setBookType(2);
                    vipBook.setOperateUserId(BigInteger.valueOf(Long.valueOf(((Map) glideList.get(i)).get("cashier").toString())));
                    vipBook.setOperateBy("收银员");
                    vipBook.setOperateAt(new Date());
                    vipBook.setVipCode(vip.getVipCode());
                    if (((Map) glideList.get(i)).get("saleCode") != null) {
                        vipBook.setPaymentCode(((Map) glideList.get(i)).get("saleCode").toString());
                    }
                    vipBook.setVipScore(new BigDecimal(amount));
                    vipBook.setIsDeleted(false);
                    vipBook.setCreateAt(new Date());
                    vipBook.setLastUpdateAt(new Date());
                    if (params.get("token") != null) {
                        vipBook.setToken(params.get("token").toString());
                    }
                    if (((Map) glideList.get(i)).get("createBy") != null) {
                        vipBook.setCreateBy(((Map) glideList.get(i)).get("createBy").toString());
                        vipBook.setLastUpdateBy(((Map) glideList.get(i)).get("createBy").toString());
                    }
                    vipBook.setStoreFrom("0");
                    vipBookMapper.insert(vipBook);
                    BigInteger branchId = null;
                    if (params.get("branchId") != null) {
                        branchId = new BigInteger(params.get("branchId").toString());
                    } else {
                        branchId = vip.getBranchId();
                    }
                    Branch branch1 = branchMapper.findBranchByTenantIdAndBranchId(vip.getTenantId(), branchId);
                    if (branch1 != null) {
                    }
                    WeiXinUtils.updateMemberBonusSafe(vip, amount.intValue() * -1, WeiXinUtils.obtainRecordBonus(2));
                    if (vip.getPhone() != null && !vip.getPhone().equals("")) {
                        Map smsMap = new HashMap();
                        smsMap.put("type", 4);
                        smsMap.put("tenantId", vip.getTenantId().toString());
                        smsMap.put("number", vip.getPhone());
                        if (branch1 != null) {
                            smsMap.put("branchId", branchId.toString());
                            DecimalFormat sd = new DecimalFormat("#");
                            smsMap.put("content", "积分支付###" + amount.intValue() + "###" + (sd.format((vip.getRemainingScore() == null ? BigDecimal.ZERO : vip.getRemainingScore()).subtract(BigDecimal.valueOf(amount)))));
                            ApiRest ssr2 = smsService.sendSMS(smsMap);
                            if (!ssr2.getIsSuccess()) {
                                LogUtil.logInfo("发送消费短信失败！vipId=" + vip.getId());
                            }
                        }
                    }
                    flag = true;
                    WeiXinUtils.sendPointChangeTemplateSafe(vip.getId(), Math.abs(amount.intValue()) * -1, 1);
                }
                //储值消费
                if ("HYQB".equals(((Map) glideList.get(i)).get("paymentCode").toString())) {
                    LogUtil.logInfo("pos上传流水,tenantId:" + vip.getTenantId() + ",vipId:" + vip.getId());
                    VipBook vipBook = new VipBook();
                    vipBook.setTotalScore(vip.getRemainingScore());
                    //修改账户信息，并且根据会员等级积分规则积分
                    params.put("tenantId", vip.getTenantId());
                    Map<String, Object> param = new HashMap<>();
                    param.put("id", vip.getTypeId());
                    VipType vipType = vipTypeMapper.findByCondition(param);
                    if (vipType == null) {
                        result.setSuccess("1");
                        result.setMsg("会员类型不存在！");
                        return result;
                    }
                    double amount = 0.0;
                    if (null != ((Map) glideList.get(i)).get("amount")) {
                        amount = Double.valueOf(((Map) glideList.get(i)).get("amount").toString());
                    }
                    BigDecimal deposit = vipType.getDeposit();
                    if (deposit == null) {
                        deposit = BigDecimal.ZERO;
                    }
                    if (null != vip.getVipStore()) {
                        if (vip.getVipStore().doubleValue() >= amount) {
                            if ((vip.getVipStore().subtract(BigDecimal.valueOf(amount))).compareTo(deposit) < 0) {
                                LogUtil.logInfo("pos流水上传异常:" + vip.getId() + "扣除押金之后储值余额不足。");
                                result.setSuccess("1");
                                result.setMsg("可用余额不足，押金为" + deposit + "!");
                                return result;
                            }
                            map.put("vipStore", vip.getVipStore().doubleValue() - amount);

                            //修改累计消费次数及金额
                            if (vip.getBuyTimes() != null) {
                                map.put("buyTimes", vip.getBuyTimes() + 1);
                            } else {
                                map.put("buyTimes", 1);
                            }
                            if (vip.getSumConsume() != null) {
                                map.put("sumConsume", vip.getSumConsume().doubleValue() + amount);
                            } else {
                                map.put("sumConsume", amount);
                            }

                            //储值扣减 记录储值消费历史
                            VipStoreHistory storeHistory = new VipStoreHistory();

                            storeHistory.setVipId(vip.getId());
                            if (((Map) glideList.get(i)).get("saleCode") != null) {
                                storeHistory.setStoreCode(((Map) glideList.get(i)).get("saleCode").toString());
                                LogUtil.logInfo("pos上传流水,saleCode:" + ((Map) glideList.get(i)).get("saleCode").toString());
                            }
                            storeHistory.setStoreType("3");
                            storeHistory.setPayAmount(new BigDecimal(amount));
                            storeHistory.setGiftAmount(new BigDecimal(0));
                            storeHistory.setPayType("7");
                            if (params.get("token") != null) {
                                storeHistory.setToken(params.get("token").toString());
                            }
                            if (params.get("branchId") != null) {
                                storeHistory.setStoreBranchId(new BigInteger(params.get("branchId").toString()));
                            } else {
                                storeHistory.setStoreBranchId(vip.getBranchId());
                            }
                            storeHistory.setIsDeleted(false);
                            storeHistory.setCreateAt(new Date());
                            storeHistory.setLastUpdateAt(new Date());
                            storeHistory.setStoreFrom("0");
                            if (params.get("branchName") != null) {
                                storeHistory.setStoreBranchName(params.get("branchName").toString());
                            }
                            if (((Map) glideList.get(i)).get("createBy") != null) {
                                storeHistory.setCreateBy(((Map) glideList.get(i)).get("createBy").toString());
                            }
                            /*消费后余额*/
                            storeHistory.setVipOperStore(BigDecimal.valueOf(vip.getVipStore().doubleValue() - amount));
                            storeHistory.setTenantId(vip.getTenantId());
                            vipStoreHistoryMapper.insert(storeHistory);

                            //记录消费历史
                            VipTradeHistory vipTradeHistory = new VipTradeHistory();
                            vipTradeHistory.setVipId(vip.getId());
                            vipTradeHistory.setTradeNo(((Map) glideList.get(i)).get("saleCode").toString());
                            vipTradeHistory.setOrderCode(((Map) glideList.get(i)).get("saleCode").toString());
                            vipTradeHistory.setTradeType("1");
                            vipTradeHistory.setTradeAmount(new BigDecimal(amount));
                            vipTradeHistory.setPayAmount(new BigDecimal(amount));
                            if (params.get("branchId") != null) {
                                vipTradeHistory.setTradeBranchId(new BigInteger(params.get("branchId").toString()));
                            } else {
                                vipTradeHistory.setTradeBranchId(vip.getBranchId());
                            }
                            vipTradeHistory.setTradeDate(new Date());
                            if (params.get("branchName") != null) {
                                vipTradeHistory.setTradeBranchName(params.get("branchName").toString());
                            }
                            //记录应积积分
                            if (((Map) glideList.get(i)).get("posPaymentId") != null) {
                                /*BigInteger payId = BigInteger.valueOf(Long.valueOf(((Map) glideList.get(i)).get("posPaymentId").toString()));
                                vipTradeHistory.setPaymentId(payId);
                                if(params.get("branchId") != null){
                                    Map payment = paymentMapper.getPaymentByIdAndBranchId(new BigInteger(params.get("branchId").toString()), payId, vip.getTenantId());
                                    if(payment.get("isScore").toString().equals("true") || payment.get("isScore").toString().equals("1")){
                                        if(vipType != null && vipType.getToSavePoints().doubleValue() == 1 && vipType.getPointsFactor() != null && vipType.getPointsFactor().doubleValue() != 0){
                                            vipTradeHistory.setAddScore(new BigDecimal((int)(amount/vipType.getPointsFactor().doubleValue())));
                                        }
                                    }
                                    else{
                                        vipTradeHistory.setAddScore(BigDecimal.ZERO);
                                    }
                                }*/
                                vipTradeHistory.setAddScore(BigDecimal.ZERO);
                            }
                            if (((Map) glideList.get(i)).get("createBy") != null) {
                                vipTradeHistory.setCreateBy(((Map) glideList.get(i)).get("createBy").toString());
                                vipTradeHistory.setLastUpdateBy(((Map) glideList.get(i)).get("createBy").toString());
                            }
                            vipTradeHistory.setCreateAt(new Date());
                            vipTradeHistory.setLastUpdateAt(new Date());
                            if (params.get("token") != null) {
                                vipTradeHistory.setToken(params.get("token").toString());
                            }
                            vipTradeHistory.setPaymentId(payId);
                            vipTradeHistory.setTenantId(vip.getTenantId());
                            vipTradeHistoryMapper.insert(vipTradeHistory);
                            BigInteger branchId = null;
                            if (params.get("branchId") != null) {
                                branchId = new BigInteger(params.get("branchId").toString());
                            } else {
                                branchId = vip.getBranchId();
                            }
                            Branch branch = branchMapper.findBranchByTenantIdAndBranchId(vip.getTenantId(), branchId);
                            if (vip.getPhone() != null && !vip.getPhone().equals("")) {
                                Map smsMap = new HashMap();
                                smsMap.put("type", 1);
                                smsMap.put("tenantId", vip.getTenantId().toString());
                                smsMap.put("number", vip.getPhone());
                                if (branch != null) {
                                    DecimalFormat df = new DecimalFormat("0.00");
                                    smsMap.put("branchId", branchId.toString());
                                    smsMap.put("content", (amount + "###" + df.format(vip.getVipStore().doubleValue() - amount)));
                                    ApiRest ssr = smsService.sendSMS(smsMap);
                                    if (!ssr.getIsSuccess()) {
                                        LogUtil.logInfo("发送消费短信失败！vipId=" + vip.getId());
                                    }
                                }
                            }
                            if (branch != null) {
                                WeiXinUtils.sendConsumptionRemindTemplateSafe(vip.getId(), branch.getName(), BigDecimal.valueOf(amount), 0);
                            }

                        } else {
                            LogUtil.logError("pos流水上传异常:" + vip.getId() + "储值余额不足。");
                            result.setSuccess("1");
                            result.setMsg("储值余额不足!");
                            return result;
                        }
                    } else {
                        LogUtil.logError("pos流水上传异常:" + vip.getId() + "储值余额不足。");
                        result.setSuccess("1");
                        result.setMsg("储值余额不足!");
                        return result;
                    }
                    map.put("lastUpdateAt", new Date());
                    if (((Map) glideList.get(i)).get("createBy") != null) {
                        map.put("lastUpdateBy", ((Map) glideList.get(i)).get("createBy").toString());
                    }
                    vipMapper.update(map);
                    flag = true;
                }
            }
            List<Vip> vipList = new ArrayList<>();
            for (BigInteger id : idList) {
                Map<String, Object> param1 = new HashMap<>();
                param1.put("id", id);
                Vip vip = vipMapper.findByCondition(param1);
                if (vip != null) {
                    vipList.add(vip);
                }
            }
            if (flag) {
                result.setSuccess("0");
                result.setMsg("上传流水完成!");
                result.setObject(vipList);
            } else {
                result.setSuccess("1");
                result.setMsg("上传流水失败!");
                result.setObject(vipList);
            }
            long endTime = System.currentTimeMillis();
            LogUtil.logInfo("pos上传流水uploadVipGlide，结束时间:" + endTime + ",用时:" + (endTime - startTime) + "ms");
        } else {
            result.setSuccess("1");
            result.setMsg("无效的流水数据!");
        }
        PartitionCacheUtils.del(redisToken);
        return result;
    }

    /**
     * 新增交易历史
     *
     * @param tradeType    业务类型 3：非储值 2:退款（非储值）
     * @param vipId
     * @param tenantId
     * @param amount       交易金额
     * @param saleCode     交易编号
     * @param branchId
     * @param branchName
     * @param posPaymentId
     * @param cashier
     * @return
     */
    public void addVipTradeHistory(Integer tradeType, BigInteger vipId, BigInteger tenantId, BigDecimal amount, String saleCode, BigInteger branchId,
                                   String branchName, BigInteger posPaymentId, String cashier) {
        try {
            if (tradeType == 3 || tradeType == 2) {
                Map<String, Object> map = new HashMap<>();
                map.put("id", vipId);
                Vip vip = vipMapper.findByCondition(map);
                //非储值消费
                if (null != vip) {
                    //修改账户信息，并且根据会员等级积分规则积分
                    Map<String, Object> param = new HashMap<>();
                    param.put("id", vip.getTypeId());
                    param.put("tenantId", tenantId);
                    VipType vipType = vipTypeMapper.findByCondition(param);
                    if (vip.getSumConsume() != null) {
                        map.put("sumConsume", vip.getSumConsume().doubleValue() + amount.doubleValue());
                    } else {
                        map.put("sumConsume", amount);
                    }
                    //记录消费历史
                    VipTradeHistory vipTradeHistory = new VipTradeHistory();
                    vipTradeHistory.setVipId(vip.getId());
                    vipTradeHistory.setTradeNo((saleCode));
                    vipTradeHistory.setTradeType(tradeType.toString());
                    vipTradeHistory.setTradeAmount(amount);
                    vipTradeHistory.setPayAmount(amount);
                    if (branchId != null) {
                        vipTradeHistory.setTradeBranchId(branchId);
                    } else {
                        vipTradeHistory.setTradeBranchId(vip.getBranchId());
                    }
                    vipTradeHistory.setTradeDate(new Date());
                    if (branchName != null) {
                        vipTradeHistory.setTradeBranchName(branchName);
                    } else {
                        Branch branch = branchMapper.findBranchByTenantIdAndBranchId(tenantId, branchId);
                        vipTradeHistory.setTradeBranchName(branch.getName());
                    }
                    //记录应积积分
                    if (posPaymentId != null) {
                        if (branchId != null) {
                            Map payment = paymentMapper.getPaymentByIdAndBranchId(branchId, posPaymentId, vip.getTenantId());
                            if (payment.get("isScore").toString().equals("true") || payment.get("isScore").toString().equals("1")) {
                                if (vipType != null && vipType.getToSavePoints().doubleValue() == 1 && vipType.getPointsFactor() != null && vipType.getPointsFactor().doubleValue() != 0) {
                                    vipTradeHistory.setAddScore(new BigDecimal((int) amount.doubleValue() / (int) vipType.getPointsFactor().doubleValue()));
                                }
                                if ("JF".equals(payment.get("paymentCode"))) {
                                    vipTradeHistory.setPayAmount(BigDecimal.ZERO);
                                    vipTradeHistory.setIntegralAmount(amount);
                                }
                            } else {
                                vipTradeHistory.setAddScore(BigDecimal.ZERO);
                            }
                        }
                    }
                    Employee employee = employeeMapper.getEmployeeById(BigInteger.valueOf(Long.valueOf(cashier)), tenantId);
                    if (employee != null) {
                        vipTradeHistory.setCreateBy(employee.getName());
                        vipTradeHistory.setLastUpdateBy(employee.getName());
                    }
                    vipTradeHistory.setPaymentId(posPaymentId);
                    vipTradeHistory.setCreateAt(new Date());
                    vipTradeHistory.setLastUpdateAt(new Date());
                    vipTradeHistory.setTenantId(vip.getTenantId());
                    vipTradeHistoryMapper.insert(vipTradeHistory);
                    map.put("lastUpdateAt", new Date());
                    if (employee != null) {
                        map.put("lastUpdateBy", employee.getName());
                    }
                    vipMapper.update(map);
                }
            }
        } catch (Exception e) {
            LogUtil.logError(e.getMessage());
        }
    }

    /**
     * 微餐厅用储值流水上传
     *
     * @param params
     * @return
     * @throws ServiceException
     */
    public ResultJSON uploadVipGlideForCt(Map params) throws ServiceException {
        ResultJSON result = new ResultJSON();
//            if(params.get("token") != null && !"".equals(params.get("token"))){
        List<Map> glideList = GsonUntil.jsonAsList(String.valueOf(params.get("jsonData")), Map.class);
        LogUtil.logInfo("微餐厅积分消费参数：" + BeanUtils.toJsonStr(params));
        List<BigInteger> idList = new ArrayList<>();
        if (glideList != null) {
            for (int i = 0; i < glideList.size(); i++) {
                BigInteger vipId = new BigInteger("-1");
                if (glideList.get(i).get("vipId") != null && !"".equals(glideList.get(i).get("vipId"))) {
                    vipId = new BigInteger(glideList.get(i).get("vipId").toString());
                }
                if (!idList.contains(vipId)) {
                    if (vipId.doubleValue() != -1) {
                        idList.add(vipId);
                    }
                }
                Map<String, Object> map = new HashMap<>();
                map.put("id", vipId);
                Vip vip = vipMapper.findByCondition(map);
                Branch branch = null;
                if (vip != null) {
                    Map<String, Object> bMap = new HashMap<>();
                    bMap.put("id", vip.getBranchId());
                    branch = branchMapper.find(bMap);
                }
                //积分
                String paymentCode = glideList.get(i).get("paymentCode").toString();
                Payment payment = paymentMapper.findByTenantIdAndPaymentCode(vip.getTenantId(), paymentCode);
                if ("JF".equals(glideList.get(i).get("paymentCode"))) {
                    if (null != vip) {
                        VipBook vipBook = new VipBook();
                        vipBook.setTotalScore(vip.getRemainingScore());
                        double amount = Double.parseDouble(glideList.get(i).get("amount").toString());
                        if (vip.getRemainingScore() != null) {
                            if (amount <= vip.getRemainingScore().doubleValue()) {
                                map.put("remainingScore", vip.getRemainingScore().doubleValue() - amount);
                                if (null != vip.getOverscore()) {
                                    map.put("overscore", vip.getOverscore().doubleValue() + amount);
                                } else {
                                    map.put("overscore", amount);
                                }
                            } else {
                                LogUtil.logError("pos流水上传异常:" + vip.getId() + "扣除积分大于积分余额。");
                                result.setSuccess("1");
                                result.setMsg("积分余额不足!");
                                return result;
                            }
                        }
                        if (vip.getBuyTimes() != null) {
                            map.put("buyTimes", vip.getBuyTimes() + 1);
                        } else {
                            map.put("buyTimes", 1);
                        }
                        VipType vipType = vipTypeMapper.findTypeById(vip.getTenantId(), vip.getTypeId());
                        BigDecimal costAmount = BigDecimal.ZERO;
                        if (vipType.getScoreUsage() != null && vipType.getPointsFactor().doubleValue() != 0) {
                            costAmount = BigDecimal.valueOf((int) amount / (int) vipType.getScoreUsage().doubleValue());
                        } else {
                            costAmount = BigDecimal.valueOf(amount);
                        }
                        if (vip.getSumConsume() != null) {
                            map.put("sumConsume", vip.getSumConsume().add(costAmount));
                        } else {
                            map.put("sumConsume", costAmount);
                        }
                        vipMapper.update(map);

                        //记录积分台帐

                        vipBook.setVipId(vip.getId());
                        vipBook.setTenantId(vip.getTenantId());
                        if (glideList.get(i).get("branchId") != null) {
                            vipBook.setBranchId(new BigInteger(glideList.get(i).get("branchId").toString()));
                        } else {
                            vipBook.setBranchId(vip.getBranchId());
                        }
                        vipBook.setBookType(2);
                        if (glideList.get(i).get("cashier") != null) {
                            vipBook.setOperateUserId(new BigInteger(glideList.get(i).get("cashier").toString()));
                        }
                        vipBook.setOperateBy("收银员");
                        vipBook.setOperateAt(new Date());
                        vipBook.setVipCode(vip.getVipCode());
                        vipBook.setPaymentCode(glideList.get(i).get("saleCode").toString());
                        vipBook.setVipScore(new BigDecimal(amount));
                        vipBook.setIsDeleted(false);
                        vipBook.setCreateAt(new Date());
                        vipBook.setLastUpdateAt(new Date());
                        if (params.get("createBy") != null) {
                            vipBook.setCreateBy(params.get("createBy").toString());
                        }
                        vipBook.setStoreFrom("1");
                        vipBookMapper.insert(vipBook);
                        if (vip.getPhone() != null && !vip.getPhone().equals("")) {
                            Map smsMap = new HashMap();
                            smsMap.put("type", 4);
                            smsMap.put("tenantId", vip.getTenantId().toString());
                            BigInteger branchId = vipBook.getBranchId();
                            /*if(params.get("branchId") != null){
                                branchId = new BigInteger(params.get("branchId").toString());
                            }
                            else{
                                branchId = vip.getBranchId();
                            }*/
                            smsMap.put("number", vip.getPhone());
                            Branch branch1 = branchMapper.findBranchByTenantIdAndBranchId(vip.getTenantId(), branchId);
                            if (branch1 != null) {
                                smsMap.put("branchId", branchId.toString());
                                DecimalFormat sd = new DecimalFormat("#");
                                smsMap.put("content", "积分支付###" + (int) amount + "###" + (sd.format((vip.getRemainingScore() == null ? BigDecimal.ZERO : vip.getRemainingScore()).subtract(BigDecimal.valueOf(amount)))));
                                ApiRest ssr2 = smsService.sendSMS(smsMap);
                                if (!ssr2.getIsSuccess()) {
                                    LogUtil.logInfo("发送消费短信失败！vipId=" + vip.getId());
                                }
                            }
                        }
                        WeiXinUtils.sendPointChangeTemplateSafe(vip.getId(), Math.abs(Double.valueOf(amount).intValue()) * -1, 1);
                        WeiXinUtils.updateMemberBonusSafe(vip, Double.valueOf(amount * -1).intValue(), WeiXinUtils.obtainRecordBonus(vipBook.getBookType()));
                    }
                }
                //储值消费
                if ("HYQB".equals(glideList.get(i).get("paymentCode"))) {
                    if (null != vip) {
                        VipBook vipBook = new VipBook();
                        VipTradeHistory vipTradeHistory = new VipTradeHistory();
                        vipBook.setTotalScore(vip.getRemainingScore());
                        //修改账户信息，并且根据会员等级积分规则积分
                        params.put("tenantId", vip.getTenantId());
                        Map<String, Object> param = new HashMap<>();
                        param.put("id", vip.getTypeId());
                        VipType vipType = vipTypeMapper.findByCondition(param);
                        if (vipType == null) {
                            result.setSuccess("1");
                            result.setMsg("会员类型不存在!");
                            return result;
                        }
                        double amount = 0.0;
                        if (null != glideList.get(i).get("amount")) {
                            amount = Double.parseDouble(glideList.get(i).get("amount").toString());
                        }
                        BigDecimal deposit = vipType.getDeposit() == null ? BigDecimal.ZERO : vipType.getDeposit();
                        if (deposit == null) {
                            deposit = BigDecimal.ZERO;
                        }
                        if (null != vip.getVipStore()) {
                            if (vip.getVipStore().doubleValue() >= amount) {
                                if ((vip.getVipStore().subtract(BigDecimal.valueOf(amount))).compareTo(deposit) < 0) {
                                    LogUtil.logInfo("pos流水上传异常:" + vip.getId() + "扣除押金之后储值余额不足。");
                                    result.setSuccess("1");
                                    result.setMsg("可用余额不足，押金为" + deposit + "!");
                                    return result;
                                }
                                map.put("vipStore", vip.getVipStore().doubleValue() - amount);

                                //修改累计消费次数及金额
                                if (vip.getBuyTimes() != null) {
                                    map.put("buyTimes", vip.getBuyTimes() + 1);
                                } else {
                                    map.put("buyTimes", 1);
                                }
                                if (vip.getSumConsume() != null) {
                                    map.put("sumConsume", vip.getSumConsume().doubleValue() + amount);
                                } else {
                                    map.put("sumConsume", amount);
                                }

                                //储值扣减 记录储值消费历史
                                VipStoreHistory storeHistory = new VipStoreHistory();

                                storeHistory.setVipId(vip.getId());
                                if (glideList.get(i).get("saleCode") != null) {
                                    storeHistory.setStoreCode(glideList.get(i).get("saleCode").toString());
                                }
                                storeHistory.setStoreType("3");
                                storeHistory.setPayAmount(new BigDecimal(amount));
                                storeHistory.setGiftAmount(new BigDecimal(0));
                                storeHistory.setPayType("7");
                                if (glideList.get(i).get("branchId") != null) {
                                    storeHistory.setStoreBranchId(new BigInteger(glideList.get(i).get("branchId").toString()));
                                } else {
                                    storeHistory.setStoreBranchId(vip.getBranchId());
                                }
                                storeHistory.setIsDeleted(false);
                                storeHistory.setCreateAt(new Date());
                                storeHistory.setLastUpdateAt(new Date());
                                storeHistory.setStoreFrom("1");
                                if (branch != null) {
                                    storeHistory.setStoreBranchName(branch.getName());
                                }
                                if (params.get("createBy") != null) {
                                    storeHistory.setCreateBy(params.get("createBy").toString());
                                }
                                /*消费后余额*/
                                storeHistory.setVipOperStore(BigDecimal.valueOf(vip.getVipStore().doubleValue() - amount));
                                storeHistory.setTenantId(vip.getTenantId());
                                vipStoreHistoryMapper.insert(storeHistory);

                                //记录消费历史
                                vipTradeHistory.setVipId(vip.getId());
                                vipTradeHistory.setTradeNo(glideList.get(i).get("saleCode").toString());
                                vipTradeHistory.setTradeType("1");
                                vipTradeHistory.setTradeAmount(new BigDecimal(amount));
                                vipTradeHistory.setPayAmount(new BigDecimal(amount));
                                if (glideList.get(i).get("orderCode") != null) {
                                    vipTradeHistory.setOrderCode(glideList.get(i).get("orderCode").toString());
                                }
                                if (branch != null) {
                                    vipTradeHistory.setTradeBranchName(branch.getName());
                                }
                                if (glideList.get(i).get("branchId") != null) {
                                    vipTradeHistory.setTradeBranchId(new BigInteger(glideList.get(i).get("branchId").toString()));
                                } else {
                                    vipTradeHistory.setTradeBranchId(vip.getBranchId());
                                }
                                //积分抵现
                                if (glideList.get(i).get("useIntegral") != null) {
                                    vipTradeHistory.setUseIntegral(new BigDecimal(glideList.get(i).get("useIntegral").toString()).toBigInteger());
                                    vipTradeHistory.setIntegralAmount(new BigDecimal(glideList.get(i).get("integralAmount").toString()));
                                }
                                vipTradeHistory.setTradeDate(new Date());
                            } else {
                                LogUtil.logError("pos流水上传异常:" + vip.getId() + "储值余额不足。");
                                result.setSuccess("1");
                                result.setMsg("储值余额不足!");
                                return result;
                            }
                        } else {
                            LogUtil.logError("pos流水上传异常:" + vip.getId() + "储值余额不足。");
                            result.setSuccess("1");
                            result.setMsg("储值余额不足!");
                            return result;
                        }
                        int score = 0;
                        if (vipType != null) {
                            //积分开启，则进行积分
                            if (vipType.getToSavePoints().doubleValue() == 1) {
                                Map p = new HashMap();
                                p.put("tenantId", vip.getTenantId());
                                BigInteger branchId;
                                if (glideList.get(i).get("branchId") != null) {
                                    branchId = new BigInteger(glideList.get(i).get("branchId").toString());
                                } else {
                                    branchId = vip.getBranchId();
                                }
                                p.put("branchId", branchId);
                                p.put("promotionType", 2);
                                VipSpecialPromotion vipSpecialPromotion = vipTypeMapper.findSpecialPromotionByTenantIdAndType(p);
                                boolean isDouble = false;
                                boolean isVipBirthday = false;
                                if (null != vipSpecialPromotion) {
                                    if (vipSpecialPromotion.getIsDoubleScoreBirthday() != null && vipSpecialPromotion.getIsDoubleScoreBirthday() == 1) {
                                        isDouble = true;
                                    }
                                }
                                SimpleDateFormat dateF = new SimpleDateFormat("MM-dd");
                                if (vip.getBirthday() != null && ((dateF.format(vip.getBirthday())).equals(dateF.format(new Date())))) {
                                    isVipBirthday = true;
                                }
                                if (vipType.getPointsFactor() != null && BigDecimal.ZERO.compareTo(vipType.getPointsFactor()) != 0) {
                                    score = (int) (amount / vipType.getPointsFactor().doubleValue());
                                } else {
                                    score = (int) (amount / 1);
                                }
                                if (isDouble && isVipBirthday) {
                                    score = score * 2;
                                }
                                score = score * vipService.getVipDayScoreTimes(vip.getTenantId(), branchId, new Date());
                                if (null != vip.getRemainingScore()) {
                                    map.put("remainingScore", vip.getRemainingScore().doubleValue() + score);
                                } else {
                                    map.put("remainingScore", score);
                                }
                                if (null != vip.getSumScore()) {
                                    map.put("sumScore", vip.getSumScore().doubleValue() + score);
                                } else {
                                    map.put("sumScore", score);
                                }
                            }
                        }

                        vipTradeHistory.setAddScore(new BigDecimal(score));
                        vipTradeHistory.setCreateAt(new Date());
                        vipTradeHistory.setLastUpdateAt(new Date());
                        vipTradeHistory.setTenantId(vip.getTenantId());
                        vipTradeHistory.setPaymentId(payment.getId() == null ? BigInteger.ZERO : payment.getId());
                        vipTradeHistoryMapper.insert(vipTradeHistory);
                        vipMapper.update(map);

                        //记录积分台帐
                        vipBook.setVipId(vip.getId());
                        vipBook.setTenantId(vip.getTenantId());

                        if (glideList.get(i).get("branchId") != null) {
                            vipBook.setBranchId(new BigInteger(glideList.get(i).get("branchId").toString()));
                        } else {
                            vipBook.setBranchId(vip.getBranchId());
                        }

//                        vipBook.setBranchId(vip.getBranchId());
                        vipBook.setBookType(1);
                        if (null != glideList.get(i).get("cashier")) {
                            vipBook.setOperateUserId(new BigInteger(glideList.get(i).get("cashier").toString()));
                        }
                        vipBook.setOperateBy("收银员");
                        vipBook.setOperateAt(new Date());
                        vipBook.setVipCode(vip.getVipCode());
                        vipBook.setPaymentCode(glideList.get(i).get("saleCode").toString());
                        vipBook.setVipScore(new BigDecimal(score));
                        vipBook.setSumConsume(new BigDecimal(amount));
                        vipBook.setTotal(new BigDecimal(amount));
                        vipBook.setRealTotal(new BigDecimal(amount));
                        vipBook.setCreateAt(new Date());
                        vipBook.setLastUpdateAt(new Date());
                        vipBook.setStoreFrom("1");
                        if (score != 0) {
                            vipBookMapper.insert(vipBook);
                        }
                        LogUtil.logInfo("vip结果：" + BeanUtils.toJsonStr(vip));
                        BigInteger branchId = null;
                        if (glideList.get(i).get("branchId") != null) {
                            branchId = new BigInteger(glideList.get(i).get("branchId").toString());
                        } else {
                            branchId = vip.getBranchId();
                        }
                        Branch branch1 = branchMapper.findBranchByTenantIdAndBranchId(vip.getTenantId(), branchId);
                        if (vip.getPhone() != null && !vip.getPhone().equals("")) {
                            Map smsMap = new HashMap();
                            smsMap.put("type", 1);
                            smsMap.put("tenantId", vip.getTenantId().toString());

                            smsMap.put("number", vip.getPhone());
                            LogUtil.logInfo("查询的机构是：" + BeanUtils.toJsonStr(branch1));
                            if (branch1 != null) {
                                if (amount != 0) {
                                    DecimalFormat df = new DecimalFormat("0.00");
                                    smsMap.put("branchId", branchId.toString());
                                    smsMap.put("content", (amount + "###" + df.format(vip.getVipStore().doubleValue() - amount)));
                                    ApiRest ssr = smsService.sendSMS(smsMap);
                                    if (!ssr.getIsSuccess()) {
                                        LogUtil.logInfo("发送消费短信失败！vipId=" + vip.getId());
                                    }
                                }
                                if (score != 0) {
                                    DecimalFormat sd = new DecimalFormat("#");
                                    smsMap.put("type", 3);
                                    smsMap.put("content", "消费###" + score + "###" + (sd.format((vip.getRemainingScore() == null ? BigDecimal.ZERO : vip.getRemainingScore()).add(BigDecimal.valueOf(score)))));
                                    ApiRest ssr2 = smsService.sendSMS(smsMap);
                                    if (!ssr2.getIsSuccess()) {
                                        LogUtil.logInfo("发送消费短信失败！vipId=" + vip.getId());
                                    }
                                }
                            }
                        }
                        if (branch1 != null) {
                            WeiXinUtils.sendConsumptionRemindTemplateSafe(vip.getId(), branch1.getName(), BigDecimal.valueOf(amount), score);
                        }
                        WeiXinUtils.updateMemberBonusSafe(vip, score, WeiXinUtils.obtainRecordBonus(vipBook.getBookType()));
                    }

                }
            }
        }
        List<Vip> vipList = new ArrayList<>();
        for (BigInteger id : idList) {
            Map<String, Object> param1 = new HashMap<>();
            param1.put("id", id);
            Vip vip = vipMapper.findByCondition(param1);
            if (vip != null) {
                vipList.add(vip);
            }
        }
        result.setSuccess("0");
        result.setMsg("上传流水完成!");
        result.setObject(vipList);
//            }
//            else{
//                result.setSuccess("1");
//                result.setMsg("无效的流水数据!");
//            }
        return result;
    }
//
//    /**
//     * 根据会员id查询会员积分历史
//     * @author szq
//     */
//    public Map<String,Object> listVipBookHistory(Map params) {
//        Map<String,Object> map = new HashMap<String,Object>();
//        try {
//            StringBuffer query = new StringBuffer("from VipBook v where 1=1");
//            StringBuffer queryCount = new StringBuffer("select count(*) from VipBook v where 1=1");
//            def queryParams = new HashMap();
//            queryParams.max = params.rows;
//            queryParams.offset = (Integer.parseInt(params.page) - 1) * Integer.parseInt(params.rows);
//            def namedParams = new HashMap();
//
//            params.each { k, v ->
//                if ('branchId'.equals(k) && v) {
//                    query.append(' AND v.branchId= :branchId ')
//                    queryCount.append(' AND v.branchId= :branchId ')
//                    namedParams.branchId = v.asType(BigInteger)
//                }
//                if ('tenantId'.equals(k) && v) {
//                    query.append(' AND v.tenantId= :tenantId ')
//                    queryCount.append(' AND v.tenantId= :tenantId ')
//                    namedParams.tenantId = v.asType(BigInteger)
//                }
//                //按会员编号 名称 查询
//                if ('queryStr'.equals(k) && v) {
//                    query.append(" AND (v.vipCode like '%"+v.toString()+"%' )" )
//                    queryCount.append(" AND (v.vipCode like '%"+v.toString()+"%' )")
//                }
//
//                //根据会员id 查询
//                if("vipId".equals(k) && v) {
//                    query.append(" AND v.vipId = :vipId ")
//                    queryCount.append(" AND v.vipId = :vipId ")
//                    namedParams.vipId = v.asType(BigInteger)
//                }
//            }
//
//            def list = VipBook.executeQuery(query.toString(), namedParams, queryParams);
//            def count = VipBook.executeQuery(queryCount.toString(), namedParams);
//            map.put("total", count.size() > 0 ? count[0] : 0);
//            map.put("rows", list);
//        } catch (Exception e) {
//            LogUtil.logError("查询会员积分历史异常" + e.message);
//            throw new ServiceException();
//        }
//        return map;
//    }
//
//    public Map<String, Object> queryVipBookHistory(Map params) {
//        Map<String, Object> map = new HashMap<String, Object>();
//        try {
//            BigInteger tenantId = new BigInteger(params.tenantId);
//            String query = "select v.id vipId,v.tenant_id tenantId,v.original_id originalId,v.type_id typeId,v.vip_code vipCode,v.vip_name vipName," +
//                    "v.sex sex,v.birthday birthday,v.phone phone,v.email email,v.memo vipMemo,v.status status,v.buy_times buyTimes,v.sum_consume sumConsume," +
//                    "v.sum_feed sumFeed,v.sum_score sumScore,v.remaining_score reaminingScore,v.largessscore largessscore,v.overscore overscore," +
//                    "v.vip_store_total vipStoreTotal,v.vip_store vipStore,v.reg_date regDate,v.reg_source regSource,vb.* " +
//                    "from vip v right join vip_book vb on v.id=vb.vip_id where v.tenant_id=${tenantId}";
//            String queryCount = "select count(*) from vip v right join vip_book vb on v.id=vb.vip_id where v.tenant_id=${tenantId}";
//            Integer max = params['rows']?.asType(int);
//            Integer page = params['page']?.asType(int);
//            def sq = getSession().createSQLQuery(query);
//            def cq = getSession().createSQLQuery(queryCount);
//            if (max != null && page != null) {//分页查询
//                sq.setMaxResults(max)
//                sq.setFirstResult((page - 1) * max)
//            }
//            def countNum = cq.list()[0]
//            def list = [];
//            if (countNum > 0) {
//                list = sq.list()
//            }
//
//            map = ["total": countNum, "rows": list]
//        } catch (Exception e) {
//            LogUtil.logError("会员积分历史查询异常" + e.message);
//            throw new ServiceException();
//        }
//        return map
//    }
}
