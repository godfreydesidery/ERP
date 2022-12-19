/**
 * 
 */
package com.orbix.api.domain;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Godfrey
 *
 */
@Entity
@Data  
@NoArgsConstructor 
@AllArgsConstructor
@Table(name = "product_to_products")
public class ProductToProduct {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@NotBlank
	@Column(unique = true)
	private String no;
	private String reason;
	private String status;
	private String comments;
	
	private Long createdBy;
	private Long createdAt;
	private Long approvedBy;
	private Long approvedAt;
		
	@OneToMany(targetEntity = ProductToProductInitial.class, mappedBy = "productToProduct", fetch = FetchType.LAZY, orphanRemoval = true)
    @Valid
    @JsonIgnoreProperties("productToProduct")
    private List<ProductToProductInitial> productToProductInitials;
	
	@OneToMany(targetEntity = ProductToProductFinal.class, mappedBy = "productToProduct", fetch = FetchType.LAZY, orphanRemoval = true)
    @Valid
    @JsonIgnoreProperties("productToProduct")
    private List<ProductToProductFinal> productToProductFinals;
}
