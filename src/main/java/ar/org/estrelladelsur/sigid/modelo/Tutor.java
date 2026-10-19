package ar.org.estrelladelsur.sigid.modelo;

/**
 * HERENCIA: adulto responsable de uno o más jugadores. Es la persona
 * que recibe las notificaciones de convocatoria.
 */
public class Tutor extends Persona {

    private String parentesco;

    public Tutor() {
        super();
    }

    public Tutor(int idTutor, String apellido, String nombre, String telefono, String email) {
        super(null, apellido, nombre);
        setId(idTutor);
        setTelefono(telefono);
        setEmail(email);
    }

    @Override public String getRol() { return "Tutor"; }

    @Override public String getDetalle() {
        String contacto = tieneEmail() ? getEmail() : getTelefono();
        return (parentesco != null ? parentesco + " | " : "") + "contacto " + contacto;
    }

    public int    getIdTutor()    { return getId(); }
    public String getParentesco() { return parentesco; }

    public void setIdTutor(int v)        { setId(v); }
    public void setParentesco(String v)  { this.parentesco = v; }
}
