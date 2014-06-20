package com.gaotianpu.ftodo.ui;

import com.gaotianpu.ftodo.MyApplication;
import com.gaotianpu.ftodo.R;

import com.gaotianpu.ftodo.da.SubjectBean;
import com.gaotianpu.ftodo.da.SubjectDa;
import com.gaotianpu.ftodo.da.UserBean;

import android.app.Activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import android.util.Log;
import android.view.KeyEvent;

import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import android.view.View.OnKeyListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

public class ItemDetailActivity extends Activity {

	public static final String SUBJECT_LOCAL_ID = "subject_local_id";

	private Context ctx;
	private MyApplication app;
	private SubjectDa subjectDa;

	private UserBean user;
	private SubjectBean parentSubject;

	private Long subject_local_id;

	private TextView txtSubjectBody;
	private EditText txtNew;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fragment_item_detail); // activity_item_detail.xml

		// 0.外部传入参数
		Intent intent = getIntent();
		subject_local_id = intent.getLongExtra(SUBJECT_LOCAL_ID, 0);

		// 1 全局
		ctx = this;
		app = (MyApplication) ctx.getApplicationContext();
		user = app.getUser();
		subjectDa = new SubjectDa(this);

		// 2 UI
		txtSubjectBody = (TextView) findViewById(R.id.subject_body);
		txtNew = (EditText) findViewById(R.id.txtNew);

		// 3 load data
		parentSubject = subjectDa.load_by_localId(user.getUserId(),
				subject_local_id);

		txtSubjectBody.setText(parentSubject.getBody());
		setTitle(parentSubject.getBody());

		// 4. event binding
		txtNew_setOnKeyListener();

		Log.i("setOnItemClickListener",
				"ItemDetailActivity:" + String.valueOf(subject_local_id));

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

							user = app.getUser();
							Long subjectID = subjectDa.insert(user.getUserId(),
									content, parentSubject.getId());

//							SubjectBean subject = new SubjectBean();
//							subject.setId(subjectID);
//							subject.setUserId(user.getUserId());
//							subject.setBody(txtNew.getText().toString().trim());
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
			break;
		case R.id.action_item_todo:
			break;
		case R.id.action_item_remind:
			break;
		case R.id.action_item_delete:
			break;
		case R.id.action_item_read:
		default:
			break;

		}

		item.setChecked(true);

		// if (id == R.id.action_item_edit) {
		// return true;
		// }

		return super.onOptionsItemSelected(item);
	}
}
