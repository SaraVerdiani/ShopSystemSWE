package store.business_logic.filter.criteria;

import java.util.List;

public class EqualsCriteria implements ProductCriteria {
    private final String field;
    private final Object value;

    public EqualsCriteria(String field, Object value) {
        this.field = field;
        this.value = value;
    }

    @Override
    public void apply(StringBuilder sql, List<Object> parameters) {
        sql.append(" AND ").append(field).append(" = ?");
        parameters.add(value);
    }
}
