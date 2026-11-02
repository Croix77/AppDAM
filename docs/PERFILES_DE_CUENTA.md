# Perfiles de cuenta

## Acceso

El estudiante toca el avatar o el texto Mi perfil en la esquina superior derecha del inicio. Docente y UTP utilizan el botón Mi perfil en la barra de su panel. Todos disponen de Volver y un botón para regresar a su inicio.

## Información disponible

| Cuenta | Información |
|---|---|
| Estudiante | Nombre e imagen, usuario, identificador ficticio, curso, docente asignado, establecimiento y asignaturas con unidades publicadas asignadas |
| Docente | Nombre/avatar, usuario, identificador ficticio, curso a cargo, establecimiento y descripción de su función |
| UTP | Nombre/avatar, usuario, identificador ficticio, área de trabajo, establecimiento y descripción de su función pedagógica |

Todos los datos son de demostración y solo de consulta en el perfil propio. No se muestran claves, sales ni derivaciones de contraseñas. No se agregaron campos para RUT, teléfono, fecha de nacimiento ni datos reales de menores.

El perfil del estudiante no muestra informes pedagógicos, observaciones reservadas, comparaciones de rendimiento ni métricas consolidadas. La imagen y las asignaciones continúan siendo administradas por el docente desde el flujo de gestión existente. La pantalla no habilita edición general de los datos de cuenta; si se requiere, debe implementarse como una función separada con autorización y validaciones.

Los controles de tamaño de texto y Salir se ubican dentro del perfil. Se conserva la preferencia DataStore ya implementada.

## Implementación al nivel de las guías

- AccountProfileScreen.kt construye una sola pantalla reutilizada según el rol mediante when, con Page, Panel, Column, Row, Text, Avatar y botones.
- El modelo User existente ya contiene los datos necesarios; no hubo migración SQLite ni cambios de identificadores.
- SchoolViewModel obtiene el nombre del docente asociado por teacherId solo para el estudiante autenticado. La pantalla usa s.user, no s.student, que representa al alumno seleccionado por un docente en otra pantalla.
- SchoolApp agrega la ruta profile en el NavHost existente. Al salir de la cuenta se limpia el estado con el mecanismo de sesión previo.
- HomeScreen utiliza Modifier.clickable para que tanto el avatar como su etiqueta abran el perfil, con una descripción de accesibilidad.

Se mantienen los patrones de las guías 8 y 11, sin dependencias nuevas ni edición de datos desde los composables.

## Verificación

Se verifica información del estudiante, restricciones de controles, tamaño de texto, cierre de sesión y perfil UTP. También se abre el perfil propio del docente después de consultar la imagen de un alumno para comprobar que no se confunden ambas cuentas.
