package erp.chain.mapper.o2o;

import erp.chain.domain.o2o.AlipayMarketingCardTemplate;
import erp.chain.utils.SearchModel;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * Created by liuyandong on 2018-01-23.
 */
@Mapper
public interface AlipayMarketingCardTemplateMapper {
    long insert(AlipayMarketingCardTemplate alipayMarketingCardTemplate);
    long update(AlipayMarketingCardTemplate alipayMarketingCardTemplate);
    AlipayMarketingCardTemplate find(SearchModel searchModel);
    List<AlipayMarketingCardTemplate> findAll(SearchModel searchModel);
}
