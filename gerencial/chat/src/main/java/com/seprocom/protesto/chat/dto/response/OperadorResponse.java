package com.seprocom.protesto.chat.dto.response;

import com.seprocom.protesto.chat.entity.Operador;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO de resposta para operador.
 * 
 * @author Seprocom
 * @version 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Response de operador")
public class OperadorResponse {

    @Schema(description = "ID do operador", example = "1")
    private Long id;

    @Schema(description = "Nome do operador", example = "Maria Santos")
    private String nome;

    @Schema(description = "Telefone do operador", example = "5562998888888")
    private String telefone;

    @Schema(description = "Email do operador", example = "maria@seprocom.com.br")
    private String email;

    @Schema(description = "ID do usuário no sistema principal", example = "1")
    private Long usuarioId;

    @Schema(description = "Status: online/offline", example = "online")
    private String status;

    @Schema(description = "Data de criação", example = "2024-01-15T10:00:00")
    private LocalDateTime dataCriacao;

    /**
     * Converte entidade para DTO
     */
    public static OperadorResponse fromEntity(Operador operador) {
        return OperadorResponse.builder()
                .id(operador.getId())
                .nome(operador.getNome())
                .telefone(operador.getTelefone())
                .email(operador.getEmail())
                .usuarioId(operador.getUsuarioId())
                .status(operador.getStatus())
                .dataCriacao(operador.getDataCriacao())
                .build();
    }
}
