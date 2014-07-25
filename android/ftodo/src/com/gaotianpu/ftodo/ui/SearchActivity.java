package com.gaotianpu.ftodo.ui;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;

import com.gaotianpu.ftodo.MyApplication;
import com.gaotianpu.ftodo.R;
import com.gaotianpu.ftodo.bean.SubjectBean;
import com.gaotianpu.ftodo.bean.UserBean;
import com.gaotianpu.ftodo.da.FTDClient;
import com.gaotianpu.ftodo.da.SubjectDa;

import com.loopj.android.http.JsonHttpResponseHandler;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;


import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;

import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageButton;

import android.widget.ListView;
import android.widget.TextView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView.OnItemClickListener;

public class SearchActivity extends Activity {
	private List<SubjectBean> subjectList;

	private MyApplication app;

	private Activity act;
	private UserBean user;
	private String query;
	private FTDClient ftd;
	private ListAdapter listAdapter;
	private ListView lvDefault;
	private TextView txtTips;
	private SubjectDa subjectDa;
	private View mStatusView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search);

		act = this;
		app = (MyApplication) getApplicationContext();

		ftd = new FTDClient(this);
		subjectDa = new SubjectDa(this);

		lvDefault = (ListView) findViewById(R.id.lvDefault);
		txtTips = (TextView) findViewById(R.id.txtTips);
		mStatusView = findViewById(R.id.status);

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
	 
		if (!app.network_available()) {
			txtTips.setText(R.string.network_failed);
			return;
		}

		txtTips.setVisibility(View.GONE);
		subjectList = new ArrayList<SubjectBean>();
		listAdapter = new ListAdapter(this);
		lvDefault.setAdapter(listAdapter);

		load_search_results(0, 100);

		lvDefault_setOnItemClickListener();
		// lvDefault_setOnScrollListener();

	}
	
	@Override
	public void onResume() {
		listAdapter.notifyDataSetChanged();
		super.onResume();
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

		ftd.search(user.getUserId(), user.getAccessToken(), this.query, offset,
				size, new JsonHttpResponseHandler() {
					@Override
					public void onSuccess(JSONObject result) {
						 Log.i("search", "search onSuccess");
						try {
							subjectList.addAll(FTDClient
									.Json2SubjectList(result));
						
							Log.i("search",String.valueOf(subjectList.size()));
							
						} catch (Exception e) {
							Log.e("search", e.toString());

						} finally {
							Log.i("search", "search finally");
							listAdapter.notifyDataSetChanged();
							showProgress(false);
						}

					}

					@Override
					public void onFailure(int statusCode, Throwable e,
							JSONObject errorResponse) {

						// Log.i("search", "search onFailure");

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

				subject = subjectDa.load_by_remoteId(subject.getUserId(),
						subject.getRemoteId());

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
					// pb_load_progress.setVisibility(View.VISIBLE);
					// tv_load_more.setText(R.string.loading_data);

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
			
			SubjectBean s = subjectDa.load_by_remoteId(subject.getUserId(),
					subject.getRemoteId());
			
			final long local_id = s.getId();
			
			new AlertDialog.Builder(act)
					.setTitle(subject.getBody())					 
					.setSingleChoiceItems(pickdates, choic_index,
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int which) { 
									Log.i("which",String.valueOf(which ) );
									switch(which){
									case 0:
										subject.setIsTodo(false);
										subject.setIsRemind(false);
										subjectDa.set_todo(local_id, false);
										subjectDa.set_remind(local_id, false);
										break;
									case 1: //todo
										subject.setIsTodo(true);
										subject.setIsRemind(false);
										subject.setStatus(0);
										subjectDa.set_todo_status(local_id,
												0);
										break;
									case 2: //block
										subject.setIsRemind(false);
										subject.setIsTodo(true);
										subject.setStatus(3);
										subjectDa.set_todo_status(local_id,
												3);
										break;		
									case 3: // done
										subject.setIsRemind(false);
										subject.setIsTodo(true);
										subject.setStatus(2);
										subjectDa.set_todo_status(local_id,
												2);
										break;
									case 4: // remind
									default:
										subject.setIsTodo(false);
										subject.setIsRemind(true);
										subjectDa.set_remind(local_id, true);
										break;
									}
									dialog.dismiss();								
									
									listAdapter.notifyDataSetChanged();
									//lvDefault.setSelection(0);
								}
							}).setNegativeButton(R.string.action_item_cancel, null).show(); 
		}


		@Override
		public View getView(int position, View convertView, ViewGroup parent) {

			final SubjectBean subject = subjectList.get(position);
			convertView = inflater1.inflate(R.layout.listview_item, null);
			TextView tv = (TextView) convertView.findViewById(R.id.tvBody);
			tv.setText("" + subject.getBody().replaceAll("\n", ""));
			
			ImageButton ibtn = (ImageButton) convertView
					.findViewById(R.id.btnIcon);
			
			ibtn.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) { 
					item_img_btn_click(subject); 
				}
			});
			
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

			return convertView;

		}

	}

}
