/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package entidades;

import java.util.ArrayList;

/**
 *
 * @author Osmar
 */
public class Contadores {
    private int id_contador;
    public String nombre;
    public String contacto;
    public ArrayList<Integer> idsClientes;

    public Contadores( String nombre, String contacto) {
        this.nombre = nombre;
        this.contacto = contacto;
    }

    public Contadores(int id_contador, String nombre, String contacto) {
        this.id_contador = id_contador;
        this.nombre = nombre;
        this.contacto = contacto;
    }
    
    public int getId(){
        return this.id_contador;
    }
}
