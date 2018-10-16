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

import entidade.Produto;
import robsonmachczew.activities.R;

public class ReadQRCodeAdapter extends RecyclerView.Adapter<ReadQRCodeAdapter.ProductViewHolder> {

    //this context we will use to inflate the layout
    private Context mCtx;
    //we are storing all the products in a list
    private List<Produto> productList;
    //getting the context and product list with constructor
    public ReadQRCodeAdapter(Context mCtx, List<Produto> productList) {
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
        TextView txtTitle, txtMarket, txtDate, txtHowMany, txtUnitPrice,  txtPrice;

        public ProductViewHolder(View itemView) {
            super(itemView);
            txtTitle = itemView.findViewById(R.id.txtTitle);
            txtMarket = itemView.findViewById(R.id.txtMarket);
            txtDate = itemView.findViewById(R.id.txtDate);
            txtHowMany = itemView.findViewById(R.id.txtHowMany);
            txtUnitPrice = itemView.findViewById(R.id.txtUnitPrice);
            txtPrice = itemView.findViewById(R.id.txtPrice);
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
        final Produto productQRCode = productList.get(position);

        //convert double to R$
        DecimalFormat decFormat = new DecimalFormat("'R$ ' #,##0.00");

        holder.txtTitle.setText(productQRCode.getDescricao().toUpperCase());
        holder.txtHowMany.setText(String.valueOf(productQRCode.getTransient_quantidade()));
        holder.txtUnitPrice.setText(String.valueOf(decFormat.format(productQRCode.getTransient_valor() / productQRCode.getTransient_quantidade())));
        holder.txtPrice.setText(String.valueOf(decFormat.format(productQRCode.getTransient_valor())));
        holder.txtPrice.setTextColor(Color.parseColor("#34a503"));

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(mCtx, R.string.toast_off_options1, Toast.LENGTH_SHORT).show();
            }
        });

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Toast.makeText(mCtx, "Produto: " + productQRCode, Toast.LENGTH_SHORT).show();
                remove(position);
                return false;
            }
        });

        //holder.imageView.setImageDrawable(mCtx.getResources().getDrawable(productQRCode.getImage()));

    }

}