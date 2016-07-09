package ru.wtg.exgalleries.helpers;

public class SelectionFavorites extends SelectionTerms {
	public SelectionFavorites(long parentId) {
		addTermIs(ItemAdapter.KEY_PARENT_ID, parentId);
		addTermIs(ItemAdapter.KEY_VISIBLE, true);
		addTermIsNull(ItemAdapter.KEY_FAILED);
		addTermIsNotNull(ItemAdapter.KEY_RATING);
		addTermIsNotNull(ItemAdapter.KEY_IMAGE_LINK);
		addTermIsNotNull(ItemAdapter.KEY_LINK);
	}

	public SelectionFavorites() {
		addTermIs(ItemAdapter.KEY_VISIBLE, true);
		addTermIsNull(ItemAdapter.KEY_FAILED);
		addTermIsNotNull(ItemAdapter.KEY_RATING);
		addTermIsNotNull(ItemAdapter.KEY_IMAGE_LINK);
		addTermIsNotNull(ItemAdapter.KEY_LINK);
	}
}

