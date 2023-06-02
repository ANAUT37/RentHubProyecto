package com.example.renthubpablo.view.adapter;

import static android.app.Activity.RESULT_OK;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.renthubpablo.R;
import com.example.renthubpablo.resources.Constants;

import org.checkerframework.checker.nullness.qual.Nullable;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class ImageSelectorAdapter extends RecyclerView.Adapter<ImageSelectorAdapter.ImageSelectorViewHolder>{
    private static final int PICK_IMAGE_REQUEST = 123;
    Context context;
    ArrayList<Uri> selectedImages;

    public ImageSelectorAdapter(Context context, ArrayList<Uri> selectedImages){
        this.context=context;
        this.selectedImages=selectedImages;
    }

    @NonNull
    @Override
    public ImageSelectorAdapter.ImageSelectorViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater=LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.image_selector_layout,parent,false);
        return new ImageSelectorAdapter.ImageSelectorViewHolder(view);
    }

    @SuppressLint("ResourceType")
    @Override
    public void onBindViewHolder(@NonNull ImageSelectorAdapter.ImageSelectorViewHolder holder, @SuppressLint("RecyclerView") int position) {
        Uri bitmap = selectedImages.get(position);
        holder.imageSelector.setImageURI(bitmap);
    }

    @Override
    public int getItemCount() {
        return selectedImages.size();
    }



    public static class ImageSelectorViewHolder extends RecyclerView.ViewHolder {
        ImageView imageSelector;
        ConstraintLayout constraintLayout;

        public ImageSelectorViewHolder(@NonNull View itemView) {
            super(itemView);
            imageSelector = itemView.findViewById(R.id.ivImageSelector);
        }
    }
}
