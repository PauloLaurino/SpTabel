export interface AssinaturaRequest {
  documento: string;
  certificado: string;
  pin?: string;
  timestamp: boolean;
}

export interface AssinaturaCaminhoRequest {
  caminho: string;
  saida: string;
  certificado?: string;
  pin?: string;
  timestamp?: boolean;
}

export interface AssinaturaResponse {
  status: string;
  documento_assinado?: string;
  certificado?: DadosCertificado;
  timestamp?: string;
  mensagem?: string;
  arquivo?: string;
}

export interface DadosCertificado {
  nome: string;
  cpf?: string;
  cnpj?: string;
  dataValidade: string;
  tipo: string;
}

export interface PdfInfo {
  nome: string;
  paginas: number;
  tamanho: number;
}

export interface CopiarPaginaRequest {
  caminhoLivro: string;
  paginaInicial: number;
  paginaFinal: number;
  saida: string;
}

export interface AgruparRequest {
  pasta: string;
  prefixo?: string;
  saida: string;
}