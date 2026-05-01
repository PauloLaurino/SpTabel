package com.seprocom.assinatura.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Min;
import lombok.Data;

@Data
public class CopiarPaginaRequest {
    
    @NotBlank(message = "Caminho do livro é obrigatório")
    private String caminhoLivro;
    
    @Min(value = 1, message = "Página inicial deve ser pelo menos 1")
    private int paginaInicial;
    
    private int paginaFinal;
    
    @NotBlank(message = "Caminho de saída é obrigatório")
    private String saida;
}