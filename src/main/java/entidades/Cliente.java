package entidades;

import java.util.ArrayList;

/**
 *
 * @author Osmar
 */
public class Cliente {
    private int id_cliente;
    public String nombre;
    public String rfc;
    public String cp;
    public String correo = "";
    public int honorarios;
    public ArrayList<Regimenes> regimenes = new ArrayList<>();
    
    public Cliente(String nombre, String rfc, String cp, int monto){
        this.id_cliente = id_cliente;
        this.nombre = nombre;
        this.rfc = rfc;
        this.cp = cp;
        this.honorarios = monto;
    }

    public Cliente(String nombre, String rfc, String cp, String correo, int monto) {
        this.id_cliente = id_cliente;
        this.nombre = nombre;
        this.rfc = rfc;
        this.cp = cp;
        this.correo = correo;
        this.honorarios = monto;
    }
    
    public Cliente(int id_cliente){
        this.id_cliente = id_cliente;
    };
    
    public int getId(){
        return this.id_cliente;
    }
    
}
