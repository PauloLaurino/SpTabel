#!/usr/bin/env node

const { chromium } = require('playwright');
const fs = require('fs');
const path = require('path');

async function analyzeVisualRendering() {
  const browser = await chromium.launch({ headless: true });
  const page = await browser.newPage();

  // Habilitar console logs
  page.on('console', (msg) => {
    if (msg.type() === 'log' || msg.type() === 'warn' || msg.type() === 'error' || msg.type() === 'debug') {
      console.log(`[${msg.type().toUpperCase()}] ${msg.text()}`);
    }
  });

  try {
    // Login
    console.log('🔐 Iniciando login...');
    await page.goto('http://localhost:3000/login', { waitUntil: 'networkidle' });
    await page.fill('input#username', 'admin');
    await page.fill('input#password', 'admin');
    await page.click('button[type="submit"]');
    await page.waitForNavigation({ waitUntil: 'networkidle' });

    // Navegar ao dashboard
    console.log('📊 Navegando para dashboard...');
    await page.goto('http://localhost:3000/workspace', { waitUntil: 'networkidle' });
    await page.waitForTimeout(3000); // Aguardar renderização completa

    // Capturar screenshot
    await page.screenshot({ path: 'debug/logs/screenshot-analysis.png', fullPage: true });
    console.log('✅ Screenshot salvo: debug/logs/screenshot-analysis.png');

    // Analisar elementos visuais
    console.log('\n📋 ANÁLISE VISUAL DETALHADA:\n');

    // 1. Verificar Canvas
    const canvasExists = await page.locator('div[data-testid="dashboard-canvas"], .Canvas-sc').first().count() > 0;
    console.log(`1️⃣  Canvas renderizado: ${canvasExists ? '✅ SIM' : '❌ NÃO'}`);

    // 2. Verificar WidgetLayers (containers dos widgets)
    const widgetLayerCount = await page.locator('div[style*="position: absolute"]').count();
    console.log(`2️⃣  Camadas de widgets (absolute positioned): ${widgetLayerCount} encontradas`);

    // 3. Verificar Gráfico
    const chartCanvas = await page.locator('canvas').count();
    console.log(`3️⃣  Elementos <canvas> (Chart.js): ${chartCanvas} encontrados`);
    
    const chartContainer = await page.locator('div.ChartWidget-sc, [data-widget-type="chart"]').count();
    console.log(`    Container de gráfico: ${chartContainer > 0 ? '✅ SIM' : '❌ NÃO'}`);

    // 4. Verificar KPI Cards
    const kpiCards = await page.locator('div.KpiWidget-sc, [data-widget-type="kpi"]').count();
    console.log(`4️⃣  Cards de KPI: ${kpiCards} encontrados`);
    
    // Extrair valores dos KPIs visíveis
    if (kpiCards > 0) {
      const kpiValues = await page.locator('[data-widget-type="kpi"]').allTextContents();
      console.log(`    Valores encontrados:\n${kpiValues.join('\n')}`);
    }

    // 5. Verificar AG Grid
    const agGrid = await page.locator('.ag-root').count();
    console.log(`5️⃣  AG Grid renderizado: ${agGrid > 0 ? '✅ SIM' : '❌ NÃO'}`);
    
    if (agGrid > 0) {
      const rowCount = await page.locator('.ag-row').count();
      console.log(`    Linhas de dados: ${rowCount}`);
    }

    // 6. Verificar Sidebar
    const sidebar = await page.locator('[data-testid="sidebar"], .CollapsibleSidebar-sc').count();
    console.log(`6️⃣  Sidebar renderizado: ${sidebar > 0 ? '✅ SIM' : '❌ NÃO'}`);

    // 7. Verificar Header
    const header = await page.locator('header, nav').count();
    console.log(`7️⃣  Header/Nav renderizado: ${header > 0 ? '✅ SIM' : '❌ NÃO'}`);

    // 8. Dimensões do viewport
    const viewport = page.viewportSize();
    console.log(`8️⃣  Viewport: ${viewport.width}x${viewport.height}`);

    // 9. Altura calculada do Canvas
    const canvasHeight = await page.evaluate(() => {
      const canvas = document.querySelector('div[style*="position: relative"]');
      if (canvas) {
        const rect = canvas.getBoundingClientRect();
        return `${rect.height}px (top: ${rect.top}px)`;
      }
      return 'Canvas não encontrado';
    });
    console.log(`9️⃣  Altura do Canvas: ${canvasHeight}`);

    // 10. Verificar erros de renderização
    const consoleErrors = [];
    page.on('console', (msg) => {
      if (msg.type() === 'error') {
        consoleErrors.push(msg.text());
      }
    });

    console.log(`🔟 Erros no console: ${consoleErrors.length > 0 ? consoleErrors.join(', ') : 'Nenhum'}`);

    // 11. Verificar visibilidade dos widgets (CSS visibility/display)
    const widgetVisibility = await page.evaluate(() => {
      const results = [];
      
      // Gráfico
      const chart = document.querySelector('[data-widget-type="chart"], .ChartWidget-sc');
      if (chart) {
        const style = window.getComputedStyle(chart);
        results.push({
          widget: 'Gráfico',
          display: style.display,
          visibility: style.visibility,
          opacity: style.opacity,
          height: style.height
        });
      }

      // KPIs
      const kpis = document.querySelectorAll('[data-widget-type="kpi"], .KpiWidget-sc');
      kpis.forEach((kpi, i) => {
        const style = window.getComputedStyle(kpi);
        results.push({
          widget: `KPI #${i + 1}`,
          display: style.display,
          visibility: style.visibility,
          opacity: style.opacity,
          height: style.height
        });
      });

      // AG Grid
      const aggrid = document.querySelector('.ag-root');
      if (aggrid) {
        const style = window.getComputedStyle(aggrid);
        results.push({
          widget: 'AG Grid',
          display: style.display,
          visibility: style.visibility,
          opacity: style.opacity,
          height: style.height
        });
      }

      return results;
    });

    console.log('\n📐 PROPRIEDADES CSS DOS WIDGETS:\n');
    widgetVisibility.forEach(w => {
      console.log(`${w.widget}:`);
      console.log(`  display: ${w.display}`);
      console.log(`  visibility: ${w.visibility}`);
      console.log(`  opacity: ${w.opacity}`);
      console.log(`  height: ${w.height}\n`);
    });

    // 12. HTML Structure
    console.log('\n🏗️  ESTRUTURA HTML DO DASHBOARD:\n');
    const htmlStructure = await page.evaluate(() => {
      const canvas = document.querySelector('div[style*="position: relative"]');
      if (canvas) {
        return `Canvas Children: ${canvas.children.length}`;
      }
      return 'Canvas não encontrado';
    });
    console.log(htmlStructure);

    // Salvar relatório
    const report = `
# 📊 ANÁLISE VISUAL DO DASHBOARD - ${new Date().toLocaleString('pt-BR')}

## ✅ Elementos Renderizados

- Canvas: ${canvasExists ? '✅' : '❌'}
- WidgetLayers (absolute): ${widgetLayerCount}
- Canvases (Chart.js): ${chartCanvas}
- KPI Cards: ${kpiCards}
- AG Grid: ${agGrid > 0 ? '✅' : '❌'}
- Sidebar: ${sidebar > 0 ? '✅' : '❌'}
- Header: ${header > 0 ? '✅' : '❌'}

## 📐 Dimensões

- Viewport: ${viewport.width}x${viewport.height}
- Canvas Height: ${canvasHeight}

## 🎨 Propriedades CSS

${widgetVisibility.map(w => `### ${w.widget}
- display: ${w.display}
- visibility: ${w.visibility}
- opacity: ${w.opacity}
- height: ${w.height}
`).join('\n')}

## 📋 Estrutura HTML

${htmlStructure}

## 🔍 Conclusão

${
  canvasExists && widgetLayerCount > 0 && kpiCards > 0 && chartCanvas > 0 && agGrid > 0
    ? '✅ TODOS OS WIDGETS RENDERIZADOS CORRETAMENTE'
    : '❌ ALGUNS WIDGETS NÃO APARECEM - VERIFICAR ACIMA'
}
    `;

    fs.writeFileSync('debug/logs/visual-analysis-report.md', report);
    console.log('\n✅ Relatório salvo: debug/logs/visual-analysis-report.md');

  } catch (error) {
    console.error('❌ Erro:', error.message);
  } finally {
    await browser.close();
  }
}

analyzeVisualRendering();
