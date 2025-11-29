package com.ecommerce.repository;

import com.ecommerce.model.Category;
import java.util.List;
import java.util.Optional;

public interface CategoryRepository {

	Category save(Category c);

	boolean existsById(Long id);

	Optional<Category> findById(Long id);

	List<Category> findAllActive();

	List<Category> findByParentCategoryId(Long parentId);

	boolean updateActiveFlag(Long id, boolean active);

	boolean update(Category c);
}