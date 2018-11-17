package entidade;

import java.io.Serializable;

public class ProdutoAbaixoMedia implements Serializable {

    private long id_produto;
    private String descricao_produto;
    private String descricao_produto2;
    private float valor_medio;
    private float valor;
    private String unidade_comercial;
    private long id_mercado;
    private String nome_mercado;
    private String data;

    public Produto converterEmProduto() {
        Produto p = new Produto();
        p.setId_produto(id_produto);
        p.setDescricao(descricao_produto);
        p.setDescricao2(descricao_produto2);
        p.setUnidade_comercial(unidade_comercial);
        return p;
    }

    public String getDescricao_produto2() {
        return descricao_produto2;
    }

    public void setDescricao_produto2(String descricao_produto2) {
        this.descricao_produto2 = descricao_produto2;
    }

    public long getId_produto() {
        return id_produto;
    }

    public void setId_produto(long id_produto) {
        this.id_produto = id_produto;
    }

    public String getDescricao_produto() {
        return descricao_produto;
    }

    public void setDescricao_produto(String descricao_produto) {
        this.descricao_produto = descricao_produto;
    }

    public float getValor_medio() {
        return valor_medio;
    }

    public void setValor_medio(float valor_medio) {
        this.valor_medio = valor_medio;
    }

    public float getValor() {
        return valor;
    }

    public void setValor(float valor) {
        this.valor = valor;
    }

    public String getUnidade_comercial() {
        return unidade_comercial;
    }

    public void setUnidade_comercial(String unidade_comercial) {
        this.unidade_comercial = unidade_comercial;
    }

    public long getId_mercado() {
        return id_mercado;
    }

    public void setId_mercado(long id_mercado) {
        this.id_mercado = id_mercado;
    }

    public String getNome_mercado() {
        return nome_mercado;
    }

    public void setNome_mercado(String nome_mercado) {
        this.nome_mercado = nome_mercado;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

}
