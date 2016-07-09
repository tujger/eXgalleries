package ru.wtg.exgalleries.helpers;

public class SelectionGalleries extends SelectionTerms {

	public SelectionGalleries(long parentId) {
		addTermIs(ItemAdapter.KEY_PARENT_ID, parentId);
		addTermIs(ItemAdapter.KEY_VISIBLE, true);
	}
}

