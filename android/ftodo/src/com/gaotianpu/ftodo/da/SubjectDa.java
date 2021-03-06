package com.gaotianpu.ftodo.da;

import java.util.ArrayList;
 
import java.util.List;

import com.gaotianpu.ftodo.bean.ReportBean;
import com.gaotianpu.ftodo.bean.SubjectBean;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class SubjectDa {
	private SQLiteHelper dbHelper;
	private SQLiteDatabase db;

	public SubjectDa(Context context) {
		dbHelper = new SQLiteHelper(context, "ftodo", null, 1);
	}

	public long insert(long user_id, String content, long parent_id) {
		ContentValues values = new ContentValues();
		values.put("body", content);
		values.put("user_id", user_id);
		values.put("parent_id", parent_id);
		values.put("is_del", 0);

		values.put("remote_id", 0);
		values.put("last_sync", 0);
		values.put("is_todo", 0);
		values.put("is_remind", 0); //
		values.put("local_version", 0);

		// 每次都要构造SQLiteDatabase， 对性能影响有多大？
		db = dbHelper.getWritableDatabase();
		long subjectID = db.insert("subjects", "pk_id", values);

		// 失败的尝试
		// sqlite全文索引，可以考虑加入事务机制
		// ContentValues ixValues = new ContentValues();
		// ixValues.put("local_id", subjectID);
		// ixValues.put("user_id", user_id);
		// ixValues.put("content", addblank(content));
		// db.insert("seachIX", "local_id", ixValues);

		db.close();

		return subjectID;
	}

	private String addblank(String content) {
		char[] array = content.toCharArray();
		int arraySize = array.length;
		StringBuffer buf = new StringBuffer();
		for (int i = 0; i < arraySize; i++) {
			buf.append(array[i]);
			buf.append(" ");
		}
		return buf.toString();
	}

	public long insert2(SubjectBean subject ) {
		ContentValues values = new ContentValues();
		values.put("user_id", subject.getUserId() );
		values.put("body", subject.getBody() );
		values.put("creation_date", subject.getCreationDate()); //
		values.put("last_update", subject.getUpdateDate());
		values.put("last_sync", 1);
		values.put("is_del", subject.getIsDel());
		
		long remote_id= subject.getRemoteId();
		values.put("remote_id", remote_id);
		values.put("local_version", subject.getLocalVersion());
		values.put("server_version", subject.getLocalVersion()); //?
		
		values.put("is_todo", subject.isTodo());
		values.put("is_remind", subject.isRemind()); //
		values.put("plan_start_date", subject.getPlanStartDate()); //
		
		//remind
		values.put("remind_datetime", subject.getRemindDate());
		values.put("remind_next", subject.getNextRemindDate());  
		values.put("remind_frequency", subject.getRemindFrequency());   
		
		values.put("closed_date", subject.getClosedDate());  
		
		values.put("task_status", subject.getStatus());  

		// 检查sqlite 是否有remote_id, 无
		db = dbHelper.getWritableDatabase();
		Cursor cursor = db.query("subjects", new String[] { "pk_id" },
				"remote_id=?", new String[] { String.valueOf(remote_id) },
				null, null, null);
		cursor.moveToFirst();

		long subjectID = 0;
		if (!cursor.isAfterLast()) {
			// has record, update
			subjectID = cursor.getLong(0);
			db.update("subjects", values, "remote_id=?",
					new String[] { String.valueOf(remote_id) });
		} else {
			subjectID = db.insert("subjects", "pk_id", values);
		}
		db.close();

		return subjectID;
	}
	
	public void set_uploading(long local_id,int status){
		ContentValues values = new ContentValues(); 
		values.put("is_uploading", status);  //0, 已结束， 1，正在上传
		
		db = dbHelper.getWritableDatabase();

		db.update("subjects", values, "pk_id=?",
				new String[] { String.valueOf(local_id) });
		
		Log.i("AsyncService","set_uploading");

	}

	public void set_remoteId(long local_id, long remote_id, long user_id,
			int serverVersion) {

		// update sqlite's remote_id

		ContentValues values = new ContentValues();
		values.put("remote_id", remote_id);
		values.put("user_id", user_id);
		values.put("server_version", serverVersion);

		values.put("last_sync", Util.getNowStr()  ); //

		db = dbHelper.getWritableDatabase();

		db.update("subjects", values, "pk_id=?",
				new String[] { String.valueOf(local_id) });

		db.close();

	}

	public void delete(long local_id) {
		ContentValues values = new ContentValues();
		values.put("is_del", 1);

		db = dbHelper.getWritableDatabase();
		db.update("subjects", values, "pk_id=?",
				new String[] { String.valueOf(local_id) });

		// 累加版本号
		update_version(db, local_id);

		db.close();
	}

	public void set_todo(long local_id, boolean todo) {
		ContentValues values = new ContentValues();
		values.put("is_todo", todo); 
//		if(todo){
//			values.put("plan_start_date", Util.getDateStr(0)); //默认为今天
//		}
		
		db = dbHelper.getWritableDatabase();
		db.update("subjects", values, "pk_id=?",
				new String[] { String.valueOf(local_id) });
		update_version(db, local_id);
		db.close();
	}
	
	public void set_todo_status(long local_id, int task_status) {
		ContentValues values = new ContentValues();
		values.put("is_todo", 1);
		values.put("is_remind", 0);
		
		values.put("task_status",  String.valueOf(task_status));
		values.put("closed_date", Util.getNowStr() );
		values.put("plan_start_date", Util.getDateStr(0)); 
		
		
		
		db = dbHelper.getWritableDatabase();
		db.update("subjects", values, "pk_id=?",
				new String[] { String.valueOf(local_id) });
		update_version(db, local_id);
		db.close();
	}
	
	public void set_todo_start_date(long local_id, String start_date) {
		ContentValues values = new ContentValues();
		values.put("is_todo", 1);
		values.put("plan_start_date", start_date);

		db = dbHelper.getWritableDatabase();
		db.update("subjects", values, "pk_id=?",
				new String[] { String.valueOf(local_id) });
		update_version(db, local_id);
		db.close();
	}

	private void update_version(SQLiteDatabase db, long local_id) {
		// 累加版本号
		db.execSQL("update subjects set local_version=local_version+1,last_update=datetime('now','localtime') where pk_id="  
				+ String.valueOf(local_id));
	}

	public void set_remind(long local_id, boolean remind) {
		ContentValues values = new ContentValues();
		values.put("is_remind", remind);

		db = dbHelper.getWritableDatabase();
		db.update("subjects", values, "pk_id=?",
				new String[] { String.valueOf(local_id) });
		update_version(db, local_id);
		db.close();
	}
	
	public void set_remind_date(long local_id,String remind_date){
		ContentValues values = new ContentValues();
		values.put("remind_datetime", remind_date);

		db = dbHelper.getWritableDatabase();
		db.update("subjects", values, "pk_id=?",
				new String[] { String.valueOf(local_id) });
		update_version(db, local_id);
		db.close();
	}
	
	public void set_remind_frequency(long local_id,int remind_frequency){
		ContentValues values = new ContentValues();
		values.put("remind_frequency", remind_frequency);

		db = dbHelper.getWritableDatabase();
		db.update("subjects", values, "pk_id=?",
				new String[] { String.valueOf(local_id) });
		update_version(db, local_id);
		db.close();
	}
	
	public void set_next_remind(long local_id,String next_remind_date){
		ContentValues values = new ContentValues();
		values.put("remind_next", next_remind_date);

		db = dbHelper.getWritableDatabase();
		db.update("subjects", values, "pk_id=?",
				new String[] { String.valueOf(local_id) });
		update_version(db, local_id);
		db.close();
	}

	public void edit_content(long local_id, String content) {
		ContentValues values = new ContentValues();
		values.put("body", content);

		db = dbHelper.getWritableDatabase();
		db.update("subjects", values, "pk_id=?",
				new String[] { String.valueOf(local_id) });

		update_version(db, local_id);

		db.close();

	}

	public SubjectBean load_by_localId(long user_id, long local_id) {
		SubjectBean subject = new SubjectBean();
		db = dbHelper.getWritableDatabase();
		try {
			Cursor cursor = db.query(
					"subjects",
					list_selected_fields,
					"pk_id=? and (user_id=? or user_id=0) ",
					new String[] { String.valueOf(local_id),
							String.valueOf(user_id) }, null, null, null);
			List<SubjectBean> list = load_list(cursor);
			if (list.size() > 0) {
				subject = list.get(0);
			}
		} catch (IllegalArgumentException e) {
			Log.e("SQLiteOp", e.toString());
		} finally {
			db.close();
		}

		return subject;
	}
	
	public SubjectBean load_by_remoteId(long user_id, long remote_id) {
		SubjectBean subject = new SubjectBean();
		db = dbHelper.getWritableDatabase();
		try {
			Cursor cursor = db.query(
					"subjects",
					list_selected_fields,
					"remote_id=? and (user_id=? or user_id=0) ",
					new String[] { String.valueOf(remote_id),
							String.valueOf(user_id) }, null, null, null);
			List<SubjectBean> list = load_list(cursor);
			if (list.size() > 0) {
				subject = list.get(0);
			}
		} catch (IllegalArgumentException e) {
			Log.e("SQLiteOp", e.toString());
		} finally {
			db.close();
		}

		return subject;
	}
	
	 
	

	// 失败的尝试，
	public List<SubjectBean> search(long user_id, String query) {
		List<SubjectBean> subjectList = new ArrayList<SubjectBean>();

		db = dbHelper.getWritableDatabase();
		Cursor cursor = db.query("seachIX", new String[] { "local_id",
				"content" }, "user_id=? and content match ?", new String[] {
				String.valueOf(user_id), this.addblank(query) }, null, null,
				null);

		cursor.moveToFirst();
		while (!cursor.isAfterLast()) { // && (cursor.getString(1) != null
			SubjectBean subject = new SubjectBean();
			subject.setId(cursor.getLong(0));
			subject.setBody(cursor.getString(1));

			subjectList.add(subject);
			cursor.moveToNext();
		}

		db.close();

		return subjectList;
	}
	
	private final String[] list_selected_fields = new String[] { "pk_id",
			"user_id", "body", "creation_date", "last_update", "remote_id",
			"is_todo", "is_remind", "parent_id", "local_version", "is_del","date(plan_start_date) as plan_start_date","task_status",
			"date(remind_datetime) as remind_datetime","date(remind_next) as  remind_next","remind_frequency","closed_date","server_version"};

	private List<SubjectBean> load_list(Cursor cursor) {
		List<SubjectBean> subjectList = new ArrayList<SubjectBean>();
		cursor.moveToFirst();
		while (!cursor.isAfterLast()) { // && (cursor.getString(1) != null)
			SubjectBean subject = new SubjectBean();
			subject.setId(cursor.getLong(0));
			subject.setUserId(cursor.getLong(1));
			subject.setBody(cursor.getString(2));
			subject.setCreationDate(cursor.getString(3));
			subject.setUpdateDate(cursor.getString(4));
			subject.setRemoteId(cursor.getLong(5));
			subject.setIsTodo(cursor.getInt(6) == 1 ? true : false);
			subject.setIsRemind(cursor.getInt(7) == 1 ? true : false);
			subject.setParentId(cursor.getLong(8));
			subject.setLocalVersion(cursor.getInt(9));
			subject.setIsDel(cursor.getInt(10));			
			subject.setPlanStartDate(cursor.getString( 11));
			subject.setStatus(cursor.getInt( 12 ));
			
			//提醒相关
			subject.setRemindDate(cursor.getString( 13));
			subject.setNextRemindDate(cursor.getString( 14));
			subject.setRemindFrequency(cursor.getInt( 15 ));
			
			subject.setClosedDate(cursor.getString( 16 ));  
			subject.setServerVersion(cursor.getInt(17));
			

			subjectList.add(subject);
			cursor.moveToNext();
		}

		return subjectList;
	}

	public List<SubjectBean> load_changed_but_not_uploaded(long user_id) {
		List<SubjectBean> subjectList = new ArrayList<SubjectBean>();
		db = dbHelper.getWritableDatabase();
		try {
			String sqlwhere = "(user_id=? or user_id=0) and (local_version>server_version or remote_id=0) ";
			Cursor cursor = db.query("subjects", list_selected_fields,
					sqlwhere, new String[] { String.valueOf(user_id) }, null,
					null, "pk_id desc limit 0,1");

			subjectList = load_list(cursor);
		} catch (IllegalArgumentException e) {
			Log.e("SQLiteOp", e.toString());
		} finally {
			db.close();
		}

		return subjectList;
	}

	public List<SubjectBean> load_son_subjects(long user_id, long parent_id) {
		List<SubjectBean> subjectList = new ArrayList<SubjectBean>();
		db = dbHelper.getWritableDatabase();
		try {

			Cursor cursor = db.query(
					"subjects",
					list_selected_fields,
					"(user_id=? or user_id=0) and parent_id=? and is_del=0 ",
					new String[] { String.valueOf(user_id),
							String.valueOf(parent_id) }, null, null,
					"pk_id desc");

			subjectList = load_list(cursor);

		} catch (IllegalArgumentException e) {
			Log.e("SQLiteOp", e.toString());
		} finally {
			db.close();
		}

		return subjectList;
	}
	
	public List<SubjectBean> load_expired_reminds(long user_id) {
		List<SubjectBean> subjectList = new ArrayList<SubjectBean>();
		db = dbHelper.getWritableDatabase();
		try {

			Cursor cursor = db.query(
					"subjects",
					list_selected_fields,
					"(user_id=? or user_id=0) and is_del=0 and is_remind=1 and remind_datetime is not null and remind_next < current_date", 
					new String[] { String.valueOf(user_id) }, null, null,
					"pk_id desc");

			subjectList = load_list(cursor);

		} catch (IllegalArgumentException e) {
			Log.e("SQLiteOp", e.toString());
		} finally {
			db.close();
		}

		return subjectList;
	}

	public List<SubjectBean> load_not_uploaded_subjects(long user_id,
			String list_sort) {
		List<SubjectBean> subjectList = new ArrayList<SubjectBean>();
		db = dbHelper.getWritableDatabase();
		try {
			String sqlwhere = "remote_id=0 and is_del=0 ";
			String orderBy = "pk_id desc";
			
			if (list_sort.equals("todo")) { // 待办
				sqlwhere = sqlwhere + " and is_todo=1 and task_status<>2 and task_status<>3 ";
			}  else if (list_sort.equals("done")) { //完成
				sqlwhere = sqlwhere + " and is_todo=1 and task_status=2 ";
			} else if (list_sort.equals("block")) { //暂停
				sqlwhere = sqlwhere +  " and is_todo=1 and task_status=3 ";
			} else if (list_sort.equals("remind")) { // 提醒
				sqlwhere = sqlwhere + " and is_remind=1";
				orderBy = "remind_next asc";
			}else { //all
				//sqlwhere = " ";
			}
			
			sqlwhere = sqlwhere + " and (user_id=? or user_id=0) ";

			Log.i("sqlwhere",sqlwhere); 
			
			Cursor cursor = db.query("subjects", list_selected_fields,
					sqlwhere, new String[] { String.valueOf(user_id) }, null,
					null, orderBy);

			subjectList = load_list(cursor);

		} catch (IllegalArgumentException e) {
			Log.e("SQLiteOp", e.toString());
		} finally {
			db.close();
		}

		return subjectList;
	}

	public List<SubjectBean> load(long user_id, String list_sort, int offset,
			int size) {
		List<SubjectBean> subjectList = new ArrayList<SubjectBean>();
		db = dbHelper.getWritableDatabase();
		try {
			String sqlwhere = "remote_id<>0 and is_del=0";
			String orderBy = "remote_id DESC,pk_id desc";
			
			if (list_sort.equals( "todo")) { // 待办
				sqlwhere = sqlwhere + "and is_todo=1 and is_remind=0 and task_status<>2 and task_status<>3 ";
			}  else if (list_sort.equals( "done")) {
				sqlwhere = sqlwhere + " and is_todo=1  and task_status=2 and is_remind=0 ";
			} else if (list_sort.equals("block")) {
				sqlwhere = sqlwhere + " and is_todo=1  and task_status=3 and is_remind=0  ";
			} else if (list_sort.equals("remind")) { // 提醒
				sqlwhere = sqlwhere + " and is_remind=1 ";
				orderBy = "remind_next asc";
			}else { //all
				 //
			}

			sqlwhere = sqlwhere + " and (user_id=? or user_id=0) ";

			 

			Cursor cursor = db.query("subjects", list_selected_fields,
					sqlwhere, new String[] { String.valueOf(user_id) }, null,
					null,
					orderBy + " limit " + String.valueOf(size)
							+ " offset " + String.valueOf(offset));

			subjectList = load_list(cursor);

		} catch (IllegalArgumentException e) {
			Log.e("SQLiteOp", e.toString());
		} finally {
			db.close();
		}

		return subjectList;
	}
	
	public List<SubjectBean> load_todo(long user_id, int offset, int size) {
		List<SubjectBean> subjectList = new ArrayList<SubjectBean>();
		db = dbHelper.getWritableDatabase();
		try { 

			String sqlwhere =  "(user_id=? or user_id=0) and is_todo=1 and is_remind=0 and is_del=0 and task_status<>2 and task_status<>3";

			Log.i("sqlwhere", sqlwhere);

			Cursor cursor = db.query("subjects", list_selected_fields,
					sqlwhere, new String[] { String.valueOf(user_id) }, null,
					null,
					"plan_start_date limit " + String.valueOf(size)
							+ " offset " + String.valueOf(offset));

			subjectList = load_list(cursor);

		} catch (IllegalArgumentException e) {
			Log.e("SQLiteOp", e.toString());
		} finally {
			db.close();
		}

		return subjectList;
	}

	public void load_ids(long min_remote_id, long max_remote_id) {
		List<SubjectBean> subjectList = new ArrayList<SubjectBean>();
		db = dbHelper.getWritableDatabase();
		try {

			Cursor cursor = db.query(
					"subjects",
					new String[] { "pk_id", "remote_id" },
					"remote_id>=? and remote_id<=?",
					new String[] { String.valueOf(min_remote_id),
							String.valueOf(max_remote_id) }, null, null, null);
			cursor.moveToFirst();
			while (!cursor.isAfterLast() && (cursor.getString(1) != null)) {
				SubjectBean subject = new SubjectBean();
				subject.setId(cursor.getLong(0));
				subject.setRemoteId(cursor.getLong(1));
				subjectList.add(subject);
				cursor.moveToNext();
			}
		} catch (IllegalArgumentException e) {
			Log.e("SQLiteOp", e.toString());
		} finally {
			db.close();
		}

		// return subjectList ?;
	}

	public long get_max_remote_id(long user_id) {
		long remote_id = 0;
		db = dbHelper.getWritableDatabase();
		Cursor cursor = db.query("subjects",
				new String[] { "max(remote_id) as max_remote_id" },
				"user_id=?", new String[] { String.valueOf(user_id) }, null,
				null, null);
		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			remote_id = cursor.getLong(0);
			break;
		}
		db.close();

		return remote_id;
	}

	public long get_local_max_offset(long user_id) {
		long max_offset = 0;
		db = dbHelper.getWritableDatabase();
		Cursor cursor = db.query("download_records",
				new String[] { "max(offset) as max_offset" }, "user_id=?",
				new String[] { String.valueOf(user_id) }, null, null, null);
		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			max_offset = cursor.getLong(0);
			break;
		}

		// finally{
		db.close();
		// }

		return max_offset;
	}

	public void save_download_records(long user_id, long total) {

		int page_size = 100;

		// cloud
		long cloud_page_count = total / page_size;

		// local
		long local_max_offset = get_local_max_offset(user_id);
		long local_page_count = local_max_offset / page_size; // local_max_offset/page_size

		db = dbHelper.getWritableDatabase();
		db.beginTransaction(); // 手动设置开始事务
		try {
			for (long i = local_page_count; i <= cloud_page_count; i++) {
				long offset = i * page_size;

				ContentValues values = new ContentValues();
				values.put("user_id", user_id);
				values.put("offset", offset);
				db.replace("download_records", null, values);

			}
			db.setTransactionSuccessful();
		} catch (Exception e) {
			Log.e("sqlite", e.toString());

		} finally {
			db.endTransaction(); // 处理完成

			db.close();

		}
	}
	
	

	public List<Long> load_not_download(long user_id) {
		// select user_id,offset,has_download download_record where user_id=?
		// and has_download=0
		db = dbHelper.getWritableDatabase();
		Cursor cursor = db.query("download_records", new String[] { "user_id",
				"offset", "has_download" }, "user_id=? and has_download=0", //
				new String[] { String.valueOf(user_id) }, null, null, "offset");
		cursor.moveToFirst();

		List<Long> offset_list = new ArrayList<Long>();
		// Log.i("dowload_records","------------");
		while (!cursor.isAfterLast()) {
			long offset = cursor.getLong(1);
			// Log.i("dowload_records", String.valueOf(offset));
			offset_list.add(offset);
			cursor.moveToNext();
		}
		// finally{
		db.close();
		// }
		return offset_list;

	}

	public void update_download_records(long user_id, long offset) {
		// update download_records set has_download=0 where user_id=? and
		// offset=?
		db = dbHelper.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put("has_download", 1);

		db.update(
				"download_records",
				values,
				"user_id=? and offset=?",
				new String[] { String.valueOf(user_id), String.valueOf(offset) });

		// finally{
		db.close();
		// }

	}

	public List<ReportBean> load_days_count(long user_id) {

		db = dbHelper.getWritableDatabase();
		Cursor cursor = db
				.query("subjects",
						new String[] { "date(creation_date) as day,count(*) as count" },
						"user_id=?", new String[] { String.valueOf(user_id) },
						"date(creation_date)", null, "creation_date desc");

		List<ReportBean> list = new ArrayList<ReportBean>();
		
		int sum_count=0;
		int max_count=0;
		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			int count =  cursor.getInt(1);
			if(count>max_count){
				max_count =  count;
			}
			sum_count = sum_count + count;
			list.add(new ReportBean(cursor.getString(0), count));
			cursor.moveToNext();
		}
		db.close();
		
		list.add(0, new ReportBean("最多每天", max_count)); //cn
		list.add(0, new ReportBean("总计", sum_count));

		return list;
	}
}
