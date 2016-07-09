package ru.wtg.exgalleries.helpers;

import android.preference.PreferenceManager;
import ru.wtg.exgalleries.Main;

public class SelectionAlbum extends SelectionTerms{

	public SelectionAlbum(long parentId) {
		addTermIs(ItemAdapter.KEY_PARENT_ID, parentId);
		addTermIs(ItemAdapter.KEY_VISIBLE, true);
		addTermIsNotNull(ItemAdapter.KEY_IMAGE_LINK);
		addTermIsNotNull(ItemAdapter.KEY_LINK);
//		addOrder(ItemAdapter.KEY_ID, false);
		
		int showTerm = PreferenceManager.getDefaultSharedPreferences(Main.getContext()).getInt("mode", SHOW_ALL);
		if (showTerm == SHOW_ONLY_NEW) {
			addTermIsNull(ItemAdapter.KEY_ACCESS_DATE);
		} else if (showTerm == SHOW_ONLY_OLD) {
			addTermIsNotNull(ItemAdapter.KEY_ACCESS_DATE);
			addTermIsNull(ItemAdapter.KEY_RATING);
		} else if (showTerm == SHOW_ONLY_RATED) {
			addTermIsNotNull(ItemAdapter.KEY_RATING);
		}
	}

}

