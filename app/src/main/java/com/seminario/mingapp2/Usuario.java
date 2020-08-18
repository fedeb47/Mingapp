package com.seminario.mingapp2;

public class Usuario {


   // private String ID;
    private String Nombre;
    private String FotoURL;
    private String Direccion;
    private String Descripcion;
    private SubirPublicacion[] publicaciones;
    private Usuario[]  Seguidores;
    private SubirPublicacion[] Favoritos;
    private String Conversaciones;

    public Usuario(){

    }

    public Usuario(String nombre, String fotoURL) {
        this.Nombre = nombre;
        this.FotoURL = fotoURL;
    }

    public String getNombre() {
        return Nombre;
    }

    public void setNombre(String nombre) {
        Nombre = nombre;
    }

    public String getFotoURL() {
        return FotoURL;
    }

    public void setFotoURL(String fotoURL) {
        FotoURL = fotoURL;
    }

    public String getDireccion() {
        return Direccion;
    }

    public void setDireccion(String direccion) {
        Direccion = direccion;
    }

    public String getDescripcion() {
        return Descripcion;
    }

    public void setDescripcion(String descripcion) {
        Descripcion = descripcion;
    }

    public SubirPublicacion[] getPublicaciones() {
        return publicaciones;
    }

    public void setPublicaciones(SubirPublicacion[] publicaciones) {
        this.publicaciones = publicaciones;
    }

    public Usuario[] getSeguidores() {
        return Seguidores;
    }

    public void setSeguidores(Usuario[] seguidores) {
        Seguidores = seguidores;
    }

    public SubirPublicacion[] getFavoritos() {
        return Favoritos;
    }

    public void setFavoritos(SubirPublicacion[] favoritos) {
        Favoritos = favoritos;
    }

    public String getConversaciones() {
        return Conversaciones;
    }

    public void setConversaciones(String conversaciones) {
        Conversaciones = conversaciones;
    }
}
