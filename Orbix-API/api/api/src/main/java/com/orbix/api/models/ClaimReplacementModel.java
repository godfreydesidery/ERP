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
	double qty = 0;
	String remarks = "";
	Product product;
    CustomerClaim customerClaim;	
}
