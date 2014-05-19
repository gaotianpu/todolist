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
//		db.execSQL("CREATE TABLE IF NOT EXISTS subjects ("
//				+ "pk_id  INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,"
//				+ "body  TEXT NOT NULL," + "creation_date  INTEGER NOT NULL)"); 
		
		db.execSQL("CREATE TABLE IF NOT EXISTS subjects ("
				+ "pk_id  INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,"
				+ "body  TEXT NOT NULL," + "creation_date  INTEGER NOT NULL,"
				+ "last_update  INTEGER NOT NULL,"
				+ "last_sync  INTEGER NOT NULL," + "is_del INTEGER NOT NULL,"
				+ "is_sync  INTEGER NOT NULL," + "remote_id  INTEGER NOT NULL)");

		db.execSQL("CREATE INDEX  ix_remote_id ON subjects (remote_id DESC)");

	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS subjects");
		onCreate(db);

	}

}
