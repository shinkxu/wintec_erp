package erp.chain.mapper;

import erp.chain.domain.CardRecord;
import org.apache.ibatis.annotations.Mapper;

/**
 * Created by xumx on 2016/11/15.
 */
@Mapper
public interface CardRecordMapper extends BaseMapper {
    int insertCardRecord(CardRecord cardRecord);

}
