import { useState, useRef } from 'react';
import { assinaturaService } from '../services/assinaturaApi';

export default function AssinarPdfUnico() {
  const [file, setFile] = useState<File | null>(null);
  const [certificado, setCertificado] = useState<'A1' | 'A3'>('A1');
  const [pin, setPin] = useState('');
  const [timestamp, setTimestamp] = useState(true);
  const [loading, setLoading] = useState(false);
  const [result, setResult] = useState<string | null>(null);
  const [error, setError] = useState<string | null>(null);
  const fileInputRef = useRef<HTMLInputElement>(null);

  const handleFileChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const selectedFile = e.target.files?.[0];
    if (selectedFile) {
      setFile(selectedFile);
      setResult(null);
      setError(null);
    }
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!file) return;

    setLoading(true);
    setError(null);
    setResult(null);

    try {
      const reader = new FileReader();
      reader.onload = async () => {
        const base64 = (reader.result as string).split(',')[1];
        
        const response = await assinaturaService.assinar({
          documento: base64,
          certificado,
          pin: pin || undefined,
          timestamp,
        });

        if (response.status === 'ok' && response.documento_assinado) {
          const link = document.createElement('a');
          link.href = `data:application/pdf;base64,${response.documento_assinado}`;
          link.download = file.name.replace('.pdf', '_assinado.pdf');
          link.click();
          setResult('Documento assinado com sucesso!');
        }
      };
      reader.readAsDataURL(file);
    } catch (err: any) {
      setError(err.response?.data?.message || err.message || 'Erro ao assinar documento');
    } finally {
      setLoading(false);
    }
  };

  return (
    <form onSubmit={handleSubmit} className="space-y-6">
      <div>
        <h2 className="text-lg font-semibold text-gray-900 mb-4">Assinar PDF Único</h2>
      </div>

      <div className="border-2 border-dashed border-gray-300 rounded-lg p-8 text-center hover:border-blue-400 transition-colors">
        <input
          ref={fileInputRef}
          type="file"
          accept=".pdf"
          onChange={handleFileChange}
          className="hidden"
        />
        <div className="cursor-pointer" onClick={() => fileInputRef.current?.click()}>
          <div className="text-4xl mb-2">📄</div>
          {file ? (
            <div>
              <p className="text-gray-900 font-medium">{file.name}</p>
              <p className="text-gray-500 text-sm">{(file.size / 1024 / 1024).toFixed(2)} MB</p>
            </div>
          ) : (
            <p className="text-gray-600">Clique ou arraste um arquivo PDF aqui</p>
          )}
        </div>
      </div>

      <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
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

        <div className="flex items-center pt-6">
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

      {error && (
        <div className="p-4 bg-red-50 border border-red-200 rounded-lg text-red-700">
          {error}
        </div>
      )}

      {result && (
        <div className="p-4 bg-green-50 border border-green-200 rounded-lg text-green-700">
          {result}
        </div>
      )}

      <button
        type="submit"
        disabled={!file || loading}
        className={`px-6 py-3 rounded-lg font-medium text-white transition-colors ${
          !file || loading
            ? 'bg-gray-400 cursor-not-allowed'
            : 'bg-blue-600 hover:bg-blue-700'
        }`}
      >
        {loading ? 'Assinando...' : 'Assinar Documento'}
      </button>
    </form>
  );
}