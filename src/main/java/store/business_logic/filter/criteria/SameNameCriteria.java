package store.business_logic.filter.criteria;

import java.util.List;

public class SameNameCriteria implements ProductCriteria {
    private final String id;

    public SameNameCriteria(String id) {
        this.id = id;
    }
    @Override
    public void apply(StringBuilder sql, List<Object> parameters) {
        sql.append(" AND p.name = (SELECT name FROM public.\"Product\" WHERE id = ?)");
        parameters.add(id);
    }

}
