package erp.chain.domain;

import java.math.BigInteger;
import java.util.Map;

/**
 * Created by xumx on 2016/11/3.
 */
public class EmployeePosR extends BaseDomain {

    /**
     * 员工id
     */
    BigInteger employeeId;
    /**
     * 权限编码
     */
    String posAuthorityKey;

    BigInteger id;

    public EmployeePosR() {
        super();
    }

    public EmployeePosR(Map domainMap) {
        super(domainMap);
    }

    public BigInteger getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(BigInteger employeeId) {
        this.employeeId = employeeId;
    }

    public String getPosAuthorityKey() {
        return posAuthorityKey;
    }

    public void setPosAuthorityKey(String posAuthorityKey) {
        this.posAuthorityKey = posAuthorityKey;
    }

    public BigInteger getId() {
        return id;
    }

    public void setId(BigInteger id) {
        this.id = id;
    }
}
