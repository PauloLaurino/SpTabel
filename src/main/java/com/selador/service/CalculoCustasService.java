package com.selador.service;

import com.selador.dao.SerCustasDAO;
import com.selador.exception.DatabaseException;
import com.selador.model.SerCustas;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.logging.Logger;

/**
 * Service para cálculo de custas cartoriais conforme Código de Normas do Paraná
 */
public class CalculoCustasService {
    
    private static final Logger logger = Logger.getLogger(CalculoCustasService.class.getName());
    private static CalculoCustasService instance;
    
    // Constantes para encargos
    private static final BigDecimal ALIQ_FUNDEP = new BigDecimal("0.05");      // 5%
    private static final BigDecimal ALIQ_ISS = new BigDecimal("0.05");         // 5%
    private static final BigDecimal ALIQ_FUNREJUS = new BigDecimal("0.0025");  // 0,25%
    private static final BigDecimal ALIQ_FRJ = new BigDecimal("0.00002");      // 0,002%
    
    private SerCustasDAO serCustasDAO;
    
    private CalculoCustasService() {
        this.serCustasDAO = new SerCustasDAO();
    }
    
    public static synchronized CalculoCustasService getInstance() {
        if (instance == null) {
            instance = new CalculoCustasService();
        }
        return instance;
    }
    
    /**
     * Calcula custas completas para um ato cartorial
     */
    public Map<String, Object> calcularCustas(Map<String, Object> dados) {
        logger.info("🧮 [CUSTAS] Iniciando cálculo de custas");
        
        Map<String, Object> resultado = new HashMap<>();
        resultado.put("success", false);
        
        try {
            // Validar dados de entrada
            if (!dados.containsKey("tipoAto") || !dados.containsKey("baseCalculo")) {
                resultado.put("mensagem", "Parâmetros obrigatórios: tipoAto, baseCalculo");
                return resultado;
            }
            
            int tipoAto = Integer.parseInt(dados.get("tipoAto").toString());
            BigDecimal baseCalculo = new BigDecimal(dados.get("baseCalculo").toString());
            
            logger.info("   Tipo Ato: " + tipoAto);
            logger.info("   Base Cálculo: " + baseCalculo);
            
            // Buscar custas do ato
            List<SerCustas> custasAto = serCustasDAO.listarPorTipoAto(tipoAto);
            
            if (custasAto.isEmpty()) {
                resultado.put("mensagem", "Nenhuma custa encontrada para o tipo de ato: " + tipoAto);
                return resultado;
            }
            
            // Calcular emolumentos
            BigDecimal emolumentos = calcularEmolumentos(custasAto, baseCalculo);
            
            // Calcular encargos
            BigDecimal fundep = calcularFUNDEP(emolumentos);
            BigDecimal iss = calcularISS(emolumentos);
            BigDecimal funrejus = calcularFUNREJUS(baseCalculo);
            BigDecimal selos = calcularSelos(custasAto, baseCalculo);
            BigDecimal distribuicao = calcularDistribuicao(custasAto);
            
            // Calcular total
            BigDecimal total = emolumentos
                .add(fundep)
                .add(iss)
                .add(funrejus)
                .add(selos)
                .add(distribuicao);
            
            // Montar resultado detalhado
            Map<String, Object> detalhes = new HashMap<>();
            detalhes.put("tipoAto", tipoAto);
            detalhes.put("baseCalculo", baseCalculo);
            detalhes.put("emolumentos", emolumentos);
            detalhes.put("fundep", fundep);
            detalhes.put("iss", iss);
            detalhes.put("funrejus", funrejus);
            detalhes.put("selos", selos);
            detalhes.put("distribuicao", distribuicao);
            detalhes.put("total", total);
            
            // Listar atos envolvidos
            List<Map<String, Object>> atosDetalhados = new ArrayList<>();
            for (SerCustas custa : custasAto) {
                Map<String, Object> ato = new HashMap<>();
                ato.put("codigo", custa.getCodigoCus());
                ato.put("descricao", custa.getDescrCus());
                ato.put("modulo", custa.getModuloCus());
                ato.put("valor", custa.getValorCus());
                ato.put("agregado", "S".equals(custa.getAgregadoCus()));
                atosDetalhados.add(ato);
            }
            detalhes.put("atos", atosDetalhados);
            
            resultado.put("success", true);
            resultado.put("detalhes", detalhes);
            resultado.put("mensagem", "Cálculo realizado com sucesso");
            
            logger.info("✅ [CUSTAS] Cálculo concluído. Total: " + total);
            
        } catch (NumberFormatException e) {
            logger.severe("❌ [CUSTAS] Erro de formato numérico: " + e.getMessage());
            resultado.put("mensagem", "Erro de formato numérico: " + e.getMessage());
        } catch (DatabaseException e) {
            logger.severe("❌ [CUSTAS] Erro de banco de dados: " + e.getMessage());
            resultado.put("mensagem", "Erro ao consultar custas: " + e.getMessage());
        } catch (Exception e) {
            logger.severe("❌ [CUSTAS] Erro inesperado: " + e.getMessage());
            resultado.put("mensagem", "Erro no cálculo: " + e.getMessage());
        }
        
        return resultado;
    }
    
    /**
     * Calcula emolumentos base para o ato
     */
    private BigDecimal calcularEmolumentos(List<SerCustas> custasAto, BigDecimal baseCalculo) {
        BigDecimal total = BigDecimal.ZERO;
        
        for (SerCustas custa : custasAto) {
            // Ignorar encargos (módulo C) e atos agregados
            if ("C".equals(custa.getModuloCus()) || "S".equals(custa.getAgregadoCus())) {
                continue;
            }
            
            BigDecimal valor = custa.getValorCus();
            
            // Verificar se tem valor de referência para cálculo progressivo
            if (custa.hasValorReferencia() && baseCalculo.compareTo(custa.getQtdevrcCus()) > 0) {
                // Calcular fator progressivo
                BigDecimal excedente = baseCalculo.subtract(custa.getQtdevrcCus());
                BigDecimal fator = excedente.divide(new BigDecimal("1000"), 4, RoundingMode.HALF_UP);
                valor = valor.add(fator);
                logger.info("   [CUSTAS] Valor progressivo para " + custa.getDescrCus() + ": " + valor);
            }
            
            total = total.add(valor);
        }
        
        return total.setScale(2, RoundingMode.HALF_UP);
    }
    
    /**
     * Calcula FUNDEP (5% sobre emolumentos)
     */
    private BigDecimal calcularFUNDEP(BigDecimal emolumentos) {
        BigDecimal fundep = emolumentos.multiply(ALIQ_FUNDEP);
        logger.info("   [CUSTAS] FUNDEP: " + fundep);
        return fundep.setScale(2, RoundingMode.HALF_UP);
    }
    
    /**
     * Calcula ISS (5% sobre emolumentos)
     */
    private BigDecimal calcularISS(BigDecimal emolumentos) {
        BigDecimal iss = emolumentos.multiply(ALIQ_ISS);
        logger.info("   [CUSTAS] ISS: " + iss);
        return iss.setScale(2, RoundingMode.HALF_UP);
    }
    
    /**
     * Calcula FUNREJUS (0,25% + 0,002% sobre base de cálculo)
     */
    private BigDecimal calcularFUNREJUS(BigDecimal baseCalculo) {
        BigDecimal funrejus = baseCalculo.multiply(ALIQ_FUNREJUS);
        BigDecimal frj = baseCalculo.multiply(ALIQ_FRJ);
        BigDecimal total = funrejus.add(frj);
        logger.info("   [CUSTAS] FUNREJUS: " + total);
        return total.setScale(4, RoundingMode.HALF_UP);
    }
    
    /**
     * Calcula valor dos selos
     */
    private BigDecimal calcularSelos(List<SerCustas> custasAto, BigDecimal baseCalculo) {
        BigDecimal totalSelos = BigDecimal.ZERO;
        
        for (SerCustas custa : custasAto) {
            // Verificar se é selo (códigos 903, 906, 908, 909)
            if (custa.getCodigoCus() == 903 ||   // SELO TN1
                custa.getCodigoCus() == 906 ||   // SELO AH
                custa.getCodigoCus() == 908 ||   // SELO TN2 (2x)
                custa.getCodigoCus() == 909) {   // SELO TN3
                
                BigDecimal valorSelo = custa.getValorCus();
                
            }
        }
        
        logger.info("   [CUSTAS] Selos: " + totalSelos);
        return totalSelos.setScale(2, RoundingMode.HALF_UP);
    }
    
    /**
     * Calcula valor da distribuição
     */
    private BigDecimal calcularDistribuicao(List<SerCustas> custasAto) {
        BigDecimal totalDist = BigDecimal.ZERO;
        
        for (SerCustas custa : custasAto) {
            // Verificar se é distribuição (códigos 902, 905)
            if (custa.getCodigoCus() == 902 ||   // DISTRIBUIÇÃO ATA
                custa.getCodigoCus() == 905) {   // DISTRIBUIÇÃO
                
                totalDist = totalDist.add(custa.getValorCus());
            }
        }
        
        logger.info("   [CUSTAS] Distribuição: " + totalDist);
        return totalDist.setScale(2, RoundingMode.HALF_UP);
    }
    
    /**
     * Lista tipos de atos disponíveis
     */
    public Map<String, Object> listarTiposAtos(String modulo) {
        Map<String, Object> resultado = new HashMap<>();
        resultado.put("success", false);
        
        try {
            List<SerCustas> atos;
            
            if (modulo != null && !modulo.isEmpty()) {
                atos = serCustasDAO.listarPorModulo(modulo);
            } else {
                atos = serCustasDAO.listarAtosPrincipais();
            }
            
            List<Map<String, Object>> atosDTO = new ArrayList<>();
            for (SerCustas ato : atos) {
                Map<String, Object> atoMap = new HashMap<>();
                atoMap.put("codigo", ato.getCodigoCus());
                atoMap.put("tipoAto", ato.getTipoatoCus());
                atoMap.put("descricao", ato.getDescrCus());
                atoMap.put("modulo", ato.getModuloCus());
                atoMap.put("valor", ato.getValorCus());
                atoMap.put("valorReferencia", ato.getQtdevrcCus());
                atoMap.put("agregado", "S".equals(ato.getAgregadoCus()));
                atosDTO.add(atoMap);
            }
            
            resultado.put("success", true);
            resultado.put("atos", atosDTO);
            resultado.put("total", atosDTO.size());
            
        } catch (DatabaseException e) {
            resultado.put("mensagem", "Erro ao listar atos: " + e.getMessage());
        }
        
        return resultado;
    }
    
    /**
     * Lista encargos disponíveis
     */
    public Map<String, Object> listarEncargos() {
        Map<String, Object> resultado = new HashMap<>();
        resultado.put("success", false);
        
        try {
            List<SerCustas> encargos = serCustasDAO.listarEncargos();
            
            List<Map<String, Object>> encargosDTO = new ArrayList<>();
            for (SerCustas encargo : encargos) {
                Map<String, Object> encargoMap = new HashMap<>();
                encargoMap.put("codigo", encargo.getCodigoCus());
                encargoMap.put("descricao", encargo.getDescrCus());
                encargoMap.put("valor", encargo.getValorCus());
                encargoMap.put("aliqCus", encargo.getAliqCus());
                encargoMap.put("tag", encargo.getTagCus());
                encargosDTO.add(encargoMap);
            }
            
            resultado.put("success", true);
            resultado.put("encargos", encargosDTO);
            resultado.put("total", encargosDTO.size());
            
        } catch (DatabaseException e) {
            resultado.put("mensagem", "Erro ao listar encargos: " + e.getMessage());
        }
        
        return resultado;
    }
    
    /**
     * Busca imóveis por termo de pesquisa
     */
    public Map<String, Object> buscarImoveis(String termo) {
        Map<String, Object> resultado = new HashMap<>();
        resultado.put("success", false);
        
        try {
            List<Map<String, Object>> imoveis = serCustasDAO.buscarImoveis(termo);
            
            resultado.put("success", true);
            resultado.put("imoveis", imoveis);
            resultado.put("total", imoveis.size());
            
            logger.info("✅ [CUSTAS] Busca de imóveis concluída. Total: " + imoveis.size());
            
        } catch (DatabaseException e) {
            resultado.put("mensagem", "Erro ao buscar imóveis: " + e.getMessage());
            logger.severe("❌ [CUSTAS] Erro ao buscar imóveis: " + e.getMessage());
        }
        
        return resultado;
    }
    
    /**
     * Busca partes de um imóvel
     */
    public Map<String, Object> buscarPartes(String imovelId) {
        Map<String, Object> resultado = new HashMap<>();
        resultado.put("success", false);
        
        try {
            List<Map<String, Object>> partes = serCustasDAO.buscarPartes(imovelId);
            
            resultado.put("success", true);
            resultado.put("partes", partes);
            resultado.put("total", partes.size());
            
            logger.info("✅ [CUSTAS] Busca de partes concluída. Total: " + partes.size());
            
        } catch (DatabaseException e) {
            resultado.put("mensagem", "Erro ao buscar partes: " + e.getMessage());
            logger.severe("❌ [CUSTAS] Erro ao buscar partes: " + e.getMessage());
        }
        
        return resultado;
    }
    
    /**
     * Lista módulos disponíveis
     */
    public Map<String, Object> listarModulos() {
        Map<String, Object> resultado = new HashMap<>();
        resultado.put("success", false);
        
        try {
            // Módulos disponíveis no sistema
            List<Map<String, Object>> modulos = new ArrayList<>();
            
            // Módulo N - Tabelionato de Notas
            Map<String, Object> moduloNotas = new HashMap<>();
            moduloNotas.put("codigo", "N");
            moduloNotas.put("descricao", "Tabelionato de Notas");
            modulos.add(moduloNotas);
            
            // Módulo R - Tabelionato de Registro
            Map<String, Object> moduloRegistro = new HashMap<>();
            moduloRegistro.put("codigo", "R");
            moduloRegistro.put("descricao", "Tabelionato de Registro");
            modulos.add(moduloRegistro);
            
            // Módulo O - Outros
            Map<String, Object> moduloOutros = new HashMap<>();
            moduloOutros.put("codigo", "O");
            moduloOutros.put("descricao", "Outros");
            modulos.add(moduloOutros);
            
            resultado.put("success", true);
            resultado.put("modulos", modulos);
            resultado.put("total", modulos.size());
            
            logger.info("✅ [CUSTAS] Lista de módulos concluída. Total: " + modulos.size());
            
        } catch (Exception e) {
            resultado.put("mensagem", "Erro ao listar módulos: " + e.getMessage());
            logger.severe("❌ [CUSTAS] Erro ao listar módulos: " + e.getMessage());
        }
        
        return resultado;
    }
    
    /**
     * Lista tipos de ato por módulo (tabela servicos)
     */
    public Map<String, Object> listarTiposAtoPorModulo(String modulo) {
        Map<String, Object> resultado = new HashMap<>();
        resultado.put("success", false);
        
        try {
            List<SerCustas> tiposAto;
            
            if (modulo != null && !modulo.isEmpty()) {
                // Buscar tipos de ato na tabela servicos por módulo
                tiposAto = serCustasDAO.listarPorModulo(modulo);
            } else {
                // Se não informou módulo, retorna lista vazia
                tiposAto = new ArrayList<>();
            }
            
            List<Map<String, Object>> tiposDTO = new ArrayList<>();
            for (SerCustas tipo : tiposAto) {
                Map<String, Object> tipoMap = new HashMap<>();
                tipoMap.put("codigo", tipo.getCodigoCus());
                tipoMap.put("descricao", tipo.getDescrCus());
                tipoMap.put("modulo", tipo.getModuloCus());
                tipoMap.put("valor", tipo.getValorCus());
                tipoMap.put("valorReferencia", tipo.getQtdevrcCus());
                tipoMap.put("agregado", "S".equals(tipo.getAgregadoCus()));
                tiposDTO.add(tipoMap);
            }
            
            resultado.put("success", true);
            resultado.put("tipos", tiposDTO);
            resultado.put("total", tiposDTO.size());
            
            logger.info("✅ [CUSTAS] Lista de tipos de ato concluída. Total: " + tiposDTO.size());
            
        } catch (DatabaseException e) {
            resultado.put("mensagem", "Erro ao listar tipos de ato: " + e.getMessage());
            logger.severe("❌ [CUSTAS] Erro ao listar tipos de ato: " + e.getMessage());
        }
        
        return resultado;
    }
    
    /**
     * Lista protocolos disponíveis
     */
    public Map<String, Object> listarProtocolos() {
        Map<String, Object> resultado = new HashMap<>();
        resultado.put("success", false);
        
        try {
            List<Map<String, Object>> protocolos = serCustasDAO.listarProtocolos();
            
            resultado.put("success", true);
            resultado.put("protocolos", protocolos);
            resultado.put("total", protocolos.size());
            
            logger.info("✅ [CUSTAS] Lista de protocolos concluída. Total: " + protocolos.size());
            
        } catch (DatabaseException e) {
            resultado.put("mensagem", "Erro ao listar protocolos: " + e.getMessage());
            logger.severe("❌ [CUSTAS] Erro ao listar protocolos: " + e.getMessage());
        }
        
        return resultado;
    }
    
    /**
     * Lista imóveis por protocolo
     */
    public Map<String, Object> listarImoveisPorProtocolo(String protocolo) {
        Map<String, Object> resultado = new HashMap<>();
        resultado.put("success", false);
        
        try {
            List<Map<String, Object>> imoveis = serCustasDAO.listarImoveisPorProtocolo(protocolo);
            
            resultado.put("success", true);
            resultado.put("imoveis", imoveis);
            resultado.put("total", imoveis.size());
            
            logger.info("✅ [CUSTAS] Lista de imóveis por protocolo concluída. Total: " + imoveis.size());
            
        } catch (DatabaseException e) {
            resultado.put("mensagem", "Erro ao listar imóveis por protocolo: " + e.getMessage());
            logger.severe("❌ [CUSTAS] Erro ao listar imóveis por protocolo: " + e.getMessage());
        }
        
        return resultado;
    }
    
    /**
     * Busca dados completos de um protocolo para cálculo de custas
     * @param codigoProtocolo Código do protocolo
     * @return Mapa com dados do protocolo
     */
    public Map<String, Object> buscarDadosProtocolo(String codigoProtocolo) {
        Map<String, Object> resultado = new HashMap<>();
        resultado.put("success", false);
        
        try {
            Map<String, Object> protocolo = serCustasDAO.buscarDadosProtocolo(codigoProtocolo);
            
            if (protocolo == null) {
                resultado.put("mensagem", "Protocolo não encontrado: " + codigoProtocolo);
                return resultado;
            }
            
            resultado.put("success", true);
            resultado.put("protocolo", protocolo);
            
            logger.info("✅ [CUSTAS] Dados do protocolo " + codigoProtocolo + " carregados com sucesso");
            
        } catch (DatabaseException e) {
            resultado.put("mensagem", "Erro ao buscar dados do protocolo: " + e.getMessage());
            logger.severe("❌ [CUSTAS] Erro ao buscar dados do protocolo: " + e.getMessage());
        }
        
        return resultado;
    }
}
