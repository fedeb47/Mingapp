package com.seminario.sopalonion;

import android.net.Uri;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Publi {
    private ImageView fotoSubida;
    private TextView tvNombre;
    private int ID;
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


}
