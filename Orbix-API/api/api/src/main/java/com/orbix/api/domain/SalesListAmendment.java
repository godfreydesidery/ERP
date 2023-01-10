/**
 * 
 */
package com.orbix.api.domain;

import java.time.LocalDate;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

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
@Table(name = "sales_list_amendments")
public class SalesListAmendment {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private double originalQty = 0;
	private double finalQty = 0;
	private String reference;
	private Long amendedBy;
	private Long amendedAt;
	
	@ManyToOne(targetEntity = Product.class, fetch = FetchType.EAGER,  optional = true)
    @JoinColumn(name = "product_id", nullable = true , updatable = true)
    @OnDelete(action = OnDeleteAction.NO_ACTION)	
    private Product product;
	
	@ManyToOne(targetEntity = SalesList.class, fetch = FetchType.EAGER, optional = true)
    @JoinColumn(name = "sales_list_id", nullable = true , updatable = true)
    @OnDelete(action = OnDeleteAction.NO_ACTION)	
    private SalesList salesList;
}
