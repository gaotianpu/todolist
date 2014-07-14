package com.gaotianpu.ftodo.ui;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import java.util.List;
import com.gaotianpu.ftodo.MainActivity;
import com.gaotianpu.ftodo.MyApplication;
import com.gaotianpu.ftodo.R;
import com.gaotianpu.ftodo.bean.DateBean;

import com.gaotianpu.ftodo.bean.SettingBean;
import com.gaotianpu.ftodo.bean.SubjectBean;
import com.gaotianpu.ftodo.bean.UserBean;
import com.gaotianpu.ftodo.da.SubjectDa;
import com.gaotianpu.ftodo.da.Util;

import android.app.Activity;
import android.app.AlertDialog;
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
import android.view.View.OnKeyListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;

import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
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

	private Long subject_local_id;

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
		// //goback?
		// Runtime runtime = Runtime.getRuntime();
		// try {
		// Log.i("back", String.valueOf( item.getItemId() ) );
		// runtime.exec("input keyevent " + KeyEvent.KEYCODE_BACK);
		// return true;
		// } catch (IOException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }

		if (item.isChecked()) {
			return true;
		}

		switch (id) {
		case R.id.action_item_edit:
			item.setChecked(true);
			getFragmentManager().beginTransaction()
					.replace(R.id.container, new DetailEditFragment()).commit();
			break;
		case R.id.action_item_todo:
			item.setChecked(true);
			getFragmentManager().beginTransaction()
					.replace(R.id.container, new DetailTodoFragment()).commit();
			break;
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

	private void delete() {
		new AlertDialog.Builder(act)
				.setTitle(R.string.dialog_delete_title)
				.setMessage(R.string.dialog_delete_message)
				.setPositiveButton(R.string.dialog_delete_sure,
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int i) {

								subjectDa.delete(subject.getId());

								Intent intent = new Intent();
								intent.setClass(getApplicationContext(),
										MainActivity.class);
								startActivity(intent);
								finish();

								// goback?
								// Runtime runtime = Runtime.getRuntime();
								// runtime.exec("input keyevent " +
								// KeyEvent.KEYCODE_BACK);

								Log.i("dialog", "ok");
							}
						})
				.setNegativeButton(R.string.dialog_delete_cancel,
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

		private void load_infos() {
			infoList.clear();
			
			txtSubjectBody.setText(subject.getBody());

			infoList.add(new SettingBean("creation_date",
					getString(R.string.label_creation_date), subject
							.getCreationDate()));
			infoList.add(new SettingBean("update_date",
					getString(R.string.label_last_update), subject
							.getUpdateDate()));

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
					R.array.subject_sorts) ; //(String[]) dates.toArray(new String[dates.size()]);
			new AlertDialog.Builder(act)
					.setTitle(subject.getBody())
					// .setIcon(android.R.drawable.ic_dialog_info)
					.setSingleChoiceItems(pickdates, -1,
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int which) { 
									
									dialog.dismiss();
									listAdapter.notifyDataSetChanged();
									
									
								}
							}).setNegativeButton("取消", null).show();
			
		}
		
		private void todo_status_picker() { 
			String[] pickdates = act.getResources().getStringArray(
					R.array.subject_todo_status) ; //(String[]) dates.toArray(new String[dates.size()]);
			new AlertDialog.Builder(act)
					.setTitle(subject.getBody())
					// .setIcon(android.R.drawable.ic_dialog_info)
					.setSingleChoiceItems(pickdates, -1,
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int which) { 
									
									dialog.dismiss();
									listAdapter.notifyDataSetChanged();
									
									
								}
							}).setNegativeButton("取消", null).show();
			
		}
		
		private void remind_frequency_picker() { 
			String[] pickdates = act.getResources().getStringArray(
					R.array.remind_frequency_items) ; //(String[]) dates.toArray(new String[dates.size()]);
			new AlertDialog.Builder(act)
					.setTitle(subject.getBody())
					// .setIcon(android.R.drawable.ic_dialog_info)
					.setSingleChoiceItems(pickdates, -1,
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int which) {

//									switch (which) {
//									case 0: // todo
//										subject.setIsTodo(true);
//										subject.setStatus(0);
//										subjectDa.set_todo_status(subject.getId(),
//												0);
//										break;
//									case 1: // todo-done
//										subject.setIsTodo(true);
//										subject.setStatus(2);
//										subjectDa.set_todo_status(subject.getId(),
//												2);
//										break;
//									case 2: // todo-block
//										subject.setIsTodo(true);
//										subject.setStatus(3);
//										subjectDa.set_todo_status(subject.getId(),
//												3);
//										break;
//									case 3: // remind
//										subject.setIsTodo(false);
//										subject.setIsRemind(true);
//										subjectDa.set_remind(subject.getId(), true);
//										break;
//									case 4: // normal-note
//									default:
//										subject.setIsTodo(false);
//										subject.setIsRemind(false);
//										subjectDa.set_todo(subject.getId(), false);
//										subjectDa.set_remind(subject.getId(), false);
//										break;
//									} 
									dialog.dismiss();
									listAdapter.notifyDataSetChanged();
									
									
								}
							}).setNegativeButton("取消", null).show();
			
		}

		private void lvSubjectInfos_setOnItemClickListener() {
			// 单击，查看明细
			lvSubjectInfos.setOnItemClickListener(new OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1,
						int arg2, long arg3) {

					SettingBean item = infoList.get(arg2);
					if(item.getId().equals("a_sort") ){						 
						sort_status_picker();
					}else if(item.getId().equals("a_remind_date")){
						//today
						//tommorrow
						//sss
						//
						Log.i("click","a_remind_date");
					}else if(item.getId().equals("a_remind_frequency")){						 
						remind_frequency_picker();
					}else if(item.getId().equals("a_task_status")){
						todo_status_picker();
					}else if(item.getId().equals("a_plan_start_date")){
						//
						Log.i("click","a_plan_start_date");
					}else{
						//
						//Log.i("click","----------------");
					}

					load_infos();
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

	// 浏览模式
	public static class DetailReadFragment1 extends Fragment {
		private TextView txtSubjectBody;
		private TextView subject_created_date;
		private TextView subject_last_update;
		private TextView subject_start_date;
		private TextView subject_done_date;
		private TextView subject_remind_date;
		private TextView subject_remind_frequency;

		private LinearLayout layoutTodoRemind;

		private LinearLayout layoutTodo;
		private LinearLayout layoutTodoDoneDate;
		private ImageButton btnTodo;
		private ImageButton btnTodo1;

		private LinearLayout layoutRemind;
		private ImageButton btnRemind;
		private ImageButton btnRemind1;

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
			subject_created_date = (TextView) rootView
					.findViewById(R.id.subject_created_date);
			subject_last_update = (TextView) rootView
					.findViewById(R.id.subject_last_update);
			subject_start_date = (TextView) rootView
					.findViewById(R.id.subject_start_date);
			subject_done_date = (TextView) rootView
					.findViewById(R.id.subject_done_date);
			subject_remind_date = (TextView) rootView
					.findViewById(R.id.subject_remind_date);
			subject_remind_frequency = (TextView) rootView
					.findViewById(R.id.subject_remind_frequency);

			layoutTodoRemind = (LinearLayout) rootView
					.findViewById(R.id.layoutTodoRemind);
			layoutTodo = (LinearLayout) rootView.findViewById(R.id.layoutTodo);
			layoutRemind = (LinearLayout) rootView
					.findViewById(R.id.layoutRemind);
			layoutTodoDoneDate = (LinearLayout) rootView
					.findViewById(R.id.layoutTodoDoneDate);

			btnRemind = (ImageButton) rootView.findViewById(R.id.btnRemind);
			btnRemind1 = (ImageButton) rootView.findViewById(R.id.btnRemind1);
			btnTodo = (ImageButton) rootView.findViewById(R.id.btnTodo);
			btnTodo1 = (ImageButton) rootView.findViewById(R.id.btnTodo1);

			// lvDefault = (ListView) rootView.findViewById(R.id.lvDefault);
			// txtNew = (EditText) rootView.findViewById(R.id.txtNew);

			// data bindding

			data_bindding();

			event_binding();

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

		private void event_binding() {
			btnRemind.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					set_or_cancel_remind();
				}
			});
			btnRemind1.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					set_or_cancel_remind();
				}
			});

			btnTodo.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					set_todo();
				}
			});
			btnTodo1.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					set_todo();
				}
			});
		}

		private void set_todo() {
			List dates = Util.getPickDates();
			dates.add("已完成");
			dates.add("先暂停");
			if (subject.isTodo()) {
				dates.add("非待办事项");
			}
			String[] pickdates = (String[]) dates.toArray(new String[dates
					.size()]);
			new AlertDialog.Builder(act)
					.setTitle(subject.getBody())
					// .setIcon(android.R.drawable.ic_dialog_info)
					.setSingleChoiceItems(pickdates, -1,
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int which) {
									subject.setIsTodo(true);
									switch (which) {
									case 0:
									case 1:
									case 2:
										String start_date = Util
												.getDateStr(which);
										subjectDa.set_todo_start_date(
												subject.getId(), start_date);
										subject.setIsTodo(true);
										subject.setPlanStartDate(start_date);
										break;
									case 3:
										String start_date2 = Util
												.getDateStr(10);
										subjectDa.set_todo_start_date(
												subject.getId(), start_date2);
										subject.setIsTodo(true);
										subject.setPlanStartDate(start_date2);
										break;
									case 4: // done
										subjectDa.set_todo_status(
												subject.getId(), 2);
										break;
									case 5: // block
										subjectDa.set_todo_status(
												subject.getId(), 3);
										break;
									case 6: // 非待办事项
										subject.setIsTodo(false);
										subjectDa.set_todo(subject.getId(),
												false);
										break;
									}

									dialog.dismiss();
									data_bindding();

								}
							}).setNegativeButton("取消", null).show();
		}

		private void set_or_cancel_remind() {
			if (subject.isRemind()) {
				// 提示是否要取消？
				new AlertDialog.Builder(act)
						.setTitle("取消提醒功能")
						.setMessage("确定吗？")
						.setPositiveButton("是",
								new DialogInterface.OnClickListener() {// 设置确定的按键
									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										// do something
										subject.setIsRemind(false);
										subjectDa.set_remind(subject.getId(),
												false);
										data_bindding();
									}
								})
						.setNegativeButton("否",
								new DialogInterface.OnClickListener() {// 设置确定的按键
									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										set_remind();
									}
								}).show();

			} else {
				set_remind();
			}
		}

		private void set_remind() {

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
									month, dayOfMonth); // String.format("%d-%d-%d",
														// year,month+1,dayOfMonth);

							subject.setRemindDate(remind_date);
							subjectDa.set_remind_date(subject.getId(),
									remind_date);

							String[] items = act.getResources().getStringArray(
									R.array.remind_frequency_items);
							new AlertDialog.Builder(act)
									.setTitle("设置重复周期")
									.setSingleChoiceItems(
											items,
											subject.getRemindFrequency() - 1, // get
																				// from
																				// data
											new DialogInterface.OnClickListener() {
												public void onClick(
														DialogInterface dialog,
														int which) {

													// change data
													subject.setIsRemind(true);
													subject.setRemindFrequency(which + 1);
													subjectDa
															.set_remind_frequency(
																	subject.getId(),
																	which + 1);

													String next_remind_date = Util.getNextDate(
															subject.getRemindDate(),
															which + 1);

													subject.setNextRemindDate(next_remind_date);
													subjectDa.set_next_remind(
															subject.getId(),
															next_remind_date);

													dialog.dismiss();

													data_bindding();
												}
											}).setNegativeButton("取消", null)
									.show();

						}
					}, c.get(Calendar.YEAR), // 传入年份
					c.get(Calendar.MONTH), // 传入月份
					c.get(Calendar.DAY_OF_MONTH) // 传入天数
			);

			dialog.setTitle("设置提醒日期");
			dialog.setCancelable(true);
			dialog.show();
		}

		private void data_bindding() {
			txtSubjectBody.setText(subject.getBody());
			subject_created_date.setText(subject.getCreationDate());
			subject_last_update.setText(subject.getUpdateDate());

			subject_start_date.setText(subject.getPlanStartDate());
			subject_done_date.setText("");

			subject_remind_date.setText(subject.getNextRemindDate());

			if (subject.getRemindFrequency() > 0) {
				subject_remind_frequency.setText(act.getResources()
						.getStringArray(R.array.remind_frequency_items)[subject
						.getRemindFrequency() - 1]);
			}

			if (!subject.isTodo() && !subject.isRemind()) {
				layoutTodoRemind.setVisibility(View.VISIBLE);
				layoutTodo.setVisibility(View.GONE);
				layoutRemind.setVisibility(View.GONE);
			} else if (subject.isRemind()) {
				layoutTodoRemind.setVisibility(View.GONE);
				layoutTodo.setVisibility(View.GONE);
				layoutRemind.setVisibility(View.VISIBLE);
			} else {
				// is todo
				layoutTodoRemind.setVisibility(View.GONE);
				layoutTodo.setVisibility(View.VISIBLE);
				layoutRemind.setVisibility(View.GONE);

				if (subject.getStatus() == 2 || subject.getStatus() == 3) {
					layoutTodoDoneDate.setVisibility(View.VISIBLE);
				} else {
					layoutTodoDoneDate.setVisibility(View.GONE);
				}
			}
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
										content, subject.getId());

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

					Intent detailIntent = new Intent(act,
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
			txtEdit.setText(subject.getBody());
			txtEdit.setSelection(subject.getBody().length());
			txtEdit.requestFocus();

			// 自动开启软键盘
			imm = (InputMethodManager) act
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
				act.setTitle(newTxt);
				subject.setBody(newTxt);
				subjectDa.edit_content(subject.getId(), newTxt);

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
		private List<DateBean> dates;
		private ListView lvDefault;
		private TextView plan_stat_date;
		private PickDatesAdapter dtAdapter;

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.detail_todo, container,
					false);

			plan_stat_date = (TextView) rootView
					.findViewById(R.id.plan_stat_date);
			lvDefault = (ListView) rootView.findViewById(R.id.lvDefault);

			dates = new ArrayList<DateBean>();

			dates.add(new DateBean(Util.getDateStr(0), Util.getDateStr(0)
					+ "今天", true));
			dates.add(new DateBean(Util.getDateStr(1), Util.getDateStr(1)
					+ " 明天", false));
			dates.add(new DateBean(Util.getDateStr(2), Util.getDateStr(2)
					+ " 后天", false));
			dates.add(new DateBean(Util.getDateStr(10), Util.getDateStr(10)
					+ " 10天后", false));

			dtAdapter = new PickDatesAdapter(act);
			lvDefault.setAdapter(dtAdapter);

			lvDefault_setOnItemClickListener();

			return rootView;
		}

		private void lvDefault_setOnItemClickListener() {
			// 单击，查看明细
			lvDefault.setOnItemClickListener(new OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1,
						int arg2, long arg3) {

					DateBean item = dates.get(arg2);
					if (item.getV()) {
						return;
					}

					item.setV(true);
					for (DateBean d : dates) {
						if (d.getId() != item.getId()) {
							d.setV(false);
						}
					}
					plan_stat_date.setText(item.getK());
					dtAdapter.notifyDataSetChanged();

					// 设置option菜单项？
					// getFragmentManager().beginTransaction()
					// .replace(R.id.container, new
					// DetailReadFragment()).commit();

				}
			});

		}

		private class PickDatesAdapter extends BaseAdapter {
			private LayoutInflater inflater1;
			private int temp = -1;

			public PickDatesAdapter(Context ctx1) {
				this.inflater1 = LayoutInflater.from(ctx1);

			}

			@Override
			public int getCount() {
				// TODO Auto-generated method stub
				return dates.size();
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
				convertView = inflater1.inflate(
						R.layout.detail_item_todo_dates_item, null);
				DateBean item = dates.get(position);

				// ui
				TextView tv = (TextView) convertView.findViewById(R.id.tvK);
				RadioButton picked = (RadioButton) convertView
						.findViewById(R.id.radioDate);

				tv.setText(item.getK());
				picked.setChecked(item.getV());

				// if(currentSubject.getId() == subject.getId() ){
				// convertView.setBackgroundColor();
				// }

				return convertView;

			}

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
