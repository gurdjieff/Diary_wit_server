package med.aymen.service.Diary;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "projectdb")
public class Users 
{
	public int id;
	public String username;
	public String password;
	
	public Users() {
		this.username = "PayPal";
		this.password = "********";
	}
	
	public Users(String username,String password) {
		super();
		this.username = username;
		this.password = password;
	}

	@Override
	public String toString() {
		return "user [id=" + id + ", username=" + username + ", password=" + password + "]";
	}

}
