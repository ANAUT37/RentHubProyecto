package com.example.renthubpablo.view.fragments;

import static com.example.renthubpablo.view.fragments.SearchFragment.globalContext;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.renthubpablo.R;
import com.example.renthubpablo.controller.Usuario;
import com.example.renthubpablo.model.ContactoHandler;
import com.example.renthubpablo.model.UsuarioHandler;
import com.example.renthubpablo.resources.Constants;
import com.example.renthubpablo.resources.ContactoUtil;
import com.example.renthubpablo.view.ChatActivity;
import com.example.renthubpablo.view.adapter.ChatAdapter;
import com.example.renthubpablo.view.adapter.ChatItemSelectedListener;

import java.util.ArrayList;
import java.util.List;


public class ChatFragment extends Fragment {
    private static ChatAdapter chatAdapter;
    ArrayList<Usuario> usuarioArrayList=new ArrayList<>();
    List<ContactoUtil> listaContactos= new ArrayList<>();
    private static String userCorreo, idAnuncio;
    private static ContactoHandler.OnReadContactos onReadContactos;
    private static UsuarioHandler.OnReadListener onReadListener;
    private static ChatItemSelectedListener chatItemSelectedListener;
    private ProgressBar progressBar;
    TextView noChats;
    RecyclerView recyclerView;
    public ChatFragment() {
        // Required empty public constructor
    }

    public static ChatFragment newInstance(String param1, String param2) {
        ChatFragment fragment = new ChatFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_chat, container, false);
        //setAnunciosList();
        usuarioArrayList.clear();

        noChats=view.findViewById(R.id.noChatsEncontrados);
        @SuppressLint({"MissingInflatedId", "LocalSuppress"})
        SharedPreferences prefs = getActivity().getSharedPreferences(Constants.SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);
        userCorreo=prefs.getString(Constants.SHARED_PREFERENCES_CORREO, "");
        progressBar=view.findViewById(R.id.progressBarChatFragment);
        ContactoHandler contactoHandler= new ContactoHandler();
        onReadContactos=setOnReadContactos(onReadContactos);
        recyclerView=view.findViewById(R.id.recyclerChat);
        contactoHandler.getContactosFromUser(userCorreo,onReadContactos);
        onReadListener = setOnReadListener(onReadListener);

        chatAdapter= new ChatAdapter(getContext(), usuarioArrayList, new ChatItemSelectedListener() {
            @Override
            public void onItemClicked(Usuario usuario, int position) {
                Intent intent = new Intent(getContext(), ChatActivity.class);
                intent.putExtra("email", usuario.getEmail());
                intent.putExtra("anuncio",listaContactos.get(position).getAnuncio());
                intent.putExtra("categoria",listaContactos.get(position).getCategoria());
                startActivity(intent);
            }
        });
        recyclerView.setAdapter(chatAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        progressBar.setVisibility(View.GONE);
        if(usuarioArrayList.size()==0){
            noChats.setVisibility(View.VISIBLE);
        }

        listaContactos.clear();
        return view;

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }
    private ContactoHandler.OnReadContactos setOnReadContactos(ContactoHandler.OnReadContactos listener){
        listener=new ContactoHandler.OnReadContactos() {
            @Override
            public void onReadSuccess(ContactoUtil contactoUtil) {
                listaContactos.add(contactoUtil);
            }

            @Override
            public void onReadComplete() {
                for (ContactoUtil contactoUtil: listaContactos){
                    UsuarioHandler usuarioHandler = new UsuarioHandler();
                    try {
                        usuarioHandler.readUserData(contactoUtil.getCorreo(), onReadListener,contactoUtil.getTitulo());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            }
        };
        return listener;
    }

    private UsuarioHandler.OnReadListener setOnReadListener(UsuarioHandler.OnReadListener onReadListener) {
        onReadListener = new UsuarioHandler.OnReadListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onReadSuccess(Usuario usuario) {
                usuarioArrayList.add(usuario);
                chatAdapter.notifyDataSetChanged();
                if(usuarioArrayList.size()!=0){
                    noChats.setVisibility(View.GONE);
                }
            }

            @Override
            public void onReadError(String error) {
                System.out.println("OCURRIO: " + error);
            }
        };
        return onReadListener;
    }

}