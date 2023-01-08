/**
 * 
 */
package com.orbix.api.service;

import javax.transaction.Transactional;

import org.springframework.stereotype.Service;

import com.orbix.api.accessories.Formater;
import com.orbix.api.domain.SalesSheet;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Godfrey
 *
 */
@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class SalesSheetServiceImpl implements SalesSheetService{
	
	@Override
	public String generateSalesSheetNo(SalesSheet salesSheet) {
		Long number = salesSheet.getId();		
		String sNumber = number.toString();
		return Formater.formatWithCurrentDate("SSH",sNumber);
	}
}
