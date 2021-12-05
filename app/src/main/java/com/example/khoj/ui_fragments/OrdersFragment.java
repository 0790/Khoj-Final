package com.example.khoj.ui_fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;

import com.example.khoj.Objects.Order;
import com.example.khoj.adapters.BookedOrderAdapter;
import com.example.khoj.databinding.FragmentOrdersBinding;
import com.example.khoj.functions.FDB;

public class OrdersFragment extends Fragment {

	private FragmentOrdersBinding binding;
	private ArrayList<Order> orders;
	BookedOrderAdapter adapter;

	public View onCreateView(@NonNull LayoutInflater inflater,
							 ViewGroup container, Bundle savedInstanceState) {

		binding = FragmentOrdersBinding.inflate(inflater, container, false);
		orders = new ArrayList<>();

		load();
		return binding.getRoot();
	}

	private void load() {
		FDB.getFDB()
				.collection("users")
				.document(FirebaseAuth.getInstance().getCurrentUser().getUid())
				.collection("orders")
				.get()
				.addOnSuccessListener(queryDocumentSnapshots -> {
					for (DocumentSnapshot snapshot : queryDocumentSnapshots.getDocuments()) {
						Order order = new Order();
						order.setHostel_name(snapshot.getString("HOTEL_NAME"))
								.setCheck_in(snapshot.getLong("CHECK_IN"))
								.setCheck_out(snapshot.getLong("CHECK_OUT"))
								.setBeds_available(snapshot.getLong("BED_NEEDED"))
								.setPrice_total(snapshot.getLong("PRICE_TOTAL"))
								.setOrder_id(snapshot.getLong("ORDER_ID"))
								.setImages_id(snapshot.getString("IMAGES_ID"))
								.setOrder_loc(snapshot.getString("IMAGES_LOC"))
								.setStatus(toIntExact(snapshot.getLong("STATUS")));
						orders.add(order);
					}
					binding.loadingStuff.setVisibility(View.GONE);
					if (orders.size() == 0) {
						binding.zeroOrders.setVisibility(View.VISIBLE);
						binding.allBookedHostels.setVisibility(View.GONE);
					} else {
						adapter = new BookedOrderAdapter(getActivity(), orders);
						binding.allBookedHostels.setAdapter(adapter);
						adapter.notifyDataSetChanged();
					}
				}).addOnFailureListener(e -> Toast.makeText(getActivity(), "Error loading orders", Toast.LENGTH_SHORT).show());
	}

	public static int toIntExact(long value) {
		if ((int) value != value) {
			throw new ArithmeticException("integer overflow");
		}
		return (int) value;
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		binding = null;
	}
}