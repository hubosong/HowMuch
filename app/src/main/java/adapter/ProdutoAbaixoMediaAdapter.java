package adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.machczew.howmuch.R;

import java.text.DecimalFormat;
import java.util.List;

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
        TextView txtTitle, txtMarket, txtDate, txtMediumPrice,  txtOff, txtPrice, txtOption, txtDescOff;
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

            txtDescOff = itemView.findViewById(R.id.txtOffDescription);

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
            holder.txtDescOff.setText("Acrescimo");
            holder.txtOff.setTextColor(Color.parseColor("#fe0303"));
            holder.txtPrice.setTextColor(Color.parseColor("#fe0303"));
            holder.txtPrice.setText(String.valueOf(decFormat.format(prodBelowAverage.getValor())));


        } else {
            holder.txtTitle.setText(prodBelowAverage.getDescricao_produto().toUpperCase());
            holder.txtMarket.setText(prodBelowAverage.getNome_mercado().toUpperCase());
            holder.txtDate.setText(prodBelowAverage.getData());
            holder.txtMediumPrice.setText(String.valueOf(decFormat.format(prodBelowAverage.getValor_medio())));
            holder.txtOff.setText(String.valueOf(decFormat.format(prodBelowAverage.getValor() - prodBelowAverage.getValor_medio())));
            holder.txtOff.setTextColor(Color.parseColor("#34a503"));
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


/*
package adapter;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import entidade.Lista;
import entidade.Produto;
import entidade.ProdutoAbaixoMedia;
import entidade.Usuario;
import entidade.Utils;
import com.machczew.howmuch.Criar_lista_Compras;
import com.machczew.howmuch.R;

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
        TextView txtTitle, txtMarket, txtDate, txtMediumPrice, txtOff, txtPrice, txtOption, txtDescOff;
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
        final ProdutoAbaixoMedia prodBelowAverage = productList.get(position);

        //convert double to R$
        DecimalFormat decFormat = new DecimalFormat("'R$ ' #,##0.00");

        if (prodBelowAverage.getValor() >= prodBelowAverage.getValor_medio()) {
            holder.txtTitle.setText(prodBelowAverage.getDescricao_produto().toUpperCase());
            holder.txtMarket.setText(prodBelowAverage.getNome_mercado().toUpperCase());
            holder.txtDate.setText(prodBelowAverage.getData());
            holder.txtMediumPrice.setText(String.valueOf(decFormat.format(prodBelowAverage.getValor_medio())));
            holder.txtOff.setText(String.valueOf(decFormat.format(prodBelowAverage.getValor() - prodBelowAverage.getValor_medio())));
            holder.txtDescOff.setText("Acrescimo");
            holder.txtOff.setTextColor(Color.parseColor("#fe0303"));
            holder.txtPrice.setTextColor(Color.parseColor("#fe0303"));
            holder.txtPrice.setText(String.valueOf(decFormat.format(prodBelowAverage.getValor())));


        } else {
            holder.txtTitle.setText(prodBelowAverage.getDescricao_produto().toUpperCase());
            holder.txtMarket.setText(prodBelowAverage.getNome_mercado().toUpperCase());
            holder.txtDate.setText(prodBelowAverage.getData());
            holder.txtMediumPrice.setText(String.valueOf(decFormat.format(prodBelowAverage.getValor_medio())));
            holder.txtOff.setText(String.valueOf(decFormat.format(prodBelowAverage.getValor() - prodBelowAverage.getValor_medio())));
            holder.txtOff.setTextColor(Color.parseColor("#34a503"));
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
                popupMenu.inflate(R.menu.navlist_produto);
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @SuppressLint("StaticFieldLeak")
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            //Adicionar a Nova Lista
                            case R.id.option1:
                                final AlertDialog.Builder builder = new AlertDialog.Builder(mCtx);
                                builder.setTitle("Adicionar " + prodBelowAverage.getDescricao_produto() + " à uma Nova Lista");
                                LinearLayout layout = new LinearLayout(mCtx);
                                layout.setOrientation(LinearLayout.VERTICAL);
                                final EditText editText = new EditText(mCtx);
                                editText.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
                                editText.setHint("Informe a quantidade (" + prodBelowAverage.getUnidade_comercial() + ")");
                                layout.addView(editText);
                                builder.setView(layout);
                                builder.setPositiveButton("Adicionar", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface arg0, int arg1) {
                                        Lista lista = new Lista();
                                        Produto p = prodBelowAverage.converterEmProduto();
                                        p.setTransient_quantidade(Float.valueOf(editText.getText().toString()));
                                        lista.getListaProdutos().add(p);
                                        Intent intent = new Intent(mCtx, Criar_lista_Compras.class);
                                        intent.putExtra("LISTA", lista);
                                        mCtx.startActivity(intent);
                                    }
                                });
                                builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface arg0, int arg1) {

                                    }
                                });
                                builder.create().show();
                                break;

                            //Adicionar produto a Lista Existente
                            case R.id.option2:
                                final Usuario usuario = Utils.loadFromSharedPreferences(mCtx);
                                if (usuario != null && usuario.getId_usuario() > 0) {
                                    new AsyncTask<String, Void, ArrayList<Lista>>() {

                                        @Override
                                        protected void onPreExecute() {
                                            super.onPreExecute();
                                        }

                                        @Override
                                        protected ArrayList<Lista> doInBackground(String... params) {
                                            ArrayList<Lista> list = null;
                                            try {
                                                URL url = new URL(Utils.URL + "lista");
                                                HttpURLConnection urlCon = (HttpURLConnection) url.openConnection();
                                                urlCon.setRequestMethod("POST");
                                                urlCon.setDoOutput(true);
                                                urlCon.setDoInput(true);

                                                ObjectOutputStream wr = new ObjectOutputStream(urlCon.getOutputStream());
                                                wr.writeUTF("GET_BY_ID_USUARIO");
                                                wr.writeLong(usuario.getId_usuario());
                                                wr.close();
                                                wr.flush();

                                                ObjectInputStream ois = new ObjectInputStream(urlCon.getInputStream());
                                                list = (ArrayList<Lista>) ois.readObject();
                                                ois.close();

                                            } catch (ClassNotFoundException | IOException e) {
                                                e.printStackTrace();
                                            }
                                            return list;
                                        }

                                        @Override
                                        protected void onPostExecute(ArrayList<Lista> list) {
                                            final AlertDialog.Builder builder2 = new AlertDialog.Builder(mCtx);
                                            if (list != null) {
                                                builder2.setTitle("Adicionar " + prodBelowAverage.getDescricao_produto() + " à Lista Existente");
                                                LinearLayout layout2 = new LinearLayout(mCtx);
                                                layout2.setOrientation(LinearLayout.VERTICAL);
                                                for (final Lista l : list) {
                                                    TextView tv = new TextView(mCtx);
                                                    tv.setText(l.getNome() + " (" + l.getTransient_qtd_produtos() + " Produtos)");
                                                    tv.setOnClickListener(new View.OnClickListener() {
                                                        @Override
                                                        public void onClick(View view) {
                                                            final AlertDialog.Builder builder = new AlertDialog.Builder(mCtx);
                                                            builder.setTitle("Adicionar " + prodBelowAverage.getDescricao_produto() + " à Lista \"" + l.getNome() + "\"");
                                                            LinearLayout layout = new LinearLayout(mCtx);
                                                            layout.setOrientation(LinearLayout.VERTICAL);
                                                            final EditText editText = new EditText(mCtx);
                                                            editText.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
                                                            editText.setHint("Informe a quantidade (" + prodBelowAverage.getUnidade_comercial() + ")");
                                                            layout.addView(editText);
                                                            builder.setView(layout);
                                                            builder.setPositiveButton("Adicionar", new DialogInterface.OnClickListener() {
                                                                public void onClick(DialogInterface arg0, int arg1) {
                                                                    Lista lista = l;
                                                                    Produto p = prodBelowAverage.converterEmProduto();
                                                                    p.setTransient_quantidade(Float.valueOf(editText.getText().toString()));
                                                                    lista.getListaProdutos().add(p);
                                                                    Intent intent = new Intent(mCtx, Criar_lista_Compras.class);
                                                                    intent.putExtra("LISTA", lista);
                                                                    mCtx.startActivity(intent);
                                                                }
                                                            });
                                                            builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                                                                public void onClick(DialogInterface arg0, int arg1) {

                                                                }
                                                            });
                                                            builder.create().show();
                                                        }
                                                    });
                                                    layout2.addView(tv);
                                                }
                                                builder2.setView(layout2);
                                                builder2.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                                                    public void onClick(DialogInterface arg0, int arg1) {

                                                    }
                                                });
                                                builder2.create().show();
                                            } else {
                                                Toast.makeText(mCtx, "Nenhuma Lista Encontrada", Toast.LENGTH_LONG).show();
                                            }
                                        }
                                    }.execute();
                                } else {
                                    Toast.makeText(mCtx, "Você não está logado!", Toast.LENGTH_LONG).show();
                                }
                                break;

                            //Compartilhar Oferta
                            case R.id.option3:
                                Intent sendIntent = new Intent();
                                sendIntent.setAction(Intent.ACTION_SEND);
                                String s = "Olá, talvez você goste desta oferta: " + prodBelowAverage.getDescricao_produto() + " - R$ " + prodBelowAverage.getValor();
                                s += "\nBaixe o app HowMuch e confira: www.howmuch.com";
                                sendIntent.putExtra(Intent.EXTRA_TEXT, s);
                                sendIntent.setType("text/plain");
                                mCtx.startActivity(sendIntent);
                                break;


                            //Criar Alerta
                            case R.id.option4:
                                Toast.makeText(mCtx, "Não Implementado.", Toast.LENGTH_SHORT).show();
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


    }

    public void remove(int position) {
        productList.remove(position);
        notifyItemRemoved(position);
    }
}
*/