# Firma del APK release

El APK de esta entrega se firma localmente con una clave del equipo. El archivo de clave y sus contraseñas no se guardan en Git. El APK final se puede compartir por separado; futuras versiones deben usar la misma clave para actualizar la aplicación instalada.

## Configurar en otro equipo

1. Obtener la clave del equipo por un canal privado y guardarla fuera de la carpeta del repositorio. No enviarla junto al APK.
2. Crear fuera del repositorio un archivo `signing.properties` con este formato:

   ```properties
   storeFile=C:/ruta/privada/release-key.p12
   storePassword=CONTRASENA_DEL_KEYSTORE
   keyAlias=ALIAS_DEL_EQUIPO
   keyPassword=CONTRASENA_DE_LA_CLAVE
   ```

3. En PowerShell, indicar la ruta privada y compilar:

   ```powershell
   $env:MPAZ_SIGNING_PROPERTIES = 'C:\ruta\privada\signing.properties'
   .\gradlew.bat assembleRelease
   ```

   También se puede definir `mpaz.signingProperties` en el `local.properties` local, que está excluido de Git.

## APK de esta entrega

- Ruta local: `app/build/outputs/apk/release/app-release.apk`.
- Firma comprobada con `apksigner verify --verbose --print-certs` (esquema v2).
- Certificado: `CN=NQDESTROYER, OU=Equipo 5, O=MPAZ PRO, L=San Bernardo, ST=Region Metropolitana, C=CL`.
- SHA-256 del APK: `6663B3F38EA6FFE4CBE4332F52E02D04DDDE8D8C4C0A496FBA7D217301860E33`.

Instalar en un teléfono físico y comprobar cámara, galería, audio y descarga antes de la demostración final.
