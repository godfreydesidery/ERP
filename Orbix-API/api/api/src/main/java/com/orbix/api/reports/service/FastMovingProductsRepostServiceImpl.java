/**
 * 
 */
package com.orbix.api.reports.service;

import java.time.LocalDate;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.stereotype.Service;

import com.orbix.api.reports.models.FastMovingProductsReport;
import com.orbix.api.repositories.ProductRepository;
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
public class FastMovingProductsRepostServiceImpl implements FastMovingProductsReportService {
	
	private final SaleRepository saleRepository;

	@Override
	public List<FastMovingProductsReport> getFastMovingProductsReport(LocalDate from, LocalDate to) {
		return saleRepository.getFastMovingProducts(from, to);
	}

}
