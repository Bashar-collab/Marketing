package com.custempmanag.marketing.repository;

import com.custempmanag.marketing.model.Offering;
import com.custempmanag.marketing.projection.OfferingProjection;
import com.custempmanag.marketing.response.OfferingResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

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
    GROUP BY o.id, o.name, o.owner.id, o.price
""")
    List<OfferingResponse> findByOwnerId(Long ownerId);

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
    List<OfferingResponse> getAllOfferings();

}
