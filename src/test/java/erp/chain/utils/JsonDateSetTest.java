package erp.chain.utils;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Before;
import org.junit.Ignore;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.jdbc.support.JdbcUtils;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

@Ignore
public class JsonDateSetTest extends AbstractTransactionalJUnit4SpringContextTests {
    private final static String INSERT_SQL = " insert into %s (%s) value(%s)";
    private static final Log logger = LogFactory.getLog(JsonDateSetTest.class);
    private final List<DataSetBean> DATA_SET_BEAN = new ArrayList<>();

    @Before
    public void configDataSet() throws IOException {
        if (logger.isDebugEnabled()) {
            logger.debug("初始化数据");
        }
        Class thisClass = this.getClass();
        if (thisClass.isAnnotationPresent(DataSet.class)) {
            DataSet dataSet = (DataSet) thisClass.getAnnotation(DataSet.class);
            String[] keys = dataSet.key();
            String[] paths = dataSet.path();
            for (String path : paths) {
                if (path == null || path.isEmpty()) {
                    return;
                }
                ClassPathResource classPathResource = new ClassPathResource(path);
                Properties properties = new Properties();
                properties.load(classPathResource.getInputStream());
                for (String key : keys) {
                    String val = properties.getProperty(key);
                    if (val != null && !val.isEmpty()) {
                        DATA_SET_BEAN.add(DataSetBean.instance(val));
                    }
                }
            }
        }
        insertData();
    }

    public void insertData() {
        if (DATA_SET_BEAN.size() > 0) {
            Connection con = DataSourceUtils.getConnection(jdbcTemplate.getDataSource());
            try {
                Connection conToUse = con;
                if (jdbcTemplate.getNativeJdbcExtractor() != null) {
                    conToUse = jdbcTemplate.getNativeJdbcExtractor().getNativeConnection(con);
                }
                for (DataSetBean bean : DATA_SET_BEAN) {
                    for (Map<String, Object> dataSet : bean.getDataSets()) {
                        PreparedStatement stmt = null;
                        try {
                            StringBuilder cols = new StringBuilder();
                            StringBuilder values = new StringBuilder();
                            Object[] val_ = createSql(dataSet, cols, values);

                            String sql = String.format(INSERT_SQL, bean.getTableName(), cols.toString(), values.toString());
                            stmt = conToUse.prepareStatement(sql);
                            for (int x = 0; x < val_.length; x++) {
                                stmt.setObject(x + 1, val_[x]);
                            }
                            stmt.execute();
                        } catch (SQLException e) {
                            logger.error("[insertData] ", e);
                        } finally {
                            JdbcUtils.closeStatement(stmt);
                        }
                    }
                }
            } catch (SQLException e) {
                logger.error("[insertData] ", e);
                DataSourceUtils.releaseConnection(con, jdbcTemplate.getDataSource());
                con = null;
            } finally {
                DataSourceUtils.releaseConnection(con, jdbcTemplate.getDataSource());
            }
        }
    }

    private Object[] createSql(Map<String, Object> dataSet, StringBuilder cols, StringBuilder values) {
        Object[] val_ = new Object[dataSet.size()];
        int x = 0;
        for (Map.Entry<String, Object> entry : dataSet.entrySet()) {
            cols.append(",").append(entry.getKey());
            values.append(",").append("?");
            val_[x++] = entry.getValue();
        }
        if (cols.length() > 0) {
            cols.delete(0, 1);
        }
        if (values.length() > 0) {
            values.delete(0, 1);
        }
        return val_;
    }

}
