package erp.chain.controller.online;

import erp.chain.annotations.ApiRestAction;
import erp.chain.model.online.weixinsale.ObtainPlanDetailModel;
import erp.chain.model.online.weixinsale.ObtainWeiXinSaleInfoModel;
import erp.chain.model.online.weixinsale.SaveOrderModel;
import erp.chain.model.online.weixinsale.SaveWeiXinSaleInfoModel;
import erp.chain.model.online.weixinsale.ObtainBranchInfoModel;
import erp.chain.model.online.weixinsale.ObtainWeiXinSaleOrderModel;
import erp.chain.service.online.WeiXinSaleService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Created by liuyandong on 2018-06-15.
 */
@Controller
@RequestMapping(value = "/weiXinSale")
public class WeiXinSaleController {
    /**
     * 获取微信售卡信息
     *
     * @return
     */
    @RequestMapping(value = "/obtainWeiXinSaleInfo", method = RequestMethod.GET)
    @ResponseBody
    @ApiRestAction(modelClass = ObtainWeiXinSaleInfoModel.class, serviceClass = WeiXinSaleService.class, serviceMethodName = "obtainWeiXinSaleInfo", error = "获取微信售卡信息失败")
    public String obtainWeiXinSaleInfo() {
        return null;
    }

    /**
     * 保存微信售卡信息
     *
     * @return
     */
    @RequestMapping(value = "/saveWeiXinSaleInfo", method = RequestMethod.POST)
    @ResponseBody
    @ApiRestAction(modelClass = SaveWeiXinSaleInfoModel.class, serviceClass = WeiXinSaleService.class, serviceMethodName = "saveWeiXinSaleInfo", error = "保存微信售卡信息失败")
    public String saveWeiXinSaleInfo() {
        return null;
    }

    /**
     * 获取方案详情
     *
     * @return
     */
    @RequestMapping(value = "/obtainPlanDetail", method = RequestMethod.GET)
    @ResponseBody
    @ApiRestAction(modelClass = ObtainPlanDetailModel.class, serviceClass = WeiXinSaleService.class, serviceMethodName = "obtainPlanDetail", error = "获取方案详情失败")
    public String obtainPlanDetail() {
        return null;
    }

    /**
     * 保存订单
     *
     * @return
     */
    @RequestMapping(value = "/saveOrder", method = RequestMethod.POST)
    @ResponseBody
    @ApiRestAction(modelClass = SaveOrderModel.class, serviceClass = WeiXinSaleService.class, serviceMethodName = "saveOrder", error = "保存订单失败")
    public String saveOrder() {
        return null;
    }

    /**
     * 获取订单
     *
     * @return
     */
    @RequestMapping(value = "/obtainWeiXinSaleOrder", method = RequestMethod.GET)
    @ResponseBody
    @ApiRestAction(modelClass = ObtainWeiXinSaleOrderModel.class, serviceClass = WeiXinSaleService.class, serviceMethodName = "obtainWeiXinSaleOrder", error = "获取订单失败")
    public String obtainWeiXinSaleOrder() {
        return null;
    }

    @RequestMapping(value = "/obtainBranchInfo", method = RequestMethod.GET)
    @ResponseBody
    @ApiRestAction(modelClass = ObtainBranchInfoModel.class, serviceClass = WeiXinSaleService.class, serviceMethodName = "obtainBranchInfo", error = "获取微餐厅门店数量失败")
    public String obtainBranchInfo() {
        return null;
    }
}
