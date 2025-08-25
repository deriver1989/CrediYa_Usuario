package co.com.pragma.r2dbc.adapters;

import co.com.pragma.model.usuario.Usuario;
import co.com.pragma.model.usuario.gateways.UsuarioRepository;
import co.com.pragma.r2dbc.entity.UsuarioEntity;
import co.com.pragma.r2dbc.repository.UsuarioEntityRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.reactive.TransactionalOperator;
import reactor.core.publisher.Mono;

@Component
@Slf4j
public class UsuarioRepositoryAdapter implements UsuarioRepository {

    private final UsuarioEntityRepository usuarioEntityRepository;
    private final TransactionalOperator txOperator;

    public UsuarioRepositoryAdapter(UsuarioEntityRepository usuarioEntityRepository, TransactionalOperator txOperator) {
        this.usuarioEntityRepository = usuarioEntityRepository;
        this.txOperator = txOperator;
    }

    @Override
    public Mono<Usuario> saveUsuario(Usuario usuario) {
        UsuarioEntity entity = new UsuarioEntity(usuario.getNombres(),
                usuario.getApellidos(),
                usuario.getFecha_nacimiento(),
                usuario.getDireccion(),
                usuario.getTelefono(),
                usuario.getCorreo_electronico(),
                usuario.getSalario_base()
        );
        return usuarioEntityRepository.existsByCorreoElectronico(entity.getCorreoElectronico())
                .flatMap(exists -> {
                    if (exists) {
                        return Mono.error(new RuntimeException("El usuario ya está registrado con este correo"));
                    }
                    return usuarioEntityRepository.save(entity)
                            .as(txOperator::transactional)
                            .map(saved -> new Usuario(saved.getNombres(),
                                    saved.getApellidos(),
                                    saved.getFechaNacimiento(),
                                    saved.getDireccion(),
                                    saved.getTelefono(),
                                    saved.getCorreoElectronico(),
                                    saved.getSalarioBase()));
                })
                .doOnError(error -> log.error("Error al guardar el usuario", error))
                .doOnSuccess(user -> log.info("Proceso finalizado con éxito, el usuario ha sido guardado."))
                ;


    }
}