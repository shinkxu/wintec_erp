package erp.chain.domain;

import java.math.BigInteger;

/**
 * Created by lipeng on 2017/5/15.
 *
 * 第三方机构
 */
public class DaqGraspBtype extends BaseDomain {

    /**
     * 商户编码
     * */
    String tenantCode;

    /**
     * 机构ID
     * */
    BigInteger typeId;

    /**
     * 机构名称
     * */
    String fullName;

    /**
     * 时间戳
     * */
    String timestamp;

    public String getTenantCode() {
        return tenantCode;
    }

    public BigInteger getTypeId() {
        return typeId;
    }

    public String getFullName() {
        return fullName;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTenantCode(String tenantCode) {
        this.tenantCode = tenantCode;
    }

    public void setTypeId(BigInteger typeId) {
        this.typeId = typeId;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}
