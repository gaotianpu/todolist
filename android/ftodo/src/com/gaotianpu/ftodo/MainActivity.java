package com.gaotianpu.ftodo;

import com.gaotianpu.ftodo.R;
import com.gaotianpu.ftodo.SubjectBean;
import com.gaotianpu.ftodo.SQLiteHelper;
import com.loopj.android.http.JsonHttpResponseHandler;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
//import android.app.ActionBar;
import android.app.Fragment;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.telephony.TelephonyManager;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnKeyListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.util.Log;
import android.support.v4.widget.SwipeRefreshLayout;

//import android.os.Build;

public class MainActivity extends Activity implements
		SwipeRefreshLayout.OnRefreshListener {

	private EditText txtNew;
	private ListView lvDefault;
	
	private ConnectivityManager cm;

	private SwipeRefreshLayout swipeLayout;

	private ListAdapter listAdapter;

	private Context context;

	private String device_type; // �豸�ͺ�
	private String deviceId; // �豸id
	private long cust_id = 1;

	private List<SubjectBean> subjectList;

	@Override
	public void onRefresh() {
		new Handler().postDelayed(new Runnable() {
			public void run() {
				NetworkInfo info = cm.getActiveNetworkInfo();
				if (info != null && info.isConnected()) {
					load_from_cloudy(1, 50);
				}else{
					swipeLayout.setRefreshing(false);
				} 
				
			}
		}, 1000);
	}

	private class ListAdapter extends BaseAdapter {

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
			convertView = getLayoutInflater().inflate(R.layout.listview_item,
					null);

			TextView tv = (TextView) convertView.findViewById(R.id.tvBody);
			tv.setText("" + subjectList.get(position).getBody());

			return convertView;
		}

	}

	private void rend_default_listview() {
		subjectList = SubjectDa.load(context, cust_id, 1, 50);
		listAdapter = new ListAdapter();
		lvDefault.setAdapter(listAdapter);
	}

	private void load_from_cloudy(int page, int size) {
		// 判断网络状态？
		FTDClient.load_by_custId(cust_id, page, size,
				new JsonHttpResponseHandler() {
					@Override
					public void onSuccess(JSONObject result) {
						try {
							JSONArray resultList = result.getJSONArray("list");
							subjectList.clear();

							for (int i = 0; i < resultList.length(); i++) {
								JSONObject item = resultList.getJSONObject(i);

								SubjectBean subject = new SubjectBean();

								if (!item.isNull("local_id")) {
									subject.setId(item.getLong("local_id"));
								} else {
									subject.setId(0);
								}
								subject.setRemoteId(item.getLong("pk_id"));
								subject.setBody(item.getString("body"));

								// item.getString("created_date");

								SubjectDa.insert2(context,
										item.getInt("pk_id"),
										item.getString("body"),
										item.getString("created_date"), 1, 1);

								subject.setCreationDate(0);
								subjectList.add(subject);

								listAdapter = new ListAdapter();
								lvDefault.setAdapter(listAdapter);
							}

						} catch (JSONException e) {
							Log.e("MainActivity", e.toString());
						}

						// 结束下拉刷新提示条
						swipeLayout.setRefreshing(false);
						listAdapter.notifyDataSetChanged();

					}
				});
	}

	private void bind_post_new_task() {

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
							Long subjectID = SubjectDa.insert(context, content);

							SubjectBean subject = new SubjectBean();
							subject.setId(subjectID);
							subject.setBody(txtNew.getText().toString().trim());
							subject.setCreationDate(1);
							subjectList.add(0, subject);

							// show new item in ListView
							lvDefault.setAdapter(new ListAdapter());
							txtNew.setText("");
						}

					}
					return true;
				}
				return false;

			}

		});
	}

	// ///////////////////
	
	private int lastItem; 
	private int page = 1;  
    private int size = 50; 
	
//	@Override  
//    public void onScroll(AbsListView view, int firstVisibleItem,  
//            int visibleItemCount, int totalItemCount) {  
//  
//        lastItem = firstVisibleItem + visibleItemCount - 1;  
//  
//        // Log.i(TAG,  
//        // "firstVisibleItem:"+firstVisibleItem+"visibleItemCount:"+visibleItemCount+" lastItem:"+lastItem);  
//    }  
//	
//	@Override  
//    public void onScrollStateChanged(AbsListView view, int scrollState) {  
//  
//        if (lastItem == listAdapter.getCount()  
//                && scrollState == OnScrollListener.SCROLL_STATE_IDLE) {  
//  
//            Log.e("scroll", "load more");  
//              
//            startIndex += requestSize;  
//              
//            //loadMoreData();  
//            Log.d("scroll", "onScrollStateChanged " + String.valueOf(scrollState));
//            
//            tv_load_more.setText(R.string.loading_data);  
//            pb_load_progress.setVisibility(View.VISIBLE);  
//            
////            tv_load_more.setText(R.string.load_more_data);  
////            pb_load_progress.setVisibility(View.GONE);  
////            
////            tv_load_more.setText(R.string.no_more_data);  
////            pb_load_progress.setVisibility(View.GONE);  
//        }  
//    }  
	
	private View moreView;
	private TextView tv_load_more;
	private ProgressBar pb_load_progress;  
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		context = this;

		if (savedInstanceState == null) {
			getFragmentManager().beginTransaction()
					.add(R.id.container, new PlaceholderFragment()).commit();
		}
		
		cm = (ConnectivityManager) this
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		
		//下拉刷新初始化设置
		swipeLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_container);
		swipeLayout.setOnRefreshListener(this);
		swipeLayout.setColorScheme(android.R.color.holo_blue_bright,
				android.R.color.holo_green_light,
				android.R.color.holo_orange_light,
				android.R.color.holo_red_light);

		//
		txtNew = (EditText) findViewById(R.id.txtNew);
		lvDefault = (ListView) findViewById(R.id.lvDefault);
		
		//滚动翻页
		//lvDefault.setOnScrollListener(this);
		
		LayoutInflater inflater = LayoutInflater.from(getApplicationContext()); 
        moreView = inflater.inflate(R.layout.footer_more, null);  
        tv_load_more = (TextView) moreView.findViewById(R.id.tv_load_more);  
        pb_load_progress = (ProgressBar) moreView.findViewById(R.id.pb_load_progress);  
		
		lvDefault.setOnScrollListener(new OnScrollListener() {
			// 添加滚动条滚到最底部，加载余下的元素
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				if (scrollState == OnScrollListener.SCROLL_STATE_IDLE) {
					//loadRemnantListItem();
					
					if(view.getLastVisiblePosition() == view.getCount() - 1){
						page ++ ;
						//load_from_cloudy(page,size);
						
						Log.d("scroll", "onScrollStateChanged " + String.valueOf(view.getLastVisiblePosition()));
					}
					
		            tv_load_more.setText(R.string.loading_data);  
		            pb_load_progress.setVisibility(View.VISIBLE); 
		            
		            Log.d("scroll", "onScrollStateChanged ");
					
				}
			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				//
				Log.d("scroll", "onScroll " + String.valueOf(visibleItemCount));
			}
		});

		// 获得设备的相关信息
		TelephonyManager tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
		deviceId = tm.getDeviceId();
		device_type = android.os.Build.MODEL;

		// 启动 AsyncService
		Intent startIntent = new Intent(this, AsyncService.class);
		startService(startIntent);

		// 提交新subject
		bind_post_new_task();

		// 从sqlite中读取数据，展示在listview中
		rend_default_listview();

		// 从cloudy加载数据，再刷新listview
		// load_from_cloudy(1, 50);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class PlaceholderFragment extends Fragment {

		public PlaceholderFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_main, container,
					false);
			return rootView;
		}
	}

}
