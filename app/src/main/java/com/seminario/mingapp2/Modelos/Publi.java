package com.seminario.mingapp2.Modelos;

import android.widget.ImageView;
import android.widget.TextView;

public class Publi {
    public String Descripcion;
    public String LinkFoto;
    public String Nombre;
    public String Precio;
    public String UserID;


    public Publi(String descripcion, String linkFoto, String nombre, String precio, String userID){
        this.Descripcion = descripcion;
        this.LinkFoto = linkFoto;
        this.Nombre = nombre;
        this.Precio = precio;
        this.UserID = userID;
    }

    public Publi() {}

    @Override
    public String toString() {
        return "Publi{" +
                ", UserID='" + UserID + '\'' +
                ", Nombre='" + Nombre + '\'' +
                ", LinkFoto='" + LinkFoto + '\'' +
                ", Descripcion='" + Descripcion + '\'' +
                ", Precio='" + Precio + '\'' +
                '}';
    }



}
