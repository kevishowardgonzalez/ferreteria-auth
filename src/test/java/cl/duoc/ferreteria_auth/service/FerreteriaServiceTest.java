package cl.duoc.ferreteria_auth.service;

import cl.duoc.ferreteria_auth.dto.AuthResponse;
import cl.duoc.ferreteria_auth.dto.LoginRequest;
import cl.duoc.ferreteria_auth.dto.RegistroRequest;
import org.springframework.security.authentication.BadCredentialsException;
import cl.duoc.ferreteria_auth.exception.ReglaNegocioException;
import cl.duoc.ferreteria_auth.model.Usuario;
import cl.duoc.ferreteria_auth.repository.UsuarioRepository;
import cl.duoc.ferreteria_auth.security.JwtService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FerreteriaServiceTest {

    @Mock UsuarioRepository repository;
    @Mock PasswordEncoder encoder;
    @Mock JwtService jwtService;
    @InjectMocks FerreteriaService service;

    private Usuario usuario(boolean activo) {
        return new Usuario(1L, "1-9", "Ana", "Pérez", "ana@duoc.cl", "hash", "VENDEDOR", activo);
    }

    private LoginRequest login(String correo, String pass) {
        LoginRequest r = new LoginRequest();
        r.setCorreo(correo);
        r.setPassword(pass);
        return r;
    }

    @Test
    void loginCorrectoDevuelveTokenYRol() {
        when(repository.findByCorreoIgnoreCase("ana@duoc.cl")).thenReturn(Optional.of(usuario(true)));
        when(encoder.matches("1234", "hash")).thenReturn(true);
        when(jwtService.generarToken(any())).thenReturn("token");

        AuthResponse resp = service.login(login("ana@duoc.cl", "1234"));

        assertEquals("token", resp.getToken());
        assertEquals("VENDEDOR", resp.getRol());
    }

    @Test
    void loginConClaveIncorrectaFalla() {
        when(repository.findByCorreoIgnoreCase("ana@duoc.cl")).thenReturn(Optional.of(usuario(true)));
        when(encoder.matches("mala", "hash")).thenReturn(false);
        assertThrows(BadCredentialsException.class, () -> service.login(login("ana@duoc.cl", "mala")));
    }

    @Test
    void loginDeUsuarioDesactivadoFalla() {
        when(repository.findByCorreoIgnoreCase("ana@duoc.cl")).thenReturn(Optional.of(usuario(false)));
        when(encoder.matches("1234", "hash")).thenReturn(true);
        assertThrows(BadCredentialsException.class, () -> service.login(login("ana@duoc.cl", "1234")));
    }

    @Test
    void registroPublicoSiempreCreaCliente() {
        RegistroRequest req = new RegistroRequest();
        req.setRun("2-7");
        req.setNombre("Luis");
        req.setApellido("Soto");
        req.setCorreo("LUIS@gmail.com");
        req.setPassword("1234");
        when(repository.existsByCorreoIgnoreCase("luis@gmail.com")).thenReturn(false);
        when(repository.existsByRunIgnoreCase("2-7")).thenReturn(false);
        when(encoder.encode("1234")).thenReturn("hash");
        when(repository.save(any(Usuario.class))).thenAnswer(i -> i.getArgument(0));
        when(jwtService.generarToken(any())).thenReturn("token");

        AuthResponse resp = service.register(req);

        assertEquals("CLIENTE", resp.getRol());
        assertEquals("luis@gmail.com", resp.getCorreo());
    }

    @Test
    void registroConCorreoDuplicadoFalla() {
        RegistroRequest req = new RegistroRequest();
        req.setRun("2-7");
        req.setNombre("Luis");
        req.setApellido("Soto");
        req.setCorreo("luis@gmail.com");
        req.setPassword("1234");
        when(repository.existsByCorreoIgnoreCase("luis@gmail.com")).thenReturn(true);
        assertThrows(ReglaNegocioException.class, () -> service.register(req));
        verify(repository, never()).save(any());
    }

    @Test
    void refreshEmiteNuevoTokenParaUsuarioActivo() {
        when(repository.findByCorreoIgnoreCase("ana@duoc.cl")).thenReturn(Optional.of(usuario(true)));
        when(jwtService.generarToken(any())).thenReturn("nuevo");
        assertEquals("nuevo", service.refresh("ana@duoc.cl").getToken());
    }
}
