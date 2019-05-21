package erp.chain.domain.system;

import erp.chain.domain.BaseDomain;

import java.math.BigInteger;
import java.sql.Timestamp;
import java.util.Date;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: AnCong
 * Date: 2018/11/2
 * <p>
 * 广告
 * <p>
 * 广告资源 resource_url
 * 本地文件：file://c:/wpos/ad/1.jpeg
 * 本地目录：file://c:/wpos/ad/180921001
 * 网络资源：http://www.smartpos.top/share/item/pictures/1234567890?t=68000001
 * 网络资源：http://www.smartpos.top/share/branch/ads/1234567890?t=68000001
 * 网络资源：http://www.smartpos.top/share/page/1234567890?t=68000001
 * 网络资源：http://vku.youku.com/live/newplay?spm=a2hlv.20025885.m_243130.5!2~5!3~5~5~A&id=14253
 * <p>
 * 媒体类型 media_type：如果指定，则可以检查与广告位是否可匹配
 * text 文字广告
 * picture 图片广告
 * video 视频广告
 */
public class AdContent extends BaseDomain{
    private BigInteger id;
    private boolean isDeleted;
    private BigInteger createdBy;
    private BigInteger updatedBy;
    private Timestamp createdAt;
    private Timestamp updatedAt;
    private BigInteger tenantId;
    private BigInteger branchId;
    private BigInteger bannerId;
    private String name;
    private String mediaType;
    private String content;
    private String resourceUrl;
    private String jumptoUrl;
    private Date beginDate;
    private Date endDate;
    private Integer plays;
    private Integer hits;
    private Integer sortCode;

    public AdContent(){
    }

    public AdContent(Map domainMap){
        super(domainMap);
    }

    public BigInteger getBranchId(){
        return branchId;
    }

    public void setBranchId(BigInteger branchId){
        this.branchId = branchId;
    }

    public BigInteger getId(){
        return id;
    }

    public void setId(BigInteger id){
        this.id = id;
    }

    public boolean getIsDeleted(){
        return isDeleted;
    }

    public void setIsDeleted(boolean deleted){
        isDeleted = deleted;
    }

    public BigInteger getCreatedBy(){
        return createdBy;
    }

    public void setCreatedBy(BigInteger createdBy){
        this.createdBy = createdBy;
    }

    public BigInteger getUpdatedBy(){
        return updatedBy;
    }

    public void setUpdatedBy(BigInteger updatedBy){
        this.updatedBy = updatedBy;
    }

    public Timestamp getCreatedAt(){
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt){
        this.createdAt = createdAt;
    }

    public Timestamp getUpdatedAt(){
        return updatedAt;
    }

    public void setUpdatedAt(Timestamp updatedAt){
        this.updatedAt = updatedAt;
    }

    public BigInteger getTenantId(){
        return tenantId;
    }

    public void setTenantId(BigInteger tenantId){
        this.tenantId = tenantId;
    }

    public BigInteger getBannerId(){
        return bannerId;
    }

    public void setBannerId(BigInteger bannerId){
        this.bannerId = bannerId;
    }

    public String getName(){
        return name;
    }

    public void setName(String name){
        this.name = name;
    }

    public String getMediaType(){
        return mediaType;
    }

    public void setMediaType(String mediaType){
        this.mediaType = mediaType;
    }

    public String getContent(){
        return content;
    }

    public void setContent(String content){
        this.content = content;
    }

    public String getResourceUrl(){
        return resourceUrl;
    }

    public void setResourceUrl(String resourceUrl){
        this.resourceUrl = resourceUrl;
    }

    public String getJumptoUrl(){
        return jumptoUrl;
    }

    public void setJumptoUrl(String jumptoUrl){
        this.jumptoUrl = jumptoUrl;
    }

    public Date getBeginDate(){
        return beginDate;
    }

    public void setBeginDate(Date beginDate){
        this.beginDate = beginDate;
    }

    public Date getEndDate(){
        return endDate;
    }

    public void setEndDate(Date endDate){
        this.endDate = endDate;
    }

    public Integer getPlays(){
        return plays;
    }

    public void setPlays(Integer plays){
        this.plays = plays;
    }

    public Integer getHits(){
        return hits;
    }

    public void setHits(Integer hits){
        this.hits = hits;
    }

    public Integer getSortCode(){
        return sortCode;
    }

    public void setSortCode(Integer sortCode){
        this.sortCode = sortCode;
    }
}
