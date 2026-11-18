package cl.duoc.ferreteria_auth.dto;

import cl.duoc.ferreteria_auth.model.Rol;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/** Datos para crear o actualizar un usuario desde el panel de administración. */
@Data
public class UsuarioRequest {
    @NotBlank(message = "El RUN es obligatorio")
    private String run;

    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;

    @NotBlank(message = "El apellido es obligatorio")
    private String apellido;

    @NotBlank(message = "El correo es obligatorio")
    @Email(message = "Correo inválido")
    private String correo;

    /** Obligatoria al crear; en la actualización, si viene vacía se mantiene la actual. */
    private String password;

    @NotNull(message = "El rol es obligatorio")
    private Rol rol;

    private Boolean activo = true;
}
