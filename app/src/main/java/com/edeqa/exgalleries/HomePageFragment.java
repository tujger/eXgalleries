package com.edeqa.exgalleries;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.TextView;
import com.edeqa.exgalleries.helpers.ItemAdapter;
import com.edeqa.exgalleries.helpers.SelectionLibraries;

/**
 * A fragment representing a list of Items.
 * <p />
 * Large screen devices (such as tablets) are supported by replacing the
 * ListView with a GridView.
 * <p />
 * Activities containing this fragment MUST implement the {@link Callbacks}
 * interface.
 */
public class HomePageFragment extends Fragment implements AbsListView.OnItemClickListener {

	private ItemAdapter la;

	private OnFragmentInteractionListener mListener;

	private AbsListView mListView;

	public HomePageFragment() {

	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_homepage, container, false);

		mListView = view.findViewById(android.R.id.list);

		la = ItemAdapter.getInstance(getActivity(), 0);
		la.setSelectionTerms(new SelectionLibraries());
		la.setView(R.layout.item_library);

		((AdapterView<ListAdapter>) mListView).setAdapter(la);

		mListView.setOnItemClickListener(this);

		return view;
	}


	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
			mListener = (OnFragmentInteractionListener) activity;
//			Main.getMainActivity().onNavigationDrawerItemSelected(0, 0);

		} catch (ClassCastException e) {
			throw new ClassCastException(activity + " must implement OnFragmentInteractionListener");
		}
	}

	@Override
	public void onDetach() {
		super.onDetach();
		mListener = null;
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		if (null != mListener) {

			mListener.onFragmentInteraction(la.getItemId(position)+"");
//			}else{
//				Toast.makeText(Main.getContext(), "Not found galleries", Toast.LENGTH_SHORT).show();
//			}
//			cur.close();
		}
	}


	/**
	 * The default content for this Fragment has a TextView that is shown when
	 * the list is empty. If you would like to change the text, call this method
	 * to supply the text it should use.
	 */
	public void setEmptyText(CharSequence emptyText) {
		View emptyView = mListView.getEmptyView();

		if (emptyText instanceof TextView) {
			((TextView) emptyView).setText(emptyText);
		}
	}

	/**
	 * This interface must be implemented by activities that contain this
	 * fragment to allow an interaction in this fragment to be communicated to
	 * the activity and potentially other fragments contained in that activity.
	 * <p>
	 * See the Android Training lesson <a href=
	 * "http://developer.android.com/training/basics/fragments/communicating.html"
	 * >Communicating with Other Fragments</a> for more information.
	 */
	public interface OnFragmentInteractionListener {
		void onFragmentInteraction(String id);
	}

}
