/**
 * 
 */
package com.orbix.api.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import com.sun.istack.NotNull;

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
@Table(name = "debts")
public class Debt {
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
	@NotBlank
	@Column(unique = true)
	private String no;
	private String status;
	@NotNull
	private double amount = 0;
	@NotNull
	private double balance = 0;
	
	@ManyToOne(targetEntity = SalesAgent.class, fetch = FetchType.EAGER, optional = true)
    @JoinColumn(name = "sales_agent_id", nullable = true , updatable = true)
    @OnDelete(action = OnDeleteAction.NO_ACTION)	
    private SalesAgent salesAgent;
	
	@ManyToOne(targetEntity = Day.class, fetch = FetchType.EAGER, optional = true)
    @JoinColumn(name = "day_id", nullable = true , updatable = false)
    @OnDelete(action = OnDeleteAction.NO_ACTION)	
    private Day day;
	
	@OneToOne(targetEntity = SalesList.class, fetch = FetchType.EAGER, optional = true)
    @JoinColumn(name = "sales_list_id", nullable = true , updatable = false)
    @OnDelete(action = OnDeleteAction.NO_ACTION)	
    private SalesList salesList;
}
