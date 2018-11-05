package adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DecimalFormat;
import java.util.List;

import entidade.NFe;
import robsonmachczew.activities.R;
import robsonmachczew.activities.VerNFe;

public class NFeAdapter extends RecyclerView.Adapter<NFeAdapter.ProductViewHolder> {

    private Context context;
    private List<NFe> lista_nfes;

    //getting the context and product list with constructor
    public NFeAdapter(Context context, List<NFe> productList) {
        this.context = context;
        this.lista_nfes = productList;
    }

    @Override
    public ProductViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.layout_products, null);
        return new ProductViewHolder(view);
    }

    class ProductViewHolder extends RecyclerView.ViewHolder {
        TextView txtTitle, txtMarket, txtDate, txtMediumPrice, txtOff, txtPrice, txtOption, txtDescOff, titleItens, txtPriceDescription;
        //ImageView imageView;

        public ProductViewHolder(View itemView) {
            super(itemView);
            txtTitle = itemView.findViewById(R.id.txtNomeLista);
            txtMarket = itemView.findViewById(R.id.txtDataLista);
            txtDate = itemView.findViewById(R.id.txtQtdItems);
            txtMediumPrice = itemView.findViewById(R.id.txtMediumPrice);
            txtOff = itemView.findViewById(R.id.txtOff);
            txtPrice = itemView.findViewById(R.id.txtPrice);
            txtOption = itemView.findViewById(R.id.txtOptions);
            titleItens = itemView.findViewById(R.id.txtMediumPriceDescription);
            txtPriceDescription = itemView.findViewById(R.id.txtPriceDescription);
            //imageView = itemView.findViewById(R.id.imageView);

            txtDescOff = itemView.findViewById(R.id.txtOffDescription);

        }
    }

    @Override
    public int getItemCount() {
        return lista_nfes.size();
    }

    @Override
    public void onBindViewHolder(final ProductViewHolder holder, final int position) {
        final NFe nfe = lista_nfes.get(position);

        //convert double to R$
        DecimalFormat decFormat = new DecimalFormat("'R$ ' #,##0.00");

        holder.txtTitle.setText(nfe.getMercado().getNome());
        holder.txtMarket.setText(nfe.getChave());
        holder.txtDate.setText("Data: " + nfe.getData());
        holder.txtMediumPrice.setText(String.valueOf(nfe.getLista_items().size()));
        holder.txtOff.setText("");
        holder.txtDescOff.setText("");
        holder.txtPrice.setTextColor(Color.parseColor("#34a503"));
        holder.txtPrice.setText(String.valueOf(nfe.getValor()));
        holder.titleItens.setText("Quantidade de Itens:");
        holder.txtPriceDescription.setText("Valor da NFe:");

        //A imagem ainda não tá implementada na classe ProdutoAbaixoMedia...
        //holder.imageView.setImageDrawable(mCtx.getResources().getDrawable(product.getImage()));

        holder.txtOption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(mCtx, "teste", Toast.LENGTH_SHORT).show();
                PopupMenu popupMenu = new PopupMenu(context, holder.txtOption);
                popupMenu.inflate(R.menu.navlist);
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.option1:
                                //Detalhes da NFe
                                Intent intent = new Intent(context, VerNFe.class);
                                intent.putExtra("NFE", nfe);
                                context.startActivity(intent);
                                break;
                            case R.id.option2:
                                //Excluir NFe
                                Toast.makeText(context, "Item adicionado", Toast.LENGTH_SHORT).show();
                                break;
                            case R.id.option3:
                                //Transformar em Lista de Compras
                                Toast.makeText(context, "Lista acessada!", Toast.LENGTH_SHORT).show();
                                break;
                        }

                        return false;
                    }
                });
                popupMenu.show();
            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, VerNFe.class);
                intent.putExtra("NFE", nfe);
                context.startActivity(intent);
            }
        });
    }

    public void remove(int position) {
        lista_nfes.remove(position);
        notifyItemRemoved(position);
    }
}
