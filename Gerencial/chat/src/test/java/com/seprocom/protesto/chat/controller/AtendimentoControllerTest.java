package com.seprocom.protesto.chat.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.seprocom.protesto.chat.dto.request.MensagemRequest;
import com.seprocom.protesto.chat.dto.response.AtendimentoResponse;
import com.seprocom.protesto.chat.dto.response.MensagemResponse;
import com.seprocom.protesto.chat.entity.Atendimento;
import com.seprocom.protesto.chat.service.AtendimentoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Testes unitários para AtendimentoController.
 * 
 * @author Seprocom
 * @version 1.0.0
 */
@WebMvcTest(AtendimentoController.class)
class AtendimentoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AtendimentoService atendimentoService;

    private AtendimentoResponse atendimentoResponse;
    private MensagemResponse mensagemResponse;

    @BeforeEach
    void setUp() {
        atendimentoResponse = AtendimentoResponse.builder()
                .id(1L)
                .contatoId(1L)
                .contatoNome("João Silva")
                .status("A")
                .dataAbertura(LocalDateTime.now())
                .build();

        mensagemResponse = MensagemResponse.builder()
                .id(1L)
                .mensagem("Olá, como posso ajudar?")
                .tipoMensagem(1)
                .dataHora(LocalDateTime.now())
                .build();
    }

    @Test
    @DisplayName("Deve criar atendimento com sucesso")
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void deveCriarAtendimentoComSucesso() throws Exception {
        // Arrange
        when(atendimentoService.criarAtendimento(eq(1L), eq(1L))).thenReturn(atendimentoResponse);

        // Act & Assert
        mockMvc.perform(post("/api/atendimentos")
                        .with(csrf())
                        .param("contatoId", "1")
                        .param("operadorId", "1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.contatoNome").value("João Silva"))
                .andExpect(jsonPath("$.status").value("A"));
    }

    @Test
    @DisplayName("Deve listar atendimentos por operador")
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void deveListarAtendimentosPorOperador() throws Exception {
        // Arrange
        List<AtendimentoResponse> atendimentos = Arrays.asList(atendimentoResponse);
        Page<AtendimentoResponse> page = new PageImpl<>(atendimentos);
        when(atendimentoService.listarAtendimentosPorOperador(eq(1L), any())).thenReturn(page);

        // Act & Assert
        mockMvc.perform(get("/api/atendimentos/operador/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(1))
                .andExpect(jsonPath("$.content[0].contatoNome").value("João Silva"));
    }

    @Test
    @DisplayName("Deve enviar mensagem com sucesso")
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void deveEnviarMensagemComSucesso() throws Exception {
        // Arrange
        MensagemRequest request = MensagemRequest.builder()
                .atendimentoId(1L)
                .mensagem("Teste de mensagem")
                .tipoMensagem(1)
                .operador(true)
                .build();

        when(atendimentoService.enviarMensagem(eq(1L), any(MensagemRequest.class)))
                .thenReturn(mensagemResponse);

        // Act & Assert
        mockMvc.perform(post("/api/atendimentos/1/mensagens")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.mensagem").value("Olá, como posso ajudar?"));
    }

    @Test
    @DisplayName("Deve buscar mensagens do atendimento")
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void deveBuscarMensagensDoAtendimento() throws Exception {
        // Arrange
        List<MensagemResponse> mensagens = Arrays.asList(mensagemResponse);
        when(atendimentoService.listarMensagens(1L)).thenReturn(mensagens);

        // Act & Assert
        mockMvc.perform(get("/api/atendimentos/1/mensagens")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].mensagem").value("Olá, como posso ajudar?"));
    }

    @Test
    @DisplayName("Deve buscar atendimento por ID")
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void deveBuscarAtendimentoPorId() throws Exception {
        // Arrange
        when(atendimentoService.buscarPorId(1L)).thenReturn(atendimentoResponse);

        // Act & Assert
        mockMvc.perform(get("/api/atendimentos/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.contatoNome").value("João Silva"));
    }

    @Test
    @DisplayName("Deve requerer autenticação para acessar endpoints")
    void deveRequererAutenticacao() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/api/atendimentos/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }
}
