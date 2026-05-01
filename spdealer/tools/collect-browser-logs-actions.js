#!/usr/bin/env node
// tools/collect-browser-logs-actions.js
// Usage: node tools/collect-browser-logs-actions.js --url http://localhost:3000/financeiro/relatorios --out debug/logs/session_interactive.json --headful --actions "dblclick:.ag-center-cols-container .ag-row"

const fs = require('fs');
const path = require('path');

async function waitForSelectors(page, selectors, timeout) {
  if (!Array.isArray(selectors) || selectors.length === 0) return null;
  for (const selector of selectors) {
    if (!selector) continue;
    try {
      await page.waitForSelector(selector, { timeout, state: 'attached' });
      return selector;
    } catch (e) {
      // tenta próximo selector
    }
  }
  return null;
}

function parseActions(str) {
  if (!str) return [];
  return String(str).split('|').map(s => s.trim()).filter(Boolean).map(raw => {
    const parts = raw.split(':');
    const type = parts.shift();
    const selector = parts.join(':');
    return { type, selector };
  });
}

async function run() {
  const argv = require('minimist')(process.argv.slice(2));
  const url = argv.url || argv.u || 'http://localhost:3000/';
  const out = argv.out || argv.o || `debug/logs/session-${new Date().toISOString().replace(/[:.]/g,'-')}.json`;
  const headful = !!argv.headful;
  const timeout = Number(argv.timeout || argv.t || 30000);
  const actions = parseActions(argv.actions || argv.a);
  const waitSelectors = String(argv['wait-for'] || argv.waitfor || '.ag-root,.kpi-card,.dashboard-container,[data-dashboard-ready]')
    .split(',')
    .map((s) => s.trim())
    .filter(Boolean);

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
      const handles = msg.args();
      for (const handle of handles) {
        try {
          const value = await handle.jsonValue();
          args.push(value);
        } catch (e) {
          args.push(`[unavailable: ${e?.message || 'context destroyed'}]`);
        }
      }
      logs.push({ ts: new Date().toISOString(), type: msg.type(), text: msg.text(), args });
      console.log(`[console][${msg.type()}] ${msg.text()}`);
    } catch (e) { }
  });

  page.on('pageerror', err => {
    logs.push({ ts: new Date().toISOString(), type: 'pageerror', message: err && err.message, stack: err && err.stack });
    console.error('[pageerror]', err && err.message);
  });

  page.on('response', async (res) => {
    try {
      const request = res.request();
      network.push({
        ts: new Date().toISOString(),
        url: request.url(),
        method: request.method(),
        status: res.status(),
        statusText: res.statusText(),
        headers: res.headers(),
        requestHeaders: request.headers(),
      });
    } catch (e) { }
  });

  page.on('requestfailed', req => {
    try {
      network.push({ ts: new Date().toISOString(), url: req.url(), method: req.method(), failure: req.failure() });
      console.warn('[requestfailed]', req.url(), req.failure());
    } catch (e) { }
  });

  const outDir = path.dirname(out);
  fs.mkdirSync(outDir, { recursive: true });

  console.log('Navigating to', url);
  try {
    await page.goto(url, { timeout, waitUntil: 'domcontentloaded' });
  } catch (e) {
    console.error('Navigation error (continuing):', e.message);
  }

  if (waitSelectors.length) {
    const matched = await waitForSelectors(page, waitSelectors, timeout);
    if (matched) console.log('Detected selector on target page:', matched);
    else console.warn('Warning: no wait selector matched on target page');
  }

  // small SPA settle
  await page.waitForTimeout(800);

  // perform actions sequentially
  for (const act of actions) {
    try {
      console.log('Performing action', act.type, act.selector);
      if (!act.selector) continue;
      // use locator for robustness
      const locator = page.locator(act.selector).first();
      await locator.waitFor({ timeout });
      if (act.type === 'click') {
        await locator.click({ timeout });
      } else if (act.type === 'dblclick' || act.type === 'doubleclick') {
        await locator.dblclick({ timeout });
      } else if (act.type === 'focus') {
        await locator.focus({ timeout });
      } else if (act.type === 'type') {
        // selector should be like selector|text, but we keep simple: text after space
        const parts = act.selector.split('|');
        await page.fill(parts[0], parts[1] || '');
      }
      // small delay between actions
      await page.waitForTimeout(600);
    } catch (e) {
      console.warn('Action failed:', act, e && e.message);
    }
  }

  // wait a bit for modal/effects
  await page.waitForTimeout(1200);

  try {
    const screenshotPath = out.replace(/\.json$/, '.png');
    await page.screenshot({ path: screenshotPath, fullPage: true });
    const htmlPath = out.replace(/\.json$/, '.html');
    const html = await page.content();
    fs.writeFileSync(htmlPath, html, 'utf8');
    console.log('Saved screenshot:', screenshotPath);
    console.log('Saved HTML snapshot:', htmlPath);
  } catch (e) {
    console.error('Failed to capture screenshot/html:', e.message);
  }

  let storage = { localStorage: {}, sessionStorage: {} };
  try {
    storage = await page.evaluate(() => {
      const items = { localStorage: {}, sessionStorage: {} };
      try { for (let i = 0; i < localStorage.length; i++) { const k = localStorage.key(i); items.localStorage[k] = localStorage.getItem(k); } } catch (e) {}
      try { for (let i = 0; i < sessionStorage.length; i++) { const k = sessionStorage.key(i); items.sessionStorage[k] = sessionStorage.getItem(k); } } catch (e) {}
      return items;
    });
  } catch (e) { console.warn('Warning: storage capture skipped -', e.message); }

  const outObj = { ts: new Date().toISOString(), url, logs: logs, network, storage };
  fs.writeFileSync(out, JSON.stringify(outObj, null, 2), 'utf8');
  console.log('Saved debug log:', out);

  await browser.close();
  process.exit(0);
}

run().catch(e => { console.error('Fatal error:', e && e.stack); process.exit(1); });
