package erp.chain.mapper;

import erp.chain.domain.o2o.DietGameScratchVip;
import org.apache.ibatis.annotations.Mapper;

import java.util.Map;

/**
 *
 * Created by wangms on 2017/5/19.
 */
@Mapper
public interface DietGameScratchVipMapper {
    DietGameScratchVip findByCondition(Map params);
    int insert(DietGameScratchVip dietGameScratchVip);
    int update(DietGameScratchVip dietGameScratchVip);
}
