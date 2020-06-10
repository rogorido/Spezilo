package com.example.igor.spezilo;

import java.util.Calendar;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.Cursor;

import android.os.Environment;
import android.util.Log;
//import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

public class MonthData {

    PurchaseSQLiteHelper dbh;
    SQLiteDatabase db;
    int imonth;
    int iyear;
    String beginMonth;
    String endMonth;
    Calendar cal;

    public enum CursorTypeToShow {
        ALLDATA, PRIVATEDATA, COMMONDATA, PURCHASES
    }

    public MonthData(int month, int year, Context context) {
        imonth = month;
        iyear = year;

        cal = Calendar.getInstance();
        cal.set(year, month, 1);

        dbh = new PurchaseSQLiteHelper(context, "DBPurchases", null, 2);

        createDateStrings();

    }

    public Cursor createCursorGeneral(int month, int year, CursorTypeToShow cursortype) {

        Cursor dataToExtract;
        String sqlGeneral;
        imonth = month;
        iyear = year;

        cal.set(iyear, imonth, 1);
        createDateStrings();

        db = dbh.getReadableDatabase();

        // pongo esto aquí porque si no me da error dee que no está inicializado...
        sqlGeneral = "SELECT * from purchases " +
                "WHERE date BETWEEN " + beginMonth + " AND " + endMonth +
                " ORDER BY category, date";
        dataToExtract = db.rawQuery(sqlGeneral, null);

        switch (cursortype) {
            case ALLDATA:
                sqlGeneral = "SELECT * from purchases " +
                             "WHERE date BETWEEN " + beginMonth + " AND " + endMonth +
                             " ORDER BY category, date";
                dataToExtract = db.rawQuery(sqlGeneral, null);
                Log.i("spezilo", "estamos en general");
                break;

            case COMMONDATA:
                sqlGeneral = "SELECT * from purchases " +
                             "WHERE (date BETWEEN " + beginMonth + " AND " + endMonth + ") " +
                             " AND privat = 0 ORDER BY category, date";
                dataToExtract = db.rawQuery(sqlGeneral, null);
                break;

            case PRIVATEDATA:
                sqlGeneral = "SELECT * from purchases " +
                             "WHERE (date BETWEEN " + beginMonth + " AND " + endMonth + ") " +
                             " AND privat = 1 ORDER BY category, date";
                dataToExtract = db.rawQuery(sqlGeneral, null);
                break;

            case PURCHASES:
                sqlGeneral = "SELECT * from purchases " +
                             "WHERE date BETWEEN " + beginMonth + " AND " + endMonth +
                             " ORDER BY date DESC";
                dataToExtract = db.rawQuery(sqlGeneral, null);
                Log.i("spezilo", "estamos en purchases");
                break;

            default: // in case... the most general one
                sqlGeneral = "SELECT * from purchases " +
                        "WHERE date BETWEEN " + beginMonth + " AND " + endMonth +
                        " ORDER BY category, date";
                dataToExtract = db.rawQuery(sqlGeneral, null);
                break;

        }

        return dataToExtract;

    }

    private void createDateStrings() {
        int lastdayofmonth;
        int mesreal;
        String mesennumero;

        /* hay q sumar uno al mes para la fecha en string
           tiene que haber otra forma mejro de hacer esto...
        */
        mesreal = imonth +1;
        lastdayofmonth = cal.getActualMaximum(Calendar.DAY_OF_MONTH);

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

    public String getTotalMonthSpendings() {
        db = dbh.getReadableDatabase();
        String sqlTotal, stTemp;
        String totalMonth;
        Cursor ctotal;

        // Primero el total-total
        sqlTotal = "SELECT round(sum(amount),2) as TOTAL from purchases " +
                "WHERE date BETWEEN " + beginMonth + "AND " + endMonth;

        ctotal = db.rawQuery(sqlTotal, null);

        ctotal.moveToFirst();
        totalMonth = "€" + ctotal.getString(ctotal.getColumnIndex("TOTAL"));

        if (totalMonth.equals("€null")) {
            totalMonth = "€ 0";
        }

        // El total-common
        sqlTotal = "SELECT round(sum(amount),2) as TOTAL from purchases " +
                "WHERE (date BETWEEN " + beginMonth + "AND " + endMonth + ") " +
                "AND privat=0";

        ctotal = db.rawQuery(sqlTotal, null);

        ctotal.moveToFirst();
        stTemp = "€" + ctotal.getString(ctotal.getColumnIndex("TOTAL"));
        if (stTemp.equals("€null")) {
            totalMonth += " ( €0";
        } else {
            totalMonth += " ( " + stTemp;
        }

        // El total privado
        sqlTotal = "SELECT round(sum(amount),2) as TOTAL from purchases " +
                "WHERE (date BETWEEN " + beginMonth + "AND " + endMonth + ") " +
                "AND privat=1";

        ctotal = db.rawQuery(sqlTotal, null);

        ctotal.moveToFirst();
        stTemp = "€" + ctotal.getString(ctotal.getColumnIndex("TOTAL"));
        if (stTemp.equals("€null")) {
            totalMonth += " + €0 )";
        } else {
            totalMonth += " + " + stTemp + " )";
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

    public boolean exportData(){

        boolean exportCommon, exportPrivate;

        File fullPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);

        // no es el mes y año actual! sino el de la exportación!
        String nameFileCommonExpenditures;
        String nameFilePrivateExpenditures;

        // hay que sumar uno al
        nameFileCommonExpenditures = String.valueOf(iyear) + "-" + String.valueOf(imonth+1)+ "-common.csv";
        nameFilePrivateExpenditures = String.valueOf(iyear) + "-" + String.valueOf(imonth+1)+ "-private.csv";

        File fileCommonExpenditures = new File(fullPath, nameFileCommonExpenditures);
        try {
            OutputStreamWriter fout =
                    new OutputStreamWriter(
                            new FileOutputStream(fileCommonExpenditures, false));

            String finalText = createCSV(false);

            fout.write(finalText);
            fout.flush();
            fout.close();

            exportCommon = true;

        } catch (IOException ioe) {
            Log.i("spezilo", "hay un error en los gastos comunes.");

            exportCommon = false;
        }

        File filePrivateExpenditures = new File(fullPath, nameFilePrivateExpenditures);
        try {
            OutputStreamWriter fout =
                    new OutputStreamWriter(
                            new FileOutputStream(filePrivateExpenditures, false));

            String finalText = createCSV(true);

            fout.write(finalText);
            fout.flush();
            fout.close();

            exportPrivate = true;

        } catch (IOException ioe) {
            Log.i("spezilo", "hay un error en los gastos privados.");

            exportPrivate = false;
        }

        if (exportCommon & exportPrivate ) {
            return  true;
        } else {
            return  false;
        }
    }

    private String createCSV(boolean privatorcommon){
        String textCSV = "";
        String notiz = "";
        Cursor monthDataToExport;

        if (privatorcommon) {
            monthDataToExport = createCursorGeneral(imonth, iyear, CursorTypeToShow.PRIVATEDATA);
        } else {
            monthDataToExport = createCursorGeneral(imonth, iyear, CursorTypeToShow.COMMONDATA);
        }

        textCSV = "NW,ISM,Datum,Ort,Kategorie,Notiz" + "\n";

        monthDataToExport.moveToFirst();

            while (!monthDataToExport.isAfterLast()) {

                String person = monthDataToExport.getString(2);

                if (person.equals("Nathalie Wergles")) {

                    textCSV += monthDataToExport.getString(1) + ",0.0";
                }
                else {
                    textCSV += "0.0," + monthDataToExport.getString(1);
                }

                textCSV += "," + monthDataToExport.getString(6) + "," + monthDataToExport.getString(4);

                // a veces se cuela un return en la notiz... lo quitamos
                notiz = monthDataToExport.getString(5).replace("\n", "").replace("\r","");
                textCSV += "," + monthDataToExport.getString(3) + "," + notiz + "\n";

                monthDataToExport.moveToNext();

            }

        return textCSV;

    }
}
