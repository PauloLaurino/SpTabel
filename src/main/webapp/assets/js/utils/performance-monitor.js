// ============================================
// PERFORMANCE MONITOR - Monitoramento de performance FASE 3
// ============================================

window.performanceMonitor = (function() {
    'use strict';
    
    var metrics = {
        api: {
            requests: 0,
            cacheHits: 0,
            errors: 0,
            totalTime: 0
        },
        ui: {
            interactions: 0,
            renders: 0
        },
        memory: {
            start: 0,
            current: 0
        }
    };
    
    var listeners = [];
    
    function inicializar() {
        console.log('📊 Performance Monitor inicializando...');
        
        // Iniciar monitoramento
        startMonitoring();
        
        // Configurar Event Bus
        configurarEventBus();
        
        console.log('✅ Performance Monitor inicializado');
    }
    
    function configurarEventBus() {
        if (!window.eventBus) return;
        
        // Monitorar requisições API
        window.eventBus.on('api:request:iniciado', function(data) {
            metrics.api.requests++;
            trackTime(data.endpoint, 'start');
        });
        
        window.eventBus.on('api:request:sucesso', function(data) {
            trackTime(data.endpoint, 'end');
        });
        
        window.eventBus.on('api:cache:hit', function() {
            metrics.api.cacheHits++;
        });
        
        window.eventBus.on('api:request:erro', function() {
            metrics.api.errors++;
        });
        
        // Monitorar interações UI
        window.eventBus.on('ui:interaction', function() {
            metrics.ui.interactions++;
        });
        
        window.eventBus.on('ui:render', function() {
            metrics.ui.renders++;
        });
    }
    
    function startMonitoring() {
        // Medir memória inicial (aproximado)
        metrics.memory.start = estimateMemoryUsage();
        
        // Atualizar memória periodicamente
        setInterval(function() {
            metrics.memory.current = estimateMemoryUsage();
        }, 30000);
    }
    
    function estimateMemoryUsage() {
        if (window.performance && window.performance.memory) {
            return Math.round(window.performance.memory.usedJSHeapSize / 1024 / 1024); // MB
        }
        return 0;
    }
    
    var timeTrackers = {};
    
    function trackTime(key, action) {
        if (action === 'start') {
            timeTrackers[key] = {
                start: Date.now(),
                end: null
            };
        } else if (action === 'end' && timeTrackers[key]) {
            timeTrackers[key].end = Date.now();
            var duration = timeTrackers[key].end - timeTrackers[key].start;
            
            metrics.api.totalTime += duration;
            
            // Notificar listeners
            notifyListeners('api:time', {
                endpoint: key,
                duration: duration
            });
            
            delete timeTrackers[key];
        }
    }
    
    function notifyListeners(event, data) {
        listeners.forEach(function(listener) {
            if (listener.event === event && typeof listener.callback === 'function') {
                try {
                    listener.callback(data);
                } catch (e) {
                    console.error('❌ Erro no listener do performance monitor:', e);
                }
            }
        });
    }
    
    function getMetrics() {
        var cacheHitRate = metrics.api.requests > 0 
            ? Math.round((metrics.api.cacheHits / metrics.api.requests) * 100) 
            : 0;
        
        var avgResponseTime = metrics.api.requests > 0
            ? Math.round(metrics.api.totalTime / metrics.api.requests)
            : 0;
        
        return {
            api: {
                requests: metrics.api.requests,
                cacheHits: metrics.api.cacheHits,
                cacheHitRate: cacheHitRate + '%',
                errors: metrics.api.errors,
                errorRate: Math.round((metrics.api.errors / Math.max(1, metrics.api.requests)) * 100) + '%',
                avgResponseTime: avgResponseTime + 'ms',
                totalTime: metrics.api.totalTime + 'ms'
            },
            ui: {
                interactions: metrics.ui.interactions,
                renders: metrics.ui.renders
            },
            memory: {
                start: metrics.memory.start + 'MB',
                current: metrics.memory.current + 'MB'
            },
            timestamp: new Date().toISOString()
        };
    }
    
    function getPerformanceScore() {
        var score = 100;
        
        // Penalizar por erros
        if (metrics.api.errors > 0) {
            score -= metrics.api.errors * 2;
        }
        
        // Penalizar por uso de memória alto
        if (metrics.memory.current > 100) { // > 100MB
            score -= 10;
        }
        
        // Bonus por cache hits
        if (metrics.api.cacheHits > 10) {
            score += Math.min(metrics.api.cacheHits, 20);
        }
        
        return Math.max(0, Math.min(100, score));
    }
    
    function gerarRelatorio() {
        var metricsData = getMetrics();
        var score = getPerformanceScore();
        
        var relatorio = [
            '📊 RELATÓRIO DE PERFORMANCE',
            '===========================',
            'Score de Performance: ' + score + '/100',
            '',
            'API:',
            '  • Requisições: ' + metricsData.api.requests,
            '  • Cache Hits: ' + metricsData.api.cacheHits + ' (' + metricsData.api.cacheHitRate + ')',
            '  • Erros: ' + metricsData.api.errors + ' (' + metricsData.api.errorRate + ')',
            '  • Tempo médio resposta: ' + metricsData.api.avgResponseTime,
            '',
            'UI:',
            '  • Interações: ' + metricsData.ui.interactions,
            '  • Renders: ' + metricsData.ui.renders,
            '',
            'Memória:',
            '  • Inicial: ' + metricsData.memory.start,
            '  • Atual: ' + metricsData.memory.current,
            '',
            'Timestamp: ' + new Date(metricsData.timestamp).toLocaleString()
        ];
        
        return relatorio.join('\n');
    }
    
    function on(event, callback) {
        listeners.push({ event: event, callback: callback });
    }
    
    function off(event, callback) {
        listeners = listeners.filter(function(listener) {
            return !(listener.event === event && listener.callback === callback);
        });
    }
    
    return {
        inicializar: inicializar,
        getMetrics: getMetrics,
        getPerformanceScore: getPerformanceScore,
        gerarRelatorio: gerarRelatorio,
        on: on,
        off: off,
        
        // Para debug
        _getMetrics: function() { return metrics; },
        _reset: function() {
            metrics = {
                api: { requests: 0, cacheHits: 0, errors: 0, totalTime: 0 },
                ui: { interactions: 0, renders: 0 },
                memory: { start: estimateMemoryUsage(), current: 0 }
            };
            timeTrackers = {};
            console.log('🧹 Métricas de performance resetadas');
        }
    };
})();

console.log('✅ performance-monitor.js criado - Bônus FASE 3');