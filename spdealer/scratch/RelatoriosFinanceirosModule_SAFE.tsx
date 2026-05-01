import React, { useState, useEffect } from 'react';
import { useNavigate, useLocation } from 'react-router-dom';
import styled from 'styled-components';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faSearch, faSpinner, faChevronRight, faChevronDown, faMoneyBillWave, faFileInvoiceDollar, faCashRegister, faCalendarAlt, faDownload, faFilter } from '@fortawesome/free-solid-svg-icons';
import { AgGridReact } from 'ag-grid-react';
import 'ag-grid-community/styles/ag-theme-quartz.css';
import { RelatoriosService } from '../../services/RelatoriosService';
import { CaixaBancosService } from '../../services/CaixaBancosService';
import { PrevisaoFinanceiraService } from '../../services/PrevisaoFinanceiraService';
import { useAuth } from '../../contexts/AuthContext';
import RegistrarLancamentoModal from '../../components/RegistrarLancamentoModal';
import CaixaBancosForm from '../../components/Forms/CaixaBancosForm';
/* eslint-disable react-hooks/exhaustive-deps */

/*
  Op+º+úo A aplicada: silenciar avisos de "react-hooks/exhaustive-deps" neste arquivo
  (n+úo alterar arrays de depend+¬ncia automaticamente conforme orienta+º+úo do time).
*/
/* eslint-disable react-hooks/exhaustive-deps */

// TODO: revisar e corrigir depend+¬ncias de React Hooks neste arquivo.
// Remover esta nota quando `useEffect`/`useCallback`/`useMemo` estiverem com depend+¬ncias corretas.

const Container = styled.div`
  display: flex;
  flex-direction: column;
  height: 100%;
  background: #f8fafc;
`;

const Header = styled.div`
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px 24px;
  background: #fff;
  border-bottom: 1px solid #e5e7eb;
  box-shadow: 0 1px 3px rgba(0,0,0,0.1);
`;

const Title = styled.h1`
  margin: 0;
  font-size: 24px;
  font-weight: 700;
  color: #1f2937;
  display: flex;
  align-items: center;
  gap: 12px;
`;

const SubMenu = styled.div`
  display: flex;
  background: #fff;
  border-bottom: 1px solid #e5e7eb;
  overflow-x: auto;
`;

const SubMenuItem = styled.button<{ $active?: boolean }>`
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 16px 24px;
  border: none;
  background: ${props => props.$active ? '#f0f9ff' : 'transparent'};
  color: ${props => props.$active ? '#0369a1' : '#6b7280'};
  font-size: 14px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.2s ease;
  border-bottom: 3px solid ${props => props.$active ? '#0369a1' : 'transparent'};
  white-space: nowrap;

  &:hover {
    background: #f0f9ff;
    color: #0369a1;
  }
`;

const Content = styled.div`
  flex: 1;
  padding: 12px;
  overflow: hidden;
  display: flex;
  flex-direction: column;
  gap: 6px;
  padding-bottom: 24px;
`;

const FilterCard = styled.div`
  background: #fff;
  padding: 12px;
  border-radius: 8px;
  box-shadow: 0 1px 3px rgba(0,0,0,0.08);
  margin-bottom: 0;
  flex-shrink: 0;
`;

const FilterTitle = styled.h3<{ $collapsed?: boolean }>`
  margin: 0 0 8px 0;
  font-size: 16px;
  font-weight: 600;
  color: #1f2937;
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
  user-select: none;
  padding: 4px 0;
  transition: color 0.2s ease;
  
  &:hover {
    color: #3b82f6;
  }
  
  svg.chevron {
    transition: transform 0.3s ease;
    transform: ${(props) => props.$collapsed ? 'rotate(90deg)' : 'rotate(0deg)'};
  }
`;

const FilterGrid = styled.div`
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 12px;
`;

const FilterContent = styled.div<{ $collapsed: boolean }>`
  max-height: ${props => props.$collapsed ? '0' : '900px'};
  overflow: hidden;
  opacity: ${props => props.$collapsed ? '0' : '1'};
  transition: max-height 0.25s ease, opacity 0.25s ease;
  margin-top: ${props => props.$collapsed ? '0' : '4px'};
`;

const FilterGroup = styled.div`
  display: flex;
  flex-direction: column;
  gap: 6px;
`;

const Label = styled.label`
  font-size: 14px;
  font-weight: 600;
  color: #374151;
`;

const Input = styled.input`
  padding: 10px 12px;
  border: 1px solid #d1d5db;
  border-radius: 6px;
  font-size: 14px;
  transition: border-color 0.2s ease;

  &:focus {
    outline: none;
    border-color: #3b82f6;
    box-shadow: 0 0 0 3px rgba(59, 130, 246, 0.1);
  }
`;

const Select = styled.select`
  padding: 10px 12px;
  border: 1px solid #d1d5db;
  border-radius: 6px;
  font-size: 14px;
  background: #fff;
  transition: border-color 0.2s ease;

  &:focus {
    outline: none;
    border-color: #3b82f6;
    box-shadow: 0 0 0 3px rgba(59, 130, 246, 0.1);
  }
`;

const Checkbox = styled.input`
  width: 16px;
  height: 16px;
  accent-color: #3b82f6;
`;

const CheckboxLabel = styled.label`
  font-size: 14px;
  color: #374151;
  cursor: pointer;
`;

const ButtonGroup = styled.div`
  display: flex;
  gap: 12px;
  justify-content: flex-end;
`;


const Button = styled.button<{ $variant?: 'primary' | 'secondary' | 'success' }>`
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 10px 20px;
  border: none;
  border-radius: 6px;
  font-size: 14px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.2s ease;
  
  ${props => {
    switch (props.$variant) {
      case 'primary':
        return `
          background: #3b82f6;
          color: #fff;
          &:hover:not(:disabled) { background: #2563eb; }
        `;
      case 'success':
        return `
          background: #10b981;
          color: #fff;
          &:hover:not(:disabled) { background: #059669; }
        `;
      default:
        return `
          background: #6b7280;
          color: #fff;
          &:hover:not(:disabled) { background: #4b5563; }
        `;
    }
  }}

  &:disabled {
    opacity: 0.6;
    cursor: not-allowed;
  }
`;

const ResultTable = styled.div`
  flex: 1;
  overflow: auto;
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 1px 3px rgba(0,0,0,0.1);
`;

const ResultCard = styled.div`
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 1px 3px rgba(0,0,0,0.1);
  overflow: hidden;
  display: flex;
  flex-direction: column;
  flex: 1;
  min-height: 0;
`;


const AgGridContainer = styled.div`
  width: 100%;
  height: 100%;
  
  .ag-root.ag-unselectable.ag-layout-normal {
    border: none;
  }
  
  .ag-header-cell-text {
    font-weight: 600;
    color: #374151;
  }
  
  .ag-cell {
    font-size: 14px;
    color: #1f2937;
  }
  
  .ag-footer-cell-text {
    font-weight: 600;
    color: #059669;
  }
`;

const PeriodCardsContainer = styled.div`
  display: flex;
  flex-direction: column;
  gap: 12px;
`;

/* SectionTitle and CardsGrid removed (unused styled components) */

const PeriodCardButton = styled.button<{ $isSelected: boolean; $color: string }>`
  padding: 6px 10px;
  border: 2px solid ${(props) => (props.$isSelected ? props.$color : '#e6e7ea')};
  background: ${(props) => (props.$isSelected ? `${props.$color}10` : '#ffffff')};
  border-radius: 6px;
  cursor: pointer;
  transition: all 0.15s ease;
  display: inline-flex;
  flex-direction: row;
  align-items: center;
  gap: 8px;
  text-align: center;
  font-family: inherit;
  min-height: 40px;

  &:hover {
    border-color: ${(props) => props.$color};
    background: ${(props) => `${props.$color}14`};
    transform: translateY(-1px);
  }

  .period-value {
    font-size: 14px;
    font-weight: 700;
    color: ${(props) => props.$color};
    min-width: 28px;
    text-align: center;
  }

  .period-label {
    font-size: 12px;
    color: #6b7280;
    font-weight: 600;
  }
`;

type TipoRelatorio = 'receber' | 'pagar' | 'fluxo' | 'consulta_caixa';

interface FiltroRelatorio {
  tipo: TipoRelatorio;
  tipoDataFiltro: string;
  dataFiltroInicial: string;
  dataFiltroFinal: string;
  pessoaTipo: string;
  tipoCobranca: string;
  tipoDocumento: string;
  tiposDocumento?: string[]; // M+¦ltiplos tipos de documento selecionados
  departamento: string;
  centroCusto: string;
  faixaAtraso: string;
  soEmAberto: boolean;
  soPagos: boolean;
  folhaPagamento?: boolean; // Flag para relat+¦rio de folha de pagamento
  tipoCampoData?: string;
  dataini?: string;
  datafim?: string;
}

interface RelatoriosFinanceirosModuleProps {
  height?: string;
}

const RelatoriosFinanceirosModule: React.FC<RelatoriosFinanceirosModuleProps> = ({ height = '100%' }) => {
  const [tipoAtivo, setTipoAtivo] = useState<TipoRelatorio>('receber');
  const [filtros, setFiltros] = useState<FiltroRelatorio>({
    tipo: 'receber',
      tipoDataFiltro: 'vencimento',
    dataFiltroInicial: '',
    dataFiltroFinal: '',
    pessoaTipo: '',
    tipoCobranca: '',
    tipoDocumento: '',
    tiposDocumento: [], // Inicializar array vazio para m+¦ltipla sele+º+úo
    departamento: '',
    centroCusto: '',
    faixaAtraso: '',
    soEmAberto: false,
    soPagos: false,
    folhaPagamento: false, // Inicializar flag de folha de pagamento
    tipoCampoData: undefined
  });
  
  const [dados, setDados] = useState<any[]>([]);
  const [loading, setLoading] = useState(false);
  // search input state removed (unused) ÔÇö AG Grid quick filter used directly
  const [collapseFilter, setCollapseFilter] = useState(false);
  const [consultaCollapseFilter, setConsultaCollapseFilter] = useState(false);
  const [periodCollapsed, setPeriodCollapsed] = useState(false);
  const [gridApi, setGridApi] = useState<any>(null);
  const [totalRow, setTotalRow] = useState<any>(null);
  const [opcoesCobranca, setOpcoesCobranca] = useState<any[]>([]);
  const [opcoesTipoDocumento, setOpcoesTipoDocumento] = useState<any[]>([]);
  const [opcoesDeptos, setOpcoesDeptos] = useState<any[]>([]);
  const [consultaDados, setConsultaDados] = useState<any[]>([]);
  const [consultaLoading, setConsultaLoading] = useState(false);
  const [consultaDataInicial, setConsultaDataInicial] = useState('');
  const [consultaDataFinal, setConsultaDataFinal] = useState('');
  const [consultaCentroCusto, setConsultaCentroCusto] = useState('');
  const [consultaOperacao, setConsultaOperacao] = useState('');
  const [consultaMascai] = useState('');
  const [opcoesOperacoesCaixa, setOpcoesOperacoesCaixa] = useState<any[]>([]);
  const [expandedDates, setExpandedDates] = useState<Set<string>>(new Set());
  const [filterOnlyHoje] = useState<boolean>(false);
  const [bancoSelecionado, setBancoSelecionado] = useState<string>('');
  // NOVO: Tracking de banco por documento - permite selecionar bancos diferentes por documento
  const [documentBanks, setDocumentBanks] = useState<Record<string, string>>({});
  const [bancoOptions, setBancoOptions] = useState<string[]>([]);
  const [saldosPorBanco, setSaldosPorBanco] = useState<Record<string, number>>({});
  const [authorizedRows, setAuthorizedRows] = useState<Set<string>>(new Set());
  const [previsoesPorOperacao, setPrevisoesPorOperacao] = useState<any>({});
  const [detalhesPorData, setDetalhesPorData] = useState<Record<string, any[]>>({});
  const [fluxoGlobalKPIs, setFluxoGlobalKPIs] = useState<any | null>(null);
  const [showRegistrarModal, setShowRegistrarModal] = useState(false);
  const [modalDocumentoIds, setModalDocumentoIds] = useState<number[]>([]);
  // NOVO: Banco por documento para passar ao modal
  const [modalDocumentBanks, setModalDocumentBanks] = useState<Record<string, string>>({});
  const [modalTipo, setModalTipo] = useState<'RECEBER'|'PAGAR'>('RECEBER');
  const [modalOperacao, setModalOperacao] = useState<number>(500);
  const [modalReloadKey, setModalReloadKey] = useState<string | null>(null);
  const [modalInitialValor, setModalInitialValor] = useState<number>(0);
  const [showCaixaPopup, setShowCaixaPopup] = useState<boolean>(false);
  const [caixaPopupPayload, setCaixaPopupPayload] = useState<any>(null);
  const [caixaPopupReadOnlyPrimary, setCaixaPopupReadOnlyPrimary] = useState<boolean>(true);
  
  // Estados para Previs+úo Financeira na Linha de Grupo
  const [previsaoPorData, setPrevisaoPorData] = useState<Record<string, any>>({});
  const [carregandoPrevisao, setCarregandoPrevisao] = useState(false);
  
  const { user } = useAuth();
  const navigate = useNavigate();

  // Sempre que `consultaDados` for atualizado, tentar aplicar ao gridApi
  useEffect(() => {
    try {
      if (!consultaDados || consultaDados.length === 0) return;
      const tryApply = (api: any) => {
        try {
          if (api && typeof api.setRowData === 'function') {
            api.setRowData(consultaDados);
            console.debug('[DEBUG-CONSULTA-CAIXA] useEffect: gridApi.setRowData aplicado, rows=', consultaDados.length);
            return true;
          }
        } catch (e) { /* noop */ }
        return false;
      };

      if (tryApply(gridApi)) return;
      if (tryApply((window as any).__agGridApi)) return;

      // Se ainda n+úo aplic+ível, guardar dados para serem aplicados quando o grid estiver pronto
      try { (window as any).__lastConsultaCaixa = consultaDados; } catch (e) {}
      console.debug('[DEBUG-CONSULTA-CAIXA] useEffect: gridApi n+úo dispon+¡vel, dados salvos em window.__lastConsultaCaixa');
    } catch (e) {
      console.warn('[DEBUG-CONSULTA-CAIXA] useEffect erro ao aplicar rowData', e);
    }
  }, [consultaDados, gridApi]);
  const dtemissaoWarningReceberShown = React.useRef(false);
  const dtemissaoWarningPagarShown = React.useRef(false);

  // Fechar o formul+írio com ESC -> volta para menu principal ('/')
  React.useEffect(() => {
    const onKeyDown = (e: KeyboardEvent) => {
      if (e.key === 'Escape' || e.key === 'Esc') {
        try { navigate('/'); } catch (err) { /* ignore */ }
      }
    };
    window.addEventListener('keydown', onKeyDown);
    return () => window.removeEventListener('keydown', onKeyDown);
  }, [navigate]);
  // Note: helpers and some other helpers/persistence are defined later in the file to avoid redeclaration

  // Carregar op+º+Áes de Tipo de Cobran+ºa e Departamentos ao montar o componente
  React.useEffect(() => {
    carregarOpcoesCobranca();
    carregarTiposDocumento();
    carregarDepartamentos();
    // carregar opera+º+Áes de caixa para a aba de consulta
    import('../../services/CaixaBancosService').then(m => {
      m.CaixaBancosService.listarOperacoesCaixa()
        .then((ops: any[]) => {
          // Normalizar diferentes formatos retornados pelo backend
          const normalized = (ops || []).map(op => {
            if (!op) return null;
            const codigo = op.operacao_ocai || op.codigo || op.id || op.codigo_ocai || op.operacao || String(op);
            const descricao = op.descr_ocai || op.descricao || op.label || op.descr || (typeof op === 'string' ? op : undefined);
            return { codigo, descricao };
          }).filter(Boolean);
          setOpcoesOperacoesCaixa(normalized as any[]);
        })
        .catch(() => setOpcoesOperacoesCaixa([]));
    }).catch(() => {});
  }, [tipoAtivo]);

  // Quando sai do Fluxo de Caixa ou muda de tipo, resetar dados se ainda estiverem vazios
  // para garantir nova renderiza+º+úo do AG Grid
  React.useEffect(() => {
    if (tipoAtivo !== 'fluxo' && dados.length === 0) {
      setGridApi(null);
    }
  }, [tipoAtivo]);

  const carregarOpcoesCobranca = async () => {
    try {
      // Passar o tipo correto baseado no tipoAtivo (receber ou pagar)
      const tipo = tipoAtivo === 'receber' ? 'receber' : 'pagar';
      const opcoes = await RelatoriosService.buscarTiposCobranca(tipo);
      setOpcoesCobranca(opcoes);
    } catch (error) {
      console.error('Erro ao carregar op+º+Áes de cobran+ºa:', error);
      // Fallback: usando dados mocados se falhar a chamada
      setOpcoesCobranca([
        { codigo: '001', descricao: '+Ç Vista' },
        { codigo: '002', descricao: 'Boleto' },
        { codigo: '003', descricao: 'Credi+írio' },
        { codigo: '004', descricao: 'Cheque' },
        { codigo: '005', descricao: 'Transfer+¬ncia' },
        { codigo: '006', descricao: 'Cart+úo' }
      ]);
    }
  };

  const carregarTiposDocumento = async (filtrarPorFolha: boolean = false) => {
    try {
      console.log('­ƒöä Carregando tipos de documento...', filtrarPorFolha ? '(Apenas FOLHA)' : '(Todos)');
      
      let url = '/api/tabelas-auxiliares/tipos-documento';
      const params = new URLSearchParams();
      
      if (filtrarPorFolha) {
        params.append('abrev', 'FOLHA');
      }
      if (filtros.tipo === 'pagar') {
        params.append('tipo', 'pagar');
      }
      
      if (params.toString()) {
        url += '?' + params.toString();
      }
      
      console.log('­ƒôí URL:', url);
      const response = await fetch(url);
      console.log('­ƒôí Response status:', response.status);
      
      if (response.ok) {
        const tipos = await response.json();
        console.log('Ô£à Tipos de documento recebidos:', tipos);
        console.log('­ƒôè Total de tipos:', tipos.length);
        
        const mapeados = tipos.map((t: any) => ({
          codigo: t.codigo || t.codigo_doc || t.codigo_docp,
          descricao: t.descricao || t.descr_doc || t.descr_docp,
          abrev: t.abrev || t.abrev_doc || t.abrev_docp
        }));
        
        console.log('Ô£à Tipos mapeados:', mapeados);
        setOpcoesTipoDocumento(mapeados);
      } else {
        throw new Error('Erro ao carregar tipos de documento');
      }
    } catch (error) {
      console.error('ÔØî Erro ao carregar tipos de documento:', error);
      setOpcoesTipoDocumento([]);
    }
  };

  // Recarregar tipos de documento quando checkbox Folha de Pagamento mudar
  useEffect(() => {
    if (filtros.tipo === 'pagar') {
      carregarTiposDocumento(filtros.folhaPagamento || false);
    }
  }, [filtros.folhaPagamento, filtros.tipo]);

  // L+¬ query param `tab` para abrir a aba diretamente (ex: /financeiro/relatorios?tab=consulta_caixa)
  const location = useLocation();

  React.useEffect(() => {
    try {
      const params = new URLSearchParams(location.search);
      const tab = params.get('tab');
      if (tab === 'consulta_caixa') {
        handleSubMenuClick('consulta_caixa');
      } else if (tab === 'fluxo') {
        handleSubMenuClick('fluxo');
      } else if (tab === 'receber' || tab === 'pagar') {
        // for+ºa mudan+ºa para receber/pagar se solicitado
        handleSubMenuClick(tab as TipoRelatorio);
      }
    } catch (e) {
      // n+úo bloquear em caso de erro de parsing
      console.warn('Erro ao ler query param tab:', e);
    }
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [location.search]);

  // Evitar chamar carregarPrevisoesPorDatas durante o render; usar useEffect
  React.useEffect(() => {
    if (filtros.tipo !== 'fluxo') return;
    try {
      const datas = Array.from(new Set((dados || []).map((r: any) => {
        const rawVenci = pickField(r, ['dtvenci_rec','dtvenci','dtvenc','dtvenc_rec','dtvenci_pag']) || r.data || r.dtmovi_cai || r.dtmovi || r.dtmovi_rec || r.dtmovi_pag || r.datai || '';
        return formatYMDUTC(rawVenci);
      }).filter((x: any) => x)));
      if (datas.length > 0) {
        carregarPrevisoesPorDatas(datas);
      }
    } catch (e) {
      // noop
    }
  }, [dados, filtros.tipo]);

  const carregarDepartamentos = async () => {
    try {
      const deptos = await RelatoriosService.buscarDepartamentos();
      setOpcoesDeptos(deptos);
    } catch (error) {
      console.error('Erro ao carregar departamentos:', error);
      setOpcoesDeptos([]);
    }
  };

  // Calcular saldos por banco sempre que os dados mudarem
  React.useEffect(() => {
    try {
      const map: Record<string, number> = {};
      (dados || []).forEach((r: any) => {
        const names = [r.nomefan_bco, r.nome_bco, r.banco, r.banco_nome, r.cliente_banco];
        const bancoName = names.find((n: any) => n) || 'OUTROS';
        const key = String(bancoName || 'OUTROS');
        const valor = Number(r.vlrsal_rec ?? r.vlrsal_pag ?? r.valor ?? r.vlr ?? 0) || 0;
        map[key] = (map[key] || 0) + valor;
      });
      setSaldosPorBanco(map);
      // manter op+º+Áes de banco derivadas dos dados
      const opts = Object.keys(map);
      setBancoOptions(opts);
    } catch (e) {
      setSaldosPorBanco({});
    }
  }, [dados, user]);

  // Carregar bancos e saldos a partir de dashboard_queries.id = 15 (cards de bancos)
  const carregarBancosDashboard = React.useCallback(async () => {
    try {
      const params: any = {};
      // 1) Preferir filial vinda da sess+úo (AuthContext)
      try {
        if (user) {
          const u: any = user as any;
          const candidatesUser = ['codigoFilSelecionado','codigo_filial','codigoFilial','filial','id_fil','codigoFil','defaultFilial','filialSelecionada','filial'];
          for (const k of candidatesUser) {
            if (u[k]) { params.filial = String(u[k]); break; }
          }
          if (!params.filial && (u.filial || u.filialId || u.id_fil)) {
            params.filial = String(u.filial || u.filialId || u.id_fil);
          }
        }
      } catch (e) {
        // ignore
      }

      // 2) Em seguida, tentar localStorage (compatibilidade retroativa)
      if (!params.filial) {
        try {
          const stored = localStorage.getItem('user');
          if (stored) {
            const parsed = JSON.parse(stored);
            const candidates = ['codigoFilSelecionado','codigo_filial','codigoFilial','filial','id_fil','codigoFil','defaultFilial','filialSelecionada'];
            for (const k of candidates) {
              if (parsed[k]) { params.filial = String(parsed[k]); break; }
            }
            if (!params.filial) {
              for (const v of Object.values(parsed)) {
                if (typeof v === 'string' && /^\d{1,4}$/.test(v)) { params.filial = v; break; }
              }
            }
          }
        } catch (e) {
          // ignorar erros de parse
        }
      }

      // Garantir filial padr+úo caso n+úo exista (evita retorno vazio da dashboard query)
      if (!params.filial) {
        params.filial = '001';
      }
      // Normalizar filial para 3 d+¡gitos ('1' -> '001') pois backend costuma esperar 3 d+¡gitos
      try {
        params.filial = String(params.filial || '001').padStart(3, '0');
      } catch (e) {
        params.filial = '001';
      }
      try { console.debug('[carregarBancosDashboard] calling dashboard-query 15 with params=', params); } catch(e) {}
      const resp = await RelatoriosService.executarDashboardQuery(15, params);
      const rows = resp?.rows || [];
      const map: Record<string, number> = {};
      const opts: string[] = [];
      rows.forEach((r: any) => {
        const banco = r.Banco || r.banco || r.nomefan_bco || r.nome_bco || r.banco_nome || r.cliente_banco || 'OUTROS';
        // suportar diferentes nomes de campo retornados (com/sem underscore ou espa+ºo)
        const saldo = Number(r['Saldo Atual'] ?? r['Saldo_Atual'] ?? r.saldo ?? r.saldo_cai ?? r.valor ?? r.amount ?? 0) || 0;
        const key = String(banco || 'OUTROS');
        if (!opts.includes(key)) opts.push(key);
        map[key] = (map[key] || 0) + saldo;
      });
      // Debug: log para inspe+º+úo em console do navegador
      try { console.debug('[carregarBancosDashboard] rowsCount=', rows.length, 'opts=', opts, 'map=', map); } catch(e) {}
      const normalizeKeys = (m: Record<string, number>) => {
        const out: Record<string, number> = {};
        Object.keys(m || {}).forEach(k => {
          const key = String(k || '').trim();
          if (!key) return;
          out[key] = (out[key] || 0) + Number(m[k] || 0);
        });
        return out;
      };

      const setIfHas = (optsArr: string[], mapObj: Record<string, number>) => {
        const filteredOpts = Array.from(new Set((optsArr || []).map((x: any) => String(x || '').trim()).filter((x: string) => x.length > 0)));
        const normalizedMap = normalizeKeys(mapObj);
        if (filteredOpts.length > 0) {
          setBancoOptions(filteredOpts);
          setSaldosPorBanco(normalizedMap);
          return true;
        }
        return false;
      };

      if (!setIfHas(opts, map)) {
        // Fallback: se a dashboard query n+úo retornou linhas, tentar derivar bancos a partir dos dados j+í carregados
        console.warn('Dashboard query id=15 retornou vazio - usando fallback a partir de `dados`.');
        const fallbackMap: Record<string, number> = {};
        (dados || []).forEach((r: any) => {
          const names = [r.nomefan_bco, r.nome_bco, r.banco, r.banco_nome, r.cliente_banco];
          const bancoName = names.find((n: any) => n) || 'OUTROS';
          const key = String(bancoName || 'OUTROS');
          const valor = Number(r.vlrsal_rec ?? r.vlrsal_pag ?? r.valor ?? r.vlr ?? 0) || 0;
          fallbackMap[key] = (fallbackMap[key] || 0) + valor;
        });
        const fallbackOpts = Object.keys(fallbackMap);
        if (!setIfHas(fallbackOpts, fallbackMap)) {
          // +Ültimo recurso: tentar chamada com filial padr+úo '001' (muitos ambientes usam '001')
          try {
            const filialRetry = params.filial || '001';
            const retryResp = await RelatoriosService.executarDashboardQuery(15, { filial: filialRetry });
            const retryRows = retryResp?.rows || [];
            const retryMap: Record<string, number> = {};
            const retryOpts: string[] = [];
            retryRows.forEach((r: any) => {
              const banco = r.Banco || r.banco || r.nomefan_bco || r.nome_bco || r.banco_nome || r.cliente_banco || 'OUTROS';
              const saldo = Number(r['Saldo Atual'] ?? r['Saldo_Atual'] ?? r.saldo ?? r.saldo_cai ?? r.valor ?? r.amount ?? 0) || 0;
              const key = String(banco || 'OUTROS');
              if (!retryOpts.includes(key)) retryOpts.push(key);
              retryMap[key] = (retryMap[key] || 0) + saldo;
            });
            if (!setIfHas(retryOpts, retryMap)) {
              // Nada encontrado: n+úo sobrescrever op+º+Áes existentes - apenas setar OUTROS se estiver vazio
              setBancoOptions(prev => (Array.isArray(prev) && prev.length > 0) ? prev : ['OUTROS']);
              setSaldosPorBanco(prev => (prev && Object.keys(prev).length > 0) ? prev : { 'OUTROS': 0 });
            }
            } catch (e) {
            console.warn('Retentativa dashboard query id=15 com filial=001 falhou:', e);
            setBancoOptions(prev => (Array.isArray(prev) && prev.length > 0) ? prev : ['OUTROS']);
            setSaldosPorBanco(prev => (prev && Object.keys(prev).length > 0) ? prev : { 'OUTROS': 0 });
          }
        }
      }
    } catch (err) {
      console.warn('Falha ao carregar bancos via dashboard query id=3:', err);
    }
  }, [dados, user]);

  // Quando o tipo for fluxo, carregar os bancos/saldos via dashboard query
  React.useEffect(() => {
    if (filtros.tipo === 'fluxo') {
      carregarBancosDashboard();
    }
  }, [filtros.tipo, carregarBancosDashboard]);

  // Quando o usu+írio abrir a aba 'consulta_caixa', garantir que as op+º+Áes de banco sejam carregadas
  React.useEffect(() => {
    if (tipoAtivo === 'consulta_caixa') {
      // Tentar carregar bancos do dashboard (cards) para popular o select de bancos
      carregarBancosDashboard();
      // Tamb+®m tentar derivar op+º+Áes a partir dos dados j+í existentes
      if ((dados || []).length > 0) {
        const bancosSet = new Set<string>(bancoOptions || []);
        (dados || []).forEach((r: any) => {
          const names = [r.nomefan_bco, r.nome_bco, r.banco, r.banco_nome, r.cliente_banco];
          names.forEach((n: any) => { if (n) bancosSet.add(String(n)); });
        });
        const novos = Array.from(bancosSet);
        if (novos.length > 0) setBancoOptions(novos);
      }
    }
  }, [tipoAtivo]);

  // Carregar KPIs globais do Fluxo (totais financeiros independentes da sele+º+úo)
  const carregarFluxoKPIs = React.useCallback(async () => {
    try {
      // Tentativa 1: pedir ao backend via mesmo endpoint sem filtros (se suportado)
      let resp: any = null;
      // Construir filtro m+¡nimo conforme tipo FiltroRelatorio para satisfazer TS
      const globalFiltro: FiltroRelatorio = {
        tipo: 'fluxo',
        tipoDataFiltro: 'vencimento',
        dataFiltroInicial: '',
        dataFiltroFinal: '',
        pessoaTipo: '',
        tipoCobranca: '',
        tipoDocumento: '',
        departamento: '',
        centroCusto: '',
        faixaAtraso: '',
        soEmAberto: false,
        soPagos: false
      };
      try {
        resp = await RelatoriosService.buscarFluxoCaixa(globalFiltro);
      } catch (e) {
        try { console.debug('[carregarFluxoKPIs] buscarFluxoCaixa com filtro global falhou:', e); } catch(er) {}
        resp = null;
      }

      let rows: any[] = [];
      if (Array.isArray(resp)) rows = resp;
      else if (resp && Array.isArray(resp.rows)) rows = resp.rows;
      else if (resp && Array.isArray(resp.data)) rows = resp.data;
      else if (resp && Array.isArray(resp.value)) rows = resp.value; // suportar backend que retorna { value: [...] }

      // Filtrar linhas que representam subtotais/rolling windows (ex: subtotal_30dias)
      // para evitar dupla contagem quando o backend retorna linhas de detalhe + subtotal.
      const detectTipoLinha = (r: any): string | null => {
        if (!r || typeof r !== 'object') return null;
        const candidateKeys = Object.keys(r || []);
        // procurar por poss+¡veis nomes de campo que indiquem tipo de linha
        const patterns = [/isTipoLinha/i, /tipoLinha/i, /is_tipo_linha/i, /tipo_linha/i, /linhaTipo/i, /isTipo/i, /is_subtotal/i, /subtotal/i, /tipo/i];
        for (const k of candidateKeys) {
          for (const p of patterns) {
            if (p.test(k)) {
              const val = r[k];
              if (val === undefined || val === null) return '';
              return String(val || '').trim();
            }
          }
        }
        return null;
      };

      const rowsFiltered = (rows || []).filter((r: any) => {
        try {
          if (!r) return false;
          const tipo = detectTipoLinha(r);
          if (tipo === null) return true; // sem meta-informacao -> manter
          const v = String(tipo || '').toLowerCase();
          // aceitar somente linhas explicitamente normais / detalhe
          if (v === '' || v === 'normal' || v === 'item' || v === 'detail' || v === 'detalhe' || v === 'linha') return true;
          // caso contr+írio (subtotal_30dias, subtotal, rolling, total_...), ignorar
          return false;
        } catch (e) {
          return true;
        }
      });

      // Deduplicar linhas por documento para evitar contagem duplicada
      const dedupeRowsByDoc = (arr: any[]) => {
        const seen = new Set<string>();
        const out: any[] = [];
        for (const r of arr) {
          try {
            // tentar extrair uma chave +¦nica de documento (preferir ids/numdup/docto)
            const docId = pickField(r, ['receber_id','codigo_rec','numdup_rec','docto_rec','codigo_rec']) || pickField(r, ['pagar_id','codigo_pag','numdup_pag','docto_pag','codigo_pag']);
            const tipoFlag = (r.tipo || r.__origem || (r.dc && String(r.dc).toUpperCase() === 'C' ? 'ENTRADA' : 'SAIDA') || '').toString();
            const key = `${tipoFlag}::${String(docId || '').trim()}::${formatYMDUTC(r.data || r.dtvenci_rec || r.dtvenci_pag || r.dtmovi || '')}`;
            if (!key || key === '::::') {
              // fallback: usar combina+º+úo de valor+data+descricao curta
              const fallback = `${String(r.valor||r.vlrsal_rec||r.vlrsal_pag||0)}::${formatYMDUTC(r.data||r.dtvenci_rec||r.dtvenci_pag||'')}`;
              if (seen.has(fallback)) continue;
              seen.add(fallback);
              out.push(r);
            } else {
              if (seen.has(key)) continue;
              seen.add(key);
              out.push(r);
            }
          } catch (e) {
            out.push(r);
          }
        }
        return out;
      };

      const rowsDedup = dedupeRowsByDoc(rowsFiltered);

      if (!rows || rows.length === 0) {
        // fallback: tentar derivar a partir do estado `dados` j+í carregado
        rows = dados || [];
      }

      // Agregar entradas/saidas (usar lista filtrada para evitar subtotais)
      const gruposData: Record<string, { entradas: number; saidas: number }> = (rowsDedup || []).reduce((acc: Record<string, { entradas: number; saidas: number }>, row: any) => {
        // Use explicit vencimento field as grouping key (same as mestre/lista)
        const rawVenci = pickField(row, ['dtvenci_rec','dtvenci','dtvenc','dtvenc_rec','dtvenci_pag','dtvenci_rec']) || row.data || row.dtmovi_cai || row.dtmovi || row.dtmovi_rec || row.dtmovi_pag || row.datai || '';
        const key = formatYMDUTC(rawVenci) || '_ALL_';
        if (!acc[key]) acc[key] = { entradas: 0, saidas: 0 };

        let e = Number(row.entradas ?? 0) || 0;
        let s = Number(row.saidas ?? 0) || 0;
        if (!e && !s) {
          e = Number(row.vlrsal_rec ?? row.vlrdup_rec ?? row.valor ?? row.amount ?? 0) || 0;
          s = Number(row.vlrsal_pag ?? row.vlrdup_pag ?? 0) || 0;
          if ((!e && !s) && row.dc) {
            const v = Number(row.valor ?? row.amount ?? 0) || 0;
            if (String(row.dc).toUpperCase() === 'C') e = v; else s = v;
          }
        }

        acc[key].entradas += Number(e) || 0;
        acc[key].saidas += Number(s) || 0;
        return acc;
      }, {} as Record<string, { entradas: number; saidas: number }>);

      const totalEntradas: number = Object.values(gruposData).reduce((s: number, g: { entradas: number; saidas: number }) => s + (Number(g.entradas) || 0), 0);
      const totalSaidas: number = Object.values(gruposData).reduce((s: number, g: { entradas: number; saidas: number }) => s + (Number(g.saidas) || 0), 0);
      const saldoFinal: number = totalEntradas - totalSaidas;

      // Tamb+®m buscar detalhes do dia (HOJE) no backend para garantir que o card HOJE reflita o total do servidor
      let entradasHoje = 0;
      let saidasHoje = 0;
      try {
        const hojeKey = formatYMDUTC(new Date());
        const detalhesHoje: any = await RelatoriosService.buscarDetalhesFluxoDia(hojeKey, false);
        if (detalhesHoje) {
          // Se o backend retornar 'totais', usar diretamente
          if (detalhesHoje.totais && (detalhesHoje.totais.entradas !== undefined || detalhesHoje.totais.saidas !== undefined)) {
            entradasHoje = Number(detalhesHoje.totais.entradas || 0) || 0;
            saidasHoje = Number(detalhesHoje.totais.saidas || 0) || 0;
          } else {
            // Caso contr+írio, somar arrays receber/pagar
            if (Array.isArray(detalhesHoje.receber)) entradasHoje = detalhesHoje.receber.reduce((s: number, r: any) => s + (Number(r.vlrsal_rec || r.vlrdup_rec || r.valor || 0) || 0), 0);
            if (Array.isArray(detalhesHoje.pagar)) saidasHoje = detalhesHoje.pagar.reduce((s: number, r: any) => s + (Number(r.vlrsal_pag || r.vlrdup_pag || r.valor || 0) || 0), 0);
          }
        }
      } catch (errDetalhes) {
        // se falhar, manter entradasHoje/saidasHoje calculadas via rowsDedup (se houvesse)
        try {
          const hojeKey = formatYMDUTC(new Date());
          entradasHoje = Number((gruposData[hojeKey] && gruposData[hojeKey].entradas) || 0) || 0;
          saidasHoje = Number((gruposData[hojeKey] && gruposData[hojeKey].saidas) || 0) || 0;
        } catch (e) {
          entradasHoje = 0; saidasHoje = 0;
        }
      }

      setFluxoGlobalKPIs({ totalEntradas, totalSaidas, saldoFinal, entradasHoje, saidasHoje, saldoHoje: entradasHoje - saidasHoje });
    } catch (e) {
      console.warn('Erro ao carregar fluxoGlobalKPIs:', e);
      setFluxoGlobalKPIs(null);
    }
  }, [dados]);

  React.useEffect(() => {
    if (filtros.tipo === 'fluxo') carregarFluxoKPIs();
  }, [filtros.tipo, carregarFluxoKPIs]);

  // Heur+¡sticas para identificar se uma linha do detalhe pertence a RECEBER ou PAGAR
  function isReceberRow(row: any): boolean {
    if (!row) return false;
    const receberIndicators = ['receber_id','vlrsal_rec','vlrdup_rec','codigo_rec','numdup_rec','dtemissi_rec','dtvenci_rec'];
    return receberIndicators.some(k => Object.prototype.hasOwnProperty.call(row, k));
  }
  function isPagarRow(row: any): boolean {
    if (!row) return false;
    const pagarIndicators = ['pagar_id','vlrsal_pag','vlrdup_pag','codigo_pag','numdup_pag','dtemissi_pag','dtvenci_pag'];
    return pagarIndicators.some(k => Object.prototype.hasOwnProperty.call(row, k));
  }

  // KPIs do Fluxo calculados a partir dos dados carregados
  // Helpers reintroduzidos: parse e formata+º+úo local (movidos antes do uso para evitar ReferenceError)
  function formatarMoeda(valor: number) {
    return new Intl.NumberFormat('pt-BR', {
      style: 'currency',
      currency: 'BRL'
    }).format(valor || 0);
  }

  function formatarData(data: string) {
    if (!data) return '';
    // Usar parseDateLocal para evitar que strings 'YYYY-MM-DD' sejam interpretadas como UTC
    // (o que pode causar off-by-one em fusos negativos como -03:00)
    const d = parseDateLocal(data);
    if (!d) return '';
    return d.toLocaleDateString('pt-BR');
  }

  function parseDateLocal(value: any): Date | null {
    if (!value) return null;
    try {
      if (value instanceof Date) return value;
      const s = String(value).trim();
      // DD/MM/YYYY
      const matchDMY = s.match(/^(\d{2})\/(\d{2})\/(\d{4})$/);
      if (matchDMY) {
        const [, d, m, y] = matchDMY;
        return new Date(Number(y), Number(m) - 1, Number(d));
      }
      // YYYY-MM-DD or YYYY-MM-DDTHH:MM:SS(.sss)Z? -> treat as local date (ignore time/fuso)
      const matchYMD = s.match(/^(\d{4})-(\d{2})-(\d{2})(?:[T ].*)?$/);
      if (matchYMD) {
        const [, y, m, d] = matchYMD;
        return new Date(Number(y), Number(m) - 1, Number(d));
      }

      // Compact YMD like YYYYMMDD
      const matchYMD8 = s.match(/^(\d{4})(\d{2})(\d{2})$/);
      if (matchYMD8) {
        const [, y, m, d] = matchYMD8;
        return new Date(Number(y), Number(m) - 1, Number(d));
      }

      // Fallback: let Date parse (may include time and timezone)
      return new Date(s);
    } catch (e) {
      return null;
    }
  }

  // formatYMDLocal removed (unused)

  // Normaliza uma data para YYYY-MM-DD usando a interpreta+º+úo UTC
  // Recebe Date | string e retorna string YYYY-MM-DD considerando componentes UTC.
  function formatYMDUTC(dateLike: any): string {
    if (!dateLike) return '';
    try {
      if (dateLike instanceof Date) {
        const y = dateLike.getUTCFullYear();
        const m = String(dateLike.getUTCMonth() + 1).padStart(2, '0');
        const d = String(dateLike.getUTCDate()).padStart(2, '0');
        return `${y}-${m}-${d}`;
      }
      const s = String(dateLike).trim();
      // DD/MM/YYYY -> convertendo diretamente
      const matchDMY = s.match(/^(\d{2})\/(\d{2})\/(\d{4})$/);
      if (matchDMY) {
        const [, dd, mm, yyyy] = matchDMY;
        return `${yyyy}-${mm}-${dd}`;
      }
      // YYYY-MM-DD (com ou sem time) -> extrair a parte Y-M-D
      const matchYMD = s.match(/^(\d{4})-(\d{2})-(\d{2})(?:[T ].*)?$/);
      if (matchYMD) {
        const [, yyyy, mm, dd] = matchYMD;
        return `${yyyy}-${mm}-${dd}`;
      }
      // fallback: deixar o Date tentar interpretar e usar UTC
      const dt = new Date(s);
      if (isNaN(dt.getTime())) return '';
      const y = dt.getUTCFullYear();
      const m = String(dt.getUTCMonth() + 1).padStart(2, '0');
      const d = String(dt.getUTCDate()).padStart(2, '0');
      return `${y}-${m}-${d}`;
    } catch (e) {
      return '';
    }
  }

  function parseNumeric(valor: any): number {
    if (valor == null) return 0;
    if (typeof valor === 'number') return isNaN(valor) ? 0 : valor;
    const s = String(valor).replace(/R\$|\s/g, '').replace(/\./g, '').replace(/,/g, '.');
    const n = parseFloat(s);
    return isNaN(n) ? 0 : n;
  }

  // Retorna documento MASCARADO (CPF/CNPJ) - apenas +¦ltimos 2 d+¡gitos vis+¡veis
  // Procura em v+írios nomes de campo poss+¡veis (compatibilidade com diferentes queries)
  function getMaskedDocumento(det: any, suffix?: 'rec' | 'pag' | ''): string {
    if (!det) return '';
    const sfx = suffix || '';
    const possibleTipoKeys = [
      `tipopessoa_${sfx}`,
      `tipopessoa${sfx ? '_' + sfx : ''}`,
      `tipo_pessoa`,
      `tipo`,
      `tipopessoa`,
      `cliforn_cli`
    ];
    // aceitar tamb+®m campos j+í formatados/formatados pelo backend/frontend (ex.: cgccpf_rec_formatted)
    const possibleCpfKeys = [
      `cgccpf_${sfx}`,
      `cgccpf${sfx ? '_' + sfx : ''}`,
      `cgccpf`,
      `cgccpf_${sfx}_formatted`,
      `cgccpf${sfx ? '_' + sfx + '_formatted' : '_formatted'}`,
      `cgccpf_formatted`,
      `cgc_cpf`,
      `cpf`,
      `cnpj`,
      `documento`,
      `document`,
      `cgccpf_rec`,
      `cgccpf_pag`,
      `cpf_cli`,
      `cpf_for`,
      `cgccpf_cli`
    ];

    let tipoPessoa: any = '';
    for (const k of possibleTipoKeys) {
      if (Object.prototype.hasOwnProperty.call(det, k) && det[k]) { tipoPessoa = det[k]; break; }
    }

    // Se existir campo j+í formatado (ex.: cgccpf_rec_formatted), usar como est+í (mant+®m comportamento do AG Grid)
    for (const k of possibleCpfKeys) {
      if (Object.prototype.hasOwnProperty.call(det, k) && det[k]) {
        // se o campo encontrado j+í cont+®m caracteres n+úo num+®ricos e aparenta estar formatado, retornamos diretamente
        const candidate = det[k];
        if (typeof candidate === 'string' && /\D/.test(candidate)) {
          return String(candidate);
        }
        // caso contr+írio, guarda para processamento num+®rico
        var rawVal: any = candidate;
        break;
      }
    }

    if (typeof rawVal === 'undefined' || rawVal == null) rawVal = '';

    if (!rawVal) return '';
    const digits = String(rawVal).replace(/\D/g, '');
    if (!digits) return '';

    const isCPF = (String(tipoPessoa).toUpperCase() === 'F') || digits.length === 11;
    const isCNPJ = (String(tipoPessoa).toUpperCase() === 'J') || digits.length === 14;

    // Manter comportamento compat+¡vel com AG-Grid: mascarar mostrando apenas os 2 +¦ltimos d+¡gitos
    if (isCPF && digits.length >= 11) {
      const d = digits.slice(-11);
      // Full formatted: 000.000.000-00
      const last2 = d.slice(-2);
      // manter formato padr+úo com h+¡fen antes dos 2 +¦ltimos d+¡gitos
      return `***.***.***-${last2}`;
    }
    if (isCNPJ && digits.length >= 14) {
      const d = digits.slice(-14);
      // Full formatted: 00.000.000/0000-00
      const last2 = d.slice(-2);
      // tentar preservar o separador original que o backend pode ter usado
      const rawStr = String(rawVal || '');
      // procurar um separador n+úo num+®rico imediatamente antes dos 2 +¦ltimos d+¡gitos
      const sepMatch = rawStr.match(/(\D)(\d{2})\s*$/);
      const sep = sepMatch ? sepMatch[1] : '-';
      return `**.***.***/****${sep}${last2}`;
    }

    return String(rawVal);
  }

  // Retorna documento COMPLETO E FORMATADO (CPF/CNPJ) - todos os d+¡gitos vis+¡veis
  // Procura em v+írios nomes de campo poss+¡veis (compatibilidade com diferentes queries)
  function getFormattedDocumento(det: any, suffix?: 'rec' | 'pag' | ''): string {
    if (!det) return '';
    const sfx = suffix || '';
    const possibleTipoKeys = [
      `tipopessoa_${sfx}`,
      `tipopessoa${sfx ? '_' + sfx : ''}`,
      `tipo_pessoa`,
      `tipo`,
      `tipopessoa`,
      `cliforn_cli`
    ];
    const possibleCpfKeys = [
      `cgccpf_${sfx}`,
      `cgccpf${sfx ? '_' + sfx : ''}`,
      `cgccpf`,
      `cgccpf_${sfx}_formatted`,
      `cgccpf${sfx ? '_' + sfx + '_formatted' : '_formatted'}`,
      `cgccpf_formatted`,
      `cgc_cpf`,
      `cpf`,
      `cnpj`,
      `documento`,
      `document`,
      `cgccpf_rec`,
      `cgccpf_pag`,
      `cpf_cli`,
      `cpf_for`,
      `cgccpf_cli`
    ];

    let tipoPessoa: any = '';
    for (const k of possibleTipoKeys) {
      if (Object.prototype.hasOwnProperty.call(det, k) && det[k]) { tipoPessoa = det[k]; break; }
    }

    // Se existir campo j+í formatado, usar como est+í
    for (const k of possibleCpfKeys) {
      if (Object.prototype.hasOwnProperty.call(det, k) && det[k]) {
        const candidate = det[k];
        if (typeof candidate === 'string' && /\D/.test(candidate)) {
          return String(candidate);
        }
        var rawVal: any = candidate;
        break;
      }
    }

    if (typeof rawVal === 'undefined' || rawVal == null) rawVal = '';

    if (!rawVal) return '';
    const digits = String(rawVal).replace(/\D/g, '');
    if (!digits) return '';

    const isCPF = (String(tipoPessoa).toUpperCase() === 'F') || digits.length === 11;
    const isCNPJ = (String(tipoPessoa).toUpperCase() === 'J') || digits.length === 14;

    // Formatar COMPLETO (sem mascarar)
    if (isCPF && digits.length >= 11) {
      const d = digits.slice(-11);
      // Formato: 000.000.000-00
      return `${d.slice(0, 3)}.${d.slice(3, 6)}.${d.slice(6, 9)}-${d.slice(9)}`;
    }
    if (isCNPJ && digits.length >= 14) {
      const d = digits.slice(-14);
      // Formato: 00.000.000/0000-00
      return `${d.slice(0, 2)}.${d.slice(2, 5)}.${d.slice(5, 8)}/${d.slice(8, 12)}-${d.slice(12)}`;
    }

    return String(rawVal);
  }

  // Helper: retorna o primeiro campo n+úo nulo/undef dentre uma lista de chaves poss+¡veis
  function pickField(det: any, candidates: string[]): any {
    if (!det) return null;
    for (const k of candidates) {
      if (Object.prototype.hasOwnProperty.call(det, k) && det[k] != null && String(det[k]).toString().trim() !== '') {
        return det[k];
      }
    }
    return null;
  }

  // Detecta linhas geradas como subtotal/agrupamento pelo backend
  function isSubtotalRow(r: any): boolean {
    if (!r || typeof r !== 'object') return false;
    try {
      const keys = Object.keys(r || {});
      for (const k of keys) {
        const lk = String(k).toLowerCase();
        if (lk.indexOf('subtotal') >= 0 || lk.indexOf('tipo_linha') >= 0 || lk.indexOf('is_tipo_linha') >= 0 || lk.indexOf('is_tipo') >= 0 || lk.indexOf('linha_tipo') >= 0) {
          const v = String(r[k] || '').toLowerCase();
          if (!v) return true; // chave existe sem valor -> prov+ível subtotal/marker
          // considerar normal/item/detalhe como v+ílido, caso contr+írio +® subtotal/rolling
          if (['normal','item','detalhe','linha','detail'].indexOf(v) === -1) return true;
        }
      }

      // casos comuns: backend pode retornar campo 'subtotal' booleano ou _subtotal
      if (r.subtotal === true || r._subtotal === true) return true;
      // ou campos que contenham 'total' mas n+úo contenham identificador de documento
      for (const k of keys) {
        const lk = String(k).toLowerCase();
        if (lk.indexOf('total') >= 0 && !Object.prototype.hasOwnProperty.call(r, 'receber_id') && !Object.prototype.hasOwnProperty.call(r, 'pagar_id')) {
          return true;
        }
      }
    } catch (e) {
      return false;
    }
    return false;
  }

  // Helper: busca por chave usando padr+Áes (case-insensitive, contains)
  function findByPattern(det: any, patterns: string[]): any {
    if (!det) return null;
    const keys = Object.keys(det || {});
    const lowerKeys = keys.map(k => String(k).toLowerCase());
    for (const p of patterns) {
      const lp = p.toLowerCase();
      // 1) exact match
      let idx = lowerKeys.indexOf(lp);
      if (idx >= 0) {
        const v = det[keys[idx]];
        if (v != null && String(v).trim() !== '') return v;
      }
      // 2) contains
      idx = lowerKeys.findIndex(k => k.indexOf(lp) >= 0);
      if (idx >= 0) {
        const v = det[keys[idx]];
        if (v != null && String(v).trim() !== '') return v;
      }
    }
    return null;
  }
  const fluxoKpis = React.useMemo(() => {
    if (filtros.tipo !== 'fluxo') return null;
    try {
      // Agrupar por data normalizada (YYYY-MM-DD)
      // Deduplicar linhas por documento antes de agregar (evita duplicidade entre fontes)
      const dedupeRowsByDocLocal = (arr: any[]) => {
        const seen = new Set<string>();
        const out: any[] = [];
        for (const r of (arr || [])) {
          try {
            const docId = pickField(r, ['receber_id','codigo_rec','numdup_rec','docto_rec','codigo_rec']) || pickField(r, ['pagar_id','codigo_pag','numdup_pag','docto_pag','codigo_pag']);
            const tipoFlag = (r.tipo || r.__origem || (r.dc && String(r.dc).toUpperCase() === 'C' ? 'ENTRADA' : 'SAIDA') || '').toString();
            const key = `${tipoFlag}::${String(docId || '').trim()}::${formatYMDUTC(r.data || r.dtvenci_rec || r.dtvenci_pag || r.dtmovi || '')}`;
            if (!key || key === '::::') {
              const fallback = `${String(r.valor||r.vlrsal_rec||r.vlrsal_pag||0)}::${formatYMDUTC(r.data||r.dtvenci_rec||r.dtvenci_pag||'')}`;
              if (seen.has(fallback)) continue;
              seen.add(fallback);
              out.push(r);
            } else {
              if (seen.has(key)) continue;
              seen.add(key);
              out.push(r);
            }
          } catch (e) {
            out.push(r);
          }
        }
        return out;
      };

      const dedupedDados = dedupeRowsByDocLocal(dados || []);

      const gruposData = (dedupedDados || []).reduce((acc: any, row: any) => {
        // Ignorar linhas que representam subtotais/agrupamentos retornados pelo backend
        if (row && row.isTipoLinha && String(row.isTipoLinha).toLowerCase() !== 'normal') {
          return acc;
        }

        const raw = row.data || row.dtmovi_cai || row.dtmovi || row.dtmovi_rec || row.dtvenci_rec || row.dtmovi_pag || row.datai || '';
        const key = formatYMDUTC(raw);
        if (!acc[key]) acc[key] = { entradas: 0, saidas: 0 };

        // Preferir campos expl+¡citos, cair para heur+¡stica quando ausentes
        let e = Number(row.entradas ?? 0) || 0;
        let s = Number(row.saidas ?? 0) || 0;

        if (!e && !s) {
          // tentar campos comuns do sistema
          e = Number(row.vlrsal_rec ?? row.vlrdup_rec ?? row.valor ?? row.amount ?? 0) || 0;
          s = Number(row.vlrsal_pag ?? row.vlrdup_pag ?? 0) || 0;
          // interpretar sinal/tipo se necess+írio
          if ((!e && !s) && row.dc) {
            const v = Number(row.valor ?? row.amount ?? 0) || 0;
            if (String(row.dc).toUpperCase() === 'C') e = v; else s = v;
          }
        }

        acc[key].entradas += e;
        acc[key].saidas += s;
        return acc;
      }, {});

      const grupos = Object.keys(gruposData).map(k => ({ dataKey: k, ...gruposData[k] }));
      const todayKey = formatYMDUTC(new Date());
      const gruposFiltrados = filterOnlyHoje ? grupos.filter((g: any) => g.dataKey === todayKey) : grupos;

      const totalEntradas = gruposFiltrados.reduce((s: number, g: any) => s + (Number(g.entradas) || 0), 0);
      const totalSaidas = gruposFiltrados.reduce((s: number, g: any) => s + (Number(g.saidas) || 0), 0);

      const hojeGroup = grupos.find((g: any) => g.dataKey === todayKey) || { entradas: 0, saidas: 0 };
      const entradasHoje = Number(hojeGroup.entradas) || 0;
      const saidasHoje = Number(hojeGroup.saidas) || 0;

      return { 
        totalEntradas, 
        totalSaidas, 
        saldoFinal: totalEntradas - totalSaidas, 
        entradasHoje, 
        saidasHoje, 
        saldoHoje: entradasHoje - saidasHoje 
      };
    } catch (e) {
      console.warn('Erro calculando fluxoKpis', e);
      return null;
    }
  }, [dados, filtros.tipo, filterOnlyHoje]);

  const subMenuItems = [
    {
      key: 'receber' as TipoRelatorio,
      label: 'Contas a Receber',
      icon: faMoneyBillWave,
      color: '#059669'
    },
    {
      key: 'pagar' as TipoRelatorio,
      label: 'Contas a Pagar',
      icon: faFileInvoiceDollar,
      color: '#dc2626'
    },
    {
      key: 'fluxo' as TipoRelatorio,
      label: 'Fluxo de Caixa',
      icon: faCashRegister,
      color: '#7c3aed'
    }
    ,{
      key: 'consulta_caixa' as TipoRelatorio,
      label: 'Consulta caixa e bancos',
      icon: faSearch,
      color: '#059669'
    }
  ];

  const handleSubMenuClick = (tipo: TipoRelatorio) => {
    setTipoAtivo(tipo);
    // IMPORTANTE: Resetar gridApi quando muda de tab para evitar estado inconsistente
    setGridApi(null);
    setTotalRow(null);
    
    // If switching to fluxo, set sensible defaults (HOJE) and fetch immediately
    if (tipo === 'fluxo') {
        const hoje = new Date();
        const dataFinal = new Date(hoje);
        dataFinal.setDate(dataFinal.getDate()); // default period = today
        const newFiltros: FiltroRelatorio = {
        ...filtros,
        tipo: 'fluxo',
        tipoCampoData: filtros.tipoCampoData || undefined,
        faixaAtraso: '',
        dataFiltroInicial: formatYMDUTC(hoje),
        dataFiltroFinal: formatYMDUTC(dataFinal)
      };
      setFiltros(newFiltros);
      setDados([]);
      buscarDados(newFiltros);
    } else {
      setFiltros(prev => ({ 
        ...prev, 
        tipo,
        tipoCampoData: tipo === 'receber' ? 'dtvenci_rec' : (tipo === 'pagar' ? 'dtvenci_pag' : prev.tipoCampoData)
      }));
      setDados([]);
    }
  };

  const handleFilterChange = (field: keyof FiltroRelatorio, value: any) => {
    // Se mudou o checkbox Folha de Pagamento, limpar sele+º+úo de tipos de documento
    if (field === 'folhaPagamento') {
      setFiltros(prev => ({ 
        ...prev, 
        [field]: value,
        tiposDocumento: [], // Limpar array de m+¦ltiplas sele+º+Áes
        tipoDocumento: ''   // Limpar sele+º+úo +¦nica
      }));
    } else {
      setFiltros(prev => ({ ...prev, [field]: value }));
    }
  };

  // handleSearchChange removed (searchText state unused). Use AG-Grid quick filter directly via `gridApi.setQuickFilter` where needed.

  // Recalcular TOTAL baseado nos dados filtrados
  const recalcularTotal = () => {
    if (!gridApi || !gridApi.forEachNodeAfterFilter) return;
    
    const visibleRows: any[] = [];
    gridApi.forEachNodeAfterFilter((node: any) => {
      if (node.data) {
        visibleRows.push(node.data);
      }
    });

    console.log('­ƒöì DEBUG recalcularTotal:', {
      tipoAtivo,
      visibleRowsCount: visibleRows.length,
      primeiraLinha: visibleRows[0],
      ultimaLinha: visibleRows[visibleRows.length - 1]
    });

    // Fun+º+úo auxiliar para extrair n+¦mero de string ou n+¦mero
    const extrairNumero = (valor: any): number => {
      if (typeof valor === 'number') {
        return isNaN(valor) ? 0 : valor;
      }
      if (!valor) return 0;
      
      const str = String(valor).trim();
      // Remove R$, espa+ºos, remove ponto de milhar, substitui v+¡rgula por ponto
      const limpo = str
        .replace(/R\$/g, '')
        .replace(/\s/g, '')
        .replace(/\./g, '') // Remove ponto de milhar
        .replace(/,/g, '.'); // Converte v+¡rgula para ponto
      
      const num = parseFloat(limpo);
      return isNaN(num) ? 0 : num;
    };

    if (tipoAtivo === 'receber') {
      const sumValorDup = visibleRows.reduce((sum, row) => {
        const valor = extrairNumero(row.vlrdup_rec);
        console.log('  vlrdup_rec:', row.vlrdup_rec, 'ÔåÆ', valor);
        return sum + valor;
      }, 0);
      
      const sumSaldo = visibleRows.reduce((sum, row) => {
        const valor = extrairNumero(row.vlrsal_rec);
        console.log('  vlrsal_rec:', row.vlrsal_rec, 'ÔåÆ', valor);
        return sum + valor;
      }, 0);

      const newTotal = {
        nome_cli: 'Ô£ô TOTAL:',
        vlrdup_rec: sumValorDup,
        vlrsal_rec: sumSaldo
      };
      console.log('Ô£à TOTAL Receber:', newTotal);
      setTotalRow(newTotal);
    } else {
      const sumValorDup = visibleRows.reduce((sum, row) => {
        const valor = extrairNumero(row.vlrdup_pag);
        console.log('  vlrdup_pag:', row.vlrdup_pag, 'ÔåÆ', valor);
        return sum + valor;
      }, 0);
      
      const sumSaldo = visibleRows.reduce((sum, row) => {
        const valor = extrairNumero(row.vlrsal_pag);
        console.log('  vlrsal_pag:', row.vlrsal_pag, 'ÔåÆ', valor);
        return sum + valor;
      }, 0);

      const newTotal = {
        nome_for: 'Ô£ô TOTAL:',
        vlrdup_pag: sumValorDup,
        vlrsal_pag: sumSaldo
      };
      console.log('Ô£à TOTAL Pagar:', newTotal);
      setTotalRow(newTotal);
    }
  };

  // Buscar Consulta Caixa e Bancos
  const buscarConsultaCaixa = async () => {
    try {
      console.log('[DEBUG-CONSULTA-CAIXA] buscarConsultaCaixa() iniciada');
      setConsultaLoading(true);
      setConsultaDados([]);
      const params: any = {
        dataInicial: consultaDataInicial || undefined,
        dataFinal: consultaDataFinal || undefined,
        centroCusto: consultaCentroCusto || undefined,
        operacao: consultaOperacao || undefined,
        mascai: consultaMascai || undefined
      };
      if (bancoSelecionado) params.banco = bancoSelecionado;
      if (bancoSelecionado) params.codbanco_cai = bancoSelecionado; // backend may expect codbanco_cai filter
      console.log('[DEBUG-CONSULTA-CAIXA] params:', params);
      const resp = await RelatoriosService.buscarConsultaCaixa(params);
      console.log('[DEBUG-CONSULTA-CAIXA] resp bruta:', resp);
      const data = Array.isArray(resp) ? resp : [];

      // Buscar previs+Áes de receitas e despesas por opera+º+úo
      let previsoes: any = {};
      try {
        const prevRec = await RelatoriosService.buscarPrevisaoReceitas();
        const prevDesp = await RelatoriosService.buscarPrevisaoDespesas();
        console.log('[DEBUG-CONSULTA-CAIXA] Previs+Áes Receitas:', prevRec);
        console.log('[DEBUG-CONSULTA-CAIXA] Previs+Áes Despesas:', prevDesp);
        
        // Mapear previs+Áes por opera+º+úo
        // Usar AMBAS as chaves: por descr_ocai (match com oper_cai text) E por operacao_ocai
        if (Array.isArray(prevRec)) {
          prevRec.forEach((p: any) => {
            // Chave 1: C-descricao (match com grid que tem oper_cai como texto)
            const keyDescr = `C-${p.descr_ocai || p.operacao_ocai}`;
            previsoes[keyDescr] = { ...p, tipo: 'C', previsto: p.valor_previsto || 0, desvioper: p.percentual_desvio || 0 };
            // Chave 2: C-operacao_ocai (fallback)
            const keyOper = `C-${p.operacao_ocai}`;
            if (!previsoes[keyOper]) {
              previsoes[keyOper] = { ...p, tipo: 'C', previsto: p.valor_previsto || 0, desvioper: p.percentual_desvio || 0 };
            }
          });
        }
        if (Array.isArray(prevDesp)) {
          prevDesp.forEach((p: any) => {
            // Chave 1: D-descricao
            const keyDescr = `D-${p.descr_ocai || p.operacao_ocai}`;
            previsoes[keyDescr] = { ...p, tipo: 'D', previsto: p.valor_previsto || 0, desvioper: p.percentual_desvio || 0 };
            // Chave 2: D-operacao_ocai (fallback)
            const keyOper = `D-${p.operacao_ocai}`;
            if (!previsoes[keyOper]) {
              previsoes[keyOper] = { ...p, tipo: 'D', previsto: p.valor_previsto || 0, desvioper: p.percentual_desvio || 0 };
            }
          });
        }
        setPrevisoesPorOperacao(previsoes);
        console.log('[DEBUG-CONSULTA-CAIXA] Previs+Áes mapeadas:', previsoes);
        console.log('[DEBUG-CONSULTA-CAIXA] Chaves de previs+úo:', Object.keys(previsoes));
      } catch (e) {
        console.warn('[DEBUG-CONSULTA-CAIXA] Erro ao buscar previs+Áes:', e);
        setPrevisoesPorOperacao({});
      }

      // Normalizar campos para o AG Grid:
      // - dtmovi_cai: 'YYYYMMDD' -> 'YYYY-LL-DD' (leg+¡vel)
      // - valor_cai: garantir n+¦mero
      // - nome_cai: fallback para banco_cai
      // - oper_cai: extrair descri+º+úo se for objeto
      const mapped = data.map((r: any) => {
        const out: any = { ...r };

        // data: 20251218 -> 2025-12-18
        try {
          if (typeof out.dtmovi_cai === 'string') {
            const s = out.dtmovi_cai.trim();
            if (/^\d{8}$/.test(s)) {
              out.dtmovi_cai = `${s.slice(0,4)}-${s.slice(4,6)}-${s.slice(6,8)}`;
            }
          }
        } catch (e) { /* noop */ }

        // valor como n+¦mero
        try {
          if (out.valor_cai !== undefined && out.valor_cai !== null) {
            const n = Number(out.valor_cai);
            out.valor_cai = isNaN(n) ? 0 : n;
          } else if (out.valor !== undefined) {
            const n = Number(out.valor);
            out.valor_cai = isNaN(n) ? 0 : n;
          } else {
            out.valor_cai = 0;
          }
        } catch (e) { out.valor_cai = 0; }

      // nome do caixa/banco
      if (!out.nome_cai || String(out.nome_cai).trim() === '') {
        out.nome_cai = out.tipocai_cai || out.banco || out.codbanco_cai || '';
        }

        // operacao: se for objeto, extrair descr_ocai / descr_ope E PRESERVAR operacao_ocai
        if (out.oper_cai && typeof out.oper_cai === 'object') {
          // Guardar o c+¦digo da opera+º+úo para posterior uso no formul+írio
          out.operacao_ocai = out.oper_cai.operacao_ocai || out.oper_cai.codigo || null;
          // Transformar oper_cai em string (descri+º+úo)
          out.oper_cai = out.oper_cai.descr_ocai || out.oper_cai.descr_ope || String(out.oper_cai || '');
        }

        return out;
      });

      try { console.debug('[buscarConsultaCaixa] mapped count=', Array.isArray(mapped) ? mapped.length : 0, mapped && mapped.length ? mapped[0] : null); } catch(e) {}
      console.log('[DEBUG-CONSULTA-CAIXA] mapped final:', mapped);
      console.log('[DEBUG-CONSULTA-CAIXA] setConsultaDados sendo chamado com:', mapped.length, 'itens');
      setConsultaDados(mapped);
      try { (window as any).__lastConsultaCaixa = mapped; } catch (e) {}
      console.log('[DEBUG-CONSULTA-CAIXA] setConsultaDados chamado');
      // Se a API do AG Grid j+í estiver dispon+¡vel, setar os dados imediatamente.
      try {
        const trySet = (api: any) => {
          try {
            if (api && typeof api.setRowData === 'function') {
              api.setRowData(mapped);
              console.log('[DEBUG-CONSULTA-CAIXA] gridApi.setRowData aplicado, rows=', mapped.length);
              return true;
            }
          } catch (e) { /* noop */ }
          return false;
        };

        if (!trySet(gridApi) && !(window as any).__agGridApi) {
          // Pollar por at+® 5 segundos para quando o grid estiver pronto
          let attempts = 0;
          const maxAttempts = 25;
          const interval = setInterval(() => {
            attempts += 1;
            const api = gridApi || (window as any).__agGridApi;
            if (trySet(api)) {
              clearInterval(interval);
            } else if (attempts >= maxAttempts) {
              clearInterval(interval);
              console.warn('[DEBUG-CONSULTA-CAIXA] gridApi n+úo dispon+¡vel ap+¦s tentativas; dados mantidos em window.__lastConsultaCaixa');
            }
          }, 200);
        } else if (!(window as any).__agGridApi) {
          // se gridApi foi setado localmente, j+í aplicamos
        }
      } catch (e) {
        console.warn('Erro ao tentar aplicar rowData no grid:', e);
      }
      return mapped;
    } catch (err) {
      console.error('Erro buscarConsultaCaixa', err);
      setConsultaDados([]);
      return [];
    } finally {
      setConsultaLoading(false);
    }
  };

  const abrirFormularioCaixaPopup = async (row: any) => {
    try {
      console.log('[abrirFormularioCaixaPopup] ========== INICIANDO FLUXO DE EDI+ç+âO ==========');
      console.log('[abrirFormularioCaixaPopup] Row completo recebido:', row);
      console.log('[abrirFormularioCaixaPopup] Keys dispon+¡veis no row:', Object.keys(row));
      
      // (filial_cai, tipocai_cai, cliforn_cai, codbanco_cai, dtmovi_cai, seq_cai)
      const filial_cai = row.filial_cai || row.filial || '001';
      const tipocai_cai = row.tipocai_cai || '001'; // TIPO: '001'=Caixa/Bancos, '002'=Viagem
      const cliforn_cai = '   '; // SEMPRE VAZIO - nunca tem valor
      const codbanco_cai = row.codbanco_cai || row.codigo_cliente || ''; // C+¦digo do banco
      const dtmovi_cai = row.dtmovi_cai || row.data || row.dtmovi || '';
      const seq_cai = row.seq_cai || row.sequencia || row.seq || row.id || null;

      // Ô£à DIAGN+ôSTICO: Verificar formato da data
      console.log('[abrirFormularioCaixaPopup] ­ƒöì DIAGN+ôSTICO DE DATA:');
      console.log('[abrirFormularioCaixaPopup]   row.dtmovi_cai (raw):', row.dtmovi_cai, '| tipo:', typeof row.dtmovi_cai);
      console.log('[abrirFormularioCaixaPopup]   row.data:', row.data, '| tipo:', typeof row.data);
      console.log('[abrirFormularioCaixaPopup]   row.dtmovi:', row.dtmovi, '| tipo:', typeof row.dtmovi);
      console.log('[abrirFormularioCaixaPopup]   dtmovi_cai (final):', dtmovi_cai, '| tipo:', typeof dtmovi_cai, '| length:', dtmovi_cai.length);
      console.log('[abrirFormularioCaixaPopup]   Esperado: YYYY-MM-DD format (ex: 2025-12-01)');
      if (dtmovi_cai && dtmovi_cai !== '' && !dtmovi_cai.includes('-') && dtmovi_cai.length !== 8) {
        console.error('[abrirFormularioCaixaPopup]   ÔØî ERRO: Data em formato desconhecido!', dtmovi_cai);
      }

      // Ô£à NOVO: Determinar tipo_documento (R=Receber, P=Pagar)
      // Se houver documentos vinculados, usar seu tipo; caso contr+írio, inferir de dc_cai ou usar padr+úo
      let tipo_documento = 'R'; // padr+úo
      if (row.documentos_vinculados && Array.isArray(row.documentos_vinculados) && row.documentos_vinculados.length > 0) {
        tipo_documento = row.documentos_vinculados[0].tipo || 'R';
      } else if (row.tipo === 'P' || row.dc_cai === 'P') {
        tipo_documento = 'P';
      }

      const payload: any = {
        // Chave prim+íria completa
        filial_cai,
        tipocai_cai,
        cliforn_cai,
        codbanco_cai,
        dtmovi_cai,
        seq_cai,
        // Ô£à NOVO: tipo_documento (necess+írio para buscar documentos corretamente)
        tipo_documento,
        // Ô£à NOVO: cliente_cai (ALIAS de codbanco_cai, necess+írio para CaixaBancosForm buscar documentos)
        cliente_cai: codbanco_cai,
        // Ô£à NOVO: codigo_cliente (necess+írio para carregarDocumentosAbertosComCliente)
        codigo_cliente: codbanco_cai,
        // Campos adicionais para popula+º+úo do formul+írio
        oper_cai: row.oper_cai || row.operacao || '',
        operacao_ocai: row.operacao_ocai || null, // Ô£à NOVO: C+¦digo da opera+º+úo (n+úo descri+º+úo)
        dc_cai: row.dc_cai || row.tipo || '',
        valor_cai: row.valor_cai || row.valor || 0,
        histor_cai: row.histor_cai || row.historico || '',
        nome_cai: row.nome_cai || row.banco || '',
        dpto_cai: row.dpto_cai || row.centro_custo || '', // Ô£à NOVO: Centro de custo
        documentos_vinculados: row.documentos_vinculados || row.linked_documents || undefined
      };
      
      console.log('[abrirFormularioCaixaPopup] Payload mapeado:', payload);
      console.log('[abrirFormularioCaixaPopup] Chave prim+íria: filial=', filial_cai, 'tipocai=', tipocai_cai, 'dtmovi=', dtmovi_cai, 'seq=', seq_cai);

      console.log('[abrirFormularioCaixaPopup] Chave prim+íria:', { filial_cai, tipocai_cai, cliforn_cai, codbanco_cai, dtmovi_cai, seq_cai });

      // Ô£à NOVO FLUXO: Se n+úo temos documentos_vinculados no payload, buscar via CHAVE_REC_A14 e CHAVE_PAG_A12
      // Sempre buscar documentos, independentemente se buscarLancamentoPorId funcionou ou n+úo
      if (seq_cai && seq_cai !== null && seq_cai !== undefined) {
        try {
          console.log('[abrirFormularioCaixaPopup] ­ƒöì Buscando documentos vinculados com par+ómetros:', { codbanco_cai, dtmovi_cai, seq_cai });
          
          // CHAVE_REC_A14: (cxbco_rec, dtpagi_rec, seqcai_rec) ÔåÆ (codbanco_cai, dtmovi_cai, seq_cai)
          // CHAVE_PAG_A12: (cxbco_pag, dtpagi_pag, seqcai_pag) ÔåÆ (codbanco_cai, dtmovi_cai, seq_cai)
          // 
          // ÔÜá´©Å NOTA IMPORTANTE - INCOMPATIBILIDADE DE TIPOS (Sistema Legado):
          //    - caixa.codbanco_cai = CHAR(5) armazena c+¦digo do banco (ex: "00003")
          //    - receber.cxbco_rec = DECIMAL(5) - tipo num+®rico
          //    - pagar.cxbco_pag = DECIMAL(5) - tipo num+®rico
          //    O Backend DEVE fazer CAST SQL: WHERE cxbco_rec = CAST(codbanco_cai AS DECIMAL(5))
          //    N+úo podemos corrigir isso agora pois o sistema legado usa a mesma BD.
          
          const [docsReceber, docsPagar] = await Promise.all([
            CaixaBancosService.buscarDocumentosReceberVinculados(codbanco_cai, dtmovi_cai, seq_cai)
              .then(docs => {
                console.log('[abrirFormularioCaixaPopup] Ô£à Documentos RECEBER retornados:', docs ? docs.length : 0, 'registros');
                return docs || [];
              })
              .catch(err => {
                console.warn('[abrirFormularioCaixaPopup] ÔÜá´©Å Erro ao buscar RECEBER:', err);
                return [];
              }),
            CaixaBancosService.buscarDocumentosPagarVinculados(codbanco_cai, dtmovi_cai, seq_cai)
              .then(docs => {
                console.log('[abrirFormularioCaixaPopup] Ô£à Documentos PAGAR retornados:', docs ? docs.length : 0, 'registros');
                return docs || [];
              })
              .catch(err => {
                console.warn('[abrirFormularioCaixaPopup] ÔÜá´©Å Erro ao buscar PAGAR:', err);
                return [];
              })
          ]);
          
          // Consolidar documentos vinculados
          const documentosVinculados: any[] = [];
          
          if (docsReceber && docsReceber.length > 0) {
            console.log('[abrirFormularioCaixaPopup] ­ƒôä Documentos RECEBER vinculados:', docsReceber);
            docsReceber.forEach((doc: any) => {
              documentosVinculados.push({
                id: doc.receber_id || doc.codigo_rec || doc.id,
                tipo: 'R',
                codigo_cliente: doc.codigo_rec || doc.codigo_cli || codbanco_cai,
                nome_cliente: doc.nome_cli || doc.nomefan_cli || '',
                documento: doc.numdup_rec || doc.docto_rec || doc.documento || '',
                parcela: doc.parcela_rec || doc.parc_rec || doc.parcela || '',
                valor_original: parseNumeric(doc.vlrdup_rec || doc.vlrtot_rec || doc.valor || 0) || 0,
                valor_aberto: parseNumeric(doc.vlrsal_rec || doc.valor || 0) || 0,
                valor_selecionado: parseNumeric(doc.vlrpag_rec || doc.vlrsal_rec || doc.valor || 0) || 0,
                juros: parseNumeric(doc.vlracre_rec || 0) || 0,
                multa: parseNumeric(doc.vlrmulta_rec || 0) || 0,
                desconto: parseNumeric(doc.vlrdesc_rec || 0) || 0,
                pago: parseNumeric(doc.vlrpag_rec || 0) || 0,
                acrescimo: 0,
                data_vencimento: doc.dtvenci_rec || doc.dtvenci || doc.dtvenc || ''
              });
            });
          }
          
          if (docsPagar && docsPagar.length > 0) {
            console.log('[abrirFormularioCaixaPopup] ­ƒôä Documentos PAGAR vinculados:', docsPagar);
            docsPagar.forEach((doc: any) => {
              documentosVinculados.push({
                id: doc.pagar_id || doc.codigo_pag || doc.id,
                tipo: 'P',
                codigo_cliente: doc.codigo_pag || doc.codigo_for || codbanco_cai,
                nome_cliente: doc.nome_cli || doc.nomefan_for || doc.nome_for || '',
                documento: doc.numdup_pag || doc.docto_pag || doc.documento || '',
                parcela: doc.parcela_pag || doc.parc_pag || doc.parcela || '',
                valor_original: parseNumeric(doc.vlrdup_pag || doc.vlrtot_pag || doc.valor || 0) || 0,
                valor_aberto: parseNumeric(doc.vlrsal_pag || doc.valor || 0) || 0,
                valor_selecionado: parseNumeric(doc.vlrpag_pag || doc.vlrsal_pag || doc.valor || 0) || 0,
                juros: parseNumeric(doc.vlracre_pag || 0) || 0,
                multa: parseNumeric(doc.vlrmulta_pag || 0) || 0,
                desconto: parseNumeric(doc.vlrdesc_pag || 0) || 0,
                pago: parseNumeric(doc.vlrpag_pag || 0) || 0,
                acrescimo: 0,
                data_vencimento: doc.dtvenci_pag || doc.dtvenci || doc.dtvenc || ''
              });
            });
          }
          
          console.log('[abrirFormularioCaixaPopup] ­ƒôï Total de documentos consolidados:', documentosVinculados.length);
          if (documentosVinculados.length > 0) {
            console.log('[abrirFormularioCaixaPopup] Ô£à Documentos consolidados:', documentosVinculados);
            payload.documentos_vinculados = documentosVinculados;
          } else {
            console.log('[abrirFormularioCaixaPopup] Ôä¦´©Å Nenhum documento vinculado encontrado');
            payload.documentos_vinculados = [];
          }
        } catch (e) {
          console.error('[abrirFormularioCaixaPopup] ÔØî Erro ao buscar documentos vinculados:', e);
          payload.documentos_vinculados = [];
        }
      } else {
        console.warn('[abrirFormularioCaixaPopup] ÔÜá´©Å Sequ+¬ncia n+úo dispon+¡vel para buscar documentos');
        payload.documentos_vinculados = [];
      }

      console.log('[abrirFormularioCaixaPopup] Ô£à Payload final completo:', payload);
      console.log('[abrirFormularioCaixaPopup] Ô£à Campos cr+¡ticos:', { 
        tipocai_cai: payload.tipocai_cai,
        codbanco_cai: payload.codbanco_cai,
        dtmovi_cai: payload.dtmovi_cai,
        seq_cai: payload.seq_cai,
        valor_cai: payload.valor_cai,
        dc_cai: payload.dc_cai,
        oper_cai: payload.oper_cai,
        histor_cai: payload.histor_cai
      });
      
      // Modo CONSULTA: abrir como somente leitura a partir da aba de Relat+¦rios
      // Garantir que o payload contenha o c+¦digo/nome do banco para o formul+írio
      try {
        payload.codbanco_cai = payload.codbanco_cai || codbanco_cai || payload.cliente_cai || payload.codigo_cliente || payload.banco_cai || '';
        payload.nome_cai = payload.nome_cai || row.nome_bco || row.nomefan_bco || row.banco || payload.nome_cai || '';
      } catch(e) { /* noop */ }

      setCaixaPopupReadOnlyPrimary(true);
      payload._mode = 'consulta';
      console.log('[abrirFormularioCaixaPopup] ­ƒÜÇ CHAMANDO setCaixaPopupPayload (modo=consulta) ...');
      setCaixaPopupPayload(payload);
      console.log('[abrirFormularioCaixaPopup] ­ƒÄ» ABRINDO MODAL setShowCaixaPopup(true) (modo=consulta)');
      setShowCaixaPopup(true);
      console.log('[abrirFormularioCaixaPopup] ========== FIM DO FLUXO ==========');
    } catch (e) {
      console.error('[abrirFormularioCaixaPopup] ÔØî ERRO CR+ìTICO:', e);
      console.error('[abrirFormularioCaixaPopup] Stack:', e instanceof Error ? e.stack : 'sem stack');
    }
  };

  // Abrir formul+írio em modo INCLUIR (novo movimento)
  // eslint-disable-next-line @typescript-eslint/no-unused-vars
  const abrirFormularioCaixaIncluir = () => {
    try {
      const hoje = new Date();
      const dt = hoje.toISOString().slice(0,10); // YYYY-MM-DD
      const payload: any = {
        filial_cai: (user && (user as any).filialId) || '001',
        tipocai_cai: '001',
        cliforn_cai: '   ',
        codbanco_cai: '',
        dtmovi_cai: dt,
        seq_cai: null,
        tipo_documento: 'R',
        cliente_cai: '',
        codigo_cliente: '',
        oper_cai: '',
        operacao_ocai: null,
        dc_cai: 'D',
        valor_cai: 0,
        histor_cai: '',
        nome_cai: '',
        dpto_cai: ''
      };
      setCaixaPopupReadOnlyPrimary(false);
      payload._mode = 'incluir';
      setCaixaPopupPayload(payload);
      setShowCaixaPopup(true);
    } catch (e) {
      console.error('[abrirFormularioCaixaIncluir] Erro ao abrir popup de inclus+úo:', e);
    }
  };

  // Ô£à NOVO: Abrir formul+írio em modo EDITAR (editar movimento existente)
  const abrirFormularioCaixaEditar = async (row: any) => {
    try {
      console.log('[abrirFormularioCaixaEditar] ========== INICIANDO FLUXO DE EDI+ç+âO ==========');
      console.log('[abrirFormularioCaixaEditar] Row completo recebido:', row);
      
      const filial_cai = row.filial_cai || row.filial || '001';
      const tipocai_cai = row.tipocai_cai || '001';
      const cliforn_cai = row.cliforn_cai || '   ';
      const codbanco_cai = row.codbanco_cai || row.codigo_cliente || '';
      const dtmovi_cai = row.dtmovi_cai || row.data || row.dtmovi || '';
      const seq_cai = row.seq_cai || row.sequencia || row.seq || row.id || null;

      // Determinar tipo_documento
      let tipo_documento = 'R';
      if (row.tipo_documento) {
        tipo_documento = row.tipo_documento;
      } else if (row.tipo === 'P' || row.dc_cai === 'P') {
        tipo_documento = 'P';
      }

      const payload: any = {
        filial_cai,
        tipocai_cai,
        cliforn_cai,
        codbanco_cai,
        dtmovi_cai,
        seq_cai,
        tipo_documento,
        cliente_cai: codbanco_cai,
        codigo_cliente: codbanco_cai,
        oper_cai: row.oper_cai || row.operacao || '',
        operacao_ocai: row.operacao_ocai || null,
        dc_cai: row.dc_cai || row.tipo || '',
        valor_cai: row.valor_cai || row.valor || 0,
        histor_cai: row.histor_cai || row.historico || '',
        nome_cai: row.nome_cai || row.banco || '',
        dpto_cai: row.dpto_cai || row.centro_custo || ''
      };

      console.log('[abrirFormularioCaixaEditar] Payload mapeado:', payload);

      // Modo EDI+ç+âO: abrir como edi+º+úo
      setCaixaPopupReadOnlyPrimary(false); // false para permitir edi+º+úo dos campos
      payload._mode = 'editar';
      console.log('[abrirFormularioCaixaEditar] ­ƒÜÇ CHAMANDO setCaixaPopupPayload (modo=editar) ...');
      setCaixaPopupPayload(payload);
      console.log('[abrirFormularioCaixaEditar] ­ƒÄ» ABRINDO MODAL setShowCaixaPopup(true) (modo=editar)');
      setShowCaixaPopup(true);
      console.log('[abrirFormularioCaixaEditar] ========== FIM DO FLUXO ==========');
    } catch (e) {
      console.error('[abrirFormularioCaixaEditar] ÔØî ERRO CR+ìTICO:', e);
      console.error('[abrirFormularioCaixaEditar] Stack:', e instanceof Error ? e.stack : 'sem stack');
    }
  };

  // Fun+º+úo para toggle expand/collapse no Fluxo de Caixa
  const toggleExpandir = async (data: string) => {
    // Suporta tanto datas (YYYY-MM-DD) quanto chaves de opera+º+úo (C-OPERACAO ou D-OPERACAO)
    // Se for uma data v+ílida (exatamente YYYY-MM-DD com 10 chars), usa formatYMDUTC
    // Se for uma chave de opera+º+úo (come+ºa com C- ou D-), usa como-+®
    let key = data;
    const isDate = data && /^\d{4}-\d{2}-\d{2}$/.test(data); // formato YYYY-MM-DD exato
    
    console.log('[toggleExpandir] ­ƒöä Toggle chamado com data:', data, 'isDate:', isDate);
    
    if (isDate) {
      try {
        key = formatYMDUTC(data);
      } catch (e) {
        // Se falhar na formata+º+úo, usa a string original
        key = data;
      }
    }
    // else: key = data j+í cont+®m C-OPERACAO ou D-OPERACAO, usa como est+í
    
    console.log('[toggleExpandir] Chave final:', key, 'expandedDates atual:', Array.from(expandedDates));
    
    const novo = new Set(expandedDates);
    
    if (novo.has(key)) {
      console.log('[toggleExpandir] ÔØî Removendo chave (colapsar):', key);
      novo.delete(key);
      setExpandedDates(novo);
      return;
    }

    // Ao expandir
    console.log('[toggleExpandir] Ô£à Adicionando chave (expandir):', key);
    novo.add(key);
    console.log('[toggleExpandir] expandedDates novo estado:', Array.from(novo));
    setExpandedDates(novo);

    // Atualizar filtros para usar a data selecionada em futuras exporta+º+Áes (apenas se for data)
    if (isDate) {
      try {
        setFiltros(prev => ({
          ...prev,
          dataFiltroInicial: key,
          dataFiltroFinal: key,
          dataini: key,
          datafim: key
        }));
      } catch (e) {
        // ignore
      }
    }

    // Se for opera+º+úo (n+úo data), n+úo precisa buscar backend - dados j+í est+úo em consultaDados
    if (!isDate) {
      // Opera+º+úo: dados j+í est+úo agrupados, s+¦ expandir visualmente
      return;
    }

    try {
      if (!detalhesPorData[key]) {
      const resp = await RelatoriosService.buscarDetalhesFluxoDia(key, filtros.soEmAberto);

        // Normalizar formato: o backend pode retornar { receber: [], pagar: [], totais: {} }
        // ou um array j+í concatenado. Convert to homogeneous array of rows.
        let detalhesArray: any[] = [];
        if (Array.isArray(resp)) {
          detalhesArray = resp;
        } else if (resp && typeof resp === 'object') {
          const receber: any[] = resp.receber || [];
          const pagar: any[] = resp.pagar || [];
          // Marcar origem para possibilitar tratamento diferenciado se necess+írio
          const rNorm = receber.map(r => ({ ...r, __origem: 'receber' }));
          const pNorm = pagar.map(p => ({ ...p, __origem: 'pagar' }));
          detalhesArray = [...rNorm, ...pNorm];
        }

        try {
          // Log tempor+írio para debug: visualizar nomes de campos retornados pelo backend
          if ((detalhesArray || []).length > 0) {
            try { console.debug('[buscarDetalhesFluxoDia] amostra keys =', Object.keys(detalhesArray[0]).slice(0,40)); } catch(e) {}
            try { console.debug('[buscarDetalhesFluxoDia] sample row =', detalhesArray[0]); } catch(e) {}
          } else {
            try { console.debug('[buscarDetalhesFluxoDia] detalhesArray vazio para key=', key); } catch(e) {}
          }
        } catch (e) {
          // ignore
        }

        // Garantir que bancoOptions contenha bancos detectados nos detalhes
        try {
          const bancosSet = new Set<string>(bancoOptions);
          detalhesArray.forEach((row: any) => {
            const names = [row.nomefan_bco, row.nome_bco, row.banco, row.banco_nome, row.cliente_banco, row.banco_autorizado];
            names.forEach((n: any) => { if (n) bancosSet.add(String(n)); });
          });
          const novos = Array.from(bancosSet);
          if (novos.length > 0) setBancoOptions(novos);
        } catch (e) {
          // ignore
        }

        setDetalhesPorData(prev => ({ ...prev, [key]: detalhesArray }));
      }
    } catch (err) {
      console.warn('Falha ao carregar detalhes do dia via backend:', err);
      // Mantemos os detalhes j+í presentes em dados (se existirem)
    }
  };

  const handleAtualizarDepartamento = async (event: any) => {
    try {
      const { data, newValue, oldValue } = event;
      
      if (newValue === oldValue) return; // Sem mudan+ºas
      
      const tipo = tipoAtivo === 'receber' ? 'receber' : 'pagar';
      // Usar ID (receber_id ou pagar_id) como chave prim+íria
      const codigo = tipo === 'receber' ? data.receber_id : data.pagar_id;
      
      const resultado = await RelatoriosService.atualizarDepartamento(tipo, codigo, newValue);
      
      if (resultado.sucesso) {
        console.log('Ô£à Departamento atualizado com sucesso:', resultado);
        // Atualizar valor na linha imediatamente (sem reload)
        data[event.colDef.field] = newValue;
        event.api.redrawRows({ rowNodes: [event.rowIndex] });
      } else {
        console.error('ÔØî Erro ao atualizar:', resultado.mensagem);
        // Restaurar valor anterior
        data[event.colDef.field] = oldValue;
        event.api.redrawRows({ rowNodes: [event.rowIndex] });
      }
    } catch (error) {
      console.error('ÔØî Erro ao atualizar departamento:', error);
    }
  };

  const handleAtualizarTipoCobranca = async (event: any) => {
    try {
      const { data, newValue, oldValue } = event;
      
      if (newValue === oldValue) return; // Sem mudan+ºas
      
      const tipo = tipoAtivo === 'receber' ? 'receber' : 'pagar';
      // Usar ID (receber_id ou pagar_id) como chave prim+íria
      const codigo = tipo === 'receber' ? data.receber_id : data.pagar_id;
      
      const resultado = await RelatoriosService.atualizarTipoCobranca(tipo, codigo, newValue);
      
      if (resultado.sucesso) {
        console.log('Ô£à Tipo de cobran+ºa atualizado com sucesso:', resultado);
        // Atualizar valor na linha imediatamente (sem reload)
        data[event.colDef.field] = newValue;
        event.api.redrawRows({ rowNodes: [event.rowIndex] });
      } else {
        console.error('ÔØî Erro ao atualizar:', resultado.mensagem);
        // Restaurar valor anterior
        data[event.colDef.field] = oldValue;
        event.api.redrawRows({ rowNodes: [event.rowIndex] });
      }
    } catch (error) {
      console.error('ÔØî Erro ao atualizar tipo de cobran+ºa:', error);
    }
  };

  const onGridReady = (event: any) => {
    setGridApi(event.api);
    try { (window as any).__agGridApi = event.api; } catch (e) {}
    // Se houver dados pendentes de consulta (quando a busca ocorreu antes do grid estar pronto), aplicar agora
    try {
      const pending = (window as any).__lastConsultaCaixa;
      if (pending && Array.isArray(pending) && pending.length > 0) {
        try { event.api.setRowData(pending); console.debug('[DEBUG-CONSULTA-CAIXA] onGridReady aplicou window.__lastConsultaCaixa, rows=', pending.length); } catch (e) {}
        try { (window as any).__lastConsultaCaixa = null; } catch (e) {}
      }
    } catch (e) { /* noop */ }
    // Adicionar listener para recalcular TOTAL quando filtro muda
    event.api.addEventListener('filterChanged', () => {
      setTimeout(() => recalcularTotal(), 100);
    });
  };


  const buscarDados = async (overrideFiltros?: FiltroRelatorio) => {
    const usedFiltros = overrideFiltros || filtros;
    
    // Resetar gridApi para for+ºar nova renderiza+º+úo do AG Grid
    // Isso +® cr+¡tico quando volta de Fluxo para Contas a Receber/Pagar
    if (usedFiltros.tipo !== 'fluxo') {
      setGridApi(null);
    }
    
    setLoading(true);
    try {
      let resultado;
      if (usedFiltros.tipo === 'fluxo') {
        resultado = await RelatoriosService.buscarFluxoCaixa(usedFiltros);
      } else if (usedFiltros.tipo === 'consulta_caixa') {
        // Para a aba Consulta Caixa e Bancos usamos um endpoint distinto (GET)
        // e filtros separados. Invocar buscarConsultaCaixa que j+í gerencia os estados
        const resp = await buscarConsultaCaixa();
        resultado = { data: resp };
      } else {
        resultado = await RelatoriosService.buscarRelatorioFinanceiro(usedFiltros);
      }
      // Normalizar diferentes formatos de resposta do backend:
      // - array direct
      // - { rows: [...] } or { data: [...] } or { value: [...] }
      // - or nested objects containing the first array (e.g. { result: { rows: [...] } })
      let rowsNormalized: any[] = [];
      const findFirstArray = (obj: any): any[] => {
        if (!obj) return [];
        if (Array.isArray(obj)) return obj;
        // direct known keys
        if (obj && Array.isArray(obj.rows)) return obj.rows;
        if (obj && Array.isArray(obj.data)) return obj.data;
        if (obj && Array.isArray(obj.value)) return obj.value;
        // scan one level deep for first array
        for (const k of Object.keys(obj)) {
          try {
            const v = obj[k];
            if (Array.isArray(v)) return v;
            if (v && typeof v === 'object') {
              // nested object with rows/data/value
              if (Array.isArray(v.rows)) return v.rows;
              if (Array.isArray(v.data)) return v.data;
              if (Array.isArray(v.value)) return v.value;
            }
          } catch (e) {
            // ignore
          }
        }
        return [];
      };

      rowsNormalized = findFirstArray(resultado);

      // Enriquecer dados com campos formatados para exibi+º+úo (documento formatado)
      const processed = (rowsNormalized || []).map((r: any) => ({
        ...r,
        cgccpf_rec_formatted: getMaskedDocumento(r, 'rec'),
        cgccpf_pag_formatted: getMaskedDocumento(r, 'pag')
      }));
      setDados(processed);
      try { console.debug('[Relatorios] buscarDados -> dados length:', (processed || []).length, 'sample:', (processed || [])[0]); } catch(e) {}
      // Extrair op+º+Áes de bancos dispon+¡veis (se houver campo de banco nas linhas)
      try {
        const bancosSet = new Set<string>();
        // usar rowsNormalized/processed para detectar bancos ao inv+®s de `resultado` cru
        (rowsNormalized || processed || []).forEach((r: any) => {
          const names = [r.nomefan_bco, r.nome_bco, r.Banco, r.banco, r.banco_nome, r.cliente_banco];
          names.forEach((n: any) => { if (n) bancosSet.add(String(n)); });
        });
        const bancosArr = Array.from(bancosSet).filter(Boolean);
        // S+¦ atualizar op+º+Áes de banco se encontrarmos ao menos uma op+º+úo v+ílida.
        // Caso contr+írio, preservamos `bancoOptions` carregadas anteriormente
        // (por exemplo, vindas de `carregarBancosDashboard`). Isso evita que
        // o clique em per+¡odo (30/60/90) remova os cards quando o endpoint
        // de fluxo n+úo retorna campos de banco.
        if (bancosArr.length > 0) {
          setBancoOptions(bancosArr);
        } else {
          try { console.debug('[buscarDados] nenhum banco detectado no resultado; preservando bancoOptions existentes.'); } catch(e) {}
        }
        // se ainda n+úo houver sele+º+úo, e existir pelo menos um banco, manter vazio (usu+írio escolhe)
      } catch (e) {
        try { console.warn('Erro extraindo bancos do resultado:', e); } catch(err) {}
        // N+úo limpar bancoOptions em caso de erro: manter o estado atual para n+úo esconder cards
      }
      
      // Inicializar TOTAL com todos os dados usando rowsNormalized (safe)
      if (tipoAtivo === 'receber') {
        setTotalRow({
          nome_cli: 'Ô£ô TOTAL:',
          vlrdup_rec: rowsNormalized.reduce((sum: number, row: any) => sum + (parseFloat(row.vlrdup_rec) || 0), 0),
          vlrsal_rec: rowsNormalized.reduce((sum: number, row: any) => sum + (parseFloat(row.vlrsal_rec) || 0), 0)
        });
      } else {
        setTotalRow({
          nome_for: 'Ô£ô TOTAL:',
          vlrdup_pag: rowsNormalized.reduce((sum: number, row: any) => sum + (parseFloat(row.vlrdup_pag) || 0), 0),
          vlrsal_pag: rowsNormalized.reduce((sum: number, row: any) => sum + (parseFloat(row.vlrsal_pag) || 0), 0)
        });
      }
    } catch (error) {
      console.error('Erro ao buscar relat+¦rio:', error);
      alert('Erro ao gerar relat+¦rio. Verifique os filtros e tente novamente.');
    } finally {
      setLoading(false);
    }
  };

  // Fun+º+úo para exportar dados do AG Grid para CSV
  const exportarCSV = () => {
    if (!gridApi) {
      alert('Erro: Grid n+úo est+í pronto para exporta+º+úo.');
      return;
    }
    
    try {
      const fileName = `relatorio_${filtros.tipo}_${new Date().toISOString().split('T')[0]}.csv`;
      gridApi.exportDataAsCsv({
        fileName: fileName,
        columnSeparator: ';',
        processCellCallback: (params: any) => {
          // Formatar valores para CSV
          if (params.value === null || params.value === undefined) return '';
          if (typeof params.value === 'number') {
            return params.value.toString().replace('.', ',');
          }
          return params.value;
        }
      });
      console.log('Ô£à CSV exportado com sucesso:', fileName);
    } catch (error) {
      console.error('ÔØî Erro ao exportar CSV:', error);
      alert('Erro ao exportar CSV. Verifique o console.');
    }
  };

  const exportarRelatorio = async () => {
    try {
      // Garantir que o filtro 'tipo' est+í preenchido
      if (!filtros.tipo) {
        alert('Erro: Tipo de relat+¦rio n+úo identificado.');
        return;
      }

      // Usar novo endpoint espec+¡fico para Contas a Pagar com filtro de data din+ómico
      if (filtros.tipo === 'pagar') {
        // Converter datas para formato YYYY-MM-DD se necess+írio
        const dataini = filtros.dataFiltroInicial || formatYMDUTC(new Date(new Date().getFullYear(), 0, 1));
        const datafim = filtros.dataFiltroFinal || formatYMDUTC(new Date());
        
        // Verificar se +® relat+¦rio de Folha de Pagamento (flag folhaPagamento)
        if (filtros.folhaPagamento) {
          console.log('­ƒôä Exportando Relat+¦rio de Folha de Pagamento PDF');
          // Exportar relat+¦rio espec+¡fico de Folha de Pagamento
          await RelatoriosService.exportarRelatorioFolhaPagamentoPDF({
            ...filtros,
            dataini,
            datafim,
            tipoCampoData: filtros.tipoCampoData || 'dtvenci_pag'
          });
        } else {
          console.log('­ƒôä Exportando Relat+¦rio de Contas a Pagar PDF');
          // Exportar relat+¦rio normal de Contas a Pagar
          await RelatoriosService.exportarRelatorioPagarPDF({
            ...filtros,
            dataini,
            datafim,
            tipoCampoData: filtros.tipoCampoData || 'dtvenci_pag'
          });
        }
      } else if (filtros.tipo === 'fluxo') {
        // Exportar Fluxo: incluir mestre (KPIs) e detalhe do dia (HOJE) para o relat+¦rio
        const hojeKey = formatYMDUTC(new Date());
        const dataini = filtros.dataFiltroInicial || hojeKey;
        const datafim = filtros.dataFiltroFinal || hojeKey;

        // preparar payload com mestre e detalhes do dia (se existirem)
        const payload: any = {
          ...filtros,
          dataini,
          datafim,
          tipoCampoData: filtros.tipoCampoData || 'dtvenci_rec'
        };

        // incluir KPIs calculados como mestre
        if (fluxoKpis) payload.mestre = fluxoKpis;

        // incluir detalhes do dia (preferir cache local 'detalhesPorData')
        const detalhesHoje = detalhesPorData[hojeKey] || [];
        payload.detalhesHoje = detalhesHoje;

        await RelatoriosService.exportarRelatorioFinanceiro(payload);
      } else {
        // Para outros tipos (receber), usar m+®todo gen+®rico
        const tipoCampo = filtros.tipoCampoData || (filtros.tipo === 'receber' ? 'dtvenci_rec' : 'dtvenci_pag');
        await RelatoriosService.exportarRelatorioFinanceiro({ ...filtros, tipoCampoData: tipoCampo });
      }
      
      alert('PDF exportado com sucesso!');
    } catch (error) {
      console.error('Erro ao exportar relat+¦rio:', error);
      const errorMsg = error instanceof Error ? error.message : String(error);
      alert(`Erro ao exportar relat+¦rio:\n${errorMsg}`);
    }
  };


  // Persist+¬ncia simples de sele+º+úo (faixaAtraso + banco) para melhorar UX
  React.useEffect(() => {
    try {
      const savedFaixa = localStorage.getItem('relatorios.faixaAtraso');
      const savedBanco = localStorage.getItem('relatorios.bancoSelecionado');
      if (savedFaixa) {
        setFiltros(prev => (prev && prev.faixaAtraso) ? prev : ({ ...prev, faixaAtraso: savedFaixa }));
      }
      if (savedBanco) {
        setBancoSelecionado(prev => prev || savedBanco);
      }
    } catch (e) {
      // ignore
    }
  }, []);

  React.useEffect(() => {
    try {
      if (filtros.faixaAtraso) localStorage.setItem('relatorios.faixaAtraso', String(filtros.faixaAtraso));
      else localStorage.removeItem('relatorios.faixaAtraso');
      if (bancoSelecionado) localStorage.setItem('relatorios.bancoSelecionado', bancoSelecionado);
      else localStorage.removeItem('relatorios.bancoSelecionado');
    } catch (e) {
      // ignore
    }
  }, [filtros.faixaAtraso, bancoSelecionado]);

  // ESC para fechar pain+®is/expans+Áes
  React.useEffect(() => {
    const handler = (e: KeyboardEvent) => {
      if (e.key === 'Escape' || e.key === 'Esc') {
        setExpandedDates(new Set());
        setCollapseFilter(true);
      }
    };
    window.addEventListener('keydown', handler);
    return () => window.removeEventListener('keydown', handler);
  }, []);

  /**
   * Carrega dados de previs+úo para as datas agrupadas no fluxo de caixa
   * Chamado uma vez quando os dados do fluxo s+úo carregados
   */
  const carregarPrevisoesPorDatas = async (datas: string[]) => {
    if (!datas || datas.length === 0) return;
    
    try {
      setCarregandoPrevisao(true);
      const filial = '001'; // Filial padr+úo do SPDealer
      
      // Buscar previs+Áes para todas as datas em paralelo
      const previsoes = await PrevisaoFinanceiraService.buscarPrevisaoPorDatas(filial, datas);
      
      setPrevisaoPorData(previsoes);
    } catch (error) {
      console.error('[RelatoriosFinanceiros] Erro ao carregar previs+Áes:', error);
      // N+úo quebrar a tela se falhar em carregar previs+úo
    } finally {
      setCarregandoPrevisao(false);
    }
  };

  const renderTabelaResultados = () => {
    const currentDados = filtros.tipo === 'consulta_caixa' ? consultaDados : dados;
    const currentLoading = filtros.tipo === 'consulta_caixa' ? consultaLoading : loading;
    
    console.log('[DEBUG-RENDER] renderTabelaResultados() - tipo:', filtros.tipo, 'currentDados.length:', currentDados.length, 'loading:', currentLoading);

        if (currentDados.length === 0) {
      if (currentLoading) {
        return (
          <div style={{ padding: '40px', textAlign: 'center', color: '#6b7280' }}>
            <FontAwesomeIcon icon={faSpinner} spin size="2x" style={{ marginBottom: '16px' }} />
            <p>Lendo...</p>
          </div>
        );
      }
      return (
        <div style={{ padding: '40px', textAlign: 'center', color: '#6b7280' }}>
          <FontAwesomeIcon icon={faSearch} size="2x" style={{ marginBottom: '16px' }} />
          <p>Nenhum resultado encontrado. Ajuste os filtros e tente novamente.</p>
        </div>
      );
    }

    // Fluxo de Caixa: Expandir/Colapsar por Data (sem Enterprise)
    if (filtros.tipo === 'fluxo') {
      // Agrupar dados por dataKey (YYYY-MM-DD) para evitar formatos inconsistentes
      const gruposData = dados.reduce((acc: any, row: any) => {
        // Ignorar linhas de subtotal/agrupamento vindas do backend para evitar dupla contagem
        if (isSubtotalRow(row)) return acc;
        // Mestre: usar explicitamente o campo de vencimento (dtvenci_rec) como chave de agrupamento
        const rawVenci = pickField(row, ['dtvenci_rec','dtvenci','dtvenc','dtvenc_rec','dtvenci_pag','dtvenci_rec']) || row.data || row.dtmovi_cai || row.dtmovi || row.dtmovi_rec || row.dtmovi_pag || row.datai || '';
        const dataKey = formatYMDUTC(rawVenci);
        if (!acc[dataKey]) {
          acc[dataKey] = {
            dataKey,
            dataOriginal: rawVenci,
            entradas: 0,
            saidas: 0,
            saldo: 0,
            detalhes: []
          };
        }
        // Calcular entradas/saidas com fallback para campos comuns (vlrsal_rec, vlrsal_pag, vlrdup_rec, vlrdup_pag, valor)
        let e = Number(row.entradas ?? 0) || 0;
        let s = Number(row.saidas ?? 0) || 0;
        if (!e) {
          e = parseNumeric(row.vlrsal_rec ?? row.vlrdup_rec ?? row.valor ?? row.amount ?? 0) || 0;
        }
        if (!s) {
          s = parseNumeric(row.vlrsal_pag ?? row.vlrdup_pag ?? row.valor ?? 0) || 0;
        }
        acc[dataKey].entradas += e;
        acc[dataKey].saidas += s;
        acc[dataKey].saldo += (Number(row.saldo) || (e - s) );
        // Armazenar a linha inteira como detalhe (para garantir que a data e campos estejam acess+¡veis)
        acc[dataKey].detalhes.push(row);
        return acc;
      }, {});

      const grupos = Object.values(gruposData).sort((a: any, b: any) => {
        // Ordenar por dataKey (YYYY-MM-DD) - mais antigo primeiro
        return a.dataKey.localeCompare(b.dataKey);
      });
      
      // Carregar previs+Áes para as datas agrupadas
      // Nota: n+úo chamar `carregarPrevisoesPorDatas` diretamente durante o render
      // para evitar setState dentro do fluxo de render (causa de re-render infinito).
      // A chamada +® feita por um useEffect abaixo quando `dados` ou `filtros.tipo` mudam.
      
      
      // Aplicar filtro 'Somente HOJE' se ativado (usar data local para evitar offset UTC)
      const todayKey = formatYMDUTC(new Date());
      const gruposFiltrados = filterOnlyHoje ? (grupos as any[]).filter((g: any) => {
        try { return String(g.dataKey) === todayKey; } catch(e) { return false; }
      }) : grupos;
      
      // Calcular totais gerais (respeitar filtro HOJE quando ativo)
      const totalEntradas: number = (gruposFiltrados as any[]).reduce((sum: number, g: any) => sum + (Number(g.entradas) || 0), 0);
      const totalSaidas: number = (gruposFiltrados as any[]).reduce((sum: number, g: any) => sum + (Number(g.saidas) || 0), 0);
      // Referenciar vari+íveis para evitar warnings de "assigned but never used".
      void totalEntradas;
      void totalSaidas;

      // Card HOJE: calcular valores para o card de hoje (evita IIFE direto no JSX)
      const hojeGroup = (grupos as any[]).find((g: any) => {
        try { return String(g.dataKey) === todayKey; } catch(e) { return false; }
      });
      const entradasHoje = hojeGroup ? Number((hojeGroup as any).entradas) || 0 : 0;
      const saidasHoje = hojeGroup ? Number((hojeGroup as any).saidas) || 0 : 0;
      void entradasHoje;
      void saidasHoje;

      return (
        <>
          {/* Lista de Datas com Expandir/Colapsar */}
          <div style={{ padding: '0 24px', maxHeight: '60vh', overflowY: 'auto' }}>
            {gruposFiltrados.map((grupo: any) => (
              <div key={grupo.dataKey} id={`grupo-${grupo.dataKey}`} style={{ marginBottom: '12px' }}>
                {/* Linha da Data (Cabe+ºalho Expand+¡vel) */}
                <div
                  onClick={() => toggleExpandir(grupo.dataKey)}
                  style={{
                    display: 'flex',
                    justifyContent: 'space-between',
                    alignItems: 'center',
                    padding: '14px 16px',
                    background: '#fff',
                    border: '1px solid #e5e7eb',
                    borderRadius: '6px 6px 0 0',
                    cursor: 'pointer',
                    transition: 'background 0.2s',
                    userSelect: 'none'
                  }}
                  onMouseEnter={(e) => e.currentTarget.style.background = '#f9fafb'}
                  onMouseLeave={(e) => e.currentTarget.style.background = '#fff'}
                >
                  <div style={{ display: 'flex', alignItems: 'center', gap: '12px' }}>
                    <FontAwesomeIcon 
                      icon={expandedDates.has(grupo.dataKey) ? faChevronDown : faChevronRight}
                      style={{ color: '#6b7280', width: '16px' }}
                    />
                    <div style={{ fontSize: '16px', fontWeight: 600, color: '#1f2937' }}>
                      ­ƒôà {formatarData(grupo.dataOriginal)}
                    </div>
                  </div>
                  <div style={{ display: 'flex', gap: '24px', alignItems: 'center' }}>
                      {(() => {
                        // Se detalhesPorData existir, usar soma dos detalhes como fonte da verdade
                        const detalhesArr: any[] = detalhesPorData[grupo.dataKey] ?? grupo.detalhes ?? [];
                        const receberListHdr = detalhesArr.filter(d => d.__origem === 'receber');
                        const pagarListHdr = detalhesArr.filter(d => d.__origem === 'pagar');
                        const totalReceberHdr = receberListHdr.length > 0
                          ? receberListHdr.reduce((s: number, r: any) => s + parseNumeric(r.vlrsal_rec ?? r.vlrdup_rec ?? r.valor ?? 0), 0)
                          : Number(grupo.entradas || 0);
                        const totalPagarHdr = pagarListHdr.length > 0
                          ? pagarListHdr.reduce((s: number, r: any) => s + parseNumeric(r.vlrsal_pag ?? r.vlrdup_pag ?? r.valor ?? 0), 0)
                          : Number(grupo.saidas || 0);
                        const saldoHdr = totalReceberHdr - totalPagarHdr;
                        
                        // Dados de previs+úo para esta data
                        const prevData = previsaoPorData[grupo.dataKey];
                        const recebasPrevistas = prevData?.receitas_previstas || 0;
                        const despesasPrevistas = prevData?.despesas_previstas || 0;
                        const saldoPrevisto = prevData?.saldo_previsto || (recebasPrevistas - despesasPrevistas);
                        
                        return (
                          <>
                            {/* COLUNA REALIZADO */}
                            <div style={{ textAlign: 'right' }}>
                              <div style={{ fontSize: '11px', color: '#6b7280', fontWeight: 600 }}>Entradas (Real)</div>
                              <div style={{ fontSize: '13px', fontWeight: 'bold', color: '#059669' }}>
                                {formatarMoeda(totalReceberHdr)}
                              </div>
                            </div>
                            <div style={{ textAlign: 'right' }}>
                              <div style={{ fontSize: '11px', color: '#6b7280', fontWeight: 600 }}>Sa+¡das (Real)</div>
                              <div style={{ fontSize: '13px', fontWeight: 'bold', color: '#dc2626' }}>
                                {formatarMoeda(totalPagarHdr)}
                              </div>
                            </div>
                            <div style={{ textAlign: 'right', minWidth: '110px' }}>
                              <div style={{ fontSize: '11px', color: '#6b7280', fontWeight: 600 }}>Saldo (Real)</div>
                              <div style={{
                                fontSize: '13px',
                                fontWeight: 'bold',
                                color: saldoHdr >= 0 ? '#059669' : '#dc2626'
                              }}>
                                {formatarMoeda(saldoHdr)}
                              </div>
                            </div>
                            
                            {/* COLUNA PREVISTO (se houver dados) */}
                            {prevData && (
                              <>
                                <div style={{ textAlign: 'right', borderLeft: '1px solid #e5e7eb', paddingLeft: '12px' }}>
                                  <div style={{ fontSize: '11px', color: '#6b7280', fontWeight: 600 }}>Entradas (Prev.)</div>
                                  <div style={{ fontSize: '13px', fontWeight: 'bold', color: '#2563eb' }}>
                                    {formatarMoeda(recebasPrevistas)}
                                  </div>
                                </div>
                                <div style={{ textAlign: 'right' }}>
                                  <div style={{ fontSize: '11px', color: '#6b7280', fontWeight: 600 }}>Sa+¡das (Prev.)</div>
                                  <div style={{ fontSize: '13px', fontWeight: 'bold', color: '#7c3aed' }}>
                                    {formatarMoeda(despesasPrevistas)}
                                  </div>
                                </div>
                                <div style={{ textAlign: 'right', minWidth: '110px' }}>
                                  <div style={{ fontSize: '11px', color: '#6b7280', fontWeight: 600 }}>Saldo (Prev.)</div>
                                  <div style={{
                                    fontSize: '13px',
                                    fontWeight: 'bold',
                                    color: saldoPrevisto >= 0 ? '#2563eb' : '#7c3aed'
                                  }}>
                                    {formatarMoeda(saldoPrevisto)}
                                  </div>
                                </div>
                              </>
                            )}
                            {carregandoPrevisao && (
                              <div style={{ fontSize: '11px', color: '#9ca3af' }}>
                                (carregando previs+úo...)
                              </div>
                            )}
                          </>
                        );
                      })()}
                    </div>
                </div>

                {/* Detalhes Expandidos */}
                {expandedDates.has(grupo.dataKey) && (
                  <div style={{
                    background: '#f9fafb',
                    borderLeft: '1px solid #e5e7eb',
                    borderRight: '1px solid #e5e7eb',
                    borderBottom: '1px solid #e5e7eb',
                    borderRadius: '0 0 6px 6px',
                    padding: '16px',
                    overflow: 'hidden'
                  }}>
                    {/* Toolbar do detalhe: Processar sele+º+úo */}
                    <div style={{ display: 'flex', justifyContent: 'flex-end', marginBottom: 12, gap: 8 }}>
                      {(() => {
                        const detalhesAll: any[] = detalhesPorData[grupo.dataKey] ?? grupo.detalhes ?? [];
                        const receberListIds = new Set((detalhesAll.filter(d => d.__origem === 'receber' || isReceberRow(d)).map(d => 'R-' + String(d.receber_id ?? d.id ?? ''))));
                        const pagarListIds = new Set((detalhesAll.filter(d => d.__origem === 'pagar' || isPagarRow(d)).map(d => 'P-' + String(d.pagar_id ?? d.id ?? ''))));
                        const availableIds = new Set<string>();
                        receberListIds.forEach(id => availableIds.add(id));
                        pagarListIds.forEach(id => availableIds.add(id));
                        const selectedForGroup = Array.from(authorizedRows).filter(id => availableIds.has(id));
                        const selectedCount = selectedForGroup.length;
                        const selectedTipo = selectedForGroup.every(s => s.startsWith('R-')) ? 'RECEBER' : (selectedForGroup.every(s => s.startsWith('P-')) ? 'PAGAR' : 'MIXED');

                        return (
                          <>
                            <Button $variant="secondary" type="button" onClick={() => {
                              if (selectedCount === 0) {
                                alert('Nenhum documento autorizado nesta data para limpar.');
                                return;
                              }
                              const copy = new Set(authorizedRows);
                              selectedForGroup.forEach(id => copy.delete(id));
                              setAuthorizedRows(copy);
                            }}>
                              Limpar Sele+º+úo
                            </Button>

                            <Button $variant="primary" type="button" disabled={selectedCount === 0} onClick={() => {
                              // Verificar se todos os documentos selecionados t+¬m banco atribu+¡do
                              const docsSemBanco = selectedForGroup.filter(id => !documentBanks[id]);
                              if (docsSemBanco.length > 0) {
                                alert(`Selecione um banco para cada documento antes de processar.\nDocumentos sem banco: ${docsSemBanco.length}`);
                                return;
                              }
                              if (selectedCount === 0) { alert('Selecione documentos autorizados para processar.'); return; }
                              if (selectedTipo === 'MIXED') { alert('N+úo +® permitido misturar Receber e Pagar no mesmo lan+ºamento.'); return; }
                              const documentoIdsNums = selectedForGroup.map(s => Number(String(s).split('-')[1] || '0')).filter(n => n > 0);
                              // calcular soma local a partir dos detalhes para exibir no modal
                              const detalhesMap: Record<string, any> = {};
                              detalhesAll.forEach((d: any) => {
                                const rid = d.receber_id ?? d.pagar_id ?? d.id;
                                if (rid != null) detalhesMap[String(rid)] = d;
                              });
                              let somaLocal = 0;
                              documentoIdsNums.forEach(idn => {
                                const det = detalhesMap[String(idn)];
                                if (det) {
                                  const v = (det.vlrsal_rec ?? det.vlrsal_pag ?? det.vlrdup_rec ?? det.vlrdup_pag ?? det.valor ?? 0);
                                  somaLocal += Number(v) || 0;
                                }
                              });
                              setModalDocumentoIds(documentoIdsNums);
                              // Passar os bancos espec+¡ficos de cada documento para o modal
                              const banksForModal: Record<string, string> = {};
                              selectedForGroup.forEach((id: string) => {
                                if (documentBanks[id]) {
                                  banksForModal[id] = documentBanks[id];
                                }
                              });
                              setModalDocumentBanks(banksForModal);
                              setModalInitialValor(somaLocal);
                              setModalTipo(selectedTipo === 'RECEBER' ? 'RECEBER' : 'PAGAR');
                              setModalOperacao(selectedTipo === 'RECEBER' ? 500 : 600);
                              setModalReloadKey(grupo.dataKey);
                              setShowRegistrarModal(true);
                            }}>
                              Processar ({selectedCount})
                            </Button>
                          </>
                        );
                      })()}
                    </div>
                    {/* label intentionally removed to save vertical space */}
                    {/* Lista customizada: separar Receber e Pagar em duas colunas com totais */}
                    {
                      (detalhesPorData[grupo.dataKey] ?? grupo.detalhes ?? []).length > 0 && (() => {
                        const detalhes: any[] = (detalhesPorData[grupo.dataKey] ?? grupo.detalhes ?? []);
                        const receberList = detalhes.filter(d => d.__origem === 'receber' || isReceberRow(d));
                        const pagarList = detalhes.filter(d => d.__origem === 'pagar' || isPagarRow(d));

                        const totalReceber = receberList.reduce((s: number, r: any) => s + parseNumeric(r.vlrsal_rec ?? r.vlrdup_rec ?? r.valor ?? 0), 0);
                        const totalPagar = pagarList.reduce((s: number, r: any) => s + parseNumeric(r.vlrsal_pag ?? r.vlrdup_pag ?? r.valor ?? 0), 0);

                        return (
                          <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: 12, fontSize: '12px', color: '#374151', minWidth: 0 }}>
                            <div style={{ background: '#fff', padding: 12, borderRadius: 6, border: '1px solid #e5e7eb', overflow: 'hidden', minWidth: 0 }}>
                              <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: 8 }}>
                                <div style={{ fontWeight: 700 }}>Receber ({receberList.length})</div>
                                <div style={{ fontWeight: 700, color: '#059669' }}>{formatarMoeda(totalReceber)}</div>
                              </div>
                              {/* Header da lista: primeira coluna = Atualizar */}
                              <div style={{ display: 'flex', gap: '12px', alignItems: 'center', padding: '6px 0', borderBottom: '1px solid #e5e7eb', fontSize: '12px', color: '#374151', minWidth: 0 }}>
                                <div style={{ width: '36px', textAlign: 'center', fontWeight: 700 }}>Atualizar</div>
                                <div style={{ width: '110px', textAlign: 'left', fontWeight: 700 }}>C+¦digo</div>
                                <div style={{ width: '110px', textAlign: 'left', fontWeight: 700 }}>Emiss+úo</div>
                                <div style={{ width: '140px', textAlign: 'left', fontWeight: 700 }}>Cobran+ºa</div>
                                <div style={{ width: '160px', textAlign: 'left', fontWeight: 700 }}>Documento</div>
                                <div style={{ width: '180px', textAlign: 'left', fontWeight: 700 }}>Cliente</div>
                                <div style={{ width: '120px', textAlign: 'right', fontWeight: 700 }}>Valor</div>
                              </div>
                              <div>
                                {receberList.map((det: any, idx: number) => {
                                  const idKey = 'R-' + String(det.receber_id ?? det.id ?? idx);
                                  // localizar data de emissao com v+írios candidatos e fallback por padr+úo de nome
                                  let rowDateEmissao = pickField(det, ['dtemissi_rec','dtemiss_rec','dtemiss','dtemissi','dtemissrec','dtemissi_rec','dtemiss_rec','dtmovi_rec','dtemissi','data','dtmovi','dtemiss_rec']);
                                  if (!rowDateEmissao) {
                                    rowDateEmissao = findByPattern(det, ['emiss','emissao','dtemiss','dtemissi','data']);
                                  }
                                  const rowDateVenci = pickField(det, ['dtvenci_rec','dtvenc','dtvenci','dtvenci_rec','dtvenc_rec']) || findByPattern(det, ['venci','vencimento','dtvenc','dtvenci']);
                                  // Se backend n+úo fornecer campo de emiss+úo (`dtemissi_rec`), n+úo usar o vencimento como fallback
                                  // (mestre usa `dtvenci_rec`; o detalhe deve exibir apenas `dtemissi_rec`).
                                  if (!rowDateEmissao && rowDateVenci) {
                                    if (!dtemissaoWarningReceberShown.current) {
                                      try { console.debug('[detalhe] dtemissao ausente (receber) - campo `dtemissi_rec` ausente, n+úo usar dtvenci como fallback no detalhe'); } catch(e) {}
                                      dtemissaoWarningReceberShown.current = true;
                                    }
                                    // keep rowDateEmissao undefined so detalhe shows empty when emission date missing
                                  }
                                  const codigoParts = [] as string[];
                                  if (det.codigo_rec) codigoParts.push(String(det.codigo_rec));
                                  if (det.numdup_rec) codigoParts.push(String(det.numdup_rec));
                                  if (det.parcela_rec) codigoParts.push(String(det.parcela_rec));
                                  const codigo = codigoParts.join(' ');
                                  const cliente = det.nome_cli || det.cliente || '';
                                  // Documento FORMATADO COMPLETO (compat+¡vel com v+írios nomes de campo)
                                  let documento = getFormattedDocumento(det, 'rec');
                                  if (!documento) {
                                    // tentar extrair explicitamente tipo de pessoa e campo cgccpf_rec/cgccpf
                                    const tipoPessoaRaw = pickField(det, ['tipopessoa_rec','tipopessoa','tipo_pessoa','tipo','cliforn_cli']);
                                    const rawDoc = pickField(det, ['cgccpf_rec','cgccpf','cpf','cnpj','documento','document','cpf_cli','cpf_cli_rec','cgccpf_cli','cgc_cpf','cgccpf_pag']) || findByPattern(det, ['cpf','cnpj','cgccpf','document','doc']);
                                    if (rawDoc) {
                                      const constructed: any = {};
                                      if (tipoPessoaRaw) constructed['tipopessoa'] = tipoPessoaRaw;
                                      constructed['cgccpf_rec'] = rawDoc;
                                      constructed['cgccpf'] = rawDoc;
                                      constructed['cpf'] = rawDoc;
                                      constructed['cnpj'] = rawDoc;
                                      constructed['document'] = rawDoc;
                                      documento = getFormattedDocumento(constructed, 'rec');
                                    }
                                    if (!documento) {
                                      documento = '';
                                      try { console.warn('[detalhe] documento ausente para linha', det); } catch(e) {}
                                    }
                                  }
                                  if (!documento) documento = '-';
                                  const valor = det.vlrsal_rec ?? det.vlrdup_rec ?? det.valor ?? null;
                                  // Obter banco espec+¡fico deste documento ou usar o banco selecionado global como default
                                  const docBanco = documentBanks[idKey] || bancoSelecionado;
                                  const isDocSelected = authorizedRows.has(idKey);
                                    return (
                                    <div key={idKey} style={{ display: 'flex', gap: '12px', alignItems: 'center', padding: '8px 0', borderBottom: '1px dashed #e5e7eb', minWidth: 0 }}>
                                      <div style={{ width: '36px', textAlign: 'center' }}>
                                        <div style={{ display: 'flex', flexDirection: 'column', alignItems: 'center' }}>
                                          <input type="checkbox" checked={isDocSelected} onChange={() => {
                                            // Se n+úo tem banco definido, usar o banco selecionado global como default
                                            if (!documentBanks[idKey] && bancoSelecionado) {
                                              setDocumentBanks(prev => ({ ...prev, [idKey]: bancoSelecionado }));
                                            }
                                            const copy = new Set(authorizedRows);
                                            if (copy.has(idKey)) copy.delete(idKey); else copy.add(idKey);
                                            setAuthorizedRows(copy);
                                          }} />
                                          {/* Dropdown para selecionar banco por documento - permite bancos diferentes */}
                                          <select
                                            value={docBanco || ''}
                                            onChange={(e) => {
                                              const novoBanco = e.target.value;
                                              setDocumentBanks(prev => ({ ...prev, [idKey]: novoBanco }));
                                              // Se o documento j+í estava selecionado, manter sele+º+úo; se n+úo, selecionar
                                              if (!authorizedRows.has(idKey) && novoBanco) {
                                                const copy = new Set(authorizedRows);
                                                copy.add(idKey);
                                                setAuthorizedRows(copy);
                                              }
                                            }}
                                            style={{ fontSize: '10px', marginTop: '2px', padding: '2px', width: '60px' }}
                                          >
                                            <option value="">--</option>
                                            {bancoOptions.map(b => (
                                              <option key={b} value={b}>{b}</option>
                                            ))}
                                          </select>
                                        </div>
                                      </div>
                                      <div style={{ width: '110px', color: '#374151' }}>{codigo}</div>
                                      <div style={{ width: '110px', color: '#6b7280' }}>{rowDateEmissao ? formatarData(rowDateEmissao) : ''}</div>
                                      <div style={{ width: '120px', color: '#6b7280', fontWeight: 600 }}>{det.tpcob_rec || det.tpcob || ''}</div>
                                      <div style={{ width: '140px', color: '#374151' }}>{documento}</div>
                                      <div style={{ width: '180px', color: '#374151' }}>{cliente}</div>
                                      <div style={{ width: '120px', textAlign: 'right', fontWeight: 700 }}>{valor ? formatarMoeda(Number(valor)) : ''}</div>
                                    </div>
                                  );
                                })}
                              </div>
                            </div>

                            <div style={{ background: '#fff', padding: 12, borderRadius: 6, border: '1px solid #e5e7eb', overflow: 'hidden', minWidth: 0 }}>
                              <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: 8 }}>
                                <div style={{ fontWeight: 700 }}>Pagar ({pagarList.length})</div>
                                <div style={{ fontWeight: 700, color: '#dc2626' }}>{formatarMoeda(totalPagar)}</div>
                              </div>
                              {/* Header da lista: primeira coluna = Autorizar */}
                              <div style={{ display: 'flex', gap: '12px', alignItems: 'center', padding: '6px 0', borderBottom: '1px solid #e5e7eb', fontSize: '12px', color: '#374151', minWidth: 0 }}>
                                <div style={{ width: '36px', textAlign: 'center', fontWeight: 700 }}>Autorizar</div>
                                <div style={{ width: '110px', textAlign: 'left', fontWeight: 700 }}>C+¦digo</div>
                                <div style={{ width: '110px', textAlign: 'left', fontWeight: 700 }}>Emiss+úo</div>
                                <div style={{ width: '140px', textAlign: 'left', fontWeight: 700 }}>Cobran+ºa</div>
                                <div style={{ width: '160px', textAlign: 'left', fontWeight: 700 }}>Documento</div>
                                <div style={{ width: '180px', textAlign: 'left', fontWeight: 700 }}>Fornecedor</div>
                                <div style={{ width: '120px', textAlign: 'right', fontWeight: 700 }}>Valor</div>
                              </div>
                              <div>
                                {pagarList.map((det: any, idx: number) => {
                                  const idKey = 'P-' + String(det.pagar_id ?? det.id ?? idx);
                                  let rowDateEmissao = pickField(det, ['dtemissi_pag','dtemiss_pag','dtemiss','dtemissi','dtemisspag','dtemissi_pag','dtemiss_pag','dtmovi_pag','data','dtmovi']);
                                  if (!rowDateEmissao) rowDateEmissao = findByPattern(det, ['emiss','emissao','dtemiss','data','dtmovi','dtemissi']);
                                  const rowDateVenci = pickField(det, ['dtvenci_pag','dtvenc_pag','dtvenc','dtvenci']) || findByPattern(det, ['venci','vencimento','dtvenc','dtvenci']);
                                  if (!rowDateEmissao && rowDateVenci) {
                                    if (!dtemissaoWarningPagarShown.current) {
                                      try { console.debug('[detalhe] dtemissao ausente (pag) - campo `dtemissi_pag` ausente, n+úo usar dtvenci como fallback no detalhe'); } catch(e) {}
                                      dtemissaoWarningPagarShown.current = true;
                                    }
                                    // keep rowDateEmissao undefined so detalhe shows empty when emission date missing
                                  }
                                  const codigoPartsP = [] as string[];
                                  if (det.codigo_pag) codigoPartsP.push(String(det.codigo_pag));
                                  if (det.numdup_pag) codigoPartsP.push(String(det.numdup_pag));
                                  if (det.parcela_pag) codigoPartsP.push(String(det.parcela_pag));
                                  const codigo = codigoPartsP.join(' ');
                                  // Alguns endpoints/queries retornam o nome do fornecedor em `nome_cli`
                                  // (ex.: quando a tabela clientes foi usada como fonte com cliforn_cli='F').
                                  // Usar `nome_for`/`fornecedor` e, se ausente, tentar `nome_cli` como fallback.
                                  const fornecedor = det.nome_for || det.fornecedor || det.nome_cli || '';
                                  let documentoP = getFormattedDocumento(det, 'pag');
                                  if (!documentoP) {
                                    const tipoPessoaRawP = pickField(det, ['tipopessoa_pag','tipopessoa','tipo_pessoa','tipo','cliforn_cli']);
                                    const rawDocP = pickField(det, ['cgccpf_pag','cgccpf','cpf','cnpj','documento','document','cpf_for','cgccpf_for']) || findByPattern(det, ['cpf','cnpj','cgccpf','document','doc']);
                                    if (rawDocP) {
                                      const constructedP: any = {};
                                      if (tipoPessoaRawP) constructedP['tipopessoa'] = tipoPessoaRawP;
                                      constructedP['cgccpf_pag'] = rawDocP;
                                      constructedP['cgccpf'] = rawDocP;
                                      constructedP['cpf'] = rawDocP;
                                      constructedP['cnpj'] = rawDocP;
                                      constructedP['document'] = rawDocP;
                                      documentoP = getFormattedDocumento(constructedP, 'pag');
                                    }
                                    if (!documentoP) {
                                      documentoP = '';
                                      try { console.warn('[detalhe] documento ausente (pag) para linha', det); } catch(e) {}
                                    }
                                  }
                                  if (!documentoP) documentoP = '-';
                                  const valor = det.vlrsal_pag ?? det.vlrdup_pag ?? det.valor ?? null;
                                  // Obter banco espec+¡fico deste documento ou usar o banco selecionado global como default
                                  const docBanco = documentBanks[idKey] || bancoSelecionado;
                                  const isDocSelected = authorizedRows.has(idKey);
                                    return (
                                    <div key={idKey} style={{ display: 'flex', gap: '12px', alignItems: 'center', padding: '8px 0', borderBottom: '1px dashed #e5e7eb', minWidth: 0 }}>
                                      <div style={{ width: '36px', textAlign: 'center' }}>
                                        <div style={{ display: 'flex', flexDirection: 'column', alignItems: 'center' }}>
                                          <input type="checkbox" checked={isDocSelected} onChange={() => {
                                            // Se n+úo tem banco definido, usar o banco selecionado global como default
                                            if (!documentBanks[idKey] && bancoSelecionado) {
                                              setDocumentBanks(prev => ({ ...prev, [idKey]: bancoSelecionado }));
                                            }
                                            const copy = new Set(authorizedRows);
                                            if (copy.has(idKey)) copy.delete(idKey); else copy.add(idKey);
                                            setAuthorizedRows(copy);
                                          }} />
                                          {/* Dropdown para selecionar banco por documento - permite bancos diferentes */}
                                          <select
                                            value={docBanco || ''}
                                            onChange={(e) => {
                                              const novoBanco = e.target.value;
                                              setDocumentBanks(prev => ({ ...prev, [idKey]: novoBanco }));
                                              // Se o documento j+í estava selecionado, manter sele+º+úo; se n+úo, selecionar
                                              if (!authorizedRows.has(idKey) && novoBanco) {
                                                const copy = new Set(authorizedRows);
                                                copy.add(idKey);
                                                setAuthorizedRows(copy);
                                              }
                                            }}
                                            style={{ fontSize: '10px', marginTop: '2px', padding: '2px', width: '60px' }}
                                          >
                                            <option value="">--</option>
                                            {bancoOptions.map(b => (
                                              <option key={b} value={b}>{b}</option>
                                            ))}
                                          </select>
                                        </div>
                                      </div>
                                      <div style={{ width: '110px', color: '#374151' }}>{codigo}</div>
                                      <div style={{ width: '110px', color: '#6b7280' }}>{rowDateEmissao ? formatarData(rowDateEmissao) : ''}</div>
                                      <div style={{ width: '120px', color: '#6b7280', fontWeight: 600 }}>{det.tpcob_pag || det.tpcob || ''}</div>
                                      <div style={{ width: '140px', color: '#374151' }}>{documentoP}</div>
                                      <div style={{ width: '180px', color: '#374151' }}>{fornecedor}</div>
                                      <div style={{ width: '120px', textAlign: 'right', fontWeight: 700 }}>{valor ? formatarMoeda(Number(valor)) : ''}</div>
                                    </div>
                                  );
                                })}
                              </div>
                            </div>
                          </div>
                        );
                      })()
                    }
                  </div>
                )}
              </div>
            ))}
          </div>
        </>
      );
    }

    // Contas a Receber: SEM coluna Tipo, COM coluna Documento, "Clientes"
    if (filtros.tipo === 'receber') {
      const columnDefs = [
        { 
          field: 'codigo_rec', 
          headerName: 'C+¦digo', 
          width: 80,
          getQuickFilterText: (params: any) => params.value || ''
        },
        { 
          field: 'numdup_rec', 
          headerName: 'N+¦mero', 
          width: 90,
          getQuickFilterText: (params: any) => params.value || ''
        },
        { 
          field: 'parcela_rec', 
          headerName: 'Parc.', 
          width: 100,
          getQuickFilterText: (params: any) => params.value || ''
        },
        { 
          field: 'cgccpf_rec_formatted', 
          headerName: 'Documento', 
          width: 140,
          getQuickFilterText: (params: any) => params.value || ''
        },
        { 
          field: 'nome_cli', 
          headerName: 'Clientes', 
          width: 250,
          getQuickFilterText: (params: any) => params.value || ''
        },
        { 
          field: 'descr_dep', 
          headerName: 'Dpto', 
          width: 130,
          editable: true,
          cellEditor: 'agSelectCellEditor',
          cellEditorParams: {
            values: opcoesDeptos.map((d: any) => d.codigo_dep)
          },
          valueFormatter: (params: any) => {
            if (!params.value) return '';
            const depto = opcoesDeptos.find((d: any) => d.codigo_dep === params.value);
            return depto ? depto.descr_dep : params.value;
          },
          onCellValueChanged: (event: any) => handleAtualizarDepartamento(event)
        },
        { 
          field: 'tpcob_rec', 
          headerName: 'Tipo de Cobran+ºa', 
          width: 150,
          editable: true,
          cellEditor: 'agSelectCellEditor',
          cellEditorParams: {
            values: opcoesCobranca.map((c: any) => c.codigo)
          },
          valueFormatter: (params: any) => {
            if (!params.value) return '';
            const cob = opcoesCobranca.find((c: any) => c.codigo === params.value);
            return cob ? cob.descricao : params.value;
          },
          filter: 'agTextColumnFilter',
          filterParams: {
            filterOptions: ['contains', 'startsWith', 'endsWith', 'equals'],
            textFormatter: (value: string) => value,
            debounceMs: 200,
            // Adicionar lista de valores para sugest+Áes
            values: opcoesCobranca.map((c: any) => c.descricao)
          },
          onCellValueChanged: (event: any) => handleAtualizarTipoCobranca(event),
          getQuickFilterText: (params: any) => {
            if (!params.value) return '';
            const cob = opcoesCobranca.find((c: any) => c.codigo === params.value);
            return cob ? cob.descricao : params.value;
          }
        },
        { 
          field: 'descr_doc', 
          headerName: 'Tipo de Documento', 
          width: 150,
          // valueGetter tenta m+¦ltiplas chaves que podem conter a descri+º+úo
          valueGetter: (params: any) => {
            const r = params.data || {};
            return r.descr_doc || r.descr_docp || r.descr_doc_pag || r.descr_docp_pag || r.descr_docp || r.descr_doc || r.descricao || r.descr_docp || r.tipodoc_pag || r.tipodoc || r.tipodoc_rec || r.tipodoc_pag || r.tipodoc || '';
          },
          getQuickFilterText: (params: any) => {
            const v = params?.value ?? '';
            return String(v || '');
          },
          filter: 'agTextColumnFilter',
          filterParams: {
            filterOptions: ['contains', 'startsWith', 'endsWith', 'equals'],
            debounceMs: 200
          }
        },
        { 
          field: 'dtmovi_rec', 
          headerName: 'Movimento', 
          width: 110,
          valueFormatter: (params: any) => formatarData(params.value),
          getQuickFilterText: (params: any) => formatarData(params.value)
        },
        { 
          field: 'dtvenci_rec', 
          headerName: 'Vencimento', 
          width: 110,
          valueFormatter: (params: any) => formatarData(params.value),
          getQuickFilterText: (params: any) => formatarData(params.value)
        },
        { 
          field: 'dtpagi_rec', 
          headerName: 'Pago', 
          width: 110,
          valueFormatter: (params: any) => formatarData(params.value),
          getQuickFilterText: (params: any) => formatarData(params.value)
        },
        { 
          field: 'vlrdup_rec', 
          headerName: 'Valor Original (R$)', 
          width: 140,
          valueFormatter: (params: any) => formatarMoeda(params.value),
          getQuickFilterText: (params: any) => formatarMoeda(params.value)
        },
        { 
          field: 'vlrsal_rec', 
          headerName: 'Saldo (R$)', 
          width: 140,
          valueFormatter: (params: any) => formatarMoeda(params.value),
          getQuickFilterText: (params: any) => formatarMoeda(params.value),
          cellStyle: (params: any) => ({
            color: params.value > 0 ? '#dc2626' : '#059669',
            fontWeight: params.value > 0 ? 'bold' : 'normal'
          })
        },
        { 
          headerName: 'Status', 
          width: 90,
          cellRenderer: (params: any) => {
            const saldo = params.data?.vlrsal_rec || 0;
            const status = saldo > 0 ? 'Em Aberto' : 'Pago';
            return (
              <span style={{
                color: saldo > 0 ? '#dc2626' : '#059669',
                fontWeight: 'bold'
              }}>
                {status}
              </span>
            );
          },
          getQuickFilterText: (params: any) => {
            const saldo = params.data?.vlrsal_rec || 0;
            return saldo > 0 ? 'Em Aberto' : 'Pago';
          }
        },
        { 
          headerName: 'Dias em Atraso', 
          width: 120,
          cellRenderer: (params: any) => {
            const dtvenci = new Date(params.data?.dtvenci_rec);
            const saldo = params.data?.vlrsal_rec || 0;
            
            // Se tem saldo (n+úo pago), calcula em rela+º+úo a hoje
            if (saldo > 0) {
              const hoje = new Date();
              const diffTime = Math.floor((hoje.getTime() - dtvenci.getTime()) / (1000 * 60 * 60 * 24));
              
              if (diffTime < 0) {
                return <span style={{ color: '#3b82f6', fontWeight: 'bold' }}>A Vencer</span>;
              } else if (diffTime === 0) {
                return <span style={{ color: '#f59e0b', fontWeight: 'bold' }}>Vence Hoje</span>;
              } else {
                return (
                  <span style={{ 
                    color: '#dc2626', 
                    fontWeight: 'bold',
                    backgroundColor: '#fee2e2',
                    padding: '4px 8px',
                    borderRadius: '4px',
                    display: 'inline-block'
                  }}>
                    {diffTime} dias
                  </span>
                );
              }
            } 
            // Se pago (saldo = 0), calcula em rela+º+úo +á data de pagamento
            else {
              const dtpagi = params.data?.dtpagi_rec;
              if (!dtpagi) {
                return <span style={{ color: '#059669', fontWeight: 'bold' }}>Pago</span>;
              }
              
              const dataPagamento = new Date(dtpagi);
              const diffTime = Math.floor((dataPagamento.getTime() - dtvenci.getTime()) / (1000 * 60 * 60 * 24));
              
              if (diffTime <= 0) {
                // Pago no dia ou antes do vencimento
                return <span style={{ color: '#059669', fontWeight: 'bold' }}>Pago</span>;
              } else {
                // Pago depois do vencimento
                return (
                  <span style={{ 
                    color: '#dc2626', 
                    fontWeight: 'bold',
                    backgroundColor: '#fee2e2',
                    padding: '4px 8px',
                    borderRadius: '4px',
                    display: 'inline-block'
                  }}>
                    {diffTime} dias
                  </span>
                );
              }
            }
          },
          getQuickFilterText: (params: any) => {
            const dtvenci = new Date(params.data?.dtvenci_rec);
            const saldo = params.data?.vlrsal_rec || 0;
            
            // Se tem saldo (n+úo pago), calcula em rela+º+úo a hoje
            if (saldo > 0) {
              const hoje = new Date();
              const diffTime = Math.floor((hoje.getTime() - dtvenci.getTime()) / (1000 * 60 * 60 * 24));
              
              if (diffTime < 0) {
                return 'A Vencer';
              } else if (diffTime === 0) {
                return 'Vence Hoje';
              } else {
                return `${diffTime} dias`;
              }
            } 
            // Se pago (saldo = 0), calcula em rela+º+úo +á data de pagamento
            else {
              const dtpagi = params.data?.dtpagi_rec;
              if (!dtpagi) {
                return 'Pago';
              }
              
              const dataPagamento = new Date(dtpagi);
              const diffTime = Math.floor((dataPagamento.getTime() - dtvenci.getTime()) / (1000 * 60 * 60 * 24));
              
              if (diffTime <= 0) {
                return 'Pago';
              } else {
                return `${diffTime} dias`;
              }
            }
          }
        }
      ];

      return (
        <ResultTable>
          <AgGridContainer className="ag-theme-quartz">
            <AgGridReact
              columnDefs={columnDefs}
              rowData={dados}
              pinnedBottomRowData={totalRow ? [totalRow] : []}
              defaultColDef={{ resizable: true, sortable: true, filter: true, floatingFilter: true }}
              pagination={true}
              paginationPageSize={50}
              domLayout="autoHeight"
              onFilterChanged={recalcularTotal}
              onGridReady={onGridReady}
            />
          </AgGridContainer>
        </ResultTable>
      );
    }

    // Consulta Caixa e Bancos: Lista com agrupamento por Tipo (C/D) ÔåÆ Opera+º+úo ÔåÆ Data
    if (filtros.tipo === 'consulta_caixa') {
      // Estrutura: agrupar primeiro por dc_cai (C/D), depois por oper_cai, depois por data
      const gruposPorTipo: any = {
        'C': { tipo: 'C', label: 'Cr+®dito', operacoes: {}, total: 0 },
        'D': { tipo: 'D', label: 'D+®bito', operacoes: {}, total: 0 }
      };

      // Primeiro agrupamento: por tipo (C/D) e opera+º+úo
      (consultaDados || []).forEach((row: any) => {
        const tipo = row.dc_cai || 'D';
        const operacao = row.oper_cai || 'Sem Opera+º+úo';
        const valor = parseNumeric(row.valor_cai ?? row.valor ?? 0) || 0;

        if (!gruposPorTipo[tipo]) {
          gruposPorTipo[tipo] = { tipo, label: tipo === 'C' ? 'Cr+®dito' : 'D+®bito', operacoes: {}, total: 0 };
        }
        
        if (!gruposPorTipo[tipo].operacoes[operacao]) {
          gruposPorTipo[tipo].operacoes[operacao] = {
            operacao,
            valor_total: 0,
            creditos: 0,
            debitos: 0,
            dc_cai: tipo,
            detalhes: []
          };
        }

        gruposPorTipo[tipo].operacoes[operacao].valor_total += valor;
        gruposPorTipo[tipo].total += valor;
        
        if (tipo === 'C') {
          gruposPorTipo[tipo].operacoes[operacao].creditos += valor;
        } else {
          gruposPorTipo[tipo].operacoes[operacao].debitos += valor;
        }

        gruposPorTipo[tipo].operacoes[operacao].detalhes.push(row);
      });

      // Ordenar dentro de cada tipo: opera+º+Áes alfab+®tica, e detalhes por data crescente
      Object.values(gruposPorTipo).forEach((tipoGroup: any) => {
        Object.values(tipoGroup.operacoes).forEach((op: any) => {
          op.detalhes.sort((a: any, b: any) => {
            const dataA = a.dtmovi_cai ? String(a.dtmovi_cai).replace(/\D/g, '') : '99999999';
            const dataB = b.dtmovi_cai ? String(b.dtmovi_cai).replace(/\D/g, '') : '99999999';
            return dataA.localeCompare(dataB); // Crescente
          });
        });
      });

      // Montar lista de tipos ordenada: C primeiro, depois D
      const tiposOrdenados = [gruposPorTipo['C'], gruposPorTipo['D']].filter(t => Object.keys(t.operacoes).length > 0);

      // Calcular totais gerais
      const totalCreditos = tiposOrdenados.find((t: any) => t.tipo === 'C')?.total || 0;
      const totalDebitos = tiposOrdenados.find((t: any) => t.tipo === 'D')?.total || 0;
      const totalConsulta = totalCreditos - totalDebitos;

      return (
        <div style={{ padding: '0 24px', maxHeight: '65vh', overflowY: 'auto' }}>
          {/* Renderizar por Tipo (C/D) e depois por Opera+º+úo dentro de cada tipo */}
          {tiposOrdenados.map((tipoGroup: any) => (
            <div key={tipoGroup.tipo} style={{ marginBottom: '24px' }}>
              {/* Subt+¡tulo por Tipo */}
              <div style={{ 
                fontSize: '16px', 
                fontWeight: 'bold', 
                color: tipoGroup.tipo === 'C' ? '#059669' : '#dc2626',
                marginBottom: '12px',
                display: 'flex',
                alignItems: 'center',
                gap: '8px'
              }}>
                {tipoGroup.tipo === 'C' ? '­ƒôÑ CR+ëDITOS' : '­ƒôñ D+ëBITOS'}
                <span style={{ fontSize: '12px', fontWeight: 'normal', color: '#6b7280' }}>
                  ({(Object.values(tipoGroup.operacoes) as any[]).reduce((sum: number, op: any) => sum + (op.detalhes?.length || 0), 0)} lan+ºamentos)
                </span>
              </div>

              {/* Linha de Cabe+ºalhos */}
              <div style={{ 
                display: 'flex', 
                justifyContent: 'space-between',
                alignItems: 'center',
                padding: '8px 14px',
                background: '#f3f4f6',
                border: '1px solid #e5e7eb',
                borderRadius: '4px',
                fontSize: '12px',
                fontWeight: '600',
                color: '#6b7280',
                marginBottom: '8px'
              }}>
                <div>OPERA+ç+âO</div>
                <div style={{ display: 'flex', gap: '24px', alignItems: 'center' }}>
                  <div style={{ textAlign: 'right', minWidth: '120px' }}>REALIZADO</div>
                  <div style={{ textAlign: 'right', minWidth: '110px' }}>PREVISTO</div>
                  <div style={{ textAlign: 'right', minWidth: '90px' }}>+ìNDICE</div>
                </div>
              </div>

              {/* Renderizar Opera+º+Áes dentro deste Tipo */}
              {Object.values(tipoGroup.operacoes).map((grupo: any) => {
                const expandKey = `${tipoGroup.tipo}-${grupo.operacao}`;
                return (
                <div key={expandKey} id={`operacao-${expandKey}`} style={{ marginBottom: '12px' }}>
                  {/* Cabe+ºalho Expans+¡vel */}
                  <div
                    onClick={() => toggleExpandir(expandKey)}
                    style={{
                      display: 'flex',
                      justifyContent: 'space-between',
                      alignItems: 'center',
                      padding: '12px 14px',
                      background: '#fff',
                      border: '1px solid #e5e7eb',
                      borderRadius: '6px 6px 0 0',
                      cursor: 'pointer',
                      transition: 'background 0.2s',
                      userSelect: 'none'
                    }}
                    onMouseEnter={(e) => e.currentTarget.style.background = '#f9fafb'}
                    onMouseLeave={(e) => e.currentTarget.style.background = '#fff'}
                  >
                    <div style={{ display: 'flex', alignItems: 'center', gap: '12px' }}>
                      <FontAwesomeIcon 
                        icon={expandedDates.has(expandKey) ? faChevronDown : faChevronRight}
                        style={{ color: '#6b7280', width: '16px' }}
                      />
                      <div style={{ fontSize: '14px', fontWeight: '600', color: '#1f2937' }}>
                        {grupo.operacao} ({grupo.detalhes.length})
                      </div>
                    </div>
                    <div style={{ display: 'flex', gap: '24px', alignItems: 'center' }}>
                      {/* Realizado */}
                      <div style={{ textAlign: 'right', minWidth: '120px' }}>
                        <div style={{
                          fontSize: '13px',
                          fontWeight: 'bold',
                          color: grupo.dc_cai === 'C' ? '#059669' : '#dc2626',
                        }}>
                          {grupo.dc_cai === 'C' ? '+' : '-'} {formatarMoeda(Math.abs(grupo.valor_total))}
                        </div>
                      </div>

                      {/* Previsto */}
                      {(() => {
                        // Tentar m+¦ltiplas chaves de lookup
                        const tipo = tipoGroup.tipo; // 'C' ou 'D'
                        const operacao = grupo.operacao; // ex: "RECEBIMENTO MATERIAL USO E CONSUMO DIVERSO"
                        let prevKey = `${tipo}-${operacao}`;
                        let prevData = previsoesPorOperacao[prevKey];
                        
                        // Se n+úo encontrar, tentar outras chaves
                        if (!prevData || prevData.previsto === undefined || prevData.previsto === 0) {
                          // Tentar com valor num+®rico do operacao_ocai (se foi extra+¡do assim)
                          for (let key of Object.keys(previsoesPorOperacao)) {
                            if (key.startsWith(`${tipo}-`) && key.includes(operacao.substring(0, 30))) {
                              prevData = previsoesPorOperacao[key];
                              prevKey = key;
                              break;
                            }
                          }
                        }
                        
                        const prevValue = prevData?.previsto || 0;
                        return (
                          <div style={{ textAlign: 'right', minWidth: '110px' }}>
                            <div style={{
                              fontSize: '13px',
                              color: prevValue > 0 ? '#2563eb' : '#999',
                              fontWeight: prevValue > 0 ? '600' : '400'
                            }}>
                              {prevValue > 0 ? formatarMoeda(prevValue) : 'ÔÇö'}
                            </div>
                          </div>
                        );
                      })()}

                      {/* +ìndice (Realizado / Previsto) */}
                      {(() => {
                        // Tentar m+¦ltiplas chaves de lookup
                        const tipo = tipoGroup.tipo;
                        const operacao = grupo.operacao;
                        let prevKey = `${tipo}-${operacao}`;
                        let prevData = previsoesPorOperacao[prevKey];
                        
                        // Se n+úo encontrar, tentar outras chaves
                        if (!prevData || prevData.previsto === undefined || prevData.previsto === 0) {
                          for (let key of Object.keys(previsoesPorOperacao)) {
                            if (key.startsWith(`${tipo}-`) && key.includes(operacao.substring(0, 30))) {
                              prevData = previsoesPorOperacao[key];
                              prevKey = key;
                              break;
                            }
                          }
                        }
                        
                        const prevValue = prevData?.previsto || 0;
                        const indice = prevValue > 0 ? ((Math.abs(grupo.valor_total) / prevValue) * 100).toFixed(1) : 0;
                        const indiceNum = Number(indice);
                        return (
                          <div style={{ textAlign: 'right', minWidth: '90px' }}>
                            <div style={{
                              fontSize: '13px',
                              fontWeight: prevValue > 0 ? '600' : '400',
                              color: indiceNum > 100 ? '#dc2626' : (prevValue > 0 ? '#059669' : '#999'),
                            }}>
                              {prevValue > 0 ? `${indice}%` : 'ÔÇö'}
                            </div>
                          </div>
                        );
                      })()}
                    </div>
                  </div>

                  {/* Detalhes Expandidos */}
                  {(() => {
                    const isExpanded = expandedDates.has(expandKey);
                    if (grupo.operacao === 'PRO-LABORE') {
                      console.log('[RENDER-OPERACAO] PRO-LABORE - expandKey:', expandKey, 'isExpanded:', isExpanded, 'expandedDates:', Array.from(expandedDates));
                    }
                    return isExpanded;
                  })() && (
                    <div style={{ background: '#f9fafb', borderLeft: '1px solid #e5e7eb', borderRight: '1px solid #e5e7eb', borderBottom: '1px solid #e5e7eb', borderRadius: '0 0 6px 6px', padding: '8px 0' }}>
                      {grupo.detalhes.map((row: any, idx: number) => (
                        <div key={idx} style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start', padding: '10px 16px', borderBottom: idx < grupo.detalhes.length - 1 ? '1px solid #e5e7eb' : 'none', fontSize: '13px' }}>
                          <div style={{ flex: 1 }}>
                            <div style={{ fontWeight: 500, color: '#1f2937', marginBottom: '4px' }}>
                              {row.nome_cai || row.tipocai_cai || row.codbanco_cai || 'ÔÇö'}
                            </div>
                            <div style={{ display: 'flex', gap: '12px', alignItems: 'flex-start', marginTop: '4px', flexWrap: 'wrap' }}>
                              <div style={{ fontSize: '12px', color: '#6b7280', minWidth: '90px' }}>
                                ­ƒôà {formatarData(row.dtmovi_cai)}
                              </div>
                              {row.seq_cai && (
                                <div style={{ fontSize: '12px', color: '#6b7280' }}>
                                  Seq: {row.seq_cai}
                                </div>
                              )}
                              {row.histor_cai && (
                                <div style={{ fontSize: '12px', color: '#4b5563', fontStyle: 'italic', flex: 1 }}>
                                  ­ƒôØ {row.histor_cai}
                                </div>
                              )}
                            </div>
                          </div>
                          <div style={{ textAlign: 'right', marginRight: '12px', minWidth: '140px' }}>
                            <div style={{ fontSize: '13px', fontWeight: '600', color: row.dc_cai === 'C' ? '#059669' : '#dc2626', marginBottom: '4px' }}>
                              {row.dc_cai === 'C' ? '+' : '-'} {formatarMoeda(row.valor_cai || row.valor || 0)}
                            </div>
                            {row.vlrprev_cai && (
                              <div style={{ fontSize: '11px', color: '#6b7280' }}>
                                Previsto: {formatarMoeda(row.vlrprev_cai)}
                              </div>
                            )}
                          </div>
                          <button 
                            className="btn btn-sm btn-primary"
                            style={{ fontSize: '11px', padding: '4px 8px', whiteSpace: 'nowrap', marginLeft: '8px' }}
                            onClick={() => abrirFormularioCaixaEditar(row)}
                          >
                            Editar
                          </button>
                        </div>
                      ))}
                    </div>
                  )}
                </div>
                );
              })}

              {/* Subtotal por Tipo */}
              <div style={{
                background: tipoGroup.tipo === 'C' ? '#d1fae5' : '#fee2e2',
                border: `2px solid ${tipoGroup.tipo === 'C' ? '#059669' : '#dc2626'}`,
                borderRadius: '6px',
                padding: '12px 16px',
                marginBottom: '12px',
                display: 'flex',
                justifyContent: 'space-between',
                alignItems: 'center'
              }}>
                <div style={{ fontSize: '14px', fontWeight: 'bold', color: tipoGroup.tipo === 'C' ? '#059669' : '#dc2626' }}>
                  Subtotal {tipoGroup.label}
                </div>
                <div style={{ display: 'flex', gap: '24px', alignItems: 'center' }}>
                  {/* Realizado */}
                  <div style={{ textAlign: 'right', minWidth: '120px' }}>
                    <div style={{ fontSize: '14px', fontWeight: 'bold', color: tipoGroup.tipo === 'C' ? '#059669' : '#dc2626' }}>
                      {tipoGroup.tipo === 'C' ? '+' : '-'} {formatarMoeda(Math.abs(tipoGroup.total))}
                    </div>
                  </div>

                  {/* Previsto (Soma de todas opera+º+Áes deste tipo) */}
                  {(() => {
                    const prevValue = Object.values(tipoGroup.operacoes).reduce((sum: number, op: any) => {
                      const prevKey = `${tipoGroup.tipo}-${op.operacao}`;
                      return sum + (previsoesPorOperacao[prevKey]?.previsto || 0);
                    }, 0) as number;
                    return (
                      <div style={{ textAlign: 'right', minWidth: '110px' }}>
                        <div style={{
                          fontSize: '14px',
                          fontWeight: '600',
                          color: '#666',
                        }}>
                          {prevValue > 0 ? formatarMoeda(Number(prevValue)) : 'ÔÇö'}
                        </div>
                      </div>
                    );
                  })()}

                  {/* +ìndice (Realizado / Previsto) */}
                  {(() => {
                    const prevValue = Object.values(tipoGroup.operacoes).reduce((sum: number, op: any) => {
                      const prevKey = `${tipoGroup.tipo}-${op.operacao}`;
                      return sum + (previsoesPorOperacao[prevKey]?.previsto || 0);
                    }, 0) as number;
                    const indice = prevValue > 0 ? ((Math.abs(tipoGroup.total) / prevValue) * 100).toFixed(1) : 0;
                    const indiceNum = Number(indice);
                    return (
                      <div style={{ textAlign: 'right', minWidth: '90px' }}>
                        <div style={{
                          fontSize: '14px',
                          fontWeight: '600',
                          color: indiceNum > 100 ? '#dc2626' : '#059669',
                        }}>
                          {prevValue > 0 ? `${indice}%` : 'ÔÇö'}
                        </div>
                      </div>
                    );
                  })()}
                </div>
              </div>
            </div>
          ))}

          {/* Linha de Total GERAL - Destacada e sempre vis+¡vel */}
          <div style={{
            position: 'sticky', 
            bottom: 0,
            marginTop: '24px', 
            padding: '16px 20px', 
            background: 'linear-gradient(135deg, #0f172a 0%, #1e293b 100%)',
            border: '3px solid #059669',
            borderRadius: '8px',
            display: 'flex', 
            justifyContent: 'space-between', 
            alignItems: 'center',
            boxShadow: '0 -4px 12px rgba(0,0,0,0.15)',
            zIndex: 10
          }}>
            <div style={{ 
              fontSize: '16px', 
              fontWeight: 'bold', 
              color: '#ffffff',
              display: 'flex',
              alignItems: 'center',
              gap: '8px'
            }}>
              ­ƒÆ¦ TOTAL GERAL FILTRADO
            </div>
            <div style={{ display: 'flex', gap: '32px', alignItems: 'center' }}>
              {/* Total Cr+®ditos - Realizado */}
              <div style={{ textAlign: 'right' }}>
                <div style={{ fontSize: '12px', color: '#a0aec0' }}>Cr+®ditos (Real)</div>
                <div style={{ fontSize: '16px', fontWeight: 'bold', color: '#059669' }}>
                  {formatarMoeda(totalCreditos)}
                </div>
              </div>

              {/* Total Cr+®ditos - Previsto */}
              {(() => {
                const prevCred = Object.values(gruposPorTipo['C']?.operacoes || {}).reduce((sum: number, op: any) => {
                  const prevKey = `C-${op.operacao}`;
                  return sum + (previsoesPorOperacao[prevKey]?.previsto || 0);
                }, 0) as number;
                return (
                  <div style={{ textAlign: 'right' }}>
                    <div style={{ fontSize: '12px', color: '#a0aec0' }}>Cr+®ditos (Prev)</div>
                    <div style={{ fontSize: '16px', fontWeight: 'bold', color: '#6b7280' }}>
                      {prevCred > 0 ? formatarMoeda(Number(prevCred)) : 'ÔÇö'}
                    </div>
                  </div>
                );
              })()}

              {/* Total Cr+®ditos - +ìndice */}
              {(() => {
                const prevCred = Object.values(gruposPorTipo['C']?.operacoes || {}).reduce((sum: number, op: any) => {
                  const prevKey = `C-${op.operacao}`;
                  return sum + (previsoesPorOperacao[prevKey]?.previsto || 0);
                }, 0) as number;
                const indiceCred = prevCred > 0 ? ((totalCreditos / prevCred) * 100).toFixed(1) : 0;
                const indiceCreditNum = Number(indiceCred);
                return (
                  <div style={{ textAlign: 'right' }}>
                    <div style={{ fontSize: '12px', color: '#a0aec0' }}>Cr+®ditos (+ìndice)</div>
                    <div style={{ fontSize: '16px', fontWeight: 'bold', color: indiceCreditNum > 100 ? '#dc2626' : '#059669' }}>
                      {prevCred > 0 ? `${indiceCred}%` : 'ÔÇö'}
                    </div>
                  </div>
                );
              })()}
              
              {/* Total D+®bitos - Realizado */}
              <div style={{ textAlign: 'right' }}>
                <div style={{ fontSize: '12px', color: '#a0aec0' }}>D+®bitos (Real)</div>
                <div style={{ fontSize: '16px', fontWeight: 'bold', color: '#dc2626' }}>
                  {formatarMoeda(totalDebitos)}
                </div>
              </div>

              {/* Total D+®bitos - Previsto */}
              {(() => {
                const prevDeb = Object.values(gruposPorTipo['D']?.operacoes || {}).reduce((sum: number, op: any) => {
                  const prevKey = `D-${op.operacao}`;
                  return sum + (previsoesPorOperacao[prevKey]?.previsto || 0);
                }, 0) as number;
                return (
                  <div style={{ textAlign: 'right' }}>
                    <div style={{ fontSize: '12px', color: '#a0aec0' }}>D+®bitos (Prev)</div>
                    <div style={{ fontSize: '16px', fontWeight: 'bold', color: '#6b7280' }}>
                      {prevDeb > 0 ? formatarMoeda(Number(prevDeb)) : 'ÔÇö'}
                    </div>
                  </div>
                );
              })()}

              {/* Total D+®bitos - +ìndice */}
              {(() => {
                const prevDeb = Object.values(gruposPorTipo['D']?.operacoes || {}).reduce((sum: number, op: any) => {
                  const prevKey = `D-${op.operacao}`;
                  return sum + (previsoesPorOperacao[prevKey]?.previsto || 0);
                }, 0) as number;
                const indiceDeb = prevDeb > 0 ? ((totalDebitos / prevDeb) * 100).toFixed(1) : 0;
                const indiceDebNum = Number(indiceDeb);
                return (
                  <div style={{ textAlign: 'right' }}>
                    <div style={{ fontSize: '12px', color: '#a0aec0' }}>D+®bitos (+ìndice)</div>
                    <div style={{ fontSize: '16px', fontWeight: 'bold', color: indiceDebNum > 100 ? '#dc2626' : '#059669' }}>
                      {prevDeb > 0 ? `${indiceDeb}%` : 'ÔÇö'}
                    </div>
                  </div>
                );
              })()}

              {/* Total L+¡quido */}
              <div style={{ textAlign: 'right', minWidth: '180px' }}>
                <div style={{ fontSize: '12px', color: '#a0aec0' }}>Total</div>
                <div style={{
                  fontSize: '18px', 
                  fontWeight: 'bold', 
                  color: totalConsulta >= 0 ? '#10b981' : '#dc2626',
                  backgroundColor: totalConsulta >= 0 ? '#d1fae5' : '#fee2e2',
                  padding: '8px 16px',
                  borderRadius: '6px',
                  textAlign: 'center'
                }}>
                  {formatarMoeda(totalConsulta)}
                </div>
              </div>
            </div>
          </div>
        </div>
      );
    }

    // Contas a Pagar: SEM coluna Tipo, COM coluna Documento, "Fornecedores"
    const columnDefs = [
      { 
        field: 'codigo_pag', 
        headerName: 'C+¦digo', 
        width: 80,
        getQuickFilterText: (params: any) => params.value || ''
      },
      { 
        field: 'numdup_pag', 
        headerName: 'N+¦mero', 
        width: 90,
        getQuickFilterText: (params: any) => params.value || ''
      },
      { 
        field: 'parcela_pag', 
        headerName: 'Parc.', 
        width: 100,
        getQuickFilterText: (params: any) => params.value || ''
      },
      { 
        field: 'cgccpf_pag_formatted', 
        headerName: 'Documento', 
        width: 140,
        getQuickFilterText: (params: any) => params.value || ''
      },
      { 
        field: 'nome_for', 
        headerName: 'Fornecedores', 
        width: 250,
        getQuickFilterText: (params: any) => params.value || ''
      },
      {
        field: 'descr_dep',
        headerName: 'Dpto',
        width: 130,
        editable: true,
        cellEditor: 'agSelectCellEditor',
        cellEditorParams: {
          values: opcoesDeptos.map((d: any) => d.codigo_dep)
        },
        valueFormatter: (params: any) => {
          if (!params.value) return '';
          const depto = opcoesDeptos.find((d: any) => d.codigo_dep === params.value);
          return depto ? depto.descr_dep : params.value;
        },
        onCellValueChanged: (event: any) => handleAtualizarDepartamento(event)
      },
      { 
        field: 'tpcob_pag', 
        headerName: 'Tipo de Cobran+ºa', 
        width: 150,
        editable: true,
        cellEditor: 'agSelectCellEditor',
        cellEditorParams: {
          values: opcoesCobranca.map((c: any) => c.codigo)
        },
        valueFormatter: (params: any) => {
          if (!params.value) return '';
          const cob = opcoesCobranca.find((c: any) => c.codigo === params.value);
          return cob ? cob.descricao : params.value;
        },
        filter: 'agTextColumnFilter',
        filterParams: {
          filterOptions: ['contains', 'startsWith', 'endsWith', 'equals'],
          textFormatter: (value: string) => value,
          debounceMs: 200,
          // Adicionar lista de valores para sugest+Áes
          values: opcoesCobranca.map((c: any) => c.descricao)
        },
        onCellValueChanged: (event: any) => handleAtualizarTipoCobranca(event),
        getQuickFilterText: (params: any) => {
          if (!params.value) return '';
          const cob = opcoesCobranca.find((c: any) => c.codigo === params.value);
          return cob ? cob.descricao : params.value;
        }
      },
      { 
        field: 'descr_doc', 
        headerName: 'Tipo de Documento', 
        width: 150,
        valueGetter: (params: any) => {
          const r = params.data || {};
          return r.descr_doc || r.descr_docp || r.descr_doc_pag || r.descr_docp_pag || r.descr_docp || r.descr_doc || r.descricao || r.descr_docp || r.tipodoc_pag || r.tipodoc || r.tipodoc_pag || r.tipodoc || '';
        },
        getQuickFilterText: (params: any) => {
          const v = params?.value ?? '';
          return String(v || '');
        },
        filter: 'agTextColumnFilter',
        filterParams: {
          filterOptions: ['contains', 'startsWith', 'endsWith', 'equals'],
          debounceMs: 200
        }
      },
      { 
        field: 'dtmovi_pag', 
        headerName: 'Movimento', 
        width: 110,
        valueFormatter: (params: any) => formatarData(params.value),
        getQuickFilterText: (params: any) => formatarData(params.value)
      },
      { 
        field: 'dtvenci_pag', 
        headerName: 'Vencimento', 
        width: 110,
        valueFormatter: (params: any) => formatarData(params.value),
        getQuickFilterText: (params: any) => formatarData(params.value)
      },
      { 
        field: 'dtpagi_pag', 
        headerName: 'Pago', 
        width: 110,
        valueFormatter: (params: any) => formatarData(params.value),
        getQuickFilterText: (params: any) => formatarData(params.value)
      },
      { 
        field: 'vlrdup_pag', 
        headerName: 'Valor Original (R$)', 
        width: 140,
        valueFormatter: (params: any) => formatarMoeda(params.value),
        getQuickFilterText: (params: any) => formatarMoeda(params.value)
      },
      { 
        field: 'vlrsal_pag', 
        headerName: 'Saldo (R$)', 
        width: 140,
        valueFormatter: (params: any) => formatarMoeda(params.value),
        getQuickFilterText: (params: any) => formatarMoeda(params.value),
        cellStyle: (params: any) => ({
          color: params.value > 0 ? '#dc2626' : '#059669',
          fontWeight: params.value > 0 ? 'bold' : 'normal'
        })
      },
      { 
        headerName: 'Status', 
        width: 90,
        cellRenderer: (params: any) => {
          const saldo = params.data?.vlrsal_pag || 0;
          const status = saldo > 0 ? 'Em Aberto' : 'Pago';
          return (
            <span style={{
              color: saldo > 0 ? '#dc2626' : '#059669',
              fontWeight: 'bold'
            }}>
              {status}
            </span>
          );
        },
        getQuickFilterText: (params: any) => {
          const saldo = params.data?.vlrsal_pag || 0;
          return saldo > 0 ? 'Em Aberto' : 'Pago';
        }
      },
      { 
        headerName: 'Dias em Atraso', 
        width: 120,
        cellRenderer: (params: any) => {
          const dtvenci = new Date(params.data?.dtvenci_pag);
          const saldo = params.data?.vlrsal_pag || 0;
          
          // Se tem saldo (n+úo pago), calcula em rela+º+úo a hoje
          if (saldo > 0) {
            const hoje = new Date();
            const diffTime = Math.floor((hoje.getTime() - dtvenci.getTime()) / (1000 * 60 * 60 * 24));
            
            if (diffTime < 0) {
              return <span style={{ color: '#3b82f6', fontWeight: 'bold' }}>A Vencer</span>;
            } else if (diffTime === 0) {
              return <span style={{ color: '#f59e0b', fontWeight: 'bold' }}>Vence Hoje</span>;
            } else {
              return (
                <span style={{ 
                  color: '#dc2626', 
                  fontWeight: 'bold',
                  backgroundColor: '#fee2e2',
                  padding: '4px 8px',
                  borderRadius: '4px',
                  display: 'inline-block'
                }}>
                  {diffTime} dias
                </span>
              );
            }
          } 
          // Se pago (saldo = 0), calcula em rela+º+úo +á data de pagamento
          else {
            const dtpagi = params.data?.dtpagi_pag;
            if (!dtpagi) {
              return <span style={{ color: '#059669', fontWeight: 'bold' }}>Pago</span>;
            }
            
            const dataPagamento = new Date(dtpagi);
            const diffTime = Math.floor((dataPagamento.getTime() - dtvenci.getTime()) / (1000 * 60 * 60 * 24));
            
            if (diffTime <= 0) {
              // Pago no dia ou antes do vencimento
              return <span style={{ color: '#059669', fontWeight: 'bold' }}>Pago</span>;
            } else {
              // Pago depois do vencimento
              return (
                <span style={{ 
                  color: '#dc2626', 
                  fontWeight: 'bold',
                  backgroundColor: '#fee2e2',
                  padding: '4px 8px',
                  borderRadius: '4px',
                  display: 'inline-block'
                }}>
                  {diffTime} dias
                </span>
              );
            }
          }
        },
        getQuickFilterText: (params: any) => {
          const dtvenci = new Date(params.data?.dtvenci_pag);
          const saldo = params.data?.vlrsal_pag || 0;
          
          // Se tem saldo (n+úo pago), calcula em rela+º+úo a hoje
          if (saldo > 0) {
            const hoje = new Date();
            const diffTime = Math.floor((hoje.getTime() - dtvenci.getTime()) / (1000 * 60 * 60 * 24));
            
            if (diffTime < 0) {
              return 'A Vencer';
            } else if (diffTime === 0) {
              return 'Vence Hoje';
            } else {
              return `${diffTime} dias`;
            }
          } 
          // Se pago (saldo = 0), calcula em rela+º+úo +á data de pagamento
          else {
            const dtpagi = params.data?.dtpagi_pag;
            if (!dtpagi) {
              return 'Pago';
            }
            
            const dataPagamento = new Date(dtpagi);
            const diffTime = Math.floor((dataPagamento.getTime() - dtvenci.getTime()) / (1000 * 60 * 60 * 24));
            
            if (diffTime <= 0) {
              return 'Pago';
            } else {
              return `${diffTime} dias`;
            }
          }
        }
      }
    ];

    return (
      <ResultTable>
        <AgGridContainer className="ag-theme-quartz">
          <AgGridReact
            columnDefs={columnDefs}
            rowData={dados}
            pinnedBottomRowData={totalRow ? [totalRow] : []}
            defaultColDef={{ resizable: true, sortable: true, filter: true, floatingFilter: true }}
            pagination={true}
            paginationPageSize={50}
            domLayout="autoHeight"
            onFilterChanged={recalcularTotal}
            onGridReady={onGridReady}
          />
        </AgGridContainer>
      </ResultTable>
    );
  };

  return (
    <Container style={{ height }}>
      <Header style={{ marginBottom: '8px' }}>
        <Title>
          <FontAwesomeIcon icon={faFileInvoiceDollar} />
          Relat+¦rios Financeiros
        </Title>
      </Header>

      <SubMenu>
        {subMenuItems.map(item => (
          <SubMenuItem
            key={item.key}
            $active={tipoAtivo === item.key}
            onClick={() => handleSubMenuClick(item.key)}
          >
            <FontAwesomeIcon icon={item.icon} style={{ color: item.color }} />
            {item.label}
          </SubMenuItem>
        ))}
      </SubMenu>

      <Content>
        {filtros.tipo !== 'consulta_caixa' && (
          <FilterCard>
            {tipoAtivo === 'fluxo' ? (
              // Exibir cards de per+¡odo APENAS para Fluxo de Caixa
              <PeriodCardsContainer style={{ gap: 12 }}>
                <FilterTitle $collapsed={periodCollapsed} onClick={() => setPeriodCollapsed(!periodCollapsed)} style={{ marginTop: 0 }}>
                  <FontAwesomeIcon icon={faCalendarAlt} />
                  Per+¡odo de An+ílise
                  <FontAwesomeIcon icon={periodCollapsed ? faChevronRight : faChevronDown} className="chevron" />
                </FilterTitle>
                <FilterContent $collapsed={periodCollapsed}>
                  {/* Layout: bancos acima, depois bot+Áes de per+¡odo +á direita com Exportar PDF */}
                  <div style={{ display: 'flex', gap: 12, alignItems: 'center', justifyContent: 'space-between', marginTop: 6 }}>
                    <div style={{ flex: '1 1 auto' }}>
                      {/* Bank cards: agora aparecem antes dos period buttons */}
                      <div style={{ display: 'flex', gap: 12, alignItems: 'center', flexWrap: 'wrap' }}>
                        {bancoOptions && bancoOptions.length > 0 ? bancoOptions.map((b) => (
                          <div
                            key={b}
                            onClick={() => {
                              setBancoSelecionado(b);
                              try {
                                const hoje = formatYMDUTC(new Date());
                                const novo = new Set(expandedDates);
                                novo.add(hoje);
                                setExpandedDates(novo);
                                setTimeout(() => {
                                  const el = document.getElementById(`grupo-${hoje}`);
                                  if (el && el.scrollIntoView) el.scrollIntoView({ behavior: 'smooth', block: 'center' });
                                }, 150);
                              } catch(e) { /* ignore */ }
                            }}
                            style={{
                              padding: '10px 14px',
                              borderRadius: 8,
                              cursor: 'pointer',
                              border: bancoSelecionado === b ? '2px solid #0369a1' : '1px solid #e5e7eb',
                              background: bancoSelecionado === b ? '#eff6ff' : '#fff',
                              minWidth: 140,
                              display: 'flex',
                              flexDirection: 'column',
                              gap: 6
                            }}
                          >
                            <div style={{ fontSize: 13, fontWeight: 700 }}>{b}</div>
                            <div style={{ fontSize: 12, color: '#6b7280' }}>{(saldosPorBanco[b] || 0).toLocaleString('pt-BR', { style: 'currency', currency: 'BRL' })}</div>
                          </div>
                        )) : (
                          <div style={{ color: '#9ca3af', fontSize: 13 }}>Nenhum banco encontrado nos resultados</div>
                        )}
                      </div>
                    </div>

                    <div style={{ display: 'flex', alignItems: 'center', gap: 12 }}>
                      <div style={{ display: 'flex', gap: 8, alignItems: 'center' }}>
                        {/* Period buttons (30/60/90/120) */}
                        <PeriodCardButton
                          $isSelected={filtros.faixaAtraso === '30'}
                          $color="#0369a1"
                          onClick={() => {
                            const hoje = new Date();
                            const dataFinal = new Date(hoje);
                            dataFinal.setDate(dataFinal.getDate() + 30);
                            const newFiltros: FiltroRelatorio = {
                              ...filtros,
                              faixaAtraso: '30',
                              dataFiltroInicial: formatYMDUTC(hoje),
                              dataFiltroFinal: formatYMDUTC(dataFinal),
                              tipo: 'fluxo'
                            };
                            setFiltros(newFiltros);
                            setDados([]);
                            buscarDados(newFiltros);
                          }}
                        >
                          <span className="period-value">30</span>
                          <span className="period-label">dias</span>
                        </PeriodCardButton>
                        <PeriodCardButton
                          $isSelected={filtros.faixaAtraso === '60'}
                          $color="#0369a1"
                          onClick={() => {
                            const hoje = new Date();
                            const dataFinal = new Date(hoje);
                            dataFinal.setDate(dataFinal.getDate() + 60);
                            const newFiltros: FiltroRelatorio = {
                              ...filtros,
                              faixaAtraso: '60',
                              dataFiltroInicial: formatYMDUTC(hoje),
                              dataFiltroFinal: formatYMDUTC(dataFinal),
                              tipo: 'fluxo'
                            };
                            setFiltros(newFiltros);
                            setDados([]);
                            buscarDados(newFiltros);
                          }}
                        >
                          <span className="period-value">60</span>
                          <span className="period-label">dias</span>
                        </PeriodCardButton>
                        <PeriodCardButton
                          $isSelected={filtros.faixaAtraso === '90'}
                          $color="#0369a1"
                          onClick={() => {
                            const hoje = new Date();
                            const dataFinal = new Date(hoje);
                            dataFinal.setDate(dataFinal.getDate() + 90);
                            const newFiltros: FiltroRelatorio = {
                              ...filtros,
                              faixaAtraso: '90',
                              dataFiltroInicial: formatYMDUTC(hoje),
                              dataFiltroFinal: formatYMDUTC(dataFinal),
                              tipo: 'fluxo'
                            };
                            setFiltros(newFiltros);
                            setDados([]);
                            buscarDados(newFiltros);
                          }}
                        >
                          <span className="period-value">90</span>
                          <span className="period-label">dias</span>
                        </PeriodCardButton>
                        <PeriodCardButton
                          $isSelected={filtros.faixaAtraso === '120'}
                          $color="#0369a1"
                          onClick={() => {
                            const hoje = new Date();
                            const dataFinal = new Date(hoje);
                            dataFinal.setDate(dataFinal.getDate() + 120);
                            const newFiltros: FiltroRelatorio = {
                              ...filtros,
                              faixaAtraso: '120',
                              dataFiltroInicial: formatYMDUTC(hoje),
                              dataFiltroFinal: formatYMDUTC(dataFinal),
                              tipo: 'fluxo'
                            };
                            setFiltros(newFiltros);
                            setDados([]);
                            buscarDados(newFiltros);
                          }}
                        >
                          <span className="period-value">120</span>
                          <span className="period-label">dias</span>
                        </PeriodCardButton>
                      </div>

                      {/* Bot+Áes Exportar CSV e PDF alinhados com os period buttons */}
                      <div style={{ display: 'flex', gap: '8px' }}>
                        {dados.length > 0 && (
                          <>
                            <Button type="button" onClick={exportarCSV} style={{ marginLeft: 0, background: '#059669', borderColor: '#059669' }}>
                              <FontAwesomeIcon icon={faDownload} />
                              Exportar CSV
                            </Button>
                            <Button type="button" onClick={exportarRelatorio} style={{ marginLeft: 0 }}>
                              <FontAwesomeIcon icon={faDownload} />
                              Exportar PDF
                            </Button>
                          </>
                        )}
                      </div>
                    </div>
                  </div>

                  {/* Fluxo KPIs (abaixo, ocupando menos espa+ºo) */}
                  {filtros.tipo === 'fluxo' && (
                    <div style={{ marginTop: 8, marginBottom: 4 }}>
                      <div style={{ display: 'grid', gridTemplateColumns: 'repeat(4, 1fr)', gap: '8px' }}>
                        <div style={{ padding: '8px 12px', borderRadius: 8, minHeight: 48, display: 'flex', flexDirection: 'column', justifyContent: 'center', background: fluxoKpis && fluxoKpis.saldoHoje >= 0 ? '#d1fae5' : '#fee2e2', border: `2px solid ${fluxoKpis && fluxoKpis.saldoHoje >= 0 ? '#059669' : '#dc2626'}` }}>
                          <div style={{ fontSize: 11, color: fluxoKpis && fluxoKpis.saldoHoje >= 0 ? '#047857' : '#b91c1c', fontWeight: 600 }}>­ƒôà HOJE</div>
                          <div style={{ fontSize: 14, fontWeight: 'bold', marginTop: 4 }}>{fluxoGlobalKPIs ? formatarMoeda(fluxoGlobalKPIs.saldoHoje) : (fluxoKpis ? formatarMoeda(fluxoKpis.saldoHoje) : '-')}</div>
                        </div>
                        <div style={{ padding: '8px 12px', borderRadius: 8, minHeight: 48, display: 'flex', flexDirection: 'column', justifyContent: 'center', background: '#d1fae5', border: '2px solid #059669' }}>
                          <div style={{ fontSize: 11, color: '#047857', fontWeight: 600 }}>­ƒÆ¦ Total a Receber</div>
                          <div style={{ fontSize: 14, fontWeight: 'bold', marginTop: 4 }}>{(fluxoGlobalKPIs ? formatarMoeda(fluxoGlobalKPIs.totalEntradas) : (fluxoKpis ? formatarMoeda(fluxoKpis.totalEntradas) : '-'))}</div>
                        </div>
                        <div style={{ padding: '8px 12px', borderRadius: 8, minHeight: 48, display: 'flex', flexDirection: 'column', justifyContent: 'center', background: '#fee2e2', border: '2px solid #dc2626' }}>
                          <div style={{ fontSize: 11, color: '#b91c1c', fontWeight: 600 }}>­ƒÆ© Total a Pagar</div>
                          <div style={{ fontSize: 14, fontWeight: 'bold', marginTop: 4 }}>{(fluxoGlobalKPIs ? formatarMoeda(fluxoGlobalKPIs.totalSaidas) : (fluxoKpis ? formatarMoeda(fluxoKpis.totalSaidas) : '-'))}</div>
                        </div>
                        <div style={{ padding: '8px 12px', borderRadius: 8, minHeight: 48, display: 'flex', flexDirection: 'column', justifyContent: 'center', background: (fluxoGlobalKPIs ? (fluxoGlobalKPIs.saldoFinal >= 0 ? '#d1fae5' : '#fee2e2') : (fluxoKpis && fluxoKpis.saldoFinal >= 0 ? '#d1fae5' : '#fee2e2')), border: `2px solid ${(fluxoGlobalKPIs ? (fluxoGlobalKPIs.saldoFinal >= 0 ? '#059669' : '#dc2626') : (fluxoKpis && fluxoKpis.saldoFinal >= 0 ? '#059669' : '#dc2626'))}` }}>
                          <div style={{ fontSize: 11, color: (fluxoGlobalKPIs ? (fluxoGlobalKPIs.saldoFinal >= 0 ? '#047857' : '#b91c1c') : (fluxoKpis && fluxoKpis.saldoFinal >= 0 ? '#047857' : '#b91c1c')), fontWeight: 600 }}>­ƒôê Saldo Projetado</div>
                          <div style={{ fontSize: 14, fontWeight: 'bold', marginTop: 4 }}>{(fluxoGlobalKPIs ? formatarMoeda(fluxoGlobalKPIs.saldoFinal) : (fluxoKpis ? formatarMoeda(fluxoKpis.saldoFinal) : '-'))}</div>
                        </div>
                      </div>
                    </div>
                  )}

                </FilterContent>
            </PeriodCardsContainer>
          ) : (
            // Exibir filtros tradicionais APENAS para Receber e Pagar
            <>
              <FilterTitle $collapsed={collapseFilter} onClick={() => setCollapseFilter(!collapseFilter)}>
                <FontAwesomeIcon icon={faFilter} />
                Filtros do Relat+¦rio
                <FontAwesomeIcon icon={faChevronRight} className="chevron" />
              </FilterTitle>

              <FilterContent $collapsed={collapseFilter}>
                <FilterGrid>
                <FilterGroup>
                  <Label>Tipo de Data</Label>
                  <Select
                    value={filtros.tipoDataFiltro}
                    onChange={(e) => handleFilterChange('tipoDataFiltro', e.target.value)}
                  >
                    <option value="vencimento">Data de Vencimento</option>
                    <option value="pagamento">Data de Pagamento</option>
                    <option value="emissao">Data de Emiss+úo</option>
                  </Select>
                </FilterGroup>

                <FilterGroup>
                  <Label>Data Inicial</Label>
                  <Input
                    type="date"
                    value={filtros.dataFiltroInicial}
                    onChange={(e) => handleFilterChange('dataFiltroInicial', e.target.value)}
                  />
                </FilterGroup>

                <FilterGroup>
                  <Label>Data Final</Label>
                  <Input
                    type="date"
                    value={filtros.dataFiltroFinal}
                    onChange={(e) => handleFilterChange('dataFiltroFinal', e.target.value)}
                  />
                </FilterGroup>

                <FilterGroup style={{ minWidth: '15%' }}>
                  <Label>Tipo de Cobran+ºa</Label>
                  <Select
                    value={filtros.tipoCobranca}
                    onChange={(e) => {
                      const selectedValue = e.target.value;
                      console.log('­ƒôØ Tipo de cobran+ºa selecionado:', selectedValue);
                      handleFilterChange('tipoCobranca', selectedValue);
                    }}
                  >
                    <option value="">Todos</option>
                    {opcoesCobranca.map(opcao => (
                      <option key={opcao.codigo} value={opcao.descricao}>
                        {opcao.descricao}
                      </option>
                    ))}
                  </Select>
                </FilterGroup>

                <FilterGroup style={{ minWidth: '15%' }}>
                  <Label>Tipo de Documento</Label>
                  {filtros.tipo === 'pagar' ? (
                    // Contas a Pagar: Sele+º+úo m+¦ltipla
                    <Select
                      multiple
                      size={5}
                      value={filtros.tiposDocumento || []}
                      onChange={(e) => {
                        const descricoes = Array.from(e.target.selectedOptions, option => option.value);
                        console.log('­ƒôØ Tipos de documento selecionados:', descricoes);
                        handleFilterChange('tiposDocumento', descricoes);
                      }}
                      style={{ height: '160px' }}
                    >
                      <option value="">Todos</option>
                      {opcoesTipoDocumento.length === 0 && (
                        <option disabled>Carregando...</option>
                      )}
                      {opcoesTipoDocumento.map(opcao => (
                        <option key={opcao.codigo} value={opcao.descricao}>
                          {opcao.descricao}
                        </option>
                      ))}
                    </Select>
                  ) : (
                    // Contas a Receber: Sele+º+úo simples
                    <Select
                      value={filtros.tipoDocumento || ''}
                      onChange={(e) => {
                        const selectedValue = e.target.value;
                        console.log('­ƒôØ Tipo de documento selecionado:', selectedValue);
                        handleFilterChange('tipoDocumento', selectedValue);
                      }}
                    >
                      <option value="">Todos</option>
                      {opcoesTipoDocumento.length === 0 && (
                        <option disabled>Carregando...</option>
                      )}
                      {opcoesTipoDocumento.map(opcao => (
                        <option key={opcao.codigo} value={opcao.descricao}>
                          {opcao.descricao}
                        </option>
                      ))}
                    </Select>
                  )}
                  {filtros.tipo === 'pagar' && (
                    <small style={{ fontSize: '9px', color: '#666', marginTop: '2px' }}>
                      Ctrl+clique para m+¦ltipla sele+º+úo
                    </small>
                  )}
                </FilterGroup>

                <FilterGroup style={{ minWidth: '12%' }}>
                  <Label>Faixa de Atraso (dias)</Label>
                  <Input
                    type="number"
                    placeholder="Ex: 30"
                    value={filtros.faixaAtraso}
                    onChange={(e) => handleFilterChange('faixaAtraso', e.target.value)}
                  />
                </FilterGroup>
                </FilterGrid>

                <div style={{ marginTop: '16px', display: 'flex', flexDirection: 'column', gap: '12px' }}>
                  <div style={{ display: 'flex', gap: '16px', alignItems: 'center', flexWrap: 'wrap' }}>
                    <Checkbox
                      type="checkbox"
                      id="soEmAberto"
                      checked={filtros.soEmAberto}
                      onChange={(e) => handleFilterChange('soEmAberto', e.target.checked)}
                    />
                    <CheckboxLabel htmlFor="soEmAberto">
                      Apenas em Aberto
                    </CheckboxLabel>

                    <Checkbox
                      type="checkbox"
                      id="soPagos"
                      checked={filtros.soPagos}
                      onChange={(e) => handleFilterChange('soPagos', e.target.checked)}
                    />
                    <CheckboxLabel htmlFor="soPagos">
                      Apenas Pagos
                    </CheckboxLabel>

                    {filtros.tipo === 'pagar' && (
                      <>
                        <Checkbox
                          type="checkbox"
                          id="folhaPagamento"
                          checked={filtros.folhaPagamento || false}
                          onChange={(e) => handleFilterChange('folhaPagamento', e.target.checked)}
                        />
                        <CheckboxLabel htmlFor="folhaPagamento">
                          Folha de Pagamento
                        </CheckboxLabel>
                      </>
                    )}

                    <ButtonGroup style={{ marginLeft: 'auto', gap: '12px' }}>
                      <Button type="button" onClick={() => buscarDados()} disabled={loading} $variant="primary">
                        {loading ? <FontAwesomeIcon icon={faSpinner} spin /> : <FontAwesomeIcon icon={faSearch} />}
                        {loading ? 'Gerando...' : 'Gerar Relat+¦rio'}
                      </Button>
                      {dados.length > 0 && (
                        <>
                          <Button type="button" onClick={exportarCSV} style={{ background: '#059669', borderColor: '#059669' }}>
                            <FontAwesomeIcon icon={faDownload} />
                            Exportar CSV
                          </Button>
                          <Button type="button" onClick={exportarRelatorio}>
                            <FontAwesomeIcon icon={faDownload} />
                            Exportar PDF
                          </Button>
                        </>
                      )}
                    </ButtonGroup>
                  </div>
                </div>
              </FilterContent>
            </>
          )}
        </FilterCard>
        )}

        {tipoAtivo === 'consulta_caixa' && (
          <FilterCard>
            <FilterTitle $collapsed={consultaCollapseFilter} onClick={() => setConsultaCollapseFilter(!consultaCollapseFilter)}>
              <FontAwesomeIcon icon={faFilter} />
              Filtros do Relat+¦rio
              <FontAwesomeIcon icon={consultaCollapseFilter ? faChevronRight : faChevronDown} className="chevron" />
            </FilterTitle>

            <FilterContent $collapsed={consultaCollapseFilter}>
              <FilterGrid>
                <FilterGroup>
                  <Label>Data Inicial</Label>
                  <Input type="date" value={consultaDataInicial} onChange={(e)=>setConsultaDataInicial(e.target.value)} />
                </FilterGroup>
                <FilterGroup>
                  <Label>Data Final</Label>
                  <Input type="date" value={consultaDataFinal} onChange={(e)=>setConsultaDataFinal(e.target.value)} />
                </FilterGroup>
                <FilterGroup>
                  <Label>Centro de Custo</Label>
                  <Select value={consultaCentroCusto} onChange={(e)=>setConsultaCentroCusto(e.target.value)}>
                    <option value="">Todos</option>
                    {opcoesDeptos.map((d:any)=>(<option key={d.codigo_dep} value={d.codigo_dep}>{d.descr_dep}</option>))}
                  </Select>
                </FilterGroup>
                <FilterGroup>
                  <Label>Opera+º+úo de Caixa</Label>
                  <Select value={consultaOperacao} onChange={(e)=>setConsultaOperacao(e.target.value)}>
                    <option value="">Todas</option>
                    {opcoesOperacoesCaixa.map((op:any)=>(<option key={op.codigo || op.id || op} value={op.codigo || op.id || op}>{op.descricao || op.label || op}</option>))}
                  </Select>
                </FilterGroup>
                <FilterGroup>
                  <Label>Banco</Label>
                  <Select value={bancoSelecionado} onChange={(e)=>setBancoSelecionado(e.target.value)}>
                    <option value="">Todos</option>
                    {bancoOptions.map((b:any)=>(<option key={b} value={b}>{b}</option>))}
                  </Select>
                </FilterGroup>
              </FilterGrid>
              <div style={{ marginTop: 12, display: 'flex', justifyContent: 'flex-end', gap: 8 }}>
                <Button $variant="primary" type="button" onClick={buscarConsultaCaixa} disabled={consultaLoading}>{consultaLoading ? 'Buscando...' : 'Buscar'}</Button>
              </div>
            </FilterContent>
          </FilterCard>
        )}

        {(() => {
          // Render resultados quando houver dados ou loading, incluindo a aba de consulta de caixa
          const shouldRenderResultados = filtros.tipo === 'consulta_caixa'
            ? (consultaLoading || consultaDados.length > 0)
            : (loading || dados.length > 0);
          return shouldRenderResultados;
        })() && (
          <ResultCard>
            {renderTabelaResultados()}
          </ResultCard>
        )}
      </Content>
      {/* Modal de edi+º+úo r+ípida do Caixa/Bancos (abrir CaixaBancosForm em overlay) */}
      {showCaixaPopup && (
        <div style={{ position: 'fixed', inset: 0, background: 'rgba(0,0,0,0.5)', zIndex: 1200, display: 'flex', alignItems: 'center', justifyContent: 'center' }}>
          <div style={{ width: '98%', maxWidth: 1386, maxHeight: '90vh', overflow: 'auto', background: '#fff', borderRadius: 8, padding: 12 }}>
            {/* Cabe+ºalho com Resumo dos Dados */}
            <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '16px', paddingBottom: '12px', borderBottom: '1px solid #e5e7eb' }}>
              <div>
                <h3 style={{ margin: '0 0 8px 0', fontSize: '18px', fontWeight: 'bold', color: '#1f2937' }}>
                  {caixaPopupPayload && caixaPopupPayload._mode === 'incluir' ? 'Incluir Movimento de Caixa' : caixaPopupPayload && caixaPopupPayload._mode === 'editar' ? 'Editar Movimento de Caixa' : 'Movimento de Caixa'}
                </h3>
                {caixaPopupPayload && (
                  <div style={{ fontSize: '12px', color: '#6b7280', display: 'flex', gap: '16px' }}>
                    <span>­ƒôà {caixaPopupPayload.dtmovi_cai ? formatarData(caixaPopupPayload.dtmovi_cai) : 'Data n+úo definida'}</span>
                    <span>­ƒÅª {caixaPopupPayload.tipocai_cai || 'Banco n+úo definido'}</span>
                    <span>­ƒÆ¦ {formatarMoeda(caixaPopupPayload.valor_cai || 0)}</span>
                  </div>
                )}
              </div>
              <div>
                <button
                  onClick={() => {
                    setShowCaixaPopup(false);
                    setCaixaPopupPayload(null);
                    setCaixaPopupReadOnlyPrimary(true);
                  }}
                  title="Fechar"
                  style={{
                    padding: '6px 10px',
                    borderRadius: 6,
                    border: '1px solid #d1d5db',
                    background: '#f3f4f6',
                    cursor: 'pointer',
                    fontWeight: 600
                  }}
                >
                  Ô£ò Fechar
                </button>
              </div>
            </div>
            <CaixaBancosForm
              initialPayload={caixaPopupPayload}
              readOnlyPrimary={caixaPopupReadOnlyPrimary}
              onClose={(refresh?: boolean) => {
                setShowCaixaPopup(false);
                setCaixaPopupPayload(null);
                setCaixaPopupReadOnlyPrimary(true);
                if (refresh) buscarConsultaCaixa();
              }}
            />
          </div>
        </div>
      )}

      {/* Modal de registro de lan+ºamento (reutiliza componente existente) */}
      <RegistrarLancamentoModal
        visible={showRegistrarModal}
        documentoIds={modalDocumentoIds}
        initialTipo={modalTipo}
        initialBanco={Object.keys(modalDocumentBanks).length > 0 ? Object.values(modalDocumentBanks)[0] : bancoSelecionado || '001'}
        initialOperacao={modalOperacao}
        initialValor={modalInitialValor}
        onClose={async (refresh?: boolean) => {
          setShowRegistrarModal(false);
          if (refresh && modalReloadKey) {
            try {
              const resp = await RelatoriosService.buscarDetalhesFluxoDia(modalReloadKey, filtros.soEmAberto);
              // normalizar: backend pode retornar { receber: [], pagar: [] } ou array
              let detalhesArray: any[] = [];
              if (Array.isArray(resp)) detalhesArray = resp;
              else if (resp && typeof resp === 'object') {
                const receber: any[] = resp.receber || [];
                const pagar: any[] = resp.pagar || [];
                const rNorm = receber.map(r => ({ ...r, __origem: 'receber' }));
                const pNorm = pagar.map(p => ({ ...p, __origem: 'pagar' }));
                detalhesArray = [...rNorm, ...pNorm];
              }
              setDetalhesPorData(prev => ({ ...prev, [modalReloadKey]: detalhesArray }));
            } catch (e) {
              console.warn('Erro ao recarregar detalhes apos registro:', e);
            }
            setModalReloadKey(null);
          }
        }}
      />
    </Container>
  );
};

export { RelatoriosFinanceirosModule };
