package ar.org.estrelladelsur.sigid.modelo;

/**
 * HERENCIA: médico o kinesiólogo que emite fichas y registra lesiones.
 */
public class ProfesionalMedico extends Persona {

    private String matricula;
    private String especialidad;

    public ProfesionalMedico() {
        super();
    }

    public ProfesionalMedico(String apellido, String nombre, String matricula, String especialidad) {
        super(null, apellido, nombre);
        this.matricula    = matricula;
        this.especialidad = especialidad;
    }

    @Override public String getRol() { return "Prof. médico"; }

    @Override public String getDetalle() {
        return especialidad + " | Mat. " + matricula;
    }

    public String getMatricula()    { return matricula; }
    public String getEspecialidad() { return especialidad; }

    public void setMatricula(String v)    { this.matricula = v; }
    public void setEspecialidad(String v) { this.especialidad = v; }
}
