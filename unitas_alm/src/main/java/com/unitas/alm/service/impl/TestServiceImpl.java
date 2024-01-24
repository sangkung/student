package com.unitas.alm.service.impl;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.unitas.alm.mapper.TestMapper;
import com.unitas.alm.service.TestService;

@Service
public class TestServiceImpl implements TestService {

	private final Logger log = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private TestMapper mapper;

	@Override
	public List<Map<String, Object>> selectTr2101List(Map<String, Object> searchDataset) throws Exception {
		List<Map<String, Object>> list = null;
		
		log.info("_________________트리그리드 데이타 조회________________");

//			for( Map.Entry<String, Object> entry : searchDataset.entrySet() ){
//				String strKey = entry.getKey();
//				String strValue = (String) entry.getValue();
//			    logger.debug("-----------------------------------------------");
//				logger.debug( strKey +":"+ strValue );
//			}
		
		/************************************************************************/
		/* 쿼리 조회 변수 */
		/************************************************************************/
//			String org_clas_c        = "10";       // 기관분류코드
//			String ac_busi_tc1       = "~";        // 회계사업구분코드
//			String base_date         = "20230613"; // 기준일자
//			String alm_work_tc       = "M";        // ALM작업구분코드
//			String br_c              = "013";      // 점소코드
//			String ac_busi_tc2       = "1105";     // 회계사업구분코드
//			String futu_pot_sect_cnt = "1";        // 미래시점구간수
//			String new_cap_qty_tc    = "0";        // 신규자금량구분코드
//			String cap_qty_snro_no   = "0001";     // 자금량시나리오번호
//			String cs_bhor_refl_yn   = "Y";        // 고객행동
//			String dimension1        = "1";        // 디멘전1
//			String dimension2        = "2";        // 디멘전2
//			String level             = "2";        // 계정레벨
		
		/************************************************************************/
		/* 만기구간일련번호 */
		/************************************************************************/ 
		String rateYN = (String) searchDataset.get("RATE_YN"); //이율표시여부
		String[] seqList = { "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12" //1~12개월
				, "13, 14, 15, 16, 17, 18" //18개월이내
				, "19, 20, 21, 22, 23, 24" //2년이내
				, "25, 26"                 //3년이내
				, "27"                     //4년이내
				, "28"                     //5년이내
				, "29, 30, 31, 32, 33"     //10년이내
				, "34, 35, 36, 37, 38"     //15년이내
				, "39, 40, 41, 42, 43"     //20년이내
				, "44"                     //20년초과
				
		};

		/************************************************************************/
		/* 조회 변수 처리 */
		/************************************************************************/
//			Map<String, Object> data = new HashedMap<String, Object>();
//			data.put("DIMENSION1", setDimensionColumn(dimension1));
//			data.put("DIMENSION2", setDimensionColumn(dimension2));
//			data.put("LEVEL",      level);   // 계정레벨
//			data.put("INT_YN",     "Y");     // 이자포함
		
		searchDataset.put("DIMENSION1", setDimensionColumn(searchDataset.get("DIMENSION1_NUM").toString()));
		searchDataset.put("DIMENSION2", setDimensionColumn(searchDataset.get("DIMENSION2_NUM").toString()));
		searchDataset.put("SEQ_LIST",   seqList); // 구간

		
		/************************************************************************/
		/* 조회 */
		/************************************************************************/
		list = mapper.selectTr2101List(searchDataset);
		
		//logger.debug("---------------------------------------------------------------------------");
		//logger.debug("금리갭 건수 : " + list.size());


		/****데이타 확인, 값 없을 때 처리****/ 
		if(list.size() <= 0) {
			return list;
		}
		
		
		/************************************************************************/
		/* 하단 계산 데이타 추출 */
		/************************************************************************/
		String rate_sstv_nm = ""; // 금리부여부명
		ArrayList<BigDecimal> capAList = new ArrayList<BigDecimal>(); //금리부 자산 구간별 합계
		ArrayList<BigDecimal> capBList = new ArrayList<BigDecimal>(); //금리부 부채 구간별 합계 
		BigDecimal assetAmt = BigDecimal.valueOf(0);


		for (int i = 0; i < list.size(); i++) {
			Map<String, Object> map = list.get(i);

			//금리부자산 합계(A), 금리부부채 합계(B) 명칭 확인
			if("1".equals(searchDataset.get("LEVEL"))){
				rate_sstv_nm = (String) map.get("RATE_SSTV_NM"); 
				
				if(searchDataset.get("DIMENSION1_NUM") != null && !"".equals(searchDataset.get("DIMENSION1_NUM").toString())) {
					rate_sstv_nm = (String) map.get("DIMENSION1");
				}
			}else {
				rate_sstv_nm = (String) map.get("L2_NM"); // 금리부여부명
			}
			

			/************************************************************************/
			/* 금리부 자산 A 배열 */
			/************************************************************************/
			if ("금리부자산 합계(A)".equals(rate_sstv_nm)) {
				capAList.add(getBigDecimal(map.get("TOT_AMT"))); //합계 금액
				
				for(int x=0; x<seqList.length; x++) {
					String key = "M" + x + "_AMT";
					capAList.add(getBigDecimal(map.get(key))); // 각 구간
				}
				
				capAList.add(getBigDecimal(map.get("NDCR_AMT"))); //요주의 금액
			}

			/************************************************************************/
			/* 금리부 부채 B 배열 */
			/************************************************************************/			
			if ("금리부부채 합계(B)".equals(rate_sstv_nm)) {					
				capBList.add(getBigDecimal(map.get("TOT_AMT")));
				
				for(int x=0; x<seqList.length; x++) {
					String key = "M" + x + "_AMT";
					capBList.add(getBigDecimal(map.get(key)));
				}
				
				capBList.add(getBigDecimal(map.get("NDCR_AMT")));
			}			 

			/************************************************************************/
			/* 자산합계 */
			/************************************************************************/
			if ("자산 합계".equals(map.get("RATE_SSTV_NM"))) {
				assetAmt = getBigDecimal(map.get("TOT_AMT"));
			}
		}
		
		//logger.debug("---------------------------------------------------------------------------");
		//logger.debug("금리부 자산(A) List Size : " + capAList.size() + "");
		//logger.debug("금리부 부채(B) List Size : " + capBList.size() + "");
		//logger.debug("자산합계                 : " + assetAmt        + "");
		//logger.debug("---------------------------------------------------------------------------");
		
		// 데이타가 없을 때 처리
		if(capAList.size() <=0) {
			BigDecimal zeroVal = BigDecimal.valueOf(0);
			capAList.add(zeroVal);
			
			for(int x=0; x<seqList.length; x++) {
				capAList.add(zeroVal);
			}
			
			capAList.add(zeroVal);
		}
		if(capBList.size() <=0) {
			BigDecimal zeroVal = BigDecimal.valueOf(0);
			capBList.add(zeroVal);
			
			for(int x=0; x<seqList.length; x++) {
				capBList.add(zeroVal);
			}
			
			capBList.add(zeroVal);
		}

		

		/************************************************************************/
		/* 계산 시작 */
		/************************************************************************/
		
		/************************************************************************/
		/* 단순GAP(A-B) 계산 처리 */
		/************************************************************************/
		Map<String,Object> addMap = new HashMap<String, Object>(); 
		addMap.put("L1_NM",        "단순갭 (A-B)");      //L1 계정코드명
		addMap.put("RATE_SSTV_NM", "");                  //금리부여부
		addMap.put("L2_NM",        "");                  //L2 계정코드명
		addMap.put("L3_NM",        "");                  //L3 계정코드명
		addMap.put("L4_NM",        "");                  //L4 계정코드명
		addMap.put("L5_NM",        "");                  //L5 계정코드명
		addMap.put("L6_NM",        "");                  //L6 계정코드명
		addMap.put("L7_NM",        "");                  //L7 계정코드명
		addMap.put("DIMENSION1",   "");                  //디멘전1
		addMap.put("DIMENSION2",   "");                  //디멘전2
		
		// 1개월, 2개월, 3개월... 구간 값 계산
		int cnt = capAList.size() > 0 ? capAList.size() : capBList.size() > 0 ?  capBList.size() : 0;
		if(cnt > 0) {
			for(int v=0; v<cnt; v++) {
				BigDecimal simpleCap = setZeroToNull(capAList.get(v).subtract(capBList.get(v))); // A - B
				
				if(v == 0) {
					if("Y".equals(rateYN)) { //이율표시여부
						addMap.put("TOT_AMT",  "");         //합계 - 금액
						addMap.put("TOT_RATE", simpleCap);  //합계 - 이율	
					}else {
						addMap.put("TOT_AMT",  simpleCap); //합계 - 금액
						addMap.put("TOT_RATE", "");        //합계 - 이율
					}
						
				}else if(v == cnt-1) {
					if("Y".equals(rateYN)) { //이율표시여부
						addMap.put("NDCR_AMT",  ""); //요주의이하 - 금액
						//20230921 요청 Null 처리
						//addMap.put("NDCR_RATE", simpleCap); //요주의 이율
						addMap.put("NDCR_RATE", ""); //요주의 이율
					}else {
						//20230921 요청 Null 처리
						//addMap.put("NDCR_AMT",  simpleCap); //요주의이하 - 금액
						addMap.put("NDCR_AMT",  ""); //요주의이하 - 금액
						addMap.put("NDCR_RATE", ""); //요주의 이율
					}
				}else {
					//각 구간 값
					String key     = "M" + (v-1) + "_AMT";
					String ratekey = "M" + (v-1) + "_RATE";
					
					if("Y".equals(rateYN)) { //이율표시여부
						addMap.put(key, "");            //각 구간 금액
						addMap.put(ratekey, simpleCap); //비율
					}else {
						addMap.put(key, simpleCap); //각 구간 금액
						addMap.put(ratekey, "");    //비율
					}
				}
			}				
		}
		/************************************************************************/
		/* 단순GAP(A-B) 최종 값 베열에 추가 */
		/************************************************************************/
		list.add(addMap);
		
		
		/************************************************************************/
		/* 단순갭비율 (A/B)   */
		/************************************************************************/
		addMap = new HashMap<String, Object>();
		addMap.put("L1_NM",        "단순갭비율 (A/B)");  //L1 계정코드명
		addMap.put("RATE_SSTV_NM", "");                  //금리부여부
		addMap.put("L2_NM",        "");                  //L2 계정코드명
		addMap.put("L3_NM",        "");                  //L3 계정코드명
		addMap.put("L4_NM",        "");                  //L4 계정코드명
		addMap.put("L5_NM",        "");                  //L5 계정코드명
		addMap.put("L6_NM",        "");                  //L6 계정코드명
		addMap.put("L7_NM",        "");                  //L7 계정코드명
		addMap.put("DIMENSION1",   "");                  //디멘전1
		addMap.put("DIMENSION2",   "");                  //디멘전2
		
		// 1개월, 2개월, 3개월... 구간 값 계산
		if(cnt > 0) {
			for(int v=0; v<cnt; v++) {
				//BigDecimal.ROUND_HALF_DOWN //5이하내림
				//BigDecimal.ROUND_HALF_UP   //반올림
				// 소수점 2자리, 반올림
				BigDecimal capRate = BigDecimal.valueOf(0);
				if(capAList.get(v).compareTo(BigDecimal.ZERO) != 0 && capBList.get(v).compareTo(BigDecimal.ZERO) != 0) { //분자 0값 확인
					 // A / B
					//capRate = setZeroToNull(capAList.get(v).divide(capBList.get(v), 2, BigDecimal.ROUND_HALF_UP));
					capRate = capAList.get(v).multiply(new BigDecimal(100));
					capRate = capRate.divide(capBList.get(v), 2, RoundingMode.HALF_UP);
					capRate = setZeroToNull(capRate);
				}
								
				if(v == 0) {
					if("Y".equals(rateYN)) { //이율표시여부
						addMap.put("TOT_AMT",  "");       //합계 - 금액
						addMap.put("TOT_RATE", capRate);  //합계 - 이율
					}else {
						addMap.put("TOT_AMT",  capRate);  //합계 - 금액
						addMap.put("TOT_RATE", "");       //합계 - 이율
					}
				}else if(v == cnt-1) {
					if("Y".equals(rateYN)) { //이율표시여부
						addMap.put("NDCR_AMT",  ""); //요주의이하 - 금액
						//20230921 요청 Null 처리
						//addMap.put("NDCR_RATE", capRate); //요주의 이율
						addMap.put("NDCR_RATE", ""); //요주의 이율
					}else {
						//20230921 요청 Null 처리
						//addMap.put("NDCR_AMT", capRate); //요주의이하 - 금액
						addMap.put("NDCR_AMT",  ""); //요주의이하 - 금액
						addMap.put("NDCR_RATE", ""); //요주의 이율
					}
				}else {
					String key     = "M" + (v-1) + "_AMT";
					String ratekey = "M" + (v-1) + "_RATE";
					
					if("Y".equals(rateYN)) { //이율표시여부
						addMap.put(key,     "");      //각 구간 금액
						addMap.put(ratekey, capRate); //비율
					}else {
						addMap.put(key,     capRate); //각 구간 금액
						addMap.put(ratekey, "");      //비율
					}
				}
			}
		}
		
		/************************************************************************/
		/* 단순갭비율 (A/B) 최종 값 배열에 추가 */
		/************************************************************************/
		list.add(addMap);
		
		
		
		/************************************************************************/
		/* 누적갭 (누적 A - 누적 B) 계산 처리 */
		/************************************************************************/
		addMap = new HashMap<String, Object>();
		addMap.put("L1_NM",        "누적갭 (누적 A - 누적 B)"); //L1 계정코드명
		addMap.put("RATE_SSTV_NM", "");                         //금리부여부
		addMap.put("L2_NM",        "");                         //L2 계정코드명
		addMap.put("L3_NM",        "");                         //L3 계정코드명
		addMap.put("L4_NM",        "");                         //L4 계정코드명
		addMap.put("L5_NM",        "");                         //L5 계정코드명
		addMap.put("L6_NM",        "");                         //L6 계정코드명
		addMap.put("L7_NM",        "");                         //L7 계정코드명
		addMap.put("DIMENSION1",   "");                         //디멘전1
		addMap.put("DIMENSION2",   "");                         //디멘전2
		
		// 1개월, 2개월, 3개월... 구간 값 계산
		if(cnt > 0) {
			BigDecimal accumulateA = BigDecimal.valueOf(0); //자산(A) 누적
			BigDecimal accumulateB = BigDecimal.valueOf(0); //부채(B) 누적
			
			for(int v=0; v<cnt; v++) {
				// A-B
				BigDecimal accumulateCap = setZeroToNull(capAList.get(v).subtract(capBList.get(v)));
				
				if(v == 0) {
					if("Y".equals(rateYN)) { //이율표시여부
						addMap.put("TOT_AMT",  "");             //합계 - 금액
						addMap.put("TOT_RATE", accumulateCap);  //합계 - 이율
					}else {
						addMap.put("TOT_AMT",  accumulateCap);  //합계 - 금액
						addMap.put("TOT_RATE", "");             //합계 - 이율
					}
				}else if(v == cnt-1) {
					if("Y".equals(rateYN)) { //이율표시여부
						//20230921 요청 Null 처리
						addMap.put("NDCR_AMT",  "");            //요주의이하 - 금액
						//addMap.put("NDCR_RATE", accumulateCap); //요주의 이율
						addMap.put("NDCR_RATE", ""); //요주의 이율
					}else {
						//20230921 요청 Null 처리
						//addMap.put("NDCR_AMT",  accumulateCap); //요주의이하 - 금액
						addMap.put("NDCR_AMT",  ""); //요주의이하 - 금액
						addMap.put("NDCR_RATE", "");            //요주의 이율
					}
				}else {
					// 각 구간 누적값으로 계산
					accumulateA = accumulateA.add(capAList.get(v)); //자산(A) 누적
					accumulateB = accumulateB.add(capBList.get(v)); //부채(B) 누적
					accumulateCap = setZeroToNull(accumulateA.subtract(accumulateB)); //누적A - 누적B
					
					String key     = "M" + (v-1) + "_AMT";
					String ratekey = "M" + (v-1) + "_RATE";
					
					if("Y".equals(rateYN)) { //이율표시여부
						addMap.put(key,     "");            //각 구간 금액
						addMap.put(ratekey, accumulateCap); //비율
					}else {
						addMap.put(key,     accumulateCap); //각 구간 금액
						addMap.put(ratekey, "");            //비율
					}
				}
			}
		}
		
		/************************************************************************/
		/* 누적갭 (누적 A - 누적 B) 최종 값 베열에 추가 */
		/************************************************************************/
		list.add(addMap);			
		
		
		
		/************************************************************************/
		/* 누적갭비율 (누적 A / 누적 B) 계산 처리 */
		/************************************************************************/
		addMap = new HashMap<String, Object>();
		addMap.put("L1_NM",        "누적갭비율");               //L1 계정코드명
		addMap.put("RATE_SSTV_NM", "");                         //금리부여부
		addMap.put("L2_NM",        "");                         //L2 계정코드명
		addMap.put("L3_NM",        "");                         //L3 계정코드명
		addMap.put("L4_NM",        "");                         //L4 계정코드명
		addMap.put("L5_NM",        "");                         //L5 계정코드명
		addMap.put("L6_NM",        "");                         //L6 계정코드명
		addMap.put("L7_NM",        "");                         //L7 계정코드명
		addMap.put("DIMENSION1",   "");                         //디멘전1
		addMap.put("DIMENSION2",   "");                         //디멘전2
		
		// 1개월, 2개월, 3개월... 구간 값 계산
		if(cnt > 0) {
			BigDecimal accumulateA = BigDecimal.valueOf(0); //자산(A) 누적
			BigDecimal accumulateB = BigDecimal.valueOf(0); //부채(B) 누적
			
			for(int v=0; v<cnt; v++) {
				BigDecimal capRate = BigDecimal.valueOf(0);
				if(capAList.get(v).compareTo(BigDecimal.ZERO) != 0 && capBList.get(v).compareTo(BigDecimal.ZERO) != 0) { //분자 0값 확인
					// A / B
					//capRate = setZeroToNull(capAList.get(v).divide(capBList.get(v), 2, BigDecimal.ROUND_HALF_UP));
					capRate = capAList.get(v).multiply(new BigDecimal(100)); //비율 * 100
					capRate = capRate.divide(capBList.get(v), 2, RoundingMode.HALF_UP); // 2자리 반올림
					//capRate = capRate.multiply(new BigDecimal(100)); //비율 * 100
					capRate = setZeroToNull(capRate);
				}
								
				if(v == 0) {
					if("Y".equals(rateYN)) { //이율표시여부
						addMap.put("TOT_AMT", "");        //합계 - 금액
						addMap.put("TOT_RATE", capRate);  //합계 - 이율
					}else {
						addMap.put("TOT_AMT",  capRate);  //합계 - 금액
						addMap.put("TOT_RATE", "");       //합계 - 이율
					}
				}else if(v == cnt-1) {
					if("Y".equals(rateYN)) { //이율표시여부
						//20230921 요청 Null 처리
						addMap.put("NDCR_AMT",  "");      //요주의이하 - 금액
						//addMap.put("NDCR_RATE", capRate); //요주의 이율
						addMap.put("NDCR_RATE", ""); //요주의 이율
					}else {
						//20230921 요청 Null 처리
						//addMap.put("NDCR_AMT",  capRate); //요주의이하 - 금액
						addMap.put("NDCR_AMT",  ""); //요주의이하 - 금액
						addMap.put("NDCR_RATE", "");      //요주의 이율
					}
				}else {
					// 각 구간 누적값으로 계산
					accumulateA = accumulateA.add(capAList.get(v)); //자산(A) 누적
					accumulateB = accumulateB.add(capBList.get(v)); //부채(B) 누적
					
					capRate = BigDecimal.valueOf(0);
					if(accumulateA.compareTo(BigDecimal.ZERO) != 0 && accumulateB.compareTo(BigDecimal.ZERO) != 0) { //분자 0값 확인
						 // A / B
						//capRate = setZeroToNull(accumulateA.divide(accumulateB, 2, BigDecimal.ROUND_HALF_UP));
						capRate = accumulateA.multiply(new BigDecimal(100));
						capRate = capRate.divide(accumulateB, 2, RoundingMode.HALF_UP);
						//capRate = capRate.multiply(new BigDecimal(100));
						capRate = setZeroToNull(capRate);
					}
					
					String key     = "M" + (v-1) + "_AMT";
					String ratekey = "M" + (v-1) + "_RATE";
					
					if("Y".equals(rateYN)) { //이율표시여부
						addMap.put(key,     "");      //각 구간 금액
						addMap.put(ratekey, capRate); //비율
					}else {
						addMap.put(key,     capRate); //각 구간 금액
						addMap.put(ratekey, "");      //비율
					}	
				}
			}
		}
		
		/************************************************************************/
		/* 누적갭비율 (누적 A / 누적 B) 최종 값 베열에 추가                     */
		/************************************************************************/
		list.add(addMap);
		
		

		/************************************************************************/
		/* 만기갭비율 (누적갭/총자산) 계산 처리                                 */
		/************************************************************************/
		addMap = new HashMap<String, Object>();
		addMap.put("L1_NM",        "만기갭비율 (누적갭/총자산)"); //L1 계정코드명
		addMap.put("RATE_SSTV_NM", "");                           //금리부여부
		addMap.put("L2_NM",        "");                           //L2 계정코드명
		addMap.put("L3_NM",        "");                           //L3 계정코드명
		addMap.put("L4_NM",        "");                           //L4 계정코드명
		addMap.put("L5_NM",        "");                           //L5 계정코드명
		addMap.put("L6_NM",        "");                           //L6 계정코드명
		addMap.put("L7_NM",        "");                           //L7 계정코드명
		addMap.put("DIMENSION1",   "");                           //디멘전1
		addMap.put("DIMENSION2",   "");                           //디멘전2
		
		// 1개월, 2개월, 3개월... 구간 값 계산
		if(cnt > 0) {
			BigDecimal accumulateA = BigDecimal.valueOf(0); //자산(A) 누적
			BigDecimal accumulateB = BigDecimal.valueOf(0); //부채(B) 누적
			
			for(int v=0; v<cnt; v++) {
				// A - B 단순누적갭
				BigDecimal accumulateCap = setZeroToNull(capAList.get(v).subtract(capBList.get(v)));
				BigDecimal capRate       = BigDecimal.valueOf(0);
				
				if((accumulateCap != null && accumulateCap.compareTo(BigDecimal.ZERO) != 0)
						&& (assetAmt != null && assetAmt.compareTo(BigDecimal.ZERO) != 0 )) { //분자 0값 확인
					// =자산합계 / 누적갭
					//capRate = setZeroToNull(accumulateCap.divide(assetAmt, 2, BigDecimal.ROUND_HALF_UP));
					capRate = accumulateCap.multiply(new BigDecimal(100)); //비율 * 100
					capRate = capRate.divide(assetAmt, 2, RoundingMode.HALF_UP); // 2자리 반올림
					//capRate = capRate.multiply(new BigDecimal(100)); //비율 * 100
					capRate = setZeroToNull(capRate);
				}
				
				if(v == 0) {
					if("Y".equals(rateYN)) { //이율표시여부
						addMap.put("TOT_AMT",  "");       //합계 - 금액
						addMap.put("TOT_RATE", capRate);  //합계 - 이율
					}else {
						addMap.put("TOT_AMT",  capRate);  //합계 - 금액
						addMap.put("TOT_RATE", "");       //합계 - 이율
					}
				}else if(v == cnt-1) {
					if("Y".equals(rateYN)) { //이율표시여부
						//20230921 요청 Null 처리
						addMap.put("NDCR_AMT",  "");      //요주의이하 - 금액
						//addMap.put("NDCR_RATE", capRate); //요주의 이율
						addMap.put("NDCR_RATE", ""); //요주의 이율
					}else {
						//20230921 요청 Null 처리
						//addMap.put("NDCR_AMT",  capRate); //요주의이하 - 금액
						addMap.put("NDCR_AMT",  ""); //요주의이하 - 금액
						addMap.put("NDCR_RATE", "");      //요주의 이율
					}
				}else {
					// 각 구간 누적값으로 계산
					accumulateA = accumulateA.add(capAList.get(v)); //자산(A) 누적
					accumulateB = accumulateB.add(capBList.get(v)); //부채(B) 누적
					accumulateCap = setZeroToNull(accumulateA.subtract(accumulateB)); //누적A - 누적B

					capRate = BigDecimal.valueOf(0);
					if( (accumulateCap != null && accumulateCap.compareTo(BigDecimal.ZERO) != 0)
							&& (assetAmt != null && assetAmt.compareTo(BigDecimal.ZERO) != 0) ) { //분자 0값 확인
						// =자산합계 / 누적갭
						capRate = accumulateCap.multiply(new BigDecimal(100)); //비율 * 100
						capRate = capRate.divide(assetAmt, 2, RoundingMode.HALF_UP); // 2자리 반올림
						//capRate = capRate.multiply(new BigDecimal(100)); //비율 * 100
						capRate = setZeroToNull(capRate);
					}
					
					String key     = "M" + (v-1) + "_AMT";
					String ratekey = "M" + (v-1) + "_RATE";
					
					if("Y".equals(rateYN)) { //이율표시여부
						addMap.put(key,     "");      //각 구간 금액
						addMap.put(ratekey, capRate); //비율
					}else {
						addMap.put(key,     capRate); //각 구간 금액
						addMap.put(ratekey, "");      //비율
					}
				}
			}
		}
		
		/************************************************************************/
		/* 만기갭비율 (누적갭/총자산) 최종 값 베열에 추가                       */
		/************************************************************************/
		list.add(addMap);
			
		

		return list;
	}

	@Override
	public List<Map<String, Object>> selectCapQtySnroNo() throws Exception {
		return mapper.selectCapQtySnroNo();
	}

	/**
	 * 디멘전속성내용 - 컬럼 조회
	 * 
	 * @param dimension
	 * @return
	 */
	private String setDimensionColumn(String dimension) {
		/**
		 * 디멘젼속성내용 1 고정변동구분코드 FIX_VAR_TC 2 금리변동주기 RATE_VAR_CYCL 3 ALM기준금리코드
		 * ALM_BASE_RATE_C 4 원금상환방법코드 PRIN_RMBR_METH_C 5 계약기간구분코드 CONT_TRM_SECT_C 6
		 * ALM담보유형 ALM_MRTG_TC
		 */
		String column = "";
		if ("1".equals(dimension)) {
			column = "FIX_VAR_TC";
		} else if ("2".equals(dimension)) {
			column = "RATE_VAR_CYCL";
		} else if ("3".equals(dimension)) {
			column = "ALM_BASE_RATE_C";
		} else if ("4".equals(dimension)) {
			column = "PRIN_RMBR_METH_C";
		} else if ("5".equals(dimension)) {
			column = "CONT_TRM_SECT_C";
		} else if ("6".equals(dimension)) {
			column = "ALM_MRTG_TC";
		} else if ("7".equals(dimension)) {
			column = "LN_DP_GD_C";
		}

		return column;
	}

	/**
	 * object to BigDecimal
	 * @param value
	 * @return
	 */
	private BigDecimal getBigDecimal(Object value) {
		/*
		 * System.out.println("더하기 : " + number1.add(number2));
         * System.out.println("빼기   : " + number1.subtract(number2));
         * System.out.println("곱하기 : " + number1.multiply(number2));
         * System.out.println("나누기 : " + number1.divide(number2));
		 * */
		BigDecimal ret = BigDecimal.valueOf(0);
		
		if (value != null ) {
			if (!"".equals(value) ) {
				if (!"NULL".equals( value.toString().toUpperCase()) ) {
					if (value instanceof BigDecimal) {
						ret = (BigDecimal) value;
					} else if (value instanceof String) {
						ret = new BigDecimal((String) value);
					} else if (value instanceof BigInteger) {
						ret = new BigDecimal((BigInteger) value);
					} else if (value instanceof Number) {
						ret = new BigDecimal(((Number) value).doubleValue());
					} else {
						throw new ClassCastException("Not possible to coerce [" + value + "] from class " + value.getClass()
								+ " into a BigDecimal.");
					}		
				}
			}
		}
		
		return ret;
	}
	
	/**
	 * 화면에 빈칸으로 표시하기 위해서 0일때 null 처리
	 * @param val
	 * @return
	 */
	private BigDecimal setZeroToNull(BigDecimal val) {
		//if(val.compareTo(BigDecimal.ZERO) == 0) {
			//val = null; //0값 일때 null로 빈값 처리
		//}
		
		return val;
	}

	

	public List<Map<String,Object>> employeeList( ){
		
		return this.mapper.employeeList();
	}
	
}


