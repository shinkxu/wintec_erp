package erp.chain.mapper.o2o;

import erp.chain.domain.o2o.MeituanGoodsToBranch;
import org.apache.ibatis.annotations.Mapper;

/**
 *
 * Created by wangms on 2017/1/10.
 */
@Mapper
public interface MeituanGoodsToBranchMapper {
    int insert(MeituanGoodsToBranch meituanGoodsToBranch);
}
