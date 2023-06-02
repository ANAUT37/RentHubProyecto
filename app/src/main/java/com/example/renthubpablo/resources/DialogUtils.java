package com.example.renthubpablo.resources;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.renthubpablo.R;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

public class DialogUtils extends androidx.fragment.app.DialogFragment {
    private static final String ARG_DOUBLE_BUTTON = "double_button";
    private OnDismissListener dismissListener;
    private static final String ARG_TITLE = "title";
    private static final String ARG_MESSAGE = "message";
    private String title,message;
    private boolean doubleButton;

    public static DialogUtils newInstance(String title, String message,boolean doubleButton) {
        DialogUtils fragment = new DialogUtils();
        Bundle args = new Bundle();
        args.putBoolean(ARG_DOUBLE_BUTTON,doubleButton);
        args.putString(ARG_TITLE, title);
        args.putString(ARG_MESSAGE, message);
        fragment.setArguments(args);
        return fragment;
    }
    public void setOnDismissListener(OnDismissListener listener) {
        dismissListener = listener;
    }
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Bundle args = getArguments();
        if (args != null) {
            doubleButton=args.getBoolean(ARG_DOUBLE_BUTTON);
            title = args.getString(ARG_TITLE);
            message = args.getString(ARG_MESSAGE);
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.custom_dialog, null);
        builder.setView(dialogView);

        TextView titleTextView = dialogView.findViewById(R.id.dialog_title);
        TextView messageTextView = dialogView.findViewById(R.id.dialog_message);
        Button acceptButton = dialogView.findViewById(R.id.dialog_accept);
        @SuppressLint({"MissingInflatedId", "LocalSuppress"})
        Button cancelButton=dialogView.findViewById(R.id.dialog_cancelar);
        if(!doubleButton){
            cancelButton.setEnabled(false);
            cancelButton.setVisibility(View.GONE);
        }
        titleTextView.setText(title);
        messageTextView.setText(message);
        acceptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        return builder.create();
    }

    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        super.onDismiss(dialog);
        if (dismissListener != null) {
            dismissListener.onDismiss();
        }
    }


    public interface OnDismissListener {
        void onDismiss();
    }
}
