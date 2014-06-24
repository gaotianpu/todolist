package com.gaotianpu.ftodo.ui;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;

import com.gaotianpu.ftodo.MyApplication;
import com.gaotianpu.ftodo.R;
import com.gaotianpu.ftodo.da.FTDClient;
import com.gaotianpu.ftodo.da.SubjectBean;
import com.gaotianpu.ftodo.da.UserBean;

import com.loopj.android.http.JsonHttpResponseHandler;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class SearchActivity extends Activity {
	private List<SubjectBean> subjectList;

	private MyApplication app;
	private ConnectivityManager cm;
	private Activity act;
	private UserBean user;
	private String query;
	private FTDClient ftd;
	private ListAdapter listAdapter;
	private ListView lvDefault;
	private TextView txtTips;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search);

		app = (MyApplication) getApplicationContext();

		ftd = new FTDClient(this);

		lvDefault = (ListView) findViewById(R.id.lvDefault);
		txtTips = (TextView) findViewById(R.id.txtTips);

		user = app.getUser();
		if (user.getUserId() == 0 || user.getTokenStatus() == 0) { 
			 
		} else {
			
			txtTips.setVisibility(View.GONE);

			Intent intent = getIntent();
			// 搜索
			if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
				query = intent.getStringExtra(SearchManager.QUERY);
				setTitle(query);

				subjectList = new ArrayList<SubjectBean>();
				listAdapter = new ListAdapter(this);
				lvDefault.setAdapter(listAdapter);

				load_search_results();
			}
		}

	}

	private void load_search_results() {
		// Log.i(TAG,"load_search_results");

		// Log.i("search", queryStr);
		// max_remote_id_in_sqlite = 500;

		ftd.search(user.getUserId(), user.getAccessToken(), this.query,
				new JsonHttpResponseHandler() {
					@Override
					public void onSuccess(JSONObject result) {
						Log.i("search", "search onSuccess");
						try {
							// 这行代码可以继续封装到 FTDClient.load_by_custId方法中去，？
							subjectList = FTDClient.Json2SubjectList(result);

							Log.i("search", String.valueOf(subjectList.size()));

						} catch (Exception e) {
							Log.e("search", e.toString());

						} finally {
							// swipeLayout.setRefreshing(false);
							listAdapter.notifyDataSetChanged();
						}

					}

					@Override
					public void onFailure(int statusCode, Throwable e,
							JSONObject errorResponse) {

						Log.i("search", "search onFailure");

						if (statusCode == 401) {
							// add code here
							app.set_token_failure();
						}

						// swipeLayout.setRefreshing(false);
						listAdapter.notifyDataSetChanged();

					}

				});

		// 何时终止？

	}

	private class ListAdapter extends BaseAdapter {
		private LayoutInflater inflater1;

		public ListAdapter(Context ctx1) {
			this.inflater1 = LayoutInflater.from(ctx1);

		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return subjectList.size();
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

			SubjectBean subject = subjectList.get(position);
			convertView = inflater1.inflate(R.layout.listview_item, null);
			TextView tv = (TextView) convertView.findViewById(R.id.tvBody);
			tv.setText("" + subject.getBody().replaceAll("\n", ""));

			return convertView;

		}

	}

}
