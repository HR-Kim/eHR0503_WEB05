package kr.co.ehr.user.service;

import java.sql.SQLException;
import java.util.List;

public interface UserService {

	public String get_excelDown(Search vo,String ext);
	public int update(User user);
	public List<User> retrieve(Search vo);
	public int deleteUser(User user);
	public User get(String id);
	
	// 최초 사용자 베이직 레벨
	public int add(User user);

	public void tx_upgradeLevels() throws SQLException;
}
