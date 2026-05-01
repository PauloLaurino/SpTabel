import React, { useState, useEffect } from 'react';
import Ajv from 'ajv';
import flowSchema from './flow-schema.json';

interface FlowEditorProps {
  flow?: any;
  onClose?: () => void;
  onSaved?: (saved: any) => void;
}

export default function FlowEditor({ flow: initialFlow, onClose, onSaved }: FlowEditorProps) {
  const [text, setText] = useState<string>(JSON.stringify(initialFlow || { id: '', name: '', params: [], steps: [], connections: [] }, null, 2));
  const [errors, setErrors] = useState<any[] | null>(null);
  const [saving, setSaving] = useState(false);

  useEffect(() => {
    setText(JSON.stringify(initialFlow || { id: '', name: '', params: [], steps: [], connections: [] }, null, 2));
  }, [initialFlow]);

  const validate = () => {
    try {
      const parsed = JSON.parse(text);
      const ajv = new Ajv();
      const validateFn = ajv.compile(flowSchema as any);
      const ok = validateFn(parsed);
      if (!ok) {
        setErrors(validateFn.errors || []);
        return false;
      }
      setErrors(null);
      return true;
    } catch (e) {
      setErrors([{ message: String(e) }]);
      return false;
    }
  };

  const handleSave = async () => {
    if (!validate()) return;
    setSaving(true);
    try {
      const payload = JSON.parse(text);
      // tentativa de POST, se falhar (409/404) tenta PUT quando houver id
      const url = '/api/flows';
      const method = payload.id ? 'PUT' : 'POST';
      const target = payload.id ? `/api/flows/${payload.id}` : url;
      const resp = await fetch(target, {
        method,
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(payload)
      });
      if (!resp.ok) {
        const body = await resp.text();
        throw new Error(`Erro ao salvar flow: ${resp.status} ${body}`);
      }
      const saved = await resp.json().catch(() => null);
      if (onSaved) onSaved(saved || payload);
      if (onClose) onClose();
    } catch (err) {
      console.error(err);
      alert(String(err));
    } finally {
      setSaving(false);
    }
  };

  return (
    <div style={{ padding: 12 }}>
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
        <h4>{initialFlow?.id ? 'Editar Flow' : 'Novo Flow'}</h4>
        <div>
          <button className="btn btn-sm btn-secondary me-2" onClick={onClose}>Fechar</button>
          <button className="btn btn-sm btn-primary" onClick={handleSave} disabled={saving}>Salvar</button>
        </div>
      </div>

      <div style={{ marginTop: 8 }}>
        <textarea value={text} onChange={(e) => setText(e.target.value)} style={{ width: '100%', minHeight: 420, fontFamily: 'monospace', fontSize: 13 }} />
      </div>

      <div style={{ marginTop: 8 }}>
        <button className="btn btn-sm btn-outline-secondary" onClick={() => { setText(JSON.stringify({ id: '', name: '', params: [], steps: [], connections: [] }, null, 2)); setErrors(null); }}>Limpar</button>
        <button className="btn btn-sm btn-outline-secondary ms-2" onClick={() => { try { const parsed = JSON.parse(text); setText(JSON.stringify(parsed, null, 2)); setErrors(null); } catch(e){ setErrors([{message: String(e)}]); } }}>Formatar JSON</button>
        <button className="btn btn-sm btn-outline-primary ms-2" onClick={() => validate()}>Validar</button>
      </div>

      <div style={{ marginTop: 12 }}>
        {errors && errors.length > 0 && (
          <div style={{ background: '#fff4f4', padding: 8, borderRadius: 6 }}>
            <strong>Erros de validação:</strong>
            <ul>
              {errors.map((err, idx) => (
                <li key={idx}>{String(err.message || err)}</li>
              ))}
            </ul>
          </div>
        )}
      </div>
    </div>
  );
}
