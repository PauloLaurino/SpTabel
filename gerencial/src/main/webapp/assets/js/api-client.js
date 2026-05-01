// ============================================
// API CLIENT - REFATORADO (FASE 2)
// Integrado com Event Bus para notificações
// ============================================

function ApiClient(baseUrl) {
    this.baseUrl = baseUrl || '/notas/api';
    this.timeout = 30000;
    this.defaultHeaders = {
        'Content-Type': 'application/json',
        'Accept': 'application/json'
    };

    // Cache simples para evitar requests duplicados
    this.cache = {};
}

// ============================================
// SISTEMA DE CACHE (Fase 3)
// ============================================

ApiClient.prototype.getCacheKey = function (method, endpoint, params) {
    var key = method + ':' + endpoint;
    if (params) {
        key += ':' + JSON.stringify(params);
    }
    return key;
};

ApiClient.prototype.getFromCache = function (key) {
    if (!this.cache[key]) return null;

    var cached = this.cache[key];
    var now = Date.now();

    // ✅ REDUZIR de 5 para 2 minutos
    if (now - cached.timestamp > 120000) { // 2 minutos
        delete this.cache[key];
        return null;
    }

    return cached.data;
};

ApiClient.prototype.setCache = function (key, data) {
    this.cache[key] = {
        data: data,
        timestamp: Date.now()
    };
};

// ============================================
// MÉTODO REQUEST REFATORADO
// ============================================

ApiClient.prototype.request = function (method, endpoint, data, params, useCache) {
    var self = this;

    // 🔥 USAR CACHE MANAGER se disponível
    if (method === 'GET' && useCache !== false && window.cacheManager) {
        var cacheKey = this.getCacheKey(method, endpoint, params);

        // Tentar obter do cache avançado
        var cached = window.cacheManager.obterCacheAPI(endpoint, params);
        if (cached) {
            console.log('💾 [API] Cache hit (Cache Manager):', endpoint);

            // Emitir evento de cache
            if (window.eventBus) {
                window.eventBus.emit('api:cache:hit', {
                    endpoint: endpoint,
                    source: 'cacheManager'
                });
            }

            return Promise.resolve(cached);
        }
    }

    // Fallback para cache antigo
    if (method === 'GET' && useCache !== false) {
        var cacheKeyFallback = this.getCacheKey(method, endpoint, params);
        var cachedFallback = this.getFromCache(cacheKeyFallback);
        if (cachedFallback) {
            console.log('💾 [API] Cache hit (legacy):', endpoint);
            return Promise.resolve(cachedFallback);
        }
    }

    // Emitir evento de início de requisição
    if (window.eventBus) {
        window.eventBus.emit('api:request:iniciado', {
            method: method,
            endpoint: endpoint,
            timestamp: new Date().toISOString()
        });
    }

    return new Promise(function (resolve, reject) {
        try {
            // Construir URL
            var url = self.baseUrl + endpoint;
            if (params) {
                var queryParams = [];
                for (var key in params) {
                    if (params.hasOwnProperty(key)) {
                        var value = params[key];
                        if (value !== undefined && value !== null) {
                            queryParams.push(encodeURIComponent(key) + '=' + encodeURIComponent(value));
                        }
                    }
                }
                if (queryParams.length > 0) {
                    url += '?' + queryParams.join('&');
                }
            }

            // Criar requisição
            var xhr = new XMLHttpRequest();
            xhr.timeout = self.timeout;
            xhr.open(method, url, true);

            // Headers
            for (var header in self.defaultHeaders) {
                if (self.defaultHeaders.hasOwnProperty(header)) {
                    xhr.setRequestHeader(header, self.defaultHeaders[header]);
                }
            }

            // Eventos
            xhr.onload = function () {
                try {
                    var responseText = xhr.responseText;

                    console.log('📡 [API] Resposta:', {
                        status: xhr.status,
                        endpoint: endpoint,
                        length: responseText.length
                    });

                    if (xhr.status >= 200 && xhr.status < 300) {
                        // Parse JSON
                        var jsonResponse = responseText ? JSON.parse(responseText) : {};

                        // 🔥 ARMazenar no Cache Manager para GET
                        if (method === 'GET' && useCache !== false && window.cacheManager) {
                            window.cacheManager.cacheAPI(endpoint, params, jsonResponse);
                        }

                        // Cache legado
                        if (method === 'GET' && useCache !== false) {
                            var cacheKey = self.getCacheKey(method, endpoint, params);
                            self.setCache(cacheKey, jsonResponse);
                        }

                        // Emitir evento de sucesso
                        if (window.eventBus) {
                            window.eventBus.emit('api:request:sucesso', {
                                method: method,
                                endpoint: endpoint,
                                status: xhr.status,
                                data: jsonResponse
                            });
                        }

                        resolve(jsonResponse);
                    } else {
                        // Erro HTTP
                        var errorResponse = responseText ? JSON.parse(responseText) : {};
                        var errorMsg = 'HTTP ' + xhr.status + ': ' + (errorResponse.message || xhr.statusText);

                        // Emitir evento de erro
                        if (window.eventBus) {
                            window.eventBus.emit('api:request:erro', {
                                method: method,
                                endpoint: endpoint,
                                status: xhr.status,
                                error: errorMsg,
                                response: errorResponse
                            });
                        }

                        reject({
                            status: xhr.status,
                            message: errorMsg,
                            response: errorResponse
                        });
                    }
                } catch (error) {
                    // Erro de parsing
                    if (window.eventBus) {
                        window.eventBus.emit('api:request:erro', {
                            method: method,
                            endpoint: endpoint,
                            status: xhr.status,
                            error: 'Erro ao processar resposta: ' + error.message
                        });
                    }

                    reject(new Error('Erro ao processar resposta: ' + error.message));
                }
            };

            xhr.onerror = function () {
                var errorMsg = 'Erro de rede: ' + url;

                if (window.eventBus) {
                    window.eventBus.emit('api:request:erro', {
                        method: method,
                        endpoint: endpoint,
                        error: errorMsg
                    });
                }

                reject(new Error(errorMsg));
            };

            xhr.ontimeout = function () {
                var errorMsg = 'Timeout: ' + url;

                if (window.eventBus) {
                    window.eventBus.emit('api:request:timeout', {
                        method: method,
                        endpoint: endpoint,
                        timeout: self.timeout
                    });
                }

                reject(new Error(errorMsg));
            };

            // Enviar
            if (data && (method === 'POST' || method === 'PUT')) {
                xhr.send(JSON.stringify(data));
            } else {
                xhr.send();
            }

        } catch (error) {
            if (window.eventBus) {
                window.eventBus.emit('api:request:erro', {
                    method: method,
                    endpoint: endpoint,
                    error: 'Erro na requisição: ' + error.message
                });
            }

            reject(new Error('Erro na requisição: ' + error.message));
        }
    });
};

// Métodos HTTP mantidos
ApiClient.prototype.get = function (endpoint, params, useCache) {
    return this.request('GET', endpoint, null, params, useCache);
};

ApiClient.prototype.post = function (endpoint, data) {
    return this.request('POST', endpoint, data);
};

// ============================================
// MÉTODOS ESPECÍFICOS (integrados com Event Bus)
// ============================================

ApiClient.prototype.contarSelos = function () {
    console.log('🧮 [API] Contando selos...');

    // Emitir evento específico
    if (window.eventBus) {
        window.eventBus.emit('selos:contagem:iniciada');
    }

    return this.get('/selos/contar', null, true) // Cache ativado
        .then(function (response) {
            // Notificar sucesso
            if (window.eventBus) {
                window.eventBus.emit('selos:contagem:concluida', response);
            }
            return response;
        })
        .catch(function (error) {
            // Notificar erro
            if (window.eventBus) {
                window.eventBus.emit('selos:contagem:erro', error);
            }
            throw error;
        });
};

ApiClient.prototype.calcularSelosNecessarios = function (parametros) {
    console.log('🧮 [API] Calculando selos...', parametros);

    if (window.eventBus) {
        window.eventBus.emit('selos:calculo:iniciado', parametros);
    }

    return this.post('/selos/calcular', parametros)
        .then(function (response) {
            if (window.eventBus) {
                window.eventBus.emit('selos:calculo:concluido', response);
            }
            return response;
        })
        .catch(function (error) {
            if (window.eventBus) {
                window.eventBus.emit('selos:calculo:erro', error);
            }
            throw error;
        });
};

ApiClient.prototype.buscarIntervaloApontamentos = function (data, codigoOperacao) {
    console.log('🔍 [API] Buscando intervalo...', { data: data, codigoOperacao: codigoOperacao });

    if (window.eventBus) {
        window.eventBus.emit('apontamentos:busca:iniciada', { data: data, codigoOperacao: codigoOperacao });
    }

    return this.get('/apontamentos/intervalo', {
        data: data,
        codigoOperacao: codigoOperacao
    }, true) // Cache ativado
        .then(function (response) {
            if (window.eventBus) {
                window.eventBus.emit('apontamentos:busca:concluida', response);
            }
            return response;
        })
        .catch(function (error) {
            if (window.eventBus) {
                window.eventBus.emit('apontamentos:busca:erro', error);
            }
            throw error;
        });
};

ApiClient.prototype.processarSelagemCompleta = function (dadosSelagem) {
    console.log('🏁 [API] Processando selagem...', JSON.stringify(dadosSelagem, null, 2));

    if (window.eventBus) {
        window.eventBus.emit('selagem:processamento:iniciado', dadosSelagem);
    }

    return this.post('/selagem', dadosSelagem)
        .then(function (response) {
            console.log('✅ [API] Selagem bem-sucedida:', response);

            if (window.eventBus) {
                window.eventBus.emit('selagem:processamento:concluido', response);
            }
            return response;
        })
        .catch(function (error) {
            console.error('❌ [API] Erro na selagem:', error);

            // Log detalhado do erro
            if (error.response) {
                console.error('📋 [API] Resposta de erro CRUA:', error.response);

                // Tentar extrair a mensagem completa
                try {
                    // A resposta parece estar em formato JSON aninhado
                    var erroJson = typeof error.response === 'string' ? JSON.parse(error.response) : error.response;
                    console.error('📋 [API] Erro parseado:', erroJson);

                    // Se houver mensagem aninhada
                    if (erroJson.mensagem && typeof erroJson.mensagem === 'string') {
                        try {
                            var mensagemAninhada = JSON.parse(erroJson.mensagem);
                            console.error('📋 [API] Mensagem aninhada:', mensagemAninhada);
                        } catch (e) {
                            console.error('📋 [API] Mensagem direta:', erroJson.mensagem);
                        }
                    }
                } catch (parseError) {
                    console.error('📋 [API] Não foi possível parsear resposta:', parseError);
                    console.error('📋 [API] Resposta original:', typeof error.response, error.response);
                }
            }

            if (window.eventBus) {
                window.eventBus.emit('selagem:processamento:erro', error);
            }
            throw error;
        });
};

// ============================================
// INICIALIZAÇÃO COM INTEGRAÇÃO
// ============================================

if (typeof window !== 'undefined') {
    // Registrar construtor
    window.ApiClient = ApiClient;

    // Criar instância padrão
    try {
        window.api = new ApiClient();
        console.log('✅ ApiClient REFATORADO inicializado');

        // Configurar listeners do Event Bus para API
        if (window.eventBus) {
            // Limpar cache quando necessário
            window.eventBus.on('cache:limpar', function () {
                window.api.cache = {};
                console.log('🧹 Cache da API limpo');
            });

            // Limpar cache quando selos são atualizados
            window.eventBus.on('selos:atualizados', function () {
                // Invalida cache relacionado a selos
                for (var key in window.api.cache) {
                    if (key.includes('/selos/')) {
                        delete window.api.cache[key];
                    }
                }
                console.log('🧹 Cache de selos invalidado');
            });
        }

    } catch (error) {
        console.error('❌ Erro ao inicializar ApiClient:', error);
        window.api = criarAPIFallback();
    }
}

function criarAPIFallback() {
    return {
        request: function () {
            return Promise.reject(new Error('API não disponível'));
        },
        contarSelos: function () {
            return Promise.reject(new Error('API não disponível'));
        }
    };
}

console.log('✅ api-client.js REFATORADO - Nova arquitetura com Event Bus');