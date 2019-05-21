package erp.chain.mapper.o2o;

import erp.chain.domain.o2o.WxPosCode;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

/**
 * Created by wangms on 2017/5/23.
 */
@Mapper
public interface WxPosCodeMapper {
    List<WxPosCode> getList(Map params);
}
