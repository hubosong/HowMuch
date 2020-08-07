package adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.machczew.howmuch.R;

import java.text.DecimalFormat;
import java.util.List;

import entidade.Item_NFe;

public class ItemNFeAdapter extends RecyclerView.Adapter<ItemNFeAdapter.ProductViewHolder> {

    private Context mCtx;
    private List<Item_NFe> productList;

    public ItemNFeAdapter(Context mCtx, List<Item_NFe> productList) {
        this.mCtx = mCtx;
        this.productList = productList;
    }

    @Override
    public ProductViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mCtx);
        View view = inflater.inflate(R.layout.layout_item_nfe, null);
        return new ProductViewHolder(view);
    }

    class ProductViewHolder extends RecyclerView.ViewHolder {
        TextView txtTitle, txtMarket, txtDate, txtHowMany, txtUnitPrice, txtPrice, txtUnitPriceDesc;

        public ProductViewHolder(View itemView) {
            super(itemView);
            txtTitle = itemView.findViewById(R.id.txtNomeLista);
            txtMarket = itemView.findViewById(R.id.txtDataLista);
            txtDate = itemView.findViewById(R.id.txtQtdItems);
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

    @Override
    public void onBindViewHolder(final ProductViewHolder holder, final int position) {
        final Item_NFe item = productList.get(position);

        DecimalFormat decFormat = new DecimalFormat("'R$ ' #,##0.00");

        holder.txtTitle.setText(item.getProduto().getDescricao().toUpperCase());
        holder.txtHowMany.setText(String.valueOf(item.getQuantidade()));
        holder.txtUnitPrice.setText(String.valueOf(decFormat.format(item.getValor() / item.getQuantidade())));
        holder.txtPrice.setText(String.valueOf(decFormat.format(item.getValor())));
        holder.txtPrice.setTextColor(Color.parseColor("#34a503"));
        holder.txtUnitPriceDesc.setText("Valor " + item.getProduto().getUnidade_comercial() + ":");


    }

}