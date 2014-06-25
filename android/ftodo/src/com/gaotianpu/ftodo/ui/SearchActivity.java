package com.gaotianpu.ftodo.ui;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;

import com.gaotianpu.ftodo.MyApplication;
import com.gaotianpu.ftodo.R;
import com.gaotianpu.ftodo.da.FTDClient;
import com.gaotianpu.ftodo.da.SubjectBean;
import com.gaotianpu.ftodo.da.SubjectDa;
import com.gaotianpu.ftodo.da.UserBean;

import com.loopj.android.http.JsonHttpResponseHandler;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView.OnItemClickListener;

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
	private SubjectDa subjectDa;
	private View mStatusView;
	private TextView mStatusMessageView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search);

		act =this;
		app = (MyApplication) getApplicationContext();

		ftd = new FTDClient(this);
		subjectDa = new SubjectDa(this);

		lvDefault = (ListView) findViewById(R.id.lvDefault);
		txtTips = (TextView) findViewById(R.id.txtTips);
		mStatusView = findViewById(R.id.status);
		mStatusMessageView = (TextView) findViewById(R.id.status_message);
		
		Intent intent = getIntent();
		if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
			query = intent.getStringExtra(SearchManager.QUERY);
			setTitle(query);
		}

		user = app.getUser();
		if (user.getUserId() == 0 || user.getTokenStatus() == 0) {
			txtTips.setText(R.string.user_unlogin);
			return;
		}

		ConnectivityManager cm = (ConnectivityManager) this
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo info = cm.getActiveNetworkInfo();
		if (info == null  || !info.isConnected()) {
			txtTips.setText(R.string.network_failed);
			return;
		}

		txtTips.setVisibility(View.GONE);
		subjectList = new ArrayList<SubjectBean>();
		listAdapter = new ListAdapter(this);
		lvDefault.setAdapter(listAdapter); 
		
		
		 
		load_search_results(0,100); 

		lvDefault_setOnItemClickListener();
		//lvDefault_setOnScrollListener();

	}
	
	/**
	 * Shows the progress UI and hides the login form.
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
	private void showProgress(final boolean show) {
		// On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
		// for very easy animations. If available, use these APIs to fade-in
		// the progress spinner.
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
			int shortAnimTime = getResources().getInteger(
					android.R.integer.config_shortAnimTime);

			mStatusView.setVisibility(View.VISIBLE);
			mStatusView.animate().setDuration(shortAnimTime)
					.alpha(show ? 1 : 0)
					.setListener(new AnimatorListenerAdapter() {
						@Override
						public void onAnimationEnd(Animator animation) {
							mStatusView.setVisibility(show ? View.VISIBLE
									: View.GONE);
						}
					});

			 
		} else {
			// The ViewPropertyAnimator APIs are not available, so simply show
			// and hide the relevant UI components.
			mStatusView.setVisibility(show ? View.VISIBLE : View.GONE);
			 
		}
	}

	private void load_search_results(long offset, int size) { 
		showProgress(true);

		ftd.search(user.getUserId(), user.getAccessToken(), this.query,offset,size,
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
							showProgress(false);
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
						
						showProgress(false);

					}

				});

		// 何时终止？

	}

	private void lvDefault_setOnItemClickListener() {
		// 单击，查看明细
		lvDefault.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {

				SubjectBean subject = subjectList.get(arg2);
				
				subject = subjectDa.load_by_remoteId(subject.getUserId(),subject.getRemoteId());
				
				Intent detailIntent = new Intent(act, ItemDetailActivity.class);
				detailIntent.putExtra(ItemDetailActivity.SUBJECT_LOCAL_ID,
						subject.getId());
				startActivity(detailIntent);
			}
		});

	}

	private void lvDefault_setOnScrollListener() {
		// 向下滚动翻页
		lvDefault.setOnScrollListener(new OnScrollListener() {
			// 添加滚动条滚到最底部，加载余下的元素
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				if (scrollState == OnScrollListener.SCROLL_STATE_IDLE
						&& view.getLastVisiblePosition() == view.getCount() - 1) {
//					pb_load_progress.setVisibility(View.VISIBLE);
//					tv_load_more.setText(R.string.loading_data);

					load_search_results(view.getCount(), 100);  
				}
			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) { 
			}
		});
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
