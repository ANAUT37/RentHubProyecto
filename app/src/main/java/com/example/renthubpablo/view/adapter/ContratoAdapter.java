package com.example.renthubpablo.view.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.renthubpablo.R;
import com.example.renthubpablo.controller.contratos.ContratoPlantilla;

import java.util.ArrayList;

public class ContratoAdapter extends RecyclerView.Adapter<ContratoAdapter.ContratoViewHolder> {
    ArrayList<ContratoPlantilla> contratoArrayList;
    Context context;
    private OnContratoClick onContratoClick;

    public ContratoAdapter(ArrayList<ContratoPlantilla> contratoArrayList, Context context, OnContratoClick onContratoClick) {
        this.contratoArrayList = contratoArrayList;
        this.context = context;
        this.onContratoClick=onContratoClick;
    }

    @NonNull
    @Override
    public ContratoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater=LayoutInflater.from(context);
        View view=inflater.inflate(R.layout.contrato_item,parent,false);
        return new ContratoAdapter.ContratoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ContratoViewHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.title.setText(contratoArrayList.get(position).getTitle());
        holder.description.setText(contratoArrayList.get(position).getDescription());
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onContratoClick.onContratoClick(contratoArrayList.get(position));
            }
        });
    }

    @Override
    public int getItemCount() {
        return contratoArrayList.size();
    }

    public static class ContratoViewHolder extends RecyclerView.ViewHolder{
        TextView title, description;
        CardView cardView;
        public ContratoViewHolder(@NonNull View itemView) {
            super(itemView);
            title=itemView.findViewById(R.id.tvContItemTitulo);
            description=itemView.findViewById(R.id.tvContItemDescripcion);
            cardView=itemView.findViewById(R.id.cvAnuncioMini);
        }
    }
    public interface OnContratoClick{
        void onContratoClick(ContratoPlantilla contrato);
        void onImageClick(Bitmap image);
    }
}
