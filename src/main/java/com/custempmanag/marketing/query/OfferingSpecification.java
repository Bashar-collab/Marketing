package com.custempmanag.marketing.query;

import com.custempmanag.marketing.model.CurrencyTypes;
import com.custempmanag.marketing.model.Offering;
import com.custempmanag.marketing.model.Rating;
import com.custempmanag.marketing.response.OfferingResponse;
import jakarta.persistence.TypedQuery;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.*;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Specification for querying Offerings with dynamic filters, sorting, and pagination.
 * Handles polymorphic relationship with Rating via rateableId and rateableType.
 */
/**
 * Specification for querying Offerings with dynamic filters, sorting, and pagination.
 * Handles polymorphic relationship with Rating via rateableId and rateableType.
 */
public class OfferingSpecification {

    /**
     * Finds Offerings by owner ID (optional) with optional filters, sorting, and pagination.
     *
     * @param ownerId      Mandatory owner ID filter
     * @param name         Optional name filter (case-insensitive partial match)
     * @param minPrice     Optional minimum price filter
     * @param maxPrice     Optional maximum price filter
     * @param categoryName Optional category name filter
     * @param currency     Optional currency
     * @param minRating    Optional minimum average rating filter
     * @param sortBy       Field to sort by (ratingValue, price, name, or id)
     * @param direction    Sort direction (asc or desc)
     * @param pageable     Pagination information
     * @param entityManager EntityManager for Criteria API
     * @return Page of OfferingResponse objects
     */
    public static Page<OfferingResponse> findByOfferingsWithFilters(
            Long ownerId,
            String name,
            Double minPrice,
            Double maxPrice,
            String categoryName,
            CurrencyTypes currency,
            Double minRating,
            String sortBy,
            String direction,
            Pageable pageable,
            EntityManager entityManager) {

        CriteriaBuilder cb = entityManager.getCriteriaBuilder();

        // Main query
        CriteriaQuery<OfferingResponse> query = cb.createQuery(OfferingResponse.class);
        Root<Offering> offering = query.from(Offering.class);
        Join<Offering, Rating> rating = offering.join("ratings", JoinType.LEFT);

        Expression<Double> avgRating = cb.coalesce(cb.avg(rating.get("ratingValue")), 0.0);
        query.multiselect(
                offering.get("id"),
                offering.get("name"),
                offering.get("owner").get("id"),
                offering.get("price"),
                avgRating
        );

        // Apply filters
        List<Predicate> predicates = buildPredicates(cb, offering, ownerId, name, minPrice, maxPrice, categoryName, currency);
        query.where(cb.and(predicates.toArray(new Predicate[0])));

        // Handle minRating filter
        if (minRating != null) {
            query.having(cb.greaterThanOrEqualTo(avgRating, minRating));
        }

        // Group by
        query.groupBy(
                offering.get("id"),
                offering.get("name"),
                offering.get("owner").get("id"),
                offering.get("price")
        );

        // Apply sorting
        List<Order> orders = buildOrders(cb, offering, sortBy, direction, avgRating);
        query.orderBy(orders);

        // Execute main query
        TypedQuery<OfferingResponse> typedQuery = entityManager.createQuery(query);
        typedQuery.setFirstResult((int) pageable.getOffset());
        typedQuery.setMaxResults(pageable.getPageSize());
        List<OfferingResponse> results = typedQuery.getResultList();

        // Log results
        System.out.println("Main query results count: " + results.size());
        results.forEach(r -> System.out.println("Offering: ID=" + r.getId() + ", Name=" + r.getName() + ", Avg Rating=" + r.getRatingValue()));

        // Count query
        CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
        Root<Offering> countRoot = countQuery.from(Offering.class);
        Join<Offering, Rating> countRating = countRoot.join("ratings", JoinType.LEFT);

        countQuery.select(cb.countDistinct(countRoot));
        List<Predicate> countPredicates = buildPredicates(cb, countRoot, ownerId, name, minPrice, maxPrice, categoryName, currency);

        if (minRating != null) {
            Expression<Double> countAvgRating = cb.coalesce(cb.avg(countRating.get("ratingValue")), 0.0);
            countQuery.groupBy(countRoot.get("id"));
            countQuery.having(cb.greaterThanOrEqualTo(countAvgRating, minRating));
        }

        countQuery.where(cb.and(countPredicates.toArray(new Predicate[0])));

        List<Long> countResults = entityManager.createQuery(countQuery).getResultList();
        Long total = countResults.isEmpty() ? 0L : countResults.get(0);
        System.out.println("Total matching records: " + total);

        return new PageImpl<>(results, pageable, total);
    }

    /**
     * Builds predicates for filtering Offerings.
     *
     * @param cb           CriteriaBuilder
     * @param offering     Root for Offering entity
     * @param ownerId      Optional owner ID
     * @param name         Optional name filter
     * @param minPrice     Optional minimum price
     * @param maxPrice     Optional maximum price
     * @param categoryName Optional category name
     * @param currency     Optional currency
     * @return List of predicates
     */
    private static List<Predicate> buildPredicates(
            CriteriaBuilder cb,
            Root<Offering> offering,
            Long ownerId,
            String name,
            Double minPrice,
            Double maxPrice,
            String categoryName,
            CurrencyTypes currency) {
        List<Predicate> predicates = new ArrayList<>();
        // Conditionally apply ownerId filter
        if (ownerId != null) {
            predicates.add(cb.equal(offering.get("owner").get("id"), ownerId));
        }
        if (StringUtils.hasText(name)) {
            predicates.add(cb.like(cb.lower(offering.get("name")), "%" + name.toLowerCase() + "%"));
        }
        if (minPrice != null) {
            predicates.add(cb.greaterThanOrEqualTo(offering.get("price"), minPrice));
        }
        if (maxPrice != null) {
            predicates.add(cb.lessThanOrEqualTo(offering.get("price"), maxPrice));
        }
        if (StringUtils.hasText(categoryName)) {
            predicates.add(cb.equal(offering.get("category").get("name"), categoryName));
        }
        if (currency != null) {
            predicates.add(cb.equal(offering.get("currency"), currency));
        }
        return predicates;
    }

    /**
     * Builds sorting orders.
     *
     * @param cb          CriteriaBuilder
     * @param offering    Root for Offering entity
     * @param sortBy      Field to sort by
     * @param direction   Sort direction
     * @param avgRating   Precomputed average rating expression
     * @return List of orders
     */
    private static List<Order> buildOrders(
            CriteriaBuilder cb,
            Root<Offering> offering,
            String sortBy,
            String direction,
            Expression<Double> avgRating) {
        List<Order> orders = new ArrayList<>();
        String normalizedSortBy = sortBy != null ? sortBy : "id";
        boolean isAsc = "asc".equalsIgnoreCase(direction);

        switch (normalizedSortBy) {
            case "ratingValue":
                orders.add(isAsc ? cb.asc(avgRating) : cb.desc(avgRating));
                break;
            case "price":
                orders.add(isAsc ? cb.asc(offering.get("price")) : cb.desc(offering.get("price")));
                break;
            case "name":
                orders.add(isAsc ? cb.asc(offering.get("name")) : cb.desc(offering.get("name")));
                break;
            default:
                orders.add(isAsc ? cb.asc(offering.get("id")) : cb.desc(offering.get("id")));
        }
        if (!"id".equalsIgnoreCase(normalizedSortBy)) {
            orders.add(cb.asc(offering.get("id"))); // Secondary sort by ID
        }
        return orders;
    }
}