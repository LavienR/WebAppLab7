package com.example.product_management.controller;

import com.example.product_management.entity.Product;
import com.example.product_management.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import java.math.BigDecimal;
import java.util.List;
import jakarta.validation.Valid;
import org.springframework.validation.BindingResult;

@Controller
@RequestMapping("/products")
public class ProductController {
    
    private final ProductService productService;
    
    @Autowired
    public ProductController(ProductService productService) {
        this.productService = productService;
    }
    
    // List all products
    //adjust for ex5.2
    @GetMapping
    public String listProducts(Model model, @RequestParam(required = false)String category,  @RequestParam(required = false) String sortBy,
    @RequestParam(defaultValue = "asc") String sortDir) {
        List<Product> products;
        
        //ex7.1 sorting
     Sort sort = null;
        if (sortBy != null) {
        sort = sortDir.equals("asc") ? 
            Sort.by(sortBy).ascending() : 
            Sort.by(sortBy).descending();
            //ex7.2
       
    } 
    //ex5.2, category filter 
    //ex7.2 category filter 2 with sorting
        if (category != null && !category.trim().isEmpty()) {
            if (sort != null) {
                products = productService.getProductsByCategory(category, sort);
            } else {
                products = productService.getProductsByCategory(category);
            }

        } else {
            if (sort != null) {
                products = productService.getAllProducts(sort);
            } else {
                products = productService.getAllProducts();
            }
        }

    
            model.addAttribute("products", products);
            //for category dropdown ex5.2
            model.addAttribute("categories", productService.getAllCategories());
            model.addAttribute("selectedCategory", category);
            //for sorting ex7.1
            model.addAttribute("sortBy", sortBy);
            model.addAttribute("sortDir", sortDir);
    
        return "product-list";  // Returns product-list.html
    }
    //ex5.2-Separate filter endpoint for category filtering
    @GetMapping("/filter")
    public String filterByCategory(@RequestParam(required = false) String category, Model model) {
        List<Product> products;
        
        if(category != null && !category.isEmpty()) {
            products = productService.getProductsByCategory(category);
        } else {
            products = productService.getAllProducts();
        }
        
        model.addAttribute("products", products);
        model.addAttribute("categories", productService.getAllCategories());
        model.addAttribute("selectedCategory", category);
        
        return "product-list";
    }
    // Show form for new product
    @GetMapping("/new")
    public String showNewForm(Model model) {
        Product product = new Product();
        model.addAttribute("product", product);
        return "product-form";
    }
    
    // Show form for editing product
    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        return productService.getProductById(id)
                .map(product -> {
                    model.addAttribute("product", product);
                    return "product-form";
                })
                .orElseGet(() -> {
                    redirectAttributes.addFlashAttribute("error", "Product not found");
                    return "redirect:/products";
                });
    }
    
    // Save product (create or update)
    //ex6.2
    @PostMapping("/save")
    public String saveProduct(@ModelAttribute("product") @Valid Product product, BindingResult result,
    Model model, RedirectAttributes redirectAttributes) {
         if (result.hasErrors()) {
        return "product-form";
    }
        try {
            productService.saveProduct(product);
            redirectAttributes.addFlashAttribute("message", 
                    product.getId() == null ? "Product added successfully!" : "Product updated successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error saving product: " + e.getMessage());
        }
        return "redirect:/products";
    }
    
    // Delete product
    @GetMapping("/delete/{id}")
    public String deleteProduct(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            productService.deleteProduct(id);
            redirectAttributes.addFlashAttribute("message", "Product deleted successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error deleting product: " + e.getMessage());
        }
        return "redirect:/products";
    }
    
    // Search products
    @GetMapping("/search")
    public String searchProducts(@RequestParam(required = false) String keyword, @RequestParam(required = false) String category, Model model, @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
         Pageable pageable = PageRequest.of(page, size);
        Page<Product> productPage;
    //ex5.3 use paginated search
    //if there are keyword only
    if(keyword != null && !keyword.isEmpty()) {
        productPage = productService.searchProducts(keyword, pageable); // Use paginated method
    }
    //no keyword, have category
    else if (category != null && !category.isEmpty()) {
 List<Product> products = productService.getProductsByCategory(category);
        model.addAttribute("products", products);
        model.addAttribute("keyword", keyword);
        model.addAttribute("categories", productService.getAllCategories());
        model.addAttribute("selectedCategory", category);
        return "product-list";
    }
    else {
    //get all products with pagination if there are no keywords and category
        productPage = productService.findByNameContaining("", pageable);
    }
    
    model.addAttribute("products", productPage.getContent());
    model.addAttribute("keyword", keyword);
    model.addAttribute("categories", productService.getAllCategories());
    model.addAttribute("selectedCategory", null);
    
    //ex5.3 pagination attributes
    model.addAttribute("currentPage", page);
    model.addAttribute("totalPages", productPage.getTotalPages());
    model.addAttribute("totalItems", productPage.getTotalElements());
    model.addAttribute("pageSize", size);
    
    return "product-list";
}
    //ex5.1
    @GetMapping("/advanced-search")
public String advancedSearch(
        @RequestParam(required = false) String name,
        @RequestParam(required = false) String category,
        @RequestParam(required = false) BigDecimal minPrice,
        @RequestParam(required = false) BigDecimal maxPrice,
        Model model) {

    List<Product> products = productService.searchProducts(name, category, minPrice, maxPrice);

    model.addAttribute("products", products);
    model.addAttribute("name", name);
    model.addAttribute("category", category);
    model.addAttribute("minPrice", minPrice);
    model.addAttribute("maxPrice", maxPrice);
    //ex5.2
    model.addAttribute("categories", productService.getAllCategories());
    model.addAttribute("selectedCategory", category);
    return "product-list";
        }

}
