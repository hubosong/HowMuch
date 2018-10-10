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

    class ProductViewHolder extends RecyclerView.ViewHolder {
        TextView txtTitle, txtMarket, txtDate, txtMediumPrice,  txtOff, txtPrice, txtOption, txtDescOff ;
        ImageView imageView;

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
        final ProductQRCode productQRCode = productList.get(position);

        //convert double to R$
        DecimalFormat decFormat = new DecimalFormat("'R$ ' #,##0.00");

        if(productQRCode.getPrice() >= productQRCode.getMediumprice()){
            holder.txtTitle.setText(productQRCode.getTitle().toUpperCase());
            holder.txtMarket.setText(productQRCode.getMarket().toUpperCase());
            holder.txtDate.setText(productQRCode.getDate());
            holder.txtMediumPrice.setText(String.valueOf(decFormat.format(productQRCode.getMediumprice())));
            holder.txtOff.setText(String.valueOf(decFormat.format(productQRCode.getPrice() - productQRCode.getMediumprice())));
            holder.txtDescOff.setText("Acrescimo");
            holder.txtOff.setTextColor(Color.parseColor("#fe0303"));
            holder.txtPrice.setTextColor(Color.parseColor("#fe0303"));
            holder.txtPrice.setText(String.valueOf(decFormat.format(productQRCode.getPrice())));
        } else {
            holder.txtTitle.setText(productQRCode.getTitle().toUpperCase());
            holder.txtMarket.setText(productQRCode.getMarket().toUpperCase());
            holder.txtDate.setText(productQRCode.getDate());
            holder.txtMediumPrice.setText(String.valueOf(decFormat.format(productQRCode.getMediumprice())));
            holder.txtOff.setText(String.valueOf(decFormat.format(productQRCode.getPrice() - productQRCode.getMediumprice())));
            holder.txtOff.setTextColor(Color.parseColor("#34a503"));
            holder.txtPrice.setTextColor(Color.parseColor("#34a503"));
            holder.txtPrice.setText(String.valueOf(decFormat.format(productQRCode.getPrice())));

        }

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
                                //Toast.makeText(mCtx, "P.M. = Preço Médio" + "\n" + "P.A. =  Preço Atual", Toast.LENGTH_SHORT).show();
                                Toast.makeText(mCtx, R.string.toast_off_options1, Toast.LENGTH_SHORT).show();
                                break;
                            case R.id.option2:
                                Toast.makeText(mCtx, "Item adicionado", Toast.LENGTH_SHORT).show();
                                break;
                            case R.id.option3:
                                Toast.makeText(mCtx, "Lista acessada!", Toast.LENGTH_SHORT).show();
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
                Toast.makeText(mCtx, "Produto: " + productQRCode, Toast.LENGTH_SHORT).show();
                remove(position);
                return false;
            }
        });

        //holder.imageView.setImageDrawable(mCtx.getResources().getDrawable(productQRCode.getImage()));

    }

}