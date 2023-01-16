/**
 * 
 */
package com.orbix.api.domain;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

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
@Table(name = "material_to_material_finals")
public class MaterialToMaterialFinal {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@NotNull
	private double qty = 0;
	private double costPriceVatIncl = 0;
	private double costPriceVatExcl = 0;
	
	@ManyToOne(targetEntity = Material.class, fetch = FetchType.EAGER,  optional = true)
    @JoinColumn(name = "material_id", nullable = true , updatable = true)
    @OnDelete(action = OnDeleteAction.NO_ACTION)	
    private Material material;
	
	@ManyToOne(targetEntity = MaterialToMaterial.class, fetch = FetchType.EAGER,  optional = true)
    @JoinColumn(name = "material_to_material_id", nullable = true , updatable = true)
    @OnDelete(action = OnDeleteAction.NO_ACTION)	
    private MaterialToMaterial materialToMaterial;
}
