#!/usr/bin/env node
// tools/collect-login-and-capture.js
// Usage example:
// node tools/collect-login-and-capture.js --base http://localhost:3000 --login /login --target /financeiro/dashboard-fluxo-caixa --out debug/logs/session_login_fluxocaixa.json --headful

const fs = require('fs');
const path = require('path');

async function run() {
  const argv = require('minimist')(process.argv.slice(2));
  const base = (argv.base || argv.b || 'http://localhost:3000').replace(/\/$/, '');
  const loginPath = argv.login || '/login';
  const targetPath = argv.target || '/financeiro/dashboard-fluxo-caixa';
  const out = argv.out || `debug/logs/session-${new Date().toISOString().replace(/[:.]/g,'-')}.json`;
  const headful = !!argv.headful;
  const timeout = Number(argv.timeout || argv.t || 30000);

  let playwright;
  try { playwright = require('playwright'); } catch (e) {
    console.error('Playwright not installed. Run: npm install -D playwright && npx playwright install');
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
      for (const h of msg.args()) {
        try { args.push(await h.jsonValue()); } catch (e) { args.push(msg.text()); }
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
      const req = res.request();
      network.push({ ts: new Date().toISOString(), url: req.url(), method: req.method(), status: res.status() });
    } catch (e) {}
  });

  const outDir = path.dirname(out);
  fs.mkdirSync(outDir, { recursive: true });

  const loginUrl = base + loginPath;
  console.log('Navigating to', loginUrl);
  try { await page.goto(loginUrl, { timeout, waitUntil: 'domcontentloaded' }); } catch (e) { console.warn('Navigation to login failed:', e.message); }

  // try several selectors for username and password
  const usernameSelectors = [
    "input[placeholder*='Usuário']",
    "input[placeholder*='Seu nome']",
    "input[name*='user']",
    "input[name*='usuario']",
    "input[type='text']"
  ];
  const passwordSelectors = ["input[type='password']", "input[name*='senha']"];

  async function tryFill(selectors, value) {
    for (const s of selectors) {
      try {
        const loc = page.locator(s).first();
        if (await loc.count() === 0) continue;
        await loc.fill(value, { timeout: 3000 });
        console.log('Filled', s);
        return true;
      } catch (e) {
        // continue
      }
    }
    return false;
  }

  // fill credentials (admin/admin provided)
  const userOk = await tryFill(usernameSelectors, 'admin');
  const passOk = await tryFill(passwordSelectors, 'admin');

  // try click submit
  const submitSelectors = ["button[type='submit']", "button:has-text('Entrar')", "button:has-text('Login')", "button:has-text('Acessar')"];
  for (const s of submitSelectors) {
    try {
      const loc = page.locator(s).first();
      if (await loc.count() === 0) continue;
      await loc.click({ timeout: 3000 });
      console.log('Clicked login button', s);
      break;
    } catch (e) {}
  }

  // wait for navigation or dashboard readiness
  try {
    await page.waitForLoadState('networkidle', { timeout: 5000 });
  } catch (e) {}

  // navigate to target dashboard to be sure
  const targetUrl = base + targetPath;
  console.log('Navigating to target', targetUrl);
  try { await page.goto(targetUrl, { timeout, waitUntil: 'domcontentloaded' }); } catch (e) { console.warn('Navigation to target failed:', e.message); }

  // optional: click a tab by visible text (e.g. --tab "Fluxo de Caixa")
  const tabText = argv.tab || argv.tabText;
  if (tabText) {
    try {
      const tab = page.locator(`text="${tabText}"`).first();
      if (await tab.count() > 0) {
        await tab.click({ timeout: 3000 });
        console.log('Clicked tab', tabText);
        // allow UI to settle after tab click
        await page.waitForTimeout(800);
      } else {
        console.warn('Tab text not found on page:', tabText);
      }
    } catch (e) {
      console.warn('click tab failed', e.message);
    }
  }

  // wait for AG Grid or dashboard ready
  try { await page.waitForSelector('.ag-root, .dashboard-container, [data-dashboard-ready]', { timeout: 20000 }); console.log('Dashboard ready'); } catch (e) { console.warn('Dashboard readiness selector not found'); }

  // try dblclick first ag row
  try {
    const row = page.locator('.ag-center-cols-container .ag-row').first();
    if (await row.count() > 0) {
      await row.dblclick({ timeout: 5000 });
      console.log('Performed dblclick on first ag-row');
    } else {
      console.warn('No ag-row found to dblclick');
    }
  } catch (e) { console.warn('dblclick failed', e.message); }

  await page.waitForTimeout(1200);

  try {
    const screenshotPath = out.replace(/\.json$/, '.png');
    await page.screenshot({ path: screenshotPath, fullPage: true });
    const htmlPath = out.replace(/\.json$/, '.html');
    const html = await page.content();
    fs.writeFileSync(htmlPath, html, 'utf8');
    console.log('Saved screenshot:', screenshotPath);
    console.log('Saved HTML snapshot:', htmlPath);
  } catch (e) { console.error('Failed to capture screenshot/html:', e.message); }

  let storage = { localStorage: {}, sessionStorage: {} };
  try {
    storage = await page.evaluate(() => {
      const items = { localStorage: {}, sessionStorage: {} };
      try { for (let i = 0; i < localStorage.length; i++) { const k = localStorage.key(i); items.localStorage[k] = localStorage.getItem(k); } } catch (e) {}
      try { for (let i = 0; i < sessionStorage.length; i++) { const k = sessionStorage.key(i); items.sessionStorage[k] = sessionStorage.getItem(k); } } catch (e) {}
      return items;
    });
  } catch (e) { console.warn('Storage capture skipped', e.message); }

  const outObj = { ts: new Date().toISOString(), base, loginPath, targetPath, logs, network, storage };
  fs.writeFileSync(out, JSON.stringify(outObj, null, 2), 'utf8');
  console.log('Saved debug log:', out);

  await browser.close();
  process.exit(0);
}

run().catch(e => { console.error('Fatal error:', e && e.stack); process.exit(1); });
