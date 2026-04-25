package com.example.venuva.Core.ServiceLayer;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.venuva.Core.Domain.Abstractions.Error;
import com.example.venuva.Core.Domain.Abstractions.Result;
import com.example.venuva.Core.Domain.Models.EventModule.Category;
import com.example.venuva.Infrastructure.PresistenceLayer.Repos.CategoryRepository;
import com.example.venuva.Shared.Dtos.CategoryDTO;

@Service
public class CategoryService {
    @Autowired
    private CategoryRepository categoryRepository;

    public Result<List<CategoryDTO>> getAll() {
        return Result.success(categoryRepository.findAll().stream().map(category -> {
            CategoryDTO dto = new CategoryDTO();
            dto.setId(category.getId());
            dto.setName(category.getName());
            return dto;
        }).toList());
    }
    
    public Result<Category> getById(int id) {
        return categoryRepository.findById(id).map(Result::success).orElse(Result.failure(new Error(null, "Category not found")));
    }

    public Result<CategoryDTO> add(CategoryDTO category) {
        Category newCategory = new Category();
        newCategory.setName(category.getName());

        categoryRepository.save(newCategory);
        return Result.success(category);
    }

    public Result<Boolean> update(int id, CategoryDTO category) {
        Category existingCategory = categoryRepository.findById(id).orElse(null);
        if (existingCategory == null) {
            return Result.failure(new Error(null, "Category not found"));
        }

        existingCategory.setName(category.getName());
        categoryRepository.save(existingCategory);
        return Result.success(true);
    }

    public Result<Boolean> delete(int id) {
        Category existingCategory = categoryRepository.findById(id).orElse(null);
        if (existingCategory == null) {
            return Result.failure(new Error(null, "Category not found"));
        }

        categoryRepository.delete(existingCategory);
        return Result.success(true);
    }
}
