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
import java.util.HashMap;
import java.util.Map;
import javax.swing.JOptionPane;

public class Controlador implements IControler{

    private ClientesDAO dBClientes;
    private ContadoresDAO dBContadores;
    private RegimenesDAO dBRegimenes;
    private TercerosDAO dBTerceros;
    private DeclaracionDAO dBDeclaraciones;
    private EFirmasDAO dBFirmas;
    
    public ArrayList<Contadores> contadores;
    public Map<Integer,Cliente> clientes;
    public ArrayList<Declaracion> declaraciones;
    public ArrayList<EFirmas> eFirmas;
    public ArrayList<Regimenes> regimenes;
    public Map<Integer,Terceros> terceros;
    
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
        clientes  = new HashMap<>();
        declaraciones = new ArrayList<>();
        eFirmas =  new ArrayList<>();
        regimenes = new ArrayList<>();
        terceros = new HashMap<>();
    }
    
    public static Controlador getControlador(){
        if(controlador == null) controlador = new Controlador();
        
        return controlador;
    }

    public ArrayList<Contadores> getAllContadores(){
        return this.contadores;
    }
    
    public Contadores getContadorByName(String nombre){
        for(Contadores c : contadores) if(nombre.equals(c.nombre)) return c;
        
        return null;
    }
    
    public ArrayList<Cliente> getClientesByContador(int idContador){
        ArrayList<Cliente> clients = new ArrayList();
        
        Contadores contador = contadores.get((idContador-1));
        
        for(Integer i : contador.idsClientes){
             clients.add(clientes.get(i));
        }
        
        return clients;
    }
    
    public String insertContador(Contadores contador){
        
        String respuesta = dBContadores.insertContador(contador);
        
        if("correcto".equals(respuesta)) contadores.add(contador);
        
        return respuesta;
    }
    
    public String deleteContador(int idContador){
        String respuesta = dBContadores.deleteContador(idContador);
        
        if("correcto".equals(respuesta)) contadores.remove((idContador-1));
                
        return respuesta;
    }
    
    public String updateContactoConta(String contacto, int idContador){
        String respuesta = dBContadores.updateContactoContador(contacto, idContador);
        
        if("correcto".equals(respuesta)) contadores = dBContadores.getContadores();
        
        return respuesta;
    }
    
    public Map<Integer,Cliente> getAllClientes(){
        return clientes;
    }
        
    public String insertCliente(int idContador, Cliente cliente){
        String respuesta =  dBClientes.insertCliente(cliente);
        
        if("correcto".equals(respuesta)) dBClientes.getClientes();
        
        return respuesta;
    }

    public String deleteCliente(int idCliente){
        String respuesta = dBClientes.deleteCliente(idCliente);
        
        if("correcto".equals(respuesta)) clientes.remove(idCliente-1);
        
        return respuesta;
    }
    
    public String addRegimenACliente(int idCliente, int idRegimen){
        String respuesta = dBClientes.agregarRegimenCliente(idCliente, idRegimen);
        
        if("correcto".equals(respuesta)){
            
           clientes.get(idCliente).idsRegimenes.add(idRegimen); 
        }
        
        return respuesta;
    }
    
    public String deleteRegimenACliente(int idCliente,  int idRegimen){
        String respuesta = dBClientes.deleteRegimenCliente(idCliente, idRegimen);
        
        if("correcto".equals(respuesta)) {
            
           clientes.get(idCliente).idsRegimenes.remove((Integer) idRegimen); 
        }
            
        return respuesta;
    }
    
    public Map<Integer, Terceros> getTerceros(){
        return terceros;
    }
    
    public String insertTercero(Terceros t, ArrayList<Integer> clientesT){
        String respuesta = dBTerceros.insertTercero(t, clientesT);
        
        if("correcto".equals(respuesta)) terceros = dBTerceros.getTerceros();
        
        return respuesta;
    }
    
    public String relacionarClientes(int idTercero, ArrayList<Integer> clientesT){
        String respuesta = dBTerceros.relacionarClientes(idTercero, clientesT);
        
        return respuesta;
    }
    
    public String borrarTercero(int idTercero){
        String respuesta = dBTerceros.borrarTercero(idTercero);
        
        if("correcto".equals(respuesta)){
            
            terceros.remove(idTercero);
        }
        
        return respuesta;
    }
    
    public String borrarRegimenTerero(int idTercero, int idRegimen){
        String respuesta = dBTerceros.eliminarRegimenTercero(idTercero, idRegimen);
        
        if("correcto".equals(respuesta)){
            terceros.get(idTercero).idsRegimenes.remove((Integer) idRegimen);
        }
        
        return respuesta;
    }
    
    public String agregarRegimenTercerro(int idTercero, int idRegimen){
        String respuesta = dBTerceros.insertarRegimenTerero(idTercero, idRegimen);
        
        if("correcto".equals(respuesta)){
            terceros.get(idTercero).idsRegimenes.add((Integer) idRegimen);
        }
        
        
        return respuesta;
    }
    
    
}
