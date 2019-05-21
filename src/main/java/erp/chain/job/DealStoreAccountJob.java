package erp.chain.job;

import com.saas.common.util.LogUtil;
import com.saas.common.util.PartitionCacheUtils;
import com.saas.common.util.PropertyUtils;
import erp.chain.domain.supply.store.StoreCompute;
import erp.chain.service.supply.store.StoreComputeService;
import erp.chain.utils.ApplicationHandler;
import erp.chain.utils.BeanUtils;
import erp.chain.utils.LogUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 功能模块说明
 * Created with IntelliJ IDEA.
 * User: AnCong
 * Date: 2018/9/6
 */
@DisallowConcurrentExecution
public class DealStoreAccountJob implements Job{
    private String className = this.getClass().getName();
    private static final String LOCAL_HOST_ADDRESS = ApplicationHandler.obtainLocalHostAddress();
    @Autowired
    private StoreComputeService storeComputeService;
    private final Log logger = LogFactory.getLog(getClass());

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException{
        LogUtils.info(LOCAL_HOST_ADDRESS + "库存计算定时任务开始执行。");
        String json = "";
        String key="";
        String errorKey="";
        while (true) {
            try {
                key=PropertyUtils.getDefault("redis_topics");
                errorKey=PropertyUtils.getDefault("redis_topics")+"_error";
                json = PartitionCacheUtils.rpop(key);
                //没有库存单结束本次计算
                if (json == null || json.isEmpty()) {
                    break;
                }
                //LogUtil.logInfo("处理库存的数据"+json);
                StoreCompute compute = BeanUtils.toBean(json, StoreCompute.class);

                StoreCompute.executeStoreCompute(compute,storeComputeService,logger);

            } catch (Exception e) {
                LogUtils.error("库存计算定时任务执行失败", className, "execute", e);
                LogUtil.logInfo("计算库存错误信息"+e.getMessage());
                PartitionCacheUtils.lpush(errorKey,json);
                break;
            }
        }

    }
}
