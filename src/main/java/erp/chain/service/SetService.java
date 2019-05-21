package erp.chain.service;

import com.saas.common.util.MD5Utils;
import erp.chain.domain.Pos;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import saas.api.common.ApiRest;

import java.math.BigInteger;
import java.util.List;
import java.util.Map;

/**
 * Created by xumx on 2016/11/8.
 */
@Service
@Transactional(propagation = Propagation.REQUIRES_NEW, isolation = Isolation.READ_COMMITTED, rollbackFor = {java.lang.Exception.class}, timeout = 120)
public class SetService extends BaseService {

    public int resetPassword(BigInteger posId, String oldPass, String newPass, BigInteger userId) {
        try {
            if (posId == null || StringUtils.isEmpty(oldPass) || StringUtils.isEmpty(newPass) || userId == null) {
                log.warn("SetService.resetPassword({},{}) - 无效参数", posId, userId);
                return -5;
            }
            Pos cPos = findPos(posId);
            if (cPos == null) {
                log.warn("SetService.resetPassword({},{}) - 无效的Pos", posId, userId);
                return -2;
            }
            List<Map<String, Object>> list = select(String.format("select * from employee where tenant_id = %s and branch_id = %s and user_id = %s and is_deleted = false", cPos.getTenantId(), cPos.getBranchId(), userId));
            if (list != null && list.size() == 1) {
                Map<String, Object> e = list.get(0);
                if (e.get("password_for_local").equals(MD5Utils.stringMD5(oldPass).toLowerCase())) {
                    return update(String.format("update employee set password_for_local = '%s',last_update_at=NOW() where id = %s", MD5Utils.stringMD5(newPass).toLowerCase(), e.get("id")));
                } else {
                    log.warn("SetService.resetPassword({},{}) - 原始密码错误", posId, userId);
                    return -3;
                }
            } else {
                log.warn("SetService.resetPassword({},{}) - 员工查询异常", posId, userId);
                return -4;
            }
        } catch (Exception e) {
            log.warn("SetService.resetPassword({},{}) - {} - {}", posId, userId, e.getClass().getSimpleName(), e.getMessage());
            return -6;
        }
    }

    public int version(BigInteger posId, String name, String version) {
        if (StringUtils.isEmpty(name) || StringUtils.isEmpty(version)) {
            log.warn("SetService.version({},{},{}) - 无效参数", posId, name, version);
            return -2;
        }
        ApiRest r = new ApiRest();
        try {
            Pos pos = findPos(posId);
            if (pos == null) {
                log.warn("SetService.version({},{},{}) - 无效pos", posId, name, version);
                return -3;
            }
            return update(String.format("update pos set app_name = '%s', app_version = '%s',last_update_at=NOW() where id = %s", name, version, posId));
        } catch (Exception e) {
            log.error("SetService.version({},{},{}) - {} - {}", posId, name, version, e.getClass().getSimpleName(), e.getMessage());
            return -4;
        }
    }

}
