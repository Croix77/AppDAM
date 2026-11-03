# Trabajos, entregas y evaluación docente

Esta ampliación fue solicitada por el usuario. Complementa las prácticas y pruebas automáticas; no cambia sus preguntas, respuestas ni límites de intentos.

## Recorrido

**Docente:** Mis cursos → elegir curso → Trabajos → Crear trabajo. Selecciona asignatura, título, instrucciones y fecha límite. Al asignar, se vincula el trabajo con los estudiantes que pertenecen a ese curso y a ese docente en ese momento.

En el detalle aparecen las entregas con estados Pendiente, Entregado y Revisado. El docente abre una entrega, escribe una nota de 1,0 a 7,0 (máximo un decimal) y un comentario. Puede guardar una corrección provisional; el estudiante no la ve. Publicar evaluación solicita una confirmación y hace visibles la nota y el comentario.

**Estudiante:** Inicio → Mis trabajos → elegir trabajo. Puede guardar un borrador de texto, cerrar sesión y continuarlo después. Entregar solicita confirmación. La entrega enviada queda bloqueada para evitar cambios después de la revisión. Cuando la evaluación está publicada, ve su nota y el comentario del docente.

## Cursos y cuentas

- 2.º Básico A: Sol (`estudiante`) y Luna (`luna`).
- 2.º Básico B: Mateo (`mateo`).
- Clave de los estudiantes ficticios: `Aprendo123`.
- Los tres están asignados a la cuenta `docente / Profe123`.

La cuenta de Mateo se agrega al crear o migrar la base, si todavía no existe. Recibe una copia de las asignaciones de unidades de Sol; no se copian respuestas, intentos, notas ni fotos. La interfaz organiza cursos ya existentes; no incluye un administrador de instituciones, matrícula o altas de cursos.

## Reglas

- Solo un docente puede crear trabajos, y únicamente para un curso con estudiantes asignados a él.
- Cada trabajo se asigna a los estudiantes presentes al crearlo. Incorporar alumnos posteriormente no los asigna automáticamente a trabajos anteriores.
- El estudiante solo consulta y responde trabajos que tiene asignados. No puede consultar la respuesta de un compañero.
- El borrador de respuesta es privado del estudiante. El docente recibe la respuesta al entregarla.
- El plazo incluye todo el día de la fecha límite según la fecha del dispositivo. No se admiten entregas atrasadas.
- Respuesta escrita: hasta 4000 caracteres. Comentario docente: hasta 2000 caracteres.
- Se aceptan notas como `6`, `6,5` o `6.5`. Se rechazan valores fuera de 1,0–7,0 y más de un decimal; no se redondean silenciosamente.
- Para publicar son obligatorios una nota válida y un comentario. Una evaluación publicada no se puede editar en esta versión.

## Implementación y relación con lo estudiado

Las pantallas usan Compose y Material 3: Column, Row, Surface, Card, campos, botones, FilterChip y AlertDialog. Los formularios conservan su texto con rememberSaveable, el ViewModel comparte el estado y NavHost conecta las rutas. No se incorporan dependencias nuevas.

SQLiteOpenHelper pasa a versión 3. La migración añade `school_work` (trabajos), `work_students` (asignaciones) y `work_submissions` (borradores, entregas y evaluación). Las tablas de aprendizaje anteriores se conservan. La base utiliza claves foráneas y transacciones para no guardar trabajos sin destinatarios ni evaluaciones parciales.

La nota se almacena como un entero de décimas: `65` representa `6,5`. Esto evita cálculos de punto flotante para una nota que solo admite un decimal. El repositorio valida permisos y estados, aunque se intentara invocar una acción fuera de su pantalla. En las consultas del estudiante, la nota y el comentario provisional se ocultan desde el repositorio, no solo desde la interfaz.

Estos detalles de SQLite, migración y permisos son complementos explicados aquí y en GUIA_COMPLEMENTARIA.md; no deben presentarse como contenidos literales de las guías.

## Límites

Las entregas son texto: no se adjuntan PDF, imágenes ni otros archivos. Todo funciona en una misma instalación, alternando cuentas. SQLite no sincroniza teléfonos ni envía notificaciones. La nota la decide el docente; no hay calificación automática de los trabajos. UTP mantiene su gestión de unidades y no accede a correcciones privadas de trabajos.
