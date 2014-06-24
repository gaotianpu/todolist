package com.gaotianpu.ftodo.ui;

import java.util.List;

import com.gaotianpu.ftodo.MyApplication;
import com.gaotianpu.ftodo.R;
import com.gaotianpu.ftodo.R.layout;
import com.gaotianpu.ftodo.da.SubjectDa;
import com.gaotianpu.ftodo.da.UserBean;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class DashboardFragment extends Fragment {
	private View rootView;
	private WebView webview;
	private ListView lvDefault;

	private Activity act;
	private ConnectivityManager cm;
	private MyApplication app;
	private UserBean user;
	private LoadReportTask reportTask;

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

		lvDefault = (ListView) rootView.findViewById(R.id.lvDefault);
		SubjectDa subjectDa = new SubjectDa(act);
		List<String> x = subjectDa.load_days_count(user.getUserId());
		String[] str = new String[x.size()];
		str = x.toArray(str);

		lvDefault.setAdapter(new ArrayAdapter<String>(act,
				android.R.layout.simple_list_item_1, str));

		// reportTask = new LoadReportTask();
		// reportTask.execute((Void) null);

		// NetworkInfo info = cm.getActiveNetworkInfo();
		// if (info != null && info.isConnected()) {
		// webview = new WebView(act);
		// // webview.getSettings().setJavaScriptEnabled(true);
		// String url = "http://ftodo.sinaapp.com/api/dashboard?user_id="
		// + String.valueOf(user.getUserId()) + "&access_token="
		// + user.getAccessToken();
		// webview.loadUrl(url);
		//
		// return webview;
		// }

		return rootView;
	}

	public class LoadReportTask extends AsyncTask<Void, Void, Boolean> {
		@Override
		protected Boolean doInBackground(Void... params) {

			SubjectDa subjectDa = new SubjectDa(act);
			List<String> strs = subjectDa.load_days_count(user.getUserId());
			lvDefault.setAdapter(new ArrayAdapter<String>(act,
					android.R.layout.simple_list_item_1, (String[]) strs
							.toArray()));

			return true;
		}

		@Override
		protected void onPostExecute(final Boolean success) {
			if (success) {
				act.finish();
			}
		}

		@Override
		protected void onCancelled() {

		}
	}

}
