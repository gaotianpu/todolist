package com.gaotianpu.ftodo;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class SettingFragment extends Fragment {
	private View rootView;

	private Activity act;
	private MyApplication app;
	private UserBean user;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		rootView = inflater
				.inflate(R.layout.fragment_setting, container, false);
		getActivity().setTitle("设置");

		act = getActivity();
		app = (MyApplication) act.getApplicationContext();
		user = app.getUser();

		// 退出按钮
		rootView.findViewById(R.id.btnLogout).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						user = app.logout();

						// 清理sqlite 里的数据？

						
						logout();
						

					}
				});

		return rootView;
	}
	
	private void logout(){
		//转至login页
		Fragment fragment = new LoginFragment();
		FragmentManager fragmentManager = getFragmentManager();
		fragmentManager.beginTransaction()
				.replace(R.id.content_frame, fragment).commit();
		
		// 修改左侧菜单？ 
		String[] drawer_menu_items_unlogin = getResources()
				.getStringArray(R.array.drawer_menu_items_unlogin);
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(act,
				R.layout.drawer_list_item, drawer_menu_items_unlogin);
		
		ListView mDrawerList = (ListView) act
				.findViewById(R.id.left_drawer); 
		mDrawerList.setAdapter(adapter);
		
		mDrawerList.setItemChecked(0, true);
	}

}
