package com.gaotianpu.ftodo;

import com.gaotianpu.ftodo.R;
import com.gaotianpu.ftodo.SubjectBean;
import com.gaotianpu.ftodo.SQLiteHelper;
import com.loopj.android.http.JsonHttpResponseHandler;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
//import android.app.ActionBar;
import android.app.Fragment;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
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
import android.util.Log;

//import android.os.Build;

public class MainActivity extends Activity {

	private EditText txtNew;
	private ListView lvDefault;
	
	private Context context;

	private SQLiteDatabase db;
	private SQLiteHelper dbHelper;
	private ListAdapter listAdapter;

	private String device_type; // 设备型号
	private String deviceId; // 设备id
	private long cust_id = 1;

	private List<SubjectBean> subjectList ;

	private class ListAdapter extends BaseAdapter {

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
			convertView = getLayoutInflater().inflate(R.layout.listview_item,
					null);

			TextView tv = (TextView) convertView.findViewById(R.id.tvBody);
			tv.setText("" + subjectList.get(position).getBody());

			return convertView;
		}

	}

	private void render_lvDefault() {
		subjectList = SubjectDa.load(context, cust_id, 1, 50); 
		listAdapter = new ListAdapter();
		lvDefault.setAdapter(listAdapter);
	}

	private void load_from_cloudy(int page,int size) {
		FTDClient.load_by_custId(cust_id, page, size, new JsonHttpResponseHandler() {
			@Override
			public void onSuccess(JSONObject  result) { 
				try {
					JSONArray  resultList = result.getJSONArray("list");
					subjectList.clear();
					
					for(int i=0;i<resultList.length();i++){
						JSONObject item = resultList.getJSONObject(i);
						
						SubjectBean subject = new SubjectBean();
						subject.setId(item.getInt("pk_id"));
						subject.setBody(item.getString("body"));
						//item.getInt("local_id");
						subject.setCreationDate(0);
						subjectList.add(subject);
						
						listAdapter = new ListAdapter();
						lvDefault.setAdapter(listAdapter); 
					}
					
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				 
				
				

			}
		});
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

						// insert into sqlite
						// show new item in ListView
						if (txtNew.getText().length() > 1) {
							Long subjectID = SubjectDa.insert(context, txtNew.getText().toString().trim());  

							SubjectBean subject = new SubjectBean();
							subject.setId(subjectID);
							subject.setBody(txtNew.getText().toString().trim());
							subject.setCreationDate(1);
							subjectList.add(0, subject);

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

		// 控件初始化
		txtNew = (EditText) findViewById(R.id.txtNew);
		lvDefault = (ListView) findViewById(R.id.lvDefault);
		
		context = this;

		// sqlite 初始化
		dbHelper = new SQLiteHelper(this, "ftodo", null, 1);
		db = dbHelper.getWritableDatabase();

		// 获得设备id
		TelephonyManager tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
		deviceId = tm.getDeviceId();
		device_type = android.os.Build.MODEL;
		
		Intent startIntent = new Intent(this, AsyncService.class);  
        startService(startIntent); 

		render_lvDefault();
		
		//检查是否联网，
		//联网 检查本地是否有未上传的task，
		//有，上传，
		//上传成功后，更新sqlite的remote_id
		//再load_from_cloudy
		
		load_from_cloudy(1,50);
		
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
