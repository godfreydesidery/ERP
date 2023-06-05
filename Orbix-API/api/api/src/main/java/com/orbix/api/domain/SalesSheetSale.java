/**
 * 
 */
package com.orbix.api.domain;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;
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
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Godfrey
 *
 */
@Entity
@Data  
@NoArgsConstructor 
@AllArgsConstructor
@Table(name = "sales_sheet_sales")
public class SalesSheetSale {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@NotBlank
	@Column(unique = true)
	private String no;
	private double totalAmount = 0;
	private double totalDiscount = 0;
	private double totalCharges = 0;
	private double totalPaid = 0;
	private double totalDue = 0;
	
	private String customerName = "";
	private String customerMobile = "";
	private String customerLocation = "";	
	
	private LocalDateTime completedAt = LocalDateTime.now();
	
	@ManyToOne(targetEntity = SalesSheet.class, fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = "sales_sheet_id", nullable = true , updatable = true)
    @OnDelete(action = OnDeleteAction.NO_ACTION)	
    private SalesSheet salesSheet;
	
	@OneToMany(targetEntity = SalesSheetSaleDetail.class, mappedBy = "salesSheetSale", fetch = FetchType.EAGER, orphanRemoval = true)
    @Valid
    @JsonIgnoreProperties("salesSheetSale")
    private List<SalesSheetSaleDetail> salesSheetSaleDetails;
}
