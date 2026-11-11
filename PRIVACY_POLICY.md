# Política de Privacidad / Privacy Policy — QR Simple

**Última actualización / Last updated:** 10 de noviembre de 2026 / November 10, 2026  
**Aplicación / Application:** QR Simple (Versión 2.0.0, `versionCode = 1`)  
**Paquete / Package ID:** `com.kabrima.qrsimple`  
**Responsable / Developer:** Kabrima (La Paz, Bolivia)  
**Contacto / Contact:** info@kabrima.com  
**Versión Web Oficial:** [https://kabrima.com/qrsimple/privacy](https://kabrima.com/qrsimple/privacy)

---

## 🇪🇸 Versión en Español

### 1. Principios de Privacidad
En **QR Simple**, respetamos profundamente tu privacidad:
- **Sin publicidad:** La aplicación no incluye redes publicitarias, anuncios ni banners.
- **Sin cuentas:** No requieres registro, correo ni contraseñas.
- **Escaneo en el dispositivo:** La decodificación de códigos ocurre directamente en tu dispositivo.
- **Historial en tu teléfono:** La base de datos de historial reside exclusivamente en tu dispositivo (Room/SQLite).
- **Conexiones de red:** La única conexión a internet es para las propinas opcionales procesadas por Google Play y para los diagnósticos automáticos del motor Google ML Kit.

---

### 2. Permisos del Dispositivo y Uso

#### Cámara (`android.permission.CAMERA`)
- **Finalidad:** Acceder a la cámara en tiempo real exclusivamente para escanear códigos QR y códigos de barras.
- **Procesamiento:** Los fotogramas se procesan en la memoria volátil (RAM) con **Google ML Kit Barcode Scanning**.
- **Garantía:** No se guardan fotos, no se graban videos ni se transmiten fotogramas de la cámara a internet.

#### Datos de Diagnóstico de Google ML Kit
- La biblioteca oficial Google ML Kit transmite a Google información técnica de diagnósticos (hardware, OS, métricas de rendimiento, códigos de error y un identificador por instalación no personal).
- Todas las transmisiones están cifradas en tránsito (HTTPS) y no se comparten con terceros con fines publicitarios.
- Divulgación técnica oficial: [https://developers.google.com/ml-kit/android-data-disclosure](https://developers.google.com/ml-kit/android-data-disclosure)

#### Vibración (`android.permission.VIBRATE`)
- **Finalidad:** Respuesta háptica sutil al detectar un código QR con éxito.

#### Guardado en Galería (`MediaStore`)
- **Finalidad:** Al pulsar "Guardar en Galería", la imagen PNG del QR generado se almacena en `Pictures/QRSimple` mediante la API estándar de MediaStore sin requerir permisos de almacenamiento amplios.

#### Conectividad y Pagos (`INTERNET`, `ACCESS_NETWORK_STATE`, `com.android.vending.BILLING`)
- **Finalidad:** Procesar compras de propinas opcionales ("Invítame un café") mediante **Google Play Billing**. El desarrollador no recibe datos bancarios ni de tarjetas de crédito.
- Aviso de Privacidad de Google Payments: [https://payments.google.com/payments/apis-secure/u/0/get_legal_document?ldo=0&ldt=privacynotice](https://payments.google.com/payments/apis-secure/u/0/get_legal_document?ldo=0&ldt=privacynotice)

---

### 3. Historial Local y Control del Usuario
- El historial se almacena únicamente en SQLite/Room en tu teléfono.
- Tienes control total: puedes editar, filtrar, eliminar registros o usar "Vaciar historial" para borrar la base de datos de inmediato. Al desinstalar la app, todos los datos se eliminan automáticamente.

---

### 4. Menores de Edad
La aplicación no está dirigida a niños menores de 13 años.

---

### 5. Contacto
- **Responsable:** Kabrima, La Paz, Bolivia
- **Email:** info@kabrima.com

---

## 🇺🇸 English Version

### 1. Privacy Principles
At **QR Simple**, your privacy is our top priority:
- **Zero Ads:** No ad banners, interstitials, or tracking ad SDKs.
- **No Accounts:** No registration, username, or login required.
- **On-Device Scanning:** Barcode decoding is powered locally on your device.
- **History stays on your phone:** Scanned records are saved strictly in your phone's local Room database.
- **Internet Usage:** Network access is used solely for optional Google Play tips and Google ML Kit automated diagnostics.

### 2. Device Permissions & Services
- **Camera:** Real-time scanning only. No photos or videos are stored or uploaded.
- **Google ML Kit Diagnostics:** Transmits device performance metrics and per-install technical IDs over HTTPS to Google. ([Google ML Kit Data Disclosure](https://developers.google.com/ml-kit/android-data-disclosure)).
- **Vibration:** Haptic feedback upon successful scan.
- **Gallery Export:** Uses Android MediaStore to save generated QR images to `Pictures/QRSimple` without broad storage permissions.
- **Google Play Billing:** Handles optional in-app tips securely. The developer never accesses financial details.

### 3. Contact
- **Entity:** Kabrima, La Paz, Bolivia
- **Email:** info@kabrima.com
