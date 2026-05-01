import './styles/global.css';
import { Header } from './components/Header/Header';
import NfseTabs from './components/NfseTabs/NfseTabs';
import './App.css';

function App() {
  return (
    <div className="app">
      <Header 
        systemName="NFS-E"
        systemDescription="Emissor de Notas Fiscais"
        version="1.0.0"
        userName="Operador"
        userRole="Administrador"
      />
      <main className="app-main">
        <div className="app-container">
          <NfseTabs />
        </div>
      </main>
    </div>
  );
}

export default App;