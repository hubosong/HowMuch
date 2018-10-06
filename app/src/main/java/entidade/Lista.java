package entidade;

import java.io.Serializable;
import java.util.ArrayList;

public class Lista implements Serializable {

    private String nome;
    private long id_lista;
    private long id_usuario;
    private String data;
    private ArrayList<Produto> listaProdutos;
    private Mercado mercado;
    private float valor_total;

    private int transient_qtd_produtos;

    public Lista() {
        listaProdutos = new ArrayList<>();
    }

    public Mercado getMercado() {
        if (mercado == null) {
            mercado = new Mercado();
        }
        return mercado;
    }

    public void setMercado(Mercado mercado) {
        this.mercado = mercado;
    }

    public float getValor_total() {
        return valor_total;
    }

    public void setValor_total(float valor_total) {
        this.valor_total = valor_total;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public long getId_lista() {
        return id_lista;
    }

    public void setId_lista(long id_lista) {
        this.id_lista = id_lista;
    }

    public long getId_usuario() {
        return id_usuario;
    }

    public void setId_usuario(long id_usuario) {
        this.id_usuario = id_usuario;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public ArrayList<Produto> getListaProdutos() {
        return listaProdutos;
    }

    public void setListaProdutos(ArrayList<Produto> listaProdutos) {
        this.listaProdutos = listaProdutos;
    }

    public int getTransient_qtd_produtos() {
        return transient_qtd_produtos;
    }

    public void setTransient_qtd_produtos(int transient_qtd_produtos) {
        this.transient_qtd_produtos = transient_qtd_produtos;
    }

}
