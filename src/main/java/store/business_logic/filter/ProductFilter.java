package store.business_logic.filter;

import store.business_logic.filter.criteria.EqualsCriteria;
import store.business_logic.filter.criteria.ProductCriteria;
import store.business_logic.filter.criteria.SameNameCriteria;

import java.util.ArrayList;
import java.util.List;

public class ProductFilter {
    private final List<ProductCriteria> criteriaList;

    private ProductFilter(Builder builder) {
        this.criteriaList = builder.criteriaList;
    }

    public void applyAll(StringBuilder sql, List<Object> params) {
        for (ProductCriteria criteria : criteriaList) {
            criteria.apply(sql, params);
        }
    }

    public static class Builder {
        private final List<ProductCriteria> criteriaList = new ArrayList<>();

        public Builder withId(String id) {
            if (id != null && !id.trim().isEmpty()) {
                criteriaList.add(new SameNameCriteria(id));
            }
            return this;
        }

        public Builder withColor(String color) {
            if (color != null && !color.equals(" Select Color ")) {
                criteriaList.add(new EqualsCriteria("v.color", color));
            }
            return this;
        }

        public Builder withSize(String size) {
            if (size != null && !size.equals(" Select Size ")) {
                criteriaList.add(new EqualsCriteria("v.size", size));
            }
            return this;
        }

        public ProductFilter build() {
            return new ProductFilter(this);
        }


    }


}
