package ar.org.estrelladelsur.sigid.modelo;

import java.util.regex.Pattern;

/**
 * ABSTRACCIÓN: representa a cualquier persona vinculada al club.
 *
 * Reúne los datos y comportamientos que comparten jugadores, tutores,
 * entrenadores y profesionales médicos. No tiene sentido instanciar una
 * "persona" genérica, por eso la clase es abstracta y obliga a cada
 * subclase a definir su rol y su ficha descriptiva.
 *
 * ENCAPSULAMIENTO: todos los atributos son privados. Los setters validan
 * el formato antes de asignar, de modo que un objeto nunca queda con un
 * documento o un correo mal formado.
 */
public abstract class Persona implements Identificable, Comparable<Persona> {

    private static final Pattern FORMATO_DOCUMENTO = Pattern.compile("\\d{7,8}");
    private static final Pattern FORMATO_EMAIL =
            Pattern.compile("^[\\w.+-]+@[\\w-]+(\\.[\\w-]+)+$");

    private int    id;
    private String documento;
    private String apellido;
    private String nombre;
    private String telefono;
    private String email;

    /** Constructor vacío: lo usan los DAO al reconstruir objetos desde la base. */
    protected Persona() { }

    /** Constructor con los datos mínimos que identifican a una persona. */
    protected Persona(String documento, String apellido, String nombre) {
        setDocumento(documento);
        setApellido(apellido);
        setNombre(nombre);
    }

    // ------------------------------------------------------------ métodos abstractos

    /** Rol que cumple la persona en el club (lo define cada subclase). */
    public abstract String getRol();

    /** Detalle propio de cada tipo de persona, usado en el listado polimórfico. */
    public abstract String getDetalle();

    // ------------------------------------------------------------ comportamiento común

    public String nombreCompleto() {
        return apellido + ", " + nombre;
    }

    /**
     * Ficha resumida. Es un MÉTODO PLANTILLA: el formato es común, pero
     * getRol() y getDetalle() se resuelven en tiempo de ejecución según la
     * clase concreta del objeto (polimorfismo por sobrescritura).
     */
    public String ficha() {
        return String.format("%-13s %-26s DNI %-9s %s",
                getRol(), nombreCompleto(), documento == null ? "-" : documento, getDetalle());
    }

    /** Orden natural: alfabético por apellido y, a igualdad, por nombre. */
    @Override
    public int compareTo(Persona otra) {
        int porApellido = String.CASE_INSENSITIVE_ORDER.compare(apellido, otra.apellido);
        return porApellido != 0 ? porApellido
                                : String.CASE_INSENSITIVE_ORDER.compare(nombre, otra.nombre);
    }

    // ------------------------------------------------------------ getters

    @Override public int getId()      { return id; }
    public String getDocumento()      { return documento; }
    public String getApellido()       { return apellido; }
    public String getNombre()         { return nombre; }
    public String getTelefono()       { return telefono; }
    public String getEmail()          { return email; }

    public boolean tieneEmail()       { return email != null && !email.isBlank(); }
    public boolean tieneTelefono()    { return telefono != null && !telefono.isBlank(); }

    // ------------------------------------------------------------ setters con validación

    public final void setId(int id) {
        if (id < 0) {
            throw new IllegalArgumentException("El identificador no puede ser negativo.");
        }
        this.id = id;
    }

    /**
     * El documento vacío se admite para que la regla RN-00 del servicio
     * informe el faltante; si viene cargado, debe tener 7 u 8 dígitos.
     */
    public final void setDocumento(String documento) {
        if (documento != null && !documento.isBlank()
                && !FORMATO_DOCUMENTO.matcher(documento.trim()).matches()) {
            throw new IllegalArgumentException(
                "El documento debe tener 7 u 8 dígitos sin puntos (se recibió '" + documento + "').");
        }
        this.documento = documento == null ? null : documento.trim();
    }

    public final void setApellido(String apellido) {
        this.apellido = apellido == null ? null : apellido.trim();
    }

    public final void setNombre(String nombre) {
        this.nombre = nombre == null ? null : nombre.trim();
    }

    public final void setTelefono(String telefono) {
        this.telefono = telefono == null ? null : telefono.trim();
    }

    public final void setEmail(String email) {
        if (email != null && !email.isBlank() && !FORMATO_EMAIL.matcher(email.trim()).matches()) {
            throw new IllegalArgumentException("El correo '" + email + "' no tiene un formato válido.");
        }
        this.email = (email == null || email.isBlank()) ? null : email.trim();
    }

    @Override public String toString() {
        return "[" + id + "] " + nombreCompleto();
    }
}
