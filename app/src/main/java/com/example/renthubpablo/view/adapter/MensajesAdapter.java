package com.example.renthubpablo.view.adapter;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.renthubpablo.R;
import com.example.renthubpablo.controller.contratos.ContratoPlantilla;
import com.example.renthubpablo.databinding.ReceivedContratoItemBinding;
import com.example.renthubpablo.databinding.ReceivedImageItemBinding;
import com.example.renthubpablo.databinding.ReceivedMessageItemBinding;
import com.example.renthubpablo.databinding.SentContratoItemBinding;
import com.example.renthubpablo.databinding.SentImageItemBinding;
import com.example.renthubpablo.databinding.SentMessageItemBinding;
import com.example.renthubpablo.model.ContratoHandler;
import com.example.renthubpablo.resources.Constants;
import com.example.renthubpablo.resources.MensajeUtil;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Objects;

public class MensajesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    private final String sender;
    private final List<MensajeUtil> mensajesList;
    private ContratoAdapter.OnContratoClick onContratoClick;

    public static final int VIEW_MESSAGE_SENT=1;
    public static final int VIEW_MESSAGE_RECEIVED=2;
    public static final int VIEW_IMAGE_SENT=3;
    public static final int VIEW_IMAGE_RECEIVED=4;
    public static final int VIEW_CONTRATO_SENT=5;
    public static final int VIEW_CONTRATO_RECEIVED=6;

    public MensajesAdapter(String sender, List<MensajeUtil> mensajesList) {
        this.sender = sender;
        this.mensajesList = mensajesList;
    }

    public MensajesAdapter(String sender, List<MensajeUtil> mensajesList, ContratoAdapter.OnContratoClick onContratoClick) {
        this.sender = sender;
        this.mensajesList = mensajesList;
        this.onContratoClick=onContratoClick;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(viewType==VIEW_MESSAGE_SENT){
           //System.out.println("OCURRIO: MENSAJE TEXTO CREATE HOLDER ENVIADO");
            return new SentMessageViewHolder(
                    SentMessageItemBinding.inflate(
                            LayoutInflater.from(parent.getContext()),
                            parent,
                            false
                    ),onContratoClick
            );
        }else if(viewType==VIEW_MESSAGE_RECEIVED){
            //System.out.println("OCURRIO: MENSAJE TEXTO CREATE HOLDER");
            return new ReceivedMessageViewHolder(
                    ReceivedMessageItemBinding.inflate(
                            LayoutInflater.from(parent.getContext()),
                            parent,
                            false
                    ),onContratoClick
            );
        }else if(viewType==VIEW_IMAGE_SENT){
            return new SentImageViewHolder(
                    SentImageItemBinding.inflate(
                            LayoutInflater.from(parent.getContext()),
                            parent,
                            false
                    ),onContratoClick
            );
        }else if(viewType==VIEW_IMAGE_RECEIVED){
            return new ReceivedImageViewHolder(
                    ReceivedImageItemBinding.inflate(
                            LayoutInflater.from(parent.getContext()),
                            parent,
                            false
                    ),onContratoClick
            );
        }else if(viewType==VIEW_CONTRATO_SENT){
            return new SentContratoViewHolder(
                    SentContratoItemBinding.inflate(
                            LayoutInflater.from(parent.getContext()),
                            parent,
                            false
                    ),
                    onContratoClick);
        }else{
            return new ReceivedContratoViewHolder(
                    ReceivedContratoItemBinding.inflate(
                            LayoutInflater.from(parent.getContext()),
                            parent,
                            false
                    ),
                    onContratoClick);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if(getItemViewType(position)==VIEW_MESSAGE_SENT){
            //System.out.println("OCURRIO: MENSAJE TEXTO CREATE HOLDER 2");
            ((SentMessageViewHolder)holder).setData(mensajesList.get(position));
        }else if(getItemViewType(position)==VIEW_MESSAGE_RECEIVED){
            ((ReceivedMessageViewHolder)holder).setData(mensajesList.get(position));
        }else if(getItemViewType(position)==VIEW_IMAGE_SENT){
            ((SentImageViewHolder)holder).setData(mensajesList.get(position));
        }else if(getItemViewType(position)==VIEW_IMAGE_RECEIVED){
            ((ReceivedImageViewHolder)holder).setData(mensajesList.get(position));
        }else if(getItemViewType(position)==VIEW_CONTRATO_SENT){
            ((SentContratoViewHolder)holder).setData(mensajesList.get(position));
        }else if(getItemViewType(position)==VIEW_CONTRATO_RECEIVED){
            ((ReceivedContratoViewHolder)holder).setData(mensajesList.get(position));
        }
    }

    @Override
    public int getItemCount() {
        return mensajesList.size();
    }

    @Override
    public int getItemViewType(int position) {
        if(mensajesList.get(position).getSender().equals(sender)&& Objects.equals(mensajesList.get(position).getType(), "texto")){
            return VIEW_MESSAGE_SENT;
        }else if(!mensajesList.get(position).getSender().equals(sender)&& Objects.equals(mensajesList.get(position).getType(), "texto")){
            return VIEW_MESSAGE_RECEIVED;
        }else if(mensajesList.get(position).getSender().equals(sender)&& Objects.equals(mensajesList.get(position).getType(), "imagen")){
            return VIEW_IMAGE_SENT;
        }else if(!mensajesList.get(position).getSender().equals(sender)&& Objects.equals(mensajesList.get(position).getType(), "imagen")){
            return VIEW_IMAGE_RECEIVED;
        }else if(mensajesList.get(position).getSender().equals(sender)&&Objects.equals(mensajesList.get(position).getType(),"contrato")){
            return VIEW_CONTRATO_SENT;
        }else if(!mensajesList.get(position).getSender().equals(sender)&&Objects.equals(mensajesList.get(position).getType(),"contrato")){
            return VIEW_CONTRATO_RECEIVED;
        }else{
            //PARA POSIBLES IMPLEMENTACIONES
            return 0;
        }
    }

    static class SentMessageViewHolder extends RecyclerView.ViewHolder{
        private final SentMessageItemBinding binding;
        private ContratoAdapter.OnContratoClick onContratoClick;

        SentMessageViewHolder(SentMessageItemBinding itemBinding,ContratoAdapter.OnContratoClick contratoClick){
            super(itemBinding.getRoot());
            binding=itemBinding;
            onContratoClick=contratoClick;
        }

        void setData(MensajeUtil mensajeUtil){
            System.out.println("OCURRIO: MENSAJE TEXTO CREATE HOLDER 1");
            binding.tvSentMessage.setText(mensajeUtil.getMessage());
        }
    }

    static class ReceivedMessageViewHolder extends RecyclerView.ViewHolder{
        private final ReceivedMessageItemBinding binding;
        private ContratoAdapter.OnContratoClick onContratoClick;

        ReceivedMessageViewHolder(ReceivedMessageItemBinding itemBinding,ContratoAdapter.OnContratoClick contratoClick){
            super(itemBinding.getRoot());
            binding=itemBinding;
            onContratoClick=contratoClick;
        }

        void setData(MensajeUtil mensajeUtil){
            System.out.println("OCURRIO: MENSAJE TEXTO CREATE HOLDER 2");
            binding.tvReceivedMessage.setText(mensajeUtil.getMessage());
        }
    }
    static class ReceivedImageViewHolder extends RecyclerView.ViewHolder{
        private final ReceivedImageItemBinding binding;
        private ContratoAdapter.OnContratoClick onContratoClick;

        ReceivedImageViewHolder(ReceivedImageItemBinding itemBinding,ContratoAdapter.OnContratoClick contratoClick){
            super(itemBinding.getRoot());
            binding=itemBinding;
            onContratoClick=contratoClick;
        }

        void setData(MensajeUtil mensajeUtil){
            System.out.println("OCURRIO: MENSAJE TEXTO CREATE HOLDER 3");
            StorageReference storageReference = FirebaseStorage.getInstance().getReference(Constants.CARPETA_FOTO_MENSAJE+mensajeUtil.getMessage());
            try {
                File localFile = File.createTempFile("tempfile",".jpg");
                storageReference.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                        Bitmap bitmap= BitmapFactory.decodeFile(localFile.getAbsolutePath());
                        binding.ivReceivedImage.setImageBitmap(bitmap);
                        binding.ivReceivedImage.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                onContratoClick.onImageClick(bitmap);
                            }
                        });
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        binding.ivReceivedImage.setImageResource(R.drawable.bg_claro_transparente_02);
                    }
                });
            } catch (IOException e) {
                binding.ivReceivedImage.setImageResource(R.drawable.bg_claro_transparente_02);
            }
        }
    }
    static class  SentImageViewHolder extends RecyclerView.ViewHolder{
        private final SentImageItemBinding binding;
        private final ContratoAdapter.OnContratoClick onContratoClick;

        SentImageViewHolder(SentImageItemBinding itemBinding,ContratoAdapter.OnContratoClick contratoClick){
            super(itemBinding.getRoot());
            binding=itemBinding;
            onContratoClick=contratoClick;

        }

        void setData(MensajeUtil mensajeUtil){
            System.out.println("OCURRIO: MENSAJE TEXTO CREATE HOLDER 4");
            StorageReference storageReference = FirebaseStorage.getInstance().getReference(Constants.CARPETA_FOTO_MENSAJE+mensajeUtil.getMessage());
            try {
                File localFile = File.createTempFile("tempfile",".jpg");
                storageReference.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                        Bitmap bitmap= BitmapFactory.decodeFile(localFile.getAbsolutePath());
                        binding.ivSentImage.setImageBitmap(bitmap);
                        binding.ivSentImage.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                onContratoClick.onImageClick(bitmap);
                            }
                        });
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        binding.ivSentImage.setImageResource(R.drawable.bg_claro_transparente_02);
                    }
                });
            } catch (IOException e) {
                binding.ivSentImage.setImageResource(R.drawable.bg_claro_transparente_02);
            }
        }
    }

    static class SentContratoViewHolder extends RecyclerView.ViewHolder{
        private final SentContratoItemBinding binding;
        private ContratoAdapter.OnContratoClick onContratoClick;
        private ContratoHandler.OnReadUnoContratoListener onReadUnoContratoListener;

        SentContratoViewHolder(SentContratoItemBinding itemBinding,ContratoAdapter.OnContratoClick contratoClick){
            super(itemBinding.getRoot());
            binding=itemBinding;
            onContratoClick=contratoClick;
        }
        SentContratoViewHolder(SentContratoItemBinding itemBinding){
            super(itemBinding.getRoot());
            binding=itemBinding;
        }

        void setData(MensajeUtil mensajeUtil){
            ContratoHandler contratoHandler= new ContratoHandler();
            System.out.println("OCURRIO: MENSAJE TEXTO CREATE HOLDER 5");
            onReadUnoContratoListener=setOnReadDefaultContratosListener(onReadUnoContratoListener);
            contratoHandler.readUnoContratos(mensajeUtil.getMessage(),onReadUnoContratoListener);
        }

        ContratoHandler.OnReadUnoContratoListener  setOnReadDefaultContratosListener(ContratoHandler.OnReadUnoContratoListener listener){
            listener= new ContratoHandler.OnReadUnoContratoListener() {
                @Override
                public void onReadSuccess(ContratoPlantilla contratoPlantilla) {
                    binding.tvSentContTitulo.setText(contratoPlantilla.getTitle());
                    binding.tvSentContEstado.setText("Estado: Pendiente");
                    binding.tvSentContDescripcion.setText(contratoPlantilla.getDescription());
                    binding.consContratoMensajeSent.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            onContratoClick.onContratoClick(contratoPlantilla);
                        }
                    });
                }
            };
            return listener;
        }
    }
    static class ReceivedContratoViewHolder extends RecyclerView.ViewHolder{
        private final ReceivedContratoItemBinding binding;
        private ContratoAdapter.OnContratoClick onContratoClick;
        private ContratoHandler.OnReadUnoContratoListener onReadUnoContratoListener;

        ReceivedContratoViewHolder(ReceivedContratoItemBinding itemBinding,ContratoAdapter.OnContratoClick contratoClick){
            super(itemBinding.getRoot());
            binding=itemBinding;
            onContratoClick=contratoClick;
        }
        ReceivedContratoViewHolder(ReceivedContratoItemBinding itemBinding){
            super(itemBinding.getRoot());
            binding=itemBinding;
        }

        void setData(MensajeUtil mensajeUtil){
            ContratoHandler contratoHandler= new ContratoHandler();
            System.out.println("OCURRIO: MENSAJE TEXTO CREATE HOLDER 6");
            onReadUnoContratoListener=setOnReadDefaultContratosListener(onReadUnoContratoListener);
            contratoHandler.readUnoContratos(mensajeUtil.getMessage(),onReadUnoContratoListener);

        }
        ContratoHandler.OnReadUnoContratoListener  setOnReadDefaultContratosListener(ContratoHandler.OnReadUnoContratoListener listener){
            listener= new ContratoHandler.OnReadUnoContratoListener() {
                @Override
                public void onReadSuccess(ContratoPlantilla contratoPlantilla) {
                    binding.tvContItemTitulo2.setText(contratoPlantilla.getTitle());
                    binding.tvContItemDescripcion2.setText(contratoPlantilla.getDescription());
                    binding.consContratoMensajeReceived.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            onContratoClick.onContratoClick(contratoPlantilla);
                        }
                    });
                }
            };
            return listener;
        }
    }
}
