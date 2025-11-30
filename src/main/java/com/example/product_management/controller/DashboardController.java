package com.example.product_management.controller;

import com.example.product_management.service.ProductService;
import com.example.product_management.entity.Product;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.math.BigDecimal;
import java.util.List;

@Controller
@RequestMapping("/dashboard")
public class DashboardController {
    
    @Autowired
    private ProductService productService;
    
    @GetMapping
    public String showDashboard(Model model) {
      // Total products count
        long totalProducts = productService.getAllProducts().size();
        model.addAttribute("totalProducts", totalProducts);
        
        // Products by category - using separate lists instead of HashMap
        List<String> categories = productService.getAllCategories();
        model.addAttribute("categories", categories);
        
        // Create arrays for category counts
        long[] categoryCounts = new long[categories.size()];
        for (int i = 0; i < categories.size(); i++) {
            categoryCounts[i] = productService.countByCategory(categories.get(i));
        }
        model.addAttribute("categoryCounts", categoryCounts);
        
        // Total inventory value
        BigDecimal totalValue = productService.calculateTotalValue();
        model.addAttribute("totalValue", totalValue);
        
        // Average product price
        BigDecimal averagePrice = productService.calculateAveragePrice();
        model.addAttribute("averagePrice", averagePrice);
        
        // Low stock alerts (quantity < 10)
        List<Product> lowStockProducts = productService.findLowStockProducts(10);
        model.addAttribute("lowStockProducts", lowStockProducts);
        
        //Recent products (last 5 added)
        List<Product> recentProducts = productService.findRecentProducts();
        model.addAttribute("recentProducts", recentProducts);
        
        return "dashboard";
    }
}
