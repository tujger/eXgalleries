package com.edeqa.exgalleries;

import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.IBinder;
import android.text.TextUtils;
import android.widget.Toast;
import com.edeqa.exgalleries.helpers.Entity;
import com.edeqa.exgalleries.helpers.Interactor;
import com.edeqa.exgalleries.helpers.InteractorJSMethods;
import com.edeqa.exgalleries.helpers.Item;
import com.edeqa.exgalleries.helpers.ItemAdapter;
import com.edeqa.exgalleries.helpers.Library;
import com.edeqa.exgalleries.helpers.NetworkState;
import com.edeqa.exgalleries.helpers.SelectionForUpdate;
import com.edeqa.exgalleries.interfaces.TaskCompletedInterface;

public class ExGalleriesService extends Service {

	final public static String ACTION_REFRESH = "refresh";
	final public static String ACTION_CHECK = "check";
	final public static int DONE_FIRST_LEVEL = 1;
	final public static int DONE_LEVELS = 2;
	protected int tasks = 0, doneTasks = 0;

	ExecutorService es;
	ItemAdapter ia;
	PendingIntent pendingIntent;
	NotificationManager notificationManager;
	Notification.Builder notificationBuilder;

	// private static boolean serviceRunning=false;

	public void onCreate() {
		super.onCreate();

		es = Executors.newFixedThreadPool(1);
		notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		notificationBuilder = new Notification.Builder(getApplicationContext());
	}

	public int onStartCommand(Intent intent, int flags, int startId) {

		String[] s = TextUtils.split(intent.getAction(), ":");
		Map<String, Long> actions = new HashMap<String, Long>();
		for (int i = 0; i < s.length; i++) {
			actions.put(s[i], Long.valueOf(s[++i]));
		}

		if (actions.containsKey(ACTION_CHECK)) {
			if (Main.isServiceRunning()) {
				Intent intent2 = new Intent(MainActivity.BROADCAST_RECEIVER);
				intent2.putExtra(MainActivity.BROADCAST_ACTION, MainActivity.BROADCAST_SERVICE_WORKING);
				sendBroadcast(intent2);
			}
			stopSelf(startId);
			return START_STICKY;
		}

		if (Main.isServiceRunning()) {
			es.shutdownNow();
			Intent intent2 = new Intent(MainActivity.BROADCAST_RECEIVER);
			intent2.putExtra(MainActivity.BROADCAST_ACTION, MainActivity.BROADCAST_SERVICE_DONE);
			Main.setServiceRunning(false);
			sendBroadcast(intent2);

			stopForeground(true);
			// notificationManager.cancel(1);

			Toast.makeText(getApplicationContext(), getString(R.string.stop_updating), Toast.LENGTH_SHORT).show();
			stopSelf();
			return super.onStartCommand(intent, flags, startId);
		}

		if (!NetworkState.getInstance().reInit().isAvailable()) {
			Toast.makeText(getApplicationContext(),
					getString(R.string.cannot_update_because_of_network_is_not_available), Toast.LENGTH_SHORT).show();

			notificationBuilder.setAutoCancel(true).setContentText(getString(R.string.network_error));
			notificationManager.notify(1, notificationBuilder.build());

			stopForeground(false);
			stopSelf(startId);
			return START_STICKY;
		}

		Intent intent2 = new Intent(MainActivity.BROADCAST_RECEIVER);
		intent2.putExtra(MainActivity.BROADCAST_ACTION, MainActivity.BROADCAST_SERVICE_WORKING);
		sendBroadcast(intent2);

		Intent notificationIntent = new Intent(getApplicationContext(), MainActivity.class);
		PendingIntent contentIntent = PendingIntent.getActivity(getApplicationContext(), 0, notificationIntent,
				PendingIntent.FLAG_CANCEL_CURRENT | PendingIntent.FLAG_IMMUTABLE);

		Resources res = getApplication().getResources();

		notificationBuilder.setContentIntent(contentIntent).setSmallIcon(R.drawable.ic_refresh_white_36dp)// .setSmallIcon(R.drawable.ic_collections_white_24dp)//
				// setSmallIcon(R.drawable.ic_launcher_32x32)
				// большая картинка
				// .setLargeIcon(R.drawable.ic_launcher_64x64)
				.setLargeIcon(BitmapFactory.decodeResource(res, R.drawable.ic_launcher_64x64))
				.setTicker(getString(R.string.app_updating))// текст в
				// строке
				// состояния
				.setWhen(System.currentTimeMillis()).setAutoCancel(false).setContentTitle(getString(R.string.updating))// Заголовок
																														// уведомления
				.setContentText(getString(R.string.updating_ellipsis)); // Текст
																		// уведомления

		Notification notification = notificationBuilder.build();
		startForeground(1, notification);

		// notificationManager.notify(1, notification);

		tasks = 0;
		doneTasks = 0;
		Main.setServiceRunning(true);
		InteractorJSMethods.setCounter(0);
		ia = ItemAdapter.getInstance(getApplicationContext(), 0);
		ia.updateAllItemsValue(ItemAdapter.KEY_FAILED, false);

		RefreshLibrary mr = new RefreshLibrary(ia.getItem(actions.get(ACTION_REFRESH)), startId);
		es.execute(mr);

		// return super.onStartCommand(intent, flags, startId);
		return START_STICKY;// FLAG_REDELIVERY;
	}

	public void onDestroy() {
		super.onDestroy();
	}

	public IBinder onBind(Intent intent) {
		return null;
	}

	public void onRebind(Intent intent) {
		super.onRebind(intent);
	}

	public boolean onUnbind(Intent intent) {
		return super.onUnbind(intent);
	}

	class RefreshLibrary implements Runnable, TaskCompletedInterface {

		Entity<?> item;
		int startId;
		int flag;
		RefreshLibrary task;
		Object monitor = new Object();
		boolean ready = false;

		public RefreshLibrary(Entity<?> item, int startId) {
			this.flag = 0;
			this.item = item;
			this.startId = startId;
		}

		public RefreshLibrary(int flag, int startId) {
			this.flag = flag;
			this.startId = startId;
		}

		public void run() {

			if (flag == DONE_FIRST_LEVEL) {// set tasks for parse new Pictures
				Cursor cur = ia.getEntries(new SelectionForUpdate());
				if (cur.moveToFirst()) {
					while (!cur.isAfterLast()) {

						task = new RefreshLibrary(ia.getItem(cur), startId);
						es.execute(task);
						cur.moveToNext();
					}
				}
				if (cur.getCount() > 0) {
					task = new RefreshLibrary(DONE_FIRST_LEVEL, startId);
					if (!es.isShutdown()) {
						es.submit(task);
					}
				} else {
					task = new RefreshLibrary(DONE_LEVELS, startId);
					if (!es.isShutdown()) {
						es.submit(task);
					}
				}
				cur.close();

			} else if (flag == DONE_LEVELS) {
				Main.getInteractor().callFinish();
				doneTasks++;

				if (doneTasks < tasks)
					return;

				Main.getInteractor().showCounter();

				if (InteractorJSMethods.getCounter() > 0) {
					stopForeground(false);
					notificationBuilder.setSmallIcon(R.drawable.ic_collections_white_36dp).setAutoCancel(true)
							.setContentTitle(getString(R.string.update_complete))
							.setContentText(getString(R.string.value_new_pictures, InteractorJSMethods.getCounter()));
					notificationManager.notify(1, notificationBuilder.build());
				} else {
					stopForeground(true);
					notificationManager.cancel(1);
				}

				Intent intent = new Intent(MainActivity.BROADCAST_RECEIVER);
				intent.putExtra(MainActivity.BROADCAST_ACTION, MainActivity.BROADCAST_SERVICE_DONE);
				sendBroadcast(intent);

				Main.setServiceRunning(false);
				Main.setInteractor(null);

				stopSelf(startId);

			} else if (item == null) {// first, set tasks for get all Galleries
				// ia.updateItem(item.setUpdateDate(new Date()));

				// ia.testAllEntries(item.getId());
				Cursor cur = ia.getEntries(new SelectionForUpdate(0));
				if (cur.moveToFirst()) {
					Item child;
					while (!cur.isAfterLast()) {
						child = (Item) ia.getItem(cur);
						task = new RefreshLibrary(child, startId);
						if (!es.isShutdown()) {
							es.execute(task);
							tasks++;
						}
						cur.moveToNext();
					}
				}
				cur.close();

				// task = new RefreshLibrary(DONE_FIRST_LEVEL, startId);
				// if (!es.isShutdown()) {
				// es.execute(task);
				// }

			} else if (item.getParentId() == 0) {// first, set tasks for get
													// Galleries
													// of selected Library

				Main.setInteractor(new Interactor(Main.getContext(), Library.getJsmain(item)));

				ia.updateItem(item.setUpdateDate(new Date()));

				Cursor cur = ia.getEntries(new SelectionForUpdate(item.getId()));
				if (cur.moveToFirst()) {
					Item child;
					while (!cur.isAfterLast()) {
						child = (Item) ia.getItem(cur);
						// System.out.println("UPDATELIB="+child.toString());
						task = new RefreshLibrary(child, startId);
						if (!es.isShutdown()) {
							es.execute(task);
						}
						cur.moveToNext();
					}
				}
				cur.close();

				task = new RefreshLibrary(DONE_FIRST_LEVEL, startId);
				if (!es.isShutdown()) {
					es.execute(task);
				}

			} else if (item instanceof Item) {// get Pictures of selected
												// Gallery
				// System.out.println("UPDATE="+item.toString());

				if (ia.isFirstLevelItem(item)) {
					ia.updateItemValueNoRefresh(item.getId(), ItemAdapter.KEY_FAILED, true);
					notificationBuilder.setContentTitle(
							getString(R.string.updating_value_ellipsis, ia.getItem(item.getParentId()).getTitle()));
				} else
					ia.updateItemValueNoRefresh(item.getId(), ItemAdapter.KEY_UPDATABLE, false);

				if (item.getTitle().length() > 0) {
					notificationBuilder.setContentText(item.getTitle());
					notificationManager.notify(1, notificationBuilder.build());
				} else {
					notificationBuilder.setContentText(
							getString(R.string.found_value_new_pictures, InteractorJSMethods.getCounter()));
					notificationManager.notify(1, notificationBuilder.build());
				}

				if (NetworkState.getInstance().isAvailable())
					getWebPage(Main.getInteractor(), "GetItem(%d)");
			}
		}

		private void getWebPage(final Interactor browser, final String command) {

			// String str = LoadData("art-of-fantasy.js");
			// browser = new Interactor(Main.getContext(), str);

			// browser = new Interactor(Main.getContext(), lib.getJsmain());

			if (browser.isReady()) {

				browser.setCallback(this);
				Handler handler = new Handler(getMainLooper());
				handler.post(new Runnable() {

					@Override
					public void run() {
						browser.callMethod(String.format(command, item.getId()));
					}
				});

				synchronized (monitor) {// waiting for loading and finishing
										// page
					while (!ready) {
						try {
							monitor.wait();
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				}
				// browser.browser.loadUrl("javascript:test('Test String');");
				// browser.callMethod("test('Test String')");
			} else {
				Toast.makeText(getApplicationContext(), getString(R.string.javascript_initialization_timeout),
						Toast.LENGTH_SHORT).show();
            }

			// while(!browser.API_DONE && i++<WebViewJS.WAIT_FOR_DONE){
			// try {
			//// U.l("API_WAIT_FOR_DONE",i,"--------"+action.call());
			// Thread.sleep(1000);
			// } catch (InterruptedException e) {
			// U.e("API_ERROR",e.getMessage());
			// browser.API_DONE=true;
			// }
			// }
			// browser.destroy();
			// }else{
			// U.e("API_ERROR","JavaScript initialization timeout, may be
			// required device restart",browser);
			// }
		}

		@Override
		public void onTaskCompleted(Object object) {
			ready = true;
			synchronized (monitor) {
				monitor.notify();
			}
		}
	}

	public String LoadData(String inFile) {
		String tContents = "";

		try {
			InputStream stream = getAssets().open(inFile);

			int size = stream.available();
			byte[] buffer = new byte[size];
			stream.read(buffer);
			stream.close();
			tContents = new String(buffer);
		} catch (IOException e) {
			// Handle exceptions here
		}

		return tContents;

	}

}
