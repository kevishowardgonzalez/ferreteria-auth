package cl.duoc.ferreteria_auth.dto;

import lombok.Data;

@Data
public class LoginRequest {
    private String correo;
    private String password;
}