# SIGID · Sistema de Gestión Integral de Divisiones Inferiores

Prototipo operacional correspondiente al **Trabajo Práctico 2** de Seminario de Práctica
(Universidad Siglo 21). Caso: Club Social y Deportivo "Estrella del Sur".

El prototipo implementa los módulos de **gestión de jugadores, asistencia, fichas médicas,
convocatorias y notificaciones**, respetando la arquitectura en capas y las reglas de negocio
definidas en el informe.

---

## 1. Tecnologías

| Componente | Versión | Rol |
|---|---|---|
| Java (JDK) | 21 LTS | Lenguaje de desarrollo |
| MySQL | 8.0 | Persistencia de los datos |
| mysql-connector-j | 8.4.0 | Driver JDBC |
| JUnit Jupiter | 5.10.2 | Modelo de pruebas del PUD |
| Maven | 3.9+ | Compilación y gestión de dependencias |

## 2. Estructura del proyecto

```
proyecto_java/
├── pom.xml
├── sql/
│   ├── 01_schema.sql      Creación de la base y de las 15 tablas
│   ├── 02_datos.sql       Inserción de registros de prueba
│   └── 03_consultas.sql   Consultas SELECT, UPDATE y DELETE
└── src/
    ├── main/java/ar/org/estrelladelsur/sigid/
    │   ├── Main.java                 Punto de entrada
    │   ├── ui/ConsolaUI.java         Capa de presentación
    │   ├── servicio/                 Capa de lógica de negocio (reglas RN-01 a RN-10)
    │   ├── persistencia/             Capa de acceso a datos (patrón DAO + Singleton)
    │   └── modelo/                   Modelo de dominio (POJOs y enumeraciones)
    └── test/java/ar/org/estrelladelsur/sigid/servicio/
        ├── JugadorServiceTest.java       CP-01 a CP-05
        ├── FichaMedicaServiceTest.java   CP-06 a CP-12
        ├── ConvocatoriaServiceTest.java  CP-13 a CP-19
        ├── AsistenciaServiceTest.java    CP-20 a CP-25
        └── Doble*.java                   Dobles de prueba en memoria
```

## 3. Puesta en marcha

### 3.1 Base de datos

```bash
mysql -u root -p < sql/01_schema.sql
mysql -u root -p < sql/02_datos.sql
```

Luego crear el usuario de aplicación con privilegios mínimos:

```sql
CREATE USER 'sigid_app'@'%' IDENTIFIED BY '<clave>' REQUIRE SSL;
GRANT SELECT, INSERT, UPDATE, DELETE ON sigid_db.* TO 'sigid_app'@'%';
FLUSH PRIVILEGES;
```

### 3.2 Configuración de la conexión

Ajustar la URL, el usuario y la clave en
`src/main/java/ar/org/estrelladelsur/sigid/persistencia/ConexionBD.java`.

### 3.3 Compilación y ejecución

```bash
mvn clean package
java -jar target/sigid-2.0.0.jar
```

### 3.4 Ejecución de las pruebas

```bash
mvn test
```

Resultado esperado: **25 pruebas ejecutadas, 25 exitosas, 0 fallidas.**

## 4. Reglas de negocio implementadas

| Código | Regla | Clase que la aplica |
|---|---|---|
| RN-01 | El año de nacimiento del jugador debe corresponder a la división | `JugadorService` |
| RN-02 | No puede superarse el cupo máximo de la división | `JugadorService` |
| RN-03 | No se convoca a un jugador sin ficha vigente o con lesión activa | `FichaMedicaService` |
| RN-04 | Una convocatoria admite como máximo 18 jugadores | `ConvocatoriaService` |
| RN-05 | La lista confirmada no puede tener menos de 11 jugadores | `ConvocatoriaService` |
| RN-06 | El documento del jugador es único en el club | `JugadorService` |
| RN-08 | El vencimiento de la ficha debe ser posterior a su emisión | `FichaMedicaService` |
| RN-09 | No se registra dos veces la asistencia al mismo evento | `AsistenciaService` |
| RN-10 | La justificación de una inasistencia no puede quedar vacía | `AsistenciaService` |

## 5. Decisiones de diseño

- **Arquitectura en tres capas** (presentación, negocio, persistencia) más el modelo de
  dominio, para que un cambio de interfaz o de motor de base de datos no afecte a las
  reglas del club.
- **Patrón DAO con interfaces**: la capa de servicio depende de `JugadorDAO`,
  `FichaMedicaDAO`, etc., y no de sus implementaciones JDBC. Eso permite sustituirlas por
  dobles de prueba en memoria y ejecutar la totalidad de las pruebas unitarias sin
  levantar MySQL.
- **Singleton en `ConexionBD`**: centraliza la configuración de la conexión.
- **`PreparedStatement` en todas las sentencias**: evita la inyección de SQL (RNF-02).
- **Excepciones de negocio con código de regla** (`ReglaNegocioException`): la capa de
  presentación muestra el motivo exacto del rechazo y la trazabilidad con el informe es
  directa.

---

Alejandro Agustín Fontinovo · Seminario de Práctica · Universidad Siglo 21
