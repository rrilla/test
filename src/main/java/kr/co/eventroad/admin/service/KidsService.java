package kr.co.eventroad.admin.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kr.co.eventroad.admin.common.Constant;
import kr.co.eventroad.admin.common.RootService;


/**
 * @제작자 jwdoe
 * @마지막수정날자 2017. 01. 31.
 * @클래스설명 notice service
 */
@Service
@Transactional
public class KidsService extends RootService {

	/**
	 * sql connector
	 */
	@Autowired
	private SqlSession sqlSession;
	
	public String insertKids(Map<String, Object> paramMap) throws Exception	{
		emptyRemoveMap(paramMap);
		int i = sqlSession.insert("insertKids", paramMap);
		if(i==0) {
			throw new Exception(Constant.CODE_SESSION_EXPIRED);
		}
//		paramMap.clear();
		return paramMap.get("seq_kids").toString();
	}
	
	public void updateKids(Map<String, Object> paramMap) {
		sqlSession.update("updateKids", paramMap);
	}
	
	public List<?> selectMyKidsList(Map<String, Object> paramMap) {
		emptyRemoveMap(paramMap);
		return sqlSession.selectList("selectMyKidsList", paramMap);
	}
	
	public List<?> selectAttendanceBookList(Map<String, Object> paramMap) {
		return sqlSession.selectList("selectAttendanceBookList", paramMap);
	}
	
	public List<?> selectAttendanceBookCheckList(Map<String, Object> paramMap) {
		emptyRemoveMap(paramMap);
		return sqlSession.selectList("selectAttendanceBookCheckList", paramMap);
	}
	
	public void insertAttendanceBook(ArrayList<Map<String, Object>> paramMap) {
		sqlSession.insert("insertAttendanceBook", paramMap);
	}
	
	public List<?> selectCheckInsertAttendanceBook(Map<String, Object> paramMap) {
		emptyRemoveMap(paramMap);
		return sqlSession.selectList("selectCheckInsertAttendanceBook", paramMap);
	}
	
	public void updateAttendanceBook(Map<String, Object> paramMap) {
		sqlSession.update("updateAttendanceBook", paramMap);
	}
	
	public List<?> selectKidsAttendanceBookList(Map<String, Object> paramMap) {
		emptyRemoveMap(paramMap);
		return sqlSession.selectList("selectKidsAttendanceBookList", paramMap);
	}
	
	public Map<String, Object> selectKidsOne(Map<String, Object> paramMap) {
		log.debug("param:" + paramMap);
		return sqlSession.selectOne("selectKidsOne", paramMap);
	}
	
	public Integer selectAttendanceBookKidsCheckOne(Map<String, Object> paramMap) {
		emptyRemoveMap(paramMap);
		return sqlSession.selectOne("selectAttendanceBookKidsCheckOne", paramMap);
	}
	
	public List<?> selectAttendanceBookKidsTermList(Map<String, Object> paramMap) {
		return sqlSession.selectList("selectAttendanceBookKidsTermList", paramMap);
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	public List<?> selectJobCategoryList(Map<String, Object> paramMap) {
		emptyRemoveMap(paramMap);
		return sqlSession.selectList("selectJobCategoryList", paramMap);
	}
	
	public List<?> selectDtlJobCategory(Map<String, Object> paramMap) {
		emptyRemoveMap(paramMap);
		return sqlSession.selectList("selectDtlJobCategory", paramMap);
	}
	
	public List<?> selectDtlAreaCategory(Map<String, Object> paramMap) {
		emptyRemoveMap(paramMap);
		return sqlSession.selectList("selectDtlAreaCategory", paramMap);
	}
	
	public List<?> selectJobList(Map<String, Object> paramMap) {
		emptyRemoveMap(paramMap);
		return sqlSession.selectList("selectJobList", paramMap);
	}
	
	public List<?> selectJobInfoList(Map<String, Object> paramMap) {
		return sqlSession.selectList("selectJobInfoList", paramMap);
	}
	
	public List<?> selectCompanyJobList(Map<String, Object> paramMap) {
		return sqlSession.selectList("selectCompanyJobList", paramMap);
	}
	
	public Map<String, Object> selectCompanyInfo(Map<String, Object> paramMap) {
		log.debug("param:" + paramMap);
		emptyRemoveMap(paramMap);
		
		return sqlSession.selectOne("selectCompanyInfo", paramMap);
	}
	
	public List<?> selectPushStationKidsList(Map<String, Object> paramMap) {
		return sqlSession.selectList("selectPushStationKidsList", paramMap);
	}
	
	
	
	
	
	
	
	
	
	
	public List<?> selectDealerList(Map<String, Object> paramMap) {
		emptyRemoveMap(paramMap);
		return sqlSession.selectList("selectDealerList", paramMap);
	}
	
	public Integer selectDealerCount(Map<String, Object> paramMap) {
		emptyRemoveMap(paramMap);
		return sqlSession.selectOne("selectDealerCount", paramMap);
	}
	
	public List<?> selectCampaignList(Map<String, Object> paramMap) {
		return sqlSession.selectList("selectCampaignList", paramMap);
	}
	
	public String insertAgency(Map<String, Object> paramMap) throws Exception{
		emptyRemoveMap(paramMap);
		int i = sqlSession.insert("insertAgency", paramMap);
		if(i==0) {
			throw new Exception(Constant.CODE_INSERT_FAIL);
		}
		return paramMap.get("seq_user").toString();
	}

	public void deleteCampaign(Map<String, Object> modelMap) {
		emptyRemoveMap(modelMap);
		int i = -1;
		try {
			i = sqlSession.delete("deleteCampaign", modelMap);
		} catch (Exception e) {
			e.printStackTrace();
		} 
	}
	
	public List<?> getBrchListExcel(Map<String, Object> paramMap) {
		return sqlSession.selectList("getBrchListExcel", paramMap);
	}

	public void updateCampaign(Map<String, Object> paramMap) {
		log.debug("paramMap updateCampaign:" + paramMap);
		emptyRemoveMap(paramMap);
		sqlSession.update("updateCampaign", paramMap);
	}

	public Map<String, Object> selectCampaign(Map<String, Object> paramMap) {
		log.debug("param:" + paramMap);
		emptyRemoveMap(paramMap);
		
		return sqlSession.selectOne("selectCampaign", paramMap);
	}

	public void updateCampaignPrdct(Map<String, Object> paramMap) {
		log.debug("paramMap updateCampaignPrdct:" + paramMap);
		emptyRemoveMap(paramMap);
		sqlSession.update("updateCampaignPrdct", paramMap);
	}

	public Map<String, Object> selectCampaignPrdct(Map<String, Object> paramMap) {
		log.debug("param:" + paramMap);
		Map<String, Object> temp = sqlSession.selectOne("selectCampaignPrdct", paramMap);
		emptyRemoveMap(temp);
		
		return temp;
	}
	
	public void insertCampaignPrdct(Map<String, Object> paramMap) {
		log.debug("params:" + paramMap);
		emptyRemoveMap(paramMap);

		int i = sqlSession.insert("insertCampaignPrdct", paramMap);
	}

	public String selectSeqCampaignPrdct(Map<String, Object> paramMap) {
		log.debug("param:" + paramMap);
		return sqlSession.selectOne("selectSeqCampaignPrdct", paramMap);
	}

	public void deleteCampaignPrdct(Map<String, Object> paramMap) {
		emptyRemoveMap(paramMap);
		sqlSession.delete("deleteCampaignPrdct", paramMap);
	}
	
	public List<?> selectBeaconConditionsList(Map<String, Object> paramMap) {
		return sqlSession.selectList("selectBeaconConditionsList", paramMap);
	}

	public List<?> selectAgencyNameList(Map<String, Object> paramMap) {
		return sqlSession.selectList("selectAgencyNameList", paramMap);
	}

	public List<?> excellBeaconFileCheck(ArrayList<String> beacon_sn_list) {
		return sqlSession.selectList("excellBeaconFileCheck", beacon_sn_list);
	}
	
	/**
	 * 매거진 insert
	 * @param params
	 */
	public void insertBeacon(ArrayList<String> params) {
		log.debug("params insertBeacon :" + params);
		// 예외처리 해야함
		sqlSession.insert("insertBeacon", params);
	}

	public void updateBeacon(Map<String, Object> modelMap) {
		log.debug("modelMap updateBeacon:" + modelMap);
		emptyRemoveMap(modelMap);

		sqlSession.update("updateBeacon", modelMap);
	}
	
	public List<?> selectDealerNameList(Map<String, Object> paramMap) {
		return sqlSession.selectList("selectDealerNameList", paramMap);
	}

	public Integer selectBeaconConditionsCount(Map<String, Object> paramMap) {
		return sqlSession.selectOne("selectBeaconConditionsCount", paramMap);
	}
	
	public Map<String, Object> selectCheckNews(Map<String, Object> paramMap) {
		emptyRemoveMap(paramMap);
		return sqlSession.selectOne("agency.selectCheckNews", paramMap);
	}
}