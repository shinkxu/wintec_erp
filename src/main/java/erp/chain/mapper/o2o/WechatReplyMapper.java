package erp.chain.mapper.o2o;

import erp.chain.domain.o2o.WxNewsArticle;
import erp.chain.domain.o2o.WxReplyKeyRule;
import erp.chain.domain.o2o.WxReplyMessage;
import erp.chain.domain.o2o.WxResourceMedia;
import erp.chain.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigInteger;
import java.util.List;
import java.util.Map;

/**
 * 微信消息
 * Created with IntelliJ IDEA.
 * User: AnCong
 * Date: 2017/1/18
 */
@Mapper
public interface WechatReplyMapper extends BaseMapper{
    List<WxResourceMedia> getList(Map params);
    List<Map> getMapList(Map params);
    List<WxReplyMessage> getWxReplyMessage(@Param("tenantId")BigInteger tenantId,@Param("messageType") BigInteger messageType);
    int replyMessageInsert(WxReplyMessage wxReplyMessage);
    int replyMessageUpdate(WxReplyMessage wxReplyMessage);
    WxResourceMedia findResourceMediaById(@Param("mediaId") String mediaId,@Param("tenantId")BigInteger tenantId);
    WxNewsArticle findNewsArticleById(@Param("id") BigInteger id, @Param("tenantId")BigInteger tenantId);
    WxNewsArticle findNewsArticleByMediaId(@Param("mediaId") String mediaId, @Param("tenantId")BigInteger tenantId);
    int newsArticleInsert(WxNewsArticle wxNewsArticle);
    int newsArticleUpdate(WxNewsArticle wxNewsArticle);
    List<WxNewsArticle> getNewArticle(@Param("tenantId") BigInteger tenantId,@Param("mediaId") String mediaId);
    int resourceMediaInsert(WxResourceMedia wxResourceMedia);
    int resourceMediaUpdate(WxResourceMedia wxResourceMedia);
    WxReplyKeyRule getReplyKeyRuleByRuleName(@Param("tenantId")BigInteger tenantId,@Param("ruleName")String ruleName);
    WxReplyKeyRule getReplyKeyRuleById(@Param("tenantId")BigInteger tenantId,@Param("id")BigInteger id);
    int replyKeyRuleInsert(WxReplyKeyRule wxReplyKeyRule);
    int replyKeyRuleUpdate(WxReplyKeyRule wxReplyKeyRule);
    WxReplyMessage getById(@Param("tenantId")BigInteger tenantId,@Param("id")BigInteger id);
    List<WxReplyKeyRule> getKeyRuleList(@Param("tenantId") BigInteger tenantId);
}
