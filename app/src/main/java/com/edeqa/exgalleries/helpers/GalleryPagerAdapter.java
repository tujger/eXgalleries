package com.edeqa.exgalleries.helpers;

import java.util.Date;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ViewSwitcher;
import in.srain.cube.views.GridViewWithHeaderAndFooter;
import com.edeqa.exgalleries.R;

// Adapter
public class GalleryPagerAdapter extends PagerAdapter {

	private GridViewWithHeaderAndFooter mGridView;

	private final Context context;
	private final ItemAdapter ga;
  private ItemAdapter ia;

	public GalleryPagerAdapter(Context context, long parentId) {
		this.context = context;

		ga = ItemAdapter.getInstance(context, parentId);
		ga.setSelectionTerms(new SelectionSlidingTab(parentId));
		ga.setView(R.layout.item_gallery);

	}


	/**
	 * Return the number of pages to display
	 */
	@Override
	public int getCount() {
		return ga.getCount();
		// return ia.getAllEntries(mLibraryId).getCount();
	}

	/**
	 * Return true if the value returned from is the same object as the View
	 * added to the ViewPager.
	 */
	@Override
	public boolean isViewFromObject(View view, java.lang.Object o) {
		return o == view;
	}

	/**
	 * Return the title of the item at position. This is important as what this
	 * method returns is what is displayed in the SlidingTabLayout.
	 */
	@Override
	public CharSequence getPageTitle(int position) {
		String str = ga.getItem(position).getTitle();
		if (str.length() > 50)
			str = str.substring(0, 49) + "…";
		// ga.updateItem(ga.getItem(position).setAccessDate(new Date()));
		return str;
	}

	/**
	 * Instantiate the View which should be displayed at position. Here we
	 * inflate a layout from the apps resources and then change the text view to
	 * signify the position.
	 */
	@Override
	public View instantiateItem(ViewGroup container, int position) {

		// Inflate a new layout from our resources
		View view = ((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE))
				.inflate(R.layout.pager_gallery, container, false);

		ga.setHeader(true);
		ViewSwitcher header = (ViewSwitcher) ga.getView(position, ((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE))
                .inflate(R.layout.item_gallery, container, false),
				null);

		// ViewSwitcher header = (ViewSwitcher) ((LayoutInflater) context
		// .getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.pager_gallery_header,
		// container,
		// false);
		long parentId = ga.getItemId(position);
		ia = ItemAdapter.getInstance(context, parentId);
		ia.setActiveParent(parentId);
		ia.setSelectionTerms(new SelectionAlbum(parentId));
		ia.setHeader(false);
		// ia.setSelection(new SelectionAll());
		// ia.setSelection(new SelectionGalleries(mLibraryId));
		ia.setView(R.layout.item_picture);

		mGridView = view.findViewById(android.R.id.list);
		mGridView.addHeaderView(header);
		mGridView.setColumnWidth(EntityAdapter.getThumbOptimalSize());
		// if(ia.getPosition()>0){
		mGridView.setSelection(ia.getPosition());
		// }
		// mGridView.setEmptyView(view.findViewById(android.R.id.empty));

		// String[] values = new String[] { "Android List View "+position,
		// "Adapter implementation",
		// "Simple List View In Android",
		// "Create List View Android",
		// };
		//
		//// Define a new Adapter
		//// First parameter - Context
		//// Second parameter - Layout for the row
		//// Third parameter - ID of the TextView to which the data is written
		//// Forth - the Array of data
		//
		// ArrayAdapter<String> adapter = new ArrayAdapter<String>(context,
		// android.R.layout.simple_list_item_1, android.R.id.text1, values);
		//

		mGridView.setAdapter(ia);
		container.addView(view);


		return view;
	}

	/**
	 * Destroy the item from the ViewPager. In our case this is simply removing
	 * the View.
	 */
	@Override
	public void destroyItem(ViewGroup container, int position, java.lang.Object object) {
		// container.removeViewAt(position);
		container.removeView((View) object);

	}

	public void setViewed(int position) {
		ga.updateItem(ga.getItem(position).setAccessDate(new Date()));
		/*
		 * if (position > 1) {
		 * ItemAdapter.getInstance(0).release(ga.getItemId(position - 2)); } if
		 * (ga.getCount() > position + 2) {
		 * ItemAdapter.getInstance(0).release(ga.getItemId(position + 2)); }
		 */
	}

	// @Override
	// public boolean handleMessage(Message msg) {
	// // TODO Auto-generated method stub
	// this.notifyDataSetChanged();
	// return false;
	// }

}
