package erp.chain.domain.o2o;

import java.math.BigDecimal;

/**
 * Created by wangms on 2017/4/1.
 */
public class MeituanOrderDetail {
    /**
     * erp����Ʒid
     */
    String app_food_code;
    /**
     * 	�ͺ�����
     */
    Integer box_num;
    /**
     * �ͺе���
     */
    BigDecimal box_price;
    /**
     * 	��Ʒ��
     */
    String food_name;
    /**
     * 	��Ʒԭ��
     */
    BigDecimal price;
    /**
     * rp����Ʒsku
     */
    String sku_id;
    /**
     * 	��Ʒ����
     */
    String quantity;
    /**
     * ��λ
     */
    String unit;

    public String getApp_food_code() {
        return app_food_code;
    }

    public void setApp_food_code(String app_food_code) {
        this.app_food_code = app_food_code;
    }

    public Integer getBox_num() {
        return box_num;
    }

    public void setBox_num(Integer box_num) {
        this.box_num = box_num;
    }

    public BigDecimal getBox_price() {
        return box_price;
    }

    public void setBox_price(BigDecimal box_price) {
        this.box_price = box_price;
    }

    public String getFood_name() {
        return food_name;
    }

    public void setFood_name(String food_name) {
        this.food_name = food_name;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public String getSku_id() {
        return sku_id;
    }

    public void setSku_id(String sku_id) {
        this.sku_id = sku_id;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }
}
