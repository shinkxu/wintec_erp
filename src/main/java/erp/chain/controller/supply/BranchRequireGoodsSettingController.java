package erp.chain.controller.supply;

import erp.chain.domain.MapUnderscoreToCamelCase;
import erp.chain.model.supply.BranchRequireGoodsSettingModel;
import erp.chain.model.supply.QueryBranchRequireGoodsSettingModel;
import erp.chain.service.supply.BranchRequireGoodsSettingService;
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

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Map;

/**
 * 机构要货关系
 *
 * @author hefuzi 2016-12-15
 */
@Controller
@ResponseBody
@RequestMapping("/branchRequireGoodsSetting")
public class BranchRequireGoodsSettingController {
    private Log logger = LogFactory.getLog(getClass());
    @Autowired
    private BranchRequireGoodsSettingService branchRequireGoodsSettingService;

    /**
     * 分页查询-
     */
    @RequestMapping("/queryPager")
    public String queryPager() {
        ApiRest apiRest = new ApiRest();
        QueryBranchRequireGoodsSettingModel model = AppHandler.bindParam(QueryBranchRequireGoodsSettingModel.class);
        if (!model.validate()) {
            apiRest.setData(model.getErrors());
            return BeanUtils.toJsonStr(apiRest);
        }
        apiRest.setData(branchRequireGoodsSettingService.queryPager(model).toMap());
        apiRest.setIsSuccess(true);
        return BeanUtils.toJsonStr(apiRest);
    }

    /**
     * id查询-
     */
    @RequestMapping("/get")
    public String get(BigInteger id) {
        ApiRest apiRest = new ApiRest();
        try {
            MapUnderscoreToCamelCase branchRequireGoodsSetting = branchRequireGoodsSettingService.get(id);
            apiRest.setData(branchRequireGoodsSetting);
            apiRest.setIsSuccess(true);
        } catch (Exception e) {
            apiRest.setMessage(e.getMessage());
        }
        return BeanUtils.toJsonStr(apiRest);
    }

    /**
     * id删除-
     *
     * @throws OptimisticLockingFailureException [%s]数据版本过期或不存在
     */
    @RequestMapping("/delete")
    public String delete() {
        ApiRest apiRest = new ApiRest();
        Map params = AppHandler.params();
        try {
            branchRequireGoodsSettingService.delete(params);
            apiRest.setIsSuccess(true);
            apiRest.setMessage("删除成功");
        } catch (OptimisticLockingFailureException e) {
            apiRest.setMessage(e.getMessage());
        }
        apiRest.setIsSuccess(true);
        return BeanUtils.toJsonStr(apiRest);
    }

    /**
     * id更新-
     * <p>
     * 数据版本过期，数据[%s]不存在
     */
    @RequestMapping("/update")
    public String update(BigInteger id, BigInteger empId, BigInteger version, BigDecimal price) {
        ApiRest apiRest = new ApiRest();
        try {
            branchRequireGoodsSettingService.updatePrice(id, empId, version, price);
            apiRest.setIsSuccess(true);
            apiRest.setMessage("保存成功");
        } catch (ResourceNotExistException | OptimisticLockingFailureException e) {
            apiRest.setMessage(e.getMessage());
        }
        return BeanUtils.toJsonStr(apiRest);
    }

    /**
     * 保存-
     */
    @RequestMapping(value = "/save", method = RequestMethod.POST)
    public String save() {
        ApiRest apiRest = new ApiRest();
        try {
            BranchRequireGoodsSettingModel model = AppHandler.bind(BranchRequireGoodsSettingModel.class);
            if (!model.validate()) {
                apiRest.setData(model.getErrors());
                return BeanUtils.toJsonStr(apiRest);
            }
            apiRest=branchRequireGoodsSettingService.save(model);
        } catch (Exception e) {
            apiRest.setMessage(e.getMessage());
        }
        return BeanUtils.toJsonStr(apiRest);
    }
}