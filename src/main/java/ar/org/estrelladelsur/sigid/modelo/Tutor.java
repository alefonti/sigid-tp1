package ar.org.estrelladelsur.sigid.modelo;

/** Adulto responsable de uno o más jugadores. */
public class Tutor {

    private int    idTutor;
    private String documento;
    private String apellido;
    private String nombre;
    private String telefono;
    private String email;

    public Tutor() { }

    public Tutor(int idTutor, String apellido, String nombre, String telefono, String email) {
        this.idTutor  = idTutor;
        this.apellido = apellido;
        this.nombre   = nombre;
        this.telefono = telefono;
        this.email    = email;
    }

    public String nombreCompleto() { return apellido + ", " + nombre; }

    public int    getIdTutor()   { return idTutor; }
    public String getDocumento() { return documento; }
    public String getApellido()  { return apellido; }
    public String getNombre()    { return nombre; }
    public String getTelefono()  { return telefono; }
    public String getEmail()     { return email; }

    public void setIdTutor(int v)     { this.idTutor = v; }
    public void setDocumento(String v){ this.documento = v; }
    public void setApellido(String v) { this.apellido = v; }
    public void setNombre(String v)   { this.nombre = v; }
    public void setTelefono(String v) { this.telefono = v; }
    public void setEmail(String v)    { this.email = v; }
}
