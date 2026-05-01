import React, { useEffect, useState } from 'react';
import { useForm } from 'react-hook-form';
import { Masfor } from './types';
import './Masfor.css';

// FormBuilder parser hint: array of fields in the expected pattern
// The FormBuilder editor looks for arrays like: { field: 'NOME', label: 'Label' }
export const MASFOR_FORM_FIELDS = [
  { field: 'tipo_for', label: 'Código' },
  { field: 'descr_for', label: 'Descrição' },
  { field: 'filler', label: 'Filler' }
];

// Full form definition that FormBuilder CODE import may detect and use
export const MASFOR_FORM_DEFINITION = {
  formName: 'masfor_form',
  formTitle: 'Tipos de Fornecedores',
  tableName: 'masfor',
  fields: [
    { id: 'f_tipo_for', name: 'tipo_for', label: 'Código', type: 'text', required: true, placeholder: 'Digite código' },
    { id: 'f_descr_for', name: 'descr_for', label: 'Descrição', type: 'text', required: true, placeholder: 'Digite descrição' },
    { id: 'f_filler', name: 'filler', label: 'Filler', type: 'text', required: false, placeholder: '' }
  ],
  layout: { columns: 1, gap: '12px', responsive: true },
  buttons: { submit: { label: 'Salvar', visible: true }, cancel: { label: 'Cancelar', visible: true }, reset: { label: 'Limpar', visible: false } }
};

const API_BASE = process.env.REACT_APP_API_URL || '/spdealer/api';

interface Props { id?: number; onSaved?: () => void }

const MasforForm: React.FC<Props> = ({ id, onSaved }) => {
  const { register, handleSubmit, setValue, formState: { errors } } = useForm<Masfor>();
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    if (id) {
      fetch(`${API_BASE}/refatorado/masfor/${id}`)
        .then(r => r.json())
        .then((data: Masfor) => {
            setValue('tipo_for', (data as any).tipo_for ?? '');
            setValue('descr_for', (data as any).descr_for ?? '');
            setValue('filler', (data as any).filler ?? '');
          });
    }
  }, [id, setValue]);

  const onSubmit = (data: Masfor) => {
    setLoading(true);
    const method = id ? 'PUT' : 'POST';
    const url = id ? `${API_BASE}/refatorado/masfor/${id}` : `${API_BASE}/refatorado/masfor`;
    const payload: any = { ...data };
    fetch(url, {
      method,
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(payload)
    }).then(r => r.json()).then(() => { setLoading(false); if (onSaved) onSaved(); });
  };

  return (
    <form onSubmit={handleSubmit(onSubmit)} className="masfor-form">
      <div>
        <label>Código</label>
        <input {...register('tipo_for', { required: true, maxLength: 30 })} />
        {errors.tipo_for && <span className="error">Obrigatório</span>}
      </div>
      <div>
        <label>Descrição</label>
        <input {...register('descr_for', { required: true, maxLength: 200 })} />
        {errors.descr_for && <span className="error">Obrigatório</span>}
      </div>
      <div>
        <label>Filler</label>
        <input {...register('filler')} />
      </div>
      <button type="submit" disabled={loading}>{loading ? 'Salvando...' : 'Salvar'}</button>
    </form>
  );
};

export default MasforForm;
