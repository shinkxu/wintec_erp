package erp.chain.domain.online;

import java.math.BigInteger;
import java.util.Date;

/**
 * Created by liuyandong on 2018-04-19.
 */
public class MiniProgramSwiper {
    /**
     * ID
     */
    private BigInteger id;
    /**
     * 商户ID
     */
    private BigInteger tenantId;
    /**
     * 机构ID
     */
    private BigInteger branchId;
    /**
     * 轮播图的位置，1-第一张轮播图，2-第二张轮播图，3-第三张轮播图，4-第四张轮播图
     */
    private Integer swiperIndex;
    /**
     * 图片地址
     */
    private String imageUrl;
    /**
     * 类型，1-仅展示，2-点击跳转链接，点击跳转指定功能
     */
    private Integer type;
    /**
     * 跳转的url
     */
    private String jumpUrl;
    /**
     * 跳转类型，1-首页，2-积分商城，3-会员中心，4-我的订单，5-我的储值，6-商品购买，7-购物车，127-自定义页面
     */
    private Integer functionType;
    /**
     * 自定义页面
     */
    private String customPage;
    private Date createAt;
    private String createBy;
    private Date lastUpdateAt;
    private String lastUpdateBy;
    private boolean isDeleted;

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

    public BigInteger getBranchId() {
        return branchId;
    }

    public void setBranchId(BigInteger branchId) {
        this.branchId = branchId;
    }

    public Integer getSwiperIndex() {
        return swiperIndex;
    }

    public void setSwiperIndex(Integer swiperIndex) {
        this.swiperIndex = swiperIndex;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getJumpUrl() {
        return jumpUrl;
    }

    public void setJumpUrl(String jumpUrl) {
        this.jumpUrl = jumpUrl;
    }

    public Integer getFunctionType() {
        return functionType;
    }

    public void setFunctionType(Integer functionType) {
        this.functionType = functionType;
    }

    public String getCustomPage() {
        return customPage;
    }

    public void setCustomPage(String customPage) {
        this.customPage = customPage;
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

    public boolean getIsDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(boolean isDeleted) {
        this.isDeleted = isDeleted;
    }
}
