package erp.chain.mapper.report;

import erp.chain.model.report.GiveAndReturnPagerModel;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

/**
 * 赠送退菜报表
 *
 * @author hefuzi 2016-11-29
 */
@Mapper
public interface GiveAndReturnMapper {

    List<Map<String,Object>> queryPager(GiveAndReturnPagerModel model);

    long queryCount(GiveAndReturnPagerModel model);
}