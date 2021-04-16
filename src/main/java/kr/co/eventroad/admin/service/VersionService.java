package kr.co.eventroad.admin.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kr.co.eventroad.admin.common.RootService;

/**
 * @제작자 jwdoe
 * @마지막수정날자 2014. 8. 4.
 * @클래스설명 inquery service
 */
@Service
@Transactional
public class VersionService extends RootService {

	/**
	 * sql connector
	 */
	@Autowired
	private SqlSession sqlSession;

	/**
	 * 버전 정보 list
	 * @param modelMap
	 * @throws Exception
	 */

	public void selectAdminVersion(Map<String, Object> params) {
		emptyRemoveMap(params);
		List<Object> list = new ArrayList<Object>();
		try {
			list = sqlSession.selectList("selectAdminVersion", params);
		} catch (Exception e) {
			e.printStackTrace();
		}

		params.clear();
		params.put("RET", list);
	}

	public void insertVersion(Map<String, Object> modelMap) throws Exception {
		log.debug("insertVersion param:" + modelMap);
		emptyRemoveMap(modelMap);

		int ret = sqlSession.update("insertVersion", modelMap);
	}
}