package erp.chain.domain;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;
import java.util.Map;

/**
 * Created by xumx on 2016/10/31.
 */
public class Tenant extends BaseDomain {
    /**
     * 对应的用户ID，sys_user.id
     */
    BigInteger userId;
    /**
     * 代理商ID，agent.id
     */
    BigInteger agentId;
    /**
     * 商户ID，8位纯数字
     */
    String code;
    /**
     * 商户名称
     */
    String name;
    /**
     * 详细地址
     */
    String address;
    /**
     * 所在省份区划代码
     */
    String province;
    /**
     * 所在地市区划代码
     */
    String city;
    /**
     * 所在区县区划代码
     */
    String county;
    /**
     * 所属区域名称（冗余字段，系统自动生成）
     */
    String areaName;
    /**
     * 手机号
     */
    String phoneNumber;
    /**
     * 联系人
     */
    String linkman;
    /**
     * 行业：1-快餐店
     */
    Integer business;
    /**
     * 一级业态：1-餐饮，2-零售
     */
    String business1;
    /**
     * 二级业态
     */
    String business2;
    /**
     * 三级业态
     */
    String business3;
    /**
     * 可查看的菜单级别：1-正式发布，2-预览，3-测试
     */
    Integer menuLevel;
    /**
     *
     */
    String email;
    /**
     *
     */
    String qq;
    /**
     * 状态：0-未激活，1-启用，2-停用
     */
    Integer status;
    /**
     * 缴费额（指商户激活以来，支付的软件费总金额）
     */
    BigDecimal paidTotal;
    /**
     * 当前激活的软件版本ID，goods.id
     */
    BigInteger goodsId;
    /**
     * 商户类型：1.云平台商户；2.方象产品用户
     */
    Integer type;
    /**
     * 备注
     */
    String remark;

    /**
     * 是否为预览版商户
     */
    boolean isBate;

    String domainName;

    String partitionCode;

    /**
     * app版本
     */
    String appVersion;

    BigInteger id;
    Date createAt;
    String createBy;
    Date lastUpdateAt;
    String lastUpdateBy;
    boolean isDeleted;
    boolean isTest;
    Integer tenantType;

    String imgUrl;
    String cashierName;
    String cashierPwd;
    String agentStockId;
    boolean isBranchManagementVip;
    boolean isBranchManagementStore;
    boolean isClearScore;

    public boolean isClearScore() {
        return isClearScore;
    }
    public void setIsClearScore(boolean isClearScore) {
        this.isClearScore = isClearScore;
    }
    public boolean getIsBranchManagementStore() {
        return isBranchManagementStore;
    }

    public void setIsBranchManagementStore(boolean isBranchManagementStore) {
        this.isBranchManagementStore = isBranchManagementStore;
    }

    public boolean isBranchManagementVip() {
        return isBranchManagementVip;
    }

    public void setIsBranchManagementVip(boolean isBranchManagementVip) {
        this.isBranchManagementVip = isBranchManagementVip;
    }

    public Tenant() {
        super();
    }

    public Tenant(Map domainMap) {
        super(domainMap);
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public String getCashierName() {
        return cashierName;
    }

    public void setCashierName(String cashierName) {
        this.cashierName = cashierName;
    }

    public String getCashierPwd() {
        return cashierPwd;
    }

    public void setCashierPwd(String cashierPwd) {
        this.cashierPwd = cashierPwd;
    }

    public String getAgentStockId() {
        return agentStockId;
    }

    public void setAgentStockId(String agentStockId) {
        this.agentStockId = agentStockId;
    }

    public Integer getTenantType() {
        return tenantType;
    }

    public void setTenantType(Integer tenantType) {
        this.tenantType = tenantType;
    }

    public boolean isTest() {
        return isTest;
    }

    public void setTest(boolean test) {
        isTest = test;
    }

    public BigInteger getUserId() {
        return userId;
    }

    public void setUserId(BigInteger userId) {
        this.userId = userId;
    }

    public BigInteger getAgentId() {
        return agentId;
    }

    public void setAgentId(BigInteger agentId) {
        this.agentId = agentId;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCounty() {
        return county;
    }

    public void setCounty(String county) {
        this.county = county;
    }

    public String getAreaName() {
        return areaName;
    }

    public void setAreaName(String areaName) {
        this.areaName = areaName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getLinkman() {
        return linkman;
    }

    public void setLinkman(String linkman) {
        this.linkman = linkman;
    }

    public Integer getBusiness() {
        return business;
    }

    public void setBusiness(Integer business) {
        this.business = business;
    }

    public String getBusiness1() {
        return business1;
    }

    public void setBusiness1(String business1) {
        this.business1 = business1;
    }

    public String getBusiness2() {
        return business2;
    }

    public void setBusiness2(String business2) {
        this.business2 = business2;
    }

    public String getBusiness3() {
        return business3;
    }

    public void setBusiness3(String business3) {
        this.business3 = business3;
    }

    public Integer getMenuLevel() {
        return menuLevel;
    }

    public void setMenuLevel(Integer menuLevel) {
        this.menuLevel = menuLevel;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getQq() {
        return qq;
    }

    public void setQq(String qq) {
        this.qq = qq;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public BigDecimal getPaidTotal() {
        return paidTotal;
    }

    public void setPaidTotal(BigDecimal paidTotal) {
        this.paidTotal = paidTotal;
    }

    public BigInteger getGoodsId() {
        return goodsId;
    }

    public void setGoodsId(BigInteger goodsId) {
        this.goodsId = goodsId;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public boolean isBate() {
        return isBate;
    }

    public void setBate(boolean bate) {
        isBate = bate;
    }

    public String getAppVersion() {
        return appVersion;
    }

    public void setAppVersion(String appVersion) {
        this.appVersion = appVersion;
    }

    public BigInteger getId() {
        return id;
    }

    public void setId(BigInteger id) {
        this.id = id;
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

    public boolean isDeleted() {
        return isDeleted;
    }

    public void setDeleted(boolean deleted) {
        isDeleted = deleted;
    }

    public String getDomainName() {
        return domainName;
    }

    public void setDomainName(String domainName) {
        this.domainName = domainName;
    }

    public String getPartitionCode() {
        return partitionCode;
    }

    public void setPartitionCode(String partitionCode) {
        this.partitionCode = partitionCode;
    }
}
