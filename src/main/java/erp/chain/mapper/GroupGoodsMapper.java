package erp.chain.mapper;


import erp.chain.domain.GroupGoods;
import erp.chain.domain.PackageGroup;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigInteger;
import java.util.List;
import java.util.Map;

@Mapper
public interface GroupGoodsMapper extends BaseMapper {
    List<Map> getGroupGoodsByGroupId(@Param("tenantId")BigInteger tenantId, @Param("groupId")BigInteger groupId);
    int deleteGoodsByGroupId(@Param("tenantId")BigInteger tenantId,@Param("groupId")BigInteger groupId);
    int deleteGroupByPackId(@Param("tenantId")BigInteger tenantId,@Param("packId")BigInteger packId);
    int insert(GroupGoods groupGoods);
    int update(GroupGoods groupGoods);
    List<PackageGroup> getGroupsByPackId(@Param("tenantId")BigInteger tenantId,@Param("packId")BigInteger packId);
    /*修改套餐/组合商品-商品明细数量*/
    int updateQuantity(Map params);
}