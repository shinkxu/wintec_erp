package erp.chain.mapper;

import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

/**
 * Created by xumx on 2016/10/31.
 */
@Mapper
public interface CommonMapper {
    List<Map<String, Object>> select(String sql);
    int insert(String sql);
    int update(String sql);
    int delete(String sql);
    int updateInfoTenant(Map<String, String> params);
}
