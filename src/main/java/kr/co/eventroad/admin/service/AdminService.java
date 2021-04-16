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
 * admin service
 * @author jinun song
 * @since 2014.07.22
 */
@Service
@Transactional
public class AdminService extends RootService {

	/**
	 * sql connector
	 */
	@Autowired
	private SqlSession sqlSession;

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
	 * login admin by id and pw
	 * @param modelMap
	 * @throws Exception
	 */
	public void login(Map<String, Object> modelMap) throws Exception {
		log.debug("param:" + modelMap);
		emptyRemoveMap(modelMap);

		Map m = sqlSession.selectOne("getAdminByIdPw", modelMap);

		modelMap.clear();
		modelMap.put("RET", m);
	}

	/**
	 * update admin passwd
	 * @param modelMap
	 * @throws Exception
	 */
	public void changePw(Map<String, Object> modelMap) throws Exception {
		log.debug("param:" + modelMap);
		emptyRemoveMap(modelMap);

		int ret = sqlSession.update("updateAdminPw", modelMap);

		modelMap.clear();
		modelMap.put("RET", ret);
	}
	
	public void insertNotice(Map<String, Object> paramMap) throws Exception {
		log.debug("insertNotice paramMap:" + paramMap);
		emptyRemoveMap(paramMap);

		int ret = sqlSession.insert("insertNotice", paramMap);
		if(ret<1){
			throw new Exception(Constant.MSG_INSERT_FAIL);
		}
	}
	
	public void updateNotice(Map<String, Object> paramMap) {
		sqlSession.update("updateNotice", paramMap);
	}
	
	public void insertNanum(Map<String, Object> paramMap) throws Exception {
		log.debug("insertNanum paramMap:" + paramMap);
		emptyRemoveMap(paramMap);

		int ret = sqlSession.insert("insertNanum", paramMap);
		if(ret<1){
			throw new Exception(Constant.MSG_INSERT_FAIL);
		}
	}
	
	public void updateNanum(Map<String, Object> paramMap) {
		sqlSession.update("updateNanum", paramMap);
	}
	
	
	
	
	
	/**
	 * update max ver
	 * @param modelMap
	 * @throws Exception
	 */
	public void selectRegInquery(Map<String, Object> modelMap) throws Exception {
		log.debug("param:" + modelMap);
		emptyRemoveMap(modelMap);

		int ret = sqlSession.selectOne("getRegInquery", modelMap);

		modelMap.clear();
		modelMap.put("RET", ret);
	}
	
	/**
	 * update 등록 된 계정 가져오기
	 * @param modelMap
	 * @throws Exception
	 */
	public void selectAllAccount(Map<String, Object> params) throws Exception {
		emptyRemoveMap(params);
		List<Object> list = new ArrayList<Object>();

		try {
			list = sqlSession.selectList("selectAllAccount", params);
		} catch (Exception e) {
			e.printStackTrace();
		}

		params.clear();
		params.put("MSG", "OK");
		params.put("RET", list);
	}

	/**
	 * 관리자 insert
	 * @param modelMap
	 */
	public void insertAdminUser(Map<String, Object> modelMap) {
		log.debug("insertVersion param:" + modelMap);
		emptyRemoveMap(modelMap);

		int ret = sqlSession.update("insertAdminUser", modelMap);
	}

	/**
	 * 잠금 계정 insert
	 * @param modelMap
	 */
	public void insertRockUser(Map<String, Object> modelMap) {
		log.debug("insertVersion param:" + modelMap);
		emptyRemoveMap(modelMap);

		int ret = sqlSession.update("insertRockUser", modelMap);
	}

	/**
	 * 잠금 신청 List
	 * @param params
	 */
	public void selectAdminRockMNG(Map<String, Object> params) {
		emptyRemoveMap(params);
		List<Object> list = new ArrayList<Object>();

		try {
			list = sqlSession.selectList("selectAdminRockMNG", params);
		} catch (Exception e) {
			e.printStackTrace();
		}
		params.put("MSG", "OK");
		params.put("RET", list);
	}

	/**
	 * 계정 잠금 해제
	 * @param modelMap
	 */
	public void updateAdminRockMNG(Map<String, Object> modelMap) {
		log.debug("param:" + modelMap);
		emptyRemoveMap(modelMap);

		int ret = sqlSession.update("updateAdminRockMNG", modelMap);

		modelMap.clear();
		modelMap.put("RET", ret);
	}

	/**
	 * 중복체크나 사용자 정보 가져오기 위함
	 * @param modelMap
	 */
	public void selectAdminUserCheck(Map<String, Object> modelMap) {
		log.debug("param:" + modelMap);
		emptyRemoveMap(modelMap);

		Map m = sqlSession.selectOne("selectAdminUserCheck", modelMap);
		modelMap.clear();
		if(m !=null){
			modelMap.putAll(m);
		}
	}

	public void selectAdminRockUserCheck(Map<String, Object> modelMap) {
		log.debug("param:" + modelMap);
		emptyRemoveMap(modelMap);

		Map m = sqlSession.selectOne("selectAdminRockUserCheck", modelMap);
		modelMap.clear();
		if(m !=null){
			modelMap.putAll(m);
		}
	}

	/**
	 * 유저 정보 수정
	 * @param modelMap
	 */
	public void updateAdminAccount(Map<String, Object> modelMap) {
		log.debug("param updateAdminAccount :" + modelMap);
		emptyRemoveMap(modelMap);

		int ret = sqlSession.update("updateAdminAccount", modelMap);
		modelMap.put("RET", ret);
	}

	public void getChangeAddressToGeocode(Map<String, Object> params) {
		emptyRemoveMap(params);
		List<Object> list = new ArrayList<Object>();

		try {
			list = sqlSession.selectList("getChangeAddressToGeocode", params);
			
			
//			String[] li = null;
//			for(int i=0;i<list.size();i++){
//				li = ((Map)list.get(i)).get("ADDR_DETAIL").toString().split(" ");
//				((Map)list.get(i)).put("ADDR_DETAIL", li[2]);
//			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}

		params.clear();
		params.put("MSG", "OK");
		params.put("CODE", "200");
		params.put("RET", list);
	}

	public void setChangeAddressToGeocode(Map<String, Object> modelMap) {
		log.debug("param:" + modelMap);
		emptyRemoveMap(modelMap);

		int ret = sqlSession.update("setChangeAddressToGeocode", modelMap);

		modelMap.put("RET", ret);
	}

	public void setInsertImageAddress(Map<String, Object> modelMap) {
		log.debug("param:" + modelMap);
		emptyRemoveMap(modelMap);

		int ret = sqlSession.update("setInsertImageAddress", modelMap);

		modelMap.put("RET", ret);
	}

	public void getAdminLogin(Map<String, Object> paramMap) throws Exception {
		emptyRemoveMap(paramMap);

		Map m = sqlSession.selectOne("getAdminLogin", paramMap);
		if(m == null){
			throw new Exception(Constant.MSG_NO_RETURN);
		}
		paramMap.clear();
		paramMap.putAll(m);
		
	}

	public List<?> getUserMember(Map<String, Object> paramMap) {
		emptyRemoveMap(paramMap);
		return sqlSession.selectList("getUserMember", paramMap);
	}

	public Map<?,?> getMember(Map<String, Object> paramMap) {
		return sqlSession.selectOne("getMember", paramMap);
	}

	
	
	
	
	
	public List<?> getLocalm(Map<String, Object> paramMap) {
		return sqlSession.selectList("getLocalm", paramMap);
	}

	public List<?> getCoupon(Map<String, Object> paramMap) {
		return sqlSession.selectList("getCoupon", paramMap);
	}

	public String getCouponCount(Map<String, Object> paramMap) {
		return sqlSession.selectOne("getCouponCount", paramMap);
	}

	public void insertCoupon(Map<String, Object> paramMap) throws Exception {
		log.debug("insertCoupon modelMap:" + paramMap);
		emptyRemoveMap(paramMap);

		int ret = sqlSession.insert("insertCoupon", paramMap);
		if(ret<1){
			throw new Exception(Constant.MSG_INSERT_FAIL);
		}
	}

	public void deleteCoupon(Map<String, Object> paramMap) {
		sqlSession.update("deleteCoupon", paramMap);
	}

	public void updateCoupon(Map<String, Object> paramMap) {
		sqlSession.update("updateCoupon", paramMap);
	}

	public List<?> getSingo(Map<String, Object> paramMap) {
		return sqlSession.selectList("getSingo", paramMap);
	}

	public String getSingoCount(Map<String, Object> paramMap) {
		return sqlSession.selectOne("getSingoCount", paramMap);
	}
	
	
	
	public List<?> selectAgencyList(Map<String, Object> paramMap) {
		emptyRemoveMap(paramMap);
		return sqlSession.selectList("selectAgencyList", paramMap);
	}
	
	public Map<String, Object> selectAgencyOne(Map<String, Object> paramMap) {
		emptyRemoveMap(paramMap);
		return sqlSession.selectOne("selectAgencyOne", paramMap);
	}
	
	public void updateUserEnabled(Map<String, Object> paramMap) {
		log.debug("paramMap:" + paramMap);
		emptyRemoveMap(paramMap);

		sqlSession.update("updateUserEnabled", paramMap);
	}
	
	public void updateMainBannerEnabled(Map<String, Object> paramMap) {
		log.debug("paramMap:" + paramMap);
		emptyRemoveMap(paramMap);

		sqlSession.update("updateMainBannerEnabled", paramMap);
	}
	
	public List<?> selectInquiryList(Map<String, Object> paramMap) {
		return sqlSession.selectList("selectInquiryList", paramMap);
	}

	public Integer selectInquiryCount(Map<String, Object> paramMap) {
		return sqlSession.selectOne("selectInquiryCount", paramMap);
	}

	public Map<String, Object> selectInquiryDetail(Map<String, Object> paramMap) {
		emptyRemoveMap(paramMap);
		return sqlSession.selectOne("selectInquiryDetail", paramMap);
	}

	public void updateInquiry(Map<String, Object> paramMap) {
		sqlSession.update("updateInquiry", paramMap);
	}
	
	public List<?> selectUserList(Map<String, Object> paramMap) {
		return sqlSession.selectList("selectUserList", paramMap);
	}
	
	public Integer selectUserCount(Map<String, Object> paramMap) {
		return sqlSession.selectOne("selectUserCount", paramMap);
	}
	
	public List<?> selectPushUserList(Map<String, Object> paramMap) {
		return sqlSession.selectList("selectPushUserList", paramMap);
	}

	public Integer selectNoticeCount(Map<String, Object> paramMap) {
		emptyRemoveMap(paramMap);
		return sqlSession.selectOne("selectNoticeCount", paramMap);
	}
	
	public List<?> selectNanumList(Map<String, Object> paramMap) {
		return sqlSession.selectList("selectNanumList", paramMap);
	}
	
	public Integer selectNanumCount(Map<String, Object> paramMap) {
		emptyRemoveMap(paramMap);
		return sqlSession.selectOne("selectNanumCount", paramMap);
	}
	
	public Integer selectAgencyCount(Map<String, Object> paramMap) {
		return sqlSession.selectOne("selectAgencyCount", paramMap);
	}
	
	public Map<String, Object> selectInquiryUser(Map<String, Object> paramMap) {
		emptyRemoveMap(paramMap);
		return sqlSession.selectOne("selectInquiryUser", paramMap);
	}

	public Map<String, Object> selectNanumOne(Map<String, Object> paramMap) {
		emptyRemoveMap(paramMap);
		return sqlSession.selectOne("selectNanumOne", paramMap);
	}

	public List<?> selectReviewList(Map<String, Object> paramMap) {
		return sqlSession.selectList("selectReviewList", paramMap);
	}
	
	public Integer selectReviewCount(Map<String, Object> paramMap) {
		return sqlSession.selectOne("selectReviewCount", paramMap);
	}
	
	public Map<String, Object> selectCheckNews(Map<String, Object> paramMap) {
		emptyRemoveMap(paramMap);
		return sqlSession.selectOne("admin.selectCheckNews", paramMap);
	}
	
	public void updateReviewEnabled(Map<String, Object> paramMap) {
		log.debug("paramMap:" + paramMap);
		emptyRemoveMap(paramMap);

		sqlSession.update("updateReviewEnabled", paramMap);
	}
	
	public Map<String, Object> selectReviewDetail(Map<String, Object> paramMap) {
		emptyRemoveMap(paramMap);
		return sqlSession.selectOne("selectReviewDetail", paramMap);
	}
	
	public void deleteNanum(Map<String, Object> paramMap) {
		log.debug("paramMap:" + paramMap);
		emptyRemoveMap(paramMap);

		sqlSession.delete("deleteNanum", paramMap);
	}
	
	
}