package org.oztrack.data.access.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.ParameterExpression;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

public class DaoHelper {
    public static <T> List<T> getEntitiesForOaiPmh(EntityManager em, Class<T> clazz, Date from, Date until, String setSpec) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<T> cq = cb.createQuery(clazz).distinct(true);
        Root<T> entity = cq.from(clazz);
        List<Predicate> predicates = new ArrayList<Predicate>();
        predicates.add(cb.isTrue(entity.<Boolean>get("includeInOaiPmh")));
        Path<Date> updateDateAttr = entity.<Date>get("updateDate");
        Path<Date> createDateAttr = entity.<Date>get("createDate");
        if (from != null) {
            ParameterExpression<Date> fromParam = cb.parameter(Date.class, "from");
            predicates.add(cb.or(
                cb.and(cb.isNotNull(updateDateAttr), cb.lessThanOrEqualTo(fromParam, updateDateAttr)),
                cb.and(cb.isNull(updateDateAttr), cb.lessThanOrEqualTo(fromParam, createDateAttr))
            ));
        }
        if (until != null) {
            ParameterExpression<Date> untilParam = cb.parameter(Date.class, "until");
            predicates.add(cb.or(
                cb.and(cb.isNotNull(updateDateAttr), cb.greaterThanOrEqualTo(untilParam, updateDateAttr)),
                cb.and(cb.isNull(updateDateAttr), cb.greaterThanOrEqualTo(untilParam, createDateAttr))
            ));
        }
        if (setSpec != null) {
            ParameterExpression<String> setSpecParam = cb.parameter(String.class, "setSpec");
            ParameterExpression<String> subsetSpecPatternParam = cb.parameter(String.class, "subsetSpecPatternParam");
            Join<T, String> setSpecsJoin = entity.join("oaiPmhSetSpecs");
            predicates.add(cb.or(
                cb.equal(setSpecsJoin, setSpecParam),
                cb.like(setSpecsJoin, subsetSpecPatternParam)
            ));
        }
        cq.where(predicates.toArray(new Predicate[] {}));
        TypedQuery<T> query = em.createQuery(cq);
        if (from != null) {
            query.setParameter("from", from);
        }
        if (until != null) {
            query.setParameter("until", until);
        }
        if (setSpec != null) {
            query.setParameter("setSpec", setSpec);
            query.setParameter("subsetSpecPatternParam", setSpec + ":%");
        }
        return query.getResultList();
    }
}
