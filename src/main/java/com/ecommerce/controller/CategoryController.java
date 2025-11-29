package com.ecommerce.controller;

import com.ecommerce.dto.ApiResponse;
import com.ecommerce.model.Category;
import com.ecommerce.service.CategoryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {

	private final CategoryService service;

	public CategoryController(CategoryService service) {
		this.service = service;
	}

	@PostMapping
	public ResponseEntity<ApiResponse<Category>> create(@RequestBody Category c) {
		return ResponseEntity.ok(new ApiResponse<>(true, "Created", service.create(c)));
	}

	@GetMapping
	public ResponseEntity<ApiResponse<?>> listActive() {
		return ResponseEntity.ok(new ApiResponse<>(true, "OK", service.listActive()));
	}
	
	@GetMapping("/{id}")
	public ResponseEntity<ApiResponse<Category>> getById(@PathVariable Long id){
		Category c = service.getById(id);
		return ResponseEntity.ok(new ApiResponse<> (true, "OK", c));
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<ApiResponse<Void>> deleteSoft(@PathVariable Long id) {
		service.deleteSoft(id);
		return ResponseEntity.ok(new ApiResponse<>(true, "Category deactivated"));
	}

	@PutMapping("/{id}/activate")
	public ResponseEntity<ApiResponse<Void>> activate(@PathVariable Long id) {
		service.activate(id);
		return ResponseEntity.ok(new ApiResponse<>(true, "Category activated"));
	}
	
	@PutMapping("/{id}")
	public ResponseEntity<ApiResponse<Void>> update(
			@PathVariable Long id,
			@RequestBody Category c){
		
		c.setId(id);
		service.update(c);
		
		return ResponseEntity.ok(new ApiResponse<>(true, "Category updated"));
	}
}