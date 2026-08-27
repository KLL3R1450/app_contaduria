/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package entidades;

/**
 *
 * @author Osmar
 */
public class EFirmas {
    public String fecha_expiracion;
    public String fecha_renovacion;
    private int id_cliente;
    public String ruta_certificado;
    public String ruta_key;
    public String contrasena;

    public EFirmas(String fecha_expiracion, String fecha_renovacion, int id_cliente) {
        this.fecha_expiracion = fecha_expiracion;
        this.fecha_renovacion = fecha_renovacion;
        this.id_cliente = id_cliente;
    }

    public EFirmas(String fecha_expiracion, String fecha_renovacion, int id_cliente, String ruta_certificado, String ruta_key, String contrasena) {
        this.fecha_expiracion = fecha_expiracion;
        this.fecha_renovacion = fecha_renovacion;
        this.id_cliente = id_cliente;
        this.ruta_certificado = ruta_certificado;
        this.ruta_key = ruta_key;
        this.contrasena = contrasena;
    }
    
    public int getIdCliente(){
        return id_cliente;
    }

    public void setIdCliente(int id_cliente) {
        this.id_cliente = id_cliente;
    }
}
