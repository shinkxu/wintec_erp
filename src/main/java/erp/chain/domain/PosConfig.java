package erp.chain.domain;

import java.math.BigInteger;
import java.util.Date;
import java.util.Map;

/**
 * Created by xumx on 2016/11/3.
 */
public class PosConfig extends BaseDomain {

    BigInteger posId;
    /**
     * POS配置参数
     配置参数可以分组，每组作为一个参数包，具有不同的用途
     */
    String packageName;
    /**
     * 版本
     程序升级后，如果设置参数的结构发生变化，需要同时改变设置参数的版本标识，原则上与第一个使用的程序版本一致
     */
    String posConfgVersion;
    /**
     * 各设置参数以json格式文本保存在字段中，完成支持json语法

     空设置
     {}

     基本结构
     {
     "key1": {
     “key1-1": 'value',
     "key1-2": 3000 ,
     desc:'说明1'
     },
     "key2-1": {
     "ip": "localhost",
     "port": 8000
     desc:'说明2'
     },
     }
     */
    String config;
    /**
     * 数据在本地被修改0否1是
     */
    Integer lsDirty;

    BigInteger id;
    BigInteger tenantId;
    String createBy;
    Date createAt;
    String lastUpdateBy;
    Date lastUpdateAt;
    boolean isDeleted;

    public PosConfig() {
        super();
    }

    public PosConfig(Map domainMap) {
        super(domainMap);
    }

    public BigInteger getPosId() {
        return posId;
    }

    public void setPosId(BigInteger posId) {
        this.posId = posId;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getPosConfgVersion() {
        return posConfgVersion;
    }

    public void setPosConfgVersion(String posConfgVersion) {
        this.posConfgVersion = posConfgVersion;
    }

    public String getConfig() {
        return config;
    }

    public void setConfig(String config) {
        this.config = config;
    }

    public Integer getLsDirty() {
        return lsDirty;
    }

    public void setLsDirty(Integer lsDirty) {
        this.lsDirty = lsDirty;
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

    public String getCreateBy() {
        return createBy;
    }

    public void setCreateBy(String createBy) {
        this.createBy = createBy;
    }

    public Date getCreateAt() {
        return createAt;
    }

    public void setCreateAt(Date createAt) {
        this.createAt = createAt;
    }

    public String getLastUpdateBy() {
        return lastUpdateBy;
    }

    public void setLastUpdateBy(String lastUpdateBy) {
        this.lastUpdateBy = lastUpdateBy;
    }

    public Date getLastUpdateAt() {
        return lastUpdateAt;
    }

    public void setLastUpdateAt(Date lastUpdateAt) {
        this.lastUpdateAt = lastUpdateAt;
    }

    public boolean isDeleted() {
        return isDeleted;
    }

    public void setDeleted(boolean deleted) {
        isDeleted = deleted;
    }
}
