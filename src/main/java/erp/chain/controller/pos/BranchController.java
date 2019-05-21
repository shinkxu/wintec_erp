package erp.chain.controller.pos;

import erp.chain.controller.BaseController;
import erp.chain.domain.Pos;
import erp.chain.service.BranchService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import saas.api.common.ApiRest;

import java.math.BigInteger;

/**
 * Created by xumx on 2016/11/1.
 */
@Controller
public class BranchController extends BaseController {

    @Autowired
    BranchService branchService;

    @RequestMapping(value = "/api/branch")
    public @ResponseBody ApiRest index(String acn, BigInteger posId, String status) {
        ApiRest r = null;
        if (StringUtils.isEmpty(acn) || posId == null || StringUtils.isEmpty(status) || !StringUtils.isNumeric(status)) {
            r = ApiRest.INVALID_PARAMS_ERROR;
        } else if ("takeoutStatus".equals(acn.trim())) {
            r = takeoutStatus(posId, status);
        } else {
            r = ApiRest.INVALID_PARAMS_ERROR;
        }
        return r;
    }

    private ApiRest takeoutStatus(BigInteger posId, String status) {
        ApiRest r = new ApiRest();
        try {
            Pos pos = findPos(posId);
            boolean c = branchService.takeoutStatus(pos.getBranchId(), Integer.valueOf(status));
            if (c) {
                r.setIsSuccess(true);
                r.setMessage("外卖状态修改成功");
            } else {
                r.setIsSuccess(false);
                r.setMessage("外卖状态修改失败");
            }
        } catch (Exception e) {
            r.setIsSuccess(false);
            r.setMessage("设置机构外卖状态异常");
            r.setError(e.getClass().getSimpleName() + " - " + e.getMessage());
            log.error("BranchController.takeoutStatus({},{}) - {} - {}", posId, status, e.getClass().getSimpleName(), e.getMessage());
        }
        return r;
    }

}
