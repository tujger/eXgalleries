package com.edeqa.exgalleries.helpers;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.preference.PreferenceManager;
import com.edeqa.exgalleries.Main;

public class NetworkState {

	private static NetworkState mInstance;
	private final ConnectivityManager connMgr;
	private int condition;

	public final int NETWORK_WIFI = 1;
	public final int NETWORK_MOBILE = 2;
	public final int NETWORK_ROAMING = 4;

	private NetworkState() {
		connMgr = (ConnectivityManager) Main.getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
		reInit();
	}

	public static NetworkState getInstance() {
		if (mInstance == null) {
			synchronized (Main.getContext()) {
				if (mInstance == null) {
					mInstance = new NetworkState();
				}
			}
		}
		return mInstance;
	}

	public NetworkState reInit(){
//		System.out.println("ACT="+Main.getMainActivity());
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(Main.getContext());
		condition=Integer.valueOf(sp.getString("use_network", "1"));
		return this;
	}

	public boolean isWifi() {
		return connMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI).isConnected();
	}

	public boolean isMobile() {
		return connMgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).isConnected();
	}

	public boolean isRoaming() {
		return connMgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).isRoaming();
	}

	public boolean isAvailable() {
		if ((condition & NETWORK_WIFI) > 0 && isWifi())
			return true;
		else if ((condition & NETWORK_MOBILE) > 0 && (isWifi() || (isMobile() && !isRoaming())))
			return true;
		else
          return (condition & NETWORK_ROAMING) > 0 && (isWifi() || isMobile());
    }

}
