package erp.chain.mapper.system;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigInteger;
import java.util.List;
import java.util.Map;

/**
 * 行政区域
 * Created with IntelliJ IDEA.
 * User: AnCong
 * Date: 2016/12/27
 */
@Mapper
public interface DistrictMapper{
    List<Map> qryDistrictByPid(@Param("pid")BigInteger pid);
    List<Map> qryAllDistrict();
    List<Map> findDistrictByIds(@Param("provinceId")String provinceId,@Param("cityId")String cityId,@Param("countyId")String countyId);
    Map findDistrictById(@Param("id")String id);
    List<Map> getSystemColor(@Param("typeId")Integer typeId);
}
