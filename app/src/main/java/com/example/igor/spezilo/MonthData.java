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
        mMonth = createandupdateCursor(imonth, iyear);

    }

    public Cursor createandupdateCursor(int month, int year) {

        imonth = month;
        iyear = year;

        cal.set(iyear, imonth, 1);
        createDateStrings();

        db = dbh.getReadableDatabase();

        String sqlGeneral = "SELECT * from purchases " +
                "WHERE date BETWEEN " + beginMonth + "AND " + endMonth +
                " ORDER BY date DESC";

        mMonth = db.rawQuery(sqlGeneral, null);

        return mMonth;

    }

    public Cursor getMonthCursor(){
        return mMonth;
    }

    private void createDateStrings() {
        int lastdayofmonth;
        int mesreal;
        String mesennumero;

        /* hay q sumar uno al mes para la fecha en string
           tiene que haber otra forma mejro de hacer esto...
        */
        mesreal = imonth +1;
        lastdayofmonth = cal.getActualMaximum(cal.DAY_OF_MONTH);

        /*
            esto es una cutrada. Los meses <10 al pasarlos a String
            son así: 2016-1-01, el problema es que sqlite necesita que
            ese "1" sea "01". De todas formas tiene que haber otro sistema mejor.
         */

        mesennumero = String.valueOf(mesreal);

        if (mesennumero.length()==1) {
            mesennumero = "0" + mesennumero;
        }

        beginMonth = String.valueOf(iyear) + "-" + mesennumero + "-01";
        endMonth = String.valueOf(iyear) + "-" + mesennumero + "-" + String.valueOf(lastdayofmonth);

        beginMonth = "date('" + beginMonth + "')";
        endMonth = "date('" + endMonth + "')";

        Log.i("fecha inicial:", beginMonth);
        Log.i("fecha final: ", endMonth);


    }

    public Cursor getCategoriesList() {

        Cursor cCategories;
        SQLiteDatabase db;

        db = dbh.getReadableDatabase();

        String sqlCategories = "SELECT _id, category, round(sum(amount),2) as TOTAL from purchases " +
                "WHERE date BETWEEN " + beginMonth + "AND " + endMonth +
                " GROUP BY category ORDER BY TOTAL DESC";
        cCategories = db.rawQuery(sqlCategories, null);

        return cCategories;

    }

    public Cursor getShopsList() {

        Cursor cShops;
        SQLiteDatabase db;

        db = dbh.getReadableDatabase();

        String sqlShops = "SELECT _id, place, round(sum(amount),2) as TOTAL from purchases " +
                "WHERE date BETWEEN " + beginMonth + "AND " + endMonth +
                " GROUP BY place ORDER BY TOTAL DESC";
        cShops = db.rawQuery(sqlShops, null);

        return cShops;

    }

    public String getTotalMonthSpendings() {
        db = dbh.getReadableDatabase();
        String totalMonth;

        String sqlTotal = "SELECT round(sum(amount),2) as TOTAL from purchases " +
                "WHERE date BETWEEN " + beginMonth + "AND " + endMonth;

        Cursor ctotal = db.rawQuery(sqlTotal, null);

        ctotal.moveToFirst();
        totalMonth = ctotal.getString(ctotal.getColumnIndex("TOTAL")) + " €";

        // esto no funciona.. no sé por qué...
        if (totalMonth == "null €") {
            totalMonth = "0 €";
        }

        return totalMonth;
    }

    /*
        esto habría q ponerlo como boolean!
     */
    public void deleteItem(long id) {
        SQLiteDatabase dbb;

        dbb = dbh.getWritableDatabase();

        String sqlDelete = "DELETE FROM purchases WHERE _id=" + id;

        dbb.execSQL(sqlDelete);
        dbb.close();

    }
}
