package erp.chain.mapper;


import erp.chain.domain.Payment;
import erp.chain.domain.PaymentBranch;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigInteger;
import java.util.List;
import java.util.Map;

@Mapper
public interface PaymentMapper extends BaseMapper {
    List<Payment> getMainPayments(@Param("tenantId")BigInteger tenantId,@Param("branchId")BigInteger branchId);
    List<Payment> listBranchsPayment(@Param("tenantId")BigInteger tenantId,@Param("branchId")String branchId);
    List<Payment> listBranchAllPayment(@Param("tenantId")BigInteger tenantId,@Param("branchId")BigInteger branchId);
    int insertBranchPayment(PaymentBranch paymentBranch);
    List<Map> queryPayment(Map params);
    Long queryPaymentSum(Map params);
    List<Map> queryPaymentListByPayment(Map params);
    Long queryPaymentListByPaymentSum(Map params);
    Payment getPaymentById(@Param("id")BigInteger id,@Param("tenantId")BigInteger tenantId);
    Payment getPaymentByIdAll(@Param("id")BigInteger id,@Param("tenantId")BigInteger tenantId);
    Map getPaymentByIdAndBranchId(@Param("branchId")BigInteger branchId,@Param("id")BigInteger id,@Param("tenantId")BigInteger tenantId);
    Long checkPaymentName(@Param("id")BigInteger id,@Param("tenantId")BigInteger tenantId,@Param("name") String name,@Param("branchId") BigInteger branchId);
    Long checkPaymentBranchName(@Param("id")BigInteger id,@Param("tenantId") BigInteger tenantId,@Param("name") String name,@Param("branchId") BigInteger branchId);
    Long checkStatus(@Param("branchId")BigInteger branchId,@Param("id")BigInteger id,@Param("tenantId")BigInteger tenantId);
    int insert(Payment payment);
    int update(Payment payment);
    int updateOrderNumber(Map params);
    int updateBranchOrderNumber(PaymentBranch paymentBranch);
    int updatePaymentBranch(PaymentBranch paymentBranch);
    String getMaxCode(@Param("code")String code,@Param("tenantId")BigInteger tenantId);
    List<PaymentBranch> listPaymentBranchByPaymentId(@Param("paymentId")BigInteger paymentId);
    PaymentBranch getPaymentBranchByPaymentId(@Param("paymentId")BigInteger paymentId,@Param("branchId")BigInteger branchId);
    Map getPaymentWithBusiness(@Param("id")BigInteger paymentId);
    Map getPaymentBranchWithBusiness(@Param("id")BigInteger paymentId);

    List<Map> queryPaymentForStore(@Param("tenantId")BigInteger tenantId, @Param("branchId")BigInteger branchId);
    Payment getPaymentByConditions(@Param("id")BigInteger id,@Param("tenantId")BigInteger tenantId, @Param("branchId")BigInteger branchId);

    Payment findByTenantIdAndPaymentCode(@Param("tenantId") BigInteger tenantId, @Param("paymentCode") String paymentCode);
}