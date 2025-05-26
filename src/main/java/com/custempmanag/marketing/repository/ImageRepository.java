package com.custempmanag.marketing.repository;

import com.custempmanag.marketing.model.Image;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ImageRepository extends CrudRepository<Image, Long> {
    List<Image> findByImageableIdAndImageableType(Long imageableId, String imageableType);

    Optional<Image> findByPath(String path);

    @Modifying
    @Query("DELETE FROM Image i WHERE i.imageableId = :id AND i.imageableType = :type")
    void deleteByImageableIdAndImageableType(@Param("id") Long id, @Param("type") String type);

    List<Image> findPathByImageableIdAndImageableType(Long imageableId, String imageableType);

    @Query("""
    SELECT i FROM Image i
    WHERE i.imageableId IN :ids AND i.imageableType = :type
""")
    List<Image> findByImageableIdInAndImageableType(@Param("ids") List<Long> ids, @Param("type") String type);

}
