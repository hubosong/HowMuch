package dao;

import android.content.Context;
import android.database.Cursor;

import java.util.ArrayList;

import bd.Adapter;
import entidade.Item_NFe;
import entidade.Mercado;
import entidade.NFe;
import entidade.Produto;

public class NFe_DAO {

    private Context context;
    private Adapter adapter;

    public NFe_DAO(Context context) {
        this.context = context;
        adapter = new Adapter(context);
    }

    public boolean insertFromServer(NFe nfe) {
        Cursor cursor = null;
        System.out.println(">>> Inserindo nota no BD local..." + nfe);
        String sql = "INSERT INTO mercado(id_mercado, nome, nome_fantasia, cnpj, endereco, bairro, cep, municipio, telefone, uf, pais, inscricao_estadual, cod_regime_tributario, horarios_funcionamento) VALUES(";
        sql += nfe.getMercado().getId_mercado() + ",";
        sql += "'" + nfe.getMercado().getNome() + "',";
        sql += "'" + nfe.getMercado().getNome_fantasia() + "',";
        sql += "'" + nfe.getMercado().getCnpj() + "',";
        sql += "'" + nfe.getMercado().getEndereco() + "',";
        sql += "'" + nfe.getMercado().getBairro() + "',";
        sql += "'" + nfe.getMercado().getCep() + "',";
        sql += "'" + nfe.getMercado().getMunicipio() + "',";
        sql += "'" + nfe.getMercado().getTelefone() + "',";
        sql += "'" + nfe.getMercado().getUf() + "',";
        sql += "'" + nfe.getMercado().getPais() + "',";
        sql += "'" + nfe.getMercado().getInscricao_estadual() + "',";
        sql += "'" + nfe.getMercado().getCod_regime_tributario() + "',";
        sql += "'" + nfe.getMercado().getHorarios_funcionamento() + "')";
        try {
            cursor = adapter.executeQuery(sql);
        } catch (Exception e) {
            System.out.println(">>> Erro inserindo mercado: " + e.getMessage());
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }
        sql = "INSERT INTO nfe(id_nfe, id_mercado, chave, data, forma_pagamento, modelo, serie, numero, valor, id_usuario) VALUES (" +
                nfe.getId_nfe() + "," +
                nfe.getMercado().getId_mercado() + ",'" +
                nfe.getChave() + "','" +
                nfe.getData() + "','" +
                nfe.getForma_pagamento() + "','" +
                nfe.getModelo() + "','" +
                nfe.getSerie() + "','" +
                nfe.getNumero() + "'," +
                nfe.getValor() + "," +
                nfe.getId_usuario() + ")";
        try {
            cursor = adapter.executeQuery(sql);
        } catch (Exception e) {
            System.out.println(">>> Erro inserindo insertFromServer_1: " + e.getMessage());
            adapter.close();
            return false;
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }
        for (int i = 0; i < nfe.getLista_items().size(); i++) {
            cursor = null;
            try {
                sql = "INSERT INTO produto(id_produto, cod_ean_comercial, descricao, unidade_comercial, codigo_do_produto, codigo_ncm, descricao2) VALUES (";
                sql += nfe.getLista_items().get(i).getProduto().getId_produto() + ",";
                sql += nfe.getLista_items().get(i).getProduto().getCod_EAN_comercial() + ",";
                sql += "'" + nfe.getLista_items().get(i).getProduto().getDescricao() + "',";
                sql += "'" + nfe.getLista_items().get(i).getProduto().getUnidade_comercial() + "',";
                sql += nfe.getLista_items().get(i).getProduto().getCodigo_do_produto() + ",";
                sql += nfe.getLista_items().get(i).getProduto().getCodigo_ncm() + ",";
                sql += "'" + nfe.getLista_items().get(i).getProduto().getDescricao2() + "')";
                cursor = adapter.executeQuery(sql);
            } catch (Exception e) {
                System.out.println(">>> ERRO registrando produto: " + e.getMessage());
            } finally {
                if (cursor != null && !cursor.isClosed()) {
                    cursor.close();
                }
            }
            try {
                sql = "INSERT INTO item_nfe(id_nfe, id_produto, valor, quantidade) VALUES(";
                sql += nfe.getLista_items().get(i).getId_nfe() + ",";
                sql += nfe.getLista_items().get(i).getProduto().getId_produto() + ",";
                sql += nfe.getLista_items().get(i).getValor() + ",";
                sql += nfe.getLista_items().get(i).getQuantidade() + ")";
                cursor = adapter.executeQuery(sql);
            } catch (Exception e) {
                System.out.println(">>> ERRO item_lista: " + e.getMessage());
            } finally {
                if (cursor != null && !cursor.isClosed()) {
                    cursor.close();
                }
            }
        }
        adapter.close();
        return true;
    }

    public boolean insertAllFromServer(ArrayList<NFe> lista) {
        //INSERE MERCADOS
        ArrayList<Long> ids_mercados = new ArrayList<>();
        ArrayList<Mercado> mercados_a_inserir = new ArrayList<>();
        for (NFe nfe : lista) {
            if(!ids_mercados.contains(nfe.getMercado().getId_mercado())){
                ids_mercados.add(nfe.getMercado().getId_mercado());
                mercados_a_inserir.add(nfe.getMercado());
            }
        }
        String sql = "INSERT INTO mercado(id_mercado, nome, nome_fantasia, cnpj, endereco, bairro, cep, municipio, telefone, uf, pais, inscricao_estadual, cod_regime_tributario, horarios_funcionamento) VALUES";
        for (int i = 0; i < mercados_a_inserir.size(); i++) {
            sql += "(" + mercados_a_inserir.get(i).getId_mercado() + ",";
            sql += "'" + mercados_a_inserir.get(i).getNome() + "',";
            sql += "'" + mercados_a_inserir.get(i).getNome_fantasia() + "',";
            sql += "'" + mercados_a_inserir.get(i).getCnpj() + "',";
            sql += "'" + mercados_a_inserir.get(i).getEndereco() + "',";
            sql += "'" + mercados_a_inserir.get(i).getBairro() + "',";
            sql += "'" + mercados_a_inserir.get(i).getCep() + "',";
            sql += "'" + mercados_a_inserir.get(i).getMunicipio() + "',";
            sql += "'" + mercados_a_inserir.get(i).getTelefone() + "',";
            sql += "'" + mercados_a_inserir.get(i).getUf() + "',";
            sql += "'" + mercados_a_inserir.get(i).getPais() + "',";
            sql += "'" + mercados_a_inserir.get(i).getInscricao_estadual() + "',";
            sql += "'" + mercados_a_inserir.get(i).getCod_regime_tributario() + "',";
            sql += "'" + mercados_a_inserir.get(i).getHorarios_funcionamento() + "'),";
        }
        sql = sql.substring(0, sql.length() - 1);
        try {
            adapter.executeQuery(sql).close();
        }catch (Exception e){
            System.out.println("Erro insertAllFromServer_merc: "+e.getMessage());
        }


        //INSERIR NFes
        sql = "INSERT INTO nfe(id_nfe, id_mercado, chave, data, forma_pagamento, modelo, serie, numero, valor, id_usuario) VALUES ";
        for (int i = 0; i < lista.size(); i++) {
            NFe nfe = lista.get(i);
            sql += "(" + nfe.getId_nfe() + "," +
                    nfe.getMercado().getId_mercado() + ",'" +
                    nfe.getChave() + "','" +
                    nfe.getData() + "','" +
                    nfe.getForma_pagamento() + "','" +
                    nfe.getModelo() + "','" +
                    nfe.getSerie() + "','" +
                    nfe.getNumero() + "'," +
                    nfe.getValor() + "," +
                    nfe.getId_usuario() + "),";
        }
        sql = sql.substring(0, sql.length() - 1);
        try {
            adapter.executeQuery(sql).close();
        }catch (Exception e){
            System.out.println("Erro insertAllFromServer_nfes: "+e.getMessage());
        }


        //INSERIR PRODUTOS
        ArrayList<Long> lista_id_prods = new ArrayList<>();
        ArrayList<Produto> lista_produtos_a_cadastrar = new ArrayList<>();
        for (NFe nfe : lista) {
            for (int i = 0; i < nfe.getLista_items().size(); i++) {
                if (!lista_id_prods.contains(nfe.getLista_items().get(i).getProduto().getId_produto())) {
                    lista_produtos_a_cadastrar.add(nfe.getLista_items().get(i).getProduto());
                    lista_id_prods.add(nfe.getLista_items().get(i).getProduto().getId_produto());
                }
            }
        }
        sql = "INSERT INTO produto(id_produto, cod_ean_comercial, descricao, unidade_comercial, codigo_do_produto, codigo_ncm, descricao2) VALUES ";
        for (Produto prod : lista_produtos_a_cadastrar) {
            sql += "(" + prod.getId_produto() + ",";
            sql += prod.getCod_EAN_comercial() + ",";
            sql += "'" + prod.getDescricao() + "',";
            sql += "'" + prod.getUnidade_comercial() + "',";
            sql += prod.getCodigo_do_produto() + ",";
            sql += prod.getCodigo_ncm() + ",";
            sql += "'" + prod.getDescricao2() + "'),";
        }
        sql = sql.substring(0, sql.length() - 1);
        try {
            adapter.executeQuery(sql).close();
        }catch (Exception e){
            System.out.println("Erro insertAllFromServer_prods: "+e.getMessage());
        }


        //INSERIR ITENS
        sql = "INSERT INTO item_nfe(id_nfe, id_produto, valor, quantidade) VALUES";
        for (NFe nfe : lista) {
            for (int i = 0; i < nfe.getLista_items().size(); i++) {
                sql += "(" + nfe.getLista_items().get(i).getId_nfe() + ",";
                sql += nfe.getLista_items().get(i).getProduto().getId_produto() + ",";
                sql += nfe.getLista_items().get(i).getValor() + ",";
                sql += nfe.getLista_items().get(i).getQuantidade() + "),";
            }
        }
        sql = sql.substring(0, sql.length() - 1);
        try {
            adapter.executeQuery(sql).close();
        }catch (Exception e){
            System.out.println("Erro insertAllFromServer_itens: "+e.getMessage());
        }


        adapter.close();
        return true;
    }

    public boolean insertToLocal(NFe nfe) {
        String sql = "INSERT INTO nfe(id_nfe, id_mercado, chave, data, forma_pagamento, modelo, serie, numero, valor, id_usuario) VALUES (" +
                nfe.getId_nfe() + "," +
                nfe.getMercado().getId_mercado() + "," +
                nfe.getChave() + ",'" +
                nfe.getData() + "','" +
                nfe.getForma_pagamento() + "','" +
                nfe.getModelo() + "','" +
                nfe.getSerie() + "','" +
                nfe.getNumero() + "'," +
                nfe.getValor() + "," +
                nfe.getId_usuario() + ")";
        try {
            adapter.executeQuery(sql);
        } catch (Exception e) {
            System.out.println(">>> Erro inserindo insertFromServer_1: " + e.getMessage());
            return false;
        }
        for (int i = 0; i < nfe.getLista_items().size(); i++) {
            try {
                sql = "INSERT INTO produto(cod_ean_comercial, descricao, unidade_comercial, codigo_do_produto, codigo_ncm, descricao2) VALUES (";
                sql += nfe.getLista_items().get(i).getProduto().getCod_EAN_comercial() + ",";
                sql += "'" + nfe.getLista_items().get(i).getProduto().getDescricao() + "',";
                sql += "'" + nfe.getLista_items().get(i).getProduto().getUnidade_comercial() + "',";
                sql += nfe.getLista_items().get(i).getProduto().getCodigo_do_produto() + ",";
                sql += nfe.getLista_items().get(i).getProduto().getCodigo_ncm() + ",";
                sql += "'" + nfe.getLista_items().get(i).getProduto().getDescricao2() + "')";
                adapter.executeQuery(sql);
            } catch (Exception e) {
                System.out.println(">>> ERRO registrando produto: " + e.getMessage());
            }
            try {
                sql = "INSERT INTO item_nfe(id_nfe, id_produto, valor, quantidade) VALUES(";
                sql += nfe.getLista_items().get(i).getId_nfe() + ",";
                sql += nfe.getLista_items().get(i).getProduto().getId_produto() + ",";
                sql += nfe.getLista_items().get(i).getValor() + ",";
                sql += nfe.getLista_items().get(i).getQuantidade() + ")";
                adapter.executeQuery(sql);
            } catch (Exception e) {
                System.out.println(">>> ERRO item_lista: " + e.getMessage());
            }
        }
        return true;
    }

    public ArrayList<NFe> getAllNFes() {
        System.out.println(">>> PEGANDO TODAS AS NOTAS...");
        ArrayList<NFe> lista = new ArrayList<>();
        String sql = "SELECT * FROM nfe JOIN mercado m ON nfe.id_mercado = m.id_mercado";
        Cursor cursor = null;
        try {
            cursor = adapter.executeQuery(sql);
        } catch (Exception e) {
            System.out.println(">>> NFe_DAO.getAllNFes_112: " + e.getMessage());
        }
        if (cursor != null && cursor.getCount() > 0) {
            System.out.println(">>> NOTAS ENCONTRADAS...");
            do {
                NFe nfe = new NFe();
                nfe.setId_nfe(cursor.getLong(cursor.getColumnIndex("id_nfe")));
                nfe.setChave(cursor.getString(cursor.getColumnIndex("chave")));
                nfe.setData(cursor.getString(cursor.getColumnIndex("data")));
                nfe.setForma_pagamento(cursor.getString(cursor.getColumnIndex("forma_pagamento")));
                nfe.setModelo(cursor.getString(cursor.getColumnIndex("modelo")));
                nfe.setSerie(cursor.getString(cursor.getColumnIndex("serie")));
                nfe.setNumero(cursor.getString(cursor.getColumnIndex("numero")));
                nfe.setValor(cursor.getFloat(cursor.getColumnIndex("valor")));
                nfe.setId_usuario(cursor.getLong(cursor.getColumnIndex("id_usuario")));
                Mercado m = new Mercado();
                m.setId_mercado(cursor.getLong(cursor.getColumnIndex("id_mercado")));
                m.setNome(cursor.getString(cursor.getColumnIndex("nome")));
                m.setNome_fantasia(cursor.getString(cursor.getColumnIndex("nome_fantasia")));
                m.setCnpj(cursor.getString(cursor.getColumnIndex("cnpj")));
                m.setEndereco(cursor.getString(cursor.getColumnIndex("endereco")));
                m.setBairro(cursor.getString(cursor.getColumnIndex("bairro")));
                m.setCep(cursor.getString(cursor.getColumnIndex("cep")));
                m.setMunicipio(cursor.getString(cursor.getColumnIndex("municipio")));
                m.setUf(cursor.getString(cursor.getColumnIndex("uf")));
                m.setPais(cursor.getString(cursor.getColumnIndex("pais")));
                m.setInscricao_estadual(cursor.getString(cursor.getColumnIndex("inscricao_estadual")));
                m.setCod_regime_tributario(cursor.getString(cursor.getColumnIndex("cod_regime_tributario")));
                m.setHorarios_funcionamento(cursor.getString(cursor.getColumnIndex("horarios_funcionamento")));
                nfe.setMercado(m);

                sql = "SELECT * FROM item_nfe i JOIN produto p ON i.id_produto = p.id_produto WHERE i.id_nfe = " + nfe.getId_nfe();
                Cursor cursor2 = null;
                try {
                    cursor2 = adapter.executeQuery(sql);
                } catch (Exception e) {
                    System.out.println(">>> Erro NFe_DAO.getAllNFes_14: " + e.getMessage());
                }
                ArrayList<Item_NFe> lista_itens = new ArrayList<>();
                if (cursor2 != null && cursor2.getCount() > 0) {
                    do {
                        Item_NFe item = new Item_NFe();
                        item.setId_nfe(nfe.getId_nfe());
                        item.setQuantidade(cursor2.getFloat(cursor2.getColumnIndex("quantidade")));
                        item.setValor(cursor2.getFloat(cursor2.getColumnIndex("valor")));
                        Produto p = new Produto();
                        p.setTransient_quantidade(item.getQuantidade());
                        p.setTransient_valor(item.getValor());
                        p.setTransient_data(nfe.getData());
                        p.setId_produto(cursor2.getLong(cursor2.getColumnIndex("id_produto")));
                        p.setCod_EAN_comercial(cursor2.getLong(cursor2.getColumnIndex("cod_ean_comercial")));
                        p.setDescricao(cursor2.getString(cursor2.getColumnIndex("descricao")));
                        p.setDescricao2(cursor2.getString(cursor2.getColumnIndex("descricao2")));
                        p.setUnidade_comercial(cursor2.getString(cursor2.getColumnIndex("unidade_comercial")));
                        p.setCodigo_do_produto(cursor2.getLong(cursor2.getColumnIndex("codigo_do_produto")));
                        p.setCodigo_ncm(cursor2.getLong(cursor2.getColumnIndex("codigo_ncm")));
                        //p.setImagem(cursor.get);
                        item.setProduto(p);
                        lista_itens.add(item);
                        cursor2.moveToNext();
                    } while (!cursor2.isAfterLast());
                }
                nfe.setLista_items(lista_itens);

                lista.add(nfe);
                cursor.moveToNext();
            } while (!cursor.isAfterLast());
        }
        return lista;
    }

    public void updateIdNFe(long id_nfe, String chave) {
        String sql = "UPDATE nfe SET id_nfe = " + id_nfe + " WHERE chave LIKE '" + chave + "'";
        adapter.executeQuery(sql);
    }

    public NFe getByChave(String chave) {
        NFe nfe = null;

        return nfe;
    }

    public ArrayList<Long> getIdsNFesCadastradasLocamente() {
        ArrayList<Long> lista = new ArrayList<>();
        String sql = "SELECT id_nfe FROM nfe";
        Cursor cursor = null;
        try {
            cursor = adapter.executeQuery(sql);
        } catch (Exception e) {
            System.out.println(">>> NFe_DAO.getAllNFes_112: " + e.getMessage());
        }
        if (cursor != null && cursor.getCount() > 0) {
            do {
                lista.add(cursor.getLong(0));
                cursor.moveToNext();
            } while (!cursor.isAfterLast());
        }
        return lista;
    }

}

