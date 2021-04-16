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
 * @마지막수정날자 2014. 8. 4.
 * @클래스설명 notice service
 */
@Service
@Transactional
public class TeacherService extends RootService {

	/**
	 * sql connector
	 */
	@Autowired
	private SqlSession sqlSession;
	
	public List<?> selectMyKindergardenList(Map<String, Object> paramMap) {
		emptyRemoveMap(paramMap);
		return sqlSession.selectList("selectMyKindergardenList", paramMap);
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	public List<?> selectAgencyList(Map<String, Object> paramMap) {
		emptyRemoveMap(paramMap);
		return sqlSession.selectList("selectAgencyList", paramMap);
	}
	
	public List<?> selectDealerList(Map<String, Object> paramMap) {
		emptyRemoveMap(paramMap);
		return sqlSession.selectList("selectDealerList", paramMap);
	}
	
	
	
	// 삭제 고민... ( 사용하는 곳이 있는지 확인 )
	public void selectMagazineBroadCast(Map<String, Object> params) {
		emptyRemoveMap(params);
		List<Object> list = new ArrayList<Object>();

		try {
			list = sqlSession.selectList("selectMagazineBroadCast", params);
		} catch (Exception e) {
			e.printStackTrace();
		}

		params.clear();
		params.put("MSG", "OK");
		params.put("CODE", "200");
		params.put("RET", list);
	}

	/**
	 * 메타 매거진 List
	 * @param params
	 */
	public void selectMagazine(Map<String, Object> params) {
		emptyRemoveMap(params);
		List<Object> list = new ArrayList<Object>();
		try {
			list = sqlSession.selectList("selectMagazine", params);
		} catch (Exception e) {
			e.printStackTrace();
		}

		params.clear();
		params.put("RET", list);
	}

	/**
	 * 매거진 insert
	 * @param params
	 */
	public void insertMagazine(Map<String, Object> params) {
		log.debug("params insertMagazine :" + params);
		emptyRemoveMap(params);
		// 예외처리 해야함
		int i = sqlSession.insert("insertMagazine", params);
	}

	/**
	 * 테마매거진 주소 (동까지) search
	 * @param params
	 */
	public void selectMagazineAddress(Map<String, Object> params) {
		emptyRemoveMap(params);
		List<Object> list = new ArrayList<Object>();
		try {
			list = sqlSession.selectList("selectMagazineAddress", params);
		} catch (Exception e) {
			e.printStackTrace();
		}

		params.clear();
		params.put("RET", list);
	}

	/**
	 * 매거진 이미지 insert
	 * @param params
	 */
	public void insertMagazineImage(Map<String, Object> params) {
		log.debug("params:" + params);
		emptyRemoveMap(params);

		// 예외처리 해야함
		int i = sqlSession.insert("insertMagazineImage", params);
	}

	/**
	 * 매거진 선택
	 * @param modelMap
	 */
	public void selectEditMagazine(Map<String, Object> modelMap) {
		log.debug("param:" + modelMap);
		emptyRemoveMap(modelMap);

		Map m = sqlSession.selectOne("selectEditMagazine", modelMap);

		modelMap.clear();
		modelMap.put("RET", m);
	}

	public void getMagazineImage(Map<String, Object> params) {
		emptyRemoveMap(params);
		List<Object> list = new ArrayList<Object>();
		try {
			list = sqlSession.selectList("getMagazineImage", params);
		} catch (Exception e) {
			e.printStackTrace();
		}

		params.clear();
		params.put("THEME_IMAGE", list);
	}

	public void updateMagazine(Map<String, Object> modelMap) {
		log.debug("modelMap updateMagazine:" + modelMap);
		emptyRemoveMap(modelMap);

		int ret = sqlSession.update("updateMagazine", modelMap);
	}
	
	public void updateMagazineImage(Map<String, Object> modelMap) {
		log.debug("modelMap:" + modelMap);
		emptyRemoveMap(modelMap);

		int ret = sqlSession.update("updateMagazineImage", modelMap);
	}

	public void deleteMagazine(Map<String, Object> modelMap) {
		emptyRemoveMap(modelMap);
		int i = -1;
		try {
			i = sqlSession.delete("deleteMagazine", modelMap);
		} catch (Exception e) {
			e.printStackTrace();
		} 
	}

	public void deleteMagazineVideo(Map<String, Object> modelMap) {
		emptyRemoveMap(modelMap);
		int i = -1;
		try {
			i = sqlSession.delete("deleteMagazineVideo", modelMap);
		} catch (Exception e) {
			e.printStackTrace();
		} 
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

	public List<?> selectCorpVideo(Map<String, Object> paramMap) {
		return sqlSession.selectList("selectCorpVideo", paramMap);
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
	
	public Map<String, Object> selectDealerOne(Map<String, Object> paramMap) {
		emptyRemoveMap(paramMap);
		return sqlSession.selectOne("selectDealerOne", paramMap);
	}
	
	public List<?> selectBeaconList(Map<String, Object> paramMap) {
		return sqlSession.selectList("selectBeaconList", paramMap);
	}
	
	public void insertBeaconReq(Map<String, Object> paramMap) {
		log.debug("params:" + paramMap);
		emptyRemoveMap(paramMap);

		sqlSession.insert("insertBeaconReq", paramMap);
	}

	public List<?> selectDealerBeaconReqList(Map<String, Object> paramMap) {
		emptyRemoveMap(paramMap);
		return sqlSession.selectList("selectDealerBeaconReqList", paramMap);
	}
	
	public Integer selectDealerBeaconReqCount(Map<String, Object> paramMap) {
		emptyRemoveMap(paramMap);
		return sqlSession.selectOne("selectDealerBeaconReqCount", paramMap);
	}
	
	public Map<String, Object> selectBeaconReq(Map<String, Object> paramMap) {
		log.debug("paramMap:" + paramMap);
		return sqlSession.selectOne("selectBeaconReq", paramMap);
	}
	
	public void updateBeaconReq(Map<String, Object> paramMap) {
		log.debug("paramMap updateBeaconReq:" + paramMap);
		emptyRemoveMap(paramMap);

		sqlSession.update("updateBeaconReq", paramMap);
	}
	
	public List<?> selectStoreList(Map<String, Object> paramMap) {
		return sqlSession.selectList("selectStoreList", paramMap);
	}
	
	public Integer selectStoreCount(Map<String, Object> paramMap) {
		emptyRemoveMap(paramMap);
		return sqlSession.selectOne("selectStoreCount", paramMap);
	}
	
	public Map<String, Object> selectStoreOne(Map<String, Object> paramMap) {
		emptyRemoveMap(paramMap);
		return sqlSession.selectOne("selectStoreOne", paramMap);
	}

	public List<?> selectDealerGpsReqList(Map<String, Object> paramMap) {
		emptyRemoveMap(paramMap);
		return sqlSession.selectList("selectDealerGpsReqList", paramMap);
	}
	
	public Integer selectDealerGpsReqCount(Map<String, Object> paramMap) {
		emptyRemoveMap(paramMap);
		return sqlSession.selectOne("selectDealerGpsReqCount", paramMap);
	}
	
	public void insertGpsReq(Map<String, Object> paramMap) {
		log.debug("params:" + paramMap);
		emptyRemoveMap(paramMap);

		sqlSession.insert("insertGpsReq", paramMap);
	}
	
	public Map<String, Object> selectGpsReq(Map<String, Object> paramMap) {
		log.debug("paramMap:" + paramMap);
		return sqlSession.selectOne("selectGpsReq", paramMap);
	}
	
	public void updateGpsReq(Map<String, Object> paramMap) {
		log.debug("paramMap updateGpsReq:" + paramMap);
		emptyRemoveMap(paramMap);

		sqlSession.update("updateGpsReq", paramMap);
	}
}