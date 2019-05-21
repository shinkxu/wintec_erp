package erp.chain.mapper;

import erp.chain.domain.GiveBackReason;
import org.apache.ibatis.annotations.Mapper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Mapper
public interface GiveBackReasonMapper extends BaseMapper {

    List<HashMap<String, Object>> queryGBReason(Map params);
    Long queryGBReasonCount(Map params);
    GiveBackReason queryGBReasonById(Map params);
    int insert(GiveBackReason params);
    int update(GiveBackReason params);
    String getMaxGBRCodeWs(Map params);
}