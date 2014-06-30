package com.gaotianpu.ftodo.bean;

public class DateBean {
	
	private String _id = "";
	
	private String _k = "";
	private boolean _v = false;

	public DateBean(String id, String k, boolean v) {
		_id = id;
		_k = k;
		_v = v;

	}
	
	public String getId() {
		return _id;
	}

	public String getK() {
		return _k;
	}

	public boolean getV() {
		return _v;
	}
	
	public void setV(boolean v) {
		 _v = v;
	}
}
