package com.example.khoj.ui_fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import com.example.khoj.Objects.Category;
import com.example.khoj.R;
import com.example.khoj.adapters.MainCategoryAdapter;
import com.example.khoj.databinding.FragmentHomeBinding;
import com.example.khoj.functions.FDB;
import com.example.khoj.functions.LocalDatabase;
import com.example.khoj.main_ui.HostelList;

public class HomeFragment extends Fragment {

	private FragmentHomeBinding binding;
	private RecyclerView main;
	private ArrayList<Category> categories;
	private MainCategoryAdapter adapter;

	public View onCreateView(@NonNull LayoutInflater inflater,
							 ViewGroup container, Bundle savedInstanceState) {

		binding = FragmentHomeBinding.inflate(inflater, container, false);

		main = binding.allMainCategory;
		main.setLayoutManager(new GridLayoutManager(getContext(), 2));
		categories = new ArrayList<>();

		FDB.getFDB()
				.collection("main")
				.document("category")
				.get()
				.addOnSuccessListener(documentSnapshot -> {
					Map<String, Object> all_places = documentSnapshot.getData();
					for (String key : Objects.requireNonNull(all_places).keySet()) {
						categories.add(new Category().setName(key).setUrl(String.valueOf(all_places.get(key))));
					}
					adapter = new MainCategoryAdapter(categories, name ->
							startActivity(new Intent(getActivity(), HostelList.class).putExtra("h_id",
							name.toLowerCase(Locale.getDefault()))));
					main.setAdapter(adapter);
					adapter.notifyDataSetChanged();

					binding.textHome.setText(getString(R.string._choose_text)
							.replaceAll("X", String.valueOf(categories.size()))
							.replaceAll("#", Objects.requireNonNull(LocalDatabase.getInstance(getContext()).getUsername()
									== null ? LocalDatabase.getInstance(getContext()).getPhoneNumber() :
									LocalDatabase.getInstance(getContext()).getUsername())));
				}).addOnFailureListener(e -> Snackbar.make(main, "Error connecting to network!", Snackbar.LENGTH_LONG).show());

		return binding.getRoot();
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		binding = null;
	}
}