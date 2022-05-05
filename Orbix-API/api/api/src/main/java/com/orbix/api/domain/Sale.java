/**
 * 
 */
package com.orbix.api.domain;

import java.util.List;

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
@Table(name = "sales")
public class Sale {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	private Long createdBy;
	private Long createdAt;
	
	private String type = "Cash"; // Cash or Credit
	private String reference;
	
	@ManyToOne(targetEntity = Day.class, fetch = FetchType.EAGER, optional = true)
    @JoinColumn(name = "day_id", nullable = true , updatable = true)
    @OnDelete(action = OnDeleteAction.NO_ACTION)	
    private Day day;
	
	@ManyToOne(targetEntity = Till.class, fetch = FetchType.EAGER, optional = true)
    @JoinColumn(name = "till_id", nullable = true , updatable = true)
    @OnDelete(action = OnDeleteAction.NO_ACTION)	
    private Till till;
	
	
	
	
	
	
	
	@ManyToOne(targetEntity = Receipt.class, fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = "receipt_id", nullable = true , updatable = true)
    @OnDelete(action = OnDeleteAction.NO_ACTION)	
    private Receipt receipt;
	
	
	@ManyToOne(targetEntity = SalesInvoice.class, fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = "sales_invoice_id", nullable = true , updatable = true)
    @OnDelete(action = OnDeleteAction.NO_ACTION)	
    private SalesInvoice salesInvoice;
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	@OneToMany(targetEntity = SaleDetail.class, mappedBy = "sale", fetch = FetchType.EAGER, orphanRemoval = true)
    @Valid
    @JsonIgnoreProperties("sale")
    private List<SaleDetail> saleDetails;
}
