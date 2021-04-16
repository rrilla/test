package kr.co.eventroad.admin.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kr.co.eventroad.admin.common.Constant;
import kr.co.eventroad.admin.common.ResultCode;
import kr.co.eventroad.admin.common.RootService;


/**
 * @제작자 jwdoe
 * @마지막수정날자 2014. 8. 4.
 * @클래스설명 notice service
 */
@Service
@Transactional
public class MainService extends RootService {

	/**
	 * sql connector
	 */
	@Autowired
	private SqlSession sqlSession;
	
	public Map<String, Object> selectVersion(Map<String, Object> paramMap) {
		emptyRemoveMap(paramMap);
		return sqlSession.selectOne("selectVersion", paramMap);
	}
	
	public Map<String, Object> selectParentMain(Map<String, Object> paramMap) {
		emptyRemoveMap(paramMap);
		return sqlSession.selectOne("selectParentMain", paramMap);
	}
	
	public Map<String, Object> selectTeacherMain(Map<String, Object> paramMap) {
		emptyRemoveMap(paramMap);
		return sqlSession.selectOne("selectTeacherMain", paramMap);
	}
	
	
	
	
	
	
	
	
	
	
	
	public List<?> selectPortfoliosList(Map<String, Object> paramMap) {
		return sqlSession.selectList("selectPortfoliosList", paramMap);
	}
	
	public List<?> selectTitleList(Map<String, Object> paramMap) {
		return sqlSession.selectList("selectTitleList", paramMap);
	}
	
	public List<?> selectContentList(Map<String, Object> paramMap) {
		return sqlSession.selectList("selectContentList", paramMap);
	}
	
	public List<?> getCostEstimateResult(Map<String, Object> paramMap) {
		return sqlSession.selectList("getCostEstimateResult", paramMap);
	}
	
	
	
	
	
	
	/**
	 * 배틀 정보
	 * @param params
	 */
	public void selectBattleInfo(Map<String, Object> params) {
		emptyRemoveMap(params);
		List<Object> list = new ArrayList<Object>();
		try {
			list = sqlSession.selectList("selectBattleInfo", params);
		} catch (Exception e) {
			e.printStackTrace();
		}
		int percent = 0;
		for(int i=0;i<list.size();i++) {
			if( ((Map<?,?>) list.get(i)).get("VOTE_COUNT_LEFT").toString().equals("0") && ((Map<?,?>) list.get(i)).get("VOTE_COUNT_RIGHT").toString().equals("0") ){// 0 일 경우 예외처리
				((Map)list.get(i)).put("PERCENT_LEFT", 0);
				((Map)list.get(i)).put("PERCENT_RIGHT", 0);
			} else {
				percent = Math.round(
						(
							Float.parseFloat(((Map) list.get(i)).get("VOTE_COUNT_LEFT").toString()) / (Float.parseFloat(((Map) list.get(i)).get("VOTE_COUNT_LEFT").toString()) + Float.parseFloat(((Map) list.get(i)).get("VOTE_COUNT_RIGHT").toString()))
						) * 100   
					);
				((Map)list.get(i)).put("PERCENT_LEFT", percent);
				((Map)list.get(i)).put("PERCENT_RIGHT", 100 - percent);
			}
		}
		
		params.clear();
		params.put("RET", list);
	}
	
	
	/**
	 * Battle 에서 비디오 insert
	 * @param params
	 */
	public void insertBattleVideo(Map<String, Object> params) {
		log.debug("params insertBattleVideo :" + params);
		emptyRemoveMap(params);
		// 예외처리 해야함
		int i = sqlSession.insert("insertBattleVideo", params);
	}
	
	/**
	 * Battle 에서 방송 insert
	 * @param params
	 */
	public void insertBattleBroadcast(Map<String, Object> params) {
		log.debug("params insertBattleBroadcast :" + params);
		emptyRemoveMap(params);

		// 예외처리 해야함
		int i = sqlSession.insert("insertBattleBroadcast", params);
	}
	
	/**
	 * 배틀 기본정보 insert
	 * @param params
	 */
	public void insertBattleInfo(Map<String, Object> params) {
		log.debug("params:" + params);
		emptyRemoveMap(params);

		// 예외처리 해야함
		int i = sqlSession.insert("insertBattleInfo", params);
	}


	/**
	 * 배틀 하나 선택
	 * @param modelMap
	 */
	public void selectOneBattle(Map<String, Object> modelMap) {
		log.debug("param:" + modelMap);
		emptyRemoveMap(modelMap);

		Map<?,?> m = sqlSession.selectOne("selectOneBattle", modelMap);

		modelMap.clear();
		modelMap.put("RET", m);
	}
	
	public void updateBattleVideo(Map<String, Object> modelMap) {
		log.debug("modelMap:" + modelMap);
		emptyRemoveMap(modelMap);

		int ret = sqlSession.update("updateBattleVideo", modelMap);
	}


	/**
	 * 배틀 update
	 * @param modelMap
	 */
	public void updateBattleInfo(Map<String, Object> modelMap) {
		log.debug("modelMap:" + modelMap);
		emptyRemoveMap(modelMap);

		int ret = sqlSession.update("updateBattleInfo", modelMap);
	}


	public void updateBattleBroadcast(Map<String, Object> modelMap) {
		log.debug("modelMap:" + modelMap);
		emptyRemoveMap(modelMap);

		int ret = sqlSession.update("updateBattleBroadcast", modelMap);
	}


	public void deleteBattleVideo(Map<String, Object> modelMap) {
		emptyRemoveMap(modelMap);
		int i = -1;
		try {
			i = sqlSession.delete("deleteBattleVideo", modelMap);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	public void deleteBattleBroadcast(Map<String, Object> modelMap) {
		emptyRemoveMap(modelMap);
		int i = -1;
		try {
			i = sqlSession.delete("deleteBattleBroadcast", modelMap);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	public void deleteBattleInfo(Map<String, Object> modelMap) {
		emptyRemoveMap(modelMap);
		int i = -1;
		try {
			i = sqlSession.delete("deleteBattleInfo", modelMap);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void testlaon(Map<String, Object> modelMap) {
		log.debug("param:" + modelMap);
		emptyRemoveMap(modelMap);
		List<Object> list = new ArrayList<Object>();
		list = sqlSession.selectList("testlaon", modelMap);

		modelMap.clear();
		modelMap.put("RET", list);
	}


	public List<?> getMainNotice(Map<String, Object> paramMap) {
		return sqlSession.selectList("getMainNotice", paramMap);
	}


	public Map<?,?> getNoticeDetail(Map<String, Object> paramMap) {
		return sqlSession.selectOne("getNoticeDetail", paramMap);
	}

	public List<?> getNoticeList(Map<String, Object> paramMap) {
		return sqlSession.selectList("getNoticeList", paramMap);
	}

	public String getNoticeListCount(Map<String, Object> paramMap) {
		return sqlSession.selectOne("getNoticeListCount", paramMap);
	}

	public void insertQuestion(Map<String, Object> paramMap) {
		log.debug("params insertQuestion :" + paramMap);
		emptyRemoveMap(paramMap);
		// 예외처리 해야함
		int i = sqlSession.insert("insertQuestion", paramMap);
	}
	
	public List<?> getQuestionList(Map<String, Object> paramMap) {
		return sqlSession.selectList("getQuestionList", paramMap);
	}

	public String getQuestionListCount(Map<String, Object> paramMap) {
		return sqlSession.selectOne("getQuestionListCount", paramMap);
	}


	public void deleteQuestion(Map<String, Object> modelMap) {
		emptyRemoveMap(modelMap);
		sqlSession.delete("deleteQuestion", modelMap);
	}

	public void updateQuestion(Map<String, Object> modelMap) {
		log.debug("modelMap:" + modelMap);
		emptyRemoveMap(modelMap);

		int ret = sqlSession.update("updateQuestion", modelMap);
	}


	public int testExcel(List<Object> list) {
		return sqlSession.insert("testExcel", list);
	}


	public String selectUserId(String username) {
		return sqlSession.selectOne("selectUserId", username);
	}

	public List<?> selectCategoryList(Map<String, Object> paramMap) {
		return sqlSession.selectList("selectCategoryList", paramMap);
	}

	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	public String insertUser(Map<String, Object> paramMap) throws Exception	{
		emptyRemoveMap(paramMap);
		int i = sqlSession.insert("insertUser", paramMap);
		if(i==0) {
			throw new Exception(Constant.CODE_SESSION_EXPIRED);
		}
//		paramMap.clear();
		return paramMap.get("seq_user").toString();
	}

	public Map<String, Object> selectUser(Map<String, Object> paramMap) {
		return sqlSession.selectOne("selectUser", paramMap);
	}

	public void getCheckUserId(Map<String, Object> paramMap) throws Exception {
		emptyRemoveMap(paramMap);

		Map m = sqlSession.selectOne("getCheckUserId", paramMap);
		
		if(m == null){
			int i = sqlSession.insert("insertUser", paramMap);
			if(i==0) {
				throw new Exception(Constant.CODE_SESSION_EXPIRED);
			}
//			throw new Exception( ResultCode.RESULT_ALREADY_USER_DUPLICATE_ID.getCode() );
		}
	}
	
	public void updateUser(Map<String, Object> modelMap) {
		log.debug("modelMap:" + modelMap);
		emptyRemoveMap(modelMap);

		sqlSession.update("updateUser", modelMap);
	}

	public void insertLocation(Map<String, Object> paramMap) throws Exception {
		int i = sqlSession.insert("insertUser", paramMap);
		if(i==0) {
			System.out.println("insertLocation CODE_SESSION_EXPIRED");
			throw new Exception(Constant.CODE_SESSION_EXPIRED);
		}
	}
	
	public List<?> selectLocationList(Map<String, Object> username) {
		return sqlSession.selectList("selectLocationList", username);
	}
	
	public Map<String, Object> checkReqFriend(Map<String, Object> paramMap) {
		return sqlSession.selectOne("checkReqFriend", paramMap);
	}
	
	public void insertReqFriend(Map<String, Object> paramMap) throws Exception	{
		emptyRemoveMap(paramMap);
		int i = sqlSession.insert("insertReqFriend", paramMap);
		if(i==0) {
			throw new Exception(Constant.CODE_INSERT_FAIL);
		}
	}
	
	public void updateReqFriend(Map<String, Object> modelMap) {
		log.debug("modelMap:" + modelMap);
		emptyRemoveMap(modelMap);

		sqlSession.update("updateReqFriend", modelMap);
	}
	
	public void deleteReqFriend(Map<String, Object> modelMap) {
		emptyRemoveMap(modelMap);
		
		sqlSession.delete("deleteReqFriend", modelMap);
	}
	
	public void insertPromise(Map<String, Object> paramMap) throws Exception	{
		emptyRemoveMap(paramMap);
		int i = sqlSession.insert("insertPromise", paramMap);
		if(i==0) {
			throw new Exception(Constant.CODE_INSERT_FAIL);
		}
	}
	
	public void updatePromise(Map<String, Object> modelMap) {
		log.debug("modelMap:" + modelMap);
		emptyRemoveMap(modelMap);

		sqlSession.update("updatePromise", modelMap);
	}
	
	public void deletePromise(Map<String, Object> modelMap) {
		emptyRemoveMap(modelMap);
		
		sqlSession.delete("deletePromise", modelMap);
	}
	
	
	
}