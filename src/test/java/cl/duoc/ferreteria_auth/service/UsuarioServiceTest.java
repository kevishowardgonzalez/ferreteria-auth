package cl.duoc.ferreteria_auth.service;

import cl.duoc.ferreteria_auth.dto.UsuarioRequest;
import cl.duoc.ferreteria_auth.dto.UsuarioResponse;
import cl.duoc.ferreteria_auth.exception.RecursoNoEncontradoException;
import cl.duoc.ferreteria_auth.exception.ReglaNegocioException;
import cl.duoc.ferreteria_auth.model.Rol;
import cl.duoc.ferreteria_auth.model.Usuario;
import cl.duoc.ferreteria_auth.repository.UsuarioRepository;
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
class UsuarioServiceTest {

    @Mock UsuarioRepository repository;
    @Mock PasswordEncoder encoder;
    @InjectMocks UsuarioService service;

    private UsuarioRequest request(Rol rol, String pass) {
        UsuarioRequest r = new UsuarioRequest();
        r.setRun("3-5");
        r.setNombre("Eva");
        r.setApellido("Rojas");
        r.setCorreo("eva@duoc.cl");
        r.setPassword(pass);
        r.setRol(rol);
        r.setActivo(true);
        return r;
    }

    private Usuario admin(long id, String correo) {
        return new Usuario(id, "9-9", "Admin", "Uno", correo, "hash", "ADMIN", true);
    }

    @Test
    void crearUsuarioEncriptaClaveYAsignaRol() {
        when(repository.existsByCorreoIgnoreCase("eva@duoc.cl")).thenReturn(false);
        when(repository.existsByRunIgnoreCase("3-5")).thenReturn(false);
        when(encoder.encode("1234")).thenReturn("hash");
        when(repository.save(any(Usuario.class))).thenAnswer(i -> i.getArgument(0));

        UsuarioResponse resp = service.crear(request(Rol.VENDEDOR, "1234"));

        assertEquals("VENDEDOR", resp.getRol());
        verify(encoder).encode("1234");
    }

    @Test
    void crearSinClaveFalla() {
        assertThrows(ReglaNegocioException.class, () -> service.crear(request(Rol.CLIENTE, " ")));
    }

    @Test
    void crearConCorreoDuplicadoFalla() {
        when(repository.existsByCorreoIgnoreCase("eva@duoc.cl")).thenReturn(true);
        assertThrows(ReglaNegocioException.class, () -> service.crear(request(Rol.CLIENTE, "1234")));
    }

    @Test
    void obtenerInexistenteLanzaNoEncontrado() {
        when(repository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(RecursoNoEncontradoException.class, () -> service.obtener(99L));
    }

    @Test
    void noSePuedeEliminarLaPropiaCuenta() {
        when(repository.findById(1L)).thenReturn(Optional.of(admin(1L, "admin@duoc.cl")));
        assertThrows(ReglaNegocioException.class, () -> service.eliminar(1L, "admin@duoc.cl"));
        verify(repository, never()).delete(any());
    }

    @Test
    void noSePuedeEliminarAlUltimoAdmin() {
        when(repository.findById(2L)).thenReturn(Optional.of(admin(2L, "otro@duoc.cl")));
        when(repository.countByRolAndActivoTrue("ADMIN")).thenReturn(1L);
        assertThrows(ReglaNegocioException.class, () -> service.eliminar(2L, "admin@duoc.cl"));
    }

    @Test
    void sePuedeEliminarUnAdminSiHayOtro() {
        Usuario objetivo = admin(2L, "otro@duoc.cl");
        when(repository.findById(2L)).thenReturn(Optional.of(objetivo));
        when(repository.countByRolAndActivoTrue("ADMIN")).thenReturn(2L);
        service.eliminar(2L, "admin@duoc.cl");
        verify(repository).delete(objetivo);
    }

    @Test
    void noSePuedeDegradarAlUltimoAdmin() {
        when(repository.findById(2L)).thenReturn(Optional.of(admin(2L, "eva@duoc.cl")));
        when(repository.countByRolAndActivoTrue("ADMIN")).thenReturn(1L);
        assertThrows(ReglaNegocioException.class, () -> service.actualizar(2L, request(Rol.CLIENTE, null)));
    }

    @Test
    void actualizarSinClaveMantieneLaActual() {
        Usuario existente = new Usuario(5L, "3-5", "Eva", "Rojas", "eva@duoc.cl", "hashViejo", "CLIENTE", true);
        when(repository.findById(5L)).thenReturn(Optional.of(existente));
        when(repository.save(any(Usuario.class))).thenAnswer(i -> i.getArgument(0));

        service.actualizar(5L, request(Rol.VENDEDOR, null));

        assertEquals("hashViejo", existente.getPassword());
        assertEquals("VENDEDOR", existente.getRol());
        verify(encoder, never()).encode(any());
    }
}
