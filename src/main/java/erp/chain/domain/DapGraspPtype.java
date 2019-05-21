package erp.chain.domain;

/**
 * Created by lipeng on 2017/5/12.
 *
 * 第三方商品表
 */
public class DapGraspPtype extends BaseDomain {

    /**
     * 商户编码
     * */
    String tenantCode;

    /**
     * 商品ID
     * */
    String typeId;

    /**
     * 商品名称
     * */
    String fullName;

    /**
     * 时间戳
     * */
    String timeStamp;

    public String getTenantCode() {
        return tenantCode;
    }

    public String getTypeId() {
        return typeId;
    }

    public String getFullName() {
        return fullName;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTenantCode(String tenantCode) {
        this.tenantCode = tenantCode;
    }

    public void setTypeId(String typeId) {
        this.typeId = typeId;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }
}
