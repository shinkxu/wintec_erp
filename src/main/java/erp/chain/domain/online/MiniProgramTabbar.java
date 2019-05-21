package erp.chain.domain.online;

import java.math.BigInteger;
import java.util.Date;

/**
 * Created by liuyandong on 2019-02-18.
 */
public class MiniProgramTabbar {
    /**
     * ID
     */
    private BigInteger id;
    /**
     * 商户ID
     */
    private BigInteger tenantId;
    /**
     * 机构ID
     */
    private BigInteger branchId;
    /**
     * 导航索引
     */
    private Integer tabbarIndex;
    /**
     * 按钮名称
     */
    private String name;
    /**
     * 跳转类型，1-首页，2-积分商城，3-会员中心，4-我的订单，5-我的储值，6-商品购买，7-购物车，127-自定义页面
     */
    private Integer functionType;
    /**
     * 自定义页面
     */
    private String customPage;
    /**
     * 图标，1-图标一，2-图标二，3-图标三
     */
    private Integer iconType;
    private Date createAt;
    private String createBy;
    private Date lastUpdateAt;
    private String lastUpdateBy;
    private boolean isDeleted;

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

    public BigInteger getBranchId() {
        return branchId;
    }

    public void setBranchId(BigInteger branchId) {
        this.branchId = branchId;
    }

    public Integer getTabbarIndex() {
        return tabbarIndex;
    }

    public void setTabbarIndex(Integer tabbarIndex) {
        this.tabbarIndex = tabbarIndex;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getFunctionType() {
        return functionType;
    }

    public void setFunctionType(Integer functionType) {
        this.functionType = functionType;
    }

    public String getCustomPage() {
        return customPage;
    }

    public void setCustomPage(String customPage) {
        this.customPage = customPage;
    }

    public Integer getIconType() {
        return iconType;
    }

    public void setIconType(Integer iconType) {
        this.iconType = iconType;
    }

    public Date getCreateAt() {
        return createAt;
    }

    public void setCreateAt(Date createAt) {
        this.createAt = createAt;
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

    public String getLastUpdateBy() {
        return lastUpdateBy;
    }

    public void setLastUpdateBy(String lastUpdateBy) {
        this.lastUpdateBy = lastUpdateBy;
    }

    public boolean getIsDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(boolean isDeleted) {
        this.isDeleted = isDeleted;
    }
}
