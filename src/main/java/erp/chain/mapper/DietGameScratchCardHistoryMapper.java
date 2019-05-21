package erp.chain.mapper;

import erp.chain.domain.DietGameScratchCardHistory;
import org.apache.ibatis.annotations.Mapper;

/**
 * Created by Administrator on 2017/5/22.
 */
@Mapper
public interface DietGameScratchCardHistoryMapper {
    int insert(DietGameScratchCardHistory dietGameScratchCardHistory);
}
