/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package main;

import controlador.Controlador;
import entidades.Cliente;

public class Main {
    public static void main(String[] args) {
        Controlador c = Controlador.getControlador();
        
        c.cargarTodo();
        
        System.out.println(c.clientes.get(c.clientes.size()).nombre);
    }
}
