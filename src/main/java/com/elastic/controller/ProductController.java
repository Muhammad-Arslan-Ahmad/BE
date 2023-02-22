package com.elastic.controller;


import com.elastic.payload.*;
import com.elastic.service.ProductService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/product")
public class ProductController {

    private final ProductService service;

    public ProductController(ProductService service) {
        this.service = service;
    }

    @PostMapping("/create-update")
    public @ResponseBody CreateUpdateProductResponse createUpdateProduct(@RequestBody CreateUpdateProductRequest request) {
        return this.service.createUpdateProduct(request);
    }

    @GetMapping("/{id}")
    public @ResponseBody GetProductResponse getProduct(@PathVariable("id") String id) {
        return this.service.getProduct(id);
    }

    @PostMapping("/small")
    public @ResponseBody GetSmallProductResponse getSmallProduct(@RequestBody GetSmallProductRequest request) {
        return this.service.getSmallProduct(request);
    }

    @PostMapping("/search")
    public @ResponseBody SearchProductResponse searchProduct(@RequestBody SearchProductRequest request) {
        return this.service.searchProduct(request);
    }

    @PostMapping("/global-search")
    public @ResponseBody SearchProductResponse globalSearch(@RequestBody GlobalSearchRequest request) {
        return this.service.globalSearch(request);
    }
    
    @PostMapping("/delete")
    public @ResponseBody
    CommonDeleteResponse delete(@RequestBody CommonDeleteRequest request) {
        return this.service.delete(request);
    }
}
