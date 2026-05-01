import axios from 'axios';
import type { AssinaturaRequest, AssinaturaCaminhoRequest, AssinaturaResponse, CopiarPaginaRequest, AgruparRequest } from '../types';

const api = axios.create({
  baseURL: '/api/v1',
  headers: {
    'Content-Type': 'application/json',
  },
});

export const assinaturaService = {
  async assinar(request: AssinaturaRequest): Promise<AssinaturaResponse> {
    const response = await api.post<AssinaturaResponse>('/assinar', request);
    return response.data;
  },

  async assinarPorCaminho(request: AssinaturaCaminhoRequest): Promise<AssinaturaResponse> {
    const response = await api.post<AssinaturaResponse>('/assinar-caminho', request);
    return response.data;
  },

  async copiarPaginas(request: CopiarPaginaRequest): Promise<{ status: string; arquivo: string }> {
    const response = await api.post<{ status: string; mensagem?: string; arquivo: string }>('/pdf/copiar-paginas', request);
    return { status: response.data.status, arquivo: response.data.arquivo };
  },

  async agruparPdfs(request: AgruparRequest): Promise<{ status: string; arquivo: string; quantidade: number }> {
    const response = await api.post<{ status: string; mensagem?: string; arquivo: string; quantidade: number }>('/pdf/agrupar', request);
    return {
      status: response.data.status,
      arquivo: response.data.arquivo,
      quantidade: response.data.quantidade
    };
  },

  async listarArquivos(caminho: string): Promise<{ status: string; arquivos: any[] }> {
    const response = await api.post<{ status: string; arquivos: any[] }>('/pdf/listar', { caminho });
    return response.data;
  },

  async health(): Promise<{ status: string }> {
    const response = await api.get<{ status: string }>('/health');
    return response.data;
  },
};