package com.seprocom.protesto.chat.service;

import com.seprocom.protesto.chat.dto.request.MensagemRequest;
import com.seprocom.protesto.chat.dto.request.VincularIntimacaoRequest;
import com.seprocom.protesto.chat.dto.response.AtendimentoResponse;
import com.seprocom.protesto.chat.dto.response.MensagemResponse;
import com.seprocom.protesto.chat.entity.Atendimento;
import com.seprocom.protesto.chat.entity.AtendimentoIntimacao;
import com.seprocom.protesto.chat.entity.Contato;
import com.seprocom.protesto.chat.entity.Mensagem;
import com.seprocom.protesto.chat.entity.Operador;
import com.seprocom.protesto.chat.repository.AtendimentoIntimacaoRepository;
import com.seprocom.protesto.chat.repository.AtendimentoRepository;
import com.seprocom.protesto.chat.repository.ContatoRepository;
import com.seprocom.protesto.chat.repository.MensagemRepository;
import com.seprocom.protesto.chat.repository.OperadorRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Testes unitários para AtendimentoService.
 * 
 * @author Seprocom
 * @version 1.0.0
 */
@ExtendWith(MockitoExtension.class)
class AtendimentoServiceTest {

    @Mock
    private AtendimentoRepository atendimentoRepository;

    @Mock
    private MensagemRepository mensagemRepository;

    @Mock
    private ContatoRepository contatoRepository;

    @Mock
    private OperadorRepository operadorRepository;

    @Mock
    private AtendimentoIntimacaoRepository atendimentoIntimacaoRepository;

    @InjectMocks
    private AtendimentoService atendimentoService;

    private Contato contato;
    private Atendimento atendimento;
    private Mensagem mensagem;
    private Operador operador;

    @BeforeEach
    void setUp() {
        contato = Contato.builder()
                .id(1L)
                .nome("João Silva")
                .telefone("11999999999")
                .email("joao@email.com")
                .build();

        operador = Operador.builder()
                .id(1L)
                .nome("Atendente")
                .build();

        atendimento = Atendimento.builder()
                .id(1L)
                .contato(contato)
                .operador(operador)
                .status("A") // A = Em Atendimento
                .dataAbertura(LocalDateTime.now())
                .build();

        mensagem = Mensagem.builder()
                .id(1L)
                .atendimento(atendimento)
                .mensagem("Olá, como posso ajudar?")
                .tipoMensagem(1) // Texto
                .entradaSaida("E") // Enviada
                .operadorId(1L)
                .dataHora(LocalDateTime.now())
                .build();
    }

    @Test
    @DisplayName("Deve criar um novo atendimento com sucesso")
    void deveCriarNovoAtendimentoComSucesso() {
        // Arrange
        when(contatoRepository.findById(1L)).thenReturn(Optional.of(contato));
        when(operadorRepository.findById(1L)).thenReturn(Optional.of(operador));
        when(atendimentoRepository.findByContatoIdAndStatusNot(eq(1L), any())).thenReturn(Optional.empty());
        when(atendimentoRepository.save(any(Atendimento.class))).thenReturn(atendimento);
        when(atendimentoIntimacaoRepository.findByAtendimentoId(1L)).thenReturn(Arrays.asList());

        // Act
        AtendimentoResponse response = atendimentoService.criarAtendimento(1L, 1L);

        // Assert
        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("A", response.getStatus());
        verify(atendimentoRepository, times(1)).save(any(Atendimento.class));
    }

    @Test
    @DisplayName("Deve lançar exceção ao criar atendimento com contato inexistente")
    void deveLancarExcecaoAoCriarAtendimentoComContatoInexistente() {
        // Arrange
        when(contatoRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(jakarta.persistence.EntityNotFoundException.class, () -> {
            atendimentoService.criarAtendimento(999L, 1L);
        });
    }

    @Test
    @DisplayName("Deve enviar mensagem com sucesso")
    void deveEnviarMensagemComSucesso() {
        // Arrange
        MensagemRequest request = MensagemRequest.builder()
                .atendimentoId(1L)
                .mensagem("Teste de mensagem")
                .tipoMensagem(1)
                .operador(true)
                .build();

        when(atendimentoRepository.findById(1L)).thenReturn(Optional.of(atendimento));
        when(mensagemRepository.save(any(Mensagem.class))).thenReturn(mensagem);
        when(atendimentoRepository.save(any(Atendimento.class))).thenReturn(atendimento);

        // Act
        MensagemResponse response = atendimentoService.enviarMensagem(1L, request);

        // Assert
        assertNotNull(response);
        verify(mensagemRepository, times(1)).save(any(Mensagem.class));
    }

    @Test
    @DisplayName("Deve lançar exceção ao enviar mensagem para atendimento inexistente")
    void deveLancarExcecaoAoEnviarMensagemParaAtendimentoInexistente() {
        // Arrange
        MensagemRequest request = MensagemRequest.builder()
                .atendimentoId(999L)
                .mensagem("Teste")
                .tipoMensagem(1)
                .operador(true)
                .build();

        when(atendimentoRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(jakarta.persistence.EntityNotFoundException.class, () -> {
            atendimentoService.enviarMensagem(999L, request);
        });
    }

    @Test
    @DisplayName("Deve vincular intimacao ao atendimento com sucesso")
    void deveVincularIntimacaoAoAtendimentoComSucesso() {
        // Arrange
        VincularIntimacaoRequest request = VincularIntimacaoRequest.builder()
                .atendimentoId(1L)
                .numapo1_001("202602")  // AAAAMM
                .numapo2_001("0004567890")  // NNNNNNNNNN
                .controle_001("01")  // NN
                .build();

        AtendimentoIntimacao intimacao = AtendimentoIntimacao.builder()
                .id(1L)
                .atendimento(atendimento)
                .numapo1_001("202602")
                .numapo2_001("0004567890")
                .controle_001("01")
                .build();

        when(atendimentoRepository.findById(1L)).thenReturn(Optional.of(atendimento));
        when(atendimentoIntimacaoRepository.findByNumapo1_001AndNumapo2_001AndControle_001(anyString(), anyString(), anyString()))
                .thenReturn(Optional.empty());
        when(atendimentoIntimacaoRepository.save(any(AtendimentoIntimacao.class))).thenReturn(intimacao);
        when(atendimentoIntimacaoRepository.findByAtendimentoId(1L)).thenReturn(Arrays.asList(intimacao));

        // Act
        AtendimentoResponse response = atendimentoService.vincularIntimacao(1L, request);

        // Assert
        assertNotNull(response);
        verify(atendimentoIntimacaoRepository, times(1)).save(any(AtendimentoIntimacao.class));
    }

    @Test
    @DisplayName("Deve listar atendimentos por operador")
    void deveListarAtendimentosPorOperador() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        List<Atendimento> atendimentos = Arrays.asList(atendimento);
        Page<Atendimento> page = new PageImpl<>(atendimentos);
        
        when(atendimentoRepository.findByOperadorId(eq(1L), any(Pageable.class))).thenReturn(page);
        when(atendimentoIntimacaoRepository.findByAtendimentoId(1L)).thenReturn(Arrays.asList());

        // Act
        Page<AtendimentoResponse> response = atendimentoService.listarAtendimentosPorOperador(1L, pageable);

        // Assert
        assertNotNull(response);
        assertEquals(1, response.getTotalElements());
    }

    @Test
    @DisplayName("Deve buscar atendimento por ID")
    void deveBuscarAtendimentoPorId() {
        // Arrange
        when(atendimentoRepository.findById(1L)).thenReturn(Optional.of(atendimento));
        when(atendimentoIntimacaoRepository.findByAtendimentoId(1L)).thenReturn(Arrays.asList());

        // Act
        AtendimentoResponse response = atendimentoService.buscarPorId(1L);

        // Assert
        assertNotNull(response);
        assertEquals(1L, response.getId());
    }

    @Test
    @DisplayName("Deve listar mensagens por atendimento")
    void deveListarMensagensPorAtendimento() {
        // Arrange
        List<Mensagem> mensagens = Arrays.asList(mensagem);
        when(mensagemRepository.findByAtendimentoIdOrderByDataHoraAsc(1L)).thenReturn(mensagens);

        // Act
        List<MensagemResponse> response = atendimentoService.listarMensagens(1L);

        // Assert
        assertNotNull(response);
        assertEquals(1, response.size());
    }

    @Test
    @DisplayName("Deve atualizar status do atendimento")
    void deveAtualizarStatusAtendimento() {
        // Arrange
        when(atendimentoRepository.findById(1L)).thenReturn(Optional.of(atendimento));
        when(atendimentoRepository.save(any(Atendimento.class))).thenReturn(atendimento);
        when(atendimentoIntimacaoRepository.findByAtendimentoId(1L)).thenReturn(Arrays.asList());

        // Act
        AtendimentoResponse response = atendimentoService.atualizarStatus(1L, Atendimento.StatusAtendimento.ENCERRADO);

        // Assert
        assertNotNull(response);
        verify(atendimentoRepository, times(1)).save(any(Atendimento.class));
    }
}
