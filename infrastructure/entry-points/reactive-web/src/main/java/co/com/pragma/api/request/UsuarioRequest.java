package co.com.pragma.api.request;

import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class UsuarioRequest {

    private String nombres;
    private String apellidos;
    private LocalDate fecha_nacimiento;
    private String direccion;
    private String telefono;
    private String correo_electronico;
    private Double salario_base;
}
