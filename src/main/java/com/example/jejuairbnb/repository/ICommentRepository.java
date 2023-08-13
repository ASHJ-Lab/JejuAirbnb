package com.example.jejuairbnb.repository;

import com.example.jejuairbnb.domain.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ICommentRepository extends JpaRepository<Comment, Long> {

    Page<Comment> findByUserId(Long userId, Pageable pageable);

    Page<Comment> findByDescriptionContaining(String description, Pageable pageable);

    @Query("SELECT AVG(c.rating) FROM Comment c WHERE c.product.id = :productId")
    Double avgByProductId(Long productId);

    List<Comment> findByProductId(Long productId);

    Long countByProductId(Long productId);

    @Query("SELECT MAX(c.rating) FROM Comment c WHERE c.product.id = :productId")
    Double maxByProductId(Long productId);
}