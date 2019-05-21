package erp.chain.domain;

import java.math.BigInteger;
import java.util.Date;
import java.util.Map;

/**
 * Created by xumx on 2016/11/3.
 */
public class Menu extends BaseDomain {

    /**
     * 菜牌编码
     */
    String menuCode;
    /**
     * 菜牌名称
     */
    String menuName;
    /**
     * 生效范围：1线上线下2线上3线下
     */
    Integer menuType;
    /**
     * 备注
     */
    String memo;
    /**
     * 0未激活1激活
     */
    boolean isActive;
    /**
     * 开始日期
     之后需要扩展可改为String cronExpression表达式
     */
    Date cronStartAt;
    /**
     * 结束日期
     */
    Date cronEndAt;
    /**
     * 0停用1正常
     */
    Integer status;

    BigInteger id;
    BigInteger tenantId;
    String createBy;
    Date createAt;
    String lastUpdateBy;
    Date lastUpdateAt;
    boolean isDeleted;
    BigInteger version;

    /**
     * 创建机构
     * */
    BigInteger createBranchId;

    public BigInteger getCreateBranchId() {
        return createBranchId;
    }

    public void setCreateBranchId(BigInteger createBranchId) {
        this.createBranchId = createBranchId;
    }

    public BigInteger getVersion(){
        return version;
    }

    public void setVersion(BigInteger version){
        this.version = version;
    }

    public Menu() {
        super();
    }

    public Menu(Map domainMap) {
        super(domainMap);
    }

    public String getMenuCode() {
        return menuCode;
    }

    public void setMenuCode(String menuCode) {
        this.menuCode = menuCode;
    }

    public String getMenuName() {
        return menuName;
    }

    public void setMenuName(String menuName) {
        this.menuName = menuName;
    }

    public Integer getMenuType() {
        return menuType;
    }

    public void setMenuType(Integer menuType) {
        this.menuType = menuType;
    }

    public String getMemo() {
        return memo;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public Date getCronStartAt() {
        return cronStartAt;
    }

    public void setCronStartAt(Date cronStartAt) {
        this.cronStartAt = cronStartAt;
    }

    public Date getCronEndAt() {
        return cronEndAt;
    }

    public void setCronEndAt(Date cronEndAt) {
        this.cronEndAt = cronEndAt;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
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

    public String getCreateBy() {
        return createBy;
    }

    public void setCreateBy(String createBy) {
        this.createBy = createBy;
    }

    public Date getCreateAt() {
        return createAt;
    }

    public void setCreateAt(Date createAt) {
        this.createAt = createAt;
    }

    public String getLastUpdateBy() {
        return lastUpdateBy;
    }

    public void setLastUpdateBy(String lastUpdateBy) {
        this.lastUpdateBy = lastUpdateBy;
    }

    public Date getLastUpdateAt() {
        return lastUpdateAt;
    }

    public void setLastUpdateAt(Date lastUpdateAt) {
        this.lastUpdateAt = lastUpdateAt;
    }

    public boolean isDeleted() {
        return isDeleted;
    }

    public void setDeleted(boolean deleted) {
        isDeleted = deleted;
    }
}
