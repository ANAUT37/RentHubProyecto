package com.example.renthubpablo.view.fragments;

import static android.content.Context.MODE_PRIVATE;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.renthubpablo.R;
import com.example.renthubpablo.controller.MainActivity;
import com.example.renthubpablo.model.UsuarioHandler;
import com.example.renthubpablo.resources.Constants;
import com.example.renthubpablo.resources.DialogUtils;


public class GestionarCuentaFragment extends Fragment {
    private static UsuarioHandler usuarioHandler=new UsuarioHandler();
    Button logout,delete;
    public GestionarCuentaFragment() {
        // Required empty public constructor
    }

    public static GestionarCuentaFragment newInstance() {
        GestionarCuentaFragment fragment = new GestionarCuentaFragment();

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.fragment_gestionar_cuenta, container, false);

        logout=view.findViewById(R.id.bttnGesLogOut);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences sharedPreferences = getActivity().getSharedPreferences("renthub", MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                String correo=sharedPreferences.getString("userCorreo","");
                editor.putString(Constants.SHARED_PREFERENCES_TOKEN,"");
                editor.putString(Constants.SHARED_PREFERENCES_CORREO, "");
                editor.putString(Constants.SHARED_PREFERENCES_PASSWORD,"");
                editor.apply();
                usuarioHandler.removeToken(correo);
                Intent intent = new Intent(requireContext(), MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                requireActivity().finish();
            }
        });
        delete=view.findViewById(R.id.bttnGesDeleteAccount);
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogUtils dialogUtils= DialogUtils.newInstance("¡Atención!",
                        "¿Estás segurx de que quieres borrar tu cuenta?" +
                                "\n\n¡Todos tus datos serán eliminados permanentemente!",
                        true);
                dialogUtils.show(getActivity().getSupportFragmentManager(), "dialog_example");
            }
        });
        return view;
    }
}