package erp.chain.mapper;

import erp.chain.utils.SearchModel;
import erp.chain.utils.UpdateModel;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface UniversalMapper {
    long universalUpdate(UpdateModel updateModel);

    List<Map<String, Object>> executeQuery(Map<String, Object> parameters);

    long executeUpdate(Map<String, Object> parameters);

    Map<String, Object> executeUniqueResultQuery(Map<String, Object> parameters);

    long universalCount(Map<String, Object> parameters);

    long insert(Object domain);

    long insertAll(List<?> domains);

    long update(Object domain);

    Map<String, Object> find(SearchModel searchModel);

    List<Map<String, Object>> findAll(SearchModel searchModel);

    long count(SearchModel searchModel);

    List<Map<String, Object>> findAllPaged(SearchModel searchModel);
}
