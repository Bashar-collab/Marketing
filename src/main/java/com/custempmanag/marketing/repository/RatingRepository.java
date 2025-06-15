package com.custempmanag.marketing.repository;

import com.custempmanag.marketing.model.Rating;
import com.custempmanag.marketing.model.User;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface RatingRepository extends CrudRepository<Rating, Long> {
    @Query("SELECT COALESCE(AVG(r.ratingValue), 0) FROM Rating r WHERE r.rateableId = :entityId")
    Double findAverageRatingById(@Param("entityId") Long entityId);

//    Double findRatingByIdAndRateableId(long userId, long offeringId);

//    @Query("SELECT COALESCE(SELECT r.ratingValue FROM Rating r WHERE r.user.id = :userId AND r.rateableId = :entityId), 0)"
    @Query(value = "SELECT COALESCE((SELECT rating_value FROM rating WHERE user_id = :userId AND rateable_id = :entityId), 0)", nativeQuery = true)
    Double findRatingByIdAndRateableId(@Param("userId") long userId, @Param("entityId") long entityId);

    Optional<Rating> findByUserAndRateableId(User user, Long rateableId);

    List<Rating> findRatingByRateableId(Long offeringId);

    List<Rating> findRatingByRateableIdAndRateableType(Long entityId, String rateableType);

    @Modifying
    @Query("DELETE FROM Rating r WHERE r.rateableId = :id AND r.rateableType = :type")
    void deleteByRateableIdAndRateableType(@Param("id") Long id, @Param("type") String type);

    @Query("SELECT COALESCE(AVG(r.ratingValue), 0.0) FROM Rating r WHERE r.rateableId = :id AND r.rateableType = :type")
    Double findAverageByRateableIdAndType(@Param("id") Long id, @Param("type") String type);

    @Query("SELECT r FROM Rating r WHERE r.user = :user AND r.rateableId = :rateableId AND r.rateableType = :rateableType")
    Optional<Rating> findByUserAndRateableIdAndRateableType(
            @Param("user") User user,
            @Param("rateableId") Long rateableId,
            @Param("rateableType") String rateableType);
}
