package kr.co.ehr;


//import static com.ehr.service.UserServiceImpl.MIN_LOGINCOUNT_FOR_SILVER;
//import static com.ehr.service.UserServiceImpl.MIN_RECCOMEND_FOR_GOLD;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.GenericXmlApplicationContext;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
//import com.ehr.service.UserService;
import org.springframework.web.context.WebApplicationContext;

import kr.co.ehr.user.service.Level;
import kr.co.ehr.user.service.Search;
import kr.co.ehr.user.service.User;
import kr.co.ehr.user.service.UserDao;
import kr.co.ehr.user.service.UserService;
import kr.co.ehr.user.service.impl.UserDaoJdbc;

@WebAppConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"file:src/main/webapp/WEB-INF/spring/root-context.xml"
		                          ,"file:src/main/webapp/WEB-INF/spring/appServlet/servlet-context.xml"})
public class UserDaoJunitFinal {
	
	private Logger LOG = LoggerFactory.getLogger(UserDaoJunitFinal.class);
	
	@Autowired
	WebApplicationContext context;
	
	@Autowired
	private UserDao dao;
	
	List<User> users;
	
	@Autowired
	UserService userService;
	
	@Before
	public void setUp() {
		LOG.debug("^^^^^^^^^^^^^^^^^^");
		LOG.debug("setUp()");
		LOG.debug("^^^^^^^^^^^^^^^^^^");
		
		users = Arrays.asList(
				 new User("j01_124","이상무01","1234",Level.BASIC,49,0,"jamesol@paran.com","2019/08/23")
				,new User("j02_124","이상무02","1234",Level.BASIC,50,0,"jamesol@paran.com","2019/08/23") //BASIC -> SILVER
				,new User("j03_124","이상무03","1234",Level.SILVER,50,29,"jamesol@paran.com","2019/08/23")
				,new User("j04_124","이상무04","1234",Level.SILVER,50,30,"jamesol@paran.com","2019/08/23") //SILVER -> GOLD
				,new User("j05_124","이상무05","1234",Level.GOLD,99,99,"jamesol@paran.com","2019/08/23")
				);
		
		
		LOG.debug("========================");
		LOG.debug("dao:"+dao);
		LOG.debug("========================");
		
	}
	
	
	
	@After
	public void tearDown() {
		LOG.debug("^^^^^^^^^^^^^^^^^^");
		LOG.debug("99 tearDown()");
		LOG.debug("^^^^^^^^^^^^^^^^^^");
	}
	
	private void checkUser(User user, Level expectedLevel) {
		User userUpdate = dao.get(user.getU_id());
		assertThat(userUpdate.gethLevel(), is(expectedLevel));
	}
	 
	@Test
	@Ignore
	public void upgradeAllOrNothing() throws SQLException {
		//전체 삭제
		dao.deleteAll();
		
		//users입력
		for(User user: users) {
			dao.add(user);
		}
		
		try {
			userService.tx_upgradeLevels();
		} catch (RuntimeException t) {
			LOG.debug("======================================");
			LOG.debug("=RuntimeException="+t.getMessage());
			LOG.debug("======================================");			
		}
		//트랜잭션처리 되면 Level.BASIC rollback;
		//checkUser(users.get(1), Level.BASIC);
	}	
	  
	@Test
	@Ignore
	public void update() {
		
		LOG.debug("======================================");
		LOG.debug("=01. 기존 데이터 삭제=");
		LOG.debug("======================================");
		for(User user: users) {
			dao.deleteUser(user);
		}		
		
		LOG.debug("======================================");
		LOG.debug("=02. 데이터 입력=");
		LOG.debug("======================================");
		dao.add(users.get(0));
		
		
		LOG.debug("======================================");
		LOG.debug("=03. 입력데이터 수정=");
		LOG.debug("=03.1 user01 수정=");
		LOG.debug("=03.1 user01 업데이트 수행=");
		LOG.debug("======================================");
		
		//User user01 = new User("j01_126","강슬기UU","1234",Level.BASIC,1,0,"zz@hanmail.net","2019/08/23");
		User user01 = users.get(0);
		user01.setName("강슬기U");
		user01.setPasswd("1234U");
		user01.sethLevel(Level.GOLD);
		user01.setLogin(99);
		user01.setRecommend(999);
		user01.setEmail("Uzz@hanmail.net");
		
		dao.update(user01);
		
		
		LOG.debug("======================================");
		LOG.debug("=04. 수정데이터와 3번 비교=");
		LOG.debug("======================================");
		User vsUser = dao.get(user01.getU_id());
		
		assertTrue(user01.equals(vsUser));
	}
	
	
	
	@Test
	@Ignore
	public void retrieve() {
		Search vo = new Search(10,1,"",""); //생성자 참고해서 하드코딩
		List<User> list = dao.retrieve(vo); //List에 담아야함. User에 대한내용을
		
		for(User user : list) {
			LOG.debug(user.toString());
		}
		
		
	}
	
	
	
	@Test
	@Ignore
	public void getAll() throws SQLException {

		
		List<User> list = dao.getAll();
		for(User user:list) {
			LOG.debug(user.toString());
		}
	}
	
	
	
	@Test(expected = EmptyResultDataAccessException.class) //예외 발생 시 true
	@Ignore
	public void getFailure() throws ClassNotFoundException, SQLException {
		LOG.debug("======================================");
		LOG.debug("=01. 기존 데이터 삭제=");
		LOG.debug("======================================");
		for(User user: users) {
			dao.deleteUser(user);
		}
		
		
		assertThat(dao.count("_124"), is(0));
		
		dao.get("unknownUserId");
	}
	
	
	@Test
	public void count() throws ClassNotFoundException, SQLException {
		LOG.debug("======================================");
		LOG.debug("=01. 기존 데이터 삭제=");
		LOG.debug("======================================");
		for(User user: users) {
			dao.deleteUser(user);
		}
		assertThat(dao.count("_124"), is(0));
		
		
		//----------------------------------
		//1건 추가
		//----------------------------------
		dao.add(users.get(0));
		assertThat(dao.count("_124"), is(1));
		
		//----------------------------------
		//1건 추가
		//----------------------------------
		dao.add(users.get(1));
		assertThat(dao.count("_124"), is(2));
		
		//----------------------------------
		//1건 추가
		//----------------------------------
		dao.add(users.get(2));
		assertThat(dao.count("_124"), is(3));
		
		//----------------------------------
		//count:3
		//----------------------------------
		
		
	}
	
	
	 
	@Test(timeout=5000) //1.JUnit에게 테스트용 메소드임을 알려줌
	@Ignore
	public void addAndGet() { //2.public
		LOG.debug("======================================");
		LOG.debug("=01. 기존 데이터 삭제=");
		LOG.debug("======================================");
		for(User user: users) {
			dao.deleteUser(user);
		}
		
		LOG.debug("======================================");
		LOG.debug("=02. 단건등록=");
		LOG.debug("======================================");
        int flag = dao.add(users.get(0));         
        flag = dao.add(users.get(1));         
        flag = dao.add(users.get(2));   
        flag = dao.add(users.get(3)); 
        flag = dao.add(users.get(4)); 
	    LOG.debug("======================================");
	    LOG.debug("=01.01 add flag="+flag);
	    LOG.debug("======================================");
		assertThat(flag, is(1));
        assertThat(dao.count("_124"), is(5)); //3건 넣었으니 3개여야 함
        
        User  userOne = dao.get(users.get(0).getU_id());
		
		LOG.debug("======================================");
		LOG.debug("=02. 단건조회=");
		LOG.debug("======================================");
		assertThat(userOne.getU_id(), is(users.get(0).getU_id()));
		assertThat(userOne.getPasswd(), is(users.get(0).getPasswd()));
			
	}
	
}
