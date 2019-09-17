/**
 * 
 */
package kr.co.ehr.user.service.impl;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import kr.co.ehr.cmn.DTO;
import kr.co.ehr.cmn.WorkDiv;
import kr.co.ehr.user.service.UserVO;

/**
 * @author sist
 *
 */
@Repository
public class UserDaoImpl implements WorkDiv {
	Logger LOG=LoggerFactory.getLogger(this.getClass());
	
	
	@Override
	public int do_update(DTO dto) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int do_delete(DTO dto) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int do_save(DTO dto) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public DTO get_selectOne(DTO dto) {
		UserVO   inVO = (UserVO) dto;
		LOG.debug("==============================");
		LOG.debug("=@Repository param="+inVO);
		LOG.debug("==============================");
		
		UserVO outVO = inVO;
		return outVO;
	}

	@Override
	public List<?> get_retrieve(DTO dto) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<?> get_excelDown(DTO dto) {
		// TODO Auto-generated method stub
		return null;
	}

}
