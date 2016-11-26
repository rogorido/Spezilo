package com.example.igor.spezilo;

import android.content.Context;
import android.database.Cursor;
//import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.CursorAdapter;


public class CategoriesAdapter extends CursorAdapter {
    public CategoriesAdapter(Context context, Cursor cursor) {
        super(context, cursor, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.categories_item, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        // Find fields to populate in inflated template
        TextView tvCategory = (TextView) view.findViewById(R.id.tvCategory);
        TextView tvAmount = (TextView) view.findViewById(R.id.tvAmount);

        // Extract properties from cursor
        String category = cursor.getString(cursor.getColumnIndex("category"));
        int amount = cursor.getInt(cursor.getColumnIndex("TOTAL"));

        // Populate fields with extracted properties
        tvCategory.setText(category);
        tvAmount.setText(String.valueOf(amount));
    }
}
