package com.gaotianpu.ftodo;

public class SubjectBean {
	public static final String PK_ID = "pk_id";
	public static final String BODY = "body";
	public static final String CREATION_DATE = "creation_date";
	
	private long pk_id;
	private long remote_id;
	
	private String body;
	private int creation_date;
	
	private long user_id;
	
	public long getId() {
		return pk_id;
	}
	public void setId(long id) {
		this.pk_id = id;
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
	public int getCreationDate() {
		return creation_date;
	}
	public void setCreationDate(int creation_date) {
		this.creation_date = creation_date;
	}
	
	public long getRemoteId() {
		return remote_id;
	}
	public void setRemoteId(long id) {
		this.remote_id = id;
	}
}
