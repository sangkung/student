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
public class TestController {
	
	private final Logger log = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	private TestService service;


	@GetMapping("/")
	public String hello(Model model) {
		log.info("index html 이동");
		model.addAttribute("hello","Hellow World!!");
		return "index";
	}	
	
	@GetMapping("/basic")
	public String basic(Model model) {
		return "grid/basicGrid";
	}
	
	@GetMapping("/tree")
	public String tree(Model model, TestData gridForm) {
		log.info("tree 화면으로 이동");
		
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
		
		return "grid/treeGrid";
	}
	
	
	@PostMapping("/treeGrid")
	@ResponseBody
	public String treeGrid(@ModelAttribute("gridForm") TestData gridFormData) throws Exception {
		log.info("tree grid 조회 테스트");
		log.info("form 전달받은 값______________________________"
				+"\n\t base_date        : " + gridFormData.getBase_date().replaceAll("-", "")
				+"\n\t br_c             : " + gridFormData.getBr_c()
				+"\n\t mo_br_c          : " + gridFormData.getMo_br_c()
				+"\n\t ac_busi_tc       : " + gridFormData.getAc_busi_tc()
				+"\n\t futu_pot_sect_cnt: " + gridFormData.getFutu_pot_sect_cnt()
				+"\n\t new_cap_qty_tc   : " + gridFormData.getNew_cap_qty_tc()
				+"\n\t cap_qty_snro_no  : " + gridFormData.getCap_qty_snro_no()
				+"\n\t cs_bhor_refl_yn  : " + gridFormData.getCs_bhor_refl_yn()
				+"\n\t level            : " + gridFormData.getLevel()
				+"\n\t dimension1       : " + gridFormData.getDimension1_num()
				+"\n\t dimension2       : " + gridFormData.getDimension2_num()
				+"\n\t int_yn           : " + gridFormData.isInt_yn()
				+"\n\t sub_yn           : " + gridFormData.isSub_yn()
				+"\n\t amt_unit         : " + gridFormData.getAmt_unit()
				+"\n\t org_clas_c       : " + gridFormData.getOrg_clas_c()
				+"\n\t blng_hq_br_c     : " + gridFormData.getBlng_hq_br_c()
				+"\n\t rate_yn          : " + gridFormData.isRate_yn() );
		
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("BASE_DATE"        , gridFormData.getBase_date().replaceAll("-", ""));
		map.put("BR_C"             , gridFormData.getBr_c());
		map.put("MO_BR_C"          , gridFormData.getMo_br_c());
		map.put("AC_BUSI_TC"       , gridFormData.getAc_busi_tc());
		map.put("FUTU_POT_SECT_CNT", gridFormData.getFutu_pot_sect_cnt());
		map.put("NEW_CAP_QTY_TC"   , gridFormData.getNew_cap_qty_tc());
		map.put("CAP_QTY_SNRO_NO"  , gridFormData.getCap_qty_snro_no());
		map.put("CS_BHOR_REFL_YN"  , gridFormData.getCs_bhor_refl_yn());
		map.put("LEVEL"            , gridFormData.getLevel());
		map.put("DIMENSION1_NUM"   , gridFormData.getDimension1_num());
		map.put("DIMENSION2_NUM"   , gridFormData.getDimension2_num());
		map.put("INT_YN"           , gridFormData.isInt_yn() == true ? "Y" : "N");
		map.put("SUB_YN"           , gridFormData.isSub_yn() == true ? "Y" : "N");
		map.put("AMT_UNIT"         , gridFormData.getAmt_unit());
		map.put("ORG_CLAS_C"       , gridFormData.getOrg_clas_c());
		map.put("BLNG_HQ_BR_C"     , gridFormData.getBlng_hq_br_c());
		map.put("RATE_YN"          , gridFormData.isRate_yn() == true ? "Y" : "N");
		
		List<Map<String, Object>> resultList = service.selectTr2101List(map);
		//log.info("____조회 결과값:" + resultList);
		
		return listmap_to_json_string(resultList);
	}
	
	/**
	 * 금리GAP분석 조회																
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@GetMapping(value = "/selectTreeGrid.do")
	public String selectTreeGrid(Model model, Map<String, Object> searchDataset, HttpServletRequest request) throws Exception {
		List<Map<String, Object>> list = service.selectTr2101List(searchDataset);
		model.addAttribute("result", list);
		return "grid/treeGrid";
	}
	
	/**
	 * 자금량 시나리오 코드 조회
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@GetMapping(value = "/selectCapQtySnroNo.do")
	public List<Map<String, Object>> selectCapQtySnroNo(HttpServletRequest request) throws Exception {
		List<Map<String, Object>> list = service.selectCapQtySnroNo();

		return list;
	}
	
	private String listmap_to_json_string(List<Map<String, Object>> list)
	{       
	    JSONArray json_arr=new JSONArray();
	    for (Map<String, Object> map : list) {
	        JSONObject json_obj=new JSONObject();
	        for (Map.Entry<String, Object> entry : map.entrySet()) {
	            String key = entry.getKey();
	            Object value = entry.getValue();
	            try {
	                json_obj.put(key,value);
	            } catch (JSONException e) {
	                // TODO Auto-generated catch block
	                e.printStackTrace();
	            }                           
	        }
	        json_arr.put(json_obj);
	    }
	    return json_arr.toString();
	}
	
	
}
