package entidade;

import java.io.Serializable;

public class Item_NFe implements Serializable {

    private long id_nfe;
    private Produto produto;
    private float valor;
    private float quantidade;
    private String data;

    //Transient Fields;
    private Mercado transient_mercado;

    @Override
    public String toString() {
        return "Item_NFe{" + "id_nfe=" + id_nfe + ", produto=" + produto + ", valor=" + valor + ", quantidade=" + quantidade + '}';
    }

    public String getPrecoUnitario() {
        return String.format("%.02f", valor / quantidade);
    }

    public Mercado getTransient_mercado() {
        return transient_mercado;
    }

    public void setTransient_mercado(Mercado transient_mercado) {
        this.transient_mercado = transient_mercado;
    }

    public long getId_nfe() {
        return id_nfe;
    }

    public void setId_nfe(long id_nfe) {
        this.id_nfe = id_nfe;
    }

    public Produto getProduto() {
        return produto;
    }

    public void setProduto(Produto produto) {
        this.produto = produto;
    }

    public float getValor() {
        return valor;
    }

    public void setValor(float valor) {
        this.valor = valor;
    }

    public float getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(float quantidade) {
        this.quantidade = quantidade;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

}
