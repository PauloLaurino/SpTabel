import React, { useEffect, useState } from 'react';
import { DragDropContext, Droppable, Draggable, DropResult } from '@hello-pangea/dnd';

interface MenuItemShape { id: number; name: string; parentId?: number | null; ordem?: number; menu_group_id?: number; route?: string }
interface MenuGroupShape { id: number; name: string; items?: MenuItemShape[] }

const MenuBuilder: React.FC = () => {
  const [groups, setGroups] = useState<MenuGroupShape[]>([]);
  const [newGroupName, setNewGroupName] = useState('');
  const [loading, setLoading] = useState(false);

  useEffect(() => { load(); }, []);

  const load = async () => {
    setLoading(true);
    try {
      const resp = await fetch('/api/admin/menu');
      if (resp.ok) {
        const data = await resp.json();
        setGroups(data.map((g:any) => ({ id: g.id, name: g.name, items: (g.items||[]).map((it:any)=>({ id: it.id, name: it.name, parentId: it.parentId, ordem: it.order, menu_group_id: it.group ? it.group.id : undefined, route: it.route })) })))
      }
    } catch (e) {
      console.error(e);
    } finally { setLoading(false); }
  }

  const createGroup = async () => {
    if (!newGroupName) return alert('Nome do grupo');
    const resp = await fetch('/api/admin/menu-groups', { method: 'POST', headers: { 'Content-Type':'application/json' }, body: JSON.stringify({ name: newGroupName }) });
    if (resp.ok) { setNewGroupName(''); load(); }
    else alert('Erro ao criar grupo');
  }

  const addItem = async (groupId:number) => {
    const name = prompt('Nome do item');
    if (!name) return;
    const resp = await fetch('/api/admin/menu-items', { method: 'POST', headers: { 'Content-Type':'application/json' }, body: JSON.stringify({ name, menuGroupId: groupId }) });
    if (resp.ok) load(); else alert('Erro criar item');
  }

  const onDragEnd = async (result: DropResult) => {
    const { source, destination, type } = result;
    if (!destination) return;

    if (type === 'GROUP') {
      // Reorder groups
      const srcIdx = source.index;
      const destIdx = destination.index;
      const newGroups = Array.from(groups);
      const [moved] = newGroups.splice(srcIdx, 1);
      newGroups.splice(destIdx, 0, moved);
      setGroups(newGroups);

      // Persist group orders by PUT each group with new order
      for (let i=0;i<newGroups.length;i++) {
        const g = newGroups[i];
        await fetch(`/api/admin/menu-groups/${g.id}`, { method: 'PUT', headers: { 'Content-Type':'application/json' }, body: JSON.stringify({ order: i }) });
      }
      return;
    }

    // ITEM drag (within same group or between groups)
    const sourceGroupId = Number(source.droppableId.replace('group-',''));
    const destGroupId = Number(destination.droppableId.replace('group-',''));

    if (sourceGroupId === destGroupId) {
      // reorder within same group
      const gIdx = groups.findIndex(g=>g.id===sourceGroupId);
      if (gIdx === -1) return;
      const items = Array.from(groups[gIdx].items || []);
      const [moved] = items.splice(source.index,1);
      items.splice(destination.index,0,moved);
      const newGroups = [...groups];
      newGroups[gIdx] = { ...newGroups[gIdx], items };
      setGroups(newGroups);

      // send reorder payload for this group
      const payload = items.map((it, idx) => ({ id: it.id, parentId: it.parentId || null, menuGroupId: sourceGroupId, ordem: idx }));
      await fetch('/api/admin/menu-items/reorder', { method: 'POST', headers: { 'Content-Type':'application/json' }, body: JSON.stringify(payload) });
      return;
    }

    // Move between groups
    const srcIdxG = groups.findIndex(g=>g.id===sourceGroupId);
    const dstIdxG = groups.findIndex(g=>g.id===destGroupId);
    if (srcIdxG === -1 || dstIdxG === -1) return;

    const srcItems = Array.from(groups[srcIdxG].items || []);
    const dstItems = Array.from(groups[dstIdxG].items || []);
    const [moved] = srcItems.splice(source.index,1);
    // update parentId to null when moving at root level
    moved.parentId = null;
    dstItems.splice(destination.index,0,moved);

    const newGroups = [...groups];
    newGroups[srcIdxG] = { ...newGroups[srcIdxG], items: srcItems };
    newGroups[dstIdxG] = { ...newGroups[dstIdxG], items: dstItems };
    setGroups(newGroups);

    // Persist both groups reorder
    const payload = [
      ...srcItems.map((it, idx) => ({ id: it.id, parentId: it.parentId || null, menuGroupId: sourceGroupId, ordem: idx })),
      ...dstItems.map((it, idx) => ({ id: it.id, parentId: it.parentId || null, menuGroupId: destGroupId, ordem: idx })),
    ];
    await fetch('/api/admin/menu-items/reorder', { method: 'POST', headers: { 'Content-Type':'application/json' }, body: JSON.stringify(payload) });
  };

  return (
    <div style={{ padding: 16 }}>
      <h2>Menu Builder (Admin — protótipo)</h2>
      {loading ? <div>Carregando...</div> : null}
      <div style={{ marginBottom: 12 }}>
        <input value={newGroupName} onChange={e=>setNewGroupName(e.target.value)} placeholder="Nome do grupo" />
        <button onClick={createGroup}>Criar Grupo</button>
      </div>

      <DragDropContext onDragEnd={onDragEnd}>
        <Droppable droppableId="all-groups" type="GROUP" direction="vertical">
          {(provided) => (
            <div ref={provided.innerRef} {...provided.droppableProps}>
              {groups.map((g, gi) => (
                <Draggable key={g.id} draggableId={`group-${g.id}`} index={gi}>
                  {(prov) => (
                    <div ref={prov.innerRef} {...prov.draggableProps} style={{ border: '1px solid #ddd', padding: 8, marginBottom: 8, background: '#fff', ...prov.draggableProps.style }}>
                      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                        <div style={{ display: 'flex', gap: 8, alignItems: 'center' }} {...prov.dragHandleProps}>
                          <strong>{g.name}</strong>
                        </div>
                        <div>
                          <button onClick={()=>addItem(g.id)}>+ Item</button>
                        </div>
                      </div>
                      <Droppable droppableId={`group-${g.id}`} type="ITEM">
                        {(dropProv) => (
                          <ul ref={dropProv.innerRef} {...dropProv.droppableProps} style={{ listStyle: 'none', padding: 8 }}>
                            {(g.items||[]).map((it, idx) => (
                              <Draggable key={it.id} draggableId={`item-${it.id}`} index={idx}>
                                {(itemProv) => (
                                  <li ref={itemProv.innerRef} {...itemProv.draggableProps} {...itemProv.dragHandleProps} style={{ display: 'flex', gap: 8, alignItems: 'center', padding: 6, border: '1px solid #eee', marginBottom: 6, background: '#fafafa', ...itemProv.draggableProps.style }}>
                                    <span>{it.name}</span>
                                    <span style={{ color: '#666' }}>{it.route}</span>
                                  </li>
                                )}
                              </Draggable>
                            ))}
                            {dropProv.placeholder}
                          </ul>
                        )}
                      </Droppable>
                    </div>
                  )}
                </Draggable>
              ))}
              {provided.placeholder}
            </div>
          )}
        </Droppable>
      </DragDropContext>
    </div>
  );
}

export default MenuBuilder;
