package ar.org.estrelladelsur.sigid.comun;

/**
 * Excepción VERIFICADA (checked) que se lanza cuando el usuario ingresa
 * un dato con formato incorrecto (una fecha mal escrita, una letra donde
 * se esperaba un número, una opción fuera de rango).
 *
 * Al extender de Exception, el compilador obliga a quien llama a
 * tratarla con try/catch o a declararla con throws, lo que garantiza
 * que ningún error de tipeo termine abruptamente el programa.
 */
public class DatoInvalidoException extends Exception {

    private final String campo;

    public DatoInvalidoException(String campo, String mensaje) {
        super(mensaje);
        this.campo = campo;
    }

    public DatoInvalidoException(String campo, String mensaje, Throwable causa) {
        super(mensaje, causa);
        this.campo = campo;
    }

    public String getCampo() { return campo; }
}
