package entidades;

import java.util.ArrayList;

/**
 *
 * @author Osmar
 */
public class Cliente extends Personas{
    public int honorarios;
    public int id_contador;
    
    public Cliente(String nombre, String rfc, String cp, int monto,int id_contador){
        this.nombre = nombre;
        this.rfc = rfc;
        this.cp = cp;
        this.honorarios = monto;
        this.id_contador= id_contador;
    }

    public Cliente(String nombre, String rfc, String cp, String correo, int monto, int id_contador) {
        this.nombre = nombre;
        this.rfc = rfc;
        this.cp = cp;
        this.correo = correo;
        this.honorarios = monto;
        this.id_contador = id_contador;
    }
    
    public Cliente(int id_cliente){
        this.id_persona = id_cliente;
    };
    
    
}
