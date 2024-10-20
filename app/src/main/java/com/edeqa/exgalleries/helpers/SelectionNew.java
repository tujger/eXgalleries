package com.edeqa.exgalleries.helpers;

public class SelectionNew extends SelectionTerms{
	public SelectionNew(long parentId) {
		addTermIs(ItemAdapter.KEY_PARENT_ID, parentId);
		addTermIs(ItemAdapter.KEY_VISIBLE, true);
		addTermIsNull(ItemAdapter.KEY_FAILED);
		addTermIsNull(ItemAdapter.KEY_ACCESS_DATE);
		addTermIsNotNull(ItemAdapter.KEY_IMAGE_LINK);
		addTermIsNotNull(ItemAdapter.KEY_LINK);
	}

	public SelectionNew() {
		addTermIs(ItemAdapter.KEY_VISIBLE, true);
		addTermIsNull(ItemAdapter.KEY_FAILED);
		addTermIsNull(ItemAdapter.KEY_ACCESS_DATE);
		addTermIsNotNull(ItemAdapter.KEY_IMAGE_LINK);
		addTermIsNotNull(ItemAdapter.KEY_LINK);
	}
}

