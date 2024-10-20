package com.edeqa.exgalleries.helpers;

import java.util.ArrayList;
import java.util.Date;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.CursorIndexOutOfBoundsException;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Point;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;

abstract public class EntityAdapter<T> extends BaseAdapter {

	final public static int COL_ID = 0;
	final public static int COL_PARENT_ID = 1;
	final public static int COL_NAME = 2;
	final public static int COL_TITLE = 3;
	final public static int COL_LINK = 4;
	final public static int COL_IMAGE_LINK = 5;
	final public static int COL_AUTHOR = 6;
	final public static int COL_DESCRIPTION = 7;
	final public static int COL_CREATE_DATE = 8;
	final public static int COL_UPDATE_DATE = 9;
	final public static int COL_ACCESS_DATE = 10;
	final public static int COL_VISIBLE = 11;
	final public static int COL_UPDATABLE = 12;
	final public static int COL_ALBUM = 13;
	final public static int COL_FAILED = 14;
	final public static int COL_RATING = 15;

	final public static String KEY_ID = "_id";
	final public static String KEY_PARENT_ID = "parent_id";
	final public static String KEY_NAME = "name";
	final public static String KEY_TITLE = "title";
	final public static String KEY_LINK = "link";
	final public static String KEY_IMAGE_LINK = "image_link";
	final public static String KEY_AUTHOR = "author";
	final public static String KEY_DESCRIPTION = "description";
	final public static String KEY_CREATE_DATE = "created";
	final public static String KEY_UPDATE_DATE = "updated";
	final public static String KEY_ACCESS_DATE = "accessed";
	final public static String KEY_VISIBLE = "visible";
	final public static String KEY_UPDATABLE = "updatable";
	final public static String KEY_ALBUM = "album";
	final public static String KEY_FAILED = "failed";
	final public static String KEY_RATING = "rating";

	protected Cursor cursor;
	protected SQLiteDatabase database;
	protected Context context;
	protected int mView;
	protected long mActiveParent;
	protected int position;
	private SelectionTerms terms;
	private static int screenWidth;

	public EntityAdapter(Context context) {
		super();
		this.context = context;

		// dbHelper = DbHelper.getInstance(context);
		// try {
		// database = dbHelper.getWritableDatabase();
		// } catch (SQLException e) {
		// // /Если база не открылась, то дальше нам дороги нет
		// // но это особый случай
		// Log.e(this.toString(), "Error while getting database");
		// throw new Error("The end");
		// }
		database = DbHelper.getDatabase(context);
		terms = new SelectionLibraries();
		cursor = getEntries();

		Point size = new Point();
		((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getSize(size);
		screenWidth = size.x;

	}

	public EntityAdapter(Context context, int view) {
		this(context);
		setView(view);
	}

	@Override
	public int getCount() {
		return cursor.getCount();
	}

	public Entity<?> getItem(long id) {
		Cursor cursor = database.query(getTableName(), null, KEY_ID + " = ?", new String[] { id + "" }, null, null,
				KEY_ID);
		if (!cursor.moveToFirst())
			return null;
		Entity<?> item = getItem(cursor);
		cursor.close();
		return item;

		// return getItem(cursor, id);
	}

	public Entity<?> getItem(Cursor cursor, long id) {
		int position = 0;
		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			if (cursor.getInt(COL_ID) == id) {
				this.position = position;
				return getItem(cursor, position);
			}
			position++;
			cursor.moveToNext();
		}
		return null;
	}

	public Entity<?> getItem() {
		return getItem(cursor);
	}

	public Entity<?> getItem(Cursor cursor) {
		return getItem(getEntity(), cursor);
	}

	public Entity<?> getItem(int position) {
		return getItem(cursor, position);
	}

	public Entity<?> getItem(Cursor cursor, int position) {
		if (cursor.moveToPosition(position)) {
			this.position = position;
			return getItem(getEntity(), cursor);
		} else {
			throw new CursorIndexOutOfBoundsException("Cant move cursor to postion");
		}
	}

	protected abstract Entity<?> getEntity();

	public boolean isFirstLevelItem(Entity<?> item) {
		if (item == null)
			return false;
		if (item.getParentId() == 0)
			return false;
		if (getItem(item.getParentId()) == null)
			return false;
		return getItem(item.getParentId()).getParentId() == 0;
	}

	abstract public Entity<?> getItem(Entity<?> item, Cursor cursor);

	@SuppressWarnings("unchecked")
	protected T getItem(Entity<?> item, Cursor cursor, boolean flag) {
		if (!cursor.isAfterLast()) {
			item.setId(cursor.getInt(COL_ID)).setParentId(cursor.getLong(COL_PARENT_ID))
					.setName(cursor.getString(COL_NAME)).setTitle(cursor.getString(COL_TITLE))
					.setLink(cursor.getString(COL_LINK)).setImageLink(cursor.getString(COL_IMAGE_LINK))
					.setAuthor(cursor.getString(COL_AUTHOR)).setDescription(cursor.getString(COL_DESCRIPTION))
					.setCreateDate(cursor.getLong(COL_CREATE_DATE)).setUpdateDate(cursor.getLong(COL_UPDATE_DATE))
					.setAccessDate(cursor.getLong(COL_ACCESS_DATE))
					.setVisible(cursor.getInt(COL_VISIBLE) == 1)
					.setUpdatable(cursor.getInt(COL_UPDATABLE) == 1)
					.setAlbum(cursor.getInt(COL_ALBUM) == 1)
					.setFailed(cursor.getInt(COL_FAILED) == 1).setRating(cursor.getInt(COL_RATING));
			return (T) item;
		} else {
			throw new CursorIndexOutOfBoundsException("Cant move cursor to postion");
		}
	}

	public Entity<?> reloadItem(Entity<?> item) {
		return getItem(item.getId());
	}

	@Override
	public long getItemId(int position) {
		if (cursor.moveToPosition(position)) {
			this.position = position;
			return cursor.getInt(cursor.getColumnIndex(KEY_ID));
		} else {
			throw new CursorIndexOutOfBoundsException("Cant move cursor to postion");
		}
	}

	public long getNextItemId(int position) {
		long id=0;
		if (cursor.moveToPosition(position+1)) {
//			this.position = position;
			id= cursor.getLong(cursor.getColumnIndex(KEY_ID));
		}
		return id;
	}

	public long getPreviousItemId(int position) {
		long id=0;
		if (cursor.moveToPosition(position-1)) {
//			this.position = position;
			id= cursor.getLong(cursor.getColumnIndex(KEY_ID));
		}
		return id;
	}

	public long findIdByLink(String link) {
		return findIdByString(KEY_LINK, link);
	}

	public long findIdByImageLink(String imageLink) {
		return findIdByString(KEY_IMAGE_LINK, imageLink);
	}

	public long findIdByName(String name) {
		return findIdByString(KEY_NAME, name);
	}

	private long findIdByString(String KEY_type, String name) {
		long res = 0;

		Cursor cur = database.query(getTableName(), new String[] { KEY_ID }, KEY_type + " = ?", new String[] { name },
				null, null, null);
		if (cur.moveToFirst()) {
			res = cur.getLong(cur.getColumnIndex(KEY_ID));
			cur.close();
		}
		cur.close();
		return res;
	}

	public int findPositionById(long id) {
		int res = -1;

		if (cursor.moveToFirst()) {
			while (!cursor.isAfterLast()) {
				res++;
				if (cursor.getLong(cursor.getColumnIndex(KEY_ID)) == id)
					break;
				cursor.moveToNext();
			}
		}
		return res;
	}

	// public long getIdByLink(Cursor cursor,String link){
	//
	//
	//
	// return 0;
	// }
	//

	@Override
	abstract public View getView(int position, View convertView, ViewGroup parent);

	// getallentries
	// getfailedentries
	// getnewentries

	/*
	 * public Cursor getAllEntries() { String selection = null, selectionArgs[]
	 * = null; if (mActiveParent > 0) { selection = KEY_PARENT_ID + " = ? AND "
	 * + queryIsNotNull(KEY_LINK); selectionArgs = new String[] { mActiveParent
	 * + "", "" }; } cursor = database.query(getTableName(), null, selection,
	 * selectionArgs, null, null, KEY_ID); return cursor; }
	 */
	/*
	 * public Cursor getAllEntries(long parentId) { String selection = null,
	 * selectionArgs[] = null; if (parentId > 0) { selection = KEY_PARENT_ID +
	 * " = ? AND " + queryIsNotNull(KEY_LINK); selectionArgs = new String[] {
	 * parentId + "", "", }; } return database.query(getTableName(), null,
	 * selection, selectionArgs, null, null, KEY_ID); }
	 */
	protected String queryIsNull(String KEY_id) {
		return "(" + KEY_id + " IS NULL OR " + KEY_id + " = ?)";
	}

	protected String queryIsNotNull(String KEY_id) {
		return "(" + KEY_id + " IS NOT NULL AND " + KEY_id + " != ?)";
	}

	// Map conditions=new HashMap();
	// conditions.put("AllEntries",
	// new Map().put(selection = KEY_PARENT_ID + " = ? AND " + KEY_IMAGE_LINK +
	// " != ? AND " + KEY_IMAGE_LINK + " IS NOT NULL"
	// +" AND "+KEY_LINK+" != ?"+" AND "+KEY_LINK+" IS NOT NULL"
	//

	public void testAllEntries(long parentId) {
		// Список колонок базы, которые следует включить в результат
		// String[] columnsToTake = { KEY_ID, KEY_NAME, KEY_TITLE, KEY_LINK,
		// KEY_IMAGE_LINK, KEY_THUMB_TYPE,
		// KEY_DESCRIPTION, KEY_CREATE_DATE, KEY_UPDATE_DATE, KEY_ACCESS_DATE,
		// KEY_PARENT_ID };

		// запрос к базе
		String selection = null;
      String[] selectionArgs = null;

      if (parentId > 0) {
			selection = KEY_PARENT_ID + " = ?";
			selectionArgs = new String[] { parentId + "" };
		}
		Cursor cur = database.query(getTableName(), null, selection, selectionArgs, null, null, KEY_ID);
		if (cur.getCount() > 0) {
			cur.moveToFirst();
			while (!cur.isAfterLast()) {
				System.out.println(getItem(cur).toString());
				cur.moveToNext();
			}
		}
		cur.close();
	}

	public void removeFault(long parentId) {
		Item item;
		Cursor cur = getEntries(new SelectionFault(parentId));

		if (cur.moveToFirst()) {
			while (!cur.isAfterLast()) {
				item = (Item) getItem(cur);
				dismissItem(item);
				cur.moveToNext();
			}
		}
		cur.close();
		ItemAdapter.getInstance(context, parentId).refresh();
		// refresh();
	}

	abstract public long addItem(T item);

	protected long addItem(Entity<?> item, ContentValues values) {

		values.put(KEY_PARENT_ID, item.getParentId());
		values.put(KEY_NAME, item.getName());
		values.put(KEY_TITLE, item.getTitle());
		values.put(KEY_LINK, item.getLink());
		values.put(KEY_IMAGE_LINK, item.getImageLink());
		values.put(KEY_AUTHOR, item.getAuthor());
		values.put(KEY_DESCRIPTION, item.getDescription());
		Date val = item.getCreateDate();
		if (val == null)
			val = new Date();
		values.put(KEY_CREATE_DATE, val.getTime());
		// values.put(KEY_UPDATE_DATE, item.getUpdateDate());
		// values.put(KEY_ACCESS_DATE, item.getAccessDate());
		values.put(KEY_VISIBLE, item.isVisible() ? 1 : 0);
		values.put(KEY_UPDATABLE, item.isUpdatable() ? 1 : 0);
		values.put(KEY_ALBUM, item.isAlbum() ? 1 : 0);
		values.put(KEY_FAILED, item.isFailed() ? 1 : 0);

		values.put(KEY_RATING, item.getRating());
		long id = database.insert(getTableName(), null, values);
		item.setId(id);
		refresh();
		return id;
	}

	public boolean removeItem(Entity<?> item) {
		boolean isDeleted = removeItemNoRefresh(item);
		if (isDeleted)
			refresh();
		return isDeleted;
	}

	public boolean removeItemNoRefresh(Entity<?> item) {
		if (item == null)
			return false;

		item.getImageCachePath().delete();
		item.getImageCachePath().getParentFile().delete();

		boolean isDeleted = (database.delete(getTableName(), KEY_ID + "=?", new String[] { item.getId() + "" })) > 0;

		return isDeleted;
	}

	public boolean removeItemRecursively(Entity<?> item) {
		boolean isDeleted = false;

		Cursor cur = getEntries(new SelectionRaw(item.getId()));
		if (cur.moveToFirst()) {
			while (!cur.isAfterLast()) {
				removeItemRecursively(getItem(cur));
				cur.moveToNext();
			}
		}
		cur.close();
		isDeleted = removeItemNoRefresh(item);

		return isDeleted;
	}

	public boolean removeItem(long id) {
		if (id <= 0)
			return false;
		boolean isDeleted = (database.delete(getTableName(), KEY_ID + "=?", new String[] { id + "" })) > 0;
		if (isDeleted)
			refresh();
		return isDeleted;
	}

	abstract public boolean updateItem(Entity<?> item);

	abstract public boolean updateItemNoRefresh(Entity<?> item);

	protected boolean updateItem(Entity<?> item, ContentValues values) {

		boolean isUpdated = updateItemNoRefresh(item, values);
		refresh();
		return isUpdated;
	}

	protected boolean updateItemNoRefresh(Entity<?> item, ContentValues values) {
		values.put(KEY_PARENT_ID, item.getParentId());
		values.put(KEY_NAME, item.getName());
		values.put(KEY_TITLE, item.getTitle());
		values.put(KEY_LINK, item.getLink());
		values.put(KEY_IMAGE_LINK, item.getImageLink());
		values.put(KEY_AUTHOR, item.getAuthor());
		values.put(KEY_DESCRIPTION, item.getDescription());

		Date val = item.getCreateDate();
		if (val == null)
			val = new Date();
		values.put(KEY_CREATE_DATE, val.getTime());

		val = item.getUpdateDate();
		if (val == null)
			val = new Date();
		values.put(KEY_UPDATE_DATE, val.getTime());

		val = item.getAccessDate();
		if (val != null)
			values.put(KEY_ACCESS_DATE, val.getTime());

		values.put(KEY_VISIBLE, item.isVisible() ? 1 : 0);
		values.put(KEY_UPDATABLE, item.isUpdatable() ? 1 : 0);
		values.put(KEY_ALBUM, item.isAlbum() ? 1 : 0);
		values.put(KEY_FAILED, item.isFailed() ? 1 : 0);
		values.put(KEY_RATING, item.getRating());
		boolean isUpdated = (database.update(getTableName(), values, KEY_ID + "=?",
				new String[] { item.getId() + "" })) > 0;

		return isUpdated;
	}

	public boolean updateItemValue(long id, String key, String value) {
		boolean res = updateItemValueNoRefresh(id, key, value);
		refresh();
		return res;
	}

	public boolean updateItemValueNoRefresh(long id, String key, String value) {
		ContentValues values = new ContentValues();
		values.put(key, value);
		return (database.update(getTableName(), values, KEY_ID + "=?", new String[] { id + "" })) > 0;
	}

	public boolean updateItemValue(long id, String key, int value) {
		boolean res = updateItemValueNoRefresh(id, key, value);
		refresh();
		return res;
	}

	public boolean updateItemValueNoRefresh(long id, String key, int value) {
		ContentValues values = new ContentValues();
		values.put(key, value);
		return (database.update(getTableName(), values, KEY_ID + "=?", new String[] { id + "" })) > 0;
	}

	public boolean updateItemValue(long id, String key, boolean value) {
		boolean res = updateItemValueNoRefresh(id, key, value);
		refresh();
		return res;
	}

	public boolean updateItemValueNoRefresh(long id, String key, boolean value) {
		ContentValues values = new ContentValues();
		values.put(key, value ? 1 : 0);
		return (database.update(getTableName(), values, KEY_ID + "=?", new String[] { id + "" })) > 0;
	}

	public boolean updateItemValue(long id, String key, Date value) {
		boolean res = updateItemValueNoRefresh(id, key, value);
		refresh();
		return res;
	}

	public boolean updateItemValueNoRefresh(long id, String key, Date value) {
		for (String str : new String[] { KEY_CREATE_DATE, KEY_UPDATE_DATE, KEY_ACCESS_DATE }) {
			if (str.equals(key)) {
				ContentValues values = new ContentValues();
				values.put(key, value != null ? value.getTime() + "" : "0");
				return (database.update(getTableName(), values, KEY_ID + "=?", new String[] { id + "" })) > 0;
			}
		}
		return false;
	}

	public boolean updateAllItemsValueIfEmpty(long parentId, String key, Date value) {
		for (String str : new String[] { KEY_CREATE_DATE, KEY_UPDATE_DATE, KEY_ACCESS_DATE }) {
			if (str.equals(key)) {
				ContentValues values = new ContentValues();
				values.put(key, value != null ? value.getTime() + "" : "0");
				boolean isUpdated = (database.update(getTableName(), values,
						KEY_PARENT_ID + "=? AND (" + KEY_ACCESS_DATE + "=?" + " OR " + KEY_ACCESS_DATE + " IS NULL)",
						new String[] { parentId + "", "0" })) > 0;
				refresh();
				return isUpdated;
			}

		}
		return false;
	}

	public boolean updateAllItemsValue(String key, boolean value) {
		// for (String str : new String[] { KEY_CREATE_DATE, KEY_UPDATE_DATE,
		// KEY_ACCESS_DATE }) {
		// if (str.equals(key)) {
		ContentValues values = new ContentValues();
		values.put(key, value ? 1 : 0);
		boolean isUpdated = (database.update(getTableName(), values, null, null)) > 0;
		refresh();
		return isUpdated;
		// }
		//
		// }
		// return false;
	}

	abstract protected String getTableName();

	public void refresh() {
		cursor = getEntries();
		notifyDataSetChanged();

		Point size = new Point();
		((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getSize(size);
		screenWidth = size.x;

	}

	public ArrayList<String> getTitles() {
		refresh();
		ArrayList<String> titles = new ArrayList<String>();
		cursor.moveToFirst();
		if (!cursor.isAfterLast()) {
			do {
				String title = cursor.getString(cursor.getColumnIndex(KEY_TITLE));
				titles.add(title);
			} while (cursor.moveToNext());
		}
		return titles;
	}

	public void setView(int view) {
		this.mView = view;
	}

	public long getActiveParent() {
		return mActiveParent;
	}

	public void setActiveParent(long mActiveParent) {
		this.mActiveParent = mActiveParent;
		refresh();
	}

	public int getPosition() {
		return position;
	}

	public int setPositionById(long id) {
		int pos=0;
		if(cursor!=null && id>0 && cursor.moveToFirst()){
			while(!cursor.isAfterLast()){
				if(cursor.getLong(cursor.getColumnIndex(KEY_ID))==id){
					position=pos;
					return pos;
				}
				pos++;
				cursor.moveToNext();
			}
		}
		return 0;
	}

	public long getIdOfCurrentPosition() {
		long id = 0;
		if (cursor != null && cursor.moveToPosition(position)) {
			id = cursor.getLong(cursor.getColumnIndex(KEY_ID));
		}

		return id;
	}

	public static int getThumbOptimalSize() {
		int width;

		if (screenWidth > 1000)
			width = screenWidth / 5 - 4;
		else if (screenWidth > 600)
			width = screenWidth / 4 - 3;
		else if (screenWidth > 400)
			width = screenWidth / 3 - 2;
		else
			width = screenWidth / 2 - 1;

		return width;
	}

	public static int getBigOptimalWidth() {
		int width;

		if (screenWidth > 600)
			width = screenWidth / 2 - 1;
		else
			width = screenWidth;

		return width;
	}

	public void setSelectionTerms(SelectionTerms terms) {
		this.terms = terms;
		refresh();
	}

	public SelectionTerms getSelectionTerms() {
		return terms;
	}

	public Cursor getEntries() {
		return database.query(getTableName(), null, terms.getSelection(), terms.getSelectionArgs(), null, null,
				terms.getOrder());
	}

	public Cursor getEntries(SelectionTerms terms) {
		return database.query(getTableName(), null, terms.getSelection(), terms.getSelectionArgs(), null, null,
				terms.getOrder());
	}

	public boolean dismissItem(Entity<?> item) {
		if (item == null)
			return false;

		if (item.isAlbum()) {
			Cursor cur = getEntries(new SelectionAlbum(item.getId()));
			if (cur.moveToFirst()) {
				while (!cur.isAfterLast()) {
					dismissItem(getItem(cur));
					cur.moveToNext();
				}
			}
			cur.close();
		}

		if (!isFirstLevelItem(item)) {
			item.getImageCachePath().delete();
			item.getImageCachePath().getParentFile().delete();

			item.setTitle("").setAuthor("").setDescription("").setAccessDate(null).setFailed(false).setLink("")
					.setRating(0);
		}
		return updateItem(item);
	}

	public boolean dismissItemRecursively(Entity<?> item) {
		if (item == null)
			return false;

		if (item.isAlbum()) {
			Cursor cur = getEntries(new SelectionAlbum(item.getId()));
			if (cur.moveToFirst()) {
				while (!cur.isAfterLast()) {
					dismissItemRecursively(getItem(cur));
					cur.moveToNext();
				}
			}
			cur.close();
		}

		return dismissItemNoRefresh(item);
	}

	public boolean dismissItemNoRefresh(Entity<?> item) {
		if (item == null)
			return false;

		if (!isFirstLevelItem(item)) {
			item.getImageCachePath().delete();
			item.getImageCachePath().getParentFile().delete();

			item.setTitle("").setAuthor("").setDescription("").setAccessDate(null).setFailed(false).setLink("")
					.setRating(0);
		}
		return updateItemNoRefresh(item);
	}

}
