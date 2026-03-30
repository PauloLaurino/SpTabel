package com.selador.model;

import com.selador.enums.StatusSelagem;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Model que representa o resultado de um processo de selagem.
 */
public class ResultadoSelagem implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    // ========== INFORMAÇÕES GERAIS ==========
    private String idProcesso;              // ID único do processo
    private Date dataInicio;                // Data/hora início
    private Date dataFim;                   // Data/hora fim
    private StatusSelagem status;           // Status geral
    private String mensagem;                // Mensagem descritiva
    
    // ========== ESTATÍSTICAS ==========
    private int totalApontamentos;          // Total de apontamentos processados
    private int totalSucesso;               // Selados com sucesso
    private int totalErros;                 // Falhas na selagem
    private int totalIgnorados;             // Ignorados (já selados, etc.)
    
    // ========== DETALHES POR TIPO DE SELO ==========
    private Map<String, Integer> selosUtilizados; // Tipo -> Quantidade
    
    // ========== DETALHES INDIVIDUAIS ==========
    private List<DetalheSelagem> detalhes;  // Detalhes por apontamento
    
    // ========== INFORMAÇÕES DO USUÁRIO ==========
    private String usuario;                 // Usuário que executou
    private String ip;                      // IP de origem
    
    // ========== CONSTRUTORES ==========
    public ResultadoSelagem() {
        this.idProcesso = generateId();
        this.dataInicio = new Date();
        this.status = StatusSelagem.PENDENTE;
        this.selosUtilizados = new HashMap<>();
        this.detalhes = new ArrayList<>();
    }
    
    public ResultadoSelagem(String usuario, String ip) {
        this();
        this.usuario = usuario;
        this.ip = ip;
    }
    
    // ========== MÉTODOS DE NEGÓCIO ==========
    
    /**
     * Gera um ID único para o processo
     */
    private String generateId() {
        return "SEL-" + System.currentTimeMillis() + "-" + 
               Integer.toHexString(this.hashCode());
    }
    
    /**
     * Adiciona um detalhe de selagem
     */
    public void addDetalhe(DetalheSelagem detalhe) {
        if (this.detalhes == null) {
            this.detalhes = new ArrayList<>();
        }
        this.detalhes.add(detalhe);
        
        // Atualiza contadores
        if (detalhe.getStatus() == StatusSelagem.SELADO) {
            this.totalSucesso++;
            
            // Contabiliza tipo de selo
            if (detalhe.getTipoSelo() != null) {
                String tipo = detalhe.getTipoSelo().getCodigo();
                selosUtilizados.put(tipo, selosUtilizados.getOrDefault(tipo, 0) + 1);
            }
        } else if (detalhe.getStatus() != null && detalhe.getStatus().isErro()) {
            this.totalErros++;
        } else {
            this.totalIgnorados++;
        }
        
        this.totalApontamentos++;
    }
    
    /**
     * Finaliza o processo com sucesso
     */
    public void finalizarComSucesso(String mensagem) {
        this.dataFim = new Date();
        this.status = StatusSelagem.CONCLUIDO;
        this.mensagem = mensagem;
    }
    
    /**
     * Finaliza o processo com erro
     */
    public void finalizarComErro(String mensagem) {
        this.dataFim = new Date();
        this.status = StatusSelagem.ERRO;
        this.mensagem = mensagem;
    }
    
    /**
     * Calcula a porcentagem de sucesso
     */
    public double getPercentualSucesso() {
        if (totalApontamentos == 0) return 0.0;
        return (totalSucesso * 100.0) / totalApontamentos;
    }
    
    /**
     * Calcula a porcentagem de erros
     */
    public double getPercentualErros() {
        if (totalApontamentos == 0) return 0.0;
        return (totalErros * 100.0) / totalApontamentos;
    }
    
    /**
     * Retorna a duração do processo em milissegundos
     */
    public long getDuracaoMillis() {
        if (dataFim == null) {
            return System.currentTimeMillis() - dataInicio.getTime();
        }
        return dataFim.getTime() - dataInicio.getTime();
    }
    
    /**
     * Retorna a duração formatada (HH:MM:SS)
     */
    public String getDuracaoFormatada() {
        long millis = getDuracaoMillis();
        
        long segundos = millis / 1000;
        long minutos = segundos / 60;
        long horas = minutos / 60;
        
        segundos = segundos % 60;
        minutos = minutos % 60;
        
        return String.format("%02d:%02d:%02d", horas, minutos, segundos);
    }
    
    /**
     * Retorna se o processo foi bem sucedido
     */
    public boolean isSucesso() {
        return status != null && status.isSucesso();
    }
    
    /**
     * Retorna se o processo teve algum erro
     */
    public boolean hasErros() {
        return totalErros > 0;
    }
    
    /**
     * Retorna a lista de detalhes com erro
     */
    public List<DetalheSelagem> getDetalhesComErro() {
        List<DetalheSelagem> erros = new ArrayList<>();
        if (detalhes != null) {
            for (DetalheSelagem detalhe : detalhes) {
                if (detalhe.getStatus() != null && detalhe.getStatus().isErro()) {
                    erros.add(detalhe);
                }
            }
        }
        return erros;
    }
    
    /**
     * Retorna a lista de detalhes com sucesso
     */
    public List<DetalheSelagem> getDetalhesComSucesso() {
        List<DetalheSelagem> sucessos = new ArrayList<>();
        if (detalhes != null) {
            for (DetalheSelagem detalhe : detalhes) {
                if (detalhe.getStatus() != null && detalhe.getStatus().isSucesso()) {
                    sucessos.add(detalhe);
                }
            }
        }
        return sucessos;
    }
    
    // ========== GETTERS E SETTERS ==========
    
    public String getIdProcesso() { return idProcesso; }
    public void setIdProcesso(String idProcesso) { this.idProcesso = idProcesso; }
    
    public Date getDataInicio() { return dataInicio; }
    public void setDataInicio(Date dataInicio) { this.dataInicio = dataInicio; }
    
    public Date getDataFim() { return dataFim; }
    public void setDataFim(Date dataFim) { this.dataFim = dataFim; }
    
    public StatusSelagem getStatus() { return status; }
    public void setStatus(StatusSelagem status) { this.status = status; }
    
    public String getMensagem() { return mensagem; }
    public void setMensagem(String mensagem) { this.mensagem = mensagem; }
    
    public int getTotalApontamentos() { return totalApontamentos; }
    public void setTotalApontamentos(int totalApontamentos) { this.totalApontamentos = totalApontamentos; }
    
    public int getTotalSucesso() { return totalSucesso; }
    public void setTotalSucesso(int totalSucesso) { this.totalSucesso = totalSucesso; }
    
    public int getTotalErros() { return totalErros; }
    public void setTotalErros(int totalErros) { this.totalErros = totalErros; }
    
    public int getTotalIgnorados() { return totalIgnorados; }
    public void setTotalIgnorados(int totalIgnorados) { this.totalIgnorados = totalIgnorados; }
    
    public Map<String, Integer> getSelosUtilizados() { return selosUtilizados; }
    public void setSelosUtilizados(Map<String, Integer> selosUtilizados) { this.selosUtilizados = selosUtilizados; }
    
    public List<DetalheSelagem> getDetalhes() { return detalhes; }
    public void setDetalhes(List<DetalheSelagem> detalhes) { this.detalhes = detalhes; }
    
    public String getUsuario() { return usuario; }
    public void setUsuario(String usuario) { this.usuario = usuario; }
    
    public String getIp() { return ip; }
    public void setIp(String ip) { this.ip = ip; }
    
    // ========== TO STRING ==========
    @Override
    public String toString() {
        return "ResultadoSelagem{" +
                "idProcesso='" + idProcesso + '\'' +
                ", status=" + status +
                ", totalSucesso=" + totalSucesso +
                ", totalErros=" + totalErros +
                ", percentualSucesso=" + String.format("%.1f%%", getPercentualSucesso()) +
                '}';
    }
    
    // ========== CLASSE INTERNA DETALHE SELAGEM ==========
    public static class DetalheSelagem implements Serializable {
        
        private static final long serialVersionUID = 1L;
        
        private Apontamento apontamento;
        private com.selador.enums.TipoSelo tipoSelo;
        private String numeroSelo;
        private StatusSelagem status;
        private String mensagem;
        private Date dataProcessamento;
        private String idap;
        private String qrcode;
        
        public DetalheSelagem() {
            this.dataProcessamento = new Date();
        }
        
        public DetalheSelagem(Apontamento apontamento) {
            this();
            this.apontamento = apontamento;
            this.status = StatusSelagem.PENDENTE;
        }
        
        // Getters e Setters
        public Apontamento getApontamento() { return apontamento; }
        public void setApontamento(Apontamento apontamento) { this.apontamento = apontamento; }
        
        public com.selador.enums.TipoSelo getTipoSelo() { return tipoSelo; }
        public void setTipoSelo(com.selador.enums.TipoSelo tipoSelo) { this.tipoSelo = tipoSelo; }
        
        public String getNumeroSelo() { return numeroSelo; }
        public void setNumeroSelo(String numeroSelo) { this.numeroSelo = numeroSelo; }
        
        public StatusSelagem getStatus() { return status; }
        public void setStatus(StatusSelagem status) { this.status = status; }
        
        public String getMensagem() { return mensagem; }
        public void setMensagem(String mensagem) { this.mensagem = mensagem; }
        
        public Date getDataProcessamento() { return dataProcessamento; }
        public void setDataProcessamento(Date dataProcessamento) { this.dataProcessamento = dataProcessamento; }
        
        public String getIdap() { return idap; }
        public void setIdap(String idap) { this.idap = idap; }
        
        public String getQrcode() { return qrcode; }
        public void setQrcode(String qrcode) { this.qrcode = qrcode; }
        
        @Override
        public String toString() {
            return "DetalheSelagem{" +
                    "apontamento=" + (apontamento != null ? apontamento.getChave() : "null") +
                    ", status=" + status +
                    ", selo='" + numeroSelo + '\'' +
                    '}';
        }
    }
}