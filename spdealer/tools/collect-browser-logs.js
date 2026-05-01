#!/usr/bin/env node
// tools/collect-browser-logs.js
// Usage: node tools/collect-browser-logs.js --url http://localhost:3000/workspace --out debug/logs/session.json --headful

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

async function run() {
  const argv = require('minimist')(process.argv.slice(2));
  const url = argv.url || argv.u || 'http://localhost:3000/';
  const out = argv.out || argv.o || `debug/logs/session-${new Date().toISOString().replace(/[:.]/g,'-')}.json`;
  const headful = !!argv.headful;
  const timeout = Number(argv.timeout || argv.t || 30000);
  const doLogin = !!argv.login;
  const loginUrl = argv['login-url'] || argv.lu || 'http://localhost:3000/login';
  const username = argv.username || argv.uuser || 'admin';
  const password = argv.password || argv.pwd || 'admin';
  const postLoginDelay = Number(argv['post-login-delay'] || argv.pld || 1200);
  const waitSelectors = String(argv['wait-for'] || argv.waitfor || '.ag-root,.kpi-card,.dashboard-container,[data-dashboard-ready]')
    .split(',')
    .map((s) => s.trim())
    .filter(Boolean);

  // lazy require playwright so script still prints helpful message when not installed
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
          // jsonValue pode falhar se o contexto for destruído durante a navegação
          const value = await handle.jsonValue();
          args.push(value);
        } catch (e) {
          args.push(`[unavailable: ${e?.message || 'context destroyed'}]`);
        }
      }
      logs.push({ ts: new Date().toISOString(), type: msg.type(), text: msg.text(), args });
      console.log(`[console][${msg.type()}] ${msg.text()}`);
    } catch (e) {
      // ignora erros no logger para não interromper a captura
    }
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
      if (res.status() >= 400) {
        console.warn(`[network][${res.status()}] ${request.method()} ${request.url()}`);
      }
    } catch (e) { /* ignore */ }
  });

  page.on('requestfailed', req => {
    try {
      network.push({ ts: new Date().toISOString(), url: req.url(), method: req.method(), failure: req.failure() });
      console.warn('[requestfailed]', req.url(), req.failure());
    } catch (e) { }
  });

  // ensure output dir exists
  const outDir = path.dirname(out);
  fs.mkdirSync(outDir, { recursive: true });

  // If requested, perform login first
  if (doLogin) {
    console.log('Performing automated login to', loginUrl, 'with user', username);
    try {
      await page.goto(loginUrl, { timeout, waitUntil: 'domcontentloaded' });
    } catch (e) {
      console.error('Login page navigation error (continuing):', e.message);
    }
    try {
      // fill username and password using known selectors from Login.tsx
      await page.waitForSelector('#username', { timeout });
      await page.fill('#username', username);
      await page.waitForSelector('#password', { timeout });
      await page.fill('#password', password);
      await Promise.all([
        page.click('button[type="submit"]'),
        page.waitForNavigation({ timeout, waitUntil: 'domcontentloaded' }).catch(() => {})
      ]);
      // small delay to let SPA settle
      await page.waitForTimeout(postLoginDelay);
      if (waitSelectors.length) {
        const matched = await waitForSelectors(page, waitSelectors, timeout + postLoginDelay);
        if (matched) {
          console.log('Detected selector after login:', matched);
        } else {
          console.warn('Warning: no wait selector matched after login');
        }
      }
      console.log('Login action completed');
    } catch (e) {
      console.error('Automated login failed (continuing):', e.message);
    }
  }

  console.log('Navigating to', url);
  try {
    await page.goto(url, { timeout, waitUntil: 'domcontentloaded' });
  } catch (e) {
    console.error('Navigation error (continuing):', e.message);
  }

  if (waitSelectors.length) {
    const matched = await waitForSelectors(page, waitSelectors, timeout);
    if (matched) {
      console.log('Detected selector on target page:', matched);
    } else {
      console.warn('Warning: no wait selector matched on target page');
    }
  }

  // wait a bit to let SPA do work
  await page.waitForTimeout(1500);

  // take screenshot and HTML
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

  // gather cookies/localStorage/sessionStorage
  let storage = { localStorage: {}, sessionStorage: {} };
  try {
    storage = await page.evaluate(() => {
      const items = { localStorage: {}, sessionStorage: {} };
      try {
        for (let i = 0; i < localStorage.length; i++) {
          const k = localStorage.key(i); items.localStorage[k] = localStorage.getItem(k);
        }
      } catch (e) { }
      try {
        for (let i = 0; i < sessionStorage.length; i++) {
          const k = sessionStorage.key(i); items.sessionStorage[k] = sessionStorage.getItem(k);
        }
      } catch (e) { }
      return items;
    });
  } catch (e) {
    console.warn('Warning: storage capture skipped -', e.message);
  }

  // Save output JSON
  const outObj = { ts: new Date().toISOString(), url, logs, network, storage };
  fs.writeFileSync(out, JSON.stringify(outObj, null, 2), 'utf8');
  console.log('Saved debug log:', out);

  await browser.close();
  process.exit(0);
}

run().catch(e => {
  console.error('Fatal error:', e && e.stack);
  process.exit(1);
});
