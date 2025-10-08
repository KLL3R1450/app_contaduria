/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package main;

import entidades.Cliente;
import java.util.ArrayList;
import persistencia.ClientesDAO;

public class Main {
    public static void main(String[] args) {
        ClientesDAO cDAO = new ClientesDAO();
        ArrayList<Cliente> c = cDAO.getClientes();
        
        for(Cliente cl : c){
            System.out.println(cl.idsRegimenes.get(0 ));
        }
    }
}
