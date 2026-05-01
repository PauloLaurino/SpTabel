import React, { useState, useRef } from 'react';

type Flow = any;

type FlowRunnerProps = {
  flow?: Flow;
  flowPath?: string; // opcional: buscar JSON via fetch
};

export default function FlowRunner({ flow: initialFlow, flowPath }: FlowRunnerProps) {
  const [flow, setFlow] = useState<any | null>(initialFlow || null);
  const [running, setRunning] = useState(false);
  const [logs, setLogs] = useState<string[]>([]);
  const ctxRef = useRef<any>({});

  async function loadFlow() {
    if (!flowPath) return;
    try {
      const res = await fetch(flowPath);
      const j = await res.json();
      setFlow(j);
      log(`Flow carregado: ${j.id || j.name}`);
    } catch (err) {
      log(`Erro ao carregar flow: ${String(err)}`);
    }
  }

  function log(msg: string) {
    setLogs((s) => [...s, `[${new Date().toLocaleTimeString()}] ${msg}`]);
  }

  function evaluateExpression(expr: string, ctx: any) {
    // Expressões devem ser do tipo: (ctx) => { ... } ou 'ctx.x === 1'
    try {
      if (expr.trim().startsWith('(') || expr.includes('=>')) {
        // Function style
        // eslint-disable-next-line no-new-func
        const fn = new Function('return ' + expr)();
        return fn(ctx);
      }
      // inline expression
      // eslint-disable-next-line no-new-func
      const fn = new Function('ctx', 'return (' + expr + ')');
      return fn(ctx);
    } catch (e) {
      log(`Erro avaliando expressão: ${String(e)}`);
      return false;
    }
  }

  async function execHttp(step: any, ctx: any) {
    const req = step.httpRequest;
    let url = req.url;
    // substituir templates simples {{var}} a partir do contexto
    url = url.replace(/{{([^}]+)}}/g, (_: string, g: string) => String(getByPath(ctx, g.trim())));
    let body: any = null;
    if (req.body) {
      if (typeof req.body === 'string') {
        body = req.body.replace(/{{([^}]+)}}/g, (_: string, g: string) => JSON.stringify(getByPath(ctx, g.trim())));
        try { body = JSON.parse(body); } catch (_) {}
      } else {
        // clone and replace
        body = JSON.parse(JSON.stringify(req.body), (k, v) => {
          if (typeof v === 'string') {
            return v.replace(/{{([^}]+)}}/g, (_: string, g: string) => String(getByPath(ctx, g.trim())));
          }
          return v;
        });
      }
    }
    const options: any = { method: req.method, headers: req.headers || {} };
    if (body !== null && req.method !== 'GET') options.body = JSON.stringify(body);
    log(`HTTP ${req.method} ${url}`);
    const res = await fetch(url, options);
    const text = await res.text();
    let parsed: any = text;
    try { parsed = JSON.parse(text); } catch (_) {}
    if (req.saveAs) ctx[req.saveAs] = parsed;
    return parsed;
  }

  function getByPath(obj: any, path: string) {
    const parts = path.split('.');
    let cur = obj;
    for (const p of parts) {
      if (cur == null) return undefined;
      cur = cur[p];
    }
    return cur;
  }

  async function run() {
    if (!flow) {
      await loadFlow();
      if (!flow) return;
    }
    setRunning(true);
    setLogs([]);
    const ctx = Object.assign({}, flow.variables || {});
    ctxRef.current = ctx;
    log(`Iniciando flow ${flow.id || flow.name}`);
    const steps = flow.steps || [];
    let currentId = steps[0]?.id;
    const stepById: any = {};
    for (const s of steps) stepById[s.id] = s;

    while (currentId) {
      const step = stepById[currentId];
      if (!step) break;
      log(`Executando step ${step.id} (${step.type})`);
      try {
        if (step.type === 'set') {
          const defs = step.set || {};
          for (const k of Object.keys(defs)) {
            const v = defs[k];
            if (typeof v === 'string' && v.includes('ctx')) {
              ctx[k] = evaluateExpression(v, ctx);
            } else {
              ctx[k] = v;
            }
            log(`set ${k} = ${JSON.stringify(ctx[k])}`);
          }
          currentId = step.next || null;
        } else if (step.type === 'httpRequest') {
          await execHttp(step, ctx);
          currentId = step.next || null;
        } else if (step.type === 'validate') {
          const ok = evaluateExpression(step.validate.expression, ctx);
          if (!ok) {
            log(`Validation failed on step ${step.id}`);
            currentId = step.validate.onFail || null;
          } else {
            currentId = step.next || null;
          }
        } else if (step.type === 'condition') {
          const res = evaluateExpression(step.condition.expression, ctx);
          currentId = res ? step.condition.trueNext : step.condition.falseNext;
        } else if (step.type === 'end') {
          log(`Reached end: ${step.name || step.id}`);
          break;
        } else {
          log(`Tipo de step não suportado: ${step.type}`);
          break;
        }
      } catch (e: any) {
        log(`Erro executando step ${step.id}: ${String(e)}`);
        break;
      }
    }
    setRunning(false);
    log('Flow finalizado');
  }

  return (
    <div style={{border:'1px solid #ddd', padding:12, borderRadius:6}}>
      <div style={{display:'flex', gap:8, marginBottom:8}}>
        <button onClick={() => run()} disabled={running}>Run</button>
        <button onClick={() => { setLogs([]); }} disabled={running}>Clear</button>
        <button onClick={() => loadFlow()} disabled={!flowPath || running}>Load</button>
      </div>
      <div style={{maxHeight:240, overflow:'auto', background:'#111', color:'#eee', padding:8}}>
        {logs.map((l, i) => <div key={i} style={{fontFamily:'monospace', fontSize:12}}>{l}</div>)}
      </div>
      <div style={{marginTop:8}}>
        <strong>Contexto</strong>
        <pre style={{maxHeight:160, overflow:'auto'}}>{JSON.stringify(ctxRef.current, null, 2)}</pre>
      </div>
    </div>
  );
}
