package erp.chain.mapper.system;

import erp.chain.domain.OnlinePayInfo;
import erp.chain.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 功能模块说明
 * Created with IntelliJ IDEA.
 * User: AnCong
 * Date: 2018/1/10
 */
@Mapper
public interface OnlinePayInfoMapper extends BaseMapper{
    OnlinePayInfo findInfo(OnlinePayInfo onlinePayInfo);
    int insert(OnlinePayInfo onlinePayInfo);
    int update(OnlinePayInfo onlinePayInfo);
}
