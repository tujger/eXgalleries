package com.edeqa.exgalleries.helpers;

public class SelectionRaw extends SelectionTerms{

	public SelectionRaw(long parentId) {
		addTermIs(ItemAdapter.KEY_PARENT_ID, parentId);
	}

}

