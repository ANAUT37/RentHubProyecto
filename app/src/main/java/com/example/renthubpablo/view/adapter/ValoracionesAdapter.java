package com.example.renthubpablo.view.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.renthubpablo.R;
import com.example.renthubpablo.controller.types.Valoracion;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class ValoracionesAdapter extends RecyclerView.Adapter<ValoracionesAdapter.ValoracionesViewHolder>{
    Context context;
    ArrayList<Valoracion> valoracionArrayList;

    public ValoracionesAdapter(Context context, ArrayList<Valoracion> valoracionArrayList){
        this.context=context;
        this.valoracionArrayList=valoracionArrayList;
    }

    @NonNull
    @Override
    public ValoracionesAdapter.ValoracionesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.valoracion_item,parent,false);

        return new ValoracionesAdapter.ValoracionesViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ValoracionesAdapter.ValoracionesViewHolder holder, int position) {
        holder.nombre.setText(valoracionArrayList.get(position).getNombreAutor());
        holder.opinion.setText(valoracionArrayList.get(position).getOpinion());
        holder.pfp.setImageBitmap(valoracionArrayList.get(position).getBitmap());

        StorageReference storageReference = FirebaseStorage.getInstance().getReference("pfp/"+
                valoracionArrayList.get(position).getSenderPfp());

        try {
            File localFile = File.createTempFile("tempfile",".jpg");
            storageReference.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                    Bitmap bitmap= BitmapFactory.decodeFile(localFile.getAbsolutePath());
                    holder.pfp.setImageBitmap(bitmap);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                }
            });
        } catch (IOException e) {
        }
        holder.rating.setRating(valoracionArrayList.get(position).getEstrellas());
    }

    @Override
    public int getItemCount() {
        return valoracionArrayList.size();
    }
    public static class ValoracionesViewHolder extends RecyclerView.ViewHolder{
        TextView nombre, opinion;
        ImageView pfp;
        RatingBar rating;

        public ValoracionesViewHolder(@NonNull View itemView) {
            super(itemView);

            nombre=itemView.findViewById(R.id.tvHistorialCategoria);
            opinion=itemView.findViewById(R.id.tvHistorialDireccion);
            pfp=itemView.findViewById(R.id.ivPfpChatItem);
            rating=itemView.findViewById(R.id.rbValoracionItem);
        }
    }
}
