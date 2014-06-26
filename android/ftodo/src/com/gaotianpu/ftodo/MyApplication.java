package com.gaotianpu.ftodo;

import com.gaotianpu.ftodo.bean.UserBean;
import com.gaotianpu.ftodo.da.SQLiteHelper;
import com.gaotianpu.ftodo.da.UserDa;

import android.app.Application;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

public class MyApplication extends Application {
	private final String TAG = "MyApplication";

	private UserBean user;
	private UserDa userDa;

	public UserBean getUser() {
		userDa = new UserDa(this);

		if (this.user != null) {
			// Log.i(TAG, "getUser");
			return this.user;
		}

		return changeUser();
	}

	public UserBean changeUser() {
		// Log.i(TAG, "changeUser");
		user = userDa.load_current_user();
		if (user == null) {
			return new UserBean();
		}
		return user;
	}

//	public UserBean login(long user_id, String mobile, String access_token) {
//		userDa.login(user_id, mobile, access_token);
//		return changeUser();
//	}

	public void set_token_failure() {
		userDa.update_token_status(user.getUserId(), 0);
		changeUser();
	}

	public UserBean logout() {
		userDa.update_token_status(user.getUserId(), 0);
		userDa.update_active(user.getUserId(), 0);

		return changeUser();
	}

	public boolean network_available() {
		NetworkInfo info = ((ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE))
				.getActiveNetworkInfo();
		if (info != null && info.isConnected()) {
			return true;
		}
		return false;
	}

	public String get_channel_no() {
		return "dev";
	}

	public String get_version_no() {
		return "0.0.0.2"; //
	}

	private SQLiteHelper dbHelper;

	public SQLiteDatabase getDB() {
		if (this.dbHelper == null) {
			dbHelper = new SQLiteHelper(this, "ftodo", null, 1);
		}
		return dbHelper.getWritableDatabase();
	}

}
