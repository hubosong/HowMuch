package entidade;

import java.io.Serializable;
import java.util.ArrayList;

public class Produto_Detalhado extends Produto implements Serializable {

    private ArrayList<Item_NFe> lista_itens_nfe;

    public Produto_Detalhado() {
        lista_itens_nfe = new ArrayList<>();
    }

    public ArrayList<Item_NFe> getLista_itens_nfe() {
        return lista_itens_nfe;
    }

    public void setLista_itens_nfe(ArrayList<Item_NFe> lista_itens_nfe) {
        this.lista_itens_nfe = lista_itens_nfe;
    }

}