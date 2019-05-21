package erp.chain.domain;

import erp.chain.annotations.Transient;

import java.math.BigInteger;
import java.util.Date;
import java.util.Map;

/**
 * Created by xumx on 2016/10/31.
 */
public class Pos extends BaseDomain {

    /**
     * 所属机构，默认为0，代表总部
     */
    BigInteger branchId;
    /**
     * 设备码
     */
    String deviceCode;
    /**
     * pos机编码
     */
    String posCode;
    /**
     * 机构编码
     */
    String branchCode;
    /**
     * 机构名称
     */
    String branchName;
    /**
     * pos密码
     */
    String password;
    /**
     * 0停用
     1启用
     */
    Integer status;
    /**
     * 备注
     */
    String memo;
    /**
     * 商户编码
     */
    String tenantCode;

    BigInteger id;
    BigInteger tenantId;
    Date createAt;
    String createBy;
    Date lastUpdateAt;
    String lastUpdateBy;
    boolean isDeleted;
    String accessToken;
    String appName;
    String appVersion;
    BigInteger version;
    BigInteger localId;
    Integer isPullLog=0;
    @Transient
    String partitionCode;

    public String getPartitionCode(){
        return partitionCode;
    }

    public void setPartitionCode(String partitionCode){
        this.partitionCode = partitionCode;
    }

    public Integer getIsPullLog(){
        return isPullLog;
    }

    public void setIsPullLog(Integer pullLog){
        isPullLog = pullLog;
    }

    public BigInteger getLocalId(){
        return localId;
    }

    public void setLocalId(BigInteger localId){
        this.localId = localId;
    }

    public BigInteger getVersion(){
        return version;
    }

    public void setVersion(BigInteger version){
        this.version = version;
    }

    public Pos() {
        super();
    }

    public Pos(Map domainMap) {
        super(domainMap);
    }

    public BigInteger getBranchId() {
        return branchId;
    }

    public void setBranchId(BigInteger branchId) {
        this.branchId = branchId;
    }

    public String getDeviceCode() {
        return deviceCode;
    }

    public void setDeviceCode(String deviceCode) {
        this.deviceCode = deviceCode;
    }

    public String getPosCode() {
        return posCode;
    }

    public void setPosCode(String posCode) {
        this.posCode = posCode;
    }

    public String getBranchCode() {
        return branchCode;
    }

    public void setBranchCode(String branchCode) {
        this.branchCode = branchCode;
    }

    public String getBranchName() {
        return branchName;
    }

    public void setBranchName(String branchName) {
        this.branchName = branchName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getMemo() {
        return memo;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }

    public String getTenantCode() {
        return tenantCode;
    }

    public void setTenantCode(String tenantCode) {
        this.tenantCode = tenantCode;
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

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getAppVersion() {
        return appVersion;
    }

    public void setAppVersion(String appVersion) {
        this.appVersion = appVersion;
    }
}
