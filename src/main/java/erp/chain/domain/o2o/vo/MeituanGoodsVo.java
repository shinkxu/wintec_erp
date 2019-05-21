package erp.chain.domain.o2o.vo;

import erp.chain.domain.Goods;

import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * ���Ų�Ʒ��
 * Created by wangms on 2017/3/22.
 */
public class MeituanGoodsVo {
    String eDishCode;
    BigInteger dishId;
    String dishName;
    String categoryName;
    String waiMaiDishSkuBases;
    BigDecimal price;
    String spec;
    String eDishSkuCode;
    Goods goods;

    public String getSpec() {
        return spec;
    }

    public void setSpec(String spec) {
        this.spec = spec;
    }

    public String geteDishSkuCode() {
        return eDishSkuCode;
    }

    public void seteDishSkuCode(String eDishSkuCode) {
        this.eDishSkuCode = eDishSkuCode;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public Goods getGoods() {
        return goods;
    }

    public void setGoods(Goods goods) {
        this.goods = goods;
    }

    public String geteDishCode() {
        return eDishCode;
    }

    public void seteDishCode(String eDishCode) {
        this.eDishCode = eDishCode;
    }

    public BigInteger getDishId() {
        return dishId;
    }

    public void setDishId(BigInteger dishId) {
        this.dishId = dishId;
    }

    public String getDishName() {
        return dishName;
    }

    public void setDishName(String dishName) {
        this.dishName = dishName;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getWaiMaiDishSkuBases() {
        return waiMaiDishSkuBases;
    }

    public void setWaiMaiDishSkuBases(String waiMaiDishSkuBases) {
        this.waiMaiDishSkuBases = waiMaiDishSkuBases;
    }
}
