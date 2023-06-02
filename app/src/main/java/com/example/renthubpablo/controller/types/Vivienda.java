package com.example.renthubpablo.controller.types;

import com.example.renthubpablo.controller.Inmueble;

public class Vivienda extends Inmueble {
    String tamanyo;
    String dormitorios;
    String banyos;
    String piso;
    boolean ascensor;
    boolean garaje;
    boolean trastero;

    public Vivienda(String piso,String tamanyo, String dormitorios, String banyos, boolean ascensor, boolean garaje, boolean trastero) {
        this.tamanyo = tamanyo;
        this.dormitorios = dormitorios;
        this.banyos = banyos;
        this.ascensor = ascensor;
        this.garaje = garaje;
        this.trastero = trastero;
        this.piso=piso;
    }

    public Vivienda() {

    }

    public String getPiso() {
        return piso;
    }

    public void setPiso(String piso) {
        this.piso = piso;
    }

    public String getTamanyo() {
        return tamanyo;
    }

    public void setTamanyo(String tamanyo) {
        this.tamanyo = tamanyo;
    }

    public String getDormitorios() {
        return dormitorios;
    }

    public void setDormitorios(String dormitorios) {
        this.dormitorios = dormitorios;
    }

    public String getBanyos() {
        return banyos;
    }

    public void setBanyos(String banyos) {
        this.banyos = banyos;
    }

    public boolean isAscensor() {
        return ascensor;
    }

    public void setAscensor(boolean ascensor) {
        this.ascensor = ascensor;
    }

    public boolean isGaraje() {
        return garaje;
    }

    public void setGaraje(boolean garaje) {
        this.garaje = garaje;
    }

    public boolean isTrastero() {
        return trastero;
    }

    public void setTrastero(boolean trastero) {
        this.trastero = trastero;
    }

    @Override
    public String toString() {
        return "Vivienda{" +
                "tamanyo='" + tamanyo + '\'' +
                ", dormitorios='" + dormitorios + '\'' +
                ", banyos='" + banyos + '\'' +
                ", ascensor=" + ascensor +
                ", garaje=" + garaje +
                ", trastero=" + trastero +
                '}';
    }
}
