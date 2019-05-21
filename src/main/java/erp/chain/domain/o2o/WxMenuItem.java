package erp.chain.domain.o2o;

import java.math.BigInteger;
import java.util.Date;

/**
 * 微信消息
 * Created with IntelliJ IDEA.
 * User: AnCong
 * Date: 2017/1/13
 */
public class WxMenuItem{
    BigInteger id;
    /**
     * 内部链接名称
     */
    String name;
    /**
     * 菜单响应key
     */
    String key;
    String controller;
    String action;
    String params;
    Date createAt;
    String createBy;
    Date lastUpdateAt;
    String lastUpdateBy;
    boolean isDeleted;

    /*public WxMenuItem(BigInteger id, String name, String key, String controller, String action, String params, Date createAt, String createBy, Date lastUpdateAt, String lastUpdateBy, boolean isDeleted){
        this.id = id;
        this.name = name;
        this.key = key;
        this.controller = controller;
        this.action = action;
        this.params = params;
        this.createAt = createAt;
        this.createBy = createBy;
        this.lastUpdateAt = lastUpdateAt;
        this.lastUpdateBy = lastUpdateBy;
        this.isDeleted = isDeleted;
    }*/

    public BigInteger getId(){
        return id;
    }

    public void setId(BigInteger id){
        this.id = id;
    }

    public String getName(){
        return name;
    }

    public void setName(String name){
        this.name = name;
    }

    public String getKey(){
        return key;
    }

    public void setKey(String key){
        this.key = key;
    }

    public String getController(){
        return controller;
    }

    public void setController(String controller){
        this.controller = controller;
    }

    public String getAction(){
        return action;
    }

    public void setAction(String action){
        this.action = action;
    }

    public String getParams(){
        return params;
    }

    public void setParams(String params){
        this.params = params;
    }

    public Date getCreateAt(){
        return createAt;
    }

    public void setCreateAt(Date createAt){
        this.createAt = createAt;
    }

    public String getCreateBy(){
        return createBy;
    }

    public void setCreateBy(String createBy){
        this.createBy = createBy;
    }

    public Date getLastUpdateAt(){
        return lastUpdateAt;
    }

    public void setLastUpdateAt(Date lastUpdateAt){
        this.lastUpdateAt = lastUpdateAt;
    }

    public String getLastUpdateBy(){
        return lastUpdateBy;
    }

    public void setLastUpdateBy(String lastUpdateBy){
        this.lastUpdateBy = lastUpdateBy;
    }

    public boolean isDeleted(){
        return isDeleted;
    }

    public void setDeleted(boolean deleted){
        isDeleted = deleted;
    }

    public boolean getIsDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(boolean isDeleted) {
        this.isDeleted = isDeleted;
    }
}
