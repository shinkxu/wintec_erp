package erp.chain.domain.system;

import erp.chain.domain.BaseDomain;

import java.math.BigInteger;
import java.util.Date;

/**
 * 系统参数
 * Created with IntelliJ IDEA.
 * User: AnCong
 * Date: 2016/11/18
 */
public class SysConfig extends BaseDomain{
    /**
     * 参数名
     */
    String name;
    /**
     * 参数值
     */
    String value;

    BigInteger id;
    Date createAt;
    String createBy;
    Date lastUpdateAt;
    String lastUpdateBy;
    boolean isDeleted;

    public String getName(){
        return name;
    }

    public void setName(String name){
        this.name = name;
    }

    public String getValue(){
        return value;
    }

    public void setValue(String value){
        this.value = value;
    }

    public BigInteger getId(){
        return id;
    }

    public void setId(BigInteger id){
        this.id = id;
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

    /**
     * 从这里定义系统变量的静态属性,所有的系统变量都应该在这里以静态属性定义
     * 在系统外面外面定义时也要使用静态变量,严禁直接使用字符串
     */
    public static final String SYS_VIP_NUM = "vipnum";
    public static final String SYS_PROMOTION_NUM = "dietnum";
    public static final String SYS_FIRST_CAT_NUM = "firstcatnum";
    public static final String SYS_SECOND_CAT_NUM = "secondcatnum";
    public static final String SYS_GOODS_NUM = "goodsnum";
    public static final String SYS_PACKAGE_NUM = "packagenum";
    public static final String SYS_HOT_GOODS_NUM = "hotgoodsnum";
    public static final String SYS_RECOMMEND_NUM = "recommendnum";
    public static final String SYS_SPEC_NUM = "specnum";
    public static final String SYS_BRANCH_NUM = "branchnum";
    public static final String SYS_EMP_NUM = "empnum";
    public static final String SYS_UNIT_NUM = "unitnum";
    public static final String SYS_PAYMENT_NUM = "paymentnum";
    public static final String SYS_DIETPROMOTION_NUM = "dietpromotionnum";
    public static final String SYS_VIP_STORE_NUM = "vipstorenum";
    public static final String SYS_LABEL_NUM = "labelnum";
    public static final String VIP_STORE_MESSAGE = "vipstoremessage";
    public static final String VIP_STORE_TRADE_MESSAGE = "vipstoretrademessage";
    public static final String VIP_SCORE_MESSAGE = "vipscoremessage";
    public static final String VIP_SCORE_USE_MESSAGE = "vipscoreusemessage";
    public static final String USE_BRANCH_VIP_MESSAGE = "usebranchvipmessage";
}
