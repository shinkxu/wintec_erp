package erp.chain.mapper;

import erp.chain.domain.Menu;
import erp.chain.domain.MenuGoods;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigInteger;
import java.util.List;
import java.util.Map;

/**
 * 菜牌
 * Created with IntelliJ IDEA.
 * User: AnCong
 * Date: 2016/11/24
 */
@Mapper
public interface MenuMapper extends BaseMapper{
    List<Map> queryMenu(Map params);
    Long queryMenuSum(Map params);
    Menu getMenuById(@Param("id")BigInteger id,@Param("tenantId")BigInteger tenantId);
    List<Map> getMenuGoods(@Param("menuId")BigInteger menuId,@Param("tenantId")BigInteger tenantId);
    List<Map> getMenuGoodsWithOrder(@Param("menuId")BigInteger menuId,
                                    @Param("tenantId")BigInteger tenantId,
                                    @Param("branchId")BigInteger branchId);
    List<Map> getMenuBranch(@Param("menuId")BigInteger menuId,@Param("tenantId")BigInteger tenantId);
    int insert(Menu menu);
    int update(Menu menu);


    int delMenuBranch(@Param("menuId")BigInteger menuId);
    int delMenuGoods(@Param("menuId")BigInteger menuId,@Param("tenantId")BigInteger tenantId);
    String getMenuCode(@Param("code")String code,@Param("tenantId")BigInteger tenantId);
    int insertMenuGoods(MenuGoods menuGoods);
    int insertBranchMenuR(Map params);


    List<Map> getMenuInfo(@Param("tenantId")String tenantId,@Param("branchId")String branchId,@Param("menuType")String menuType);
    List<Map> findMenuGoods(@Param("tenantId")String tenantId,@Param("goodsId")String goodsId);

    int menuStatusSetting(Menu menu);

    //导出电子秤商品
    List<Map> exportWeighGoods(Map params);
}
