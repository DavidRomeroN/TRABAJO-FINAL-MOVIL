package org.example.granturismo.dtos;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioLoginRespuestaDTO {
    private Long idUsuario;
    private String email;
    //Se Agrega Role de Yamil
    private String role;
    private String token;
}
