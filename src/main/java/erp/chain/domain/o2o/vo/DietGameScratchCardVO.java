package erp.chain.domain.o2o.vo;

import java.math.BigInteger;
import java.util.Date;

/**
 * Created by wangms on 2017/5/19.
 */
public class DietGameScratchCardVO {
    BigInteger id;
    BigInteger dietPromotionId;
    BigInteger tenantId;
    /**
     * ��Ϸ����
     */
    String gameName;
    /**
     * �������
     */
    BigInteger attendTimes;
    String remark;
    /**
     * ��Ӧ��ƷitemId
     */
    BigInteger itemId;
    /**
     * ��Ʒ����
     */
    String itemName;
    /**
     * ��Ʒ��ȯPromotionId
     */
    BigInteger prizePromotionId;
    /**
     * ��ƷPromotion���� 12��ʾ��ȯ
     */
    BigInteger prizePromotionType;
    /**
     * ����ʱ��(����ʱ��)
     */
    Date createAt;

    public BigInteger getId() {
        return id;
    }

    public void setId(BigInteger id) {
        this.id = id;
    }

    public BigInteger getDietPromotionId() {
        return dietPromotionId;
    }

    public void setDietPromotionId(BigInteger dietPromotionId) {
        this.dietPromotionId = dietPromotionId;
    }

    public BigInteger getTenantId() {
        return tenantId;
    }

    public void setTenantId(BigInteger tenantId) {
        this.tenantId = tenantId;
    }

    public String getGameName() {
        return gameName;
    }

    public void setGameName(String gameName) {
        this.gameName = gameName;
    }

    public BigInteger getAttendTimes() {
        return attendTimes;
    }

    public void setAttendTimes(BigInteger attendTimes) {
        this.attendTimes = attendTimes;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public BigInteger getItemId() {
        return itemId;
    }

    public void setItemId(BigInteger itemId) {
        this.itemId = itemId;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public BigInteger getPrizePromotionId() {
        return prizePromotionId;
    }

    public void setPrizePromotionId(BigInteger prizePromotionId) {
        this.prizePromotionId = prizePromotionId;
    }

    public BigInteger getPrizePromotionType() {
        return prizePromotionType;
    }

    public void setPrizePromotionType(BigInteger prizePromotionType) {
        this.prizePromotionType = prizePromotionType;
    }

    public Date getCreateAt() {
        return createAt;
    }

    public void setCreateAt(Date createAt) {
        this.createAt = createAt;
    }
}
