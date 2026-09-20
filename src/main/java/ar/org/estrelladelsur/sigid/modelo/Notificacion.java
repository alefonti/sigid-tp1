package ar.org.estrelladelsur.sigid.modelo;

import java.time.LocalDateTime;

/** Aviso enviado a un tutor a raíz de un evento. */
public class Notificacion {

    private int               idNotificacion;
    private int               idTutor;
    private int               idEvento;
    private CanalNotificacion canal;
    private String            mensaje;
    private LocalDateTime     fechaEnvio = LocalDateTime.now();
    private String            estadoEnvio = "PENDIENTE";

    public Notificacion() { }

    public Notificacion(int idTutor, int idEvento, CanalNotificacion canal, String mensaje) {
        this.idTutor  = idTutor;
        this.idEvento = idEvento;
        this.canal    = canal;
        this.mensaje  = mensaje;
    }

    public int               getIdNotificacion() { return idNotificacion; }
    public int               getIdTutor()        { return idTutor; }
    public int               getIdEvento()       { return idEvento; }
    public CanalNotificacion getCanal()          { return canal; }
    public String            getMensaje()        { return mensaje; }
    public LocalDateTime     getFechaEnvio()     { return fechaEnvio; }
    public String            getEstadoEnvio()    { return estadoEnvio; }

    public void setIdNotificacion(int v)        { this.idNotificacion = v; }
    public void setIdTutor(int v)               { this.idTutor = v; }
    public void setIdEvento(int v)              { this.idEvento = v; }
    public void setCanal(CanalNotificacion v)   { this.canal = v; }
    public void setMensaje(String v)            { this.mensaje = v; }
    public void setFechaEnvio(LocalDateTime v)  { this.fechaEnvio = v; }
    public void setEstadoEnvio(String v)        { this.estadoEnvio = v; }
}
