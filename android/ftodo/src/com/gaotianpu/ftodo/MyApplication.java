package com.gaotianpu.ftodo;

import com.gaotianpu.ftodo.bean.UserBean;
import com.gaotianpu.ftodo.da.SQLiteHelper;
import com.gaotianpu.ftodo.da.UserDa;
import android.app.Application;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class MyApplication extends Application {

	private UserBean user;
	private UserDa userDa;

	public UserBean getUser() {
		userDa = new UserDa(this);

		if (this.user != null) {

			return this.user;
		}

		return changeUser();
	}

	public UserBean changeUser() {

		user = userDa.load_current_user();
		if (user == null) {
			return new UserBean();
		}
		return user;
	}

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
		return "baidu"; // dev,360,baidu,qq
	}

	public String get_version_no() {

		try {
			PackageInfo pi = this.getPackageManager().getPackageInfo(
					this.getPackageName(), 0);
			return pi.versionName; // pi.versionCode
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return "unkonwn";

	}

	// public void set_api_host() {
	// SharedPreferences hostSp = getSharedPreferences("APIHost", 0);
	// int last = hostSp.getInt("timestamp", 0);
	//
	// if (!network_available()) {
	// return;
	// }
	//
	// if (true) {// last<curent- 5days
	// AsyncHttpClient client = new AsyncHttpClient();
	// client.setTimeout(20000);
	// client.post("http://ftodo.sinaapp.com/host", null,
	// new JsonHttpResponseHandler() {
	// @Override
	// public void onSuccess(JSONObject result) {
	// int timestamp = 0;
	// String host = "";
	// try {
	// host = result.getString("host");
	// } catch (JSONException e) {
	// Log.e("hosterr", e.toString());
	// }
	// SharedPreferences hostSp = getSharedPreferences(
	// "APIHost", 0);
	// hostSp.edit().putString("host", host).commit();
	// hostSp.edit().putInt("timestamp", timestamp)
	// .commit();
	// }
	// });
	//
	// }
	// }
	//
	// public String get_api_host() {
	// SharedPreferences hostSp = getSharedPreferences("APIHost", 0);
	// return hostSp.getString("host", "ftodo.sinaapp.com");
	// }

	private SQLiteHelper dbHelper;

	public SQLiteDatabase getDB() {
		if (this.dbHelper == null) {
			dbHelper = new SQLiteHelper(this, "ftodo", null, 1);
		}
		return dbHelper.getWritableDatabase();
	}

	// private Builder b;
	// public Builder builderDialog(){
	// if(b==null){
	// b = new AlertDialog.Builder(this); //防止重复弹出对话框
	// }
	// return b;
	// }

}
