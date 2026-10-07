/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package main;

import UI.Index;
import controlador.Controlador;
import utils.LicenseManager;

public class Main {
    public static void main(String[] args) {
        // 1. Verificación obligatoria de licencia offline y amarre por hardware
        LicenseManager.validarLicenciaOExit();

        // 2. Inicialización de tema corporativo y base de datos
        UI.AppTheme.setupTheme();
        Controlador c = Controlador.getControlador();
        c.cargarTodo();
        Index i = new Index(c);
        i.setVisible(true);
    }
}
