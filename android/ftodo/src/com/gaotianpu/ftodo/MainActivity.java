package com.gaotianpu.ftodo; 


import com.gaotianpu.ftodo.R;
import com.gaotianpu.ftodo.SubjectBean;
import com.gaotianpu.ftodo.SQLiteHelper; 


import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
//import android.app.ActionBar;
import android.app.Fragment;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnKeyListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
//import android.os.Build;

public class MainActivity extends Activity {

	private EditText txtNew;
	private ListView lvDefault;
	
	private SQLiteDatabase db;
	private SQLiteHelper dbHelper;
	private ListAdapter listAdapter;
	
	private String deviceId; //设备id
	
	private List<SubjectBean> subjectList = new ArrayList<SubjectBean>();
	
	private class ListAdapter extends BaseAdapter{

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
			convertView = getLayoutInflater().inflate(R.layout.listview_item, null);
			
			TextView tv = (TextView) convertView.findViewById(R.id.tvBody);
			tv.setText("" + subjectList.get(position).getBody());
			
			return convertView;
		}
		
	}
	
	private void render_lvDefault(){
		try{
    		    	
        	/* 查询表，得到cursor对象 */
        	Cursor cursor = db.query("subjects", null, null, null, null, null, "remote_id DESC,pk_id desc");
        	cursor.moveToFirst();
        	while(!cursor.isAfterLast() && (cursor.getString(1) != null)){    
        		SubjectBean subject = new SubjectBean();
        		subject.setId(cursor.getLong(0));
        		subject.setBody(cursor.getString(1));
        		subject.setCreationDate(cursor.getInt(2));
        		subjectList.add(subject);
        		cursor.moveToNext();
        	}
    	}catch(IllegalArgumentException e){
    		//当用SimpleCursorAdapter装载数据时，表ID列必须是_id，否则报错column '_id' does not exist
    		e.printStackTrace();
    		//当版本变更时会调用SQLiteHelper.onUpgrade()方法重建表 注：表以前数据将丢失
//    		++ DB_VERSION;
//    		dbHelper.onUpgrade(db, --DB_VERSION, DB_VERSION);
//    		dbHelper.updateColumn(db, SQLiteHelper.ID, "_"+SQLiteHelper.ID, "integer");
    	}
		
		 
    	listAdapter = new ListAdapter();
    	lvDefault.setAdapter(listAdapter);
	}

	private void bind_post_new_task() {

		
		txtNew.setOnKeyListener(new OnKeyListener() {
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				// TODO Auto-generated method stub
				if (keyCode == KeyEvent.KEYCODE_ENTER) {
					InputMethodManager imm = (InputMethodManager) v
							.getContext().getSystemService(
									Context.INPUT_METHOD_SERVICE);
					if (imm.isActive()) {
						imm.hideSoftInputFromWindow(
								v.getApplicationWindowToken(), 0);
						
						//insert into sqlite						
						//show new item in ListView
						if(txtNew.getText().length() > 1 ){
							ContentValues values = new ContentValues();
							values.put("body", txtNew.getText().toString().trim());
							values.put("creation_date",1); //
							values.put("last_update",0);
							values.put("last_sync",0);
							values.put("is_del",0);
							values.put("is_sync",0);
							values.put("remote_id",0);
							//插入数据 用ContentValues对象也即HashMap操作,并返回ID号
							Long subjectID = db.insert("subjects", "pk_id", values);
							
							SubjectBean subject = new SubjectBean();
			        		subject.setId(subjectID);
			        		subject.setBody(txtNew.getText().toString().trim()  );
			        		subject.setCreationDate(1);
			        		subjectList.add(0,subject);  
			        		
			        		lvDefault.setAdapter(new ListAdapter());
			        		txtNew.setText(""); 
						}
						
					}
					return true;
				}
				return false;

			}

		});
	} 

	// ///////////////////

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		if (savedInstanceState == null) {
			getFragmentManager().beginTransaction()
					.add(R.id.container, new PlaceholderFragment()).commit();
		}
		
		//控件初始化
		txtNew = (EditText) findViewById(R.id.txtNew);
		lvDefault = (ListView)findViewById(R.id.lvDefault);
		
		// sqlite 初始化
		dbHelper = new SQLiteHelper(this, "ftodo", null, 1);		 
		db = dbHelper.getWritableDatabase();	
		
		//获得设备id
		TelephonyManager tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE); 
		deviceId = tm.getDeviceId();
		
		render_lvDefault();
		bind_post_new_task();

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class PlaceholderFragment extends Fragment {

		public PlaceholderFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_main, container,
					false);
			return rootView;
		}
	}

}
