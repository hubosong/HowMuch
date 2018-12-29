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

import entidade.Item_NFe;
import robsonmachczew.activities.R;
import robsonmachczew.activities.VerProduto;

public class Item_NFeAdapter extends RecyclerView.Adapter<Item_NFeAdapter.ProductViewHolder> {

    private Context mCtx;
    private List<Item_NFe> productList;

    //getting the context and product list with constructor
    public Item_NFeAdapter(Context mCtx, List<Item_NFe> productList) {
        this.mCtx = mCtx;
        this.productList = productList;
    }

    @Override
    public ProductViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mCtx);
        View view = inflater.inflate(R.layout.layout_products, null);
        return new ProductViewHolder(view);
    }

    class ProductViewHolder extends RecyclerView.ViewHolder {
        TextView txtTitle, txtMarket, txtDate, txtMediumPriceDescription, txtMediumPrice,  txtOff, txtPrice, txtOption, txtDescOff;
        //ImageView imageView;

        public ProductViewHolder(View itemView) {
            super(itemView);
            txtTitle = itemView.findViewById(R.id.txtNomeLista);
            txtMarket = itemView.findViewById(R.id.txtDataLista);
            txtDate = itemView.findViewById(R.id.txtQtdItems);
            txtMediumPriceDescription = itemView.findViewById(R.id.txtMediumPriceDescription);
            txtMediumPrice = itemView.findViewById(R.id.txtMediumPrice);
            txtOff = itemView.findViewById(R.id.txtOff);
            txtPrice = itemView.findViewById(R.id.txtPrice);
            txtOption = itemView.findViewById(R.id.txtOptions);
            //imageView = itemView.findViewById(R.id.imageView);

            txtDescOff = itemView.findViewById(R.id.txtOffDescription);

        }
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    @Override
    public void onBindViewHolder(final ProductViewHolder holder, final int position) {
        final Item_NFe item_nfe = productList.get(position);

        //convert double to R$
        DecimalFormat decFormat = new DecimalFormat("'R$ ' #,##0.00");

        holder.txtTitle.setText(item_nfe.getProduto().getDescricao());
        holder.txtMarket.setText(item_nfe.getTransient_mercado().getNome());
        holder.txtDate.setText(item_nfe.getData());
        holder.txtMediumPriceDescription.setText("");
        holder.txtMediumPrice.setText("");
        holder.txtOff.setText("");
        holder.txtDescOff.setText("");
        holder.txtPrice.setTextColor(Color.parseColor("#34a503"));
        holder.txtPrice.setText(String.valueOf(decFormat.format(item_nfe.getValor() / item_nfe.getQuantidade())) + " ("+item_nfe.getProduto().getUnidade_comercial()+")");



        holder.txtOption.setText("+");
        holder.txtOption.setBackgroundResource(R.drawable.ic_add);
        holder.txtOption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(mCtx, "item adicionado", Toast.LENGTH_SHORT).show();            }
        });

        /*
        holder.txtOption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(mCtx, "teste", Toast.LENGTH_SHORT).show();
                PopupMenu popupMenu = new PopupMenu(mCtx, holder.txtOption);
                popupMenu.inflate(R.menu.navlist);
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()){
                            case R.id.option1:
                                Intent intent = new Intent(mCtx, VerProduto.class);
                                intent.putExtra("ITEM", item_nfe);
                                mCtx.startActivity(intent);
                                break;
                            case R.id.option2:
                                Toast.makeText(mCtx, "Item adicionado", Toast.LENGTH_SHORT).show();
                                break;
                            case R.id.option3:

                                Toast.makeText(mCtx, "Lista acessada!123456", Toast.LENGTH_SHORT).show();
                                break;
                        }

                        return false;
                    }
                });
                popupMenu.show();
            }
        });

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Toast.makeText(mCtx, "Produto: " + item_nfe, Toast.LENGTH_SHORT).show();
                remove(position);
                return false;
            }
        });
        */
    }

    public void remove(int position) {
        productList.remove(position);
        notifyItemRemoved(position);
    }
}
