package com.gaotianpu.ftodo.ui;

import java.util.List;

import com.gaotianpu.ftodo.MyApplication;
import com.gaotianpu.ftodo.R;
import com.gaotianpu.ftodo.bean.ReportBean;
import com.gaotianpu.ftodo.bean.UserBean;
import com.gaotianpu.ftodo.da.SubjectDa;
import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class DashboardFragment extends Fragment {
	private View rootView;

	private ListView lvDefault;

	private Activity act;

	private MyApplication app;
	private UserBean user;

	private ListAdapter listAdapter;
	private List<ReportBean> reportList;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		rootView = inflater.inflate(R.layout.fragment_dashboard, container,
				false);

		act = getActivity();
		app = (MyApplication) act.getApplicationContext();

		user = app.getUser();
		act.setTitle(R.string.dashboard_title);

		lvDefault = (ListView) rootView.findViewById(R.id.lvDefault);

		SubjectDa subjectDa = new SubjectDa(act);
		reportList = subjectDa.load_days_count(user.getUserId());
		listAdapter = new ListAdapter(act);
		lvDefault.setAdapter(listAdapter);

		// String[] str = new String[x.size()];
		// str = x.toArray(str);
		// lvDefault.setAdapter(new ArrayAdapter<String>(act,
		// android.R.layout.simple_list_item_1, str));

		// reportTask = new LoadReportTask();
		// reportTask.execute((Void) null);

		// if (app.network_available()) {
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

	private class ListAdapter extends BaseAdapter {
		private LayoutInflater inflater1;

		public ListAdapter(Context ctx1) {
			this.inflater1 = LayoutInflater.from(ctx1);
		}

		@Override
		public int getCount() {
			return reportList.size();
		}

		@Override
		public Object getItem(int position) {
			return position;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ReportBean item = reportList.get(position);
			convertView = inflater1.inflate(R.layout.dashboard_listview_item,
					null);

			TextView tv = (TextView) convertView.findViewById(R.id.tvK);
			tv.setText(item.getK());

			TextView tvV = (TextView) convertView.findViewById(R.id.tvV);
			tvV.setText(String.valueOf(item.getV()));

			// if(currentSubject.getId() == subject.getId() ){
			// convertView.setBackgroundColor();
			// }

			return convertView;

		}

	}

	public class LoadReportTask extends AsyncTask<Void, Void, Boolean> {
		@Override
		protected Boolean doInBackground(Void... params) {

			// SubjectDa subjectDa = new SubjectDa(act);
			// List<String> x = subjectDa.load_days_count(user.getUserId());
			// String[] str = new String[x.size()];
			// str = x.toArray(str);
			//
			// lvDefault.setAdapter(new ArrayAdapter<String>(act,
			// android.R.layout.simple_list_item_1, str));

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
