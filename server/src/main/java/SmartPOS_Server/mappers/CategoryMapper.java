package SmartPOS_Server.mappers;

import SmartPOS_Server.dtos.CategoryDTO;
import SmartPOS_Server.models.Category;
import org.springframework.stereotype.Component;

@Component
public class CategoryMapper {

    public Category toEntity(CategoryDTO dto) {

        Category category = new Category();

        category.setName(dto.name());
        category.setDescription(dto.description());
        category.setImageUrl(dto.imageUrl());

        return category;
    }

    public void updateEntity(Category category, CategoryDTO dto) {

        category.setName(dto.name());
        category.setDescription(dto.description());
        category.setImageUrl(dto.imageUrl());
    }
}