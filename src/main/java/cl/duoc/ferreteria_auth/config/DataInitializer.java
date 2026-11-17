package cl.duoc.ferreteria_auth.config;

import cl.duoc.ferreteria_auth.model.Usuario;
import cl.duoc.ferreteria_auth.repository.UsuarioRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        if (usuarioRepository.count() == 0) {
            usuarioRepository.save(new Usuario(null, "12345678-5", "Kevis", "Admin", "admin@duoc.cl", passwordEncoder.encode("1234"), "ADMIN", true));
            usuarioRepository.save(new Usuario(null, "15987456-3", "Javier", "Cruz", "vendedor@duoc.cl", passwordEncoder.encode("1234"), "VENDEDOR", true));
            usuarioRepository.save(new Usuario(null, "19876543-2", "Carlos", "Maestro", "carlos@gmail.com", passwordEncoder.encode("1234"), "CLIENTE", true));
        }
    }
}