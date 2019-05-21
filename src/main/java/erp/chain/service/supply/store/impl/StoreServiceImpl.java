package erp.chain.service.supply.store.impl;

import erp.chain.mapper.supply.store.StoreMapper;
import erp.chain.model.Pager;
import erp.chain.model.supply.store.QueryGoodsStoreModel;
import erp.chain.model.supply.store.QueryStoreModel;
import erp.chain.service.supply.store.StoreService;
import erp.chain.model.supply.store.QueryStoreAccountModel;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 分页查询库存流水
 */
@Service("storeService")
@Transactional(rollbackFor = Exception.class)
public class StoreServiceImpl implements StoreService {
    @Autowired
    StoreMapper storeMapper;

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public Pager queryStoreAccountPager(QueryStoreAccountModel model) {
        Map<String,Object> res = storeMapper.queryStoreAccountCount(model);
        model.getPager().setTotal((long) res.get("count"));
        List<Map<String, Object>> footer = new ArrayList<>();
        res.remove("count");
        footer.add(res);
        model.getPager().setFooter(footer);

        if (model.getPager().getTotal() > 0){
            List<HashMap<String,Object>> rows = storeMapper.queryStoreAccountPager(model);
            model.getPager().setRows(rows);
        }
        return model.getPager();
    }

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public Pager queryStorePager(QueryStoreModel model) {
        long count = storeMapper.queryStoreCount(model);
        model.getPager().setTotal(count);
        List<Map> footer = storeMapper.queryStoreFooter(model);
        model.getPager().setFooter(footer);
        if (model.getPager().getTotal() > 0){
            List<HashMap<String,Object>> rows = storeMapper.queryStorePager(model);
            model.getPager().setRows(rows);
        }
        return model.getPager();
    }

    @Override
    public Pager queryGoodsStorePager(QueryGoodsStoreModel model) {
        if(!StringUtils.isEmpty(model.getBarCode())){
            model.setGoodsNameOrCodeLike("");
        }
        long count = storeMapper.queryGoodsStoreCount(model);
        model.getPager().setTotal(count);
        if (model.getPager().getTotal() > 0){
            List<HashMap<String,Object>> rows = storeMapper.queryGoodsStorePager(model);
            model.getPager().setRows(rows);
        }
        return model.getPager();
    }
}
