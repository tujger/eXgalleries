package ru.wtg.exgalleries.helpers;

public class SelectionPictures extends SelectionTerms{

	public SelectionPictures(long parentId) {
		addTermIs(ItemAdapter.KEY_PARENT_ID, parentId);
		addTermIsNotNull(ItemAdapter.KEY_VISIBLE);
		addTermIsNull(ItemAdapter.KEY_FAILED);
		addTermIsNotNull(ItemAdapter.KEY_IMAGE_LINK);
		addTermIsNotNull(ItemAdapter.KEY_LINK);
	}

	public SelectionPictures() {
		addTermIsNotNull(ItemAdapter.KEY_VISIBLE);
		addTermIsNull(ItemAdapter.KEY_FAILED);
		addTermIsNotNull(ItemAdapter.KEY_IMAGE_LINK);
		addTermIsNotNull(ItemAdapter.KEY_LINK);
	}
}

