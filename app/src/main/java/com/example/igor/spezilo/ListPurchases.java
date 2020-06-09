package com.example.igor.spezilo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.database.Cursor;

import android.util.Log;

public class ListPurchases extends AppCompatActivity {

    ListView lvPurchases;
    int month;
    int year;

    MonthData datosmes;
    Cursor cMonth;
    PurchasesAdapter adaptador;

    PurchaseSQLiteHelper dbh;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_purchases);

        month = Integer.valueOf(getIntent().getExtras().getString("month"));
        year = Integer.valueOf(getIntent().getExtras().getString("year"));

        datosmes = new MonthData(month, year, this);
        cMonth = datosmes.getMonthCursor();

        dbh = new PurchaseSQLiteHelper(this, "DBPurchases", null, 2);

        lvPurchases = (ListView) findViewById(R.id.lv_purchases);

        fillList();
        connectWidgets();

    }

    private void fillList() {

        adaptador = new PurchasesAdapter(this, cMonth);

        lvPurchases.setAdapter(adaptador);
    }

    /*
        esto es realmetne para que al hacer click largo
        borre un item
     */
    private void connectWidgets() {
        lvPurchases.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, final long idpurchase) {

                new AlertDialog.Builder(ListPurchases.this)
                        .setMessage("¿Quieres realmente borrar esta entrada?")
                        .setCancelable(false)
                        .setPositiveButton("Sí", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                Log.w("Spezilo", "borrando");
                                datosmes.deleteItem(idpurchase);
                                cMonth = datosmes.createCursorAll(month, year);
                                adaptador.changeCursor(cMonth);
                            }
                        })
                        .setNegativeButton("No", null)
                        .show();

                return false;
            }
        });

    }
}
