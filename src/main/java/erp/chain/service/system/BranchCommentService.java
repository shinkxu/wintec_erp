package erp.chain.service.system;

import erp.chain.domain.Branch;
import erp.chain.domain.system.BranchComment;
import erp.chain.mapper.BranchMapper;
import erp.chain.mapper.system.BranchCommentMapper;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import saas.api.common.ApiRest;

import java.math.BigInteger;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 店铺评价
 * Created with IntelliJ IDEA.
 * User: AnCong
 * Date: 2019/1/17
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class BranchCommentService{
    @Autowired
    private BranchCommentMapper branchCommentMapper;
    @Autowired
    private BranchMapper branchMapper;

    public ApiRest initBranchComment(Map<String,String> params){
        ApiRest apiRest=new ApiRest();
        BigInteger tenantId=BigInteger.valueOf(Long.valueOf(params.get("tenantId")));
        BigInteger branchId=BigInteger.valueOf(Long.valueOf(params.get("branchId")));
        Branch branch = branchMapper.findBranchByTenantIdAndBranchId(tenantId,branchId);
        if(branch==null){
            apiRest.setIsSuccess(false);
            apiRest.setError("未查询到店铺信息！");
            return apiRest;
        }
        Map scoreMap=branchCommentMapper.getBranchScore(branchId,tenantId);
        double resultScore=5;
        if(scoreMap!=null&&scoreMap.get("count")!=null&&!scoreMap.get("count").toString().equals("0")){
            Integer count=Integer.valueOf(scoreMap.get("count").toString());
            Integer sumScore=Integer.valueOf(scoreMap.get("sumScore").toString());
            resultScore=Math.ceil(sumScore.doubleValue()/count.doubleValue());
        }
        List<Map> commentList = new ArrayList<>();
        if(StringUtils.isNotEmpty(params.get("saleCode"))){
            commentList = branchCommentMapper.getBranchComment(params);
        }
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("branch", branch);
        resultMap.put("branchScore", resultScore);
        resultMap.put("comment",commentList.size()>0?commentList.get(0):new ArrayList<>());
        apiRest.setIsSuccess(true);
        apiRest.setMessage("查询成功！");
        apiRest.setData(resultMap);
        return apiRest;
    }

    public ApiRest getBranchComment(Map<String,String> params){
        ApiRest apiRest=new ApiRest();
        Integer page = 1;
        Integer rows = 20;
        if(StringUtils.isNotEmpty(params.get("page"))){
            page = Integer.valueOf(params.get("page"));
        }
        if(StringUtils.isNotEmpty(params.get("rows"))){
            rows = Integer.valueOf(params.get("rows"));
        }
        Integer offset = (page - 1) * rows;
        params.put("offset", offset.toString());
        if(params.get("noPage") != null && params.get("noPage").equals("1")){
            params.remove("page");
            params.remove("rows");
            params.remove("offset");
        }
        List<Map> commentList = branchCommentMapper.getBranchComment(params);
        long count = branchCommentMapper.countBranchComment(params);
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("rows", commentList);
        resultMap.put("total", count);
        apiRest.setIsSuccess(true);
        apiRest.setMessage("查询评价成功！");
        apiRest.setData(resultMap);
        return apiRest;
    }

    public ApiRest addOrUpdateComment(Map<String, String> params){
        ApiRest apiRest = new ApiRest();
        String tenantId = params.get("tenantId");
        String branchId = params.get("branchId");
        String customerName = params.get("customerName");
        String customerPhone = params.get("customerPhone");
        String customerSex = params.get("customerSex");
        String saleCode = params.get("saleCode")==null?"":params.get("saleCode");
        String customerScore = params.get("customerScore");
        String comment = params.get("comment");
        String extraComment = params.get("extraComment");
        if(StringUtils.isEmpty(params.get("commentId"))){//新增
            BranchComment branchComment = new BranchComment();
            branchComment.setTenantId(BigInteger.valueOf(Long.valueOf(tenantId)));
            branchComment.setBranchId(BigInteger.valueOf(Long.valueOf(branchId)));
            branchComment.setCreatedBy(BigInteger.ZERO);
            branchComment.setUpdatedBy(BigInteger.ZERO);
            branchComment.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
            branchComment.setCreatedAt(new Timestamp(System.currentTimeMillis()));
            branchComment.setCustomerName(customerName);
            branchComment.setCustomerPhone(customerPhone);
            branchComment.setComment(comment);
            branchComment.setCommentAt(new Timestamp(System.currentTimeMillis()));
            branchComment.setCustomerSex(Integer.valueOf(customerSex));
            branchComment.setSaleCode(saleCode);
            branchComment.setCustomerScore(Integer.valueOf(customerScore));
            branchComment.setState(0);
            branchCommentMapper.insertComment(branchComment);
            apiRest.setIsSuccess(true);
            apiRest.setMessage("新增评论成功！");
        }
        else{//修改
            String commentId = params.get("commentId");
            BranchComment branchComment = branchCommentMapper.getCommentById(commentId, tenantId);
            if(branchComment != null){
                if(StringUtils.isEmpty(extraComment)){
                    apiRest.setIsSuccess(false);
                    apiRest.setError("追加评论不能为空！");
                    return apiRest;
                }
                branchComment.setUpdatedBy(BigInteger.ZERO);
                branchComment.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
                branchComment.setExtraComment(extraComment);
                branchComment.setExtraCommentAt(new Timestamp(System.currentTimeMillis()));
                branchComment.setState(1);
                branchCommentMapper.updateComment(branchComment);
                apiRest.setIsSuccess(true);
                apiRest.setMessage("追加评论成功！");
            }
            else{
                apiRest.setIsSuccess(false);
                apiRest.setMessage("未查询到评价信息！");
            }
        }
        return apiRest;
    }
}
