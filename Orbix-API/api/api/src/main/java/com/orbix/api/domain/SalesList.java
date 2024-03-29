/**
 * 
 */
package com.orbix.api.domain;

import java.time.LocalDate;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author GODFREY
 *
 */
@Entity
@Data  
@NoArgsConstructor 
@AllArgsConstructor
@Table(name = "sales_lists")
public class SalesList {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@NotBlank
	@Column(unique = true)
	private String no;
	private LocalDate issueDate;
	private String status;
	private String comments;
	
	private Long createdBy;
	private Long createdAt;
	private Long approvedBy;
	private Long approvedAt;
	private Long postedBy;
	private Long postedAt;
	
	private double totalReturns = 0;
	private double totalDamages = 0;
	private double totalDiscounts = 0;
	private double totalExpenditures = 0;
	private double totalCommission = 0;
	private double totalBank = 0;
	private double totalCash = 0;
	private double totalOther = 0;
	private double totalDeficit = 0;
	
	@ManyToOne(targetEntity = Customer.class, fetch = FetchType.EAGER, optional = true)
    @JoinColumn(name = "customer_id", nullable = true , updatable = true)
    @OnDelete(action = OnDeleteAction.NO_ACTION)	
    private Customer customer;
	
	@ManyToOne(targetEntity = SalesAgent.class, fetch = FetchType.EAGER, optional = true)
    @JoinColumn(name = "sales_agent_id", nullable = false , updatable = true)
    @OnDelete(action = OnDeleteAction.NO_ACTION)	
    private SalesAgent salesAgent;
	
	@ManyToOne(targetEntity = PackingList.class, fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = "packing_list_id", nullable = true , updatable = true)
    @OnDelete(action = OnDeleteAction.NO_ACTION)	
    private PackingList packingList;
	
	@OneToMany(targetEntity = SalesListDetail.class, mappedBy = "salesList", fetch = FetchType.EAGER, orphanRemoval = true)
    @Valid
    @JsonIgnoreProperties("salesList")
    private List<SalesListDetail> salesListDetails;
}
