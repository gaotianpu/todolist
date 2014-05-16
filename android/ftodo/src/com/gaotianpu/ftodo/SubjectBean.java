package com.gaotianpu.ftodo;

public class SubjectBean {
	public static final String PK_ID = "pk_id";
	public static final String BODY = "body";
	public static final String CREATION_DATE = "creation_date";
	
	private long pk_id;
	private String body;
	private int creation_date;
	
	public long getId() {
		return pk_id;
	}
	public void setId(long id) {
		this.pk_id = id;
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
}
