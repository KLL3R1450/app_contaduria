
package UI;

import entidades.Cliente;
import entidades.Personas;
import entidades.Regimenes;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.JOptionPane;

/**
 *
 * @author Osmar
 */
public class Clickeo implements MouseListener{

    private DefaultListModel<String> modeloActuales;
    private DefaultListModel<String> modeloDisponibles;
    private ArrayList<Object> añadidos;
    private ArrayList<Object> quitados;
    private ArrayList<Object> all;
    private JList<String> listaA;
    private JList<String> ListaD;
    private boolean hayCambios = false;

    public Clickeo(DefaultListModel<String> modeloActuales, DefaultListModel<String> modeloDisponibles, ArrayList<Object> añadidos, ArrayList<Object> quitados, ArrayList<Object> all, JList<String> listaA, JList<String> ListaD) {
        this.modeloActuales = modeloActuales;
        this.modeloDisponibles = modeloDisponibles;
        this.añadidos = añadidos;
        this.quitados = quitados;
        this.all = all;
        this.listaA = listaA;
        this.ListaD = ListaD;
        this.hayCambios = false;
    }

    
    
    @Override
        public void mouseClicked(MouseEvent e) {
            if(e.getClickCount() >= 2){
                if(e.getSource().equals(listaA)){
                    quitarOpcion();
                }
                else if(e.getSource().equals(ListaD)){
                    añadirOpcion();
                }
            }
        }

        @Override
        public void mousePressed(MouseEvent e) {
        }

        @Override
        public void mouseReleased(MouseEvent e) {
        }

        @Override
        public void mouseEntered(MouseEvent e) {
        }

        @Override
        public void mouseExited(MouseEvent e) {
        }
        
        private void quitarOpcion(){
             String nombre = listaA.getSelectedValue();

            if(nombre == null) {

                JOptionPane.showMessageDialog(null, "Selecciona una opcion");
                return;

            }

            Object r = new Object();
            

            for(Object re : all){
                if(re instanceof Regimenes){
                    if(((Regimenes)re).regimen.equals(nombre)){
                        r = re;
                        break;
                    }
                }
                
                else if(re instanceof Personas){
                    if(((Personas)re).nombre.equals(nombre)){
                        r = re;
                        break;
                    }
                }
                
            }
            modeloActuales.removeElement(nombre);
            quitados.add(r);
            if(r instanceof  Regimenes)
                
                modeloDisponibles.addElement(((Regimenes)r).regimen);
            
            else
                
                modeloDisponibles.addElement(((Personas)r).nombre);
            
            if(!hayCambios) hayCambios = true;
        }
        
        private void añadirOpcion(){
            Object r = new Object();
            String nombre = ListaD.getSelectedValue();

            if(nombre == null) {

                JOptionPane.showMessageDialog(null, "Selecciona una opcion");
                return;

            }

            for(Object re : all){
                if(re instanceof Regimenes){
                    if(((Regimenes)re).regimen.equals(nombre)){
                        r = re;
                        break;
                    }
                }
                
                else if(re instanceof Personas){
                    if(((Personas)re).nombre.equals(nombre)){
                        r = re;
                        break;
                    }
                }
            }

            modeloDisponibles.removeElement(nombre);
            añadidos.add(r);
            if(r instanceof  Regimenes)
                
                modeloActuales.addElement(((Regimenes)r).regimen);
            
            else
                
                modeloActuales.addElement(((Personas)r).nombre);
            if(!hayCambios) hayCambios = true;
        }
        
        public boolean getHayCambios(){
            return hayCambios;
        }
        
        public void setHayCambios(boolean b){
            this.hayCambios = b;
        }
    
}
