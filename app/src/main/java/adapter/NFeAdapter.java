package adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.util.List;

import entidade.Lista;
import entidade.NFe;
import entidade.Usuario;
import entidade.Utils;
import robsonmachczew.activities.Lista_Compras;
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
                                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                                builder.setTitle("Confirmar Exclusão de NFe");
                                builder.setMessage("Deseja realmente excluir esta NFe?\nEsta ação não pode ser desfeita.");
                                final Usuario usuario = Utils.loadFromSharedPreferences(context);
                                builder.setPositiveButton("Excluir", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface arg0, int arg1) {
                                        new AsyncTask<String, Void, String>() {
                                            @Override
                                            protected void onPreExecute() {
                                                super.onPreExecute();
                                            }

                                            @Override
                                            protected String doInBackground(String... params) {
                                                String res = "";
                                                try {
                                                    String urlParameters = "idnfe=" + nfe.getId_nfe() + "&idusuario=" + usuario.getId_usuario();
                                                    byte[] postData = urlParameters.getBytes(StandardCharsets.UTF_8);

                                                    URL url = new URL(Utils.URL + "excluir_nfe");
                                                    HttpURLConnection urlCon = (HttpURLConnection) url.openConnection();
                                                    urlCon.setRequestMethod("POST");
                                                    urlCon.setDoOutput(true); // Habilita o envio da chave por stream
                                                    urlCon.setDoInput(true); // Habilita o recebimento via stream

                                                    DataOutputStream wr = new DataOutputStream(urlCon.getOutputStream()); // Stream que envia a chave para o servidor
                                                    wr.write(postData); // Envia a chave
                                                    wr.close();
                                                    wr.flush();

                                                    InputStream ois = urlCon.getInputStream();
                                                    BufferedReader reader = new BufferedReader(new InputStreamReader(ois));
                                                    StringBuilder result = new StringBuilder();
                                                    String line;

                                                    while ((line = reader.readLine()) != null) {
                                                        result.append(line);
                                                    }
                                                    res = result.toString();
                                                } catch (Exception e) {
                                                    e.printStackTrace();
                                                }
                                                return res;
                                            }

                                            @Override
                                            protected void onPostExecute(String resultado) {
                                                if (resultado == "1") {
                                                    Toast.makeText(context, "NFe excluida!", Toast.LENGTH_LONG).show();
                                                } else {
                                                    Toast.makeText(context, "Erro!", Toast.LENGTH_LONG).show();
                                                }
                                            }
                                        }.execute();
                                    }
                                });
                                builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface arg0, int arg1) {

                                    }
                                });
                                builder.create().show();
                                break;
                            case R.id.option3:
                                Lista lista = new Lista();
                                for (int i = 0; i < nfe.getLista_items().size(); i++) {
                                    lista.getListaProdutos().add(nfe.getLista_items().get(i).getProduto());
                                    lista.getListaProdutos().get(i).setTransient_quantidade(nfe.getLista_items().get(i).getQuantidade());
                                }
                                Intent intent1 = new Intent(context, Lista_Compras.class);
                                intent1.putExtra("LISTA", lista);
                                context.startActivity(intent1);
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
