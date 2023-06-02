package com.example.renthubpablo.view.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.renthubpablo.R;
import com.example.renthubpablo.controller.Anuncio;

import java.util.ArrayList;

public class AnuncioMiniAdapter extends RecyclerView.Adapter<AnuncioMiniAdapter.AnuncioMiniViewHolder> {
    Context context;
    ArrayList<Anuncio> anuncioArrayList;
    OnMiniAnuncioListener onMiniAnuncioListener;

    public AnuncioMiniAdapter(Context context, ArrayList<Anuncio> anuncioArrayList, OnMiniAnuncioListener listener) {
        this.context = context;
        this.anuncioArrayList = anuncioArrayList;
        this.onMiniAnuncioListener=listener;
    }

    @NonNull
    @Override
    public AnuncioMiniAdapter.AnuncioMiniViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater=LayoutInflater.from(parent.getContext());
        View view=inflater.inflate(R.layout.anuncio_mini_item,parent,false);
        return new AnuncioMiniAdapter.AnuncioMiniViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AnuncioMiniAdapter.AnuncioMiniViewHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.categoria.setText(anuncioArrayList.get(position).getCategoria());
        holder.direccion.setText(anuncioArrayList.get(position).getDireccion());
        String precio=String.valueOf(anuncioArrayList.get(position).getPrecio());
        holder.precio.setText(precio+"€/mes");
        holder.titulo.setText(anuncioArrayList.get(position).getTitulo());
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onMiniAnuncioListener.onItemClicked(anuncioArrayList.get(position));
            }
        });
    }

    @Override
    public int getItemCount() {
        return anuncioArrayList.size();
    }

    public static class AnuncioMiniViewHolder extends RecyclerView.ViewHolder{
        ConstraintLayout cardView;
        TextView titulo, precio, direccion, categoria;
        public AnuncioMiniViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView=itemView.findViewById(R.id.consAnuncioMini);
            titulo=itemView.findViewById(R.id.AnunMiniTitulo);
            precio=itemView.findViewById(R.id.AnunMiniPrecio);
            direccion=itemView.findViewById(R.id.AnunMiniDireccion);
            categoria=itemView.findViewById(R.id.AnunMiniCategoria);
        }
    }

    public interface OnMiniAnuncioListener{
        void onItemClicked(Anuncio anuncio);
    }
}
