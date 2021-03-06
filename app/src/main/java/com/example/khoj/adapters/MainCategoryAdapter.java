package com.example.khoj.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import com.example.khoj.Objects.Category;
import com.example.khoj.R;
import com.example.khoj.functions.FDB;

public class MainCategoryAdapter extends RecyclerView.Adapter<MainCategoryAdapter.MainCategoryHolder> {

	ArrayList<Category> categories;
	catClickListener listener;

	public MainCategoryAdapter(ArrayList<Category> all, catClickListener catClickListener) {
		listener = catClickListener;
		categories = all;
	}

	@NonNull
	@NotNull
	@Override
	public MainCategoryHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
		return new MainCategoryHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_main_category_layout_items,
				parent, false));
	}

	@Override
	public void onBindViewHolder(@NonNull @NotNull MainCategoryHolder holder, int position) {
		holder.location_name.setText(categories.get(position).getName());

		holder.itemView.setOnClickListener(v -> listener.click(categories.get(position).getName()));

		FDB.getFDS().getReference()
				.child("main")
				.child(categories.get(position).getUrl())
				.getDownloadUrl()
				.addOnSuccessListener(uri ->
						Glide.with(holder.itemView)
								.load(uri)
								.diskCacheStrategy(DiskCacheStrategy.ALL)
								.into(holder.single_image))
				.addOnFailureListener(Throwable::printStackTrace);
	}

	@Override
	public int getItemCount() {
		return categories.size();
	}

	static class MainCategoryHolder extends RecyclerView.ViewHolder {

		ImageView single_image;
		TextView location_name;

		public MainCategoryHolder(@NonNull @NotNull View itemView) {
			super(itemView);
			single_image = itemView.findViewById(R.id.single_image);

			location_name = itemView.findViewById(R.id.location_name);
		}
	}

	public interface catClickListener {
		void click(String name);
	}
}
