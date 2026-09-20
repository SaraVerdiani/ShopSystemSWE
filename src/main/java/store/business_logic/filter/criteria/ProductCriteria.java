package store.business_logic.filter.criteria;

import java.util.List;

public interface ProductCriteria {
    void apply(StringBuilder sql, List<Object> parameters);

}
