# QR Simple 2.0 — Guía Oficial de Publicación en Google Play Store y Auditoría Técnica

Esta guía documenta la auditoría técnica exhaustiva realizada sobre el código fuente de **QR Simple 2.0** (`com.kabrima.qrsimple`), la configuración de compilación y los pasos paso a paso para firmar y publicar la aplicación en Google Play Console de manera profesional, segura y conforme a todas las políticas de Google Play.

---

## 1. Auditoría Técnica de Código y Cumplimiento de Políticas

Antes de publicar, se realizó una revisión integral de arquitectura, ciclo de vida, permisos y seguridad:

| Aspecto Evaluado | Estado | Detalle y Mitigación |
| :--- | :---: | :--- |
| **Permisos Invasivos** | **Mínimo Necesario** | Solo solicita `CAMERA` (escaneo en tiempo real en el dispositivo) y `VIBRATE` (haptic feedback). `INTERNET`, `ACCESS_NETWORK_STATE` y `BILLING` son utilizados por el SDK oficial de Google Play Billing para propinas y diagnósticos de Google ML Kit. El permiso `AD_ID` está explícitamente bloqueado con `tools:node="remove"`. |
| **Procesamiento de Cámara** | **On-Device** | Los fotogramas se procesan directamente en el dispositivo utilizando **Google ML Kit Barcode Scanning**. No se capturan fotos, no se graban videos ni se transmiten fotogramas a internet. |
| **Generador QR y Galería** | **Android 10 a 16 Ready** | Guardado de imágenes mediante `MediaStore.Images` en `Pictures/QRSimple` sin requerir permisos de almacenamiento obsoletos (`WRITE_EXTERNAL_STORAGE`). Compatible con Scoped Storage moderno. |
| **Historial y Base de Datos** | **Privado y Local** | SQLite/Room (`QrDatabase`) alojado en el almacenamiento interno privado de la aplicación. Con soporte completo de búsqueda en tiempo real, filtros y favoritos. |
| **Monetización Ética** | **Play Billing 8.0.0** | Diálogo "Invítame un café" con productos consumibles (`tip_coffee`, `tip_lunch`, `tip_supporter`). Sin funciones bloqueadas, sin muros de pago y sin publicidad. |
| **Compatibilidad de Plataforma** | **Android 8.0 a Android 16** | `minSdk = 26` (Android 8.0 Oreo), `compileSdk = 36` y `targetSdk = 36` (Android 16), cumpliendo con los estándares más estrictos y futuros de Google Play. |
| **Pruebas Automatizadas** | **100% Pasando** | Pruebas unitarias superadas (`./gradlew test`). Verificación `lintVitalRelease` completada con 0 errores fatales. |

---

## 2. Almacén de Claves de Producción (*Keystore*)

La aplicación se encuentra firmada con su almacén de claves criptográficas oficial:

- **Archivo:** `qrsimple-upload-key.jks`
- **Alias:** `qrsimple`
- **Configuración de credenciales:** Definidas de forma segura en `keystore.properties` (ignorado en Git por seguridad).
- **Algoritmo:** RSA 2048 bits (`SHA384withRSA`)
- **Validez:** Hasta el 28 de marzo de 2054
- **Huella digital SHA-256:**
  ```
  1F:F0:57:E7:AC:79:F6:B1:69:D6:DE:69:F8:2D:3C:38:47:2F:2A:C2:0E:89:40:2D:61:73:BA:D6:ED:EA:0D:3D
  ```

> [!IMPORTANT]
> Guarda una copia de respaldo de `qrsimple-upload-key.jks` y de `keystore.properties` en un lugar seguro (por ejemplo, en un gestor de contraseñas o disco cifrado). Si en el futuro subes actualizaciones con otra clave, Google Play rechazará el paquete.

---

## 3. Paquetes de Compilación Generados

| Paquete | Ubicación | Uso |
|---|---|---|
| **Android App Bundle (.aab)** | `/home/franz/PROYECTOS/KBM-QRSimple/app-release.aab` | **Subir a Google Play Console** (Firmado con release key, `applicationId = "com.kabrima.qrsimple"`, `versionCode = 1`, `versionName = "2.0.0"`). |
| **Páginas Legales Estáticas** | `/home/franz/PROYECTOS/KBM-QRSimple/legal/qrsimple/` | Publicar en `https://kabrima.com/qrsimple/privacy` y `terms`. |

---

## 4. Recursos Gráficos Preparados para Google Play Console

En la carpeta [`google_play_assets/`](file:///home/franz/PROYECTOS/KBM-QRSimple/google_play_assets/) dispones de todos los elementos requeridos por la consola:

1. **Ícono de la aplicación (512 x 512 px PNG 32-bit):**
   - [`app_icon_512.png`](file:///home/franz/PROYECTOS/KBM-QRSimple/google_play_assets/app_icon_512.png)
2. **Gráficos de funciones (1024 x 500 px PNG RGB sin transparencia):**
   - 🇪🇸 [`feature_graphic_es_1024x500.png`](file:///home/franz/PROYECTOS/KBM-QRSimple/google_play_assets/feature_graphic_es_1024x500.png)
   - 🌐 [`feature_graphic_1024x500.png`](file:///home/franz/PROYECTOS/KBM-QRSimple/google_play_assets/feature_graphic_1024x500.png)
3. **Capturas de pantalla de teléfono (1080 x 2400 px PNG):**
   - 🇪🇸 [`screenshot_1_es_scanner.png`](file:///home/franz/PROYECTOS/KBM-QRSimple/google_play_assets/screenshot_1_es_scanner.png) — *Escáner Ultra Rápido*
   - 🇪🇸 [`screenshot_2_es_actions.png`](file:///home/franz/PROYECTOS/KBM-QRSimple/google_play_assets/screenshot_2_es_actions.png) — *Acciones Inteligentes (Wi-Fi, URLs, Contactos)*
   - 🇪🇸 [`screenshot_3_es_generator.png`](file:///home/franz/PROYECTOS/KBM-QRSimple/google_play_assets/screenshot_3_es_generator.png) — *Generador de Códigos QR*
   - 🇪🇸 [`screenshot_4_es_history.png`](file:///home/franz/PROYECTOS/KBM-QRSimple/google_play_assets/screenshot_4_es_history.png) — *Historial en tu Teléfono*
   - *(También se generaron las versiones en inglés `screenshot_*_en_*.png`)*
4. **Íconos de productos integrados (512 x 512 px PNG):**
   - ☕ [`tip_coffee_512.png`](file:///home/franz/PROYECTOS/KBM-QRSimple/google_play_assets/tip_coffee_512.png)
   - 🍕 [`tip_lunch_512.png`](file:///home/franz/PROYECTOS/KBM-QRSimple/google_play_assets/tip_lunch_512.png)
   - 🚀 [`tip_supporter_512.png`](file:///home/franz/PROYECTOS/KBM-QRSimple/google_play_assets/tip_supporter_512.png)

---

## 5. Paso a Paso en Google Play Console

Sigue este orden en el panel de control de tu aplicación `com.kabrima.qrsimple`:

### Paso 1: Configurar Productos Integrados (Monetización)
1. En el menú lateral izquierdo, ve a **Monetizar con Play** > **Productos integrados**.
2. Haz clic en **Crear producto**:
   - **ID del producto:** `tip_coffee`
   - **Nombre:** `Invítame un café ☕`
   - **Descripción:** `Apoya el desarrollo independiente invitándome un rico café.`
   - **Precio:** `$1.49 USD` (o equivalente local)
   - **Icono:** Sube [`tip_coffee_512.png`](file:///home/franz/PROYECTOS/KBM-QRSimple/google_play_assets/tip_coffee_512.png)
   - Guarda y activa el producto.
3. Repite para `tip_lunch` ($4.99 USD) y `tip_supporter` ($9.99 USD).

### Paso 2: Ficha Principal de Play Store
1. En el menú ve a **Crecer** > **Presencia en Google Play Store** > **Ficha de Play Store principal**.
2. Pega los textos de [`GOOGLE_PLAY_LISTING.md`](file:///home/franz/PROYECTOS/KBM-QRSimple/GOOGLE_PLAY_LISTING.md):
   - **Nombre de la app:** `QR Simple: Lector y Creador`
   - **Descripción breve:** `Escaneo en el dispositivo, sin anuncios, sin cuentas y con historial privado.`
   - **Descripción completa:** Copia el markdown completo.
3. Sube los gráficos:
   - **Ícono de la aplicación:** [`app_icon_512.png`](file:///home/franz/PROYECTOS/KBM-QRSimple/google_play_assets/app_icon_512.png)
   - **Gráfico de funciones:** [`feature_graphic_es_1024x500.png`](file:///home/franz/PROYECTOS/KBM-QRSimple/google_play_assets/feature_graphic_es_1024x500.png)
   - **Capturas de pantalla para teléfono:** Sube las capturas correspondientes.

### Paso 3: Contenido de la Aplicación y Datos de Contacto
En **Contenido de la aplicación** y **Configuración de la tienda**:
- **Correo electrónico de contacto:** `info@kabrima.com`
- **Política de Privacidad:** Pega `https://kabrima.com/qrsimple/privacy`.
- **Acceso a aplicaciones:** Todas las funciones están disponibles sin restricciones.
- **Anuncios:** Selecciona **No, mi aplicación no contiene anuncios**.
- **Público objetivo:** Apta para todo público (Mayores de 13 años o Todas las edades).
- **Seguridad de los datos:**
  - Sigue la tabla de diagnósticos de Google ML Kit (Información y rendimiento de la app / IDs de dispositivo) especificada en `GOOGLE_PLAY_LISTING.md`.

### Paso 4: Crear y Lanzar la Versión
1. Ve a **Lanzamiento** > **Producción** (o pruebas según corresponda).
2. Haz clic en **Crear nuevo lanzamiento**.
3. Sube el archivo `app-release.aab`.
4. En **Notas de la versión**, pega el texto de novedades v2.0.0.
5. Revisa y confirma el lanzamiento.
