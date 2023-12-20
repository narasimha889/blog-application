package com.blog.controllers;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.blog.payloads.ApiResponse;
import com.blog.payloads.CategoryDto;
import com.blog.payloads.UserInt;
import com.blog.services.CategoryService;

import jakarta.validation.Valid;
@RestController
@RequestMapping("/api/v1/categories")
public class CategoryController {

	@Autowired
	private CategoryService categoryService;
	
	//Post - create Category
	@PostMapping("/")
	public ResponseEntity<CategoryDto> createCategory(@ Valid @RequestBody CategoryDto categoryDto){
		CategoryDto createCategoryDto = this.categoryService.createCategory(categoryDto);
		return new ResponseEntity<>(createCategoryDto,HttpStatus.CREATED);
	}
	//PUT- update category
	@PutMapping("/{categoryId}")
	public ResponseEntity<CategoryDto> updateCategory(@Valid @RequestBody CategoryDto CategoryDto,@PathVariable("categoryId") Integer cid){
		CategoryDto updateCategoryDto = this.categoryService.updateCategory(CategoryDto, cid);
		return ResponseEntity.ok(updateCategoryDto);
	}
	//Delete- delete category
	@DeleteMapping("/{categoryId}")
	public ResponseEntity<ApiResponse> deleteCategory(@PathVariable("categoryId") Integer cid){
		this.categoryService.deleteCategory(cid);
		return new ResponseEntity<ApiResponse>(new ApiResponse("category deleted successfully",true),HttpStatus.OK);
	}
	
	//Get all categories
	@GetMapping("/")
	public ResponseEntity<List<CategoryDto>> getAllCategories(){
		return ResponseEntity.ok(this.categoryService.getAllCategories());
	}
	//Get Single-User
	@GetMapping("/{categoryId}")
	public ResponseEntity<CategoryDto> getSingleCategory(@PathVariable("categoryId") Integer cid){
		return ResponseEntity.ok(this.categoryService.getCategoryByCategoryId(cid));
	}
	//check category by category Title
	@PostMapping("/categoryTitle")
	public String checkCategory(@RequestBody UserInt cat) {
		return this.categoryService.getCategoryByTitle(cat.getEmail());
	}
}
