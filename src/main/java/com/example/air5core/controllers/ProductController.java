package com.example.air5core.controllers;

import com.example.air5core.models.entities.Product;
import com.example.air5core.models.request.SearchProductRequest;
import com.example.air5core.services.ProductService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/products")
public class ProductController {
    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping("")
    public List<Product> getProducts() {
        return productService.getProducts();
    }

    @PostMapping("search")
    public List<Product> searchProducts(@RequestBody SearchProductRequest searchProductRequest) {
        return productService.searchProducts(searchProductRequest);
    }

    @GetMapping("{id}")
    public Product getProduct(@PathVariable("id") String id) {
        return productService.getProduct(id);
    }

    @PostMapping("")
    public Product addProduct(@RequestBody Product product) {
        return productService.addProduct(product);
    }

    @PostMapping("import")
    public String importProducts(@RequestBody List<Product> products) {
        productService.importProducts(products);

        return "Import success";
    }
}
