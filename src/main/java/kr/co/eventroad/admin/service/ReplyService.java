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
 * @마지막수정날자 2014. 8. 26.
 * @클래스설명 notice service
 */
@Service
@Transactional
public class ReplyService extends RootService {

	/**
	 * sql connector
	 */
	@Autowired
	private SqlSession sqlSession;
	
	public void insertReply(Map<String, Object> params) throws Exception {
		log.debug("params:" + params);
		emptyRemoveMap(params);

		// 예외처리 해야함
		int i = sqlSession.insert("insertReply", params);
		
		if(i==0) {
			throw new Exception(Constant.CODE_SESSION_EXPIRED);
		}
	}
	
	public void deleteReply(Map<String, Object> paramMap) {
		log.debug("paramMap:" + paramMap);
		emptyRemoveMap(paramMap);

		sqlSession.delete("deleteReply", paramMap);
	}
	
	public List<?> selectReplyList(Map<String, Object> paramMap) {
		return sqlSession.selectList("selectReplyList", paramMap);
	}
	
	public List<?> selectPushNoticeUserOne(Map<String, Object> paramMap) {
		return sqlSession.selectList("selectPushNoticeUserOne", paramMap);
	}
	
	public List<?> selectPushAlbumUserOne(Map<String, Object> paramMap) {
		return sqlSession.selectList("selectPushAlbumUserOne", paramMap);
	}
	
		
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	/**
	 * 리뷰 정보
	 * @param params
	 */
	public void selectAdminReviewInfo(Map<String, Object> params) {
		emptyRemoveMap(params);
		List<Object> list = new ArrayList<Object>();

		try {
			list = sqlSession.selectList("selectAdminReviewInfo", params);
		} catch (Exception e) {
			e.printStackTrace();
		}
//		params.clear();
		params.put("RET", list);
	}

	/**
	 * 리뷰 차단
	 * @param params
	 */
	public void updateBoard(Map<String, Object> params) {
		log.debug("params:" + params);
		emptyRemoveMap(params);

		int i = sqlSession.update("updateBoard", params);
	}
}