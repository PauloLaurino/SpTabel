import React, { useEffect, useState } from 'react';

/**
 * Formulário de administração de visibilidade de menu por usuário.
 * Permite ao admin selecionar um usuário e marcar quais itens de menu são visíveis.
 */

interface MenuItemDTO {
  id: number;
  name: string;
  route?: string;
  filhos?: MenuItemDTO[];
}

interface MenuGroupDTO {
  id: number;
  name: string;
  icon?: string;
  items: MenuItemDTO[];
}

interface UserMenuConfigState {
  menu_item_id: number;
  visivel: boolean;
  ordem: number;
}

export const MenuAdminForm: React.FC = () => {
  const [users, setUsers] = useState<any[]>([]);
  const [selectedUser, setSelectedUser] = useState<number | null>(null);
  const [menuGroups, setMenuGroups] = useState<MenuGroupDTO[]>([]);
  const [config, setConfig] = useState<Record<number, UserMenuConfigState>>({});
  const [isLoading, setIsLoading] = useState(false);
  const [toast, setToast] = useState<{ type: 'success' | 'error'; text: string } | null>(null);
  const [duplicateTarget, setDuplicateTarget] = useState<number | null>(null);

  // Carregar lista de usuários
  useEffect(() => {
    const load = async () => {
      try {
        const resp = await fetch('/api/users', { credentials: 'include' });
        if (resp.ok) {
          const data = await resp.json();
          setUsers(Array.isArray(data) ? data : []);
          return;
        }
      } catch (e) {
        console.debug('[MenuAdminForm] /api/users falhou, tentando masusu');
      }
      try {
        const resp2 = await fetch('/api/tabelas-auxiliares/masusu');
        if (resp2.ok) {
          const list = await resp2.json();
          setUsers(list.map((x: any) => ({ id: x.codigo, username: x.descricao, name: x.descricao })));
        }
      } catch (e) {
        console.error('[MenuAdminForm] Erro ao carregar usuários:', e);
      }
    };
    load();
  }, []);

  // Carregar estrutura de menu (todos os grupos e itens)
  useEffect(() => {
    fetch('/api/menu-groups', { credentials: 'include' })
      .then(r => r.json())
      .then(data => setMenuGroups(Array.isArray(data) ? data : []))
      .catch(e => console.error('[MenuAdminForm] Erro ao carregar menu-groups:', e));
  }, []);

  // Carregar configuração do usuário selecionado
  useEffect(() => {
    if (!selectedUser) return;
    setConfig({});
    fetch(`/api/admin/user-menu-config?usuarioId=${selectedUser}`, { credentials: 'include' })
      .then(r => r.json())
      .then((list: any[]) => {
        const map: Record<number, UserMenuConfigState> = {};
        list.forEach(l => {
          const id = Number(l.menuItemId ?? l.menu_item_id);
          map[id] = {
            menu_item_id: id,
            visivel: Boolean(l.visible ?? l.visivel ?? false),
            ordem: Number(l.order ?? l.ordem ?? 0),
          };
        });
        setConfig(map);
      })
      .catch(e => console.error('[MenuAdminForm] Erro ao carregar config:', e));
  }, [selectedUser]);

  const toggleVisivel = (itemId: number) => {
    setConfig(prev => ({
      ...prev,
      [itemId]: {
        menu_item_id: itemId,
        visivel: !(prev[itemId]?.visivel ?? false),
        ordem: prev[itemId]?.ordem ?? 0,
      },
    }));
  };

  // Achata a árvore de itens em lista plana (para incluir filhos recursivamente)
  const flattenItems = (items: MenuItemDTO[]): MenuItemDTO[] => {
    const result: MenuItemDTO[] = [];
    const recurse = (list: MenuItemDTO[]) => {
      list.forEach(item => {
        result.push(item);
        if (item.filhos && item.filhos.length > 0) recurse(item.filhos);
      });
    };
    recurse(items);
    return result;
  };

  const save = async () => {
    if (!selectedUser) return alert('Selecione um usuário');
    setIsLoading(true);

    // Monta o payload com TODOS os itens do menu (visíveis e ocultos),
    // garantindo que o backend tenha a imagem completa das configurações.
    const allItems: MenuItemDTO[] = menuGroups.flatMap(g => flattenItems(g.items));
    const payload = allItems.map(item => ({
      usuario_id: selectedUser,
      menu_item_id: item.id,
      visivel: config[item.id]?.visivel ?? false,
      ordem: config[item.id]?.ordem ?? 0,
    }));

    try {
      const resp = await fetch('/api/admin/user-menu-config', {
        method: 'POST',
        credentials: 'include',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(payload),
      });

      if (resp.ok) {
        setToast({ type: 'success', text: `Configuração salva: ${payload.length} itens` });
        window.dispatchEvent(new CustomEvent('menu-config-updated', { detail: { userId: selectedUser } }));
      } else {
        const text = await resp.text();
        setToast({ type: 'error', text: 'Erro ao salvar: ' + text });
      }
    } catch (e) {
      setToast({ type: 'error', text: 'Erro de conexão: ' + String(e) });
    } finally {
      setIsLoading(false);
    }
  };

  const handleDuplicateToUser = async () => {
    if (!selectedUser) return alert('Selecione o usuário de origem');
    if (!duplicateTarget) return alert('Selecione o usuário destino');
    if (!window.confirm('Confirma a duplicação das configurações de menu?')) return;

    const allItems: MenuItemDTO[] = menuGroups.flatMap(g => flattenItems(g.items));
    const payload = allItems.map(item => ({
      usuario_id: duplicateTarget,
      menu_item_id: item.id,
      visivel: config[item.id]?.visivel ?? false,
      ordem: config[item.id]?.ordem ?? 0,
    }));

    setIsLoading(true);
    try {
      const resp = await fetch('/api/admin/user-menu-config', {
        method: 'POST',
        credentials: 'include',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(payload),
      });
      if (resp.ok) {
        setToast({ type: 'success', text: 'Duplicação concluída com sucesso!' });
      } else {
        setToast({ type: 'error', text: 'Erro ao duplicar menu' });
      }
    } catch (e) {
      setToast({ type: 'error', text: 'Erro de conexão: ' + String(e) });
    } finally {
      setIsLoading(false);
    }
  };

  const renderItems = (items: MenuItemDTO[], level = 0): React.ReactNode => (
    items.map(item => (
      <React.Fragment key={item.id}>
        <tr style={{ background: level === 0 ? '#f8fafc' : 'white' }}>
          <td style={{ paddingLeft: 8 + level * 20, fontSize: 13, color: level === 0 ? '#374151' : '#6b7280' }}>
            {level > 0 ? '↳ ' : ''}{item.name}
            {item.route && <span style={{ marginLeft: 6, fontSize: 11, color: '#9ca3af' }}>{item.route}</span>}
          </td>
          <td style={{ textAlign: 'center' }}>
            <input
              type="checkbox"
              checked={config[item.id]?.visivel ?? false}
              onChange={() => toggleVisivel(item.id)}
            />
          </td>
        </tr>
        {item.filhos && item.filhos.length > 0 && renderItems(item.filhos, level + 1)}
      </React.Fragment>
    ))
  );

  return (
    <div style={{ maxHeight: 'calc(100vh - 120px)', overflowY: 'auto', padding: '0 8px 24px' }}>
      <h2 style={{ fontSize: 18, fontWeight: 700, marginBottom: 16 }}>
        Administração de Menu por Usuário
      </h2>

      {/* Toast */}
      {toast && (
        <div style={{
          position: 'fixed', top: 16, right: 16, padding: '12px 16px', zIndex: 9999,
          background: toast.type === 'success' ? '#d1fae5' : '#fee2e2',
          border: `1px solid ${toast.type === 'success' ? '#6ee7b7' : '#fca5a5'}`,
          borderRadius: 8, display: 'flex', gap: 12, alignItems: 'center',
        }}>
          {toast.text}
          <button onClick={() => setToast(null)} style={{ background: 'none', border: 'none', cursor: 'pointer', fontSize: 16 }}>×</button>
        </div>
      )}

      {isLoading && (
        <div style={{ position: 'fixed', top: 8, left: 8, padding: 8, background: '#fff', border: '1px solid #ccc', borderRadius: 4 }}>
          Processando...
        </div>
      )}

      {/* Seletor de usuário */}
      <div style={{ display: 'flex', gap: 16, alignItems: 'flex-end', marginBottom: 24, flexWrap: 'wrap' }}>
        <div>
          <label style={{ display: 'block', fontSize: 13, fontWeight: 600, marginBottom: 4 }}>Usuário</label>
          <select
            style={{ padding: '8px 12px', border: '1px solid #d1d5db', borderRadius: 6, fontSize: 14 }}
            onChange={e => setSelectedUser(e.target.value ? Number(e.target.value) : null)}
          >
            <option value="">-- selecione --</option>
            {users.map(u => (
              <option key={u.id} value={u.id}>{u.username || u.name || u.id}</option>
            ))}
          </select>
        </div>

        {selectedUser && (
          <button
            onClick={save}
            disabled={isLoading}
            style={{ padding: '8px 20px', background: '#0d9488', color: '#fff', border: 'none', borderRadius: 6, fontWeight: 600, cursor: 'pointer' }}
          >
            Salvar Configurações
          </button>
        )}

        {selectedUser && (
          <div style={{ display: 'flex', gap: 8, alignItems: 'flex-end' }}>
            <div>
              <label style={{ display: 'block', fontSize: 13, fontWeight: 600, marginBottom: 4 }}>Duplicar para</label>
              <select
                style={{ padding: '8px 12px', border: '1px solid #d1d5db', borderRadius: 6, fontSize: 14 }}
                value={duplicateTarget ?? ''}
                onChange={e => setDuplicateTarget(e.target.value ? Number(e.target.value) : null)}
              >
                <option value="">-- usuário destino --</option>
                {users.filter(u => u.id !== selectedUser).map(u => (
                  <option key={u.id} value={u.id}>{u.username || u.name || u.id}</option>
                ))}
              </select>
            </div>
            {duplicateTarget && (
              <button
                onClick={handleDuplicateToUser}
                disabled={isLoading}
                style={{ padding: '8px 16px', background: '#6366f1', color: '#fff', border: 'none', borderRadius: 6, fontWeight: 600, cursor: 'pointer' }}
              >
                Duplicar
              </button>
            )}
          </div>
        )}
      </div>

      {/* Tabela de menu items */}
      {selectedUser && menuGroups.length > 0 && menuGroups.map(group => (
        <div key={group.id} style={{ marginBottom: 16, border: '1px solid #e5e7eb', borderRadius: 8, overflow: 'hidden' }}>
          <div style={{ background: '#f1f5f9', padding: '10px 16px', fontWeight: 700, fontSize: 14, color: '#1e293b' }}>
            {group.name}
          </div>
          <table style={{ width: '100%', borderCollapse: 'collapse' }}>
            <thead>
              <tr style={{ background: '#f8fafc', borderBottom: '1px solid #e5e7eb' }}>
                <th style={{ textAlign: 'left', padding: '8px 16px', fontSize: 12, color: '#6b7280', fontWeight: 600 }}>Item de Menu</th>
                <th style={{ textAlign: 'center', padding: '8px 16px', fontSize: 12, color: '#6b7280', fontWeight: 600, width: 80 }}>Visível</th>
              </tr>
            </thead>
            <tbody>
              {renderItems(group.items)}
            </tbody>
          </table>
        </div>
      ))}

      {selectedUser && menuGroups.length === 0 && (
        <p style={{ color: '#9ca3af', fontSize: 14 }}>Carregando itens de menu...</p>
      )}

      {!selectedUser && (
        <p style={{ color: '#9ca3af', fontSize: 14 }}>Selecione um usuário para configurar a visibilidade do menu.</p>
      )}
    </div>
  );
};

export default MenuAdminForm;
