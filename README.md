# Ferretería Los Maestros — Backend 1 (`ferreteria-auth`)

Microservicio de autenticación, autorización y gestión de usuarios para la plataforma FullStack **Ferretería Los Maestros**, desarrollado con Spring Boot, Spring Security, JWT y MySQL.

---

## 1. Tecnologías utilizadas
- **Java 21** & **Spring Boot**
- **Spring Security** + **JJWT (JSON Web Tokens)**
- **Spring Data JPA** + **MySQL** (`ferreteria_auth`)
- **Springdoc OpenAPI (Swagger UI)**
- **Lombok**

---

## 2. Variables de Entorno y Configuración

Por seguridad, ninguna credencial ni clave secreta se almacena en el código fuente ni en Git. El servicio lee sus parámetros desde variables de entorno (configuradas en `application.properties` para desarrollo y `application-prod.properties` para despliegue):

| Variable de Entorno | Descripción | Ejemplo / Valor Local |
| :--- | :--- | :--- |
| `SERVER_PORT` | Puerto donde levanta el microservicio | `8081` |
| `AUTH_DB_URL` | Cadena de conexión JDBC a MySQL | `jdbc:mysql://localhost:3306/ferreteria_auth?createDatabaseIfNotExist=true&useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true` |
| `AUTH_DB_USER` | Usuario de la base de datos MySQL | `root` |
| `AUTH_DB_PASSWORD` | Contraseña de la base de datos MySQL | *(definida en entorno local o `.env`)* |
| `JWT_SECRET` | Clave secreta Base64 (32 bytes) compartida con Backend 2 para firmar/validar JWT | `<clave-base64-compartida>` |
| `JWT_ISSUER` | Emisor oficial del token (`iss`) | `ferreteria-auth` |
| `APP_CORS_ALLOWED_ORIGIN` | Origen permitido para peticiones desde Frontend | `http://localhost:5173` |
| `AUTH_SEED_PASSWORD` | Contraseña inicial para cuentas de prueba en entorno no productivo (`!prod`) | *(definida vía variable de entorno)* |

---

## 3. URLs del Servicio y Documentación
- **Base URL Local:** `http://localhost:8081`
- **Swagger UI:** `http://localhost:8081/swagger-ui/index.html`
- **OpenAPI JSON:** `http://localhost:8081/v3/api-docs`

### Endpoints Principales (`/api/v1/auth`)
- `POST /api/v1/auth/register` — Registro de nuevos usuarios con validación estricta de rol (`ADMIN`, `VENDEDOR`, `CLIENTE`) y encriptación BCrypt.
- `POST /api/v1/auth/login` — Autenticación de credenciales y emisión de token JWT.

---

## 4. Seguridad y Gestión de Cuentas Iniciales (`DataInitializer`)
- **Retiro de credenciales hardcodeadas:** Se eliminaron las contraseñas de ejemplo del código fuente (`DataInitializer.java`). La clave inicial de prueba se inyecta exclusivamente mediante la variable de entorno `AUTH_SEED_PASSWORD`.
- **Aislamiento por perfil (`@Profile("!prod")`):** El cargador de datos iniciales (`admin@duoc.cl`, `vendedor@duoc.cl`, `carlos@gmail.com`) solo se ejecuta en entornos de desarrollo/evaluación cuando la tabla `usuarios` está vacía, quedando completamente deshabilitado en producción (`prod`).
- **Almacenamiento seguro:** Todas las contraseñas se codifican con `BCryptPasswordEncoder` antes de guardarse en MySQL; nunca se almacenan ni se retornan en texto plano.

---

## 5. Evidencias de Pruebas (Swagger / HTTP Status)
Las capturas de pantalla de las pruebas funcionales y de seguridad se encuentran en `/docs/evidencias/`:
- `200_login_ok.png` — Autenticación exitosa y emisión de JWT.
- `201_register_ok.png` — Creación de usuario y persistencia en MySQL.
- `401_login_invalido.png` — Rechazo por credenciales incorrectas.
- `403_rol_no_autorizado.png` — Bloqueo de escalada de privilegios ante roles no permitidos.