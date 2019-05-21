package erp.chain.model.online.onlinevip;

import erp.chain.model.online.BasicModel;
import erp.chain.utils.ApplicationHandler;

import javax.validation.constraints.NotNull;
import java.math.BigInteger;

/**
 * Created by liuyandong on 2018-04-13.
 */
public class ObtainVipInfoModel extends BasicModel {
    @NotNull
    private String openId;

    @NotNull
    private BigInteger tenantId;

    @NotNull
    private BigInteger branchId;

    private String scene;

    public String getOpenId() {
        return openId;
    }

    public void setOpenId(String openId) {
        this.openId = openId;
    }

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

    public String getScene() {
        return scene;
    }

    public void setScene(String scene) {
        this.scene = scene;
    }

    @Override
    public void validateAndThrow() {
        super.validateAndThrow();
        ApplicationHandler.inArray(new String[]{"MINI_PROGRAM", "PUBLIC_ACCOUNT"}, scene, "scene");
    }
}
