package com.gaotianpu.ftodo;

import com.gaotianpu.ftodo.da.SQLiteHelper;
import com.gaotianpu.ftodo.da.UserBean;
import com.gaotianpu.ftodo.da.UserDa;

import android.app.Application;
import android.database.sqlite.SQLiteDatabase;
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
		return user;
	}

	public UserBean login(long user_id, String user_name, String access_token) {
		userDa.login(user_id, user_name, access_token);
		return changeUser();
	}

	public void set_token_failure() {
		userDa.update_token_status(user.getUserId(), 0);
		changeUser();
	}

	public UserBean logout() {
		userDa.update_token_status(user.getUserId(), 0);
		return changeUser();
	}

	private SQLiteHelper dbHelper;

	public SQLiteDatabase getDB() {
		if (this.dbHelper == null) {
			dbHelper = new SQLiteHelper(this, "ftodo", null, 1);
		}
		return dbHelper.getWritableDatabase();
	}

}
