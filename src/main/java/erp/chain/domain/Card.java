package erp.chain.domain;

import java.math.BigInteger;
import java.util.Date;
import java.util.Map;

/**
 * Created by xumx on 2016/11/15.
 */
public class Card extends BaseDomain {

    /**
     * 卡号
     */
    String code;
    /**
     * 卡类型
     */
    Integer type;
    /**
     * 卡状态(1:启动 0:停用)
     */
    Integer state;
    /**
     * 密钥
     */
    String secretKey;
    /**
     * 商户code
     */
    String tenantCode;
    /**
     * 机构code
     */
    String branchCode;
    /**
     * 授权卡id
     */
    BigInteger authCardId;
    /**
     * 持卡人id
     */
    BigInteger holderId;
    BigInteger branchId;

    BigInteger id;
    BigInteger tenantId;
    Date createAt;
    String createBy;
    Date lastUpdateAt;
    String lastUpdateBy;
    boolean isDeleted;
    String cardFaceNum;

    public String getCardFaceNum(){
        return cardFaceNum;
    }

    public void setCardFaceNum(String cardFaceNum){
        this.cardFaceNum = cardFaceNum;
    }

    public BigInteger getBranchId(){
        return branchId;
    }

    public void setBranchId(BigInteger branchId){
        this.branchId = branchId;
    }

    public Card() {
        super();
    }

    public Card(Map domainMap) {
        super(domainMap);
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Integer getState() {
        return state;
    }

    public void setState(Integer state) {
        this.state = state;
    }

    public String getSecretKey() {
        return secretKey;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }

    public String getTenantCode() {
        return tenantCode;
    }

    public void setTenantCode(String tenantCode) {
        this.tenantCode = tenantCode;
    }

    public String getBranchCode() {
        return branchCode;
    }

    public void setBranchCode(String branchCode) {
        this.branchCode = branchCode;
    }

    public BigInteger getAuthCardId() {
        return authCardId;
    }

    public void setAuthCardId(BigInteger authCardId) {
        this.authCardId = authCardId;
    }

    public BigInteger getHolderId() {
        return holderId;
    }

    public void setHolderId(BigInteger holderId) {
        this.holderId = holderId;
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
}
