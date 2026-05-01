package com.seprocom.protesto.chat.entity;

import lombok.*;

import java.io.Serializable;

/**
 * Classe de chave composta para a entidade Ctp001.
 * 
 * Composta por: numapo1_001 + numapo2_001 + controle_001
 * 
 * @author Seprocom
 * @version 1.0.0
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class Ctp001Id implements Serializable {

    private static final long serialVersionUID = 1L;

    private String numapo1_001;
    private String numapo2_001;
    private String controle_001;
}
