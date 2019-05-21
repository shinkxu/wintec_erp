package erp.chain.domain;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;
import java.util.Map;

/**
 * Created by xumx on 2016/11/3.
 */
public class UserLog extends BaseDomain {

    /**
     * 用户id
     */
    BigInteger userId;
    /**
     * 登陆时间戳
     */
    Date loginTime;
    /**
     * 下班时间戳
     */
    Date exitTime;
    /**
     * 备用金
     */
    BigDecimal restMoney;
    /**
     *
     */
    BigInteger branchId;
    /**
     *
     */
    BigInteger posId;

    BigInteger id;
    BigInteger tenantId;

    public UserLog() {
        super();
    }

    public UserLog(Map domainMap) {
        super(domainMap);
    }

    public BigInteger getUserId() {
        return userId;
    }

    public void setUserId(BigInteger userId) {
        this.userId = userId;
    }

    public Date getLoginTime() {
        return loginTime;
    }

    public void setLoginTime(Date loginTime) {
        this.loginTime = loginTime;
    }

    public Date getExitTime() {
        return exitTime;
    }

    public void setExitTime(Date exitTime) {
        this.exitTime = exitTime;
    }

    public BigDecimal getRestMoney() {
        return restMoney;
    }

    public void setRestMoney(BigDecimal restMoney) {
        this.restMoney = restMoney;
    }

    public BigInteger getBranchId() {
        return branchId;
    }

    public void setBranchId(BigInteger branchId) {
        this.branchId = branchId;
    }

    public BigInteger getPosId() {
        return posId;
    }

    public void setPosId(BigInteger posId) {
        this.posId = posId;
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
