/**
 * 
 */
package com.orbix.api.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;

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
@Table(name = "sales_agents")
public class SalesAgent {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@NotBlank
	@Column(unique = true)
	private String no;
	@NotBlank
	@Column(unique = true)
	private String name;
	@NotBlank
	private String contactName;
	private String alias;
	private boolean active = true;
	private double creditLimit = 0;
	private double invoiceLimit = 0;
	private double balance = 0;
	private double salesTarget = 0;
	private int creditDays = 1;
	private String physicalAddress;
	private String postCode;
	private String postAddress;
	private String telephone;
	private String mobile;
	private String email;
	private String fax;
	
	private String passName;
	private String passCode;
}
