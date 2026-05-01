import { useState } from 'react'
import { 
  Search, 
  User, 
  Phone, 
  Building,
  Loader2,
  Plus,
  X
} from 'lucide-react'
import { ctp001Api, contatosApi } from '../services/api'
import type { DevedorCtp001, Contato } from '../types'
import toast from 'react-hot-toast'

interface ContatoSearchProps {
  onContatoSelecionado: (contato: Contato) => void
  onClose?: () => void
}

export default function ContatoSearch({ onContatoSelecionado, onClose }: ContatoSearchProps) {
  const [tipoBusca, setTipoBusca] = useState<'cpfCnpj' | 'nome' | 'telefone'>('cpfCnpj')
  const [termoBusca, setTermoBusca] = useState('')
  const [buscando, setBuscando] = useState(false)
  const [resultados, setResultados] = useState<DevedorCtp001[] | Contato[]>([])
  const [mostrarFormNovo, setMostrarFormNovo] = useState(false)
  const [novoContato, setNovoContato] = useState({
    nome: '',
    telefone: '',
    email: '',
    cpfCnpj: ''
  })
  const [criando, setCriando] = useState(false)

  const buscar = async () => {
    if (!termoBusca.trim()) {
      toast.error('Digite um termo para busca')
      return
    }

    setBuscando(true)
    setResultados([])

    try {
      let response
      if (tipoBusca === 'cpfCnpj') {
        // Busca na tabela ctp001 por CPF/CNPJ
        response = await ctp001Api.buscarPorCpfCnpj(termoBusca.replace(/\D/g, ''))
        if (response.data) {
          setResultados([response.data])
        }
      } else if (tipoBusca === 'nome') {
        // Busca na tabela ctp001 por nome
        response = await ctp001Api.buscarPorNome(termoBusca)
        setResultados(response.data || [])
      } else {
        // Busca contatos por telefone
        response = await contatosApi.buscarPorTelefone(termoBusca.replace(/\D/g, ''))
        if (response.data) {
          setResultados([response.data])
        }
      }

      if (!response.data || (Array.isArray(response.data) && response.data.length === 0)) {
        toast.error('Nenhum resultado encontrado')
      }
    } catch (error) {
      console.error('Erro na busca:', error)
      toast.error('Erro ao realizar busca')
    } finally {
      setBuscando(false)
    }
  }

  const formatarCpfCnpj = (cpfCnpj: string) => {
    const numeros = cpfCnpj.replace(/\D/g, '')
    if (numeros.length === 11) {
      return numeros.replace(/(\d{3})(\d{3})(\d{3})(\d{2})/, '$1.$2.$3-$4')
    } else if (numeros.length === 14) {
      return numeros.replace(/(\d{2})(\d{3})(\d{3})(\d{4})(\d{2})/, '$1.$2.$3/$4-$5')
    }
    return cpfCnpj
  }

  const formatarTelefone = (telefone: string) => {
    const numeros = telefone.replace(/\D/g, '')
    if (numeros.length === 11) {
      return numeros.replace(/(\d{2})(\d{5})(\d{4})/, '($1) $2-$3')
    } else if (numeros.length === 10) {
      return numeros.replace(/(\d{2})(\d{4})(\d{4})/, '($1) $2-$3')
    }
    return telefone
  }

  const criarContato = async () => {
    if (!novoContato.nome || !novoContato.telefone) {
      toast.error('Nome e telefone são obrigatórios')
      return
    }

    setCriando(true)
    try {
      const response = await contatosApi.criar({
        nome: novoContato.nome,
        telefone: novoContato.telefone.replace(/\D/g, ''),
        email: novoContato.email || undefined,
        cpfCnpj: novoContato.cpfCnpj.replace(/\D/g, '') || undefined
      })
      toast.success('Contato criado com sucesso!')
      onContatoSelecionado(response.data)
      setMostrarFormNovo(false)
      setNovoContato({ nome: '', telefone: '', email: '', cpfCnpj: '' })
    } catch (error) {
      console.error('Erro ao criar contato:', error)
      toast.error('Erro ao criar contato')
    } finally {
      setCriando(false)
    }
  }

  const selecionarDevedor = async (devedor: DevedorCtp001) => {
    // Verificar se já existe contato com este CPF/CNPJ
    try {
      const response = await contatosApi.buscarPorCpfCnpj(devedor.cpfCnpj_001)
      if (response.data) {
        onContatoSelecionado(response.data)
        return
      }
    } catch {
      // Contato não existe, criar novo
    }

    // Preencher formulário com dados do devedor
    setNovoContato({
      nome: devedor.nome_001,
      telefone: '',
      email: '',
      cpfCnpj: devedor.cpfCnpj_001
    })
    setMostrarFormNovo(true)
  }

  return (
    <div className="fixed inset-0 bg-black/50 flex items-center justify-center z-50 p-4">
      <div className="bg-[#111b21] rounded-lg w-full max-w-lg max-h-[90vh] overflow-hidden flex flex-col">
        {/* Header */}
        <div className="p-4 bg-[#202c33] border-b border-[#2a3942] flex items-center justify-between">
          <h2 className="text-lg font-semibold text-white">Buscar Contato</h2>
          {onClose && (
            <button
              onClick={onClose}
              className="p-2 hover:bg-[#2a3942] rounded-full"
            >
              <X className="w-5 h-5 text-[#8696a0]" />
            </button>
          )}
        </div>

        {/* Conteúdo */}
        <div className="flex-1 overflow-y-auto p-4 space-y-4">
          {/* Tipo de Busca */}
          <div className="flex gap-2">
            <button
              onClick={() => setTipoBusca('cpfCnpj')}
              className={`flex-1 py-2 px-3 rounded-lg text-sm font-medium transition-colors ${
                tipoBusca === 'cpfCnpj'
                  ? 'bg-[#00a884] text-white'
                  : 'bg-[#2a3942] text-[#8696a0] hover:bg-[#3a4952]'
              }`}
            >
              <Building className="w-4 h-4 inline mr-1" />
              CPF/CNPJ
            </button>
            <button
              onClick={() => setTipoBusca('nome')}
              className={`flex-1 py-2 px-3 rounded-lg text-sm font-medium transition-colors ${
                tipoBusca === 'nome'
                  ? 'bg-[#00a884] text-white'
                  : 'bg-[#2a3942] text-[#8696a0] hover:bg-[#3a4952]'
              }`}
            >
              <User className="w-4 h-4 inline mr-1" />
              Nome
            </button>
            <button
              onClick={() => setTipoBusca('telefone')}
              className={`flex-1 py-2 px-3 rounded-lg text-sm font-medium transition-colors ${
                tipoBusca === 'telefone'
                  ? 'bg-[#00a884] text-white'
                  : 'bg-[#2a3942] text-[#8696a0] hover:bg-[#3a4952]'
              }`}
            >
              <Phone className="w-4 h-4 inline mr-1" />
              Telefone
            </button>
          </div>

          {/* Campo de Busca */}
          <div className="flex gap-2">
            <div className="flex-1 relative">
              <Search className="absolute left-3 top-1/2 transform -translate-y-1/2 w-5 h-5 text-[#8696a0]" />
              <input
                type="text"
                value={termoBusca}
                onChange={(e) => setTermoBusca(e.target.value)}
                onKeyPress={(e) => e.key === 'Enter' && buscar()}
                placeholder={
                  tipoBusca === 'cpfCnpj' ? 'Digite o CPF ou CNPJ...' :
                  tipoBusca === 'nome' ? 'Digite o nome...' :
                  'Digite o telefone...'
                }
                className="w-full pl-10 pr-4 py-3 bg-[#2a3942] rounded-lg text-white placeholder-[#8696a0] focus:outline-none focus:ring-1 focus:ring-[#00a884]"
              />
            </div>
            <button
              onClick={buscar}
              disabled={buscando}
              className="px-4 bg-[#00a884] hover:bg-[#008f72] text-white rounded-lg disabled:opacity-50"
            >
              {buscando ? (
                <Loader2 className="w-5 h-5 animate-spin" />
              ) : (
                <Search className="w-5 h-5" />
              )}
            </button>
          </div>

          {/* Resultados */}
          {resultados.length > 0 && (
            <div className="space-y-2">
              <h3 className="text-sm font-medium text-[#8696a0]">Resultados</h3>
              {resultados.map((resultado, index) => {
                const isDevedor = 'cpfCnpj_001' in resultado
                return (
                  <button
                    key={index}
                    onClick={() => isDevedor 
                      ? selecionarDevedor(resultado as DevedorCtp001)
                      : onContatoSelecionado(resultado as Contato)
                    }
                    className="w-full p-3 bg-[#202c33] hover:bg-[#2a3942] rounded-lg text-left transition-colors"
                  >
                    <div className="flex items-center gap-3">
                      <div className="w-10 h-10 rounded-full bg-[#00a884] flex items-center justify-center text-white font-semibold">
                        {(isDevedor ? (resultado as DevedorCtp001).nome_001 : (resultado as Contato).nome).charAt(0).toUpperCase()}
                      </div>
                      <div className="flex-1">
                        <p className="text-white font-medium">
                          {isDevedor ? (resultado as DevedorCtp001).nome_001 : (resultado as Contato).nome}
                        </p>
                        <div className="flex items-center gap-2 text-sm text-[#8696a0]">
                          {isDevedor ? (
                            <>
                              <Building className="w-3 h-3" />
                              <span>{formatarCpfCnpj((resultado as DevedorCtp001).cpfCnpj_001)}</span>
                            </>
                          ) : (
                            <>
                              <Phone className="w-3 h-3" />
                              <span>{formatarTelefone((resultado as Contato).telefone)}</span>
                            </>
                          )}
                        </div>
                      </div>
                    </div>
                  </button>
                )
              })}
            </div>
          )}

          {/* Botão Novo Contato */}
          {!mostrarFormNovo && (
            <button
              onClick={() => setMostrarFormNovo(true)}
              className="w-full py-3 border-2 border-dashed border-[#2a3942] rounded-lg text-[#8696a0] hover:border-[#00a884] hover:text-[#00a884] transition-colors flex items-center justify-center gap-2"
            >
              <Plus className="w-5 h-5" />
              Criar Novo Contato
            </button>
          )}

          {/* Formulário Novo Contato */}
          {mostrarFormNovo && (
            <div className="bg-[#202c33] rounded-lg p-4 space-y-3">
              <h3 className="text-sm font-medium text-white">Novo Contato</h3>
              
              <div>
                <label className="block text-xs text-[#8696a0] mb-1">Nome *</label>
                <input
                  type="text"
                  value={novoContato.nome}
                  onChange={(e) => setNovoContato({ ...novoContato, nome: e.target.value })}
                  className="w-full px-3 py-2 bg-[#2a3942] rounded text-white placeholder-[#8696a0] focus:outline-none focus:ring-1 focus:ring-[#00a884]"
                  placeholder="Nome completo"
                />
              </div>

              <div>
                <label className="block text-xs text-[#8696a0] mb-1">Telefone *</label>
                <input
                  type="text"
                  value={novoContato.telefone}
                  onChange={(e) => setNovoContato({ ...novoContato, telefone: e.target.value })}
                  className="w-full px-3 py-2 bg-[#2a3942] rounded text-white placeholder-[#8696a0] focus:outline-none focus:ring-1 focus:ring-[#00a884]"
                  placeholder="(00) 00000-0000"
                />
              </div>

              <div>
                <label className="block text-xs text-[#8696a0] mb-1">E-mail</label>
                <input
                  type="email"
                  value={novoContato.email}
                  onChange={(e) => setNovoContato({ ...novoContato, email: e.target.value })}
                  className="w-full px-3 py-2 bg-[#2a3942] rounded text-white placeholder-[#8696a0] focus:outline-none focus:ring-1 focus:ring-[#00a884]"
                  placeholder="email@exemplo.com"
                />
              </div>

              <div>
                <label className="block text-xs text-[#8696a0] mb-1">CPF/CNPJ</label>
                <input
                  type="text"
                  value={novoContato.cpfCnpj}
                  onChange={(e) => setNovoContato({ ...novoContato, cpfCnpj: e.target.value })}
                  className="w-full px-3 py-2 bg-[#2a3942] rounded text-white placeholder-[#8696a0] focus:outline-none focus:ring-1 focus:ring-[#00a884]"
                  placeholder="000.000.000-00"
                />
              </div>

              <div className="flex gap-2">
                <button
                  onClick={() => setMostrarFormNovo(false)}
                  className="flex-1 py-2 bg-[#2a3942] hover:bg-[#3a4952] text-white rounded-lg transition-colors"
                >
                  Cancelar
                </button>
                <button
                  onClick={criarContato}
                  disabled={criando}
                  className="flex-1 py-2 bg-[#00a884] hover:bg-[#008f72] text-white rounded-lg disabled:opacity-50 transition-colors flex items-center justify-center gap-2"
                >
                  {criando ? (
                    <Loader2 className="w-4 h-4 animate-spin" />
                  ) : (
                    <Plus className="w-4 h-4" />
                  )}
                  Criar
                </button>
              </div>
            </div>
          )}
        </div>
      </div>
    </div>
  )
}
