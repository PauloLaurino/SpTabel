import React, { useEffect, useState, useMemo, useRef } from 'react';
import GerencialPanel from '../../components/Estoque/GerencialPanel';
import ServicoGerencialPanel from '../../components/Servico/ServicoGerencialPanel';
import MultiSelectDropdown from '../../components/MultiSelectDropdown';

type MasterRow = {
  nro_os: string;
  tipo: string;
  data_ini: string;
  data_fim: string;
  documento: string;
  nome: string;
  modelo: string;
  valor_ser: number;
  valor_pec: number;
  total: number;
  descpec_ser?: number;
  descser_ser?: number;
}

type DetailRow = {
  servico: string;
  data_ini: string;
  data_fim: string;
  tempo: number;
  valor: number;
}

export default function ServicoPorTipo() {
  const [dataini, setDataini] = useState('');
  const [datafim, setDatafim] = useState('');
  const [tiposSelecionados, setTiposSelecionados] = useState<string[]>([]);
  const [tipos, setTipos] = useState<Array<any>>([]);
  const [dados, setDados] = useState<MasterRow[]>([]);
  const [detalhe, setDetalhe] = useState<Record<string, DetailRow[]>>({});
  const [loading, setLoading] = useState(false);
  const [currentPage, setCurrentPage] = useState(1);
  const pageSize = 10;

  const formatDateBR = (s: any) => {
    if (!s) return '';
    try {
      const d = String(s).slice(0, 10);
      const [y, m, day] = d.split('-');
      if (!y || !m || !day) return s;
      return `${day}-${m}-${y}`;
    } catch (e) {
      return s;
    }
  };

  const formatMoney = (v: any) => {
    const n = Number(v || 0);
    return n.toLocaleString('pt-BR', { style: 'currency', currency: 'BRL' });
  };

  useEffect(() => {
    fetch('/api/relatorios/servico/por-tipo/tipos')
      .then(r => r.json())
      .then(data => {
        if (Array.isArray(data)) {
          setTipos(data);
        } else if (data && Array.isArray((data as any).rows)) {
          setTipos((data as any).rows);
        } else if (data && Array.isArray((data as any).data)) {
          setTipos((data as any).data);
        } else {
          setTipos([]);
        }
      })
      .catch(() => setTipos([]));
  }, []);

  const buscar = async () => {
    setLoading(true);
    try {
      const params = new URLSearchParams();
      if (dataini) params.set('dataini', dataini);
      if (datafim) params.set('datafim', datafim);
      if (tiposSelecionados.length > 0) {
        params.set('tipos', tiposSelecionados.join(','));
      }
      const resp = await fetch(`/api/relatorios/servico/por-tipo?${params.toString()}`);
      const json = await resp.json();
      if (Array.isArray(json)) {
        setDados(json);
        setCurrentPage(1);
      } else if (json && Array.isArray((json as any).rows)) {
        setDados((json as any).rows);
        setCurrentPage(1);
      } else if (json && Array.isArray((json as any).data)) {
        setDados((json as any).data);
        setCurrentPage(1);
      } else if (json && Array.isArray((json as any).result)) {
        setDados((json as any).result);
        setCurrentPage(1);
      } else {
        setDados([]);
      }
    } catch (e) {
      setDados([]);
    } finally {
      setLoading(false);
    }
  };

  const toggleGerencial = () => {
    setShowGerencial(prev => !prev);
  };

  const [showGerencial, setShowGerencial] = useState(false);

  const carregarDetalhe = async (nro: string) => {
    if (detalhe[nro]) {
      setDetalhe(prev => {
        const copy = { ...prev } as Record<string, DetailRow[]>;
        delete copy[nro];
        return copy;
      });
      return;
    }
    try {
      const resp = await fetch(`/api/relatorios/servico/por-tipo/${encodeURIComponent(nro)}/detalhe`);
      const rows = await resp.json();
      setDetalhe(prev => ({ ...prev, [nro]: rows }));
    } catch (e) {
      setDetalhe(prev => ({ ...prev, [nro]: [] }));
    }
  };

  const totalFiltrado = useMemo(() => {
    if (!Array.isArray(dados) || dados.length === 0) return 0;
    return dados.reduce((sum, r) => sum + ((Number(r.total) || 0) - (Number(r.descser_ser) || 0)), 0);
  }, [dados]);

  const totalValorSer = useMemo(() => {
    if (!Array.isArray(dados) || dados.length === 0) return 0;
    return dados.reduce((sum, r) => sum + ((Number(r.valor_ser) || 0) - (Number(r.descser_ser) || 0)), 0);
  }, [dados]);

  const totalValorPec = useMemo(() => {
    if (!Array.isArray(dados) || dados.length === 0) return 0;
    return dados.reduce((sum, r) => sum + ((Number(r.valor_pec) || 0) - (Number(r.descpec_ser) || 0)), 0);
  }, [dados]);

  const scrollVertical = (amt: number) => {
    const c = contentRef.current;
    if (c) {
      try {
        c.scrollBy({ top: amt, behavior: 'smooth' });
      } catch (e) {
        c.scrollTop = c.scrollTop + amt;
      }
      return;
    }
    if (typeof window !== 'undefined') {
      try {
        window.scrollBy({ top: amt, behavior: 'smooth' });
      } catch (e) {
        window.scrollTo(0, window.pageYOffset + amt);
      }
    }
  };

  const contentRef = useRef<HTMLDivElement | null>(null);

  return (
    <div ref={contentRef} style={{ padding: 16, maxHeight: '100vh', overflowY: 'auto' }}>
      <h2>Serviço por tipo</h2>
      <div style={{ display: 'flex', gap: 8, marginBottom: 12, alignItems: 'center' }}>
        <div className="form-group" style={{ marginBottom: 0 }}>
          <label className="form-label">Data Inicial</label>
          <input className="form-control" type="date" value={dataini} onChange={e => setDataini(e.target.value)} />
        </div>
        <div className="form-group" style={{ marginBottom: 0 }}>
          <label className="form-label">Data Final</label>
          <input className="form-control" type="date" value={datafim} onChange={e => setDatafim(e.target.value)} />
        </div>
        <div className="form-group" style={{ marginBottom: 0, minWidth: 200 }}>
          <label className="form-label">Tipo de Serviço</label>
          <MultiSelectDropdown
            placeholder="Selecione os tipos..."
            options={Array.isArray(tipos) ? tipos.map((t: any) => ({ value: t.id_tmo, label: t.descr_tmo })) : []}
            selectedValues={tiposSelecionados}
            onChange={(vals) => setTiposSelecionados(vals)}
          />
        </div>
        <div style={{ display: 'flex', gap: 8 }}>
          <button className="btn btn-primary" style={{ height: 36 }} onClick={buscar} disabled={loading}>{loading ? 'Carregando...' : 'Buscar'}</button>
          <button className="btn btn-secondary" style={{ height: 36 }} onClick={toggleGerencial}>{showGerencial ? 'Fechar Gerencial' : 'Gerencial'}</button>
        </div>
      </div>

      <div className="row">
        {showGerencial && (
          <aside className="col-md-7">
            <ServicoGerencialPanel filters={{ dataini, datafim, tipos: tiposSelecionados }} rows={dados} />
          </aside>
        )}
        <main className={showGerencial ? 'col-md-5' : 'col-12'}>
          <table className="table" style={{ width: '100%', borderCollapse: 'collapse' }}>
        <thead>
          <tr>
            <th>Nro OS</th>
            <th>Tipo</th>
            <th>Data Ini</th>
            <th>Data Fim</th>
            <th>Documento</th>
            <th>Nome</th>
            <th>Modelo</th>
            <th style={{ textAlign: 'right' }}>Valor S</th>
            <th style={{ textAlign: 'right' }}>Valor P</th>
            <th style={{ textAlign: 'right' }}>Total</th>
            <th></th>
          </tr>
        </thead>
        <tbody>
          {Array.isArray(dados) && dados.slice((currentPage-1)*pageSize, currentPage*pageSize).map((r: any) => (
            <React.Fragment key={r.nro_os}>
              <tr>
                <td>{r.nro_os}</td>
                <td>{r.tipo}</td>
                <td>{formatDateBR(r.data_ini)}</td>
                <td>{formatDateBR(r.data_fim)}</td>
                <td>{r.documento}</td>
                <td>{r.nome}</td>
                <td>{r.modelo}</td>
                <td style={{ textAlign: 'right' }}>{formatMoney((Number(r.valor_ser) || 0) - (Number(r.descser_ser) || 0))}</td>
                <td style={{ textAlign: 'right' }}>{formatMoney((Number(r.valor_pec) || 0) - (Number(r.descpec_ser) || 0))}</td>
                <td style={{ textAlign: 'right' }}>{formatMoney((Number(r.total) || 0) - (Number(r.descser_ser) || 0))}</td>
                <td>
                  <button onClick={() => carregarDetalhe(r.nro_os)}>Detalhe</button>
                </td>
              </tr>
              {detalhe[r.nro_os] && detalhe[r.nro_os].length > 0 && (
                <tr>
                  <td colSpan={11} style={{ background: '#f7f7f7' }}>
                    <table style={{ width: '100%' }}>
                      <thead>
                        <tr>
                          <th>Serviço</th>
                          <th>Data Ini</th>
                          <th>Data Fim</th>
                          <th style={{ textAlign: 'right' }}>Tempo</th>
                          <th style={{ textAlign: 'right' }}>Valor</th>
                        </tr>
                      </thead>
                      <tbody>
                        {detalhe[r.nro_os].map((d: any, idx: number) => (
                          <tr key={idx}>
                            <td>{d.servico}</td>
                            <td>{formatDateBR(d.data_ini)}</td>
                            <td>{formatDateBR(d.data_fim)}</td>
                            <td style={{ textAlign: 'right' }}>{Number(d.tempo || 0).toFixed(2)}</td>
                            <td style={{ textAlign: 'right' }}>{formatMoney(d.valor)}</td>
                          </tr>
                        ))}
                      </tbody>
                    </table>
                  </td>
                </tr>
              )}
            </React.Fragment>
          ))}
        </tbody>
        <tfoot>
          <tr>
            <td colSpan={7}></td>
            <td style={{ textAlign: 'right', fontWeight: 600 }}>{formatMoney(totalValorSer)}</td>
            <td style={{ textAlign: 'right', fontWeight: 600 }}>{formatMoney(totalValorPec)}</td>
            <td style={{ textAlign: 'right', fontWeight: 700 }}>{formatMoney(totalFiltrado)}</td>
            <td></td>
          </tr>
        </tfoot>
          </table>
        </main>
      </div>
      {!loading && Array.isArray(dados) && dados.length === 0 && (
        <div style={{ marginTop: 12, padding: 12, background: '#fff3f3', color: '#a00', border: '1px solid #f5c2c2' }}>
          Nenhum registro encontrado para os filtros selecionados.
        </div>
      )}
      {Array.isArray(dados) && dados.length > pageSize && (
        <div style={{ marginTop: 12 }}>
          <div style={{
            padding: '8px 12px',
            background: '#f1f1f1',
            borderTop: '1px solid #ddd',
            display: 'flex',
            justifyContent: 'space-between',
            alignItems: 'center'
          }}>
            <div>Mostrando {dados.length} registros — página {currentPage} de {Math.ceil(dados.length / pageSize)}</div>
            <div style={{ fontSize: 12, color: '#666' }}>Use os filtros acima para refinar a busca.</div>
          </div>
          <div style={{ display: 'flex', justifyContent: 'center', gap: 8, marginTop: 8 }}>
            <button onClick={() => setCurrentPage(p => Math.max(1, p - 1))} disabled={currentPage === 1}>Anterior</button>
            <button onClick={() => setCurrentPage(p => Math.min(Math.ceil(dados.length / pageSize), p + 1))} disabled={currentPage === Math.ceil(dados.length / pageSize)}>Próxima</button>
          </div>
        </div>
      )}
      {showGerencial && (
        <div style={{ position: 'fixed', right: 12, top: '50%', transform: 'translateY(-50%)', zIndex: 9999 }}>
          <div style={{ width: 44, background: 'rgba(255,255,255,0.95)', borderRadius: 8, boxShadow: '0 2px 8px rgba(0,0,0,0.12)', display: 'flex', flexDirection: 'column', alignItems: 'center', padding: 6 }}>
            <button onClick={() => scrollVertical(-500)} style={{ width: 32, height: 32, borderRadius: 6, border: 'none', background: '#fff', cursor: 'pointer' }} aria-label="rolar para cima">↑</button>
            <div style={{ height: 6 }} />
            <button onClick={() => scrollVertical(500)} style={{ width: 32, height: 32, borderRadius: 6, border: 'none', background: '#fff', cursor: 'pointer' }} aria-label="rolar para baixo">↓</button>
          </div>
        </div>
      )}
    </div>
  );
}
