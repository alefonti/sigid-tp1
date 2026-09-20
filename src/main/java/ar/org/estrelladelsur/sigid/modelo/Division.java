package ar.org.estrelladelsur.sigid.modelo;

/** División de inferiores (categoría por año de nacimiento). */
public class Division {

    private int    idDivision;
    private String nombre;
    private String categoria;
    private int    anioNacDesde;
    private int    anioNacHasta;
    private int    cupoMaximo;
    private int    idEntrenador;

    public Division() { }

    public Division(int idDivision, String nombre, String categoria,
                    int anioNacDesde, int anioNacHasta, int cupoMaximo) {
        this.idDivision   = idDivision;
        this.nombre       = nombre;
        this.categoria    = categoria;
        this.anioNacDesde = anioNacDesde;
        this.anioNacHasta = anioNacHasta;
        this.cupoMaximo   = cupoMaximo;
    }

    /** Regla RN-01: indica si un año de nacimiento es admitido por la división. */
    public boolean admiteAnioNacimiento(int anio) {
        return anio >= anioNacDesde && anio <= anioNacHasta;
    }

    public int    getIdDivision()   { return idDivision; }
    public String getNombre()       { return nombre; }
    public String getCategoria()    { return categoria; }
    public int    getAnioNacDesde() { return anioNacDesde; }
    public int    getAnioNacHasta() { return anioNacHasta; }
    public int    getCupoMaximo()   { return cupoMaximo; }
    public int    getIdEntrenador() { return idEntrenador; }

    public void setIdDivision(int v)   { this.idDivision = v; }
    public void setNombre(String v)    { this.nombre = v; }
    public void setCategoria(String v) { this.categoria = v; }
    public void setAnioNacDesde(int v) { this.anioNacDesde = v; }
    public void setAnioNacHasta(int v) { this.anioNacHasta = v; }
    public void setCupoMaximo(int v)   { this.cupoMaximo = v; }
    public void setIdEntrenador(int v) { this.idEntrenador = v; }

    @Override public String toString() {
        return nombre + " (" + anioNacDesde + "-" + anioNacHasta + ")";
    }
}
