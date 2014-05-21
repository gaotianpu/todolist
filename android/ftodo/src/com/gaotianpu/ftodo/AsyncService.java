package com.gaotianpu.ftodo;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
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
		// TODO Auto-generated method stub
		return asyncBinder;
	}

	public static final String TAG = "AsyncService";
	private int times = 0;
	private Handler mHandler;
	private ConnectivityManager cm;

	private String devie_no;
	private String device_type;
	private Context context;

	@Override
	public void onCreate() {
		super.onCreate();
		Log.d(TAG, "onCreate() executed");

		// 获得设备id
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
		Log.d(TAG, "onStartCommand() executed");

		// 循环执行upload任务
		mHandler.post(new Runnable() {
			@Override
			public void run() {
				// 检查是否能联网
				NetworkInfo info = cm.getActiveNetworkInfo();
				if (info != null && info.isConnected()) {
					Log.d(TAG, "isConnected times " + String.valueOf(times));
					// 检查sqlite中是否有未同步数据
					List<SubjectBean> subjectList = SubjectDa
							.load_not_uploaded_subjects(context);

					if (subjectList.size() > 0) {
						Log.d(TAG,
								"has not async subjects times "
										+ String.valueOf(times));

						for (SubjectBean subject : subjectList) {
							long cust_id = 1;

							FTDClient.post_new_task(cust_id, subject.getBody(),
									device_type, devie_no, subject.getId(),
									subject.getCreationDate(),
									new JsonHttpResponseHandler() {
										@Override
										public void onSuccess(JSONObject result) {
											try {
												JSONObject data = result
														.getJSONObject("data");
												
												SubjectDa
														.set_remoteId(
																context,
																data.getLong("local_id"),
																data.getLong("pk_id"));
												Log.d(TAG, "sucess");

											} catch (JSONException e) {
												Log.e(TAG, e.toString());
											}
										}
									});
						}
					}

				}

				times++;

				mHandler.postDelayed(this, 9000);
			}
		});

		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		Log.d(TAG, "onDestroy() executed");
	}

}
