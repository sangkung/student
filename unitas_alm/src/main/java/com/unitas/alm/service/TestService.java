package com.unitas.alm.service;

import java.util.List;
import java.util.Map;

public interface TestService {
	
	/**
	 * 금리GAP분석 조회
	 * @param searchDataset
	 * @return
	 * @throws Exception
	 */
	public List<Map<String,Object>> selectTr2101List(Map<String, Object> searchDataset) throws Exception;

	/**
	 * 자금량 시나리오 코드 조회
	 * @return
	 * @throws Exception
	 */
	public List<Map<String, Object>> selectCapQtySnroNo() throws Exception;


	public List<Map<String,Object>> employeeList( );
	
}
