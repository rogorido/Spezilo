package com.example.igor.spezilo;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.CursorAdapter;


public class PurchasesAdapter extends CursorAdapter {
    public PurchasesAdapter(Context context, Cursor cursor) {
        super(context, cursor, 0);
    }

    /*
    uso el layout de categories_item por no hacer otro...
     */
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.purchase_item, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        // Find fields to populate in inflated template
        TextView tvDate = (TextView) view.findViewById(R.id.tvDatePurchase);
        TextView tvAmount = (TextView) view.findViewById(R.id.tvAmountPurchase);
        TextView tvCategory = (TextView) view.findViewById(R.id.tvCategoryPurchase);
        TextView tvPerson = (TextView) view.findViewById(R.id.tvPersonPurchase);
        TextView tvPlace = (TextView) view.findViewById(R.id.tvPlacePurchase);
        TextView tvDescription = (TextView) view.findViewById(R.id.tvDescriptionPurchase);
        TextView tvPrivate = (TextView) view.findViewById(R.id.tvPrivatePurchase);

        // Extract properties from cursor
        String date = cursor.getString((cursor.getColumnIndex("date")));
        double amount = cursor.getDouble(cursor.getColumnIndex("amount"));
        String category = cursor.getString(cursor.getColumnIndex("category"));
        String person = cursor.getString(cursor.getColumnIndex("person"));
        String place = cursor.getString(cursor.getColumnIndex("place"));
        String description = cursor.getString(cursor.getColumnIndex("description"));
        int privat = cursor.getInt(cursor.getColumnIndex("privat"));
        String privatLabel;

        if (privat == 1) {
            privatLabel = "Privat";
        } else {
            privatLabel = "Gemeinsam";
        }

        // Populate fields with extracted properties
        tvDate.setText(date);
        tvAmount.setText(String.valueOf(amount));
        tvCategory.setText(category);
        tvPerson.setText(person);
        tvPlace.setText(place);
        tvDescription.setText(description);
        tvPrivate.setText(privatLabel);

    }
}