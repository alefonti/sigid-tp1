# SIGID

Sistema de gestión de las divisiones inferiores del Club Social y Deportivo "Estrella del Sur".
Es el prototipo que vengo desarrollando para Seminario de Práctica (Universidad Siglo 21).

Permite registrar jugadores, armar la agenda de entrenamientos y partidos, tomar asistencia,
controlar las fichas médicas, generar convocatorias y avisar a los tutores.

## Requisitos

- Java 21
- MySQL 8
- Maven (o cualquier IDE que abra proyectos Maven)

## Cómo ejecutarlo

1. Crear la base con los scripts de la carpeta `sql`:

   ```
   mysql -u root -p < sql/01_schema.sql
   mysql -u root -p < sql/02_datos.sql
   ```

2. Revisar usuario y contraseña de la base en `src/main/resources/sigid.properties`.

3. Abrir el proyecto en el IDE y ejecutar la clase `Main`.

Si MySQL no está disponible, el programa avisa y arranca igual con datos de prueba en memoria.
También se puede forzar ese modo pasándole el argumento `--memoria`.

Las pruebas se corren con `mvn test`.

## Organización del código

- `modelo`: clases del dominio (jugadores, tutores, eventos, fichas médicas, etc.)
- `servicio`: reglas de negocio del club
- `persistencia`: acceso a la base de datos
- `ui`: menú de consola
- `estructuras`: lista enlazada, pila y cola
- `algoritmos`: ordenamiento y búsqueda
- `comun`: excepciones propias

---

Alejandro Fontinovo
