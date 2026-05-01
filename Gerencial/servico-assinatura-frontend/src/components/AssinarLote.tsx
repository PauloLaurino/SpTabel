import { useState, useRef, useCallback } from 'react';
import { assinaturaService } from '../services/assinaturaApi';

export default function AssinarLote() {
  const [files, setFiles] = useState<File[]>([]);
  const [certificado, setCertificado] = useState<'A1' | 'A3'>('A1');
  const [pin, setPin] = useState('');
  const [timestamp, setTimestamp] = useState(true);
  const [pastaSaida, setPastaSaida] = useState('');
  const [loading, setLoading] = useState(false);
  const [progress, setProgress] = useState(0);
  const [result, setResult] = useState<{ sucesso: number; erros: number } | null>(null);
  const [error, setError] = useState<string | null>(null);
  const fileInputRef = useRef<HTMLInputElement>(null);

  const handleFileChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const selectedFiles = Array.from(e.target.files || []);
    setFiles(selectedFiles);
    setResult(null);
    setError(null);
  };

  const handleDrop = useCallback((e: React.DragEvent) => {
    e.preventDefault();
    const droppedFiles = Array.from(e.dataTransfer.files).filter(f => f.type === 'application/pdf');
    setFiles(prev => [...prev, ...droppedFiles]);
  }, []);

  const removeFile = (index: number) => {
    setFiles(prev => prev.filter((_, i) => i !== index));
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (files.length === 0 || !pastaSaida) return;

    setLoading(true);
    setError(null);
    setResult(null);
    setProgress(0);

    let sucesso = 0;
    let erros = 0;

    for (let i = 0; i < files.length; i++) {
      const file = files[i];
      try {
        const reader = new FileReader();
        await new Promise<void>((resolve) => {
          reader.onload = async () => {
            try {
              const base64 = (reader.result as string).split(',')[1];
              const response = await assinaturaService.assinar({
                documento: base64,
                certificado,
                pin: pin || undefined,
                timestamp,
              });

              if (response.status === 'ok') {
                sucesso++;
              } else {
                erros++;
              }
              resolve();
            } catch {
              erros++;
              resolve();
            }
          };
          reader.onerror = () => {
            erros++;
            resolve();
          };
          reader.readAsDataURL(file);
        });
      } catch {
        erros++;
      }

      setProgress(Math.round(((i + 1) / files.length) * 100));
    }

    setResult({ sucesso, erros });
    setLoading(false);
  };

  return (
    <form onSubmit={handleSubmit} className="space-y-6">
      <div>
        <h2 className="text-lg font-semibold text-gray-900 mb-4">Assinar em Lote</h2>
      </div>

      <div
        onDrop={handleDrop}
        onDragOver={(e) => e.preventDefault()}
        className="border-2 border-dashed border-gray-300 rounded-lg p-8 text-center hover:border-blue-400 transition-colors"
      >
        <input
          ref={fileInputRef}
          type="file"
          accept=".pdf"
          multiple
          onChange={handleFileChange}
          className="hidden"
        />
        <div className="cursor-pointer" onClick={() => fileInputRef.current?.click()}>
          <div className="text-4xl mb-2">📚</div>
          <p className="text-gray-600">
            Arraste arquivos PDF aqui ou clique para selecionar
          </p>
          <p className="text-gray-400 text-sm mt-1">
            Múltiplos arquivos permitidos
          </p>
        </div>
      </div>

      {files.length > 0 && (
        <div className="bg-gray-50 rounded-lg p-4">
          <div className="flex justify-between items-center mb-2">
            <span className="text-sm font-medium text-gray-700">
              {files.length} arquivo(s) selecionado(s)
            </span>
            <button
              type="button"
              onClick={() => setFiles([])}
              className="text-sm text-red-600 hover:text-red-800"
            >
              Limpar todos
            </button>
          </div>
          <div className="max-h-40 overflow-y-auto space-y-1">
            {files.map((file, index) => (
              <div key={index} className="flex items-center justify-between bg-white px-3 py-2 rounded">
                <span className="text-sm text-gray-700 truncate">{file.name}</span>
                <button
                  type="button"
                  onClick={() => removeFile(index)}
                  className="text-gray-400 hover:text-red-600 ml-2"
                >
                  ✕
                </button>
              </div>
            ))}
          </div>
        </div>
      )}

      <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
        <div>
          <label className="block text-sm font-medium text-gray-700 mb-1">Tipo de Certificado</label>
          <select
            value={certificado}
            onChange={(e) => setCertificado(e.target.value as 'A1' | 'A3')}
            className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500"
          >
            <option value="A1">A1 (Arquivo)</option>
            <option value="A3">A3 (Token/HSM)</option>
          </select>
        </div>

        {certificado === 'A3' && (
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">PIN do Certificado</label>
            <input
              type="password"
              value={pin}
              onChange={(e) => setPin(e.target.value)}
              placeholder="Digite o PIN"
              className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500"
            />
          </div>
        )}

        <div>
          <label className="block text-sm font-medium text-gray-700 mb-1">Pasta de Saída</label>
          <input
            type="text"
            value={pastaSaida}
            onChange={(e) => setPastaSaida(e.target.value)}
            placeholder="/caminho/para/saida/"
            className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500 mono text-sm"
          />
        </div>

        <div className="flex items-center">
          <label className="flex items-center gap-2 cursor-pointer">
            <input
              type="checkbox"
              checked={timestamp}
              onChange={(e) => setTimestamp(e.target.checked)}
              className="w-4 h-4 text-blue-600 border-gray-300 rounded focus:ring-blue-500"
            />
            <span className="text-sm text-gray-700">Incluir timestamp</span>
          </label>
        </div>
      </div>

      {loading && (
        <div className="space-y-2">
          <div className="h-2 bg-gray-200 rounded-full overflow-hidden">
            <div
              className="h-full bg-blue-600 transition-all duration-300"
              style={{ width: `${progress}%` }}
            />
          </div>
          <p className="text-sm text-gray-600 text-center">
            Processando... {progress}%
          </p>
        </div>
      )}

      {error && (
        <div className="p-4 bg-red-50 border border-red-200 rounded-lg text-red-700">
          {error}
        </div>
      )}

      {result && (
        <div className="p-4 bg-green-50 border border-green-200 rounded-lg text-green-700">
          <p className="font-medium">Processamento concluído!</p>
          <p className="text-sm mt-1">Sucesso: {result.sucesso} | Erros: {result.erros}</p>
        </div>
      )}

      <button
        type="submit"
        disabled={files.length === 0 || !pastaSaida || loading}
        className={`px-6 py-3 rounded-lg font-medium text-white transition-colors ${
          files.length === 0 || !pastaSaida || loading
            ? 'bg-gray-400 cursor-not-allowed'
            : 'bg-blue-600 hover:bg-blue-700'
        }`}
      >
        {loading ? 'Processando...' : 'Assinar em Lote'}
      </button>
    </form>
  );
}