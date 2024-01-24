package com.unitas.alm.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface TestMapper {

	/**
	 * 금리GAP분석 조회
	 * @return
	 * @throws Exception
	 */
	List<Map<String, Object>> selectTr2101List(Map<String, Object> data) throws Exception;

	/**
	 * 자금량 시나리오 코드 조회
	 * @return
	 * @throws Exception
	 */
	List<Map<String, Object>> selectCapQtySnroNo() throws Exception;
	
	

	List<Map<String,Object>> employeeList( );
}
