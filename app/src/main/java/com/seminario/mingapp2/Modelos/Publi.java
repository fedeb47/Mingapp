package com.seminario.mingapp2.Modelos;

import android.widget.ImageView;
import android.widget.TextView;

public class Publi {
    public String ID;
    public String UserID;
    public String Nombre;
    public String LinkFoto;
    public String Descripcion;
    public String Precio;

    public Publi(String userID, String nombre, String linkFoto, String descripcion, String precio){
        this.UserID = userID;
        this.Nombre = nombre;
        this.LinkFoto = linkFoto;
        this.Descripcion = descripcion;
        this.Precio = precio;
    }

    public Publi() {}

    @Override
    public String toString() {
        return "Publi{" +
                "ID='" + ID + '\'' +
                ", UserID='" + UserID + '\'' +
                ", Nombre='" + Nombre + '\'' +
                ", LinkFoto='" + LinkFoto + '\'' +
                ", Descripcion='" + Descripcion + '\'' +
                ", Precio='" + Precio + '\'' +
                '}';
    }

    public String getID() { return ID; }

    public void setID(String ID) { this.ID = ID; }

    public void setUserID(String userID) {
        UserID = userID;
    }

    public void setNombre(String nombre) {
        Nombre = nombre;
    }

    public void setLinkFoto(String linkFoto) {
        LinkFoto = linkFoto;
    }

    public void setDescripcion(String descripcion) {
        Descripcion = descripcion;
    }

    public void setPrecio(String precio) {
        Precio = precio;
    }

    public String getUserID() {
        return UserID;
    }

    public String getNombre() {
        return Nombre;
    }

    public String getLinkFoto() {
        return LinkFoto;
    }

    public String getDescripcion() {
        return Descripcion;
    }

    public String getPrecio() {
        return Precio;
    }


}
