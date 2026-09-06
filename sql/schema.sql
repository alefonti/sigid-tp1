-- ============================================================
-- SIGID - Sistema de Gestion de Divisiones Inferiores
-- Script de creacion de base de datos (MySQL 8.x)
-- ============================================================
CREATE DATABASE IF NOT EXISTS sigid_db CHARACTER SET utf8mb4;
USE sigid_db;

CREATE TABLE entrenador (
    id_entrenador INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    licencia_dt VARCHAR(50)
);

CREATE TABLE tutor (
    id_tutor INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    telefono VARCHAR(30),
    email VARCHAR(100)
);

CREATE TABLE division (
    id_division INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(50) NOT NULL,
    categoria_edad VARCHAR(30) NOT NULL,
    id_entrenador INT,
    FOREIGN KEY (id_entrenador) REFERENCES entrenador(id_entrenador)
);

CREATE TABLE jugador (
    id_jugador INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(60) NOT NULL,
    apellido VARCHAR(60) NOT NULL,
    fecha_nacimiento DATE NOT NULL,
    dni VARCHAR(20) NOT NULL UNIQUE,
    id_division INT,
    id_tutor INT,
    FOREIGN KEY (id_division) REFERENCES division(id_division),
    FOREIGN KEY (id_tutor) REFERENCES tutor(id_tutor)
);

CREATE TABLE ficha_medica (
    id_ficha INT AUTO_INCREMENT PRIMARY KEY,
    id_jugador INT NOT NULL,
    apto_fisico BOOLEAN NOT NULL DEFAULT FALSE,
    fecha_vencimiento DATE,
    observaciones VARCHAR(255),
    FOREIGN KEY (id_jugador) REFERENCES jugador(id_jugador)
);

CREATE TABLE asistencia (
    id_asistencia INT AUTO_INCREMENT PRIMARY KEY,
    id_jugador INT NOT NULL,
    fecha DATE NOT NULL,
    presente BOOLEAN NOT NULL,
    FOREIGN KEY (id_jugador) REFERENCES jugador(id_jugador)
);

CREATE TABLE lesion (
    id_lesion INT AUTO_INCREMENT PRIMARY KEY,
    id_jugador INT NOT NULL,
    tipo VARCHAR(100) NOT NULL,
    fecha DATE NOT NULL,
    dias_reposo INT DEFAULT 0,
    FOREIGN KEY (id_jugador) REFERENCES jugador(id_jugador)
);

CREATE TABLE evento (
    id_evento INT AUTO_INCREMENT PRIMARY KEY,
    id_division INT NOT NULL,
    tipo VARCHAR(30) NOT NULL,
    fecha DATETIME NOT NULL,
    rival VARCHAR(100),
    lugar VARCHAR(150),
    FOREIGN KEY (id_division) REFERENCES division(id_division)
);

-- Datos de ejemplo
INSERT INTO entrenador (nombre, licencia_dt) VALUES ('Marcelo Ríos', 'DT-2451');
INSERT INTO tutor (nombre, telefono, email) VALUES ('Laura Gómez', '11-5555-2233', 'laura.gomez@mail.com');
INSERT INTO division (nombre, categoria_edad, id_entrenador) VALUES ('Sub-13', '12-13 años', 1);
INSERT INTO jugador (nombre, apellido, fecha_nacimiento, dni, id_division, id_tutor)
VALUES ('Tomás', 'Gómez', '2013-04-12', '55123456', 1, 1);
INSERT INTO ficha_medica (id_jugador, apto_fisico, fecha_vencimiento, observaciones)
VALUES (1, TRUE, '2027-03-01', 'Sin observaciones');
