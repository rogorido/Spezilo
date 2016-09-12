package com.example.igor.spezilo;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.AdapterView;
import android.widget.TextView;
import android.database.sqlite.SQLiteDatabase;
import android.database.Cursor;

import android.util.Log;

public class MainSpezilo extends AppCompatActivity {

    Spinner monthspinner;
    TextView lblspendings;
    PurchaseSQLiteHelper dbh;
    SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_spezilo);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        monthspinner = (Spinner) findViewById(R.id.cmbMonths);
        ArrayAdapter<CharSequence> adapter =
                ArrayAdapter.createFromResource(this,
                        R.array.months_array,
                        android.R.layout.simple_spinner_item);
        monthspinner.setAdapter(adapter);

        lblspendings = (TextView) findViewById(R.id.lblTotalSpendings);

        dbh = new PurchaseSQLiteHelper(this, "DBPurchases", null, 1);

        connectWidgets();
        mostrarDatos();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main_spezilo, menu);
        return true;
    }

    void connectWidgets() {

        monthspinner.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {
                    public void onItemSelected(AdapterView<?> parent, android.view.View v, int pos, long id) {
                        lblspendings.setText("Seleccionado: " + parent.getItemAtPosition(pos));
                    }

                    public void onNothingSelected(AdapterView<?> parent) {
                        lblspendings.setText("Nada seleccionado");
                    }
                }
        );

    }

    private void mostrarDatos() {
        db = dbh.getWritableDatabase();

        String sqlC = "SELECT * from purchases";

        Cursor c = db.rawQuery(sqlC, null);

        String total = "Total es: " + c.getCount();

        Log.i("Mostrar", total);

        db.close();


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                Log.i("ActionBar", "Settings!");
                return true;
            case R.id.action_delete_db:
                Log.i("ActionBar", "Borrar db!");
                deleteDB();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void deleteDB() {
        db = dbh.getWritableDatabase();

        if (db!=null) {

            String sqlSentence = "DELETE FROM purchases";
            db.execSQL(sqlSentence);
            mostrarDatos();

        }

        db.close();
    }


}
