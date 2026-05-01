#!/usr/bin/env node
// tools/collect-browser-logs-fix.js
// Versão com tratamento para iframe webpack-dev-server que bloqueia eventos

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

  // Listener para console, erros de página, etc.
  page.on('console', (msg) => {
    const args = msg.args();
    if (args.length > 0) {
      args.forEach((arg) => {
        arg.jsonValue()
          .then((value) => {
            const type = msg.type().toUpperCase();
            console.log(`[console][${type}] ${JSON.stringify(value)}`);
          })
          .catch((e) => {
            const type = msg.type().toUpperCase();
            console.log(`[console][${type}] [Unable to serialize: ${e.message}]`);
          });
      });
    } else {
      const type = msg.type().toUpperCase();
      console.log(`[console][${type}] ${msg.text()}`);
    }
  });

  page.on('pageerror', (err) => {
    console.log(`[PAGE ERROR] ${err.message}`);
  });

  page.on('requestfailed', (req) => {
    console.log(`[REQUEST FAILED] ${req.method()} ${req.url()} - ${req.failure()?.errorText}`);
  });

  const networkRequests = [];
  page.on('requestfinished', (req) => {
    try {
      const response = req.response();
      networkRequests.push({
        method: req.method(),
        url: req.url(),
        status: response?.status ? response.status() : (response?.status ? response.status : null),
        statusText: response?.statusText ? response.statusText() : null,
      });
    } catch (e) {
      // silencioso se não conseguir capturar response
    }
  });

  // PARTE CRÍTICA: Remover iframe webpack-dev-server que bloqueia cliques
  async function removeWebpackOverlay(page) {
    try {
      await page.evaluate(() => {
        const iframes = document.querySelectorAll('iframe#webpack-dev-server-client-overlay');
        iframes.forEach((iframe) => iframe.remove());
      });
    } catch (e) {
      // silenciosamente falha se não conseguir
    }
  }

  try {
    if (doLogin) {
      console.log(`Performing automated login to ${loginUrl} with user ${username}`);
      
      await page.goto(loginUrl, { waitUntil: 'networkidle', timeout });
      await page.waitForTimeout(500); // pequeno delay
      
      // Remove webpack overlay
      await removeWebpackOverlay(page);
      
      // Preenche username (usa ID em vez de name)
      await page.fill('input#username', username);
      
      // Preenche password (usa ID em vez de name)
      await page.fill('input#password', password);
      
      // Clica submit
      await page.click('button[type="submit"]', { timeout: 10000 });
      
      // Aguarda redirecionamento
      await page.waitForNavigation({ waitUntil: 'networkidle', timeout });
      
      console.log(`Login successful, redirected to ${page.url()}`);
      
      await page.waitForTimeout(postLoginDelay);
    }

    console.log(`Navigating to ${url}`);
    await page.goto(url, { waitUntil: 'networkidle', timeout });

    // Remove webpack overlay novamente após navegação
    await removeWebpackOverlay(page);

    // Aguarda pelo menos um dos seletores
    const matchedSelector = await waitForSelectors(page, waitSelectors, timeout);
    if (matchedSelector) {
      console.log(`[Dashboard Ready] Matched selector: ${matchedSelector}`);
      await page.waitForTimeout(1500); // aguarda renderização completa
    } else {
      console.log('Warning: no wait selector matched on target page');
    }

    // Captura screenshot
    const screenshotPath = path.join(path.dirname(out), 'screenshot.png');
    await page.screenshot({ path: screenshotPath, fullPage: true });
    console.log(`Screenshot saved: ${screenshotPath}`);

    // Captura HTML
    const htmlContent = await page.content();
    const htmlPath = path.join(path.dirname(out), 'page.html');
    fs.writeFileSync(htmlPath, htmlContent);
    console.log(`HTML saved: ${htmlPath}`);

    // Captura localStorage e sessionStorage
    let storageData = {};
    try {
      storageData = await page.evaluate(() => {
        const storage = {};
        storage.localStorage = { ...localStorage };
        storage.sessionStorage = { ...sessionStorage };
        return storage;
      });
    } catch (e) {
      console.log(`[Storage Capture] Could not capture storage: ${e.message}`);
    }

    // Monta resultado final
    const result = {
      url,
      timestamp: new Date().toISOString(),
      networkRequests,
      storage: storageData,
      pages: {
        screenshot: 'screenshot.png',
        html: 'page.html',
      },
    };

    // Cria diretório se não existir
    const outDir = path.dirname(out);
    if (!fs.existsSync(outDir)) {
      fs.mkdirSync(outDir, { recursive: true });
    }

    fs.writeFileSync(out, JSON.stringify(result, null, 2));
    console.log(`\nSession data saved: ${out}`);
  } catch (e) {
    console.error(`Fatal error: ${e.message}`);
    process.exit(1);
  } finally {
    await browser.close();
  }
}

run().catch((e) => {
  console.error('Error:', e);
  process.exit(1);
});
