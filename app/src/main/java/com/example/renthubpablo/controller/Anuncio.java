package com.example.renthubpablo.controller;

import android.net.Uri;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.GeoPoint;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;

public class Anuncio {
    String idAnuncio;
    String latitud;
    String longitud;
    String titulo;
    String direccion;
    String descripcion;
    Timestamp fechaPublicacion;
    Date fecha;
    String emailEmisor;
    Inmueble inmueble;
    String categoria;
    GeoPoint localizacion;
    int precio;
    int favoritos;
    int visitas;
    List<String> imagen;
    List<Uri> imagesUri;

    public Anuncio() {
    }

    public Anuncio(String idAnuncio, String titulo, String descripcion, Date fechaPublicacion, String emisor, Inmueble inmueble, int favoritos, int visitas, List<String> imagen) {
        this.idAnuncio = idAnuncio;
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.fechaPublicacion = new Timestamp(fechaPublicacion);
        this.emailEmisor = emisor;
        this.inmueble = inmueble;
        this.favoritos = favoritos;
        this.visitas = visitas;
        this.imagen = imagen;
    }
    public Anuncio(String longitud, String latitud,String titulo, String descripcion, Date fechaPublicacion, String emisor, Inmueble inmueble, int favoritos, int visitas, List<String> imagen) {
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.fechaPublicacion = new Timestamp(fechaPublicacion);
        this.emailEmisor = emisor;
        this.inmueble = inmueble;
        this.favoritos = favoritos;
        this.visitas = visitas;
        this.imagen = imagen;
        this.latitud=latitud;
        this.longitud=longitud;
    }

    public Anuncio(String mTitulo, String mDescripcion, String mLatitud, String mLongitud, String mPrecio, String mCategoria, String mEmailEmisor,Date mFechaPublicacion, GeoPoint mLocalizacion) {
        this.titulo=mTitulo;
        this.descripcion=mDescripcion;
        this.latitud=mLatitud;
        this.longitud=mLongitud;
        this.precio=Integer.parseInt(mPrecio);
        this.categoria=mCategoria;
        this.emailEmisor=mEmailEmisor;
        this.fechaPublicacion=new Timestamp(mFechaPublicacion);
        this.localizacion=mLocalizacion;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public GeoPoint getLocalizacion() {
        return localizacion;
    }

    public void setLocalizacion(GeoPoint localizacion) {
        this.localizacion = localizacion;
    }

    public String getIdAnuncio() {
        return idAnuncio;
    }

    public void setIdAnuncio(String idAnuncio) {
        this.idAnuncio = idAnuncio;
    }

    public String getLatitud() {
        return latitud;
    }

    public void setLatitud(String latitud) {
        this.latitud = latitud;
    }

    public String getLongitud() {
        return longitud;
    }

    public void setLongitud(String longitud) {
        this.longitud = longitud;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }


    public String getEmailEmisor() {
        return emailEmisor;
    }

    public void setEmailEmisor(String emailEmisor) {
        this.emailEmisor = emailEmisor;
    }

    public Inmueble getInmueble() {
        return inmueble;
    }

    public void setInmueble(Inmueble inmueble) {
        this.inmueble = inmueble;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public int getPrecio() {
        return precio;
    }

    public void setPrecio(int precio) {
        this.precio = precio;
    }

    public int getFavoritos() {
        return favoritos;
    }

    public void setFavoritos(int favoritos) {
        this.favoritos = favoritos;
    }

    public int getVisitas() {
        return visitas;
    }

    public void setVisitas(int visitas) {
        this.visitas = visitas;
    }

    public List<String> getImagen() {
        return imagen;
    }

    public void setImagen(List<String> imagen) {
        this.imagen = imagen;
    }

    public void setFechaPublicacion(Timestamp fechaPublicacion) {
        this.fechaPublicacion = fechaPublicacion;
    }

    public Timestamp getFechaPublicacion() {
        return fechaPublicacion;
    }

    public List<Uri> getImagesUri() {
        return imagesUri;
    }

    public void setImagesUri(List<Uri> imagesUri) {
        this.imagesUri = imagesUri;
    }

    public String generarNombreImagenAnuncio(){
        String result = "";
        try {
            Random rand = new Random();
            int seed = rand.nextInt();
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(String.valueOf(seed).getBytes());
            byte[] bytes = md.digest();
            StringBuilder sb = new StringBuilder();
            for (byte b : bytes) {
                sb.append(String.format("%02x", b));
            }
            result = sb.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        return result.substring(0, 20)+".jpg"; // Devuelve solo los primeros 20 caracteres
    }

    @Override
    public String toString() {
        return "Anuncio{" +
                "idAnuncio='" + idAnuncio + '\'' +
                ", latitud='" + latitud + '\'' +
                ", longitud='" + longitud + '\'' +
                ", titulo='" + titulo + '\'' +
                ", direccion='" + direccion + '\'' +
                ", descripcion='" + descripcion + '\'' +
                ", fechaPublicacion=" + fechaPublicacion +
                ", emailEmisor='" + emailEmisor + '\'' +
                ", inmueble=" + inmueble +
                ", categoria='" + categoria + '\'' +
                ", localizacion=" + localizacion +
                ", precio=" + precio +
                ", favoritos=" + favoritos +
                ", visitas=" + visitas +
                ", imagen=" + imagen +
                ", imagesUri=" + imagesUri +
                '}';
    }
}
