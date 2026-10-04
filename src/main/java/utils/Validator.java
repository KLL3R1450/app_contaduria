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
    private static final String REGEX_FECHA = "^(0[1-9]|[12][0-9]|3[01])/(0[1-9]|1[0-2])/\\d{4}$";
    
    public static boolean isCorreoVacioOSinCorreo(String correo) {
        if (correo == null) return true;
        String trimmed = correo.trim();
        if (trimmed.isEmpty()) return true;
        String upper = trimmed.toUpperCase();
        return upper.equals("SIN CORREO") 
            || upper.equals("S/C") 
            || upper.equals("NO TIENE") 
            || upper.equals("N/A") 
            || upper.equals("NA") 
            || upper.equals("NINGUNO")
            || upper.equals("NO")
            || upper.equals("S/N")
            || upper.equals("-");
    }

    public static boolean validarContacto(String contacto){
        if (contacto == null) return false;
        return Pattern.matches(CONTACTO_PATTERN, contacto.trim());
    }
    
    public static boolean validarCorreo(String correo){
        if (correo == null) return false;
        String trimmed = correo.trim();
        if (trimmed.isEmpty()) return false;
        return Pattern.matches(REGEX_EMAIL, trimmed);
    }
    
    public static boolean validarCorreoOpcional(String correo){
        if (isCorreoVacioOSinCorreo(correo)) {
            return true;
        }
        return validarCorreo(correo);
    }
    
    public static boolean validarCodigoPostal(String cp){
        if (cp == null) return false;
        return Pattern.matches(REGEX_CP, cp.trim());
    }
    
    public static boolean validarRFC(String rfc){
        if (rfc == null) return false;
        return Pattern.matches(REGEX_RFC, rfc.trim());
    }

    public static boolean validarFecha(String fecha) {
        if (fecha == null) return false;
        String trimmed = fecha.trim();
        if (!Pattern.matches(REGEX_FECHA, trimmed)) return false;
        try {
            java.time.format.DateTimeFormatter dtf = java.time.format.DateTimeFormatter.ofPattern("dd/MM/uuuu")
                    .withResolverStyle(java.time.format.ResolverStyle.STRICT);
            java.time.LocalDate.parse(trimmed, dtf);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    
}
