package com.gaotianpu.ftodo.da;

/**
 * 基于android-async-http实现http访问
 * https://github.com/loopj/android-async-http
 * 
 *
 **/

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.util.Log;

import com.loopj.android.http.*;

public class FTDClient {
	private final String AUTH_URL = "https://ftodo.sinaapp.com/register";  
	private final String RES_BASE_URL = "http://ftodo.sinaapp.com/api/";

	private AsyncHttpClient client;
	// private PersistentCookieStore myCookieStore;

	public FTDClient(Context context) {
		client = new AsyncHttpClient();
		client.setTimeout(20000);

		// myCookieStore = new PersistentCookieStore(context);
		// client.setCookieStore(myCookieStore);

		// 单例模式？
	}
	
	public void update_task(long user_id,String access_token,long remote_id,String content,AsyncHttpResponseHandler responseHandler){
		String url = RES_BASE_URL + "edit";
		
		RequestParams params = new RequestParams();
		params.put("user_id", String.valueOf(user_id));
		params.put("remote_id", String.valueOf(remote_id));
		params.put("access_token", access_token);
		params.put("content", content);
		client.post(url, params, responseHandler);
		return;
	}

	public void post_task(long user_id, String access_token, long remote_id, String content, String device_type,
			String devie_no, long local_id, String creation_date,String last_update,boolean is_todo,boolean is_remind,
			int local_version,
			int is_del,
			AsyncHttpResponseHandler responseHandler) {
		
		String edit_or_new = (remote_id==0 ) ? "new" : "edit";
		String url = RES_BASE_URL + edit_or_new;
		
		Log.i("AsyncService",url);

		RequestParams params = new RequestParams();
		params.put("user_id", String.valueOf(user_id));
		params.put("access_token", access_token);
		params.put("content", content);
		params.put("creation_date", creation_date); // 
		params.put("last_update", last_update); // 
		params.put("is_todo", String.valueOf(is_todo ? 1 :0) );
		params.put("is_remind", String.valueOf(is_remind ? 1 : 0)  );
		params.put("local_id", String.valueOf(local_id) );
		params.put("remote_id", String.valueOf(remote_id) );
		params.put("local_version", String.valueOf(local_version) );
		params.put("is_del", String.valueOf(is_del) );
		
		Log.i("AsyncService",params.toString());
		

		client.post(url, params, responseHandler);
		return;

		// local_uniq_id = {cust_id}_{device_no}_{sqlite_pk_id}
		// remote_id = Զ�̵�pk_id

		// ����� local_uniq_id,body,creation_date,cust_id
		// Ȩ����֤����
	}

	public void load_by_custId(long user_id, String access_token,long offset, int page_size, 
			AsyncHttpResponseHandler responseHandler) {
		String url = RES_BASE_URL + "list";

		RequestParams params = new RequestParams();
		params.put("user_id", String.valueOf(user_id));
		params.put("access_token", access_token);
		params.put("offset", String.valueOf(offset));
		params.put("size", String.valueOf(page_size));

		client.get(url, params, responseHandler);
		return;

	}

	public void load_by_last_async_remote_id(long user_id, String access_token,long last_remote_id,
			int size, AsyncHttpResponseHandler responseHandler) {
		String url = RES_BASE_URL + "list3";

		RequestParams params = new RequestParams();
		params.put("user_id", String.valueOf(user_id));
		params.put("access_token", access_token);
		params.put("min_pk_id", String.valueOf(last_remote_id));
		params.put("size", String.valueOf(size));

		client.get(url, params, responseHandler);

	}
	
	public void search(long user_id,String access_token,String query, long offset, int size, AsyncHttpResponseHandler responseHandler){
		//public void update_task(long user_id,String access_token,long remote_id,String content,AsyncHttpResponseHandler responseHandler){
		String url = RES_BASE_URL + "search";
		
		RequestParams params = new RequestParams();
		params.put("user_id", String.valueOf(user_id));
		params.put("access_token", access_token);
		params.put("query", query); 
		params.put("offset", String.valueOf(offset));
		params.put("size", String.valueOf(size));
		client.get(url, params, responseHandler);
		return;
	 
	}
	

	public void load_total(long user_id, String access_token,
			AsyncHttpResponseHandler responseHandler) {
		String url = RES_BASE_URL + "total";

		RequestParams params = new RequestParams();
		params.put("user_id", String.valueOf(user_id));
		params.put("access_token", access_token);

		client.get(url, params, responseHandler);
	}

	public static List<SubjectBean> Json2SubjectList(JSONObject result) {
		List<SubjectBean> subjectList = new ArrayList<SubjectBean>();

		try {
			JSONArray resultList = result.getJSONArray("list"); 

			for (int i = 0; i < resultList.length(); i++) {
				JSONObject item = resultList.getJSONObject(i);

				SubjectBean subject = new SubjectBean();

				if (!item.isNull("local_id")) {
					subject.setId(item.getLong("local_id"));
				} else {
					subject.setId(0);
				}
				subject.setUserId(item.getLong("user_id")); //user_id
				subject.setRemoteId(item.getLong("pk_id"));
				subject.setBody(item.getString("body"));
				subject.setCreationDate(item.getString("created_date")); 
				subject.setIsDel(item.getInt("is_delete")); 

				subjectList.add(subject);

				// item.getString("created_date");

			}

		} catch (JSONException e) {
			Log.e("Json2SubjectList", e.toString());
		}

		return subjectList;
	}

	public void login_or_register(String name, String password,
			String device_no, String device_type, String os_type,
			AsyncHttpResponseHandler responseHandler) {

		RequestParams params = new RequestParams();
		params.put("name", name);
		params.put("password", password);
		params.put("device_no", device_no);
		params.put("device_type", device_type);
		params.put("os_type", os_type);

		client.post(AUTH_URL, params, responseHandler);
	} 

}
