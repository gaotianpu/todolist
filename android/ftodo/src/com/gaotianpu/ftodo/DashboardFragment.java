package com.gaotianpu.ftodo;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class DashboardFragment extends Fragment {
	private View rootView; 
	
	private MyApplication app;
	private UserBean user;
	
	private Activity act;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		rootView = inflater.inflate(R.layout.fragment_dashboard, container, false);
		
		act = getActivity();
		app = (MyApplication) act.getApplicationContext();
		user = app.getUser();
		
		act.setTitle(  user.getEmail()  );
		return rootView;
	}

}
