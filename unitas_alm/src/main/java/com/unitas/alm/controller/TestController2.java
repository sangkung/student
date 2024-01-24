package com.unitas.alm.controller;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.configurationprocessor.json.JSONArray;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.unitas.alm.Data.TestData;
import com.unitas.alm.service.TestService;

import jakarta.servlet.http.HttpServletRequest;



@Controller
public class TestController2 {
	
	private final Logger log = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	private TestService service;


	@GetMapping("/tree2")
	public String tree2(Model model, TestData gridForm) {
		log.info("그리드 테스트 화면으로 이동");
		
		//오늘 일자
		Date dNow = new Date( );
	    SimpleDateFormat ft = new SimpleDateFormat ("yyyy-MM-dd");
	    String nowDate = ft.format(dNow);
	    log.info("Current Date: " + nowDate);
	    
	    //초기값 셋팅
	    //gridForm.setBase_date(nowDate);
	    gridForm.setBase_date("2023-08-31"); //기준년월
	    gridForm.setBr_c("120"); //점소
	    gridForm.setMo_br_c("120"); //모점소
	    gridForm.setAc_busi_tc("2102"); //회계사업구분
	    gridForm.setNew_cap_qty_tc("0"); //미래시점 해당없음(0) 포함(1) 신규(2)
	    gridForm.setCs_bhor_refl_yn("N");//고객행동
	    gridForm.setLevel("4"); //트리레벨
	    gridForm.setInt_yn(false); //이자포함
	    gridForm.setSub_yn(false); //지소포함
	    gridForm.setAmt_unit("1000000"); //금액단위
	    gridForm.setOrg_clas_c("30"); //기관분류코드
	    gridForm.setBlng_hq_br_c("041"); //소속본부점소코드
	    gridForm.setRate_yn(false); //이율표시여부
	    
		model.addAttribute("gridForm",gridForm);
		
		return "grid/treeGrid2";
	}
	
	
	@PostMapping( value="/employeeList" , produces="application/json;charset=UTF-8")
	@ResponseBody
	public List<Map<String,Object>> employeeList(  ){

		log.debug("사원리스트 조회 테스트___________________________________");
		
		return this.service.employeeList();
	}
	
	
}
