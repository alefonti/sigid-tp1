package ar.org.estrelladelsur.sigid.modelo;

/**
 * HERENCIA: cuerpo técnico a cargo de una división. Se incorpora al
 * modelo de objetos en el TP3 (en el TP2 sólo existía en la base).
 */
public class Entrenador extends Persona {

    private String nroLicencia;

    public Entrenador() {
        super();
    }

    public Entrenador(String documento, String apellido, String nombre, String nroLicencia) {
        super(documento, apellido, nombre);
        this.nroLicencia = nroLicencia;
    }

    @Override public String getRol() { return "Entrenador"; }

    @Override public String getDetalle() {
        return "Licencia " + (nroLicencia == null ? "en trámite" : nroLicencia);
    }

    public String getNroLicencia()        { return nroLicencia; }
    public void   setNroLicencia(String v){ this.nroLicencia = v; }
}
