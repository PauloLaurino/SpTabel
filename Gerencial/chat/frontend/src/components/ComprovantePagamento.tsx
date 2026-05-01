import { useState, useRef } from 'react'
import { 
  Upload, 
  FileText, 
  X,
  Loader2,
  CheckCircle,
  AlertCircle,
  DollarSign,
  Calendar
} from 'lucide-react'
import { atendimentosApi, ctp001Api } from '../services/api'
import type { VinculoIntimacao, DevedorCtp001 } from '../types'
import toast from 'react-hot-toast'
import { format } from 'date-fns'

interface ComprovantePagamentoProps {
  atendimentoId: number
  vinculo: VinculoIntimacao
  devedor?: DevedorCtp001
  onClose: () => void
  onSuccess: () => void
}

export default function ComprovantePagamento({ 
  atendimentoId, 
  vinculo, 
  devedor,
  onClose, 
  onSuccess 
}: ComprovantePagamentoProps) {
  const [arquivo, setArquivo] = useState<File | null>(null)
  const [preview, setPreview] = useState<string | null>(null)
  const [dataPagamento, setDataPagamento] = useState(format(new Date(), 'yyyy-MM-dd'))
  const [valorPago, setValorPago] = useState(devedor?.valor_001?.toString() || '')
  const [observacao, setObservacao] = useState('')
  const [uploading, setUploading] = useState(false)
  const [dragOver, setDragOver] = useState(false)
  const fileInputRef = useRef<HTMLInputElement>(null)

  const handleFileSelect = (file: File) => {
    const tiposPermitidos = ['application/pdf', 'image/jpeg', 'image/jpg', 'image/png']
    
    if (!tiposPermitidos.includes(file.type)) {
      toast.error('Tipo de arquivo não permitido. Use PDF, JPG ou PNG.')
      return
    }

    if (file.size > 10 * 1024 * 1024) {
      toast.error('Arquivo muito grande. Máximo 10MB.')
      return
    }

    setArquivo(file)

    // Criar preview se for imagem
    if (file.type.startsWith('image/')) {
      const reader = new FileReader()
      reader.onload = (e) => {
        setPreview(e.target?.result as string)
      }
      reader.readAsDataURL(file)
    } else {
      setPreview(null)
    }
  }

  const handleDrop = (e: React.DragEvent) => {
    e.preventDefault()
    setDragOver(false)
    
    const file = e.dataTransfer.files[0]
    if (file) {
      handleFileSelect(file)
    }
  }

  const handleDragOver = (e: React.DragEvent) => {
    e.preventDefault()
    setDragOver(true)
  }

  const handleDragLeave = (e: React.DragEvent) => {
    e.preventDefault()
    setDragOver(false)
  }

  const handleUpload = async () => {
    if (!arquivo) {
      toast.error('Selecione um arquivo')
      return
    }

    setUploading(true)
    try {
      // Enviar comprovante
      await atendimentosApi.enviarComprovante(atendimentoId, vinculo.id, arquivo)

      // Atualizar data de pagamento no CTP001
      if (dataPagamento) {
        await ctp001Api.atualizarPagamento(
          vinculo.numapo1_001,
          vinculo.numapo2_001,
          vinculo.controle_001,
          dataPagamento
        )
      }

      toast.success('Comprovante enviado com sucesso!')
      onSuccess()
      onClose()
    } catch (error) {
      console.error('Erro ao enviar comprovante:', error)
      toast.error('Erro ao enviar comprovante')
    } finally {
      setUploading(false)
    }
  }

  const formatarValor = (valor: string) => {
    const numeros = valor.replace(/\D/g, '')
    const valorNumerico = parseFloat(numeros) / 100
    return valorNumerico.toLocaleString('pt-BR', {
      style: 'currency',
      currency: 'BRL'
    })
  }

  const handleValorChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const valor = e.target.value.replace(/\D/g, '')
    setValorPago(valor)
  }

  return (
    <div className="fixed inset-0 bg-black/50 flex items-center justify-center z-50 p-4">
      <div className="bg-[#111b21] rounded-lg w-full max-w-md overflow-hidden">
        {/* Header */}
        <div className="p-4 bg-[#202c33] border-b border-[#2a3942] flex items-center justify-between">
          <h2 className="text-lg font-semibold text-white flex items-center gap-2">
            <DollarSign className="w-5 h-5 text-green-400" />
            Comprovante de Pagamento
          </h2>
          <button
            onClick={onClose}
            className="p-2 hover:bg-[#2a3942] rounded-full"
          >
            <X className="w-5 h-5 text-[#8696a0]" />
          </button>
        </div>

        {/* Conteúdo */}
        <div className="p-4 space-y-4">
          {/* Informações do Vínculo */}
          <div className="bg-[#202c33] rounded-lg p-3">
            <div className="text-xs text-[#8696a0] font-mono mb-2">
              {vinculo.numapo1_001}-{vinculo.numapo2_001}-{vinculo.controle_001}
            </div>
            {devedor && (
              <>
                <div className="text-white font-medium">{devedor.nome_001}</div>
                <div className="text-sm text-[#8696a0]">
                  Valor original: {formatarValor(devedor.valor_001.toString())}
                </div>
              </>
            )}
          </div>

          {/* Data do Pagamento */}
          <div>
            <label className="block text-sm text-[#8696a0] mb-1">
              <Calendar className="w-4 h-4 inline mr-1" />
              Data do Pagamento
            </label>
            <input
              type="date"
              value={dataPagamento}
              onChange={(e) => setDataPagamento(e.target.value)}
              className="w-full px-3 py-2 bg-[#2a3942] rounded text-white focus:outline-none focus:ring-1 focus:ring-[#00a884]"
            />
          </div>

          {/* Valor Pago */}
          <div>
            <label className="block text-sm text-[#8696a0] mb-1">
              <DollarSign className="w-4 h-4 inline mr-1" />
              Valor Pago
            </label>
            <input
              type="text"
              value={valorPago ? formatarValor(valorPago) : ''}
              onChange={handleValorChange}
              placeholder="R$ 0,00"
              className="w-full px-3 py-2 bg-[#2a3942] rounded text-white focus:outline-none focus:ring-1 focus:ring-[#00a884]"
            />
          </div>

          {/* Área de Upload */}
          <div
            onDrop={handleDrop}
            onDragOver={handleDragOver}
            onDragLeave={handleDragLeave}
            onClick={() => fileInputRef.current?.click()}
            className={`border-2 border-dashed rounded-lg p-6 text-center cursor-pointer transition-colors ${
              dragOver
                ? 'border-[#00a884] bg-[#00a884]/10'
                : 'border-[#2a3942] hover:border-[#00a884]'
            }`}
          >
            <input
              ref={fileInputRef}
              type="file"
              accept=".pdf,.jpg,.jpeg,.png"
              className="hidden"
              onChange={(e) => {
                const file = e.target.files?.[0]
                if (file) handleFileSelect(file)
              }}
            />

            {arquivo ? (
              <div className="space-y-2">
                {preview ? (
                  <img
                    src={preview}
                    alt="Preview"
                    className="max-h-32 mx-auto rounded"
                  />
                ) : (
                  <FileText className="w-12 h-12 mx-auto text-[#00a884]" />
                )}
                <p className="text-white text-sm">{arquivo.name}</p>
                <p className="text-[#8696a0] text-xs">
                  {(arquivo.size / 1024).toFixed(1)} KB
                </p>
              </div>
            ) : (
              <div className="space-y-2">
                <Upload className="w-12 h-12 mx-auto text-[#8696a0]" />
                <p className="text-[#8696a0]">
                  Arraste o arquivo ou clique para selecionar
                </p>
                <p className="text-[#8696a0] text-xs">
                  PDF, JPG ou PNG (máx. 10MB)
                </p>
              </div>
            )}
          </div>

          {/* Observação */}
          <div>
            <label className="block text-sm text-[#8696a0] mb-1">
              Observação (opcional)
            </label>
            <textarea
              value={observacao}
              onChange={(e) => setObservacao(e.target.value)}
              rows={2}
              className="w-full px-3 py-2 bg-[#2a3942] rounded text-white placeholder-[#8696a0] focus:outline-none focus:ring-1 focus:ring-[#00a884] resize-none"
              placeholder="Adicione uma observação..."
            />
          </div>

          {/* Status do Vínculo */}
          <div className="flex gap-2">
            <div className={`flex items-center gap-1 px-2 py-1 rounded text-xs ${
              vinculo.pdfEnviado ? 'bg-green-900 text-green-300' : 'bg-yellow-900 text-yellow-300'
            }`}>
              {vinculo.pdfEnviado ? (
                <CheckCircle className="w-3 h-3" />
              ) : (
                <AlertCircle className="w-3 h-3" />
              )}
              PDF
            </div>
            <div className={`flex items-center gap-1 px-2 py-1 rounded text-xs ${
              vinculo.comprovanteRecebido ? 'bg-green-900 text-green-300' : 'bg-yellow-900 text-yellow-300'
            }`}>
              {vinculo.comprovanteRecebido ? (
                <CheckCircle className="w-3 h-3" />
              ) : (
                <AlertCircle className="w-3 h-3" />
              )}
              Comprovante
            </div>
          </div>

          {/* Botões */}
          <div className="flex gap-2">
            <button
              onClick={onClose}
              className="flex-1 py-3 bg-[#2a3942] hover:bg-[#3a4952] text-white rounded-lg transition-colors"
            >
              Cancelar
            </button>
            <button
              onClick={handleUpload}
              disabled={!arquivo || uploading}
              className="flex-1 py-3 bg-green-600 hover:bg-green-700 text-white rounded-lg disabled:opacity-50 transition-colors flex items-center justify-center gap-2"
            >
              {uploading ? (
                <Loader2 className="w-5 h-5 animate-spin" />
              ) : (
                <Upload className="w-5 h-5" />
              )}
              Enviar
            </button>
          </div>
        </div>
      </div>
    </div>
  )
}
