import { useState } from 'react'
import { 
  Search, 
  FileText, 
  Upload, 
  CheckCircle, 
  XCircle, 
  Loader2,
  Link2,
  DollarSign,
  Calendar,
  User,
  Building
} from 'lucide-react'
import { ctp001Api, atendimentosApi } from '../services/api'
import type { DevedorCtp001, VinculoIntimacao } from '../types'
import toast from 'react-hot-toast'
import { format } from 'date-fns'
import { ptBR } from 'date-fns/locale'

interface IntimacaoPanelProps {
  atendimentoId: number
  vinculos: VinculoIntimacao[]
  onVinculoAtualizado: () => void
}

export default function IntimacaoPanel({ atendimentoId, vinculos, onVinculoAtualizado }: IntimacaoPanelProps) {
  const [buscaChaves, setBuscaChaves] = useState({
    numapo1: '',
    numapo2: '',
    controle: ''
  })
  const [devedorEncontrado, setDevedorEncontrado] = useState<DevedorCtp001 | null>(null)
  const [buscando, setBuscando] = useState(false)
  const [vinculando, setVinculando] = useState(false)
  const [uploadingPdf, setUploadingPdf] = useState<number | null>(null)
  const [uploadingComprovante, setUploadingComprovante] = useState<number | null>(null)
  const [showPanel, setShowPanel] = useState(true)

  const buscarDevedor = async () => {
    if (!buscaChaves.numapo1 || !buscaChaves.numapo2 || !buscaChaves.controle) {
      toast.error('Preencha todas as chaves para busca')
      return
    }

    setBuscando(true)
    try {
      const response = await ctp001Api.buscarPorChaves(
        buscaChaves.numapo1,
        buscaChaves.numapo2,
        buscaChaves.controle
      )
      if (response.data) {
        setDevedorEncontrado(response.data)
        toast.success('Devedor encontrado!')
      } else {
        toast.error('Devedor não encontrado')
      }
    } catch (error) {
      console.error('Erro ao buscar devedor:', error)
      toast.error('Erro ao buscar devedor')
    } finally {
      setBuscando(false)
    }
  }

  const vincularIntimacao = async () => {
    if (!devedorEncontrado) return

    setVinculando(true)
    try {
      await atendimentosApi.vincularIntimacao(atendimentoId, {
        numapo1_001: devedorEncontrado.numapo1_001,
        numapo2_001: devedorEncontrado.numapo2_001,
        controle_001: devedorEncontrado.controle_001
      })
      toast.success('Intimação vinculada com sucesso!')
      setDevedorEncontrado(null)
      setBuscaChaves({ numapo1: '', numapo2: '', controle: '' })
      onVinculoAtualizado()
    } catch (error) {
      console.error('Erro ao vincular intimação:', error)
      toast.error('Erro ao vincular intimação')
    } finally {
      setVinculando(false)
    }
  }

  const handleUploadPdf = async (vinculoId: number, file: File) => {
    setUploadingPdf(vinculoId)
    try {
      await atendimentosApi.enviarPdf(atendimentoId, vinculoId, file)
      toast.success('PDF enviado com sucesso!')
      onVinculoAtualizado()
    } catch (error) {
      console.error('Erro ao enviar PDF:', error)
      toast.error('Erro ao enviar PDF')
    } finally {
      setUploadingPdf(null)
    }
  }

  const handleUploadComprovante = async (vinculoId: number, file: File) => {
    setUploadingComprovante(vinculoId)
    try {
      await atendimentosApi.enviarComprovante(atendimentoId, vinculoId, file)
      toast.success('Comprovante enviado com sucesso!')
      onVinculoAtualizado()
    } catch (error) {
      console.error('Erro ao enviar comprovante:', error)
      toast.error('Erro ao enviar comprovante')
    } finally {
      setUploadingComprovante(null)
    }
  }

  const formatarValor = (valor: number) => {
    return new Intl.NumberFormat('pt-BR', {
      style: 'currency',
      currency: 'BRL'
    }).format(valor)
  }

  const formatarCpfCnpj = (cpfCnpj: string) => {
    if (cpfCnpj.length === 11) {
      return cpfCnpj.replace(/(\d{3})(\d{3})(\d{3})(\d{2})/, '$1.$2.$3-$4')
    } else if (cpfCnpj.length === 14) {
      return cpfCnpj.replace(/(\d{2})(\d{3})(\d{3})(\d{4})(\d{2})/, '$1.$2.$3/$4-$5')
    }
    return cpfCnpj
  }

  return (
    <div className="bg-[#111b21] border-l border-[#2a3942] flex flex-col">
      {/* Header */}
      <div className="p-4 bg-[#202c33] border-b border-[#2a3942]">
        <div className="flex items-center justify-between">
          <h2 className="text-lg font-semibold text-white flex items-center gap-2">
            <FileText className="w-5 h-5 text-[#00a884]" />
            Intimações
          </h2>
          <button
            onClick={() => setShowPanel(!showPanel)}
            className="p-2 hover:bg-[#2a3942] rounded-full md:hidden"
          >
            <XCircle className="w-5 h-5 text-[#8696a0]" />
          </button>
        </div>
      </div>

      {showPanel && (
        <div className="flex-1 overflow-y-auto p-4 space-y-4">
          {/* Busca por Chaves */}
          <div className="bg-[#202c33] rounded-lg p-4">
            <h3 className="text-sm font-medium text-[#8696a0] mb-3">Buscar por Chaves CTP001</h3>
            <div className="grid grid-cols-3 gap-2 mb-3">
              <input
                type="text"
                placeholder="Num Apo 1"
                value={buscaChaves.numapo1}
                onChange={(e) => setBuscaChaves({ ...buscaChaves, numapo1: e.target.value })}
                className="px-3 py-2 bg-[#2a3942] rounded text-white text-sm placeholder-[#8696a0] focus:outline-none focus:ring-1 focus:ring-[#00a884]"
              />
              <input
                type="text"
                placeholder="Num Apo 2"
                value={buscaChaves.numapo2}
                onChange={(e) => setBuscaChaves({ ...buscaChaves, numapo2: e.target.value })}
                className="px-3 py-2 bg-[#2a3942] rounded text-white text-sm placeholder-[#8696a0] focus:outline-none focus:ring-1 focus:ring-[#00a884]"
              />
              <input
                type="text"
                placeholder="Controle"
                value={buscaChaves.controle}
                onChange={(e) => setBuscaChaves({ ...buscaChaves, controle: e.target.value })}
                className="px-3 py-2 bg-[#2a3942] rounded text-white text-sm placeholder-[#8696a0] focus:outline-none focus:ring-1 focus:ring-[#00a884]"
              />
            </div>
            <button
              onClick={buscarDevedor}
              disabled={buscando}
              className="w-full py-2 bg-[#00a884] hover:bg-[#008f72] text-white rounded-lg flex items-center justify-center gap-2 disabled:opacity-50"
            >
              {buscando ? (
                <Loader2 className="w-4 h-4 animate-spin" />
              ) : (
                <Search className="w-4 h-4" />
              )}
              Buscar
            </button>
          </div>

          {/* Devedor Encontrado */}
          {devedorEncontrado && (
            <div className="bg-[#202c33] rounded-lg p-4 border border-[#00a884]">
              <h3 className="text-sm font-medium text-[#00a884] mb-3">Devedor Encontrado</h3>
              <div className="space-y-2 text-sm">
                <div className="flex items-center gap-2 text-white">
                  <User className="w-4 h-4 text-[#8696a0]" />
                  <span className="font-medium">{devedorEncontrado.nome_001}</span>
                </div>
                <div className="flex items-center gap-2 text-[#8696a0]">
                  <Building className="w-4 h-4" />
                  <span>{formatarCpfCnpj(devedorEncontrado.cpfCnpj_001)}</span>
                </div>
                <div className="flex items-center gap-2 text-[#8696a0]">
                  <DollarSign className="w-4 h-4" />
                  <span className="text-[#00a884] font-semibold">
                    {formatarValor(devedorEncontrado.valor_001)}
                  </span>
                </div>
                {devedorEncontrado.dataProtesto_001 && (
                  <div className="flex items-center gap-2 text-[#8696a0]">
                    <Calendar className="w-4 h-4" />
                    <span>Protesto: {format(new Date(devedorEncontrado.dataProtesto_001), 'dd/MM/yyyy', { locale: ptBR })}</span>
                  </div>
                )}
                <div className="mt-2">
                  <span className={`inline-flex items-center px-2 py-1 rounded text-xs font-medium ${
                    devedorEncontrado.statusProtesto === 'PAGO' ? 'bg-green-900 text-green-300' :
                    devedorEncontrado.statusProtesto === 'INTIMADO' ? 'bg-blue-900 text-blue-300' :
                    devedorEncontrado.statusProtesto === 'CANCELADO' ? 'bg-red-900 text-red-300' :
                    'bg-yellow-900 text-yellow-300'
                  }`}>
                    {devedorEncontrado.statusProtesto}
                  </span>
                </div>
              </div>
              <button
                onClick={vincularIntimacao}
                disabled={vinculando}
                className="w-full mt-4 py-2 bg-[#00a884] hover:bg-[#008f72] text-white rounded-lg flex items-center justify-center gap-2 disabled:opacity-50"
              >
                {vinculando ? (
                  <Loader2 className="w-4 h-4 animate-spin" />
                ) : (
                  <Link2 className="w-4 h-4" />
                )}
                Vincular ao Atendimento
              </button>
            </div>
          )}

          {/* Lista de Vínculos */}
          <div className="space-y-3">
            <h3 className="text-sm font-medium text-[#8696a0]">
              Vínculos ({vinculos.length})
            </h3>
            
            {vinculos.length === 0 ? (
              <div className="text-center py-8 text-[#8696a0]">
                <FileText className="w-8 h-8 mx-auto mb-2 opacity-50" />
                <p className="text-sm">Nenhuma intimação vinculada</p>
              </div>
            ) : (
              vinculos.map((vinculo) => (
                <div
                  key={vinculo.id}
                  className="bg-[#202c33] rounded-lg p-3 space-y-3"
                >
                  {/* Chaves */}
                  <div className="text-xs text-[#8696a0] font-mono">
                    {vinculo.numapo1_001}-{vinculo.numapo2_001}-{vinculo.controle_001}
                  </div>

                  {/* Status */}
                  <div className="flex gap-2">
                    <span className={`inline-flex items-center gap-1 px-2 py-1 rounded text-xs ${
                      vinculo.pdfEnviado ? 'bg-green-900 text-green-300' : 'bg-yellow-900 text-yellow-300'
                    }`}>
                      {vinculo.pdfEnviado ? (
                        <>
                          <CheckCircle className="w-3 h-3" />
                          PDF Enviado
                        </>
                      ) : (
                        <>
                          <XCircle className="w-3 h-3" />
                          PDF Pendente
                        </>
                      )}
                    </span>
                    <span className={`inline-flex items-center gap-1 px-2 py-1 rounded text-xs ${
                      vinculo.comprovanteRecebido ? 'bg-green-900 text-green-300' : 'bg-yellow-900 text-yellow-300'
                    }`}>
                      {vinculo.comprovanteRecebido ? (
                        <>
                          <CheckCircle className="w-3 h-3" />
                          Comprovante
                        </>
                      ) : (
                        <>
                          <XCircle className="w-3 h-3" />
                          Sem Comprovante
                        </>
                      )}
                    </span>
                  </div>

                  {/* Datas */}
                  {vinculo.dataEnvioPdf && (
                    <div className="text-xs text-[#8696a0]">
                      PDF enviado em: {format(new Date(vinculo.dataEnvioPdf), 'dd/MM/yyyy HH:mm', { locale: ptBR })}
                    </div>
                  )}
                  {vinculo.dataRecebimentoComprovante && (
                    <div className="text-xs text-[#8696a0]">
                      Comprovante em: {format(new Date(vinculo.dataRecebimentoComprovante), 'dd/MM/yyyy HH:mm', { locale: ptBR })}
                    </div>
                  )}

                  {/* Ações */}
                  <div className="flex gap-2">
                    {!vinculo.pdfEnviado && (
                      <label className="flex-1 cursor-pointer">
                        <input
                          type="file"
                          accept=".pdf"
                          className="hidden"
                          onChange={(e) => {
                            const file = e.target.files?.[0]
                            if (file) handleUploadPdf(vinculo.id, file)
                          }}
                          disabled={uploadingPdf === vinculo.id}
                        />
                        <div className={`py-2 rounded-lg flex items-center justify-center gap-2 text-sm ${
                          uploadingPdf === vinculo.id
                            ? 'bg-[#2a3942] text-[#8696a0] cursor-wait'
                            : 'bg-[#00a884] hover:bg-[#008f72] text-white'
                        }`}>
                          {uploadingPdf === vinculo.id ? (
                            <Loader2 className="w-4 h-4 animate-spin" />
                          ) : (
                            <Upload className="w-4 h-4" />
                          )}
                          Enviar PDF
                        </div>
                      </label>
                    )}
                    
                    {!vinculo.comprovanteRecebido && (
                      <label className="flex-1 cursor-pointer">
                        <input
                          type="file"
                          accept=".pdf,.jpg,.jpeg,.png"
                          className="hidden"
                          onChange={(e) => {
                            const file = e.target.files?.[0]
                            if (file) handleUploadComprovante(vinculo.id, file)
                          }}
                          disabled={uploadingComprovante === vinculo.id}
                        />
                        <div className={`py-2 rounded-lg flex items-center justify-center gap-2 text-sm ${
                          uploadingComprovante === vinculo.id
                            ? 'bg-[#2a3942] text-[#8696a0] cursor-wait'
                            : 'bg-blue-600 hover:bg-blue-700 text-white'
                        }`}>
                          {uploadingComprovante === vinculo.id ? (
                            <Loader2 className="w-4 h-4 animate-spin" />
                          ) : (
                            <Upload className="w-4 h-4" />
                          )}
                          Comprovante
                        </div>
                      </label>
                    )}
                  </div>
                </div>
              ))
            )}
          </div>
        </div>
      )}
    </div>
  )
}
