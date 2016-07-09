package ru.wtg.exgalleries.helpers;

import java.io.IOException;
import java.io.Serializable;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import android.text.TextUtils;

public class LibraryParser implements Serializable{

	private static final long serialVersionUID = -2096257442937879400L;
	
	public final static String NAME = "name";
	public final static String TITLE = "title";
	public final static String LINK = "link";
	public final static String IMAGE_LINK = "image";
	public final static String VERSION = "version";
	public final static String MIN_API = "min_api";
	public final static String DESCRIPTION = "description";
	public final static String AUTHOR = "author";
	public final static String KEYWORDS = "keywords";
	public final static String JSMAIN = "jsmain";
	public final static String SOURCE = "source";

	private StringBuffer sb;
	private Map<String, ArrayList<String>> properties;
	private LinkedList<Item> galleries;
	private boolean bSuccess;

	public LibraryParser(StringBuffer sb) {
		this.sb = sb;
		bSuccess = false;
		properties = new HashMap<String, ArrayList<String>>();
		galleries = new LinkedList<Item>();
		parse();
	}

	private XmlPullParser prepareXpp() throws XmlPullParserException {
		XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
		factory.setNamespaceAware(true);
		XmlPullParser xpp = factory.newPullParser();
		xpp.setInput(new StringReader(sb.toString()));
		return xpp;
	}

	private void parse() {
		String property = "";
		Item lg=null;
		try {
			XmlPullParser xpp = prepareXpp();

			while (xpp.getEventType() != XmlPullParser.END_DOCUMENT) {
				switch (xpp.getEventType()) {
				case XmlPullParser.START_DOCUMENT:
					break;
				case XmlPullParser.START_TAG:
					if (xpp.getDepth() == 2) {
						property = xpp.getName();
						if (xpp.getName().equals("gallery")) {
							lg = new Item();
							for (int i = 0; i < xpp.getAttributeCount(); i++) {
								lg.setProperty(xpp.getAttributeName(i), xpp.getAttributeValue(i));
							}
						}
					}
					break;
				case XmlPullParser.END_TAG:
					property = "";
					break;
				case XmlPullParser.TEXT:
					if (property.length() > 0 && !property.equals("gallery")) {
						if (properties.containsKey(property)) {
							properties.get(property).add(xpp.getText());
						} else {
							ArrayList<String> list = new ArrayList<String>();
							list.add(xpp.getText().trim());
							properties.put(property, list);
						}
					} else if (lg!=null) {
						lg.setProperty("title", xpp.getText().trim());
						galleries.add(lg);
						lg=null;
					}
					property = "";
					break;

				default:
					break;
				}
				xpp.next();
			}
			bSuccess = true;
		} catch (

		XmlPullParserException e)

		{
			e.printStackTrace();
		} catch (

		IOException e)

		{
			e.printStackTrace();
		}

	}

	public boolean isSuccess() {
		return bSuccess;
	}

	public String getProperty(String name) {
		String str = "";

		if (properties.containsKey(name)) {
			str = TextUtils.join("\n", properties.get(name));

		}

		return str;
	}

	public LinkedList<Item> getGalleries() {
		return galleries;
	}

}
