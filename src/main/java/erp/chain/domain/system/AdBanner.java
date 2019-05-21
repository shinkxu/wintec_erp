package erp.chain.domain.system;

import erp.chain.domain.BaseDomain;

import java.math.BigInteger;
import java.sql.Timestamp;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: AnCong
 * Date: 2018/11/2
 *
 * 横幅/广告位、公告位
 * 横幅广告是横跨于网页或APP上的矩形公告牌，当用户点击这些横幅的时候，可以链接到广告内容的网页
 * 一个广告位可以承接多个广告
 * <p>
 * 名称 name：广告媒介使用name获取广告位定义，采用“.”分隔的多段命名
 * web.home.top：website表示放置在网站页面的banner
 * wap.home.top：wap表示放置在微网站页面的banner，包括微信端、手机浏览器端
 * applet.home.top：applet表示在各种小程序页面的banner，包括微信小程序，支付宝小程序等
 * mobile.erp.top：表示在云管理APP页面的banner
 * mobile.pos.top：表示在移动收银端页面的banner
 * pos.side.left：pos.side表示在收款机副屏上的banner
 * <p>
 * 横幅类型 banner_type：
 * text 文字广告，可以显示无格式纯文本或html文本
 * picture 图片广告，可以显示各种图片格式
 * video 视频广告，可以显示各种视频格式
 * any 任意媒体，不限制广告媒体类型
 * <p>
 * 展示方式 display_mode：
 * slide：幻灯片，屏幕切换广告内容的方式
 * tile: 平铺，屏幕以瓷砖拼接方式同时显示广告所有内容
 * cover：封面，屏幕只展示单一广告内容
 * <p>
 * 按横幅获取广告url： //~/banner/positon?name=xx
 * 前台使用name从广告接口获取播放内容，返回json格式：
 * {
 * "name": "pos.slide.left",
 * "description": "POS副屏左侧横幅广告"
 * banner_type: "picture"
 * content: [{
 * resource_url:...,
 * jumpto_url:....，
 * },
 * content: {
 * resource_url:...,
 * jumpto_url:...
 * }]
 * }
 */
public class AdBanner extends BaseDomain{

    private BigInteger id;
    private boolean isDeleted;
    private BigInteger createdBy;
    private BigInteger updatedBy;
    private Timestamp createdAt;
    private Timestamp updatedAt;
    private BigInteger tenantId;
    private BigInteger branchId;
    private String name;
    private String description;
    private String bannerType;
    private String displayMode;
    private Integer width;
    private Integer height;
    private boolean active;
    private Integer sortCode;

    public AdBanner(Map domainMap){
        super(domainMap);
    }

    public AdBanner(){
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

    public BigInteger getBranchId(){
        return branchId;
    }

    public void setBranchId(BigInteger branchId){
        this.branchId = branchId;
    }

    public String getName(){
        return name;
    }

    public void setName(String name){
        this.name = name;
    }

    public String getDescription(){
        return description;
    }

    public void setDescription(String description){
        this.description = description;
    }

    public String getBannerType(){
        return bannerType;
    }

    public void setBannerType(String bannerType){
        this.bannerType = bannerType;
    }

    public String getDisplayMode(){
        return displayMode;
    }

    public void setDisplayMode(String displayMode){
        this.displayMode = displayMode;
    }

    public Integer getWidth(){
        return width;
    }

    public void setWidth(Integer width){
        this.width = width;
    }

    public Integer getHeight(){
        return height;
    }

    public void setHeight(Integer height){
        this.height = height;
    }

    public boolean isActive(){
        return active;
    }

    public void setActive(boolean active){
        this.active = active;
    }

    public Integer getSortCode(){
        return sortCode;
    }

    public void setSortCode(Integer sortCode){
        this.sortCode = sortCode;
    }
}
