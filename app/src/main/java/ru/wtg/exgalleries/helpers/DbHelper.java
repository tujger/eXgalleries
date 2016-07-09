package ru.wtg.exgalleries.helpers;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DbHelper extends SQLiteOpenHelper {

	private static final String DB_NAME = "exgalleries.db";
	private static final int DB_VERSION = 1;

	private static DbHelper mInstance = null;
	private static SQLiteDatabase database = null;

	public static DbHelper getInstance(Context context) {
		if (mInstance == null) {
			synchronized (context) {
				if (mInstance == null) {
					mInstance = new DbHelper(context.getApplicationContext());
				}
			}
		}
		return mInstance;
	}

	public static SQLiteDatabase getDatabase(Context context) {
		mInstance = getInstance(context);
		try {
			database = mInstance.getWritableDatabase();
		} catch (SQLException e) {
			// /Если база не открылась, то дальше нам дороги нет
			// но это особый случай
			Log.e(context.toString(), "Error while getting database");
			throw new Error("The end");
		}
		return database;
	}

	/**
	 * Constructor should be private to prevent direct instantiation. make call
	 * to static factory method "getInstance()" instead.
	 */
	private DbHelper(Context context) {
		super(context, DB_NAME, null, DB_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// request for create libraries db

		// request for create galleries db
		final String CREATE_PICTURE = "CREATE TABLE " + ItemAdapter.TABLE_NAME + " (" + ItemAdapter.KEY_ID
				+ " INTEGER PRIMARY KEY AUTOINCREMENT, " + ItemAdapter.KEY_PARENT_ID + " INT, " + ItemAdapter.KEY_NAME
				+ " TEXT, " + ItemAdapter.KEY_TITLE + " TEXT, " + ItemAdapter.KEY_LINK + " TEXT NOT NULL, "
				+ ItemAdapter.KEY_IMAGE_LINK + " TEXT, " + ItemAdapter.KEY_AUTHOR + " TEXT, "
				+ ItemAdapter.KEY_DESCRIPTION + " TEXT, " + ItemAdapter.KEY_CREATE_DATE + " INT NOT NULL, "
				+ ItemAdapter.KEY_UPDATE_DATE + " INT, " + ItemAdapter.KEY_ACCESS_DATE + " INT, "
				+ ItemAdapter.KEY_VISIBLE + " INT, " + ItemAdapter.KEY_UPDATABLE + " INT, " + ItemAdapter.KEY_ALBUM
				+ " INT, " + ItemAdapter.KEY_FAILED + " INT, " + ItemAdapter.KEY_RATING + " INT " + ");";

		db.execSQL(CREATE_PICTURE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// Тут можно организовать миграцию данных из старой базы в новую
		// или просто "выбросить" таблицу и создать заново
		// db.execSQL(ALTER TABLE table_name ADD column_name datatype);
		// db.execSQL("DROP TABLE IF EXISTS " + LibraryAdapter.TABLE_NAME);
		// db.execSQL("DROP TABLE IF EXISTS " + GalleryAdapter.TABLE_NAME);
		db.execSQL("DROP TABLE IF EXISTS " + ItemAdapter.TABLE_NAME);
		onCreate(db);
	}
}