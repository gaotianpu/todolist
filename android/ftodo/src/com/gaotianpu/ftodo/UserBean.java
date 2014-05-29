package com.gaotianpu.ftodo;

public class UserBean {

	private long user_id;
	private long mobile;
	private String email;
	private String display_name;
	private String access_token;
	private int token_status;
	 

	public long getUserId() {
		return user_id;
	}

	public void setUserId(long user_id) {
		this.user_id = user_id;
	}

	public long getMobile() {
		return mobile;
	}

	public void setMobile(long mobile) {
		this.mobile = mobile;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getDisplayName() {
		return display_name;
	}

	public void setDisplayName(String display_name) {
		this.display_name = display_name;
	}

	public String getAccessToken() {
		return access_token;
	}

	public void setAccessToken(String access_token) {
		this.access_token = access_token;
	}
	
	public int getTokenStatus() {
		return token_status;
	}

	public void setTokenStatus(int token_status) {
		this.token_status = token_status;
	}

}
