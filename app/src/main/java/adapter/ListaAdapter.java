package adapter;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import entidade.Lista;
import entidade.Utils;
import robsonmachczew.activities.R;
import robsonmachczew.activities.VerComparacaoLista;

public class ListaAdapter extends RecyclerView.Adapter<ListaAdapter.ProductViewHolder> {

    private Context mCtx;
    private List<Lista> listas;

    //getting the context and product list with constructor
    public ListaAdapter(Context mCtx, List<Lista> productList) {
        this.mCtx = mCtx;
        this.listas = productList;
    }

    @Override
    public ProductViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mCtx);
        View view = inflater.inflate(R.layout.layout_lista_de_listas, null);
        return new ProductViewHolder(view);
    }

    class ProductViewHolder extends RecyclerView.ViewHolder {
        TextView txtNomeLista, txtDataLista, txtQtdItems;
        //ImageView imageView;

        public ProductViewHolder(View itemView) {
            super(itemView);
            txtNomeLista = itemView.findViewById(R.id.txtNomeLista);
            txtDataLista = itemView.findViewById(R.id.txtDataLista);
            txtQtdItems = itemView.findViewById(R.id.txtQtdItems);
        }
    }

    @Override
    public int getItemCount() {
        return listas.size();
    }

    @Override
    public void onBindViewHolder(final ProductViewHolder holder, final int position) {
        final Lista lista = listas.get(position);

        holder.txtNomeLista.setText(lista.getNome());
        holder.txtQtdItems.setText(String.valueOf(lista.getTransient_qtd_produtos()) + " Produtos");
        holder.txtDataLista.setText(lista.getData());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final Dialog dialog = new Dialog(mCtx);
                dialog.setContentView(R.layout.dialog_minha_lista);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                //funcoes
                Button btnComparar = dialog.findViewById(R.id.btn_comparar_lista);
                btnComparar.setVisibility(View.VISIBLE);
                btnComparar.setOnClickListener(new View.OnClickListener() {
                    @SuppressLint("StaticFieldLeak")
                    public void onClick(View v) {
                        new AsyncTask<String, Void, ArrayList<Lista>>() {
                            @Override
                            protected void onPreExecute() {
                                //progWait.setVisibility(View.VISIBLE);
                                //txtWait.setVisibility(View.VISIBLE);
                                super.onPreExecute();
                            }
                            @Override
                            protected ArrayList<Lista> doInBackground(String... params) {
                                ArrayList<Lista> list = null;
                                try {
                                    URL url = new URL(Utils.URL + "comparar_lista");
                                    HttpURLConnection urlCon = (HttpURLConnection) url.openConnection();
                                    urlCon.setRequestMethod("POST");
                                    urlCon.setDoOutput(true);
                                    urlCon.setDoInput(true);

                                    ObjectOutputStream wr = new ObjectOutputStream(urlCon.getOutputStream());
                                    wr.writeLong(lista.getId_lista());
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
                                Intent i = new Intent(mCtx, VerComparacaoLista.class);
                                i.putExtra("LISTAS", list);
                                mCtx.startActivity(i);
                            }
                        }.execute();
                    }
                });

                Button btnExcluir = dialog.findViewById(R.id.btn_excluir_lista);
                btnExcluir.setVisibility(View.VISIBLE);
                btnExcluir.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });

                Button btnEditar = dialog.findViewById(R.id.btn_editar_lista);
                btnEditar.setVisibility(View.VISIBLE);
                btnEditar.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });

                dialog.create();
                dialog.show();
            }
        });

    }

    public void remove(int position) {
        listas.remove(position);
        notifyItemRemoved(position);
    }
}
