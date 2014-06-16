package com.gaotianpu.ftodo;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

public class DashboardFragment extends Fragment {
	private View rootView;
	private WebView webview;

	private Activity act;
	private ConnectivityManager cm;
	private MyApplication app;
	private UserBean user;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		rootView = inflater.inflate(R.layout.fragment_dashboard, container,
				false);

		act = getActivity();
		app = (MyApplication) act.getApplicationContext();
		cm = (ConnectivityManager) act
				.getSystemService(Context.CONNECTIVITY_SERVICE);

		user = app.getUser();
		act.setTitle(user.getEmail());

		NetworkInfo info = cm.getActiveNetworkInfo();
		if (info != null && info.isConnected()) {
			webview = new WebView(act);
			// webview.getSettings().setJavaScriptEnabled(true);
			String url = "http://ftodo.sinaapp.com/api/dashboard?user_id="
					+ String.valueOf(user.getUserId()) + "&access_token="
					+ user.getAccessToken();
			webview.loadUrl(url);

			return webview;
		}

		return rootView;
	}

}
