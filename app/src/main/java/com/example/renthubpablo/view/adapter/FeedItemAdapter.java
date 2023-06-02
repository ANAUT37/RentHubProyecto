package com.example.renthubpablo.view.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.example.renthubpablo.R;
import com.example.renthubpablo.controller.Anuncio;
import com.example.renthubpablo.model.AnuncioHandler;
import com.example.renthubpablo.model.FavoritoHandler;
import com.example.renthubpablo.resources.Constants;
import com.example.renthubpablo.resources.FavoritoUtil;
import com.example.renthubpablo.view.AnuncioViewExtended;
import com.example.renthubpablo.view.MainNoSession;
import com.example.renthubpablo.view.NoAppStatus;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import me.relex.circleindicator.CircleIndicator;

public class FeedItemAdapter extends RecyclerView.Adapter<FeedItemAdapter.FeedViewHolder> {
    private Context context;
    private ArrayList<Anuncio> anuncioArrayList;
    private SelectListenerFeed selectListenerFeed;

    private FragmentManager mfragmentManager;

    private String correo;
    private static boolean esFav;
    public static FavoritoHandler.OnFavoritoVerificadoListener onFavoritoVerificadoListener;

    public FeedItemAdapter(Context context, ArrayList<Anuncio> anuncioArrayList,SelectListenerFeed selectListenerFeed,String correo,FragmentManager mfragmentManager){
        this.context=context;
        this.anuncioArrayList=anuncioArrayList;
        this.selectListenerFeed=selectListenerFeed;
        this.correo=correo;
        this.mfragmentManager=mfragmentManager;
    }

    @NonNull
    @Override
    public FeedItemAdapter.FeedViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater=LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.feed_item,parent,false);


        return new FeedItemAdapter.FeedViewHolder(view);
    }

    @SuppressLint("RecyclerView")
    @Override
    public void onBindViewHolder(@NonNull FeedViewHolder holder, int position) {

        SharedPreferences prefs = context.getSharedPreferences(Constants.SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);
        correo=prefs.getString(Constants.SHARED_PREFERENCES_CORREO, "");

        FavoritoHandler favoritoHandler=new FavoritoHandler();
        FavoritoUtil favoritoUtil= new FavoritoUtil(anuncioArrayList.get(position).getIdAnuncio(),
                anuncioArrayList.get(position).getCategoria(),
                correo);

        favoritoHandler.verificarFavorito(
                favoritoUtil,
                new FavoritoHandler.OnFavoritoVerificadoListener() {
                    @Override
                    public void onFavoritoVerificado(boolean existe) {
                        if(existe){
                            esFav=true;
                            holder.fav.setImageResource(R.drawable.heart_full);
                        }else{
                            esFav=false;
                            holder.fav.setImageResource(R.drawable.heart_empty);
                        }
                    }
                }
        );
        holder.fav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                favoritoHandler.verificarFavorito(
                        favoritoUtil,
                        new FavoritoHandler.OnFavoritoVerificadoListener() {
                            @Override
                            public void onFavoritoVerificado(boolean existe) {
                                AnuncioHandler anuncioHandler=new AnuncioHandler();
                                if(existe){
                                    favoritoHandler.removeFavorito(favoritoUtil);
                                    holder.fav.setImageResource(R.drawable.heart_empty);
                                    if(Integer.valueOf(holder.countFavs.getText().toString())>0){
                                        holder.countFavs.setText(String.valueOf(Integer.valueOf(holder.countFavs.getText().toString())-1));
                                        anuncioHandler.changeFavorito(favoritoUtil.getAnuncio(),favoritoUtil.getCategoria(),false);
                                    }
                                }else{
                                    favoritoHandler.setFavorito(favoritoUtil);
                                    holder.fav.setImageResource(R.drawable.heart_full);
                                    holder.countFavs.setText(String.valueOf(Integer.valueOf(holder.countFavs.getText().toString())+1));
                                    anuncioHandler.changeFavorito(favoritoUtil.getAnuncio(),favoritoUtil.getCategoria(),true);

                                }
                            }
                        });
            }
        });

        holder.titulo.setText(anuncioArrayList.get(position).getTitulo());
        holder.descripcion.setText(anuncioArrayList.get(position).getDescripcion());
        holder.precio.setText(anuncioArrayList.get(position).getPrecio() +"€/Mes");
        holder.countVisitas.setText(String.valueOf(anuncioArrayList.get(position).getVisitas()));
        holder.countFavs.setText(String.valueOf(anuncioArrayList.get(position).getFavoritos()));
        holder.direccion.setText(String.valueOf(anuncioArrayList.get(position).getDireccion()));

        List<Uri> urisAnuncio=new ArrayList<>();


        FeedSliderAdapter feedSliderAdapter = new FeedSliderAdapter(urisAnuncio,context,mfragmentManager);
        feedSliderAdapter.notifyDataSetChanged();
        holder.slider.setAdapter(feedSliderAdapter);
        AnuncioHandler anuncioHandler=new AnuncioHandler();
        for(String name: anuncioArrayList.get(position).getImagen()){
            anuncioHandler.getImagenesInmueble(name, new AnuncioHandler.OnGetImagenesInmuebleListener() {
                @Override
                public void onGetImagesSuccess(Uri uri) {
                    urisAnuncio.add(uri);
                    feedSliderAdapter.notifyDataSetChanged();
                }

                @Override
                public void onGetImagesComplete() {

                }

                @Override
                public void onGetImagesError(String error) {

                }
            });
        }
        holder.circleIndicator.setViewPager(holder.slider);

        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectListenerFeed.onItemClicked(anuncioArrayList.get(position));
            }
        });


    }

    @Override
    public int getItemCount() {
        return anuncioArrayList.size();
    }

    public static class FeedViewHolder extends RecyclerView.ViewHolder{
        TextView titulo, descripcion, countFavs, countVisitas, precio, direccion;
        ViewPager slider;
        CardView cardView;
        ImageButton fav;
        CircleIndicator circleIndicator;
        public FeedViewHolder(@NonNull View itemView) {
            super(itemView);

            direccion=itemView.findViewById(R.id.ifeedDireccion);
            titulo=itemView.findViewById(R.id.ifeedTitulo);
            descripcion=itemView.findViewById(R.id.ifeedDescripcion);
            countFavs=itemView.findViewById(R.id.ifeedVistasCount);
            countVisitas=itemView.findViewById(R.id.ifeedFavCount);
            precio=itemView.findViewById(R.id.ifeedPrecio);
            slider=itemView.findViewById(R.id.iExtendedSlider);
            circleIndicator=itemView.findViewById(R.id.sliderIndicator);
            cardView=itemView.findViewById(R.id.cvItemFeed);
            fav=itemView.findViewById(R.id.ifeedbttnFav);
        }
    }

}
