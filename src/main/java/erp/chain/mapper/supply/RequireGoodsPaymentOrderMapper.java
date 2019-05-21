package erp.chain.mapper.supply;

import erp.chain.domain.supply.RequireGoodsPaymentOrder;
import org.apache.ibatis.annotations.Mapper;

/**
 * Created by liuyandong on 2017/3/21.
 */
@Mapper
public interface RequireGoodsPaymentOrderMapper {
    int insert(RequireGoodsPaymentOrder requireGoodsPaymentOrder);
}
