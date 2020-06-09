package com.example.igor.spezilo;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
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
import android.widget.Toast;

import java.util.Calendar;

public class MainSpezilo extends AppCompatActivity {

    Spinner monthspinner;
    Spinner yearspinner;
    TextView lblspendings;
    TextView lblmonthSpendings;
    ListView lvCategories;
    ListView lvShops;
    Button btnListPurchases;
    Button btnExport;

    PurchaseSQLiteHelper dbh;
    SQLiteDatabase db;

    int month;
    int year;
    String MonthSelected;
    String YearSelected;

    MonthData datosmes;

    String[] years = { "2016", "2017", "2018", "2019", "2020", "2021", "2022", "2023", "2024", "2025", "2026", "2027", "2028", "2029", "2030"};

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

        lblmonthSpendings = (TextView) findViewById(R.id.lblMonthSpendings);

        btnListPurchases = (Button) findViewById(R.id.btnListPurchases);
        btnExport = (Button) findViewById(R.id.btnExport);

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
        loadCurrentDate();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main_spezilo, menu);
        return true;
    }

    @Override
    public void onResume()
    {  // After a pause OR at startup
        super.onResume();
        fillGridCategories();
        mostrarDatos();

    }

    void connectWidgets() {

        monthspinner.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {
                    public void onItemSelected(AdapterView<?> parent, android.view.View v, int pos, long id) {
                        MonthSelected = String.valueOf(pos+1);
                        month = pos;
                        Log.i("Escogido: ", MonthSelected);
                        datosmes.createCursorAll(month, year);
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
                        datosmes.createCursorAll(month, year);
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
                listPurchases();
            }
        });

        btnExport.setOnClickListener( new View.OnClickListener(){
            public void onClick(View arg0){
                boolean exporting;

                exporting = datosmes.exportData();

                if(exporting)
                    Toast.makeText(getApplicationContext(), "Export war erfolgreich!",
                            Toast.LENGTH_LONG).show();
                else
                    Toast.makeText(getApplicationContext(), "Export scheiterte!",
                            Toast.LENGTH_LONG).show();

            }

        });

    }

    private void mostrarDatos() {

        String totalMes = datosmes.getTotalMonthSpendings();
        lblmonthSpendings.setText(totalMes);
        
    }

    private void fillGridCategoriesANTIGUO() {
        /*
        esto es antiguo; ahora uso la clase MonthData
         */
        db = dbh.getWritableDatabase();

        String sqlCategories = "SELECT _id, category, round(sum(amount),2) as TOTAL from purchases GROUP BY category ORDER BY TOTAL DESC";
        Cursor ctotal = db.rawQuery(sqlCategories, null);

        CategoriesAdapter adaptador = new CategoriesAdapter(this, ctotal);
        lvCategories.setAdapter(adaptador);

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

    private void loadCurrentDate() {
        Calendar now;
        int currentMonth;
        int currentYear;

        now = Calendar.getInstance();
        currentMonth = now.get(now.MONTH);
        currentYear = now.get(now.YEAR);

        /*
            tenemos que restar 2016 pq para el spinner ese
            hay que pasarle la posición
         */
        currentYear = currentYear - 2016;

        monthspinner.setSelection(currentMonth);
        yearspinner.setSelection(currentYear);

        }


}
