package com.example.air5core.controllers;

import com.example.air5core.models.entities.Product;
import com.example.air5core.models.others.Paging;
import com.example.air5core.models.others.ProductMeta;
import com.example.air5core.models.request.FilterProductRequest;
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

    @PostMapping("filter")
    public Paging<Product> filterProducts(@RequestBody FilterProductRequest filterProductRequest,
                                          @RequestParam int pageNumber,
                                          @RequestParam int pageSize) {
        return productService.filterProducts(filterProductRequest, pageNumber, pageSize);
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
