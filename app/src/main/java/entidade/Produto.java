package entidade;

import java.io.Serializable;

public class Produto implements Serializable {

    private long id_produto;
    private long codigo_do_produto;
    private long codigo_ncm;
    private String descricao;
    private String descricao2;
    private String unidade_comercial;
    private long cod_EAN_comercial;
    private boolean auditado;
    private boolean imagem;

    private float transient_quantidade;
    private float transient_valor;
    private String transient_data;

    @Override
    public String toString() {
        return "Produto{" + "id_produto=" + id_produto + ", codigo_do_produto=" + codigo_do_produto + ", codigo_ncm=" + codigo_ncm + ", descricao=" + descricao + ", unidade_comercial=" + unidade_comercial + ", cod_EAN_comercial=" + cod_EAN_comercial + '}';
    }

    public String getDescricao2() {
        return descricao2;
    }

    public void setDescricao2(String descricao2) {
        this.descricao2 = descricao2;
    }

    public boolean isAuditado() {
        return auditado;
    }

    public void setAuditado(boolean auditado) {
        this.auditado = auditado;
    }

    public boolean isImagem() {
        return imagem;
    }

    public void setImagem(boolean imagem) {
        this.imagem = imagem;
    }

    public long getCod_EAN_comercial() {
        return cod_EAN_comercial;
    }

    public void setCod_EAN_comercial(long cod_EAN_comercial) {
        this.cod_EAN_comercial = cod_EAN_comercial;
    }

    public long getId_produto() {
        return id_produto;
    }

    public void setId_produto(long id_produto) {
        this.id_produto = id_produto;
    }

    public long getCodigo_do_produto() {
        return codigo_do_produto;
    }

    public void setCodigo_do_produto(long codigo_do_produto) {
        this.codigo_do_produto = codigo_do_produto;
    }

    public long getCodigo_ncm() {
        return codigo_ncm;
    }

    public void setCodigo_ncm(long codigo_ncm) {
        this.codigo_ncm = codigo_ncm;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getUnidade_comercial() {
        return unidade_comercial;
    }

    public void setUnidade_comercial(String unidade_comercial) {
        this.unidade_comercial = unidade_comercial;
    }

    public float getTransient_quantidade() {
        return transient_quantidade;
    }

    public void setTransient_quantidade(float transient_quantidade) {
        this.transient_quantidade = transient_quantidade;
    }

    public float getTransient_valor() {
        return transient_valor;
    }

    public void setTransient_valor(float transient_valor) {
        this.transient_valor = transient_valor;
    }

    public String getTransient_data() {
        return transient_data;
    }

    public void setTransient_data(String transient_data) {
        this.transient_data = transient_data;
    }

}
