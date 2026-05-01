import { useState, useEffect, useRef } from 'react'
import { useParams } from 'react-router-dom'
import { useAuthStore } from '../stores/authStore'
import { atendimentosApi } from '../services/api'
import type { Atendimento, Mensagem } from '../types'
import { format } from 'date-fns'
import { ptBR } from 'date-fns/locale'
import toast from 'react-hot-toast'
import { 
  Send, 
  Paperclip, 
  MoreVertical, 
  Search, 
  ArrowLeft,
  Check,
  CheckCheck,
  FileText,
  Image as ImageIcon,
  Loader2,
  PanelRightOpen,
  PanelRightClose,
  UserPlus,
  FileCheck
} from 'lucide-react'
import IntimacaoPanel from '../components/IntimacaoPanel'
import ContatoSearch from '../components/ContatoSearch'

export default function Chat() {
  const { id } = useParams()
  const { user } = useAuthStore()
  const [atendimentos, setAtendimentos] = useState<Atendimento[]>([])
  const [atendimentoAtivo, setAtendimentoAtivo] = useState<Atendimento | null>(null)
  const [mensagens, setMensagens] = useState<Mensagem[]>([])
  const [novaMensagem, setNovaMensagem] = useState('')
  const [loading, setLoading] = useState(true)
  const [sending, setSending] = useState(false)
  const [searchTerm, setSearchTerm] = useState('')
  const [showIntimacaoPanel, setShowIntimacaoPanel] = useState(false)
  const [showContatoSearch, setShowContatoSearch] = useState(false)
  const messagesEndRef = useRef<HTMLDivElement>(null)
  const wsRef = useRef<WebSocket | null>(null)

  // Carregar atendimentos
  useEffect(() => {
    carregarAtendimentos()
  }, [user])

  // Carregar mensagens quando selecionar atendimento
  useEffect(() => {
    if (id) {
      carregarMensagens(parseInt(id))
      carregarAtendimento(parseInt(id))
      connectWebSocket(parseInt(id))
    }
    
    return () => {
      disconnectWebSocket()
    }
  }, [id])

  // Auto-scroll para última mensagem
  useEffect(() => {
    scrollToBottom()
  }, [mensagens])

  const carregarAtendimentos = async () => {
    if (!user) return
    
    try {
      const response = await atendimentosApi.listar(user.id)
      setAtendimentos(response.data.content || [])
    } catch (error) {
      console.error('Erro ao carregar atendimentos:', error)
      toast.error('Erro ao carregar atendimentos')
    } finally {
      setLoading(false)
    }
  }

  const carregarAtendimento = async (atendimentoId: number) => {
    try {
      const response = await atendimentosApi.buscar(atendimentoId)
      setAtendimentoAtivo(response.data)
    } catch (error) {
      console.error('Erro ao carregar atendimento:', error)
    }
  }

  const carregarMensagens = async (atendimentoId: number) => {
    try {
      const response = await atendimentosApi.listarMensagens(atendimentoId)
      setMensagens(response.data || [])
    } catch (error) {
      console.error('Erro ao carregar mensagens:', error)
    }
  }

  const connectWebSocket = (atendimentoId: number) => {
    const wsUrl = `${window.location.protocol === 'https:' ? 'wss:' : 'ws:'}//${window.location.host}/chat/ws/chat`
    wsRef.current = new WebSocket(wsUrl)

    wsRef.current.onopen = () => {
      console.log('WebSocket conectado')
      wsRef.current?.send(JSON.stringify({
        type: 'JOIN',
        atendimentoId,
      }))
    }

    wsRef.current.onmessage = (event) => {
      const data = JSON.parse(event.data)
      
      if (data.type === 'NEW_MESSAGE' && data.message) {
        setMensagens(prev => [...prev, data.message])
      }
    }

    wsRef.current.onerror = (error) => {
      console.error('WebSocket erro:', error)
    }

    wsRef.current.onclose = () => {
      console.log('WebSocket desconectado')
    }
  }

  const disconnectWebSocket = () => {
    if (wsRef.current) {
      wsRef.current.close()
      wsRef.current = null
    }
  }

  const scrollToBottom = () => {
    messagesEndRef.current?.scrollIntoView({ behavior: 'smooth' })
  }

  const enviarMensagem = async () => {
    if (!novaMensagem.trim() || !atendimentoAtivo || sending) return

    setSending(true)
    try {
      await atendimentosApi.enviarMensagem(atendimentoAtivo.id, {
        tipo: 'TEXTO',
        conteudo: novaMensagem,
        operador: true,
      })
      setNovaMensagem('')
    } catch (error) {
      console.error('Erro ao enviar mensagem:', error)
      toast.error('Erro ao enviar mensagem')
    } finally {
      setSending(false)
    }
  }

  const handleKeyPress = (e: React.KeyboardEvent) => {
    if (e.key === 'Enter' && !e.shiftKey) {
      e.preventDefault()
      enviarMensagem()
    }
  }

  const formatarHora = (data: string) => {
    return format(new Date(data), 'HH:mm', { locale: ptBR })
  }

  const handleContatoSelecionado = async (contato: { id: number; nome: string; telefone: string }) => {
    try {
      const response = await atendimentosApi.criar(contato.id, user!.id)
      toast.success('Atendimento criado com sucesso!')
      setShowContatoSearch(false)
      window.location.href = `/chat/${response.data.id}`
    } catch (error) {
      console.error('Erro ao criar atendimento:', error)
      toast.error('Erro ao criar atendimento')
    }
  }

  const handleVinculoAtualizado = () => {
    if (id) {
      carregarAtendimento(parseInt(id))
    }
  }

  const atendimentosFiltrados = atendimentos.filter(a => 
    a.contatoNome.toLowerCase().includes(searchTerm.toLowerCase()) ||
    a.contatoTelefone.includes(searchTerm)
  )

  if (loading) {
    return (
      <div className="flex items-center justify-center h-screen bg-[#0b141a]">
        <Loader2 className="w-8 h-8 animate-spin text-[#00a884]" />
      </div>
    )
  }

  return (
    <div className="flex h-screen bg-[#0b141a]">
      {/* Sidebar - Lista de Atendimentos */}
      <div className={`w-full md:w-96 bg-[#111b21] flex flex-col border-r border-[#2a3942] ${atendimentoAtivo ? 'hidden md:flex' : 'flex'}`}>
        {/* Header */}
        <div className="p-4 bg-[#202c33]">
          <div className="flex items-center justify-between mb-4">
            <h1 className="text-xl font-semibold text-white">Chat</h1>
            <div className="flex items-center gap-2">
              <button 
                onClick={() => setShowContatoSearch(true)}
                className="p-2 hover:bg-[#2a3942] rounded-full"
                title="Novo Atendimento"
              >
                <UserPlus className="w-5 h-5 text-[#8696a0]" />
              </button>
              <button className="p-2 hover:bg-[#2a3942] rounded-full">
                <MoreVertical className="w-5 h-5 text-[#8696a0]" />
              </button>
            </div>
          </div>
          
          {/* Search */}
          <div className="relative">
            <Search className="absolute left-3 top-1/2 transform -translate-y-1/2 w-5 h-5 text-[#8696a0]" />
            <input
              type="text"
              placeholder="Pesquisar..."
              value={searchTerm}
              onChange={(e) => setSearchTerm(e.target.value)}
              className="w-full pl-10 pr-4 py-2 bg-[#2a3942] rounded-lg text-white placeholder-[#8696a0] focus:outline-none"
            />
          </div>
        </div>

        {/* Lista de atendimentos */}
        <div className="flex-1 overflow-y-auto">
          {atendimentosFiltrados.map((atendimento) => (
            <button
              key={atendimento.id}
              onClick={() => window.location.href = `/chat/${atendimento.id}`}
              className={`w-full p-4 flex items-center gap-3 hover:bg-[#202c33] border-b border-[#2a3942] ${
                atendimentoAtivo?.id === atendimento.id ? 'bg-[#202c33]' : ''
              }`}
            >
              {/* Avatar */}
              <div className="w-12 h-12 rounded-full bg-[#00a884] flex items-center justify-center text-white font-semibold">
                {atendimento.contatoNome.charAt(0).toUpperCase()}
              </div>

              {/* Info */}
              <div className="flex-1 text-left">
                <div className="flex justify-between items-center">
                  <h3 className="text-white font-medium">{atendimento.contatoNome}</h3>
                  {atendimento.dataUltimaMensagem && (
                    <span className="text-xs text-[#8696a0]">
                      {formatarHora(atendimento.dataUltimaMensagem)}
                    </span>
                  )}
                </div>
                <div className="flex justify-between items-center">
                  <p className="text-sm text-[#8696a0] truncate">
                    {atendimento.contatoTelefone}
                  </p>
                  <div className="flex items-center gap-1">
                    {atendimento.vinculos && atendimento.vinculos.length > 0 && (
                      <span className="text-xs text-[#00a884] flex items-center gap-1">
                        <FileCheck className="w-3 h-3" />
                        {atendimento.vinculos.length}
                      </span>
                    )}
                    <span className={`status-badge status-${atendimento.status.toLowerCase()}`}>
                      {atendimento.status}
                    </span>
                  </div>
                </div>
              </div>
            </button>
          ))}

          {atendimentosFiltrados.length === 0 && (
            <div className="p-8 text-center text-[#8696a0]">
              Nenhum atendimento encontrado
            </div>
          )}
        </div>
      </div>

      {/* Área do Chat */}
      {atendimentoAtivo ? (
        <div className="flex-1 flex flex-col md:flex-row">
          {/* Chat Principal */}
          <div className="flex-1 flex flex-col">
            {/* Header do Chat */}
            <div className="p-4 bg-[#202c33] flex items-center gap-3 border-b border-[#2a3942]">
              <button 
                onClick={() => window.location.href = '/'}
                className="md:hidden p-2 hover:bg-[#2a3942] rounded-full"
              >
                <ArrowLeft className="w-5 h-5 text-[#8696a0]" />
              </button>
              
              <div className="w-10 h-10 rounded-full bg-[#00a884] flex items-center justify-center text-white font-semibold">
                {atendimentoAtivo.contatoNome.charAt(0).toUpperCase()}
              </div>
              
              <div className="flex-1">
                <h2 className="text-white font-medium">{atendimentoAtivo.contatoNome}</h2>
                <p className="text-sm text-[#8696a0]">{atendimentoAtivo.contatoTelefone}</p>
              </div>

              {/* Botão do Painel de Intimações */}
              <button 
                onClick={() => setShowIntimacaoPanel(!showIntimacaoPanel)}
                className={`p-2 rounded-full ${showIntimacaoPanel ? 'bg-[#00a884] text-white' : 'hover:bg-[#2a3942] text-[#8696a0]'}`}
                title="Painel de Intimações"
              >
                {showIntimacaoPanel ? (
                  <PanelRightClose className="w-5 h-5" />
                ) : (
                  <PanelRightOpen className="w-5 h-5" />
                )}
              </button>

              <button className="p-2 hover:bg-[#2a3942] rounded-full">
                <MoreVertical className="w-5 h-5 text-[#8696a0]" />
              </button>
            </div>

            {/* Mensagens */}
            <div className="flex-1 overflow-y-auto p-4 space-y-2 bg-[#0b141a]">
              {/* Background pattern */}
              <div className="absolute inset-0 opacity-5 pointer-events-none" 
                   style={{ backgroundImage: 'url("data:image/svg+xml,%3Csvg width=\'60\' height=\'60\' viewBox=\'0 0 60 60\' xmlns=\'http://www.w3.org/2000/svg\'%3E%3Cg fill=\'none\' fill-rule=\'evenodd\'%3E%3Cg fill=\'%23ffffff\' fill-opacity=\'0.4\'%3E%3Cpath d=\'M36 34v-4h-2v4h-4v2h4v4h2v-4h4v-2h-4zm0-30V0h-2v4h-4v2h4v4h2V6h4V4h-4zM6 34v-4H4v4H0v2h4v4h2v-4h4v-2H6zM6 4V0H4v4H0v2h4v4h2V6h4V4H6z\'/%3E%3C/g%3E%3C/g%3E%3C/svg%3E")' }} 
              />

              {mensagens.map((mensagem) => (
                <div
                  key={mensagem.id}
                  className={`flex ${mensagem.remetente === 'OPERADOR' ? 'justify-end' : 'justify-start'} animate-fadeIn`}
                >
                  <div
                    className={`max-w-[70%] px-3 py-2 ${
                      mensagem.remetente === 'OPERADOR' ? 'chat-bubble-out' : 'chat-bubble-in'
                    }`}
                  >
                    {mensagem.tipo === 'TEXTO' ? (
                      <p className="text-white whitespace-pre-wrap">{mensagem.conteudo}</p>
                    ) : mensagem.tipo === 'DOCUMENTO' ? (
                      <div className="flex items-center gap-2">
                        <FileText className="w-8 h-8 text-[#8696a0]" />
                        <div>
                          <p className="text-white text-sm">{mensagem.arquivoNome}</p>
                          <p className="text-[#8696a0] text-xs">
                            {mensagem.arquivoTamanho && `${(mensagem.arquivoTamanho / 1024).toFixed(1)} KB`}
                          </p>
                        </div>
                      </div>
                    ) : mensagem.tipo === 'IMAGEM' ? (
                      <div className="flex items-center gap-2">
                        <ImageIcon className="w-8 h-8 text-[#8696a0]" />
                        <p className="text-white text-sm">{mensagem.arquivoNome}</p>
                      </div>
                    ) : null}

                    <div className="flex items-center justify-end gap-1 mt-1">
                      <span className="text-xs text-[#8696a0]">
                        {formatarHora(mensagem.dataEnvio)}
                      </span>
                      {mensagem.remetente === 'OPERADOR' && (
                        mensagem.statusEntrega === 'LIDO' ? (
                          <CheckCheck className="w-4 h-4 text-[#53bdeb]" />
                        ) : mensagem.statusEntrega === 'ENTREGUE' ? (
                          <CheckCheck className="w-4 h-4 text-[#8696a0]" />
                        ) : (
                          <Check className="w-4 h-4 text-[#8696a0]" />
                        )
                      )}
                    </div>
                  </div>
                </div>
              ))}
              <div ref={messagesEndRef} />
            </div>

            {/* Input de mensagem */}
            <div className="p-4 bg-[#202c33]">
              <div className="flex items-center gap-2">
                <button className="p-2 hover:bg-[#2a3942] rounded-full">
                  <Paperclip className="w-6 h-6 text-[#8696a0]" />
                </button>

                <input
                  type="text"
                  value={novaMensagem}
                  onChange={(e) => setNovaMensagem(e.target.value)}
                  onKeyPress={handleKeyPress}
                  placeholder="Digite uma mensagem"
                  className="flex-1 px-4 py-3 bg-[#2a3942] rounded-lg text-white placeholder-[#8696a0] focus:outline-none"
                  disabled={sending}
                />

                <button
                  onClick={enviarMensagem}
                  disabled={!novaMensagem.trim() || sending}
                  className="p-3 bg-[#00a884] rounded-full hover:bg-[#008f72] transition-colors disabled:opacity-50 disabled:cursor-not-allowed"
                >
                  {sending ? (
                    <Loader2 className="w-5 h-5 text-white animate-spin" />
                  ) : (
                    <Send className="w-5 h-5 text-white" />
                  )}
                </button>
              </div>
            </div>
          </div>

          {/* Painel de Intimações */}
          {showIntimacaoPanel && (
            <div className="w-full md:w-80 border-l border-[#2a3942]">
              <IntimacaoPanel
                atendimentoId={atendimentoAtivo.id}
                vinculos={atendimentoAtivo.vinculos || []}
                onVinculoAtualizado={handleVinculoAtualizado}
              />
            </div>
          )}
        </div>
      ) : (
        <div className="flex-1 flex items-center justify-center bg-[#0b141a]">
          <div className="text-center">
            <div className="w-24 h-24 mx-auto mb-4 bg-[#2a3942] rounded-full flex items-center justify-center">
              <svg className="w-12 h-12 text-[#8696a0]" fill="currentColor" viewBox="0 0 24 24">
                <path d="M17.472 14.382c-.297-.149-1.758-.867-2.03-.967-.273-.099-.471-.148-.67.15-.197.297-.767.966-.94 1.164-.173.199-.347.223-.644.075-.297-.15-1.255-.463-2.39-1.475-.883-.788-1.48-1.761-1.653-2.059-.173-.297-.018-.458.13-.606.134-.133.298-.347.446-.52.149-.174.198-.298.298-.497.099-.198.05-.371-.025-.52-.075-.149-.669-1.612-.916-2.207-.242-.579-.487-.5-.669-.51-.173-.008-.371-.01-.57-.01-.198 0-.52.074-.792.372-.272.297-1.04 1.016-1.04 2.479 0 1.462 1.065 2.875 1.213 3.074.149.198 2.096 3.2 5.077 4.487.709.306 1.262.489 1.694.625.712.227 1.36.195 1.871.118.571-.085 1.758-.719 2.006-1.413.248-.694.248-1.289.173-1.413-.074-.124-.272-.198-.57-.347m-5.421 7.403h-.004a9.87 9.87 0 01-5.031-1.378l-.361-.214-3.741.982.998-3.648-.235-.374a9.86 9.86 0 01-1.51-5.26c.001-5.45 4.436-9.884 9.888-9.884 2.64 0 5.122 1.03 6.988 2.898a9.825 9.825 0 012.893 6.994c-.003 5.45-4.437 9.884-9.885 9.884m8.413-18.297A11.815 11.815 0 0012.05 0C5.495 0 .16 5.335.157 11.892c0 2.096.547 4.142 1.588 5.945L.057 24l6.305-1.654a11.882 11.882 0 005.683 1.448h.005c6.554 0 11.89-5.335 11.893-11.893a11.821 11.821 0 00-3.48-8.413z" />
              </svg>
            </div>
            <h2 className="text-xl text-white font-medium mb-2">Chat WhatsApp</h2>
            <p className="text-[#8696a0]">Selecione um atendimento para iniciar a conversa</p>
          </div>
        </div>
      )}

      {/* Modal de Busca de Contato */}
      {showContatoSearch && (
        <ContatoSearch
          onContatoSelecionado={handleContatoSelecionado}
          onClose={() => setShowContatoSearch(false)}
        />
      )}
    </div>
  )
}
