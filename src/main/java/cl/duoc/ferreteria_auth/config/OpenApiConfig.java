package cl.duoc.ferreteria_auth.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.context.annotation.Configuration;

/** Guía 17, sección 13: define el esquema Bearer para que Swagger muestre el botón Authorize. */
@Configuration
@OpenAPIDefinition(
        info = @Info(title = "Ferretería Los Maestros - Auth API", version = "1.0",
                description = "Autenticación, emisión de JWT y gestión de usuarios por rol"),
        security = @SecurityRequirement(name = "bearerAuth")
)
@SecurityScheme(
        name = "bearerAuth",
        type = SecuritySchemeType.HTTP,
        scheme = "bearer",
        bearerFormat = "JWT"
)
public class OpenApiConfig {}