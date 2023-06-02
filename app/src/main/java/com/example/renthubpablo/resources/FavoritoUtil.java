package com.example.renthubpablo.resources;

public class FavoritoUtil {
    String anuncio;
    String categoria;
    String user;

    public FavoritoUtil(String anuncio,String categoria, String user) {
        this.anuncio = anuncio;
        this.user = user;
        this.categoria=categoria;
    }
    public FavoritoUtil(){

    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public String getAnuncio() {
        return anuncio;
    }

    public void setAnuncio(String anuncio) {
        this.anuncio = anuncio;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }
}
