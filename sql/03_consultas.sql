-- =====================================================================
--  SIGID  -  03_consultas.sql
--  Consultas SQL del sistema: SELECT, UPDATE y DELETE
--  Cada consulta indica el caso de uso o requerimiento que le da origen.
-- =====================================================================
USE sigid_db;

-- ---------------------------------------------------------------------
-- C1. Plantel completo de una división con su estado de aptitud médica
--     Origen: CU03 (Registrar asistencia) y CU05 (Generar convocatoria)
-- ---------------------------------------------------------------------
SELECT j.id_jugador,
       CONCAT(j.apellido, ', ', j.nombre)              AS jugador,
       TIMESTAMPDIFF(YEAR, j.fecha_nacimiento, CURDATE()) AS edad,
       d.nombre                                        AS division,
       v.estado_ficha,
       v.lesiones_activas
FROM jugador j
JOIN division d           ON d.id_division = j.id_division
JOIN v_aptitud_jugador v  ON v.id_jugador  = j.id_jugador
WHERE j.id_division = 1
  AND j.estado = 'ACTIVO'
ORDER BY j.apellido, j.nombre;

-- ---------------------------------------------------------------------
-- C2. Jugadores habilitados para ser convocados
--     Origen: CU10 (Validar aptitud médica) – implementa la RN-03
-- ---------------------------------------------------------------------
SELECT CONCAT(j.apellido, ', ', j.nombre) AS jugador,
       f.fecha_vencimiento                AS ficha_vence
FROM jugador j
JOIN ficha_medica f ON f.id_jugador = j.id_jugador
WHERE j.id_division = 1
  AND j.estado = 'ACTIVO'
  AND f.apto_fisico = TRUE
  AND f.fecha_vencimiento >= CURDATE()
  AND NOT EXISTS (SELECT 1 FROM lesion l
                   WHERE l.id_jugador = j.id_jugador
                     AND l.fecha_alta_real IS NULL)
ORDER BY j.apellido;

-- ---------------------------------------------------------------------
-- C3. Porcentaje de asistencia por jugador
--     Origen: CU08 (Emitir reporte de asistencia) – RF07
-- ---------------------------------------------------------------------
SELECT CONCAT(j.apellido, ', ', j.nombre) AS jugador,
       d.nombre                           AS division,
       COUNT(a.id_asistencia)             AS eventos_registrados,
       SUM(a.estado = 'PRESENTE')         AS presentes,
       ROUND(100 * SUM(a.estado = 'PRESENTE') / COUNT(a.id_asistencia), 1)
                                          AS porcentaje_asistencia
FROM jugador j
JOIN division   d ON d.id_division = j.id_division
JOIN asistencia a ON a.id_jugador  = j.id_jugador
GROUP BY j.id_jugador, j.apellido, j.nombre, d.nombre
HAVING COUNT(a.id_asistencia) > 0
ORDER BY porcentaje_asistencia DESC, jugador;

-- ---------------------------------------------------------------------
-- C4. Fichas médicas vencidas o por vencer en los próximos 45 días
--     Origen: CU02 (Gestionar ficha médica) – alerta preventiva, RF04
-- ---------------------------------------------------------------------
SELECT CONCAT(j.apellido, ', ', j.nombre) AS jugador,
       d.nombre                           AS division,
       f.fecha_vencimiento,
       DATEDIFF(f.fecha_vencimiento, CURDATE()) AS dias_restantes,
       CASE WHEN f.fecha_vencimiento <  CURDATE() THEN 'VENCIDA'
            ELSE 'POR VENCER' END         AS situacion
FROM ficha_medica f
JOIN jugador  j ON j.id_jugador  = f.id_jugador
JOIN division d ON d.id_division = j.id_division
WHERE f.fecha_vencimiento <= DATE_ADD(CURDATE(), INTERVAL 45 DAY)
ORDER BY f.fecha_vencimiento;

-- ---------------------------------------------------------------------
-- C5. Convocados a un partido con el contacto primario de su tutor
--     Origen: CU06 (Notificar a tutores) – RF06
-- ---------------------------------------------------------------------
SELECT e.fecha,
       CONCAT(j.apellido, ', ', j.nombre) AS jugador,
       CONCAT(t.apellido, ', ', t.nombre) AS tutor,
       t.telefono,
       t.email,
       IF(c.confirmada, 'Confirmada', 'Pendiente') AS estado_convocatoria
FROM convocatoria c
JOIN evento        e  ON e.id_evento  = c.id_evento
JOIN jugador       j  ON j.id_jugador = c.id_jugador
JOIN jugador_tutor jt ON jt.id_jugador = j.id_jugador AND jt.contacto_primario = TRUE
JOIN tutor         t  ON t.id_tutor   = jt.id_tutor
WHERE e.id_evento = 3
ORDER BY j.apellido;

-- ---------------------------------------------------------------------
-- C6. Ocupación de cada división respecto de su cupo máximo
--     Origen: CU01 (Gestionar jugador) – control de la RN-02
-- ---------------------------------------------------------------------
SELECT d.nombre                                   AS division,
       CONCAT(en.apellido, ', ', en.nombre)       AS entrenador,
       COUNT(j.id_jugador)                        AS jugadores_activos,
       d.cupo_maximo,
       d.cupo_maximo - COUNT(j.id_jugador)        AS vacantes
FROM division d
LEFT JOIN entrenador en ON en.id_entrenador = d.id_entrenador
LEFT JOIN jugador    j  ON j.id_division = d.id_division AND j.estado = 'ACTIVO'
GROUP BY d.id_division, d.nombre, en.apellido, en.nombre, d.cupo_maximo
ORDER BY d.nombre;

-- ---------------------------------------------------------------------
-- C7. Jugadores sin ninguna ficha médica cargada
--     Origen: control de integridad del área médica – RF04
-- ---------------------------------------------------------------------
SELECT CONCAT(j.apellido, ', ', j.nombre) AS jugador, d.nombre AS division
FROM jugador j
JOIN division d ON d.id_division = j.id_division
WHERE NOT EXISTS (SELECT 1 FROM ficha_medica f WHERE f.id_jugador = j.id_jugador)
ORDER BY d.nombre, j.apellido;

-- =====================================================================
--  ACTUALIZACIÓN DE REGISTROS (UPDATE)
-- =====================================================================

-- U1. El tutor confirma la presencia del jugador al partido  (CU06)
UPDATE convocatoria
   SET confirmada = TRUE
 WHERE id_evento = 3 AND id_jugador = 4;

-- U2. Alta médica de una lesión: se cierra el período  (CU09)
UPDATE lesion
   SET fecha_alta_real = CURDATE()
 WHERE id_jugador = 7 AND fecha_alta_real IS NULL;

-- U3. Renovación de la ficha médica vencida  (CU02)
UPDATE ficha_medica
   SET fecha_emision     = CURDATE(),
       fecha_vencimiento = DATE_ADD(CURDATE(), INTERVAL 1 YEAR),
       apto_fisico       = TRUE,
       observaciones     = 'Ficha renovada - apto favorable'
 WHERE id_jugador = 3;

-- U4. Suspensión de un evento por lluvia  (CU04)
UPDATE evento
   SET estado = 'SUSPENDIDO'
 WHERE id_evento = 5;

-- =====================================================================
--  BORRADO DE REGISTROS (DELETE)
-- =====================================================================

-- D1. Se da de baja una convocatoria mal cargada  (CU05)
DELETE FROM convocatoria
 WHERE id_evento = 3 AND id_jugador = 4;

-- D2. Se elimina una notificación que quedó en estado de error  (CU06)
DELETE FROM notificacion
 WHERE estado_envio = 'ERROR';

-- D3. Baja definitiva de un jugador inactivo.
--     Por las FK ON DELETE CASCADE se eliminan también sus fichas,
--     lesiones, asistencias, convocatorias y vínculos con tutores.  (CU01)
DELETE FROM jugador
 WHERE id_jugador = 10 AND estado = 'INACTIVO';

-- Verificación del borrado en cascada
SELECT (SELECT COUNT(*) FROM jugador       WHERE id_jugador = 10) AS jugador,
       (SELECT COUNT(*) FROM ficha_medica  WHERE id_jugador = 10) AS fichas,
       (SELECT COUNT(*) FROM lesion        WHERE id_jugador = 10) AS lesiones,
       (SELECT COUNT(*) FROM asistencia    WHERE id_jugador = 10) AS asistencias;
