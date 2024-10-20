package com.edeqa.exgalleries;

import android.app.Fragment;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;
import com.edeqa.exgalleries.helpers.GalleryPagerAdapter;
import com.edeqa.exgalleries.helpers.ItemAdapter;
import com.edeqa.exgalleries.helpers.SelectionTerms;
import com.edeqa.exgalleries.helpers.SlidingTabLayout;

public class GalleryListFragment extends Fragment implements GridView.OnItemClickListener {

	// private static final String STATE_ACTIVE_ID = "selected_library_id";
	// private static final String STATE_SELECTED_POSITION =
	// "selected_position";

	private SlidingTabLayout mSlidingTabLayout;
	private GalleryPagerAdapter gpa;
	private static ViewPager mViewPager;
	private long activeId = 0;
	private long currentId = 0;
	private long scrollToId = 0;

	public GalleryListFragment(long activeId, long currentId,long scrollToId) {

		setActiveId(activeId);
		setPositionId(currentId);
		setScrollToId(scrollToId);
	}

	public void setPositionId(long currentId) {
		this.currentId = currentId;

	}
	
	public void setScrollToId(long scrollToId) {
		this.scrollToId = scrollToId;
	}

	public long getPositionId() {
		if (mViewPager != null) {
			return ItemAdapter.getInstance(getActivity(), activeId).getItemId(mViewPager.getCurrentItem());
		}
		return 0;
	}

	public long getScrollToId() {
		return ItemAdapter.getInstance(getActivity(), currentId).getIdOfCurrentPosition();
	}
	
	public GalleryListFragment() {
	}

	public long getActiveId() {
		if (mViewPager == null)
			return 0L;
		else
			// return
			// ItemAdapter.getInstance(activeId).getItemId(mViewPager.getCurrentItem());
			return activeId;
		// return mViewPager.getCurrentItem();
		// mViewPager.setCurrentItem(mPosition);
		// return mLibraryId;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// if (savedInstanceState != null) {
		// setPosition(savedInstanceState.getInt(STATE_SELECTED_POSITION));
		// }
		setHasOptionsMenu(true);
	}

	@Override
	public void onResume() {
		// ItemAdapter.getInstance(getActiveId());

		// ItemAdapter.getInstance(activeId).testAllEntries(activeId);

		super.onResume();
	}

	@Override
	public void onStop() {
		// ItemAdapter.getInstance(getActivity(), getActiveId());
		super.onResume();
	}

	@Override
	public void onDestroy() {
		mViewPager = null;
		super.onDestroy();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		View rootView = inflater.inflate(R.layout.fragment_gallery_list, container, false);

		return rootView;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		mViewPager = (ViewPager) view.findViewById(R.id.viewpager);

		gpa = new GalleryPagerAdapter(getActivity(), activeId);


		// if (ItemAdapter.getInstance(activeId).findPositionById(currentId) >
		// gpa.getCount() - 1)
		// ItemAdapter.getInstance(activeId).position = gpa.getCount() - 1;

		mViewPager.setAdapter(gpa);
		// mViewPager.setCurrentItem(ItemAdapter.getInstance(activeId).findPositionById(getActiveId()));

		if (currentId > 0) {
			mViewPager.setCurrentItem(ItemAdapter.getInstance(getActivity(), activeId).findPositionById(currentId));
			ItemAdapter.getInstance(getActivity(), currentId).setPositionById(scrollToId);
//			System.out.println("SETPOS="+ItemAdapter.getInstance(activeId).findPositionById(currentId));
//			ItemAdapter.getInstance(ItemAdapter.getInstance(activeId).findPositionById(currentId)).setPositionById(scrollToId);
		}

		mViewPager.addOnPageChangeListener(new OnPageChangeListener() {

			@Override
			public void onPageSelected(int arg0) {
				gpa.setViewed(arg0);
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
			}

			@Override
			public void onPageScrollStateChanged(int arg0) {
			}
		});

		mSlidingTabLayout = (SlidingTabLayout) view.findViewById(R.id.sliding_tabs);
		mSlidingTabLayout.setViewPager(mViewPager);

	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		// System.out.println("SAVESTATE");
		// activeId=ItemAdapter.getInstance(activeId).getItemId(mViewPager.getCurrentItem());
		// outState.putLong(STATE_ACTIVE_ID, getActiveId());
		// outState.putInt(STATE_SELECTED_POSITION,
		// mViewPager.getCurrentItem());
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		// Toast.makeText(getActivity(), "POSITION="+position,
		// Toast.LENGTH_SHORT).show();
	}

	public void setActiveId(long activeId) {
		this.activeId = activeId;
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		getActivity().getMenuInflater().inflate(R.menu.gallery_main, menu);

		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
		int mode = sp.getInt("mode", SelectionTerms.SHOW_ALL);
		if (mode == SelectionTerms.SHOW_ALL) {
			menu.findItem(R.id.action_mode).setIcon(R.drawable.ic_collections_white_48dp);
		} else if (mode == SelectionTerms.SHOW_ONLY_NEW) {
			menu.findItem(R.id.action_mode).setIcon(R.drawable.ic_new_releases_white_48dp);
		} else if (mode == SelectionTerms.SHOW_ONLY_OLD) {
			menu.findItem(R.id.action_mode).setIcon(R.drawable.ic_photo_white_48dp);
		} else if (mode == SelectionTerms.SHOW_ONLY_RATED) {
			menu.findItem(R.id.action_mode).setIcon(R.drawable.ic_favorite_white_48dp);
		}

		super.onCreateOptionsMenu(menu, inflater);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		switch (id) {
		case R.id.action_mode:

			SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
			int mode = sp.getInt("mode", SelectionTerms.SHOW_ALL);

			mode++;
			if (mode > 3)
				mode = 0;

			sp.edit().putInt("mode", mode).commit();

			String text = null;
			if (mode == SelectionTerms.SHOW_ALL) {
				text = "Shows all";
				item.setIcon(R.drawable.ic_collections_white_48dp);
			} else if (mode == SelectionTerms.SHOW_ONLY_NEW) {
				text = "Shows only new";
				item.setIcon(R.drawable.ic_new_releases_white_48dp);
			} else if (mode == SelectionTerms.SHOW_ONLY_OLD) {
				text = "Shows only old";
				item.setIcon(R.drawable.ic_photo_white_48dp);
			} else if (mode == SelectionTerms.SHOW_ONLY_RATED) {
				text = "Shows only rated";
				item.setIcon(R.drawable.ic_favorite_white_48dp);
			}

			Toast.makeText(getActivity(), text, Toast.LENGTH_SHORT).show();

			// ItemAdapter la = getInstance(item.getParentId());
			Main.getMainActivity().onNavigationDrawerItemSelected(getActiveId(), getPositionId(), getScrollToId());

			//// ItemAdapter.getInstance(getActiveId()).refresh();
			// ItemAdapter.getInstance(ItemAdapter.getInstance(getActiveId()).getItem(getPosition()).getId()).notifyDataSetChanged();
			//

			gpa.notifyDataSetChanged();

			break;
		}
		return super.onOptionsItemSelected(item);
	}

}