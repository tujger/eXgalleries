package com.edeqa.exgalleries;

import java.util.HashMap;
import java.util.Map;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.view.MenuItem;
import com.edeqa.exgalleries.helpers.Interactor;

/**
 *
 */

/**
 * @author Edward
 *
 */
public class Main extends Application{

	private static Context context;
	// private static Context currentActivity=null;
	private static Interactor interactor;
	private static MainActivity mainActivity=null;
	@SuppressLint("UseSparseArrays")
	private static final Map<Integer,MenuItem> menuItem=new HashMap<Integer, MenuItem>();
	private static boolean serviceRunning=false;

    public void onCreate(){
        super.onCreate();
        Main.context = getApplicationContext();
    }

    public static Context getContext() {
        return Main.context;
    }

	public static boolean isServiceRunning() {
		return serviceRunning;
	}

	public static void setServiceRunning(boolean serviceRunning) {
		Main.serviceRunning = serviceRunning;
	}

	public static Interactor getInteractor() {
		return interactor;
	}

	public static void setInteractor(Interactor interactor) {
		Main.interactor = interactor;
	}

	public static MainActivity getMainActivity() {
		return mainActivity;
	}

	public static void setMainActivity(MainActivity mainActivity) {
		Main.mainActivity = mainActivity;
	}

	/*
	 * public static Context getCurrentActivity() { return currentActivity; }
	 *
	 * public static void setCurrentActivity(Context currentActivity) {
	 * Main.currentActivity = currentActivity; }
	 */
	public static MenuItem getMenuItem(int id) {
		return Main.menuItem.get(id);
	}

	public static void setMenuItem(int id,MenuItem menuItem) {
		Main.menuItem.put(id,menuItem);
	}
}
