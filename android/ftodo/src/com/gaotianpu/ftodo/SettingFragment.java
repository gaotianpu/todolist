package com.gaotianpu.ftodo;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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

						// 修改左侧菜单？

						// 定位到login

					}
				});

		return rootView;
	}

}
