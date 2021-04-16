package kr.co.eventroad.admin.service;

import java.util.ArrayList;
import java.util.HashMap;
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
public class CarService extends RootService {

	/**
	 * sql connector
	 */
	@Autowired
	private SqlSession sqlSession;
	
	public List<?> selectKindergardenKidsStationList(Map<String, Object> paramMap) {
		return sqlSession.selectList("selectKindergardenKidsStationList", paramMap);
	}
	
	public List<?> selectKidsStationList(Map<String, Object> paramMap) {
		return sqlSession.selectList("selectKidsStationList", paramMap);
	}
	
	public List<?> selectCarList(Map<String, Object> paramMap) {
		return sqlSession.selectList("selectCarList", paramMap);
	}
	
	public List<?> selectCarStationList(Map<String, Object> paramMap) {
		return sqlSession.selectList("selectCarStationList", paramMap);
	}
	
	public Integer selectKidsStationCheck(Map<String, Object> paramMap) {
		emptyRemoveMap(paramMap);
		return sqlSession.selectOne("selectKidsStationCheck", paramMap);
	}
	
	public String insertKidsStation(Map<String, Object> paramMap) throws Exception	{
		emptyRemoveMap(paramMap);
		int i = sqlSession.insert("insertKidsStation", paramMap);
		if(i==0) {
			throw new Exception(Constant.CODE_SESSION_EXPIRED);
		}
//		paramMap.clear();
		return paramMap.get("seq_kids_station").toString();
	}
	
	public void updateKidsStation(Map<String, Object> paramMap) {
		sqlSession.update("updateKidsStation", paramMap);
	}
	
	public void deleteKidsStation(Map<String, Object> paramMap) {
		log.debug("paramMap:" + paramMap);
		emptyRemoveMap(paramMap);

		sqlSession.delete("deleteKidsStation", paramMap);
	}
	
	public List<?> selectGuideCarList(Map<String, Object> paramMap) {
		return sqlSession.selectList("selectGuideCarList", paramMap);
	}
	
	public List<?> selectGuideKidsStationKidsList(Map<String, Object> paramMap) {
		return sqlSession.selectList("selectGuideKidsStationKidsList", paramMap);
	}
	
	public Map<String, Object> selectMyKidsCarRaceInfoOne(Map<String, Object> paramMap) {
		emptyRemoveMap(paramMap);
		return sqlSession.selectOne("selectMyKidsCarRaceInfoOne", paramMap);
	}
	
	public void updateCarRace(Map<String, Object> paramMap) {
		sqlSession.update("updateCarRace", paramMap);
	}
	
	public Map<String, Object> selectCarOne(Map<String, Object> paramMap) {
		emptyRemoveMap(paramMap);
		return sqlSession.selectOne("selectCarOne", paramMap);
	}
	
	public List<?> selectPushCarRaceKidsUserList(Map<String, Object> paramMap) {
		return sqlSession.selectList("selectPushCarRaceKidsUserList", paramMap);
	}
	
	
	
	
	
	
	
	
	
	
	
	
	/**
	 * 리얼스토리, 메인동영상 List
	 * @param params
	 */
	public void selectRealStoryBroadCast(Map<String, Object> params) {
		emptyRemoveMap(params);
		List<Object> list = new ArrayList<Object>();

		try {
			list = sqlSession.selectList("selectRealStoryBroadCast", params);
		} catch (Exception e) {
			e.printStackTrace();
		}

		params.clear();
		params.put("MSG", "OK");
		params.put("CODE", "200");
		params.put("RET", list);
	}

	public void selectRealStoryStoreList(Map<String, Object> params) {
		emptyRemoveMap(params);
		List<Object> list = new ArrayList<Object>();
		List<Object> temp_list = new ArrayList<Object>();
		try {
			list = sqlSession.selectList("selectRealStoryStoreList", params);
			
			String[] li = null;
			if(params.get("AR2") !=null) {
				if(params.get("AR2").equals("전체") && !params.get("AR3").equals("전체")){
					for(int i=0;i<list.size();i++){
						li = ((Map)list.get(i)).get("ADDR_DETAIL").toString().split(" ");
						
						if(li[2].equals(params.get("AR3"))){
							temp_list.add(list.get(i));
						}
					} // end for
					params.clear();
					params.put("MSG", "OK");
					params.put("CODE", "200");
					params.put("RET", temp_list);
				} else if(!params.get("AR2").equals("전체") && !params.get("AR3").equals("전체")){
					for(int i=0;i<list.size();i++){
						li = ((Map)list.get(i)).get("ADDR_DETAIL").toString().split(" ");
						
						if(li[1].equals(params.get("AR2"))){
							if(li[2].equals(params.get("AR3"))){
								temp_list.add(list.get(i));
							}
						} else {
						}
					} // end for
					params.clear();
					params.put("MSG", "OK");
					params.put("CODE", "200");
					params.put("RET", temp_list);
				} else if(!params.get("AR2").equals("전체") && params.get("AR3").equals("전체")){
					for(int i=0;i<list.size();i++){
						li = ((Map)list.get(i)).get("ADDR_DETAIL").toString().split(" ");
						
						if(li[1].equals(params.get("AR2"))){
							temp_list.add(list.get(i));
						} else {
						}
					} // end for
					params.clear();
					params.put("MSG", "OK");
					params.put("CODE", "200");
					params.put("RET", temp_list);
				} else if(params.get("AR2").equals("전체") && params.get("AR3").equals("전체")){
					params.clear();
					params.put("MSG", "OK");
					params.put("CODE", "200");
					params.put("RET", list);
				}
				
			} else {
				params.clear();
				params.put("MSG", "OK");
				params.put("CODE", "200");
				params.put("RET", list);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}

		
	}

	public void selectRealStorySearchArea(Map<String, Object> params) {
		emptyRemoveMap(params);
		List<Object> list = new ArrayList<Object>();

		try {
			list = sqlSession.selectList("selectRealStorySearchArea", params);
		} catch (Exception e) {
			e.printStackTrace();
		}

		params.clear();
		params.put("RET", list);
	}

	public void selectRealStorySearchStore(Map<String, Object> params) {
		emptyRemoveMap(params);
		List<Object> list = new ArrayList<Object>();

		try {
			list = sqlSession.selectList("selectRealStorySearchStore", params);
		} catch (Exception e) {
			e.printStackTrace();
		}

		params.clear();
		params.put("RET", list);
	}

	public void insertRealStoryVideo(Map<String, Object> params) {
		log.debug("params:" + params);
		emptyRemoveMap(params);

		// 예외처리 해야함
		int i = sqlSession.insert("insertRealStoryVideo", params);
	}

	public void insertRealStoryBroadCast(Map<String, Object> params) {
		log.debug("params:" + params);
		emptyRemoveMap(params);

		// 예외처리 해야함
		int i = sqlSession.insert("insertRealStoryBroadCast", params);
	}

	public void insertRealStoryBoard(Map<String, Object> params) {
		log.debug("params:" + params);
		emptyRemoveMap(params);

		// 예외처리 해야함
		int i = sqlSession.insert("insertRealStoryBoard", params);
	}

	public void selectOneRealStory(Map<String, Object> modelMap) {
		emptyRemoveMap(modelMap);
		
		Map m = sqlSession.selectOne("selectOneRealStory", modelMap);

		modelMap.clear();
		modelMap.put("RET", m);
	}

	public void updateRealStoryBroadCast(Map<String, Object> modelMap) {
		log.debug("param:" + modelMap);
		emptyRemoveMap(modelMap);

		int ret = sqlSession.update("updateRealStoryBroadCast", modelMap);

//		modelMap.clear();
//		modelMap.put("RET", ret);
	}

	public void updateRealStoryVideo(Map<String, Object> modelMap) {
		log.debug("param:" + modelMap);
		emptyRemoveMap(modelMap);

		int ret = sqlSession.update("updateRealStoryVideo", modelMap);
	}

	public void updateRealStoryBoard(Map<String, Object> modelMap) {
		log.debug("param:" + modelMap);
		emptyRemoveMap(modelMap);

		int ret = sqlSession.update("updateRealStoryBoard", modelMap);
	}

	public void deleteRealStoryVideo(Map<String, Object> modelMap) {
		emptyRemoveMap(modelMap);
		int i = -1;
		try {
			i = sqlSession.delete("deleteRealStoryVideo", modelMap);
		} catch (Exception e) {
			e.printStackTrace();
		} 
	}
	
	public void deleteRealStoryBroadCast(Map<String, Object> modelMap) {
		emptyRemoveMap(modelMap);
		int i = -1;
		try {
			i = sqlSession.delete("deleteRealStoryBroadCast", modelMap);
		} catch (Exception e) {
			e.printStackTrace();
		} 
	}
	
	/**
	 * 리얼스토리, 메인동영상 delete
	 * @param modelMap
	 */
	public void deleteRealStoryBoard(Map<String, Object> modelMap) {
		emptyRemoveMap(modelMap);
		int i = -1;
		try {
			i = sqlSession.delete("deleteRealStoryBoard", modelMap);
		} catch (Exception e) {
			e.printStackTrace();
		} 
	}

	/**
	 * 리얼스토리, 메인동영상 전체댓글 List
	 * @param params
	 */
	public void selectReplyRealStory(Map<String, Object> params) {
		emptyRemoveMap(params);
		List<Object> list = new ArrayList<Object>();

		try {
			list = sqlSession.selectList("selectReplyRealStory", params);
		} catch (Exception e) {
			e.printStackTrace();
		}

		params.clear();
		params.put("RET", list);
	}
}