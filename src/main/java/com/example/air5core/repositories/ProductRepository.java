package com.example.air5core.repositories;

import com.example.air5core.models.entities.Product;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface ProductRepository extends MongoRepository<Product, String> {
    List<Product> findAllBy(Query query);
}
