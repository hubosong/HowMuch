package adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import entidade.Lista;
import robsonmachczew.activities.R;

public class ListaAdapter extends RecyclerView.Adapter<ListaAdapter.ProductViewHolder> {

    private Context mCtx;
    private List<Lista> listas;

    //getting the context and product list with constructor
    public ListaAdapter(Context mCtx, List<Lista> productList) {
        this.mCtx = mCtx;
        this.listas = productList;
    }

    @Override
    public ProductViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mCtx);
        View view = inflater.inflate(R.layout.layout_lista_de_listas, null);
        return new ProductViewHolder(view);
    }

    class ProductViewHolder extends RecyclerView.ViewHolder {
        TextView txtNomeLista, txtDataLista, txtQtdItems;
        //ImageView imageView;

        public ProductViewHolder(View itemView) {
            super(itemView);
            txtNomeLista = itemView.findViewById(R.id.txtNomeLista);
            txtDataLista = itemView.findViewById(R.id.txtDataLista);
            txtQtdItems = itemView.findViewById(R.id.txtQtdItems);
        }
    }

    @Override
    public int getItemCount() {
        return listas.size();
    }

    @Override
    public void onBindViewHolder(final ProductViewHolder holder, final int position) {
        final Lista lista = listas.get(position);

        holder.txtNomeLista.setText(lista.getNome());
        holder.txtQtdItems.setText(String.valueOf(lista.getTransient_qtd_produtos()) + " Produtos");
        holder.txtDataLista.setText(lista.getData());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(mCtx);
                TextView title = new TextView(mCtx);
                title.setText("Opções da Lista");
                title.setBackgroundColor(ContextCompat.getColor(mCtx, R.color.toolbar_status));
                title.setPadding(10, 10, 10, 10);
                title.setGravity(Gravity.CENTER);
                title.setTextColor(Color.WHITE);
                title.setTextSize(20);
                dialog.setCustomTitle(title);
                dialog.setNeutralButton("Comparar em Mercados", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
                dialog.setNegativeButton("Excluir Lista", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
                dialog.setPositiveButton("Editar Lista", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
                AlertDialog alert = dialog.create();
                alert.show();
            }
        });

    }

    public void remove(int position) {
        listas.remove(position);
        notifyItemRemoved(position);
    }
}
