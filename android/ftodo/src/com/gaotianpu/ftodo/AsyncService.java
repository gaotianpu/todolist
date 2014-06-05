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
					upload();
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
		UserBean user = app.getUser();
		// Log.i(TAG, String.valueOf(user.getUserId()) +
		// ","+String.valueOf(user.getTokenStatus()) );

		if (user.getUserId() == 0 || user.getTokenStatus() == 0) {
			return;
		}

		// Log.i(TAG, "has_active_user " );

		List<SubjectBean> subjectList = SubjectDa.load_not_uploaded_subjects(
				context, user.getUserId());
		if (subjectList.size() == 0) {
			return;
		}

		Log.i(TAG, "has not async subjects times ");

		// todo, 单个上传要改成批量上传
		for (SubjectBean subject : subjectList) {

			ftd.post_new_task(subject.getUserId(), subject.getBody(),
					device_type, devie_no, subject.getId(),
					subject.getCreationDate(), new JsonHttpResponseHandler() {
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
					});
		}
	}

	private void download() {
		UserBean user = app.getUser();
		// Log.i(TAG, String.valueOf(user.getUserId()) +
		// ","+String.valueOf(user.getTokenStatus()) );
		if (user.getUserId() == 0 || user.getTokenStatus() == 0) {
			return;
		}
		
		//获得total count, 这个步骤适合放在用户下拉刷新的时候触发		 
		ftd.load_total(user.getUserId(), user.getAccessToken(),
				new JsonHttpResponseHandler() {
					@Override
					public void onSuccess(JSONObject result) {
						try {
							long total = result.getLong("total");
							long user_id = result.getLong("user_id");
							
							long local_max_offset = SubjectDa.get_local_max_offset(context, user_id);
							long page_size = 100;
							
							if ( (total - local_max_offset)/page_size>0){
								//??????
								SubjectDa.save_download_records(context, user_id,
										total); // 每次都要全部写入？
							} 
							

						} catch (JSONException e) {
							Log.e(TAG, e.toString());
						}

					}
				});
		

		// download and insert into subjects
		int page_size = 100;
		List<Long> offset_list = SubjectDa.load_not_download(context,
				user.getUserId());
		for (Long offset : offset_list) {			
			ftd.load_by_custId(user.getUserId(), offset, page_size,
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

									SubjectDa.insert2(
											context,
											s.getUserId(),
											s.getId(),
											s.getBody(),
											String.valueOf(s.getCreationDate()),
											1, 1);
								}
								
								//then,update the download records
								SubjectDa.update_download_records(context, uid,
										offset);

							} catch (JSONException e) {
								Log.e(TAG, e.toString());
							}

						}
					});
		}

	}
}
