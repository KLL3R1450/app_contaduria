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
    public ArrayList<Regimenes> regimenes;
    
    private static Controlador controlador = null;
    
    @Override
    public void cargarTodo() {
        try {
            regimenes = dBRegimenes.getRegimenes();
            contadores = dBContadores.getContadores();
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
        regimenes = new ArrayList<>();
    }
    
    public static Controlador getControlador(){
        if(controlador == null) controlador = new Controlador();
        
        return controlador;
    }

    public Map<Integer,Contadores> getAllContadores(){
        return this.contadores;
    }
    
    
    public ArrayList<Cliente> getClientesByContador(int idContador){
        return dBClientes.getClientesDeContadorObj(idContador);
    }
    
    public String insertContador(String nombre){
        Contadores c = new Contadores(nombre, "SIN CONTACTO");
        return insertContador(c);
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

    public String updateNombreContador(int idContador, String nombre){
        String respuesta = dBContadores.updateNombreContador(idContador, nombre);
        
        if("correcto".equals(respuesta)){
            if (contadores.containsKey(idContador)) {
                contadores.get(idContador).nombre = nombre;
            }
        }
        
        return respuesta;
    }
    
    public String updateContactoConta(String contacto, int idContador){
        String respuesta = dBContadores.updateContactoContador(contacto, idContador);
        
        if("correcto".equals(respuesta)){
            if (contadores.containsKey(idContador)) {
                contadores.get(idContador).contacto = contacto;
            }
        }
        
        return respuesta;
    }

    public ArrayList<Cliente> getClientesSinContador(){
        return dBClientes.getClientesSinContador();
    }

    public String asignarClienteAContador(int idCliente, int idContador){
        return dBClientes.asignarContador(idCliente, idContador);
    }

    public String desasignarCliente(int idCliente){
        return dBClientes.desasignarContador(idCliente);
    }
    


    public java.util.List<Cliente> getClientesLigeros() {
        return dBClientes.getClientesLigeros();
    }
        
    public String insertCliente(Cliente cliente) {
        return insertCliente(cliente, null);
    }

    public String insertCliente(Cliente cliente, EFirmas firma){
        String respuesta =  dBClientes.insertCliente(cliente, firma);
        return respuesta;
    }

    public String deleteCliente(int idCliente){
        String respuesta = dBClientes.deleteCliente(idCliente);
        return respuesta;
    }
    
    public String addRegimenACliente(int idCliente, int idRegimen){
        String respuesta = dBClientes.agregarRegimenCliente(idCliente, idRegimen);
        return respuesta;
    }
    
    public String deleteRegimenACliente(int idCliente,  int idRegimen){
        String respuesta = dBClientes.deleteRegimenCliente(idCliente, idRegimen);
     
        return respuesta;
    }
    

    public java.util.List<Terceros> getTercerosLigeros() {
        return dBTerceros.getTercerosLigeros();
    }
    
    public String insertTercero(Terceros t, ArrayList<Integer> clientesT){
        String respuesta = dBTerceros.insertTercero(t, clientesT);
        return respuesta;
    }
    
    public String relacionarClientes(int idTercero, ArrayList<Integer> clientesT){
        String respuesta = dBTerceros.relacionarClientes(idTercero, clientesT);
        return respuesta;
    }
    
    public String borrarTercero(int idTercero){
        String respuesta = dBTerceros.borrarTercero(idTercero);
        return respuesta;
    }
    
    public String borrarRegimenTerero(int idTercero, int idRegimen){
        String respuesta = dBTerceros.eliminarRegimenTercero(idTercero, idRegimen);
        return respuesta;
    }
    
    public String agregarRegimenTercerro(int idTercero, int idRegimen){
        String respuesta = dBTerceros.insertarRegimenTerero(idTercero, idRegimen);
        return respuesta;
    }
    
    public ArrayList<Terceros> getTercerosDeCliente(int idCliente){
        return dBTerceros.getTercerosDeClienteObj(idCliente);
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
    
    public String renovarFirma(String fechaE, String fechaR, int idCliente){
        EFirmas existing = getFirmaDe(idCliente);
        String rCert = (existing != null) ? existing.ruta_certificado : null;
        String rKey = (existing != null) ? existing.ruta_key : null;
        String pass = (existing != null) ? existing.contrasena : null;
        return renovarFirma(fechaE, fechaR, rCert, rKey, pass, idCliente);
    }

    public String renovarFirma(String fechaE, String fechaR, String rCert, String rKey, String pass, int idCliente){
        String respuesta = dBFirmas.renovacion(fechaE, fechaR, rCert, rKey, pass, idCliente);
        return respuesta;
    }
    
    public EFirmas getFirmaDe(int idCliente){
        return dBFirmas.getFirmaDe(idCliente);
    }
    
    public String insertarDeclaracion(int id_cliente, int anio, int mes){
        String respuesta = dBDeclaraciones.insertDeclaracion(id_cliente, anio, mes);
        return respuesta;
    }
    
    public String colocarGastosDeclaracion(int id_declaracion){
        String respuesta = dBDeclaraciones.colocarGastos(id_declaracion);
        return respuesta;
    }
    
    public String colocarIngresosDeclaracion(int id_declaracion){
        String respuesta = dBDeclaraciones.colocarIngresos(id_declaracion);
        return respuesta;
    }
    
    public String colocarIngresosYGastosDeclaracion(int id_declaracion){
        String respuesta = dBDeclaraciones.colocarIngresosGastos(id_declaracion);
        return respuesta;
    }
    
    public String setDeclarado(int id_declaracion){
        String respuesta = dBDeclaraciones.setDeclarado(id_declaracion);
        return respuesta;
    }
    
    public String desDeclarar(int id_declaracion){
        String respuesta = dBDeclaraciones.desDeclarar(id_declaracion);
        return respuesta;
    }
    
    public Cliente getClienteById(int id){
        return dBClientes.getClienteById(id);
    }
    
    public Terceros getTerceroById(int id){
        return dBTerceros.getTerceroById(id);
    }
    
    public String updateCliente(Cliente c){
        String respuesta = dBClientes.updateCliente(c, c.id_persona);
        return respuesta;  
    }
    
    public String updateTercero(Terceros t){
        String respuesta = dBTerceros.updateTercero(t);
        return respuesta;
    }

    public Declaracion obtenerOCrearDeclaracion(int idCliente, int anio, int mes) {
        Declaracion d = dBDeclaraciones.getDeclaracionPeriodo(idCliente, anio, mes);
        if (d == null) {
            String res = insertarDeclaracion(idCliente, anio, mes);
            if ("correcto".equals(res)) {
                d = dBDeclaraciones.getDeclaracionPeriodo(idCliente, anio, mes);
            }
        }
        return d;
    }

    public String toggleGastos(int idDeclaracion, boolean check) {
        return check ? dBDeclaraciones.colocarGastos(idDeclaracion) : dBDeclaraciones.revertirGastos(idDeclaracion);
    }

    public String toggleIngresos(int idDeclaracion, boolean check) {
        return check ? dBDeclaraciones.colocarIngresos(idDeclaracion) : dBDeclaraciones.revertirIngresos(idDeclaracion);
    }

    public String toggleDeclarado(int idDeclaracion, boolean check) {
        return check ? setDeclarado(idDeclaracion) : desDeclarar(idDeclaracion);
    }

    public List<Pago> obtenerPagosPorCliente(int idCliente) {
        return dBPagos.obtenerPagosPorCliente(idCliente);
    }

    public String registrarPago(Pago pago) {
        return dBPagos.insertarPago(pago);
    }

    public java.util.List<Object[]> getDeclaracionesMensualesContador(int idContador, int anio, int mes) {
        return dBDeclaraciones.getDeclaracionesMensualesContador(idContador, anio, mes);
    }

    public java.util.List<Declaracion> getDeclaracionesPorCliente(int idCliente) {
        return dBDeclaraciones.getDeclaracionesPorCliente(idCliente);
    }

    public String[] getDatosSatCliente(int id) {
        return dBClientes.getDatosSatCliente(id);
    }

    public String[] getDatosSatTercero(int id) {
        return dBTerceros.getDatosSatTercero(id);
    }

    public java.util.List<Object[]> obtenerSemaforoDashboard() {
        return dBFirmas.obtenerSemaforoDashboard();
    }
}
