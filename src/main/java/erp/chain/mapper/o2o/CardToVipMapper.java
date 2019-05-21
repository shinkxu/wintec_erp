package erp.chain.mapper.o2o;

import erp.chain.domain.o2o.CardToVip;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 *
 * Created by wangms on 2017/4/27.
 */
@Mapper
public interface CardToVipMapper{
    List<CardToVip> listCardToVip(Map params);
    int insert(CardToVip cardToVip);
    int update(CardToVip cardToVip);
    CardToVip getVipCardByCode(Map params);
    void insertList(@Param("cardToVips") List<CardToVip> cardToVips);
}
