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
 

public class ItemDetailActivity extends Activity {
	
	public static final String SUBJECT_LOCAL_ID = "subject_local_id"; 
	
	private Context ctx;	
	private MyApplication app;
	private UserBean user;
	
	private Long subject_local_id  ;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_item_detail);  
		
		ctx = this;		
		app = (MyApplication) ctx.getApplicationContext(); 
		user = app.getUser();
		
		Intent intent= getIntent();
		subject_local_id = intent.getLongExtra(SUBJECT_LOCAL_ID, 0);
		
		render_details();
		
		Log.i("setOnItemClickListener", "ItemDetailActivity:" + String.valueOf(subject_local_id));

		if (savedInstanceState == null) {
			getFragmentManager().beginTransaction()
					.add(R.id.container, new PlaceholderFragment()).commit();
		}
	}
	
	private void render_details(){		
		SubjectBean subject = SubjectDa.load_by_localId(ctx, user.getUserId(), subject_local_id);
		Log.i("render_details", subject.getBody());
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.item_detail, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId(); 
		
		if (id == android.R.id.home) {
			// This ID represents the Home or Up button. In the case of this
			// activity, the Up button is shown. Use NavUtils to allow users
			// to navigate up one level in the application structure. For
			// more details, see the Navigation pattern on Android Design:
			//
			// http://developer.android.com/design/patterns/navigation.html#up-vs-back
			//
			NavUtils.navigateUpTo(this,
					new Intent(this, MainActivity.class));
			return true;
		}
		
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
			View rootView = inflater.inflate(R.layout.fragment_item_detail,
					container, false);
			return rootView;
		}
	}

}
