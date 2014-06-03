package com.gaotianpu.ftodo;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.util.Log;

import com.loopj.android.http.*;

public class FTDClient {
	private final String REGISTER_URL = "https://ftodo.sinaapp.com/register";
	private final String AUTH_URL = "https://ftodo.sinaapp.com/login";

	private final String RES_BASE_URL = "http://ftodo.sinaapp.com/api/";

	private AsyncHttpClient client;
	private PersistentCookieStore myCookieStore;

	public FTDClient(Context context) {
		client = new AsyncHttpClient(); 
		client.setTimeout(20000);

		//myCookieStore = new PersistentCookieStore(context);
		//client.setCookieStore(myCookieStore);

		// 单例模式？
	}

	public void post_new_task(long user_id, String content, String device_type,
			String devie_no, long local_id, int creation_date,
			AsyncHttpResponseHandler responseHandler) {

		String url = RES_BASE_URL + "new2";

		RequestParams params = new RequestParams();
		params.put("user_id", String.valueOf(user_id));
		params.put("content", content);
		params.put("creation_date", String.valueOf(creation_date)); // ���ĸ�Ϊ׼�أ�
		params.put("device_type", device_type);
		params.put("device_no", devie_no);
		params.put("local_id", String.valueOf(local_id));

		client.post(url, params, responseHandler);
		return;

		// local_uniq_id = {cust_id}_{device_no}_{sqlite_pk_id}
		// remote_id = Զ�̵�pk_id

		// ����� local_uniq_id,body,creation_date,cust_id
		// Ȩ����֤����
	}

	// public static void load_by_custId(long cust_id, int page_index,
	// int page_size, AsyncHttpResponseHandler responseHandler) {
	// String url = BASE_URL + "list2";
	//
	// RequestParams params = new RequestParams();
	// params.put("cust_id", String.valueOf(cust_id));
	// params.put("page", String.valueOf(page_index));
	// params.put("size", String.valueOf(page_size));
	//
	// client.get(url, params, responseHandler);
	// return;
	//
	// }

	public void load_by_last_async_remote_id(long user_id, long last_remote_id,
			int size, AsyncHttpResponseHandler responseHandler) {
		String url = RES_BASE_URL + "list3";

		RequestParams params = new RequestParams();
		params.put("user_id", String.valueOf(user_id));
		params.put("min_pk_id", String.valueOf(last_remote_id));
		params.put("size", String.valueOf(size));

		client.get(url, params, responseHandler);

	}

	public static List<SubjectBean> Json2SubjectList(JSONObject result) {
		List<SubjectBean> subjectList = new ArrayList<SubjectBean>();

		try {
			JSONArray resultList = result.getJSONArray("list");
			// subjectList.clear();

			for (int i = 0; i < resultList.length(); i++) {
				JSONObject item = resultList.getJSONObject(i);

				SubjectBean subject = new SubjectBean();

				if (!item.isNull("local_id")) {
					subject.setId(item.getLong("local_id"));
				} else {
					subject.setId(0);
				}
				subject.setRemoteId(item.getLong("pk_id"));
				subject.setBody(item.getString("body"));
				subject.setCreationDate(0);

				subjectList.add(subject);

				// item.getString("created_date");

			}

		} catch (JSONException e) {
			Log.e("MainActivity", e.toString());
		}

		return subjectList;
	}

	public void login_or_register(String name, String password,
			String device_no, String device_type, String os_type,
			AsyncHttpResponseHandler asyncHttpResponseHandler) {
		
		RequestParams params = new RequestParams();
		params.put("name", name);
		params.put("password", password);
		params.put("device_no", device_no);
		params.put("device_type", device_type);
		params.put("os_type", os_type);
		
		client.post(REGISTER_URL, params, asyncHttpResponseHandler);
	}

	// public void register(String name,String password,String device_no,String
	// device_type,AsyncHttpResponseHandler responseHandler){
	// RequestParams params = new RequestParams();
	// params.put("name", name);
	// params.put("passsword", password);
	// params.put("device_no", device_no);
	// params.put("device_type", device_type);
	// client.post(REGISTER_URL, params, responseHandler);
	// }

}
