package com.edeqa.exgalleries;

import android.app.ActionBar;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.edeqa.exgalleries.helpers.Entity;
import com.edeqa.exgalleries.helpers.Item;
import com.edeqa.exgalleries.helpers.ItemAdapter;
import com.edeqa.exgalleries.helpers.Library;
import com.edeqa.exgalleries.helpers.LibraryParser;

public class AddLibraryActivity extends Activity {

	LibraryParser lp;
	ProgressDialog dialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_library);

		ActionBar actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setHomeButtonEnabled(true);

		dialog = new ProgressDialog(this);
		dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		dialog.setMessage(getString(R.string.please_wait));
		dialog.setIndeterminate(true);
		dialog.setCanceledOnTouchOutside(false);
		dialog.show();

		lp = (LibraryParser) getIntent().getSerializableExtra("LibraryParsed");
		fillScreen();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.add_library, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_ok:
			applyLibrary();
			break;
		case R.id.action_cancel:
		case android.R.id.home:
			finish();
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	public void fillScreen() {

		((TextView) findViewById(R.id.tvTitle)).setText(lp.getProperty(LibraryParser.TITLE));
		((TextView) findViewById(R.id.tvLink)).setText(lp.getProperty(LibraryParser.LINK));
		((TextView) findViewById(R.id.tvAuthor)).setText(lp.getProperty(LibraryParser.AUTHOR));
		((TextView) findViewById(R.id.tvVersion)).setText(lp.getProperty(LibraryParser.VERSION));
		((TextView) findViewById(R.id.tvDescription)).setText(Html.fromHtml(lp.getProperty(LibraryParser.DESCRIPTION)));

		LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		TableLayout tl = (TableLayout) findViewById(R.id.galleries);

		for (Item lg : lp.getGalleries()) {
			RelativeLayout tr = (RelativeLayout) inflater.inflate(R.layout.activity_add_library_item, null);
			TextView tv = (TextView) tr.findViewById(R.id.tvLibraryTitle);
			tv.setText(lg.getTitle());
			tl.addView(tr);
		}

		if (dialog != null && dialog.isShowing())
			dialog.dismiss();
	}

	private void applyLibrary() {
		ItemAdapter ia = ItemAdapter.getInstance(this, 0);
		long libId, galId;

		Library lib = new Library();
		lib.setName(lp.getProperty(LibraryParser.NAME)).setTitle(lp.getProperty(LibraryParser.TITLE))
				.setLink(lp.getProperty(LibraryParser.LINK)).setImageLink(lp.getProperty(LibraryParser.IMAGE_LINK))
				.setAuthor(lp.getProperty(LibraryParser.AUTHOR))
				.setDescription(lp.getProperty(LibraryParser.DESCRIPTION)).setVisible(true).setUpdatable(true)
				.setAlbum(true);

		if ((libId = ia.findIdByName(lp.getProperty(LibraryParser.NAME))) > 0) {
			lib.setId(libId);
			ia.updateItem(lib);
			Toast.makeText(this, getString(R.string.library_exists_updated), Toast.LENGTH_SHORT).show();
		} else {
			libId = ia.addItem((Entity<?>) lib);
		}

		// ItemAdapter ia = ItemAdapter.getInstance(Main.getContext(),libId);
		Item newGal;
		for (Item gal : lp.getGalleries()) {
			gal.setName(lib.getName() + "_" + gal.getName());
			gal.setParentId(libId);

			if ((galId = ia.findIdByName(gal.getName())) > 0) {
				newGal = (Item) ia.getItem(galId);
				// gal.setVisible(true);
				// gal.setUpdatable(true);
			} else {
				newGal = new Item();
				newGal.setLink(gal.getLink());
				ia.addItem(newGal);
			}

			newGal.setTitle(gal.getTitle());
			newGal.setLink(gal.getLink());
			newGal.setImageLink(gal.getImageLink());
			newGal.setName(gal.getName());
			newGal.setParentId(gal.getParentId());

			ia.updateItem(newGal);
		}
		Library.setVersion(lib, Double.valueOf(lp.getProperty(LibraryParser.VERSION)));
		Library.setKeywords(lib, lp.getProperty(LibraryParser.KEYWORDS));
		Library.setJsmain(lib, lp.getProperty(LibraryParser.JSMAIN));
		Library.setSource(lib, lp.getProperty(LibraryParser.SOURCE));

		setResult(RESULT_OK);
		finish();
	}

}
