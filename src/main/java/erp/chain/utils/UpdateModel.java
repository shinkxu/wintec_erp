package erp.chain.utils;

import erp.chain.common.Constants;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UpdateModel {
    private String tableName;
    private List<SearchCondition> searchConditions = new ArrayList<SearchCondition>();
    private List<ContentValue> contentValues = new ArrayList<ContentValue>();
    private String whereClause;
    private Map<String, Object> namedParameters = new HashMap<String, Object>();

    public UpdateModel() {

    }

    public UpdateModel(boolean autoSetDeletedFalse) {
        if (autoSetDeletedFalse) {
            this.addSearchCondition("is_deleted", Constants.SQL_OPERATION_SYMBOL_EQUALS, 0);
        }
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public List<SearchCondition> getSearchConditions() {
        return searchConditions;
    }

    public void setSearchConditions(List<SearchCondition> searchConditions) {
        this.searchConditions = searchConditions;
    }

    public List<ContentValue> getContentValues() {
        return contentValues;
    }

    public void setContentValues(List<ContentValue> contentValues) {
        this.contentValues = contentValues;
    }

    public void addSearchCondition(String columnName, String operationSymbol, Object searchParameter) {
        searchConditions.add(new SearchCondition(columnName, operationSymbol, searchParameter));
    }

    public void addContentValue(String columnName, Object value, String symbol) {
        contentValues.add(new ContentValue(columnName, value, symbol));
    }

    public String getWhereClause() {
        return whereClause;
    }

    public void setWhereClause(String whereClause) {
        this.whereClause = whereClause;
    }

    public Map<String, Object> getNamedParameters() {
        return namedParameters;
    }

    public void setNamedParameters(Map<String, Object> namedParameters) {
        this.namedParameters = namedParameters;
    }

    public void addNamedParameter(String name, Object value) {
        this.namedParameters.put(name, value);
    }

    public String getGroupBy() {
        return null;
    }

    public String getOrderBy() {
        return null;
    }
}
