import { useState } from 'react';
import AssinarPdfUnico from './components/AssinarPdfUnico';
import AssinarLote from './components/AssinarLote';
import CopiarPagina from './components/CopiarPagina';
import AgruparPdfs from './components/AgruparPdfs';

type Tab = 'unico' | 'lote' | 'copiar' | 'agrupar';

function App() {
  const [activeTab, setActiveTab] = useState<Tab>('unico');

  const tabs: { id: Tab; label: string; icon: string }[] = [
    { id: 'unico', label: 'Assinar PDF Único', icon: '📄' },
    { id: 'lote', label: 'Assinar em Lote', icon: '📚' },
    { id: 'copiar', label: 'Copiar Página', icon: '📑' },
    { id: 'agrupar', label: 'Agrupar PDFs', icon: '📦' },
  ];

  return (
    <div className="min-h-screen bg-gray-100">
      <header className="bg-white shadow-sm border-b border-gray-200">
        <div className="max-w-7xl mx-auto px-4 py-4">
          <h1 className="text-2xl font-bold text-gray-900">
            Serviço de Assinatura Digital
          </h1>
        </div>
      </header>

      <main className="max-w-7xl mx-auto px-4 py-6">
        <div className="bg-white rounded-xl shadow-sm border border-gray-200 overflow-hidden">
          <div className="flex border-b border-gray-200 overflow-x-auto">
            {tabs.map((tab) => (
              <button
                key={tab.id}
                onClick={() => setActiveTab(tab.id)}
                className={`flex items-center gap-2 px-6 py-4 text-sm font-medium whitespace-nowrap transition-colors ${
                  activeTab === tab.id
                    ? 'border-b-2 border-blue-600 text-blue-600 bg-blue-50'
                    : 'text-gray-600 hover:text-gray-900 hover:bg-gray-50'
                }`}
              >
                <span>{tab.icon}</span>
                {tab.label}
              </button>
            ))}
          </div>

          <div className="p-6">
            {activeTab === 'unico' && <AssinarPdfUnico />}
            {activeTab === 'lote' && <AssinarLote />}
            {activeTab === 'copiar' && <CopiarPagina />}
            {activeTab === 'agrupar' && <AgruparPdfs />}
          </div>
        </div>
      </main>
    </div>
  );
}

export default App;