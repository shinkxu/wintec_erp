package erp.chain.domain.o2o.vo;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;

/**
 * Created by wangms on 2017/2/9.
 */
public class VipGlide {
    BigInteger saleId;
    String saleCode;
    BigInteger posPaymentId;
    String paymentCode;
    BigDecimal amount;
    String memo;
    BigInteger cashier;
    BigInteger vipId;
    String BranchId;
    /**
     * ʹ�û���
     */
    String useIntegral;
    /**
     * ���ֵ��ֽ��
     */
    String integralAmount;
    /**
     * ������
     */
    String createBy;

    BigInteger id;
    Date createAt;
    BigInteger lastUpdateAt;
    boolean isDeleted;

    public BigInteger getSaleId() {
        return saleId;
    }

    public void setSaleId(BigInteger saleId) {
        this.saleId = saleId;
    }

    public String getSaleCode() {
        return saleCode;
    }

    public void setSaleCode(String saleCode) {
        this.saleCode = saleCode;
    }

    public BigInteger getPosPaymentId() {
        return posPaymentId;
    }

    public void setPosPaymentId(BigInteger posPaymentId) {
        this.posPaymentId = posPaymentId;
    }

    public String getPaymentCode() {
        return paymentCode;
    }

    public void setPaymentCode(String paymentCode) {
        this.paymentCode = paymentCode;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getMemo() {
        return memo;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }

    public BigInteger getCashier() {
        return cashier;
    }

    public void setCashier(BigInteger cashier) {
        this.cashier = cashier;
    }

    public BigInteger getVipId() {
        return vipId;
    }

    public void setVipId(BigInteger vipId) {
        this.vipId = vipId;
    }

    public String getBranchId() {
        return BranchId;
    }

    public void setBranchId(String branchId) {
        BranchId = branchId;
    }

    public String getUseIntegral() {
        return useIntegral;
    }

    public void setUseIntegral(String useIntegral) {
        this.useIntegral = useIntegral;
    }

    public String getIntegralAmount() {
        return integralAmount;
    }

    public void setIntegralAmount(String integralAmount) {
        this.integralAmount = integralAmount;
    }

    public String getCreateBy() {
        return createBy;
    }

    public void setCreateBy(String createBy) {
        this.createBy = createBy;
    }

    public BigInteger getId() {
        return id;
    }

    public void setId(BigInteger id) {
        this.id = id;
    }

    public Date getCreateAt() {
        return createAt;
    }

    public void setCreateAt(Date createAt) {
        this.createAt = createAt;
    }

    public BigInteger getLastUpdateAt() {
        return lastUpdateAt;
    }

    public void setLastUpdateAt(BigInteger lastUpdateAt) {
        this.lastUpdateAt = lastUpdateAt;
    }

    public boolean isDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(boolean isDeleted) {
        this.isDeleted = isDeleted;
    }
}
