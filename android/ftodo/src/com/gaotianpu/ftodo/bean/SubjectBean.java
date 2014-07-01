package com.gaotianpu.ftodo.bean;

import java.util.Date;

import android.R;
import android.util.Log;

import com.gaotianpu.ftodo.da.Util;
 

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
	
	private int localVersion=0;
	
	public int getLocalVersion() {
		return localVersion;
	}
	public void setLocalVersion(int localVersion) {
		this.localVersion = localVersion;
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
	
	private int isDel=0;
	public int getIsDel() {
		return isDel;
	}
	public void setIsDel(int isDel) {
		this.isDel = isDel;
	}
	
	private String _plan_start_date;
	public String getPlanStartDate() {
		return _plan_start_date;
	}
	public void setPlanStartDate(String plan_start_date) {
		this._plan_start_date = plan_start_date;
	}
	
	public int getPlanStartSort(){
		Date d = Util.str2Date(_plan_start_date);
		if(d==null){
			return 0;
		}
		
		//Log.i("getPlanStartSort",Util.getDateStr(0));
		Log.i("getPlanStartSort",_plan_start_date);
		
		if(d.getTime() < Util.str2Date(Util.getDateStr(0)).getTime()){
			return 1;
		}
		if(_plan_start_date.equals(Util.getDateStr(0))  ){
			return 2;
		}
		
		if(_plan_start_date.equals(Util.getDateStr(1))  ){
			return 3;
		}
		
		if(_plan_start_date.equals(Util.getDateStr(2))  ){
			return 4;
		}
		
		if(d.getTime() > Util.str2Date(Util.getDateStr(0)).getTime()){
			return 5;
		}
		//this._plan_start_date
		//未设置 0
		//过期 1
		//今天 2
		//明天 3
		//后天 4
		//更晚 5
		
		//R.string.todo_plan_date_sort;
		
		return 0;
	}
	
	
}
