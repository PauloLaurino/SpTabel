import React, { useState } from 'react';
import FlowEditor from './FlowEditor';

export default function FlowEditorPage() {
  const [currentFlow, setCurrentFlow] = useState<any | null>(null);

  return (
    <div style={{ padding: 16 }}>
      <h2>Flow Editor</h2>
      <p>Editor visual / JSON para criar e editar flows. Salve para persistir no servidor.</p>

      <div style={{ marginTop: 12 }}>
        <FlowEditor flow={currentFlow} onClose={() => { /* nada */ }} onSaved={(saved) => { setCurrentFlow(saved); alert('Flow salvo com sucesso'); }} />
      </div>
    </div>
  );
}
