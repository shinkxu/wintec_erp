package erp.chain.domain;

import java.math.BigInteger;

/**
 * Created by lipeng on 2017/5/15.
 *
 * 机构对应关系表
 */
public class BranchMapping extends BaseDomain{
    /**
    * 商户ID
    * */
    BigInteger tenantId;

    /**
     * 配送中心ID
     * */
    BigInteger distributionCenterId;

    /**
     *  本地机构ID
     * */
    BigInteger ourBranchId;

    /**
     * 第三方机构ID
     * */
    String otherBranchId;

    public BigInteger getTenantId() {
        return tenantId;
    }

    public BigInteger getDistributionCenterId() {
        return distributionCenterId;
    }

    public BigInteger getOurBranchId() {
        return ourBranchId;
    }

    public String getOtherBranchId() {
        return otherBranchId;
    }

    public void setTenantId(BigInteger tenantId) {
        this.tenantId = tenantId;
    }

    public void setDistributionCenterId(BigInteger distributionCenterId) {
        this.distributionCenterId = distributionCenterId;
    }

    public void setOurBranchId(BigInteger ourBranchId) {
        this.ourBranchId = ourBranchId;
    }

    public void setOtherBranchId(String otherBranchId) {
        this.otherBranchId = otherBranchId;
    }
}
