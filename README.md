# QR Simple 2.0 — Escáner y Generador de Códigos QR

[![License: GPL v3](https://img.shields.io/badge/License-GPLv3-blue.svg)](https://www.gnu.org/licenses/gpl-3.0)
[![Android](https://img.shields.io/badge/Android-8.0%2B%20(API%2026%2B)-green.svg)](https://developer.android.com)
[![Google Play](https://img.shields.io/badge/Google_Play-QR_Simple-brightgreen.svg?logo=google-play&logoColor=white)](https://play.google.com/store/apps/details?id=com.kabrima.qrsimple)

**QR Simple 2.0** es una aplicación moderna para Android diseñada y construida con Jetpack Compose y Material 3 para escanear y generar códigos QR con velocidad instantánea, **cero anuncios**, **sin creación de cuentas**, con **historial local en el dispositivo** y soporte multilingüe en 5 idiomas.

Disponible en [Google Play Store](https://play.google.com/store/apps/details?id=com.kabrima.qrsimple).

---

## 📢 Aviso sobre Código Abierto y Dependencias (Google ML Kit)

QR Simple es software libre de código abierto bajo los términos de la licencia **GNU General Public License v3.0 (GPLv3)**.

> **Nota sobre el motor de escaneo:**  
> Para la detección ultrarrápida y en tiempo real de códigos de barras y QR en el dispositivo (on-device), la aplicación utiliza la biblioteca **Google ML Kit Barcode Scanning** (`com.google.android.gms:play-services-mlkit-barcode-scanning`).  
> 
> Ten en cuenta que **Google ML Kit es una biblioteca propietaria de código cerrado** provista por Google LLC. De acuerdo con las políticas y la arquitectura de Google ML Kit, el SDK envía de forma automática datos técnicos de diagnóstico y uso (como información del dispositivo, identificadores técnicos por instalación y métricas de rendimiento/errores) cifrados mediante HTTPS a los servidores de Google para el mantenimiento y monitoreo del servicio. No se transmiten imágenes ni el contenido de los códigos escaneados. Para más información, consulta la [Divulgación de datos de Google ML Kit](https://developers.google.com/ml-kit/android-data-disclosure).

---

## 📱 1. Ficha Técnica de la APK / Bundle

| Parámetro | Valor Oficial | Detalle |
|---|---|---|
| **Nombre de la Aplicación** | **QR Simple** | Definido en `strings.xml` y adaptable por idioma. |
| **Identificador de Paquete (Package ID)** | `com.kabrima.qrsimple` | Mapeo exacto a la app registrada en Google Play Console (`applicationId`). |
| **Espacio de Nombres (Namespace)** | `qrsimple.franz.com.qrsimple` | Namespace interno de paquetes Kotlin. |
| **Versión (versionName)** | `2.0.0` | Salto mayor: reescritura total a Compose + Material 3. |
| **Código de Versión (versionCode)** | `1` | Versión inicial de lanzamiento para la nueva publicación. |
| **SDK Mínimo (minSdk)** | `26` (Android 8.0 Oreo) | Cobertura de más del 95% de dispositivos activos. |
| **SDK Objetivo (targetSdk)** | `36` (Android 16) | Cumple con los requisitos más estrictos y futuros de Google Play. |
| **SDK de Compilación (compileSdk)** | `36` | Soporte para las APIs más recientes. |
| **Toolchain Java / Kotlin** | Adoptium OpenJDK 17 / Kotlin 2.1.0 | Compilación reproducible en entornos modernos. |
| **Formato de Producción** | **Android App Bundle (.aab)** | Paquete optimizado con Play Feature Delivery y recursos divididos. |

---

## 🌍 2. Soporte Multilingüe Completo (i18n)

QR Simple implementa localización completa en **5 idiomas principales**. Toda la interfaz se adapta automáticamente al idioma del sistema y es compatible con el selector de idiomas por aplicación de Android 13+ (`res/xml/locales_config.xml`):

| Código | Idioma | Carpeta de Recursos | Estado |
|:---:|---|---|:---:|
| 🇪🇸 | **Español (Predeterminado)** | [`res/values/strings.xml`](file:///home/franz/PROYECTOS/KBM-QRSimple/app/src/main/res/values/strings.xml) | ✅ 100% Traducido |
| 🇺🇸 | **Inglés** | [`res/values-en/strings.xml`](file:///home/franz/PROYECTOS/KBM-QRSimple/app/src/main/res/values-en/strings.xml) | ✅ 100% Traducido |
| 🇧🇷 | **Portugués** | [`res/values-pt/strings.xml`](file:///home/franz/PROYECTOS/KBM-QRSimple/app/src/main/res/values-pt/strings.xml) | ✅ 100% Traducido |
| 🇩🇪 | **Alemán** | [`res/values-de/strings.xml`](file:///home/franz/PROYECTOS/KBM-QRSimple/app/src/main/res/values-de/strings.xml) | ✅ 100% Traducido |
| 🇫🇷 | **Francés** | [`res/values-fr/strings.xml`](file:///home/franz/PROYECTOS/KBM-QRSimple/app/src/main/res/values-fr/strings.xml) | ✅ 100% Traducido |

### Módulos Localizados:
- **Navegación:** Pestañas *Escanear*, *Crear QR*, *Historial*.
- **Pantalla de Escáner:** Permisos de cámara, botón de galería, ajustes de app, linterna, zoom.
- **Modal de Resultados:** Detección de tipos (*Sitio Web, Wi-Fi, Contacto, Teléfono, SMS, Email, Ubicación, Texto*), conexión directa, copia de contraseña y favoritos.
- **Generador QR:** Formularios por categoría, campos de entrada, botones de guardado y compartir.
- **Historial:** Búsqueda en tiempo real, chips de categoría, vaciado de historial, edición de títulos y fechas relativas.
- **Monetización ("Invítame un café"):** Títulos, descripciones de propinas y mensajes de agradecimiento.
- **Acerca de:** Diálogo con enlaces directos a la Política de Privacidad y Términos de Servicio.

---

## 🏗️ 3. Arquitectura y Stack Tecnológico

```
qrsimple.franz.com.qrsimple/
├── core/
│   ├── actions/          # QrActionHandler (Abrir URLs, conectar Wi-Fi, añadir contactos, SMS, Maps)
│   ├── billing/          # BillingManager (Google Play Billing 8.0.0, consumo y reintentos)
│   ├── datastore/        # UserPreferencesDataStore (Persistencia reactiva con DataStore)
│   ├── generator/        # QrGenerator (ZXing 800x800 bitmap + MediaStore Scoped Storage)
│   ├── parser/           # QrParser & ParsedQrContent (Regex y decodificación tipada)
│   ├── scanner/          # QrImageAnalyzer (Google ML Kit on-device) & GalleryQrScanner
│   ├── theme/            # Material 3 Dark Palette (Obsidian slate & Emerald Green #10B981)
│   └── util/             # VibrationHelper (Haptic feedback) & DateTimeFormatter
├── data/
│   ├── local/            # Room Database (QrDatabase, QrDao, QrEntity)
│   └── repository/       # QrHistoryRepositoryImpl
├── domain/
│   ├── model/            # QrItem, QrType, ParsedQrContent
│   └── repository/       # QrHistoryRepository, UserPreferencesRepository
└── feature/
    ├── about/            # AboutDialog (Versión, licencias, enlaces legales)
    ├── generator/        # GeneratorScreen, GeneratorViewModel, GeneratorForm, QrPreviewCard
    ├── history/          # HistoryScreen, HistoryViewModel, HistoryItemRow, FilterChips, SearchBar
    ├── result/           # QrResultBottomSheet (Material 3 Smart Bottom Sheet)
    ├── scanner/          # ScannerScreen, ScannerViewModel, CameraPreview, ScannerOverlay
    └── support/          # SupportProjectDialog ("Invítame un café")
```

---

## 🔐 4. Configuración de Firma

Para compilar versiones de producción (`bundleRelease` o `assembleRelease`), se requiere un archivo `keystore.properties` en la raíz del proyecto (excluido en `.gitignore` por seguridad) con el siguiente formato:

```properties
storeFile=qrsimple-upload-key.jks
storePassword=TU_PASSWORD_AQUI
keyAlias=qrsimple
keyPassword=TU_PASSWORD_AQUI
```

---

## ☕ 5. Soporte y Propinas (In-App Purchases)

La aplicación incluye un sistema opcional y voluntario de propinas ("Invítame un café") a través de Google Play Billing:

| ID de Producto | Nombre | Precio sugerido | Tipo |
|---|---|---|---|
| `tip_coffee` | Invítame un café ☕ | $1.49 USD | Consumible |
| `tip_lunch` | Invítame un almuerzo 🍕 | $4.99 USD | Consumible |
| `tip_supporter` | Súper Mecenas 🚀 | $9.99 USD | Consumible |

Las propinas son 100% voluntarias, no bloquean ninguna funcionalidad esencial de la aplicación y son procesadas de extremo a extremo por la pasarela de pagos de Google Play.

---

## 🛡️ 6. Privacidad y Seguridad de Datos

1. **Cámara:**  
   Utilizada exclusivamente en memoria RAM en tiempo real para decodificar códigos QR mediante Google ML Kit on-device. No se capturan fotos, no se graban videos ni se suben fotogramas a la nube.
2. **Almacenamiento Local:**  
   Los datos del historial se almacenan exclusivamente en SQLite/Room (`qr_database.db`) dentro del sandbox privado del dispositivo. El usuario puede borrar todo con el botón "Vaciar historial" o desinstalando la app.
3. **Imágenes en Galería:**  
   Los códigos QR generados se guardan en la carpeta estándar `Pictures/QRSimple` utilizando la API MediaStore de Android (Scoped Storage), sin necesidad de permisos amplios de almacenamiento.
4. **Telemetría y Diagnóstico:**  
   La biblioteca cerrada Google ML Kit recopila de forma automática identificadores de instalación y datos técnicos de rendimiento/errores para monitoreo de la API por parte de Google.
5. **Políticas Legales:**  
   - [Política de Privacidad](https://kabrima.com/qrsimple/privacy) (o ver [`legal/qrsimple/privacy.html`](legal/qrsimple/privacy.html))
   - [Términos y Condiciones](https://kabrima.com/qrsimple/terms) (o ver [`legal/qrsimple/terms.html`](legal/qrsimple/terms.html))

---

## 🛠️ 7. Compilación y Construcción

```bash
# Ejecutar todas las pruebas unitarias
./gradlew test

# Compilar APK de depuración
./gradlew assembleDebug

# Empaquetar y firmar el Android App Bundle de producción para Google Play
./gradlew bundleRelease
```

---

## 📄 8. Licencia

Este proyecto está bajo la Licencia **GNU General Public License v3.0** — consulta el archivo [`LICENSE`](LICENSE) para más detalles.

Copyright (C) 2026 Kabrima / Franz Huayta (<info@kabrima.com>).
