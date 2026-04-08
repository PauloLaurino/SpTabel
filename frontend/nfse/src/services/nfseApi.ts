import axios, { AxiosInstance, AxiosError } from 'axios';
import {
  NotasCabDTO,
  NotasDetDTO,
  NfseResponseDTO,
  PageResponse,
  PeriodFilter,
  SearchFilter,
} from '../types/nfse';

const API_BASE_URL = '/api/nfse';

class NfseApi {
  private client: AxiosInstance;

  constructor() {
    this.client = axios.create({
      baseURL: API_BASE_URL,
      headers: {
        'Content-Type': 'application/json',
      },
    });

    this.client.interceptors.response.use(
      (response) => response,
      (error: AxiosError) => {
        console.error('API Error:', error.response?.data);
        return Promise.reject(error);
      }
    );
  }

  // ===== LISTAGEM DE NOTAS =====

  async listarNotas(
    pagina: number = 0,
    tamanho: number = 20,
    ordenacao: string = 'dataEmissao',
    direcao: 'ASC' | 'DESC' = 'DESC'
  ): Promise<PageResponse<NotasCabDTO>> {
    const response = await this.client.get<PageResponse<NotasCabDTO>>('/notas', {
      params: { pagina, tamanho, ordenacao, direcao },
    });
    return response.data;
  }

  async listarPendentes(
    pagina: number = 0,
    tamanho: number = 20
  ): Promise<PageResponse<NotasCabDTO>> {
    const response = await this.client.get<PageResponse<NotasCabDTO>>('/pendentes', {
      params: { pagina, tamanho },
    });
    return response.data;
  }

  async buscarPorPeriodo(filter: PeriodFilter): Promise<NotasCabDTO[]> {
    const response = await this.client.get<NotasCabDTO[]>('/notas/periodo', {
      params: filter,
    });
    return response.data;
  }

  async buscarPorTomador(termo: string): Promise<NotasCabDTO[]> {
    const response = await this.client.get<NotasCabDTO[]>('/notas/tomador', {
      params: { termo },
    });
    return response.data;
  }

  async buscarGlobal(filter: SearchFilter): Promise<PageResponse<NotasCabDTO>> {
    const response = await this.client.get<PageResponse<NotasCabDTO>>('/notas/busca', {
      params: filter,
    });
    return response.data;
  }

  // ===== OPERAÇÕES DE NOTA =====

  async buscarNotaPorId(id: number): Promise<NotasCabDTO> {
    const response = await this.client.get<NotasCabDTO>(`/notas/${id}`);
    return response.data;
  }

  async criarNota(nota: Partial<NotasCabDTO>): Promise<NotasCabDTO> {
    const response = await this.client.post<NotasCabDTO>('/notas', nota);
    return response.data;
  }

  async atualizarNota(id: number, nota: Partial<NotasCabDTO>): Promise<NotasCabDTO> {
    const response = await this.client.put<NotasCabDTO>(`/notas/${id}`, nota);
    return response.data;
  }

  async excluirNota(id: number): Promise<void> {
    await this.client.delete(`/notas/${id}`);
  }

  // ===== ITENS =====

  async listarItens(idNota: number): Promise<NotasDetDTO[]> {
    const response = await this.client.get<NotasDetDTO[]>(`/notas/${idNota}/itens`);
    return response.data;
  }

  // ===== EMISSÃO =====

  async emitirNota(id: number): Promise<NfseResponseDTO> {
    const response = await this.client.post<NfseResponseDTO>(`/notas/${id}/emitir`);
    return response.data;
  }

  async emitirLote(notaIds: string[]): Promise<NfseResponseDTO> {
    const response = await this.client.post<NfseResponseDTO>('/notas/emissão-lote', {
      notaIds,
    });
    return response.data;
  }

  // ===== CANCELAMENTO =====

  async cancelarNota(id: number, motivo: string): Promise<NfseResponseDTO> {
    const response = await this.client.post<NfseResponseDTO>(`/notas/${id}/cancelar`, {
      motivo,
    });
    return response.data;
  }

  // ===== CONSULTA =====

  async consultarNota(id: number): Promise<NotasCabDTO> {
    const response = await this.client.get<NotasCabDTO>(`/notas/${id}/consultar`);
    return response.data;
  }

  async consultarNfsePorNumero(
    numero: string,
    cnpjTomador?: string
  ): Promise<NfseResponseDTO> {
    const response = await this.client.get<NfseResponseDTO>('/nfse/consultar', {
      params: { numero, cnpjTomador },
    });
    return response.data;
  }

  async consultarLote(numeroLote: string): Promise<NfseResponseDTO> {
    const response = await this.client.get<NfseResponseDTO>(`/nfse/lote/${numeroLote}`);
    return response.data;
  }

  // ===== UTILITÁRIOS =====

  async baixarXml(id: number): Promise<string> {
    const response = await this.client.get<string>(`/notas/${id}/xml`);
    return response.data;
  }

  async enviarPorEmail(id: number, email: string): Promise<void> {
    await this.client.post(`/notas/${id}/email`, { email });
  }
}

export const nfseApi = new NfseApi();
export default nfseApi;