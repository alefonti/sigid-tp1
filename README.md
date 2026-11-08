# SIGID

Sistema de gestión de las divisiones inferiores del Club Social y Deportivo "Estrella del Sur".
Es el prototipo que vengo desarrollando para Seminario de Práctica (Universidad Siglo 21).

Permite registrar jugadores, armar la agenda de entrenamientos y partidos, tomar asistencia,
controlar las fichas médicas, generar convocatorias y avisar a los tutores.
También saca el reporte de presentismo por división, cierra los eventos que quedaron abiertos
y exporta/importa planillas CSV (padrón de jugadores e inscripciones).

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

## Archivos que usa el programa

- `importar/jugadores_nuevos.csv`: planilla de ejemplo para probar la importación de inscripciones
  (tiene algunas líneas con errores a propósito, para ver cómo las informa).
- `reportes/`: ahí quedan los CSV que se exportan (presentismo y padrón). Se crea sola.
- `logs/sigid-errores.log`: detalle técnico de los errores de base de datos y de archivos. Se crea sola.

Los CSV usan `;` como separador, así se abren directo en Excel o LibreOffice.

## Organización del código

- `modelo`: clases del dominio (jugadores, tutores, eventos, fichas médicas, etc.)
- `servicio`: reglas de negocio del club
- `persistencia`: acceso a la base de datos (patrón DAO, con una clase base común para todos los DAO de MySQL)
- `archivos`: lectura y escritura de CSV y bitácora de errores
- `ui`: menú de consola
- `estructuras`: lista enlazada, pila y cola
- `algoritmos`: ordenamiento y búsqueda
- `comun`: excepciones propias

---

Alejandro Fontinovo
