package com.gaotianpu.ftodo.ui;

import java.io.IOException;

import com.gaotianpu.ftodo.MyApplication;
import com.gaotianpu.ftodo.R;
 
import com.gaotianpu.ftodo.bean.UserBean;
 

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
 
import android.view.MenuItem;
import android.webkit.WebView;

public class SettingDetailActivity extends Activity {

	public static final String SETTING_ITEM_ID = "SETTING_ITEM_ID";
	public static final String SETTING_ITEM_TITLE = "SETTING_ITEM_TITLE";

	private static Activity act;
	private MyApplication app;
	private static UserBean user;

	private String setting_item_id;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setting_detail);

		// 0.外部传入参数
		Intent intent = getIntent();
		setting_item_id = intent.getStringExtra(SETTING_ITEM_ID);  
		
		this.setTitle(intent.getStringExtra(SETTING_ITEM_TITLE));

		// 1 全局
		act = this;
		app = (MyApplication) getApplicationContext();
		user = app.getUser();

		if (app.network_available()) {
			WebView webview = new WebView(act);
			// webview.getSettings().setJavaScriptEnabled(true);
			String url = "http://ftodo.sinaapp.com/api/android_page?user_id="
					+ String.valueOf(user.getUserId()) + "&access_token="
					+ user.getAccessToken() + "&item=" + setting_item_id
					+ "&module=setting";

			if (setting_item_id.equals( "about")) { //java 判断字符串相等，不能使用==
				url = "http://ftodo.sinaapp.com/about";
			}
			
			webview.loadUrl(url);
			setContentView(webview);

			return;
		}

	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			// //goback?
			Runtime runtime = Runtime.getRuntime();
			try {
				//Log.i("back", String.valueOf(item.getItemId()));
				runtime.exec("input keyevent " + KeyEvent.KEYCODE_BACK);
				return true;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				Log.e("goback", e.toString());
				e.printStackTrace();
			}
			break;
		}

		return super.onOptionsItemSelected(item);
	}
}
