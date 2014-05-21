package com.gaotianpu.ftodo;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.loopj.android.http.JsonHttpResponseHandler;

import android.app.Service;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
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
	private SQLiteHelper dbHelper;
	private SQLiteDatabase db;

	@Override
	public void onCreate() {
		super.onCreate();
		Log.d(TAG, "onCreate() executed");

		mHandler = new Handler();

		cm = (ConnectivityManager) this
				.getSystemService(Context.CONNECTIVITY_SERVICE);

		// sqlite 初始化
		dbHelper = new SQLiteHelper(this, "ftodo", null, 1);
		db = dbHelper.getWritableDatabase();
	}

	private List<SubjectBean> load_not_uploaded_subjects() {
		List<SubjectBean> subjectList = new ArrayList<SubjectBean>();
		try {
			/* 查询表，得到cursor对象 */
			Cursor cursor = db.query("subjects", null, "is_sync=0", null, null,
					null, null);
			cursor.moveToFirst();
			while (!cursor.isAfterLast() && (cursor.getString(1) != null)) {
				SubjectBean subject = new SubjectBean();
				subject.setId(cursor.getLong(0));
				subject.setBody(cursor.getString(1));
				subject.setCreationDate(cursor.getInt(2));
				subjectList.add(subject);
				cursor.moveToNext();
			}
		} catch (IllegalArgumentException e) {
			Log.e(TAG, e.toString());
		}

		return subjectList;
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
					List<SubjectBean> subjectList = load_not_uploaded_subjects();
					if (subjectList.size() > 0) {
						Log.d(TAG,
								"has not async subjects times "
										+ String.valueOf(times));

						for (SubjectBean subject : subjectList) {
							long cust_id = 1;
							String device_type = "";
							String devie_no = "";

							FTDClient.post_new_task(cust_id, subject.getBody(),
									device_type, devie_no, subject.getId(),
									subject.getCreationDate(),
									new JsonHttpResponseHandler() {
										@Override
										public void onSuccess(JSONObject result) {
											try {
												JSONObject data = result.getJSONObject("data");
												long remote_id = data.getLong("pk_id");
												long local_id = data.getLong("local_id");
												//update sqlite's  remote_id
												
												ContentValues values = new ContentValues();
												values.put("remote_id", remote_id);
												values.put("is_sync", 1);
												values.put("last_sync", 1); //上次同步日期 
												
												db.update("subjects", values, "pk_id=?",new String[]{String.valueOf(local_id)});
												
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
