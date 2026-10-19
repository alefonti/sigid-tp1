# SIGID · Sistema de Gestión Integral de Divisiones Inferiores

Prototipo operacional de Seminario de Práctica (Universidad Siglo 21).
Caso: Club Social y Deportivo "Estrella del Sur".

| Entrega | Alcance |
|---|---|
| TP1 | Análisis inicial, arquitectura y prototipo de alta de jugadores y asistencia |
| TP2 | PUD completo (análisis, diseño, implementación, pruebas), base MySQL de 15 tablas, 25 pruebas JUnit |
| **TP3 (v3.0.0)** | **Aplicación de POO en Java: abstracción, herencia, polimorfismo y encapsulamiento; menú de selección con submenús; manejo de excepciones; estructuras de datos propias (lista enlazada, pila, cola); algoritmos de ordenación y búsqueda; 40 pruebas JUnit** |

---

## 1. Tecnologías

| Componente | Versión | Rol |
|---|---|---|
| Java (JDK) | 21 LTS | Lenguaje de desarrollo |
| MySQL | 8.0 | Persistencia de los datos |
| mysql-connector-j | 8.4.0 | Driver JDBC |
| JUnit Jupiter | 5.10.2 | Pruebas unitarias |
| Maven | 3.9+ | Compilación y dependencias |

## 2. Estructura del proyecto

```
src/main/java/ar/org/estrelladelsur/sigid/
├── Main.java                  Punto de entrada: elige MySQL o memoria y lanza el menú
├── comun/                     SigidException (raíz) y DatoInvalidoException (verificada)
├── modelo/                    Persona (abstracta) → Jugador, Tutor, Entrenador, ProfesionalMedico
│                              Evento (abstracta)  → Entrenamiento, Partido
├── estructuras/               ListaEnlazada<T>, Pila<T>, Cola<T> (implementadas con nodos)
├── algoritmos/                Ordenamiento (merge sort, quick sort, inserción) y Busqueda (binaria, secuencial)
├── persistencia/              Contratos DAO, implementaciones JDBC y FabricaDAO (abstracta)
│   └── memoria/               FabricaDAOMemoria: modo demostración sin MySQL
├── servicio/                  Reglas de negocio RN-00 a RN-13
│   ├── notificacion/          Notificador (abstracta) → NotificadorEmail, NotificadorSMS
│   └── historial/             Operacion (abstracta) → OperacionAsistencia, OperacionConvocatoria,
│                              OperacionBajaJugador; HistorialOperaciones (pila para deshacer)
└── ui/                        Entrada (lectura validada), Contexto
    └── menu/                  MenuConsola (abstracta, método plantilla) → MenuPrincipal y 6 submenús
```

## 3. Puesta en marcha

### 3.1 Base de datos

```bash
mysql -u root -p < sql/01_schema.sql
mysql -u root -p < sql/02_datos.sql
```

Usuario de aplicación con privilegios mínimos:

```sql
CREATE USER 'sigid_app'@'localhost' IDENTIFIED BY 'Sigid2026!';
GRANT SELECT, INSERT, UPDATE, DELETE ON sigid_db.* TO 'sigid_app'@'localhost';
FLUSH PRIVILEGES;
```

La conexión se configura en `src/main/resources/sigid.properties`. Para cambiarla sin
recompilar, se puede copiar ese archivo a la carpeta desde donde se ejecuta el programa.

### 3.2 Compilación y ejecución

Desde un IDE (NetBeans, IntelliJ IDEA o Eclipse): abrir la carpeta como proyecto Maven y
ejecutar la clase `ar.org.estrelladelsur.sigid.Main`.

Desde la terminal:

```bash
mvn clean package
java -jar target/sigid-3.0.0-jar-with-dependencies.jar            # usa MySQL
java -jar target/sigid-3.0.0-jar-with-dependencies.jar --memoria  # modo demostración
```

Si MySQL no responde al iniciar, el programa avisa y continúa automáticamente en modo
memoria con los mismos datos de `02_datos.sql`, para que el prototipo pueda probarse en
cualquier equipo.

### 3.3 Pruebas

```bash
mvn test
```

Resultado esperado: **40 pruebas ejecutadas, 40 exitosas, 0 fallidas** (CP-01 a CP-40).

## 4. Menú del prototipo

```
1. Jugadores            registrar, plantel ordenado, buscar por DNI / apellido, baja
2. Agenda deportiva     ver agenda, programar entrenamiento o partido
3. Asistencia           tomar asistencia (con justificación), ranking de presentismo
4. Convocatorias        generar convocatoria con validación médica, ver convocados
5. Área médica          aptitud de un jugador, informe por división, nueva ficha
6. Notificaciones       ver y procesar la cola de avisos (FIFO) por e-mail o SMS
7. Padrón general       listado polimórfico de todas las personas del club
8. Historial            operaciones reversibles de la sesión
9. Deshacer             revierte la última operación (pila LIFO)
0. Salir
```

## 5. Reglas de negocio

| Código | Regla | Clase |
|---|---|---|
| RN-00 | Documento, apellido, nombre y nacimiento son obligatorios | `JugadorService` |
| RN-01 | El año de nacimiento debe corresponder a la división | `JugadorService` |
| RN-02 | No puede superarse el cupo de la división | `JugadorService` |
| RN-03 | No se convoca sin ficha vigente o con lesión activa | `FichaMedicaService` |
| RN-04 | Una convocatoria admite como máximo 18 jugadores | `ConvocatoriaService` |
| RN-05 | La lista confirmada no puede tener menos de 11 jugadores | `ConvocatoriaService` |
| RN-06 | El documento es único en el club | `JugadorService` |
| RN-07 | Sólo se opera sobre registros existentes | servicios |
| RN-08 | El vencimiento de la ficha es posterior a la emisión | `FichaMedicaService` |
| RN-09 | No se registra dos veces la asistencia al mismo evento | `AsistenciaService` |
| RN-10 | La justificación no puede quedar vacía | `AsistenciaService` |
| RN-11 | Sólo los partidos admiten convocatoria (TP3) | `ConvocatoriaService` |
| RN-12 | No se programan eventos en el pasado ni fuera del horario del predio (TP3) | `AgendaService` |
| RN-13 | El tutor debe tener el dato de contacto del canal elegido (TP3) | `Notificador` |

---

Alejandro Agustín Fontinovo · Seminario de Práctica · Universidad Siglo 21
