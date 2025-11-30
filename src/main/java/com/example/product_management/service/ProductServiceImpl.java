package com.example.product_management.service;

import com.example.product_management.entity.Product;
import com.example.product_management.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Sort;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ProductServiceImpl implements ProductService {
    
    private final ProductRepository productRepository;
    
    @Autowired
    public ProductServiceImpl(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }
    
    @Override
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }
    
    @Override
    public Optional<Product> getProductById(Long id) {
        return productRepository.findById(id);
    }
    
    @Override
    public Product saveProduct(Product product) {
        // Validation logic can go here
        return productRepository.save(product);
    }
    
    @Override
    public void deleteProduct(Long id) {
        productRepository.deleteById(id);
    }
    
    @Override
    public List<Product> searchProducts(String keyword) {
        return productRepository.findByNameContaining(keyword);
    }
    
    @Override
    public List<Product> getProductsByCategory(String category) {
        return productRepository.findByCategory(category);
    }
    //ex5.1
    @Override
    public List<Product> searchProducts(String name, String category, BigDecimal minPrice, BigDecimal maxPrice) {
        if(name!= null && name.isEmpty()) { //convert empty string
       name = null;                         //and category to null
         }                                  //so query will ignore them
        if(category!= null && category.isEmpty()) {
            category = null;
        }
        return productRepository.searchProducts(name, category, minPrice, maxPrice);
}
    //ex5.2
    @Override
    public List<String> getAllCategories() {
        return productRepository.findAllCategories();
    }
    //ex5.3 paginated search
    @Override
    public Page<Product> findByNameContaining(String keyword, Pageable pageable) {
    return productRepository.findByNameContaining(keyword, pageable);
}

    @Override
    public Page<Product> searchProducts(String keyword, Pageable pageable) {
    return productRepository.findByNameContaining(keyword, pageable);
}

    //ex7.1 sort
    @Override
    public List<Product> getAllProducts(Sort sort) {
        return productRepository.findAll(sort);
    }
    //ex7.2 sort with category and filter
     @Override
    public List<Product> getProductsByCategory(String category, Sort sort) {
        return productRepository.findByCategory(category, sort);
    }
    //ex8.1
     @Override
    public long countByCategory(String category) {
        return productRepository.countByCategory(category);
    }

    @Override
    public BigDecimal calculateTotalValue() {
        BigDecimal totalValue = productRepository.calculateTotalValue();
        return totalValue != null ? totalValue : BigDecimal.ZERO;
    }

    @Override
    public BigDecimal calculateAveragePrice() {
        BigDecimal avgPrice = productRepository.calculateAveragePrice();
        return avgPrice != null ? avgPrice : BigDecimal.ZERO;
    }

    @Override
    public List<Product> findLowStockProducts(int threshold) {
        return productRepository.findLowStockProducts(threshold);
    }

    @Override
    public List<Product> findRecentProducts() {
        return productRepository.findTop5ByOrderByCreatedAtDesc();
    }
}