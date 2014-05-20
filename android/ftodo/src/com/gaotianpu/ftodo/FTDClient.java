package com.gaotianpu.ftodo;

import com.loopj.android.http.*;

public class FTDClient {
	private static final String BASE_URL = "http://ftodo.sinaapp.com/";

	private static AsyncHttpClient client = new AsyncHttpClient();

	public static void post_new_task(int cust_id, String devie_no,
			long local_id, String content, int creation_date,
			AsyncHttpResponseHandler responseHandler) {

		String url = BASE_URL + "new";

		RequestParams params = new RequestParams();
		params.put("cust_id", cust_id);
		params.put("device_no", devie_no);
		params.put("local_id", local_id);
		params.put("body", content);
		params.put("creation_date", creation_date); // 按哪个为准呢？
		client.post(url, params, responseHandler);
		return;

		// local_uniq_id = {cust_id}_{device_no}_{sqlite_pk_id}
		// remote_id = 远程的pk_id

		// 基本参数： local_uniq_id,body,creation_date,cust_id
		// 权限认证参数？
	}
	
	public static void load_by_custId(int cust_id,int page_index,int page_size,AsyncHttpResponseHandler responseHandler){
		String url = BASE_URL + "list";
		
		RequestParams params = new RequestParams();
		params.put("cust_id", cust_id);
		params.put("page_index", page_index);
		params.put("page_size", page_size);
		client.get(url, params, responseHandler);
		return;
		
	}

}
