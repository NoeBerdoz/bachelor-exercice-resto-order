package ch.hearc.ig.orderresto.persistence.filter;

import java.util.ArrayList;
import java.util.List;

public class Filter {
    private List<Condition> conditions = new ArrayList<>();

    /* TODO
        maybe here implement addWhereEquals addWhereLike addWhereGraterThan etc...
        so this shows to the next programmer what method he can directly used (are supported)
        This implementation can lead to SQL injection
        Take more time on this only if you finished the project, as this is a bit out of the project course scope
    * */
    public void add(String operator, String columnName, Object value) {
        conditions.add(new Condition(operator, columnName, value));
    }

    public List<Condition> getConditions() {
        return conditions;
    }
}
