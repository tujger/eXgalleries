package com.edeqa.exgalleries.helpers;

import com.squareup.picasso.Picasso;

import android.content.Context;
import android.content.Intent;
import android.preference.PreferenceManager;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;
import com.edeqa.exgalleries.PictureActivity;
import com.edeqa.exgalleries.R;

public class PicturePagerAdapter extends PagerAdapter {

	private Context ssContext;
	private ItemAdapter ia;
	private PictureActivity callback;

	// private int[] ssImages = new int[] {
	// R.drawable.splash1,R.drawable.splash2, R.drawable.splash3 };

	public PicturePagerAdapter(Context ssContext, Intent intent) {

		this.ssContext = ssContext;

		ia = ItemAdapter.getInstance(ssContext, intent.getExtras().getLong(PictureActivity.CURRENT_GALLERY_ID));

	}

	@Override
	public int getCount() {
		return ia.getCount();
	}

	@Override
	public boolean isViewFromObject(View view, java.lang.Object o) {
		return o == view;
	}

	@Override
	public CharSequence getPageTitle(int position) {
		return ia.getItem(ia.getPosition()).getTitle();
	}

	@Override
	public View instantiateItem(ViewGroup container, int position) {

		// Inflate a new layout from our resources
		final View view = ((LayoutInflater) ssContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE))
				.inflate(R.layout.picture_show, container, false);

		final TouchImageView iv = (TouchImageView) view.findViewById(R.id.ivPicture);
		iv.setMaxZoom(5.f);
		iv.setMinZoom(1.f);
		iv.setZoom(1.f);

		Item pic = (Item) ia.getItem(position);

		// Picasso.with(Main.getContext()).load(pic.getImageCache()).into(iv);

		Picasso.get().load(pic.getImageCache()).error(R.id.rlError).into(iv,
				new com.squareup.picasso.Callback() {
					@Override
					public void onSuccess() {
						// view.findViewById(R.id.pbThumb).setVisibility(View.INVISIBLE);
						view.findViewById(R.id.rlError).setVisibility(View.INVISIBLE);
					}

					@Override
					public void onError(Exception e) {
						System.err.println(e);
						// view.findViewById(R.id.pbThumb).setVisibility(View.INVISIBLE);
						view.findViewById(R.id.rlError).setVisibility(View.VISIBLE);
						TextView tv = (TextView) view.findViewById(R.id.tvError);
						if (tv != null) {
							if (NetworkState.getInstance().isAvailable()) {
								tv.setText(ssContext.getString(R.string.error));
							} else {
								tv.setText(ssContext.getString(R.string.network_error));
							}
							tv.setVisibility(View.VISIBLE);
						}

					}
				});

		if (!pic.isImageCached())
			PictureLoader.saveImage(pic.getImageLink(), pic.getImageCachePath().getAbsolutePath());

		iv.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				PreferenceManager.getDefaultSharedPreferences(ssContext)
						.edit()
						.putBoolean("picture_full_screen", !PreferenceManager
								.getDefaultSharedPreferences(ssContext).getBoolean("picture_full_screen", true))
						.commit();

				callback.onCallback(PictureActivity.TOGGLE_FULLSCREEN, 0);
			}
		});

		container.addView(view);

		return view;
	}

	@Override
	public void destroyItem(ViewGroup container, int position, java.lang.Object object) {
		container.removeView((View) object);
	}

	public void setCallback(Object callback) {
		this.callback = (PictureActivity) callback;
	}

}