package com.example.khoj.adapters;

import android.annotation.SuppressLint;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.SetOptions;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import org.jetbrains.annotations.NotNull;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import com.example.khoj.Objects.Hostel;
import com.example.khoj.R;
import com.example.khoj.databinding.LayoutBookHotelBinding;
import com.example.khoj.databinding.RecyclerMainCategoryListItemsBinding;
import com.example.khoj.functions.FDB;
import com.example.khoj.main_ui.HostelList;

public class HostelListAdapter extends RecyclerView.Adapter<HostelListAdapter.ViewHolder> {
	ArrayList<Hostel> localList;
	AlertDialog bookingDialogue;
	Timer timer;
	int position = 0;
	private final HostelList hawaii;

	public HostelListAdapter(ArrayList<Hostel> hostelArrayList, HostelList hostelList) {
		localList = hostelArrayList;
		hawaii = hostelList;
	}

	@NonNull
	@NotNull
	@Override
	public ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
		return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_main_category_list_items,
				parent, false));
	}

	@SuppressLint("NotifyDataSetChanged")
	@Override
	public void onBindViewHolder(@NonNull @NotNull ViewHolder holder, int position) {
		Hostel hostel = localList.get(position);
		RecyclerMainCategoryListItemsBinding bindingX = holder.binding;
		bindingX.hotelName.setText(hostel.getName());
		bindingX.hotelAddress.setText(hostel.getAddress());
		bindingX.hotelRoomDetails.setText((int) hostel.getTotal_available() + " out of " + (int) hostel.getTotal_room() + " available.");
		bindingX.hotelDiningFacilities.setText(hostel.getD_facilities());
		bindingX.hotelHotelFacilities.setText(hostel.getH_facilities());
		bindingX.hotelRoomFacilities.setText(hostel.getR_facilities());
		bindingX.hotelBedsAvailable.setText((int) hostel.getBeds_available() + " Beds");
		bindingX.hotelPhone.setText(hostel.getPhone());
		bindingX.hotelRoomPrice.setText("INR " + hostel.getPrice());
		bindingX.hotelRoomRating.setText(String.valueOf(hostel.getRating()));

		if (hostel.getTotal_available() == 0) {
			bindingX.hotelRoomDetails.setText("No rooms are currently available for booking!");
			bindingX.hotelRoomDetails.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_room
					, 0, R.drawable.ic_no, 0);
			bindingX.bookHotel.setEnabled(false);
		}

		bindingX.hotelParking.setText(hostel.isParking() ?
				"Parking Available 24X7" : "Parking Not Available");
		bindingX.hotelParking.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_parking, 0,
				hostel.isWifi() ? R.drawable.ic_yes : R.drawable.ic_no, 0);

		bindingX.hotelWifi.setText(hostel.isParking() ?
				"WiFi Available 24X7" : "WiFi Not Available");
		bindingX.hotelWifi.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_wifi, 0,
				hostel.isWifi() ? R.drawable.ic_yes : R.drawable.ic_no, 0);

		bindingX.showDesc.setOnClickListener(v -> bindingX.hotelShowDesc.setVisibility(bindingX.hotelShowDesc.getVisibility()
				== View.VISIBLE ? View.GONE : View.VISIBLE));
		bindingX.bookHotel.setOnClickListener(v -> showBookDialogueExternal(position));

		FDB.getFDS()
				.getReference()
				.child("categories")
				.child(hostel.getParent_location())
				.child(hostel.getImages_id())
				.listAll().addOnSuccessListener(listResult -> {
			ImageAdapter adapter = new ImageAdapter(listResult.getItems(), hawaii);
			bindingX.hotelRoomImages.setNestedScrollingEnabled(false);
			bindingX.hotelRoomImages.setAdapter(adapter);
			adapter.notifyDataSetChanged();

			timer = new Timer();
			timer.scheduleAtFixedRate(new AutoScrollTask(bindingX.hotelRoomImages,
					listResult.getItems().size()), 2000, 2000);

		}).addOnFailureListener(e -> Toast.makeText(holder.itemView.getContext(),
				"Error loading images", Toast.LENGTH_SHORT).show());
	}

	private void showBookDialogueExternal(int position) {
		Calendar now = Calendar.getInstance();
		DatePickerDialog check_in = DatePickerDialog.newInstance(
				(view, year, monthOfYear, dayOfMonth) -> {
					Calendar now1 = Calendar.getInstance();
					TimePickerDialog check_out = TimePickerDialog.newInstance(
							(view1, hourOfDay, minute, second) ->
									showBookDialogueInternal(position, monthOfYear, dayOfMonth, hourOfDay, minute),
							now1.get(Calendar.HOUR_OF_DAY),
							now1.get(Calendar.MINUTE),
							true
					);

					check_out.setTitle("Select Check-in Time");
					check_out.show(hawaii.getSupportFragmentManager(), "sco");
				},
				now.get(Calendar.YEAR),
				now.get(Calendar.MONTH),
				now.get(Calendar.DAY_OF_MONTH)
		);

		check_in.setTitle("Select Check-in Date");
		check_in.show(hawaii.getSupportFragmentManager(), "scn");
	}

	private void showBookDialogueInternal(int position, int old_month, int old_day, int old_hour, int old_minute) {
		Calendar now = Calendar.getInstance();
		DatePickerDialog check_in = DatePickerDialog.newInstance(
				(view, year, monthOfYear, dayOfMonth) -> {
					Calendar now1 = Calendar.getInstance();
					TimePickerDialog check_out = TimePickerDialog.newInstance(
							(view1, hourOfDay, minute, second) ->
							{
								try {
									showBookDialogue(position, year, old_month, old_day, old_hour,
											old_minute, monthOfYear, dayOfMonth, hourOfDay, minute);
								} catch (ParseException e) {
									e.printStackTrace();
								}
							},
							now1.get(Calendar.HOUR_OF_DAY),
							now1.get(Calendar.MINUTE),
							true
					);

					check_out.setTitle("Select Check-out Time");
					check_out.show(hawaii.getSupportFragmentManager(), "sco");
				},
				now.get(Calendar.YEAR),
				now.get(Calendar.MONTH),
				now.get(Calendar.DAY_OF_MONTH)
		);

		check_in.setTitle("Select Check-out Date");
		check_in.show(hawaii.getSupportFragmentManager(), "scn");
	}

	private void showBookDialogue(int position, int year, int old_month, int old_day, int old_hour, int old_minute,
								  int monthOfYear, int dayOfMonth, int hourOfDay, int minute) throws ParseException {
		Hostel hostel = localList.get(position);

		View view = LayoutInflater.from(hawaii).inflate(R.layout.layout_book_hotel, null);
		LayoutBookHotelBinding bookHotelBinding = LayoutBookHotelBinding.bind(view);

		bookingDialogue = new AlertDialog.Builder(hawaii)
				.setView(view)
				.create();

		bookHotelBinding.checkInDate.setText(old_day + " " + monthName(old_month) + "\n" + old_hour + ":" + old_minute);
		bookHotelBinding.checkOutDate.setText(dayOfMonth + " " + monthName(monthOfYear) + "\n" + hourOfDay + ":" + minute);

		bookHotelBinding.bedsNeed.setRange(1, (int) hostel.getBeds_available());
		SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss", Locale.ENGLISH);
		Date firstDate = sdf.parse(old_month + "/" + old_day + "/" + year + " " + old_hour + ":" + old_minute + ":00");
		Date secondDate = sdf.parse(monthOfYear + "/" + dayOfMonth + "/" + year + " " + hourOfDay + ":" + minute + ":00");

		long diff = TimeUnit.DAYS.convert(Math.abs(secondDate.getTime() - firstDate.getTime()), TimeUnit.MILLISECONDS);

		bookHotelBinding.hotelRoomPriceTotal.setText(
				"INR " + (diff * hostel.getPrice()) + " total for " + diff + " days."
		);

		bookHotelBinding.bookHotelConfirm.setOnSlideCompleteListener(slideToActView -> {

			long order_id = System.currentTimeMillis();

			Map<String, Object> all = new HashMap<>();

			all.put("HOTEL_NAME", hostel.getName());
			all.put("CHECK_IN", firstDate.getTime());
			all.put("CHECK_OUT", secondDate.getTime());
			all.put("BED_NEEDED", Integer.parseInt(bookHotelBinding.bedsNeed.getNumber()));
			all.put("PRICE_TOTAL", diff * hostel.getPrice());
			all.put("ORDER_ID", order_id);
			all.put("IMAGES_ID", hostel.getImages_id());
			all.put("IMAGES_LOC", hostel.getParent_location());
			all.put("STATUS", 0);

			FDB.getFDB()
					.collection("users")
					.document(Objects.requireNonNull(FirebaseAuth.getInstance().getUid()))
					.collection("orders")
					.document(String.valueOf(order_id))
					.set(all, SetOptions.merge())
					.addOnSuccessListener(unused -> Toast.makeText(hawaii, "Hostel is booked!", Toast.LENGTH_SHORT).show())
					.addOnFailureListener(e -> Toast.makeText(hawaii, "Hostel booking failed! Please retry!", Toast.LENGTH_SHORT).show());

			if (bookingDialogue.isShowing())
				bookingDialogue.dismiss();
		});

		if (!hawaii.isFinishing())
			bookingDialogue.show();
	}

	private String monthName(int index) {
		String[] all = {"January", "February", "March", "April", "May", "June",
				"July", "August", "September", "October", "November", "December"};
		return all[index];
	}

	@Override
	public int getItemCount() {
		return localList.size();
	}


	public static class ViewHolder extends RecyclerView.ViewHolder {

		RecyclerMainCategoryListItemsBinding binding;

		public ViewHolder(@NonNull @NotNull View itemView) {
			super(itemView);
			binding = RecyclerMainCategoryListItemsBinding.bind(itemView);
		}
	}


	private class AutoScrollTask extends TimerTask {
		RecyclerView recyclerView;
		int size;

		public AutoScrollTask(RecyclerView hotelRoomImages, int l_size) {
			recyclerView = hotelRoomImages;
			size = l_size;
		}

		public void run() {
			recyclerView.post(() -> {
				if (position < size)
					position++;
				else
					position = 0;
				recyclerView.smoothScrollToPosition(position);
			});
		}
	}
}
