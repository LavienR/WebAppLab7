package com.example.product_management.service;

import com.example.product_management.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Sort;
import java.math.BigDecimal;
public interface ProductService {
    
    List<Product> getAllProducts();
    //ex7.1 sort
     List<Product> getAllProducts(Sort sort);
    
    Optional<Product> getProductById(Long id);
    
    Product saveProduct(Product product);
    
    void deleteProduct(Long id);
    
    List<Product> searchProducts(String keyword);
    
    List<Product> getProductsByCategory(String category);
    //ex7.2 sort with category and filter
    List<Product> getProductsByCategory(String category, Sort sort);

    //ex5.1
    List<Product> searchProducts(String name, String category, java.math.BigDecimal minPrice, java.math.BigDecimal maxPrice);
    //ex5.2
    List<String> getAllCategories();
    //ex5.3
    Page<Product> findByNameContaining(String keyword, Pageable pageable);
    Page<Product> searchProducts(String keyword, Pageable pageable);

    //ex8.1
     long countByCategory(String category);
    BigDecimal calculateTotalValue();
    BigDecimal calculateAveragePrice();
    List<Product> findLowStockProducts(int threshold);
    List<Product> findRecentProducts();
}
