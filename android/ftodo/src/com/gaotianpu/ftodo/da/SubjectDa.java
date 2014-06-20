package com.gaotianpu.ftodo.da;

import java.util.ArrayList;
import java.util.List;

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

	// public static SQLiteDatabase getDb(Context context) {
	// SQLiteHelper dbHelper = new SQLiteHelper(context, "ftodo", null, 1);
	// SQLiteDatabase db = dbHelper.getWritableDatabase();
	// return db;
	// }

	public long insert(long user_id, String content) {
		ContentValues values = new ContentValues();
		values.put("body", content);
		values.put("user_id", user_id);
		values.put("is_del", 0);
		values.put("is_sync", 0);
		values.put("remote_id", 0);
		values.put("last_sync", 0);

		// 每次都要构造SQLiteDatabase， 对性能影响有多大？
		db = dbHelper.getWritableDatabase();
		long subjectID = db.insert("subjects", "pk_id", values);
		db.close();

		return subjectID;
	}

	public long insert2(long user_id, long remote_id, String content,
			String creation_date, int last_update, int last_sync) {
		ContentValues values = new ContentValues();
		values.put("user_id", user_id);
		values.put("body", content);
		values.put("creation_date", creation_date); //
		values.put("last_update", last_update);
		values.put("last_sync", last_sync);
		values.put("is_del", 0);
		values.put("is_sync", 1);
		values.put("remote_id", remote_id);

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
			db.update("subjects", values, "remote_id=?",  new String[] { String.valueOf(remote_id) });
		} else {
			subjectID = db.insert("subjects", "pk_id", values);
		}
		db.close();

		return subjectID;
	}

	public void set_remoteId(long local_id, long remote_id, long user_id) {

		// update sqlite's remote_id

		ContentValues values = new ContentValues();
		values.put("remote_id", remote_id);
		values.put("user_id", user_id);
		values.put("is_sync", 1);
		values.put("last_sync", 1); //

		db = dbHelper.getWritableDatabase();

		db.update("subjects", values, "pk_id=?",
				new String[] { String.valueOf(local_id) });
		db.close();

	}
	
	public void set_todo(long local_id, boolean todo){
		ContentValues values = new ContentValues();
		values.put("is_todo", todo);

		db = dbHelper.getWritableDatabase();
		db.update("subjects", values, "pk_id=?",
				new String[] { String.valueOf(local_id) });
		db.close();
	}
	
	public void set_remind(long local_id, boolean remind){
		ContentValues values = new ContentValues();
		values.put("is_remind", remind);

		db = dbHelper.getWritableDatabase();
		db.update("subjects", values, "pk_id=?",
				new String[] { String.valueOf(local_id) });
		db.close();
	}

	public void edit_content(long local_id, String content) {

	}

	public SubjectBean load_by_localId(long user_id, long local_id) {
		SubjectBean subject = new SubjectBean();
		db = dbHelper.getWritableDatabase();
		try {
			Cursor cursor = db.query(
					"subjects",
					new String[] { "pk_id", "user_id", "body", "creation_date",
							"last_update", "remote_id","is_todo","is_remind" },
					"pk_id=? and (user_id=? or user_id=0) ",
					new String[] { String.valueOf(local_id),
							String.valueOf(user_id) }, null, null, null);
			cursor.moveToFirst();
			while (!cursor.isAfterLast()) {

				subject.setId(cursor.getLong(0));
				subject.setUserId(cursor.getLong(1));
				subject.setBody(cursor.getString(2));
				subject.setCreationDate(cursor.getString(3));
				subject.setUpdateDate(cursor.getString(4));
				subject.setRemoteId(cursor.getLong(5));
				subject.setIsTodo(cursor.getInt(6)==1 ? true : false);
				subject.setIsRemind(cursor.getInt(7)==1 ? true : false);
				break;
			}
		} catch (IllegalArgumentException e) {
			Log.e("SQLiteOp", e.toString());
		} finally {
			db.close();
		}

		return subject;
	}

	public List<SubjectBean> load_not_uploaded_subjects(long user_id,int list_sort) {
		List<SubjectBean> subjectList = new ArrayList<SubjectBean>();
		db = dbHelper.getWritableDatabase();
		try {
			String sqlwhere;
			if(list_sort==1){ //待办
				sqlwhere = "(user_id=? or user_id=0) and (is_sync=0 or remote_id=0) and is_todo=1";
			}else if(list_sort==2){ //提醒
				sqlwhere = "(user_id=? or user_id=0) and (is_sync=0 or remote_id=0) and is_remind=1";
			}else{
				sqlwhere = "(user_id=? or user_id=0) and (is_sync=0 or remote_id=0) " ;
			}
			
			Cursor cursor = db.query("subjects", new String[] { "pk_id",
					"user_id", "body", "creation_date", "last_update",
					"remote_id", "is_todo","is_remind"},
					sqlwhere,
					new String[] { String.valueOf(user_id) }, null, null, "pk_id desc");
			cursor.moveToFirst();
			while (!cursor.isAfterLast() && (cursor.getString(1) != null)) {
				SubjectBean subject = new SubjectBean();
				subject.setId(cursor.getLong(0));
				subject.setUserId(user_id); // cursor.getLong(1));
				subject.setBody(cursor.getString(2));
				subject.setCreationDate(cursor.getString(3));

				// Log.i("setCreationDate", String.valueOf(cursor.getInt(3))
				// +"," + cursor.getString(3) ) ;

				subject.setUpdateDate(cursor.getString(4));
				subject.setRemoteId(cursor.getLong(5));
				subject.setIsTodo(cursor.getInt(6)==1 ? true : false);
				subject.setIsRemind(cursor.getInt(7)==1 ? true : false);
				subjectList.add(subject);
				cursor.moveToNext();
			}
		} catch (IllegalArgumentException e) {
			Log.e("SQLiteOp", e.toString());
		} finally {
			db.close();
		}

		return subjectList;
	}

	public List<SubjectBean> load(long user_id, int list_sort, int offset, int size) {
		List<SubjectBean> subjectList = new ArrayList<SubjectBean>();
		db = dbHelper.getWritableDatabase();
		try {

			// int offset = (page - 1) * size;
			String sqlwhere;
			if(list_sort==1){ //待办
				sqlwhere = "user_id=? and remote_id<>0 and is_todo=1";
			}else if(list_sort==2){ //提醒
				sqlwhere = "user_id=? and remote_id<>0 and is_remind=1";
			}else{
				sqlwhere = "user_id=? and remote_id<>0" ;
			}

			Cursor cursor = db.query("subjects", new String[] { "pk_id",
					"user_id", "body", "creation_date", "last_update",
					"remote_id", "is_todo","is_remind" }, sqlwhere,
					new String[] { String.valueOf(user_id) }, null, null,
					"remote_id DESC,pk_id desc limit " + String.valueOf(size)
							+ " offset " + String.valueOf(offset));
			cursor.moveToFirst();
			while (!cursor.isAfterLast() && (cursor.getString(1) != null)) {
				SubjectBean subject = new SubjectBean();
				subject.setId(cursor.getLong(0));
				subject.setUserId(cursor.getLong(1));
				subject.setBody(cursor.getString(2));

				subject.setCreationDate(cursor.getString(3));
				subject.setUpdateDate(cursor.getString(4));
				subject.setRemoteId(cursor.getLong(5));
				subject.setIsTodo(cursor.getInt(6)==1 ? true : false);
				subject.setIsRemind(cursor.getInt(7)==1 ? true : false);
				subjectList.add(subject);
				cursor.moveToNext();
			}
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
}
