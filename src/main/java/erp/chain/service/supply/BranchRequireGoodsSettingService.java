package erp.chain.service.supply;

import erp.chain.domain.MapUnderscoreToCamelCase;
import erp.chain.domain.supply.BranchRequireGoodsSetting;
import erp.chain.mapper.GoodsMapper;
import erp.chain.mapper.supply.BranchRequireGoodsSettingMapper;
import erp.chain.model.Pager;
import erp.chain.model.supply.BranchRequireGoodsSettingModel;
import erp.chain.model.supply.QueryBranchRequireGoodsSettingModel;
import erp.chain.utils.NonUniqueResultException;
import erp.chain.utils.ResourceNotExistException;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import saas.api.common.ApiRest;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 机构要货关系
 *
 * @author hefuzi 2016-12-15
 */
@Service
@Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED,
        rollbackFor = {RuntimeException.class})
public class BranchRequireGoodsSettingService {
    private Log logger = LogFactory.getLog(getClass());
    @Autowired
    private BranchRequireGoodsSettingMapper branchRequireGoodsSettingMapper;
    @Autowired
    private GoodsMapper goodsMapper;

    /**
     * 分页查询-
     */
    public Pager queryPager(QueryBranchRequireGoodsSettingModel model) {
        long count = branchRequireGoodsSettingMapper.queryCount(model);
        model.getPager().setTotal(count);
        if (count > 0) {
            List<MapUnderscoreToCamelCase> rows = branchRequireGoodsSettingMapper.queryPager(model);
            model.getPager().setRows(rows);
        }
        return model.getPager();
    }

    /**
     * List查询-
     */
    public List<BranchRequireGoodsSetting> queryList(QueryBranchRequireGoodsSettingModel model) {
        return branchRequireGoodsSettingMapper.queryList(model);
    }

    /**
     * id查询-
     */
    public MapUnderscoreToCamelCase get(BigInteger id) {
        return branchRequireGoodsSettingMapper.getInfo(id);
    }

    /**
     * id删除-
     *
     * @throws OptimisticLockingFailureException [%s]数据版本过期或不存在
     */
    public void delete(Map params) {
        String ids = "";
        int num = 0;
        /*BigInteger id = BigInteger.valueOf(Long.parseLong(params.get("id").toString()));*/
        BigInteger empId = BigInteger.valueOf(Long.parseLong(params.get("empId").toString()));
        /*BigInteger version = BigInteger.valueOf(Long.parseLong(params.get("version").toString()));*/
        if(params.get("id") != null && !"".equals(params.get("id"))){
            ids = params.get("id").toString();
        }
        if(params.get("ids") != null && !"".equals(params.get("ids"))){
            ids = params.get("ids").toString();
        }
        for(String id1:ids.split(",")){
            BigInteger id = BigInteger.valueOf(Long.parseLong(id1));
            num += branchRequireGoodsSettingMapper.delete(id, empId);
        }
        if (num == 0) {
            throw new OptimisticLockingFailureException(String.format("[%s]数据版本过期或不存在"));
        }
    }

    /**
     * id更新-配货价
     *
     * @throws OptimisticLockingFailureException 数据版本过期
     * @throws ResourceNotExistException         数据[%s]不存在
     */
    public void updatePrice(BigInteger id, BigInteger empId, BigInteger version , BigDecimal price) {
        BranchRequireGoodsSetting oldBranchRequireGoodsSetting = branchRequireGoodsSettingMapper.get(id);
        if (oldBranchRequireGoodsSetting == null) {
            throw new ResourceNotExistException(String.format("数据[%s]不存在", id));
        }
        int num = branchRequireGoodsSettingMapper.updatePrice(id,empId,version,price);
        if (num != 1) {
            throw new OptimisticLockingFailureException("数据版本过期");
        }
    }

    /**
     * 保存-
     */
    public ApiRest save(BranchRequireGoodsSettingModel model) {
        ApiRest apiRest=new ApiRest();
       List<BranchRequireGoodsSetting> settingList = new ArrayList<>();
        BigInteger[] goodsIds = new BigInteger[model.details.size()];
        Date createDate = new Date();
        int x = 0;
        for (BranchRequireGoodsSettingModel.Detail detail : model.details){
            Long count=branchRequireGoodsSettingMapper.countDetail(model.tenantId,model.branchId,model.distributionCenterId,detail.goodsId);
            if(count>0){
                apiRest.setIsSuccess(false);
                apiRest.setError("商品已经设置，请不要重复添加！");
                apiRest.setMessage("商品已经设置，请不要重复添加！");
                return apiRest;
            }
            BranchRequireGoodsSetting setting = new BranchRequireGoodsSetting();
            setting.setTenantId(model.tenantId);
            setting.setBranchId(model.branchId);
            setting.setDistributionCenterId(model.distributionCenterId);
            setting.setCreateBy(model.empId.toString());
            setting.setCreateAt(createDate);
            setting.setLastUpdateAt(createDate);
            setting.setLastUpdateBy(model.empId.toString());
            setting.setGoodsId(detail.goodsId);
            setting.setShippingPrice(detail.price);
            settingList.add(setting);

            goodsIds[x++] = detail.goodsId;
        }
        List rootGoodsList = goodsMapper.queryRootBranchGoodsInfo(goodsIds);
        if (rootGoodsList.size() != goodsIds.length) {
            //throw new NonUniqueResultException(String.format("%s中有商品不存在,", ArrayUtils.toString(goodsIds)));
            throw new NonUniqueResultException(String.format("所选有异常商品,", ArrayUtils.toString(goodsIds)));
        }

        branchRequireGoodsSettingMapper.saveList(settingList);
        apiRest.setIsSuccess(true);
        apiRest.setMessage("保存成功！");
        return apiRest;
    }
}