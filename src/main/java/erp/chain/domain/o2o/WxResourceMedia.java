package erp.chain.domain.o2o;

import java.math.BigInteger;
import java.util.Date;

/**
 * 微信消息
 * Created with IntelliJ IDEA.
 * User: AnCong
 * Date: 2017/1/18
 */
public class WxResourceMedia implements Comparable<WxResourceMedia>{
    BigInteger id;
    BigInteger tenantId;
    /**
     * 图片所在微信服务器url
     */
    String url;
    /**
     * media_id
     */
    String mediaId;
    /**
     * 素材类型1图片（image）、2语音（voice）、3视频（video）和4缩略图（thumb）5图文素材
     */
    BigInteger mediaType;
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

    public String getUrl(){
        return url;
    }

    public void setUrl(String url){
        this.url = url;
    }

    public String getMediaId(){
        return mediaId;
    }

    public void setMediaId(String mediaId){
        this.mediaId = mediaId;
    }

    public BigInteger getMediaType(){
        return mediaType;
    }

    public void setMediaType(BigInteger mediaType){
        this.mediaType = mediaType;
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

    @Override
    public int compareTo(WxResourceMedia o) {
        return (o.lastUpdateAt.compareTo(this.lastUpdateAt));
    }

    public boolean getIsDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(boolean isDeleted) {
        this.isDeleted = isDeleted;
    }
}
