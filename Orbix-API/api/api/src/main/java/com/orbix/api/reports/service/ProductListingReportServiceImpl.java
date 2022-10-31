/**
 * 
 */
package com.orbix.api.reports.service;

import java.time.LocalDate;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.stereotype.Service;

import com.orbix.api.reports.models.ProductListingReport;
import com.orbix.api.repositories.ProductionRepository;
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
public class ProductListingReportServiceImpl implements ProductListingReportService {
	
	private final SaleRepository saleRepository;
	
	@Override
	public List<ProductListingReport> getProductListingReport(LocalDate from, LocalDate to, String tillNo) {
		if(tillNo == null) {
			return saleRepository.getProductListingReport(from, to);
		}else {
			return saleRepository.getProductListingReportByTill(from, to, tillNo);
		}	
	}

}
