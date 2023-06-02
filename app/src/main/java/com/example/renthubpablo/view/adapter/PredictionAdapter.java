package com.example.renthubpablo.view.adapter;

import android.content.Context;
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
import com.google.android.libraries.places.api.model.AutocompletePrediction;
import com.google.maps.errors.ApiException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PredictionAdapter extends RecyclerView.Adapter<PredictionAdapter.PredictionsViewHolder>{
    Context context;
    List<AutocompletePrediction> predictions;
    private SelectedListenerPrediction selectedListenerPrediction;

    public PredictionAdapter(Context context, List<AutocompletePrediction> predictions,SelectedListenerPrediction selectedListenerPrediction){
        this.context=context;
        this.predictions=predictions;
        this.selectedListenerPrediction=selectedListenerPrediction;
    }

    @NonNull
    @Override
    public PredictionAdapter.PredictionsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.prediction_layout,parent,false);

        return new PredictionAdapter.PredictionsViewHolder(view);
    }



    @Override
    public void onBindViewHolder(@NonNull PredictionAdapter.PredictionsViewHolder holder, int position) {
        holder.ubicacion.setText(predictions.get(position).getFullText(null).toString());
        holder.ubicacion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    selectedListenerPrediction.onItemClicked(holder.ubicacion.getText().toString());
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ApiException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return predictions.size();
    }
    public static class PredictionsViewHolder extends RecyclerView.ViewHolder{
        TextView ubicacion;

        public PredictionsViewHolder(@NonNull View itemView) {
            super(itemView);
            ubicacion=itemView.findViewById(R.id.predictionText);

        }
    }
}
