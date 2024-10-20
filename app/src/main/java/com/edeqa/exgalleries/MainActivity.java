package com.edeqa.exgalleries;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentManager.BackStackEntry;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.widget.Toast;

import com.edeqa.exgalleries.HomePageFragment.OnFragmentInteractionListener;
import com.edeqa.exgalleries.helpers.ItemAdapter;
import com.edeqa.exgalleries.helpers.LibraryParser;
import com.edeqa.exgalleries.helpers.ReadFileTask;
import com.edeqa.exgalleries.helpers.SelectionLibraries;
import com.edeqa.exgalleries.helpers.TextFileFromUri;
import com.edeqa.exgalleries.interfaces.Callback;
import com.edeqa.exgalleries.interfaces.TaskCompletedInterface;

public class MainActivity extends Activity implements NavigationDrawerFragment.NavigationDrawerCallbacks,
		OnFragmentInteractionListener, TaskCompletedInterface, Callback {

	private static final String STATE_ACTIVE_ID = "active_id";
	private static final String STATE_POSITION = "active_position";
	private static final String STATE_SCROLL_TO = "active_thumb";

	/**
	 * Fragment managing the behaviors, interactions and presentation of the
	 * navigation drawer.
	 */
	private NavigationDrawerFragment mNavigationDrawerFragment;

	/**
	 * Used to store the last screen title. For use in
	 * {@link #restoreActionBar()}.
	 */
	private CharSequence mTitle;
	static final int FOR_OPENFILE = 1;
	static final int FOR_ADDLIBRARY = 2;
	static final String CURRENT_SELECTED_POSITION = "current_selected_position";
	public final static String BROADCAST_RECEIVER = "com.edeqa.exgalleries.receiver";
	public final static String BROADCAST_ACTION = "service_status";
	public final static String BROADCAST_SERVICE_DONE = "done";
	public final static String BROADCAST_SERVICE_WORKING = "working";

	private ProgressDialog progressDialog;
	private ItemAdapter la;
	private BroadcastReceiver receiver;
	PendingIntent pendingIntent;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		long topId = 0, activeId = 0, scrollTo = 0;

		if (savedInstanceState != null) {
			topId = savedInstanceState.getLong(STATE_ACTIVE_ID);
			activeId = savedInstanceState.getLong(STATE_POSITION);
			scrollTo = savedInstanceState.getLong(STATE_SCROLL_TO);
			// GalleryListFragment.setActiveId(topId);
			// GalleryListFragment.setPositionId(activeId);
		}

		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		setContentView(R.layout.activity_main);

		Main.setMainActivity(this);
		// Main.setCurrentActivity(this);

		la = ItemAdapter.getInstance(this, 0);

		mNavigationDrawerFragment = (NavigationDrawerFragment) getFragmentManager()
				.findFragmentById(R.id.navigation_drawer);
		mTitle = getTitle();

		receiver = new BroadcastReceiver() {
			public void onReceive(Context context, Intent intent) {
				String status = intent.getStringExtra(BROADCAST_ACTION);
				if (BROADCAST_SERVICE_DONE.equals(status)) {
					setProgressBarIndeterminateVisibility(false);
					Main.getMenuItem(R.id.action_update).setIcon(R.drawable.ic_refresh_white_48dp);
					Main.getMenuItem(R.id.action_update).setTitle(R.string.update);
				} else if (BROADCAST_SERVICE_WORKING.equals(status)) {
					setProgressBarIndeterminateVisibility(true);
					Main.getMenuItem(R.id.action_update).setIcon(R.drawable.ic_close_white_48dp);
					Main.getMenuItem(R.id.action_update).setTitle(R.string.stop);
				}
			}
		};
		IntentFilter filter = new IntentFilter(BROADCAST_RECEIVER);
		registerReceiver(receiver, filter);

		mNavigationDrawerFragment.setUp(R.id.navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout));

		if (getFragmentManager().getBackStackEntryCount() == 0) {
			onNavigationDrawerItemSelected(0, 0, 0);
		} else {
			onNavigationDrawerItemSelected(topId, activeId, scrollTo);
		}
	}

	@Override
	public void onNavigationDrawerItemSelected(long topId, long gridId, long scrollToId) {
		Fragment fragment = null;

		la.release(topId);

		la = ItemAdapter.getInstance(this, 0);
		la.setSelectionTerms(new SelectionLibraries());
		if (la.isEmpty() || topId <= 0) {
			mTitle = getString(R.string.app_name);
			fragment = new HomePageFragment();
		} else {
			mTitle = la.getItem(topId).getTitle();
			fragment = new GalleryListFragment(topId, gridId, scrollToId);
		}

		restoreActionBar();

		if (fragment != null) {
			FragmentManager fragmentManager = getFragmentManager();
			fragmentManager.beginTransaction().replace(R.id.container, fragment, mTitle.toString())
					.addToBackStack(mTitle.toString()).commit();
		}

	}

	/*
	 * public void onSectionAttached(int number) { switch (number) { case 0:
	 * mTitle = getString(R.string.title_section1); break; case 2: mTitle =
	 * getString(R.string.title_section2); break; case 3: mTitle =
	 * getString(R.string.title_section3); break; } }
	 */
	@SuppressWarnings("deprecation")
	public void restoreActionBar() {
		ActionBar actionBar = getActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
		actionBar.setDisplayShowTitleEnabled(true);
		actionBar.setTitle(mTitle);
	}

	@Override
	protected void onResume() {

		// checking for service started or not
		Intent intent = new Intent(this, ExGalleriesService.class);
		intent.setAction(TextUtils.join(":", new String[] { ExGalleriesService.ACTION_CHECK, "0" }));
		startService(intent);

		super.onResume();
	}

	@Override
	protected void onStart() {

		// checking for service started or not
		Intent intent = new Intent(this, ExGalleriesService.class);
		intent.setAction(TextUtils.join(":", new String[] { ExGalleriesService.ACTION_CHECK, "0" }));
		startService(intent);

		super.onStart();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		if (!mNavigationDrawerFragment.isDrawerOpen()) {
			getMenuInflater().inflate(R.menu.main, menu);
			Main.setMenuItem(R.id.action_update, menu.findItem(R.id.action_update));

			restoreActionBar();
			return true;
		}
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		switch (id) {
		case R.id.action_settings:
			return true;
		case R.id.action_add_library:
			addLibrary();
			return true;
		case R.id.action_update:
			Intent intent = new Intent(this, ExGalleriesService.class);

			long id1 = 0;

			if (getFragmentManager().getBackStackEntryCount() > 1) {

				BackStackEntry entry = getFragmentManager()
						.getBackStackEntryAt(getFragmentManager().getBackStackEntryCount() - 1);
				GalleryListFragment glf = (GalleryListFragment) getFragmentManager().findFragmentByTag(entry.getName());
				id1 = glf.getActiveId();

			}
			// if (GalleryListFragment.getActiveId() > 0)
			// id1 = GalleryListFragment.getActiveId();
			intent.setAction(TextUtils.join(":", new String[] { ExGalleriesService.ACTION_REFRESH, id1 + "" }));
			startService(intent);
			break;
		// case R.id.action_stop:
		// stopService(new Intent(this, ExGalleriesService.class));
		// break;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onFragmentInteraction(String id) {
		mNavigationDrawerFragment.selectItem(Long.valueOf(id));
	}

	private void addLibrary() {
		Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
		intent.addCategory(Intent.CATEGORY_OPENABLE);
		intent.setType("*/*");
		startActivityForResult(intent, FOR_OPENFILE);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case FOR_OPENFILE:
			if (resultCode == RESULT_OK) {
				TextFileFromUri readFile = null;
				ReadFileTask readFileTask = new ReadFileTask(this, data.getData(), readFile);
				readFileTask.execute();
			}
			break;
		case FOR_ADDLIBRARY:
			la.refresh();
			break;
		}

		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	public void onTaskCompleted(Object object) {
		/*
		 * Showing Library properties dialog
		 */
		if (object instanceof TextFileFromUri) {
			TextFileFromUri fileFromUri = (TextFileFromUri) object;
			if (!fileFromUri.isSuccess()) {
				Toast.makeText(this, fileFromUri.getErrorMessage(), Toast.LENGTH_SHORT).show();
				return;
			}

			ProgressDialog dialog = new ProgressDialog(this);
			dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			dialog.setMessage(getString(R.string.please_wait));
			dialog.setIndeterminate(true);
			dialog.setCanceledOnTouchOutside(false);
			dialog.show();

			LibraryParser lp = new LibraryParser(((TextFileFromUri) fileFromUri).getStringBuffer());

			if (dialog.isShowing())
				dialog.dismiss();

			if (!lp.isSuccess()) {
				Toast.makeText(this, R.string.file_is_not_a_library, Toast.LENGTH_SHORT).show();
				return;
			}
			Intent intent = new Intent(this, AddLibraryActivity.class);
			intent.putExtra("LibraryParsed", lp);
			startActivityForResult(intent, FOR_ADDLIBRARY);
		}
	}

	@Override
	protected void onDestroy() {
		unregisterReceiver(receiver);

		la = null;
		ItemAdapter.clear();
		Main.setMainActivity(null);
		Main.setMenuItem(R.id.action_update, null);
		// Main.setStarted(false);
		super.onDestroy();
	}

	@Override
	public void onCallback(String string, int value) {
		if ("options".equals(string)) {
			Fragment fragment = null;
			String name;

			if (value > 0) {
				fragment = new LibraryOptionsFragment((long) value);
				name = "library_options";
			} else {
				fragment = new OptionsFragment();
				mNavigationDrawerFragment.closeDrawer();
				name = "options";
			}

			if (fragment != null) {
				FragmentManager fragmentManager = getFragmentManager();
				fragmentManager.beginTransaction().replace(R.id.container, fragment, name).addToBackStack(name)
						.commit();

			}
		}

	}

	@Override
	public void onBackPressed() {
		if (mNavigationDrawerFragment.isDrawerOpen()) {

			mNavigationDrawerFragment.closeDrawer();
			return;
		} else {
			if (getFragmentManager().getBackStackEntryCount() > 1) {

				BackStackEntry entry = getFragmentManager()
						.getBackStackEntryAt(getFragmentManager().getBackStackEntryCount() - 1);
				while (entry.getName().equals(getFragmentManager()
						.getBackStackEntryAt(getFragmentManager().getBackStackEntryCount() - 1).getName())) {
					getFragmentManager().popBackStackImmediate();
				}
				getFragmentManager().beginTransaction().commit();

				entry = getFragmentManager().getBackStackEntryAt(getFragmentManager().getBackStackEntryCount() - 1);

				if (getFragmentManager().findFragmentByTag(entry.getName()) instanceof GalleryListFragment) {
					GalleryListFragment glf = (GalleryListFragment) getFragmentManager()
							.findFragmentByTag(entry.getName());
					long id = glf.getActiveId();
					if (id > 0) {
						mTitle = la.getItem(id).getTitle();
					}
				} else {
					mTitle = getString(R.string.app_name);
				}
				restoreActionBar();

			} else {
				finish();
				return;
				// super.onBackPressed();
			}

			onResume();
			return;
		}
		// super.onBackPressed();
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);

		BackStackEntry entry = getFragmentManager()
				.getBackStackEntryAt(getFragmentManager().getBackStackEntryCount() - 1);

		if (getFragmentManager().findFragmentByTag(entry.getName()) instanceof GalleryListFragment) {
			GalleryListFragment glf = (GalleryListFragment) getFragmentManager().findFragmentByTag(entry.getName());

			outState.putLong(STATE_ACTIVE_ID, glf.getActiveId());
			outState.putLong(STATE_POSITION, glf.getPositionId());
			outState.putLong(STATE_SCROLL_TO, glf.getScrollToId());
		}
	}

	public void showProgressDialog() {

		if (progressDialog == null) {
			progressDialog = new ProgressDialog(this);
			progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			progressDialog.setMessage(getString(R.string.please_wait));
			progressDialog.setIndeterminate(true);
			progressDialog.setCanceledOnTouchOutside(false);
			progressDialog.setCancelable(false);
		}
		if (!progressDialog.isShowing()) {
			progressDialog.show();
		}
	}

	public void dismissProgressDialog() {
		if (progressDialog != null) {
			progressDialog.dismiss();
		}
	}

}
