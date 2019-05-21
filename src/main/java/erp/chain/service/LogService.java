package erp.chain.service;

import com.saas.common.Constants;
import com.saas.common.util.PropertyUtils;
import erp.chain.domain.Pos;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by xumx on 2016/11/8.
 */
@Service
@Transactional(propagation = Propagation.REQUIRES_NEW, isolation = Isolation.READ_COMMITTED, rollbackFor = {java.lang.Exception.class}, timeout = 120)
public class LogService extends BaseService {

    public Map<String, Object> console(BigInteger podId, MultipartFile log) {
        Map<String, Object> resultMap = new HashMap<>();
        try {
            if (log != null) {
                Pos pos = findPos(podId);
                String logPath = PropertyUtils.getDefault("pos.console.log.path");
                File logFile = new File(String.format("%s/%s/%s/%s", logPath, pos.getTenantId(), pos.getBranchId(), pos.getId()), log.getOriginalFilename());
                File path = logFile.getParentFile();
                if (!path.exists() || !path.isDirectory()) {
                    path.mkdirs();
                }
                log.transferTo(logFile);
                resultMap.put("Result", Constants.REST_RESULT_SUCCESS);
            } else {
                resultMap.put("Result",Constants.REST_RESULT_FAILURE);
                resultMap.put("Message", "无日志文件");
            }
        } catch (Exception e) {
            resultMap.put("Result",Constants.REST_RESULT_FAILURE);
            resultMap.put("Message", "上传文件出错");
            resultMap.put("Error", e.getMessage());
        }
        return resultMap;
    }

}
