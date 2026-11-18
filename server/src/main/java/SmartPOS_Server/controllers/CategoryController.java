package SmartPOS_Server.controllers;

import SmartPOS_Server.dtos.CategoryDTO;
import SmartPOS_Server.interfaces.ICategoryService;
import SmartPOS_Server.models.Category;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
@CrossOrigin(origins = "http://localhost:4200")
public class CategoryController {

    private final ICategoryService categoryService;

    public CategoryController(ICategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping
    public List<Category> getAllCategories() {
        return categoryService.getAllCategories();
    }

    @GetMapping("/{id}")
    public Category getCategoryById(@PathVariable String id) {
        return categoryService.getCategoryById(id);
    }

    @PostMapping
    public Category addCategory(@Valid @RequestBody CategoryDTO category) {
        return categoryService.addCategory(category);
    }

    @PutMapping("/{id}")
    public Category updateCategory(
            @PathVariable String id,
            @Valid @RequestBody CategoryDTO category) {

        return categoryService.updateCategory(id, category);
    }

    @DeleteMapping("/{id}")
    public void deleteCategory(@PathVariable String id) {
        categoryService.deleteCategory(id);
    }
}