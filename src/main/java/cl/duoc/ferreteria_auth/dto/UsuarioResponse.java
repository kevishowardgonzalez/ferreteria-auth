package cl.duoc.ferreteria_auth.dto;

import cl.duoc.ferreteria_auth.model.Usuario;
import lombok.AllArgsConstructor;
import lombok.Data;

/** Representación pública de un usuario (nunca incluye la contraseña). */
@Data
@AllArgsConstructor
public class UsuarioResponse {
    private Long id;
    private String run;
    private String nombre;
    private String apellido;
    private String correo;
    private String rol;
    private Boolean activo;

    public static UsuarioResponse desde(Usuario u) {
        return new UsuarioResponse(u.getId(), u.getRun(), u.getNombre(), u.getApellido(),
                u.getCorreo(), u.getRol(), u.getActivo());
    }
}
