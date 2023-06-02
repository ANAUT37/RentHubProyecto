package com.example.renthubpablo.controller.types;

import android.graphics.Bitmap;
import android.net.Uri;

public class Valoracion {
    Bitmap bitmap;
    String senderPfp;
    String sender;
    String nombreAutor;
    String opinion;
    float estrellas;
    String receiver;

    public Valoracion(String idAutor, String nombreAutor, String opinion, float estrellas, String idAnuncio) {
        this.sender = idAutor;
        this.nombreAutor = nombreAutor;
        this.opinion = opinion;
        this.estrellas = estrellas;
        this.receiver = idAnuncio;
    }
    public Valoracion(){

    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public String getSenderPfp() {
        return senderPfp;
    }

    public void setSenderPfp(String senderPfp) {
        this.senderPfp = senderPfp;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getNombreAutor() {
        return nombreAutor;
    }

    public void setNombreAutor(String nombreAutor) {
        this.nombreAutor = nombreAutor;
    }

    public String getOpinion() {
        return opinion;
    }

    public void setOpinion(String opinion) {
        this.opinion = opinion;
    }

    public float getEstrellas() {
        return estrellas;
    }

    public void setEstrellas(float estrellas) {
        this.estrellas = estrellas;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }
}
