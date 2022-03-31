/**
 * 
 */
package com.orbix.api.reports.service;

import java.time.LocalDate;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.stereotype.Service;

import com.orbix.api.reports.models.SlowMovingProductsReport;
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
public class SlowMovingProductsReportServiceImpl implements SlowMovingProductsReportService {

	private final SaleRepository saleRepository;
	
	@Override
	public List<SlowMovingProductsReport> getSlowMovingProductsReport(LocalDate from, LocalDate to) {
		return saleRepository.getSlowMovingProducts(from, to);
	}

}
