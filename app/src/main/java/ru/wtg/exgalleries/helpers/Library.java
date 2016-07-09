package ru.wtg.exgalleries.helpers;

import java.util.Date;

import android.content.Context;
import android.content.SharedPreferences.Editor;
import ru.wtg.exgalleries.Main;

public class Library extends Entity<Library> {

	private static final long serialVersionUID = 1912146259453287102L;

	private Date sourceUpdateDate;

	public Library() {
		super();
	}

	@Override
	protected Library getThis() {
		return this;
	}

	@Override
	public long getParentId() {
		return 0;
	}

	@Override
	public Library setParentId(long parentId) {
		return getThis();
	}

	public boolean setProperty(String property, String value) {
		if (super.setProperty(property, value))
			return true;
		boolean res = false;
		if (property.equals("version")) {
			setVersion(this, Double.valueOf(value));
			res = true;
		} else if (property.equals("keywords")) {
			setKeywords(this, value);
			res = true;
		} else if (property.equals("source")) {
			setSource(this, value);
			res = true;
		} else if (property.equals("jsmain")) {
			setJsmain(this, value);
			res = true;
		}
		return res;
	}

	public static Double getVersion(Entity<?> item) {
		return (double) Main.getContext().getSharedPreferences("library_options", Context.MODE_PRIVATE)
				.getFloat(item.getName() + "_version", 1.f);
	}

	public static void setVersion(Entity<?> item, Double version) {
		Editor editor = Main.getContext().getSharedPreferences("library_options", Context.MODE_PRIVATE).edit();
		editor.putFloat(item.getName() + "_version", (float) (double) version);
		editor.commit();
	}

	public static int getThumbType(Entity<?> item) {
		return Math.round(Main.getContext().getSharedPreferences("library_options", Context.MODE_PRIVATE)
				.getFloat(item.getName() + "_thumb_type", 0));
	}

	public static void setThumbType(Entity<?> item, int type) {
		Editor editor = Main.getContext().getSharedPreferences("library_options", Context.MODE_PRIVATE).edit();
		editor.putFloat(item.getName() + "_thumb_type", type);
		editor.commit();
	}

	public static String getKeywords(Entity<?> item) {
		return Main.getContext().getSharedPreferences("library_options", Context.MODE_PRIVATE)
				.getString(item.getName() + "_keywords", "");
	}

	public static void setKeywords(Entity<?> item, String keywords) {
		Editor editor = Main.getContext().getSharedPreferences("library_options", Context.MODE_PRIVATE).edit();
		editor.putString(item.getName() + "_keywords", keywords);
		editor.commit();
	}

	public static String getJsmain(Entity<?> item) {
		return Main.getContext().getSharedPreferences("library_options", Context.MODE_PRIVATE)
				.getString(item.getName() + "_jsmain", "");
	}

	public static void setJsmain(Entity<?> item, String jsmain) {
		Editor editor = Main.getContext().getSharedPreferences("library_options", Context.MODE_PRIVATE).edit();
		editor.putString(item.getName() + "_jsmain", jsmain);
		editor.commit();
	}

	public static String getSource(Entity<?> item) {
		return Main.getContext().getSharedPreferences("library_options", Context.MODE_PRIVATE)
				.getString(item.getName() + "_source", "");
	}

	public static void setSource(Entity<?> item, String source) {
		Editor editor = Main.getContext().getSharedPreferences("library_options", Context.MODE_PRIVATE).edit();
		editor.putString(item.getName() + "_source", source);
		editor.commit();
	}

	public static Date getSourceUpdateDate(Entity<?> item) {
		long l;
		l = Main.getContext().getSharedPreferences("library_options", Context.MODE_PRIVATE)
				.getLong(item.getName() + "_source_updated", 0);
		if (l > 0)
			return new Date(l);
		else
			return null;
	}

	public static void setSourceUpdated(Entity<?> item, Date updateSourceDate) {
		if (updateSourceDate != null) {
			Editor editor = Main.getContext().getSharedPreferences("library_options", Context.MODE_PRIVATE).edit();
			editor.putLong(item.getName() + "_source_updated", updateSourceDate.getTime());
			editor.commit();
		}
	}

	public static void setSourceUpdated(Entity<?> item, long updateSourceDate) {
		if (updateSourceDate > 0)
			setSourceUpdated(item, new Date(updateSourceDate));
	}

	public String getFormattedSourceUpdateDate(int DATEFORMAT_mode) {
		return getDateFormat(DATEFORMAT_mode, sourceUpdateDate);
	}

	public static void clearPreferences(Entity<?> item) {
		clearPreferences(item.getName());
	}

	public static void clearPreferences(String name) {
		Main.getContext().getSharedPreferences("library_options", Context.MODE_PRIVATE).edit()
		.remove(name + "_version").commit();
		Main.getContext().getSharedPreferences("library_options", Context.MODE_PRIVATE).edit()
		.remove(name + "_thumb_type").commit();
		Main.getContext().getSharedPreferences("library_options", Context.MODE_PRIVATE).edit()
		.remove(name + "_keywords").commit();
		Main.getContext().getSharedPreferences("library_options", Context.MODE_PRIVATE).edit()
		.remove(name + "_jsmain").commit();
		Main.getContext().getSharedPreferences("library_options", Context.MODE_PRIVATE).edit()
		.remove(name + "_source").commit();
		Main.getContext().getSharedPreferences("library_options", Context.MODE_PRIVATE).edit()
		.remove(name + "_source_updated").commit();
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return toString("");
	}

	// @Override
	// public String toString() {
	// return toString("VERSION=[" + getVersion() + "] "
	// + (getSource().length() > 0 ? "SOURCE=[" + getSource() + "] " : "") +
	// (getSourceUpdateDate() != null
	// ? "SOURCEUPDATED=[" + getFormattedSourceUpdateDate(DATEFORMAT_ADAPTIVE) +
	// "] " : ""));
	// }

}
