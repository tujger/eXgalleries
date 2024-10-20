package com.edeqa.exgalleries.helpers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.util.Date;

import android.content.Context;
import android.os.StrictMode;
import android.util.Log;
import com.edeqa.exgalleries.interfaces.TaskCompletedInterface;

public class InteractorJSMethods {

	public String m_error_message;

	private final ItemAdapter ia;
	private TaskCompletedInterface callback;
	// private Context context;
	private boolean ready = false;
	// private boolean cancelled=false;
	@SuppressWarnings("unused")
	private final Context context;
	private static int counter;

	public InteractorJSMethods(Context context) {
		this.context = context;
		ia = ItemAdapter.getInstance(context, 0);
	}

	public void setCallback(TaskCompletedInterface callback) {
		this.callback = callback;
	}


	// API FUNCTIONS
	public String Print(String text) {
		Log.v("Interactor", text);
		return "true";
	}

	public void loaded() {
		ready = true;
	}

	public boolean isReady() {
		return ready;
	}

	public void done(String str) {
		if (str.length() > 0)
			// Log.i("Interactor", str);
			callback.onTaskCompleted("");
		// context.API_DONE=true;
	}

	public String RequestGetCharset(String url, String urlCharset) {
		String line = null;
		StringBuilder sb = new StringBuilder();
		InputStream in = null;

		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
		StrictMode.setThreadPolicy(policy);

		URLConnection feedUrl = null;
		try {
			feedUrl = new URL(url).openConnection();
			feedUrl.setRequestProperty("User-Agent",
					"Mozilla/5.0 (Windows; U; Windows NT 5.1; ru; rv:1.8.1.12) Gecko/20080201 Firefox");
			in = feedUrl.getInputStream();
			BufferedReader reader;
			reader = new BufferedReader(new InputStreamReader(in, urlCharset));
			while ((line = reader.readLine()) != null) {
				sb.append(new String(line.getBytes(StandardCharsets.UTF_8)) + "\n");
			}
			in.close();
		} catch (MalformedURLException e) {
			e.printStackTrace();
			return "";
		} catch (IOException e) {
			e.printStackTrace();
			return "";
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}

		return sb.toString();
	}

	public String GetLink(long id) {
		return ia.getItem(id).getLink();
	}

	public long GetParentId(long id) {
		return ia.getItem(id).getParentId();
	}

	// public boolean IsUpdate(){
	// return A.serviceStarted;
	// }

	public long AddItem(long parentId, String link) {
		long res = 0;

		Item item = new Item();
		item.setParentId(parentId).setLink(link);
		res = ia.addItem(item);

		return res;
	}

	public void FinishItem(long id) {
		ia.updateItemValueNoRefresh(id, ItemAdapter.KEY_UPDATABLE, false);
	}

	public boolean SaveItem(long id, long parentId, String imageLink, String title, String author, String description,
			long createDate) {
		boolean res = false;

		if (ia.findIdByImageLink(imageLink) > 0) {
			ia.removeItem(id);
		} else {

			Item item = (Item) ia.getItem(id);

			if(imageLink.length()>0){
				item.setImageLink(imageLink);

				item.setParentId(parentId);
				if (parentId > 0) {
					ia.updateItemValueNoRefresh(parentId, ItemAdapter.KEY_ALBUM, true);
				}

				item.setTitle(title);
				item.setAuthor(author);
				item.setDescription(description);
				item.setCreateDate(createDate);
				item.setFailed(false);
				item.setUpdateDate(new Date());
//				ia.updateItemValueNoRefresh(item.getId(), ItemAdapter.KEY_UPDATE_DATE, new Date());


				if(!ia.isFirstLevelItem(item))
					item.setUpdatable(false);

				if (ia.updateItem(item)){
					setCounter(getCounter() + 1);
					res=true;
				}
			}
		}
		return res;
	}

	public long FindIdByLink(String link) {
		return ia.findIdByLink(link);
	}

	public void Finish() {
		System.out.println("API_FINISH");
	}

	public void Start(int id) {
		System.out.println("API_START");
	}

	public void SetError(int id, int event) {
		System.out.println("API_ERROR " + event);
	}

	public void Notification(int event, long genreId, int counter) {

		// if(!A.serviceStarted || event==NOTIFICATION_CANCEL){
		// A.notificationManager.cancel(101);
		// A.messageForReceiver.setUpdateInactive().setRefreshList().sendMessage();
		// return;
		// }
		// int icon=0;
		// String text="",ticker="";
		//
		// Notification.Builder nb = new Notification.Builder(context.context)
		// .setContentIntent(PendingIntent.getActivity(context.context, 0,
		// new Intent(context.context, Plugins.class), 0));
		//
		// switch(event){
		// case NOTIFICATION_NEW:
		// icon=R.drawable.navigation_refresh;
		// ticker=context.context.getString(R.string.notification_updating_ticker);
		//// nb.setWhen(new Date().getTime());
		//
		// if(genreId>0){
		// GenreHelper genre=new GenreHelper(A.db, genreId);
		// text=context.context.getString(R.string.notification_updating_text,
		// A.db.getPluginString(genre.getPluginId(),
		// A.KEY_LABEL),genre.getLabel(),counter,A.totalCounter+counter);
		// }else
		// text=context.context.getString(R.string.notification_updating_ticker);
		//
		// A.totalCounter+=counter;
		//
		// break;
		// case NOTIFICATION_CURRENT:
		// icon = R.drawable.av_download;
		// A.currentCounter+=counter;
		// ticker=context.context.getString(R.string.notification_updating_ticker);
		// text=context.context.getString(R.string.notification_downloading_text,A.currentCounter,A.totalCounter);
		// nb.setProgress(A.totalCounter, A.currentCounter, false);
		// break;
		// case NOTIFICATION_FINISH:
		// icon = R.drawable.content_unread;
		// ticker=context.context.getString(R.string.notification_finish_ticker,A.currentCounter);
		// text=context.context.getString(R.string.notification_finish_text,A.currentCounter);
		// A.messageForReceiver.setUpdateInactive();
		// break;
		// }
		//
		// Notification notification = nb
		// .setTicker(ticker)
		// .setContentTitle(A.APP_NAME)
		// .setContentText(text)
		// .setSmallIcon(icon)
		// .setOngoing(true).build();
		//
		// notification.flags |= Notification.FLAG_AUTO_CANCEL;
		//// AppHelper.mNotification.flags |= Notification.FLAG_ONGOING_EVENT;
		//
		//// AppHelper.mNotification.setLatestEventInfo(context, "Feeder", text,
		// contentIntent);
		// A.notificationManager.notify(101, notification);
		// A.messageForReceiver.setRefreshList().sendMessage();
		//
	}

	public static int getCounter() {
		return counter;
	}

	public static void setCounter(int counter) {
		InteractorJSMethods.counter = counter;
	}

}
