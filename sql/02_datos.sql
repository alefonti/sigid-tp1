-- =====================================================================
--  SIGID  -  02_datos.sql
--  Inserción de registros de prueba (entregable "Inserción de registros")
-- =====================================================================
USE sigid_db;

-- ---------- Roles y usuarios ----------
INSERT INTO rol (nombre, descripcion) VALUES
  ('SECRETARIA',  'Alta y mantenimiento de jugadores y tutores'),
  ('ENTRENADOR',  'Asistencia, convocatorias y consulta de historial'),
  ('COORDINADOR', 'Programación de eventos y reportes'),
  ('MEDICO',      'Fichas médicas y registro de lesiones');

INSERT INTO usuario (username, password_hash, id_rol, activo) VALUES
  ('mgarcia',  '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZbnQ1niUu', 1, TRUE),
  ('rlopez',   '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZbnQ1niUu', 2, TRUE),
  ('pfernan',  '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZbnQ1niUu', 3, TRUE),
  ('cdominguez','$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZbnQ1niUu', 4, TRUE);

-- ---------- Entrenadores y divisiones ----------
INSERT INTO entrenador (documento, apellido, nombre, nro_licencia, telefono, email) VALUES
  ('28456123', 'López',    'Ricardo', 'AFA-10233', '11-4455-8822', 'rlopez@estrelladelsur.org'),
  ('30112456', 'Suárez',   'Matías',  'AFA-11894', '11-6633-2211', 'msuarez@estrelladelsur.org'),
  ('25998741', 'Peralta',  'Gustavo', 'AFA-09117', '11-5522-9988', 'gperalta@estrelladelsur.org');

INSERT INTO division (nombre, categoria, anio_nac_desde, anio_nac_hasta, cupo_maximo, id_entrenador) VALUES
  ('Sub-13', 'SUB13', 2013, 2013, 25, 1),
  ('Sub-15', 'SUB15', 2011, 2011, 25, 2),
  ('Sub-17', 'SUB17', 2009, 2009, 28, 3);

-- ---------- Jugadores ----------
INSERT INTO jugador (documento, apellido, nombre, fecha_nacimiento, fecha_alta, estado, id_division) VALUES
  ('55123456', 'Acuña',    'Tomás',    '2013-03-14', '2025-02-10', 'ACTIVO',  1),
  ('55234567', 'Benítez',  'Lucas',    '2013-07-02', '2025-02-10', 'ACTIVO',  1),
  ('55345678', 'Cabrera',  'Joaquín',  '2013-11-21', '2025-03-01', 'ACTIVO',  1),
  ('55456789', 'Domínguez','Bautista', '2013-01-09', '2025-03-01', 'ACTIVO',  1),
  ('54123456', 'Escobar',  'Thiago',   '2011-05-30', '2024-02-15', 'ACTIVO',  2),
  ('54234567', 'Figueroa', 'Iván',     '2011-09-12', '2024-02-15', 'ACTIVO',  2),
  ('54345678', 'Gómez',    'Santino',  '2011-12-04', '2024-08-20', 'ACTIVO',  2),
  ('53123456', 'Herrera',  'Nicolás',  '2009-04-18', '2023-03-06', 'ACTIVO',  3),
  ('53234567', 'Ibáñez',   'Franco',   '2009-08-25', '2023-03-06', 'ACTIVO',  3),
  ('53345678', 'Juárez',   'Valentín', '2009-02-11', '2024-03-04', 'INACTIVO',3);

-- ---------- Tutores y vínculo N:M ----------
INSERT INTO tutor (documento, apellido, nombre, telefono, email) VALUES
  ('27888111', 'Acuña',    'Marcela',  '11-3344-5566', 'macuna@correo.com'),
  ('29112333', 'Benítez',  'Jorge',    '11-2233-4455', 'jbenitez@correo.com'),
  ('31445666', 'Cabrera',  'Silvina',  '11-7788-9900', 'scabrera@correo.com'),
  ('26778999', 'Escobar',  'Daniel',   '11-4411-2200', 'descobar@correo.com'),
  ('28334555', 'Herrera',  'Patricia', '11-9911-3344', 'pherrera@correo.com'),
  ('30556777', 'Acuña',    'Sergio',   '11-6677-1122', 'sacuna@correo.com'),
  ('29667888', 'Juárez',   'Andrea',   '11-2255-6677', 'ajuarez@correo.com');

INSERT INTO jugador_tutor (id_jugador, id_tutor, parentesco, contacto_primario) VALUES
  (1, 1, 'Madre', TRUE),
  (1, 6, 'Padre', FALSE),
  (2, 2, 'Padre', TRUE),
  (3, 3, 'Madre', TRUE),
  (5, 4, 'Padre', TRUE),
  (8, 5, 'Madre', TRUE),
  (10, 7, 'Madre', TRUE);

-- ---------- Profesionales, fichas médicas y lesiones ----------
INSERT INTO profesional (apellido, nombre, matricula, especialidad) VALUES
  ('Domínguez', 'Carolina', 'MN-84512', 'Medicina deportiva'),
  ('Ruiz',      'Esteban',  'MN-77310', 'Kinesiología');

INSERT INTO ficha_medica (id_jugador, id_profesional, fecha_emision, fecha_vencimiento, apto_fisico, observaciones) VALUES
  (1, 1, '2026-03-01', '2027-03-01', TRUE,  'Sin observaciones'),
  (2, 1, '2026-02-20', '2027-02-20', TRUE,  'Control de crecimiento semestral'),
  (3, 1, '2025-04-10', '2026-04-10', TRUE,  'Ficha vencida, requiere renovación'),
  (4, 1, '2026-03-05', '2027-03-05', TRUE,  NULL),
  (5, 1, '2026-01-15', '2027-01-15', TRUE,  NULL),
  (6, 1, '2026-02-02', '2027-02-02', FALSE, 'Soplo funcional en estudio'),
  (7, 1, '2026-03-12', '2027-03-12', TRUE,  NULL),
  (8, 1, '2026-02-28', '2027-02-28', TRUE,  NULL),
  (9, 1, '2026-03-18', '2027-03-18', TRUE,  NULL);

INSERT INTO lesion (id_jugador, id_profesional, tipo, gravedad, fecha_inicio, fecha_alta_real) VALUES
  (2, 2, 'Esguince de tobillo grado I',  'LEVE',     '2026-08-02', '2026-08-25'),
  (7, 2, 'Distensión de isquiotibiales', 'MODERADA', '2026-09-05', NULL),
  (9, 2, 'Contractura lumbar',           'LEVE',     '2026-09-12', NULL),
  (10, 2, 'Fractura de escafoides',      'GRAVE',    '2026-06-18', '2026-09-01');

-- ---------- Sedes y eventos ----------
INSERT INTO sede (nombre, direccion, superficie) VALUES
  ('Predio Estrella del Sur', 'Av. Cruz 2450, CABA',      'Césped natural'),
  ('Polideportivo Municipal', 'Larrazábal 1180, CABA',    'Sintético'),
  ('Cancha auxiliar N.º 2',   'Av. Cruz 2450, CABA',      'Césped natural');

INSERT INTO evento (tipo, fecha, hora_inicio, hora_fin, rival, condicion, estado, id_division, id_sede) VALUES
  ('ENTRENAMIENTO', '2026-09-15', '17:00:00', '18:30:00', NULL,                 NULL,       'FINALIZADO',  1, 1),
  ('ENTRENAMIENTO', '2026-09-17', '17:00:00', '18:30:00', NULL,                 NULL,       'FINALIZADO',  1, 1),
  ('PARTIDO',       '2026-09-27', '10:00:00', '11:30:00', 'Club Atlético Norte','LOCAL',    'PROGRAMADO',  1, 1),
  ('ENTRENAMIENTO', '2026-09-16', '19:00:00', '20:30:00', NULL,                 NULL,       'FINALIZADO',  2, 2),
  ('PARTIDO',       '2026-09-28', '12:00:00', '13:30:00', 'Deportivo Oeste',    'VISITANTE','PROGRAMADO',  2, 2),
  ('ENTRENAMIENTO', '2026-09-18', '19:00:00', '20:30:00', NULL,                 NULL,       'FINALIZADO',  3, 3);

-- ---------- Asistencias ----------
INSERT INTO asistencia (id_evento, id_jugador, estado, motivo) VALUES
  (1, 1, 'PRESENTE',    NULL),
  (1, 2, 'PRESENTE',    NULL),
  (1, 3, 'AUSENTE',     NULL),
  (1, 4, 'TARDE',       'Llegó 20 minutos después'),
  (2, 1, 'PRESENTE',    NULL),
  (2, 2, 'JUSTIFICADO', 'Certificado escolar'),
  (2, 3, 'AUSENTE',     NULL),
  (2, 4, 'PRESENTE',    NULL),
  (4, 5, 'PRESENTE',    NULL),
  (4, 6, 'PRESENTE',    NULL),
  (4, 7, 'JUSTIFICADO', 'Lesión en tratamiento'),
  (6, 8, 'PRESENTE',    NULL),
  (6, 9, 'AUSENTE',     NULL),
  (6, 10,'JUSTIFICADO', 'Jugador con baja temporaria');

-- ---------- Convocatorias y notificaciones ----------
INSERT INTO convocatoria (id_evento, id_jugador, confirmada) VALUES
  (3, 1, TRUE),
  (3, 2, TRUE),
  (3, 4, FALSE);

INSERT INTO notificacion (id_tutor, id_evento, canal, mensaje, estado_envio) VALUES
  (1, 3, 'EMAIL', 'Tomás fue convocado al partido del 27/09 a las 10:00 en el predio del club.', 'ENVIADA'),
  (6, 3, 'SMS',   'Tomás fue convocado al partido del 27/09 a las 10:00.',                        'ENVIADA'),
  (2, 3, 'EMAIL', 'Lucas fue convocado al partido del 27/09 a las 10:00 en el predio del club.',  'ENVIADA');
