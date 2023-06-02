package com.example.renthubpablo.view.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.renthubpablo.R;
import com.example.renthubpablo.resources.BusquedaUtil;

import java.util.List;

public class HistorialAdapter extends RecyclerView.Adapter<HistorialAdapter.HistorialViewHolder> {
    List<BusquedaUtil> busquedaList;
    OnHistorialItemListener onHistorialItemListener;
    Context context;

    public HistorialAdapter(List<BusquedaUtil> busquedaList, Context context,OnHistorialItemListener onHistorialItemListener) {
        this.busquedaList = busquedaList;
        this.context = context;
        this.onHistorialItemListener=onHistorialItemListener;
    }

    @NonNull
    @Override
    public HistorialAdapter.HistorialViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater=LayoutInflater.from(context);
        View view=inflater.inflate(R.layout.historial_item,parent,false);
        return new HistorialAdapter.HistorialViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HistorialAdapter.HistorialViewHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.direccion.setText(busquedaList.get(position).getDireccion());
        holder.categoria.setText(busquedaList.get(position).getCategoria());
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onHistorialItemListener.onItemClicked(busquedaList.get(position).getDireccion(),busquedaList.get(position).getCategoria());
            }
        });
    }

    @Override
    public int getItemCount() {
        return busquedaList.size();
    }

    public static class HistorialViewHolder extends RecyclerView.ViewHolder{
        TextView direccion;
        TextView categoria;
        CardView cardView;
        public HistorialViewHolder(@NonNull View itemView) {
            super(itemView);
            direccion=itemView.findViewById(R.id.tvHistorialCategoria);
            categoria=itemView.findViewById(R.id.tvHistorialDireccion);
            cardView=itemView.findViewById(R.id.cvAnuncioMini);
        }
    }
    public interface OnHistorialItemListener{
        void onItemClicked(String direccion, String categoria);
    }
}
