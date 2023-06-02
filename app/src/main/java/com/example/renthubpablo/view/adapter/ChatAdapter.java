package com.example.renthubpablo.view.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.renthubpablo.R;
import com.example.renthubpablo.controller.Anuncio;
import com.example.renthubpablo.controller.Usuario;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ChatViewHolder> {
    private ChatItemSelectedListener chatItemSelectedListener;
    Context context;
    ArrayList<Usuario> usuarioArrayList;


    public ChatAdapter(Context context, ArrayList<Usuario> usuarioArrayList, ChatItemSelectedListener chatItemSelectedListener){
        this.context=context;
        this.usuarioArrayList=usuarioArrayList;
        this.chatItemSelectedListener=chatItemSelectedListener;
    }

    @NonNull
    @Override
    public ChatAdapter.ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater=LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.chat_item,parent,false);
        return new ChatAdapter.ChatViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ChatAdapter.ChatViewHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.userName.setText(usuarioArrayList.get(position).getNombre()+" "+usuarioArrayList.get(position).getApellido());
        holder.notread.setVisibility(View.GONE);
        holder.anuncio.setText(usuarioArrayList.get(position).getDescripcion());
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chatItemSelectedListener.onItemClicked(usuarioArrayList.get(position),position);
            }
        });

        StorageReference storageReference = FirebaseStorage.getInstance()
                .getReference("pfp/" + usuarioArrayList.get(position).getPfp());
        try {
            File localFile = File.createTempFile("tempfile", ".jpg");
            storageReference.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                    Bitmap bitmap = BitmapFactory.decodeFile(localFile.getAbsolutePath());
                    holder.pfp.setImageBitmap(bitmap);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    holder.pfp.setImageResource(R.drawable.user_no_pic);
                }
            });
        } catch (IOException e) {
            System.out.println("OCURRIO: " + e.getMessage());
        }
    }

    @Override
    public int getItemCount() {
        return usuarioArrayList.size();
    }

    public static class ChatViewHolder extends RecyclerView.ViewHolder{
        CardView cardView;
        TextView userName,lastMessage,date, anuncio;
        CircleImageView pfp;
        ImageView notread;
        public ChatViewHolder(@NonNull View itemView) {
            super(itemView);
            userName=itemView.findViewById(R.id.tvNombreChatItem);
            pfp=itemView.findViewById(R.id.ivPfpChatItem);
            notread=itemView.findViewById(R.id.ivUnreadChatItem);
            anuncio=itemView.findViewById(R.id.tvAnuncioChatItem);
            cardView=itemView.findViewById(R.id.cardViewChatItem);

        }
    }
}
