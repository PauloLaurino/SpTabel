/**
 * Serviço de API para comunicação com backend NFSe
 * 
 * @author Seprocom
 */

const API_BASE_URL = process.env.REACT_APP_API_URL || 'http://localhost:8080/gerencial/api/nfse';

/**
 * Headers padrão para todas as requisições
 */
const getHeaders = () => {
  const token = localStorage.getItem('nfse_token');
  return {
    'Content-Type': 'application/json',
    'Accept': 'application/json',
    ...(token ? { 'Authorization': `Bearer ${token}` } : {}),
  };
};

/**
 * Função auxiliar para tratar respostas
 */
const handleResponse = async (response) => {
  if (!response.ok) {
    const error = await response.json().catch(() => ({ mensagem: 'Erro desconhecido' }));
    throw new Error(error.mensagem || `HTTP ${response.status}`);
  }
  return response.json();
};

/**
 * Lista notas fiscais com paginação
 * @param {number} pagina - Número da página (0-indexed)
 * @param {number} tamanho - Tamanho da página
 * @param {string} ordenacao - Campo de ordenação
 * @param {string} direcao - Direção da ordenação (ASC/DESC)
 */
export const listarNotas = async (pagina = 0, tamanho = 20, ordenacao = 'dataEmissao', direcao = 'DESC') => {
  const params = new URLSearchParams({
    pagina,
    tamanho,
    ordenacao,
    direcao,
  });
  
  const response = await fetch(`${API_BASE_URL}/notas?${params}`, {
    method: 'GET',
    headers: getHeaders(),
  });
  
  return handleResponse(response);
};

/**
 * Lista notas pendentes de emissão
 * @param {number} pagina - Número da página
 * @param {number} tamanho - Tamanho da página
 */
export const listarPendentes = async (pagina = 0, tamanho = 20) => {
  const params = new URLSearchParams({ pagina, tamanho });
  
  const response = await fetch(`${API_BASE_URL}/pendentes?${params}`, {
    method: 'GET',
    headers: getHeaders(),
  });
  
  return handleResponse(response);
};

/**
 * Busca nota fiscal por ID
 * @param {number} id - ID da nota
 */
export const buscarNotaPorId = async (id) => {
  const response = await fetch(`${API_BASE_URL}/notas/${id}`, {
    method: 'GET',
    headers: getHeaders(),
  });
  
  return handleResponse(response);
};

/**
 * Busca notas por período
 * @param {string} dataInicio - Data inicial (YYYY-MM-DD)
 * @param {string} dataFim - Data final (YYYY-MM-DD)
 */
export const buscarNotasPorPeriodo = async (dataInicio, dataFim) => {
  const params = new URLSearchParams({ dataInicio, dataFim });
  
  const response = await fetch(`${API_BASE_URL}/notas/periodo?${params}`, {
    method: 'GET',
    headers: getHeaders(),
  });
  
  return handleResponse(response);
};

/**
 * Busca notas por tomador (busca global)
 * @param {string} termo - Termo de busca
 * @param {number} pagina - Número da página
 * @param {number} tamanho - Tamanho da página
 */
export const buscarNotas = async (termo, pagina = 0, tamanho = 20) => {
  const params = new URLSearchParams({ termo, pagina, tamanho });
  
  const response = await fetch(`${API_BASE_URL}/notas/busca?${params}`, {
    method: 'GET',
    headers: getHeaders(),
  });
  
  return handleResponse(response);
};

/**
 * Cria nova nota fiscal
 * @param {Object} nota - Dados da nota
 */
export const criarNota = async (nota) => {
  const response = await fetch(`${API_BASE_URL}/notas`, {
    method: 'POST',
    headers: getHeaders(),
    body: JSON.stringify(nota),
  });
  
  return handleResponse(response);
};

/**
 * Atualiza nota fiscal existente
 * @param {number} id - ID da nota
 * @param {Object} nota - Dados atualizados
 */
export const atualizarNota = async (id, nota) => {
  const response = await fetch(`${API_BASE_URL}/notas/${id}`, {
    method: 'PUT',
    headers: getHeaders(),
    body: JSON.stringify(nota),
  });
  
  return handleResponse(response);
};

/**
 * Exclui nota fiscal
 * @param {number} id - ID da nota
 */
export const excluirNota = async (id) => {
  const response = await fetch(`${API_BASE_URL}/notas/${id}`, {
    method: 'DELETE',
    headers: getHeaders(),
  });
  
  if (!response.ok) {
    const error = await response.json().catch(() => ({ mensagem: 'Erro desconhecido' }));
    throw new Error(error.mensagem || `HTTP ${response.status}`);
  }
  
  return true;
};

/**
 * Lista itens de uma nota
 * @param {number} notaId - ID da nota
 */
export const listarItens = async (notaId) => {
  const response = await fetch(`${API_BASE_URL}/notas/${notaId}/itens`, {
    method: 'GET',
    headers: getHeaders(),
  });
  
  return handleResponse(response);
};

/**
 * Emite nota fiscal
 * @param {number} id - ID da nota
 */
export const emitirNota = async (id) => {
  const response = await fetch(`${API_BASE_URL}/notas/${id}/emitir`, {
    method: 'POST',
    headers: getHeaders(),
  });
  
  return handleResponse(response);
};

/**
 * Cancela nota fiscal
 * @param {number} id - ID da nota
 * @param {string} motivo - Motivo do cancelamento
 */
export const cancelarNota = async (id, motivo) => {
  const response = await fetch(`${API_BASE_URL}/notas/${id}/cancelar`, {
    method: 'POST',
    headers: getHeaders(),
    body: JSON.stringify({ motivo }),
  });
  
  return handleResponse(response);
};

/**
 * Consulta NFS-e por número
 * @param {string} numero - Número da NFS-e
 * @param {string} cnpjTomador - CNPJ do tomador (opcional)
 */
export const consultarNfse = async (numero, cnpjTomador = null) => {
  const params = new URLSearchParams({ numero });
  if (cnpjTomador) params.append('cnpjTomador', cnpjTomador);
  
  const response = await fetch(`${API_BASE_URL}/nfse/consultar?${params}`, {
    method: 'GET',
    headers: getHeaders(),
  });
  
  return handleResponse(response);
};

/**
 * Autentica usuário e obtém token
 * @param {string} login - Login do usuário
 * @param {string} senha - Senha do usuário
 */
export const login = async (login, senha) => {
  const response = await fetch(`${API_BASE_URL}/auth/login`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ login, senha }),
  });
  
  const data = await handleResponse(response);
  
  if (data.token) {
    localStorage.setItem('nfse_token', data.token);
  }
  
  return data;
};

/**
 * Realiza logout
 */
export const logout = () => {
  localStorage.removeItem('nfse_token');
};

export default {
  listarNotas,
  listarPendentes,
  buscarNotaPorId,
  buscarNotasPorPeriodo,
  buscarNotas,
  criarNota,
  atualizarNota,
  excluirNota,
  listarItens,
  emitirNota,
  cancelarNota,
  consultarNfse,
  login,
  logout,
};