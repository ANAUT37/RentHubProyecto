package com.example.renthubpablo.resources;

import androidx.annotation.Nullable;

import java.util.Objects;

public class BusquedaUtil {
    String direccion;
    String categoria;
    double latitud;
    double longitud;

    public BusquedaUtil(String direccion, String categoria, double latitud, double longitud) {
        this.direccion = direccion;
        this.categoria = categoria;
        this.latitud = latitud;
        this.longitud = longitud;
    }

    public BusquedaUtil() {
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public double getLatitud() {
        return latitud;
    }

    public void setLatitud(double latitud) {
        this.latitud = latitud;
    }

    public double getLongitud() {
        return longitud;
    }

    public void setLongitud(double longitud) {
        this.longitud = longitud;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        BusquedaUtil other = (BusquedaUtil) obj;
        return Objects.equals(direccion, other.direccion) && categoria == other.direccion;
    }

    @Override
    public int hashCode() {
        return Objects.hash(direccion, categoria);
    }
}
