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
public class Terceros extends Personas{

    
    public Terceros(int id_tercero, String nombre, String rfc, String cp, String correo) {
        this.id_persona = id_tercero;
        this.nombre = nombre;
        this.rfc = rfc;
        this.cp = cp;
        this.correo = correo;
    }

    public Terceros(int id_tercero, String nombre, String rfc, String cp) {
        this.id_persona = id_tercero;
        this.nombre = nombre;
        this.rfc = rfc;
        this.cp = cp;
    }
    
    public Terceros(int id_tercero){
        this.id_persona = id_tercero;
    }
    
    public void updateTercero(String nombre, String rfc, String cp, String correo){
        this.nombre = nombre;
        this.rfc = rfc;
        this.cp = cp;
        this.correo = correo;
    }
    

}
