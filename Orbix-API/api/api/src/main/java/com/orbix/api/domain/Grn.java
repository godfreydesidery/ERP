/**
 * 
 */
package com.orbix.api.domain;

import java.time.LocalDate;
import java.util.List;

import javax.persistence.CascadeType;
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
@Table(name = "grns")
public class Grn {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@NotBlank
	@Column(unique = true)
	private String no;
	@Column(unique = true)
	private String orderNo;
	private String refNo;
	private double invoiceAmount = 0;
	private String invoiceNo;	
	private String status;	
	private String comments;
		
	private Long createdBy;
	private Long createdAt;
	private Long approvedBy;	
	private Long approvedAt;
	
	@ManyToOne(targetEntity = Lpo.class, fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = "lpo_id", nullable = true , updatable = true)
    @OnDelete(action = OnDeleteAction.NO_ACTION)
	@JsonIgnoreProperties("lpoDetails")
    private Lpo lpo;
	
	@ManyToOne(targetEntity = Drp.class, fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = "drp_id", nullable = true , updatable = true)
    @OnDelete(action = OnDeleteAction.NO_ACTION)
	@JsonIgnoreProperties("drpDetails")
    private Drp drp;
	
	@OneToMany(targetEntity = GrnDetail.class, mappedBy = "grn", fetch = FetchType.EAGER, orphanRemoval = true, cascade = CascadeType.ALL)
    @Valid
    @JsonIgnoreProperties("grn")
    private List<GrnDetail> grnDetails;
	
}
