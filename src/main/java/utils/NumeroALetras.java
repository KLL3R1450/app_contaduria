package utils;

/**
 * Utilidad para convertir números y montos monetarios a su representación
 * en palabras en idioma español formal (Mayúsculas terminando en "PESOS").
 *
 * Ejemplos:
 *   600     -> "SEISCIENTOS PESOS"
 *   1500    -> "MIL QUINIENTOS PESOS"
 *   25340   -> "VEINTICINCO MIL TRESCIENTOS CUARENTA PESOS"
 *   1000000 -> "UN MILLON DE PESOS"
 *
 * @author Osmar & Antigravity
 */
public class NumeroALetras {

    private static final String[] UNIDADES = {
        "", "UN", "DOS", "TRES", "CUATRO", "CINCO", "SEIS", "SIETE", "OCHO", "NUEVE", "DIEZ",
        "ONCE", "DOCE", "TRECE", "CATORCE", "QUINCE", "DIECISEIS", "DIECISIETE", "DIECIOCHO", "DIECINUEVE", "VEINTE"
    };

    private static final String[] DECENAS = {
        "", "", "VEINTI", "TREINTA", "CUARENTA", "CINCUENTA", "SESENTA", "SETENTA", "OCHENTA", "NOVENTA"
    };

    private static final String[] CENTENAS = {
        "", "CIENTO", "DOSCIENTOS", "TRESCIENTOS", "CUATROCIENTOS", "QUINIENTOS",
        "SEISCIENTOS", "SETECIENTOS", "OCHOCIENTOS", "NOVECIENTOS"
    };

    /**
     * Convierte un valor numérico entero a su expresión en letras terminando en "PESOS".
     *
     * @param n Monto entero a convertir.
     * @return Cadena formateada en MAYÚSCULAS.
     */
    public static String convertir(long n) {
        if (n == 0) {
            return "CERO PESOS";
        }
        if (n == 1) {
            return "UN PESO";
        }

        String texto = convertirNumero(n).trim();
        if (n % 1_000_000 == 0) {
            return (texto + " DE PESOS").replaceAll("\\s+", " ").toUpperCase();
        }
        return (texto + " PESOS").replaceAll("\\s+", " ").toUpperCase();
    }

    private static String convertirNumero(long n) {
        if (n <= 20) {
            return UNIDADES[(int) n];
        } else if (n < 30) {
            return "VEINTI" + UNIDADES[(int) (n - 20)];
        } else if (n < 100) {
            int d = (int) (n / 10);
            int u = (int) (n % 10);
            return DECENAS[d] + (u > 0 ? " Y " + UNIDADES[u] : "");
        } else if (n == 100) {
            return "CIEN";
        } else if (n < 1000) {
            int c = (int) (n / 100);
            long resto = n % 100;
            return CENTENAS[c] + (resto > 0 ? " " + convertirNumero(resto) : "");
        } else if (n < 2000) {
            long resto = n % 1000;
            return "MIL" + (resto > 0 ? " " + convertirNumero(resto) : "");
        } else if (n < 1_000_000) {
            long miles = n / 1000;
            long resto = n % 1000;
            return convertirNumero(miles) + " MIL" + (resto > 0 ? " " + convertirNumero(resto) : "");
        } else if (n < 2_000_000) {
            long resto = n % 1_000_000;
            return "UN MILLON" + (resto > 0 ? " " + convertirNumero(resto) : "");
        } else if (n < 1_000_000_000) {
            long millones = n / 1_000_000;
            long resto = n % 1_000_000;
            return convertirNumero(millones) + " MILLONES" + (resto > 0 ? " " + convertirNumero(resto) : "");
        }
        return String.valueOf(n);
    }
}
