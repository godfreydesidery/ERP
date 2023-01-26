/**
 * 
 */
package com.orbix.api.service;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.orbix.api.domain.SalesList;
import com.orbix.api.domain.SalesListDetail;
import com.orbix.api.domain.SalesSheet;
import com.orbix.api.models.SalesListDetailModel;
import com.orbix.api.models.SalesListModel;
import com.orbix.api.models.SalesSheetModel;
import com.orbix.api.models.SalesSheetSaleDetailModel;

/**
 * @author Godfrey
 *
 */
public interface SalesSheetService {
	
	SalesSheetModel get(Long id);
	SalesSheetModel getByNo(String no);	
	
	String generateSalesSheetNo(SalesSheet salesSheet);
}
