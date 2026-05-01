import { useState } from 'react';
import { assinaturaService } from '../services/assinaturaApi';

interface FileInfo {
  nome: string;
  caminho: string;
  tamanho: number;
  dataModificacao: string;
  selecionado: boolean;
  status?: 'pendente' | 'processando' | 'sucesso' | 'erro';
  mensagem?: string;
}

export default function AssinarLote() {
  const [pasta, setPasta] = useState('');
  const [arquivos, setArquivos] = useState<FileInfo[]>([]);
  const [loading, setLoading] = useState(false);
  const [processing, setProcessing] = useState(false);
  const [progress, setProgress] = useState(0);
  
  // Opções
  const [pin, setPin] = useState('');
  const [pdfa, setPdfa] = useState(false);
  const [unirPdfs, setUnirPdfs] = useState(false);
  const [nomeSaida, setNomeSaida] = useState('documentos_assinados.pdf');
  const timestamp = true;

  const listarArquivos = async () => {
    if (!pasta) return;
    setLoading(true);
    try {
      const response = await assinaturaService.listarArquivos(pasta);
      if (response.status === 'ok') {
        setArquivos(response.arquivos.map(a => ({ ...a, selecionado: true, status: 'pendente' })));
      }
    } catch (err: any) {
      alert('Erro ao listar arquivos: ' + err.message);
    } finally {
      setLoading(false);
    }
  };

  const toggleSelectAll = (val: boolean) => {
    setArquivos(prev => prev.map(a => ({ ...a, selecionado: val })));
  };

  const toggleFile = (index: number) => {
    setArquivos(prev => {
      const next = [...prev];
      next[index].selecionado = !next[index].selecionado;
      return next;
    });
  };

  const formatSize = (bytes: number) => {
    if (bytes === 0) return '0 Bytes';
    const k = 1024;
    const sizes = ['Bytes', 'KB', 'MB', 'GB'];
    const i = Math.floor(Math.log(bytes) / Math.log(k));
    return parseFloat((bytes / Math.pow(k, i)).toFixed(2)) + ' ' + sizes[i];
  };

  const processarLote = async () => {
    const selecionados = arquivos.filter(a => a.selecionado);
    if (selecionados.length === 0) return;

    setProcessing(true);
    setProgress(0);

    const pastaSaida = pasta + '/assinados_' + Date.now();
    
    for (let i = 0; i < selecionados.length; i++) {
      const file = selecionados[i];
      const fileIdx = arquivos.findIndex(a => a.caminho === file.caminho);
      
      // Atualiza status para processando
      setArquivos(prev => {
        const next = [...prev];
        next[fileIdx].status = 'processando';
        return next;
      });

      try {
        await assinaturaService.assinarPorCaminho({
          caminho: file.caminho,
          saida: pastaSaida + '/' + file.nome,
          pin: pin || undefined,
          pdfa: pdfa,
          timestamp: timestamp
        });

        setArquivos(prev => {
          const next = [...prev];
          next[fileIdx].status = 'sucesso';
          return next;
        });
      } catch (err: any) {
        setArquivos(prev => {
          const next = [...prev];
          next[fileIdx].status = 'erro';
          next[fileIdx].mensagem = err.message;
          return next;
        });
      }

      setProgress(Math.round(((i + 1) / selecionados.length) * 100));
    }

    if (unirPdfs) {
      try {
        await assinaturaService.agruparPdfs({
          pasta: pastaSaida,
          saida: pasta + '/' + nomeSaida,
          prefixo: ''
        });
        alert('Arquivos unidos com sucesso em: ' + pasta + '/' + nomeSaida);
      } catch (err: any) {
        alert('Erro ao unir PDFs: ' + err.message);
      }
    }

    setProcessing(false);
  };

  return (
    <div className="space-y-6 animate-in fade-in slide-in-from-bottom-4 duration-500">
      <header className="flex items-center justify-between">
        <div>
          <h2 className="text-2xl font-bold text-white">Assinatura em Lote</h2>
          <p className="text-slate-400 text-sm">Gerencie múltiplos documentos de uma só vez.</p>
        </div>
        <div className="flex gap-2">
          <button 
            onClick={listarArquivos}
            disabled={loading || !pasta}
            className="px-4 py-2 bg-blue-600 hover:bg-blue-500 disabled:bg-slate-800 disabled:text-slate-600 text-white rounded-xl text-sm font-semibold transition-all shadow-lg shadow-blue-600/20"
          >
            {loading ? 'Lendo...' : 'Listar Arquivos'}
          </button>
        </div>
      </header>

      {/* Path Input Area */}
      <div className="p-4 bg-slate-800/40 border border-slate-700/50 rounded-2xl">
        <label className="block text-[10px] uppercase tracking-widest text-slate-500 font-bold mb-2">Caminho da Pasta</label>
        <input 
          type="text" 
          value={pasta}
          onChange={(e) => setPasta(e.target.value)}
          placeholder="Ex: C:\Documentos\ParaAssinar"
          className="w-full bg-slate-900/60 border border-slate-700/50 rounded-xl px-4 py-3 text-sm text-slate-200 placeholder:text-slate-600 focus:ring-2 focus:ring-blue-500/50 outline-none transition-all"
        />
      </div>

      <div className="grid grid-cols-12 gap-6">
        {/* File Table Area */}
        <div className="col-span-8 flex flex-col min-h-[400px]">
          <div className="flex-1 bg-slate-900/60 border border-slate-800/60 rounded-3xl overflow-hidden flex flex-col">
            <div className="px-6 py-4 border-b border-slate-800/60 bg-slate-800/20 flex items-center justify-between">
              <div className="flex items-center gap-4">
                <input 
                  type="checkbox" 
                  checked={arquivos.length > 0 && arquivos.every(a => a.selecionado)}
                  onChange={(e) => toggleSelectAll(e.target.checked)}
                  className="w-4 h-4 rounded border-slate-700 bg-slate-800 text-blue-600 focus:ring-blue-500/50 focus:ring-offset-0"
                />
                <span className="text-xs font-bold text-slate-400 uppercase tracking-wider">Documento</span>
              </div>
              <span className="text-xs font-bold text-slate-400 uppercase tracking-wider">Tamanho</span>
            </div>
            
            <div className="flex-1 overflow-y-auto max-h-[400px]">
              {arquivos.length === 0 ? (
                <div className="h-full flex flex-col items-center justify-center text-slate-600 space-y-3 p-12">
                  <span className="text-4xl">📂</span>
                  <p className="text-sm font-medium">Nenhum arquivo listado. Informe o caminho e clique em Listar.</p>
                </div>
              ) : (
                <div className="divide-y divide-slate-800/40">
                  {arquivos.map((file, idx) => (
                    <div 
                      key={idx} 
                      className={`group flex items-center justify-between px-6 py-3 hover:bg-blue-600/5 transition-colors ${file.selecionado ? 'bg-blue-600/5' : ''}`}
                    >
                      <div className="flex items-center gap-4 min-w-0">
                        <input 
                          type="checkbox" 
                          checked={file.selecionado}
                          onChange={() => toggleFile(idx)}
                          className="w-4 h-4 rounded border-slate-700 bg-slate-800 text-blue-600 focus:ring-blue-500/50 focus:ring-offset-0"
                        />
                        <div className="min-w-0">
                          <p className="text-sm font-medium text-slate-200 truncate">{file.nome}</p>
                          <p className="text-[10px] text-slate-500 truncate">{file.caminho}</p>
                        </div>
                      </div>
                      <div className="flex items-center gap-4 shrink-0">
                        <span className="text-xs text-slate-500 tabular-nums">{formatSize(file.tamanho)}</span>
                        {file.status === 'sucesso' && <span className="text-emerald-500">✓</span>}
                        {file.status === 'erro' && <span className="text-red-500" title={file.mensagem}>⚠</span>}
                        {file.status === 'processando' && <div className="w-3 h-3 border-2 border-blue-500 border-t-transparent rounded-full animate-spin" />}
                      </div>
                    </div>
                  ))}
                </div>
              )}
            </div>
          </div>
        </div>

        {/* Options Area */}
        <div className="col-span-4 space-y-4">
          <div className="p-6 bg-slate-800/40 border border-slate-700/50 rounded-3xl space-y-6">
            <h3 className="text-sm font-bold text-white uppercase tracking-wider">Configurações</h3>
            
            <div className="space-y-4">
              <div className="flex items-center justify-between">
                <div>
                  <p className="text-xs font-semibold text-slate-300">Converter para PDF/A</p>
                  <p className="text-[10px] text-slate-500">Garantir preservação a longo prazo.</p>
                </div>
                <button 
                  onClick={() => setPdfa(!pdfa)}
                  className={`w-10 h-5 rounded-full transition-all relative ${pdfa ? 'bg-blue-600' : 'bg-slate-700'}`}
                >
                  <div className={`absolute top-1 w-3 h-3 bg-white rounded-full transition-all ${pdfa ? 'left-6' : 'left-1'}`} />
                </button>
              </div>

              <div className="flex items-center justify-between">
                <div>
                  <p className="text-xs font-semibold text-slate-300">Unir PDFs (Merge)</p>
                  <p className="text-[10px] text-slate-500">Gerar um arquivo único ao final.</p>
                </div>
                <button 
                  onClick={() => setUnirPdfs(!unirPdfs)}
                  className={`w-10 h-5 rounded-full transition-all relative ${unirPdfs ? 'bg-blue-600' : 'bg-slate-700'}`}
                >
                  <div className={`absolute top-1 w-3 h-3 bg-white rounded-full transition-all ${unirPdfs ? 'left-6' : 'left-1'}`} />
                </button>
              </div>

              {unirPdfs && (
                <div className="space-y-2 animate-in fade-in zoom-in-95 duration-200">
                  <label className="block text-[10px] uppercase font-bold text-slate-500">Nome do Arquivo Final</label>
                  <input 
                    type="text" 
                    value={nomeSaida}
                    onChange={(e) => setNomeSaida(e.target.value)}
                    className="w-full bg-slate-900/60 border border-slate-700/50 rounded-lg px-3 py-2 text-xs text-slate-200 outline-none focus:ring-1 focus:ring-blue-500"
                  />
                </div>
              )}

              <div className="space-y-2 pt-2">
                <label className="block text-[10px] uppercase font-bold text-slate-500">PIN do Certificado</label>
                <input 
                  type="password" 
                  value={pin}
                  onChange={(e) => setPin(e.target.value)}
                  placeholder="••••"
                  className="w-full bg-slate-900/60 border border-slate-700/50 rounded-lg px-3 py-2 text-xs text-slate-200 outline-none focus:ring-1 focus:ring-blue-500"
                />
              </div>
            </div>

            <button 
              disabled={processing || arquivos.filter(a => a.selecionado).length === 0}
              onClick={processarLote}
              className="w-full py-4 bg-gradient-to-r from-blue-600 to-indigo-600 hover:from-blue-500 hover:to-indigo-500 disabled:from-slate-800 disabled:to-slate-800 disabled:text-slate-600 text-white rounded-2xl text-sm font-bold shadow-xl shadow-blue-900/20 transition-all active:scale-95"
            >
              {processing ? 'Processando...' : 'Executar Lote'}
            </button>

            {processing && (
              <div className="space-y-2">
                <div className="flex justify-between text-[10px] font-bold text-slate-400">
                  <span>Progresso</span>
                  <span>{progress}%</span>
                </div>
                <div className="h-1.5 bg-slate-800 rounded-full overflow-hidden">
                  <div 
                    className="h-full bg-blue-500 transition-all duration-300" 
                    style={{ width: `${progress}%` }}
                  />
                </div>
              </div>
            )}
          </div>
        </div>
      </div>
    </div>
  );
}