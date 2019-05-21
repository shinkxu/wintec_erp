package erp.chain.domain.o2o;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;

/**
 *
 * Created by wangms on 2017/1/21.
 */
public class DietOrderDetail {
    /**
     * ������ID�������
     */
    BigInteger dietOrderInfoId;
    /**
     * �Ƿ����ײͲ�Ʒ��0�����ǣ�1���ǣ�
     */
    boolean isPackage;
    /**
     * ��Ʒ��ID�������
     */
    BigInteger goodsId;
    /**
     * ��Ʒ����
     */
    String goodsName;
    /**
     * �ײͱ�ID��������ֶ������ݶ�
     */
    BigInteger packageId;
    /**
     * �ײͱ��Ӧ��¼������
     */
    String packageName;
    /**
     * ���۵���
     */
    BigDecimal salePrice;
    /**
     * ʵ�ʵ���
     */
    BigDecimal salePriceActual;
    /**
     * ����
     */
    BigDecimal quantity;
    /**
     * Ӧ���ܼ�
     */
    BigDecimal totalAmount;
    /**
     * ʵ���ܼ�
     */
    BigDecimal receivedAmount;
    /**
     * �Ƿ��ⵥ��������
     */
    boolean isFreeOfCharge;
    /**
     * �Ƿ����
     */
    boolean isRefund;
    /**
     * ζ��
     */
    Integer taste;
    /**
     * ζ����
     */
    String tasteName;
    /**
     * �ߴ�
     */
    Integer size;
    /**
     * �ߴ���
     */
    String sizeName;
    /**
     * ״̬ Ĭ��0
     */
    Integer status;
    BigInteger tenantId;
    BigInteger branchId;

    BigInteger id;
    Date createAt;
    String createBy;
    Date lastUpdateAt;
    String lastUpdateBy;
    boolean isDeleted;
    BigInteger version;
    BigInteger localId;

    String RetailLocalId;
    private String packageCode;
    private String groupId;
    private String groupName;
    private String groupType;

    public String getRetailLocalId() {
        return RetailLocalId;
    }

    public void setRetailLocalId(String retailLocalId) {
        RetailLocalId = retailLocalId;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public BigInteger getBranchId() {
        return branchId;
    }

    public void setBranchId(BigInteger branchId) {
        this.branchId = branchId;
    }

    public BigInteger getTenantId() {
        return tenantId;
    }

    public void setTenantId(BigInteger tenantId) {
        this.tenantId = tenantId;
    }

    public BigInteger getVersion() {
        return version;
    }

    public void setVersion(BigInteger version) {
        this.version = version;
    }

    public BigInteger getLocalId() {
        return localId;
    }

    public void setLocalId(BigInteger localId) {
        this.localId = localId;
    }

    public BigInteger getDietOrderInfoId() {
        return dietOrderInfoId;
    }

    public void setDietOrderInfoId(BigInteger dietOrderInfoId) {
        this.dietOrderInfoId = dietOrderInfoId;
    }

    public boolean isPackage() {
        return isPackage;
    }

    public void setIsPackage(boolean isPackage) {
        this.isPackage = isPackage;
    }

    public BigInteger getGoodsId() {
        return goodsId;
    }

    public void setGoodsId(BigInteger goodsId) {
        this.goodsId = goodsId;
    }

    public String getGoodsName() {
        return goodsName;
    }

    public void setGoodsName(String goodsName) {
        this.goodsName = goodsName;
    }

    public BigInteger getPackageId() {
        return packageId;
    }

    public void setPackageId(BigInteger packageId) {
        this.packageId = packageId;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public BigDecimal getSalePrice() {
        return salePrice;
    }

    public void setSalePrice(BigDecimal salePrice) {
        this.salePrice = salePrice;
    }

    public BigDecimal getSalePriceActual() {
        return salePriceActual;
    }

    public void setSalePriceActual(BigDecimal salePriceActual) {
        this.salePriceActual = salePriceActual;
    }

    public BigDecimal getQuantity() {
        return quantity;
    }

    public void setQuantity(BigDecimal quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public BigDecimal getReceivedAmount() {
        return receivedAmount;
    }

    public void setReceivedAmount(BigDecimal receivedAmount) {
        this.receivedAmount = receivedAmount;
    }

    public boolean getIsFreeOfCharge() {
        return isFreeOfCharge;
    }

    public void setIsFreeOfCharge(boolean isFreeOfCharge) {
        this.isFreeOfCharge = isFreeOfCharge;
    }

    public boolean isRefund() {
        return isRefund;
    }

    public void setIsRefund(boolean isRefund) {
        this.isRefund = isRefund;
    }

    public Integer getTaste() {
        return taste;
    }

    public void setTaste(Integer taste) {
        this.taste = taste;
    }

    public String getTasteName() {
        return tasteName;
    }

    public void setTasteName(String tasteName) {
        this.tasteName = tasteName;
    }

    public Integer getSize() {
        return size;
    }

    public void setSize(Integer size) {
        this.size = size;
    }

    public String getSizeName() {
        return sizeName;
    }

    public void setSizeName(String sizeName) {
        this.sizeName = sizeName;
    }

    public BigInteger getId() {
        return id;
    }

    public void setId(BigInteger id) {
        this.id = id;
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

    public void setIsDeleted(boolean isDeleted) {
        this.isDeleted = isDeleted;
    }

    public String getPackageCode() {
        return packageCode;
    }

    public void setPackageCode(String packageCode) {
        this.packageCode = packageCode;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
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
