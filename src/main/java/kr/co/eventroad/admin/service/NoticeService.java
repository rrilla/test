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
public class NoticeService extends RootService {

	/**
	 * sql connector
	 */
	@Autowired
	private SqlSession sqlSession;
	
	public String insertNotice(Map<String, Object> paramMap) throws Exception	{
		emptyRemoveMap(paramMap);
		int i = sqlSession.insert("insertNotice", paramMap);
		if(i==0) {
			throw new Exception(Constant.CODE_SESSION_EXPIRED);
		}
//		paramMap.clear();
		return paramMap.get("seq_notice").toString();
	}
	
	public void updateNotice(Map<String, Object> paramMap) {
		sqlSession.update("updateNotice", paramMap);
	}
	
	public void deleteNotice(Map<String, Object> paramMap) {
		log.debug("paramMap:" + paramMap);
		emptyRemoveMap(paramMap);

		sqlSession.delete("deleteNotice", paramMap);
	}
	
	public Map<String, Object> selectNoticeOne(Map<String, Object> paramMap) {
		emptyRemoveMap(paramMap);
		return sqlSession.selectOne("selectNoticeOne", paramMap);
	}
	
	public List<?> selectNoticeList(Map<String, Object> paramMap) {
		return sqlSession.selectList("selectNoticeList", paramMap);
	}
	
	public List<?> selectTopNoticeList(Map<String, Object> paramMap) {
		return sqlSession.selectList("selectTopNoticeList", paramMap);
	}
	
	public String insertSurvey(Map<String, Object> paramMap) throws Exception	{
		emptyRemoveMap(paramMap);
		int i = sqlSession.insert("insertSurvey", paramMap);
		if(i==0) {
			throw new Exception(Constant.CODE_SESSION_EXPIRED);
		}
//		paramMap.clear();
		return paramMap.get("seq_survey").toString();
	}
	
	public void updateSurvey(Map<String, Object> paramMap) {
		sqlSession.update("updateSurvey", paramMap);
	}
	
	public void insertSurveyVoteItem(Map<String, Object> paramMap) throws Exception	{
		emptyRemoveMap(paramMap);
		int i = sqlSession.insert("insertSurveyVoteItem", paramMap);
		if(i==0) {
			throw new Exception(Constant.CODE_SESSION_EXPIRED);
		}
	}
	
	public void deleteSurvey(Map<String, Object> paramMap) {
		log.debug("paramMap:" + paramMap);
		emptyRemoveMap(paramMap);

		sqlSession.delete("deleteSurvey", paramMap);
	}
	
	public void deleteSurveyVoteItem(Map<String, Object> paramMap) {
		log.debug("paramMap:" + paramMap);
		emptyRemoveMap(paramMap);

		sqlSession.delete("deleteSurveyVoteItem", paramMap);
	}
	
	public List<?> selectSurveyList(Map<String, Object> paramMap) {
		return sqlSession.selectList("selectSurveyList", paramMap);
	}
	
	public List<?> selectTopSurveyList(Map<String, Object> paramMap) {
		return sqlSession.selectList("selectTopSurveyList", paramMap);
	}
	
	public Map<String, Object> selectSurveyOne(Map<String, Object> paramMap) {
		emptyRemoveMap(paramMap);
		return sqlSession.selectOne("selectSurveyOne", paramMap);
	}
	
	public List<?> selectSurveyVoteItemList(Map<String, Object> paramMap) {
		return sqlSession.selectList("selectSurveyVoteItemList", paramMap);
	}
	
	public String insertEducationalPlan(Map<String, Object> paramMap) throws Exception	{
		emptyRemoveMap(paramMap);
		int i = sqlSession.insert("insertEducationalPlan", paramMap);
		if(i==0) {
			throw new Exception(Constant.CODE_SESSION_EXPIRED);
		}
		return paramMap.get("seq_educational_plan").toString();
	}
	
	public void updateEducationalPlan(Map<String, Object> paramMap) {
		sqlSession.update("updateEducationalPlan", paramMap);
	}
	
	public void deleteEducationalPlan(Map<String, Object> paramMap) {
		log.debug("paramMap:" + paramMap);
		emptyRemoveMap(paramMap);

		sqlSession.delete("deleteEducationalPlan", paramMap);
	}
	
	public List<?> selectEducationalPlanList(Map<String, Object> paramMap) {
		return sqlSession.selectList("selectEducationalPlanList", paramMap);
	}
	
	public Map<String, Object> selectEducationalPlanOne(Map<String, Object> paramMap) {
		emptyRemoveMap(paramMap);
		return sqlSession.selectOne("selectEducationalPlanOne", paramMap);
	}
	
	public void insertMedicationRequest(Map<String, Object> paramMap) throws Exception {
		emptyRemoveMap(paramMap);
		int i = sqlSession.insert("insertMedicationRequest", paramMap);
		if(i==0) {
			throw new Exception(Constant.CODE_SESSION_EXPIRED);
		}
	}
	
	public void updateMedicationRequest(Map<String, Object> paramMap) {
		sqlSession.update("updateMedicationRequest", paramMap);
	}
	
	public void deleteMedicationRequest(Map<String, Object> paramMap) {
		log.debug("paramMap:" + paramMap);
		emptyRemoveMap(paramMap);

		sqlSession.delete("deleteMedicationRequest", paramMap);
	}
	
	public List<?> selectMedicationRequestList(Map<String, Object> paramMap) {
		return sqlSession.selectList("selectMedicationRequestList", paramMap);
	}
	
	public Map<String, Object> selectMedicationRequestOne(Map<String, Object> paramMap) {
		emptyRemoveMap(paramMap);
		return sqlSession.selectOne("selectMedicationRequestOne", paramMap);
	}
	
	public Integer checkHomeRequestCount(Map<String, Object> paramMap) {
		emptyRemoveMap(paramMap);
		return sqlSession.selectOne("checkHomeRequestCount", paramMap);
	}
	
	public void insertHomeRequest(Map<String, Object> paramMap) throws Exception {
		emptyRemoveMap(paramMap);
		int i = sqlSession.insert("insertHomeRequest", paramMap);
		if(i==0) {
			throw new Exception(Constant.CODE_SESSION_EXPIRED);
		}
	}
	
	public void updateHomeRequest(Map<String, Object> paramMap) {
		sqlSession.update("updateHomeRequest", paramMap);
	}
	
	public void deleteHomeRequest(Map<String, Object> paramMap) {
		log.debug("paramMap:" + paramMap);
		emptyRemoveMap(paramMap);

		sqlSession.delete("deleteHomeRequest", paramMap);
	}
	
	public List<?> selectHomeRequestList(Map<String, Object> paramMap) {
		return sqlSession.selectList("selectHomeRequestList", paramMap);
	}
	
	public Map<String, Object> selectHomeRequestOne(Map<String, Object> paramMap) {
		emptyRemoveMap(paramMap);
		return sqlSession.selectOne("selectHomeRequestOne", paramMap);
	}
	
	public void insertScheduleManagement(Map<String, Object> paramMap) throws Exception {
		emptyRemoveMap(paramMap);
		int i = sqlSession.insert("insertScheduleManagement", paramMap);
		if(i==0) {
			throw new Exception(Constant.CODE_SESSION_EXPIRED);
		}
	}
	
	public void updateScheduleManagement(Map<String, Object> paramMap) {
		sqlSession.update("updateScheduleManagement", paramMap);
	}
	
	public void deleteScheduleManagement(Map<String, Object> paramMap) {
		log.debug("paramMap:" + paramMap);
		emptyRemoveMap(paramMap);

		sqlSession.delete("deleteScheduleManagement", paramMap);
	}
	
	public List<?> selectScheduleManagementList(Map<String, Object> paramMap) {
		return sqlSession.selectList("selectScheduleManagementList", paramMap);
	}
	
	public Map<String, Object> selectScheduleManagementOne(Map<String, Object> paramMap) {
		emptyRemoveMap(paramMap);
		return sqlSession.selectOne("selectScheduleManagementOne", paramMap);
	}
	
	public void insertMenuManagement(Map<String, Object> paramMap) throws Exception {
		emptyRemoveMap(paramMap);
		int i = sqlSession.insert("insertMenuManagement", paramMap);
		if(i==0) {
			throw new Exception(Constant.CODE_SESSION_EXPIRED);
		}
	}
	
	public void updateMenuManagement(Map<String, Object> paramMap) {
		sqlSession.update("updateMenuManagement", paramMap);
	}
	
	public void deleteMenuManagement(Map<String, Object> paramMap) {
		log.debug("paramMap:" + paramMap);
		emptyRemoveMap(paramMap);

		sqlSession.delete("deleteMenuManagement", paramMap);
	}
	
	public List<?> selectMenuManagementList(Map<String, Object> paramMap) {
		return sqlSession.selectList("selectMenuManagementList", paramMap);
	}
	
	public Map<String, Object> selectMenuManagementOne(Map<String, Object> paramMap) {
		emptyRemoveMap(paramMap);
		return sqlSession.selectOne("selectMenuManagementOne", paramMap);
	}
	
	public String insertConfirm(Map<String, Object> paramMap) throws Exception	{
		emptyRemoveMap(paramMap);
		int i = sqlSession.insert("insertConfirm", paramMap);
		if(i==0) {
			throw new Exception(Constant.CODE_SESSION_EXPIRED);
		}
//		paramMap.clear();
		return paramMap.get("seq_confirm").toString();
	}
	
	public Map<String, Object> selectConfirmCheck(Map<String, Object> paramMap) {
		emptyRemoveMap(paramMap);
		return sqlSession.selectOne("selectConfirmCheck", paramMap);
	}
	
	public Map<String, Object> selectConfirmAll(Map<String, Object> paramMap) {
		emptyRemoveMap(paramMap);
		return sqlSession.selectOne("selectConfirmAll", paramMap);
	}
	
	public Integer selectSurveyVoteCheck(Map<String, Object> paramMap) {
		emptyRemoveMap(paramMap);
		return sqlSession.selectOne("selectSurveyVoteCheck", paramMap);
	}
	
	public void insertSurveyVote(Map<String, Object> paramMap) throws Exception	{
		emptyRemoveMap(paramMap);
		int i = sqlSession.insert("insertSurveyVote", paramMap);
		if(i==0) {
			throw new Exception(Constant.CODE_SESSION_EXPIRED);
		}
	}
	
	public void insertNews(Map<String, Object> paramMap) throws Exception	{
		emptyRemoveMap(paramMap);
		int i = sqlSession.insert("insertNews", paramMap);
		if(i==0) {
			throw new Exception(Constant.CODE_SESSION_EXPIRED);
		}
	}
	
	/**
	 * @param paramMap
	 * @return 작성자 제외, 해당 유치원/반의 교사 및 보호자
	 */
	public List<?> selectPushKindergardenClassList(Map<String, Object> paramMap) {
		return sqlSession.selectList("selectPushKindergardenClassList", paramMap);
	}
	
	/**
	 * @param paramMap
	 * @return 학부모 작성자 자녀의 담임교사
	 */
	public List<?> selectPushKidsTeacherList(Map<String, Object> paramMap) {
		return sqlSession.selectList("selectPushKidsTeacherList", paramMap);
	}
	
	/**
	 * @param paramMap
	 * @return 아이의 학부모
	 */
	public List<?> selectPushKidsOneToList(Map<String, Object> paramMap) {
		return sqlSession.selectList("selectPushKidsOneToList", paramMap);
	}
	
	public List<?> selectNewsList(Map<String, Object> paramMap) {
		return sqlSession.selectList("selectNewsList", paramMap);
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	/**
	 * check admin id
	 * @param modelMap
	 * @throws Exception
	 */
	public void checkId(Map<String, Object> modelMap) throws Exception {
		log.debug("param:" + modelMap);
		emptyRemoveMap(modelMap);

		Map m = sqlSession.selectOne("getAdminById", modelMap);

		modelMap.clear();
		modelMap.put("RET", m);
	}

	/**
	 * join admin
	 * @param modelMap
	 * @throws Exception
	 */
	public void join(Map<String, Object> modelMap) throws Exception {
		log.debug("param:" + modelMap);
		emptyRemoveMap(modelMap);

		int ret = sqlSession.update("setAdmin", modelMap);

		modelMap.clear();
		modelMap.put("RET", ret);
	}

	public void selectNotice(Map<String, Object> params) {
		emptyRemoveMap(params);
		List<Object> list = new ArrayList<Object>();

		try {
			list = sqlSession.selectList("selectNotice", params);
		} catch (Exception e) {
			e.printStackTrace();
		}

		params.clear();
		params.put("MSG", "OK");
		params.put("CODE", "200");
		params.put("RET", list);
	}

	/**
	 * 공지, 이벤트 List
	 * @param params
	 */
	public void selectNoticeEvent(Map<String, Object> params) {
		emptyRemoveMap(params);
		List<Object> list = new ArrayList<Object>();

		try {
			list = sqlSession.selectList("selectNoticeEvent", params);
		} catch (Exception e) {
			e.printStackTrace();
		}

		params.clear();
		params.put("MSG", "OK");
		params.put("CODE", "200");
		params.put("RET", list);
	}

	/**
	 * 공지, 이벤트 insert
	 * @param modelMap
	 * @throws Exception
	 */
	/*public void insertNoticeEvent(Map<String, Object> modelMap) throws Exception{
		log.debug("params: insertNoticeEvent" + modelMap);
		emptyRemoveMap(modelMap);
		try {
			int i = sqlSession.insert("insertNoticeEvent", modelMap);
			modelMap.clear();
			modelMap.put("RET", i);
		} catch (Exception e) {
			e.printStackTrace();
		}
//		throw new Exception(Constant.CODE_NO_RETURN);
	}*/

	public void selectOneNoticeEvent(Map<String, Object> modelMap) {
		log.debug("param:" + modelMap);
		emptyRemoveMap(modelMap);

		Map m = sqlSession.selectOne("selectOneNoticeEvent", modelMap);

		modelMap.clear();
		modelMap.put("RET", m);
	}

	/**
	 * 공지, 이벤트 update
	 * @param modelMap
	 */
	public void updateNoticeEvent(Map<String, Object> modelMap) {
		log.debug("param updateNoticeEvent :" + modelMap);
		emptyRemoveMap(modelMap);

		int ret = sqlSession.update("updateNoticeEvent", modelMap);

		modelMap.clear();
		modelMap.put("RET", ret);
	}

	/**
	 * 메인 노출 체크/체크해제
	 * @param modelMap
	 */
	public void editCheckExpose(Map<String, Object> modelMap) {
		log.debug("param editCheckExpose :" + modelMap);
		emptyRemoveMap(modelMap);

		int ret = sqlSession.update("editCheckExpose", modelMap);

		modelMap.clear();
		modelMap.put("RET", ret);
	}

	/**
	 * 메인 노출 체크 된 게시물들 갯수 체크
	 * @param modelMap
	 */
	public void checkNoticeCount(Map<String, Object> modelMap) {
		log.debug("param:" + modelMap);
		emptyRemoveMap(modelMap);

		Map m = sqlSession.selectOne("checkNoticeCount", modelMap);
		modelMap.putAll(m);
	}
}