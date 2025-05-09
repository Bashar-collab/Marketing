package com.custempmanag.marketing.repository;

import com.custempmanag.marketing.model.Rating;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

public interface RatingRepository extends CrudRepository<Rating, Long> {
    @Query("SELECT AVG(r.ratingValue) FROM Rating r WHERE r.rateableId = :entityId")
    Double findAverageRatingById(@Param("entityId") Long entityId);

//    Double findRatingByIdAndRateableId(long userId, long offeringId);

    @Query("SELECT r.ratingValue FROM Rating r WHERE r.user.id = :userId AND r.rateableId = :entityId")
    Double findRatingByIdAndRateableId(@Param("userId") long userId, @Param("entityId") long entityId);

}
