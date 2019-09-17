package kr.co.ehr.user.service;

import kr.co.ehr.cmn.DTO;

public class UserVO extends DTO {
	/**이름 */
	private String name;
	/**성별 */
	private String sex;
	/**전화 */
	private String tel;
	/**나이 */
	private String age;
	
	/**나이 */
	private String cell_phone;
	
	
	public String getCell_phone() {
		return cell_phone;
	}

	public void setCell_phone(String cell_phone) {
		this.cell_phone = cell_phone;
	}

	public UserVO() {}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSex() {
		return sex;
	}

	public void setSex(String sex) {
		this.sex = sex;
	}

	public String getTel() {
		return tel;
	}

	public void setTel(String tel) {
		this.tel = tel;
	}

	public String getAge() {
		return age;
	}

	public void setAge(String age) {
		this.age = age;
	}

	@Override
	public String toString() {
		return "UserVO [name=" + name + ", sex=" + sex + ", tel=" + tel + ", age=" + age + ", cell_phone=" + cell_phone
				+ ", toString()=" + super.toString() + "]";
	}


	
	

}
