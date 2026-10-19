package ar.org.estrelladelsur.sigid.modelo;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/** Pruebas de encapsulamiento, herencia y polimorfismo del modelo (TP3). */
class ModeloTest {

    @Test
    @DisplayName("CP-32: los setters rechazan documentos y correos mal formados (encapsulamiento)")
    void encapsulamiento() {
        Jugador j = new Jugador("55111222", "Pérez", "Juan", LocalDate.of(2013, 5, 10), 1);
        assertThrows(IllegalArgumentException.class, () -> j.setDocumento("55.111.222"));
        assertThrows(IllegalArgumentException.class, () -> j.setEmail("sin-arroba.com"));
        assertThrows(IllegalArgumentException.class, () -> j.setFechaNacimiento(LocalDate.now().plusDays(1)));
        assertEquals("55111222", j.getDocumento());          // el valor anterior no se pisó
    }

    @Test
    @DisplayName("CP-33: cada subclase de Persona responde getRol() a su manera (polimorfismo)")
    void polimorfismoPersonas() {
        List<Persona> personas = List.of(
                new Jugador("55111222", "Pérez", "Juan", LocalDate.of(2013, 5, 10), 1),
                new Tutor(1, "Pérez", "Ana", "11-1111-2222", "ana@correo.com"),
                new Entrenador("28456123", "López", "Ricardo", "AFA-10233"),
                new ProfesionalMedico("Ruiz", "Esteban", "MN-77310", "Kinesiología"));

        List<String> roles = personas.stream().map(Persona::getRol).toList();
        assertEquals(List.of("Jugador", "Tutor", "Entrenador", "Prof. médico"), roles);
        assertTrue(personas.get(3).ficha().contains("Kinesiología"));
    }

    @Test
    @DisplayName("CP-34: sólo el Partido admite convocatoria y la fábrica crea la subclase correcta")
    void polimorfismoEventos() {
        Evento entrenamiento = new Entrenamiento(LocalDate.of(2026, 10, 20),
                LocalTime.of(17, 0), LocalTime.of(18, 30), 1, 1);
        Evento partido = new Partido(LocalDate.of(2026, 10, 25),
                LocalTime.of(10, 0), LocalTime.of(11, 30), 1, 1, "Social Lugano", "local");

        assertFalse(entrenamiento.admiteConvocatoria());
        assertTrue(partido.admiteConvocatoria());
        assertEquals(90, entrenamiento.duracionMinutos());
        assertEquals("vs Social Lugano (LOCAL)", partido.descripcion());
        assertInstanceOf(Partido.class, Evento.crear(TipoEvento.PARTIDO));
        assertThrows(IllegalArgumentException.class, () -> new Entrenamiento(LocalDate.now(),
                LocalTime.of(18, 0), LocalTime.of(17, 0), 1, 1));
    }
}
