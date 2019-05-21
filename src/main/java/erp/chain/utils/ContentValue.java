package erp.chain.utils;

public class ContentValue {
    private String columnName;
    private Object value;
    private String symbol;

    public ContentValue() {

    }

    public ContentValue(String columnName, Object value, String symbol) {
        this.columnName = columnName;
        this.value = value;
        this.symbol = symbol;
    }

    public String getColumnName() {
        return columnName;
    }

    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }
}
