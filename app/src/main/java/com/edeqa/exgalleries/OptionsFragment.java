package com.edeqa.exgalleries;

import android.app.ActionBar;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.text.Html;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import com.edeqa.exgalleries.helpers.ItemAdapter;
import com.edeqa.exgalleries.helpers.NetworkState;
import com.edeqa.exgalleries.helpers.SelectionAlbumRaw;
import com.edeqa.exgalleries.helpers.SelectionAll;
import com.edeqa.exgalleries.helpers.SelectionFault;
import com.edeqa.exgalleries.helpers.SelectionFavorites;
import com.edeqa.exgalleries.helpers.SelectionNew;

public class OptionsFragment extends PreferenceFragment {

	public OptionsFragment() {
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Load the preferences from an XML resource
		setHasOptionsMenu(true);
		addPreferencesFromResource(R.xml.pref_options);

		ListPreference lp = (ListPreference) getPreferenceManager().findPreference("use_network");
		lp.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
			@Override
			public boolean onPreferenceChange(Preference preference, Object newValue) {

				((ListPreference) preference).setValue(newValue.toString());
				((ListPreference) preference).setSummary(((ListPreference) preference).getEntry());
				NetworkState.getInstance().reInit();
				return true;
			}
		});

		Preference p = (Preference) getPreferenceManager().findPreference("app");
		try {
			p.setSummary(getString(R.string.version_number,
					getActivity().getPackageManager().getPackageInfo(getActivity().getPackageName(), 0).versionName));
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}

		p = (Preference) getPreferenceManager().findPreference("using");
		p.setSummary(
				Html.fromHtml(TextUtils.join("",
						new String[] { getString(R.string.using_picasso), "<br>",
								getString(R.string.using_touchimageview), "<br>",
								getString(R.string.using_gridviewwithheaderandfooter) })));


		p = (Preference) getPreferenceManager().findPreference("counter_albums_total");
		p.setSummary(ItemAdapter.getInstance(getActivity(), 0).getEntries(new SelectionAlbumRaw()).getCount() + "");

		p = (Preference) getPreferenceManager().findPreference("counter_pictures_total");
		p.setSummary(ItemAdapter.getInstance(getActivity(), 0).getEntries(new SelectionAll()).getCount() + "");

		p = (Preference) getPreferenceManager().findPreference("counter_pictures_new");
		p.setSummary(ItemAdapter.getInstance(getActivity(), 0).getEntries(new SelectionNew()).getCount() + "");

		p = (Preference) getPreferenceManager().findPreference("counter_pictures_favorite");
		p.setSummary(ItemAdapter.getInstance(getActivity(), 0).getEntries(new SelectionFavorites()).getCount() + "");

		p = (Preference) getPreferenceManager().findPreference("counter_pictures_fault");
		p.setSummary(ItemAdapter.getInstance(getActivity(), 0).getEntries(new SelectionFault()).getCount() + "");

	}

	@Override
	public void onPrepareOptionsMenu(Menu menu) {
		MenuItem item;
		item = menu.findItem(R.id.action_add_library);
		if (item != null)
			item.setVisible(false);
		item = menu.findItem(R.id.action_update);
		if (item != null)
			item.setVisible(false);
		super.onPrepareOptionsMenu(menu);
	}

	@SuppressWarnings("deprecation")
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		getActivity().getMenuInflater().inflate(R.menu.options, menu);

		ActionBar actionBar = getActivity().getActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
		actionBar.setDisplayShowTitleEnabled(true);
		actionBar.setTitle(getString(R.string.options));

		super.onCreateOptionsMenu(menu, inflater);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		switch (id) {
		case R.id.action_ok:
			Main.getMainActivity().onBackPressed();
			break;
		}
		return super.onOptionsItemSelected(item);
	}

}
