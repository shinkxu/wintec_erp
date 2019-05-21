package erp.chain.controller.online;

import erp.chain.annotations.ApiRestAction;
import erp.chain.model.online.onlinegoods.*;
import erp.chain.service.online.OnlineGoodsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Created by liuyandong on 2018-04-11.
 */
@Controller
@RequestMapping(value = "/onlineGoods")
public class OnlineGoodsController {
    @Autowired
    private OnlineGoodsService onlineGoodsService;

    /**
     * 查询带有角标的商品
     *
     * @return
     */
    @RequestMapping(value = "/listCornerGoodsInfos", method = RequestMethod.GET)
    @ResponseBody
    @ApiRestAction(modelClass = ListCornerGoodsInfosModel.class, serviceClass = OnlineGoodsService.class, serviceMethodName = "listCornerGoodsInfos", error = "查询带有角标的商品信息失败")
    public String listCornerGoodsInfos() throws Exception {
        /*Map<String, String> requestParameters = ApplicationHandler.getRequestParameters();
        ListCornerGoodsInfosModel listCornerGoodsInfosModel = ApplicationHandler.instantiateObject(ListCornerGoodsInfosModel.class, requestParameters);
        listCornerGoodsInfosModel.validateAndThrow();

        return BeanUtils.toJsonStr(onlineGoodsService.listCornerGoodsInfos(listCornerGoodsInfosModel));*/
        return null;
    }

    /**
     * 查询商品列表
     *
     * @return
     */
    @RequestMapping(value = "/listGoodsInfos", method = RequestMethod.GET)
    @ResponseBody
    @ApiRestAction(modelClass = ListGoodsInfosModel.class, serviceClass = OnlineGoodsService.class, serviceMethodName = "listGoodsInfos", error = "查询商品列表失败")
    public String listGoodsInfos() throws Exception {
        /*Map<String, String> requestParameters = ApplicationHandler.getRequestParameters();
        ListGoodsInfosModel listGoodsInfosModel = ApplicationHandler.instantiateObject(ListGoodsInfosModel.class, requestParameters);
        listGoodsInfosModel.validateAndThrow();

        return BeanUtils.toJsonStr(onlineGoodsService.listGoodsInfos(listGoodsInfosModel));*/
        return null;
    }

    /**
     * 查询促销活动
     *
     * @return
     */
    @RequestMapping(value = "/listGoodsPromotions", method = RequestMethod.GET)
    @ResponseBody
    @ApiRestAction(modelClass = ListGoodsPromotionModel.class, serviceClass = OnlineGoodsService.class, serviceMethodName = "listGoodsPromotions", error = "查询促销活动失败")
    public String listGoodsPromotions() throws Exception {
        /*Map<String, String> requestParameters = ApplicationHandler.getRequestParameters();
        ListGoodsPromotionModel listGoodsPromotionModel = ApplicationHandler.instantiateObject(ListGoodsPromotionModel.class, requestParameters);
        listGoodsPromotionModel.validateAndThrow();
        return BeanUtils.toJsonStr(onlineGoodsService.listGoodsPromotions(listGoodsPromotionModel));*/
        return null;
    }

    /**
     * 查询当前生效的整单优惠活动
     *
     * @return
     */
    @RequestMapping(value = "/findEffectiveDietPromotionTotalReduce", method = RequestMethod.GET)
    @ResponseBody
    @ApiRestAction(modelClass = FindEffectiveDietPromotionTotalReduceModel.class, serviceClass = OnlineGoodsService.class, serviceMethodName = "findEffectiveDietPromotionTotalReduce", error = "查询当前生效的整单优惠活动失败")
    public String findEffectiveDietPromotionTotalReduce() throws Exception {
        /*Map<String, String> requestParameters = ApplicationHandler.getRequestParameters();
        FindEffectiveDietPromotionTotalReduceModel findEffectiveDietPromotionTotalReduceModel = ApplicationHandler.instantiateObject(FindEffectiveDietPromotionTotalReduceModel.class, requestParameters);
        findEffectiveDietPromotionTotalReduceModel.validateAndThrow();

        return BeanUtils.toJsonStr(onlineGoodsService.findEffectiveDietPromotionTotalReduce(findEffectiveDietPromotionTotalReduceModel));*/
        return null;
    }

    /**
     * 查询菜牌信息
     *
     * @return
     */
    @RequestMapping(value = "/listMenuInfos", method = RequestMethod.GET)
    @ResponseBody
    @ApiRestAction(modelClass = ListMenuInfosModel.class, serviceClass = OnlineGoodsService.class, serviceMethodName = "listMenuInfos", error = "查询菜牌信息失败")
    public String listMenuInfos() {
        return null;
    }

    @RequestMapping(value = "/obtainGoodsInfo", method = RequestMethod.GET)
    @ResponseBody
    @ApiRestAction(modelClass = ObtainGoodsInfoModel.class, serviceClass = OnlineGoodsService.class, serviceMethodName = "obtainGoodsInfo", error = "获取商品信息失败")
    public String obtainGoodsInfo() {
        return null;
    }

    @RequestMapping(value = "/listShoppingBags", method = RequestMethod.GET)
    @ResponseBody
    @ApiRestAction(modelClass = ListShoppingBagsModel.class, serviceClass = OnlineGoodsService.class, serviceMethodName = "listShoppingBags", error = "查询购物袋失败")
    public String listShoppingBags() {
        return null;
    }
}
