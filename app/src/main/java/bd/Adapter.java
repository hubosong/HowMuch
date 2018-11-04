package bd;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

public class Adapter {

    private Context context;
    private DataBaseHelper dataBaseHelper;

    public Adapter(Context context) {
        this.context = context;
    }

    private SQLiteDatabase mDb;

    public Adapter open() throws SQLException {
        try {
            dataBaseHelper = new DataBaseHelper(context);
            dataBaseHelper.openDataBase();
            mDb = dataBaseHelper.getWritableDatabase();
        } catch (SQLException mSQLException) {
            System.out.println(mSQLException.toString());
            throw mSQLException;
        } catch (java.sql.SQLException s) {
            System.out.println("ERRO: " + s.getMessage());
        }
        return this;
    }

    public void close() {
        dataBaseHelper.close();
    }

    public Cursor executeQuery(String query) {
        try {
            open();
            Cursor mCur = mDb.rawQuery(query, null);
            if (mCur != null) {
                mCur.moveToFirst();
            }
            close();
            return mCur;
        } catch (SQLException mSQLException) {
            System.out.println(mSQLException.toString());
            throw mSQLException;
        }
    }


}