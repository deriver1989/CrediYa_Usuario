package co.com.pragma.r2dbc.adapters;

import co.com.pragma.model.usuario.Usuario;
import co.com.pragma.model.usuario.gateways.UsuarioRepository;
import co.com.pragma.r2dbc.entity.UsuarioEntity;
import co.com.pragma.r2dbc.mensaje.Mensaje;
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
        UsuarioEntity entity = mapToUsuario(usuario);
        return usuarioEntityRepository.save(entity)
                .as(txOperator::transactional)
                .map(saved -> new Usuario(saved.getNombres(),
                        saved.getApellidos(),
                        saved.getFechaNacimiento(),
                        saved.getDireccion(),
                        saved.getTelefono(),
                        saved.getCorreoElectronico(),
                        saved.getSalarioBase(),
                        saved.getDocumento()
                ))
                .doOnError(error -> log.error(Mensaje.ERROR_GUARDAR_USUARIO, error))
                .doOnSuccess(user -> log.info(Mensaje.GUARDAR_USUARIO_OK));
    }

    @Override
    public Mono<Boolean> existeCorreoElectronico(String correo) {
        return usuarioEntityRepository.existsByCorreoElectronico(correo);
    }

    private UsuarioEntity mapToUsuario(Usuario request) {
        return new UsuarioEntity(
                request.getNombres(),
                request.getApellidos(),
                request.getFecha_nacimiento(),
                request.getDireccion(),
                request.getTelefono(),
                request.getCorreo_electronico(),
                request.getSalario_base(),
                request.getDocumento()
        );
    }
}