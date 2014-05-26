package com.gaotianpu.ftodo;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.loopj.android.http.*;

public class FTDClient {
	private static final String BASE_URL = "http://ftodo.sinaapp.com/api/";

	private static AsyncHttpClient client = new AsyncHttpClient();

	public static void post_new_task(long cust_id, String content,
			String device_type, String devie_no, long local_id,
			int creation_date, AsyncHttpResponseHandler responseHandler) {

		String url = BASE_URL + "new2";

		RequestParams params = new RequestParams();
		params.put("cust_id", String.valueOf(cust_id));
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

//	public static void load_by_custId(long cust_id, int page_index,
//			int page_size, AsyncHttpResponseHandler responseHandler) {
//		String url = BASE_URL + "list2";
//
//		RequestParams params = new RequestParams();
//		params.put("cust_id", String.valueOf(cust_id));
//		params.put("page", String.valueOf(page_index));
//		params.put("size", String.valueOf(page_size));
//
//		client.get(url, params, responseHandler);
//		return;
//
//	}

	public static void load_by_last_async_remote_id(long cust_id,
			long last_remote_id, int size,
			AsyncHttpResponseHandler responseHandler) {
		String url = BASE_URL + "list3";

		RequestParams params = new RequestParams();
		params.put("cust_id", String.valueOf(cust_id));
		params.put("min_pk_id", String.valueOf(last_remote_id));
		params.put("size", String.valueOf(size));

		client.get(url, params, responseHandler);
		return;
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

}
