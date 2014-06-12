package com.gaotianpu.ftodo;

import android.app.Application; 
import android.util.Log;

public class MyApplication extends Application {

	private UserBean user;

	//private final String TAG = "MyApplication";

	public UserBean getUser() {
		if (this.user != null) {
			//Log.i(TAG, "getUser");
			return this.user;
		}

		return changeUser();
	}

	public UserBean changeUser() {
		//Log.i(TAG, "changeUser");
		user = UserDa.load_current_user(this);
		return user;
	}

	public UserBean login(long user_id, String user_name, String access_token) {
		UserDa.login(this,user_id,user_name,access_token);
		return changeUser();
	}
	
	public void set_token_failure(){
		UserDa.update_token_status(this,user.getUserId(),0);
		changeUser();
	}

}
