package erp.chain.domain.o2o;

import erp.chain.domain.o2o.vo.ElemeGoodsAttribute;
import erp.chain.domain.o2o.vo.ElemeGoodsNewSpec;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by liuyandong on 2017/5/16.
 */
public class ElemeGoodsItem {
    private BigInteger id;

    private BigInteger itemId;

    private String groupId;

    /**
     * 规格Id||2543
     */
    private BigInteger skuId;

    /**
     * 商品名称||"奶茶"
     */
    private String name = "";

    /**
     * 商品分类Id||26940000135
     */
    private long categoryId = 0;

    /**
     * 商品单价||9.0
     */
    private double price = 0.0;

    /**
     * 商品数量||2
     */
    private int quantity = 0;

    /**
     * 总价||18.0
     */
    private double total = 0.0;
    private List<ElemeGoodsNewSpec> newSpecs;
    private List<ElemeGoodsAttribute> attributes;
    private String groupName;
    private String groupType;

    public BigInteger getId() {
        return id;
    }

    public void setId(BigInteger id) {
        this.id = id;
    }

    public BigInteger getItemId() {
        return itemId;
    }

    public void setItemId(BigInteger itemId) {
        this.itemId = itemId;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public BigInteger getSkuId() {
        return skuId;
    }

    public void setSkuId(BigInteger skuId) {
        this.skuId = skuId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(long categoryId) {
        this.categoryId = categoryId;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public List<ElemeGoodsNewSpec> getNewSpecs() {
        return newSpecs;
    }

    public void setNewSpecs(List<ElemeGoodsNewSpec> newSpecs) {
        this.newSpecs = newSpecs;
    }

    public List<ElemeGoodsAttribute> getAttributes() {
        return attributes;
    }

    public void setAttributes(List<ElemeGoodsAttribute> attributes) {
        this.attributes = attributes;
    }

    public String getTasteName() {
        String tasteName = null;
        if (CollectionUtils.isNotEmpty(attributes)) {
            List<String> attributeValues = new ArrayList<String>();
            for (ElemeGoodsAttribute elemeGoodsAttribute : attributes) {
                attributeValues.add(elemeGoodsAttribute.getValue());
            }
            tasteName = StringUtils.join(attributeValues, ",");
        }
        return tasteName;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getGroupType() {
        return groupType;
    }

    public void setGroupType(String groupType) {
        this.groupType = groupType;
    }
}
