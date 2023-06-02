package com.example.renthubpablo.view.fragments;

import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.example.renthubpablo.R;

public class ImageViewFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private Bitmap mParam1;

    public ImageViewFragment() {

    }
    public static ImageViewFragment newInstance(Bitmap param1) {
        ImageViewFragment fragment = new ImageViewFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_PARAM1, param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getParcelable(ARG_PARAM1);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_image_view, container, false);

        ImageView imagen=view.findViewById(R.id.ivViewContainer);
        if(mParam1!=null){
            imagen.setImageBitmap(mParam1);
        }
        ImageButton rotate=view.findViewById(R.id.ivViewRotate);
        rotate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Matrix matrix = new Matrix();
                imagen.getImageMatrix().invert(matrix);
                int centerX = imagen.getWidth() / 2;
                int centerY = imagen.getHeight() / 2;
                matrix.postRotate(90, centerX, centerY);
                imagen.setImageMatrix(matrix);
            }
        });
        return view;
    }
}