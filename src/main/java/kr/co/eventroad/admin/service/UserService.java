package kr.co.eventroad.admin.service;

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
 * user service
 * @author jw doe
 * @since 2016.01.22
 */
@Service
@Transactional
public class UserService extends RootService {

	@Autowired
	private SqlSession sqlSession;
	
	public Map<String, Object> selectUserIdCheck(Map<String, Object> paramMap) {
		return sqlSession.selectOne("selectUserIdCheck", paramMap);
	}
	
	public Map<String, Object> login(Map<String, Object> paramMap) {
		return sqlSession.selectOne("login", paramMap);
	}
	
	public void updateUser(Map<String, Object> paramMap) {
		sqlSession.update("updateUser", paramMap);
	}
	
	public String insertSign(Map<String, Object> paramMap) throws Exception	{
		emptyRemoveMap(paramMap);
		int i = sqlSession.insert("insertSign", paramMap);
		if(i==0) {
			throw new Exception(Constant.CODE_SESSION_EXPIRED);
		}
//		paramMap.clear();
		return paramMap.get("seq_user").toString();
	}
	
	public void insertAuth(Map<String, Object> paramMap) throws Exception {
		emptyRemoveMap(paramMap);
		int i = sqlSession.insert("insertAuth", paramMap);
		if(i==0) {
			throw new Exception(Constant.CODE_SESSION_EXPIRED);
		}
	}
	
	public void insertTitle(Map<String, Object> paramMap) throws Exception {
		emptyRemoveMap(paramMap);
		int i = sqlSession.insert("insertTitle", paramMap);
		if(i==0) {
			throw new Exception(Constant.CODE_SESSION_EXPIRED);
		}
	}
	
	public void updateTitle(Map<String, Object> paramMap) {
		sqlSession.update("updateTitle", paramMap);
	}
	
	public String selectUserReduplicationIdCheck(Map<String, Object> paramMap) {
		return sqlSession.selectOne("selectUserReduplicationIdCheck", paramMap);
	}
	
	public Map<String, Object> selectUserOne(Map<String, Object> paramMap) {
		return sqlSession.selectOne("selectUserOne", paramMap);
	}
	
	public void updateBeaconInit(Map<String, Object> paramMap) {
		sqlSession.update("updateBeaconInit", paramMap);
	}
	
	public void deleteKidsStationAll(Map<String, Object> paramMap) {
		log.debug("paramMap:" + paramMap);
		emptyRemoveMap(paramMap);

		sqlSession.delete("deleteKidsStationAll", paramMap);
	}
	
	public void deleteNewsAll(Map<String, Object> paramMap) {
		log.debug("paramMap:" + paramMap);
		emptyRemoveMap(paramMap);

		sqlSession.delete("deleteNewsAll", paramMap);
	}
	
	public void deleteApply(Map<String, Object> paramMap) {
		log.debug("paramMap:" + paramMap);
		emptyRemoveMap(paramMap);

		sqlSession.delete("deleteApply", paramMap);
	}
	
	public void updateCarInit(Map<String, Object> paramMap) {
		sqlSession.update("updateCarInit", paramMap);
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	public void insertUserEx(Map<String, Object> paramMap) throws Exception {
		emptyRemoveMap(paramMap);
		int i = sqlSession.insert("insertUserEx", paramMap);
		if(i==0) {
			throw new Exception(Constant.CODE_SESSION_EXPIRED);
		}
	}
	
	public void getUserLogin(Map<String, Object> modelMap) throws Exception	{
//		log.debug("param:" + modelMap);
		emptyRemoveMap(modelMap);

		Map m = sqlSession.selectOne("getUserLogin", modelMap);
		if(m == null){
			throw new Exception(Constant.MSG_NO_RETURN);
		}
		modelMap.clear();
		modelMap.putAll(m);
	}
	
	public List<?> selectUserInfoList(Map<String, Object> paramMap) throws Exception	{
//		log.debug("param:" + paramMap);
		emptyRemoveMap(paramMap);
		return sqlSession.selectList("selectUserInfoList", paramMap);
		/*Map m = sqlSession.selectOne("getUserInfo", paramMap);
		if(m == null){
			throw new Exception(Constant.MSG_NO_RETURN);
		}*/
		
//		paramMap.clear();
//		paramMap.putAll(m);
	}
	
	public Map<String, Object> selectUserEx(Map<String, Object> paramMap) {
		return sqlSession.selectOne("selectUserEx", paramMap);
	}

	public void updateUserEx(Map<String, Object> paramMap) {
		sqlSession.update("updateUserEx", paramMap);
	}

	public String selectUserPassword(Map<String, Object> paramMap) {
		return sqlSession.selectOne("selectUserPassword", paramMap);
	}
}