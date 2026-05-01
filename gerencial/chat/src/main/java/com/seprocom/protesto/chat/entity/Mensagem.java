package com.seprocom.protesto.chat.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Entidade que representa uma mensagem no sistema de chat.
 * 
 * As mensagens podem ser de diferentes tipos: texto, áudio, vídeo,
 * imagem ou anexo (PDF de intimação, comprovante de pagamento).
 * 
 * @author Seprocom
 * @version 1.0.0
 */
@Entity
@Table(name = "cad_atendimentos_msgs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = {"atendimento"})
@EqualsAndHashCode(of = "id")
public class Mensagem implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_mensagem")
    private Long id;

    /**
     * Atendimento ao qual a mensagem pertence
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_atendimento")
    private Atendimento atendimento;

    /**
     * Data e hora da mensagem
     */
    @NotNull(message = "Data/hora é obrigatória")
    @Column(name = "data_hora")
    private LocalDateTime dataHora;

    /**
     * Tipo da mensagem:
     * 1 = Texto
     * 2 = Áudio
     * 3 = Vídeo
     * 4 = Imagem
     * 5 = Anexo (PDF, comprovante)
     */
    @NotNull(message = "Tipo de mensagem é obrigatório")
    @Column(name = "tipo_mensagem")
    private Integer tipoMensagem;

    /**
     * Direção da mensagem:
     * R = Recebida (do contato)
     * E = Enviada (do operador)
     */
    @Size(max = 20, message = "Entrada/Saída deve ter no máximo 20 caracteres")
    @Column(name = "entrada_saida", length = 20)
    private String entradaSaida;

    /**
     * Código identificador da mensagem (Message ID do WhatsApp)
     */
    @Size(max = 100, message = "Código deve ter no máximo 100 caracteres")
    @Column(name = "codigo", length = 100)
    private String codigo;

    /**
     * Conteúdo da mensagem (texto ou legenda)
     */
    @Column(name = "mensagem", columnDefinition = "TEXT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci")
    private String mensagem;

    /**
     * Status da mensagem:
     * 1 = Enviada
     * 2 = Entregue
     * 3 = Lida
     * 4 = Erro
     */
    @Column(name = "status")
    @Builder.Default
    private Integer status = 1;

    /**
     * Resposta automática (se houver)
     */
    @Column(name = "resposta", columnDefinition = "TEXT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci")
    private String resposta;

    /**
     * Nome do arquivo anexo
     */
    @Size(max = 100, message = "Anexo deve ter no máximo 100 caracteres")
    @Column(name = "anexo", length = 100)
    private String anexo;

    /**
     * MIME type do anexo
     */
    @Size(max = 100, message = "MIME type deve ter no máximo 100 caracteres")
    @Column(name = "mime_type", length = 100)
    private String mimeType;

    /**
     * Tamanho do anexo em bytes
     */
    @Column(name = "tamanho")
    @Builder.Default
    private Integer tamanho = 0;

    /**
     * Duração em segundos (para áudio/vídeo)
     */
    @Column(name = "tempo")
    @Builder.Default
    private Integer tempo = 0;

    /**
     * Operador que enviou a mensagem (se for enviada)
     */
    @Column(name = "operador")
    private Long operadorId;

    /**
     * Indica se a mensagem foi lida
     */
    @Column(name = "lida")
    @Builder.Default
    private Boolean lida = false;

    // ============ Campos adicionais para compatibilidade com o service ============

    /**
     * Tipo da mensagem (enum) - compatibilidade com service
     */
    @Transient
    private TipoMensagem tipo;

    /**
     * Conteúdo da mensagem - alias para mensagem
     */
    @Transient
    private String conteudo;

    /**
     * Remetente da mensagem
     */
    @Transient
    private Remetente remetente;

    /**
     * Data de envio - alias para dataHora
     */
    @Transient
    private LocalDateTime dataEnvio;

    /**
     * Status de entrega
     */
    @Transient
    private StatusEntrega statusEntrega;

    /**
     * URL do arquivo
     */
    @Transient
    private String arquivoUrl;

    /**
     * Nome do arquivo
     */
    @Transient
    private String arquivoNome;

    /**
     * Tamanho do arquivo
     */
    @Transient
    private Long arquivoTamanho;

    /**
     * Data/hora em que a mensagem foi lida
     */
    @Column(name = "data_leitura")
    private LocalDateTime dataLeitura;

    /**
     * Latitude (para mensagens de localização)
     */
    @Column(name = "latitude")
    private Double latitude;

    /**
     * Longitude (para mensagens de localização)
     */
    @Column(name = "longitude")
    private Double longitude;

    /**
     * Nome do arquivo de mídia baixado
     */
    @Size(max = 255, message = "Nome do arquivo deve ter no máximo 255 caracteres")
    @Column(name = "arquivo_midia", length = 255)
    private String arquivoMidia;

    /**
     * Data de criação do registro
     */
    @Column(name = "data_criacao", updatable = false)
    private LocalDateTime dataCriacao;

    /**
     * Data da última atualização
     */
    @Column(name = "data_atualizacao")
    private LocalDateTime dataAtualizacao;

    /**
     * Callbacks JPA para auditoria
     */
    @PrePersist
    protected void onCreate() {
        dataCriacao = LocalDateTime.now();
        dataAtualizacao = LocalDateTime.now();
        if (dataHora == null) {
            dataHora = LocalDateTime.now();
        }
    }

    @PreUpdate
    protected void onUpdate() {
        dataAtualizacao = LocalDateTime.now();
    }

    /**
     * Verifica se a mensagem foi recebida (do contato)
     */
    public boolean isRecebida() {
        return "R".equals(entradaSaida);
    }

    /**
     * Verifica se a mensagem foi enviada (do operador)
     */
    public boolean isEnviada() {
        return "E".equals(entradaSaida);
    }

    /**
     * Verifica se é uma mensagem de texto
     */
    public boolean isTexto() {
        return tipoMensagem != null && tipoMensagem == 1;
    }

    /**
     * Verifica se é uma mensagem de áudio
     */
    public boolean isAudio() {
        return tipoMensagem != null && tipoMensagem == 2;
    }

    /**
     * Verifica se é uma mensagem de vídeo
     */
    public boolean isVideo() {
        return tipoMensagem != null && tipoMensagem == 3;
    }

    /**
     * Verifica se é uma mensagem de imagem
     */
    public boolean isImagem() {
        return tipoMensagem != null && tipoMensagem == 4;
    }

    /**
     * Verifica se é uma mensagem com anexo
     */
    public boolean isAnexo() {
        return tipoMensagem != null && tipoMensagem == 5;
    }

    /**
     * Marca a mensagem como lida
     */
    public void marcarComoLida() {
        this.lida = true;
        this.dataLeitura = LocalDateTime.now();
        this.status = 3;
    }

    /**
     * Retorna a descrição do tipo de mensagem
     */
    public String getDescricaoTipoMensagem() {
        return switch (tipoMensagem) {
            case 1 -> "Texto";
            case 2 -> "Áudio";
            case 3 -> "Vídeo";
            case 4 -> "Imagem";
            case 5 -> "Anexo";
            default -> "Desconhecido";
        };
    }

    /**
     * Retorna a descrição do status
     */
    public String getDescricaoStatus() {
        return switch (status) {
            case 1 -> "Enviada";
            case 2 -> "Entregue";
            case 3 -> "Lida";
            case 4 -> "Erro";
            default -> "Desconhecido";
        };
    }

    /**
     * Enum para tipos de mensagem
     */
    public enum TipoMensagem {
        TEXTO(1),
        AUDIO(2),
        VIDEO(3),
        IMAGEM(4),
        ANEXO(5),
        DOCUMENTO(5);  // Alias para ANEXO

        private final int valor;

        TipoMensagem(int valor) {
            this.valor = valor;
        }

        public int getValor() {
            return valor;
        }

        public static TipoMensagem fromValor(int valor) {
            return switch (valor) {
                case 1 -> TEXTO;
                case 2 -> AUDIO;
                case 3 -> VIDEO;
                case 4 -> IMAGEM;
                case 5 -> ANEXO;
                default -> throw new IllegalArgumentException("Tipo de mensagem inválido: " + valor);
            };
        }
    }

    /**
     * Enum para direção da mensagem
     */
    public enum Direcao {
        RECEBIDA("R"),
        ENVIADA("E");

        private final String valor;

        Direcao(String valor) {
            this.valor = valor;
        }

        public String getValor() {
            return valor;
        }

        public static Direcao fromValor(String valor) {
            return "R".equals(valor) ? RECEBIDA : ENVIADA;
        }
    }

    /**
     * Enum para remetente da mensagem
     */
    public enum Remetente {
        OPERADOR,
        CONTATO
    }

    /**
     * Enum para status de entrega
     */
    public enum StatusEntrega {
        ENVIADO,
        ENTREGUE,
        LIDO,
        ERRO
    }

    // ============ Getters/Setters para campos transientes ============

    public TipoMensagem getTipo() {
        if (tipo != null) return tipo;
        if (tipoMensagem != null) {
            return TipoMensagem.fromValor(tipoMensagem);
        }
        return null;
    }

    public void setTipo(TipoMensagem tipo) {
        this.tipo = tipo;
        if (tipo != null) {
            this.tipoMensagem = tipo.getValor();
        }
    }

    public String getConteudo() {
        return conteudo != null ? conteudo : mensagem;
    }

    public void setConteudo(String conteudo) {
        this.conteudo = conteudo;
        this.mensagem = conteudo;
    }

    public Remetente getRemetenteEnum() {
        return remetente;
    }

    public Remetente getRemetente() {
        if (remetente != null) return remetente;
        if (entradaSaida != null) {
            return "E".equals(entradaSaida) ? Remetente.OPERADOR : Remetente.CONTATO;
        }
        return null;
    }

    public void setRemetente(Remetente remetente) {
        this.remetente = remetente;
        if (remetente != null) {
            this.entradaSaida = remetente == Remetente.OPERADOR ? "E" : "R";
        }
    }

    public LocalDateTime getDataEnvio() {
        return dataEnvio != null ? dataEnvio : dataHora;
    }

    public void setDataEnvio(LocalDateTime dataEnvio) {
        this.dataEnvio = dataEnvio;
        this.dataHora = dataEnvio;
    }

    public StatusEntrega getStatusEntrega() {
        if (statusEntrega != null) return statusEntrega;
        if (status != null) {
            return switch (status) {
                case 1 -> StatusEntrega.ENVIADO;
                case 2 -> StatusEntrega.ENTREGUE;
                case 3 -> StatusEntrega.LIDO;
                case 4 -> StatusEntrega.ERRO;
                default -> StatusEntrega.ENVIADO;
            };
        }
        return StatusEntrega.ENVIADO;
    }

    public void setStatusEntrega(StatusEntrega statusEntrega) {
        this.statusEntrega = statusEntrega;
        if (statusEntrega != null) {
            this.status = switch (statusEntrega) {
                case ENVIADO -> 1;
                case ENTREGUE -> 2;
                case LIDO -> 3;
                case ERRO -> 4;
            };
        }
    }

    public String getArquivoUrl() {
        return arquivoUrl != null ? arquivoUrl : anexo;
    }

    public void setArquivoUrl(String arquivoUrl) {
        this.arquivoUrl = arquivoUrl;
        this.anexo = arquivoUrl;
    }

    public String getArquivoNome() {
        return arquivoNome;
    }

    public void setArquivoNome(String arquivoNome) {
        this.arquivoNome = arquivoNome;
    }

    public Long getArquivoTamanho() {
        return arquivoTamanho != null ? arquivoTamanho : (tamanho != null ? tamanho.longValue() : null);
    }

    public void setArquivoTamanho(Long arquivoTamanho) {
        this.arquivoTamanho = arquivoTamanho;
        if (arquivoTamanho != null) {
            this.tamanho = arquivoTamanho.intValue();
        }
    }
}
