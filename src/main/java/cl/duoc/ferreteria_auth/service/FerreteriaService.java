package cl.duoc.ferreteria_auth.service;

import cl.duoc.ferreteria_auth.dto.AuthResponse;
import cl.duoc.ferreteria_auth.dto.LoginRequest;
import cl.duoc.ferreteria_auth.dto.RegistroRequest;
import cl.duoc.ferreteria_auth.model.Usuario;
import cl.duoc.ferreteria_auth.repository.UsuarioRepository;
import cl.duoc.ferreteria_auth.security.JwtService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class FerreteriaService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public FerreteriaService(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    public AuthResponse login(LoginRequest request) {
        Usuario usuario = usuarioRepository.findByCorreoIgnoreCase(request.getCorreo())
                .orElseThrow(() -> new RuntimeException("Credenciales inválidas"));

        if (!passwordEncoder.matches(request.getPassword(), usuario.getPassword())) {
            throw new RuntimeException("Credenciales inválidas");
        }

        String token = jwtService.generarToken(usuario);
        return new AuthResponse(
                token,
                usuario.getId(),
                usuario.getNombre(),
                usuario.getApellido(),
                usuario.getCorreo(),
                usuario.getRol()
        );
    }

    public AuthResponse register(RegistroRequest request) {
        if (usuarioRepository.existsByCorreoIgnoreCase(request.getCorreo())) {
            throw new RuntimeException("El correo ya está registrado");
        }

        String rolAsignado = (request.getRol() == null)
                ? "CLIENTE"
                : request.getRol().name();

        Usuario nuevo = new Usuario(
                null,
                request.getRun(),
                request.getNombre(),
                request.getApellido(),
                request.getCorreo().trim().toLowerCase(),
                passwordEncoder.encode(request.getPassword()),
                rolAsignado,
                true
        );

        Usuario guardado = usuarioRepository.save(nuevo);
        String token = jwtService.generarToken(guardado);

        return new AuthResponse(
                token,
                guardado.getId(),
                guardado.getNombre(),
                guardado.getApellido(),
                guardado.getCorreo(),
                guardado.getRol()
        );
    }
}