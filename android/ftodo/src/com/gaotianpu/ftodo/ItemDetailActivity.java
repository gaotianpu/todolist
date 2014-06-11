package com.gaotianpu.ftodo; 

import android.app.Activity;
 
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
 

public class ItemDetailActivity extends Activity {
	
	public static final String SUBJECT_LOCAL_ID = "subject_local_id"; 
	
	private Context ctx;	
	private MyApplication app;
	private UserBean user;
	
	private Long subject_local_id  ;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fragment_item_detail );  // activity_item_detail.xml
		
		ctx = this;		
		app = (MyApplication) ctx.getApplicationContext(); 
		user = app.getUser();
		
		Intent intent= getIntent();
		subject_local_id = intent.getLongExtra(SUBJECT_LOCAL_ID, 0);
		
		render_details();
		
		Log.i("setOnItemClickListener", "ItemDetailActivity:" + String.valueOf(subject_local_id));

		if (savedInstanceState == null) {
//			getFragmentManager().beginTransaction()
//					.add(R.id.container, new PlaceholderFragment()).commit();
		}
		
		setTitle("details");
	}
	
	private void render_details(){		
		SubjectBean subject = SubjectDa.load_by_localId(ctx, user.getUserId(), subject_local_id);
		Log.i("render_details", subject.getBody());
		
		//subject_body
		TextView txtSubjectBody = (TextView) findViewById(R.id.subject_body);
		txtSubjectBody.setText(subject.getBody());
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		//getMenuInflater().inflate(R.menu.item_detail, menu);
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
}
