/**
 * 
 */
package com.orbix.api.reports.service;

import java.time.LocalDate;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.stereotype.Service;

import com.orbix.api.reports.models.DebtTrackerReport;
import com.orbix.api.repositories.DebtTrackerRepository;
import com.orbix.api.repositories.GrnRepository;
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
public class DebtTrackerReportServiceImpl implements DebtTrackerReportService {

	private final DebtTrackerRepository debtTrackerRepository;
	
	@Override
	public List<DebtTrackerReport> getDebtTrackerReport(LocalDate from, LocalDate to) {
		// TODO Auto-generated method stub
		return debtTrackerRepository.getDebtTrackerReport(from, to);
	}

}
