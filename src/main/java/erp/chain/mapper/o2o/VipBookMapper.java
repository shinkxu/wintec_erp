package erp.chain.mapper.o2o;

import erp.chain.domain.o2o.VipBook;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigInteger;
import java.util.List;
import java.util.Map;

/**
 *
 * Created by wangms on 2017/1/10.
 */
@Mapper
public interface VipBookMapper {
    List<VipBook> vipBookList(Map params);
    int addVipBook(Map params);
    int insert(VipBook vipBook);
    int updateVipBook(Map params);
    int update(VipBook VipBook);
    VipBook findVipBookById(@Param("id")BigInteger id);
    VipBook findByCondition(Map params);
    VipBook findVipBookHistory(Map params);
}
