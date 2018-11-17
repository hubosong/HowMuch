package adapter;

import android.content.Context;
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

import entidade.Produto;
import robsonmachczew.activities.R;

public class ProdutoAdapter extends RecyclerView.Adapter<ProdutoAdapter.ProductViewHolder> {

    //this context we will use to inflate the layout
    private Context mCtx;
    //we are storing all the products in a list
    private List<Produto> productList;
    //getting the context and product list with constructor
    public ProdutoAdapter(Context mCtx, List<Produto> productList) {
        this.mCtx = mCtx;
        this.productList = productList;
    }

    @Override
    public ProductViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //inflating and returning our view holder
        LayoutInflater inflater = LayoutInflater.from(mCtx);
        View view = inflater.inflate(R.layout.layout_products, null);
        return new ProductViewHolder(view);
    }

    class ProductViewHolder extends RecyclerView.ViewHolder {
        TextView txtTitle, txtMarket, txtDate, txtMediumPrice,  txtOff, txtPrice, txtOption, txtDescOff ;
        ImageView imageView;

        public ProductViewHolder(View itemView) {
            super(itemView);
            txtTitle = itemView.findViewById(R.id.txtNomeLista);
            txtMarket = itemView.findViewById(R.id.txtDataLista);
            txtDate = itemView.findViewById(R.id.txtQtdItems);
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

    /*
    public void add(int position, String product) {
        productList.add(position, product);
        notifyItemInserted(position);
    }
    */

    public void remove(int position) {
        productList.remove(position);
        notifyItemRemoved(position);
    }

    @Override
    public void onBindViewHolder(final ProductViewHolder holder, final int position) {
        //getting the productQRCode of the specified position
        final Produto produto = productList.get(position);

        //convert double to R$
        DecimalFormat decFormat = new DecimalFormat("'R$ ' #,##0.00");

        holder.txtOption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(mCtx, "teste", Toast.LENGTH_SHORT).show();
                PopupMenu popupMenu = new PopupMenu(mCtx, holder.txtOption);
                popupMenu.inflate(R.menu.navlist_produto);
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()){
                            case R.id.option1:
                                //Toast.makeText(mCtx, "P.M. = Preço Médio" + "\n" + "P.A. =  Preço Atual", Toast.LENGTH_SHORT).show();
                                Toast.makeText(mCtx, R.string.toast_off_options1, Toast.LENGTH_SHORT).show();
                                break;
                            case R.id.option2:
                                Toast.makeText(mCtx, "Item adicionado", Toast.LENGTH_SHORT).show();
                                break;
                            case R.id.option3:
                                Toast.makeText(mCtx, "Lista acessada!11", Toast.LENGTH_SHORT).show();
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
                Toast.makeText(mCtx, "Produto: " + produto, Toast.LENGTH_SHORT).show();
                remove(position);
                return false;
            }
        });

        //holder.imageView.setImageDrawable(mCtx.getResources().getDrawable(productQRCode.getImage()));

    }

}