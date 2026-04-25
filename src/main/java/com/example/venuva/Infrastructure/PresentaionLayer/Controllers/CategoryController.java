package com.example.venuva.Infrastructure.PresentaionLayer.Controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.venuva.Core.Domain.Abstractions.Result;
import com.example.venuva.Core.ServiceLayer.CategoryService;
import com.example.venuva.Infrastructure.Config.ResponseUtility;
import com.example.venuva.Shared.Dtos.CategoryDTO;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;




@RestController
@RequestMapping("/api/categories")
public class CategoryController {
    @Autowired 
    private CategoryService categoryService;

    @GetMapping
    public ResponseEntity<?> getAll() {
        Result<List<CategoryDTO>> result = categoryService.getAll();
        return ResponseUtility.toResponse(result);
    }

    @GetMapping("{Id}")
    public ResponseEntity<?> getById(@PathVariable int id) {
        Result<?> result = categoryService.getById(id);
        return ResponseUtility.toResponse(result);
    }
    
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> add(@RequestBody CategoryDTO category) {
        Result<?> result = categoryService.add(category);
        return ResponseUtility.toResponse(result);
    }

    @PutMapping("{Id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> update(@PathVariable int id, @RequestBody CategoryDTO category) {
        Result<?> result = categoryService.update(id, category);
        return ResponseUtility.toResponse(result);
    }

    @DeleteMapping("{Id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> delete(@PathVariable int id) {
        Result<?> result = categoryService.delete(id);
        return ResponseUtility.toResponse(result);
    }
}
