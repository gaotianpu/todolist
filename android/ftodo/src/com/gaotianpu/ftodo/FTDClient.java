package com.gaotianpu.ftodo;

import com.loopj.android.http.*;

public class FTDClient {
	private static final String BASE_URL = "http://ftodo.sinaapp.com/";

	private static AsyncHttpClient client = new AsyncHttpClient();

	public static void post_new_task(long cust_id, String content,
			String device_type, String devie_no, long local_id,
			int creation_date, AsyncHttpResponseHandler responseHandler) { 
		
		String url = BASE_URL + "new2";

		RequestParams params = new RequestParams();
		params.put("cust_id", String.valueOf(cust_id));
		params.put("content", content);
		params.put("creation_date", String.valueOf(creation_date)); // 按哪个为准呢？
		params.put("device_type", device_type);
		params.put("device_no", devie_no);
		params.put("local_id", String.valueOf(local_id));

		client.post(url, params, responseHandler);
		return;

		// local_uniq_id = {cust_id}_{device_no}_{sqlite_pk_id}
		// remote_id = 远程的pk_id

		// 基本参数： local_uniq_id,body,creation_date,cust_id
		// 权限认证参数？
	}

	public static void load_by_custId(int cust_id, int page_index,
			int page_size,AsyncHttpResponseHandler responseHandler) {
		String url = BASE_URL + "list2";

		RequestParams params = new RequestParams();
		params.put("cust_id", String.valueOf(cust_id) );
		params.put("page", String.valueOf(page_index) );
		params.put("size", String.valueOf(page_size));
		client.get(url, params, responseHandler);
		return;

	}

}
