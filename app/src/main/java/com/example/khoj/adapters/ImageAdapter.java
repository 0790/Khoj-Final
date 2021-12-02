package com.example.khoj.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.firebase.storage.StorageReference;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import com.example.khoj.R;
import com.example.khoj.databinding.RecyclerMainCategoryImageItemsBinding;
import com.example.khoj.main_ui.HostelList;

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.Internal> {

	List<StorageReference> result;
	HostelList hostelList;

	public ImageAdapter(List<StorageReference> listResult, HostelList activity) {
		result = listResult;
		hostelList = activity;
	}

	@NonNull
	@NotNull
	@Override
	public Internal onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
		return new Internal(LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_main_category_image_items,
				parent, false));
	}

	@Override
	public void onBindViewHolder(@NonNull @NotNull Internal holder, int position) {
		result.get(position).getDownloadUrl()
				.addOnSuccessListener(uri -> {
							if (hostelList != null && !hostelList.isFinishing()) {
								Glide.with(hostelList)
										.load(uri)
										.diskCacheStrategy(DiskCacheStrategy.ALL)
										.into(holder.binding.imageSrc);
							}
						}
				)
				.addOnFailureListener(e -> Toast.makeText(holder.itemView.getContext(),
						"Error loading images", Toast.LENGTH_SHORT).show());
	}

	@Override
	public int getItemCount() {
		return result.size();
	}

	public static class Internal extends RecyclerView.ViewHolder {
		RecyclerMainCategoryImageItemsBinding binding;

		public Internal(@NonNull @NotNull View itemView) {
			super(itemView);
			binding = RecyclerMainCategoryImageItemsBinding.bind(itemView);
		}
	}
}
