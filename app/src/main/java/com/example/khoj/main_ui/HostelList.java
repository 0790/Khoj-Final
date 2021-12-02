package com.example.khoj.main_ui;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.Objects;

import com.example.khoj.Objects.Hostel;
import com.example.khoj.adapters.HostelListAdapter;
import com.example.khoj.databinding.ActivityHotelListBinding;
import com.example.khoj.functions.FDB;

public class HostelList extends AppCompatActivity {

	ActivityHotelListBinding binding;
	ArrayList<Hostel> hostelArrayList;
	HostelListAdapter adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		binding = ActivityHotelListBinding.inflate(getLayoutInflater());
		setContentView(binding.getRoot());

		String id;

		if (getIntent() != null && ((id = getIntent().getStringExtra("h_id")) != null)) {
			loadHotels(FDB.getFDB().collection("main")
					.document("category")
					.collection(id), id);

			Objects.requireNonNull(getSupportActionBar()).setTitle(String.valueOf(id.charAt(0)).toUpperCase() + id.substring(1));
			hostelArrayList = new ArrayList<>();
			binding.mainHotelsRecycler.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
		} else {
			finish();
			Toast.makeText(this, "No destination specified", Toast.LENGTH_SHORT).show();
		}
	}


	private void loadHotels(CollectionReference collection, String parent) {
		collection
				.get()
				.addOnSuccessListener(queryDocumentSnapshots -> {
					for (DocumentSnapshot snapshot : queryDocumentSnapshots.getDocuments()) {
						Hostel hostel = new Hostel();
						hostel.setName(snapshot.getId())
								.setParent_location(parent)
								.setImages_id(String.valueOf(snapshot.get("images_id")))
								.setAddress(String.valueOf(snapshot.get("address")))
								.setTotal_available(Double.parseDouble(String.valueOf(snapshot.get("total_available"))))
								.setTotal_room(Double.parseDouble(String.valueOf(snapshot.get("total_room"))))
								.setD_facilities(String.valueOf(snapshot.get("d_facilities")))
								.setH_facilities(String.valueOf(snapshot.get("h_facilities")))
								.setR_facilities(String.valueOf(snapshot.get("r_facilities")))
								.setParking(Boolean.parseBoolean(String.valueOf(snapshot.get("parking"))))
								.setPeople_adult(Double.parseDouble(String.valueOf(snapshot.get("people_adult"))))
								.setPeople_child(Double.parseDouble(String.valueOf(snapshot.get("people_child"))))
								.setPhone(String.valueOf(snapshot.get("phone")))
								.setPrice(Double.parseDouble(String.valueOf(snapshot.get("price"))))
								.setWifi(Boolean.parseBoolean(String.valueOf(snapshot.get("wifi"))))
								.setRating(Double.parseDouble(String.valueOf(snapshot.get("rating"))));
						hostelArrayList.add(hostel);
					}
					if (hostelArrayList.size() > 1) {
						Snackbar.make(binding.mainHotelsRecycler, "Slide Right To Explore More Hostels!", Snackbar.LENGTH_SHORT).show();
					}
					adapter = new HostelListAdapter(hostelArrayList, HostelList.this);
					binding.mainHotelsRecycler.setAdapter(adapter);
					adapter.notifyDataSetChanged();
				}).addOnFailureListener(listener);
	}

	private final OnFailureListener listener = e -> Toast.makeText(HostelList.this,
			"Error connecting to server/server operation quota exceeded", Toast.LENGTH_SHORT).show();
}