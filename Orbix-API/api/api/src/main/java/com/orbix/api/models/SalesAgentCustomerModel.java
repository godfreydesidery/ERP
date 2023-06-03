/**
 * 
 */
package com.orbix.api.models;

import com.orbix.api.domain.SalesAgent;
import lombok.Data;

/**
 * @author Godfrey
 *
 */
@Data
public class SalesAgentCustomerModel {
	Long id = null;	
	String name = "";
	String location = "";
	String mobile = "";
	SalesAgent salesAgent = null;
}