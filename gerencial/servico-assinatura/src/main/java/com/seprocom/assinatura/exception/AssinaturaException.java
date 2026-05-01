package com.seprocom.assinatura.exception;

public class AssinaturaException extends RuntimeException {
    
    private final String codigoErro;

    public AssinaturaException(String codigoErro, String mensagem) {
        super(mensagem);
        this.codigoErro = codigoErro;
    }

    public AssinaturaException(String codigoErro, String mensagem, Throwable causa) {
        super(mensagem, causa);
        this.codigoErro = codigoErro;
    }

    public String getCodigoErro() {
        return codigoErro;
    }
}
