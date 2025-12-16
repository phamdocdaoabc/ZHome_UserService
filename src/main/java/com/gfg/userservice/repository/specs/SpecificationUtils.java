package com.gfg.userservice.repository.specs;

import org.springframework.data.jpa.domain.Specification;

import java.util.Collection;
import java.util.function.Function;

public class SpecificationUtils {
    // String
    public static <T>Specification<T> addIfHasText(Specification<T> spec, String value, Function<String, Specification<T>> specificationSupplier){
        if (value != null && !value.trim().isEmpty()) {
            return spec.and(specificationSupplier.apply(value));
        }
        return spec;
    }

    public static <T, V> Specification<T> addIfNotNull(Specification<T> spec, V value, Function<V, Specification<T>> specificationSupplier) {
        if (value != null) {
            return spec.and(specificationSupplier.apply(value));
        }
        return spec;
    }

    public static <T, V> Specification<T> addIfNotEmpty(Specification<T> spec, Collection<V> value, Function<Collection<V>, Specification<T>> specificationSupplier) {
        if (value != null && !value.isEmpty()) {
            return spec.and(specificationSupplier.apply(value));
        }
        return spec;
    }

}
