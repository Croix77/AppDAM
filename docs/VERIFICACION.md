# Verificación del MVP

Fecha: 2 de noviembre de 2026. Entorno local: Windows, Android Studio JBR 21, SDK de compilación 36, emulador Pixel 3a x86_64 con API 37. El proyecto conserva minSdk 26 y targetSdk 36.

## Resultados comprobados

- `assembleDebug`: correcto.
- `testDebugUnitTest`: 4 pruebas correctas (incluye la prueba de plantilla).
- `connectedDebugAndroidTest`: 10 pruebas correctas en tamaño compacto, y 10 correctas con tamaño expandido simulado (1600 x 1200, densidad 160). Las dimensiones se restauraron al finalizar.
- Las 3 pruebas de interfaz también pasaron en tamaño medio simulado (1200 x 1600, densidad 240, ancho de 800 dp).
- `lintDebug`: correcto, sin errores bloqueantes. Conserva advertencias por versiones más recientes de herramientas/dependencias, recursos de plantilla sin uso, ubicación del logo y sugerencias de estilo. No se actualiza el entorno de forma innecesaria solo para silenciar esas advertencias.
- Login de estudiante, docente y UTP; práctica correcta/incorrecta, prueba enviada, revisión y acceso a gestión: comprobados mediante pruebas Compose.
- Persistencia al cerrar y reabrir la base, límite de intentos, validaciones, fechas, bloqueo por rol, publicación y deshabilitación: comprobados con SQLite real en una base de pruebas separada.
- Cámara del emulador: abrir, capturar, aceptar, ver vista previa y guardar copia JPEG privada: comprobado.
- Galería del emulador: abrir selector del sistema, seleccionar imagen de prueba, confirmar y guardar: comprobado.
- Cancelación de captura: regresa sin fallos y conserva la imagen guardada: comprobado.
- Capturas de inicio, unidad, prueba y paneles: inspeccionadas visualmente. No se usaron personas reales.

Las primeras ampliaciones de la prueba de UI fallaron por buscar el texto exacto de una alternativa sin su símbolo de selección. Se corrigió el selector y la sincronización con el guardado; la ejecución completa posterior terminó sin fallos. No se ocultan ni se convierten en aprobadas pruebas fallidas.

## Pendiente de comprobación por el equipo

- Cámara y galería en teléfono físico, como pide la guía 13. Probar además orientación de fotos y cancelaciones en ese dispositivo.
- Abrir el informe TXT desde una aplicación externa después de descargarlo; el texto generado y la autorización docente sí se comprobaron en el repositorio.
- Escuchar y validar el audio con altavoces del dispositivo objetivo. La reproducción usa un WAV local; no se verificó audibilidad desde el equipo anfitrión.
- Validación pedagógica de objetivos y materiales, aceptación de cámara/galería como adaptación del caso, integrantes/número de equipo y evidencias reales de Trello.
- Firma release bajo control del equipo. El APK entregado es debug, apto para pruebas.

La aplicación no implementa sincronización remota ni backend. Las pruebas no certifican seguridad de producción ni equivalencia completa con el sistema institucional descrito como evolución del caso.

## Ampliación de actividades por asignatura

- Ocho unidades y 42 preguntas totales; cuatro unidades nuevas con 14 prácticas y 12 preguntas de prueba.
- Cinco pruebas unitarias aprobadas, incluyendo validación numérica y orden de pasos.
- Doce pruebas en emulador aprobadas, incluyendo escritura de resultado numérico, orden de un relato y migración real desde el esquema SQLite v1 sin perder respuestas/intent os.
- Compilación y lint correctos. Las comprobaciones anteriores de tamaños y recursos nativos corresponden a la entrega base; la ampliación se ejecutó en el tamaño compacto del mismo Pixel 3a.
