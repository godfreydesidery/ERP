/**
 * 
 */
package com.orbix.api.reports.service;

import java.time.LocalDate;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.stereotype.Service;

import com.orbix.api.reports.models.ProductStockCardReport;
import com.orbix.api.repositories.CategoryRepository;
import com.orbix.api.repositories.ClassRepository;
import com.orbix.api.repositories.DayRepository;
import com.orbix.api.repositories.DepartmentRepository;
import com.orbix.api.repositories.LevelFourRepository;
import com.orbix.api.repositories.LevelOneRepository;
import com.orbix.api.repositories.LevelThreeRepository;
import com.orbix.api.repositories.LevelTwoRepository;
import com.orbix.api.repositories.ProductStockCardRepository;
import com.orbix.api.repositories.SaleDetailRepository;
import com.orbix.api.repositories.SaleRepository;
import com.orbix.api.repositories.SubClassRepository;
import com.orbix.api.repositories.SupplierRepository;

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
public class ProductStockCardReportServiceImpl implements ProductStockCardReportService {

	private final ProductStockCardRepository productStockCardRepository;
	
	@Override
	public List<ProductStockCardReport> getProductStockCardReport(LocalDate from, LocalDate to) {
		return productStockCardRepository.getStockCardReport(from, to);
	}

}
