package erp.chain.domain;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;
import java.util.Map;

/**
 * Created by xumx on 2016/11/3.
 */
public class Employee extends BaseDomain {

    BigInteger branchId;
    /**
     * 用户ID，sys_user.id（只有设置了用户ID的员工可以登录系统）
     */
    BigInteger userId;
    /**
     * 登录帐号，sys_user.login_name（冗余字段）
     */
    String loginName;
    /**
     * 工号
     */
    String code;
    /**
     * 姓名
     */
    String name;
    /**
     * 本地登录密码
     仅用于POS本地登录
     */
    String passwordForLocal;
    /**
     * 联系电话
     */
    String qq;
    /**
     * QQ号
     */
    String phone;
    /**
     * 邮箱
     */
    String email;
    /**
     * 备注
     */
    String memo;
    /**
     * 1启用2停用
     */
    Integer state;
    BigInteger id;
    BigInteger tenantId;
    String createBy;
    Date createAt;
    String lastUpdateBy;
    Date lastUpdateAt;
    boolean isDeleted;
    /**
     * 1男 2 女
     */
    Integer sex;
    Date birthday;
    String headPortraitBig;
    String headPortraitSmall;
    /**
     * 折扣率（0-100）
     */
    Integer discountRate;
    /**
     * 优惠金额 默认0
     */
    Integer discountAmount;

    BigInteger cardId;
    String cardCode;
    BigInteger version;
    BigInteger localId;

    /**
     * 权限区域
     * */
    String userAreas;

    /**
     * 进价权限
     * */
    Integer pricePrivilege;
    BigDecimal saleRate;
    BigDecimal storeRate;
    BigDecimal ckRate;
    BigDecimal yfkRate;

    public BigDecimal getSaleRate(){
        return saleRate;
    }

    public void setSaleRate(BigDecimal saleRate){
        this.saleRate = saleRate;
    }

    public BigDecimal getStoreRate(){
        return storeRate;
    }

    public void setStoreRate(BigDecimal storeRate){
        this.storeRate = storeRate;
    }

    public BigDecimal getCkRate(){
        return ckRate;
    }

    public void setCkRate(BigDecimal ckRate){
        this.ckRate = ckRate;
    }

    public BigDecimal getYfkRate(){
        return yfkRate;
    }

    public void setYfkRate(BigDecimal yfkRate){
        this.yfkRate = yfkRate;
    }



    public String getUserAreas() {
        return userAreas;
    }

    public void setUserAreas(String userAreas) {
        this.userAreas = userAreas;
    }

    public BigInteger getLocalId() {
        return localId;
    }

    public void setLocalId(BigInteger localId) {
        this.localId = localId;
    }

    public BigInteger getVersion() {
        return version;
    }

    public void setVersion(BigInteger version) {
        this.version = version;
    }

    public Employee() {
        super();
    }

    public Employee(Map domainMap) {
        super(domainMap);
    }

    public BigInteger getBranchId() {
        return branchId;
    }

    public void setBranchId(BigInteger branchId) {
        this.branchId = branchId;
    }

    public BigInteger getUserId() {
        return userId;
    }

    public void setUserId(BigInteger userId) {
        this.userId = userId;
    }

    public String getLoginName() {
        return loginName;
    }

    public void setLoginName(String loginName) {
        this.loginName = loginName;
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

    public String getPasswordForLocal() {
        return passwordForLocal;
    }

    public void setPasswordForLocal(String passwordForLocal) {
        this.passwordForLocal = passwordForLocal;
    }

    public String getQq() {
        return qq;
    }

    public void setQq(String qq) {
        this.qq = qq;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMemo() {
        return memo;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }

    public Integer getState() {
        return state;
    }

    public void setState(Integer state) {
        this.state = state;
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

    public boolean getIsDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(boolean deleted) {
        isDeleted = deleted;
    }

    public Integer getSex() {
        return sex;
    }

    public void setSex(Integer sex) {
        this.sex = sex;
    }

    public Date getBirthday() {
        return birthday;
    }

    public void setBirthday(Date birthday) {
        this.birthday = birthday;
    }

    public String getHeadPortraitBig() {
        return headPortraitBig;
    }

    public void setHeadPortraitBig(String headPortraitBig) {
        this.headPortraitBig = headPortraitBig;
    }

    public String getHeadPortraitSmall() {
        return headPortraitSmall;
    }

    public void setHeadPortraitSmall(String headPortraitSmall) {
        this.headPortraitSmall = headPortraitSmall;
    }

    public Integer getDiscountRate() {
        return discountRate;
    }

    public void setDiscountRate(Integer discountRate) {
        this.discountRate = discountRate;
    }

    public Integer getDiscountAmount() {
        return discountAmount;
    }

    public void setDiscountAmount(Integer discountAmount) {
        this.discountAmount = discountAmount;
    }

    public BigInteger getCardId() {
        return cardId;
    }

    public void setCardId(BigInteger cardId) {
        this.cardId = cardId;
    }

    public String getCardCode() {
        return cardCode;
    }

    public void setCardCode(String cardCode) {
        this.cardCode = cardCode;
    }

    public Integer getPricePrivilege() {
        return pricePrivilege;
    }

    public void setPricePrivilege(Integer pricePrivilege) {
        this.pricePrivilege = pricePrivilege;
    }
}
