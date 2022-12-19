/**
 * 
 */
package com.orbix.api.models;

import java.util.List;

import com.orbix.api.domain.Product;
import com.orbix.api.domain.ProductToProduct;

import lombok.Data;

/**
 * @author Godfrey
 *
 */
@Data
public class ProductToProductModel {
	Long id = null;
	String no = "";
	String status = "";
	String reason = "";
	String comments = "";
	String created = "";
	String approved = "";
	
	List<ProductToProductInitialModel> productToProductInitials;
	List<ProductToProductFinalModel> productToProductFinals;
}
