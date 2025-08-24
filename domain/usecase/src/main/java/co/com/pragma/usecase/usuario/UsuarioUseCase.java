package co.com.pragma.usecase.usuario;

import co.com.pragma.model.usuario.Usuario;
import co.com.pragma.model.usuario.gateways.UsuarioRepository;
import reactor.core.publisher.Mono;
import java.time.LocalDate;

//@RequiredArgsConstructor
public class UsuarioUseCase {

    private final UsuarioRepository usuarioRepository;

    public UsuarioUseCase(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    public Mono<Usuario> guardarUsuario(String nombres,
                                        String apellidos,
                                        LocalDate fecha_nacimiento,
                                        String direccion,
                                        String telefono,
                                        String correo_electronico,
                                        Double salario_base
    ) {
        Usuario usuario = new Usuario(nombres,
                apellidos,
                fecha_nacimiento,
                direccion,
                telefono,
                correo_electronico,
                salario_base);
        return usuarioRepository.saveUsuario(usuario);
    }
}
