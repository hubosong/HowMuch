package entidade;

import java.util.Date;

public class Preco_Atual {

    private Produto produto;
    private Mercado mercado;
    private float valor_atual;
    private Date data;

    @Override
    public String toString() {
        return "Preco_Atual{" + "produto=" + produto + ", mercado=" + mercado + ", valor_atual=" + valor_atual + ", data=" + data + '}';
    }

    public Produto getProduto() {
        return produto;
    }

    public void setProduto(Produto produto) {
        this.produto = produto;
    }

    public Mercado getMercado() {
        return mercado;
    }

    public void setMercado(Mercado mercado) {
        this.mercado = mercado;
    }

    public float getValor_atual() {
        return valor_atual;
    }

    public void setValor_atual(float valor_atual) {
        this.valor_atual = valor_atual;
    }

    public Date getData() {
        return data;
    }

    public void setData(Date data) {
        this.data = data;
    }

}
