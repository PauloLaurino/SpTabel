// ============================================
// CUSTAS API CLIENT - Sistema de Cálculo de Custas Cartoriais
// Integrado com Event Bus para notificações
// ============================================

function CustasApiClient(baseUrl) {
    this.baseUrl = baseUrl || '/sptabel/api';
    this.timeout = 30000;
    this.defaultHeaders = {
        'Content-Type': 'application/json',
        'Accept': 'application/json'
    };
    
    // Cache simples para evitar requests duplicados
    this.cache = {};
}

// ============================================
// SISTEMA DE CACHE
// ============================================

CustasApiClient.prototype.getCacheKey = function(method, endpoint, params) {
    var key = method + ':' + endpoint;
    if (params) {
        key += ':' + JSON.stringify(params);
    }
    return key;
};

CustasApiClient.prototype.getFromCache = function(key) {
    if (!this.cache[key]) return null;
    
    var cached = this.cache[key];
    var now = Date.now();
    
    // Cache válido por 5 minutos
    if (now - cached.timestamp > 300000) {
        delete this.cache[key];
        return null;
    }
    
    return cached.data;
};

CustasApiClient.prototype.setCache = function(key, data) {
    this.cache[key] = {
        data: data,
        timestamp: Date.now()
    };
};

// ============================================
// MÉTODO REQUEST
// ============================================

CustasApiClient.prototype.request = function(method, endpoint, data, params, useCache) {
    var self = this;
    
    // Usar cache para GET
    if (method === 'GET' && useCache !== false) {
        var cacheKey = this.getCacheKey(method, endpoint, params);
        var cached = this.getFromCache(cacheKey);
        if (cached) {
            console.log('💾 [CUSTAS API] Cache hit:', endpoint);
            return Promise.resolve(cached);
        }
    }
    
    // Emitir evento de início
    if (window.eventBus) {
        window.eventBus.emit('custas:request:iniciado', {
            method: method,
            endpoint: endpoint,
            timestamp: new Date().toISOString()
        });
    }
    
    return new Promise(function(resolve, reject) {
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
            xhr.onload = function() {
                try {
                    var responseText = xhr.responseText;
                    
                    console.log('📡 [CUSTAS API] Resposta:', {
                        status: xhr.status,
                        endpoint: endpoint,
                        length: responseText.length
                    });
                    
                    if (xhr.status >= 200 && xhr.status < 300) {
                        var jsonResponse = responseText ? JSON.parse(responseText) : {};
                        
                        // Armazenar no cache para GET
                        if (method === 'GET' && useCache !== false) {
                            var cacheKey = self.getCacheKey(method, endpoint, params);
                            self.setCache(cacheKey, jsonResponse);
                        }
                        
                        // Emitir evento de sucesso
                        if (window.eventBus) {
                            window.eventBus.emit('custas:request:sucesso', {
                                method: method,
                                endpoint: endpoint,
                                status: xhr.status,
                                data: jsonResponse
                            });
                        }
                        
                        resolve(jsonResponse);
                    } else {
                        var errorResponse = responseText ? JSON.parse(responseText) : {};
                        var errorMsg = 'HTTP ' + xhr.status + ': ' + (errorResponse.message || xhr.statusText);
                        
                        // Emitir evento de erro
                        if (window.eventBus) {
                            window.eventBus.emit('custas:request:erro', {
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
                    reject(new Error('Erro ao processar resposta: ' + error.message));
                }
            };
            
            xhr.onerror = function() {
                reject(new Error('Erro de rede: ' + url));
            };
            
            xhr.ontimeout = function() {
                reject(new Error('Timeout: ' + url));
            };
            
            // Enviar
            if (data && (method === 'POST' || method === 'PUT')) {
                xhr.send(JSON.stringify(data));
            } else {
                xhr.send();
            }
            
        } catch (error) {
            reject(new Error('Erro na requisição: ' + error.message));
        }
    });
};

// Métodos HTTP
CustasApiClient.prototype.get = function(endpoint, params, useCache) {
    return this.request('GET', endpoint, null, params, useCache);
};

CustasApiClient.prototype.post = function(endpoint, data) {
    return this.request('POST', endpoint, data);
};

// ============================================
// MÉTODOS ESPECÍFICOS DO MÓDULO DE CUSTAS
// ============================================

/**
 * Lista tipos de atos disponíveis
 */
CustasApiClient.prototype.listarTiposAtos = function(modulo) {
    console.log('📋 [CUSTAS API] Listando tipos de atos, módulo: ' + modulo);
    
    if (window.eventBus) {
        window.eventBus.emit('custas:atos:listagem:iniciada', { modulo: modulo });
    }
    
    return this.get('/custas', { modulo: modulo }, true)
        .then(function(response) {
            if (window.eventBus) {
                window.eventBus.emit('custas:atos:listagem:concluida', response);
            }
            return response;
        })
        .catch(function(error) {
            if (window.eventBus) {
                window.eventBus.emit('custas:atos:listagem:erro', error);
            }
            throw error;
        });
};

/**
 * Lista atos por módulo (N=Notas, R=Registro, O=Outros)
 */
CustasApiClient.prototype.listarAtosPorModulo = function(modulo) {
    console.log('📋 [CUSTAS API] Listando atos por módulo: ' + modulo);
    
    return this.get('/custas/atos', { modulo: modulo }, true)
        .then(function(response) {
            return response;
        })
        .catch(function(error) {
            console.error('❌ [CUSTAS API] Erro ao listar atos por módulo:', error);
            throw error;
        });
};

/**
 * Lista encargos (FUNDEP, ISS, FUNREJUS, SELOS, Distribuição)
 */
CustasApiClient.prototype.listarEncargos = function() {
    console.log('💰 [CUSTAS API] Listando encargos');
    
    if (window.eventBus) {
        window.eventBus.emit('custas:encargos:listagem:iniciada');
    }
    
    return this.get('/custas/encargos', null, true)
        .then(function(response) {
            if (window.eventBus) {
                window.eventBus.emit('custas:encargos:listagem:concluida', response);
            }
            return response;
        })
        .catch(function(error) {
            if (window.eventBus) {
                window.eventBus.emit('custas:encargos:listagem:erro', error);
            }
            throw error;
        });
};

/**
 * Lista protocolos disponíveis
 */
CustasApiClient.prototype.listarProtocolos = function() {
    console.log('📋 [CUSTAS API] Listando protocolos');
    
    return this.get('/custas/protocolos', null, true)
        .then(function(response) {
            console.log('✅ [CUSTAS API] Protocolos carregados:', response);
            return response;
        })
        .catch(function(error) {
            console.error('❌ [CUSTAS API] Erro ao listar protocolos:', error);
            throw error;
        });
};

/**
 * Lista imóveis por protocolo
 */
CustasApiClient.prototype.listarImoveisPorProtocolo = function(protocolo) {
    console.log('🏠 [CUSTAS API] Listando imóveis para protocolo: ' + protocolo);
    
    return this.get('/custas/imoveis-protocolo', { protocolo: protocolo }, true)
        .then(function(response) {
            console.log('✅ [CUSTAS API] Imóveis carregados:', response);
            return response;
        })
        .catch(function(error) {
            console.error('❌ [CUSTAS API] Erro ao listar imóveis por protocolo:', error);
            throw error;
        });
};

/**
 * Calcula custas para um ato cartorial
 */
CustasApiClient.prototype.calcularCustas = function(dados) {
    console.log('🧮 [CUSTAS API] Calculando custas:', dados);
    
    if (window.eventBus) {
        window.eventBus.emit('custas:calculo:iniciado', dados);
    }
    
    return this.post('/custas/calcular', dados)
        .then(function(response) {
            console.log('✅ [CUSTAS API] Cálculo concluído:', response);
            
            if (window.eventBus) {
                window.eventBus.emit('custas:calculo:concluido', response);
            }
            return response;
        })
        .catch(function(error) {
            console.error('❌ [CUSTAS API] Erro no cálculo:', error);
            
            if (window.eventBus) {
                window.eventBus.emit('custas:calculo:erro', error);
            }
            throw error;
        });
};

/**
 * Busca atos por descrição
 */
CustasApiClient.prototype.buscarAtosPorDescricao = function(search) {
    console.log('🔍 [CUSTAS API] Buscando atos por: ' + search);
    
    return this.get('/custas/tipos', { search: search }, true)
        .then(function(response) {
            return response;
        })
        .catch(function(error) {
            console.error('❌ [CUSTAS API] Erro na busca:', error);
            throw error;
        });
};

// ============================================
// INICIALIZAÇÃO
// ============================================

if (typeof window !== 'undefined') {
    // Registrar construtor
    window.CustasApiClient = CustasApiClient;
    
    // Criar instância padrão
    try {
        window.custasApi = new CustasApiClient();
        console.log('✅ CustasApiClient inicializado');
        
        // Configurar listeners do Event Bus
        if (window.eventBus) {
            // Limpar cache quando necessário
            window.eventBus.on('cache:limpar', function() {
                window.custasApi.cache = {};
                console.log('🧹 Cache de custas limpo');
            });
        }
        
    } catch (error) {
        console.error('❌ Erro ao inicializar CustasApiClient:', error);
        window.custasApi = criarCustasAPIFallback();
    }
}

function criarCustasAPIFallback() {
    return {
        request: function() {
            return Promise.reject(new Error('API de custas não disponível'));
        },
        listarTiposAtos: function() {
            return Promise.reject(new Error('API de custas não disponível'));
        },
        listarProtocolos: function() {
            return Promise.reject(new Error('API de custas não disponível'));
        },
        listarImoveisPorProtocolo: function() {
            return Promise.reject(new Error('API de custas não disponível'));
        },
        calcularCustas: function() {
            return Promise.reject(new Error('API de custas não disponível'));
        }
    };
}

console.log('✅ custas-api-client.js - Sistema de Cálculo de Custas Cartoriais');
