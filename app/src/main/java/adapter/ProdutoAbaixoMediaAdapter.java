package adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DecimalFormat;
import java.util.List;

import robsonmachczew.howmuch.R;
import entidade.ProdutoAbaixoMedia;

public class ProdutoAbaixoMediaAdapter extends RecyclerView.Adapter<ProdutoAbaixoMediaAdapter.ProductViewHolder> {

    private Context mCtx;
    private List<ProdutoAbaixoMedia> productList;

    //getting the context and product list with constructor
    public ProdutoAbaixoMediaAdapter(Context mCtx, List<ProdutoAbaixoMedia> productList) {
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
        TextView txtTitle, txtMarket, txtDate, txtMediumPrice,  txtOff, txtPrice, txtOption;
        //ImageView imageView;

        public ProductViewHolder(View itemView) {
            super(itemView);
            txtTitle = itemView.findViewById(R.id.txtTitle);
            txtMarket = itemView.findViewById(R.id.txtMarket);
            txtDate = itemView.findViewById(R.id.txtDate);
            txtMediumPrice = itemView.findViewById(R.id.txtMediumPrice);
            txtOff = itemView.findViewById(R.id.txtOff);
            txtPrice = itemView.findViewById(R.id.txtPrice);
            txtOption = itemView.findViewById(R.id.txtOptions);
            //imageView = itemView.findViewById(R.id.imageView);

        }
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    @Override
    public void onBindViewHolder(final ProductViewHolder holder, final int position) {
        final ProdutoAbaixoMedia prodBelowAverage = productList.get(position);

        //convert double to R$
        DecimalFormat decFormat = new DecimalFormat("'R$ ' #,##0.00");

        if(prodBelowAverage.getValor() >= prodBelowAverage.getValor_medio()){
            holder.txtTitle.setText(prodBelowAverage.getDescricao_produto().toUpperCase());
            holder.txtMarket.setText(prodBelowAverage.getNome_mercado().toUpperCase());
            holder.txtDate.setText(prodBelowAverage.getData());
            holder.txtMediumPrice.setText(String.valueOf(decFormat.format(prodBelowAverage.getValor_medio())));
            holder.txtOff.setText(String.valueOf(decFormat.format(prodBelowAverage.getValor() - prodBelowAverage.getValor_medio())));
            holder.txtPrice.setTextColor(Color.parseColor("#fe0303"));
            holder.txtPrice.setText(String.valueOf(decFormat.format(prodBelowAverage.getValor())));


        } else {
            holder.txtTitle.setText(prodBelowAverage.getDescricao_produto().toUpperCase());
            holder.txtMarket.setText(prodBelowAverage.getNome_mercado().toUpperCase());
            holder.txtDate.setText(prodBelowAverage.getData());
            holder.txtMediumPrice.setText(String.valueOf(decFormat.format(prodBelowAverage.getValor_medio())));
            holder.txtOff.setText(String.valueOf(decFormat.format(prodBelowAverage.getValor() - prodBelowAverage.getValor_medio())));
            holder.txtPrice.setTextColor(Color.parseColor("#34a503"));
            holder.txtPrice.setText(String.valueOf(decFormat.format(prodBelowAverage.getValor())));



        }

        //A imagem ainda não tá implementada na classe ProdutoAbaixoMedia...
        //holder.imageView.setImageDrawable(mCtx.getResources().getDrawable(product.getImage()));

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
                                Toast.makeText(mCtx, "P.M. = Preço Médio" + "\n" + "P.A. =  Preço Atual", Toast.LENGTH_SHORT).show();
                                break;
                            case R.id.option2:
                                Toast.makeText(mCtx, "option2", Toast.LENGTH_SHORT).show();
                                break;
                            case R.id.option3:
                                Toast.makeText(mCtx, "option3", Toast.LENGTH_SHORT).show();
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
                Toast.makeText(mCtx, "Produto: " + prodBelowAverage, Toast.LENGTH_SHORT).show();
                remove(position);
                return false;
            }
        });
        /*
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(mCtx, "P.M. = Preço Médio" + "\n" + "P.A. =  Preço Atual", Toast.LENGTH_SHORT).show();
            }
        });
        */

    }

    public void remove(int position) {
        productList.remove(position);
        notifyItemRemoved(position);
    }
}
