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
    public int id_contador;
    public ArrayList<Integer> idsRegimenes = new ArrayList<>();
    
    public Cliente(String nombre, String rfc, String cp, int monto,int id_contador){
        this.nombre = nombre;
        this.rfc = rfc;
        this.cp = cp;
        this.honorarios = monto;
        this.id_contador = id_contador;
    }

    public Cliente(String nombre, String rfc, String cp, String correo, int monto, int id_contador) {
        this.id_cliente = id_cliente;
        this.nombre = nombre;
        this.rfc = rfc;
        this.cp = cp;
        this.correo = correo;
        this.honorarios = monto;
        this.id_contador = id_contador;
    }
    
    public Cliente(int id_cliente){
        this.id_cliente = id_cliente;
    };
    
    public int getId(){
        return this.id_cliente;
    }
    
}
