package com.gaotianpu.ftodo.ui;

 

import com.gaotianpu.ftodo.MyApplication;
import com.gaotianpu.ftodo.R;

import com.gaotianpu.ftodo.da.SubjectBean;
import com.gaotianpu.ftodo.da.SubjectDa;
import com.gaotianpu.ftodo.da.UserBean;

import android.app.Activity;
import android.app.Fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

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
import android.widget.EditText;
import android.widget.TextView;

public class ItemDetailActivity extends Activity {

	public static final String SUBJECT_LOCAL_ID = "subject_local_id";
	
	public static final String FRAGMENT_SORT = "FRAGMENT_SORT";

	private Context ctx;
	private MyApplication app;
	private SubjectDa subjectDa;

	private UserBean user;
	private static SubjectBean parentSubject;

	private Long subject_local_id; 

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_item_detail);  

		// 0.外部传入参数
		Intent intent = getIntent();
		subject_local_id = intent.getLongExtra(SUBJECT_LOCAL_ID, 0); 

		// 1 全局
		ctx = this;
		app = (MyApplication) ctx.getApplicationContext();
		user = app.getUser();
		subjectDa = new SubjectDa(this); 
		
		// 2 UI
		
		// 3 load data
		parentSubject = subjectDa.load_by_localId(user.getUserId(),
				subject_local_id);
		setTitle(parentSubject.getBody());
		
		// 4. event binding  
 		if (savedInstanceState == null) {
 			Fragment f = new DetailReadFragment(); 
			getFragmentManager().beginTransaction()
					.add(R.id.container, f).commit();
		}

	}

	

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.item, menu);
		return super.onCreateOptionsMenu(menu);

	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		int id = item.getItemId();

		if (item.isChecked()) {
			return true;
		}

		switch (id) {
		case R.id.action_item_edit:
			getFragmentManager().beginTransaction()
			.add(R.id.container, new DetailEditFragment()).commit();
			break;
		case R.id.action_item_todo:
			getFragmentManager().beginTransaction()
			.add(R.id.container, new DetailTodoFragment()).commit();
			break;
		case R.id.action_item_remind:
			getFragmentManager().beginTransaction()
			.add(R.id.container, new DetailRemindFragment()).commit();
			break;
		case R.id.action_item_delete:
			getFragmentManager().beginTransaction()
			.add(R.id.container, new DetailDeleteFragment()).commit();
			break;
		case R.id.action_item_read:
			getFragmentManager().beginTransaction()
			.add(R.id.container, new DetailReadFragment()).commit();
		default:
			break;

		}

		item.setChecked(true);  
		return super.onOptionsItemSelected(item);
	}
	
	//浏览模式
	public static class DetailReadFragment extends Fragment {
		private UserBean user;
		private MyApplication app;
		
		private TextView txtSubjectBody;
		private EditText txtNew;
		
		
		
		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.detail_read, container,
					false);
			
			txtSubjectBody = (TextView)rootView.findViewById(R.id.subject_body);
			txtNew = (EditText) rootView.findViewById(R.id.txtNew);
			
			txtNew_setOnKeyListener(); 
			
			return rootView;
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
							// insert into sqlite
							String content = txtNew.getText().toString().trim();
							if (content.length() > 1) {

//								user = app.getUser();
//								Long subjectID = subjectDa.insert(user.getUserId(),
//										content, parentSubject.getId());

//								SubjectBean subject = new SubjectBean();
//								subject.setId(subjectID);
//								subject.setUserId(user.getUserId());
//								subject.setBody(txtNew.getText().toString().trim());
//							 

								// insert_new_item(subject, 0);

								// show new item in ListView
								// lvDefault.setAdapter(new ListAdapter(act, 0));
								txtNew.setText("");
							}

						}
						return true;
					}
					return false;

				}

			});
		}
	}
	
	//文本编辑模式
	public static class DetailEditFragment extends Fragment { 
		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.detail_edit, container,
					false);
			
			return rootView;
		}
	}
	
	//todo模式
	public static class DetailTodoFragment extends Fragment { 
		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.detail_todo, container,
					false);
			return rootView;
		}
	}
	
	//提醒模式
	public static class DetailRemindFragment extends Fragment { 
		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.detail_todo, container,
					false);
			return rootView;
		}
	}
	
	//删除模式
	public static class DetailDeleteFragment extends Fragment { 
		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.detail_todo, container,
					false);
			return rootView;
		}
	}
	
}
