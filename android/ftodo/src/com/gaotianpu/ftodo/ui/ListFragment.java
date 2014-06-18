package com.gaotianpu.ftodo.ui;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;

import com.gaotianpu.ftodo.MyApplication;
import com.gaotianpu.ftodo.R;
import com.gaotianpu.ftodo.R.id;
import com.gaotianpu.ftodo.R.layout;
import com.gaotianpu.ftodo.R.string;
import com.gaotianpu.ftodo.da.FTDClient;
import com.gaotianpu.ftodo.da.SubjectBean;
import com.gaotianpu.ftodo.da.SubjectDa;
import com.gaotianpu.ftodo.da.UserBean;
import com.loopj.android.http.JsonHttpResponseHandler;

import android.app.Fragment;
import android.app.SearchManager;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnKeyListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.EditText;

import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.AbsListView.OnScrollListener;

public class ListFragment extends Fragment {

	public static final String TAG = "ListFragment";

	private MyApplication app;
	private ConnectivityManager cm;
	private Context ctx;
	private UserBean user;
	private long cust_id = 0;
	private String device_type;
	private String deviceId;
	private String queryStr = "";

	private SubjectDa subjectDa;
	private FTDClient ftd;
	private List<SubjectBean> subjectList;
	private ListAdapter listAdapter;
	
	private TabHost tabHost;

	private View rootView;
	private ListView lvDefault;
	private EditText txtNew;
	private SwipeRefreshLayout swipeLayout;
	private View moreView;
	private TextView tv_load_more;
	private ProgressBar pb_load_progress;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// 1.系统全局
		ctx = this.getActivity();
		app = (MyApplication) ctx.getApplicationContext();

		cm = (ConnectivityManager) ctx
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		// 获得设备的相关信息
		TelephonyManager tm = (TelephonyManager) ctx
				.getSystemService(Context.TELEPHONY_SERVICE);
		deviceId = tm.getDeviceId();
		device_type = android.os.Build.MODEL;

		user = app.getUser();
		cust_id = user.getUserId();

		subjectDa = new SubjectDa(ctx);
		ftd = new FTDClient(ctx);

		// 2.控件相关 
		
		rootView = inflater.inflate(R.layout.fragment_list, container, false);
		lvDefault = (ListView) rootView.findViewById(R.id.lvDefault);
		txtNew = (EditText) rootView.findViewById(R.id.txtNew);

		// 下拉刷新初始化设置
		swipeLayout = (SwipeRefreshLayout) rootView
				.findViewById(R.id.swipe_container);
		swipeLayout.setColorScheme(android.R.color.holo_blue_bright,
				android.R.color.holo_green_light,
				android.R.color.holo_orange_light,
				android.R.color.holo_red_light);

		moreView = inflater.inflate(R.layout.footer_more, null);
		tv_load_more = (TextView) moreView.findViewById(R.id.tv_load_more);
		pb_load_progress = (ProgressBar) moreView
				.findViewById(R.id.pb_load_progress);
		lvDefault.addFooterView(moreView); // 设置列表底部视图

		getActivity().setTitle("全部");

		Intent intent = this.getActivity().getIntent();
		if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
			queryStr = intent.getStringExtra(SearchManager.QUERY);
			getActivity().setTitle("搜索:" + queryStr);

			// Log.i("search",queryStr);
			moreView.setVisibility(0);
		}

		// 3.数据加载
		subjectList = new ArrayList<SubjectBean>();
		listAdapter = new ListAdapter(ctx);
		lvDefault.setAdapter(listAdapter);

		if(queryStr==""){
			load_new_data();
		}else{
			this.search();
		}

		// 4.事件绑定
		txtNew_setOnKeyListener(); // 提交新subject
		lvDefault_setOnItemClickListener();
		lvDefault_setOnScrollListener(); // 滚动翻页
		swipeLayout_setOnRefreshListener(); // 下拉刷新

		return rootView;
	}

	private void load_new_data() {
		// 从sqlite中读取数据，展示在listview中
		subjectList = subjectDa.load_not_uploaded_subjects(cust_id); // 加载未上传的
		add_data(0, 100);
	}

	private void add_data(int offset, int limit) {
		List<SubjectBean> list = subjectDa.load(cust_id, offset, limit);
		for (SubjectBean s : list) {
			subjectList.add(s);
		}
		listAdapter.notifyDataSetChanged(); // 数据集变化后,通知adapter
	}

	private void lvDefault_setOnItemClickListener() {
		// 单击，查看明细
		lvDefault.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {

				SubjectBean subject = subjectList.get(arg2);
				subject.getId();

				Log.i("setOnItemClickListener", String.valueOf(arg2) + ","
						+ String.valueOf(subject.getId()));

				Intent detailIntent = new Intent(ctx, ItemDetailActivity.class);
				detailIntent.putExtra(ItemDetailActivity.SUBJECT_LOCAL_ID,
						subject.getId());
				startActivity(detailIntent);

				// Bundle args = new Bundle();
				// args.putLong(ItemDetailFragment.SUBJECT_LOCAL_ID,
				// subject.getId());
				// Fragment fragment = new ItemDetailFragment();
				// fragment.setArguments(args);
				// FragmentManager fragmentManager = getFragmentManager();
				// fragmentManager.beginTransaction()
				// .replace(R.id.content_frame, fragment).commit();

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
					pb_load_progress.setVisibility(View.VISIBLE);
					tv_load_more.setText(R.string.loading_data);

					add_data(view.getCount(), 100);

					// Log.i("onScroll", "loading..."+
					// String.valueOf(view.getLastVisiblePosition()) +","+
					// String.valueOf(view.getCount()) );

				}
			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				// Log.i("onScroll",
				// "firstVisibleItem:" + String.valueOf(firstVisibleItem)
				// + ",visibleItemCount:"
				// + String.valueOf(visibleItemCount)
				// + ",totalItemCount:"
				// + String.valueOf(totalItemCount));
			}
		});
	}

	private void swipeLayout_setOnRefreshListener() {
		swipeLayout
				.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
					// 下拉刷新
					@Override
					public void onRefresh() {
						new Handler().postDelayed(new Runnable() {
							public void run() {
								NetworkInfo info = cm.getActiveNetworkInfo();
								if (info != null && info.isConnected()) {
									download();
								} else {
									swipeLayout.setRefreshing(false);
								}

							}
						}, 1000);
					}
				});
	}

	private void txtNew_setOnKeyListener() {

		txtNew.setOnKeyListener(new OnKeyListener() {
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				// TODO Auto-generated method stub
				if (keyCode == KeyEvent.KEYCODE_ENTER) {
					InputMethodManager imm = (InputMethodManager) v
							.getContext().getSystemService(
									Context.INPUT_METHOD_SERVICE);
					if (imm.isActive()) {
						// 输入一条厚，键盘不收起，允许用户多次提交
						// imm.hideSoftInputFromWindow(
						// v.getApplicationWindowToken(), 0);

						// insert into sqlite
						String content = txtNew.getText().toString().trim();
						if (content.length() > 1) {

							user = app.getUser();
							cust_id = user.getUserId();

							Long subjectID = subjectDa.insert(cust_id, content);

							SubjectBean subject = new SubjectBean();
							subject.setId(subjectID);
							subject.setUserId(cust_id);
							subject.setBody(txtNew.getText().toString().trim());
							// subject.setCreationDate(1);

							insert_new_item(subject, 0);

							// show new item in ListView
							lvDefault.setAdapter(new ListAdapter(ctx));
							txtNew.setText("");
						}

					}
					return true;
				}
				return false;

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

			convertView = inflater1.inflate(R.layout.listview_item, null);

			TextView tv = (TextView) convertView.findViewById(R.id.tvBody);
			tv.setText("" + subjectList.get(position).getBody());

			return convertView;

		}

	}

	// ///////////////////
	
	private void search() {
		// Log.i(TAG,"download");
		user = app.getUser();
		if (user.getUserId() == 0 || user.getTokenStatus() == 0) {
			return;
		}

		Log.i("search", queryStr); 
		// max_remote_id_in_sqlite = 500;

		ftd.search(user.getUserId(), user.getAccessToken(), this.queryStr,
				new JsonHttpResponseHandler() {
					@Override
					public void onSuccess(JSONObject result) {
						Log.i("search", "search onSuccess");
						try {
							 
							// 这行代码可以继续封装到 FTDClient.load_by_custId方法中去，？
							 subjectList = FTDClient
									.Json2SubjectList(result);  
							 
							 Log.i("search", String.valueOf(  subjectList.size() ) );
							 

						} catch (Exception e) {
							Log.e("search", e.toString());

						} finally {
							swipeLayout.setRefreshing(false);
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

						swipeLayout.setRefreshing(false);
						listAdapter.notifyDataSetChanged();

					}

				});

		// 何时终止？

	}

	private void download() {
		// Log.i(TAG,"download");
		user = app.getUser();
		if (user.getUserId() == 0 || user.getTokenStatus() == 0) {
			return;
		}

		Log.i(TAG, "cust_id & token status is ok");

		// get max remote_id from sqlite
		long max_remote_id_in_sqlite = subjectDa.get_max_remote_id(user
				.getUserId());

		Log.i("max_remote_id_in_sqlite",
				"max_remote_id_in_sqlite "
						+ String.valueOf(max_remote_id_in_sqlite));

		// max_remote_id_in_sqlite = 500;

		ftd.load_by_last_async_remote_id(user.getUserId(),
				user.getAccessToken(), max_remote_id_in_sqlite, 50,
				new JsonHttpResponseHandler() {
					@Override
					public void onSuccess(JSONObject result) {

						try {

							// 这行代码可以继续封装到 FTDClient.load_by_custId方法中去，？
							List<SubjectBean> subjectList = FTDClient
									.Json2SubjectList(result);

							// get user's total count
							long total = result.getLong("total");
							long user_id = result.getLong("user_id");
							subjectDa.save_download_records(user_id, total); // 每次都要全部写入？

							Log.i(TAG,
									"subject count "
											+ String.valueOf(subjectList.size()));

							for (SubjectBean s : subjectList) {

								long local_id = subjectDa.insert2(
										s.getUserId(), s.getRemoteId(),
										s.getBody(),
										String.valueOf(s.getCreationDate()), 1,
										1);

								s.setId(local_id);
								// insert_new_item(s, 0);
							}

							load_new_data();

						} catch (Exception e) {
							Log.e("load_by_last_async_remote_id", e.toString());

						} finally {
							swipeLayout.setRefreshing(false);
							listAdapter.notifyDataSetChanged();
						}

					}

					@Override
					public void onFailure(int statusCode, Throwable e,
							JSONObject errorResponse) {
						if (statusCode == 401) {
							// add code here
							app.set_token_failure();
						}

						swipeLayout.setRefreshing(false);
						listAdapter.notifyDataSetChanged();

					}

				});

		// 何时终止？

	}

	private void insert_new_item(SubjectBean subject, int index) {
		// 判断是否已存在
		// 重新排序？
		for (SubjectBean s : subjectList) {
			if (s.getId() == subject.getId()) {
				// update
				return;
			}
		}

		// insert
		subjectList.add(index, subject);
	}

}
