package entidade;

import java.io.Serializable;
import java.util.ArrayList;

public class NFe implements Serializable {

    private long id_nfe;
    private long id_usuario;
    private Mercado mercado;
    private String chave;
    private String data;
    private String forma_pagamento;
    private String modelo;
    private String serie;
    private String numero;
    private float valor;
    private ArrayList<Item_NFe> lista_items;

    @Override
    public String toString() {
        return "NFe{" + "id_nfe=" + id_nfe + ", id_usuario=" + id_usuario + ", mercado=" + mercado + ", chave=" + chave + ", data=" + data + ", forma_pagamento=" + forma_pagamento + ", modelo=" + modelo + ", serie=" + serie + ", numero=" + numero + ", valor=" + valor + ", lista_items=" + lista_items + '}';
    }

    public long getId_usuario() {
        return id_usuario;
    }

    public void setId_usuario(long id_usuario) {
        this.id_usuario = id_usuario;
    }

    public float getValor() {
        return valor;
    }

    public void setValor(float valor) {
        this.valor = valor;
    }

    public String getModelo() {
        return modelo;
    }

    public void setModelo(String modelo) {
        this.modelo = modelo;
    }

    public String getSerie() {
        return serie;
    }

    public void setSerie(String serie) {
        this.serie = serie;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public long getId_nfe() {
        return id_nfe;
    }

    public void setId_nfe(long id_nfe) {
        this.id_nfe = id_nfe;
    }

    public Mercado getMercado() {
        return mercado;
    }

    public void setMercado(Mercado mercado) {
        this.mercado = mercado;
    }

    public String getChave() {
        return chave;
    }

    public void setChave(String chave) {
        this.chave = chave;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getForma_pagamento() {
        return forma_pagamento;
    }

    public void setForma_pagamento(String forma_pagamento) {
        this.forma_pagamento = forma_pagamento;
    }

    public ArrayList<Item_NFe> getLista_items() {
        if (lista_items == null) {
            //lista_items = new Item_NFe_DAO().getByIdNFe(id_nfe);
            lista_items = new ArrayList<>();
        }
        return lista_items;
    }

    public void setLista_items(ArrayList<Item_NFe> lista_items) {
        this.lista_items = lista_items;
    }

}
