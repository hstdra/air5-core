package com.example.air5core.services;

import com.example.air5core.helpers.MapperManager;
import com.example.air5core.models.entities.Product;
import com.example.air5core.models.entities.RecommendProduct;
import com.example.air5core.models.others.Paging;
import com.example.air5core.models.others.ProductAttribute;
import com.example.air5core.models.others.ProductMeta;
import com.example.air5core.models.request.FilterProductRequest;
import com.example.air5core.repositories.ProductRepository;
import com.example.air5core.repositories.RecommendProductRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.CriteriaDefinition;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.TextCriteria;
import org.springframework.data.repository.support.PageableExecutionUtils;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.*;

@Service
public class ProductService {
    private final ProductRepository productRepository;
    private final RecommendProductRepository recommendProductRepository;
    private final MongoTemplate mongoTemplate;

    public ProductService(ProductRepository productRepository, RecommendProductRepository recommendProductRepository, MongoTemplate mongoTemplate) {
        this.productRepository = productRepository;
        this.recommendProductRepository = recommendProductRepository;
        this.mongoTemplate = mongoTemplate;
    }

    public List<Product> getProducts() {
        return productRepository.findAll();
    }

    public Paging<Product> getRecommendedProducts(String userId, int pageNumber, int pageSize) {
        RecommendProduct recommendProduct = recommendProductRepository.findByUserId(userId).orElse(null);
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        Query query;
        List<Product> products;

        if (recommendProduct != null) {
            query = new Query(Criteria.where("name").in(recommendProduct.getProductNames()));
            products = mongoTemplate.find(query.with(pageable), Product.class);
            products.sort(Comparator.comparing(c -> recommendProduct.getProductNames().indexOf(c.getName())));
        } else {
            query = new Query();
            products = mongoTemplate.find(query.with(pageable), Product.class);
        }

        Page<Product> page = PageableExecutionUtils.getPage(
                products,
                pageable,
                () -> mongoTemplate.count(Query.of(query).limit(-1).skip(-1), Product.class));

        return MapperManager.pageToPaging(page);
    }

    public Paging<Product> filterProducts(FilterProductRequest filterProductRequest, int pageNumber, int pageSize) {
        Query query = new Query();
        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by("score"));

        getCriteriaDefinitions(filterProductRequest).forEach(query::addCriteria);
        Page<Product> page = PageableExecutionUtils.getPage(
                mongoTemplate.find(query.with(pageable), Product.class),
                pageable,
                () -> mongoTemplate.count(Query.of(query).limit(-1).skip(-1), Product.class));

        return MapperManager.pageToPaging(page);
    }

    public ProductMeta getProductMeta(FilterProductRequest filterProductRequest) {
        List<AggregationOperation> operations = new LinkedList<>();

        getCriteriaDefinitions(filterProductRequest).forEach(criteriaDefinition -> operations.add(match(criteriaDefinition)));

        operations.add(
                facet(sortByCount("room")).as("rooms")
                        .and(sortByCount("category")).as("categories")
                        .and(unwind("productAttributes"), sortByCount("productAttributes")).as("values")
                        .and(unwind("productAttributes"), sortByCount("productAttributes.attribute")).as("attributes")
        );

        Aggregation aggregation = newAggregation(operations);

        return mongoTemplate.aggregate(aggregation, "product", ProductMeta.class).getMappedResults().get(0);
    }

    public Product getProduct(String id) {
        return productRepository.findById(id).orElse(null);
    }

    public void importProducts(List<Product> products) {
        products.parallelStream().forEach(productRepository::save);
    }

    private List<CriteriaDefinition> getCriteriaDefinitions(FilterProductRequest filterProductRequest) {
        String q = filterProductRequest.getQuery();
        String room = filterProductRequest.getRoom();
        String category = filterProductRequest.getCategory();
        double minPrice = filterProductRequest.getMinPrice();
        double maxPrice = filterProductRequest.getMaxPrice();

        List<ProductAttribute> productAttributes = filterProductRequest.getProductAttributes();
        List<CriteriaDefinition> criteriaDefinitions = new LinkedList<>();

        if (q != null && !q.trim().isEmpty()) {
            criteriaDefinitions.add(new TextCriteria().matchingAny(q.trim()));
        }

        if (room != null && !room.trim().isEmpty()) {
            criteriaDefinitions.add(Criteria.where("room").is(room.trim()));
        }

        if (category != null && !category.trim().isEmpty()) {
            criteriaDefinitions.add(Criteria.where("category").is(category.trim()));
        }

        if (productAttributes != null) {
            productAttributes.forEach(productAttribute -> criteriaDefinitions.add(Criteria.where("productAttributes").elemMatch(
                    Criteria.where("attribute").is(productAttribute.getAttribute()).and("value").is(productAttribute.getValue())
            )));
        }

        criteriaDefinitions.add(Criteria.where("discountPrice").gte(minPrice).lte(maxPrice));

        return criteriaDefinitions;
    }
}
