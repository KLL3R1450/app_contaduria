/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JDialog.java to edit this template
 */
package UI;

import controlador.Controlador;
import entidades.Cliente;
import entidades.Contadores;
import entidades.Regimenes;
import java.util.ArrayList;
import javax.swing.DefaultListModel;
import javax.swing.JOptionPane;
import utils.Validator;

/**
 *
 * @author Osmar
 */
public class AñadirCliente extends javax.swing.JDialog {
    
    private static Controlador c;
    private DefaultListModel<String> modeloActuales;
    private DefaultListModel<String> modeloDisponibles;
    private ArrayList<Object> añadidos;
    private ArrayList<Object> quitados;
    private ArrayList<Object> all;
    private Clickeo l;

    // Campos personalizados para registro opcional de E-Firma
    private javax.swing.JCheckBox chkAgregarFirma;
    private javax.swing.JTextField txtFExp;
    private javax.swing.JTextField txtFRen;
    private javax.swing.JTextField txtFCert;
    private javax.swing.JTextField txtFKey;
    private javax.swing.JTextField txtFPass;
    private javax.swing.JButton btnFCert;
    private javax.swing.JButton btnFKey;
    private javax.swing.JButton btnFPass;
    
    
    public AñadirCliente(java.awt.Frame parent, boolean modal, Controlador controler) {
        super(parent, modal);
        initComponents();
        
        c = controler;
        modeloActuales = new DefaultListModel<>();
        modeloDisponibles = new DefaultListModel<>();
        añadidos = new ArrayList<>();
        quitados = new ArrayList<>();
        all = new ArrayList<>();
        l = new Clickeo(modeloActuales, modeloDisponibles, añadidos, quitados, all, listaA, ListaD);
        
        ListaD.setModel(modeloDisponibles);
        listaA.setModel(modeloActuales);
        
        listaA.addMouseListener(l);
        ListaD.addMouseListener(l);
        
        setRegimenes();
        setContadores();

        // FlatLaf styling
        TNom.putClientProperty("JTextField.roundRect", true);
        TNom.putClientProperty("JTextField.placeholderText", "Nombre completo...");
        TRfc.putClientProperty("JTextField.roundRect", true);
        TRfc.putClientProperty("JTextField.placeholderText", "RFC de 12 o 13 dígitos...");
        TCp.putClientProperty("JTextField.roundRect", true);
        TCp.putClientProperty("JTextField.placeholderText", "Código Postal...");
        TCorr.putClientProperty("JTextField.roundRect", true);
        TCorr.putClientProperty("JTextField.placeholderText", "Correo electrónico...");
        THon.putClientProperty("JTextField.roundRect", true);
        THon.putClientProperty("JTextField.placeholderText", "Monto de honorarios...");

        CCon.putClientProperty("JComboBox.isButtonRoundRect", true);
        jButton1.putClientProperty("JButton.buttonType", "roundRect");

        setResizable(false);
        setJMenuBar(null);
        initFirmaPanel();
        setLocationRelativeTo(parent);
    }
    
    private void setRegimenes(){
        
        for(Regimenes r : c.getRegimenes()) {
            all.add(r);
            modeloDisponibles.addElement(r.regimen);
        }
        
    }
    
    private void setContadores(){
        CCon.removeAllItems();
        CCon.addItem("Selecciona un contador");
        
        for(Contadores con : c.getAllContadores().values()){
            CCon.addItem(con.nombre);
        }
    }

    private void initFirmaPanel() {
        setResizable(true);
        setSize(new java.awt.Dimension(1250, 480));
        setMinimumSize(new java.awt.Dimension(1250, 480));
        setPreferredSize(new java.awt.Dimension(1250, 480));

        java.awt.Container originalContentPane = getContentPane();
        javax.swing.JPanel newContentPane = new javax.swing.JPanel(new java.awt.BorderLayout(15, 15));

        javax.swing.JPanel rightPanel = new javax.swing.JPanel(new java.awt.GridBagLayout());
        rightPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Firma Electrónica (Opcional)"));
        
        java.awt.GridBagConstraints gbc = new java.awt.GridBagConstraints();
        gbc.insets = new java.awt.Insets(6, 6, 6, 6);
        gbc.fill = java.awt.GridBagConstraints.HORIZONTAL;
        
        chkAgregarFirma = new javax.swing.JCheckBox("Registrar E-Firma con el cliente");
        chkAgregarFirma.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 12));
        
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        rightPanel.add(chkAgregarFirma, gbc);
        
        javax.swing.JPanel fieldsPanel = new javax.swing.JPanel(new java.awt.GridBagLayout());
        java.awt.GridBagConstraints sGbc = new java.awt.GridBagConstraints();
        sGbc.insets = new java.awt.Insets(4, 4, 4, 4);
        sGbc.fill = java.awt.GridBagConstraints.HORIZONTAL;
        
        // Expiration
        sGbc.gridx = 0; sGbc.gridy = 0; sGbc.weightx = 0.3;
        fieldsPanel.add(new javax.swing.JLabel("Expira (YYYY-MM-DD):"), sGbc);
        sGbc.gridx = 1; sGbc.weightx = 0.7;
        txtFExp = new javax.swing.JTextField(10);
        txtFExp.putClientProperty("JTextField.roundRect", true);
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd");
        txtFExp.setText(sdf.format(new java.util.Date()));
        fieldsPanel.add(txtFExp, sGbc);
        
        // Renewal
        sGbc.gridx = 0; sGbc.gridy = 1; sGbc.weightx = 0.3;
        fieldsPanel.add(new javax.swing.JLabel("Renovación (YYYY-MM-DD):"), sGbc);
        sGbc.gridx = 1; sGbc.weightx = 0.7;
        txtFRen = new javax.swing.JTextField(10);
        txtFRen.putClientProperty("JTextField.roundRect", true);
        txtFRen.setText(sdf.format(new java.util.Date()));
        fieldsPanel.add(txtFRen, sGbc);
        
        // Certificate
        sGbc.gridx = 0; sGbc.gridy = 2; sGbc.weightx = 0.3;
        fieldsPanel.add(new javax.swing.JLabel("Certificado (.cer):"), sGbc);
        sGbc.gridx = 1; sGbc.weightx = 0.7;
        javax.swing.JPanel cPanel = new javax.swing.JPanel(new java.awt.BorderLayout(5, 0));
        txtFCert = new javax.swing.JTextField();
        txtFCert.putClientProperty("JTextField.roundRect", true);
        cPanel.add(txtFCert, java.awt.BorderLayout.CENTER);
        btnFCert = new javax.swing.JButton("Examinar...");
        btnFCert.putClientProperty("JButton.buttonType", "roundRect");
        btnFCert.addActionListener(e -> examinarFirmaArchivo(txtFCert, "Archivos Certificado (*.cer)", "cer"));
        cPanel.add(btnFCert, java.awt.BorderLayout.EAST);
        fieldsPanel.add(cPanel, sGbc);
        
        // Key
        sGbc.gridx = 0; sGbc.gridy = 3; sGbc.weightx = 0.3;
        fieldsPanel.add(new javax.swing.JLabel("Llave (.key):"), sGbc);
        sGbc.gridx = 1; sGbc.weightx = 0.7;
        javax.swing.JPanel kPanel = new javax.swing.JPanel(new java.awt.BorderLayout(5, 0));
        txtFKey = new javax.swing.JTextField();
        txtFKey.putClientProperty("JTextField.roundRect", true);
        kPanel.add(txtFKey, java.awt.BorderLayout.CENTER);
        btnFKey = new javax.swing.JButton("Examinar...");
        btnFKey.putClientProperty("JButton.buttonType", "roundRect");
        btnFKey.addActionListener(e -> examinarFirmaArchivo(txtFKey, "Archivos Key (*.key)", "key"));
        kPanel.add(btnFKey, java.awt.BorderLayout.EAST);
        fieldsPanel.add(kPanel, sGbc);
        
        // Pass
        sGbc.gridx = 0; sGbc.gridy = 4; sGbc.weightx = 0.3;
        fieldsPanel.add(new javax.swing.JLabel("Contraseña (.txt):"), sGbc);
        sGbc.gridx = 1; sGbc.weightx = 0.7;
        javax.swing.JPanel pPanel = new javax.swing.JPanel(new java.awt.BorderLayout(5, 0));
        txtFPass = new javax.swing.JTextField();
        txtFPass.putClientProperty("JTextField.roundRect", true);
        pPanel.add(txtFPass, java.awt.BorderLayout.CENTER);
        btnFPass = new javax.swing.JButton("Examinar...");
        btnFPass.putClientProperty("JButton.buttonType", "roundRect");
        btnFPass.addActionListener(e -> examinarFirmaArchivo(txtFPass, "Archivos de texto (*.txt)", "txt"));
        pPanel.add(btnFPass, java.awt.BorderLayout.EAST);
        fieldsPanel.add(pPanel, sGbc);
        
        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 2; gbc.weighty = 1.0;
        gbc.fill = java.awt.GridBagConstraints.BOTH;
        rightPanel.add(fieldsPanel, gbc);
        
        newContentPane.add(originalContentPane, java.awt.BorderLayout.WEST);
        newContentPane.add(rightPanel, java.awt.BorderLayout.CENTER);
        setContentPane(newContentPane);
        
        java.util.function.Consumer<Boolean> toggleFields = enabled -> {
            txtFExp.setEnabled(enabled);
            txtFRen.setEnabled(enabled);
            txtFCert.setEnabled(enabled);
            txtFKey.setEnabled(enabled);
            txtFPass.setEnabled(enabled);
            btnFCert.setEnabled(enabled);
            btnFKey.setEnabled(enabled);
            btnFPass.setEnabled(enabled);
        };
        
        toggleFields.accept(false);
        chkAgregarFirma.addActionListener(e -> toggleFields.accept(chkAgregarFirma.isSelected()));
    }

    private void examinarFirmaArchivo(javax.swing.JTextField textField, String desc, String ext) {
        javax.swing.JFileChooser fileChooser = new javax.swing.JFileChooser();
        fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter(desc, ext));
        int selection = fileChooser.showOpenDialog(this);
        if (selection == javax.swing.JFileChooser.APPROVE_OPTION) {
            textField.setText(fileChooser.getSelectedFile().getAbsolutePath());
        }
    }
    
    private void borrarValores(){
        TCorr.setText("");
        TCp.setText("");
        THon.setText("");
        TNom.setText("");
        TRfc.setText("");
        CCon.setSelectedIndex(0);
        setRegimenes();
        añadidos.clear();
        quitados.clear();
        modeloActuales.removeAllElements();
        
        if (chkAgregarFirma != null) {
            chkAgregarFirma.setSelected(false);
            java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd");
            String hoy = sdf.format(new java.util.Date());
            txtFExp.setText(hoy);
            txtFRen.setText(hoy);
            txtFCert.setText("");
            txtFKey.setText("");
            txtFPass.setText("");
            txtFExp.setEnabled(false);
            txtFRen.setEnabled(false);
            txtFCert.setEnabled(false);
            txtFKey.setEnabled(false);
            txtFPass.setEnabled(false);
            btnFCert.setEnabled(false);
            btnFKey.setEnabled(false);
            btnFPass.setEnabled(false);
        }
    }
    


    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel7 = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        TNom = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        TRfc = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        TCp = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        TCorr = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        THon = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        CCon = new javax.swing.JComboBox<>();
        jScrollPane1 = new javax.swing.JScrollPane();
        listaA = new javax.swing.JList<>();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        ListaD = new javax.swing.JList<>();
        jButton1 = new javax.swing.JButton();
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
        setMaximumSize(new java.awt.Dimension(800, 400));
        setMinimumSize(new java.awt.Dimension(800, 400));
        setPreferredSize(new java.awt.Dimension(800, 400));
        setSize(new java.awt.Dimension(652, 376));

        jLabel7.setText("Datos Clientes");

        jLabel1.setText("Nombre");

        TNom.setMaximumSize(new java.awt.Dimension(7, 22));

        jLabel2.setText("RFC");

        TRfc.setMaximumSize(new java.awt.Dimension(7, 22));

        jLabel3.setText("C.P.");

        TCp.setMaximumSize(new java.awt.Dimension(7, 22));

        jLabel4.setText("Correo");

        TCorr.setMaximumSize(new java.awt.Dimension(7, 22));

        jLabel5.setText("Monto H");

        THon.setMaximumSize(new java.awt.Dimension(7, 22));

        jLabel6.setText("Contador");

        CCon.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        CCon.setMaximumSize(new java.awt.Dimension(7, 22));
        CCon.setMinimumSize(new java.awt.Dimension(7, 22));
        CCon.setPreferredSize(new java.awt.Dimension(7, 22));

        listaA.setMaximumSize(new java.awt.Dimension(270, 89));
        listaA.setMinimumSize(new java.awt.Dimension(270, 89));
        jScrollPane1.setViewportView(listaA);

        jLabel8.setText("Regimene(s)");

        jLabel9.setText("Regimenes Disponibles");

        ListaD.setMaximumSize(new java.awt.Dimension(270, 89));
        ListaD.setMinimumSize(new java.awt.Dimension(270, 89));
        jScrollPane2.setViewportView(ListaD);

        jButton1.setText("Guardar");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

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
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 143, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 56, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(CCon, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 56, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(TNom, javax.swing.GroupLayout.PREFERRED_SIZE, 158, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 56, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(TRfc, javax.swing.GroupLayout.PREFERRED_SIZE, 158, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 56, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(TCp, javax.swing.GroupLayout.PREFERRED_SIZE, 158, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 56, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(TCorr, javax.swing.GroupLayout.PREFERRED_SIZE, 158, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 56, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(THon, javax.swing.GroupLayout.PREFERRED_SIZE, 158, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addGap(54, 54, 54))
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                                .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)))
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 94, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel9)
                            .addComponent(jScrollPane1)
                            .addComponent(jScrollPane2))))
                .addContainerGap(109, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(4, 4, 4)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(TNom, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(TRfc, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(TCp, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 89, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(TCorr, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel9))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(THon, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(18, 18, 18)
                                .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addGap(28, 28, 28)
                                .addComponent(CCon, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 89, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(13, 13, 13)
                .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(46, 46, 46))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jMenuItem10ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem10ActionPerformed
        this.setVisible(false);
    }//GEN-LAST:event_jMenuItem10ActionPerformed

    private void jMenuItem9ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem9ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jMenuItem9ActionPerformed

    private void jMenuItem11ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem11ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jMenuItem11ActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        
        if(TNom.getText().isEmpty() || TCorr.getText().isEmpty() || TCp.getText().isEmpty()
                || THon.getText().isEmpty() || TRfc.getText().isEmpty() || CCon.getSelectedIndex() == 0){
            JOptionPane.showMessageDialog(rootPane, "Valores no validos");
            return;
        }
        
        if(TNom.getText().isEmpty()){
            JOptionPane.showMessageDialog(rootPane, "Nombre no valido");
            return;
        }
        
        if(!Validator.validarCorreo(TCorr.getText())){
            JOptionPane.showMessageDialog(rootPane, "Correo no valido");
            return;
        }
        
        if(!Validator.validarCodigoPostal(TCp.getText())){
            JOptionPane.showMessageDialog(rootPane, "Codigo Postal no valido");
            return;
        }
        
        String formatedRFC = TRfc.getText().toUpperCase();
        
        if(!Validator.validarRFC(formatedRFC)){
            JOptionPane.showMessageDialog(rootPane, "RFC no valido");
            return;
        }
       
        int montoH = 0;
        try{
            montoH = Integer.parseInt(THon.getText());
        }catch(NumberFormatException ex){
            JOptionPane.showMessageDialog(rootPane, "El monto no es un numero valido");
            return;
        }
        
        int idContador = 0;
        
        for(Contadores con : c.getAllContadores().values()){
            if(con.nombre == CCon.getSelectedItem()) {
                
                idContador = con.getId();
                break;
            }
        }
        
        ArrayList<Integer> idReg = new ArrayList<>();
        
        for(Object regimen : añadidos) idReg.add(((Regimenes)regimen).getId());
        
        Cliente cliente = new Cliente(
                TNom.getText(), 
                  formatedRFC, 
                   TCp.getText(),
                TCorr.getText(), 
                 montoH, 
            idContador);
        
        cliente.idsRegimenes = idReg;

        entidades.EFirmas firma = null;
        if (chkAgregarFirma != null && chkAgregarFirma.isSelected()) {
            String fExp = txtFExp.getText().trim();
            String fRen = txtFRen.getText().trim();
            String rCert = txtFCert.getText().trim();
            String rKey = txtFKey.getText().trim();
            String rPass = txtFPass.getText().trim();
            
            if (!fExp.matches("^\\d{4}-(0[1-9]|1[0-2])-(0[1-9]|[12]\\d|3[01])$") ||
                !fRen.matches("^\\d{4}-(0[1-9]|1[0-2])-(0[1-9]|[12]\\d|3[01])$")) {
                JOptionPane.showMessageDialog(rootPane, "Formato de fecha de firma inválido. Utilice AAAA-MM-DD.");
                return;
            }
            
            firma = new entidades.EFirmas(fExp, fRen, 0, rCert, rKey, rPass);
        }
        
        String respuesta = c.insertCliente(cliente, firma);
        
        if(!"correcto".equals(respuesta)) {
            JOptionPane.showMessageDialog(rootPane, respuesta);
            return;
        }
        
        borrarValores();
        JOptionPane.showMessageDialog(rootPane, "Cliente insertado con exito");
    }//GEN-LAST:event_jButton1ActionPerformed

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
            java.util.logging.Logger.getLogger(AñadirCliente.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(AñadirCliente.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(AñadirCliente.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(AñadirCliente.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                AñadirCliente dialog = new AñadirCliente(new javax.swing.JFrame(), true,c);
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

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox<String> CCon;
    private javax.swing.JList<String> ListaD;
    private javax.swing.JTextField TCorr;
    private javax.swing.JTextField TCp;
    private javax.swing.JTextField THon;
    private javax.swing.JTextField TNom;
    private javax.swing.JTextField TRfc;
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
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
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JList<String> listaA;
    // End of variables declaration//GEN-END:variables
    

}
