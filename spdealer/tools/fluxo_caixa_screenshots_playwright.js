// Script Playwright para capturar telas do fluxo de caixa.
// Uso: node tools/fluxo_caixa_screenshots_playwright.js --url http://localhost:3000 --user admin --pass admin --empresa 001 --filial 001 --out docs/screenshots/ [--headful] [--slowMo 50] [--recordVideo]

const { chromium } = require('playwright');
const fs = require('fs');
const path = require('path');

async function run() {
  const args = require('minimist')(process.argv.slice(2));
  const baseUrl = args.url || 'http://localhost:3000';
  const outDir = args.out || 'docs/screenshots/';
  if (!fs.existsSync(outDir)) fs.mkdirSync(outDir, { recursive: true });

  const headful = !!args.headful || !!args.h;
  const slowMo = args.slowMo ? Number(args.slowMo) : 0;
  const recordVideo = !!args.recordVideo;
  console.log('Playwright options:', { headful, slowMo, recordVideo });

  const browser = await chromium.launch({ headless: !headful, slowMo: slowMo });
  const contextOptions = recordVideo ? { recordVideo: { dir: outDir } } : {};
  const context = await browser.newContext(contextOptions);
  const page = await context.newPage();

  page.on('console', msg => console.log('[page]', msg.type(), msg.text()));
  page.on('pageerror', err => console.error('[page error]', err));
  page.on('close', () => console.warn('[page] closed event'));
  page.on('crash', () => console.error('[page] crashed'));
  browser.on('disconnected', () => console.error('[browser] disconnected'));

  try {
    // 1) Login
    console.log('Navegando para /login...');
    try { await page.goto(baseUrl + '/login', { timeout: 60000, waitUntil: 'domcontentloaded' }); } catch (err) { console.error('Erro ao abrir /login:', err); }

    await page.waitForSelector('#empresa option:not(:empty)', { timeout: 10000 }).catch(()=>{});
    await page.waitForSelector('#filial option:not(:empty)', { timeout: 10000 }).catch(()=>{});

    const argsUser = args.user || args.u || process.env.FLUXO_USER;
    const argsPass = args.pass || args.p || process.env.FLUXO_PASS;
    const argsEmpresa = args.empresa || args.emp || null;
    const argsFilial = args.filial || args.f || null;

    if (argsEmpresa) await page.selectOption('#empresa', argsEmpresa).catch(()=>{});
    if (argsFilial) await page.selectOption('#filial', argsFilial).catch(()=>{});

    if (argsUser && argsPass) {
      try {
        await page.fill('#username', argsUser);
        await page.fill('#password', argsPass);
        await Promise.all([
          page.click('button[type=submit]'),
          page.waitForNavigation({ timeout: 20000, waitUntil: 'networkidle' })
        ]);
      } catch (err) { console.error('Erro durante login:', err); }
    } else {
      console.log('Aviso: credenciais não fornecidas. Tentando capturar telas sem login.');
    }

    await page.waitForTimeout(1500);

    // 2) Ir para relatórios (rota fixa correta) e selecionar submenu 'Fluxo de Caixa'
    console.log('Navegando para /financeiro/relatorios...');
    try { await page.goto(baseUrl + '/financeiro/relatorios', { timeout: 60000, waitUntil: 'domcontentloaded' }); } catch (err) { console.error('Erro ao abrir /financeiro/relatorios:', err); }

    // Clicar explicitamente no submenu 'Fluxo de Caixa' para forçar buscarDados()
    try {
      await page.waitForTimeout(500);
      const fluxoBtn = await page.$('button:has-text("Fluxo de Caixa")') || await page.$('text=Fluxo de Caixa');
      if (fluxoBtn) {
        console.log('Clicando no submenu Fluxo de Caixa...');
        await fluxoBtn.click().catch(()=>{});
        // aguardar que o componente processe a mudança de tab e faça calls XHR
        await page.waitForTimeout(1000);
      } else {
        console.log('Botão Fluxo de Caixa não encontrado no submenu — prosseguindo sem clique explícito');
      }
    } catch (e) {
      console.warn('Erro tentando clicar no submenu Fluxo de Caixa:', e);
    }

    // esperar XHR e grid
    try { await page.waitForResponse(resp => resp.url().includes('/api') && resp.status() === 200, { timeout: 30000 }); } catch (err) { console.warn('Nenhuma resposta XHR dentro do timeout:', err.message); }
    try { await page.waitForSelector('.fluxo-container, .ag-root', { timeout: 30000 }); } catch (err) { console.error('Timeout esperando grid:', err); }

    // capturas
    await page.screenshot({ path: path.join(outDir, 'fluxo_01.png'), fullPage: true });

    const expandBtn = await page.$('.expand-button');
    if (expandBtn) { await expandBtn.click(); await page.waitForTimeout(500); }
    await page.screenshot({ path: path.join(outDir, 'fluxo_02.png'), fullPage: false });

    const checkboxes = await page.$$('input[type=checkbox]');
    for (let i=0; i< Math.min(2, checkboxes.length); i++) { await checkboxes[i].check().catch(()=>{}); }
    await page.waitForTimeout(300);
    await page.screenshot({ path: path.join(outDir, 'fluxo_03.png'), fullPage: false });

    const processBtn = await page.$('button:has-text("Processar")');
    if (processBtn) { await processBtn.click().catch(()=>{}); await page.waitForTimeout(500); await page.screenshot({ path: path.join(outDir, 'fluxo_04.png'), fullPage: false }); }

  } catch (err) {
    console.error('Erro no fluxo principal:', err);
    try { await page.screenshot({ path: path.join(outDir, 'fluxo_error.png'), fullPage: true }); } catch(e){console.warn('Erro ao salvar screenshot de erro', e);}
  } finally {
    try { await context.close(); } catch(e){console.warn('Erro fechando context', e);}
    try { await browser.close(); } catch(e){console.warn('Erro fechando browser', e);}
  }

  console.log('Execução finalizada. Verifique', outDir);
}

run().catch(err => { console.error(err); process.exit(1); });
