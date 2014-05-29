package com.gaotianpu.ftodo;

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
				+ "body  TEXT NOT NULL," + "creation_date  timestamp  NOT NULL,"
				+ "last_update  timestamp  NOT NULL,"
				+ "last_sync  timestamp  NOT NULL," + "is_del INTEGER NOT NULL,"
				+ "is_sync  INTEGER NOT NULL," + "remote_id  INTEGER NOT NULL)");
		db.execSQL("CREATE INDEX  ix_remote_id ON subjects (remote_id DESC)");
		
		//create user tables
		db.execSQL("CREATE TABLE users IF NOT EXISTS  users("
				+ "user_id  INTEGER NOT NULL,"
				+ "account  NVARCHAR(50),"
				+ "access_token  NVARCHAR(50),"
				+ "token_status  INTEGER,"
				+ "current_active  INTEGER,"
				+ "last_update  timestamp,"
				+ "PRIMARY KEY (user_id)"
				+ ");");  
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS subjects");
		
		db.execSQL("DROP TABLE IF EXISTS users");
		onCreate(db);

	}

}
