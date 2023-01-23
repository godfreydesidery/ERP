/**
 * 
 */
package com.orbix.api.models;

import java.util.List;

import com.orbix.api.domain.Day;
import com.orbix.api.domain.Product;
import com.orbix.api.domain.Till;

import lombok.Data;

/**
 * @author Godfrey
 *
 */
@Data
public class WMSSalesModel {
	Long id = null;	
	String no = "";
	String salesListNo = "";
	String customerName = "";	
	String customerMobile = "";
	String customerLocation = "";	
    List<WMSProductModel> products;
}
