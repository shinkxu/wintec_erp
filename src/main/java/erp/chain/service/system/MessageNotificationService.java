package erp.chain.service.system;

import com.alibaba.fastjson.JSONObject;
import com.saas.common.exception.ServiceException;
import com.saas.common.util.*;
import erp.chain.domain.system.MessageNotification;
import erp.chain.domain.system.MessageReceipt;
import erp.chain.mapper.system.MessageNotificationMapper;
import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Transaction;
import saas.api.common.ApiRest;

import java.io.IOException;
import java.math.BigInteger;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 功能模块说明
 * Created with IntelliJ IDEA.
 * User: AnCong
 * Date: 2018/3/15
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class MessageNotificationService{

    @Autowired
    private MessageNotificationMapper messageNotificationMapper;

    private ObjectMapper objectMapper = new ObjectMapper();
    protected Logger log = LoggerFactory.getLogger(getClass());

    private long redisLockTimeOut = 60000;

    public ApiRest getMessageList(Map params){
        ApiRest apiRest = new ApiRest();
        if(params.get("rows") != null && !params.get("rows").equals("") && params.get("page") != null && !params.get("page").equals("")){
            Integer rows = Integer.parseInt(params.get("rows").toString());
            Integer offset = (Integer.parseInt(params.get("page").toString()) - 1) * rows;
            params.put("rows", rows);
            params.put("offset", offset);
        }
        Map map = new HashMap();
        List<Map> messageList = messageNotificationMapper.getMessageList(params);
        Long count = messageNotificationMapper.countMessageList(params);
        map.put("total", count);
        map.put("rows", messageList);
        apiRest.setIsSuccess(true);
        apiRest.setData(map);
        apiRest.setMessage("查询消息通知列表成功！");
        return apiRest;
    }

    public ApiRest getMessageById(Map params){
        ApiRest apiRest = new ApiRest();
        BigInteger id = BigInteger.valueOf(Long.valueOf(params.get("id").toString()));
        BigInteger tenantId = BigInteger.valueOf(Long.valueOf(params.get("tenantId").toString()));
        Map messageNotification = messageNotificationMapper.messageById(tenantId, id);
        if(messageNotification == null){
            apiRest.setIsSuccess(false);
            apiRest.setError("查询消息通知失败！");
            apiRest.setMessage("查询消息通知失败！");
            return apiRest;
        }
        List<Map> branches = messageNotificationMapper.getMessageBranch(tenantId, id);
        Map result = new HashMap();
        result.put("message", messageNotification);
        result.put("branches", branches);
        apiRest.setIsSuccess(true);
        apiRest.setData(result);
        apiRest.setMessage("查询消息通知成功！");
        return apiRest;
    }

    public ApiRest saveOrUpdateMessage(Map params){
        ApiRest apiRest = new ApiRest();
        BigInteger tenantId = BigInteger.valueOf(Long.valueOf(params.get("tenantId").toString()));
        BigInteger createEmpId = BigInteger.valueOf(Long.valueOf(params.get("createEmpId").toString()));
        Integer type = Integer.valueOf(params.get("type").toString());
        Integer targetType = Integer.valueOf(params.get("targetType").toString());
        Integer isSendApos = Integer.valueOf(params.get("isSendApos").toString());
        Integer isSendWpos = Integer.valueOf(params.get("isSendWpos").toString());
        Integer isNewUser = Integer.valueOf(params.get("isNewUser").toString());
        Integer isOldUser = Integer.valueOf(params.get("isOldUser").toString());
        Integer displayType = Integer.valueOf(params.get("displayType").toString());
        String title = params.get("title").toString();
        String content = params.get("content").toString();
        String url = params.get("url") == null ? "" : params.get("url").toString();
        String opBy = params.get("opBy").toString();
        String branchIds = params.get("branchIds") == null ? "" : params.get("branchIds").toString();
        if(params.get("id") != null && !params.get("id").toString().equals("")){
            BigInteger id = BigInteger.valueOf(Long.valueOf(params.get("id").toString()));
            MessageNotification messageNotification = messageNotificationMapper.getMessageById(tenantId, id);
            if(messageNotification != null){
                if(messageNotification.getStatus() != 1){
                    apiRest.setIsSuccess(false);
                    apiRest.setError("只有未投递状态的通知允许修改！");
                    return apiRest;
                }
                messageNotification.setStatus(1);
                messageNotification.setType(type);
                messageNotification.setTargetType(targetType);
                messageNotification.setIsSendApos(isSendApos == 1);
                messageNotification.setIsSendWpos(isSendWpos == 1);
                messageNotification.setIsNewUser(isNewUser == 1);
                messageNotification.setIsOldUser(isOldUser == 1);
                messageNotification.setDisplayType(displayType);
                messageNotification.setTitle(title);
                messageNotification.setContent(content);
                messageNotification.setUrl(url);
                messageNotification.setLastUpdateAt(new Date());
                messageNotification.setLastUpdateBy(opBy);
                int i = messageNotificationMapper.update(messageNotification);
                if(i <= 0){
                    apiRest.setIsSuccess(false);
                    apiRest.setError("更新消息通知失败！");
                    return apiRest;
                }
                if(targetType == 2){
                    messageNotificationMapper.deletedSendBranches(tenantId, messageNotification.getId());
                    messageNotificationMapper.saveSendBranches(tenantId, messageNotification.getId(), Arrays.asList(branchIds.split(",")));
                }
                apiRest.setIsSuccess(true);
                apiRest.setMessage("更新消息通知成功！");
            }
            else{
                apiRest.setIsSuccess(false);
                apiRest.setError("查询消息通知失败！");
                return apiRest;
            }
        }
        else{
            MessageNotification messageNotification = new MessageNotification();
            messageNotification.setCreateEmpId(createEmpId);
            messageNotification.setStatus(1);
            messageNotification.setType(type);
            messageNotification.setTargetType(targetType);
            messageNotification.setIsSendApos(isSendApos == 1);
            messageNotification.setIsSendWpos(isSendWpos == 1);
            messageNotification.setIsNewUser(isNewUser == 1);
            messageNotification.setIsOldUser(isOldUser == 1);
            messageNotification.setDisplayType(displayType);
            messageNotification.setTitle(title);
            messageNotification.setContent(content);
            messageNotification.setUrl(url);
            messageNotification.setCreateAt(new Date());
            messageNotification.setCreateBy(opBy);
            messageNotification.setLastUpdateAt(new Date());
            messageNotification.setLastUpdateBy(opBy);
            messageNotification.setTenantId(tenantId);
            int i = messageNotificationMapper.insert(messageNotification);
            if(i <= 0){
                apiRest.setIsSuccess(false);
                apiRest.setError("新增消息通知失败！");
                return apiRest;
            }
            if(targetType == 2){
                messageNotificationMapper.saveSendBranches(tenantId, messageNotification.getId(), Arrays.asList(branchIds.split(",")));
            }
            apiRest.setIsSuccess(true);
            apiRest.setMessage("新增消息通知成功！");
        }
        return apiRest;
    }

    public ApiRest deleteMessage(Map params){
        ApiRest apiRest = new ApiRest();
        String opBy = params.get("opBy") == null ? "" : params.get("opBy").toString();
        BigInteger id = BigInteger.valueOf(Long.valueOf(params.get("id").toString()));
        BigInteger tenantId = BigInteger.valueOf(Long.valueOf(params.get("tenantId").toString()));
        MessageNotification messageNotification = messageNotificationMapper.getMessageById(tenantId, id);
        if(messageNotification == null){
            apiRest.setIsSuccess(false);
            apiRest.setError("未找到相关消息通知！");
            return apiRest;
        }
        if(messageNotification.getStatus() != 1){
            apiRest.setIsSuccess(false);
            apiRest.setError("只有未投递状态的通知允许删除！");
            return apiRest;
        }
        messageNotificationMapper.deletedSendBranches(tenantId, messageNotification.getId());
        messageNotification.setIsDeleted(true);
        messageNotification.setLastUpdateBy(opBy);
        messageNotification.setLastUpdateAt(new Date());
        int i = messageNotificationMapper.update(messageNotification);
        if(i <= 0){
            apiRest.setIsSuccess(false);
            apiRest.setError("删除消息通知失败！");
            return apiRest;
        }
        apiRest.setIsSuccess(true);
        apiRest.setData(messageNotification);
        apiRest.setMessage("删除消息通知成功！");
        return apiRest;
    }


    /**
     * 发送消息
     *
     * @param params
     * @return
     */
    public ApiRest sendMessageNotification(Map params){
        ApiRest apiRest = new ApiRest();
        String opBy = params.get("opBy") == null ? "" : params.get("opBy").toString();
        BigInteger id = BigInteger.valueOf(Long.valueOf(params.get("id").toString()));
        BigInteger tenantId = BigInteger.valueOf(Long.valueOf(params.get("tenantId").toString()));
        MessageNotification messageNotification = messageNotificationMapper.getMessageById(tenantId, id);
        if(messageNotification == null){
            apiRest.setIsSuccess(false);
            apiRest.setError("未找到相关消息通知！");
            return apiRest;
        }
        ApiRest sendMessageNotificationRest = handleMessageNotification(messageNotification, "00");
        // 投递成功，将状态改为投递成功，投递失败，将状态改为投递失败
        if(sendMessageNotificationRest.getIsSuccess()){
            messageNotification.setSendTime(new Date());
            messageNotification.setStatus(2);
            messageNotification.setLastUpdateAt(new Date());
            messageNotification.setLastUpdateBy(opBy);
            apiRest.setIsSuccess(true);
            apiRest.setMessage("通知发送成功！");
        }
        else{
            messageNotification.setStatus(3);
            apiRest.setIsSuccess(false);
            apiRest.setError("通知发送失败：" + sendMessageNotificationRest.getError());
        }
        int i = messageNotificationMapper.update(messageNotification);
        if(i <= 0){
            apiRest.setIsSuccess(false);
            apiRest.setError("更新消息状态失败！");
        }
        return apiRest;
    }

    /**
     * 重新投递消息
     *
     * @return
     */
    public ApiRest resendMessageNotification(Map params){
        ApiRest apiRest = new ApiRest();
        String opBy = params.get("opBy") == null ? "" : params.get("opBy").toString();
        BigInteger id = BigInteger.valueOf(Long.valueOf(params.get("id").toString()));
        BigInteger tenantId = BigInteger.valueOf(Long.valueOf(params.get("tenantId").toString()));
        MessageNotification messageNotification = messageNotificationMapper.getMessageById(tenantId, id);
        if(messageNotification == null){
            apiRest.setIsSuccess(false);
            apiRest.setError("未找到相关消息通知！");
            return apiRest;
        }
        if(messageNotification.getStatus() != 3){
            apiRest.setIsSuccess(false);
            apiRest.setError("只有投递失败的通知才可以进行重新投递操作！");
            return apiRest;
        }
        ApiRest resendMessageNotificationRest = handleMessageNotification(messageNotification, "01");
        // 如果重新投递成功，将状态改为投递成功，如果重新投递失败，状态依然是投递失败
        if(resendMessageNotificationRest.getIsSuccess()){
            messageNotification.setSendTime(new Date());
            messageNotification.setStatus(2);
            messageNotification.setLastUpdateAt(new Date());
            messageNotification.setLastUpdateBy(opBy);
            int i = messageNotificationMapper.update(messageNotification);
            if(i <= 0){
                apiRest.setIsSuccess(false);
                apiRest.setError("更新消息失败！");
                return apiRest;
            }
            apiRest.setIsSuccess(true);
            apiRest.setMessage("重新投递消息成功！");
        }
        else{
            apiRest.setIsSuccess(false);
            apiRest.setError("重新投递失败：" + resendMessageNotificationRest.getError());
        }
        return apiRest;
    }

    public ApiRest listMessageReceipt(Map params){
        ApiRest apiRest = new ApiRest();
        if(params.get("rows") != null && !params.get("rows").equals("") && params.get("page") != null && !params.get("page").equals("")){
            Integer rows = Integer.parseInt(params.get("rows").toString());
            Integer offset = (Integer.parseInt(params.get("page").toString()) - 1) * rows;
            params.put("rows", rows);
            params.put("offset", offset);
        }
        Map map = new HashMap();
        List<MessageReceipt> receiptList = messageNotificationMapper.listMessageReceipt(params);
        Long count = messageNotificationMapper.countMessageReceipt(params);
        map.put("total", count);
        map.put("rows", receiptList);
        apiRest.setIsSuccess(true);
        apiRest.setData(map);
        apiRest.setMessage("查询消息通知回执列表成功！");
        return apiRest;
    }

    /**
     * 撤回消息
     *
     * @param
     * @return
     */
    public ApiRest withdrawMessageNotification(Map params){
        ApiRest apiRest = new ApiRest();
        String opBy = params.get("opBy") == null ? "" : params.get("opBy").toString();
        BigInteger id = BigInteger.valueOf(Long.valueOf(params.get("id").toString()));
        BigInteger tenantId = BigInteger.valueOf(Long.valueOf(params.get("tenantId").toString()));
        MessageNotification messageNotification = messageNotificationMapper.getMessageById(tenantId, id);
        if(messageNotification == null){
            apiRest.setIsSuccess(false);
            apiRest.setError("未找到相关消息通知！");
            return apiRest;
        }
        if(messageNotification.getStatus() != 2){
            apiRest.setIsSuccess(false);
            apiRest.setError("只有投递成功的通知才可以进行撤回操作！");
            return apiRest;
        }
        // 发送撤回通知
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("code", "04");
        jsonObject.put("id", messageNotification.getId().toString());
        onMessage(jsonObject.toString(), tenantId);
        messageNotification.setStatus(4);
        messageNotification.setLastUpdateBy(opBy);
        messageNotification.setLastUpdateAt(new Date());
        int i = messageNotificationMapper.update(messageNotification);
        if(i <= 0){
            apiRest.setIsSuccess(false);
            apiRest.setError("更新消息失败！");
            return apiRest;
        }
        apiRest.setIsSuccess(true);
        apiRest.setMessage("撤回通知成功！");
        return apiRest;
    }

    /**
     * 负责消息的处理工作，包括发送，重新投递
     */
    private ApiRest handleMessageNotification(MessageNotification messageNotification, String messageCode){
        ApiRest apiRest = new ApiRest();
        try{
            String branchId = null;
            String posType = null;
            if(messageNotification.getTargetType() == 1){
                branchId = "";
            }
            else if(messageNotification.getTargetType() == 2){
                List<BigInteger> branchIds = messageNotificationMapper.getSendBranches(messageNotification.getTenantId(), messageNotification.getId());
                branchId = StringUtils.join(branchIds, ",");
            }
            if(messageNotification.getIsSendApos() && messageNotification.getIsSendWpos()){
                posType = "";
            }
            else if(messageNotification.getIsSendApos()){
                posType = "apos";
            }
            else if(messageNotification.getIsSendWpos()){
                posType = "wpos";
            }
            SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            JSONObject message = new JSONObject();
            message.put("id", messageNotification.getId().toString());
            message.put("title", messageNotification.getTitle());
            message.put("content", messageNotification.getContent());
            message.put("url", messageNotification.getUrl());
            message.put("send_date", sf.format(new Date()));
            message.put("branch_id", branchId);
            message.put("pos_type", posType);
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("code", messageCode);
            jsonObject.put("message", message);

            onMessage(jsonObject.toString(), messageNotification.getTenantId());
            apiRest.setIsSuccess(true);
            apiRest.setMessage("");
        }
        catch(Exception e){
            apiRest.setIsSuccess(false);
            apiRest.setError(e.getMessage());
        }
        return apiRest;
    }

    private void onMessage(String message, BigInteger tenantId){
        try{
            Map jsonMap = objectMapper.readValue(message, Map.class);
            if("00".equals(jsonMap.get("code"))){//一般消息
                Map messageMap = (Map)jsonMap.get("message");
                if(messageMap == null || messageMap.isEmpty()){
                    throw new Exception("Message消息主体为空");
                }

                String messageId = (String)messageMap.get("id");
                if(StringUtils.isEmpty(messageId)){
                    throw new Exception("Message ID 为空");
                }

                String branchId = (String)messageMap.get("branch_id");
                if(StringUtils.isBlank(branchId)){
                    branchId = ",";
                }
                else{
                    branchId = "," + branchId + ",";
                }

                String expiry = (String)messageMap.get("expiry");
                if(StringUtils.isEmpty(expiry)){
                    expiry = "1440";
                }

                String key = PropertyUtils.getDefault("partition.code") + "_" + PropertyUtils.getDefault("erp.message.queue") + tenantId + "_" + branchId + "_" + messageId;

                Map<String, String> dataMap = new HashMap<>();
                dataMap.put("data", message);
                if(StringUtils.isNotBlank((String)messageMap.get("pos_type"))){
                    dataMap.put("posType", messageMap.get("pos_type").toString());
                }

                PartitionCacheUtils.hmsetExpire(key, dataMap, Integer.valueOf(expiry) * 60);

            }
            else if("01".equals(jsonMap.get("code"))){//重新投递消息
                String messageId = (String)jsonMap.get("id");
                if(StringUtils.isEmpty(messageId)){
                    throw new Exception("Message ID 为空");
                }
                String key = PropertyUtils.getDefault("partition.code") + "_" + PropertyUtils.getDefault("erp.message.queue") + tenantId + "_" + "*_" + messageId;
                Set<String> keys = PartitionCacheUtils.keys(key);
                if(keys != null && !keys.isEmpty()){

                    Jedis jedis = null;
                    try{
                        jedis = PartitionCacheUtils.getJedis();
                        for(String k : keys){
                            long start = System.nanoTime();
                            boolean isSent = false;
                            while(redisLockTimeOut > 0 && (System.nanoTime() - start) / 1000000 < redisLockTimeOut){
                                jedis.watch(k);
                                Transaction transaction = jedis.multi();
                                transaction.hdel(k, "sent");
                                transaction.expire(key, 1440 * 60);
                                if(transaction.exec() != null){
                                    jedis.unwatch();
                                    isSent = true;
                                    break;
                                }
                                jedis.unwatch();
                                Thread.sleep(10);
                            }
                            if(!isSent){
                                log.warn("Pos 消息重发失败 - {} - {}", messageId, k);
                            }
                        }

                    }
                    catch(Exception e){
                        log.error("Pos 消息重发异常 - {} - {} - {}", messageId, e.getClass().getSimpleName(), e.getMessage());
                    }
                    finally{
                        PartitionCacheUtils.returnResource(jedis);
                    }

                }
            }
            else if("04".equals(jsonMap.get("code"))){//撤回消息
                String messageId = (String)jsonMap.get("id");
                if(StringUtils.isEmpty(messageId)){
                    throw new Exception("Message ID 为空");
                }
                String key = PropertyUtils.getDefault("partition.code") + "_" + PropertyUtils.getDefault("erp.message.queue") + tenantId + "_" + "*_" + messageId;
                Set<String> keys = PartitionCacheUtils.keys(key);
                if(keys != null && !keys.isEmpty()){
                    for(String k : keys){
                        PartitionCacheUtils.del(k);
                        Map<String, String> dataMap = new HashMap<>();
                        dataMap.put("data", message);
                        PartitionCacheUtils.hmsetExpire(k, dataMap, 1440 * 60);
                    }
                }

            }
            else{
                log.warn("未知的Pos消息 - {}", message);
            }
        }
        catch(Exception e){
            log.error("Pos消息处理异常 - {} - {} - {}", message, e.getClass().getSimpleName(), e.getMessage());
        }
    }

    /**
     * 处理消息回执队列
     *
     * @param
     * @return
     */
    public ApiRest dealMessageReceipt() throws IOException{
        ApiRest rest = new ApiRest();
            LogUtil.logInfo("***********处理消息通知回执任务开始************");
            String key=PropertyUtils.getDefault("partition.code") + "_" + PropertyUtils.getDefault("topic.message.erp");
            String errorKey=PropertyUtils.getDefault("partition.code") + "_" + PropertyUtils.getDefault("topic.message.erp")+"_error";
            while(true){
                String message = PartitionCacheUtils.rpop(errorKey);
                if(message != null && !"".equals(message)){
                    try{
                        MessageReceipt messageReceipt = buildMessageReceipt(message);
                        int i = messageNotificationMapper.saveMessageReceipt(messageReceipt);
                        if(i <= 0){
                            LogUtil.logInfo("保存消息回执失败！");
                        }
                        rest.setIsSuccess(true);
                        rest.setMessage("回执消息保存成功！");
                    }
                    catch(Exception e){
                        LogUtil.logError("消息通知回执任务处理异常:" + e.getMessage());
                        //处理异常数据
                        PartitionCacheUtils.lpush(key, message);
                    }
                }
                else{
                    rest.setIsSuccess(true);
                    rest.setMessage("消息通知回执任务:回执任务队列为空");
                    LogUtil.logInfo("***********消息通知回执任务:回执任务队列为空************");
                    //队列为空则结束
                    break;
                }
            }
        while(true){
            String message = PartitionCacheUtils.rpop(key);
            if(message != null && !"".equals(message)){
                try{
                    MessageReceipt messageReceipt = buildMessageReceipt(message);
                    int i = messageNotificationMapper.saveMessageReceipt(messageReceipt);
                    if(i <= 0){
                        LogUtil.logInfo("保存消息回执失败！");
                    }
                    rest.setIsSuccess(true);
                    rest.setMessage("回执消息保存成功！");
                }
                catch(Exception e){
                    LogUtil.logError("消息通知回执任务处理异常:" + e.getMessage());
                    //处理异常数据
                    PartitionCacheUtils.lpush(errorKey, message);
                }
            }
            else{
                rest.setIsSuccess(true);
                rest.setMessage("消息通知回执任务:回执任务队列为空");
                LogUtil.logInfo("***********消息通知回执任务:回执任务队列为空************");
                //队列为空则结束
                break;
            }
        }
        return rest;
    }

    private MessageReceipt buildMessageReceipt(String message) throws ParseException{
        net.sf.json.JSONObject jsonObject = net.sf.json.JSONObject.fromObject(message);
        String messageCode = (String)jsonObject.get("code");
        Object messageContentJson = jsonObject.get("message");
        net.sf.json.JSONObject messageContent = net.sf.json.JSONObject.fromObject(messageContentJson);

        String messageNotificationId = messageContent.getString("id");

        String employeeId = messageContent.getString("employeeId");
        String employeeCode = messageContent.getString("employeeCode");
        String employeeName = messageContent.getString("employeeName");

        String branchId = messageContent.getString("branchId");
        String branchCode = messageContent.getString("branchCode");
        String branchName = messageContent.getString("branchName");

        String tenantId = messageContent.getString("tenantId");
        String tenantCode = messageContent.getString("tenantCode");
        String tenantName = messageContent.getString("tenantName");

        String updateTime = messageContent.getString("updateTime");

        MessageReceipt messageReceipt = new MessageReceipt();

        messageReceipt.setMessageNotificationId(BigInteger.valueOf(Long.valueOf(messageNotificationId)));

        messageReceipt.setEmployeeId(BigInteger.valueOf(Long.valueOf(employeeId)));
        messageReceipt.setEmployeeCode(employeeCode);
        messageReceipt.setEmployeeName(employeeName);

        messageReceipt.setBranchId(BigInteger.valueOf(Long.valueOf(branchId)));
        messageReceipt.setBranchCode(branchCode);
        messageReceipt.setBranchName(branchName);

        messageReceipt.setTenantId(BigInteger.valueOf(Long.valueOf(tenantId)));
        messageReceipt.setTenantCode(tenantCode);
        messageReceipt.setTenantName(tenantName);

        if("02".equals(messageCode)){
            messageReceipt.setStatus(1);
        }
        else if("03".equals(messageCode)){
            messageReceipt.setStatus(2);
        }
        SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        messageReceipt.setUpdateTime(sf.parse(updateTime));
        messageReceipt.setCreateBy("System");
        messageReceipt.setCreateAt(new Date());
        messageReceipt.setLastUpdateBy("System");
        messageReceipt.setLastUpdateAt(new Date());

        return messageReceipt;
    }

}
