package com.example.igor.spezilo;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class PurchaseSQLiteHelper extends SQLiteOpenHelper {

    String sqlSentence = "CREATE TABLE purchases (_id INTEGER PRIMARY KEY, amount REAL, person " +
            "TEXT, category TEXT, place TEXT, description TEXT, date TEXT, privat INTEGER, exported INTEGER)";

    public PurchaseSQLiteHelper(Context context, String name, CursorFactory cf, int version) {
        super(context, name, cf, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //Se ejecuta la sentencia SQL de creación de la tabla
        db.execSQL(sqlSentence);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int versionAnterior,
                          int versionNueva) {
        //NOTA: Por simplicidad del ejemplo aquí utilizamos directamente
        //      la opción de eliminar la tabla anterior y crearla de nuevo
        //      vacía con el nuevo formato.
        //      Sin embargo lo normal será que haya que migrar datos de la
        //      tabla antigua a la nueva, por lo que este método debería
        //      ser más elaborado.

        //Se elimina la versión anterior de la tabla
        //db.execSQL("DROP TABLE IF EXISTS DBPurchases");

        //Se crea la nueva versión de la tabla
        //db.execSQL(sqlSentence);
    }
}
