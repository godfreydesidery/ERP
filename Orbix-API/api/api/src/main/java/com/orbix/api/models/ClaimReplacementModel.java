/**
 * 
 */
package com.orbix.api.models;

import com.orbix.api.domain.CustomerClaim;
import com.orbix.api.domain.Product;

import lombok.Data;

/**
 * @author Godfrey
 *
 */
@Data
public class ClaimReplacementModel {
	Long id;
	double costPriceVatIncl = 0;
	double costPriceVatExcl  = 0;
	double sellingPriceVatIncl = 0;
	double sellingPriceVatExcl = 0;
	double qty = 0;
	String remarks = "";
	Product product;
    CustomerClaim customerClaim;	
}
