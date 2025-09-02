package co.com.pragma.api.request;

import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class UsuarioRequest {

    @NotBlank(message = "El nombre no puede estar vacío")
    @NotNull(message = "El nombre es obligatorio")
    private String nombres;

    @NotBlank(message = "El apellido no puede estar vacío")
    @NotNull(message = "El apellido es obligatorio")
    private String apellidos;

    private LocalDate fecha_nacimiento;

    private String direccion;

    private String telefono;

    @NotBlank(message = "El correo electrónico no puede estar vacío")
    @NotNull(message = "El correo electrónico es obligatorio")
    @Email(message = "El correo electrónico no es válido")
    private String correo_electronico;

    @NotNull(message = "El salario es obligatorio")
    @DecimalMin(value = "0.0", inclusive = true, message = "El salario debe ser mayor o igual a 0")
    @DecimalMax(value = "15000000.0", inclusive = true, message = "El salario no puede superar 15,000,000")
    private Double salario_base;

    @NotBlank(message = "El documento no puede estar vacío")
    @NotNull(message = "El documento es obligatorio")
    private String documento;

}
