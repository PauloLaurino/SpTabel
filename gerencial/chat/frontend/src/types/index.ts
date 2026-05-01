// Tipos para o Chat WhatsApp

export interface User {
  id: number
  nome: string
  login: string
  perfil: string
  departamentoId?: number
}

export interface AuthResponse {
  accessToken: string
  refreshToken: string
  tokenType: string
  expiresIn: number
  operadorId: number
  nome: string
  login: string
  perfil: string
  departamentoId?: number
}

export interface LoginRequest {
  login: string
  senha: string
}

export interface Contato {
  id: number
  nome: string
  telefone: string
  email?: string
  cpfCnpj?: string
  fotoUrl?: string
  ativo: boolean
}

export interface VinculoIntimacao {
  id: number
  numapo1_001: string
  numapo2_001: string
  controle_001: string
  pdfEnviado: boolean
  comprovanteRecebido: boolean
  dataEnvioPdf?: string
  dataRecebimentoComprovante?: string
}

export interface Atendimento {
  id: number
  contatoId: number
  contatoNome: string
  contatoTelefone: string
  operadorId: number
  operadorNome: string
  departamentoId?: number
  departamentoNome?: string
  status: StatusAtendimento
  dataCriacao: string
  dataUltimaMensagem?: string
  dataFechamento?: string
  vinculos: VinculoIntimacao[]
}

export type StatusAtendimento = 'ABERTO' | 'EM_ATENDIMENTO' | 'AGUARDANDO' | 'FECHADO'

export interface Mensagem {
  id: number
  atendimentoId: number
  tipo: TipoMensagem
  conteudo: string
  arquivoUrl?: string
  arquivoNome?: string
  arquivoTamanho?: number
  remetente: Remetente
  dataEnvio: string
  statusEntrega: StatusEntrega
}

export type TipoMensagem = 'TEXTO' | 'IMAGEM' | 'DOCUMENTO' | 'AUDIO' | 'VIDEO'
export type Remetente = 'OPERADOR' | 'CONTATO'
export type StatusEntrega = 'ENVIADO' | 'ENTREGUE' | 'LIDO' | 'ERRO'

export interface MensagemRequest {
  tipo: TipoMensagem
  conteudo: string
  operador: boolean
}

export interface VincularIntimacaoRequest {
  numapo1_001: string
  numapo2_001: string
  controle_001: string
}

// WebSocket Message Types
export interface WebSocketMessage {
  type: 'JOIN' | 'JOINED' | 'MESSAGE' | 'NEW_MESSAGE' | 'TYPING' | 'LEAVE' | 'ERROR'
  atendimentoId?: number
  content?: string
  messageType?: TipoMensagem
  operador?: boolean
  typing?: boolean
  message?: Mensagem
}

// API Response Types
export interface PageResponse<T> {
  content: T[]
  totalElements: number
  totalPages: number
  size: number
  number: number
  first: boolean
  last: boolean
  empty: boolean
}

export interface ErrorResponse {
  timestamp: string
  status: number
  error: string
  message: string
  path: string
  errors?: Record<string, string>
}

// Tipos para integração com CTP001 (Sistema de Protesto)
export interface DevedorCtp001 {
  numapo1_001: string
  numapo2_001: string
  controle_001: string
  nome_001: string
  cpfCnpj_001: string
  endereco_001?: string
  cidade_001?: string
  uf_001?: string
  cep_001?: string
  valor_001: number
  dataProtesto_001?: string
  dtintimacao_001?: string
  dataocr_001?: string
  datapag_001?: string
  statusProtesto: 'PENDENTE' | 'INTIMADO' | 'PAGO' | 'CANCELADO'
}

export interface BuscaDevedorRequest {
  numapo1_001?: string
  numapo2_001?: string
  controle_001?: string
  cpfCnpj?: string
  nome?: string
}

export interface SincronizacaoResponse {
  totalSincronizados: number
  totalAtualizados: number
  totalErros: number
  detalhes: string[]
}

export interface IntimacaoResponse {
  vinculo: VinculoIntimacao
  devedor: DevedorCtp001
  mensagemEnviada: boolean
  dataEnvio: string
}
