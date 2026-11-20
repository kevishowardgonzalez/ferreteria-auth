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

El servicio utiliza variables de entorno con valores por defecto configurados en `src/main/resources/application.properties` para facilitar su ejecución en entornos locales y de despliegue:

| Variable de Entorno | Descripción | Valor por Defecto (Desarrollo) |
| :--- | :--- | :--- |
| `SERVER_PORT` | Puerto donde levanta el microservicio | `8081` |
| `AUTH_DB_URL` | Cadena de conexión JDBC a MySQL | `jdbc:mysql://localhost:3306/ferreteria_auth?createDatabaseIfNotExist=true&useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true` |
| `AUTH_DB_USER` | Usuario de la base de datos MySQL | `root` |
| `AUTH_DB_PASSWORD` | Contraseña de la base de datos MySQL | *(vacío en XAMPP local)* |
| `JWT_SECRET` | Clave secreta Base64 compartida con Backend 2 para firmar/validar JWT | `ZmVycmV0ZXJpYUxvc01hZXN0cm9zMjAyNkR1b2NTZWNyZXRLZXk=` |
| `JWT_ISSUER` | Emisor oficial del token (`iss`) | `ferreteria-auth` |

---

## 3. URLs del Servicio y Documentación
- **Base URL Local:** `http://localhost:8081`
- **Swagger UI:** `http://localhost:8081/swagger-ui/index.html`
- **OpenAPI JSON:** `http://localhost:8081/v3/api-docs`

### Endpoints Principales (`/api/v1/auth`)
- `POST /api/v1/auth/register` — Registro de nuevos clientes (`CLIENTE`) con encriptación BCrypt.
- `POST /api/v1/auth/login` — Autenticación de credenciales y emisión de token JWT.

---

## 4. Nota de Seguridad sobre Credenciales de Prueba (`DataInitializer`)
Para facilitar la evaluación académica, las pruebas de integración entre microservicios (`Backend 1` $\leftrightarrow$ `Backend 2`) y la demostración de roles en Swagger, el componente `DataInitializer.java` precarga tres cuentas iniciales al iniciar el servidor si la tabla `usuarios` está vacía:
- **Administrador:** `admin@duoc.cl` / `1234` (Rol: `ADMIN`)
- **Vendedor:** `vendedor@duoc.cl` / `1234` (Rol: `VENDEDOR`)
- **Cliente:** `cliente@duoc.cl` / `1234` (Rol: `CLIENTE`)

**Justificación técnica:** Las contraseñas de prueba (`1234`) nunca se almacenan en texto plano; son procesadas mediante `BCryptPasswordEncoder` antes de persistirse en MySQL. En un entorno productivo real, `DataInitializer` debe deshabilitarse mediante perfiles de Spring (`@Profile("dev")`) y las claves deben inyectarse exclusivamente vía variables de entorno.

---

## 5. Evidencias de Pruebas (Swagger / HTTP Status)
Las capturas de pantalla de las pruebas funcionales y de seguridad se encuentran en la carpeta `/docs/evidencias/`:
- `200_login_ok.png` — Autenticación exitosa y emisión de JWT.
- `201_register_ok.png` — Creación de usuario y persistencia en MySQL.
- `401_login_invalido.png` — Rechazo por credenciales incorrectas.
- `403_rol_no_autorizado.png` — Bloqueo de escalada de privilegios ante roles no permitidos.