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

	public static long insert(Context context, String content) {
		ContentValues values = new ContentValues();
		values.put("body", content);
		values.put("creation_date", 1); //
		values.put("last_update", 0);
		values.put("last_sync", 0);
		values.put("is_del", 0);
		values.put("is_sync", 0);
		values.put("remote_id", 0);

		// 插入数据 用ContentValues对象也即HashMap操作,并返回ID号
		SQLiteDatabase db = getDb(context);
		long subjectID = db.insert("subjects", "pk_id", values);

		return subjectID;
	}

	public static void set_remoteId(Context context, long local_id,
			long remote_id) {

		// update sqlite's remote_id

		ContentValues values = new ContentValues();
		values.put("remote_id", remote_id);
		values.put("is_sync", 1);
		values.put("last_sync", 1); // 上次同步日期

		SQLiteDatabase db = getDb(context);
		db.update("subjects", values, "pk_id=?",
				new String[] { String.valueOf(local_id) });

	}

	public static void edit_content(long local_id, String content) {

	}

	public static List<SubjectBean> load_not_uploaded_subjects(Context context) {
		List<SubjectBean> subjectList = new ArrayList<SubjectBean>();
		try {
			/* 查询表，得到cursor对象 */
			SQLiteDatabase db = getDb(context);
			Cursor cursor = db.query("subjects", null, "is_sync=0", null, null,
					null, null);
			cursor.moveToFirst();
			while (!cursor.isAfterLast() && (cursor.getString(1) != null)) {
				SubjectBean subject = new SubjectBean();
				subject.setId(cursor.getLong(0));
				subject.setBody(cursor.getString(1));
				subject.setCreationDate(cursor.getInt(2));
				subjectList.add(subject);
				cursor.moveToNext();
			}
		} catch (IllegalArgumentException e) {
			Log.e("SQLiteOp", e.toString());
		}

		return subjectList;
	}

	public static List<SubjectBean> load(Context context, long cust_id,
			int page, int size) {
		List<SubjectBean> subjectList = new ArrayList<SubjectBean>();
		try {

			/* 查询表，得到cursor对象 */
			SQLiteDatabase db = getDb(context);
			Cursor cursor = db.query("subjects", null, null, null, null, null,
					"remote_id DESC,pk_id desc");
			cursor.moveToFirst();
			while (!cursor.isAfterLast() && (cursor.getString(1) != null)) {
				SubjectBean subject = new SubjectBean();
				subject.setId(cursor.getLong(0));
				subject.setBody(cursor.getString(1));
				subject.setCreationDate(cursor.getInt(2));
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
			/* 查询表，得到cursor对象 */
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

		// return subjectList;
	}
}
