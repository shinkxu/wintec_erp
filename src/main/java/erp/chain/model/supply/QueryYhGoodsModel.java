package erp.chain.model.supply;

import erp.chain.model.SearchModel;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.math.BigInteger;

/**
 * 分页查询要货商品信息
 */
public class QueryYhGoodsModel extends SearchModel {
    @NotNull
    public BigInteger tenantId;
    /**
     * 要货机构
     */
    @NotNull
    public BigInteger yhBranchId;
    /**
     * 配送机构
     */
    @NotNull
    public BigInteger psBranchId;
    /**
     * 商品分类id集合，不同分类按','隔开，格式：catId1,catId2
     */
    @Pattern(regexp = "\\d+(,\\d+)?", message = "格式错误")
    public String catIds;

    /**
     * 只读设置关系中的商品
     */
    @NotNull
    public Boolean onlySetting;

    /**
     * 商品编号或名称模糊查询
     */
    public String goodsNameOrCodeLike;

    /**
     *条码精确检索
     */
    public String barCode;

    /**
     * 不使用总部菜品
     * */
    public int onlySelf;

    public BigInteger getTenantId() {
        return tenantId;
    }

    public void setTenantId(BigInteger tenantId) {
        this.tenantId = tenantId;
    }

    public BigInteger getYhBranchId() {
        return yhBranchId;
    }

    public void setYhBranchId(BigInteger yhBranchId) {
        this.yhBranchId = yhBranchId;
    }

    public BigInteger getPsBranchId() {
        return psBranchId;
    }

    public void setPsBranchId(BigInteger psBranchId) {
        this.psBranchId = psBranchId;
    }

    public String getCatIds() {
        return catIds;
    }

    public void setCatIds(String catIds) {
        this.catIds = catIds;
    }

    public String getGoodsNameOrCodeLike() {
        return like(goodsNameOrCodeLike);
    }
    public String [] getCatIdArray(){
        if (this.catIds != null){
            return catIds.split(",");
        }
        return null;
    }
    public void setGoodsNameOrCodeLike(String goodsNameOrCodeLike) {
        this.goodsNameOrCodeLike = goodsNameOrCodeLike;
    }

    public Boolean getOnlySetting() {
        return onlySetting;
    }

    public void setOnlySetting(Boolean onlySetting) {
        this.onlySetting = onlySetting;
    }

    public int getOnlySelf() {
        return onlySelf;
    }

    public void setOnlySelf(int onlySelf) {
        this.onlySelf = onlySelf;
    }

    public String getBarCode() {
        return barCode;
    }

    public void setBarCode(String barCode) {
        this.barCode = barCode;
    }
}
