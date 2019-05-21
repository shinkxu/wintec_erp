package erp.chain.controller.online;

import erp.chain.annotations.ApiRestAction;
import erp.chain.domain.Branch;
import erp.chain.model.online.onlinebranch.ListCtBranchesModel;
import erp.chain.model.online.onlinebranch.ObtainNearestBranchModel;
import erp.chain.service.online.OnlineBranchService;
import erp.chain.utils.ApplicationHandler;
import erp.chain.utils.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import saas.api.common.ApiRest;

import java.util.List;
import java.util.Map;

/**
 * Created by liuyandong on 2018-04-14.
 */
@Controller
@RequestMapping(value = "/onlineBranch")
public class OnlineBranchController {
    @Autowired
    private OnlineBranchService onlineBranchService;

    @RequestMapping(value = "/listCtBranches")
    @ResponseBody
    @ApiRestAction(error = "获取微餐厅机构列表失败")
    public String listCtBranches() throws Exception {
        Map<String, String> requestParameters = ApplicationHandler.getRequestParameters();
        ListCtBranchesModel listCtBranchesModel = ApplicationHandler.instantiateObject(ListCtBranchesModel.class, requestParameters);
        listCtBranchesModel.validateAndThrow();

        ApiRest apiRest = new ApiRest();
        apiRest.setData(onlineBranchService.listCtBranches(listCtBranchesModel.getTenantId(), listCtBranchesModel.getLongitude(), listCtBranchesModel.getLatitude()));
        apiRest.setMessage("获取微餐厅机构列表成功！");
        apiRest.setIsSuccess(true);
        return BeanUtils.toJsonStr(apiRest);
    }

    @RequestMapping(value = "/obtainNearestBranch")
    @ResponseBody
    @ApiRestAction(error = "获取最近机构失败")
    public String obtainNearestBranch() throws Exception {
        Map<String, String> requestParameters = ApplicationHandler.getRequestParameters();
        ObtainNearestBranchModel obtainNearestBranchModel = ApplicationHandler.instantiateObject(ObtainNearestBranchModel.class, requestParameters);
        obtainNearestBranchModel.validateAndThrow();

        List<Branch> branches = onlineBranchService.listCtBranches(obtainNearestBranchModel.getTenantId(), obtainNearestBranchModel.getLongitude(), obtainNearestBranchModel.getLatitude());
        ApiRest apiRest = new ApiRest();
        apiRest.setData(branches.get(0));
        apiRest.setMessage("获取最近机构成功！");
        apiRest.setIsSuccess(true);
        return BeanUtils.toJsonStr(apiRest);
    }
}
