package com.gaotianpu.ftodo.da;

public class SubjectBean {
	public static final String PK_ID = "pk_id";
	public static final String BODY = "body";
	public static final String CREATION_DATE = "creation_date";
	
	private long pk_id;
	private long remote_id;
	private long parent_id=0;
	
	private long user_id;
	private String body;
	private String creation_date;
	private String update_date;
	
	private boolean is_todo=false;
	private boolean is_remind=false; 
	
	private int updateVersion=0;
	
	public long getUpdateVersion() {
		return updateVersion;
	}
	public void setUpdateVersion(int updateVersion) {
		this.updateVersion = updateVersion;
	}
	
	
	public long getId() {
		return pk_id;
	}
	public void setId(long id) {
		this.pk_id = id;
	}
	
	public long getParentId() {
		return parent_id;
	}
	public void setParentId(long parent_id) {
		this.parent_id = parent_id;
	}
	
	public long getUserId() {
		return user_id;
	}
	public void setUserId(long user_id) {
		this.user_id = user_id;
	}
	
	public String getBody() {
		return body;
	}
	public void setBody(String body) {
		this.body = body;
	}
	public String getCreationDate() {
		return creation_date;
	}
	public void setCreationDate(String creation_date) {
		this.creation_date = creation_date;
	}
	
	public String getUpdateDate() {
		return update_date;
	}
	public void setUpdateDate(String update_date) {
		this.update_date = update_date;
	}
	
	public long getRemoteId() {
		return remote_id;
	}
	public void setRemoteId(long id) {
		this.remote_id = id;
	}
	
	//
	public boolean getIsTodo() {
		return is_todo;
	}
	public void setIsTodo(boolean t) {
		this.is_todo = t;
	}
	//
	public boolean getIsRemind() {
		return is_remind;
	}
	public void setIsRemind(boolean r) {
		this.is_remind = r;
	}
	//
}
