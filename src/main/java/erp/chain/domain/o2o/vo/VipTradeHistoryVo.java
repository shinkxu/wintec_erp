package erp.chain.domain.o2o.vo;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;

/**
 * Created by wangms on 2017/2/6.
 */
public class VipTradeHistoryVo {
    String code;
    String tradeNo;
    String name;
    String phone;
    /**
     *1:����,2:����
     */
    String tradeType;
    /**
     *���ѽ��
     */
    BigDecimal tradeAmount;
    /**
     *ʵ��֧�����
     */
    BigDecimal payAmount;
    /**
     *�����ŵ�����
     */
    String tradeBranchName;
    /**
     *����ʱ��
     */
    Date tradeDate;
    /**
     *���ײ���������
     */
    String tradeUserName;
    /**
     * ���ֽ��
     */
    BigDecimal accumulateAmount;
    /**
     * ��ֵ֧��
     */
    BigDecimal storeAmount;
    /**
     * ���ֵ���
     */
    BigDecimal integralAmount;
    /**
     * ��������
     */
    BigDecimal addScore;
    BigInteger branchId;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getTradeNo() {
        return tradeNo;
    }

    public void setTradeNo(String tradeNo) {
        this.tradeNo = tradeNo;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getTradeType() {
        return tradeType;
    }

    public void setTradeType(String tradeType) {
        this.tradeType = tradeType;
    }

    public BigDecimal getTradeAmount() {
        return tradeAmount;
    }

    public void setTradeAmount(BigDecimal tradeAmount) {
        this.tradeAmount = tradeAmount;
    }

    public BigDecimal getPayAmount() {
        return payAmount;
    }

    public void setPayAmount(BigDecimal payAmount) {
        this.payAmount = payAmount;
    }

    public String getTradeBranchName() {
        return tradeBranchName;
    }

    public void setTradeBranchName(String tradeBranchName) {
        this.tradeBranchName = tradeBranchName;
    }

    public Date getTradeDate() {
        return tradeDate;
    }

    public void setTradeDate(Date tradeDate) {
        this.tradeDate = tradeDate;
    }

    public String getTradeUserName() {
        return tradeUserName;
    }

    public void setTradeUserName(String tradeUserName) {
        this.tradeUserName = tradeUserName;
    }

    public BigDecimal getAccumulateAmount() {
        return accumulateAmount;
    }

    public void setAccumulateAmount(BigDecimal accumulateAmount) {
        this.accumulateAmount = accumulateAmount;
    }

    public BigDecimal getStoreAmount() {
        return storeAmount;
    }

    public void setStoreAmount(BigDecimal storeAmount) {
        this.storeAmount = storeAmount;
    }

    public BigDecimal getIntegralAmount() {
        return integralAmount;
    }

    public void setIntegralAmount(BigDecimal integralAmount) {
        this.integralAmount = integralAmount;
    }

    public BigDecimal getAddScore() {
        return addScore;
    }

    public void setAddScore(BigDecimal addScore) {
        this.addScore = addScore;
    }

    public BigInteger getBranchId() {
        return branchId;
    }

    public void setBranchId(BigInteger branchId) {
        this.branchId = branchId;
    }
}
