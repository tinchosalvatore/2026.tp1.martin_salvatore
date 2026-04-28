# Documento de Diseño Técnico - BiblioTech

Este documento detalla las decisiones de arquitectura, diseño y las "leyes" del sistema implementadas para alcanzar el **Nivel Experto** definido en la rúbrica del proyecto.

---

## 1. Arquitectura y Reglas de Negocio

### Gestión de Roles y Seguridad
Se ha implementado una distinción clara entre roles para cumplir con los requerimientos de gestión y uso del sistema:
- **Bibliotecario (Librarian):** Actúa como el administrador del sistema. Es el único actor capaz de registrar nuevos recursos, dar de alta socios, procesar devoluciones y visualizar el historial completo de transacciones.
- **Socios (Estudiante/Docente):** Tienen acceso restringido. Su flujo principal es la búsqueda de recursos y la solicitud de préstamos. El sistema los identifica mediante su DNI para validar sus límites de préstamo y estados de sanción.

### Ley de Sanciones (Bonus)
Para fomentar la responsabilidad en las devoluciones, se implementó un sistema de sanciones automáticas:
- **Cálculo:** Se optó por una penalización exponencial de tiempo. Por cada día de retraso en la devolución de un recurso, el socio recibe una sanción de **2 días de suspensión** por cada día de mora (Días de retraso * 2).
- **Efecto:** Mientras un socio esté sancionado (fecha actual < fecha de liberación), el sistema bloquea automáticamente cualquier nueva solicitud de préstamo mediante el `LoanValidator`, lanzando una `ValidationException`.

### Reglas de Préstamo y Consistencia
- **Límites por Tipo de Socio:** Implementado mediante el método `maxLoans()` en la interfaz `Member`, con valores específicos para `Student` (3) y `Teacher` (5).
- **Validación de Integridad:** Un préstamo solo se registra si:
    1. El recurso existe y no tiene un préstamo activo (`ResourceNotAvailableException`).
    2. El socio existe y no ha superado su límite (`MemberLimitExceededException`).
    3. El socio no tiene deudas vencidas ni sanciones activas.

---

## 2. Diseño de Software y Patrones (SOLID)

### Composición y Acoplamiento (Dependency Injection)
Se ha evitado el uso de "Singletons" globales o instanciación directa dentro de los servicios.
- **Composition Root:** `Main.java` asume la responsabilidad de instanciar todo el grafo de objetos. Primero se crea la base de datos, luego los repositorios, los validadores y finalmente los servicios. 
- **Inyección por Constructor:** Todos los servicios reciben sus dependencias (interfaces) por constructor, lo que facilita enormemente el testing unitario y permite intercambiar la persistencia (Memory vs JSON) sin tocar una sola línea de lógica de negocio.

### Abstracción de Datos (Generics & Identifiable)
Para evitar la duplicación de código en la capa de persistencia, se diseñó la interfaz `Identifiable<ID>`.
- Todas las entidades del modelo (`Loan`, `Resource`, `Member`, `Sanction`) implementan esta interfaz.
- Esto permite que `JsonRepository<T, ID>` sea completamente genérico: puede buscar, filtrar y eliminar cualquier entidad basándose únicamente en su contrato de identidad, sin conocer los detalles del objeto.

### Segregación de Interfaces (ISP)
No existe una única interfaz de repositorio gigante. Cada dominio tiene su propia interfaz (`ResourceRepository`, `MemberRepository`) que extiende de la base genérica. Esto permite añadir métodos de búsqueda especializada (como el motor de búsqueda por criterios múltiples) sin "ensuciar" el contrato básico de persistencia.

---

## 3. Implementación Técnica (Expert Level)

### Persistencia Polimórfica con Jackson
El desafío técnico fue persistir jerarquías de interfaces (`Resource` y `Member`) en un único archivo JSON.
- **Metadata de Tipo:** Se configuró Jackson para incluir una propiedad `"type"` en el JSON.
- **Módulos Específicos:** Se registraron `Jdk8Module` y `JavaTimeModule`. Esto es crucial para que el sistema pueda serializar tipos modernos de Java como `Optional<T>` (usado en `returnDate`) y `LocalDate` (usado en todas las fechas). Sin estos módulos, el motor de JSON fallaría al encontrar estos tipos complejos.

### Manejo de Tiempo Moderno (java.time)
Se ha prohibido el uso de la antigua API `java.util.Date`. En su lugar, se utiliza exclusivamente `java.time.LocalDate`.
- Los cálculos de demora y fechas de vencimiento se realizan mediante `ChronoUnit.DAYS.between()`, garantizando precisión y evitando problemas de zonas horarias o formatos de fecha locales.

### Inmutabilidad con Records
Todas las entidades son `records`. Esta decisión de diseño garantiza que:
1. El estado de un préstamo o recurso no pueda ser alterado accidentalmente una vez creado (Thread-safety).
2. Para realizar un cambio (como marcar una devolución), se debe crear una nueva instancia del record, lo que hace que las transiciones de estado sean explícitas y fáciles de rastrear.

---

## 4. Interfaz de Usuario (CLI)
La interfaz de línea de comandos fue diseñada para ser robusta pero simple:
- **Banner Pro:** Al iniciar, el sistema compone dinámicamente el logo y el nombre del sistema desde archivos externos.
- **Manejo de Ciclo de Vida:** El sistema utiliza un bucle infinito que solo termina mediante una salida controlada, asegurando que la `JsonDatabase` guarde el estado final correctamente antes de cerrar.

---

## 5. Funcionalidades Finales por tipo de Usuario
#### Bibliotecario
**US 1.1: Registro de Recursos Multiformato**
- registro de e-books y libros fisicos
**US 1.2: Alta de Socios con Diferenciación**
- registro de estudiantes y docentes
**US 1.3: Gestión de Devoluciones y Control de demora**
- procesamiento de devoluciones y calculo automatico de demora por parte del sistema
**US 1.4: Auditoría de Transacciones**
- historial completo de préstamos y devoluciones

#### Socio
**US 2.1: Autenticación por Identidad**
- ingreso al sistema mediante DNI como forma de "login"
**US 2.2: Búsqueda Multicriterio de Catálogo**
- busqueda de libros por título o autor
**US 2.3: Solicitud de Préstamo con Validación Automática**
- solicitud de préstamo mediante ISBN, siempre y cuando no haya superado el límite legal, no tenga deudas vencidas y no esté sancionado

#### Sistema
**US 3.1: Persistencia Transparente**
- todos los cambios se guardan automáticamente en un archivo JSON
**US 3.2: Feedback de Errores de Negocio**
- mensajes claros cuando una operación falla (ej: "Socio sancionado", "Libro no disponible")