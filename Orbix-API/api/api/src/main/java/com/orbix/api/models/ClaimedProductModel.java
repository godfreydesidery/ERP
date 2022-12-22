/**
 * 
 */
package com.orbix.api.models;

import com.orbix.api.domain.CustomerClaim;
import com.orbix.api.domain.Product;
import com.orbix.api.domain.ProductToProduct;

import lombok.Data;

/**
 * @author Godfrey
 *
 */
@Data
public class ClaimedProductModel {
	Long id;
	double qty = 0;
	String reason = "";
	String remarks = "";
	Product product;
    CustomerClaim customerClaim;	
}
