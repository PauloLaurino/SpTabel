package com.seprocom.protesto.chat.dto.response;

import com.seprocom.protesto.chat.entity.Departamento;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO de resposta para departamento.
 * 
 * @author Seprocom
 * @version 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Response de departamento")
public class DepartamentoResponse {

    @Schema(description = "ID do departamento", example = "1")
    private Long id;

    @Schema(description = "Descrição do departamento", example = "Financeiro")
    private String descricao;

    @Schema(description = "Data de criação", example = "2024-01-15T10:00:00")
    private LocalDateTime dataCriacao;

    /**
     * Converte entidade para DTO
     */
    public static DepartamentoResponse fromEntity(Departamento departamento) {
        return DepartamentoResponse.builder()
                .id(departamento.getId())
                .descricao(departamento.getDescricao())
                .dataCriacao(departamento.getDataCriacao())
                .build();
    }
}
