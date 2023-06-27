package com.adrianibanez.nosequeponerme.Pojos;


public class Calzado{
    private String imagen;
    private String nombre;
    private String color;
    private String estilo;
    private String talla;


    public Calzado() {

    }

    public Calzado(String imagen, String nombre, String color, String estilo,String talla) {
        this.imagen = imagen;
        this.nombre = nombre;
        this.color = color;
        this.estilo = estilo;
        this.talla = talla;
    }

    public String getImagen() {
        return imagen;
    }

    public String getNombre() {
        return nombre;
    }

    public String getColor() {
        return color;
    }

    public String getEstilo() {
        return estilo;
    }

    public String getTalla() {
        return talla;
    }

}

