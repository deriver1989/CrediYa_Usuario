package co.com.pragma.usecase.usuario;

import co.com.pragma.model.usuario.Usuario;
import co.com.pragma.model.usuario.gateways.UsuarioRepository;
import co.com.pragma.usecase.mensaje.Mensaje;
import reactor.core.publisher.Mono;

//@RequiredArgsConstructor
public class UsuarioUseCase {

    private final UsuarioRepository usuarioRepository;

    public UsuarioUseCase(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    public Mono<Usuario> guardarUsuario(Usuario usuario) {

        return usuarioRepository.existeCorreoElectronico(usuario.getCorreo_electronico())
                .flatMap(exists -> {
                    if (exists) {
                        return Mono.error(new RuntimeException(Mensaje.CORREO_ELECTRONICO_REGISTRADO));
                    }
                    return usuarioRepository.saveUsuario(usuario);
                });
    }



}
