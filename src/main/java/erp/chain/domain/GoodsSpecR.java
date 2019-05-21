package erp.chain.domain;

import java.math.BigInteger;
import java.util.Map;

/**
 * Created by xumx on 2016/11/3.
 */
public class GoodsSpecR extends BaseDomain {
    BigInteger goodsId;
    BigInteger specGroupId;
    BigInteger id;
    BigInteger tenantId;
    BigInteger version;

    public BigInteger getVersion(){
        return version;
    }

    public void setVersion(BigInteger version){
        this.version = version;
    }

    public BigInteger getSpecGroupId(){
        return specGroupId;
    }

    public void setSpecGroupId(BigInteger specGroupId){
        this.specGroupId = specGroupId;
    }

    public GoodsSpecR() {
        super();
    }

    public GoodsSpecR(Map domainMap) {
        super(domainMap);
    }

    public BigInteger getGoodsId() {
        return goodsId;
    }

    public void setGoodsId(BigInteger goodsId) {
        this.goodsId = goodsId;
    }

    public BigInteger getId() {
        return id;
    }

    public void setId(BigInteger id) {
        this.id = id;
    }

    public BigInteger getTenantId() {
        return tenantId;
    }

    public void setTenantId(BigInteger tenantId) {
        this.tenantId = tenantId;
    }
}
