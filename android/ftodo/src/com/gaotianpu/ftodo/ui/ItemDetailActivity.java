package com.gaotianpu.ftodo.ui;

 
import java.util.List;
import com.gaotianpu.ftodo.MainActivity;
import com.gaotianpu.ftodo.MyApplication;
import com.gaotianpu.ftodo.R;
import com.gaotianpu.ftodo.da.SubjectBean;
import com.gaotianpu.ftodo.da.SubjectDa;
import com.gaotianpu.ftodo.da.UserBean;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
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
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText; 
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
 

public class ItemDetailActivity extends Activity {

	public static final String SUBJECT_LOCAL_ID = "subject_local_id";

	public static final String FRAGMENT_SORT = "FRAGMENT_SORT";

	private static Activity ctx;
	private MyApplication app;
	private static SubjectDa subjectDa;

	private static UserBean user;
	private static SubjectBean currentSubject;

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
		currentSubject = subjectDa.load_by_localId(user.getUserId(),
				subject_local_id);
		setTitle(currentSubject.getBody());

		// 4. event binding
		if (savedInstanceState == null) {
			Fragment f = new DetailReadFragment();
			getFragmentManager().beginTransaction().replace(R.id.container, f)
					.commit();
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
			item.setChecked(true);
			getFragmentManager().beginTransaction()
					.replace(R.id.container, new DetailEditFragment()).commit();
			break;
		// case R.id.action_item_todo:
		// getFragmentManager().beginTransaction()
		// .replace(R.id.container, new DetailTodoFragment()).commit();
		// break;
		// case R.id.action_item_remind:
		// getFragmentManager().beginTransaction()
		// .replace(R.id.container, new DetailRemindFragment())
		// .commit();
		// break;
		case R.id.action_item_delete:
			delete();
			break;
		case R.id.action_item_read:
			item.setChecked(true);
			getFragmentManager().beginTransaction()
					.replace(R.id.container, new DetailReadFragment()).commit();
		default:
			return super.onOptionsItemSelected(item); // 如果没有这条语句，Fragment下的菜单将不会被执行
		}

		
		return super.onOptionsItemSelected(item);
	}
	
	
	
	private void delete(){
		new AlertDialog.Builder(ctx)
		.setTitle(R.string.dialog_delete_title)
		.setMessage(R.string.dialog_delete_message)
		.setPositiveButton(R.string.dialog_delete_sure,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog,
							int i) { 
						
						 
							subjectDa.delete(currentSubject.getId());
							
							Intent intent=new Intent();
							intent.setClass(getApplicationContext(), MainActivity.class);
							startActivity(intent);
							finish();

							
							//goback?
//							Runtime runtime = Runtime.getRuntime();
//							runtime.exec("input keyevent " + KeyEvent.KEYCODE_BACK);
						 
						
						Log.i("dialog", "ok");
					}
				})
		.setNegativeButton(R.string.dialog_delete_cancel,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog,
							int i) {
						//Log.i("dialog", "cancel"); 
					}
				}).show();
	}

	// 浏览模式
	public static class DetailReadFragment extends Fragment {
		private TextView txtSubjectBody;
		private ListView lvDefault;
		private EditText txtNew;

		private ListAdapter listAdapter;
		private List<SubjectBean> subjectList;

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.detail_read, container,
					false);

			// 追加评论这部分，还没有想好该怎么处理，贸然的添加该共功能，容易给用户造成理解上的困扰？

			// 1. ui
			txtSubjectBody = (TextView) rootView
					.findViewById(R.id.subject_body);
			// lvDefault = (ListView) rootView.findViewById(R.id.lvDefault);
			// txtNew = (EditText) rootView.findViewById(R.id.txtNew);

			// data bindding
			txtSubjectBody.setText(currentSubject.getBody());

			// subjectList = new ArrayList<SubjectBean>();
			// listAdapter = new ListAdapter(getActivity());
			// lvDefault.setAdapter(listAdapter);

			//
			// subjectList = subjectDa.load_son_subjects(user.getUserId(),
			// currentSubject.getId());
			//
			// if (currentSubject.getParentId() != 0) {
			// SubjectBean subject = subjectDa.load_by_localId(
			// user.getUserId(), currentSubject.getParentId());
			// if (subject != null) {
			// subjectList.add(0, subject);
			// }
			// }
			// subjectList.add(0,currentSubject);
			// listAdapter.notifyDataSetChanged();

			// txtNew_setOnKeyListener();
			// lvDefault_setOnItemClickListener(); 需要太多次后退才能到达？ Fragment 堆栈？

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

								// user = app.getUser();
								// Long subjectID =
								long pk_id = subjectDa.insert(user.getUserId(),
										content, currentSubject.getId());

								SubjectBean sbean = subjectDa.load_by_localId(
										user.getUserId(), pk_id);
								subjectList.add(sbean);
								listAdapter.notifyDataSetChanged();

								// SubjectBean subject = new SubjectBean();
								// subject.setId(subjectID);
								// subject.setUserId(user.getUserId());
								// subject.setBody(txtNew.getText().toString().trim());
								//

								// insert_new_item(subject, 0);

								// show new item in ListView
								// lvDefault.setAdapter(new ListAdapter(act,
								// 0));
								txtNew.setText("");
							}

						}
						return true;
					}
					return false;

				}

			});
		}

		private void lvDefault_setOnItemClickListener() {
			// 单击，查看明细
			lvDefault.setOnItemClickListener(new OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1,
						int arg2, long arg3) {

					SubjectBean subject = subjectList.get(arg2);

					Intent detailIntent = new Intent(ctx,
							ItemDetailActivity.class);
					detailIntent.putExtra(ItemDetailActivity.SUBJECT_LOCAL_ID,
							subject.getId());
					startActivity(detailIntent);

				}
			});

		}

		private class ListAdapter extends BaseAdapter {
			private LayoutInflater inflater1;
			private int action_sort = 0;

			public ListAdapter(Context ctx1) {
				this.inflater1 = LayoutInflater.from(ctx1);

			}

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
				SubjectBean subject = subjectList.get(position);
				convertView = inflater1.inflate(R.layout.listview_item, null);
				TextView tv = (TextView) convertView.findViewById(R.id.tvBody);
				tv.setText("" + subject.getBody());

				// if(currentSubject.getId() == subject.getId() ){
				// convertView.setBackgroundColor();
				// }

				return convertView;

			}

		}

	}

	// 文本编辑模式
	public static class DetailEditFragment extends Fragment {
		private EditText txtEdit;
		private InputMethodManager imm;

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.detail_edit, container,
					false);

			txtEdit = (EditText) rootView.findViewById(R.id.txtEdit);
			txtEdit.setText(currentSubject.getBody());
			txtEdit.setSelection(currentSubject.getBody().length());
			txtEdit.requestFocus();

			// 自动开启软键盘
			imm = (InputMethodManager) ctx
					.getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);

			setHasOptionsMenu(true);
			return rootView;
		}

		@Override
		public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
			menu.clear();
			inflater.inflate(R.menu.item_edit, menu);
		}

		@Override
		public boolean onOptionsItemSelected(MenuItem item) {
			// 隐藏键盘
			imm.hideSoftInputFromWindow(txtEdit.getWindowToken(), 0);

			int id = item.getItemId();
			switch (id) {
			case R.id.action_item_save:
				String newTxt = txtEdit.getText().toString().trim();
				ctx.setTitle(newTxt);
				currentSubject.setBody(newTxt);
				subjectDa.edit_content(currentSubject.getId(), newTxt);

				getFragmentManager().beginTransaction()
						.replace(R.id.container, new DetailReadFragment())
						.commit();

				break;
			case R.id.action_item_cancel:
				getFragmentManager().beginTransaction()
						.replace(R.id.container, new DetailReadFragment())
						.commit();

				break;
			}
			return super.onOptionsItemSelected(item);
		}
	}

	// todo模式
	public static class DetailTodoFragment extends Fragment {
		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.detail_todo, container,
					false);
			return rootView;
		}
	}

	// 提醒模式
	public static class DetailRemindFragment extends Fragment {
		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.detail_todo, container,
					false);
			return rootView;
		}
	}

	// 删除模式
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
