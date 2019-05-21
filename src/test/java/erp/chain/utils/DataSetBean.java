package erp.chain.utils;

import com.alibaba.fastjson.JSON;

import java.util.List;
import java.util.Map;

/**
 * {@link DataSet}数据抽象类
 */
public class DataSetBean {
    /**
     * 数据库表名
     */
    private String tableName;
    /**
     * 数据集合
     */
    private List<Map<String, Object>> dataSets;

    private DataSetBean() {
    }

    /**
     * new 对象
     *
     * @param json bean json 字符串
     */
    public static DataSetBean instance(String json) {
        return JSON.parseObject(json, DataSetBean.class);
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public List<Map<String, Object>> getDataSets() {
        return dataSets;
    }

    public void setDataSets(List<Map<String, Object>> dataSets) {
        this.dataSets = dataSets;
    }
}
