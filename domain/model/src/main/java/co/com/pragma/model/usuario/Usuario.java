package co.com.pragma.model.usuario;
import lombok.Builder;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class Usuario {

    private String nombres;

    private String apellidos;

    private LocalDate fecha_nacimiento;

    private String direccion;

    private String telefono;

    private String correo_electronico;

    private Double salario_base;

    private String documento;
}
