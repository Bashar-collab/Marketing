package com.custempmanag.marketing.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.custempmanag.marketing.model.Offering;
import com.custempmanag.marketing.response.OfferingResponse;

public interface OfferingRepository extends JpaRepository<Offering, Long> {
    @Query("""
SELECT new com.custempmanag.marketing.response.OfferingResponse(
        o.id,
        o.name,
        o.owner.id,
        o.price,
        COALESCE(AVG(r.ratingValue), 0.0)
        )
    FROM Offering o
    LEFT JOIN Rating r ON o.id = r.rateableId AND r.rateableType = 'Offering'
    WHERE o.owner.id = :ownerId
    GROUP BY o.id, o.name, o.owner.id, o.price
""")
    List<OfferingResponse> findByOwnerId(@Param("ownerId") Long ownerId);

    @Query("""
    SELECT new com.custempmanag.marketing.response.OfferingResponse(
        o.id,
        o.name,
        o.price,
        COALESCE(AVG(r.ratingValue), 0.0)
    )
    FROM Offering o
    LEFT JOIN Rating r ON o.id = r.rateableId AND r.rateableType = 'Offering'
    WHERE (:name IS NULL OR LOWER(o.name) ILIKE '%' || CAST(:name AS text) || '%')
    AND (:minPrice IS NULL OR o.price >= :minPrice)
    AND (:maxPrice IS NULL OR o.price <= :maxPrice)
    AND (:categoryName IS NULL OR o.category.name = :categoryName)
    GROUP BY o.id, o.name, o.price
    HAVING (:minRating IS NULL OR COALESCE(AVG(r.ratingValue), 0.0) >= :minRating)
    ORDER BY 
        CASE 
            WHEN :sortBy = 'ratingValue' AND :direction = 'asc' THEN AVG(r.ratingValue)
            WHEN :sortBy = 'ratingValue' AND :direction = 'desc' THEN -AVG(r.ratingValue)
            ELSE o.id
        END
""")
    Page<OfferingResponse> findAllWithFilters(
            @Param("name") String name,
            @Param("minPrice") Double minPrice,
            @Param("maxPrice") Double maxPrice,
            @Param("categoryName") String categoryName,
            @Param("minRating") Double minRating,
            @Param("sortBy") String sortBy,
            @Param("direction") String direction,
            Pageable pageable
    );

    @Query("""
    SELECT new com.custempmanag.marketing.response.OfferingResponse(
        o.id,
        o.name,
        o.price,
        COALESCE(AVG(r.ratingValue), 0.0)
    )
    FROM Offering o
    LEFT JOIN Rating r ON o.id = r.rateableId AND r.rateableType = 'Offering'
    GROUP BY o.id, o.name, o.price
""")
    Page<OfferingResponse> getAllOfferings(Pageable pageable);
//
//    @Query("""
//    SELECT new com.custempmanag.marketing.response.OfferingResponse(
//        o.id,
//        o.name,
//        o.owner.id,
//        o.price,
//        COALESCE(AVG(r.ratingValue), 0.0)
//    )
//    FROM Offering o
//    LEFT JOIN Rating r ON o.id = r.rateableId AND r.rateableType = 'Offering'
//    WHERE o.owner.id = :ownerId
//    AND (:name IS NULL OR LOWER(o.name) ILIKE '%' || CAST(:name AS text) || '%')
//    AND (:minPrice IS NULL OR o.price >= :minPrice)
//    AND (:maxPrice IS NULL OR o.price <= :maxPrice)
//    AND (:categoryName IS NULL OR o.category.name = :categoryName)
//    GROUP BY o.id, o.name, o.owner.id, o.price
//    HAVING (:minRating IS NULL OR COALESCE(AVG(r.ratingValue), 0.0) >= :minRating)
//   ORDER BY
//            CASE WHEN :sortBy = 'ratingValue' AND :direction = 'asc'  THEN AVG(r.ratingValue) END ASC,
//            CASE WHEN :sortBy = 'ratingValue' AND :direction = 'desc' THEN AVG(r.ratingValue) END DESC,
//
//            CASE WHEN :sortBy = 'price' AND :direction = 'asc'  THEN o.price END ASC,
//            CASE WHEN :sortBy = 'price' AND :direction = 'desc' THEN o.price END DESC,
//
//            CASE WHEN :sortBy = 'name' AND :direction = 'asc'  THEN o.name END ASC,
//            CASE WHEN :sortBy = 'name' AND :direction = 'desc' THEN o.name END DESC,
//
//        o.id
//""")
//    Page<OfferingResponse> findByOwnerIdWithFilters(
//            @Param("ownerId") Long ownerId,
//            @Param("name") String name,
//            @Param("minPrice") Double minPrice,
//            @Param("maxPrice") Double maxPrice,
//            @Param("categoryName") String categoryName,
//            @Param("minRating") Double minRating,
//            @Param("sortBy") String sortBy,
//            @Param("direction") String direction,
//            Pageable pageable
//    );

//    Optional<Offering> findById(long offeringId);

//    @Query(value = """
//    SELECT
//        o.id AS id,
//        o.name AS name,
//        o.price AS price,
//        i.path AS imagePath
//    FROM offering o
//    LEFT JOIN image i
//        ON i.imageable_id = o.id AND i.imageable_type = 'Offering'
//""", nativeQuery = true)
//    List<OfferingProjection> findAllProjected();

//    @Query(value = """
//            SELECT
//                        o.id,\s
//                        o.name,\s
//                        o.price,
//						min(i.id) as image_id,
//                        min(i.path) as image_path,
//                       COALESCE (AVG(r.rating_value), 0.0) as average
//                       FROM offering o LEFT JOIN image i on o.id = i.imageable_id and i.imageable_type = 'offering'
//                       LEFT JOIN rating r on o.id = r.rateable_id and r.rateable_type = 'Offering'
//						GROUP BY o.id, o.name, o.price""", nativeQuery = true)
//    List<Offering> getAllOfferings();

}
