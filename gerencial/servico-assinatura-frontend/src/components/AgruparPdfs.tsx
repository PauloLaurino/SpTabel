import { useState } from 'react';
import { assinaturaService } from '../services/assinaturaApi';

export default function AgruparPdfs() {
  const [pasta, setPasta] = useState('');
  const [prefixo, setPrefixo] = useState('');
  const [saida, setSaida] = useState('');
  const [loading, setLoading] = useState(false);
  const [result, setResult] = useState<{ arquivo: string; quantidade: number } | null>(null);
  const [error, setError] = useState<string | null>(null);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!pasta || !saida) return;

    setLoading(true);
    setError(null);
    setResult(null);

    try {
      const response = await assinaturaService.agruparPdfs({
        pasta,
        prefixo: prefixo || undefined,
        saida,
      });
      setResult({
        arquivo: response.arquivo,
        quantidade: response.quantidade,
      });
    } catch (err: any) {
      setError(err.response?.data?.mensagem || err.message || 'Erro ao agrupar PDFs');
    } finally {
      setLoading(false);
    }
  };

  return (
    <form onSubmit={handleSubmit} className="space-y-6">
      <div>
        <h2 className="text-lg font-semibold text-gray-900 mb-4">Agrupar PDFs em Livro</h2>
        <p className="text-gray-600 text-sm mb-4">
          Selecione uma pasta contendo arquivos PDF para agrupá-los em um único arquivo (livro).
        </p>
      </div>

      <div>
        <label className="block text-sm font-medium text-gray-700 mb-1">
          Pasta com PDFs
        </label>
        <input
          type="text"
          value={pasta}
          onChange={(e) => setPasta(e.target.value)}
          placeholder="/caminho/pasta/com/pdfs/"
          className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500 mono text-sm"
        />
        <p className="text-xs text-gray-500 mt-1">
          Todos os arquivos PDF na pasta serão incluídos na ordem alfabética
        </p>
      </div>

      <div>
        <label className="block text-sm font-medium text-gray-700 mb-1">
          Prefixo (opcional)
        </label>
        <input
          type="text"
          value={prefixo}
          onChange={(e) => setPrefixo(e.target.value)}
          placeholder="Ex: Livro_Ato_"
          className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500"
        />
        <p className="text-xs text-gray-500 mt-1">
          Se preenchido, apenas arquivos com este prefixo serão incluídos
        </p>
      </div>

      <div>
        <label className="block text-sm font-medium text-gray-700 mb-1">
          Arquivo de Saída (Livro)
        </label>
        <input
          type="text"
          value={saida}
          onChange={(e) => setSaida(e.target.value)}
          placeholder="/caminho/livro_unico.pdf"
          className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500 mono text-sm"
        />
      </div>

      {error && (
        <div className="p-4 bg-red-50 border border-red-200 rounded-lg text-red-700">
          {error}
        </div>
      )}

      {result && (
        <div className="p-4 bg-green-50 border border-green-200 rounded-lg text-green-700">
          <p className="font-medium">Livro criado com sucesso!</p>
          <p className="text-sm mt-1">Arquivo: {result.arquivo}</p>
          <p className="text-sm">Quantidade de PDFs: {result.quantidade}</p>
        </div>
      )}

      <button
        type="submit"
        disabled={!pasta || !saida || loading}
        className={`px-6 py-3 rounded-lg font-medium text-white transition-colors ${
          !pasta || !saida || loading
            ? 'bg-gray-400 cursor-not-allowed'
            : 'bg-blue-600 hover:bg-blue-700'
        }`}
      >
        {loading ? 'Agrupando...' : 'Agrupar PDFs'}
      </button>
    </form>
  );
}