package erp.chain.domain.o2o;

import java.math.BigInteger;
import java.util.Date;

/**
 * 微信消息
 * Created with IntelliJ IDEA.
 * User: AnCong
 * Date: 2017/1/19
 */
public class WxNewsArticle{
    BigInteger id;
    BigInteger tenantId;
    /**
     * media_id
     */
    String mediaId;
    /**
     * 标题
     */
    String title;
    /**
     * 图文消息的封面图片素材id
     */
    String thumbMediaId;
    /**
     * 是否显示封面，0为false，即不显示，1为true，即显示
     */
    boolean showCoverPic;
    /**
     * 作者
     */
    String author;
    /**
     * 图文消息的具体内容
     */
    String content;
    /**
     * 图文消息的原文地址，即点击“阅读原文”后的URL
     */
    /**
     * 图文消息的摘要，仅有单图文消息才有摘要，多图文此处为空
     */
    String digest;
    String contentSourceUrl;
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

    public String getMediaId(){
        return mediaId;
    }

    public void setMediaId(String mediaId){
        this.mediaId = mediaId;
    }

    public String getTitle(){
        return title;
    }

    public void setTitle(String title){
        this.title = title;
    }

    public String getThumbMediaId(){
        return thumbMediaId;
    }

    public void setThumbMediaId(String thumbMediaId){
        this.thumbMediaId = thumbMediaId;
    }

    public boolean isShowCoverPic(){
        return showCoverPic;
    }

    public void setShowCoverPic(boolean showCoverPic){
        this.showCoverPic = showCoverPic;
    }

    public String getAuthor(){
        return author;
    }

    public void setAuthor(String author){
        this.author = author;
    }

    public String getContent(){
        return content;
    }

    public void setContent(String content){
        this.content = content;
    }

    public String getDigest(){
        return digest;
    }

    public void setDigest(String digest){
        this.digest = digest;
    }

    public String getContentSourceUrl(){
        return contentSourceUrl;
    }

    public void setContentSourceUrl(String contentSourceUrl){
        this.contentSourceUrl = contentSourceUrl;
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
