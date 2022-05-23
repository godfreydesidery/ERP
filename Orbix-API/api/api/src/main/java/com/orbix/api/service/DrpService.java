/**
 * 
 */
package com.orbix.api.service;

import java.util.List;

import com.orbix.api.domain.Drp;
import com.orbix.api.domain.DrpDetail;
import com.orbix.api.models.DrpDetailModel;
import com.orbix.api.models.DrpModel;
import com.orbix.api.models.GrnModel;
import com.orbix.api.models.RecordModel;

/**
 * @author Godfrey
 *
 */
public interface DrpService {
	DrpModel save(Drp drp);
	DrpModel get(Long id);
	DrpModel getByNo(String no);
	boolean delete(Drp drp);
	List<DrpModel>getAllVisible();
	DrpDetailModel saveDetail(DrpDetail drpDetail);
	DrpDetailModel getDetail(Long id);
	boolean deleteDetail(DrpDetail drpDetail);
	List<DrpDetailModel>getAllDetails(Drp drp);	
	boolean archive(Drp drp);
	boolean archiveAll();
	RecordModel requestDrpNo();
	
	GrnModel approve(Drp drp);
}
