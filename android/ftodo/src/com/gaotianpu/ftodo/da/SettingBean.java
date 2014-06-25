package com.gaotianpu.ftodo.da;

public class SettingBean {
	
	private String _id = "";
	
	private String _k = "";
	private String _v = "";

	public SettingBean(String id, String k, String v) {
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

	public String getV() {
		return _v;
	}
}
