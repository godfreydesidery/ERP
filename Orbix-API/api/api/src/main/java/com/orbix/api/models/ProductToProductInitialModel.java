/**
 * 
 */
package com.orbix.api.models;

import com.orbix.api.domain.Product;
import com.orbix.api.domain.ProductToProduct;
import lombok.Data;

/**
 * @author Godfrey
 *
 */
@Data
public class ProductToProductInitialModel {
	Long id;
	double qty = 0;
	double costPriceVatIncl = 0;
	double costPriceVatExcl = 0;
	double sellingPriceVatIncl = 0;
	double sellingPriceVatExcl = 0;	
    ProductToProduct productToProduct;	
    Product product;
}
