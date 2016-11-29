package com.example.igor.spezilo;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import android.view.Menu;
import android.view.MenuItem;

import android.widget.Spinner;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.Toast;

import android.database.sqlite.SQLiteDatabase;
import android.content.ContentValues;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.util.Log;

public class Purchase extends AppCompatActivity {

    static final String[] Persons = new String[] { "Nathalie Wergles", "Igor Sosa Mayor"};
    PurchaseSQLiteHelper dbh;
    SQLiteDatabase db;

    Spinner spinnerPerson;
    Spinner spCategory;
    Spinner spShop;
    DatePicker datePicker;
    TextView txtAmount;
    TextView txtDescription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_purchase);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        txtAmount = (TextView) findViewById(R.id.txtAmount);
        txtDescription = (TextView) findViewById(R.id.txtDescription);

        spinnerPerson = (Spinner) findViewById(R.id.cboPerson);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, Persons);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerPerson.setAdapter(adapter);

        spCategory = (Spinner) findViewById(R.id.cboCategory);
        ArrayAdapter<String> categoryAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item,
                getResources().getStringArray(R.array.categories_array));
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spCategory.setAdapter(categoryAdapter);

        spShop = (Spinner) findViewById(R.id.cboShop);
        ArrayAdapter<String> shopAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item,
                getResources().getStringArray(R.array.shops_array));
        shopAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spShop.setAdapter(shopAdapter);

        datePicker = (DatePicker) findViewById(R.id.dtDatePicker);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_purchase, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_accept:
                savePurchase();
                finish();

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    void savePurchase() {
        dbh = new PurchaseSQLiteHelper(this, "DBPurchases", null, 2);
        db = dbh.getWritableDatabase();

        long rowInserted = db.insert(
                "purchases",
                null,
                datosValues());

        if(rowInserted != -1)
            Toast.makeText(getApplicationContext(), "Añadida nueva compra",
                    Toast.LENGTH_SHORT).show();
        else
            Toast.makeText(getApplicationContext(), "Something wrong",
                    Toast.LENGTH_SHORT).show();

    }

    public ContentValues datosValues ()    {
        ContentValues values = new ContentValues();

        String valor = txtAmount.getText().toString();
        String persona = spinnerPerson.getSelectedItem().toString();
        String categoria = spCategory.getSelectedItem().toString();
        String lugar = spShop.getSelectedItem().toString();
        String descripcion = txtDescription.getText().toString();

        /*
        Atención: realmente lo de Date está deprecado y hay que usar no sé que de
        Calendar. Además no sé por qué añade 1900. Ver la ayuda.
         */
        int   day  = datePicker.getDayOfMonth();
        int   month= datePicker.getMonth();
        int   year = datePicker.getYear()-1900;

        String ano = String.valueOf(year);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String fecha = sdf.format(new Date(year, month, day));

        //Log.i("Año es: ", ano);
        Log.i("Fecha metida es: ", fecha);

        values.put("amount", valor);
        values.put("person", persona);
        values.put("category", categoria);
        values.put("place", lugar);
        values.put("description", descripcion);
        values.put("date", fecha);
        values.put("exported", 0);

        return values;
    }
}
