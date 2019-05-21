package erp.chain.service.o2o;

import com.saas.common.ResultJSON;
import com.saas.common.exception.ServiceException;
import com.saas.common.util.LogUtil;
import erp.chain.domain.o2o.VipTradeHistory;
import erp.chain.mapper.o2o.VipTradeHistoryMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by wangms on 2017/1/16.
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class VipTradeHistoryService{
    @Autowired
    private VipTradeHistoryMapper vipTradeHistoryMapper;

    /**
     * 分页查询
     *
     * @param params
     * @return
     * @throws ServiceException
     */
    public ResultJSON vipTradeList(Map params) throws ServiceException{
        ResultJSON result = new ResultJSON();
        Map<String, Object> map = new HashMap<>();

        Map<String, Object> queryParams = new HashMap<>();
        if(params.get("page") != null && params.get("rows") != null){
            queryParams.put("rows", params.get("rows"));
            queryParams.put("offset", (Integer.parseInt(params.get("page").toString()) - 1) * Integer.parseInt(params.get("rows").toString()));
        }
        queryParams.put("vipId", params.get("vipId"));
        if(params.get("branchId") != null){
            queryParams.put("tradeBranchId", params.get("branchId"));
        }

        List<VipTradeHistory> list = vipTradeHistoryMapper.vipTradeList(queryParams);
        int count = list.size();
        map.put("total", count > 0 ? count : 0);
        map.put("rows", list);
        result.setJsonMap(map);

        return result;
    }

//    /**
//     * 根据会员id查询会员消费历史
//     * @author szq
//     */
//    public Map<String, Object> listVipTradeHistory(Map params) {
//        Map<String, Object> map = new HashMap<String, Object>();
//        try {
//            StringBuffer query = new StringBuffer("from VipTradeHistory vsh where 1=1 ");
//            StringBuffer queryCount = new StringBuffer("select count(*) from VipTradeHistory vsh where 1=1 ");
//            def queryParams = new HashMap();
//            queryParams.max = params.rows;
//            queryParams.offset = (Integer.parseInt(params.page) - 1) * Integer.parseInt(params.rows);
//            def nameParams = new HashMap();
//
//            params.each { k, v ->
//                if ('vipId'.equals(k) && v) {
//                    query.append(" AND vsh.vipId = :vipId ")
//                    queryCount.append(" AND vsh.vipId = :vipId ")
//                    nameParams.vipId = v.asType(BigInteger)
//                }
//            }
//
//            def list = VipTradeHistory.executeQuery(query.toString(), nameParams, queryParams);
//            def count = VipTradeHistory.executeQuery(queryCount.toString(), nameParams);
//            map.put("total", count.size() > 0 ? count[0] : 0);
//            map.put("rows", list);
//        } catch (Exception e) {
//            LogUtil.logError("查询会员消费历史异常" + e.message);
//            throw new ServiceException();
//        }
//        return map;
//    }
//
//    public Map<String, Object> queryVipTradeHistory(Map params) {
//        Map<String, Object> map = new HashMap<String, Object>();
//        try {
//            BigInteger tenantId = new BigInteger(params.tenantId);
//            String query = "select v.id vipId,v.tenant_id tenantId,v.original_id originalId,v.type_id typeId,v.vip_code vipCode,v.vip_name vipName," +
//                    "v.sex sex,v.birthday birthday,v.phone phone,v.email email,v.memo memo,v.status status,v.buy_times buyTimes,v.sum_consume sumConsume," +
//                    "v.sum_feed sumFeed,v.sum_score sumScore,v.remaining_score reaminingScore,v.largessscore largessscore,v.overscore overscore," +
//                    "v.vip_store_total vipStoreTotal,v.vip_store vipStore,v.reg_date regDate,v.reg_source regSource,vth.id vipTradeHistoryId,vth.trade_no tradeNo," +
//                    "vth.trade_type tradeType,vth.trade_amount tradeAmount,vth.use_integral useIntegral,vth.integral_amount integralAmount,vth.use_coupon_code useCouponCode," +
//                    "vth.use_coupon_amount useCouponAmount,vth.pay_amount payAmount,vth.trade_branch_id tradeBranchId,vth.trade_branch_name tradeBranchName " +
//                    "from vip v right join vip_trade_history vth on v.id=vth.vip_id where v.tenant_id=${tenantId}";
//            String queryCount = "select count(*) from vip v right join vip_trade_history vth on v.id=vth.vip_id where v.tenant_id=${tenantId}";
//            Integer max = params['rows']?.asType(int);
//            Integer page = params['page']?.asType(int);
//            def sq = getSession().createSQLQuery(query);
//            def cq = getSession().createSQLQuery(queryCount);
//            if (max != null && page != null) {//分页查询
//                sq.setMaxResults(max)
//                sq.setFirstResult((page - 1) * max)
//            }
//            def countNum = cq.list()[0]
//            def list = [];
//            if (countNum > 0) {
//                list = sq.list()
//            }
//
//            map = ["total": countNum, "rows": list]
//        } catch (Exception e) {
//            LogUtil.logError("会员消费历史查询异常" + e.message);
//            throw new ServiceException();
//        }
//        return map
//    }
//
//    public Map<String, Object> findVipTradeHistory(Map params) {
//        Map<String, Object> map = new HashMap<String, Object>();
//        List<VipTradeHistoryVo> vipTradeHistoryVoArrayList = new ArrayList<VipTradeHistoryVo>();
//        String tenantId = params.tenantId;
//        try {
//            StringBuffer query = new StringBuffer("select v.id vipId,v.tenant_id tenantId,v.original_id originalId,v.type_id typeId,v.vip_code vipCode,v.vip_name vipName," +
//                    "v.sex sex,v.birthday birthday,v.phone phone,v.email email,v.memo memo,v.status status,v.buy_times buyTimes,v.sum_consume sumConsume," +
//                    "v.sum_feed sumFeed,v.sum_score sumScore,v.remaining_score reaminingScore,v.largessscore largessscore,v.overscore overscore," +
//                    "v.vip_store_total vipStoreTotal,v.vip_store vipStore,v.reg_date regDate,v.reg_source regSource,vth.id vipTradeHistoryId,vth.trade_no tradeNo," +
//                    "vth.trade_type tradeType,vth.trade_amount tradeAmount,vth.use_integral useIntegral,vth.integral_amount integralAmount,vth.use_coupon_code useCouponCode," +
//                    "vth.use_coupon_amount useCouponAmount,vth.pay_amount payAmount,vth.trade_branch_id tradeBranchId,vth.trade_branch_name tradeBranchName,vth.trade_date tradeDate,vth.trade_user_name tradeUserName,vth.add_score addScore " +
//                    "from vip v right join vip_trade_history vth on v.id=vth.vip_id where v.tenant_id=${tenantId}");
//            StringBuffer queryCount = new StringBuffer("select count(*) from vip v right join vip_trade_history vth on v.id=vth.vip_id where v.tenant_id=${tenantId}");
//            if (params.startTime) {
//                String startTime = params.startTime;
//                query.append(" and vth.trade_date >= '${startTime}'");
//                queryCount.append(" and vth.trade_date >= '${startTime}'");
//            }
//            if (params.endTime) {
//                String endTime = params.endTime;
//                query.append(" and vth.trade_date <= '${endTime}'");
//                queryCount.append(" and vth.trade_date <= '${endTime}'");
//            }
//            if (params.branchId) {
//                BigInteger branchId = new BigInteger(params.branchId);
//                query.append(" and vth.trade_branch_id = ${branchId}");
//                queryCount.append(" and vth.trade_branch_id = ${branchId}");
//            }
//            if (params.phoneOrCode) {
//                String phoneOrCode =params.phoneOrCode;
//                query.append(" and (v.phone = ${phoneOrCode} or v.vip_code = ${phoneOrCode})");
//                queryCount.append(" and (v.phone = ${phoneOrCode} or v.vip_code = ${phoneOrCode})");
//            }
//            Integer max = params['rows']?.asType(int);
//            Integer page = params['page']?.asType(int);
//            def sq = getSession().createSQLQuery(query.toString());
//            def cq = getSession().createSQLQuery(queryCount.toString());
//            if (max != null && page != null) {//分页查询
//                sq.setMaxResults(max)
//                sq.setFirstResult((page - 1) * max)
//            }
//            def countNum = cq.list()[0]
//            def list = [];
//            if (countNum > 0) {
//                list = sq.list();
//            }
//
//            if (null != list && list.size() > 0) {
//                VipTradeHistoryVo vipTradeHistoryVo = null;
//                for (int i = 0; i < list.size(); i++) {
//                    vipTradeHistoryVo = new VipTradeHistoryVo();
//                    vipTradeHistoryVo.code = list[i][4];
//                    vipTradeHistoryVo.tradeNo = list[i][24];
//                    vipTradeHistoryVo.name = list[i][5];
//                    vipTradeHistoryVo.phone = list[i][8];
//                    vipTradeHistoryVo.tradeType = list[i][25];
//                    vipTradeHistoryVo.tradeAmount = list[i][26];
//
//                    vipTradeHistoryVo.payAmount = list[i][31];
//                    vipTradeHistoryVo.tradeBranchName = list[i][33];
//                    vipTradeHistoryVo.branchId = list[i][32];
//                    vipTradeHistoryVo.tradeDate = list[i][34];
//
//                    vipTradeHistoryVo.tradeUserName = list[i][35];
//                    vipTradeHistoryVo.accumulateAmount = list[i][26];
//
//                    if ("1".equals(list[i][25]) || "4".equals(list[i][25])) {
//                        if (list[i][27]) {
//                            vipTradeHistoryVo.storeAmount = list[i][26];
//                        }
//                    } else {
//                        vipTradeHistoryVo.storeAmount = 0;
//                    }
//                    vipTradeHistoryVo.integralAmount = list[i][28];
//                    vipTradeHistoryVo.addScore = list[i][35];
//                    vipTradeHistoryVoArrayList.add(vipTradeHistoryVo);
//                }
//            }
//            map = ["total": countNum, "rows": vipTradeHistoryVoArrayList]
//        } catch (Exception e) {
//            LogUtil.logError("会员消费历史查询异常" + e.message);
//            throw new ServiceException();
//        }
//        return map
//    }

}
