package com.gaotianpu.ftodo;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class UserDa {
	public static SQLiteDatabase getDb(Context context) {
		SQLiteHelper dbHelper = new SQLiteHelper(context, "ftodo", null, 1);
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		return db;
	}

	public static UserBean load_current_user(Context context) {
		UserBean user = new UserBean();

		SQLiteDatabase db = getDb(context);
		Cursor cursor = db.query("users",
				new String[] { "user_id", "account", "access_token",
						"token_status", "current_active", "last_update" },
				"current_active=1", null, null, null, null);
		
		cursor.moveToFirst();
		if (!cursor.isAfterLast()) {
			user.setUserId(cursor.getLong(0));
			user.setEmail(cursor.getString(1));
			user.setAccessToken(cursor.getString(2));
			user.setTokenStatus(cursor.getInt(3));
		}
		
		db.close();

		return user;
	}

	public static void login(Context context, long user_id, String user_name,
			String access_token) {
		SQLiteDatabase db = getDb(context);
		db.beginTransaction(); // 手动设置开始事务
		try {
			ContentValues values = new ContentValues();
			values.put("user_id", user_id);
			values.put("account", user_name); //
			values.put("access_token", access_token);
			values.put("token_status", 1); // token_status
			values.put("current_active", 1); // current_active
			values.put("last_update", 0);
			db.replace("users", "user_id", values); 

			// update users set current_active=0 where user_id<>?
			ContentValues values1 = new ContentValues();
			values1.put("current_active", 0);
			db.update("users", values1, "user_id<>?",
					new String[] { String.valueOf(user_id) });
			
			
			 db.setTransactionSuccessful(); 
		} catch (Exception e) {
			Log.e("sqlite", e.toString());			
		} finally {
			 db.endTransaction(); //处理完成
			 db.close();
		}

	}

//	public static void insert(Context context, long user_id, String user_name,
//			String access_token) {
//		ContentValues values = new ContentValues();
//		values.put("user_id", user_id);
//		values.put("account", user_name); //
//		values.put("access_token", access_token);
//		values.put("token_status", 1); // token_status
//		values.put("current_active", 1); // current_active
//		values.put("last_update", 0);
//		// last_update?
//
//		//
//		SQLiteDatabase db = getDb(context);
//		db.replace("users", "user_id", values);
//
//		//
//		update_active(context, user_id);
//
//		// 应该用事务操作
//
//	}

//	public static void update_token(Context context, long user_id,
//			String access_token) {
//		ContentValues values = new ContentValues();
//		values.put("access_token", access_token);
//		values.put("token_status", 1); // token_status
//		values.put("current_active", 1); // current_active
//		values.put("last_update", 0);
//
//		SQLiteDatabase db = getDb(context);
//		db.update("users", values, "user_id=?",
//				new String[] { String.valueOf(user_id) });
//	}

//	private static void update_active(Context context, long user_id) {
//		// db.update("users",)
//		SQLiteDatabase db = getDb(context);
//
//		// update users set current_active=1 where user_id=?
//		ContentValues values = new ContentValues();
//		values.put("current_active", 1);
//		db.update("users", values, "pk_id=?",
//				new String[] { String.valueOf(user_id) });
//
//		// update users set current_active=0 where user_id<>?
//		ContentValues values1 = new ContentValues();
//		values1.put("current_active", 0);
//		db.update("users", values1, "pk_id<>?",
//				new String[] { String.valueOf(user_id) });
//	}

}
