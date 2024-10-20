package com.edeqa.exgalleries;

import java.util.Date;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;
import com.edeqa.exgalleries.helpers.Item;
import com.edeqa.exgalleries.helpers.ItemAdapter;
import com.edeqa.exgalleries.helpers.PicturePagerAdapter;
import com.edeqa.exgalleries.helpers.SelectionTerms;

public class PictureActivity extends Activity implements com.edeqa.exgalleries.interfaces.Callback {

	final public static String CURRENT_GALLERY_ID = "current_gallery_id";
	final public static String CURRENT_PICTURE_ID = "current_picture_id";
	final public static String TOGGLE_FULLSCREEN = "toggle_full_screen";

	private ViewPager contentView = null;
	private boolean visible = true;
	private ItemAdapter ia;
	private int prevPosition, prevPageState;
	PicturePagerAdapter ssAdapter = null;
	private View controlsView;
	private SelectionTerms oldTerms;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_picture);

		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setHomeButtonEnabled(true);

		controlsView = findViewById(R.id.rlInfo);
		contentView = findViewById(R.id.vpPicture);

		ssAdapter = new PicturePagerAdapter(this, getIntent());
		ssAdapter.setCallback(this);
		contentView.setAdapter(ssAdapter);

		ia = ItemAdapter.getInstance(this, getIntent().getExtras().getLong(PictureActivity.CURRENT_GALLERY_ID));
		oldTerms = ia.getSelectionTerms();
		// ia.setSelectionTerms(
		// new
		// SelectionPictures(getIntent().getExtras().getLong(PictureActivity.CURRENT_GALLERY_ID)));

		ssAdapter.notifyDataSetChanged();

		prevPosition = ia.findPositionById(getIntent().getExtras().getLong(PictureActivity.CURRENT_PICTURE_ID));
		contentView.setCurrentItem(selected(prevPosition));

		contentView.addOnPageChangeListener(new OnPageChangeListener() {

			@Override
			public void onPageSelected(int position) {

				Item pic = (Item) ia.getItem(position);
				if (pic.isFailed()) {
					int inc = position - prevPosition;
					if (position + inc < 0 || position + inc > ssAdapter.getCount())
						contentView.setCurrentItem(position - inc, true);
					else
						contentView.setCurrentItem(position + inc, true);
				} else {
					selected(position);
					prevPosition = position;
				}
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
			}

			@Override
			public void onPageScrollStateChanged(int arg0) {
				if (arg0 == 0 && prevPageState == 1) {
					finish();
				}
				prevPageState = arg0;
			}
		});

		Button b = controlsView.findViewById(R.id.bLeft);
		b.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (contentView.getCurrentItem() > 0) {
					contentView.setCurrentItem(contentView.getCurrentItem() - 1, true);
				} else {
					finish();
				}
			}
		});

		b = controlsView.findViewById(R.id.bRight);
		b.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (contentView.getCurrentItem() < ssAdapter.getCount() - 1) {
					contentView.setCurrentItem(contentView.getCurrentItem() + 1, true);
				} else {
					finish();
				}

			}
		});

		onCallback("", 0);

	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.picture, menu);

		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {
		case R.id.open_in_web:
			this.startActivity(
					new Intent(Intent.ACTION_VIEW, Uri.parse(ia.getItem(ia.getItemId(prevPosition)).getLink()))
							.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
			break;
		case R.id.remove:
			ia.dismissItem(ia.getItem(ia.getItemId(prevPosition)));
			ssAdapter.notifyDataSetChanged();
			recreate();
			Toast.makeText(this, R.string.picture_removed, Toast.LENGTH_SHORT).show();

			break;
		case android.R.id.home:
		case R.id.close:
			finish();
		}

		return super.onOptionsItemSelected(item);
	}

	@SuppressLint("InlinedApi")
	@Override
	public void onCallback(String value, int val) {

		visible = PreferenceManager.getDefaultSharedPreferences(this).getBoolean("picture_full_screen",
				true);

		View mDecorView = getWindow().getDecorView();
		if (visible) {
			getActionBar().hide();
			findViewById(R.id.rlInfo).setVisibility(View.INVISIBLE);

			mDecorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
					| View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
					| View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_FULLSCREEN
					| View.SYSTEM_UI_FLAG_LOW_PROFILE | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);

		} else {
			getActionBar().show();
			findViewById(R.id.rlInfo).setVisibility(View.VISIBLE);

			mDecorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
		}
		// visible = !visible;

	}

	private int selected(int position) {

		final Item pic = (Item) ia.getItem(position);

		setTitle("(" + (position + 1) + "/" + ia.getCount() + ") " + pic.getTitle());

		if (pic.getDescription().length() > 0) {
			((TextView) controlsView.findViewById(R.id.tvDescription)).setText(Html.fromHtml(pic.getDescription()));
			controlsView.findViewById(R.id.tvDescription).setVisibility(View.VISIBLE);
		} else {
			controlsView.findViewById(R.id.tvDescription).setVisibility(View.GONE);
		}

		if (pic.getAuthor().length() > 0) {
			((TextView) controlsView.findViewById(R.id.tvAuthor)).setText(pic.getAuthor());
			controlsView.findViewById(R.id.tvAuthor).setVisibility(View.VISIBLE);
		} else {
			controlsView.findViewById(R.id.tvAuthor).setVisibility(View.GONE);
		}

		if (pic.getImageCachePath() != null) {
			((TextView) controlsView.findViewById(R.id.tvSize))
					.setText((pic.getImageCachePath().length() / 1024) + " Kb");
			controlsView.findViewById(R.id.tvSize).setVisibility(View.VISIBLE);
		} else {
			controlsView.findViewById(R.id.tvSize).setVisibility(View.GONE);
		}

		((TextView) controlsView.findViewById(R.id.tvCreateDate))
				.setText(pic.getFormattedCreateDate(Item.DATEFORMAT_ADAPTIVE));

		RatingBar rb = controlsView.findViewById(R.id.rbRating);
		rb.setRating(pic.getRating());

		rb.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
			@Override
			public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
				if (fromUser) {
					pic.setRating((int) rating);
					ia.updateItem(pic);
				}
			}
		});

		Item gal = (Item) ItemAdapter.getInstance(this, 0).getItem(pic.getParentId());
		Item lib = (Item) ItemAdapter.getInstance(this, 0).getItem(gal.getParentId());

		((TextView) controlsView.findViewById(R.id.tvPath))
				.setText(lib.getTitle() + " / " + gal.getTitle() + " / " + pic.getTitle());

		pic.setAccessDate(new Date());
		ia.updateItem(pic);
		ssAdapter.notifyDataSetChanged();


		return position;
	}

	@Override
	protected void onDestroy() {

		// long id = ia.getItemId(contentView.getCurrentItem());

		ia.setSelectionTerms(oldTerms);

		// Main.getMainActivity().onNavigationDrawerItemSelected(GalleryListFragment.getActiveId(),
		// GalleryListFragment.getPositionId(), id);

		setResult(RESULT_OK);
		super.onDestroy();
	}

}
