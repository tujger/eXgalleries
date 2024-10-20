package com.edeqa.exgalleries;

import java.util.ArrayList;

import com.squareup.picasso.Picasso;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.edeqa.exgalleries.helpers.Item;
import com.edeqa.exgalleries.helpers.ItemAdapter;
import com.edeqa.exgalleries.helpers.Library;
import com.edeqa.exgalleries.helpers.SelectionLibraries;

/**
 * Fragment used for managing interactions for and presentation of a navigation
 * drawer. See the <a href=
 * "https://developer.android.com/design/patterns/navigation-drawer.html#Interaction">
 * design guidelines</a> for a complete explanation of the behaviors implemented
 * here.
 */
@SuppressWarnings("deprecation")
public class NavigationDrawerFragment extends Fragment {

	/**
	 * Remember the position of the selected item.
	 */
	public static final String STATE_SELECTED_POSITION = "selected_navigation_drawer_position";

	/**
	 * Per the design guidelines, you should show the drawer on launch until the
	 * user manually expands it. This shared preference tracks this.
	 */
	private static final String PREF_USER_LEARNED_DRAWER = "navigation_drawer_learned";

	/**
	 * A pointer to the current callbacks instance (the Activity).
	 */
	private NavigationDrawerCallbacks mCallbacks;

	/**
	 * Helper component that ties the action bar to the navigation drawer.
	 */
	private ActionBarDrawerToggle mDrawerToggle;

	private DrawerLayout mDrawerLayout;
	private RelativeLayout mDrawerHeader;
	private ListView mDrawerListView;
	private View mFragmentContainerView;
	private ItemAdapter la;

	private long mCurrentSelectedPosition = 0;
	private boolean mFromSavedInstanceState;
	private boolean mUserLearnedDrawer;

	public NavigationDrawerFragment() {
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Read in the flag indicating whether or not the user has demonstrated
		// awareness of the
		// drawer. See PREF_USER_LEARNED_DRAWER for details.
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
		mUserLearnedDrawer = sp.getBoolean(PREF_USER_LEARNED_DRAWER, false);

		if (savedInstanceState != null) {
			mCurrentSelectedPosition = savedInstanceState.getLong(STATE_SELECTED_POSITION);
			mFromSavedInstanceState = true;
		}

		la = ItemAdapter.getInstance(getActivity(), 0);

	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		// Indicate that this fragment would like to influence the set of
		// actions in the action bar.
		setHasOptionsMenu(true);

		// setDrawerIndicatorEnabled(true);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		// LinearLayout
		// mDrawerView=(LinearLayout)inflater.inflate(R.layout.fragment_navigation_drawer,
		// container, false);

		mDrawerListView = (ListView) inflater.inflate(R.layout.fragment_navigation_drawer, container, false);

		la.setSelectionTerms(new SelectionLibraries());

		ArrayList<String> list = la.getTitles();
		list.add(0, getString(R.string.home));
		String[] str = list.size() > 0 ? list.toArray(new String[list.size()])
				: new String[] { getString(R.string.home) };

		mDrawerListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				if(position>1)
					selectItem(la.getItemId(position-2));
				else
					selectItem(-1);
			}
		});

		mDrawerHeader = (RelativeLayout) inflater.inflate(R.layout.fragment_navigation_drawer_header, container, false);
		mDrawerListView.addHeaderView(mDrawerHeader);

//TODO		if (mCurrentSelectedPosition > str.length)
//			mCurrentSelectedPosition = str.length;

		RelativeLayout drawerFooter = (RelativeLayout) inflater.inflate(R.layout.fragment_navigation_drawer_footer,
				container, false);
		drawerFooter.findViewById(R.id.bExit).setOnClickListener(buttonClickListener);
		drawerFooter.findViewById(R.id.bAbout).setOnClickListener(buttonClickListener);
		drawerFooter.findViewById(R.id.bSettings).setOnClickListener(buttonClickListener);
		mDrawerListView.addFooterView(drawerFooter);

		// la.setView(R.layout.drawer_list_item);
		// mDrawerListView.setAdapter(la);
		mDrawerListView.setAdapter(
				new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, android.R.id.text1, str));

//TODO		mDrawerListView.setItemChecked(mCurrentSelectedPosition, true);

		return mDrawerListView;
	}

	public boolean isDrawerOpen() {
		return mDrawerLayout != null && mDrawerLayout.isDrawerOpen(mFragmentContainerView);
	}

	/**
	 * Users of this fragment must call this method to set up the navigation
	 * drawer interactions.
	 *
	 * @param fragmentId
	 *            The android:id of this fragment in its activity's layout.
	 * @param drawerLayout
	 *            The DrawerLayout containing this fragment's UI.
	 */
	public void setUp(int fragmentId, DrawerLayout drawerLayout) {
		mFragmentContainerView = getActivity().findViewById(fragmentId);
		mDrawerLayout = drawerLayout;

		// set a custom shadow that overlays the main content when the drawer
		// opens
		mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
		// set up the drawer's list view with items and click listener

		ActionBar actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setHomeButtonEnabled(true);

		// ActionBarDrawerToggle ties together the the proper interactions
		// between the navigation drawer and the action bar app icon.
		mDrawerToggle = new ActionBarDrawerToggle(
				getActivity(), /* host Activity */
				mDrawerLayout, /* DrawerLayout object */
				R.drawable.ic_drawer, /*
										 * nav drawer image to replace 'Up'
										 * caret
										 */
				R.string.navigation_drawer_open, /*
													 * "open drawer" description
													 * for accessibility
													 */
				R.string.navigation_drawer_close /*
													 * "close drawer"
													 * description for
													 * accessibility
													 */
		) {
			@Override
			public void onDrawerClosed(View drawerView) {
				super.onDrawerClosed(drawerView);
				if (!isAdded()) {
					return;
				}

				getActivity().invalidateOptionsMenu(); // calls
														// onPrepareOptionsMenu()
			}

			@Override
			public void onDrawerOpened(View drawerView) {
				super.onDrawerOpened(drawerView);
				if (!isAdded()) {
					return;
				}

				if (!mUserLearnedDrawer) {
					// The user manually opened the drawer; store this flag to
					// prevent auto-showing
					// the navigation drawer automatically in the future.
					mUserLearnedDrawer = true;
					SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
					sp.edit().putBoolean(PREF_USER_LEARNED_DRAWER, true).apply();
				}

				ImageView iv = mDrawerHeader.findViewById(R.id.ivThumb);
				// iv.setImageBitmap(bm);
				// mDrawerHeader.setLayoutParams(new
				// LayoutParams(LayoutParams.MATCH_PARENT, 150));
				// iv.setLayoutParams(new
				// LayoutParams(LayoutParams.MATCH_PARENT,
				// LayoutParams.WRAP_CONTENT));
				iv.setScaleType(ImageView.ScaleType.CENTER_CROP);

				if (mCurrentSelectedPosition > 0) {
					Item lib = (Item) la.getItem(mCurrentSelectedPosition);
					int width = (int) getActivity().getResources().getDimension(R.dimen.navigation_drawer_width);
					int height = (int) getActivity().getResources().getDimension(R.dimen.navigation_drawer_height);

					switch (Library.getThumbType(lib)) {
					case 0:
						final ProgressBar progressBar = mDrawerHeader.findViewById(R.id.pbThumb);
						progressBar.setVisibility(View.VISIBLE);

						Picasso.get().load(lib.getImageLink()).resize(width, height)
								.error(R.drawable.ic_warning_black_48dp).centerCrop()
								.into(iv, new com.squareup.picasso.Callback() {
							@Override
							public void onSuccess() {
								progressBar.setVisibility(View.INVISIBLE);
							}

							@Override
							public void onError(Exception e) {
								System.err.println(e);
								progressBar.setVisibility(View.INVISIBLE);

							}
						});

						// }
						break;
					case 1:
						Picasso.get().load(R.drawable.icon_image_grey).into(iv);
						break;
					case 2:
						//todo
						// iv.setRotation(180);
						break;
					}

					((TextView) mDrawerHeader.findViewById(R.id.tvTitle)).setText(lib.getTitle());
					((TextView) mDrawerHeader.findViewById(R.id.tvLink)).setText(lib.getLink());
					mDrawerHeader.findViewById(R.id.tvLink).setVisibility(View.VISIBLE);
//					int cnt;
//					if ((cnt = la.getAllIds(lib.getId()).length) > 0) {
//						((TextView) mDrawerHeader.findViewById(R.id.tvCount)).setText(cnt+"");
//						((TextView) mDrawerHeader.findViewById(R.id.tvCount)).setVisibility(View.VISIBLE);
//					} else
//						((TextView) mDrawerHeader.findViewById(R.id.tvCount)).setVisibility(View.INVISIBLE);
//					if (lib.getUpdateDate() != null) {
//						((TextView) mDrawerHeader.findViewById(R.id.tvUpdated))
//								.setText(lib.getFormattedUpdateDate(Library.DATEFORMAT_ADAPTIVE));
//						((TextView) mDrawerHeader.findViewById(R.id.tvUpdated)).setVisibility(View.VISIBLE);
//					} else
//						((TextView) mDrawerHeader.findViewById(R.id.tvUpdated)).setVisibility(View.INVISIBLE);
//					((RelativeLayout) mDrawerHeader.findViewById(R.id.rlBottom)).setVisibility(View.VISIBLE);
				} else {
					// iv.setImageResource(R.drawable.drawer_header);
					int width = (int) getActivity().getResources().getDimension(R.dimen.navigation_drawer_width);
					int height = (int) getActivity().getResources().getDimension(R.dimen.navigation_drawer_height);

					final ProgressBar progressBar = mDrawerHeader.findViewById(R.id.pbThumb);
					progressBar.setVisibility(View.VISIBLE);

					Picasso.get().load(R.drawable.drawer_header).resize(width, height)
							.error(R.drawable.ic_warning_black_48dp).centerCrop()
							.into(iv, new com.squareup.picasso.Callback() {
						@Override
						public void onSuccess() {
							progressBar.setVisibility(View.INVISIBLE);
						}

						@Override
						public void onError(Exception e) {
							System.err.println(e);
							progressBar.setVisibility(View.INVISIBLE);

						}
					});

					((TextView) mDrawerHeader.findViewById(R.id.tvTitle)).setText(getString(R.string.app_name));
					mDrawerHeader.findViewById(R.id.rlBottom).setVisibility(View.INVISIBLE);
					mDrawerHeader.findViewById(R.id.tvLink).setVisibility(View.GONE);
				}

				getActivity().invalidateOptionsMenu(); // calls
														// onPrepareOptionsMenu()
			}
		};

		// If the user hasn't 'learned' about the drawer, open it to introduce
		// them to the drawer,
		// per the navigation drawer design guidelines.
		if (!mUserLearnedDrawer && !mFromSavedInstanceState) {
			mDrawerLayout.openDrawer(mFragmentContainerView);
		}

		// Defer code dependent on restoration of previous instance state.
		mDrawerLayout.post(new Runnable() {
			@Override
			public void run() {
				mDrawerToggle.syncState();
			}
		});

		mDrawerLayout.setDrawerListener(mDrawerToggle);
	}

	public void selectItem(long id) {
		mCurrentSelectedPosition = id;
		if (mDrawerListView != null) {
//TODO			mDrawerListView.setItemChecked(position, true);
		}
		if (mDrawerLayout != null) {
			mDrawerLayout.closeDrawer(mFragmentContainerView);
		}
		if (mCallbacks != null) {
			mCallbacks.onNavigationDrawerItemSelected(id,0,0);
		}
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
			mCallbacks = (NavigationDrawerCallbacks) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException("Activity must implement NavigationDrawerCallbacks.");
		}
	}

	@Override
	public void onDetach() {
		super.onDetach();
		mCallbacks = null;
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putLong(STATE_SELECTED_POSITION, mCurrentSelectedPosition);
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		// Forward the new configuration the drawer toggle component.
		mDrawerToggle.onConfigurationChanged(newConfig);
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		// If the drawer is open, show the global app actions in the action bar.
		// See also
		// showGlobalContextActionBar, which controls the top-left area of the
		// action bar.
		// if (mDrawerLayout != null && isDrawerOpen()) {
		// inflater.inflate(R.menu.global, menu);
		// showGlobalContextActionBar();
		// }
		super.onCreateOptionsMenu(menu, inflater);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (mDrawerToggle.onOptionsItemSelected(item)) {
			return true;
		}

		if (item.getItemId() == R.id.action_update) {
			// Toast.makeText(getActivity(), R.string.refresh,
			// Toast.LENGTH_SHORT).show();
			return true;
		}

		return super.onOptionsItemSelected(item);
	}

	/**
	 * Per the navigation drawer design guidelines, updates the action bar to
	 * show the global app 'context', rather than just what's in the current
	 * screen.
	 */
	@SuppressWarnings("unused")
	private void showGlobalContextActionBar() {
		ActionBar actionBar = getActionBar();
		actionBar.setDisplayShowTitleEnabled(true);
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
		actionBar.setTitle(R.string.app_name);
	}

	private ActionBar getActionBar() {
		return getActivity().getActionBar();
	}

	/**
	 * Callbacks interface that all activities using this fragment must
	 * implement.
	 */
	public interface NavigationDrawerCallbacks {
		/**
		 * Called when an item in the navigation drawer is selected.
		 */
		void onNavigationDrawerItemSelected(long topId,long gridId,long scrollToId);

	}

	View.OnClickListener buttonClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {

			switch (v.getId()) {
			case R.id.bExit:
				getActivity().finish();
				break;
			case R.id.bSettings:
				Main.getMainActivity().onCallback("options", 0);
				break;
			case R.id.bAbout:
//				Toast.makeText(getActivity(), "About", Toast.LENGTH_SHORT).show();
				break;
			}

		}
	};

	public void closeDrawer() {
		mDrawerLayout.closeDrawers();
	}

}
