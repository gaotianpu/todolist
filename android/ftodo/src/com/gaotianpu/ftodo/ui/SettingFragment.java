package com.gaotianpu.ftodo.ui;

import java.util.ArrayList;
import java.util.List;

import com.gaotianpu.ftodo.MyApplication;
import com.gaotianpu.ftodo.R;

import com.gaotianpu.ftodo.bean.ReportBean;
import com.gaotianpu.ftodo.bean.SettingBean;
import com.gaotianpu.ftodo.bean.SubjectBean;
import com.gaotianpu.ftodo.bean.UserBean;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class SettingFragment extends Fragment {
	private View rootView;

	private Activity act;
	private MyApplication app;
	private UserBean user;

	private ListView lvDefault;
	private ListAdapter listAdapter;
	private List<SettingBean> reportList;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		rootView = inflater
				.inflate(R.layout.fragment_setting, container, false);

		act = getActivity();
		app = (MyApplication) act.getApplicationContext();
		user = app.getUser();

		lvDefault = (ListView) rootView.findViewById(R.id.lvDefault);

		reportList = load_data();
		listAdapter = new ListAdapter(act);
		lvDefault.setAdapter(listAdapter);

		if (app.network_available()) {
			lvDefault_setOnItemClickListener();
		}

		return rootView;
	}

	private List<SettingBean> load_data() {
		List<SettingBean> l = new ArrayList<SettingBean>();
		if (user.getUserId() != 0 && user.getTokenStatus() != 0) {
			l.add(new SettingBean("mobile", "账号(手机号)", String.valueOf(user
					.getMobile())));
			l.add(new SettingBean("password", "密码", ""));
			l.add(new SettingBean("email", "电子邮箱", user.getEmail()));
			l.add(new SettingBean("about", "关于", app.get_version_no()));
			l.add(new SettingBean("logout", "退出", ""));
		} else {
			l.add(new SettingBean("about", "关于", app.get_version_no()));
		}
		return l;
	}

	private void lvDefault_setOnItemClickListener() {
		// 单击，查看明细
		lvDefault.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {

				SettingBean item = reportList.get(arg2);
				if (item.getId() == "logout") {
					logout();
				} else {
					Intent intent = new Intent(act, SettingDetailActivity.class);
					intent.putExtra(SettingDetailActivity.SETTING_ITEM_ID,
							item.getId());
					
					intent.putExtra(SettingDetailActivity.SETTING_ITEM_TITLE,
							item.getK());
					
					startActivity(intent);
				}

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
			SettingBean item = reportList.get(position);
			convertView = inflater1.inflate(R.layout.setting_listview_item,
					null);

			TextView tv = (TextView) convertView.findViewById(R.id.tvK);
			tv.setText(item.getK());

			TextView tvV = (TextView) convertView.findViewById(R.id.tvV);
			tvV.setText(item.getV());

			// if(currentSubject.getId() == subject.getId() ){
			// convertView.setBackgroundColor();
			// }

			return convertView;

		}

	}

	private void logout() {
		user = app.logout();

		// 转至login页
		Fragment fragment = new LoginFragment();
		FragmentManager fragmentManager = getFragmentManager();
		fragmentManager.beginTransaction()
				.replace(R.id.content_frame, fragment).commit();

		// 修改左侧菜单？
		String[] drawer_menu_items_unlogin = getResources().getStringArray(
				R.array.drawer_menu_items);
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(act,
				R.layout.drawer_list_item, drawer_menu_items_unlogin);

		ListView mDrawerList = (ListView) act.findViewById(R.id.left_drawer);
		mDrawerList.setAdapter(adapter);

		mDrawerList.setItemChecked(0, true);
	}

}
