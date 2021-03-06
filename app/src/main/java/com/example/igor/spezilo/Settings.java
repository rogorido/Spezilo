package com.example.igor.spezilo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.database.sqlite.SQLiteDatabase;
import android.view.View;
import android.widget.Button;

import android.util.Log;

public class Settings extends AppCompatActivity {

    PurchaseSQLiteHelper dbh;
    SQLiteDatabase db;
    Button btnDelete;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        dbh = new PurchaseSQLiteHelper(this, "DBPurchases", null, 2);
        
        btnDelete = (Button) findViewById(R.id.btnDeleteDB);

        btnDelete.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                Log.i("spezilo", "estamos aquíantes de borrar...");
                deleteDB();
            }
        });
    }

    private void deleteDB() {
        db = dbh.getWritableDatabase();

        if (db!=null) {

            Log.i("spezilo", "estamos aquí...");

            db.delete("purchases", null, null);

                    /*
            String sqlSentence = "DELETE FROM purchases";
            db.execSQL(sqlSentence);
            */

        }

        db.close();
    }

}
