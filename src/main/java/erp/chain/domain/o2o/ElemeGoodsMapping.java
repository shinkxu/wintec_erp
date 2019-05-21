package erp.chain.domain.o2o;

import java.math.BigInteger;

/**
 * Created by liuyandong on 2018-01-18.
 */
public class ElemeGoodsMapping {
    private BigInteger tenantId;
    private BigInteger branchId;
    private BigInteger elemeGoodsId;
    private BigInteger ourGoodsId;

    public BigInteger getTenantId() {
        return tenantId;
    }

    public void setTenantId(BigInteger tenantId) {
        this.tenantId = tenantId;
    }

    public BigInteger getBranchId() {
        return branchId;
    }

    public void setBranchId(BigInteger branchId) {
        this.branchId = branchId;
    }

    public BigInteger getElemeGoodsId() {
        return elemeGoodsId;
    }

    public void setElemeGoodsId(BigInteger elemeGoodsId) {
        this.elemeGoodsId = elemeGoodsId;
    }

    public BigInteger getOurGoodsId() {
        return ourGoodsId;
    }

    public void setOurGoodsId(BigInteger ourGoodsId) {
        this.ourGoodsId = ourGoodsId;
    }
}
