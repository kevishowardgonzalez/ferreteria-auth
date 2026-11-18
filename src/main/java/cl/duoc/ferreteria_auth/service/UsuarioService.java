package cl.duoc.ferreteria_auth.service;

import cl.duoc.ferreteria_auth.dto.UsuarioRequest;
import cl.duoc.ferreteria_auth.dto.UsuarioResponse;
import cl.duoc.ferreteria_auth.exception.RecursoNoEncontradoException;
import cl.duoc.ferreteria_auth.exception.ReglaNegocioException;
import cl.duoc.ferreteria_auth.model.Rol;
import cl.duoc.ferreteria_auth.model.Usuario;
import cl.duoc.ferreteria_auth.repository.UsuarioRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

/** Lógica de negocio del CRUD de usuarios (solo accesible para ADMIN). */
@Service
public class UsuarioService {

    private final UsuarioRepository repository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioService(UsuarioRepository repository, PasswordEncoder passwordEncoder) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
    }

    public List<UsuarioResponse> listar() {
        return repository.findAll().stream().map(UsuarioResponse::desde).toList();
    }

    public UsuarioResponse obtener(Long id) {
        return UsuarioResponse.desde(buscar(id));
    }

    public UsuarioResponse crear(UsuarioRequest req) {
        if (req.getPassword() == null || req.getPassword().isBlank()) {
            throw new ReglaNegocioException("La contraseña es obligatoria");
        }
        validarUnicos(req, null);

        Usuario u = new Usuario(null, req.getRun().trim(), req.getNombre().trim(), req.getApellido().trim(),
                req.getCorreo().trim().toLowerCase(), passwordEncoder.encode(req.getPassword()),
                req.getRol().name(), valorActivo(req));
        return UsuarioResponse.desde(repository.save(u));
    }

    public UsuarioResponse actualizar(Long id, UsuarioRequest req) {
        Usuario u = buscar(id);
        validarUnicos(req, u);

        boolean dejaDeSerAdminActivo = Rol.ADMIN.name().equals(u.getRol())
                && (req.getRol() != Rol.ADMIN || !valorActivo(req));
        if (dejaDeSerAdminActivo && esUltimoAdminActivo(u)) {
            throw new ReglaNegocioException("No se puede quitar el rol ni desactivar al último administrador");
        }

        u.setRun(req.getRun().trim());
        u.setNombre(req.getNombre().trim());
        u.setApellido(req.getApellido().trim());
        u.setCorreo(req.getCorreo().trim().toLowerCase());
        u.setRol(req.getRol().name());
        u.setActivo(valorActivo(req));
        if (req.getPassword() != null && !req.getPassword().isBlank()) {
            u.setPassword(passwordEncoder.encode(req.getPassword()));
        }
        return UsuarioResponse.desde(repository.save(u));
    }

    public void eliminar(Long id, String correoSolicitante) {
        Usuario u = buscar(id);
        if (u.getCorreo().equalsIgnoreCase(correoSolicitante)) {
            throw new ReglaNegocioException("No puedes eliminar tu propia cuenta");
        }
        if (esUltimoAdminActivo(u)) {
            throw new ReglaNegocioException("No se puede eliminar al último administrador");
        }
        repository.delete(u);
    }

    // ---------- helpers ----------

    private Usuario buscar(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Usuario con id " + id + " no encontrado"));
    }

    private boolean valorActivo(UsuarioRequest req) {
        return req.getActivo() == null || req.getActivo();
    }

    private boolean esUltimoAdminActivo(Usuario u) {
        return Rol.ADMIN.name().equals(u.getRol())
                && Boolean.TRUE.equals(u.getActivo())
                && repository.countByRolAndActivoTrue(Rol.ADMIN.name()) <= 1;
    }

    /** Correo y RUN no pueden repetirse (se ignora el propio usuario al actualizar). */
    private void validarUnicos(UsuarioRequest req, Usuario actual) {
        String correo = req.getCorreo().trim();
        String run = req.getRun().trim();
        boolean correoCambia = actual == null || !actual.getCorreo().equalsIgnoreCase(correo);
        boolean runCambia = actual == null || !actual.getRun().equalsIgnoreCase(run);

        if (correoCambia && repository.existsByCorreoIgnoreCase(correo)) {
            throw new ReglaNegocioException("El correo ya está registrado");
        }
        if (runCambia && repository.existsByRunIgnoreCase(run)) {
            throw new ReglaNegocioException("El RUN ya está registrado");
        }
    }
}
