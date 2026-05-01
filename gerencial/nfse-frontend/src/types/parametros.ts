// Tipos para parâmetros do sistema
export interface ParametrosDTO {
  codigoPar?: number;
  docPar?: string;
  nomeTabeliao?: string;
  codigoTabeliao?: string;
  codigoCartorio?: string;
  nomeCartorio?: string;
  cnpjCartorio?: string;
  inscricaoEstadual?: string;
  enderecoCartorio?: string;
  telefoneCartorio?: string;
  emailCartorio?: string;
  ativa?: string;
  dtVersaoFunarpen?: string;
  obsFunarpen?: string;
  
  // Impostos
  cbsPar?: number;
  ibsPar?: number;
  pisPar?: number;
  cofinsPar?: number;
  csllPar?: number;
  irpjPar?: number;
  irpjAdicPar?: number;
  cppPar?: number;
  
  // Fase
  faseReformaPar?: string;
  cbsAtivoPar?: boolean;
  ibsAtivoPar?: boolean;
  cppAtivoPar?: boolean;
  
  // Deduções
  cbsDeduzirPar?: number;
  ibsDeduzirPar?: number;
}

export interface LogParametros {
  id?: number;
  codigoParametro?: number;
  nomeCampo?: string;
  valorAnterior?: string;
  valorNovo?: string;
  tipoOperacao?: string;
  dataAlteracao?: string;
  usuarioAlteracao?: string;
  ipOrigem?: string;
  moduloOrigem?: string;
  observacao?: string;
}

export interface PageResponse<T> {
  content: T[];
  totalPages: number;
  totalElements: number;
  size: number;
  number: number;
  first: boolean;
  last: boolean;
  empty: boolean;
}

// Campos agrupados por aba (baseado na especificação)
export const CAMPOS_POR_TABA = {
  cadstro: [
    'codigoPar', 'docPar', 'nomeTabeliao', 'codigoTabeliao', 'codigoCartorio',
    'nomeCartorio', 'cnpjCartorio', 'inscricaoEstadual', 'enderecoCartorio',
    'telefoneCartorio', 'emailCartorio', 'ativa'
  ],
  gerencial: [
    'ativa', 'dtVersaoFunarpen', 'obsFunarpen'
  ],
  ccn: [
    'cbsPar', 'ibsPar', 'pisPar', 'cofinsPar', 'csllPar'
  ],
  funarpen: [
    'irpjPar', 'irpjAdicPar', 'cppPar', 'faseReformaPar', 'cbsAtivoPar',
    'ibsAtivoPar', 'cppAtivoPar', 'cbsDeduzirPar', 'ibsDeduzirPar'
  ],
  arquivos: [
    // Arquivos - campos a definir
  ],
  boleto: [
    // Boleto - campos a definir
  ],
  nfse: [
    // NFS-e - campos a definir
  ],
  cabecalho: [
    // Cabeçalho - campo 45
  ]
};