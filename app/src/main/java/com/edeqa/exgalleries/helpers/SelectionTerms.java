package com.edeqa.exgalleries.helpers;

import java.util.LinkedHashMap;
import java.util.Map;

import android.text.TextUtils;

public class SelectionTerms {

	public static final int SHOW_ALL=0;
	public static final int SHOW_ONLY_NEW=1;
	public static final int SHOW_ONLY_OLD=2;
	public static final int SHOW_ONLY_RATED=3;

	private final Map<String, String> terms_key;
	private final Map<String, String> terms_value;
	private String order;

	public SelectionTerms() {
		terms_key = new LinkedHashMap<String, String>();
		terms_value = new LinkedHashMap<String, String>();
		order = ItemAdapter.KEY_ID + " ASC";
		// TODO Auto-generated constructor stub
	}

	public String getSelection() {
		return TextUtils.join(" AND ", terms_key.values().toArray(new String[terms_key.keySet().size()]));
	}

  public String[] getSelectionArgs() {
		return terms_value.values().toArray(new String[terms_value.values().size()]);
	}

  public String getOrder(){
		return order;
	}

	public void addOrder(String KEY_type,boolean ASC){
		order=KEY_type + (ASC?" ASC":" DESC");
	}

	public void addTermIs(String name, String value) {
		terms_key.put(name, queryIs(name));
		terms_value.put(name, value);
	}

	public void addTermIsNotNull(String name) {
		terms_key.put(name, queryIsNotNull(name));
		terms_value.put(name, "");
	}

	public void addTermIsNull(String name) {
		terms_key.put(name, queryIsNull(name));
		terms_value.put(name, "");
	}

	public void addTermIs(String name, boolean value) {
		terms_key.put(name, queryIs(name));
		terms_value.put(name, value ? "1" : "0");
	}

	public void addTermIs(String name, int value) {
		terms_key.put(name, queryIs(name));
		terms_value.put(name, value + "");
	}

	public void addTermIs(String name, double value) {
		terms_key.put(name, queryIs(name));
		terms_value.put(name, value + "");
	}

	public void addTermIs(String name, long value) {
		terms_key.put(name, queryIs(name));
		terms_value.put(name, value + "");
	}

	private String queryIsNull(String KEY_id) {
		return "(" + KEY_id + " IS NULL OR "+KEY_id+" = 0 OR " + KEY_id + " = ?)";
	}

	private String queryIsNotNull(String KEY_id) {
		return "(" + KEY_id + " IS NOT NULL AND "+KEY_id+" != 0 AND " + KEY_id + " != ?)";
	}

	private String queryIs(String KEY_id) {
		return "(" + KEY_id + " IS NOT NULL AND " + KEY_id + " = ?)";
	}



}
