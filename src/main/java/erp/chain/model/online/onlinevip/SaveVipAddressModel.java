package erp.chain.model.online.onlinevip;

import erp.chain.model.online.BasicModel;

import javax.validation.constraints.NotNull;
import java.math.BigInteger;

/**
 * Created by liuyandong on 2018-04-19.
 */
public class SaveVipAddressModel extends BasicModel {
    @NotNull
    private BigInteger vipId;

    @NotNull
    private String consignee;

    @NotNull
    private String area;

    @NotNull
    private String address;

    @NotNull
    private String mobilePhone;

    @NotNull
    private String areaName;

    private BigInteger vipAddressId;

    public BigInteger getVipId() {
        return vipId;
    }

    public void setVipId(BigInteger vipId) {
        this.vipId = vipId;
    }

    public String getConsignee() {
        return consignee;
    }

    public void setConsignee(String consignee) {
        this.consignee = consignee;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getMobilePhone() {
        return mobilePhone;
    }

    public void setMobilePhone(String mobilePhone) {
        this.mobilePhone = mobilePhone;
    }

    public String getAreaName() {
        return areaName;
    }

    public void setAreaName(String areaName) {
        this.areaName = areaName;
    }

    public BigInteger getVipAddressId() {
        return vipAddressId;
    }

    public void setVipAddressId(BigInteger vipAddressId) {
        this.vipAddressId = vipAddressId;
    }
}
