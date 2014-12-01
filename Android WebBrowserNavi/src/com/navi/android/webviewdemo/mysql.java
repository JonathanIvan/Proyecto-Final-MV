package com.navi.android.webviewdemo;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class mysql extends SQLiteOpenHelper{

	public mysql(Context context, String name, CursorFactory factory,
			int version) {
		super(context, "ProyectoMV", factory, version);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
	db.execSQL("CREATE TABLE  Favoritos (nombre text , url text);");
	db.execSQL("insert into Favoritos values('google','https://www.google.com.mx')");
	db.execSQL("insert into Favoritos values('youtube','https://www.youtube.com.mx')");
	db.execSQL("insert into Favoritos values('debian','https://www.debian.org')");
	}
	
	
	
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		
	}
		
		
		
}
