/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package utils;

import java.util.regex.Pattern;
import java.util.regex.Matcher;

/**
 *
 * @author Osmar
 */
public abstract class Validator {
    
    private static final Pattern CONTACTO_PATTERN = Pattern.compile("^\\d{10}");
    
    public static boolean validarContacto(String contacto){
        if(contacto == null) return false;
        
        Matcher m = CONTACTO_PATTERN.matcher(contacto);
        
        return m.matches();
    }
    
    
}
