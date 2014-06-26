package com.gaotianpu.ftodo.da;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class SQLiteHelper extends SQLiteOpenHelper {

	public SQLiteHelper(Context context, String name, CursorFactory factory,
			int version) {
		super(context, name, factory, version);
	}

	@Override
	public void onCreate(SQLiteDatabase db) { 
		db.execSQL("CREATE TABLE IF NOT EXISTS subjects ("				
				+ "pk_id  INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,"
				+ "user_id  INTEGER NOT NULL,"
				+ "body  TEXT NOT NULL," 
				+ "creation_date  datetime default CURRENT_TIMESTAMP,"
				+ "last_update  datetime default CURRENT_TIMESTAMP,"
				+ "last_sync  datetime  ," 
				+ "is_del INTEGER NOT NULL default 0,"	
				+ "parent_id INTEGER NOT NULL default 0,"
				+ "is_todo INTEGER NOT NULL default 0,"
				+ "is_remind INTEGER NOT NULL default 0 ,"
				+ "remind_datetime datetime ,"
				+ "remind_frequency INTEGER NOT NULL default 0,"
				+ "remind_next datetime ,"
				+ "local_version INTEGER NOT NULL default 0,"	
				+ "server_version INTEGER NOT NULL default 0,"	
				+ "remote_id  INTEGER NOT NULL default 0)");
		
		db.execSQL("CREATE INDEX  ix_remote_id ON subjects (remote_id DESC)");
		
		//create user tables
		db.execSQL("CREATE TABLE IF NOT EXISTS  users("
				+ "user_id  INTEGER NOT NULL,"
				+ "mobile  NVARCHAR(50) NOT NULL,"
				+ "email  NVARCHAR(50) NOT NULL default '',"
				+ "password_level  NVARCHAR(50),"
				+ "access_token  NVARCHAR(50),"
				+ "token_status  INTEGER,"
				+ "current_active  INTEGER,"
				+ "last_update datetime default CURRENT_TIMESTAMP,"
				+ "mobile_validate  INTEGER NOT NULL default 0,"
				+ "email_validate  INTEGER NOT NULL default 0,"
				+ "PRIMARY KEY (user_id)"
				+ ");");  
		
		db.execSQL("CREATE TABLE download_records ("
				+ "user_id  INTEGER NOT NULL,"
				+ "offset  INTEGER NOT NULL,"
				+ "has_download  INTEGER NOT NULL DEFAULT 0 "
				+ ");");
		db.execSQL("CREATE UNIQUE INDEX uniq_ix ON download_records (user_id, offset);");
		
		//全文检索
		db.execSQL("create virtual table seachIX using fts3(local_id INTEGER PRIMARY KEY, user_id INTEGER, content text); ");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS subjects");
		
		db.execSQL("DROP TABLE IF EXISTS users");
		onCreate(db);

	}

}
