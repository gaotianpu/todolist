package com.gaotianpu.ftodo.ui;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
 
import java.util.List;

import org.json.JSONObject;

import com.gaotianpu.ftodo.MyApplication;
import com.gaotianpu.ftodo.R;

import com.gaotianpu.ftodo.bean.SubjectBean;
import com.gaotianpu.ftodo.bean.UserBean;
import com.gaotianpu.ftodo.da.FTDClient;
import com.gaotianpu.ftodo.da.SubjectDa;
import com.gaotianpu.ftodo.da.Util;
import com.loopj.android.http.JsonHttpResponseHandler;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.Fragment;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnKeyListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;

import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

 
import android.widget.ListView;
import android.widget.ProgressBar;

import android.widget.TextView;
import android.widget.AbsListView.OnScrollListener;

public class ListFragment extends Fragment {

	public static final String TAG = "ListFragment";
	public static final String LIST_SORT = "ListSort";

	private String list_sort = "all"; // 1全部,2待办,3提醒

	private MyApplication app;
	private Activity act;
	private UserBean user;
	private long cust_id = 0;

	private SubjectDa subjectDa;
	private FTDClient ftd;
	private List<SubjectBean> subjectList;
	// private List<HashMap<String, Object>> subjectList = new
	// ArrayList<HashMap<String,Object>>();
	private ListAdapter listAdapter;

	private View rootView;
	private ListView lvDefault;
	private EditText txtNew;
	private SwipeRefreshLayout swipeLayout;
	private View moreView;
	private TextView tv_load_more;
	private ProgressBar pb_load_progress;
	private int action_menu_checked_menu;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// 0.外部传入参数
		// list sort

		list_sort = getArguments() != null ? getArguments()
				.getString(LIST_SORT) : "all";

		// int drawer_item_position = getArguments() != null ? getArguments()
		// .getInt(LIST_SORT) : 1;
		// list_sort = drawer_item_position - 1; //
		// Log.i("list_sort", String.valueOf(drawer_item_position));

		// 1.系统全局
		act = this.getActivity();
		app = (MyApplication) act.getApplicationContext();

		user = app.getUser();
		cust_id = user.getUserId();

		subjectDa = new SubjectDa(act);
		ftd = new FTDClient(act);

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

		// lvDefault.addFooterView(moreView); // 设置列表底部视图
		// moreView.setVisibility(View.GONE);

		action_menu_checked_menu = R.id.action_list_normal;

		String[] mDrawerItems = getResources().getStringArray(
				R.array.drawer_menu_items);
		// act.setTitle(mDrawerItems[drawer_item_position]);

		// 定义可选菜单
		setHasOptionsMenu(true);

		// 3.数据加载

		subjectList = new ArrayList<SubjectBean>();
		listAdapter = new ListAdapter(act, 0);
		lvDefault.setAdapter(listAdapter);

		load_new_data();

		// 4.事件绑定
		txtNew_setOnKeyListener(); // 提交新subject
		lvDefault_setOnItemClickListener();
		lvDefault_setOnScrollListener(); // 滚动翻页
		swipeLayout_setOnRefreshListener(); // 下拉刷新

		return rootView;
	}

	@Override
	public void onResume() {
		listAdapter.notifyDataSetChanged();
		super.onResume();
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		// onCreateView setHasOptionsMenu(true);
		// menu.clear();
		// inflater.inflate(R.menu.list, menu);

	}

	@Override
	public void onPrepareOptionsMenu(Menu menu) {
		DrawerLayout mDrawerLayout = (DrawerLayout) act
				.findViewById(R.id.drawer_layout);
		ListView mDrawerList = (ListView) act.findViewById(R.id.left_drawer);
		boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);
		if (drawerOpen) {
			menu.clear();
		}

		// menu.findItem(R.id.action_websearch).setVisible(!drawerOpen);
		// menu.findItem(R.id.action_settings).setVisible(!drawerOpen);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Log.i("menu_onOptionsItemSelected",
		// String.valueOf(R.id.action_list_todo));

		// if (item.isChecked()) {
		// return true;
		// }
		//
		// item.setChecked(true);
		//
		// action_menu_checked_menu = item.getItemId();
		// listAdapter = new ListAdapter(act, item.getItemId());
		// lvDefault.setAdapter(listAdapter);

		return super.onOptionsItemSelected(item);
	}

	private void load_new_data() {
		// 从sqlite中读取数据，展示在listview中
		Log.i("list_sort", list_sort);
		subjectList = subjectDa.load_not_uploaded_subjects(cust_id, list_sort); // 加载未上传的
		add_data(0, 100);
	}

	private void add_data(int offset, int limit) {
		List<SubjectBean> list = subjectDa.load(cust_id, list_sort, offset,
				limit);

		Log.i("localdata", String.valueOf(list.size()));
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
					pb_load_progress.setVisibility(View.VISIBLE);
					tv_load_more.setText(R.string.loading_data);

					add_data(view.getCount(), 100);

					// Log.i("onScroll", "loading..."+
					// String.valueOf(view.getLastVisiblePosition()) +","+
					// String.valueOf(view.getCount()) );

					// txtNew.setVisibility(View.GONE);

				}
			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {

				// txtNew.setVisibility(View.VISIBLE);

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

								if (app.network_available()) {
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

							Long subjectID = subjectDa.insert(cust_id, content,
									0);

							SubjectBean subject = new SubjectBean();
							subject.setId(subjectID);
							subject.setUserId(cust_id);
							subject.setBody(txtNew.getText().toString().trim());
							// subject.setCreationDate(1);

							insert_new_item(subject, 0);

							// show new item in ListView
							lvDefault.setAdapter(new ListAdapter(act, 0));
							txtNew.setText("");
						}

					}
					return true;
				}
				return false;

			}

		});
	}
	
//	private void remind_img_btn_click(final SubjectBean subject) { 
//		Calendar c = Calendar.getInstance(); 
//		Date d = Util.str2Date(subject.getRemindDate());
//	 
//		if(d!=null){
//			c.setTime(d);
//		} 
//	 
//		Dialog dialog = new DatePickerDialog(act,
//				new DatePickerDialog.OnDateSetListener() {
//					public void onDateSet(DatePicker dp, int year, int month,
//							int dayOfMonth) { 
//						
//						String remind_date = Util.GetDateFromInts(year,month,dayOfMonth); // String.format("%d-%d-%d", year,month+1,dayOfMonth);
//						 
//						subject.setRemindDate(remind_date);
//						subjectDa.set_remind_date(subject.getId(),remind_date); 
//						
//						String[] items = {"每天","每周","每月","每年"};
//						new AlertDialog.Builder(act)
//						.setTitle("设置重复周期")
//						.setSingleChoiceItems(
//								items, subject.getRemindFrequency()-1,  //get from data
//								new DialogInterface.OnClickListener() {
//									public void onClick(DialogInterface dialog,
//											int which) {
//										
//										//change data
//										subject.setRemindFrequency(which+1);
//										subjectDa.set_remind_frequency(subject.getId(), which+1);
//										
//										String next_remind_date = Util.getNextDate(subject.getRemindDate(),which+1);
//										
//										subject.setNextRemindDate(next_remind_date);
//										subjectDa.set_next_remind(subject.getId(), next_remind_date);
//										
//										dialog.dismiss();
//										
//										listAdapter.notifyDataSetChanged();
//									}
//								}).setNegativeButton("取消", null).show();
//
//					}
//				}, c.get(Calendar.YEAR), // 传入年份
//				c.get(Calendar.MONTH), // 传入月份
//				c.get(Calendar.DAY_OF_MONTH) // 传入天数
//		);
//
//		dialog.setTitle("设置提醒日期");
//		dialog.setCancelable(true);
//		dialog.show();
//	}

	private void item_img_btn_click(final SubjectBean subject) {
		String[] pickdates = act.getResources().getStringArray(
				R.array.subject_sorts) ;  
		
		int choic_index = -1;
		if(subject.isRemind()){
			choic_index = 4;
		}else if(subject.isTodo()){
			if(subject.getStatus()==3){
				choic_index = 2;
			}else if(subject.getStatus()==2){
				choic_index = 3;
			}else{
				choic_index = 1;
			}
			
		}else{
			choic_index = 0;
		}
		
		new AlertDialog.Builder(act)
				.setTitle(subject.getBody())					 
				.setSingleChoiceItems(pickdates, choic_index,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) { 
								switch(which){
								case 0:
									subject.setIsTodo(false);
									subject.setIsRemind(false);
									subjectDa.set_todo(subject.getId(), false);
									subjectDa.set_remind(subject.getId(), false);
									break;
								case 1: //todo
									subject.setIsTodo(true);
									subject.setStatus(0);
									subjectDa.set_todo_status(subject.getId(),
											0);
									break;
								case 2: //block
									subject.setIsTodo(true);
									subject.setStatus(3);
									subjectDa.set_todo_status(subject.getId(),
											3);
									break;		
								case 3: // done
									subject.setIsTodo(true);
									subject.setStatus(2);
									subjectDa.set_todo_status(subject.getId(),
											2);
									break;
								case 4: // remind
								default:
									subject.setIsTodo(false);
									subject.setIsRemind(true);
									subjectDa.set_remind(subject.getId(), true);
									break;
								}
								dialog.dismiss();
								listAdapter.notifyDataSetChanged();
							}
						}).setNegativeButton(R.string.action_item_cancel, null).show(); 
	}

	private class ListAdapter extends BaseAdapter {
		private LayoutInflater inflater1;
		private int action_sort = 0;

		public ListAdapter(Context ctx1, int sort) {
			this.inflater1 = LayoutInflater.from(ctx1);
			action_sort = sort;
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

			final SubjectBean subject = subjectList.get(position);
			convertView = inflater1.inflate(R.layout.listview_item, null);

			TextView tv = (TextView) convertView.findViewById(R.id.tvBody);
			Log.i("remind",list_sort);
			String content = "";
			if(list_sort.equals("remind") && subject.isRemind() ){				
				if(subject.getNextRemindDate()!=null){
					content = content + subject.getNextRemindDate();
				}
				if(subject.getRemindFrequency()>0){
					switch(subject.getRemindFrequency()){
					case 1:
						content = content + " 天 ";
						break;
					case 2:
						content = content + " 周 ";
						break;
					case 3:
						content = content + " 月 ";
						break;
					case 4:
						content = content + " 年 ";
						break;
					}					
				}
				
				Log.i("remind",String.valueOf(subject.getRemindFrequency()));
				
				content = content + "\n" + subject.getBody().replaceAll("\n", "") ;
				
				//debug, 看listview中item重复的原因
				//content = content + " l:" + String.valueOf(subject.getId()) + ",r:" + String.valueOf(subject.getRemoteId()) ;
				
				tv.setText(content);
			}else{
				content = subject.getBody().replaceAll("\n", "") ;
				
				//debug, 看listview中item重复的原因
				//content = content + " l:" + String.valueOf(subject.getId()) + ",r:" + String.valueOf(subject.getRemoteId()) ;
				
				tv.setText(content);
			}
			

			ImageButton ibtn = (ImageButton) convertView
					.findViewById(R.id.btnIcon);
			switch (subject.get_sort_status()) {
			case 0:
			default: // 0, 普通备忘
				ibtn.setImageResource(R.drawable.ic_note);
				break;
			case 1: // 1, 待办
				ibtn.setImageResource(R.drawable.ic_flag);
				ibtn.setColorFilter(Color.RED);
				break;
			case 12: // 12, 待办完成
				ibtn.setImageResource(R.drawable.ic_done);
				break;
			case 13: // 13, 待办暂停
				ibtn.setImageResource(R.drawable.ic_pause);
				break;
			case 2: // 2, 提醒
				ibtn.setImageResource(R.drawable.ic_alarm);
				break;
			}

			ibtn.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) { 
					item_img_btn_click(subject); 
				}
			});

			return convertView;

		}

	}

	// ///////////////////

	private void download() {
		// Log.i(TAG,"download");
		user = app.getUser();
		if (user.getUserId() == 0 || user.getTokenStatus() == 0) {
			load_new_data();
			swipeLayout.setRefreshing(false);
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

								long local_id = subjectDa.insert2(s);

								s.setId(local_id);
								// insert_new_item(s, 0);
							}

							load_new_data();

						} catch (Exception e) {
							Log.e("load_by_last_async_remote_id", e.toString());

						} finally {
							swipeLayout.setRefreshing(false);
							 
						}

					}

					@Override
					public void onFailure(int statusCode, Throwable e,
							JSONObject errorResponse) {
						if (statusCode == 401) {
							// add code here
							app.set_token_failure();
						}
						load_new_data();
						swipeLayout.setRefreshing(false);
						 

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
