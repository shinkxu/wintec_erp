package erp.chain.domain.system;

import erp.chain.domain.BaseDomain;

import java.math.BigInteger;
import java.util.Date;

/**
 * 功能模块说明
 * Created with IntelliJ IDEA.
 * User: AnCong
 * Date: 2018/3/21
 */
public class MessageReceipt extends BaseDomain{
    /**
     * ID
     */
    BigInteger id;

    /**
     * 消息通知ID
     */
    BigInteger messageNotificationId;

    /**
     * 员工ID
     */
    BigInteger employeeId;

    /**
     * 员工编号
     */
    String employeeCode;

    /**
     * 员工姓名
     */
    String employeeName;

    /**
     * 机构ID
     */
    BigInteger branchId;

    /**
     * 机构编号
     */
    String branchCode;

    /**
     * 机构姓名
     */
    String branchName;

    /**
     * 商户ID
     */
    BigInteger tenantId;

    /**
     * 商户编号
     */
    String tenantCode;

    /**
     * 商户姓名
     */
    String tenantName;

    /**
     * 更新时间
     */
    Date updateTime;

    /**
     * 状态，1-已收到，2-已读
     */
    Integer status;

    Date createAt;
    String createBy;
    Date lastUpdateAt;
    String lastUpdateBy;
    boolean isDeleted;

    public BigInteger getId(){
        return id;
    }

    public void setId(BigInteger id){
        this.id = id;
    }

    public BigInteger getMessageNotificationId(){
        return messageNotificationId;
    }

    public void setMessageNotificationId(BigInteger messageNotificationId){
        this.messageNotificationId = messageNotificationId;
    }

    public BigInteger getEmployeeId(){
        return employeeId;
    }

    public void setEmployeeId(BigInteger employeeId){
        this.employeeId = employeeId;
    }

    public String getEmployeeCode(){
        return employeeCode;
    }

    public void setEmployeeCode(String employeeCode){
        this.employeeCode = employeeCode;
    }

    public String getEmployeeName(){
        return employeeName;
    }

    public void setEmployeeName(String employeeName){
        this.employeeName = employeeName;
    }

    public BigInteger getBranchId(){
        return branchId;
    }

    public void setBranchId(BigInteger branchId){
        this.branchId = branchId;
    }

    public String getBranchCode(){
        return branchCode;
    }

    public void setBranchCode(String branchCode){
        this.branchCode = branchCode;
    }

    public String getBranchName(){
        return branchName;
    }

    public void setBranchName(String branchName){
        this.branchName = branchName;
    }

    public BigInteger getTenantId(){
        return tenantId;
    }

    public void setTenantId(BigInteger tenantId){
        this.tenantId = tenantId;
    }

    public String getTenantCode(){
        return tenantCode;
    }

    public void setTenantCode(String tenantCode){
        this.tenantCode = tenantCode;
    }

    public String getTenantName(){
        return tenantName;
    }

    public void setTenantName(String tenantName){
        this.tenantName = tenantName;
    }

    public Date getUpdateTime(){
        return updateTime;
    }

    public void setUpdateTime(Date updateTime){
        this.updateTime = updateTime;
    }

    public Integer getStatus(){
        return status;
    }

    public void setStatus(Integer status){
        this.status = status;
    }

    public Date getCreateAt(){
        return createAt;
    }

    public void setCreateAt(Date createAt){
        this.createAt = createAt;
    }

    public String getCreateBy(){
        return createBy;
    }

    public void setCreateBy(String createBy){
        this.createBy = createBy;
    }

    public Date getLastUpdateAt(){
        return lastUpdateAt;
    }

    public void setLastUpdateAt(Date lastUpdateAt){
        this.lastUpdateAt = lastUpdateAt;
    }

    public String getLastUpdateBy(){
        return lastUpdateBy;
    }

    public void setLastUpdateBy(String lastUpdateBy){
        this.lastUpdateBy = lastUpdateBy;
    }

    public boolean getIsDeleted(){
        return isDeleted;
    }

    public void setIsDeleted(boolean deleted){
        isDeleted = deleted;
    }
}
