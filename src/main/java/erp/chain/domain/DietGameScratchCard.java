package erp.chain.domain;

import java.math.BigInteger;
import java.util.Date;

/**
 * Created by songzhiqiang on 2017/2/7.
 */
public class DietGameScratchCard {
    BigInteger id;
    BigInteger dietPromotionId;
    BigInteger tenantId;
    /**
     * 游戏名称
     */
    String gameName;
    /**
     * 参与次数
     */
    BigInteger attendTimes;
    String remark;
    boolean isDeleted;
    String lastUpdateBy;
    String createBy;
    /**
     * 更新时间
     */
    Date lastUpdateAt;
    /**
     * 创建时间(申请时间)
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

    public boolean isDeleted() {
        return isDeleted;
    }

    public void setDeleted(boolean isDeleted) {
        this.isDeleted = isDeleted;
    }

    public String getLastUpdateBy() {
        return lastUpdateBy;
    }

    public void setLastUpdateBy(String lastUpdateBy) {
        this.lastUpdateBy = lastUpdateBy;
    }

    public String getCreateBy() {
        return createBy;
    }

    public void setCreateBy(String createBy) {
        this.createBy = createBy;
    }

    public Date getLastUpdateAt() {
        return lastUpdateAt;
    }

    public void setLastUpdateAt(Date lastUpdateAt) {
        this.lastUpdateAt = lastUpdateAt;
    }

    public Date getCreateAt() {
        return createAt;
    }

    public void setCreateAt(Date createAt) {
        this.createAt = createAt;
    }
}
