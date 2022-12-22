/**
 * 
 */
package com.orbix.api.models;

import java.util.List;

import com.orbix.api.domain.Customer;

import lombok.Data;

/**
 * @author Godfrey
 *
 */
@Data
public class CustomerClaimModel {
	Long id = null;
	String no = "";
	String status = "";
	String reason = "";
	String comments = "";
	String created = "";
	String approved = "";
	Customer customer = null;
	
	List<ClaimedProductModel> claimedProducts;
	List<ClaimReplacementModel> replacementProducts;
}
