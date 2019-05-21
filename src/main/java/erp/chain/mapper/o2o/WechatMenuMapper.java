package erp.chain.mapper.o2o;

import erp.chain.domain.o2o.WxMenu;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigInteger;
import java.util.List;
import java.util.Map;

/**
 * 微信菜单
 * Created with IntelliJ IDEA.
 * User: AnCong
 * Date: 2016/12/28
 */
@Mapper
public interface WechatMenuMapper{
    List<Map> listWxMenu(@Param("tenantId")BigInteger tenantId,@Param("flag")String flag,@Param("parentId")BigInteger parentId,@Param("menuId")BigInteger menuId);
    List<WxMenu> getWxMenu(@Param("tenantId")BigInteger tenantId,@Param("flag")String flag,@Param("parentId")BigInteger parentId,@Param("menuId")BigInteger menuId);
    WxMenu getWxMenuById(@Param("tenantId")BigInteger tenantId,@Param("menuId")BigInteger menuId);
    int insert(WxMenu wxMenu);
    int update(WxMenu wxMenu);
    WxMenu getWxMenuByRank(@Param("tenantId")BigInteger tenantId,@Param("rank")BigInteger rank);
    List<Map> wxMenuItem();
    List<WxMenu> listWxMenuInfo(@Param("tenantId")BigInteger tenantId,@Param("grade")Integer grade);
    WxMenu findWxMenuByMediaId(@Param("tenantId")BigInteger tenantId,@Param("mediaId")String mediaId);
}
