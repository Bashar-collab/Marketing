package com.custempmanag.marketing.repository;

import com.custempmanag.marketing.model.Post;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {
    List<Post> findByOwnerId(Long ownerId);
    List<Post> findByTitle(String title);
    List<Post> findByTitleContaining(String title);
    List<Post> findByTitleStartingWith(String title);
    List<Post> findByTitleEndingWith(String title);
    List<Post> findByOfferingId(Long offeringId);
}
