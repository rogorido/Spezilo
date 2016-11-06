package com.example.igor.spezilo;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import android.view.Menu;
import android.view.MenuItem;

import android.widget.Spinner;
import android.widget.ArrayAdapter;
import android.widget.TimePicker;

import android.database.sqlite.SQLiteDatabase;
import android.content.ContentValues;

public class Purchase extends AppCompatActivity {

    static final String[] Persons = new String[] { "Nathalie Wergles", "Igor Sosa Mayor"};
    PurchaseSQLiteHelper dbh;
    SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_purchase);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        final Spinner spinnerPerson = (Spinner) findViewById(R.id.cboPerson);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, Persons);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerPerson.setAdapter(adapter);

        final Spinner spCategory = (Spinner) findViewById(R.id.cboCategory);
        ArrayAdapter<String> categoryAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item,
                getResources().getStringArray(R.array.categories_array));
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spCategory.setAdapter(categoryAdapter);

        final Spinner spShop = (Spinner) findViewById(R.id.cboShop);
        ArrayAdapter<String> shopAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item,
                getResources().getStringArray(R.array.shops_array));
        shopAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spShop.setAdapter(shopAdapter);

        TimePicker timePicker = (TimePicker) findViewById(R.id.dtTimePicker);
        timePicker.setIs24HourView(true);

        dbh = new PurchaseSQLiteHelper(this, "DBPurchases", null, 1);
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

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public long savePurchase() {
        SQLiteDatabase db = dbh.getWritableDatabase();

        return db.insert(
                "purchases",
                null,
                toValues());

    }

    public ContentValues toValues () {
        ContentValues values = new ContentValues();

        String valor = 

        values.put("amount", valor);
        values.put("person", persona);
        values.put("category", categoria);
        values.put("place", lugar);
        values.put("description", descripcion);
        values.put("date", fecha);
        values.put("exported", 0);

    }
}
