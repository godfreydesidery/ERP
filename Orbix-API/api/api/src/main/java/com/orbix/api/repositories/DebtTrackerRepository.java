/**
 * 
 */
package com.orbix.api.repositories;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.orbix.api.domain.Debt;
import com.orbix.api.domain.DebtTracker;
import com.orbix.api.domain.SalesAgent;
import com.orbix.api.domain.SalesList;
import com.orbix.api.reports.models.DebtTrackerReport;
import com.orbix.api.reports.models.SalesSummaryReport;

/**
 * @author Godfrey
 *
 */
public interface DebtTrackerRepository extends JpaRepository<DebtTracker, Long> {
	
	/**
	 * @param customer
	 * @param string
	 * @return
	 */
	//@Query("SELECT d FROM Debt d WHERE d.salesAgent =:salesAgent AND d.status IN (:statuses)")
	//List<Debt> findBySalesAgentAndPendingOrPartial(SalesAgent salesAgent, List<String> statuses);
	
	@Query("SELECT MAX(d.id) FROM DebtTracker d")
	Long getLastId();
	
	@Query("SELECT d FROM DebtTracker d WHERE d.status =:status")
	List<DebtTracker> findAllPaid(String status);
	
	@Query("SELECT d FROM DebtTracker d WHERE d.status IN (:statuses)")
	List<DebtTracker> findAllVisible(List<String> statuses);
	
	
	@Query(
			value = "SELECT\r\n" + 
					"`days`.`bussiness_date` AS `inceptionDate`,\r\n" +
					"`customers`.`name` AS `customerName`,\r\n" +
					"`sales_agents`.`name` AS `officerIncharge`,\r\n" +
					"`debt_trackers`.`id` AS `id`,\r\n" +
					"`debt_trackers`.`no` AS `no`,\r\n" +
					"`debt_trackers`.`amount` AS `totalAmount`,\r\n" +
					"`debt_trackers`.`paid` AS `amountPaid`,\r\n" +
					"`debt_trackers`.`balance` AS `balance`,\r\n" +
					"`debt_trackers`.`status` AS `status`\r\n" +
					"FROM\r\n" + 
					"(SELECT * FROM `days` WHERE `bussiness_date` BETWEEN :from AND :to)`days`\r\n" + 
					"JOIN\r\n" + 
					"`debt_trackers`\r\n" + 
					"ON\r\n" + 
					"`days`.`id`=`debt_trackers`.`inception_day`\r\n" + 
					"JOIN\r\n" + 
					"`customers`\r\n" + 
					"ON\r\n" + 
					"`customers`.`id`=`debt_trackers`.`customer_id`\r\n" + 
					"JOIN\r\n" + 
					"`sales_agents`\r\n" + 
					"ON\r\n" + 
					"`sales_agents`.`id`=`debt_trackers`.`sales_agent_id`\r\n",
					nativeQuery = true					
			)
	List<DebtTrackerReport> getDebtTrackerReport(LocalDate from, LocalDate to);
}
