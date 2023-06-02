package com.example.renthubpablo.controller.contratos;

import java.io.File;

public class ContratoPlantilla {
    String title;
    String owner;
    String fileName;
    String description;
    File archivo;

    public ContratoPlantilla(){}

    public ContratoPlantilla(String title, String owner, String fileName, String description) {
        this.title = title;
        this.owner = owner;
        this.fileName = fileName;
        this.description = description;
    }

    public File getArchivo() {
        return archivo;
    }

    public void setArchivo(File archivo) {
        this.archivo = archivo;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
