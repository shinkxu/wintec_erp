package erp.chain.model.wxpermission;

import erp.chain.model.online.BasicModel;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.NotNull;
import java.math.BigInteger;
import java.util.Date;
import java.util.List;

/**
 * Created by liuyandong on 2018-11-05.
 */
public class DoGrantAuthorityModel extends BasicModel {
    @NotNull
    private BigInteger tenantId;

    @NotEmpty
    private List<BigInteger> userIds;

    @NotEmpty
    private List<String> permissionCodes;

    @NotNull
    private String shopCodes;

    @NotNull
    private String deadLine;

    public BigInteger getTenantId() {
        return tenantId;
    }

    public void setTenantId(BigInteger tenantId) {
        this.tenantId = tenantId;
    }

    public List<BigInteger> getUserIds() {
        return userIds;
    }

    public void setUserIds(List<BigInteger> userIds) {
        this.userIds = userIds;
    }

    public List<String> getPermissionCodes() {
        return permissionCodes;
    }

    public void setPermissionCodes(List<String> permissionCodes) {
        this.permissionCodes = permissionCodes;
    }

    public String getShopCodes() {
        return shopCodes;
    }

    public void setShopCodes(String shopCodes) {
        this.shopCodes = shopCodes;
    }

    public String getDeadLine() {
        return deadLine;
    }

    public void setDeadLine(String deadLine) {
        this.deadLine = deadLine;
    }
}
