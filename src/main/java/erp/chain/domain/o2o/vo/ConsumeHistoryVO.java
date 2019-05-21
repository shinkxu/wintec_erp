package erp.chain.domain.o2o.vo;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;

/**
 *
 * Created by wangms on 2017/4/18.
 */
public class ConsumeHistoryVO implements Comparable<ConsumeHistoryVO>{
    /**
     * ��Աid
     */
    String vipId;

    /**
     *���׵���
     */
    String tradeNo;
    /**
     *1:��ֵ����,2:���� 3�Ǵ�ֵ���� 4 ��ֵ
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
     *�ŵ�id
     */
    BigInteger branchId;
    /**
     *�ŵ�����
     */
    String branchName;
    /**
     *����ʱ��
     */
    Date tradeDate;
    /**
     * ��������
     */
    BigDecimal addScore;

    Date createAt;

    public String getVipId() {
        return vipId;
    }

    public void setVipId(String vipId) {
        this.vipId = vipId;
    }

    public String getTradeNo() {
        return tradeNo;
    }

    public void setTradeNo(String tradeNo) {
        this.tradeNo = tradeNo;
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

    public BigInteger getBranchId() {
        return branchId;
    }

    public void setBranchId(BigInteger branchId) {
        this.branchId = branchId;
    }

    public String getBranchName() {
        return branchName;
    }

    public void setBranchName(String branchName) {
        this.branchName = branchName;
    }

    public Date getTradeDate() {
        return tradeDate;
    }

    public void setTradeDate(Date tradeDate) {
        this.tradeDate = tradeDate;
    }

    public BigDecimal getAddScore() {
        return addScore;
    }

    public void setAddScore(BigDecimal addScore) {
        this.addScore = addScore;
    }

    public Date getCreateAt() {
        return createAt;
    }

    public void setCreateAt(Date createAt) {
        this.createAt = createAt;
    }

    @Override
    public int compareTo(ConsumeHistoryVO o) {
        if(this.getCreateAt() != null && o.getCreateAt() != null){
            if(this.getCreateAt().before(o.getCreateAt())){
                return 1;
            } else {
                return -1;
            }
        }
        return 0;
    }
}
