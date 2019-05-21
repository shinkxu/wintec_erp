package erp.chain.domain.o2o;

import java.math.BigInteger;
import java.util.Date;

/**
 * 消息回复规则
 * Created with IntelliJ IDEA.
 * User: AnCong
 * Date: 2017/1/19
 */
public class WxReplyKeyRule{
    BigInteger id;
    BigInteger tenantId;
    /**
     * 消息id
     */
    BigInteger messageId;
    /**
     * 规则名称
     */
    String ruleName;
    /**
     * 关键词
     */
    String keyWord;
    String content;
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

    public BigInteger getTenantId(){
        return tenantId;
    }

    public void setTenantId(BigInteger tenantId){
        this.tenantId = tenantId;
    }

    public BigInteger getMessageId(){
        return messageId;
    }

    public void setMessageId(BigInteger messageId){
        this.messageId = messageId;
    }

    public String getRuleName(){
        return ruleName;
    }

    public void setRuleName(String ruleName){
        this.ruleName = ruleName;
    }

    public String getKeyWord(){
        return keyWord;
    }

    public void setKeyWord(String keyWord){
        this.keyWord = keyWord;
    }

    public String getContent(){
        return content;
    }

    public void setContent(String content){
        this.content = content;
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

    public boolean isDeleted(){
        return isDeleted;
    }

    public void setDeleted(boolean deleted){
        isDeleted = deleted;
    }

    public boolean getIsDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(boolean isDeleted) {
        this.isDeleted = isDeleted;
    }
}
