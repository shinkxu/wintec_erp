package erp.chain.mapper.system;

import erp.chain.domain.system.SmsUseHistory;
import erp.chain.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 短信
 * Created with IntelliJ IDEA.
 * User: AnCong
 * Date: 2017/6/22
 */
@Mapper
public interface SmsMapper extends BaseMapper{
    int saveSmsLog(SmsUseHistory smsUseHistory);
}
