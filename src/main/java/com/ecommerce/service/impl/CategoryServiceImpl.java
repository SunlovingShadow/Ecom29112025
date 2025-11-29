package com.ecommerce.service.impl;

import com.ecommerce.exception.BadRequestException;
import com.ecommerce.exception.ResourceNotFoundException;
import com.ecommerce.model.Category;
import com.ecommerce.repository.CategoryRepository;
import com.ecommerce.service.CategoryService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CategoryServiceImpl implements CategoryService {

	private final CategoryRepository repo;

	public CategoryServiceImpl(CategoryRepository repo) {
		this.repo = repo;
	}

	@Override
	public Category create(Category c) {
		return repo.save(c);
	}

	@Override
	public List<Category> listActive() {
		return repo.findAllActive();
	}
	
	@Override
	public Category getById(Long id) {
		return repo.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Category not found"));
	}

	@Override
	@Transactional
	public void deleteSoft(Long id) {
		if (!repo.existsById(id))
			throw new ResourceNotFoundException("Category not found");

// Remove children FIRST (optional)
		List<Category> children = repo.findByParentCategoryId(id);
		if (!children.isEmpty())
			throw new BadRequestException("Cannot deactivate category: it has subcategories.");

		repo.updateActiveFlag(id, false);
	}

	@Override
	@Transactional
	public void activate(Long id) {
		if (!repo.existsById(id))
			throw new ResourceNotFoundException("Category not found");

		repo.updateActiveFlag(id, true);
	}
	
	@Override
	public void update(Category c) {
		if(!repo.existsById(c.getId()))
			throw new ResourceNotFoundException("Category not found");
		
		repo.update(c);
	}
}