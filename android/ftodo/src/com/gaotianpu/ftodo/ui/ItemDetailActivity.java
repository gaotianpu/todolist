package com.gaotianpu.ftodo.ui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import java.util.List;
import com.gaotianpu.ftodo.MainActivity;
import com.gaotianpu.ftodo.MyApplication;
import com.gaotianpu.ftodo.R;

import com.gaotianpu.ftodo.bean.SettingBean;
import com.gaotianpu.ftodo.bean.SubjectBean;
import com.gaotianpu.ftodo.bean.UserBean;
import com.gaotianpu.ftodo.da.SubjectDa;
import com.gaotianpu.ftodo.da.Util;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.DatePickerDialog;
import android.app.Dialog;
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

import android.widget.AdapterView;
import android.widget.BaseAdapter;

import android.widget.DatePicker;
import android.widget.EditText;

import android.widget.ListView;

import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class ItemDetailActivity extends Activity {

	public static final String SUBJECT_LOCAL_ID = "subject_local_id";

	public static final String FRAGMENT_SORT = "FRAGMENT_SORT";

	private static Activity act;
	private MyApplication app;
	private static SubjectDa subjectDa;

	private static UserBean user;
	private static SubjectBean subject;

	private static Long subject_local_id;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_item_detail);

		// 0.外部传入参数
		Intent intent = getIntent();
		subject_local_id = intent.getLongExtra(SUBJECT_LOCAL_ID, 0);

		// 1 全局
		act = this;
		app = (MyApplication) act.getApplicationContext();
		user = app.getUser();
		subjectDa = new SubjectDa(this);

		// 2 UI
		//返回按钮
		act.getActionBar().setHomeButtonEnabled(true);
		act.getActionBar().setDisplayHomeAsUpEnabled(true);
		act.getActionBar().setDisplayShowHomeEnabled(true);

		// 3 load data
		subject = subjectDa.load_by_localId(user.getUserId(), subject_local_id);
		setTitle(subject.getBody());

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

		// Log.i("back", String.valueOf( KeyEvent.KEYCODE_HOME ));
		//

		if (item.isChecked()) {
			return true;
		}

		switch (item.getItemId()) {
		case android.R.id.home:
			// //goback?
			Runtime runtime = Runtime.getRuntime();
			try {

				runtime.exec("input keyevent " + KeyEvent.KEYCODE_BACK);
				return true;
			} catch (IOException e) {
				Log.e("goback", e.toString());
				e.printStackTrace();
			}
			break;

		case R.id.action_item_delete:
			delete();
			break;

		default:
			return super.onOptionsItemSelected(item); // 如果没有这条语句，Fragment下的菜单将不会被执行
		}

		return super.onOptionsItemSelected(item);
	}

	private void delete() {
		new AlertDialog.Builder(act)
				.setTitle(R.string.dialog_delete_title)
				.setMessage(R.string.dialog_delete_message)
				.setPositiveButton(R.string.dialog_sure,
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int i) {

								subjectDa.delete(subject.getId());

								Intent intent = new Intent();
								intent.setClass(getApplicationContext(),
										MainActivity.class);
								startActivity(intent);
								finish(); 

								//Log.i("dialog", "ok");
							}
						})
				.setNegativeButton(R.string.dialog_cancel,
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int i) {
								// Log.i("dialog", "cancel");
							}
						}).show();
	}

	public static class DetailReadFragment extends Fragment {
		private TextView txtSubjectBody;
		private ListView lvSubjectInfos;

		private List<SettingBean> infoList;
		private ListAdapter listAdapter;

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.detail_main, container,
					false);

			// UI widget
			txtSubjectBody = (TextView) rootView
					.findViewById(R.id.subject_body);
			lvSubjectInfos = (ListView) rootView
					.findViewById(R.id.lvSubjectInfos);

			// data
			infoList = new ArrayList<SettingBean>();
			listAdapter = new ListAdapter(act);
			lvSubjectInfos.setAdapter(listAdapter);

			load_infos();

			lvSubjectInfos_setOnItemClickListener();

			return rootView;
		}

		private Builder b;
		private Builder builderDialog() {
			if (b == null) {
				b = new AlertDialog.Builder(act);
			}
			return b;
		}

		@Override
		public void onResume() {
			listAdapter.notifyDataSetChanged();
			super.onResume();
		}

		private void load_infos() {
			infoList.clear();

			subject = subjectDa.load_by_localId(user.getUserId(),
					subject_local_id);

			txtSubjectBody.setText(subject.getBody());

			infoList.add(new SettingBean("creation_date",
					getString(R.string.label_creation_date), subject
							.getCreationDate()));
			infoList.add(new SettingBean("a_update_date", "* "
					+ getString(R.string.label_last_update), subject
					.getUpdateDate()));

			// remind
			if (subject.isRemind()) {
				infoList.add(new SettingBean("a_sort", "* "
						+ getString(R.string.label_subject_sort),
						getString(R.string.label_remind)));
				infoList.add(new SettingBean("a_remind_date", "* "
						+ getString(R.string.label_remind_date), subject
						.getRemindDate()));

				String remind_f = "";
				if (subject.getRemindFrequency() > 0) {
					remind_f = act.getResources().getStringArray(
							R.array.remind_frequency_items)[subject
							.getRemindFrequency() - 1];
				}
				infoList.add(new SettingBean("a_remind_frequency", "* "
						+ getString(R.string.label_remind_frequency), remind_f));

				infoList.add(new SettingBean("a_remind_next", "* "
						+ getString(R.string.label_remind_next), subject
						.getNextRemindDate()));

			} else if (subject.isTodo()) {
				infoList.add(new SettingBean("a_sort", "* "
						+ getString(R.string.label_subject_sort),
						getString(R.string.label_todo)));

				String todoStatus = "";
				if (subject.getStatus() == 2) {
					todoStatus = getString(R.string.label_todo_done);
				} else if (subject.getStatus() == 3) {
					todoStatus = getString(R.string.label_todo_block);
				} else {
					todoStatus = getString(R.string.label_todo_doing);
				}

				infoList.add(new SettingBean("a_task_status", "* "
						+ getString(R.string.label_todo_status), todoStatus));

				infoList.add(new SettingBean("a_plan_start_date", "* "
						+ getString(R.string.label_todo_plan_start_date),
						subject.getPlanStartDate()));

				if (subject.getStatus() == 2) {
					infoList.add(new SettingBean("closed_date",
							getString(R.string.label_todo_done_date), subject
									.getClosedDate()));
				} else if (subject.getStatus() == 3) {
					infoList.add(new SettingBean("closed_date",
							getString(R.string.label_todo_block_date), subject
									.getClosedDate()));

				} else {

				}

			} else {

				infoList.add(new SettingBean("a_sort", "* "
						+ getString(R.string.label_subject_sort),
						getString(R.string.label_note)));
				// note
			}

			listAdapter.notifyDataSetChanged();

		}

		private void sort_status_picker() {
			String[] pickdates = act.getResources().getStringArray(
					R.array.subject_sorts);

			int choic_index = -1;
			if (subject.isRemind()) {
				choic_index = 4;
			} else if (subject.isTodo()) {
				if (subject.getStatus() == 3) {
					choic_index = 2;
				} else if (subject.getStatus() == 2) {
					choic_index = 3;
				} else {
					choic_index = 1;
				}

			} else {
				choic_index = 0;
			}

			// new AlertDialog.Builder(act)
			new AlertDialog.Builder(act)
					.setTitle(subject.getBody())
					.setSingleChoiceItems(pickdates, choic_index,
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int which) {
									switch (which) {
									case 0:
										subject.setIsTodo(false);
										subject.setIsRemind(false);
										subjectDa.set_todo(subject.getId(),
												false);
										subjectDa.set_remind(subject.getId(),
												false);
										break;
									case 1: // todo
										subject.setIsTodo(true);
										subject.setStatus(0);
										subjectDa.set_todo_status(
												subject.getId(), 0);
										break;
									case 2: // block
										subject.setIsTodo(true);
										subject.setStatus(3);
										subjectDa.set_todo_status(
												subject.getId(), 3);
										break;
									case 3: // done
										subject.setIsTodo(true);
										subject.setStatus(2);
										subjectDa.set_todo_status(
												subject.getId(), 2);
										break;
									case 4: // remind
									default:
										subject.setIsTodo(false);
										subject.setIsRemind(true);
										subjectDa.set_remind(subject.getId(),
												true);
										break;
									}
									dialog.dismiss();
									load_infos();
								}
							}).setNegativeButton(R.string.dialog_cancel, null)
					.show();

		}

		private void todo_status_picker() {
			String[] pickdates = act.getResources().getStringArray(
					R.array.subject_todo_status);

			int choic_index = -1;
			if (subject.getStatus() == 3) {
				choic_index = 1;
			} else if (subject.getStatus() == 2) {
				choic_index = 2;
			} else {
				choic_index = 0;
			}

			new AlertDialog.Builder(act)
					.setTitle(subject.getBody())
					.setSingleChoiceItems(pickdates, choic_index,
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int which) {
									switch (which) {
									case 0: // todo
										subject.setStatus(0);
										subjectDa.set_todo_status(
												subject.getId(), 0);
										break;
									case 1: // todo-block
										subject.setStatus(3);
										subjectDa.set_todo_status(
												subject.getId(), 3);
										break;
									case 2: // todo-done
										subject.setStatus(2);
										subjectDa.set_todo_status(
												subject.getId(), 2);
										break;
									}

									dialog.dismiss();
									load_infos();

								}
							}).setNegativeButton(R.string.dialog_cancel, null)
					.show();

		}

		private void remind_frequency_picker() {
			String[] pickdates = act.getResources().getStringArray(
					R.array.remind_frequency_items);
			int choic_index = -1;
			if (subject.getRemindFrequency() > 0) {
				choic_index = subject.getRemindFrequency() - 1;
			}
			new AlertDialog.Builder(act)
					.setTitle(subject.getBody())
					.setSingleChoiceItems(pickdates, choic_index,
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int which) {
									subject.setRemindFrequency(which + 1);
									subjectDa.set_remind_frequency(
											subject.getId(),
											subject.getRemindFrequency());

									String next_remind_date = Util.getNextDate(
											subject.getRemindDate(),
											subject.getRemindFrequency());

									subject.setNextRemindDate(next_remind_date);
									subjectDa.set_next_remind(subject.getId(),
											next_remind_date);

									dialog.dismiss();
									load_infos();

								}
							}).setNegativeButton(R.string.dialog_cancel, null)
					.show();

		}

		private void set_remind_date() {
			Calendar c = Calendar.getInstance();

			Date d = Util.str2Date(subject.getRemindDate());
			if (d != null) {
				c.setTime(d);
			}

			Dialog dialog = new DatePickerDialog(act,
					new DatePickerDialog.OnDateSetListener() {
						public void onDateSet(DatePicker dp, int year,
								int month, int dayOfMonth) {

							String remind_date = Util.GetDateFromInts(year,
									month, dayOfMonth);
							subject.setRemindDate(remind_date);
							subjectDa.set_remind_date(subject.getId(),
									remind_date);

							load_infos();
						}
					}, c.get(Calendar.YEAR), // 传入年份
					c.get(Calendar.MONTH), // 传入月份
					c.get(Calendar.DAY_OF_MONTH) // 传入天数
			);
			dialog.setTitle(R.string.label_set_remind_date);
			dialog.setCancelable(true);
			dialog.show();
		}

		private void set_todo_plan_date() {

			List<String> dates = Util.load_pick_dates(act);
			final String[] pickdates = (String[]) dates.toArray(new String[0]);

			int choic_index = -1;
			new AlertDialog.Builder(act)
					.setTitle(subject.getBody())
					.setSingleChoiceItems(pickdates, choic_index,
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int which) {
									String start_date = pickdates[which]
											.split(" ")[1];
									subjectDa.set_todo_start_date(
											subject.getId(), start_date);
									subject.setPlanStartDate(start_date);

									dialog.dismiss();
									load_infos();

								}
							}).setNegativeButton(R.string.dialog_cancel, null)
					.show();
		}

		private void edit_content() {
			final EditText et = new EditText(act);
			et.setText(subject.getBody());
			et.setSelection(subject.getBody().length());

			// ViewGroup.LayoutParams lp = et.getLayoutParams();
			// lp.height = 50;
			// et.setLayoutParams(lp);

			new AlertDialog.Builder(act)
					.setView(et)
					.setPositiveButton(R.string.dialog_sure,
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int i) {
									String newTxt = et.getText().toString()
											.trim();
									act.setTitle(newTxt);
									subject.setBody(newTxt);
									subjectDa.edit_content(subject.getId(),
											newTxt);

									load_infos();

								}
							}).setNegativeButton(R.string.dialog_cancel, null)
					.show();

			// InputMethodManager imm =
			// (InputMethodManager)act.getSystemService(Context.INPUT_METHOD_SERVICE);
			// //得到InputMethodManager的实例
			// if (imm.isActive()) {
			// //如果开启
			// imm.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT,
			// InputMethodManager.HIDE_NOT_ALWAYS);
			// //关闭软键盘，开启方法相同，这个方法是切换开启与关闭状态的
			// }
		}

		private void remin_next() {
			new AlertDialog.Builder(act)
					.setTitle(R.string.label_run_next_remind)
					// .setMessage("已执行")
					.setPositiveButton(R.string.dialog_sure,
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int i) {
//									Log.i("remin_next",
//											subject.getRemindDate()
//													+ " "
//													+ String.valueOf(subject
//															.getRemindFrequency()));
									String next_remind_date = Util.getNextDate2(
											subject.getNextRemindDate(),
											subject.getRemindFrequency());
//									Log.i("remin_next", next_remind_date);

									subject.setNextRemindDate(next_remind_date);
									subjectDa.set_next_remind(subject.getId(),
											next_remind_date);

									load_infos();
								}
							})
					.setNegativeButton(R.string.dialog_cancel,
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int i) {
									// Log.i("dialog", "cancel");
								}
							}).show();
		}

		private void lvSubjectInfos_setOnItemClickListener() {
			// 单击，查看明细
			lvSubjectInfos.setOnItemClickListener(new OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1,
						int arg2, long arg3) {

					SettingBean item = infoList.get(arg2);
					if (item.getId().equals("a_sort")) {
						sort_status_picker();
					} else if (item.getId().equals("a_remind_date")) {
						set_remind_date();
					} else if (item.getId().equals("a_remind_frequency")) {
						remind_frequency_picker();
					} else if (item.getId().equals("a_task_status")) {
						todo_status_picker();
					} else if (item.getId().equals("a_plan_start_date")) {
						set_todo_plan_date();
					} else if (item.getId().equals("a_update_date")) {
						edit_content();
					} else if (item.getId().equals("a_remind_next")) {
						remin_next();
					} else {
						//
						// Log.i("click","----------------");
					}

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
				return infoList.size();
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
				SettingBean item = infoList.get(position);

				convertView = inflater1.inflate(R.layout.setting_listview_item,
						null);

				TextView tv = (TextView) convertView.findViewById(R.id.tvK);
				tv.setText(item.getK());

				TextView tvV = (TextView) convertView.findViewById(R.id.tvV);
				tvV.setText(item.getV());

				boolean is_action = item.getId().substring(0, 2).equals("a_");
				if (!is_action) {
					// 不可进行点击设置的项目，颜色只为灰色？
					// tv.setTextColor( 0xcccccc ) ;
				}

				return convertView;

			}

		}

	}

}
