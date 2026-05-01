import React, { useState } from 'react';
import Ajv from 'ajv';
import flowSchema from './flow-schema.json';
import exampleFlow from './example-flow-masfor.json';
import FlowRunner from './FlowRunner';

export default function FlowRunnerPage() {
  const [flowJson, setFlowJson] = useState<any>(exampleFlow);
  const [text, setText] = useState<string>(JSON.stringify(exampleFlow, null, 2));
  const [errors, setErrors] = useState<any[] | null>(null);

  const ajv = new Ajv({ allErrors: true, strict: false });
  const validate = ajv.compile(flowSchema as any);

  function runValidation(payloadText?: string) {
    try {
      const parsed = payloadText ? JSON.parse(payloadText) : flowJson;
      const ok = validate(parsed as any);
      if (!ok) {
        setErrors(validate.errors || []);
        return false;
      }
      setErrors(null);
      setFlowJson(parsed);
      setText(JSON.stringify(parsed, null, 2));
      return true;
    } catch (e: any) {
      setErrors([{ message: 'JSON inválido: ' + e.message }]);
      return false;
    }
  }

  return (
    <div style={{padding:12}}>
      <h2>Flow Runner (Homologação)</h2>
      <p>Valide o JSON do flow contra <code>flow-schema.json</code> e execute com o FlowRunner.</p>
      <div style={{display:'flex', gap:12}}>
        <div style={{flex:1}}>
          <textarea
            value={text}
            onChange={(e) => setText(e.target.value)}
            style={{width:'100%', height:320, fontFamily:'monospace'}}
          />
          <div style={{marginTop:8, display:'flex', gap:8}}>
            <button onClick={() => { runValidation(text); }}>Validar e Carregar</button>
            <button onClick={() => { setText(JSON.stringify(exampleFlow, null, 2)); setFlowJson(exampleFlow); setErrors(null); }}>Usar Exemplo</button>
          </div>
          {errors && (
            <div style={{marginTop:8, background:'#fee', padding:8, border:'1px solid #fbb'}}>
              <strong>Erros de Validação:</strong>
              <ul>
                {errors.map((err: any, i: number) => (
                  <li key={i}>{err.instancePath || ''} {err.message}</li>
                ))}
              </ul>
            </div>
          )}
        </div>
        <div style={{width:480}}>
          <FlowRunner flow={flowJson} />
        </div>
      </div>
    </div>
  );
}
