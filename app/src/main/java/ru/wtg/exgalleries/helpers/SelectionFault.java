package ru.wtg.exgalleries.helpers;

public class SelectionFault extends SelectionTerms {

	public SelectionFault(long parentId) {
		addTermIs(ItemAdapter.KEY_PARENT_ID, parentId);
		addTermIs(ItemAdapter.KEY_VISIBLE, true);
		addTermIsNotNull(ItemAdapter.KEY_FAILED);
		addTermIsNotNull(ItemAdapter.KEY_IMAGE_LINK);
		addTermIsNotNull(ItemAdapter.KEY_LINK);
	}
	public SelectionFault() {
		addTermIs(ItemAdapter.KEY_VISIBLE, true);
		addTermIsNotNull(ItemAdapter.KEY_FAILED);
		addTermIsNotNull(ItemAdapter.KEY_IMAGE_LINK);
		addTermIsNotNull(ItemAdapter.KEY_LINK);
	}
}
