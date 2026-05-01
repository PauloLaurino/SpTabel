#!/usr/bin/env node
// tools/collect-client-flow.js
// Usage: node tools/collect-client-flow.js --url http://localhost:3000/#/clientes --out debug/logs/session-client-flow.json --headful

const fs = require('fs');
const path = require('path');

async function run() {
  const argv = require('minimist')(process.argv.slice(2));
  const url = argv.url || argv.u || 'http://localhost:3000/#/clientes';
  const out = argv.out || argv.o || `debug/logs/session-client-flow-${new Date().toISOString().replace(/[:.]/g,'-')}.json`;
  const headful = !!argv.headful;
  const timeout = Number(argv.timeout || argv.t || 30000);

  let playwright;
  try {
    playwright = require('playwright');
  } catch (e) {
    console.error('\nPlaywright not found. Install it with:');
    console.error('  npm install -D playwright');
    console.error('  npx playwright install\n');
    process.exit(1);
  }

  const browser = await playwright.chromium.launch({ headless: !headful });
  const context = await browser.newContext();
  const page = await context.newPage();

  const logs = [];
  const network = [];

  page.on('console', async (msg) => {
    try {
      const args = [];
      for (const handle of msg.args()) {
        try { args.push(await handle.jsonValue()); } catch (e) { args.push(msg.text()); }
      }
      logs.push({ ts: new Date().toISOString(), type: msg.type(), text: msg.text(), args });
      console.log(`[console][${msg.type()}] ${msg.text()}`);
    } catch (e) {}
  });

  page.on('pageerror', err => {
    logs.push({ ts: new Date().toISOString(), type: 'pageerror', message: err && err.message, stack: err && err.stack });
    console.error('[pageerror]', err && err.message);
  });

  page.on('response', async (res) => {
    try {
      const request = res.request();
      network.push({ ts: new Date().toISOString(), url: request.url(), method: request.method(), status: res.status(), statusText: res.statusText(), headers: res.headers(), requestHeaders: request.headers() });
    } catch (e) {}
  });

  page.on('requestfailed', req => {
    try { network.push({ ts: new Date().toISOString(), url: req.url(), method: req.method(), failure: req.failure() }); } catch (e) {}
  });

  fs.mkdirSync(path.dirname(out), { recursive: true });

  console.log('Navigating to', url);
  try {
    await page.goto(url, { timeout, waitUntil: 'domcontentloaded' });
  } catch (e) { console.error('Navigation error (continuing):', e.message); }

  // wait for AG Grid rows or dashboard container
  try {
    await page.waitForSelector('.ag-root, .ag-center-cols-container .ag-row, [data-dashboard-ready]', { timeout: 20000 });
    console.log('AG Grid or dashboard detected');
  } catch (e) { console.warn('Grid not detected:', e.message); }

  // Try to click the first ag-row if present
  try {
    const row = await page.$('.ag-center-cols-container .ag-row');
    if (row) {
      console.log('Clicking first grid row');
      await row.click();
      // wait for any network activity
      await page.waitForTimeout(1200);
    } else {
      console.log('No .ag-row found to click');
    }
  } catch (e) { console.warn('Click action failed:', e.message); }

  // try to click any details button/link if present
  try {
    const detail = await page.$('button[title="Abrir"], button.open-details, a.open-details, .btn-details');
    if (detail) { console.log('Clicking details control'); await detail.click(); await page.waitForTimeout(1000); }
  } catch (e) { }

  // final wait and capture
  await page.waitForTimeout(1000);

  try {
    const screenshotPath = out.replace(/\.json$/, '.png');
    await page.screenshot({ path: screenshotPath, fullPage: true });
    const htmlPath = out.replace(/\.json$/, '.html');
    const html = await page.content();
    fs.writeFileSync(htmlPath, html, 'utf8');
    console.log('Saved screenshot:', screenshotPath);
    console.log('Saved HTML snapshot:', htmlPath);
  } catch (e) { console.error('Failed to capture snapshot:', e.message); }

  let storage = { localStorage: {}, sessionStorage: {} };
  try {
    storage = await page.evaluate(() => {
      const s = { localStorage: {}, sessionStorage: {} };
      try { for (let i=0;i<localStorage.length;i++){ const k=localStorage.key(i); s.localStorage[k]=localStorage.getItem(k);} } catch(e){}
      try { for (let i=0;i<sessionStorage.length;i++){ const k=sessionStorage.key(i); s.sessionStorage[k]=sessionStorage.getItem(k);} } catch(e){}
      return s;
    });
  } catch (e) { }

  const outObj = { ts: new Date().toISOString(), url, logs, network, storage };
  fs.writeFileSync(out, JSON.stringify(outObj, null, 2), 'utf8');
  console.log('Saved debug log:', out);

  await browser.close();
  process.exit(0);
}

run().catch(e => { console.error('Fatal error:', e && e.stack); process.exit(1); });
