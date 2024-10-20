package com.edeqa.exgalleries;

import android.app.ActionBar;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceCategory;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;
import android.text.Html;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import com.edeqa.exgalleries.helpers.Item;
import com.edeqa.exgalleries.helpers.ItemAdapter;
import com.edeqa.exgalleries.helpers.Library;
import com.edeqa.exgalleries.helpers.SelectionAllGalleries;

public class LibraryOptionsFragment extends PreferenceFragment {

	private Item item;
	private ItemAdapter ia;
	private long libId;
	private EditTextPreference ep;

	public LibraryOptionsFragment(long libId) {
		this.libId = libId;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Load the preferences from an XML resource
		setHasOptionsMenu(true);
		addPreferencesFromResource(R.xml.pref_library);

		ia = ItemAdapter.getInstance(getActivity(), 0);
		ia.refresh();

		item = (Item) ia.getItem(libId);

		ep = (EditTextPreference) getPreferenceManager().findPreference("title");
		ep.setText(item.getTitle());
		ep.setSummary(item.getTitle());
		ep.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
			@Override
			public boolean onPreferenceChange(Preference preference, Object newValue) {
				String str = (String) newValue;
				if (str.toString().length() > 0) {
					preference.setDefaultValue(str);
					preference.setSummary(str);
					item.setTitle(str);
					ia.updateItem(item);
					return true;
				}
				return false;
			}
		});

		Preference p = (Preference) getPreferenceManager().findPreference("description");
		p.setSummary(Html.fromHtml(item.getDescription()));

		p = (Preference) getPreferenceManager().findPreference("author");
		p.setSummary(Html.fromHtml(item.getAuthor()));

		p = (Preference) getPreferenceManager().findPreference("version");
		p.setSummary(String.format("%.1f", Library.getVersion(item)));

		p = getPreferenceManager().findPreference("link_to");
		p.setIntent(new Intent(Intent.ACTION_VIEW, Uri.parse(item.getLink())));
		p.setSummary(item.getLink());

		ListPreference lp = (ListPreference) getPreferenceManager().findPreference("thumbnail_type");
		final String[] entries = { getString(R.string.default_word), getString(R.string.mask),
				getString(R.string.collage) };
		lp.setEntryValues(new String[] { "0", "1", "2" });
		lp.setEntries(entries);
		lp.setSummary(entries[Library.getThumbType(item)]);

		lp.setValue(Library.getThumbType(item) + "");
		lp.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
			@Override
			public boolean onPreferenceChange(Preference preference, Object newValue) {
				Library.setThumbType(item, Integer.valueOf(newValue.toString()));
				preference.setSummary(entries[Library.getThumbType(item)]);
				return true;
			}
		});

		final PreferenceScreen category = (PreferenceScreen) findPreference("galleries");

		ia = ItemAdapter.getInstance(getActivity(), libId);

		Cursor cur = ia.getEntries(new SelectionAllGalleries(libId));

		if (cur.moveToFirst()) {
			while (!cur.isAfterLast()) {

				final Item i = (Item) ia.getItem(cur);

				PreferenceCategory pc = new PreferenceCategory(getActivity());
				pc.setTitle(i.getTitle());
				category.addPreference(pc);

				CheckBoxPreference cbp = new CheckBoxPreference(getActivity());
				cbp.setTitle(R.string.visible);
				cbp.setChecked(i.isVisible());
				cbp.setKey(i.getName() + "_visible");
				cbp.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
					@Override
					public boolean onPreferenceChange(Preference preference, final Object newValue) {
						if (newValue instanceof Boolean) {
							new Thread(new Runnable() {
								@Override
								public void run() {
									i.setVisible((Boolean) newValue);
									ia.updateItemNoRefresh(i);
								}
							}).start();
							return true;
						}
						return false;
					}
				});
				pc.addPreference(cbp);

				cbp = new CheckBoxPreference(getActivity());
				cbp.setTitle(R.string.updatable);
				cbp.setChecked(i.isUpdatable());
				cbp.setKey(i.getName() + "_updatable");
				cbp.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
					@Override
					public boolean onPreferenceChange(Preference preference, final Object newValue) {
						if (newValue instanceof Boolean) {
							new Thread(new Runnable() {
								@Override
								public void run() {
									i.setUpdatable((Boolean) newValue);
									ia.updateItemNoRefresh(i);
								}
							}).start();
							return true;
						}
						return false;
					}
				});
				pc.addPreference(cbp);

				cur.moveToNext();
			}

		}
		cur.close();

		CheckBoxPreference cb = (CheckBoxPreference) getPreferenceManager().findPreference("visible");
		((CheckBoxPreference)cb).setChecked(false);

		cb.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
			@Override
			public boolean onPreferenceChange(Preference preference, final Object newValue) {
				if (newValue instanceof Boolean) {
					for (int i = 0; i < category.getPreferenceCount(); i++) {
						PreferenceCategory p = (PreferenceCategory) category.getPreference(i);
						for (int j = 0; j < p.getPreferenceCount(); j++) {
							if (p.getPreference(j).getKey().endsWith("_visible")) {
								((CheckBoxPreference) p.getPreference(j)).setChecked((Boolean) newValue);
							}
						}
					}
					new Thread(new Runnable() {
						@Override
						public void run() {
							ia = ItemAdapter.getInstance(getActivity(), libId);
							Cursor cur = ia.getEntries(new SelectionAllGalleries(libId));
							if (cur.moveToFirst()) {
								while (!cur.isAfterLast()) {
									ia.updateItemValueNoRefresh(cur.getLong(cur.getColumnIndex(ItemAdapter.KEY_ID)),
											ItemAdapter.KEY_VISIBLE, (Boolean) newValue);
									cur.moveToNext();
								}
							}
							cur.close();
						}
					}).start();
					return true;
				}
				return false;
			}
		});

		cb = (CheckBoxPreference) getPreferenceManager().findPreference("updatable");
		((CheckBoxPreference)cb).setChecked(false);

		cb.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
			@Override
			public boolean onPreferenceChange(Preference preference, final Object newValue) {
				if (newValue instanceof Boolean) {
					for (int i = 0; i < category.getPreferenceCount(); i++) {
						PreferenceCategory p = (PreferenceCategory) category.getPreference(i);
						for (int j = 0; j < p.getPreferenceCount(); j++) {
							if (p.getPreference(j).getKey().endsWith("_updatable")) {
								((CheckBoxPreference) p.getPreference(j)).setChecked((Boolean) newValue);
							}
						}
					}
					new Thread(new Runnable() {
						@Override
						public void run() {
							ia = ItemAdapter.getInstance(getActivity(), libId);
							Cursor cur = ia.getEntries(new SelectionAllGalleries(libId));
							if (cur.moveToFirst()) {
								while (!cur.isAfterLast()) {
									ia.updateItemValueNoRefresh(cur.getLong(cur.getColumnIndex(ItemAdapter.KEY_ID)),
											ItemAdapter.KEY_UPDATABLE, (Boolean) newValue);
									cur.moveToNext();
								}
							}
							cur.close();
						}
					}).start();
					return true;
				}
				return false;
			}
		});

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
		getActivity().getMenuInflater().inflate(R.menu.library_options, menu);

		ActionBar actionBar = getActivity().getActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
		actionBar.setDisplayShowTitleEnabled(true);
		actionBar.setTitle(getString(R.string.library_options));

		super.onCreateOptionsMenu(menu, inflater);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		switch (id) {
		case R.id.action_ok:
			Main.getMainActivity().onBackPressed();
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onDestroy() {
		ItemAdapter.getInstance(getActivity(), libId).refresh();

		super.onDestroy();
	}
}
