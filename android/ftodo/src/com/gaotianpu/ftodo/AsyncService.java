package com.gaotianpu.ftodo;

import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import com.loopj.android.http.JsonHttpResponseHandler;

import android.app.Service;

import android.content.Context;
import android.content.Intent;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.telephony.TelephonyManager;
import android.util.Log;

public class AsyncService extends Service {

	public class AsyncBinder extends Binder {
		AsyncService getService() {
			return AsyncService.this;
		}
	}

	private final IBinder asyncBinder = new AsyncBinder();

	@Override
	public IBinder onBind(Intent intent) {
		return asyncBinder;
	}

	public static final String TAG = "AsyncService";
	private Handler mHandler;
	private ConnectivityManager cm;

	private String devie_no;
	private String device_type;
	private Context context;

	private MyApplication app;
	private FTDClient ftd;
	private UserBean user;

	@Override
	public void onCreate() {
		super.onCreate();

		app = (MyApplication) getApplicationContext();
		ftd = new FTDClient(context);

		TelephonyManager tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
		devie_no = tm.getDeviceId();
		device_type = android.os.Build.MODEL;

		mHandler = new Handler();

		cm = (ConnectivityManager) this
				.getSystemService(Context.CONNECTIVITY_SERVICE);

		context = this;

	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {

		mHandler.post(new Runnable() {
			@Override
			public void run() {
				NetworkInfo info = cm.getActiveNetworkInfo();
				if (info != null && info.isConnected()) {
					user = app.getUser();
					 Log.i(TAG, String.valueOf(user.getUserId()) +
					 ","+String.valueOf(user.getTokenStatus()) );
					if (user.getUserId() == 0 || user.getTokenStatus() == 0) {
						return;
					}
					
					upload();					
					download();
				}

				mHandler.postDelayed(this, 9000);
			}
		});

		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	private void upload() { 
		// Log.i(TAG, "has_active_user " );

		List<SubjectBean> subjectList = SubjectDa.load_not_uploaded_subjects(
				context, user.getUserId());
		if (subjectList.size() == 0) {
			return;
		}

		Log.i(TAG, "has not async subjects times ");

		// todo, 单个上传要改成批量上传
		for (SubjectBean subject : subjectList) {
			Log.i(TAG, String.valueOf(subject.getCreationDate()) + "," + String.valueOf(subject.getUpdateDate())  );

			ftd.post_new_task(subject.getUserId(),user.getAccessToken(), subject.getBody(),
					device_type, devie_no, subject.getId(),
					subject.getCreationDate(), subject.getUpdateDate(),new JsonHttpResponseHandler() {
						@Override
						public void onSuccess(JSONObject result) {
							try {
								JSONObject data = result.getJSONObject("data");

								SubjectDa.set_remoteId(context,
										data.getLong("local_id"),
										data.getLong("pk_id"));
								// Log.d(TAG, "sucess");

							} catch (JSONException e) {
								Log.e(TAG, e.toString());
							}
						}
						
						@Override
						public void onFailure(int statusCode, Throwable e, JSONObject errorResponse){
							if(statusCode==401){
								//add code here
								app.set_token_failure();
							} 
							
						} 
					});
		}
	}

	private void download() { 
		//获得total count, 这个步骤适合放在用户下拉刷新的时候触发		 
//		ftd.load_total(user.getUserId(), user.getAccessToken(),
//				new JsonHttpResponseHandler() {
//					@Override
//					public void onSuccess(JSONObject result) {
//						try {
//							long total = result.getLong("total");
//							long user_id = result.getLong("user_id");
//							
//							SubjectDa.save_download_records(context, user_id,
//									total); // 每次都要全部写入？
//							
//
//						} catch (JSONException e) {
//							Log.e(TAG, e.toString());
//						}
//
//					}
//				}); 

		// download and insert into subjects
		int page_size = 100;
		List<Long> offset_list = SubjectDa.load_not_download(context,
				user.getUserId());
		//Log.i(TAG, String.valueOf( offset_list.size() ) );
		
		if(offset_list.size()==0){
			return ;
		}  
		
		//for (Long offset : offset_list) {			
			ftd.load_by_custId(user.getUserId(), user.getAccessToken(), offset_list.get(0), page_size,
					new JsonHttpResponseHandler() {
						@Override
						public void onSuccess(JSONObject result) {
							try {
								long uid = result.getLong("user_id");
								long offset = result.getLong("offset");

								List<SubjectBean> subjectList = FTDClient
										.Json2SubjectList(result);

								for (SubjectBean s : subjectList) {
									uid = s.getUserId();
									
									SubjectDa.insert2(context,
											s.getUserId(), s.getRemoteId(),
											s.getBody(),
											String.valueOf(s.getCreationDate()), 1,
											1); 
									 
								}
								
								//then,update the download records
								SubjectDa.update_download_records(context, uid,
										offset);

							} catch (JSONException e) {
								Log.e(TAG, e.toString());
							}
						}
						
						@Override
						public void onFailure(int statusCode, Throwable e, JSONObject errorResponse){
							if(statusCode==401){
								//add code here
								app.set_token_failure();
							} 
						} 
					}); 
		//}

	}
}
