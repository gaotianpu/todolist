package com.gaotianpu.ftodo;

import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import com.gaotianpu.ftodo.bean.SubjectBean;
import com.gaotianpu.ftodo.bean.UserBean;
import com.gaotianpu.ftodo.da.FTDClient;
import com.gaotianpu.ftodo.da.SubjectDa;
import com.gaotianpu.ftodo.da.Util;
import com.loopj.android.http.JsonHttpResponseHandler;

import android.app.Service;

import android.content.Context;
import android.content.Intent;

import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;

import android.util.Log;

public class AsyncService extends Service {
	private SubjectDa subjectDa;

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
	 
	private Context context;

	private MyApplication app;
	private FTDClient ftd;
	private UserBean user;

	@Override
	public void onCreate() {
		super.onCreate();
		
		context = this;

		app = (MyApplication) getApplicationContext();
		ftd = new FTDClient(context);
		subjectDa = new SubjectDa(context);

		mHandler = new Handler();  
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {

		mHandler.post(new Runnable() {
			@Override
			public void run() {
				 Log.i(TAG,"run");
				 
				if (app.network_available()) {
					user = app.getUser();
					 Log.i(TAG,
					 String.valueOf(user.getUserId()) + ","
					 + String.valueOf(user.getTokenStatus()));
					 
					if (user.getUserId() != 0 && user.getTokenStatus() != 0) {
						
						
						upload();
						
						download();

						try {
							next_remind();
						} catch (Exception ex) {
							Log.e(TAG, ex.getMessage());
						}
					}

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
		List<SubjectBean> subjectList = subjectDa
				.load_changed_but_not_uploaded(user.getUserId());
		
		Log.i(TAG,"upload");
		
		if (subjectList.size() == 0) {
			return;
		}
		
		//Log.i(TAG, "getLocalVersion:" + String.valueOf( subjectList.get(0).getLocalVersion()   ) );
		//Log.i(TAG, "getserverVersion:" + String.valueOf( subjectList.get(0).getServerVersion()  ) );

		// todo, 单个上传要改成批量上传
		for (final SubjectBean subject : subjectList) {
//			Log.i(TAG,
//					String.valueOf(subject.getCreationDate()) + ","
//							+ String.valueOf(subject.getUpdateDate()));
			long user_id = subject.getUserId();
			if (user_id == 0) {
				user_id = user.getUserId();
			}
  
			
			//Log.i(TAG, String.valueOf(subject.getRemoteId()));
			
			subjectDa.set_uploading(subject.getId(), 1);

			ftd.post_task(user_id, user.getAccessToken(), subject,
					new JsonHttpResponseHandler() {
						@Override
						public void onSuccess(JSONObject result) {
							try { 
								JSONObject data = result.getJSONObject("data"); 
								subjectDa.set_remoteId(
										data.getLong("local_id"),
										data.getLong("pk_id"),
										data.getLong("user_id"),
										data.getInt("version"));
								//Log.d(TAG, "sucess");

							} catch (JSONException e) {
								Log.e(TAG, e.toString());
							}finally{
								subjectDa.set_uploading(subject.getId(), 0);
							}
						}

						@Override
						public void onFailure(int statusCode, Throwable e,
								JSONObject errorResponse) {
							if (statusCode == 401) {
								// add code here
								app.set_token_failure();
							}
							
							subjectDa.set_uploading(subject.getId(), 0);

						//	Log.d(TAG, String.valueOf(statusCode));

						}
					});
			break;
		}

	}

	private void download() {
		// 获得total count, 这个步骤适合放在用户下拉刷新的时候触发
		// ftd.load_total(user.getUserId(), user.getAccessToken(),
		// new JsonHttpResponseHandler() {
		// @Override
		// public void onSuccess(JSONObject result) {
		// try {
		// long total = result.getLong("total");
		// long user_id = result.getLong("user_id");
		//
		// SubjectDa.save_download_records(context, user_id,
		// total); // 每次都要全部写入？
		//
		//
		// } catch (JSONException e) {
		// Log.e(TAG, e.toString());
		// }
		//
		// }
		// });

		// download and insert into subjects
		int page_size = 100;
		List<Long> offset_list = subjectDa.load_not_download(user.getUserId());
		// Log.i(TAG, "download" );

		if (offset_list.size() == 0) {
			return;
		}

		// for (Long offset : offset_list) {
		ftd.load_by_custId(user.getUserId(), user.getAccessToken(),
				offset_list.get(0), page_size, new JsonHttpResponseHandler() {
					@Override
					public void onSuccess(JSONObject result) {
						try {
							long uid = result.getLong("user_id");
							long offset = result.getLong("offset");

							List<SubjectBean> subjectList = FTDClient
									.Json2SubjectList(result);

							for (SubjectBean s : subjectList) {
								uid = s.getUserId();

								subjectDa.insert2(s);

							}

							// then,update the download records
							subjectDa.update_download_records(uid, offset);

						} catch (JSONException e) {
							Log.e(TAG, e.toString());
						}
					}

					@Override
					public void onFailure(int statusCode, Throwable e,
							JSONObject errorResponse) {
						if (statusCode == 401) {
							// add code here
							app.set_token_failure();
						}
					}
				});
		// }

	}

	private void next_remind() {
		List<SubjectBean> subjectList = subjectDa.load_expired_reminds(user
				.getUserId());
		
		//Log.i("next_remind", String.valueOf( subjectList.size()));
		
		for (SubjectBean subject : subjectList) {
			String next_remind_date = Util.getNextDate2(subject.getNextRemindDate(),
					subject.getRemindFrequency());
			
			//Log.i(TAG,next_remind_date);
			subjectDa.set_next_remind(subject.getId(), next_remind_date);
		}
	}
}
