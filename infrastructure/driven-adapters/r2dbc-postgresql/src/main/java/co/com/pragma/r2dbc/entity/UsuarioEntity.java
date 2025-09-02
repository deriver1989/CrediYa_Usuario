package co.com.pragma.r2dbc.entity;

import jakarta.persistence.Column;
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

    @Column(name = "fecha_nacimiento")
    private LocalDate fechaNacimiento;

    private String direccion;

    private String telefono;

    @Column(name = "correo_electronico")
    private String correoElectronico;

    @Column(name = "salario_base")
    private Double salarioBase;

    private String documento;

    public UsuarioEntity(String nombres, String apellidos, LocalDate fechaNacimiento, String direccion, String telefono, String correoElectronico, Double salarioBase, String documento) {
        this.nombres = nombres;
        this.apellidos = apellidos;
        this.fechaNacimiento = fechaNacimiento;
        this.direccion = direccion;
        this.telefono = telefono;
        this.correoElectronico = correoElectronico;
        this.salarioBase = salarioBase;
        this.documento = documento;
    }
}