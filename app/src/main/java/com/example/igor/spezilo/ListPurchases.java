package com.example.igor.spezilo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.widget.ArrayAdapter;
import android.widget.AdapterView;
import android.widget.TextView;
import android.widget.ListView;
import android.database.sqlite.SQLiteDatabase;
import android.database.Cursor;

import android.util.Log;

public class ListPurchases extends AppCompatActivity {

    ListView lvPurchases;
    int month;
    int year;

    MonthData datosmes;
    Cursor cMonth;

    PurchaseSQLiteHelper dbh;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_purchases);

        month = Integer.valueOf(getIntent().getExtras().getString("month"));
        year = Integer.valueOf(getIntent().getExtras().getString("year"));

        Log.i("spezilo", "estamos aquí en la activity listpurchases");
        datosmes = new MonthData(month, year, this);
        cMonth = datosmes.getMonthCursor();

        dbh = new PurchaseSQLiteHelper(this, "DBPurchases", null, 2);

        lvPurchases = (ListView) findViewById(R.id.lv_purchases);

        fillList();

    }

    private void fillList() {

        PurchasesAdapter adaptador = new PurchasesAdapter(this, cMonth);

        lvPurchases.setAdapter(adaptador);
    }
}
