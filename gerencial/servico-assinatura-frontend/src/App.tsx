import { useState } from 'react';
import AssinarPdfUnico from './components/AssinarPdfUnico';
import AssinarLote from './components/AssinarLote';
import CopiarPagina from './components/CopiarPagina';
import AgruparPdfs from './components/AgruparPdfs';

type Tab = 'unico' | 'lote' | 'copiar' | 'agrupar';

function App() {
  const [activeTab, setActiveTab] = useState<Tab>('lote');

  const tabs: { id: Tab; label: string; icon: string }[] = [
    { id: 'lote', label: 'Assinatura em Lote', icon: '📚' },
    { id: 'unico', label: 'PDF Único', icon: '📄' },
    { id: 'copiar', label: 'Separar Páginas', icon: '📑' },
    { id: 'agrupar', label: 'Unir PDFs', icon: '📦' },
  ];

  return (
    <div className="min-h-screen bg-[#0f172a] text-slate-200 font-inter selection:bg-blue-500/30">
      {/* Background Glow */}
      <div className="fixed inset-0 overflow-hidden pointer-events-none">
        <div className="absolute -top-[10%] -left-[10%] w-[40%] h-[40%] bg-blue-600/10 blur-[120px] rounded-full" />
        <div className="absolute top-[60%] -right-[10%] w-[40%] h-[40%] bg-indigo-600/10 blur-[120px] rounded-full" />
      </div>

      <header className="relative z-10 border-b border-slate-800/60 bg-slate-900/40 backdrop-blur-md">
        <div className="max-w-7xl mx-auto px-6 py-4 flex items-center justify-between">
          <div className="flex items-center gap-3">
            <div className="w-10 h-10 bg-gradient-to-br from-blue-500 to-indigo-600 rounded-xl flex items-center justify-center shadow-lg shadow-blue-500/20">
              <span className="text-xl">✍️</span>
            </div>
            <div>
              <h1 className="text-xl font-bold tracking-tight bg-clip-text text-transparent bg-gradient-to-r from-white to-slate-400">
                Seprocom Assinatura
              </h1>
              <p className="text-[10px] uppercase tracking-[0.2em] text-slate-500 font-semibold">
                Portal de Assinatura Digital
              </p>
            </div>
          </div>
          
          <div className="flex items-center gap-4">
            <div className="px-3 py-1 bg-emerald-500/10 border border-emerald-500/20 rounded-full flex items-center gap-2">
              <div className="w-1.5 h-1.5 bg-emerald-500 rounded-full animate-pulse" />
              <span className="text-xs font-medium text-emerald-400">Servidor Online</span>
            </div>
          </div>
        </div>
      </header>

      <main className="relative z-10 max-w-7xl mx-auto px-6 py-8">
        <div className="grid grid-cols-12 gap-8">
          {/* Sidebar Navigation */}
          <div className="col-span-3 space-y-2">
            {tabs.map((tab) => (
              <button
                key={tab.id}
                onClick={() => setActiveTab(tab.id)}
                className={`w-full flex items-center gap-3 px-4 py-3 rounded-xl text-sm font-medium transition-all duration-200 ${
                  activeTab === tab.id
                    ? 'bg-blue-600 text-white shadow-lg shadow-blue-600/20 translate-x-1'
                    : 'text-slate-400 hover:text-slate-100 hover:bg-slate-800/50'
                }`}
              >
                <span className="text-lg">{tab.icon}</span>
                {tab.label}
              </button>
            ))}
            
            <div className="pt-8 px-4">
              <div className="p-4 bg-slate-800/40 border border-slate-700/50 rounded-2xl">
                <h3 className="text-xs font-bold text-slate-500 uppercase tracking-wider mb-2">Suporte</h3>
                <p className="text-[11px] text-slate-400 leading-relaxed">
                  Problemas com o certificado? Certifique-se que o token está conectado corretamente.
                </p>
              </div>
            </div>
          </div>

          {/* Main Content Area */}
          <div className="col-span-9 bg-slate-900/40 border border-slate-800/60 backdrop-blur-xl rounded-[2rem] overflow-hidden shadow-2xl">
            <div className="p-8">
              {activeTab === 'unico' && <AssinarPdfUnico />}
              {activeTab === 'lote' && <AssinarLote />}
              {activeTab === 'copiar' && <CopiarPagina />}
              {activeTab === 'agrupar' && <AgruparPdfs />}
            </div>
          </div>
        </div>
      </main>
    </div>
  );
}

export default App;