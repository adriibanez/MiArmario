package com.adrianibanez.nosequeponerme.Pojos;

import java.io.Serializable;
import java.util.ArrayList;

public class Usuario implements Serializable {

    private String nombre;
    private String email;
    private String pass;
    private ArrayList<Camiseta> lps;
    private ArrayList<Pantalon> lpi;
    private ArrayList<Calzado> lc;


    public Usuario() {
    }

    public Usuario(String email, String pass) {
        this.email = email;
        this.pass = pass;
    }

    public Usuario(String nombre, String email, String pass) {
        this.nombre = nombre;
        this.email = email;
        this.pass = pass;

    }

    public Usuario(String nombre, String email, String pass, ArrayList<Camiseta> lps, ArrayList<Pantalon> lpi, ArrayList<Calzado> lc) {
        this.nombre = nombre;
        this.email = email;
        this.pass = pass;
        this.lps = lps;
        this.lpi = lpi;
        this.lc = lc;

    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPass() {
        return pass;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }

    public void addRopa(Object o)
    {
        if(o instanceof Camiseta)
        {
            lps.add((Camiseta) o);
        }
        else if (o instanceof Pantalon){
            lpi.add((Pantalon) o);
        }
        else{
            lc.add((Calzado) o);
        }
    }

    @Override
    public String toString() {
        return "Usuario{" +
                "nombre='" + nombre + '\'' +
                ", email='" + email + '\'' +
                ", pass='" + pass + '\''+
                '}';
    }
}
