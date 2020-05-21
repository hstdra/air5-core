package com.example.air5core.repositories;

import com.example.air5core.models.entities.RecommendProduct;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface RecommendProductRepository extends MongoRepository<RecommendProduct, String> {
    Optional<RecommendProduct> findByUserId(String userId);
}
