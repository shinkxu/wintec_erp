package erp.chain.service.online;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import saas.api.common.ApiRest;

import java.sql.DatabaseMetaData;
import java.sql.SQLException;

/**
 * Created by liuyandong on 2018-07-17.
 */
@Service
public class ReadWriteSplittingService {
    @Autowired
    private SqlSession sqlSession;

    @Transactional(readOnly = true)
    public ApiRest read() throws SQLException {
        ApiRest apiRest = obtainMetaData();
        apiRest.setMessage("测试读数据库成功！");
        apiRest.setIsSuccess(true);
        return apiRest;
    }

    @Transactional(rollbackFor = Exception.class)
    public ApiRest write() throws SQLException {
        ApiRest apiRest = obtainMetaData();
        apiRest.setMessage("测试读数据库成功！");
        apiRest.setIsSuccess(true);
        return apiRest;
    }

    private ApiRest obtainMetaData() throws SQLException {
        DatabaseMetaData databaseMetaData = sqlSession.getConnection().getMetaData();
        String url = databaseMetaData.getURL();
        ApiRest apiRest = new ApiRest();
        apiRest.setData(url);
        return apiRest;
    }
}
