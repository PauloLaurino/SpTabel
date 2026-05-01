/* eslint-disable @typescript-eslint/no-explicit-any */
export interface NotasCabDTO {
  id?: number;
  numeroNota?: string;
  serie?: string;
  dataEmissao?: string;
  dataCompetencia?: string;
  dataVencimento?: string;
  situacao?: string;
  tomadorCnpjCpf?: string;
  tomadorNome?: string;
  tomadorEndereco?: string;
  tomadorBairro?: string;
  tomadorCidade?: string;
  tomadorUf?: string;
  tomadorCep?: string;
  tomadorEmail?: string;
  tomadorTelefone?: string;
  valorServico?: number;
  valorIss?: number;
  valorDesc?: number;
  valorTotal?: number;
  discriminacao?: string;
  codigoMunicipio?: string;
  codigoServico?: string;
  aliquota?: number;
  emitenteCnpj?: string;
  emitenteInscricaoMunicipal?: string;
  emitenteRazaoSocial?: string;
  chaveNfse?: string;
  numeroRps?: string;
  linkConsulta?: string;
  xmlNfse?: string;
  protocolo?: string;
  mensagemErro?: string;
  observacoes?: string;
  codigoUsu?: number;
  dtcadastro?: string;
  dtalteracao?: string;
  itens?: NotasDetDTO[];
}

export interface NotasDetDTO {
  id?: number;
  notaCabId?: number;
  numeroItem?: number;
  codigoServico?: string;
  discriminacao?: string;
  quantidade?: number;
  valorUnitario?: number;
  valorTotal?: number;
  valorDesc?: number;
  valorIss?: number;
  aliquota?: number;
  tributavel?: string;
}

export interface NfseResponseDTO {
  sucesso: boolean;
  mensagem?: string;
  numeroNfse?: string;
  chaveNfse?: string;
  protocolo?: string;
  dataEmissao?: string;
  linkConsulta?: string;
  xml?: string;
  erros?: string[];
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

export interface PeriodFilter {
  dataInicio: string;
  dataFim: string;
}

export interface SearchFilter {
  termo: string;
  pagina?: number;
  tamanho?: number;
}

export type SituacaoNota = 'P' | 'E' | 'C' | 'X';
export type OrderDirection = 'ASC' | 'DESC';