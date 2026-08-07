/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package main;

import UI.Index;
import controlador.Controlador;
import entidades.Cliente;

public class Main {
    public static void main(String[] args) {
        try {
            com.formdev.flatlaf.FlatDarkLaf.setup();
        } catch(Exception ex) {
            System.err.println("Fallo al iniciar FlatLaf: " + ex.getMessage());
        }
        Controlador c = Controlador.getControlador();;
        c.cargarTodo();
        Index i = new Index(c);
        i.setVisible(true);
    }
}
