package utils;

import java.io.Serializable;

import javax.enterprise.context.SessionScoped;

@SessionScoped
public class UserSession implements Serializable{

	private static final long serialVersionUID = 1229764;
	
	private String username;
	private long userid;
	
	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public long getUserid() {
		return userid;
	}

	public void setUserid(long userid) {
		this.userid = userid;
	}
	
	
	
}
