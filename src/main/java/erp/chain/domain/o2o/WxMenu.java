package erp.chain.domain.o2o;

import erp.chain.domain.BaseDomain;
import net.sf.json.JSONObject;

import java.math.BigInteger;
import java.util.Date;

/**
 * 微信菜单
 * Created with IntelliJ IDEA.
 * User: AnCong
 * Date: 2016/12/29
 */
public class WxMenu extends BaseDomain {
    /**
     * id
     */
    BigInteger id;
    /**
     * 商户ID
     */
    BigInteger tenantId;
    /**
     * 父菜单id 0表示一级菜单
     */
    BigInteger parentId;
    /**
     * 排序值
     */
    BigInteger rank;
    /**
     * 菜单名称
     */
    String name;
    /**
     * 菜单类型
     */
    Integer menuType;
    /**
     * 文本消息内容
     */
    String msgContent;
    /**
     * 永久素材media_id
     */
    String mediaId;
    /**
     * 链接url
     */
    String url;
    /**
     * 外键wx_menu_item id
     */
    BigInteger itemId;
    Date createAt;
    String createBy;
    Date lastUpdateAt;
    String lastUpdateBy;
    boolean isDeleted;

    private String miniProgramAppId;
    private BigInteger miniProgramItemId;
    private String pagePath;
    private String miniProgramOriginalId;

    public JSONObject getJsonObj(String type) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("name", this.name);
        jsonObject.put("type", type);
        String key = "";
        if ((this.url != null && !this.url.equals("")) && (this.mediaId == null || this.mediaId.equals(""))) {
            jsonObject.put("url", this.url);
            key = "url" + this.rank;
            jsonObject.put("key", key);
        } else if (this.mediaId != null && !this.mediaId.equals("")) {
            jsonObject.put("media_id", this.mediaId);
        } else {
            key = "text" + this.rank;
            jsonObject.put("key", key);
        }
        return jsonObject;
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

    public BigInteger getParentId() {
        return parentId;
    }

    public void setParentId(BigInteger parentId) {
        this.parentId = parentId;
    }

    public BigInteger getRank() {
        return rank;
    }

    public void setRank(BigInteger rank) {
        this.rank = rank;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getMenuType() {
        return menuType;
    }

    public void setMenuType(Integer menuType) {
        this.menuType = menuType;
    }

    public String getMsgContent() {
        return msgContent;
    }

    public void setMsgContent(String msgContent) {
        this.msgContent = msgContent;
    }

    public String getMediaId() {
        return mediaId;
    }

    public void setMediaId(String mediaId) {
        this.mediaId = mediaId;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public BigInteger getItemId() {
        return itemId;
    }

    public void setItemId(BigInteger itemId) {
        this.itemId = itemId;
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

    public boolean getIsDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(boolean isDeleted) {
        this.isDeleted = isDeleted;
    }

    public String getMiniProgramAppId() {
        return miniProgramAppId;
    }

    public void setMiniProgramAppId(String miniProgramAppId) {
        this.miniProgramAppId = miniProgramAppId;
    }

    public BigInteger getMiniProgramItemId() {
        return miniProgramItemId;
    }

    public void setMiniProgramItemId(BigInteger miniProgramItemId) {
        this.miniProgramItemId = miniProgramItemId;
    }

    public String getPagePath() {
        return pagePath;
    }

    public void setPagePath(String pagePath) {
        this.pagePath = pagePath;
    }

    public String getMiniProgramOriginalId() {
        return miniProgramOriginalId;
    }

    public void setMiniProgramOriginalId(String miniProgramOriginalId) {
        this.miniProgramOriginalId = miniProgramOriginalId;
    }
}
