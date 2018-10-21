package adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DecimalFormat;
import java.util.List;

import entidade.Item_NFe;
import robsonmachczew.activities.R;

public class ReadQRCodeAdapter extends RecyclerView.Adapter<ReadQRCodeAdapter.ProductViewHolder> {

    //this context we will use to inflate the layout
    private Context mCtx;
    //we are storing all the products in a list
    private List<Item_NFe> productList;

    //getting the context and product list with constructor
    public ReadQRCodeAdapter(Context mCtx, List<Item_NFe> productList) {
        this.mCtx = mCtx;
        this.productList = productList;
    }

    @Override
    public ProductViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //inflating and returning our view holder
        LayoutInflater inflater = LayoutInflater.from(mCtx);
        View view = inflater.inflate(R.layout.layout_qrcode_products, null);
        return new ProductViewHolder(view);
    }

    class ProductViewHolder extends RecyclerView.ViewHolder {
        TextView txtTitle, txtMarket, txtDate, txtHowMany, txtUnitPrice, txtPrice, txtUnitPriceDesc;

        public ProductViewHolder(View itemView) {
            super(itemView);
            txtTitle = itemView.findViewById(R.id.txtTitle);
            txtMarket = itemView.findViewById(R.id.txtMarket);
            txtDate = itemView.findViewById(R.id.txtDate);
            txtHowMany = itemView.findViewById(R.id.txtHowMany);
            txtUnitPrice = itemView.findViewById(R.id.txtUnitPrice);
            txtPrice = itemView.findViewById(R.id.txtPrice);
            txtUnitPriceDesc = itemView.findViewById(R.id.txtUnitPriceDesc);
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
        final Item_NFe item = productList.get(position);

        //convert double to R$
        DecimalFormat decFormat = new DecimalFormat("'R$ ' #,##0.00");

        holder.txtTitle.setText(item.getProduto().getDescricao().toUpperCase());
        holder.txtHowMany.setText(String.valueOf(item.getQuantidade()));
        holder.txtUnitPrice.setText(String.valueOf(decFormat.format(item.getValor() / item.getQuantidade())));
        holder.txtPrice.setText(String.valueOf(decFormat.format(item.getValor())));
        holder.txtPrice.setTextColor(Color.parseColor("#34a503"));
        holder.txtUnitPriceDesc.setText("Valor " + item.getProduto().getUnidade_comercial() + ":");

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(mCtx, R.string.toast_off_options1, Toast.LENGTH_SHORT).show();
            }
        });

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Toast.makeText(mCtx, "Produto: " + item.getProduto(), Toast.LENGTH_SHORT).show();
                remove(position);
                return false;
            }
        });

        //holder.imageView.setImageDrawable(mCtx.getResources().getDrawable(productQRCode.getImage()));

    }

}