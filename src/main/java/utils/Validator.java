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
    
    private static final String CONTACTO_PATTERN = "^\\d{10}";
    private static final String REGEX_EMAIL = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
    private static final String REGEX_CP = "^[0-9]{5}$";
    private static final String REGEX_RFC = "^[A-ZÑ&]{3,4}\\d{2}(0[1-9]|1[0-2])(0[1-9]|[12]\\d|3[01])[A-Z0-9]{3}$";
    
    public static boolean validarContacto(String contacto){
        return Pattern.matches(CONTACTO_PATTERN, contacto);
    }
    
    public static boolean validarCorreo(String correo){
        return Pattern.matches(REGEX_EMAIL, correo);
    }
    
    public static boolean validarCodigoPostal(String cp){
        return Pattern.matches(REGEX_CP, cp);
    }
    
    public static boolean validarRFC(String rfc){
        return Pattern.matches(REGEX_RFC, rfc);
    }
    
}
