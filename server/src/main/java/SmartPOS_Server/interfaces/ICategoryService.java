package SmartPOS_Server.interfaces;

import SmartPOS_Server.dtos.CategoryDTO;
import SmartPOS_Server.models.Category;

import java.util.List;

public interface ICategoryService {

    List<Category> getAllCategories();

    Category getCategoryById(String id);

    Category addCategory(CategoryDTO categoryDTO);

    Category updateCategory(String id, CategoryDTO categoryDTO);

    void deleteCategory(String id);
}