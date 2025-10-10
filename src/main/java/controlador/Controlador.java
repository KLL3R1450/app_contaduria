package controlador;

import persistencia.RegimenesDAO;
import persistencia.ClientesDAO;
import persistencia.ContadoresDAO;
import persistencia.DeclaracionDAO;
import persistencia.EFirmasDAO;
import persistencia.TercerosDAO;
import entidades.Cliente;
import entidades.Contadores;
import entidades.Declaracion;
import entidades.EFirmas;
import entidades.Regimenes;
import entidades.Terceros;
import java.util.ArrayList;
import javax.swing.JOptionPane;

public class Controlador implements IControler{

    private ClientesDAO dBClientes;
    private ContadoresDAO dBContadores;
    private RegimenesDAO dBRegimenes;
    private TercerosDAO dBTerceros;
    private DeclaracionDAO dBDeclaraciones;
    private EFirmasDAO dBFirmas;
    
    public ArrayList<Contadores> contadores;
    public ArrayList<Cliente> clientes;
    public ArrayList<Declaracion> declaraciones;
    public ArrayList<EFirmas> eFirmas;
    public ArrayList<Regimenes> regimenes;
    public ArrayList<Terceros> terceros;
    
    private static Controlador controlador = null;
    
    @Override
    public void cargarTodo() {
        contadores = dBContadores.getContadores();
        clientes = dBClientes.getClientes();
        regimenes = dBRegimenes.getRegimenes();
        eFirmas = dBFirmas.getAllFirmas();
        declaraciones = dBDeclaraciones.getAllDeclaraciones();
        terceros =  dBTerceros.getTerceros();
        
        JOptionPane.showMessageDialog(null, "Datos Extraidos con exito puedes empezar el dia");
    }

    private Controlador() {
        dBClientes = new ClientesDAO();
        dBContadores = new ContadoresDAO();
        dBRegimenes = new RegimenesDAO();
        dBTerceros = new TercerosDAO();
        dBDeclaraciones = new DeclaracionDAO();
        dBFirmas = new EFirmasDAO();
        contadores = new ArrayList<>();
        clientes  = new ArrayList<>();
        declaraciones = new ArrayList<>();
        eFirmas =  new ArrayList<>();
        regimenes = new ArrayList<>();
        terceros = new ArrayList<>();
    }
    
    public static Controlador getControlador(){
        if(controlador == null) controlador = new Controlador();
        
        return controlador;
    }


}
