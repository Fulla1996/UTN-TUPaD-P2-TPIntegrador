# Sistema de Gestión de Productos y Códigos de Barras

## Trabajo Práctico Integrador - Programación 2

### Descripción del Proyecto

Este Trabajo Práctico Integrador tiene como objetivo demostrar la aplicación práctica de los conceptos fundamentales de Programación Orientada a Objetos, JDBC y Arquitectura en Capas aprendidos durante la cursada de Programación 2.

El sistema desarrollado permite gestionar productos y sus códigos de barras, modelando una relación 1:1 entre ambas entidades, implementando persistencia en MySQL y un diseño profesional en capas.

Se implementan operaciones CRUD completas, validaciones de negocio, transacciones y una estructura limpia y mantenible que sigue estándares reales de la industria.

### Objetivos Académicos

El desarrollo de este proyecto permite aplicar y consolidar los siguientes conceptos clave:

**1. Arquitectura en Capas (Layered Architecture)**
- Implementación de separación de responsabilidades en 4 capas diferenciadas:
    - Capa de Presentación (Main/UI): Interacción con el usuario mediante consola
    - Capa de Lógica de Negocio (Service): Validaciones y reglas de negocio
    - Capa de Acceso a Datos (DAO): Operaciones de persistencia
    - Capa de Modelo (Models): Representación de entidades del dominio

**2. Programación Orientada a Objetos**
- Aplicación de principios SOLID (Single Responsibility, Dependency Injection)
- Uso de herencia mediante clase abstracta Base
- Implementación de interfaces genéricas (GenericDAO, GenericService)
- Encapsulamiento con atributos privados y métodos de acceso
- Sobrescritura de métodos (equals, hashCode, toString)

**3. Persistencia de Datos con JDBC**
- Conexión a base de datos MySQL mediante JDBC
- Implementación del patrón DAO (Data Access Object)
- Uso de PreparedStatements para prevenir SQL Injection
- Gestión de transacciones con commit y rollback
- Manejo de claves autogeneradas (AUTO_INCREMENT)
- Consultas con LEFT JOIN para relaciones entre entidades

**4. Manejo de Recursos y Excepciones**
- Uso de bloques try/catch/finally
- Cierre correcto de conexiones JDBC
- Manejo apropiado de excepciones con propagación controlada
- Validación multi-nivel: base de datos y aplicación

**5. Patrones de Diseño**
- DAO (Data Access Object): Aísla el acceso a base de datos del resto del sistema.
- Service Layer: Orquesta validaciones, transacciones y reglas del negocio.
- Factory (DatabaseConnection): Centraliza la creación de conexiones JDBC
- Soft Delete (Eliminación lógica): Las entidades no se eliminan físicamente, sino que eliminado = TRUE.

**6. Validación de Integridad de Datos**
Se implementan validaciones tanto en la aplicación como en la base de datos:
- Validaciones en aplicación (capa de servicio)
    Precio > 0
    Peso > 0
    Nombre no vacío
    Tipo de código válido (EAN13 / EAN8 / UPC)
    Longitud del código consistente con su tipo
    Existencia del código de barras antes de asociarlo a un producto

- Validaciones en base de datos
    CHECK de longitud del código
    CHECK de precios y peso
    Claves primarias
    Unique en valor del código de barras
    Clave foránea 1:1 entre Producto → CódigoBarras
    Clave foránea 1:1 entre `Producto → CodigoBarras`

- **Modelo de Datos – Relación 1:1**  
    `Producto (1) ───────── (1) CodigoBarras`
  - Cada producto tiene un único código de barras.  
  - Cada código de barras pertenece a un único producto.
    
### Funcionalidades Implementadas

El sistema permite gestionar dos entidades principales con las siguientes operaciones:

## Características Principales

- **Gestión de Productos** CRUD completo con validaciones y transacciones.
- **Gestión de Códigos de Barras**: CRUD completo con validaciones por tipo.
- **Operación avanzada**: Crear producto y código de barras juntos (transacción atómica).
- **Eliminación lógica**: Los registros no se borran; se marcan con eliminado = TRUE.
- **Integridad absoluta**: No se permite ingresar un producto cuyo codigoBarras.id no exista.

## Requisitos del Sistema

| Componente | Versión Requerida |
|------------|-------------------|
| Java JDK | 17 o superior |
| MySQL | 8.0 o superior |
| Gradle | 8.12 (incluido wrapper) |
| Sistema Operativo | Windows, Linux o macOS |

## Instalación

### 1. Crear Base de Datos

El proyecto usa este script (ya incluido y ejecutado por vos):

```sql
CREATE DATABASE IF NOT EXISTS tpint;
USE tpint;
CREATE TABLE CodigoBarras(
    id BIGINT PRIMARY KEY,
    eliminado BOOLEAN DEFAULT FALSE,
    tipo VARCHAR(5) NOT NULL CHECK (tipo IN ('EAN13','EAN8','UPC')),
    valor VARCHAR(20) NOT NULL UNIQUE,
    fechaAsignacion DATE,
    observaciones VARCHAR(255),
    CHECK((tipo = 'EAN13' AND CHAR_LENGTH(valor) = 13)
        OR (tipo = 'EAN8' AND CHAR_LENGTH(valor) = 8)
        OR (tipo = 'UPC' AND CHAR_LENGTH(valor) = 12))
);
CREATE TABLE Producto(
    id BIGINT PRIMARY KEY,
    eliminado BOOLEAN DEFAULT FALSE,
    nombre VARCHAR(120) NOT NULL, marca VARCHAR(80),
    categoria VARCHAR(80),
    precio DECIMAL(10,2) NOT NULL CHECK(precio > 0),
    peso DECIMAL(10,3) CHECK(peso > 0),
    codigoBarras BIGINT UNIQUE NOT NULL,
    CONSTRAINT fk_codigo FOREIGN KEY (codigoBarras)
        REFERENCES CodigoBarras(id)
);
```
2. Configurar la Conexión
    
La clase DatabaseConnection obtiene los datos de conexión (por ejemplo) desde un archivo db.properties, que define:
```
db.url=jdbc:mysql://localhost:3306/tpint
db.user=root
db.password= ""
```
Ajustar usuario/contraseña según la configuración local de MySQL. 

## Arquitectura del Sistema

```
UTN-TUPaD-P2-TPIntegrador/
src/
   ├── Config/
   |   ├── DatabaseConnection.java
   |   └── TransactionManager
   ├── Dao/
   |   ├── GenericDao.java
   |   ├── ProductoDaoImpl.java
   |   └── CodigoBarrasDaoImpl.java
   ├── Main/
   |   ├── AppMenu.java
   |   ├── Main.java
   |   ├── MenuDisplay.java
   |   ├── MenuHandler.java
   |   └── TestConexion,java
   ├── Models/Entities/
   |   ├── Base.java
   |   ├── TipoCB.java 
   |   ├── Producto.java 
   |   └── CodigoBarras.java          
   └── Service/
       ├── GenericService.java
       ├── ServiceProducto.java 
       └── ServiceCodigoBarras.java
```

## Patrones y Buenas Prácticas Implementadas
- Validación en capa de servicio (no en la UI).
- Restricciones fuertes en la base de datos.
- Eliminación lógica (no se pierde información).
- Transacciones en operaciones críticas.
- Código organizado, con responsabilidades claras y comentado.
- PreparedStatement en todas las consultas para mayor seguridad.
- Uso de IDs coherentes, evitando valores no válidos.

## Reglas de Negocio Específicas
- Un producto debe tener un código de barras válido y existente (o crearse junto con él).
- El valor del código de barras no puede repetirse en la base de datos.
- El precio y el peso deben ser mayores que cero.
- La eliminación de registros es lógica, no física.
- Si se crea producto + código de barras en la misma operación, debe hacerse en una única transacción.
- El tipo de código de barras determina la longitud válida (13/12/8 dígitos).
  
## Evaluación Académica Cubierta

Este proyecto demuestra:
- Arquitectura profesional en capas
- Aplicación de POO (encapsulamiento, interfaces, modelo de dominio)
-  Uso correcto de JDBC y patrón DAO
-  Manejo de transacciones y rollback
-  Integridad referencial con claves foráneas
-  CRUD completo sobre Productos y Códigos de Barras
-  Buenas prácticas de validación y eliminación lógica
-  Documentación clara y estructurada
