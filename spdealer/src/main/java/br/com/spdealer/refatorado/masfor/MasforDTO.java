package br.com.spdealer.refatorado.masfor;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class MasforDTO {

    private Long id;

    @NotBlank
    @Size(max = 30)
    private String codigo;

    @NotBlank
    @Size(max = 200)
    private String descricao;

    private String ativo = "S";

    private Integer idFil;

    public MasforDTO() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getCodigo() { return codigo; }
    public void setCodigo(String codigo) { this.codigo = codigo; }

    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }

    public String getAtivo() { return ativo; }
    public void setAtivo(String ativo) { this.ativo = ativo; }

    public Integer getIdFil() { return idFil; }
    public void setIdFil(Integer idFil) { this.idFil = idFil; }
}
