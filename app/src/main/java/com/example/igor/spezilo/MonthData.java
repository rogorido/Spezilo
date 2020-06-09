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

    Cursor mMonthAll;
    Cursor mMonthCommonExpenditures;
    Cursor mMonthPrivate;

    public MonthData(int month, int year, Context context) {
        imonth = month;
        iyear = year;

        cal = Calendar.getInstance();

        cal.set(year, month, 1);

        dbh = new PurchaseSQLiteHelper(context, "DBPurchases", null, 2);

        createDateStrings();

        mMonthAll = createCursorAll(imonth, iyear);
        // mMonthCommonExpenditures = createCursorCommon(imonth, iyear);
        // mMonthPrivate = createCursorPrivate(imonth, iyear);

    }

    public Cursor createCursorAll(int month, int year) {

        Cursor dataToExtract;
        imonth = month;
        iyear = year;

        cal.set(iyear, imonth, 1);
        createDateStrings();

        db = dbh.getReadableDatabase();

        String sqlGeneral = "SELECT * from purchases " +
                "WHERE date BETWEEN " + beginMonth + " AND " + endMonth +
                " ORDER BY category, date";

        dataToExtract = db.rawQuery(sqlGeneral, null);
        Log.i("spezilo", "estamos en general");

        return dataToExtract;

    }

    public Cursor createCursorCommon(int month, int year) {

        Cursor dataToExtract;
        imonth = month;
        iyear = year;

        cal.set(iyear, imonth, 1);
        createDateStrings();

        db = dbh.getReadableDatabase();

        String sqlGeneral = "SELECT * from purchases " +
                "WHERE (date BETWEEN " + beginMonth + " AND " + endMonth + ") " +
                " AND privat = 0" +
                " ORDER BY category, date";

        dataToExtract = db.rawQuery(sqlGeneral, null);
        Log.i("spezilo", "estamos en común");

        return dataToExtract;

    }

    public Cursor createCursorPrivate(int month, int year) {

        Cursor dataToExtract;
        imonth = month;
        iyear = year;

        cal.set(iyear, imonth, 1);
        createDateStrings();

        db = dbh.getReadableDatabase();

        String sqlGeneral = "SELECT * from purchases " +
                "WHERE (date BETWEEN " + beginMonth + " AND " + endMonth + ") " +
                " AND privat = 1 " +
                " ORDER BY category, date";

        Log.i("spezilo", sqlGeneral);

        dataToExtract = db.rawQuery(sqlGeneral, null);

        return dataToExtract;

    }

    public Cursor getMonthCursor(){
        return mMonthAll;
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

    public String getTotalMonthSpendings() {
        db = dbh.getReadableDatabase();
        String sqlTotal;
        String totalMonth;
        Cursor ctotal;

        /*
           Primero el total-total
         */
        sqlTotal = "SELECT round(sum(amount),2) as TOTAL from purchases " +
                "WHERE date BETWEEN " + beginMonth + "AND " + endMonth;

        ctotal = db.rawQuery(sqlTotal, null);

        ctotal.moveToFirst();
        totalMonth = "€" + ctotal.getString(ctotal.getColumnIndex("TOTAL"));

        // El total-common
        sqlTotal = "SELECT round(sum(amount),2) as TOTAL from purchases " +
                "WHERE (date BETWEEN " + beginMonth + "AND " + endMonth + ") " +
                "AND privat=0";

        ctotal = db.rawQuery(sqlTotal, null);

        ctotal.moveToFirst();
        totalMonth += "(" + ctotal.getString(ctotal.getColumnIndex("TOTAL"));

        // El total privado
        sqlTotal = "SELECT round(sum(amount),2) as TOTAL from purchases " +
                "WHERE (date BETWEEN " + beginMonth + "AND " + endMonth + ") " +
                "AND privat=1";

        ctotal = db.rawQuery(sqlTotal, null);

        ctotal.moveToFirst();
        totalMonth += " + " + ctotal.getString(ctotal.getColumnIndex("TOTAL")) + " )";

        // esto no funciona.. no sé por qué...
        /*if (totalMonth == "null €") {
            Log.i("totalgastos", totalMonth);
            totalMonth = "0 €";
        }*/

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

//Creamos un fichero en la memoria interna
        //String fullPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/documents";
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
            monthDataToExport = createCursorPrivate(imonth, iyear);
        } else {
            monthDataToExport = createCursorCommon(imonth, iyear);
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
