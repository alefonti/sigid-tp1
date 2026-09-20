-- =====================================================================
--  SIGID - Sistema de Gestión Integral de Divisiones Inferiores
--  Club Social y Deportivo "Estrella del Sur"
--  01_schema.sql  -  Creación de la base de datos y de las tablas
--  Motor: MySQL 8.0 / InnoDB / utf8mb4
--  Modelo relacional normalizado hasta 3FN
-- =====================================================================

DROP DATABASE IF EXISTS sigid_db;
CREATE DATABASE sigid_db
  DEFAULT CHARACTER SET utf8mb4
  DEFAULT COLLATE utf8mb4_unicode_ci;
USE sigid_db;

-- ---------------------------------------------------------------------
-- 1. Seguridad: roles y usuarios del sistema
-- ---------------------------------------------------------------------
CREATE TABLE rol (
  id_rol      INT AUTO_INCREMENT PRIMARY KEY,
  nombre      VARCHAR(30)  NOT NULL UNIQUE,
  descripcion VARCHAR(120) NOT NULL
) ENGINE=InnoDB;

CREATE TABLE usuario (
  id_usuario    INT AUTO_INCREMENT PRIMARY KEY,
  username      VARCHAR(40) NOT NULL UNIQUE,
  password_hash CHAR(60)    NOT NULL,           -- BCrypt
  id_rol        INT         NOT NULL,
  activo        BOOLEAN     NOT NULL DEFAULT TRUE,
  CONSTRAINT fk_usuario_rol FOREIGN KEY (id_rol) REFERENCES rol(id_rol)
      ON UPDATE CASCADE ON DELETE RESTRICT
) ENGINE=InnoDB;

-- ---------------------------------------------------------------------
-- 2. Estructura deportiva
-- ---------------------------------------------------------------------
CREATE TABLE entrenador (
  id_entrenador INT AUTO_INCREMENT PRIMARY KEY,
  documento     VARCHAR(11) NOT NULL UNIQUE,
  apellido      VARCHAR(50) NOT NULL,
  nombre        VARCHAR(50) NOT NULL,
  nro_licencia  VARCHAR(20),
  telefono      VARCHAR(25),
  email         VARCHAR(80)
) ENGINE=InnoDB;

CREATE TABLE division (
  id_division    INT AUTO_INCREMENT PRIMARY KEY,
  nombre         VARCHAR(30) NOT NULL UNIQUE,
  categoria      VARCHAR(10) NOT NULL,
  anio_nac_desde YEAR        NOT NULL,
  anio_nac_hasta YEAR        NOT NULL,
  cupo_maximo    INT         NOT NULL DEFAULT 30,
  id_entrenador  INT,
  CONSTRAINT fk_division_entrenador FOREIGN KEY (id_entrenador)
      REFERENCES entrenador(id_entrenador) ON UPDATE CASCADE ON DELETE SET NULL,
  CONSTRAINT ck_division_anios CHECK (anio_nac_hasta >= anio_nac_desde),
  CONSTRAINT ck_division_cupo  CHECK (cupo_maximo > 0)
) ENGINE=InnoDB;

CREATE TABLE jugador (
  id_jugador       INT AUTO_INCREMENT PRIMARY KEY,
  documento        VARCHAR(11) NOT NULL UNIQUE,
  apellido         VARCHAR(50) NOT NULL,
  nombre           VARCHAR(50) NOT NULL,
  fecha_nacimiento DATE        NOT NULL,
  fecha_alta       DATE        NOT NULL,
  estado           ENUM('ACTIVO','INACTIVO','BAJA') NOT NULL DEFAULT 'ACTIVO',
  id_division      INT         NOT NULL,
  CONSTRAINT fk_jugador_division FOREIGN KEY (id_division)
      REFERENCES division(id_division) ON UPDATE CASCADE ON DELETE RESTRICT
) ENGINE=InnoDB;

CREATE INDEX ix_jugador_division ON jugador(id_division);
CREATE INDEX ix_jugador_apellido ON jugador(apellido, nombre);

-- ---------------------------------------------------------------------
-- 3. Tutores  (relación N:M con jugador, resuelta en 1FN)
-- ---------------------------------------------------------------------
CREATE TABLE tutor (
  id_tutor  INT AUTO_INCREMENT PRIMARY KEY,
  documento VARCHAR(11) NOT NULL UNIQUE,
  apellido  VARCHAR(50) NOT NULL,
  nombre    VARCHAR(50) NOT NULL,
  telefono  VARCHAR(25) NOT NULL,
  email     VARCHAR(80)
) ENGINE=InnoDB;

CREATE TABLE jugador_tutor (
  id_jugador        INT         NOT NULL,
  id_tutor          INT         NOT NULL,
  parentesco        VARCHAR(20) NOT NULL,
  contacto_primario BOOLEAN     NOT NULL DEFAULT FALSE,
  PRIMARY KEY (id_jugador, id_tutor),
  CONSTRAINT fk_jt_jugador FOREIGN KEY (id_jugador)
      REFERENCES jugador(id_jugador) ON UPDATE CASCADE ON DELETE CASCADE,
  CONSTRAINT fk_jt_tutor FOREIGN KEY (id_tutor)
      REFERENCES tutor(id_tutor) ON UPDATE CASCADE ON DELETE RESTRICT
) ENGINE=InnoDB;

-- ---------------------------------------------------------------------
-- 4. Área médica
-- ---------------------------------------------------------------------
CREATE TABLE profesional (
  id_profesional INT AUTO_INCREMENT PRIMARY KEY,
  apellido       VARCHAR(50) NOT NULL,
  nombre         VARCHAR(50) NOT NULL,
  matricula      VARCHAR(20) NOT NULL UNIQUE,
  especialidad   VARCHAR(40) NOT NULL
) ENGINE=InnoDB;

CREATE TABLE ficha_medica (
  id_ficha          INT AUTO_INCREMENT PRIMARY KEY,
  id_jugador        INT     NOT NULL,
  id_profesional    INT     NOT NULL,
  fecha_emision     DATE    NOT NULL,
  fecha_vencimiento DATE    NOT NULL,
  apto_fisico       BOOLEAN NOT NULL,
  observaciones     VARCHAR(255),
  CONSTRAINT fk_ficha_jugador FOREIGN KEY (id_jugador)
      REFERENCES jugador(id_jugador) ON UPDATE CASCADE ON DELETE CASCADE,
  CONSTRAINT fk_ficha_profesional FOREIGN KEY (id_profesional)
      REFERENCES profesional(id_profesional) ON UPDATE CASCADE ON DELETE RESTRICT,
  CONSTRAINT ck_ficha_fechas CHECK (fecha_vencimiento > fecha_emision)
) ENGINE=InnoDB;

CREATE INDEX ix_ficha_jugador_venc ON ficha_medica(id_jugador, fecha_vencimiento);

CREATE TABLE lesion (
  id_lesion       INT AUTO_INCREMENT PRIMARY KEY,
  id_jugador      INT         NOT NULL,
  id_profesional  INT         NOT NULL,
  tipo            VARCHAR(60) NOT NULL,
  gravedad        ENUM('LEVE','MODERADA','GRAVE') NOT NULL,
  fecha_inicio    DATE        NOT NULL,
  fecha_alta_real DATE        NULL,               -- NULL = lesión activa
  CONSTRAINT fk_lesion_jugador FOREIGN KEY (id_jugador)
      REFERENCES jugador(id_jugador) ON UPDATE CASCADE ON DELETE CASCADE,
  CONSTRAINT fk_lesion_profesional FOREIGN KEY (id_profesional)
      REFERENCES profesional(id_profesional) ON UPDATE CASCADE ON DELETE RESTRICT
) ENGINE=InnoDB;

-- ---------------------------------------------------------------------
-- 5. Agenda deportiva
-- ---------------------------------------------------------------------
CREATE TABLE sede (
  id_sede    INT AUTO_INCREMENT PRIMARY KEY,
  nombre     VARCHAR(60)  NOT NULL,
  direccion  VARCHAR(120) NOT NULL,
  superficie VARCHAR(20)  NOT NULL
) ENGINE=InnoDB;

CREATE TABLE evento (
  id_evento   INT AUTO_INCREMENT PRIMARY KEY,
  tipo        ENUM('ENTRENAMIENTO','PARTIDO') NOT NULL,
  fecha       DATE NOT NULL,
  hora_inicio TIME NOT NULL,
  hora_fin    TIME NOT NULL,
  rival       VARCHAR(60),
  condicion   ENUM('LOCAL','VISITANTE') NULL,
  estado      ENUM('PROGRAMADO','CONFIRMADO','SUSPENDIDO','FINALIZADO')
              NOT NULL DEFAULT 'PROGRAMADO',
  id_division INT NOT NULL,
  id_sede     INT NOT NULL,
  CONSTRAINT fk_evento_division FOREIGN KEY (id_division)
      REFERENCES division(id_division) ON UPDATE CASCADE ON DELETE RESTRICT,
  CONSTRAINT fk_evento_sede FOREIGN KEY (id_sede)
      REFERENCES sede(id_sede) ON UPDATE CASCADE ON DELETE RESTRICT,
  CONSTRAINT ck_evento_horas CHECK (hora_fin > hora_inicio),
  CONSTRAINT uq_evento_sede UNIQUE (id_sede, fecha, hora_inicio)
) ENGINE=InnoDB;

CREATE INDEX ix_evento_division_fecha ON evento(id_division, fecha);

-- ---------------------------------------------------------------------
-- 6. Clases asociativas: asistencia y convocatoria
-- ---------------------------------------------------------------------
CREATE TABLE asistencia (
  id_asistencia  INT AUTO_INCREMENT PRIMARY KEY,
  id_evento      INT NOT NULL,
  id_jugador     INT NOT NULL,
  estado         ENUM('PRESENTE','AUSENTE','TARDE','JUSTIFICADO') NOT NULL,
  motivo         VARCHAR(120),
  fecha_registro DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT fk_asis_evento FOREIGN KEY (id_evento)
      REFERENCES evento(id_evento) ON UPDATE CASCADE ON DELETE CASCADE,
  CONSTRAINT fk_asis_jugador FOREIGN KEY (id_jugador)
      REFERENCES jugador(id_jugador) ON UPDATE CASCADE ON DELETE CASCADE,
  CONSTRAINT uq_asistencia UNIQUE (id_evento, id_jugador)
) ENGINE=InnoDB;

CREATE TABLE convocatoria (
  id_convocatoria    INT AUTO_INCREMENT PRIMARY KEY,
  id_evento          INT NOT NULL,
  id_jugador         INT NOT NULL,
  fecha_convocatoria DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  confirmada         BOOLEAN  NOT NULL DEFAULT FALSE,
  CONSTRAINT fk_conv_evento FOREIGN KEY (id_evento)
      REFERENCES evento(id_evento) ON UPDATE CASCADE ON DELETE CASCADE,
  CONSTRAINT fk_conv_jugador FOREIGN KEY (id_jugador)
      REFERENCES jugador(id_jugador) ON UPDATE CASCADE ON DELETE CASCADE,
  CONSTRAINT uq_convocatoria UNIQUE (id_evento, id_jugador)
) ENGINE=InnoDB;

-- ---------------------------------------------------------------------
-- 7. Comunicación con los tutores
-- ---------------------------------------------------------------------
CREATE TABLE notificacion (
  id_notificacion INT AUTO_INCREMENT PRIMARY KEY,
  id_tutor        INT NOT NULL,
  id_evento       INT NOT NULL,
  canal           ENUM('EMAIL','SMS') NOT NULL,
  mensaje         VARCHAR(255) NOT NULL,
  fecha_envio     DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  estado_envio    ENUM('PENDIENTE','ENVIADA','ERROR') NOT NULL DEFAULT 'PENDIENTE',
  CONSTRAINT fk_notif_tutor FOREIGN KEY (id_tutor)
      REFERENCES tutor(id_tutor) ON UPDATE CASCADE ON DELETE CASCADE,
  CONSTRAINT fk_notif_evento FOREIGN KEY (id_evento)
      REFERENCES evento(id_evento) ON UPDATE CASCADE ON DELETE CASCADE
) ENGINE=InnoDB;

-- ---------------------------------------------------------------------
-- 8. Vista de apoyo: aptitud médica vigente por jugador  (soporte de CU10)
-- ---------------------------------------------------------------------
CREATE OR REPLACE VIEW v_aptitud_jugador AS
SELECT j.id_jugador,
       j.apellido,
       j.nombre,
       d.nombre AS division,
       MAX(f.fecha_vencimiento) AS vence_el,
       CASE WHEN MAX(CASE WHEN f.apto_fisico = TRUE
                           AND f.fecha_vencimiento >= CURRENT_DATE
                          THEN 1 ELSE 0 END) = 1
            THEN 'APTO' ELSE 'NO APTO' END AS estado_ficha,
       (SELECT COUNT(*) FROM lesion l
         WHERE l.id_jugador = j.id_jugador
           AND l.fecha_alta_real IS NULL) AS lesiones_activas
FROM jugador j
JOIN division d ON d.id_division = j.id_division
LEFT JOIN ficha_medica f ON f.id_jugador = j.id_jugador
WHERE j.estado = 'ACTIVO'
GROUP BY j.id_jugador, j.apellido, j.nombre, d.nombre;
