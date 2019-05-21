package erp.chain.domain.o2o;

import java.math.BigInteger;
import java.util.Date;

/**
 *
 * Created by wangms on 2017/5/19.
 */
public class DietGameScratchVip {
    BigInteger id;
    /**
     * ��Աid
     */
    BigInteger vipId;
    /**
     * ʣ��������
     */
    BigInteger remainTimes;
    /**
     * �ι��ֻid
     */
    BigInteger scratchCardId;

    BigInteger tenantId;
    boolean isDeleted;
    String lastUpdateBy;
    String createBy;
    /**
     * ����ʱ��
     */
    Date lastUpdateAt;
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

    public BigInteger getVipId() {
        return vipId;
    }

    public void setVipId(BigInteger vipId) {
        this.vipId = vipId;
    }

    public BigInteger getRemainTimes() {
        return remainTimes;
    }

    public void setRemainTimes(BigInteger remainTimes) {
        this.remainTimes = remainTimes;
    }

    public BigInteger getScratchCardId() {
        return scratchCardId;
    }

    public void setScratchCardId(BigInteger scratchCardId) {
        this.scratchCardId = scratchCardId;
    }

    public BigInteger getTenantId() {
        return tenantId;
    }

    public void setTenantId(BigInteger tenantId) {
        this.tenantId = tenantId;
    }

    public boolean isDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(boolean isDeleted) {
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
