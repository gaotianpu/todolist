package com.gaotianpu.ftodo.ui;

import com.gaotianpu.ftodo.MyApplication;
import com.gaotianpu.ftodo.R;
import com.gaotianpu.ftodo.R.layout;
import com.gaotianpu.ftodo.bean.UserBean;
import com.gaotianpu.ftodo.da.SubjectDa;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebView;

public class SettingDetailActivity extends Activity {

	public static final String SETTING_ITEM_ID = "SETTING_ITEM_ID";

	private static Activity act;
	private MyApplication app;
	private static UserBean user;

	private int setting_item_id;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		

		// 0.外部传入参数
		Intent intent = getIntent();
		setting_item_id = intent.getIntExtra(SETTING_ITEM_ID, 0);

		// 1 全局
		act = this;
		app = (MyApplication) getApplicationContext();
		user = app.getUser();

		if (app.network_available()) {
			WebView webview = new WebView(act);
			// webview.getSettings().setJavaScriptEnabled(true);
			String url = "http://ftodo.sinaapp.com/api/dashboard?user_id="
					+ String.valueOf(user.getUserId()) + "&access_token="
					+ user.getAccessToken();
			webview.loadUrl(url);

			return ;
		}
		
		setContentView(R.layout.activity_setting_detail);

	}
}
