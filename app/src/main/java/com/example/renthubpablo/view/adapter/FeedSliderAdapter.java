package com.example.renthubpablo.view.adapter;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.PagerAdapter;

import com.example.renthubpablo.R;
import com.example.renthubpablo.view.fragments.ImageViewFragment;


import java.io.FileNotFoundException;
import java.util.List;

public class FeedSliderAdapter extends PagerAdapter {
    List<Uri> imagesSlider;
    Context context;
    private FragmentManager mfragmentManager;

    public FeedSliderAdapter(List<Uri> imagesSlider, Context context,FragmentManager fragmentManager) {
        this.imagesSlider=imagesSlider;
        this.context=context;
        this.mfragmentManager=fragmentManager;

    }
    public FeedSliderAdapter(List<Uri> imagesSlider, Context context) {
        this.imagesSlider=imagesSlider;
        this.context=context;

    }
    @Override
    public int getCount() {
        return imagesSlider.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view==object;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        View view = LayoutInflater.from(container.getContext()).inflate(R.layout.slider_item,container,false);
        @SuppressLint({"MissingInflatedId", "LocalSuppress"})
        ImageView itemImage=view.findViewById(R.id.ivImageSelector);
        itemImage.setImageURI(imagesSlider.get(position));
        itemImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    ContentResolver resolver = context.getContentResolver();
                    Bitmap bitmap = BitmapFactory.decodeStream(resolver.openInputStream(imagesSlider.get(position)));
                    ImageViewFragment imageViewFragment= ImageViewFragment.newInstance(bitmap);
                    if(mfragmentManager==null){
                        mfragmentManager = ((AppCompatActivity) context).getSupportFragmentManager();
                    }
                    FragmentTransaction fragmentTransaction = mfragmentManager.beginTransaction();
                    fragmentTransaction.add(android.R.id.content, imageViewFragment);
                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.commit();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }

            }
        });
        container.addView(view);
        if(position==imagesSlider.size()){
            imagesSlider.clear();
        }
        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }
}
