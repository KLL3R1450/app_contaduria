/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package entidades;


public class Regimenes {
    private int id_regimen;
    public String regimen;

    public Regimenes(int id_regimen, String regimen) {
        this.id_regimen = id_regimen;
        this.regimen = regimen;
    }
    
    public int getId(){
        return id_regimen;
    }
}
