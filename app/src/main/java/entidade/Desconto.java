package entidade;

public class Desconto {

    private Produto produto;
    private Mercado mercado;
    private float valor;
    private float valor_medio;
    private String data;

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

    public float getValor() {
        return valor;
    }

    public void setValor(float valor) {
        this.valor = valor;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public float getValor_medio() {
        return valor_medio;
    }

    public void setValor_medio(float valor_medio) {
        this.valor_medio = valor_medio;
    }

}
