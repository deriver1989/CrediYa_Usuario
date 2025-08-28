package co.com.pragma.api.config;

import org.junit.jupiter.api.Test;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = SwaggerConfig.class)
class SwaggerConfigTest {

    @Autowired
    private GroupedOpenApi usuarioApi;

    @Test
    void deberiaCargarElBeanUsuarioApi() {
        assertThat(usuarioApi).isNotNull();
        assertThat(usuarioApi.getGroup()).isEqualTo("usuarios-api");
        assertThat(usuarioApi.getPathsToMatch()).containsExactly("/api/v1/usuarios/**");
    }
}
