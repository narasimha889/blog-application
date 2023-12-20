package com.blog.payloads;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Data
@NoArgsConstructor
@Getter
@Setter
public class CategoryDto {

	private Integer categoryId;
	@NotBlank
	@Size(min = 4,message="Category Title must contain min of 4 characters !!")
	private String categoryTitle;
	@NotBlank
	@Size(min = 10,message="Category Description must be min of 10 characters !!")
	private String categoryDescription;
}
