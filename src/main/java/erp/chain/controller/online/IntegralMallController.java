package erp.chain.controller.online;

import erp.chain.annotations.ApiRestAction;
import erp.chain.model.online.integralmall.*;
import erp.chain.service.online.IntegralMallService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Created by liuyandong on 2018-10-25.
 */
@Controller
@RequestMapping(value = "/integralMall")
public class IntegralMallController {
    /**
     * 查询积分商城所有商品
     *
     * @return
     */
    @RequestMapping(value = "/listGoodsInfos", method = RequestMethod.GET)
    @ResponseBody
    @ApiRestAction(modelClass = ListGoodsInfosModel.class, serviceClass = IntegralMallService.class, serviceMethodName = "listGoodsInfos", error = "获取商品信息失败")
    public String listGoodsInfos() {
        return null;
    }

    /**
     * 保存积分商城订单
     *
     * @return
     */
    @RequestMapping(value = "/saveOrder", method = RequestMethod.POST)
    @ResponseBody
    @ApiRestAction(modelClass = SaveOrderModel.class, serviceClass = IntegralMallService.class, serviceMethodName = "saveOrder", error = "保存订单失败")
    public String saveOrder() {
        return null;
    }

    /**
     * 获取积分商城门店信息
     *
     * @return
     */
    @RequestMapping(value = "/obtainBranchInfo", method = RequestMethod.GET)
    @ResponseBody
    @ApiRestAction(modelClass = ObtainBranchInfoModel.class, serviceClass = IntegralMallService.class, serviceMethodName = "obtainBranchInfo", error = "获取门店信息失败")
    public String obtainBranchInfo() {
        return null;
    }

    /**
     * 获取积分商城商品明细
     *
     * @return
     */
    @RequestMapping(value = "/obtainGoodsInfo", method = RequestMethod.GET)
    @ResponseBody
    @ApiRestAction(modelClass = ObtainGoodsInfoModel.class, serviceClass = IntegralMallService.class, serviceMethodName = "obtainGoodsInfo", error = "获取商品信息失败")
    public String obtainGoodsInfo() {
        return null;
    }

    /**
     * 分页查询积分商城订单
     *
     * @return
     */
    @RequestMapping(value = "/listOrderInfos", method = RequestMethod.GET)
    @ResponseBody
    @ApiRestAction(modelClass = ListOrderInfosModel.class, serviceClass = IntegralMallService.class, serviceMethodName = "listOrderInfos", error = "查询订单列表失败")
    public String listOrderInfos() {
        return null;
    }

    /**
     * 获取积分商城订单明细
     *
     * @return
     */
    @RequestMapping(value = "/obtainOrderInfo", method = RequestMethod.GET)
    @ResponseBody
    @ApiRestAction(modelClass = ObtainOrderInfoModel.class, serviceClass = IntegralMallService.class, serviceMethodName = "obtainOrderInfo", error = "获取订单信息失败")
    public String obtainOrderInfo() {
        return null;
    }

    /**
     * 查询积分商城订单列表
     *
     * @return
     */
    @RequestMapping(value = "/listOrders", method = RequestMethod.GET)
    @ResponseBody
    @ApiRestAction(modelClass = ListOrdersModel.class, serviceClass = IntegralMallService.class, serviceMethodName = "listOrders", error = "查询订单列表失败")
    public String listOrders() {
        return null;
    }

    /**
     * 核销订单
     *
     * @return
     */
    @RequestMapping(value = "/verifyOrder", method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    @ApiRestAction(modelClass = VerifyOrderModel.class, serviceClass = IntegralMallService.class, serviceMethodName = "verifyOrder", error = "核销订单失败")
    public String verifyOrder() {
        return null;
    }

    @RequestMapping(value = "/settingGoodsList", method = RequestMethod.GET)
    @ResponseBody
    @ApiRestAction(modelClass = SettingGoodsModel.class, serviceClass = IntegralMallService.class, serviceMethodName = "settingGoodsList", error = "查询上架商品列表失败")
    public String settingGoodsList() {
        return null;
    }

    @RequestMapping(value = "/updateGoodsSetting", method = RequestMethod.POST)
    @ResponseBody
    @ApiRestAction(modelClass = SettingGoodsModel.class, serviceClass = IntegralMallService.class, serviceMethodName = "updateGoodsSetting", error = "修改上架商品设置失败")
    public String updateGoodsSetting() {
        return null;
    }
}
