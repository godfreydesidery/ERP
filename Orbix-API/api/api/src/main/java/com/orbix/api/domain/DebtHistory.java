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
 * @author Godfrey
 *
 */
@Entity
@Data  
@NoArgsConstructor 
@AllArgsConstructor
@Table(name = "debt_histories")
public class DebtHistory {
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;	
	@NotNull
	private double amount = 0;
	@NotNull
	private double paid = 0;
	@NotNull
	private double balance = 0;
	
	private Long createdBy;	
	private Long createdAt;
	
	private String reference;
	
	@ManyToOne(targetEntity = Debt.class, fetch = FetchType.EAGER, optional = true)
    @JoinColumn(name = "debt_id", nullable = true , updatable = true)
    @OnDelete(action = OnDeleteAction.NO_ACTION)	
    private Debt debt;
	
	@ManyToOne(targetEntity = DebtTracker.class, fetch = FetchType.EAGER, optional = true)
    @JoinColumn(name = "debt_tracker_id", nullable = true , updatable = true)
    @OnDelete(action = OnDeleteAction.NO_ACTION)	
    private DebtTracker debtTracker;
	
	@ManyToOne(targetEntity = Day.class, fetch = FetchType.EAGER, optional = true)
    @JoinColumn(name = "day_id", nullable = true , updatable = false)
    @OnDelete(action = OnDeleteAction.NO_ACTION)	
    private Day day;	
}
