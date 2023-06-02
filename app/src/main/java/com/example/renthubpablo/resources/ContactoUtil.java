package com.example.renthubpablo.resources;

public class ContactoUtil {
    String correo;
    String anuncio;
    String titulo;
    String categoria;

    public ContactoUtil(String correo, String anuncio, String titulo, String categoria) {
        this.correo = correo;
        this.anuncio = anuncio;
        this.titulo=titulo;
        this.categoria=categoria;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getAnuncio() {
        return anuncio;
    }

    public void setAnuncio(String anuncio) {
        this.anuncio = anuncio;
    }

    @Override
    public String toString() {
        return "ContactoUtil{" +
                "correo='" + correo + '\'' +
                ", anuncio='" + anuncio + '\'' +
                '}';
    }
}
