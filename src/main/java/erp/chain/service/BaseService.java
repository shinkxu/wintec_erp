package erp.chain.service;

import erp.chain.domain.Pos;
import erp.chain.mapper.CommonMapper;
import erp.chain.mapper.PosMapper;
import org.apache.commons.lang.StringUtils;
import org.apache.ibatis.session.SqlSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by xumx on 2016/11/3.
 */
@Service
public class BaseService {

    protected Logger log = LoggerFactory.getLogger(getClass());

    @Autowired
    protected SqlSession sqlSession;

    @Autowired
    protected CommonMapper baseMapper;

    @Autowired
    PosMapper posMapper;

    public List<Map<String, Object>> select(String sql) {
        return StringUtils.isEmpty(sql) ? null : baseMapper.select(sql);
    }

    public int insert(String sql) {
        return StringUtils.isEmpty(sql) ? 0 : baseMapper.insert(sql);
    }

    public int update(String sql) {
        return StringUtils.isEmpty(sql) ? 0 : baseMapper.update(sql);
    }

    public int delete(String sql) {
        return StringUtils.isEmpty(sql) ? 0 : baseMapper.delete(sql);
    }

    public Pos findPos(BigInteger id) throws Exception {
        if (id == null) {
            return null;
        }
        Map<String, Object> params = new HashMap<>();
        params.put("id", id);
        Pos p = posMapper.find(params);
        if (p == null) {
            throw new Exception("未查询到pos信息");
        }
        return p;
    }
}
