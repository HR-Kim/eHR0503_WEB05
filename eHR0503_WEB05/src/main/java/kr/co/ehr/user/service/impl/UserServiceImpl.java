package kr.co.ehr.user.service.impl;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import kr.co.ehr.cmn.UserExcelWriter;
import kr.co.ehr.user.service.Level;
import kr.co.ehr.user.service.Search;
import kr.co.ehr.user.service.User;
import kr.co.ehr.user.service.UserDao;
import kr.co.ehr.user.service.UserService;


@Service
public class UserServiceImpl implements UserService {
	private Logger LOG = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private MailSender mailSender;

	
	@Autowired
	private UserDao userDao; // 인터페이스 통해 만들어야함
	
	@Autowired
	private UserExcelWriter userExcelWriter;
	
	public static final int MIN_LOGINCOUNT_FOR_SILVER = 50;
	public static final int MIN_RECCOMEND_FOR_GOLD = 30;

	// 최초 사용자 베이직 레벨
	public int add(User user) {
		if (null == user.gethLevel()) {
			user.sethLevel(Level.BASIC);
		}
		return userDao.add(user);
	}

//	//level upgrade
//	//1. 전체 사용자를 조회
//	//2. 대상자를 선별
//	// 2.1. BASIC사용자, 로그인cnt가 50이상이면 : BASIC -> SILVER
//	// 2.2. SILVER사용자, 추천cnt가 300이상이면 : SILVER -> GOLD
//	//3. 대상자 업그레이드
//	public void upgradeLevels() {
//		int upCnt = 0;
//		
//		
//		//1.전체 사용자를 조회
//		List<User> users = userDao.getAll();
//		for(User user : users) {
//			Boolean changed = null;
//			
//			// BASIC -> SILVER
//			if(user.gethLevel() == Level.BASIC && user.getLogin() >= 50) {
//				user.sethLevel(Level.SILVER);
//				changed = true;
//				
//			//	SILVER -> GOLD
//			}else if(user.gethLevel() == Level.SILVER && user.getRecommend() >= 30) {
//				user.sethLevel(Level.GOLD);
//				changed = true;
//			}else if(user.gethLevel() == Level.GOLD) {
//				changed = false;
//			}else {
//				changed = false;
//			}
//			
//			
//			if(changed == true) {
//				userDao.update(user);
//				upCnt++; //업뎃 됐는지 확인 (전역변수 확인해)
//			}
//			
//		}//--for
//		
//		LOG.debug("upCnt:"+upCnt);
//		
//	}

	protected void upgradeLevel(User user)throws SQLException {
		//----------------------------------
		//-Transaction Test Source: 운영반영금지
		//----------------------------------
//		String id="j04_124";
//		if(user.getU_id().equals(id)) {
//			LOG.debug("=====upgradeLevel==user.getU_id()="+user.getU_id());
//			throw new RuntimeException(id+" 트랜잭션 HR테스트");
//		}
		
		user.upgradeLevel(); // VO부분에 기능을 만듦
		userDao.update(user);

		sendUpgradeMail(user);// mail send
	}


	/**
	 * 등업 사용자에게 mail 전송.
	 * 
	 * @param user
	 */
	private void sendUpgradeMail(User user) {
		try {
			// POP 서버명 : pop.naver.com
			// SMTP 서버명 : smtp.naver.com
			// POP 포트 : 995, 보안연결(SSL) 필요
			// SMTP 포트 : 465, 보안 연결(SSL) 필요
			// 아이디 : jamesol
			// 비밀번호 : 네이버 로그인 비밀번호
			// 보내는 사람
			String host = "smtp.naver.com";
			final String userName = "jamesol";
			final String password = "naver로그인 비번";
			int port = 465;

			// 받는사람
			String recipient = user.getEmail();
			// 제목
			String title = user.getName() + "님 등업(https://cafe.naver.com/kndjang)";
			// 내용
			String contents = user.getU_id() + "님의 등급이\n" + user.gethLevel().name() + "로 업되었습니다.";
			// SMTP서버 설정
			Properties props = System.getProperties();
			props.put("mail.smtp.host", host);
			props.put("mail.smtp.port", port);
			props.put("mail.smtp.auth", "true");
			props.put("mail.smtp.ssl.enable", "true");
			props.put("mail.smtp.ssl.trust", host);

			// 인증
			Session session = Session.getInstance(props, new Authenticator() {
				String uName = userName;
				String passwd = password;

				@Override
				protected PasswordAuthentication getPasswordAuthentication() {
					// TODO Auto-generated method stub
					return new PasswordAuthentication(uName, passwd);
				}
			});

			session.setDebug(true);

			SimpleMailMessage mimeMessage = new SimpleMailMessage();
			// 보내는 사람
			mimeMessage.setFrom("jamesol@naver.com");
			// 받는사람
			mimeMessage.setTo(recipient);
			// 제목
			mimeMessage.setSubject(title);
			// 내용
			mimeMessage.setText(contents);
			// 전송
			mailSender.send(mimeMessage);

		} catch (Exception e) {
			e.printStackTrace();
		}
		LOG.debug("==============================");
		LOG.debug("=mail send=");
		LOG.debug("==============================");
	}

	// level upgrade
	// 1. 전체 사용자를 조회
	// 2. 대상자를 선별
	// 2.1. BASIC사용자, 로그인cnt가 50이상이면 : BASIC -> SILVER
	// 2.2. SILVER사용자, 추천cnt가 300이상이면 : SILVER -> GOLD
	// 3. 대상자 업그레이드
	public void tx_upgradeLevels() throws SQLException {

		List<User> users = userDao.getAll();
		for (User user : users) {
			if (canUpgradeLevel(user) == true) {
				upgradeLevel(user);

			}
		} // --for

	}

	// 업그레이드 대상여부 파악 : true
	private boolean canUpgradeLevel(User user) {
		Level currLevel = user.gethLevel();

		switch (currLevel) {
		case BASIC:
			return (user.getLogin() >= MIN_LOGINCOUNT_FOR_SILVER);
		case SILVER:
			return (user.getRecommend() >= MIN_RECCOMEND_FOR_GOLD);
		case GOLD:
			return false;
		default:
			throw new IllegalArgumentException("Unknown Level:" + currLevel);
		}

	}

	@Override
	public int update(User user) {
		return userDao.update(user);
	}

	@Override
	public List<User> retrieve(Search vo) {
		return userDao.retrieve(vo);
	}

	@Override
	public int deleteUser(User user) {
		return userDao.deleteUser(user);
	}

	@Override
	public User get(String id) {
		return userDao.get(id);
	}

	@Override
	public String get_excelDown(Search vo,String ext) {
		List<User> list = userDao.retrieve(vo);
		List<String> headers = Arrays.asList("아이디"
				,"이름"
				,"비번"
				,"레벨"
				,"로그인"
				,"추천"
				,"이메일"
				,"등록일"
				,"레벨(Value)");		
		//String saveFileNm = userExcelWriter.xlsxWriterGeneralization(list, headers);
		String saveFileNm = "";
		
		if(ext.equals("xlsx")) {
			saveFileNm = userExcelWriter.xlsxWriterGeneralization(list, headers);
		}else if(ext.equals("csv")) {
			saveFileNm = userExcelWriter.csvWriterGeneralization(list, headers);
		}
		
		
		LOG.debug("==============================");
		LOG.debug("=saveFileNm="+saveFileNm);
		LOG.debug("==============================");
		return saveFileNm;
	}

}
