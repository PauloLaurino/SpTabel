import axios, { AxiosInstance } from 'axios';
import { ParametrosDTO, LogParametros } from '../types/parametros';

const API_BASE_URL = '/api/nfse/parametros';

class ParametrizeApi {
  private client: AxiosInstance;

  constructor() {
    this.client = axios.create({
      baseURL: API_BASE_URL,
      headers: {
        'Content-Type': 'application/json',
      },
    });
  }

  /**
   * Busca os parâmetros atuais do sistema
   */
  async buscarParametros(): Promise<ParametrosDTO> {
    const response = await this.client.get<ParametrosDTO>('');
    return response.data;
  }

  /**
   * Salva/Atualiza os parâmetros do sistema
   */
  async salvarParametros(parametros: ParametrosDTO): Promise<ParametrosDTO> {
    const response = await this.client.put<ParametrosDTO>('', parametros);
    return response.data;
  }

  /**
   * Busca histórico de alterações com paginação
   */
  async buscarHistorico(pagina: number = 0, tamanho: number = 20) {
    const response = await this.client.get<any>('/historico', {
      params: { pagina, tamanho },
    });
    return response.data;
  }

  /**
   * Busca histórico por período
   */
  async buscarHistoricoPorPeriodo(dataInicio: string, dataFim: string): Promise<LogParametros[]> {
    const response = await this.client.get<LogParametros[]>('/historico/periodo', {
      params: { dataInicio, dataFim },
    });
    return response.data;
  }

  /**
   * Busca histórico por campo
   */
  async buscarHistoricoPorCampo(campo: string): Promise<LogParametros[]> {
    const response = await this.client.get<LogParametros[]>(`/historico/campo/${campo}`);
    return response.data;
  }
}

export const parametrizeApi = new ParametrizeApi();
export default parametrizeApi;