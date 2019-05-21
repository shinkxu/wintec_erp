package erp.chain.domain;

import java.math.BigInteger;
import java.math.BigDecimal;
import java.util.Date;
/**
 * 商品加工单
 *
 * @author hefuzi 2016-11-29
 */
public class GoodsProduceOrder extends erp.chain.utils.BaseDomain{
    /**
     * 
     */
    private String code;
    /**
     * 组合/拆分商品id
     */
    private BigInteger goodsId;
    /**
     *
     */
    private BigInteger branchId;
    /**
     * 类型：1-组合 2-拆分
     */
    private Integer type;
    /**
     * 状态：1-未审核 2-已审核
     */
    private Integer status;
    /**
     * 单价
     */
    private BigDecimal price;
    /**
     * 数量
     */
    private BigDecimal quantity;
    /**
     * 制单员工id
     */
    private BigInteger makeEmpId;
    /**
     * 审核员工id
     */
    private BigInteger auditEmpId;
    /**
     * 制单时间
     */
    private Date makeAt;
    /**
     * 审核时间
     */
    private Date auditAt;

    /**
     * 是否更新商品档案
     * */
    public Integer isUpdatePrice;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public BigInteger getGoodsId() {
        return goodsId;
    }

    public void setGoodsId(BigInteger goodsId) {
        this.goodsId = goodsId;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public BigDecimal getQuantity() {
        return quantity;
    }

    public void setQuantity(BigDecimal quantity) {
        this.quantity = quantity;
    }

    public BigInteger getMakeEmpId() {
        return makeEmpId;
    }

    public void setMakeEmpId(BigInteger makeEmpId) {
        this.makeEmpId = makeEmpId;
    }

    public BigInteger getAuditEmpId() {
        return auditEmpId;
    }

    public void setAuditEmpId(BigInteger auditEmpId) {
        this.auditEmpId = auditEmpId;
    }

    public Date getMakeAt() {
        return makeAt;
    }

    public void setMakeAt(Date makeAt) {
        this.makeAt = makeAt;
    }

    public Date getAuditAt() {
        return auditAt;
    }

    public void setAuditAt(Date auditAt) {
        this.auditAt = auditAt;
    }

    public BigInteger getBranchId() {
        return branchId;
    }

    public void setBranchId(BigInteger branchId) {
        this.branchId = branchId;
    }

    public Integer getIsUpdatePrice() {
        return isUpdatePrice;
    }

    public void setIsUpdatePrice(Integer isUpdatePrice) {
        this.isUpdatePrice = isUpdatePrice;
    }
}