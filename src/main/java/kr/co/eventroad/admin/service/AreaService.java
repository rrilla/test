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
public class AreaService extends RootService {

	/**
	 * sql connector
	 */
	@Autowired
	private SqlSession sqlSession;
	
	public List<?> selectAreaList(Map<String, Object> paramMap) {
		return sqlSession.selectList("selectAreaList", paramMap);
	}
	
	
	
	
	
	
	
	
	
	
	public List<?> selectArea(Map<String, Object> paramMap) {
		return sqlSession.selectList("selectArea", paramMap);
	}
}