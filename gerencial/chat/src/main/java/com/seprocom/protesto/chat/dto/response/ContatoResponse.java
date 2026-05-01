package com.seprocom.protesto.chat.dto.response;

import com.seprocom.protesto.chat.entity.Contato;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO de resposta para contato.
 * 
 * @author Seprocom
 * @version 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Response de contato")
public class ContatoResponse {

    @Schema(description = "ID do contato", example = "1")
    private Long id;

    @Schema(description = "Telefone do contato", example = "5562999999999")
    private String telefone;

    @Schema(description = "Nome do contato", example = "João da Silva")
    private String nome;

    @Schema(description = "Email do contato", example = "joao@email.com")
    private String email;

    @Schema(description = "URL da foto do contato")
    private String fotoUrl;

    @Schema(description = "Data de criação", example = "2024-01-15T10:00:00")
    private LocalDateTime dataCriacao;

    /**
     * Converte entidade para DTO
     */
    public static ContatoResponse fromEntity(Contato contato) {
        return ContatoResponse.builder()
                .id(contato.getId())
                .telefone(contato.getTelefone())
                .nome(contato.getNome())
                .email(contato.getEmail())
                .fotoUrl(contato.getFotoUrl())
                .dataCriacao(contato.getDataCriacao())
                .build();
    }
}
