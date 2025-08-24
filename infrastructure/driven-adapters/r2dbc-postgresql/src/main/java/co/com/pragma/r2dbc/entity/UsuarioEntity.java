package co.com.pragma.r2dbc.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDate;


@Table("usuarios")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioEntity {
    @Id
    private Long id;

    private String nombres;

    private String apellidos;

    private LocalDate fecha_nacimiento;

    private String direccion;

    private String telefono;

    private String correo_electronico;

    private Double salario_base;

    public UsuarioEntity(String nombres, String apellidos, LocalDate fecha_nacimiento, String direccion, String telefono, String correo_electronico, Double salario_base) {
        this.nombres = nombres;
        this.apellidos = apellidos;
        this.fecha_nacimiento = fecha_nacimiento;
        this.direccion = direccion;
        this.telefono = telefono;
        this.correo_electronico = correo_electronico;
        this.salario_base = salario_base;
    }
}