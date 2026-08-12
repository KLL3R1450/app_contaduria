/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JDialog.java to edit this template
 */
package UI;

import controlador.Controlador;
import entidades.Cliente;
import entidades.EFirmas;
import entidades.Terceros;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.regex.PatternSyntaxException;
import javax.swing.*;
import java.awt.*;
import javax.swing.event.DocumentEvent;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;

/**
 *
 * @author Osmar
 */
public class BuscarPersonas extends javax.swing.JDialog {

    private DefaultTableModel dtm;
    private static Controlador c;
    private TableRowSorter acomodador ;
    private Eventos l;
    private String tipoPersona;
    private int idPersona;
    
    /**
     * Creates new form BuscarCliente
     */
    public BuscarPersonas(java.awt.Frame parent, boolean modal, Controlador controler, String tp) {
        super(parent, modal);
        initComponents();
        setResizable(false);
        setJMenuBar(null);
        l = new Eventos();
        
        c = controler;
        tipoPersona = tp;
          
        dtm = (DefaultTableModel) tabla.getModel();
                        
        acomodador = new TableRowSorter(dtm);
        tabla.setRowSorter(acomodador);
        tabla.addMouseListener(l);
        
        campo.getDocument().addDocumentListener(l);
        
        cargarPersonas();
        ocultarColumnaId();
        
        // Restructure layout to BorderLayout for a cleaner look
        JPanel contentPane = new JPanel(new BorderLayout(15, 15));
        contentPane.setBorder(new javax.swing.border.EmptyBorder(15, 15, 15, 15));
        setContentPane(contentPane);

        // Search Panel (North)
        JPanel searchPanel = new JPanel(new BorderLayout(5, 5));
        searchPanel.add(jLabel1, BorderLayout.NORTH);
        searchPanel.add(campo, BorderLayout.CENTER);
        contentPane.add(searchPanel, BorderLayout.NORTH);

        // Table Panel (Center)
        contentPane.add(jScrollPane1, BorderLayout.CENTER);

        // Action Buttons Panel (South)
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        
        JButton btnAgregar = new JButton("➕ Agregar");
        btnAgregar.putClientProperty("JButton.buttonType", "roundRect");
        btnAgregar.addActionListener(e -> accionAgregar());
        
        JButton btnDetalles = new JButton("🔍 Ver Detalles");
        btnDetalles.putClientProperty("JButton.buttonType", "roundRect");
        btnDetalles.addActionListener(e -> accionDetalles());
        
        JButton btnEliminar = new JButton("❌ Eliminar");
        btnEliminar.putClientProperty("JButton.buttonType", "roundRect");
        btnEliminar.addActionListener(e -> accionEliminar());
        
        if ("clientes".equals(tipoPersona) || "terceros".equals(tipoPersona) || "firmas".equals(tipoPersona)) {
            actionPanel.add(btnAgregar);
            if (!"firmas".equals(tipoPersona)) {
                actionPanel.add(btnEliminar);
            }
        }
        
        if ("clientes".equals(tipoPersona)) {
            JButton btnGenerarRecibo = new JButton("💵 Generar Recibo");
            btnGenerarRecibo.putClientProperty("JButton.buttonType", "roundRect");
            btnGenerarRecibo.addActionListener(e -> accionGenerarRecibo());
            actionPanel.add(btnGenerarRecibo);
        }
        
        JButton btnCopiarSAT = new JButton("📋 Copiar SAT");
        btnCopiarSAT.putClientProperty("JButton.buttonType", "roundRect");
        btnCopiarSAT.addActionListener(e -> accionCopiarSAT());
        actionPanel.add(btnCopiarSAT);

        actionPanel.add(btnDetalles);
        
        contentPane.add(actionPanel, BorderLayout.SOUTH);
        
        pack();
        setLocationRelativeTo(parent);
    }
    
    private void ocultarColumnaId(){
        tabla.getColumnModel().getColumn(0).setMaxWidth(0);
        tabla.getColumnModel().getColumn(0).setMinWidth(0);
        tabla.getColumnModel().getColumn(0).setPreferredWidth(0);
    }
    
    private void cargarPersonas(){
        boolean nulo = false;
        
        while(dtm.getRowCount() != 0){
            dtm.removeRow(0);
        }
        
        if("clientes".equals(tipoPersona) || "firmas".equals(tipoPersona)){
            for(Cliente cliente : c.getAllClientes().values()){  
                dtm.addRow( 
                    new Object[]{
                     cliente.id_persona,cliente.nombre
                    }
            );
            
            }
        }
        
        else if("terceros".equals(tipoPersona)){
            
            for(Terceros t : c.getTerceros().values()){
                
                dtm.addRow( 
                        
                    new Object[]{
                        t.id_persona,t.nombre
                    });
                
            }
        }
        
        else if("tercerosDe".equals(tipoPersona)){
            for(Terceros t : c.getTercerosDeCliente(idPersona)){
                dtm.addRow(
                        new Object[]{
                            t.id_persona,t.nombre
                        }
                );
            }
        }
        
        
        if(dtm.getRowCount() == 0){
            JOptionPane.showMessageDialog(rootPane, "Este cliente no tiene terceros relacionados");
        }
       
    }
    
    protected void setIdPersona(int id){
        this.idPersona = id;
    }
    
    private void detallesPersonas(int id){
        if ("clientes".equals(tipoPersona)){
            
            DetallesClientes dc = 
                new DetallesClientes((java.awt.Frame) this.getParent(), rootPaneCheckingEnabled,
                        c, c.getClienteById(id));
    
            dc.setVisible(true);
        }
        
        else if("terceros".equals(tipoPersona)){
            DetallesTerceros dt = 
                    new DetallesTerceros((java.awt.Frame) this.getParent(), rootPaneCheckingEnabled, 
                            c, c.getTerceroById(id));
            
            dt.setVisible(true);
        }
        else if("tercerosDe".equals(tipoPersona)){
            DetallesTerceros dt = 
                    new DetallesTerceros((java.awt.Frame) this.getParent(), rootPaneCheckingEnabled, 
                            c, c.getTerceroById(id));
            
            dt.setVisible(true);
        }
        else if("firmas".equals(tipoPersona)){
            EFirmas efirma = c.getFirmaDe(id);
            
            if(efirma == null) {
                int res = JOptionPane.showConfirmDialog(rootPane, 
                    "El cliente no cuenta con EFirma registrada en el sistema. ¿Quieres agregarla de una vez?", 
                    "Agregar E-Firma", 
                    JOptionPane.YES_NO_OPTION);
                if (res == JOptionPane.YES_OPTION) {
                    new EditarFirmaDialog((java.awt.Window) this.getParent(), c, c.getClienteById(id)).setVisible(true);
                }
            } else {
                new EditarFirmaDialog((java.awt.Window) this.getParent(), c, c.getClienteById(id)).setVisible(true);
            }
        }
        
        cargarPersonas();
    }   

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        campo = new javax.swing.JTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        tabla = new javax.swing.JTable();
        jLabel1 = new javax.swing.JLabel();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        jMenuItem10 = new javax.swing.JMenuItem();
        jMenu2 = new javax.swing.JMenu();
        jMenuItem5 = new javax.swing.JMenuItem();
        jMenuItem7 = new javax.swing.JMenuItem();
        jMenuItem8 = new javax.swing.JMenuItem();
        jMenuItem9 = new javax.swing.JMenuItem();
        jMenu3 = new javax.swing.JMenu();
        jMenuItem11 = new javax.swing.JMenuItem();
        jMenu4 = new javax.swing.JMenu();
        jMenuItem12 = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        campo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                campoActionPerformed(evt);
            }
        });

        tabla.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Id", "Nombre"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tabla.setFocusable(false);
        jScrollPane1.setViewportView(tabla);

        jLabel1.setText("Nombre a Buscar");

        jMenu1.setText("Inicio");

        jMenuItem10.setText("Incio");
        jMenuItem10.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem10ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem10);

        jMenuBar1.add(jMenu1);

        jMenu2.setText("Terceros");

        jMenuItem5.setText("Buscar");
        jMenu2.add(jMenuItem5);

        jMenuItem7.setText("Agregar");
        jMenu2.add(jMenuItem7);

        jMenuItem8.setText("Eliminar");
        jMenu2.add(jMenuItem8);

        jMenuItem9.setText("Editar");
        jMenuItem9.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem9ActionPerformed(evt);
            }
        });
        jMenu2.add(jMenuItem9);

        jMenuBar1.add(jMenu2);

        jMenu3.setText("EFirmas");

        jMenuItem11.setText("Editar");
        jMenuItem11.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem11ActionPerformed(evt);
            }
        });
        jMenu3.add(jMenuItem11);

        jMenuBar1.add(jMenu3);

        jMenu4.setText("Listas");

        jMenuItem12.setText("Ver listas");
        jMenu4.add(jMenuItem12);

        jMenuBar1.add(jMenu4);

        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(33, 33, 33)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 98, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(campo, javax.swing.GroupLayout.PREFERRED_SIZE, 462, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 452, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(24, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 18, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(campo, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 136, Short.MAX_VALUE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jMenuItem9ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem9ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jMenuItem9ActionPerformed

    private void jMenuItem11ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem11ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jMenuItem11ActionPerformed

    private void campoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_campoActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_campoActionPerformed

    private void jMenuItem10ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem10ActionPerformed
        this.dispose();
    }//GEN-LAST:event_jMenuItem10ActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(BuscarPersonas.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(BuscarPersonas.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(BuscarPersonas.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(BuscarPersonas.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                BuscarPersonas dialog = new BuscarPersonas(new javax.swing.JFrame(), true, c, "");
                dialog.addWindowListener(new java.awt.event.WindowAdapter() {
                    @Override
                    public void windowClosing(java.awt.event.WindowEvent e) {
                        System.exit(0);
                    }
                });
                dialog.setVisible(true);
            }
        });
    }
    
    class Eventos implements javax.swing.event.DocumentListener, MouseListener{

        @Override
        public void insertUpdate(DocumentEvent e) {
            filtrarTabla();
        }

        @Override
        public void removeUpdate(DocumentEvent e) {
            filtrarTabla();
        }

        @Override
        public void changedUpdate(DocumentEvent e) {
            //Sin usar
        }
        
        private void filtrarTabla(){
            String texto = campo.getText();
            
            if(texto.trim().length() == 0){
                acomodador.setRowFilter(null);
                return;
            }
            
            try{
                RowFilter<DefaultTableModel, Object> filtro = RowFilter.regexFilter("(?i)" + texto,
                        1);
                
                acomodador.setRowFilter(filtro);
            }catch(PatternSyntaxException e){
                return;
            }
        }

        @Override
        public void mouseClicked(MouseEvent e) {
            if(e.getClickCount() >= 2){
                
                int fila = tabla.getSelectedRow();
                
                if(fila != -1){
                    int filaModelo = tabla.convertRowIndexToModel(fila);
                    
                    Integer id = (Integer) tabla.getModel().getValueAt(filaModelo, 0);
                    
                    detallesPersonas(id);
                    
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
    }
    

    private void accionAgregar() {
        if ("clientes".equals(tipoPersona)) {
            new AñadirCliente((java.awt.Frame) this.getParent(), true, c).setVisible(true);
        } else if ("terceros".equals(tipoPersona)) {
            new añadirTercero((java.awt.Frame) this.getParent(), true, c).setVisible(true);
        } else if ("firmas".equals(tipoPersona)) {
            int selectedRow = tabla.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this, "Seleccione un cliente de la lista para añadirle una E-Firma.");
                return;
            }
            int modelRow = tabla.convertRowIndexToModel(selectedRow);
            int id = (Integer) tabla.getModel().getValueAt(modelRow, 0);
            if (c.getFirmaDe(id) != null) {
                JOptionPane.showMessageDialog(this, "El cliente seleccionado ya tiene una E-Firma registrada. Utilice Ver Detalles/Doble Click para editarla.");
            } else {
                new EditarFirmaDialog((java.awt.Window) this.getParent(), c, c.getClienteById(id)).setVisible(true);
            }
        }
        cargarPersonas();
    }

    private void accionDetalles() {
        int fila = tabla.getSelectedRow();
        if (fila != -1) {
            int filaModelo = tabla.convertRowIndexToModel(fila);
            Integer id = (Integer) tabla.getModel().getValueAt(filaModelo, 0);
            detallesPersonas(id);
        } else {
            JOptionPane.showMessageDialog(this, "Por favor, seleccione un registro para ver detalles.");
        }
    }

    private void accionGenerarRecibo() {
        int fila = tabla.getSelectedRow();
        if (fila != -1) {
            int filaModelo = tabla.convertRowIndexToModel(fila);
            Integer id = (Integer) tabla.getModel().getValueAt(filaModelo, 0);
            Cliente cl = c.getClienteById(id);
            if (cl != null) {
                new GenerarReciboDialog((java.awt.Window) this.getParent(), c, cl).setVisible(true);
            }
        } else {
            JOptionPane.showMessageDialog(this, "Por favor, seleccione un cliente para generar su recibo.");
        }
    }

    private void accionEliminar() {
        int fila = tabla.getSelectedRow();
        if (fila != -1) {
            int filaModelo = tabla.convertRowIndexToModel(fila);
            Integer id = (Integer) tabla.getModel().getValueAt(filaModelo, 0);
            String nombre = (String) tabla.getModel().getValueAt(filaModelo, 1);
            
            int confirm = JOptionPane.showConfirmDialog(this, 
                "¿Está seguro de que desea eliminar a: " + nombre + "?", 
                "Confirmar Eliminación", 
                JOptionPane.YES_NO_OPTION);
                
            if (confirm == JOptionPane.YES_OPTION) {
                String res;
                if ("clientes".equals(tipoPersona)) {
                    res = c.deleteCliente(id);
                } else {
                    res = c.borrarTercero(id);
                }
                
                if ("correcto".equals(res)) {
                    JOptionPane.showMessageDialog(this, "Registro eliminado con éxito.");
                    cargarPersonas();
                } else {
                    JOptionPane.showMessageDialog(this, "Error al eliminar: " + res);
                }
            }
        } else {
            JOptionPane.showMessageDialog(this, "Por favor, seleccione un registro para eliminar.");
        }
    }

    private void accionCopiarSAT() {
        int fila = tabla.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(this, "Por favor, seleccione una persona de la lista.");
            return;
        }
        int filaModelo = tabla.convertRowIndexToModel(fila);
        Integer id = (Integer) tabla.getModel().getValueAt(filaModelo, 0);

        String rfc = "";
        String cp = "";
        java.util.ArrayList<Integer> regIds = null;
        String nombre = "";

        if ("clientes".equals(tipoPersona) || "firmas".equals(tipoPersona)) {
            Cliente cli = c.getClienteById(id);
            if (cli != null) {
                rfc = cli.rfc;
                cp = cli.cp;
                regIds = cli.idsRegimenes;
                nombre = cli.nombre;
            }
        } else if ("terceros".equals(tipoPersona) || "tercerosDe".equals(tipoPersona)) {
            Terceros ter = c.getTerceroById(id);
            if (ter != null) {
                rfc = ter.rfc;
                cp = ter.cp;
                regIds = ter.idsRegimenes;
                nombre = ter.nombre;
            }
        }

        StringBuilder sb = new StringBuilder();
        sb.append("Nombre/Razón Social: ").append(nombre).append("\n");
        sb.append("RFC: ").append(rfc).append("\n");
        sb.append("CP: ").append(cp).append("\n");
        sb.append("Régimen(es): ");
        if (regIds != null && !regIds.isEmpty()) {
            java.util.ArrayList<String> regNames = new java.util.ArrayList<>();
            for (Integer rId : regIds) {
                for (entidades.Regimenes r : c.getRegimenes()) {
                    if (r.getId() == rId) {
                        regNames.add(r.regimen);
                    }
                }
            }
            sb.append(String.join(", ", regNames));
        } else {
            sb.append("Sin registrar");
        }

        try {
            java.awt.datatransfer.StringSelection selection = new java.awt.datatransfer.StringSelection(sb.toString());
            java.awt.datatransfer.Clipboard clipboard = java.awt.Toolkit.getDefaultToolkit().getSystemClipboard();
            clipboard.setContents(selection, selection);
            JOptionPane.showMessageDialog(this, "¡Copiado al portapapeles con éxito!\n\n" + sb.toString());
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error al copiar al portapapeles: " + ex.getMessage());
        }
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextField campo;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenu jMenu2;
    private javax.swing.JMenu jMenu3;
    private javax.swing.JMenu jMenu4;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JMenuItem jMenuItem10;
    private javax.swing.JMenuItem jMenuItem11;
    private javax.swing.JMenuItem jMenuItem12;
    private javax.swing.JMenuItem jMenuItem5;
    private javax.swing.JMenuItem jMenuItem7;
    private javax.swing.JMenuItem jMenuItem8;
    private javax.swing.JMenuItem jMenuItem9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable tabla;
    // End of variables declaration//GEN-END:variables
}
