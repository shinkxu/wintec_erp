package erp.chain.mapper.system;

import erp.chain.domain.system.MessageNotification;
import erp.chain.domain.system.MessageReceipt;
import erp.chain.mapper.o2o.AlipayMarketingCardTemplateMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigInteger;
import java.util.List;
import java.util.Map;

/**
 * 功能模块说明
 * Created with IntelliJ IDEA.
 * User: AnCong
 * Date: 2018/3/15
 */
@Mapper
public interface MessageNotificationMapper extends AlipayMarketingCardTemplateMapper{

    List<Map> getMessageList(Map params);
    long countMessageList(Map params);
    Map messageById(@Param("tenantId")BigInteger tenantId,@Param("id")BigInteger id);
    MessageNotification getMessageById(@Param("tenantId")BigInteger tenantId,@Param("id")BigInteger id);
    int insert(MessageNotification messageNotification);
    int update(MessageNotification messageNotification);
    List<Map> getMessageBranch(@Param("tenantId")BigInteger tenantId,@Param("messageId")BigInteger messageId);
    void deletedSendBranches(@Param("tenantId")BigInteger tenantId,@Param("messageId")BigInteger messageId);
    void saveSendBranches(@Param("tenantId")BigInteger tenantId,@Param("messageId")BigInteger messageId,@Param("branchIds")List<String> branchIds);
    List<BigInteger> getSendBranches(@Param("tenantId")BigInteger tenantId,@Param("messageId")BigInteger messageId);
    int saveMessageReceipt(MessageReceipt messageReceipt);
    List<MessageReceipt> listMessageReceipt(Map params);
    long countMessageReceipt(Map params);
}
