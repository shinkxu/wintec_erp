package erp.chain.mapper.o2o;

import erp.chain.domain.o2o.WxMenuItem;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigInteger;

/**
 * 微信消息
 * Created with IntelliJ IDEA.
 * User: AnCong
 * Date: 2017/1/16
 */
@Mapper
public interface WechatMenuItemMapper{

    WxMenuItem getMenuItemById(@Param("itemId")BigInteger itemId);

}
