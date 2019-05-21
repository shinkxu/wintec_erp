package erp.chain.service;

import erp.chain.domain.Employee;
import erp.chain.mapper.EmployeeMapper;
import erp.chain.utils.CommonUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import saas.api.common.ApiRest;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by xumx on 2016/11/8.
 */
@Service
@Transactional(propagation = Propagation.REQUIRES_NEW, isolation = Isolation.READ_COMMITTED, rollbackFor = {java.lang.Exception.class}, timeout = 120)
public class EmployeeService extends BaseService {
    @Autowired
    private EmployeeMapper employeeMapper;

    public Map find(BigInteger tenantId, BigInteger branchId, String employeeCode) {
        boolean b = false;
        StringBuilder hql = new StringBuilder("select * from employee where is_deleted = false ");
        if (tenantId != null) {
            hql.append(" and tenant_id = ").append(tenantId);
            b = true;
        }
        if (branchId != null) {
            hql.append(" and branch_id = ").append(branchId);
            b = true;
        }
        if (StringUtils.isNotEmpty(employeeCode)) {
            hql.append(" and code = '").append(employeeCode).append("' ");
            b = true;
        }
        if (b) {
            List<Map<String, Object>> result = select(hql.toString());
            if (result != null && !result.isEmpty()) {
                return CommonUtils.DomainMap(result.get(0));
            }
        }
        return null;
    }

    public int saveCard(BigInteger id, BigInteger cardId, String cardCode) {
        List<Map<String, Object>> employeeList = select(String.format("select * from employee where id = %s and isDeleted = false", id));
        if (employeeList == null || employeeList.isEmpty()) {
            return 0;
        } else if (employeeList.size() > 1) {
            log.warn("EmployeeService.saveCard({},{},{}) - 查询到的员工数量大于1", id, cardId, cardCode);
            return 0;
        }
        int r = update(String.format("update employee set card_id = %s, card_code = '%s' where id = %s", cardId, cardCode, id));
        if (r > 0) {
            return r;
        }
        return -1;
    }

    public ApiRest checkPhoneRole(Map params) {
        ApiRest apiRest = new ApiRest();
        //根据userId去employee表查询是否有该用户
        Map map = employeeMapper.queryEmployeeByTenantIdAndBranchIdAndUserId(params);
        if (map != null && !map.isEmpty()) {
            //如果有该用户查询该用户是否是管理员或店长
            List<Map> list = employeeMapper.queryRolesByCheckPhoneRoles(new BigInteger(map.get("userId").toString()));
            if (list != null && list.size() > 0) {
                apiRest.setMessage("校验成功");
                apiRest.setIsSuccess(true);
            } else {
                apiRest.setMessage("请输入管理员或店长手机号");
                apiRest.setIsSuccess(false);
            }
        } else {
            apiRest.setMessage("门店下没有该员工");
            apiRest.setIsSuccess(false);
        }

        return apiRest;
    }


}
