package com.example.khoj.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.SetOptions;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import com.example.khoj.Objects.Order;
import com.example.khoj.R;
import com.example.khoj.databinding.RecyclerviewOrderListBinding;
import com.example.khoj.functions.FDB;

public class BookedOrderAdapter extends RecyclerView.Adapter<BookedOrderAdapter.OrderHolder> {

	FragmentActivity mActivity;
	ArrayList<Order> mOrders;

	public BookedOrderAdapter(FragmentActivity activity, ArrayList<Order> orders) {
		mActivity = activity;
		mOrders = orders;
	}

	@NonNull
	@NotNull
	@Override
	public OrderHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
		return new OrderHolder(LayoutInflater.from(parent.getContext())
				.inflate(R.layout.recyclerview_order_list, parent, false));

	}

	@Override
	public void onBindViewHolder(@NonNull @NotNull OrderHolder holder, int position) {
		RecyclerviewOrderListBinding bindingX = holder.binding;
		Order order = mOrders.get(position);
		Calendar calendar = Calendar.getInstance();

		bindingX.orderHostelName.setText(order.getHostel_name());

		calendar.setTimeInMillis(order.getCheck_in());
		bindingX.orderHostelCheckIn.setText("Check-in : " + calendar.get(Calendar.DAY_OF_MONTH) + "th " + monthName(calendar.get(Calendar.MONTH))
				+ " " + calendar.get(Calendar.HOUR_OF_DAY) + ":" + calendar.get(Calendar.MINUTE));

		calendar.setTimeInMillis(order.getCheck_out());
		bindingX.orderHostelCheckOut.setText("Check-out : " + calendar.get(Calendar.DAY_OF_MONTH) + "th " + monthName(calendar.get(Calendar.MONTH))
				+ " " + calendar.get(Calendar.HOUR_OF_DAY) + ":" + calendar.get(Calendar.MINUTE));

		bindingX.orderHostelRoomAdult.setText(order.getBeds_available() + " Beds");


		bindingX.orderHostelRoomPrice.setText("INR " + order.getPrice_total() + " in total");
		bindingX.orderHostelRoomStatus.setText(FDB.status(order.getStatus()));

		if (order.getStatus() != FDB.STATUS_JUST_ORDERED) {
			bindingX.orderCancelHostel.setEnabled(false);
		}


		FDB.getFDS()
				.getReference()
				.child("categories")
				.child(order.getOrder_loc())
				.child(order.getImages_id())
				.child("1.jpg") // categorized - following naming system - must be existing according to the data structure
				.getDownloadUrl().addOnSuccessListener(uri -> Glide.with(mActivity)
				.load(uri)
				.into(bindingX.orderHostelRoomImages))
				.addOnFailureListener(e -> Toast.makeText(mActivity, "Failed to load hostel image.", Toast.LENGTH_SHORT).show());


		bindingX.orderCancelHostel.setOnClickListener(v -> {
			Map<String, Object> update = new HashMap<>();
			update.put("STATUS", FDB.STATUS_REQ_CANCELLATION);

			FDB.getFDB()
					.collection("users")
					.document(FirebaseAuth.getInstance().getUid())
					.collection("orders")
					.document("" + order.getOrder_id())
					.set(update, SetOptions.merge()).addOnSuccessListener(unused -> {
				Toast.makeText(mActivity, "Booking cancellation is requested! Wait for sometime.", Toast.LENGTH_SHORT).show();
				bindingX.orderHostelRoomStatus.setText("Cancellation Requested");
				BookedOrderAdapter.this.notifyDataSetChanged();
			}).addOnFailureListener(e -> Toast.makeText(mActivity, "Failed to request cancellation, retry.", Toast.LENGTH_SHORT).show());
		});
	}

	@Override
	public int getItemCount() {
		return mOrders.size();
	}

	private String monthName(int index) {
		String[] all = {"January", "February", "March", "April", "May", "June",
				"July", "August", "September", "October", "November", "December"};
		return all[index];
	}

	public static class OrderHolder extends RecyclerView.ViewHolder {

		RecyclerviewOrderListBinding binding;

		public OrderHolder(@NonNull @NotNull View itemView) {
			super(itemView);
			binding = RecyclerviewOrderListBinding.bind(itemView);
		}
	}
}
