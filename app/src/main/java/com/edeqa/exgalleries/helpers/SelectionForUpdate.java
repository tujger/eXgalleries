package com.edeqa.exgalleries.helpers;

public class SelectionForUpdate extends SelectionTerms {

	public SelectionForUpdate(long parentId) {
		addTermIs(ItemAdapter.KEY_PARENT_ID, parentId);
		addTermIsNotNull(ItemAdapter.KEY_UPDATABLE);
	}
	public SelectionForUpdate() {
		addTermIsNotNull(ItemAdapter.KEY_PARENT_ID);
		addTermIsNotNull(ItemAdapter.KEY_UPDATABLE);
		addTermIsNull(ItemAdapter.KEY_IMAGE_LINK);
		addTermIsNotNull(ItemAdapter.KEY_LINK);
		addTermIsNull(ItemAdapter.KEY_FAILED);
	}
}
