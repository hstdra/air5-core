package com.example.air5core.controllers;

import com.example.air5core.models.entities.Product;
import com.example.air5core.models.others.Paging;
import com.example.air5core.models.others.ProductMeta;
import com.example.air5core.models.request.FilterProductRequest;
import com.example.air5core.models.request.UserPointRequest;
import com.example.air5core.services.AdapterService;
import com.example.air5core.services.ProductService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/products")
public class ProductController {
    private final ProductService productService;
    private final AdapterService adapterService;

    public ProductController(ProductService productService, AdapterService adapterService) {
        this.productService = productService;
        this.adapterService = adapterService;
    }

    @GetMapping("")
    public List<Product> getProducts() {
        return productService.getProducts();
    }

    @GetMapping("recommend")
    public Paging<Product> recommendedProducts(@RequestHeader(value = "x-access-token") String userId,
                                               @RequestParam int pageNumber,
                                               @RequestParam int pageSize) {
        return productService.getRecommendedProducts(userId, pageNumber, pageSize);
    }

    @PostMapping("filter")
    public Paging<Product> filterProducts(@RequestHeader(value = "x-access-token", required = false) String userId,
                                          @RequestBody FilterProductRequest filterProductRequest,
                                          @RequestParam int pageNumber,
                                          @RequestParam int pageSize) {
        Paging<Product> paging = productService.filterProducts(filterProductRequest, pageNumber, pageSize);
        if (userId != null && !filterProductRequest.isFresh()) {
            List<String> productNames = paging.getData().subList(0, Math.min(10, paging.getData().size()))
                    .stream().map(Product::getName).collect(Collectors.toList());
            System.out.println(productNames);
            if (!productNames.isEmpty()) {
                UserPointRequest userPointRequest = new UserPointRequest();

                userPointRequest.setUserId(userId);
                userPointRequest.setPoint(2);
                userPointRequest.setProductNames(productNames);

                adapterService.updateUserPoint(userPointRequest);
            }
        }

        return paging;
    }

    @PostMapping("meta")
    public ProductMeta getProductMeta(@RequestBody FilterProductRequest filterProductRequest) {
        return productService.getProductMeta(filterProductRequest);
    }

    @GetMapping("{id}")
    public Product getProduct(@PathVariable("id") String id) {
        return productService.getProduct(id);
    }

    @PostMapping("import")
    public String importProducts(@RequestBody List<Product> products) {
        productService.importProducts(products);

        return "Import success";
    }
}
