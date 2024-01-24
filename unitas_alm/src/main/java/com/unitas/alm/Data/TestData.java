package com.unitas.alm.Data;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TestData {
	private String base_date; //기준년월
	private String br_c; //점소코드
	private String mo_br_c; //모점소코드
	private String ac_busi_tc; //회계사업구분코드
	private String futu_pot_sect_cnt; //미래시점
	private String new_cap_qty_tc; //신규자금량구분코드
	private String cap_qty_snro_no; //자금량시나리오번호
	private String cs_bhor_refl_yn; //고객행동
	private String level; //계정레벨
	private String dimension1_num; //디멘전1
	private String dimension2_num; //디멘전2
	private boolean int_yn; //이자포함
	private boolean sub_yn; //지소포함
	private String amt_unit; //금액단위
	private String org_clas_c; //기관분류코드
	private String blng_hq_br_c; //소속본부점소코드
	private boolean rate_yn; //이율표시여부
}
