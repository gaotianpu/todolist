package com.gaotianpu.ftodo.ui;

import java.util.ArrayList;
import java.util.List;

import com.gaotianpu.ftodo.MyApplication;
import com.gaotianpu.ftodo.R;

import com.gaotianpu.ftodo.bean.SubjectBean;
import com.gaotianpu.ftodo.bean.UserBean;
import com.gaotianpu.ftodo.da.SubjectDa;
import com.gaotianpu.ftodo.da.Util;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;

import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class ListTodoFragment extends Fragment {
	private View rootView;

	private Activity act;
	private MyApplication app;
	private UserBean user;
	private SubjectDa subjectDa;

	private ListView lvDefault;
	private ListAdapter listAdapter;

	private int action_menu_checked_menu;

	private List<Object> taskList = new ArrayList<Object>(); // 合并后的数据

	private List<String> groups;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		rootView = inflater.inflate(R.layout.fragment_list_todo, container,
				false);

		act = getActivity();
		app = (MyApplication) act.getApplicationContext();
		user = app.getUser();

		subjectDa = new SubjectDa(act);

		groups = java.util.Arrays.asList(act.getResources().getStringArray(
				R.array.todo_plan_date_sorts));

		setHasOptionsMenu(true);
		action_menu_checked_menu = R.id.action_list_normal;

		lvDefault = (ListView) rootView.findViewById(R.id.lvDefault);
		Log.i("lvDefault", lvDefault.toString());

		listAdapter = new ListAdapter(act);
		lvDefault.setAdapter(listAdapter);
		load_data(0, 500);

		lvDefault_setOnItemClickListener();

		return rootView;
	}
	
	@Override
	public void onResume() {
		listAdapter.notifyDataSetChanged();
		super.onResume();
	}

	private void load_data(int offset, int size) {
		List<SubjectBean> taskList0 = new ArrayList<SubjectBean>();
		List<SubjectBean> taskList1 = new ArrayList<SubjectBean>();
		List<SubjectBean> taskList2 = new ArrayList<SubjectBean>();
		List<SubjectBean> taskList3 = new ArrayList<SubjectBean>();
		List<SubjectBean> taskList4 = new ArrayList<SubjectBean>();
		List<SubjectBean> taskList5 = new ArrayList<SubjectBean>();

		List<SubjectBean> list = subjectDa.load_todo(user.getUserId(), offset,
				size);
		for (SubjectBean s : list) {
			switch (s.getPlanStartSort()) {
			case 0:
			default:
				taskList0.add(s);
				break;
			case 1:
				taskList1.add(s);
				break;
			case 2:
				taskList2.add(s);
				break;
			case 3:
				taskList3.add(s);
				break;
			case 4:
				taskList4.add(s);
				break;
			case 5:
				taskList5.add(s);
				break;
			}
		}

		taskList = new ArrayList<Object>();
		if (taskList0.size() > 0) {
			taskList.add(groups.get(0));
			taskList.addAll(taskList0);
		}

		if (taskList1.size() > 0) {
			taskList.add(groups.get(1));
			taskList.addAll(taskList1);
		}
		if (taskList2.size() > 0) {
			taskList.add(groups.get(2));
			taskList.addAll(taskList2);
		}
		if (taskList3.size() > 0) {
			taskList.add(groups.get(3));
			taskList.addAll(taskList3);
		}
		if (taskList4.size() > 0) {
			taskList.add(groups.get(4));
			taskList.addAll(taskList4);
		}
		if (taskList5.size() > 0) {
			taskList.add(groups.get(5));
			taskList.addAll(taskList5);
		}
		listAdapter.notifyDataSetChanged();

	}

	private void lvDefault_setOnItemClickListener() {
		// 单击，查看明细
		lvDefault.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {

				Object o = taskList.get(arg2);
				if (o.getClass() != SubjectBean.class) {
					return;
				}

				SubjectBean subject = (SubjectBean) o;

				Intent detailIntent = new Intent(act, ItemDetailActivity.class);
				detailIntent.putExtra(ItemDetailActivity.SUBJECT_LOCAL_ID,
						subject.getId());
				startActivity(detailIntent);

			}
		});

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
		// // listAdapter = new ListAdapter(act);
		// // lvDefault.setAdapter(listAdapter);
		// listAdapter.notifyDataSetChanged();

		return super.onOptionsItemSelected(item);
	}

	private void item_img_btn_click(final SubjectBean subject) {
		List dates = Util.getPickDates();
		dates.add("已完成");
		dates.add("先暂停");
		if (subject.getIsTodo()) {
			dates.add("非待办事项");
		}
		String[] pickdates = (String[]) dates.toArray(new String[dates.size()]);
		new AlertDialog.Builder(act)
				.setTitle(subject.getBody())
				// .setIcon(android.R.drawable.ic_dialog_info)
				.setSingleChoiceItems(pickdates, -1,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								subject.setIsTodo(true);
								switch (which) {
								case 0:
								case 1:
								case 2:
									String start_date = Util.getDateStr(which);
									subjectDa.set_todo_start_date(
											subject.getId(), start_date);
									subject.setIsTodo(true);
									subject.setPlanStartDate(start_date);
									break;
								case 3:
									String start_date2 = Util.getDateStr(10);
									subjectDa.set_todo_start_date(
											subject.getId(), start_date2);
									subject.setIsTodo(true);
									subject.setPlanStartDate(start_date2);
									break;
								case 4: // done
									subjectDa.set_todo_status(subject.getId(),
											2);
									break;
								case 5: // block
									subjectDa.set_todo_status(subject.getId(),
											3);
									break;
								case 6: // 非待办事项
									subject.setIsTodo(false);
									subjectDa.set_todo(subject.getId(), false);
									break;
								}

								dialog.dismiss();
								
								taskList.clear();
								load_data(0, 500);

								
							}
						}).setNegativeButton("取消", null).show();
	}

	private class ListAdapter extends BaseAdapter {
		private LayoutInflater inflater1;

		public ListAdapter(Context ctx1) {
			this.inflater1 = LayoutInflater.from(ctx1);
		}

		@Override
		public int getCount() {
			return taskList.size();
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
		public boolean isEnabled(int position) {
			// TODO Auto-generated method stub
			if (groups.contains(getItem(position))) {
				return false;
			}
			return super.isEnabled(position);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {

			Object item = taskList.get(position);
			Log.i("listviewgetview", String.valueOf(item));

			if (item.getClass() == SubjectBean.class) {
				final SubjectBean s = (SubjectBean) item;
				
				convertView = inflater1.inflate(R.layout.listview_item, null);

				ImageButton ibtn = (ImageButton) convertView
						.findViewById(R.id.btnIcon);
				ibtn.setImageResource(R.drawable.ic_flag);
				TextView text = (TextView) convertView
						.findViewById(R.id.tvBody);
				
				String content = s.getBody().replaceAll("\n", "") ;
				//content = content + " l:" + String.valueOf(s.getId()) + ",r:" + String.valueOf(s.getRemoteId()) ;
				text.setText(content);

				switch (s.get_sort_status()) {
				case 0:
				default: // 0, 普通备忘
					ibtn.setImageResource(R.drawable.ic_note);
					break;
				case 1: // 1, 待办
					ibtn.setImageResource(R.drawable.ic_flag);
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
						item_img_btn_click(s);
					}
				}); 

			} else {
				convertView = inflater1.inflate(R.layout.listview_group, null);
				TextView text = (TextView) convertView
						.findViewById(R.id.group_title);
				text.setText((String) item);
			}

			return convertView;

		}

	}

}
