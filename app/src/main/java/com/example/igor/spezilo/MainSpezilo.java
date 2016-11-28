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
import android.widget.ListView;
import android.widget.Button;
import android.database.sqlite.SQLiteDatabase;
import android.database.Cursor;

import android.content.Intent;

import android.util.Log;

public class MainSpezilo extends AppCompatActivity {

    Spinner monthspinner;
    Spinner yearspinner;
    TextView lblspendings;
    TextView lblmonthSpendings;
    ListView lvCategories;
    ListView lvShops;
    Button btnListPurchases;

    PurchaseSQLiteHelper dbh;
    SQLiteDatabase db;

    int month;
    int year;
    String MonthSelected;
    String YearSelected;

    MonthData datosmes;

    String[] years = { "2016", "2017", "2018", "2019", "2020"};

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
                Intent intent = new Intent(MainSpezilo.this, Purchase.class);
                startActivity(intent);
            }
        });

        monthspinner = (Spinner) findViewById(R.id.cmbMonths);
        ArrayAdapter<CharSequence> adapter =
                ArrayAdapter.createFromResource(this,
                        R.array.months_array,
                        R.layout.spinner_layout);
        monthspinner.setAdapter(adapter);

        yearspinner = (Spinner) findViewById(R.id.cmbYears);
        ArrayAdapter<String> adapteryears = new ArrayAdapter<String>(this,
                R.layout.spinner_layout, years);
        yearspinner.setAdapter(adapteryears);

        lvCategories = (ListView) findViewById(R.id.lv_categories);
        lvShops = (ListView) findViewById(R.id.lv_shops);

        lblspendings = (TextView) findViewById(R.id.lblTotalSpendings);
        lblmonthSpendings = (TextView) findViewById(R.id.lblMonthSpendings);

        btnListPurchases = (Button) findViewById(R.id.btnListPurchases);

        dbh = new PurchaseSQLiteHelper(this, "DBPurchases", null, 2);

        /*
         lo cargamos con estos datos inicialmente; luego se cambia en connectWidgets
         pero esto habría que hacerlo mejor
          */
        datosmes = new MonthData(10, 2016, this);

        fillGridCategories();
        connectWidgets(); //esto lo dejo para verlo, pero ahora no hace falta
        //updateCursorMonth();
        //mostrarDatos();
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
                        MonthSelected = String.valueOf(pos+1);
                        month = pos;
                        Log.i("Escogido: ", MonthSelected);
                        datosmes.createandupdateCursor(month, year);
                        fillGridCategories();
                        mostrarDatos();
                    }

                    public void onNothingSelected(AdapterView<?> parent) {
                        lblspendings.setText("Nada seleccionado");
                    }
                }
        );

        yearspinner.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {
                    public void onItemSelected(AdapterView<?> parent, android.view.View v, int pos, long id) {
                        YearSelected = parent.getItemAtPosition(pos).toString();
                        year = Integer.valueOf(YearSelected);
                        Log.i("Año escogido: ", YearSelected);
                        datosmes.createandupdateCursor(month, year);
                        fillGridCategories();
                        mostrarDatos();
                    }

                    public void onNothingSelected(AdapterView<?> parent) {
                        lblspendings.setText("Nada seleccionado");
                    }
                }
        );

        btnListPurchases.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                Log.i("spezilo", "estamos aquí antes de listas...");
                listPurchases();
            }
        });

    }

    private void mostrarDatos() {
        db = dbh.getWritableDatabase();

        String sqlC = "SELECT * from purchases";
        String sqlTotal = "SELECT sum(amount) as TOTAL from purchases";

        Cursor c = db.rawQuery(sqlC, null);
        Cursor ctotal = db.rawQuery(sqlTotal, null);

        String total = "Total es: " + c.getCount();
        String totalAusgabe = "";

        if (ctotal.moveToFirst()){
            totalAusgabe = "Total gastado: " + ctotal.getString(ctotal.getColumnIndex("TOTAL")) + " €";}
        //double totalAusgabe = ctotal.getDouble(0);

        Log.i("Mostrar", total);

        lblspendings.setText(totalAusgabe);

        String totalMes;

        totalMes = datosmes.getTotalMonthSpendings();
        totalMes = "Total gastado en el mes: " + totalMes;

        lblmonthSpendings.setText(totalMes);

        c.close();
        ctotal.close();

        db.close();
    }

    private void fillGridCategoriesANTIGUO() {
        /*
        esto es antiguo; ahora uso la clase MonthData
         */
        db = dbh.getWritableDatabase();

        String sqlCategories = "SELECT _id, category, sum(amount) as TOTAL from purchases GROUP BY category ORDER BY TOTAL DESC";
        Cursor ctotal = db.rawQuery(sqlCategories, null);
        String sqlShops = "SELECT _id, place, sum(amount) as TOTAL from purchases GROUP BY place ORDER BY TOTAL DESC";
        Cursor shopstotal = db.rawQuery(sqlShops, null);

        CategoriesAdapter adaptador = new CategoriesAdapter(this, ctotal);
        lvCategories.setAdapter(adaptador);

        ShopsAdapter shopsadaptador = new ShopsAdapter(this, shopstotal);
        lvShops.setAdapter(shopsadaptador);

        /*
        si cierro estos, se me crashea la aplicación...
        ctotal.close();

        db.close();
         */

    }

    private void fillGridCategories() {
        Cursor ctotal;
        Cursor stotal;

        ctotal = datosmes.getCategoriesList();
        stotal = datosmes.getShopsList();

        CategoriesAdapter adaptador = new CategoriesAdapter(this, ctotal);
        lvCategories.setAdapter(adaptador);

        ShopsAdapter shopsadaptador = new ShopsAdapter(this, stotal);
        lvShops.setAdapter(shopsadaptador);

    }

    private void updateCursorMonth() {

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                Log.i("ActionBar", "Abriendo Settings!");
                Intent intent = new Intent(MainSpezilo.this, Settings.class);
                startActivity(intent);
                mostrarDatos();
                return true;
            case R.id.action_delete_db:
                Log.i("ActionBar", "Abrir activity!");
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void listPurchases() {
        Intent intent = new Intent(MainSpezilo.this, ListPurchases.class);
        intent.putExtra("month", String.valueOf(month));
        intent.putExtra("year", String.valueOf(year));
        startActivity(intent);

    }

}
