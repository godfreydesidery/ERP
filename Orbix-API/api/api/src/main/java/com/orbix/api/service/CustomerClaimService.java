/**
 * 
 */
package com.orbix.api.service;

import java.util.List;

import com.orbix.api.domain.ClaimReplacementProduct;
import com.orbix.api.domain.ClaimedProduct;
import com.orbix.api.domain.CustomerClaim;
import com.orbix.api.models.ClaimReplacementModel;
import com.orbix.api.models.ClaimedProductModel;
import com.orbix.api.models.CustomerClaimModel;
import com.orbix.api.models.RecordModel;

/**
 * @author Godfrey
 *
 */
public interface CustomerClaimService {
	CustomerClaimModel save(CustomerClaim productToProduct);
	CustomerClaimModel get(Long id);
	CustomerClaimModel getByNo(String no);
	boolean delete(CustomerClaim productToProduct);
	List<CustomerClaimModel>getAllVisible();
	ClaimedProductModel saveClaimedProduct(ClaimedProduct claimedProduct);
	ClaimReplacementModel saveReplacement(ClaimReplacementProduct claimReplacementProduct);
	ClaimedProductModel getClaimedProduct(Long id);
	ClaimReplacementModel getReplacement(Long id);
	boolean deleteClaimedProduct(ClaimedProduct claimedProduct);
	boolean deleteReplacement(ClaimReplacementProduct claimReplacementProduct);
	List<ClaimedProductModel>getAllClaimedProducts(CustomerClaim customerClaim);
	List<ClaimReplacementModel>getAllReplacementProducts(CustomerClaim customerClaim);
	boolean archive(CustomerClaim customerClaim);
	boolean archiveAll();
	CustomerClaimModel approve(CustomerClaim customerClaim);
	RecordModel requestCCLNo();
}
