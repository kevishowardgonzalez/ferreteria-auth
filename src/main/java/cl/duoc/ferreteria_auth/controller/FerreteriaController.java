package cl.duoc.ferreteria_auth.controller;

import cl.duoc.ferreteria_auth.dto.AuthResponse;
import cl.duoc.ferreteria_auth.dto.LoginRequest;
import cl.duoc.ferreteria_auth.dto.RegistroRequest;
import cl.duoc.ferreteria_auth.service.FerreteriaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@Tag(name = "Autenticación", description = "Login, registro y renovación de token JWT")
public class FerreteriaController {

    private final FerreteriaService ferreteriaService;

    public FerreteriaController(FerreteriaService ferreteriaService) {
        this.ferreteriaService = ferreteriaService;
    }

    @Operation(summary = "Iniciar sesión", description = "Devuelve un token JWT y los datos del usuario. Público.")
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(ferreteriaService.login(request));
    }

    @Operation(summary = "Registrar cliente", description = "Crea un usuario con rol CLIENTE. Público.")
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegistroRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ferreteriaService.register(request));
    }

    @Operation(summary = "Renovar token", description = "Requiere un token vigente; devuelve uno nuevo. Cualquier rol autenticado.")
    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refresh(Authentication authentication) {
        return ResponseEntity.ok(ferreteriaService.refresh(authentication.getName()));
    }
}
