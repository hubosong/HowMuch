package adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

public class ComparacaoListasAdapter extends RecyclerView.Adapter<ComparacaoListasAdapter.ProductViewHolder> {

    private Context mCtx;
    private List<Lista> listas;

    //getting the context and product list with constructor
    public ComparacaoListasAdapter(Context mCtx, List<Lista> productList) {
        this.mCtx = mCtx;
        this.listas = productList;
    }

    @Override
    public ProductViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mCtx);
        View view = inflater.inflate(R.layout.layout_comparacao_de_listas, null);
        return new ProductViewHolder(view);
    }

    class ProductViewHolder extends RecyclerView.ViewHolder {
        TextView txtDataLista, txtQtdItems;
        //ImageView imageView;

        public ProductViewHolder(View itemView) {
            super(itemView);
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

        holder.txtQtdItems.setText(String.valueOf(lista.getValor_total()));
        holder.txtDataLista.setText(lista.getMercado().getNome());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(mCtx);
                TextView title = new TextView(mCtx);
                title.setText("Opções da Lista");
                title.setBackgroundColor(ContextCompat.getColor(mCtx, R.color.toolbar_status));
                title.setPadding(10, 10, 10, 10);
                title.setGravity(Gravity.CENTER);
                title.setTextColor(Color.WHITE);
                title.setTextSize(20);
                dialog.setCustomTitle(title);
                dialog.setNeutralButton("Comparar em Mercados", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
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

                            }
                        }.execute();
                    }
                });
                dialog.setNegativeButton("Excluir Lista", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
                dialog.setPositiveButton("Editar Lista", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
                AlertDialog alert = dialog.create();
                alert.show();
            }
        });

    }

    public void remove(int position) {
        listas.remove(position);
        notifyItemRemoved(position);
    }
}
