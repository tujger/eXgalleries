package ru.wtg.exgalleries.helpers;

public class SelectionSlidingTab extends SelectionTerms{

	public SelectionSlidingTab(long parentId) {
		addTermIs(ItemAdapter.KEY_PARENT_ID, parentId);
		addTermIsNotNull(ItemAdapter.KEY_VISIBLE);
		addTermIsNotNull(ItemAdapter.KEY_IMAGE_LINK);
		addTermIsNotNull(ItemAdapter.KEY_LINK);
		
		
//		int showTerm=PreferenceManager.getDefaultSharedPreferences(Main.getContext()).getInt("mode", SHOW_ALL);
//		if(showTerm==SHOW_ONLY_NEW){
//			addTermIsNull(ItemAdapter.KEY_ACCESS_DATE);
//		}else if(showTerm==SHOW_ONLY_OLD){
//			addTermIsNotNull(ItemAdapter.KEY_ACCESS_DATE);
//		}
		
//		addOrder(ItemAdapter.KEY_ID, true);
	}
}

