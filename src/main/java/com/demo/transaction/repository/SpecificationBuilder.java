package com.demo.transaction.repository;

import com.demo.transaction.enumeration.SearchOperation;
import org.springframework.data.jpa.domain.Specification;
import jakarta.persistence.criteria.*;

import java.util.ArrayList;
import java.util.List;

public class SpecificationBuilder<T> {

    private final List<SearchCriteria> criteriaList = new ArrayList<>();

    public SpecificationBuilder<T> with(String key, Object value, SearchOperation operation) {
        if (value != null && !(value instanceof String str && str.isBlank())) {
            criteriaList.add(new SearchCriteria(key, value, operation));
        }
        return this;
    }

    public Specification<T> build() {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            for (SearchCriteria criteria : criteriaList) {
                switch (criteria.operation()) {
                    case EQUAL -> {
                        if (criteria.key().contains(".")) {
                            String[] keys = criteria.key().split("\\.");
                            predicates.add(cb.equal(root.get(keys[0]).get(keys[1]), criteria.value()));
                        } else {
                            predicates.add(cb.equal(root.get(criteria.key()), criteria.value()));
                        }
                    }
                    case LIKE -> predicates.add(cb.like(root.get(criteria.key()), "%" + criteria.value() + "%"));
                    case GREATER_THAN -> predicates.add(cb.greaterThan(root.get(criteria.key()), criteria.value().toString()));
                    case LESS_THAN -> predicates.add(cb.lessThan(root.get(criteria.key()), criteria.value().toString()));
                    // 可扩展更多操作
                }
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}

