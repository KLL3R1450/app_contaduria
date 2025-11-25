/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package entidades;

/**
 *
 * @author Osmar
 */
public class Declaracion {
    private int id_declaracion;
    private int id_cliente;
    public int anio;
    public int mes;
    public int gastos;
    public int ingresos;
    public int declarado;

    public Declaracion(int id_declaracion, int id_cliente, int anio, int mes, int gastos, int ingresos, int declarado) {
        this.id_declaracion = id_declaracion;
        this.id_cliente = id_cliente;
        this.anio = anio;
        this.mes = mes;
        this.gastos = gastos;
        this.ingresos = ingresos;
        this.declarado = declarado;
    }
    
    
    
    public int getIdDeclaracion(){
        return id_declaracion;
    }

    
    public int getIdCliente(){
        return id_cliente;
    }
    
    
}
