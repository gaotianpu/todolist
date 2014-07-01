package com.gaotianpu.ftodo.ui;

import java.util.ArrayList;
import java.util.List;

import com.gaotianpu.ftodo.MyApplication;
import com.gaotianpu.ftodo.R;
import com.gaotianpu.ftodo.bean.SettingBean;
import com.gaotianpu.ftodo.bean.SubjectBean;
import com.gaotianpu.ftodo.bean.UserBean;
import com.gaotianpu.ftodo.da.SubjectDa;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class ListTodoFragment extends Fragment {
	private View rootView;

	private Activity act;
	private MyApplication app;
	private UserBean user;
	private SubjectDa da;

	private ListView lvDefault;
	private ListAdapter listAdapter;

	private List<Object> taskList = new ArrayList<Object>(); // 合并后的数据
	private List<SubjectBean> taskList0 = new ArrayList<SubjectBean>();
	private List<SubjectBean> taskList1 = new ArrayList<SubjectBean>();
	private List<SubjectBean> taskList2 = new ArrayList<SubjectBean>();
	private List<SubjectBean> taskList3 = new ArrayList<SubjectBean>();
	private List<SubjectBean> taskList4 = new ArrayList<SubjectBean>();
	private List<SubjectBean> taskList5 = new ArrayList<SubjectBean>();

	private List<String> groups;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		rootView = inflater.inflate(R.layout.fragment_list_todo, container,
				false);

		act = getActivity();
		app = (MyApplication) act.getApplicationContext();
		user = app.getUser();

		da = new SubjectDa(act);

		groups = java.util.Arrays.asList(act.getResources().getStringArray(
				R.array.todo_plan_date_sorts));

		lvDefault = (ListView) rootView.findViewById(R.id.lvDefault);
		Log.i("lvDefault", lvDefault.toString());

		listAdapter = new ListAdapter(act);
		lvDefault.setAdapter(listAdapter);
		load_data(0, 100);

		// lvDefault_setOnItemClickListener();

		return rootView;
	}

	private void load_data(int offset, int size) {
		List<SubjectBean> list = da.load_todo(user.getUserId(), offset, size);
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
				convertView = inflater1.inflate(R.layout.listview_item, null);
				TextView text = (TextView) convertView
						.findViewById(R.id.tvBody);

				SubjectBean s = (SubjectBean) item;
				text.setText(s.getBody());

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
