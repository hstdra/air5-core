package com.example.air5core.services;

import com.example.air5core.helpers.MapperManager;
import com.example.air5core.models.entities.Product;
import com.example.air5core.models.entities.ProductAttribute;
import com.example.air5core.models.others.Paging;
import com.example.air5core.models.request.FilterProductRequest;
import com.example.air5core.repositories.ProductRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.TextCriteria;
import org.springframework.data.repository.support.PageableExecutionUtils;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductService {
    private final ProductRepository productRepository;
    private final MongoTemplate mongoTemplate;

    public ProductService(ProductRepository productRepository, MongoTemplate mongoTemplate) {
        this.productRepository = productRepository;
        this.mongoTemplate = mongoTemplate;
    }

    public List<Product> getProducts() {
        return productRepository.findAll();
    }

    public Paging<Product> filterProducts(FilterProductRequest filterProductRequest, int pageNumber, int pageSize) {
        Query query = new Query();
        String q = filterProductRequest.getQuery();
        String room = filterProductRequest.getRoom();
        String category = filterProductRequest.getCategory();
        List<ProductAttribute> productAttributes = filterProductRequest.getProductAttributes();

        if (q != null && !q.isEmpty()) {
            query.addCriteria(new TextCriteria().matching(q.trim()));
        }

        if (room != null && !room.trim().isEmpty()) {
            query.addCriteria(Criteria.where("room").is(room));
        }

        if (category != null && !category.trim().isEmpty()) {
            query.addCriteria(Criteria.where("category").is(category));
        }

        if (productAttributes != null){
            productAttributes.forEach(productAttribute -> query.addCriteria(Criteria.where("productAttributes").elemMatch(
                    Criteria.where("attribute").is(productAttribute.getAttribute()).and("value").is(productAttribute.getValue())
            )));
        }

        Pageable pageable = PageRequest.of(pageNumber, pageSize);

        Page<Product> page = PageableExecutionUtils.getPage(
                mongoTemplate.find(query.with(pageable), Product.class),
                pageable,
                () -> mongoTemplate.count(Query.of(query).limit(-1).skip(-1), Product.class));

        return MapperManager.pageToPaging(page);
    }

    public Product getProduct(String id) {
        return productRepository.findById(id).orElse(null);
    }

    public void importProducts(List<Product> products) {
        products.parallelStream().forEach(productRepository::save);
    }
}
