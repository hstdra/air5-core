package com.example.air5core.services;

import com.example.air5core.models.entities.Product;
import com.example.air5core.models.entities.ProductAttribute;
import com.example.air5core.models.request.SearchProductRequest;
import com.example.air5core.repositories.ProductRepository;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.TextCriteria;
import org.springframework.stereotype.Service;

import java.util.LinkedList;
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

    public List<Product> searchProducts(SearchProductRequest searchProductRequest) {
        String q = searchProductRequest.getQuery();
        String room = searchProductRequest.getRoom();
        String category = searchProductRequest.getCategory();
        List<ProductAttribute> productAttributes = searchProductRequest.getProductAttributes();

        List<Criteria> criteriaList = new LinkedList<>();

        if (room != null && !room.isEmpty()) {
            criteriaList.add(Criteria.where("room").is(room));
        }

        if (category != null && !category.isEmpty()) {
            criteriaList.add(Criteria.where("category").is(category));
        }

        productAttributes.forEach(productAttribute -> {
            criteriaList.add(Criteria.where("productAttributes").elemMatch(
                    Criteria.where("attribute").is(productAttribute.getAttribute()).and("value").is(productAttribute.getValue())
            ));
        });

        Query query = new Query(new Criteria().andOperator(criteriaList.toArray(new Criteria[0])));

        if (q != null && !q.isEmpty()) {
            TextCriteria textCriteria = new TextCriteria().matching(q);
            query.addCriteria(textCriteria);
        }
        return mongoTemplate.find(query, Product.class);
    }

    public Product getProduct(String id) {
        return productRepository.findById(id).orElse(null);
    }

    public Product addProduct(Product product) {
        return productRepository.save(product);
    }

    public void importProducts(List<Product> products) {
        products.parallelStream().forEach(productRepository::save);
    }
}
