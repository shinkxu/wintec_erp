package erp.chain.controller.base;

import erp.chain.model.base.ProduceOrderModel;
import erp.chain.model.base.ProduceOrderUpdateModel;
import erp.chain.model.base.QueryGoodsProduceOrderModel;
import erp.chain.service.base.GoodsProduceOrderService;
import erp.chain.utils.AppHandler;
import erp.chain.utils.BeanUtils;
import erp.chain.utils.ResourceNotExistException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import saas.api.common.ApiRest;

import java.math.BigInteger;

/**
 * 商品加工单表
 *
 * @author hefuzi 2016-11-29
 */
@Controller
@ResponseBody
@RequestMapping("/goodsProduceOrder")
public class GoodsProduceOrderController {
    private Log logger = LogFactory.getLog(getClass());
    @Autowired
    private GoodsProduceOrderService goodsProduceOrderService;

    /**
     * 分页查询-商品加工单表
     */
    @RequestMapping("/queryPager")
    public String queryPager() {
        ApiRest apiRest = new ApiRest();
        QueryGoodsProduceOrderModel model = AppHandler.bindParam(QueryGoodsProduceOrderModel.class);
        if (!model.validate()) {
            apiRest.setData(model.getErrors());
            return BeanUtils.toJsonStr(apiRest);
        }
        apiRest.setData(goodsProduceOrderService.queryPager(model).toMap());
        apiRest.setIsSuccess(true);
        return BeanUtils.toJsonStr(apiRest);
    }

    /**
     * id查询-商品加工单信息
     */
    @RequestMapping("/getInfo")
    public String getInfo(BigInteger id) {
        ApiRest apiRest = new ApiRest();
        try {
            apiRest.setData(goodsProduceOrderService.getInfo(id));
            apiRest.setIsSuccess(true);
        } catch (Exception e) {
            apiRest.setMessage(e.getMessage());
        }
        return BeanUtils.toJsonStr(apiRest);
    }

    /**
     * id删除-商品加工单
     * <p>
     * [%s]数据版本过期或不存在
     */
    @RequestMapping("/delete")
    public String delete(BigInteger id, BigInteger empId, BigInteger version) {
        ApiRest apiRest = new ApiRest();
        try {
            goodsProduceOrderService.delete(id, empId, version);
            apiRest.setIsSuccess(true);
        } catch (OptimisticLockingFailureException | ResourceNotExistException e) {
            apiRest.setMessage(e.getMessage());
        }
        apiRest.setIsSuccess(true);
        return BeanUtils.toJsonStr(apiRest);
    }

    /**
     * 审核单据
     * <p>
     * [%s]数据版本过期或不存在
     */
    @RequestMapping("/audit")
    public String audit(BigInteger id, BigInteger empId, BigInteger version) {
        ApiRest apiRest = new ApiRest();
        try {
            goodsProduceOrderService.audit(id, empId, version);
            apiRest.setIsSuccess(true);
        } catch (OptimisticLockingFailureException | ResourceNotExistException e) {
            apiRest.setMessage(e.getMessage());
        }
        apiRest.setIsSuccess(true);
        return BeanUtils.toJsonStr(apiRest);
    }

    /**
     * id更新-商品加工单表
     * <p>
     * 数据版本过期，数据[%s]不存在
     */
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public String update() {
        ApiRest apiRest = new ApiRest();
        try {
            ProduceOrderUpdateModel model = AppHandler.bind(ProduceOrderUpdateModel.class);
            if (!model.validate()) {
                apiRest.setData(model.getErrors());
                return BeanUtils.toJsonStr(apiRest);
            }
            goodsProduceOrderService.update(model);
            apiRest.setIsSuccess(true);
        } catch (Exception e) {
            apiRest.setMessage(e.getMessage());
        }
        return BeanUtils.toJsonStr(apiRest);
    }

    /**
     * 保存-商品加工单表
     */
    @RequestMapping(value = "/save", method = RequestMethod.POST)
    public String save() {
        ApiRest apiRest = new ApiRest();
        try {
            ProduceOrderModel model = AppHandler.bind(ProduceOrderModel.class);
            if (!model.validate()) {
                apiRest.setData(model.getErrors());
                return BeanUtils.toJsonStr(apiRest);
            }
            BigInteger orderId = goodsProduceOrderService.save(model);
            apiRest.setIsSuccess(true);
            apiRest.setData(orderId);
        } catch (Exception e) {
            apiRest.setMessage(e.getMessage());
        }
        return BeanUtils.toJsonStr(apiRest);
    }
}