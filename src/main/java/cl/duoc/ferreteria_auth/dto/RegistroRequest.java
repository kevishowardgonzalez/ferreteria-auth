package cl.duoc.ferreteria_auth.dto;

import cl.duoc.ferreteria_auth.model.Rol;
import lombok.Data;

@Data
public class RegistroRequest {
    private String run;
    private String nombre;
    private String apellido;
    private String correo;
    private String password;
    private Rol rol; // Solo ADMIN, VENDEDOR o CLIENTE
}