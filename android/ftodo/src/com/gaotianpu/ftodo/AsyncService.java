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
		// TODO Auto-generated method stub
		return asyncBinder;
	}

	public static final String TAG = "AsyncService";
	// private int times = 0;
	private Handler mHandler;
	private ConnectivityManager cm;

	private String devie_no;
	private String device_type;
	private Context context;
	private long cust_id = 0;
	private UserBean user;

	@Override
	public void onCreate() {
		super.onCreate();
		// Log.d(TAG, "onCreate() executed");

		// ����豸id
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
		// Log.d(TAG, "onDestroy() executed");
	}

	private boolean has_active_user() { 
		// 获取当前用户账户  
		user = UserDa.load_current_user(context);  //每次请求都要检查一次sqlite？
//		if (user.getUserId() == 0) {
//			// 菜单显示 未登录...
//			return false;
//		}
//		
//		if (user.getTokenStatus() == 0) {
//			// 显示登录账号，提示需重新登录
//			return false;
//		}
		
		return true;
	}

	private void upload() {
//		if(!has_active_user()){
//			return ;
//		} 
		
		
		user = UserDa.load_current_user(context);
		if (user.getUserId() == 0 || user.getTokenStatus() == 0) {
			return ;
		}
		
		//Log.d(TAG, "has_active_user " );
		 
		List<SubjectBean> subjectList = SubjectDa
				.load_not_uploaded_subjects(context,user.getUserId());
		if (subjectList.size() == 0) {
			return;
		}

		 //Log.d(TAG, "has not async subjects times " );

		for (SubjectBean subject : subjectList) {

			FTDClient ftd = new FTDClient(context);			
			ftd.post_new_task(subject.getUserId() , subject.getBody(), device_type,
					devie_no, subject.getId(), subject.getCreationDate(),
					new JsonHttpResponseHandler() {
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

}
