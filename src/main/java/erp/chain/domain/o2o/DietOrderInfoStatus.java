package erp.chain.domain.o2o;

/**
 * 功能模块说明
 * Created with IntelliJ IDEA.
 * User: AnCong
 * Date: 2017/2/17
 */
public class DietOrderInfoStatus{
    /**
     * 订单类型 1：堂食
     */
    public static final Integer ORDERMODE_TANGSHI = 1;

    /**
     * 订单类型 2：店内自提
     */
    public static final Integer ORDERMODE_ZITI = 2;

    /**
     * 订单类型 3:预约点餐
     */
    public static final Integer ORDERMODE_DIANCAN = 3;

    /**
     * 订单类型 4：外卖
     */
    public static final Integer ORDERMODE_WAIMAI = 4;

    /**
     * 订单类型 6：美团外卖
     */
    public static final Integer ORDERMODE_MEITUAN = 6;

    /**
     * 订单类型 7：饿了么外卖
     */
    public static final Integer ORDERMODE_ELEME = 7;

    /**
     * 订单状态 1:已下单
     */
    public static final Integer ORDERSTATUS_YIXIADAN = 1;
    /**
     * 订单状态 2:已接单,提交厨房
     */
    public static final Integer ORDERSTATUS_YIJIEDAN = 2;
    /**
     * 订单状态 3:正在派送<外卖状态>
     */
    public static final Integer ORDERSTATUS_PAISONGZHONG = 3;
    /**
     * 订单状态 4:已派送<外卖状态>
     */
    public static final Integer ORDERSTATUS_YIPAISONG = 4;
    /**
     * 订单状态 5:已上菜
     */
    public static final Integer ORDERSTATUS_YISHANGCAI = 5;
    /**
     * 订单状态 6:已结束（暂停用）
     */
    public static final Integer ORDERSTATUS_OVER = 6;
    /**
     * 订单状态 7:已自提<堂食及自提状态>
     */
    public static final Integer ORDERSTATUS_YIZITI = 7;
    /**
     * 订单状态 8:排队中
     */
    public static final Integer ORDERSTATUS_PAIDUI = 8;
    /**
     * 订单状态 9:已拒绝
     */
    public static final Integer ORDERSTATUS_REFUSED = 9;
    /**
     * 订单状态 10:已取消订单
     */
    public static final Integer ORDERSTATUS_CANCEL = 10;




    /**
     * 支付状态 0：未支付
     */
    public static final Integer PAYSTATUS_NO = 0;
    /**
     * 支付状态 1：已支付
     */
    public static final Integer PAYSTATUS_YES = 1;

    /**
     *  1:微信支付
     */
    public static final Integer PAY_WAY_WEPAY = 1;
    /**
     * 2:支付宝支付
     */
    public static final Integer PAY_WAY_ALIPAY = 2;
    /**
     * 3:货到付款
     */
    public static final Integer PAY_WAY_FACE = 3;
}
