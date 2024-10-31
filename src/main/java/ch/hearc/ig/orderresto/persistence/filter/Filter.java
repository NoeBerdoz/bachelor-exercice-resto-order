package ch.hearc.ig.orderresto.persistence.filter;

import java.util.ArrayList;
import java.util.List;

public class Filter {
    private List<Condition> conditions = new ArrayList<>();

    /* TODO
        maybe here implement addWhereEquals addWhereLike addWhereGraterThan etc...
        so this shows to the next programmer what method he can directly used (are supported)
    * */
    public void add(String operator, String columnName, Object value) {
        conditions.add(new Condition(operator, columnName, value));
    }

    public List<Condition> getConditions() {
        return conditions;
    }
}
