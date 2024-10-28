package ch.hearc.ig.orderresto.persistence.criteria;

public class Condition {
    private String operator;
    private String columnName;
    private Object value;

    public Condition(String operator, String columnName, Object value) {
        this.operator = operator;
        this.columnName = columnName;
        this.value = value;
    }

    public String getOperator() {
        return operator;
    }

    public String getColumnName() {
        return columnName;
    }

    public Object getValue() {
        return value;
    }
}
