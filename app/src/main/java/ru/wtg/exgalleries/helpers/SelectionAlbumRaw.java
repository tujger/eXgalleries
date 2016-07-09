package ru.wtg.exgalleries.helpers;

public class SelectionAlbumRaw extends SelectionTerms {

	public SelectionAlbumRaw() {
		// addTermIs(ItemAdapter.KEY_PARENT_ID, parentId);
		addTermIs(ItemAdapter.KEY_VISIBLE, true);
		addTermIsNotNull(ItemAdapter.KEY_IMAGE_LINK);
		addTermIsNotNull(ItemAdapter.KEY_LINK);
		addTermIsNotNull(ItemAdapter.KEY_ALBUM);
		// addOrder(ItemAdapter.KEY_ID, false);

	}

}
