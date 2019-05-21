package erp.chain.domain.system;

import erp.chain.domain.BaseDomain;

import java.math.BigInteger;
import java.util.Date;

/**
 * 功能模块说明
 * Created with IntelliJ IDEA.
 * User: AnCong
 * Date: 2018/3/15
 */
public class MessageNotification extends BaseDomain{
    BigInteger id;
    Date sendTime;
    Integer status;
    Integer type;
    Integer targetType;
    boolean isSendApos;
    boolean isSendWpos;
    String title;
    String content;
    String url;
    BigInteger createEmpId;
    Date createAt;
    String createBy;
    Date lastUpdateAt;
    String lastUpdateBy;
    boolean isDeleted;
    Integer displayType;
    boolean isNewUser;
    boolean isOldUser;
    BigInteger tenantId;

    public BigInteger getTenantId(){
        return tenantId;
    }

    public void setTenantId(BigInteger tenantId){
        this.tenantId = tenantId;
    }

    public BigInteger getId(){
        return id;
    }

    public void setId(BigInteger id){
        this.id = id;
    }

    public Date getSendTime(){
        return sendTime;
    }

    public void setSendTime(Date sendTime){
        this.sendTime = sendTime;
    }

    public Integer getStatus(){
        return status;
    }

    public void setStatus(Integer status){
        this.status = status;
    }

    public Integer getType(){
        return type;
    }

    public void setType(Integer type){
        this.type = type;
    }

    public Integer getTargetType(){
        return targetType;
    }

    public void setTargetType(Integer targetType){
        this.targetType = targetType;
    }

    public boolean getIsSendApos(){
        return isSendApos;
    }

    public void setIsSendApos(boolean sendApos){
        isSendApos = sendApos;
    }

    public boolean getIsSendWpos(){
        return isSendWpos;
    }

    public void setIsSendWpos(boolean sendWpos){
        isSendWpos = sendWpos;
    }

    public String getTitle(){
        return title;
    }

    public void setTitle(String title){
        this.title = title;
    }

    public String getContent(){
        return content;
    }

    public void setContent(String content){
        this.content = content;
    }

    public String getUrl(){
        return url;
    }

    public void setUrl(String url){
        this.url = url;
    }

    public BigInteger getCreateEmpId(){
        return createEmpId;
    }

    public void setCreateEmpId(BigInteger createEmpId){
        this.createEmpId = createEmpId;
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

    public Integer getDisplayType(){
        return displayType;
    }

    public void setDisplayType(Integer displayType){
        this.displayType = displayType;
    }

    public boolean getIsNewUser(){
        return isNewUser;
    }

    public void setIsNewUser(boolean newUser){
        isNewUser = newUser;
    }

    public boolean getIsOldUser(){
        return isOldUser;
    }

    public void setIsOldUser(boolean oldUser){
        isOldUser = oldUser;
    }
}
