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
public class KindergardenService extends RootService {

	/**
	 * sql connector
	 */
	@Autowired
	private SqlSession sqlSession;
	
	public List<?> selectKindergardenList(Map<String, Object> paramMap) {
		return sqlSession.selectList("selectKindergardenList", paramMap);
	}
	
	public List<?> selectKindergardenClassList(Map<String, Object> paramMap) {
		return sqlSession.selectList("selectKindergardenClassList", paramMap);
	}
	
	public Map<String, Object> selectReqKindergardenApplyCheck(Map<String, Object> paramMap) {
		return sqlSession.selectOne("selectReqKindergardenApplyCheck", paramMap);
	}
	
	public void inserReqKindergardenApply(Map<String, Object> paramMap) throws Exception {
		emptyRemoveMap(paramMap);
		int i = sqlSession.insert("inserReqKindergardenApply", paramMap);
		if(i==0) {
			throw new Exception(Constant.CODE_SESSION_EXPIRED);
		}
	}
	
	public void updateReqKindergardenApply(Map<String, Object> paramMap) {
		sqlSession.update("updateReqKindergardenApply", paramMap);
	}
	
	public Map<String, Object> selectKindergardenOne(Map<String, Object> paramMap) {
		return sqlSession.selectOne("selectKindergardenOne", paramMap);
	}
	
}