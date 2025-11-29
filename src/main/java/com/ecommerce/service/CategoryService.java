package com.ecommerce.service;

import com.ecommerce.model.Category;
import java.util.List;

public interface CategoryService {

	Category create(Category c);

	List<Category> listActive();

	void deleteSoft(Long id);

	void activate(Long id);
	
	void update(Category c);
	
	Category getById(Long id);
}