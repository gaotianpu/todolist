package com.gaotianpu.ftodo.bean;

public class UserBean {

	private long user_id=0;
	private long mobile=0;
	private String email="";
	private String display_name="";
	private String access_token="";
	private int token_status=0;
	private String _passwordLevel="";
	 

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
		return email.equals(null)  ? "" : email ;
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
	
	public void setPasswordLevel(String passwordLevel) {
		this._passwordLevel = passwordLevel;
	}

	public String getPasswordLevel() {
		return _passwordLevel;
	}

}
