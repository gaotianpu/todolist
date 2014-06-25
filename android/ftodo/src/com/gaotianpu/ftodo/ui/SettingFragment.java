package com.gaotianpu.ftodo.ui;

import java.util.ArrayList;
import java.util.List;

import com.gaotianpu.ftodo.MyApplication;
import com.gaotianpu.ftodo.R;

import com.gaotianpu.ftodo.da.ReportBean;
import com.gaotianpu.ftodo.da.SettingBean;
import com.gaotianpu.ftodo.da.UserBean;
 
 

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

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

		if (user.getUserId() == 0 || user.getTokenStatus() == 0) {
			//rootView.findViewById(R.id.btnLogout).setVisibility(View.GONE);
		} 

		return rootView;
	}
	
	private List<SettingBean> load_data(){
		List<SettingBean> l = new ArrayList<SettingBean>();
		l.add(new SettingBean("账号(手机号)","138****509" ) );
		l.add(new SettingBean("密码","强" ) );
		l.add(new SettingBean("电子邮箱","gtp@163.com" ) );		
		l.add(new SettingBean("关于","0.1.1.1" ) );		 
		l.add(new SettingBean("退出","" ) );
		return l;
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
			convertView = inflater1.inflate(R.layout.setting_listview_item, null);
			
			TextView tv = (TextView) convertView.findViewById(R.id.tvK);
			tv.setText(item.getK());
			
			TextView tvV = (TextView) convertView.findViewById(R.id.tvV);
			tvV.setText( item.getV() );

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
