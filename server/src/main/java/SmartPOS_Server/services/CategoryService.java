
package SmartPOS_Server.services;

import SmartPOS_Server.dtos.CategoryDTO;
import SmartPOS_Server.exceptions.ResourceNotFoundException;
import SmartPOS_Server.mappers.CategoryMapper;
import SmartPOS_Server.models.Category;
import SmartPOS_Server.repositories.CategoryRepository;
import SmartPOS_Server.repositories.ProductRepository;
import SmartPOS_Server.interfaces.ICategoryService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryService implements ICategoryService {

    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;
    private final CategoryMapper categoryMapper;

    public CategoryService(
            CategoryRepository categoryRepository,
            ProductRepository productRepository,
            CategoryMapper categoryMapper) {

        this.categoryRepository = categoryRepository;
        this.productRepository = productRepository;
        this.categoryMapper = categoryMapper;
    }

    @Override
    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    @Override
    public Category getCategoryById(String id) {
        return categoryRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("הקטגוריה לא נמצאה"));
    }

    @Override
    public Category addCategory(CategoryDTO categoryDTO) {
        checkDuplicateName(categoryDTO.name(), null);

        Category category = categoryMapper.toEntity(categoryDTO);

        return categoryRepository.save(category);
    }

    @Override
    public Category updateCategory(String id, CategoryDTO categoryDTO) {
        Category existingCategory = getCategoryById(id);

        checkDuplicateName(categoryDTO.name(), id);

        categoryMapper.updateEntity(existingCategory, categoryDTO);

        return categoryRepository.save(existingCategory);
    }

    @Override
    public void deleteCategory(String id) {
        Category category = getCategoryById(id);

        if (!productRepository.findByCategoryId(id).isEmpty()) {
            throw new IllegalArgumentException(
                    "לא ניתן למחוק קטגוריה שמשויכים אליה מוצרים"
            );
        }

        categoryRepository.delete(category);
    }

    private void checkDuplicateName(String name, String currentId) {
        categoryRepository.findByNameIgnoreCase(name)
                .filter(category -> !category.getId().equals(currentId))
                .ifPresent(category -> {
                    throw new IllegalArgumentException(
                            "כבר קיימת קטגוריה בשם הזה"
                    );
                });
    }
}
