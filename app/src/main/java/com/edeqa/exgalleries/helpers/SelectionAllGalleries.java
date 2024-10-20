package com.edeqa.exgalleries.helpers;

public class SelectionAllGalleries extends SelectionTerms {

	public SelectionAllGalleries(long parentId) {
		addTermIs(ItemAdapter.KEY_PARENT_ID, parentId);
	}
}

