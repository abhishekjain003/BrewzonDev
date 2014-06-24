package com.asb.fragments;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import com.asb.adapters.ListingAdapter;
import com.asb.brewzon.R;
import com.asb.details.ListingDetail;

@SuppressLint("NewApi")
public class ListingLogicFragment extends BaseFragment {

	private GridView gridList;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub

		getActivity().getActionBar().show();

		View rootView = inflater.inflate(R.layout.listing_logic_fragment,
				container, false);

		inItView(rootView);

		return rootView;
	}

	private void inItView(View rootView) {
		// TODO Auto-generated method stub

		gridList = (GridView) rootView.findViewById(R.id.gv_listing);

		ArrayList<ListingDetail> list = new ArrayList<ListingDetail>();

		ListingDetail detail;

		for (int i = 0; i < 6; i++) {

			detail = new ListingDetail();
			detail.setImgId(0);
			detail.setListingType("");
			detail.setPrice("");

			list.add(detail);
		}

		ListingAdapter adapter = new ListingAdapter(getActivity(), list);

		gridList.setAdapter(adapter);
	}
}
