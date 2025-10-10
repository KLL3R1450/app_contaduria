/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package entidades;

import java.util.ArrayList;

/**
 *
 * @author Osmar
 */
public abstract class Personas {
    public int id_persona;
    public String nombre;
    public String rfc;
    public String cp;
    public String correo;
    
    public ArrayList<Integer> idsRegimenes = new ArrayList<>();

    
}
