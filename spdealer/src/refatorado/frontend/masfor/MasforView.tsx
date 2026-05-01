import React, { useEffect, useState } from 'react';
import { Masfor } from './types';
import MasforForm from './MasforForm';

const API_BASE = process.env.REACT_APP_API_URL || '/spdealer/api';

export const MasforView: React.FC = () => {
  const [items, setItems] = useState<Masfor[]>([]);
  const [editingId, setEditingId] = useState<number | undefined>(undefined);

  const load = () => fetch(`${API_BASE}/refatorado/masfor`).then(r => r.json()).then(setItems);
  useEffect(() => { load(); }, []);

  return (
    <div>
      <h3>Tipos de Fornecedores</h3>
      <button onClick={() => setEditingId(undefined)}>Novo</button>
      <table>
        <thead><tr><th>Código</th><th>Descrição</th><th>Filler</th><th>Ações</th></tr></thead>
        <tbody>
          {items.map(i => (
            <tr key={i.id}>
              <td>{(i as any).tipo_for}</td>
              <td>{(i as any).descr_for}</td>
              <td>{(i as any).filler}</td>
              <td>
                <button onClick={() => setEditingId(i.id)}>Editar</button>
                <button onClick={() => fetch(`${API_BASE}/refatorado/masfor/${i.id}`, { method: 'DELETE' }).then(load)}>Excluir</button>
              </td>
            </tr>
          ))}
        </tbody>
      </table>
      <div style={{ marginTop: 20 }}>
        <MasforForm id={editingId} onSaved={() => { setEditingId(undefined); load(); }} />
      </div>
    </div>
  );
};

export default MasforView;
