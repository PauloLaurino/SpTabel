import { useState } from 'react';
import { assinaturaService } from '../services/assinaturaApi';

export default function CopiarPagina() {
  const [caminhoLivro, setCaminhoLivro] = useState('');
  const [paginaInicial, setPaginaInicial] = useState<number>(1);
  const [paginaFinal, setPaginaFinal] = useState<number>(1);
  const [saida, setSaida] = useState('');
  const [loading, setLoading] = useState(false);
  const [result, setResult] = useState<string | null>(null);
  const [error, setError] = useState<string | null>(null);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!caminhoLivro || !saida) return;

    setLoading(true);
    setError(null);
    setResult(null);

    try {
      await assinaturaService.copiarPaginas({
        caminhoLivro,
        paginaInicial,
        paginaFinal: paginaFinal || paginaInicial,
        saida,
      });
      setResult(`Página(s) copiada(s) com sucesso para: ${saida}`);
    } catch (err: any) {
      setError(err.response?.data?.mensagem || err.message || 'Erro ao copiar página(s)');
    } finally {
      setLoading(false);
    }
  };

  return (
    <form onSubmit={handleSubmit} className="space-y-6">
      <div>
        <h2 className="text-lg font-semibold text-gray-900 mb-4">Copiar Página de Livro</h2>
        <p className="text-gray-600 text-sm mb-4">
          Copie uma ou mais páginas de um livro PDF e salve como um novo arquivo PDF.
        </p>
      </div>

      <div>
        <label className="block text-sm font-medium text-gray-700 mb-1">
          Caminho do Livro (PDF de origem)
        </label>
        <input
          type="text"
          value={caminhoLivro}
          onChange={(e) => setCaminhoLivro(e.target.value)}
          placeholder="/caminho/livro_original.pdf"
          className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500 mono text-sm"
        />
      </div>

      <div className="grid grid-cols-2 gap-4">
        <div>
          <label className="block text-sm font-medium text-gray-700 mb-1">
            Página Inicial
          </label>
          <input
            type="number"
            min={1}
            value={paginaInicial}
            onChange={(e) => setPaginaInicial(parseInt(e.target.value) || 1)}
            className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500"
          />
        </div>

        <div>
          <label className="block text-sm font-medium text-gray-700 mb-1">
            Página Final
          </label>
          <input
            type="number"
            min={paginaInicial}
            value={paginaFinal}
            onChange={(e) => setPaginaFinal(parseInt(e.target.value) || paginaInicial)}
            className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500"
          />
        </div>
      </div>

      <div>
        <label className="block text-sm font-medium text-gray-700 mb-1">
          Caminho de Saída (PDF de destino)
        </label>
        <input
          type="text"
          value={saida}
          onChange={(e) => setSaida(e.target.value)}
          placeholder="/caminho/paginas_extraidas.pdf"
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
          {result}
        </div>
      )}

      <button
        type="submit"
        disabled={!caminhoLivro || !saida || loading}
        className={`px-6 py-3 rounded-lg font-medium text-white transition-colors ${
          !caminhoLivro || !saida || loading
            ? 'bg-gray-400 cursor-not-allowed'
            : 'bg-blue-600 hover:bg-blue-700'
        }`}
      >
        {loading ? 'Copiando...' : 'Copiar Página(s)'}
      </button>
    </form>
  );
}