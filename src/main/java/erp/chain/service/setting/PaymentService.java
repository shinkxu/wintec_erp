package erp.chain.service.setting;

import com.saas.common.util.LogUtil;
import erp.chain.domain.Branch;
import erp.chain.domain.Payment;
import erp.chain.domain.PaymentBranch;
import erp.chain.mapper.BranchMapper;
import erp.chain.mapper.CommonMapper;
import erp.chain.mapper.PaymentMapper;
import erp.chain.service.system.OperateLogService;
import erp.chain.service.system.TenantConfigService;
import erp.chain.utils.CommonUtils;
import erp.chain.utils.SerialNumberGenerate;
import net.sf.json.JSONArray;
import org.apache.commons.lang.Validate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import saas.api.SaaSApi;
import saas.api.common.ApiRest;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;

/**
 * 支付方式
 * Created with IntelliJ IDEA.
 * User: AnCong
 * Date: 2016/12/6
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class PaymentService{
    @Autowired
    private CommonMapper commonMapper;
    @Autowired
    private PaymentMapper paymentMapper;
    @Autowired
    private TenantConfigService tenantConfigService;
    @Autowired
    private BranchMapper branchMapper;
    @Autowired
    private OperateLogService operateLogService;

    /**
     * 支付方式列表
     *
     * @param params
     * @return
     */
    public ApiRest queryPayment(Map params){
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
        if(params.get("field") != null && !params.get("field").toString().equals("") && params.get("order") != null && !params.get("order").toString().equals("")){
            params.put("field", CommonUtils.formatOrderField(params.get("field").toString()));
            params.put("order", params.get("order"));
        }
        /*Branch branch=branchMapper.getMainBranch(tenantId.toString());
        if(branch==null){
            apiRest.setIsSuccess(false);
            apiRest.setError("查询总部机构失败！");
            return apiRest;
        }
        params.put("branchId",branch.getId());*/
        List<Map> list = paymentMapper.queryPayment(params);
        Long count = paymentMapper.queryPaymentSum(params);
        map.put("total", count);
        map.put("rows", list);
        apiRest.setIsSuccess(true);
        apiRest.setData(map);
        apiRest.setMessage("查询支付方式成功！");
        return apiRest;
    }
    /**
     * 支付方式列表(包含已删除的支付方式)为支付流水支付方式创建
     *
     * @param params
     * @return
     */
    public ApiRest queryPaymentListByPayment(Map params){
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
        /*Branch branch=branchMapper.getMainBranch(tenantId.toString());
        if(branch==null){
            apiRest.setIsSuccess(false);
            apiRest.setError("查询总部机构失败！");
            return apiRest;
        }
        params.put("branchId",branch.getId());*/
        List<Map> list = paymentMapper.queryPaymentListByPayment(params);
        Long count = paymentMapper.queryPaymentListByPaymentSum(params);
        map.put("total", count);
        map.put("rows", list);
        apiRest.setIsSuccess(true);
        apiRest.setData(map);
        apiRest.setMessage("查询支付方式成功！");
        return apiRest;
    }
    /**
     * 根据ID查询支付方式
     *
     * @param params
     * @return
     */
    public ApiRest getPaymentById(Map params){
        ApiRest apiRest = new ApiRest();
        BigInteger branchId = BigInteger.valueOf(Long.parseLong((String)params.get("branchId")));
        BigInteger tenantId = BigInteger.valueOf(Long.parseLong((String)params.get("tenantId")));
        BigInteger id = BigInteger.valueOf(Long.parseLong((String)params.get("id")));
        Map payment = paymentMapper.getPaymentByIdAndBranchId(branchId, id, tenantId);
        if(payment == null || payment.isEmpty()){
            apiRest.setIsSuccess(false);
            apiRest.setError("查询支付方式失败！");
            return apiRest;
        }
        apiRest.setIsSuccess(true);
        apiRest.setData(payment);
        apiRest.setMessage("查询支付方式成功！");
        return apiRest;
    }

    /**
     * 新增或修改支付方式
     *
     * @param params
     * @return
     */
    public ApiRest saveOrUpdatePayment(Map params) throws IllegalAccessException{
        ApiRest apiRest = new ApiRest();
        BigInteger tenantId = BigInteger.valueOf(Long.parseLong((String)params.get("tenantId")));
        BigInteger branchId = BigInteger.valueOf(Long.parseLong((String)params.get("branchId")));
        Branch branch = branchMapper.findBranchByTenantIdAndBranchId(tenantId, branchId);
        ApiRest are = SaaSApi.findTenantById(tenantId);
        Map tenant = new HashMap();
        if(are.getIsSuccess()){
            tenant = (Map)(((Map)are.getData()).get("tenant"));
        }
        if(params.get("id") != null && !params.get("id").equals("")){
            BigInteger id = BigInteger.valueOf(Long.parseLong((String)params.get("id")));
            String userName = params.get("userName") == null ? "" : params.get("userName").toString();
            String opFrom = params.get("opFrom") == null ? "" : params.get("opFrom").toString();
            Payment payment = paymentMapper.getPaymentById(id, tenantId);
            Map oldPayment = CommonUtils.ObjectToMap(payment);
            if(payment == null){
                apiRest.setIsSuccess(false);
                apiRest.setError("支付方式不存在！");
                return apiRest;
            }
            if(branchId.equals(payment.getBranchId())){
                Long count = paymentMapper.checkPaymentName(id, tenantId, params.get("paymentName").toString(),branchId);
                if(count > 0){
                    apiRest.setIsSuccess(false);
                    apiRest.setError("名字已存在，请重新命名");
                    return apiRest;
                }
                if(Integer.valueOf(params.get("paymentStatus").toString()) == 0){
                    Long enableCount = paymentMapper.checkStatus(branchId, id, tenantId);
                    if(enableCount >= 20){
                        apiRest.setIsSuccess(false);
                        apiRest.setError("最多可以启用20种支付方式");
                        return apiRest;
                    }
                    else{
                        payment.setPaymentStatus(0);
                    }
                }
                else{
                    payment.setPaymentStatus(Integer.valueOf(params.get("paymentStatus").toString()));
                }
                String name = params.get("paymentName").toString();
                payment.setIsScore(Integer.valueOf(params.get("isScore").toString()) == 1);
                payment.setPaymentType(1);
                payment.setIsVoucher(0);
                payment.setFixNum(0);
                payment.setFixValue(BigDecimal.ZERO);
                if(payment.getPaymentCode().equals("CSH") || payment.getPaymentCode().equals("DJQ") || payment.getPaymentCode().equals("CRD") || payment.getPaymentCode().equals("ZFB") ||
                        payment.getPaymentCode().equals("WX") || payment.getPaymentCode().equals("JF") || payment.getPaymentCode().equals("HYQB")||payment.getPaymentCode().equals("MTTG") ||
                        payment.getPaymentCode().equals("HZF") || payment.getPaymentCode().equals("CKZF") || payment.getPaymentCode().equals("YHQ") || payment.getPaymentCode().equals("MYQ")||
                        payment.getPaymentCode().equals("MZK")){
                    apiRest.setMessage("修改支付方式成功,“" + payment.getPaymentName() + "”支付方式名称不允许修改！");
                }
                else{
                    payment.setPaymentName(name);
                }
                payment.setLastUpdateAt(new Date());
                payment.setLastUpdateBy("System");
                payment.setVersion(payment.getVersion().add(BigInteger.ONE));
                if(params.get("isStore") != null && !params.get("isStore").equals("")){
                    payment.setIsStore(Integer.valueOf(params.get("isStore").toString()) == 1);
                }
                if(params.get("isOpenCashbox") != null && !params.get("isOpenCashbox").equals("")){
                    payment.setIsOpenCashbox(Integer.valueOf(params.get("isOpenCashbox").toString()) == 1);
                }
                if(branch.getBranchType() == 0){
                    List<PaymentBranch> paymentBranchList = paymentMapper.listPaymentBranchByPaymentId(payment.getId());
                    for(PaymentBranch paymentBranch : paymentBranchList){
                        paymentBranch.setLastUpdateAt(payment.getLastUpdateAt());
                        paymentBranch.setLastUpdateBy(payment.getLastUpdateBy());
                        paymentBranch.setPaymentName(payment.getPaymentName());
                        paymentBranch.setIsScore(payment.getIsScore());
                        paymentBranch.setIsVoucher(payment.getIsVoucher());
                        paymentBranch.setFixNum(payment.getFixNum());
                        paymentBranch.setFixValue(payment.getFixValue());
                        paymentBranch.setVersion(paymentBranch.getVersion().add(BigInteger.ONE));
                        paymentBranch.setIsStore(payment.getIsStore());
                        paymentBranch.setIsOpenCashbox(payment.getIsOpenCashbox());
                        int a = paymentMapper.updatePaymentBranch(paymentBranch);
                        if(a < 0){
                            apiRest.setIsSuccess(false);
                            apiRest.setError("修改分店支付方式失败！");
                        }
                        else{
                            apiRest.setIsSuccess(true);
                            apiRest.setMessage("修改支付方式成功！");
                        }
                    }
                }
                int i = paymentMapper.update(payment);
                if(i < 0){
                    apiRest.setIsSuccess(false);
                    apiRest.setError("修改支付方式失败！");
                }
                else{
                    operateLogService.saveLog(payment, oldPayment, 2, opFrom, userName, payment.getTenantId(), payment.getBranchId());
                    Map payment1 = paymentMapper.getPaymentWithBusiness(payment.getId());
                    if(tenant.get("business1").equals("1")){
                        apiRest.setData(payment);
                    }
                    else if(tenant.get("business1").equals("2")){
                        apiRest.setData(payment1);
                    }
                    apiRest.setIsSuccess(true);
                    apiRest.setMessage(apiRest.getMessage() == null ? "修改支付方式成功！" : apiRest.getMessage());
                }

            }
            else{
                PaymentBranch paymentBranch = paymentMapper.getPaymentBranchByPaymentId(id, branchId);
                if(paymentBranch == null){
                    apiRest.setIsSuccess(false);
                    apiRest.setError("查询机构支付方式失败！");
                    return apiRest;
                }
                if((params.get("isScore").toString().equals("1")) != paymentBranch.getIsScore()){
                    apiRest.setIsSuccess(false);
                    apiRest.setError("您无法修改此支付方式的积分状态！");
                    return apiRest;
                }
                if(Integer.valueOf(params.get("paymentStatus").toString()) == 0){
                    Long enableCount = paymentMapper.checkStatus(branchId, id, tenantId);
                    if(enableCount >= 20){
                        apiRest.setIsSuccess(false);
                        apiRest.setError("最多可以启用20种支付方式");
                        return apiRest;
                    }
                    else{
                        paymentBranch.setPaymentStatus(0);
                    }
                }
                else{
                    paymentBranch.setPaymentStatus(Integer.valueOf(params.get("paymentStatus").toString()));
                }
                if(params.get("isStore") != null && !params.get("isStore").equals("")){
                    paymentBranch.setIsStore(Integer.valueOf(params.get("isStore").toString()) == 1);
                }
                if(params.get("isOpenCashbox") != null && !params.get("isOpenCashbox").equals("")){
                    paymentBranch.setIsOpenCashbox(Integer.valueOf(params.get("isOpenCashbox").toString()) == 1);
                }
                paymentBranch.setLastUpdateAt(new Date());
                paymentBranch.setLastUpdateBy("System");
                paymentBranch.setVersion(paymentBranch.getVersion().add(BigInteger.ONE));
                int i = paymentMapper.updatePaymentBranch(paymentBranch);
                if(i < 0){
                    apiRest.setIsSuccess(false);
                    apiRest.setError("修改机构支付方式失败！");
                }
                else{
                    Map paymentBranch1 = paymentMapper.getPaymentBranchWithBusiness(id);
                    if(tenant.get("business1").equals("1")){
                        apiRest.setData(paymentBranch);
                    }
                    else if(tenant.get("business1").equals("2")){
                        apiRest.setData(paymentBranch1);
                    }
                    apiRest.setIsSuccess(true);
                    apiRest.setMessage(apiRest.getMessage() == null ? "修改机构支付方式成功！" : apiRest.getMessage());
                }
            }
        }
        else{
                /*ApiRest i=tenantConfigService.checkConfig(tenantId, SysConfig.SYS_PAYMENT_NUM);
                if(!i.getIsSuccess()){
                    apiRest=i;
                    return apiRest;
                }*/
                /*Branch branch=branchMapper.getMainBranch(tenantId.toString());
                if(branch==null){
                    apiRest.setIsSuccess(false);
                    apiRest.setError("查询总部机构失败！");
                    return apiRest;
                }*/
            //branchId=branch.getId();
            Payment payment1 = new Payment();
            Long count = paymentMapper.checkPaymentName(null, tenantId, params.get("paymentName").toString(),branchId);//查看名字是否已存在
            if(count > 0){
                apiRest.setIsSuccess(false);
                apiRest.setError("名字已存在，请重新命名");
                return apiRest;
            }
            ApiRest rest = getPaymentCode(params);//自定义编码
            if(!rest.getIsSuccess()){
                apiRest.setIsSuccess(false);
                apiRest.setError("获取支付方式编码失败！");
                return apiRest;
            }
            if(Integer.valueOf(params.get("paymentStatus").toString()) == 0){
                Long enableCount = paymentMapper.checkStatus(branchId, null, tenantId);
                if(enableCount >= 20){
                    apiRest.setIsSuccess(false);
                    apiRest.setError("最多可以启用20种支付方式");
                    return apiRest;
                }
                else{
                    payment1.setPaymentStatus(0);
                }
            }
            else{
                payment1.setPaymentStatus(Integer.valueOf(params.get("paymentStatus").toString()));
            }
            payment1.setPaymentCode(rest.getData().toString());
            payment1.setTenantId(tenantId);
            payment1.setBranchId(branchId);
            payment1.setPaymentName(params.get("paymentName").toString());

            payment1.setIsScore(Integer.valueOf(params.get("isScore").toString()) == 1);
            payment1.setPaymentType(1);
            payment1.setIsVoucher(0);
            payment1.setFixNum(0);
            payment1.setCurrencyId(BigInteger.ONE);
            payment1.setFixValue(BigDecimal.ZERO);
            payment1.setLastUpdateAt(new Date());
            payment1.setLastUpdateBy("System");
            payment1.setCreateAt(new Date());
            payment1.setCreateBy("System");
            payment1.setVersion(BigInteger.ONE);
            payment1.setIsDeleted(false);
            if(params.get("isStore") != null && !params.get("isStore").equals("")){
                payment1.setIsStore(Integer.valueOf(params.get("isStore").toString()) == 1);
            }
            if(params.get("isOpenCashbox") != null && !params.get("isOpenCashbox").equals("")){
                payment1.setIsOpenCashbox(Integer.valueOf(params.get("isOpenCashbox").toString()) == 1);
            }
            int j = paymentMapper.insert(payment1);
            if(j < 0){
                apiRest.setIsSuccess(false);
                apiRest.setError("新增支付方式失败！");
            }
            else{
                apiRest.setIsSuccess(true);
                apiRest.setMessage("新增支付方式成功！");
                Map payment2 = paymentMapper.getPaymentWithBusiness(payment1.getId());
                if(tenant.get("business1").equals("1")){
                    apiRest.setData(payment1);
                }
                else if(tenant.get("business1").equals("2")){
                    apiRest.setData(payment2);
                }
            }
            if(branch.getBranchType() == 0){
                List<Branch> childBranchList = branchMapper.getChildBranches(tenantId);
                for(Branch branch1 : childBranchList){
                    PaymentBranch paymentBranch = new PaymentBranch();
                    if(Integer.valueOf(params.get("paymentStatus").toString()) == 0){
                        Long enableCount = paymentMapper.checkStatus(branch1.getId(), null, tenantId);
                        if(enableCount >= 20){
                            paymentBranch.setPaymentStatus(1);
                        }
                        else{
                            paymentBranch.setPaymentStatus(0);
                        }
                    }
                    else{
                        paymentBranch.setPaymentStatus(payment1.getPaymentStatus());
                    }
                    paymentBranch.setCreateAt(new Date());
                    paymentBranch.setCreateBy("System");
                    paymentBranch.setLastUpdateAt(new Date());
                    paymentBranch.setCurrencyId(BigInteger.ONE);
                    paymentBranch.setLastUpdateBy("System");
                    paymentBranch.setTenantPaymentId(payment1.getId());
                    paymentBranch.setPaymentCode(payment1.getPaymentCode());
                    paymentBranch.setPaymentName(payment1.getPaymentName());
                    paymentBranch.setPaymentStatus(payment1.getPaymentStatus());
                    paymentBranch.setBranchId(branch1.getId());
                    paymentBranch.setIsScore(payment1.getIsScore());
                    paymentBranch.setIsChange(payment1.getChange());
                    paymentBranch.setIsMemo(payment1.getMemo());
                    paymentBranch.setIsSale(payment1.getSale());
                    paymentBranch.setFixValue(payment1.getFixValue());
                    paymentBranch.setFixNum(payment1.getFixNum());
                    paymentBranch.setPaymentType(payment1.getPaymentType());
                    paymentBranch.setIsVoucher(payment1.getIsVoucher());
                    paymentBranch.setVersion(BigInteger.ONE);
                    paymentBranch.setTenantId(tenantId);
                    paymentBranch.setDeleted(false);
                    paymentBranch.setIsStore(payment1.getIsStore());
                    paymentBranch.setIsOpenCashbox(payment1.getIsOpenCashbox());
                    int k = paymentMapper.insertBranchPayment(paymentBranch);
                    if(k < 0){
                        apiRest.setIsSuccess(false);
                        apiRest.setError("新增机构支付方式失败！");
                    }
                    else{
                        apiRest.setIsSuccess(true);
                        apiRest.setMessage("新增支付方式成功！");
                    }
                }
            }
        }
        return apiRest;
    }

    /**
     * 启用或者禁用支付方式
     * 修改是否积分
     *
     * @param params
     * @return
     */
    public ApiRest isEnablePayment(Map params) throws IllegalAccessException{
        ApiRest apiRest = new ApiRest();
        BigInteger id = BigInteger.valueOf(Long.valueOf(params.get("id").toString()));
        BigInteger tenantId = BigInteger.valueOf(Long.valueOf(params.get("tenantId").toString()));
        BigInteger branchId = BigInteger.valueOf(Long.valueOf(params.get("branchId").toString()));
        ApiRest are = SaaSApi.findTenantById(tenantId);
        Map tenant = new HashMap();
        if(are.getIsSuccess()){
            tenant = (Map)(((Map)are.getData()).get("tenant"));
        }
        Branch branch = branchMapper.findBranchByTenantIdAndBranchId(tenantId, branchId);
        String userName = params.get("userName") == null ? "" : params.get("userName").toString();
        String opFrom = params.get("opFrom") == null ? "" : params.get("opFrom").toString();
        Payment payment = paymentMapper.getPaymentById(id, tenantId);
        Map oldPayment = CommonUtils.ObjectToMap(payment);
        if(payment == null){
            apiRest.setIsSuccess(false);
            apiRest.setError("支付方式不存在！");
            return apiRest;
        }
        if(branchId.equals(payment.getBranchId())){
            if(params.get("paymentStatus") != null && !params.get("paymentStatus").equals("")){
                if(Integer.valueOf(params.get("paymentStatus").toString()) == 0){
                    Long enableCount = paymentMapper.checkStatus(branchId, id, tenantId);
                    if(enableCount >= 20){
                        apiRest.setIsSuccess(false);
                        apiRest.setError("最多可以启用20种支付方式");
                        return apiRest;
                    }
                    else{
                        payment.setPaymentStatus(0);
                    }
                }
                else{
                    payment.setPaymentStatus(Integer.valueOf(params.get("paymentStatus").toString()));
                }
            }
            if(params.get("isScore") != null && !params.get("isScore").equals("")){
                payment.setIsScore(Integer.valueOf(params.get("isScore").toString()) == 1);
            }
            payment.setLastUpdateAt(new Date());
            payment.setLastUpdateBy("System");
            payment.setVersion(payment.getVersion().add(BigInteger.ONE));
            if(branch.getBranchType() == 0){
                List<PaymentBranch> paymentBranchList = paymentMapper.listPaymentBranchByPaymentId(payment.getId());
                for(PaymentBranch paymentBranch : paymentBranchList){
                    paymentBranch.setLastUpdateAt(payment.getLastUpdateAt());
                    paymentBranch.setLastUpdateBy(payment.getLastUpdateBy());
                    paymentBranch.setIsScore(payment.getIsScore());
                    paymentBranch.setVersion(paymentBranch.getVersion().add(BigInteger.ONE));
                    int a = paymentMapper.updatePaymentBranch(paymentBranch);
                    if(a < 0){
                        apiRest.setIsSuccess(false);
                        apiRest.setError("修改分店支付方式失败！");
                    }
                    else{
                        apiRest.setIsSuccess(true);
                        apiRest.setMessage("修改支付方式成功！");
                    }
                }
            }
            int i = paymentMapper.update(payment);
            if(i < 0){
                apiRest.setIsSuccess(false);
                apiRest.setError("修改支付方式失败！");
            }
            else{
                operateLogService.saveLog(payment, oldPayment, 2, opFrom, userName, payment.getTenantId(), payment.getBranchId());
                apiRest.setIsSuccess(true);
                apiRest.setMessage(apiRest.getMessage() == null ? "修改支付方式成功！" : apiRest.getMessage());
                Map payment1 = paymentMapper.getPaymentWithBusiness(payment.getId());
                if(tenant.get("business1").equals("1")){
                    apiRest.setData(payment);
                }
                else if(tenant.get("business1").equals("2")){
                    apiRest.setData(payment1);
                }
            }
        }
        else{
            PaymentBranch paymentBranch = paymentMapper.getPaymentBranchByPaymentId(id, branchId);
            if(paymentBranch == null){
                apiRest.setIsSuccess(false);
                apiRest.setError("查询机构支付方式失败！");
                return apiRest;
            }
            if(params.get("paymentStatus") != null && !params.get("paymentStatus").equals("")){
                if(Integer.valueOf(params.get("paymentStatus").toString()) == 0){
                    Long enableCount = paymentMapper.checkStatus(branchId, id, tenantId);
                    if(enableCount >= 20){
                        apiRest.setIsSuccess(false);
                        apiRest.setError("最多可以启用20种支付方式");
                        return apiRest;
                    }
                    else{
                        paymentBranch.setPaymentStatus(0);
                    }
                }
                else{
                    paymentBranch.setPaymentStatus(Integer.valueOf(params.get("paymentStatus").toString()));
                }
            }
            if(params.get("isScore") != null && !params.get("isScore").equals("")){
                apiRest.setIsSuccess(false);
                apiRest.setError("您不能修改此支付方式的积分状态");
                apiRest.setMessage("您不能修改此支付方式的积分状态");
                return apiRest;
            }
            paymentBranch.setLastUpdateAt(new Date());
            paymentBranch.setLastUpdateBy("System");
            paymentBranch.setVersion(paymentBranch.getVersion().add(BigInteger.ONE));
            int i = paymentMapper.updatePaymentBranch(paymentBranch);
            if(i < 0){
                apiRest.setIsSuccess(false);
                apiRest.setError("修改机构支付方式失败！");
            }
            else{
                apiRest.setIsSuccess(true);
                apiRest.setMessage(apiRest.getMessage() == null ? "修改机构支付方式成功！" : apiRest.getMessage());
                Map paymentBranch1 = paymentMapper.getPaymentBranchWithBusiness(id);
                if(tenant.get("business1").equals("1")){
                    apiRest.setData(paymentBranch);
                }
                else if(tenant.get("business1").equals("2")){
                    apiRest.setData(paymentBranch1);
                }
            }
        }
        return apiRest;
    }
    /**
     * 查询门店支付方式
     */
    public ApiRest listBranchsPayment(Map params){
        ApiRest apiRest=new ApiRest();
        BigInteger tenantId= new BigInteger(params.get("tenantId").toString());
        String branchId= params.get("branchId").toString();
        try {
            List<Payment>  paymentList = paymentMapper.listBranchsPayment(tenantId, branchId);
            apiRest.setIsSuccess(true);
            apiRest.setMessage("查询门店支付方式成功");
            apiRest.setData(paymentList);
        }catch(Exception e){
            apiRest.setIsSuccess(false);
            apiRest.setMessage("查询门店支付方式失败");
        }
        return apiRest;
    }

    /**
     * 新增或修改支付方式
     *
     * @param params
     * @return
     */
    public ApiRest addOrUpdatePayment(Map params) throws IllegalAccessException{
        ApiRest apiRest = new ApiRest();
        BigInteger tenantId = BigInteger.valueOf(Long.parseLong((String)params.get("tenantId")));
        BigInteger branchId = BigInteger.valueOf(Long.parseLong((String)params.get("branchId")));
        Branch branch = branchMapper.findBranchByTenantIdAndBranchId(tenantId, branchId);
        if(params.get("id") != null && !params.get("id").equals("")){
            BigInteger id = BigInteger.valueOf(Long.parseLong((String)params.get("id")));
            String userName = params.get("userName") == null ? "" : params.get("userName").toString();
            String opFrom = params.get("opFrom") == null ? "" : params.get("opFrom").toString();
            Payment payment = paymentMapper.getPaymentById(id, tenantId);
            Map oldPayment = CommonUtils.ObjectToMap(payment);
            if(payment == null){
                apiRest.setIsSuccess(false);
                apiRest.setError("支付方式不存在！");
                return apiRest;
            }
            if(branchId.equals(payment.getBranchId())){
                Long count = paymentMapper.checkPaymentName(id, tenantId, params.get("paymentName").toString(),branchId);
                if(count > 0){
                    apiRest.setIsSuccess(false);
                    apiRest.setError("名字已存在，请重新命名");
                    return apiRest;
                }
                if(branch.getBranchType()>0){ //0为总部,分门店修改自己支付方式
                    Long num= paymentMapper.checkPaymentBranchName(null,tenantId,params.get("paymentName").toString(),branchId);
                    if(num > 0){
                        apiRest.setIsSuccess(false);
                        apiRest.setError("名字已存在，请重新命名");
                        return apiRest;
                    }
                }

                if(Integer.valueOf(params.get("paymentStatus").toString()) == 0){
                    Long enableCount = paymentMapper.checkStatus(branchId, id, tenantId);
                    if(enableCount >= 20){
                        apiRest.setIsSuccess(false);
                        apiRest.setError("最多可以启用20种支付方式");
                        return apiRest;
                    }
                    else{
                        payment.setPaymentStatus(0);
                    }
                }
                else{
                    payment.setPaymentStatus(Integer.valueOf(params.get("paymentStatus").toString()));
                }
                String name = params.get("paymentName").toString();
                payment.setIsScore(Integer.valueOf(params.get("isScore").toString()) == 1);
                payment.setPaymentType(1);
                payment.setIsVoucher(Integer.valueOf(params.get("isVoucher").toString()));
                if(params.get("isVoucher").equals("0")){
                    payment.setFixNum(0);
                    payment.setFixValue(BigDecimal.ZERO);
                }
                else{
                    payment.setFixValue(BigDecimal.valueOf(Double.valueOf(params.get("fixValue").toString())));
                    payment.setFixNum(Integer.valueOf(params.get("fixNum").toString()));
                }
                if(payment.getPaymentCode().equals("CSH") || payment.getPaymentCode().equals("DJQ") || payment.getPaymentCode().equals("CRD") || payment.getPaymentCode().equals("ZFB") ||
                        payment.getPaymentCode().equals("WX") || payment.getPaymentCode().equals("JF") || payment.getPaymentCode().equals("HYQB") ||
                        payment.getPaymentCode().equals("HZF") || payment.getPaymentCode().equals("YHQ") || payment.getPaymentCode().equals("MYQ") ||
                        payment.getPaymentCode().equals("MTTG")|| payment.getPaymentCode().equals("CKZF")||payment.getPaymentCode().equals("MZK")){
                    apiRest.setMessage("修改支付方式成功,“" + payment.getPaymentName() + "”支付方式名称不允许修改！");
                }
                else{
                    payment.setPaymentName(name);
                }
                payment.setLastUpdateAt(new Date());
                payment.setLastUpdateBy("System");
                payment.setVersion(payment.getVersion().add(BigInteger.ONE));
                if(params.get("isStore") != null && !params.get("isStore").equals("")){
                    payment.setIsStore(Integer.valueOf(params.get("isStore").toString()) == 1);
                }
                if(params.get("isOpenCashbox") != null && !params.get("isOpenCashbox").equals("")){
                    payment.setIsOpenCashbox(Integer.valueOf(params.get("isOpenCashbox").toString()) == 1);
                }
                if(branch.getBranchType() == 0){
                    List<PaymentBranch> paymentBranchList = paymentMapper.listPaymentBranchByPaymentId(payment.getId());
                    for(PaymentBranch paymentBranch : paymentBranchList){
                        paymentBranch.setLastUpdateAt(payment.getLastUpdateAt());
                        paymentBranch.setLastUpdateBy(payment.getLastUpdateBy());
                        paymentBranch.setPaymentName(payment.getPaymentName());
                        paymentBranch.setIsScore(payment.getIsScore());
                        paymentBranch.setIsVoucher(payment.getIsVoucher());
                        paymentBranch.setFixNum(payment.getFixNum());
                        paymentBranch.setFixValue(payment.getFixValue());
                        paymentBranch.setVersion(paymentBranch.getVersion().add(BigInteger.ONE));
                        paymentBranch.setIsStore(payment.getIsStore());
                        paymentBranch.setIsOpenCashbox(payment.getIsOpenCashbox());
                        int a = paymentMapper.updatePaymentBranch(paymentBranch);
                        if(a < 0){
                            apiRest.setIsSuccess(false);
                            apiRest.setError("修改分店支付方式失败！");
                        }
                        else{
                            apiRest.setIsSuccess(true);
                            apiRest.setMessage(apiRest.getMessage() == null ? "修改支付方式成功！" : apiRest.getMessage());
                        }
                    }
                }
                int i = paymentMapper.update(payment);
                if(i < 0){
                    apiRest.setIsSuccess(false);
                    apiRest.setError("修改支付方式失败！");
                }
                else{
                    operateLogService.saveLog(payment, oldPayment, 2, opFrom, userName, payment.getTenantId(), payment.getBranchId());
                    apiRest.setIsSuccess(true);
                    apiRest.setMessage(apiRest.getMessage() == null ? "修改支付方式成功！" : apiRest.getMessage());
                    apiRest.setData(payment);
                }

            }
            else{
                PaymentBranch paymentBranch = paymentMapper.getPaymentBranchByPaymentId(id, branchId);
                if(paymentBranch == null){
                    apiRest.setIsSuccess(false);
                    apiRest.setError("查询机构支付方式失败！");
                    return apiRest;
                }
                if(Integer.valueOf(params.get("paymentStatus").toString()) == 0){
                    Long enableCount = paymentMapper.checkStatus(branchId, id, tenantId);
                    if(enableCount >= 20){
                        apiRest.setIsSuccess(false);
                        apiRest.setError("最多可以启用20种支付方式");
                        return apiRest;
                    }
                    else{
                        paymentBranch.setPaymentStatus(0);
                    }
                }
                else{
                    paymentBranch.setPaymentStatus(Integer.valueOf(params.get("paymentStatus").toString()));
                }
                if(params.get("isStore") != null && !params.get("isStore").equals("")){
                    paymentBranch.setIsStore(Integer.valueOf(params.get("isStore").toString()) == 1);
                }
                if(params.get("isOpenCashbox") != null && !params.get("isOpenCashbox").equals("")){
                    paymentBranch.setIsOpenCashbox(Integer.valueOf(params.get("isOpenCashbox").toString()) == 1);
                }
                if(params.get("isScore") != null && !params.get("isScore").equals("")){
                    paymentBranch.setIsScore(Integer.valueOf(params.get("isScore").toString()) == 1);
                }
                if(params.get("isVoucher") != null && !params.get("isVoucher").equals("")){
                    if(params.get("isVoucher").equals("0")){
                        paymentBranch.setIsVoucher(0);
                        paymentBranch.setFixNum(0);
                        paymentBranch.setFixValue(BigDecimal.ZERO);
                    }else{
                        paymentBranch.setIsVoucher(1);
                        paymentBranch.setFixValue(BigDecimal.valueOf(Double.valueOf(params.get("fixValue").toString())));
                        paymentBranch.setFixNum(Integer.valueOf(params.get("fixNum").toString()));
                    }
                }
                paymentBranch.setLastUpdateAt(new Date());
                paymentBranch.setLastUpdateBy("System");
                paymentBranch.setVersion(paymentBranch.getVersion().add(BigInteger.ONE));
                int i = paymentMapper.updatePaymentBranch(paymentBranch);
                if(i < 0){
                    apiRest.setIsSuccess(false);
                    apiRest.setError("修改机构支付方式失败！");
                }
                else{
                    apiRest.setIsSuccess(true);
                    apiRest.setMessage(apiRest.getMessage() == null ? "修改机构支付方式成功！" : apiRest.getMessage());
                    apiRest.setData(paymentBranch);
                }
            }
        }
        else{
                /*ApiRest i=tenantConfigService.checkConfig(tenantId, SysConfig.SYS_PAYMENT_NUM);
                if(!i.getIsSuccess()){
                    apiRest=i;
                    return apiRest;
                }*/
                /*Branch branch=branchMapper.getMainBranch(tenantId.toString());
                if(branch==null){
                    apiRest.setIsSuccess(false);
                    apiRest.setError("查询总部机构失败！");
                    return apiRest;
                }*/
            //branchId=branch.getId();
            Payment payment1 = new Payment();
            Long count = paymentMapper.checkPaymentName(null, tenantId, params.get("paymentName").toString(),branchId);//查看名字是否已存在
            if(count > 0){
                apiRest.setIsSuccess(false);
                apiRest.setError("名字已存在，请重新命名");
                return apiRest;
            }
            if(branch.getBranchType()>0){ //0为总部,如果不是总部再查询下分店支付中是否有该支付名称
               Long num= paymentMapper.checkPaymentBranchName(null,tenantId,params.get("paymentName").toString(),branchId);
                if(num > 0){
                    apiRest.setIsSuccess(false);
                    apiRest.setError("名字已存在，请重新命名");
                    return apiRest;
                }
            }
            ApiRest rest = getPaymentCode(params);//自定义编码
            if(!rest.getIsSuccess()){
                apiRest.setIsSuccess(false);
                apiRest.setError("获取支付方式编码失败！");
                return apiRest;
            }
            if(Integer.valueOf(params.get("paymentStatus").toString()) == 0){//如果新增为启用,检查启用了多少种支付方式
                Long enableCount = paymentMapper.checkStatus(branchId, null, tenantId);
                if(enableCount >= 20){
                    apiRest.setIsSuccess(false);
                    apiRest.setError("最多可以启用20种支付方式");
                    return apiRest;
                }
                else{
                    payment1.setPaymentStatus(0);
                }
            }
            else{
                payment1.setPaymentStatus(Integer.valueOf(params.get("paymentStatus").toString()));
            }
            payment1.setPaymentCode(rest.getData().toString());
            payment1.setTenantId(tenantId);
            payment1.setBranchId(branchId);
            payment1.setPaymentName(params.get("paymentName").toString());
            payment1.setIsScore(Integer.valueOf(params.get("isScore").toString()) == 1);
            payment1.setPaymentType(1);
            payment1.setIsVoucher(Integer.valueOf(params.get("isVoucher").toString()));
            if(params.get("isVoucher").equals("0")){
                payment1.setFixNum(0);
                payment1.setFixValue(BigDecimal.ZERO);
            }
            else{
                payment1.setFixValue(BigDecimal.valueOf(Double.valueOf(params.get("fixValue").toString())));
                payment1.setFixNum(Integer.valueOf(params.get("fixNum").toString()));
            }
            payment1.setLastUpdateAt(new Date());
            payment1.setLastUpdateBy("System");
            payment1.setCreateAt(new Date());
            payment1.setCreateBy("System");
            payment1.setVersion(BigInteger.ONE);
            payment1.setIsDeleted(false);
            if(params.get("isStore") != null && !params.get("isStore").equals("")){
                payment1.setIsStore(Integer.valueOf(params.get("isStore").toString()) == 1);
            }
            if(params.get("isOpenCashbox") != null && !params.get("isOpenCashbox").equals("")){
                payment1.setIsOpenCashbox(Integer.valueOf(params.get("isOpenCashbox").toString()) == 1);
            }
            int j = paymentMapper.insert(payment1);
            if(j < 0){
                apiRest.setIsSuccess(false);
                apiRest.setError("新增支付方式失败！");
            }
            else{
                apiRest.setIsSuccess(true);
                apiRest.setMessage("新增支付方式成功 ！");
                apiRest.setData(payment1);
            }
            if(branch.getBranchType() == 0){
                List<Branch> childBranchList = branchMapper.getChildBranches(tenantId);
                for(Branch branch1 : childBranchList){
                    PaymentBranch paymentBranch = new PaymentBranch();
                    if(Integer.valueOf(params.get("paymentStatus").toString()) == 0){
                        Long enableCount = paymentMapper.checkStatus(branch1.getId(), null, tenantId);
                        if(enableCount >= 20){
                            paymentBranch.setPaymentStatus(1);
                        }
                        else{
                            paymentBranch.setPaymentStatus(0);
                        }
                    }
                    else{
                        paymentBranch.setPaymentStatus(payment1.getPaymentStatus());
                    }
                    paymentBranch.setCreateAt(new Date());
                    paymentBranch.setCreateBy("System");
                    paymentBranch.setLastUpdateAt(new Date());
                    paymentBranch.setLastUpdateBy("System");
                    paymentBranch.setTenantPaymentId(payment1.getId());
                    paymentBranch.setPaymentCode(payment1.getPaymentCode());
                    paymentBranch.setPaymentName(payment1.getPaymentName());
                    paymentBranch.setBranchId(branch1.getId());
                    paymentBranch.setIsScore(payment1.getIsScore());
                    paymentBranch.setIsChange(payment1.getChange());
                    paymentBranch.setIsMemo(payment1.getMemo());
                    paymentBranch.setIsSale(payment1.getSale());
                    paymentBranch.setFixValue(payment1.getFixValue());
                    paymentBranch.setFixNum(payment1.getFixNum());
                    paymentBranch.setPaymentType(payment1.getPaymentType());
                    paymentBranch.setIsVoucher(payment1.getIsVoucher());
                    paymentBranch.setVersion(BigInteger.ONE);
                    paymentBranch.setTenantId(tenantId);
                    paymentBranch.setDeleted(false);
                    paymentBranch.setIsStore(payment1.getIsStore());
                    paymentBranch.setIsOpenCashbox(payment1.getIsOpenCashbox());
                    int k = paymentMapper.insertBranchPayment(paymentBranch);
                    if(k < 0){
                        apiRest.setIsSuccess(false);
                        apiRest.setError("新增机构支付方式失败！");
                    }
                    else{
                        apiRest.setIsSuccess(true);
                        apiRest.setMessage("新增支付方式成功！");
                    }
                }
            }
        }
        return apiRest;
    }

    /**
     * 获取支付方式编码
     *
     * @return
     */
    public ApiRest getPaymentCode(Map params){
        ApiRest apiRest = new ApiRest();
        BigInteger tenantId = BigInteger.valueOf(Long.parseLong((String)params.get("tenantId")));
        String maxCode = paymentMapper.getMaxCode("ZDY_", tenantId);
        String curr;
        String number;
        if(maxCode != null && !maxCode.equals("")){
            curr = maxCode.substring(4, 8);
            java.util.regex.Pattern pattern = java.util.regex.Pattern.compile("[0-9]{4}");
            java.util.regex.Matcher match = pattern.matcher(curr);
            Validate.isTrue(match.matches(), "自定义编码格式不正确");
            number = SerialNumberGenerate.getNextCode(4, curr);
        }
        else{
            number = SerialNumberGenerate.getNextCode(4, "0000");
        }
        apiRest.setIsSuccess(true);
        apiRest.setMessage("获取支付方式编码成功！");
        apiRest.setData("ZDY_" + number);
        return apiRest;
    }

    /**
     * 删除支付方式
     *
     * @return
     */
    public ApiRest delPayment(Map params) throws IllegalAccessException{
        ApiRest apiRest = new ApiRest();
        BigInteger branchId = BigInteger.valueOf(Long.parseLong((String)params.get("branchId")));
        BigInteger tenantId = BigInteger.valueOf(Long.parseLong((String)params.get("tenantId")));
        BigInteger id = BigInteger.valueOf(Long.parseLong((String)params.get("id")));
        String userName = params.get("userName") == null ? "" : params.get("userName").toString();
        ;
        String opFrom = params.get("opFrom") == null ? "" : params.get("opFrom").toString();
        Payment payment = paymentMapper.getPaymentById(id, tenantId);
        Map oldPayment = CommonUtils.ObjectToMap(payment);
        if(payment == null){
            apiRest.setIsSuccess(false);
            apiRest.setError("查询支付方式失败！");
        }
        else{
            if(branchId.equals(payment.getBranchId())){
                if(payment.getPaymentCode().equals("CSH") || payment.getPaymentCode().equals("DJQ") || payment.getPaymentCode().equals("CRD") || payment.getPaymentCode().equals("ZFB") ||
                        payment.getPaymentCode().equals("WX") || payment.getPaymentCode().equals("JF") || payment.getPaymentCode().equals("HYQB") ||
                        payment.getPaymentCode().equals("MTTG") || payment.getPaymentCode().equals("HZF") || payment.getPaymentCode().equals("CKZF") || payment.getPaymentCode().equals("YHQ") ||
                        payment.getPaymentCode().equals("MYQ")||payment.getPaymentCode().equals("MZK")){

                    apiRest.setIsSuccess(false);
                    apiRest.setError(payment.getPaymentName() + "支付方式不允许删除！");
                    return apiRest;
                }
                payment.setLastUpdateAt(new Date());
                payment.setLastUpdateBy("System");
                payment.setVersion(payment.getVersion().add(BigInteger.ONE));
                payment.setIsDeleted(true);
                List<PaymentBranch> paymentBranchList = paymentMapper.listPaymentBranchByPaymentId(payment.getId());
                for(PaymentBranch paymentBranch : paymentBranchList){
                    paymentBranch.setLastUpdateAt(new Date());
                    paymentBranch.setLastUpdateBy("System");
                    paymentBranch.setVersion(paymentBranch.getVersion().add(BigInteger.ONE));
                    paymentBranch.setDeleted(true);
                    int t = paymentMapper.updatePaymentBranch(paymentBranch);
                    if(t > 0){
                        apiRest.setIsSuccess(true);
                        apiRest.setMessage("删除分店支付方式成功！");
                    }
                    else{
                        apiRest.setIsSuccess(false);
                        apiRest.setError("删除分店支付方式失败！");
                    }
                }
                int i = paymentMapper.update(payment);
                if(i > 0){
                    operateLogService.saveLog(payment, oldPayment, 3, opFrom, userName, payment.getTenantId(), payment.getBranchId());
                    apiRest.setIsSuccess(true);
                    apiRest.setMessage("删除支付方式成功！");
                }
                else{
                    apiRest.setIsSuccess(false);
                    apiRest.setError("删除支付方式失败！");
                }
            }
            else{
                apiRest.setIsSuccess(false);
                apiRest.setError("您无法删除此支付方式！");
            }
        }
        return apiRest;
    }

    /**
     * 删除支付方式
     *
     * @return
     */
    public ApiRest deletePayments(Map params) throws IllegalAccessException{
        ApiRest apiRest = new ApiRest();
        BigInteger branchId = BigInteger.valueOf(Long.parseLong((String)params.get("branchId")));
        BigInteger tenantId = BigInteger.valueOf(Long.parseLong((String)params.get("tenantId")));
        String ids = params.get("ids").toString();
        Map resultMap = new HashMap();
        int count = 0;
        List<Map> errorList = new ArrayList<>();
        for(String payId : ids.split(",")){
            Map errorMap = new HashMap();
            BigInteger id = BigInteger.valueOf(Long.parseLong(payId));
            String userName = params.get("userName") == null ? "" : params.get("userName").toString();
            String opFrom = params.get("opFrom") == null ? "" : params.get("opFrom").toString();
            Payment payment = paymentMapper.getPaymentById(id, tenantId);
            Map oldPayment = CommonUtils.ObjectToMap(payment);
            if(payment == null){
                errorMap.put("id", id);
                errorMap.put("mes", "未查询到支付方式！");
                errorList.add(errorMap);
                apiRest.setIsSuccess(false);
                apiRest.setError("查询支付方式失败！");
            }
            else{
                if(branchId.equals(payment.getBranchId())){
                    if(payment.getPaymentCode().equals("CSH") || payment.getPaymentCode().equals("DJQ") || payment.getPaymentCode().equals("CRD") || payment.getPaymentCode().equals("ZFB") ||
                            payment.getPaymentCode().equals("WX") || payment.getPaymentCode().equals("JF") || payment.getPaymentCode().equals("HYQB") ||
                            payment.getPaymentCode().equals("MTTG") || payment.getPaymentCode().equals("HZF") || payment.getPaymentCode().equals("CKZF") || payment.getPaymentCode().equals("YHQ") ||
                            payment.getPaymentCode().equals("MYQ")||payment.getPaymentCode().equals("MZK")){
                        errorMap.put("id", id);
                        errorMap.put("mes", payment.getPaymentName() + "支付方式不允许删除！");
                        errorList.add(errorMap);
                        apiRest.setIsSuccess(false);
                        apiRest.setError(payment.getPaymentName() + "支付方式不允许删除！");
                        return apiRest;
                    }
                    payment.setLastUpdateAt(new Date());
                    payment.setLastUpdateBy("System");
                    payment.setVersion(payment.getVersion().add(BigInteger.ONE));
                    payment.setIsDeleted(true);
                    List<PaymentBranch> paymentBranchList = paymentMapper.listPaymentBranchByPaymentId(payment.getId());
                    for(PaymentBranch paymentBranch : paymentBranchList){
                        paymentBranch.setLastUpdateAt(new Date());
                        paymentBranch.setLastUpdateBy("System");
                        paymentBranch.setVersion(paymentBranch.getVersion().add(BigInteger.ONE));
                        paymentBranch.setDeleted(true);
                        int t = paymentMapper.updatePaymentBranch(paymentBranch);
                        if(t > 0){
                            apiRest.setIsSuccess(true);
                            apiRest.setMessage("删除分店支付方式成功！");
                        }
                        else{
                            errorMap.put("id", id);
                            errorMap.put("mes", "删除分店支付方式失败！");
                            errorList.add(errorMap);
                            apiRest.setIsSuccess(false);
                            apiRest.setError("删除分店支付方式失败！");
                        }
                    }
                    int i = paymentMapper.update(payment);
                    if(i > 0){
                        count++;
                        operateLogService.saveLog(payment, oldPayment, 3, opFrom, userName, payment.getTenantId(), payment.getBranchId());
                        apiRest.setIsSuccess(true);
                        apiRest.setMessage("删除支付方式成功！");
                    }
                    else{
                        apiRest.setIsSuccess(false);
                        apiRest.setError("删除支付方式失败！");
                    }
                }
                else{
                    apiRest.setIsSuccess(false);
                    apiRest.setError("您无法删除此支付方式！");
                }
            }
        }
        if(count > 0){
            apiRest.setIsSuccess(true);
            apiRest.setMessage("支付方式删除成功");
        }
        if(errorList.size() > 0){
            apiRest.setError("");
            for(int i = 0; i < errorList.size(); i++){
                apiRest.setError("id为" + errorList.get(i).get("id") + "的支付方式：" + errorList.get(i).get("mes") + "。");
            }
        }
        resultMap.put("num", count);
        resultMap.put("errors", errorList);
        apiRest.setData(resultMap);
        return apiRest;
    }

    /**
     * 查询可用于储值的支付方式
     */
    public ApiRest queryPaymentForStore(Map params){
        ApiRest apiRest = new ApiRest();
        BigInteger tenantId = BigInteger.valueOf(Long.parseLong(params.get("tenantId").toString()));
        BigInteger branchId = BigInteger.valueOf(Long.parseLong(params.get("branchId").toString()));
        List<Map> list = paymentMapper.queryPaymentForStore(tenantId, branchId);
        apiRest.setIsSuccess(true);
        apiRest.setMessage("查询支付方式成功");
        apiRest.setData(list);
        return apiRest;
    }

    /**
     * 支付方式排序
     */
    public ApiRest savePaymentSort(Map params){
        ApiRest apiRest = new ApiRest();
        BigInteger tenantId = BigInteger.valueOf(Long.parseLong(params.get("tenantId").toString()));
        BigInteger branchId = BigInteger.valueOf(Long.parseLong(params.get("branchId").toString()));
        List paymentList = JSONArray.fromObject(params.get("paymentList"));
        if(paymentList != null && paymentList.size() > 0){
            for(int i = 0; i < paymentList.size(); i++){
                Map map = (Map)paymentList.get(i);
                String id = map.get("id").toString();
                Integer orderNumber = Integer.parseInt(map.get("orderNumber").toString());
                Payment payment = paymentMapper.getPaymentByConditions(BigInteger.valueOf(Long.parseLong(id)), tenantId, branchId);
                if(payment != null){
                    payment.setOrderNumber(orderNumber);
                    payment.setLastUpdateAt(new Date());
                    int m = paymentMapper.update(payment);
                    if(m < 0){
                        apiRest.setIsSuccess(false);
                        apiRest.setError("支付方式排序失败");
                    }
                }
                else{
                    PaymentBranch paymentBranch = paymentMapper.getPaymentBranchByPaymentId(BigInteger.valueOf(Long.parseLong(id)), branchId);
                    paymentBranch.setOrderNumber(orderNumber);
                    paymentBranch.setLastUpdateAt(new Date());
                    int n = paymentMapper.updatePaymentBranch(paymentBranch);
                    if(n < 0){
                        apiRest.setIsSuccess(false);
                        apiRest.setError("支付方式排序失败");
                    }
                }
            }
            params.put("nameOrCode", "CSH");
            List<Map> list = paymentMapper.queryPayment(params);
            for(int m = 0; m < list.size(); m++){
                Map map = list.get(m);
                map.put("orderNumber", 1);
                int z = paymentMapper.updateOrderNumber(map);
                if(z == 0){
                    PaymentBranch paymentBranch = new PaymentBranch();
                    paymentBranch.setId(BigInteger.valueOf(Long.parseLong(map.get("id").toString())));
                    paymentBranch.setOrderNumber(1);
                    paymentBranch.setBranchId(BigInteger.valueOf(Long.parseLong(map.get("branchId").toString())));
                    paymentMapper.updateBranchOrderNumber(paymentBranch);
                }
                else if(z < 0){
                    apiRest.setIsSuccess(false);
                    apiRest.setError("支付方式排序失败");
                }
            }
            apiRest.setIsSuccess(true);
            apiRest.setMessage("支付方式排序成功");
        }
        else{
            apiRest.setIsSuccess(false);
            apiRest.setError("参数错误");
        }
        return apiRest;
    }
}
