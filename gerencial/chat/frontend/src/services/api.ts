import axios from 'axios'
import { useAuthStore } from '../stores/authStore'

const api = axios.create({
  baseURL: '/chat/api',
  headers: {
    'Content-Type': 'application/json',
  },
})

// Interceptor para adicionar token JWT
api.interceptors.request.use(
  (config) => {
    const token = useAuthStore.getState().token
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }
    return config
  },
  (error) => {
    return Promise.reject(error)
  }
)

// Interceptor para tratar erros de autenticação
api.interceptors.response.use(
  (response) => response,
  async (error) => {
    const originalRequest = error.config

    // Se erro 401 e não é uma tentativa de refresh
    if (error.response?.status === 401 && !originalRequest._retry) {
      originalRequest._retry = true

      try {
        const refreshToken = useAuthStore.getState().refreshToken
        if (refreshToken) {
          const response = await axios.post('/api/auth/refresh', null, {
            headers: {
              'Refresh-Token': refreshToken,
            },
          })

          const { accessToken, refreshToken: newRefreshToken } = response.data
          
          // Atualiza o token no store
          useAuthStore.getState().setAuth({
            ...response.data,
            accessToken,
            refreshToken: newRefreshToken,
          })

          // Repete a requisição original com o novo token
          originalRequest.headers.Authorization = `Bearer ${accessToken}`
          return api(originalRequest)
        }
      } catch (refreshError) {
        // Se falhar o refresh, faz logout
        useAuthStore.getState().logout()
        window.location.href = '/chat/login'
        return Promise.reject(refreshError)
      }
    }

    return Promise.reject(error)
  }
)

export default api

// Auth API
export const authApi = {
  login: (login: string, senha: string) =>
    api.post('/auth/login', { login, senha }),
  
  refresh: (refreshToken: string) =>
    api.post('/auth/refresh', null, {
      headers: { 'Refresh-Token': refreshToken },
    }),
  
  validate: () => api.get('/auth/validate'),
  
  logout: () => api.post('/auth/logout'),
}

// Atendimentos API
export const atendimentosApi = {
  listar: (operadorId: number, page = 0, size = 20) =>
    api.get(`/atendimentos/operador/${operadorId}`, {
      params: { page, size },
    }),
  
  buscar: (id: number) =>
    api.get(`/atendimentos/${id}`),
  
  criar: (contatoId: number, operadorId: number) =>
    api.post('/atendimentos', null, {
      params: { contatoId, operadorId },
    }),
  
  atualizarStatus: (id: number, status: string) =>
    api.patch(`/atendimentos/${id}/status`, null, {
      params: { status },
    }),
  
  listarMensagens: (id: number) =>
    api.get(`/atendimentos/${id}/mensagens`),
  
  enviarMensagem: (id: number, data: { tipo: string; conteudo: string; operador: boolean }) =>
    api.post(`/atendimentos/${id}/mensagens`, data),
  
  vincularIntimacao: (id: number, data: { numapo1_001: string; numapo2_001: string; controle_001: string }) =>
    api.post(`/atendimentos/${id}/vinculos`, data),
  
  enviarPdf: (id: number, vinculoId: number, arquivo: File) => {
    const formData = new FormData()
    formData.append('arquivo', arquivo)
    return api.post(`/atendimentos/${id}/vinculos/${vinculoId}/pdf`, formData, {
      headers: { 'Content-Type': 'multipart/form-data' },
    })
  },
  
  enviarComprovante: (id: number, vinculoId: number, arquivo: File) => {
    const formData = new FormData()
    formData.append('arquivo', arquivo)
    return api.post(`/atendimentos/${id}/vinculos/${vinculoId}/comprovante`, formData, {
      headers: { 'Content-Type': 'multipart/form-data' },
    })
  },
}

// CTP001 API - Integração com Sistema de Protesto
export const ctp001Api = {
  buscarPorChaves: (numapo1: string, numapo2: string, controle: string) =>
    api.get(`/ctp001/buscar`, {
      params: { numapo1_001: numapo1, numapo2_001: numapo2, controle_001: controle },
    }),
  
  buscarPorCpfCnpj: (cpfCnpj: string) =>
    api.get(`/ctp001/cpfCnpj/${cpfCnpj}`),
  
  buscarPorNome: (nome: string) =>
    api.get(`/ctp001/nome`, {
      params: { nome },
    }),
  
  sincronizar: () =>
    api.post(`/ctp001/sincronizar`),
  
  atualizarIntimacao: (numapo1: string, numapo2: string, controle: string, dataIntimacao: string) =>
    api.patch(`/ctp001/intimacao`, null, {
      params: { numapo1_001: numapo1, numapo2_001: numapo2, controle_001: controle, dataIntimacao },
    }),
  
  atualizarPagamento: (numapo1: string, numapo2: string, controle: string, dataPagamento: string) =>
    api.patch(`/ctp001/pagamento`, null, {
      params: { numapo1_001: numapo1, numapo2_001: numapo2, controle_001: controle, dataPagamento },
    }),
}

// Contatos API
export const contatosApi = {
  listar: (page = 0, size = 20) =>
    api.get(`/contatos`, {
      params: { page, size },
    }),
  
  buscar: (id: number) =>
    api.get(`/contatos/${id}`),
  
  criar: (data: { nome: string; telefone: string; email?: string; cpfCnpj?: string }) =>
    api.post(`/contatos`, data),
  
  atualizar: (id: number, data: { nome: string; telefone: string; email?: string; cpfCnpj?: string }) =>
    api.put(`/contatos/${id}`, data),
  
  buscarPorTelefone: (telefone: string) =>
    api.get(`/contatos/telefone/${telefone}`),
  
  buscarPorCpfCnpj: (cpfCnpj: string) =>
    api.get(`/contatos/cpfCnpj/${cpfCnpj}`),
}
