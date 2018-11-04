package bd;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.preference.PreferenceManager;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.SQLException;

public class DataBaseHelper extends SQLiteOpenHelper{

    private static String DB_PATH = "";
    private static String DB_NAME ="database.db";
    private static int DB_VERSION = 1;
    private SQLiteDatabase mDataBase;
    private final Context mContext;

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase){

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    public DataBaseHelper(Context context) {
        super(context, DB_NAME, null, 1);// 1? its Database Version
        this.mContext = context;
        if(android.os.Build.VERSION.SDK_INT >= 17){
            DB_PATH = context.getApplicationInfo().dataDir + "/databases/";
        }
        else {
            DB_PATH = "/data/data/" + context.getPackageName() + "/databases/";
        }
        try {
            createDataBase();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void createDataBase() throws IOException{    //If database not exists copy it from the assets
        boolean mDataBaseExist = checkDataBase();
        if(!mDataBaseExist){
            this.getReadableDatabase();
            this.close();
            try{
                copyDataBase();     //Copy the database from assests
                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mContext);
                SharedPreferences.Editor editor = prefs.edit();
                editor.putInt("BDV", DB_VERSION);
                editor.apply();
                System.out.println(">>>> BD copiado. Versao: " + DB_VERSION);
            }
            catch (IOException mIOException){
                throw new Error("ErrorCopyingDataBase");
            }
        }else{
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mContext);
            int dbVersion = prefs.getInt("BDV", 1);
            if (DB_VERSION != dbVersion) {
                File dbFile = mContext.getDatabasePath(DB_PATH + DB_NAME);
                System.out.println(">>>> Apagando BD velho.");
                if (!dbFile.delete()) {
                    System.out.println(">>>> ERRO AO APAGAR BD!");
                }else{
                    createDataBase();
                }
            }
        }
    }

    private boolean checkDataBase(){     //Check that the database exists here: /data/data/your package/databases/Da Name
        File dbFile = new File(DB_PATH + DB_NAME);
        return dbFile.exists();
    }

    private void copyDataBase() throws IOException{     //Copy the database from assets
        InputStream mInput = mContext.getAssets().open(DB_NAME);
        String outFileName = DB_PATH + DB_NAME;
        OutputStream mOutput = new FileOutputStream(outFileName);
        byte[] mBuffer = new byte[1024];
        int mLength;
        while ((mLength = mInput.read(mBuffer))>0){
            mOutput.write(mBuffer, 0, mLength);
        }
        mOutput.flush();
        mOutput.close();
        mInput.close();
    }

    public boolean openDataBase() throws SQLException{      //Open the database, so we can query it
        String mPath = DB_PATH + DB_NAME;
        mDataBase = SQLiteDatabase.openDatabase(mPath, null, SQLiteDatabase.CREATE_IF_NECESSARY);
        return mDataBase != null;
    }

    @Override
    public synchronized void close(){
        if(mDataBase != null)
            mDataBase.close();
        super.close();
    }



}