/**
 * 
 */
package com.orbix.api.reports.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.stereotype.Service;

import com.orbix.api.domain.Category;
import com.orbix.api.domain.Class;
import com.orbix.api.domain.Department;
import com.orbix.api.domain.LevelFour;
import com.orbix.api.domain.LevelOne;
import com.orbix.api.domain.LevelThree;
import com.orbix.api.domain.LevelTwo;
import com.orbix.api.domain.SubCategory;
import com.orbix.api.domain.SubClass;
import com.orbix.api.reports.models.DailyPurchaseReport;
import com.orbix.api.reports.models.DailySalesReport;
import com.orbix.api.repositories.ProductStockCardRepository;
import com.orbix.api.repositories.PurchaseRepository;
import com.orbix.api.repositories.SaleRepository;

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
public class PurchaseReportServiceImpl implements PurchaseReportService {
	
	private final PurchaseRepository purchaseRepository;
	
	@Override
	public List<DailyPurchaseReport> getDailyPurchaseReport(
			LocalDate fromDate, 
			LocalDate toDate, 
			Department department, 
			Class clas,
			SubClass subClass, 
			Category category, 
			SubCategory subCategory, 
			List<LevelOne> levelOnes,
			List<LevelTwo> levelTwos, 
			List<LevelThree> levelThrees, 
			List<LevelFour> levelFours) {
		
		List<DailyPurchaseReport> report = new ArrayList<>();
		if(department == null && category == null && levelOnes == null && levelTwos == null && levelThrees == null && levelFours == null) {
			
		}
		return purchaseRepository.getDailyPurchaseReport(fromDate, toDate);
	}
}
