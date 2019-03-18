package dao;

import android.content.Context;
import android.database.Cursor;

import java.util.ArrayList;

import bd.Adapter;
import entidade.NCM;

public class NCM_DAO {

    private Context context;
    private Adapter adapter;

    public NCM_DAO(Context context) {
        this.context = context;
        adapter = new Adapter(context);
    }

    public NCM getByNCM(long cod_ncm) {
        String sql = "SELECT * FROM tabela_ncm WHERE ncm = " + cod_ncm;
        NCM ncm = null;
        Cursor cursor = adapter.executeQuery(sql);
        if (!cursor.isAfterLast()) {
            ncm = new NCM();
            ncm.setNcm(cursor.getLong(0));
            ncm.setCategoria(cursor.getString(1));
            ncm.setDescricao(cursor.getString(2));
            cursor.close();
        }
        adapter.close();
        return ncm;
    }

    public ArrayList<NCM> getByDescricao(String descricao) {
        String sql = "SELECT * FROM tabela_ncm WHERE descricao LIKE '%" + descricao + "%'";
        ArrayList<NCM> lista = new ArrayList<>();
        Cursor cursor = adapter.executeQuery(sql);
        if (!cursor.isAfterLast()) {
            NCM ncm = new NCM();
            ncm.setNcm(cursor.getLong(0));
            ncm.setCategoria(cursor.getString(1));
            ncm.setDescricao(cursor.getString(2));
            lista.add(ncm);
        }
        cursor.close();
        adapter.close();
        return lista;
    }

    public ArrayList<NCM> getByCategoria(String categoria) {
        String sql = "SELECT * FROM tabela_ncm WHERE descricao LIKE '" + categoria + "%'";
        ArrayList<NCM> lista = new ArrayList<>();
        Cursor cursor = adapter.executeQuery(sql);
        if (!cursor.isAfterLast()) {
            NCM ncm = new NCM();
            ncm.setNcm(cursor.getLong(0));
            ncm.setCategoria(cursor.getString(1));
            ncm.setDescricao(cursor.getString(2));
            lista.add(ncm);
        }
        cursor.close();
        adapter.close();
        return lista;
    }
}
