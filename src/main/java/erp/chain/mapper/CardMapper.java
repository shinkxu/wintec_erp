package erp.chain.mapper;

import erp.chain.domain.Card;
import erp.chain.domain.CardAccount;
import erp.chain.domain.CardBook;
import erp.chain.domain.CardType;
import erp.chain.domain.o2o.WxCard;
import erp.chain.domain.o2o.vo.CardAccountVo;
import erp.chain.domain.o2o.vo.CardTypeVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigInteger;
import java.util.List;
import java.util.Map;

/**
 * Created by xumx on 2016/11/15.
 */
@Mapper
public interface CardMapper extends BaseMapper {

    List<Card> select(Map<String, Object> map);
    List<Card> findCardByCode(Map<String, Object> map);
    List<Card> select1(Map<String, Object> map);
    int insertCard(Card card);
    int updateCard(Card card);
    Card findByCodeAndTenantId(Map<String, Object> map);
    int insertCardBook(CardBook cardBook);
    int insertCardType(CardType cardType);
    int updateCardType(CardType cardType);
    int cardIsUsed(Map params);
    List<CardType> listCardTypes(Map<String, Object> map);
    List<CardTypeVo> listCardTypesVo(Map<String, Object> map);
    CardType findCardTypeById(Map<String, Object> map);
    CardType findCardTypeById(@Param("id")BigInteger typeId,@Param("tenantId")BigInteger tenantId);
    int queryUsedTimes(Map params);
    int insertCardAccount(CardAccount cardAccount);
    List<CardAccount> listCardAccounts(Map<String, Object> map);
    List<CardAccountVo> listCardAccountsForBalance(Map<String, Object> map);
    int updateCardAccount(CardAccount cardAccount);
    List<CardBook> listCardBook(Map<String, Object> map);
    CardAccount findCardAccountById(Map<String, Object> map);
    int insertWxCard(WxCard wxCard);
    int updateWxCard(WxCard wxCard);
    WxCard findWxCard(Map<String, Object> map);
    List<Map> listCardBookVo(Map<String, Object> map);
    List<Map> listCardBookVoSum(Map<String, Object> map);
}
