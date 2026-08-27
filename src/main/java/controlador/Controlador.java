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
import entidades.Pago;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.JOptionPane;

public class Controlador implements IControler{

    private ClientesDAO dBClientes;
    private ContadoresDAO dBContadores;
    private RegimenesDAO dBRegimenes;
    private TercerosDAO dBTerceros;
    private DeclaracionDAO dBDeclaraciones;
    private EFirmasDAO dBFirmas;
    private persistencia.PagosDAO dBPagos;
    
    public Map<Integer,Contadores> contadores;
    public Map<Integer,Cliente> clientes;
    public Map<Integer,Declaracion> declaraciones;
    public Map<Integer,EFirmas> eFirmas;
    public ArrayList<Regimenes> regimenes;
    public Map<Integer,Terceros> terceros;
    
    private static Controlador controlador = null;
    
    @Override
    public void cargarTodo() {
        try {
            contadores = dBContadores.getContadores();
            clientes = dBClientes.getClientes();
            regimenes = dBRegimenes.getRegimenes();
            eFirmas = dBFirmas.getAllFirmas();
            declaraciones = dBDeclaraciones.getAllDeclaraciones();
            terceros =  dBTerceros.getTerceros();
            
            JOptionPane.showMessageDialog(null, "Datos Extraidos con exito puedes empezar el dia");
        } catch (RuntimeException ex) {
            JOptionPane.showMessageDialog(null, "Fallo critico al cargar los datos de la base de datos:\n" + ex.getMessage(), "Error de Inicializacion", JOptionPane.ERROR_MESSAGE);
        }
    }

    private Controlador() {
        dBClientes = new ClientesDAO();
        dBContadores = new ContadoresDAO();
        dBRegimenes = new RegimenesDAO();
        dBTerceros = new TercerosDAO();
        dBDeclaraciones = new DeclaracionDAO();
        dBFirmas = new EFirmasDAO();
        dBPagos = new persistencia.PagosDAO();
        contadores = new HashMap<>();
        clientes  = new HashMap<>();
        declaraciones = new HashMap<>();
        eFirmas =  new HashMap<>();
        regimenes = new ArrayList<>();
        terceros = new HashMap<>();
    }
    
    public static Controlador getControlador(){
        if(controlador == null) controlador = new Controlador();
        
        return controlador;
    }

    public Map<Integer,Contadores> getAllContadores(){
        return this.contadores;
    }
    
    
    public ArrayList<Cliente> getClientesByContador(int idContador){
        ArrayList<Cliente> clients = new ArrayList();
        
        Contadores contador = contadores.get(idContador);
        
        for(Integer i : contador.idsClientes){
             clients.add(clientes.get(i));
        }
        
        return clients;
    }
    
    public String insertContador(Contadores contador){
        
        String respuesta = dBContadores.insertContador(contador);
        
        if("correcto".equals(respuesta)){
            contadores.put(contador.getId(), contador);
        }
        
        return respuesta;
    }
    
    public String deleteContador(int idContador){
        String respuesta = dBContadores.deleteContador(idContador);
        
        if("correcto".equals(respuesta)) contadores.remove(idContador);
                
        return respuesta;
    }
    
    public String updateContactoConta(String contacto, int idContador){
        String respuesta = dBContadores.updateContactoContador(contacto, idContador);
        
        if("correcto".equals(respuesta)){
            contadores.get(idContador).contacto = contacto;
        }
        
        return respuesta;
    }
    
    public Map<Integer,Cliente> getAllClientes(){
        return clientes;
    }
        
    public String insertCliente(Cliente cliente) {
        return insertCliente(cliente, null);
    }

    public String insertCliente(Cliente cliente, EFirmas firma){
        String respuesta =  dBClientes.insertCliente(cliente, firma);
        
        if("correcto".equals(respuesta)){
            clientes.put(cliente.id_persona, cliente);
            if (firma != null) {
                eFirmas.put(cliente.id_persona, firma);
            }
        }
        
        return respuesta;
    }

    public String deleteCliente(int idCliente){
        String respuesta = dBClientes.deleteCliente(idCliente);
        
        if("correcto".equals(respuesta)) clientes.remove(idCliente);
        
        return respuesta;
    }
    
    public String addRegimenACliente(int idCliente, int idRegimen){
        String respuesta = dBClientes.agregarRegimenCliente(idCliente, idRegimen);
        
        if("correcto".equals(respuesta)){
            
           clientes.get(idCliente).idsRegimenes.add((Integer)idRegimen); 
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
        
        if("correcto".equals(respuesta)){
            terceros.put(t.id_persona, t);
        }
        
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
    
    public ArrayList<Terceros> getTercerosDeCliente(int idCliente){
        ArrayList<Integer> idTerceros = dBClientes.getTercerosCliente(idCliente);
        ArrayList<Terceros> terce = new ArrayList<>();
        
        if(idTerceros.isEmpty()) return terce;
        
        for(Integer i : idTerceros) terce.add(terceros.get(i));
        
        return terce;
    }
    
    public ArrayList<Regimenes> getRegimenes(){
        return regimenes;
    }
    
    public String addRegimen(Regimenes r){
        String respuesta = dBRegimenes.addRegimen(r);
        
        if("correcto".equals(respuesta)) regimenes.add(r);
        
        return respuesta;
    }
    
    public String deleteRegimen(Regimenes r){
        String respuesta = dBRegimenes.deleteRegimen(r.getId());
        
        if("correcto".equals(respuesta)){
            
            for(int i = 0; i < regimenes.size(); i++){
                
                if(r.getId() == regimenes.get(i).getId()){
                    
                    regimenes.remove(i);
                    break;
                    
                }
            }
        }
        
        return respuesta;
    }
        
    public Map<Integer, EFirmas> getAllFirmas(){
        return eFirmas;
    }
    
    public String renovarFirma(String fechaE, String fechaR, int idCliente){
        EFirmas existing = eFirmas.get(idCliente);
        String rCert = (existing != null) ? existing.ruta_certificado : null;
        String rKey = (existing != null) ? existing.ruta_key : null;
        String pass = (existing != null) ? existing.contrasena : null;
        return renovarFirma(fechaE, fechaR, rCert, rKey, pass, idCliente);
    }

    public String renovarFirma(String fechaE, String fechaR, String rCert, String rKey, String pass, int idCliente){
        String respuesta = dBFirmas.renovacion(fechaE, fechaR, rCert, rKey, pass, idCliente);
        
        if("correcto".equals(respuesta)){
            EFirmas ef = eFirmas.get(idCliente);
            if (ef != null) {
                ef.fecha_expiracion = fechaE;
                ef.fecha_renovacion = fechaR;
                ef.ruta_certificado = rCert;
                ef.ruta_key = rKey;
                ef.contrasena = pass;
            } else {
                eFirmas.put(idCliente, new EFirmas(fechaE, fechaR, idCliente, rCert, rKey, pass));
            }
        }
        
        return respuesta;
    }
    
    public EFirmas getFirmaDe(int idCliente){
        return(eFirmas.containsKey(idCliente)) ? eFirmas.get(idCliente) : null;
    }
    
    public String insertarDeclaracion(int id_cliente, int anio, int mes){
        String respuesta = dBDeclaraciones.insertDeclaracion(id_cliente, anio, mes);
        
        if("correcto".equals(respuesta)){
            declaraciones = dBDeclaraciones.getAllDeclaraciones();
        }
        
        return respuesta;
    }
    
    public String colocarGastosDeclaracion(int id_declaracion){
        String respuesta = dBDeclaraciones.colocarGastos(id_declaracion);
        
        if("correcto".equals(respuesta)){
            declaraciones.get(id_declaracion).gastos = 1;
        }
        
        return respuesta;
    }
    
    public String colocarIngresosDeclaracion(int id_declaracion){
        String respuesta = dBDeclaraciones.colocarIngresos(id_declaracion);
        
        if("correcto".equals(respuesta)){
            declaraciones.get(id_declaracion).ingresos = 1;
        }
        
        return respuesta;
    }
    
    public String colocarIngresosYGastosDeclaracion(int id_declaracion){
        String respuesta = dBDeclaraciones.colocarIngresosGastos(id_declaracion);
        
        if("correcto".equals(respuesta)){
            declaraciones.get(id_declaracion).ingresos = 1;
            declaraciones.get(id_declaracion).gastos = 1;
        }
        
        return respuesta;
    }
    
    public String setDeclarado(int id_declaracion){
        String respuesta = dBDeclaraciones.setDeclarado(id_declaracion);
        
        if("correcto".equals(respuesta)){
            declaraciones.get(id_declaracion).declarado = 1;
        }
        
        return respuesta;
    }
    
    public String desDeclarar(int id_declaracion){
        String respuesta = dBDeclaraciones.desDeclarar(id_declaracion);
        
        if("correcto".equals(respuesta)){
            declaraciones.get(id_declaracion).declarado = 0;
        }
        
        return respuesta;
    }
    
    
    public Cliente getClienteById(int id){
        return clientes.get(id);
    }
    
    public Terceros getTerceroById(int id){
        return terceros.get(id);
    }
    
    
    public String updateCliente(Cliente c){
        String respuesta = dBClientes.updateCliente(c, c.id_persona);
        
        if("correcto".equals(respuesta)) clientes.put(c.id_persona, c);
        
        
        return respuesta;  
    }
    
    public String updateTercero(Terceros t){
        String respuesta = dBTerceros.updateTercero(t);
        
        if("correcto".equals(respuesta)){
            terceros = dBTerceros.getTerceros();
        }
        
        return respuesta;
    }

    public Declaracion obtenerOCrearDeclaracion(int idCliente, int anio, int mes) {
        for (Declaracion d : declaraciones.values()) {
            if (d.getIdCliente() == idCliente && d.anio == anio && d.mes == mes) {
                return d;
            }
        }
        String res = insertarDeclaracion(idCliente, anio, mes);
        if ("correcto".equals(res)) {
            for (Declaracion d : declaraciones.values()) {
                if (d.getIdCliente() == idCliente && d.anio == anio && d.mes == mes) {
                    return d;
                }
            }
        }
        return null;
    }

    public String toggleGastos(int idDeclaracion, boolean check) {
        String sql = check ? dBDeclaraciones.colocarGastos(idDeclaracion) : dBDeclaraciones.revertirGastos(idDeclaracion);
        if ("correcto".equals(sql)) {
            declaraciones.get(idDeclaracion).gastos = check ? 1 : 0;
            return "correcto";
        }
        return sql;
    }

    public String toggleIngresos(int idDeclaracion, boolean check) {
        String sql = check ? dBDeclaraciones.colocarIngresos(idDeclaracion) : dBDeclaraciones.revertirIngresos(idDeclaracion);
        if ("correcto".equals(sql)) {
            declaraciones.get(idDeclaracion).ingresos = check ? 1 : 0;
            return "correcto";
        }
        return sql;
    }

    public String toggleDeclarado(int idDeclaracion, boolean check) {
        String sql = check ? setDeclarado(idDeclaracion) : desDeclarar(idDeclaracion);
        if ("correcto".equals(sql)) {
            declaraciones.get(idDeclaracion).declarado = check ? 1 : 0;
            return "correcto";
        }
        return sql;
    }

    public List<Pago> obtenerPagosPorCliente(int idCliente) {
        return dBPagos.obtenerPagosPorCliente(idCliente);
    }

    public String registrarPago(Pago pago) {
        return dBPagos.insertarPago(pago);
    }
}
