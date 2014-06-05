package com.gaotianpu.ftodo;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class SubjectDa {

	public static SQLiteDatabase getDb(Context context) {
		SQLiteHelper dbHelper = new SQLiteHelper(context, "ftodo", null, 1);
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		return db;
	}

	public static long insert(Context context, long user_id, String content) {
		ContentValues values = new ContentValues();
		values.put("body", content);
		values.put("user_id", user_id);
		values.put("creation_date", 1); //
		values.put("last_update", 0);
		values.put("last_sync", 0);
		values.put("is_del", 0);
		values.put("is_sync", 0);
		values.put("remote_id", 0);

		// 每次都要构造SQLiteDatabase， 对性能影响有多大？
		SQLiteDatabase db = getDb(context);
		long subjectID = db.insert("subjects", "pk_id", values);

		return subjectID;
	}

	public static long insert2(Context context, long user_id,  long remote_id, String content,
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

		//
		SQLiteDatabase db = getDb(context);

		// 检查sqlite 是否有remote_id, 无
		Cursor cursor = db.query("subjects", new String[] { "pk_id" },
				"remote_id=?", new String[] { String.valueOf(remote_id) },
				null, null, null);
		cursor.moveToFirst();

		long subjectID = 0;
		if (!cursor.isAfterLast()) {
			// has record, update
			subjectID = cursor.getLong(0);
		} else {
			subjectID = db.insert("subjects", "pk_id", values);
		}

		return subjectID;
	}

	public static void set_remoteId(Context context, long local_id,
			long remote_id) {

		// update sqlite's remote_id

		ContentValues values = new ContentValues();
		values.put("remote_id", remote_id);
		values.put("is_sync", 1);
		values.put("last_sync", 1); //

		SQLiteDatabase db = getDb(context);
		db.update("subjects", values, "pk_id=?",
				new String[] { String.valueOf(local_id) });

	}

	public static void edit_content(long local_id, String content) {

	}

	public static List<SubjectBean> load_not_uploaded_subjects(Context context,
			long user_id) {
		List<SubjectBean> subjectList = new ArrayList<SubjectBean>();
		try {

			SQLiteDatabase db = getDb(context);
			Cursor cursor = db.query("subjects", new String[] { "pk_id",
					"user_id", "body", "creation_date" },
					"user_id=? and is_sync=0 ",
					new String[] { String.valueOf(user_id) }, null, null, null);
			cursor.moveToFirst();
			while (!cursor.isAfterLast() && (cursor.getString(1) != null)) {
				SubjectBean subject = new SubjectBean();
				subject.setId(cursor.getLong(0));
				subject.setUserId(cursor.getLong(1));
				subject.setBody(cursor.getString(2));
				subject.setCreationDate(cursor.getInt(3));
				subjectList.add(subject);
				cursor.moveToNext();
			}
		} catch (IllegalArgumentException e) {
			Log.e("SQLiteOp", e.toString());
		}

		return subjectList;
	}

	public static List<SubjectBean> load(Context context, long user_id,
			int page, int size) {
		List<SubjectBean> subjectList = new ArrayList<SubjectBean>();
		try {

			int offset = (page - 1) * size;

			SQLiteDatabase db = getDb(context);
			Cursor cursor = db.query("subjects", new String[] { "pk_id",
					"user_id", "body", "creation_date" }, "user_id=?",
					new String[] { String.valueOf(user_id) }, null, null,
					"remote_id DESC,pk_id desc limit " + String.valueOf(size)
							+ " offset " + String.valueOf(offset));
			cursor.moveToFirst();
			while (!cursor.isAfterLast() && (cursor.getString(1) != null)) {
				SubjectBean subject = new SubjectBean();
				subject.setId(cursor.getLong(0));
				subject.setUserId(cursor.getLong(1));
				subject.setBody(cursor.getString(2));
				subject.setCreationDate(cursor.getInt(3));
				subjectList.add(subject);
				cursor.moveToNext();
			}
		} catch (IllegalArgumentException e) {
			Log.e("SQLiteOp", e.toString());
		}

		return subjectList;
	}

	public static void load_ids(Context context, long min_remote_id,
			long max_remote_id) {
		List<SubjectBean> subjectList = new ArrayList<SubjectBean>();
		try {

			SQLiteDatabase db = getDb(context);
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
		}

		// return subjectList ?;
	}

	public static long get_max_remote_id(Context context, long cust_id) {
		long remote_id = 0;

		SQLiteDatabase db = getDb(context);

		Cursor cursor = db.query("subjects",
				new String[] { "max(remote_id) as max_remote_id" }, null, null,
				null, null, null);
		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			remote_id = cursor.getLong(0);
			break;
		}

		return remote_id;
	}
	
	
	public static void save_download_records(Context context, long user_id, long total){
		int size = 100;
		long count = total/size;
		for(long i=0;i<count;i++){
			long offset = i*size;
		}
		
		ContentValues values = new ContentValues();
		values.put("user_id", user_id);
		values.put("offset", value)
		
		
		SQLiteDatabase db = getDb(context);
		
		db.replace("", null, values)
	}
}
