package adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DecimalFormat;
import java.util.List;

import robsonmachczew.howmuch.ProductQRCode;
import robsonmachczew.howmuch.R;

public class ProductQRCodeAdapter extends RecyclerView.Adapter<ProductQRCodeAdapter.ProductViewHolder> {

    //this context we will use to inflate the layout
    private Context mCtx;
    //we are storing all the products in a list
    private List<ProductQRCode> productList;
    //getting the context and product list with constructor
    public ProductQRCodeAdapter(Context mCtx, List<ProductQRCode> productList) {
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
        final ProductQRCode productQRCode = productList.get(position);

        //convert double to R$
        DecimalFormat decFormat = new DecimalFormat("'R$ ' #,##0.00");

        if(productQRCode.getPrice() <= productQRCode.getMediumprice()){
            holder.txtPrice.setTextColor(Color.parseColor("#fe0303"));

            holder.txtTitle.setText(productQRCode.getTitle());
            holder.txtMediumPrice.setText(String.valueOf(decFormat.format(productQRCode.getMediumprice())));
            holder.txtDate.setText(productQRCode.getDate());
            holder.txtPrice.setText(String.valueOf(decFormat.format(productQRCode.getPrice())));
        } else {
            holder.txtTitle.setText(productQRCode.getTitle());
            holder.txtMediumPrice.setText(String.valueOf(decFormat.format(productQRCode.getMediumprice())));
            holder.txtDate.setText(productQRCode.getDate());
            holder.txtPrice.setText(String.valueOf(decFormat.format(productQRCode.getPrice())));
        }

        holder.imageView.setImageDrawable(mCtx.getResources().getDrawable(productQRCode.getImage()));

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Toast.makeText(mCtx, "Produto: " + productQRCode, Toast.LENGTH_SHORT).show();
                remove(position);
                return false;
            }
        });
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(mCtx, "P.M. = Preço Médio" + "\n" + "P.A. =  Preço Atual", Toast.LENGTH_SHORT).show();
            }
        });

    }


    @Override
    public int getItemCount() {
        return productList.size();
    }

    class ProductViewHolder extends RecyclerView.ViewHolder {
        TextView txtTitle, txtMediumPrice, txtDate, txtPrice;
        ImageView imageView;

        public ProductViewHolder(View itemView) {
            super(itemView);
            txtTitle = itemView.findViewById(R.id.txtTitle);
            txtMediumPrice = itemView.findViewById(R.id.txtMediumPrice);
            txtDate = itemView.findViewById(R.id.txtDate);
            txtPrice = itemView.findViewById(R.id.txtPrice);
            imageView = itemView.findViewById(R.id.imageView);
        }

    }
}