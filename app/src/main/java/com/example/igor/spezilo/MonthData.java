package com.example.igor.spezilo;

import java.util.Calendar;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.Cursor;

import android.util.Log;

public class MonthData {

    PurchaseSQLiteHelper dbh;
    SQLiteDatabase db;
    int imonth;
    int iyear;
    String beginMonth;
    String endMonth;
    Calendar cal;

    Cursor mMonth;

    public MonthData(int month, int year, Context context) {
        imonth = month;
        iyear = year;

        cal = Calendar.getInstance();

        cal.set(year, month, 1);

        dbh = new PurchaseSQLiteHelper(context, "DBPurchases", null, 2);

        createDateStrings();
        mMonth = createandupdateCursor();

    }

    private Cursor createandupdateCursor(int month, int year) {

        imonth = month;
        iyear = year;

        createDateStrings();

        db = dbh.getReadableDatabase();

        String sqlCategories = "SELECT _id, category, sum(amount) as TOTAL from purchases GROUP BY category ORDER BY TOTAL DESC";

        return mMonth;

    }

    public Cursor getMonthCursor(){
        return mMonth;
    }

    private void createDateStrings() {
        int lastdayofmonth;

        lastdayofmonth = cal.getActualMaximum(cal.DAY_OF_MONTH);

        beginMonth = String.valueOf(iyear) + "-" + String.valueOf(imonth) + "-01";
        endMonth = String.valueOf(iyear) + "-" + String.valueOf(imonth) + "-" + String.valueOf(lastdayofmonth);

        Log.i("fecha inicial:", beginMonth);
        Log.i("fecha final: ", endMonth);


    }

    public Cursor getCategoriesList() {

        Cursor cCategories;
        SQLiteDatabase db;

        db = dbh.getReadableDatabase();

        String sqlCategories = "SELECT _id, category, sum(amount) as TOTAL from purchases GROUP BY category ORDER BY TOTAL DESC";
        cCategories = db.rawQuery(sqlCategories, null);

        return cCategories;

    }

    public Cursor getShopsList() {

        Cursor cShops;
        SQLiteDatabase db;

        db = dbh.getReadableDatabase();

        String sqlShops = "SELECT _id, place, sum(amount) as TOTAL from purchases GROUP BY place ORDER BY TOTAL DESC";
        cShops = db.rawQuery(sqlShops, null);

        return cShops;

    }
}
