package com.seprocom.protesto.chat.controller;

import com.seprocom.protesto.chat.entity.Contato;
import com.seprocom.protesto.chat.repository.ContatoRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

/**
 * Controller para gerenciamento de contatos.
 * Permite CRUD completo e busca de contatos.
 */
@RestController
@RequestMapping("/api/contatos")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Contatos", description = "Endpoints para gerenciamento de contatos")
@SecurityRequirement(name = "bearerAuth")
public class ContatoController {

    private final ContatoRepository contatoRepository;

    /**
     * Lista todos os contatos com paginação
     */
    @GetMapping
    @Operation(summary = "Listar contatos", description = "Lista todos os contatos com paginação")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista de contatos retornada com sucesso")
    })
    public ResponseEntity<Page<Contato>> listar(
            @Parameter(description = "Número da página (0-indexed)") 
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Tamanho da página") 
            @RequestParam(defaultValue = "20") int size) {
        
        log.info("Listando contatos - página: {}, tamanho: {}", page, size);
        
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by("nome").ascending());
        Page<Contato> contatos = contatoRepository.findAll(pageRequest);
        
        return ResponseEntity.ok(contatos);
    }

    /**
     * Busca um contato por ID
     */
    @GetMapping("/{id}")
    @Operation(summary = "Buscar contato por ID", description = "Retorna um contato pelo seu ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Contato encontrado"),
        @ApiResponse(responseCode = "404", description = "Contato não encontrado")
    })
    public ResponseEntity<Contato> buscarPorId(
            @Parameter(description = "ID do contato") 
            @PathVariable Long id) {
        
        log.info("Buscando contato por ID: {}", id);
        
        return contatoRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Busca um contato por telefone
     */
    @GetMapping("/telefone/{telefone}")
    @Operation(summary = "Buscar contato por telefone", description = "Retorna um contato pelo telefone")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Contato encontrado"),
        @ApiResponse(responseCode = "404", description = "Contato não encontrado")
    })
    public ResponseEntity<Contato> buscarPorTelefone(
            @Parameter(description = "Telefone do contato") 
            @PathVariable String telefone) {
        
        log.info("Buscando contato por telefone: {}", telefone);
        
        return contatoRepository.findByTelefone(telefone)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Busca contatos por nome
     */
    @GetMapping("/nome/{nome}")
    @Operation(summary = "Buscar contatos por nome", description = "Retorna contatos que contêm o nome informado")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista de contatos encontrados")
    })
    public ResponseEntity<List<Contato>> buscarPorNome(
            @Parameter(description = "Nome ou parte do nome") 
            @PathVariable String nome) {
        
        log.info("Buscando contatos por nome: {}", nome);
        
        List<Contato> contatos = contatoRepository.findByNomeContainingIgnoreCase(nome);
        return ResponseEntity.ok(contatos);
    }

    /**
     * Cria um novo contato
     */
    @PostMapping
    @Operation(summary = "Criar contato", description = "Cria um novo contato")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Contato criado com sucesso"),
        @ApiResponse(responseCode = "400", description = "Dados inválidos ou telefone já cadastrado")
    })
    public ResponseEntity<?> criar(
            @Valid @RequestBody ContatoRequest request) {
        
        log.info("Criando contato: {}", request.getNome());
        
        // Verificar se telefone já existe
        if (contatoRepository.existsByTelefone(request.getTelefone())) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("Telefone já cadastrado");
        }
        
        Contato contato = Contato.builder()
                .nome(request.getNome())
                .telefone(request.getTelefone())
                .email(request.getEmail())
                .fotoUrl(request.getFotoUrl())
                .build();
        
        Contato salvo = contatoRepository.save(contato);
        return ResponseEntity.status(HttpStatus.CREATED).body(salvo);
    }

    /**
     * Atualiza um contato existente
     */
    @PutMapping("/{id}")
    @Operation(summary = "Atualizar contato", description = "Atualiza um contato existente")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Contato atualizado com sucesso"),
        @ApiResponse(responseCode = "404", description = "Contato não encontrado"),
        @ApiResponse(responseCode = "400", description = "Telefone já cadastrado para outro contato")
    })
    public ResponseEntity<?> atualizar(
            @Parameter(description = "ID do contato") 
            @PathVariable Long id,
            @Valid @RequestBody ContatoRequest request) {
        
        log.info("Atualizando contato: {}", id);
        
        Optional<Contato> contatoOpt = contatoRepository.findById(id);
        if (contatoOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        
        Contato contato = contatoOpt.get();
        
        // Verificar se telefone já existe para outro contato
        Optional<Contato> contatoComTelefone = contatoRepository.findByTelefone(request.getTelefone());
        if (contatoComTelefone.isPresent() && !contatoComTelefone.get().getId().equals(id)) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("Telefone já cadastrado para outro contato");
        }
        
        contato.setNome(request.getNome());
        contato.setTelefone(request.getTelefone());
        contato.setEmail(request.getEmail());
        contato.setFotoUrl(request.getFotoUrl());
        
        Contato salvo = contatoRepository.save(contato);
        return ResponseEntity.ok(salvo);
    }

    /**
     * Remove um contato
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "Remover contato", description = "Remove um contato pelo ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Contato removido com sucesso"),
        @ApiResponse(responseCode = "404", description = "Contato não encontrado")
    })
    public ResponseEntity<Void> remover(
            @Parameter(description = "ID do contato") 
            @PathVariable Long id) {
        
        log.info("Removendo contato: {}", id);
        
        if (!contatoRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        
        contatoRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Busca contatos com filtro
     */
    @GetMapping("/filtro")
    @Operation(summary = "Buscar contatos com filtro", description = "Busca contatos com filtros opcionais")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista de contatos filtrados")
    })
    public ResponseEntity<Page<Contato>> buscarComFiltro(
            @Parameter(description = "Nome para filtro") 
            @RequestParam(required = false) String nome,
            @Parameter(description = "Telefone para filtro") 
            @RequestParam(required = false) String telefone,
            @Parameter(description = "Número da página") 
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Tamanho da página") 
            @RequestParam(defaultValue = "20") int size) {
        
        log.info("Buscando contatos com filtro - nome: {}, telefone: {}", nome, telefone);
        
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by("nome").ascending());
        Page<Contato> contatos = contatoRepository.findByFiltro(nome, telefone, pageRequest);
        
        return ResponseEntity.ok(contatos);
    }

    /**
     * Request DTO para criação/atualização de contato
     */
    @lombok.Data
    public static class ContatoRequest {
        @jakarta.validation.constraints.NotBlank(message = "Nome é obrigatório")
        @jakarta.validation.constraints.Size(max = 80, message = "Nome deve ter no máximo 80 caracteres")
        private String nome;

        @jakarta.validation.constraints.NotBlank(message = "Telefone é obrigatório")
        @jakarta.validation.constraints.Size(max = 20, message = "Telefone deve ter no máximo 20 caracteres")
        private String telefone;

        @jakarta.validation.constraints.Email(message = "Email inválido")
        @jakarta.validation.constraints.Size(max = 100, message = "Email deve ter no máximo 100 caracteres")
        private String email;

        @jakarta.validation.constraints.Size(max = 255, message = "URL da foto deve ter no máximo 255 caracteres")
        private String fotoUrl;
    }
}
