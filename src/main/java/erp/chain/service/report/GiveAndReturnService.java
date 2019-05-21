package erp.chain.service.report;

import erp.chain.mapper.report.GiveAndReturnMapper;
import erp.chain.model.Pager;
import erp.chain.model.report.GiveAndReturnPagerModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 赠送退菜报表
 *
 * @author hefuzi 2016-11-29
 */
@Service
public class GiveAndReturnService {
    @Autowired
    private GiveAndReturnMapper mapper;

    public Pager queryPager(GiveAndReturnPagerModel model) {
        long num = mapper.queryCount(model);
        if (num > 0){
            model.getPager().setRows(mapper.queryPager(model));
        }
        model.getPager().setTotal(num);
        return model.getPager();
    }
}
