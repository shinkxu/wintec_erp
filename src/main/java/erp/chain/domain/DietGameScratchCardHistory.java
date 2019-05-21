package erp.chain.domain;

import java.math.BigInteger;
import java.util.Date;

/**
 * Created by wangms on 2017/5/22.
 */
public class DietGameScratchCardHistory {
    BigInteger id;
    /**
     * ��Աid
     */
    BigInteger vipId;
    /**
     * �ι��ֻid
     */
    BigInteger scratchCardId;
    /**
     * ��Ʒ���
     */
    String resultName;
    /**
     * ��ƷitemId
     */
    BigInteger resultItemId;


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

    public BigInteger getScratchCardId() {
        return scratchCardId;
    }

    public void setScratchCardId(BigInteger scratchCardId) {
        this.scratchCardId = scratchCardId;
    }

    public String getResultName() {
        return resultName;
    }

    public void setResultName(String resultName) {
        this.resultName = resultName;
    }

    public BigInteger getResultItemId() {
        return resultItemId;
    }

    public void setResultItemId(BigInteger resultItemId) {
        this.resultItemId = resultItemId;
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
