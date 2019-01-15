package robsonmachczew.activities;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import entidade.Lista;
import entidade.Mercado;
import entidade.Produto;

public class VerSimulacaoListaEmMercado extends Nav {

    private Lista lista;
    private LinearLayout layout_itens;
    private TextView textView_titulo_da_lista;
    private TextView tv_preco_lista;
    private TextView tv_quant_itens;
    private TextView txt_Mercado;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //basic config
        FrameLayout contentFrameLayout = (FrameLayout) findViewById(R.id.content_frame);
        getLayoutInflater().inflate(R.layout.activity_ver_simulacao_lista_em_mercado, contentFrameLayout);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        getSupportActionBar().setTitle("Simulação de Lista em Mercado");

        lista = (Lista) getIntent().getSerializableExtra("LISTA");
        layout_itens = findViewById(R.id.layout_itens);
        textView_titulo_da_lista = findViewById(R.id.textView_titulo_da_lista);
        tv_preco_lista = findViewById(R.id.tv_preco_lista);
        tv_quant_itens = findViewById(R.id.tv_quant_itens);
        txt_Mercado = findViewById(R.id.txt_Mercado);

        renderizaSimulacao();
    }

    private void renderizaSimulacao() {
        if (lista != null) {
            try {
                layout_itens.removeAllViews();
                textView_titulo_da_lista.setText(lista.getNome());
                DecimalFormat df = new DecimalFormat("0.00");
                tv_preco_lista.setText("R$ " + df.format(lista.getValor_total()).replace(",", "."));
                tv_quant_itens.setText("Itens Simulados (" + lista.getListaProdutos().size() + "):");
                txt_Mercado.setText(lista.getMercado().getNome());
                for (final Produto item : lista.getListaProdutos()) {
                    View view; // Creating an instance for View Object
                    LayoutInflater inflater = (LayoutInflater) getBaseContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    view = inflater.inflate(R.layout.layout_produtos_simulacao_mercado, null);
                    ((TextView) view.findViewById(R.id.txtNomeProduto)).setText(item.getDescricao());
                    ((TextView) view.findViewById(R.id.txtDataNFe)).setText("Data da Compra: "+item.getTransient_data());
                    ((TextView) view.findViewById(R.id.txtMediumPrice)).setText("R$: -");
                    ((TextView) view.findViewById(R.id.txtOff)).setText("R$: -");
                    ((TextView) view.findViewById(R.id.txtPrice)).setText("R$: " + df.format(item.getTransient_valor()).replace(",", ".") + " " + item.getUnidade_comercial());
                    view.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            final Dialog dialog_opcoes_produto = new Dialog(VerSimulacaoListaEmMercado.this);

                            dialog_opcoes_produto.requestWindowFeature(Window.FEATURE_NO_TITLE); //no toolbar
                            dialog_opcoes_produto.setContentView(R.layout.dialog_opcoes_produto_abaixo_media);

                            //change alpha intensity
                            WindowManager.LayoutParams lp = dialog_opcoes_produto.getWindow().getAttributes();
                            lp.dimAmount=0.8f;
                            dialog_opcoes_produto.getWindow().setAttributes(lp);
                            dialog_opcoes_produto.getWindow().addFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND);

                            ((Button) dialog_opcoes_produto.findViewById(R.id.bt_adiciona_produto_nova_lista)).setVisibility(View.GONE);
                            ((Button) dialog_opcoes_produto.findViewById(R.id.bt_adiciona_produto_lista_existente)).setVisibility(View.GONE);
                            ((Button) dialog_opcoes_produto.findViewById(R.id.bt_compartilhar_produto_abaixo_media)).setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    Intent sendIntent = new Intent();
                                    sendIntent.setAction(Intent.ACTION_SEND);
                                    String s = "Olá, talvez você goste desta oferta: " + item.getDescricao() + " - R$ " + item.getTransient_valor();
                                    s += "\nBaixe o app HowMuch e confira: www.howmuch.com";
                                    sendIntent.putExtra(Intent.EXTRA_TEXT, s);
                                    sendIntent.setType("text/plain");
                                    startActivity(sendIntent);
                                    dialog_opcoes_produto.cancel();
                                }
                            });
                            ((Button) dialog_opcoes_produto.findViewById(R.id.bt_criar_alerta)).setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    Toast.makeText(VerSimulacaoListaEmMercado.this, "Não Implementado", Toast.LENGTH_LONG).show();
                                }
                            });
                            ((Button) dialog_opcoes_produto.findViewById(R.id.bt_historico_precos_produto)).setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    Intent intent = new Intent(VerSimulacaoListaEmMercado.this, VerProduto.class);
                                    intent.putExtra("PRODUTO", item);
                                    startActivity(intent);
                                    dialog_opcoes_produto.cancel();
                                }
                            });
                            dialog_opcoes_produto.show();
                        }
                    });
                    layout_itens.addView(view);
                }
            }catch (Exception e){
                Toast.makeText(this, "Erro ao Exibir Produtos", Toast.LENGTH_LONG).show();
                System.out.println(">>> Erro: " + e.getMessage());
            }
        }
    }
}
