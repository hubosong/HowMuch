package entidade;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import static android.content.Context.MODE_PRIVATE;

public class Utils {

    public static final String URL = "http://187.181.170.135:8080/Mercado/";

    public static Usuario loadFromSharedPreferences(Context context){
        Usuario u = new Usuario();
        SharedPreferences sharedPreferences = context.getSharedPreferences("SHARED", MODE_PRIVATE);
        u.setNome(sharedPreferences.getString("NOME", null));
        u.setId_usuario(sharedPreferences.getLong("IDUSUARIO", 0));
        u.setSenha(sharedPreferences.getString("SENHA", null));
        u.setEmail(sharedPreferences.getString("EMAIL", null));
        u.setCpf(sharedPreferences.getString("CPF", null));
        return u;
    }

    public static boolean saveToSharedPreferences(Usuario usuario, Context context){
        SharedPreferences sharedPreferences = context.getSharedPreferences("SHARED", MODE_PRIVATE);
        SharedPreferences.Editor edit = sharedPreferences.edit();
        edit.putString("NOME", usuario.getNome());
        edit.putString("SENHA", usuario.getSenha());
        edit.putString("EMAIL", usuario.getEmail());
        edit.putLong("IDUSUARIO", usuario.getId_usuario());
        edit.putString("CPF", usuario.getCpf());
        return edit.commit();
    }
    
    public static boolean logout(Context context){
        SharedPreferences sharedPreferences = context.getSharedPreferences("SHARED", MODE_PRIVATE);
        SharedPreferences.Editor edit = sharedPreferences.edit();
        edit.clear();
        return edit.commit();
    }

    public static boolean estaConectado(Context context){
        ConnectivityManager connectivityManager = (ConnectivityManager)  context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if(connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
            return true;
        }
        return false;
    }

}
